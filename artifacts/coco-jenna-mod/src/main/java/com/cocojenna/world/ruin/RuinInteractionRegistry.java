package com.cocojenna.world.ruin;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

/** 遺跡獨特互動（設計書 D3）. */
public final class RuinInteractionRegistry {

    private RuinInteractionRegistry() {}

    public static void onRuinPlaced(ServerLevel level, BlockPos origin, RuinMatrixRegistry ruin,
            RuinVariant variant) {
        // Placement metadata registered by RuinMatrixPlacer / RuinNbtPlacer.
    }

    public static void onPlayerTick(ServerPlayer player) {
        RuinMatrixSavedData data = RuinMatrixSavedData.get(player.serverLevel());
        BlockPos pos = player.blockPosition();
        var ruinOpt = data.ruinAt(pos, 24);
        if (ruinOpt.isEmpty()) return;
        String ruinId = ruinOpt.get().ruinId();
        RuinMatrixRegistry.byId(ruinId).ifPresent(ruin -> tickNearRuin(player, ruin));
    }

    private static void tickNearRuin(ServerPlayer player, RuinMatrixRegistry ruin) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        switch (ruin) {
            case OUTPOST -> tryOutpostLore(player, pos);
            case WAR_RUINS -> tryRuinLore(player, ruin);
            case STRAY_CAT_CANTEEN -> tryRuinLore(player, ruin);
            case ABANDONED_TOY_VAULT -> tryRuinLore(player, ruin);
            case VELVET_TOWER -> {
                checkCampfireBeacon(level, pos, player);
                tryRuinLore(player, ruin);
            }
            case FULL_MOON_STARGAZING_WELL -> {
                if (level.isNight() && level.getMoonBrightness() >= 1.0f) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0));
                }
            }
            case BLACK_MUD_CONTAMINATED_TEMPLE -> {
                tryPurifyMud(level, pos, player);
                tryRuinLore(player, ruin);
            }
            case MOON_SEALED_DUNGEON -> {
                if (tryMoonDungeonDoor(level, pos)) {
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                }
                tryRuinLore(player, ruin);
            }
            case FORGOTTEN_ALTAR -> {
                if (player.isShiftKeyDown()) {
                    tryForgottenAltarExchange(player);
                    tryRuinLore(player, ruin);
                }
            }
            case FALLEN_HEROES_MONUMENT -> {
                if (player.isShiftKeyDown()) tryMonumentOffering(player, pos);
                tryRuinLore(player, ruin);
            }
            case MEMORY_STONE_CIRCLE -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
            case HOLY_WATER_POTION_LAB -> player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 120, 0));
            case TEMPORAL_RIFT_PORTAL -> player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 0));
            default -> {}
        }
    }

    public static boolean tryMonumentOffering(ServerPlayer player, BlockPos pos) {
        ItemStack hand = player.getMainHandItem();
        if (!hand.is(ModItems.MEMORY_SHARD.get())) return false;
        if (!consumeOne(player, ModItems.MEMORY_SHARD.get())) return false;
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));
        return true;
    }

    public static boolean tryForgottenAltarExchange(ServerPlayer player) {
        if (!consumeOne(player, ModItems.MEMORY_SHARD.get())) return false;
        if (!player.addItem(new ItemStack(ModItems.MEMORY_PARTICLE.get(), 2))) {
            player.drop(new ItemStack(ModItems.MEMORY_PARTICLE.get(), 2), false);
        }
        return true;
    }

    public static void onBellUsed(ServerLevel level, BlockPos bellPos, Player player) {
        var data = RuinMatrixSavedData.get(level);
        if (data.ruinAt(bellPos, 8).filter(r -> "abandoned_toy_vault".equals(r.ruinId())).isEmpty()) {
            return;
        }
        if (level.random.nextFloat() < 0.5f) {
            var golem = ModEntities.WANDERING_SLUDGE.get().create(level);
            if (golem != null) {
                golem.moveTo(bellPos.getX() + 0.5, bellPos.getY(), bellPos.getZ() + 0.5, 0, 0);
                level.addFreshEntity(golem);
            }
        }
    }

    public static boolean tryMoonDungeonDoor(ServerLevel level, BlockPos doorPos) {
        boolean fullMoon = level.getMoonBrightness() >= 1.0f && level.isNight();
        return fullMoon;
    }

    public static void tryPurifyMud(ServerLevel level, BlockPos pos, ServerPlayer player) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.BLACK_MUD.get())) return;
        if (!player.getMainHandItem().is(ModItems.HOLY_WATER.get())) return;
        level.setBlock(pos, Blocks.WATER.defaultBlockState(), 2);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));
    }

    private static boolean consumeOne(ServerPlayer player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static void tryOutpostLore(ServerPlayer player, BlockPos pos) {
        if (!player.isShiftKeyDown()) return;
        if (player.tickCount % 40 != 0) return;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("ruin.cocojenna.outpost.lectern"), false);
    }

    public static void tryRuinLore(ServerPlayer player, RuinMatrixRegistry ruin) {
        if (!player.isShiftKeyDown() || player.tickCount % 60 != 0) return;
        String key = "ruin.cocojenna." + ruin.id() + ".lore";
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(key), false);
    }

    private static void checkCampfireBeacon(ServerLevel level, BlockPos playerPos, ServerPlayer player) {
        for (int dx = -12; dx <= 12; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -12; dz <= 12; dz++) {
                    BlockPos p = playerPos.offset(dx, dy, dz);
                    BlockState st = level.getBlockState(p);
                    if (st.is(Blocks.CAMPFIRE) && st.getValue(CampfireBlock.LIT)) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 0));
                        return;
                    }
                }
            }
        }
    }

    public static void onChainBroken(ServerLevel level, BlockPos pos, ServerPlayer player) {
        var data = RuinMatrixSavedData.get(level);
        if (data.ruinAt(pos, 16).filter(r -> "abyss_seal_anchor".equals(r.ruinId())).isEmpty()) {
            return;
        }
        var boss = ModEntities.PRIMAL_CHAOS.get().create(level);
        if (boss != null) {
            boss.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(boss);
        }
    }
}
