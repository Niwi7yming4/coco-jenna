package com.cocojenna.gear;

import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** 披風被動效果（設計書第四章）. */
public final class CloakEffectHelper {

    private CloakEffectHelper() {}

    public static ItemStack equippedCloak(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof com.cocojenna.item.CatCloakItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static String cloakId(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("CloakId")) {
            return stack.getTag().getString("CloakId");
        }
        var key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.getPath() : "";
    }

    public static void tickPlayer(ServerPlayer player) {
        ItemStack cloak = equippedCloak(player);
        if (cloak.isEmpty()) return;
        long tick = player.level().getGameTime();
        String id = cloakId(cloak);

        switch (id) {
            case "cloak_anti_corrosion" -> {
                if (player.hasEffect(ModEffects.BLACK_MUD_STAGE1.get())) {
                    player.removeEffect(ModEffects.BLACK_MUD_STAGE1.get());
                }
                if (player.hasEffect(ModEffects.BLACK_MUD_STAGE2.get())) {
                    player.removeEffect(ModEffects.BLACK_MUD_STAGE2.get());
                }
            }
            case "cloak_moonlight" -> {
                if (player.level().isNight() && tick % 40 == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, false, false, true));
                }
            }
            case "cloak_warm", "velvet_tail_cape" -> {
                if (tick % 40 == 0 && player.getFoodData().needsFood()) {
                    player.heal(0.2f);
                }
            }
            case "cloak_hibiscus" -> {
                if (player.getHealth() / player.getMaxHealth() < 0.2f && tick % 20 == 0) {
                    player.heal(0.1f);
                }
            }
            case "cloak_traveler" -> {
                if (tick % 80 == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 0, false, false, true));
                }
            }
            case "cloak_purr" -> healNearbyCats(player, 1.0f);
            case "cloak_guardian" -> {} // cat damage reduction in cat hurt event
            default -> {}
        }
    }

    public static float catDamageMultiplier(Player owner, AbstractCatEntity cat) {
        ItemStack cloak = equippedCloak(owner);
        if (cloak.isEmpty()) return 1f;
        if (cloakId(cloak).equals("cloak_guardian")) return 0.7f;
        return 1f;
    }

    public static float shardDropBonus(Player player) {
        ItemStack cloak = equippedCloak(player);
        if (cloak.isEmpty()) return 1f;
        if (cloakId(cloak).equals("cloak_memory")) return 1.5f;
        return 1f;
    }

    private static void healNearbyCats(ServerPlayer player, float amount) {
        if (player.level().getGameTime() % 20 != 0) return;
        for (AbstractCatEntity cat : player.serverLevel().getEntitiesOfClass(
                AbstractCatEntity.class, player.getBoundingBox().inflate(8),
                c -> player.getUUID().equals(c.getOwnerUUID()))) {
            cat.heal(amount);
        }
    }
}
