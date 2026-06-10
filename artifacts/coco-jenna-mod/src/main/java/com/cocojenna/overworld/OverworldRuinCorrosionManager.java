package com.cocojenna.overworld;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** 主世界黑泥遺跡腐蝕疊加（設計書 主世界再多點 §6.1）. */
public final class OverworldRuinCorrosionManager {

    private OverworldRuinCorrosionManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        if (player.tickCount % 40 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        OverworldRuinType ruin = OverworldPenetrationSavedData.get(player.serverLevel())
                .findRuinNear(player.blockPosition(), 24);

        if (isCorrosiveRuin(ruin)) {
            bond.addOverworldRuinCorrosion(2);
            applyCorrosionEffects(player, bond);
        } else if (bond.getOverworldRuinCorrosion() > 0) {
            bond.addOverworldRuinCorrosion(-1);
        }
    }

    public static boolean tryPurify(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOverworldRuinCorrosion() < 10) return false;
        boolean hasHoly = player.getInventory().contains(new net.minecraft.world.item.ItemStack(ModItems.HOLY_WATER.get()));
        if (!hasHoly) {
            hasHoly = player.getInventory().contains(new net.minecraft.world.item.ItemStack(Items.MILK_BUCKET));
            if (!hasHoly) return false;
            if (!player.getAbilities().instabuild) consumeOne(player, Items.MILK_BUCKET);
        } else if (!player.getAbilities().instabuild) {
            consumeOne(player, ModItems.HOLY_WATER.get());
        }
        bond.setOverworldRuinCorrosion(Math.max(0, bond.getOverworldRuinCorrosion() - 30));
        player.removeEffect(ModEffects.CORROSION_MARK.get());
        player.displayClientMessage(Component.translatable("penetration.cocojenna.corrosion_purified"), true);
        return true;
    }

    private static boolean isCorrosiveRuin(OverworldRuinType ruin) {
        return ruin == OverworldRuinType.MUD_FARM
                || ruin == OverworldRuinType.POLLUTED_TEMPLE
                || ruin == OverworldRuinType.MOON_SEAL;
    }

    private static void applyCorrosionEffects(ServerPlayer player, BondData bond) {
        int c = bond.getOverworldRuinCorrosion();
        if (c > 30) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, true, true));
        }
        if (c > 60) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, false, true, true));
        }
        if (c > 90) {
            player.addEffect(new MobEffectInstance(ModEffects.CORROSION_MARK.get(), 100, 2, false, true, true));
        }
    }

    private static void consumeOne(ServerPlayer player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                stack.shrink(1);
                return;
            }
        }
    }
}
