package com.cocojenna.quest;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.BondSyncCoordinator;
import com.cocojenna.world.FirstCryVillageGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 深化設計書 Ch.4 四階段教程（0–5 / 5–20 / 20–40 / 40–60 分鐘）.
 * 綁定可可危險感知與珍奶好奇心 action-bar 提示.
 */
public final class KingdomTutorialManager {

    public static final int STAGE_ARRIVAL = 0;
    public static final int STAGE_HOME = 1;
    public static final int STAGE_WORLD_CALL = 2;
    public static final int STAGE_FIRST_BATTLE = 3;
    public static final int STAGE_COMPLETE = 4;

    private static final String TAG = "cocojenna_kingdom_tutorial";

    private KingdomTutorialManager() {}

    public static int getStage(ServerPlayer player) {
        return player.getPersistentData().getInt(TAG);
    }

    private static void setStage(ServerPlayer player, int stage) {
        player.getPersistentData().putInt(TAG, stage);
    }

    public static void onEnteredKingdom(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (getStage(player) > STAGE_ARRIVAL) return;
        setStage(player, STAGE_ARRIVAL);
        showHint(player, "tutorial.cocojenna.arrival");
    }

    public static void onFeedOrPet(ServerPlayer player) {
        if (getStage(player) == STAGE_ARRIVAL) {
            setStage(player, STAGE_HOME);
            showHint(player, "tutorial.cocojenna.home");
        }
    }

    public static void onAlphaTalk(ServerPlayer player) {
        if (getStage(player) == STAGE_HOME) {
            setStage(player, STAGE_WORLD_CALL);
            showHint(player, "tutorial.cocojenna.world_call");
        }
    }

    public static void onMonumentFound(ServerPlayer player) {
        if (getStage(player) == STAGE_WORLD_CALL) {
            setStage(player, STAGE_FIRST_BATTLE);
            showHint(player, "tutorial.cocojenna.first_battle");
        }
    }

    public static void onFirstDistill(ServerPlayer player) {
        if (getStage(player) == STAGE_FIRST_BATTLE) {
            setStage(player, STAGE_COMPLETE);
            showHint(player, "tutorial.cocojenna.distill_done");
            ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get(), 2);
            if (!player.addItem(shard)) player.drop(shard, false);
            BondData bond = ModCapabilities.getOrDefault(player);
            bond.addCatKingdomInfluence(5);
            BondSyncCoordinator.onHighFrequencyChange(player);
        }
    }

    public static void tickHints(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.level().getGameTime() % 200 != 0) return;
        tickTimeBand(player);
        if (player.blockPosition().distSqr(FirstCryVillageGenerator.CENTER) > 120 * 120) return;

        int stage = getStage(player);
        if (stage >= STAGE_COMPLETE) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        String key = switch (stage) {
            case STAGE_ARRIVAL -> "tutorial.cocojenna.hud.arrival";
            case STAGE_HOME -> bond.getCocoEmotion() >= 20
                    ? "tutorial.cocojenna.hud.coco_sense"
                    : "tutorial.cocojenna.hud.home";
            case STAGE_WORLD_CALL -> bond.getJennaIndependence() >= 15
                    ? "tutorial.cocojenna.hud.jenna_curiosity"
                    : "tutorial.cocojenna.hud.world_call";
            case STAGE_FIRST_BATTLE -> "tutorial.cocojenna.hud.first_battle";
            default -> null;
        };
        if (key != null) {
            player.displayClientMessage(Component.translatable(key), true);
        }
    }

    private static void showHint(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable(key), true);
        OnboardingQuestManager.sendHint(player, key);
    }

    /** 0–5 / 5–20 / 20–40 / 40–60 分鐘時間帶提示. */
    public static void tickTimeBand(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getCatKingdomEnterDay() <= 0) return;
        long day = player.level().getDayTime() / 24000L;
        long minutes = (day - bond.getCatKingdomEnterDay()) * 20L;
        String key = null;
        if (minutes < 5) key = "tutorial.cocojenna.band.early";
        else if (minutes < 20) key = "tutorial.cocojenna.band.mid";
        else if (minutes < 40) key = "tutorial.cocojenna.band.late";
        else if (minutes < 60) key = "tutorial.cocojenna.band.expert";
        if (key != null && player.tickCount % 1200 == 0) {
            player.displayClientMessage(Component.translatable(key), true);
        }
    }
}
