package com.cocojenna.overworld;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** 融合建築解鎖與生成（聲望 + 雙向影響）. */
public final class FusionBuildingManager {

    private static final int INFLUENCE_THRESHOLD = 50;
    private static final long PLAYTIME_TICKS = 720000L; // ~10 小時

    private FusionBuildingManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOverworldInfluence() < INFLUENCE_THRESHOLD
                || bond.getCatKingdomInfluence() < INFLUENCE_THRESHOLD) {
            return;
        }
        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) < PLAYTIME_TICKS) return;

        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        int placed = bond.getFusionBuildingsPlaced();

        if ((placed & FusionBuildingType.EMBASSY.bit) == 0 && player.getRandom().nextFloat() < 0.02f) {
            tryPlace(player, data, FusionBuildingType.EMBASSY);
        }
        if ((placed & FusionBuildingType.TWIN_STATUE.bit) == 0
                && bond.getPenetrationQuestStage() >= 4
                && player.getRandom().nextFloat() < 0.015f) {
            tryPlace(player, data, FusionBuildingType.TWIN_STATUE);
        }
        if ((placed & FusionBuildingType.CATNIP_EXCHANGE.bit) == 0
                && bond.getCatnipTradedTotal() >= 100
                && player.getRandom().nextFloat() < 0.02f) {
            tryPlace(player, data, FusionBuildingType.CATNIP_EXCHANGE);
        }
    }

    public static void tickAura(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        BlockPos statue = data.findFusionBuilding(FusionBuildingType.TWIN_STATUE, player.blockPosition(), 48);
        if (statue == null) return;
        if (player.blockPosition().distSqr(statue) > 32 * 32) return;
        if (player.level().getGameTime() % 80 != 0) return;
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.REGENERATION, 100, 0, false, true, true));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 100, 0, false, true, true));
    }

    public static boolean tryEmbassyTeleport(ServerPlayer player, BlockPos pos) {
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        BlockPos embassy = data.findFusionBuilding(FusionBuildingType.EMBASSY, pos, 6);
        if (embassy == null) return false;
        BondData bond = ModCapabilities.getOrDefault(player);
        long now = player.level().getGameTime();
        if (now - bond.getLastEmbassyTeleportTick() < 6000L) {
            player.displayClientMessage(Component.translatable("fusion.cocojenna.embassy_cooldown"), true);
            return true;
        }
        ServerLevel kingdom = player.server.getLevel(ModDimensions.CAT_KINGDOM);
        if (kingdom == null) return false;
        BlockPos dest = kingdom.getSharedSpawnPos();
        player.teleportTo(kingdom, dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        bond.setLastEmbassyTeleportTick(now);
        player.displayClientMessage(Component.translatable("fusion.cocojenna.embassy_teleport"), true);
        return true;
    }

    private static void tryPlace(ServerPlayer player, OverworldPenetrationSavedData data,
            FusionBuildingType type) {
        ServerLevel level = player.serverLevel();
        BlockPos center = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE,
                player.blockPosition().offset(player.getRandom().nextInt(96) - 48, 0,
                        player.getRandom().nextInt(96) - 48));
        if (data.findFusionBuilding(type, center, 200) != null) return;

        FusionBuildingGenerator.build(level, center, type);
        data.putFusionBuilding(center, type);
        ModCapabilities.getOrDefault(player).markFusionBuilding(type);
        player.displayClientMessage(Component.translatable("fusion.cocojenna.placed." + type.name().toLowerCase(),
                center.getX(), center.getZ()), false);
    }
}
