package com.cocojenna.endgame;

import com.cocojenna.blackmud.BlackMudSavedData;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.FurBallSpiritEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import com.cocojenna.world.BlindPortGenerator;
import com.cocojenna.world.FirstCryVillageGenerator;
import com.cocojenna.world.GearTownGenerator;
import com.cocojenna.world.MoonAlleyGenerator;
import com.cocojenna.world.VelvetForestPoiGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;

/** 《雨後的絨尾之鄉》DLC 主體框架 — 和平模式生態與王國管理. */
public final class AfterRainGameplayManager {

    private static final int SPAWN_INTERVAL = 400;

    private AfterRainGameplayManager() {}

    public static void tick(ServerLevel level) {
        if (!AfterRainManager.isAfterRain(level)) return;
        long now = level.getGameTime();

        if (now % 20 == 0) {
            for (ServerPlayer player : level.players()) {
                com.cocojenna.endgame.kingdom.FestivalEventManager.tickPlayer(player);
            }
        }
        if (now % 24000 == 0) {
            level.setDayTime(level.getDayTime() + 1);
            for (ServerPlayer player : level.players()) {
                com.cocojenna.endgame.kingdom.AfterRainKingdomManager.tickDaily(player);
            }
        }
        if (now % SPAWN_INTERVAL == 0) {
            trySpawnFurBall(level);
        }
        if (now % 1200 == 0) {
            tickPeaceBond(level);
        }
        if (now % 40 == 0) {
            for (ServerPlayer player : level.players()) {
                tryPeaceDialogue(player);
            }
        }
        if (now % 20 == 0) {
            for (ServerPlayer player : level.players()) {
                FallenVelvetRedemptionManager.tickPlayer(player);
            }
        }
        if (now % 40 == 0) {
            for (ServerPlayer player : level.players()) {
                NightSecretEventManager.tickPlayer(player);
            }
        }
    }

    private static void tryPeaceDialogue(ServerPlayer player) {
        if (!isPeaceMode(player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        BlockPos pos = player.blockPosition();
        record Region(String scene, BlockPos center, int radius) {}
        Region[] regions = {
                new Region("afterrain_first_cry", FirstCryVillageGenerator.CENTER, 48),
                new Region("afterrain_gear_town", GearTownGenerator.CENTER, 40),
                new Region("afterrain_blind_port", BlindPortGenerator.CENTER, 44),
                new Region("afterrain_moon_alley", MoonAlleyGenerator.CENTER, 36),
                new Region("afterrain_velvet", VelvetForestPoiGenerator.CENTER, 40),
        };
        for (Region r : regions) {
            if (bond.hasPeaceScene(r.scene())) continue;
            if (pos.distSqr(r.center()) > (long) r.radius() * r.radius()) continue;
            bond.markPeaceScene(r.scene());
            DialogueManager.play(player, r.scene());
            return;
        }
    }

    public static boolean isPeaceMode(ServerPlayer player) {
        return ModCapabilities.getOrDefault(player).isEndgameUnlocked()
                && AfterRainManager.isAfterRain(player.serverLevel());
    }

    public static void openKingdomTerminal(ServerPlayer player) {
        KingdomDecreeManager.openTerminal(player);
    }

    private static void trySpawnFurBall(ServerLevel level) {
        if (level.players().isEmpty()) return;
        ServerPlayer anchor = level.players().get(level.random.nextInt(level.players().size()));
        if (level.getEntitiesOfClass(FurBallSpiritEntity.class,
                anchor.getBoundingBox().inflate(48)).size() >= 6) {
            return;
        }
        BlockPos pos = anchor.blockPosition().offset(
                level.random.nextInt(16) - 8, 1, level.random.nextInt(16) - 8);
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.below()).isSolidRender(level, pos.below())) return;
        var spirit = ModEntities.FUR_BALL_SPIRIT.get().create(level);
        if (spirit == null) return;
        spirit.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        spirit.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
        level.addFreshEntity(spirit);
    }

    private static void tickPeaceBond(ServerLevel level) {
        BlackMudSavedData data = BlackMudSavedData.get(level);
        if (!data.isAfterRain()) return;
        for (ServerPlayer player : level.players()) {
            BondData bond = ModCapabilities.getOrDefault(player);
            bond.addKingdomProsperity(1);
            bond.modifyCocoEmotion(0.2f);
            bond.modifyJennaEmotion(0.2f);
        }
    }
}
