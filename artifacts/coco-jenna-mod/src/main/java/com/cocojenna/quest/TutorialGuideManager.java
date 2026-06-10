package com.cocojenna.quest;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModEntities;
import com.cocojenna.network.BondSyncCoordinator;
import com.cocojenna.util.MemoryShardUtil;
import com.cocojenna.world.FirstCryVillageGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 第六章新手引導 — 可可的第一次凝視（設計書 6.1–6.4）.
 */
public final class TutorialGuideManager {

    public static final int STAGE_COCO_GAZE = 0;
    public static final int STAGE_MET_JENNA = 1;
    public static final int STAGE_VILLAGE_WALK = 2;
    public static final int STAGE_ALPHA_SHARD = 3;
    public static final int STAGE_DUNGEON_HINT = 4;
    public static final int STAGE_DISTILL_LESSON = 5;
    public static final int STAGE_DONE = 6;

    private static final BlockPos COCO_TREE = new BlockPos(-14, FirstCryVillageGenerator.FLOOR_Y + 1, 16);
    private static final BlockPos JENNA_PLAY = new BlockPos(12, FirstCryVillageGenerator.FLOOR_Y + 1, -6);
    private static final BlockPos VILLAGE_GATE = new BlockPos(0, FirstCryVillageGenerator.FLOOR_Y + 1, 4);

    private TutorialGuideManager() {}

    public static void onFirstEnterCatKingdom(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getArrivalTutorialStage() >= STAGE_DONE) return;
        if (bond.getArrivalTutorialStage() == STAGE_COCO_GAZE) {
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.coco_gaze"), false);
        }
    }

    public static void tick(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        int stage = bond.getArrivalTutorialStage();
        if (stage >= STAGE_DONE) return;

        switch (stage) {
            case STAGE_COCO_GAZE -> handleCocoGaze(player, level, bond);
            case STAGE_MET_JENNA -> handleMeetJenna(player, level, bond);
            case STAGE_VILLAGE_WALK -> handleVillageWalk(player, bond);
            case STAGE_ALPHA_SHARD -> handleAlphaShard(player, bond);
            case STAGE_DUNGEON_HINT -> handleDungeonHint(player, bond);
            default -> { }
        }
    }

    private static void handleCocoGaze(ServerPlayer player, ServerLevel level, BondData bond) {
        if (player.tickCount % 20 != 0) return;
        CocoEntity coco = findOwnedCat(level, player, CocoEntity.class);
        if (coco == null) return;
        if (player.distanceToSqr(Vec3.atCenterOf(COCO_TREE)) < 8 * 8) {
            coco.getNavigation().moveTo(JENNA_PLAY.getX(), JENNA_PLAY.getY(), JENNA_PLAY.getZ(), 0.7);
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.coco_follow"), true);
            advance(player, bond, STAGE_MET_JENNA);
        }
    }

    private static void handleMeetJenna(ServerPlayer player, ServerLevel level, BondData bond) {
        if (player.tickCount % 20 != 0) return;
        JennaEntity jenna = findOwnedCat(level, player, JennaEntity.class);
        if (jenna == null) return;
        if (player.distanceToSqr(Vec3.atCenterOf(JENNA_PLAY)) < 7 * 7) {
            jenna.getNavigation().moveTo(VILLAGE_GATE.getX(), VILLAGE_GATE.getY(), VILLAGE_GATE.getZ(), 0.85);
            CocoEntity coco = findOwnedCat(level, player, CocoEntity.class);
            if (coco != null) {
                coco.getNavigation().moveTo(VILLAGE_GATE.getX() - 1, VILLAGE_GATE.getY(), VILLAGE_GATE.getZ(), 0.75);
            }
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.jenna_lead"), true);
            advance(player, bond, STAGE_VILLAGE_WALK);
        }
    }

    private static void handleVillageWalk(ServerPlayer player, BondData bond) {
        if (player.distanceToSqr(Vec3.atCenterOf(VILLAGE_GATE)) < 6 * 6) {
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.village_arrive"), false);
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.elder_hint"), true);
            advance(player, bond, STAGE_ALPHA_SHARD);
        }
    }

    private static void handleAlphaShard(ServerPlayer player, BondData bond) {
        if (bond.getMemoryShardsTotal() > 0 || bond.getFirstCryQuestStage() >= FirstCryQuestManager.STAGE_MET_ELDER) {
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.monument_hint"), true);
            advance(player, bond, STAGE_DUNGEON_HINT);
        }
    }

    private static void handleDungeonHint(ServerPlayer player, BondData bond) {
        if (player.distanceToSqr(Vec3.atCenterOf(FirstCryVillageGenerator.CENTER)) < 12 * 12) {
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.dungeon_hint"), true);
            advance(player, bond, STAGE_DISTILL_LESSON);
        }
    }

    public static void onFirstHypothermiaEncounter(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getArrivalTutorialStage() == STAGE_DISTILL_LESSON) {
            player.displayClientMessage(Component.translatable("tutorial.cocojenna.distill_hint"), true);
            advance(player, bond, STAGE_DONE);
        }
    }

    public static void grantAlphaShardIfNeeded(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getArrivalTutorialStage() != STAGE_ALPHA_SHARD) return;
        ItemStack shard = MemoryShardUtil.create("alpha_first_shard");
        if (!player.addItem(shard)) player.drop(shard, false);
        player.displayClientMessage(Component.translatable("tutorial.cocojenna.alpha_shard"), false);
    }

    private static void advance(ServerPlayer player, BondData bond, int next) {
        bond.setArrivalTutorialStage(next);
        BondSyncCoordinator.syncKingdomLowFrequency(player, bond);
    }

    private static <T extends com.cocojenna.entity.AbstractCatEntity> T findOwnedCat(
            ServerLevel level, ServerPlayer player, Class<T> type) {
        return level.getEntitiesOfClass(type, player.getBoundingBox().inflate(48))
                .stream()
                .filter(c -> player.getUUID().equals(c.getOwnerUUID()))
                .findFirst()
                .orElse(null);
    }
}
