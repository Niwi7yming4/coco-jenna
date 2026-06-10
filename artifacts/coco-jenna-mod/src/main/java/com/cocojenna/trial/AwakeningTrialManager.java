package com.cocojenna.trial;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 覺醒試煉 — 碎片里程碑 10 / 25 / 40 / 50（設計書覺醒線）. */
public final class AwakeningTrialManager {

    private static final int[] THRESHOLDS = {10, 25, 40, 50};

    private AwakeningTrialManager() {}

    public static void check(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int tier = bond.getAwakeningTrialTier();
        if (tier >= THRESHOLDS.length) return;
        if (bond.getMemoryShardsTotal() < THRESHOLDS[tier]) return;
        if (bond.isAwakeningTrialActive()) return;
        AwakeningTrialCombatManager.offerTrial(player, bond);
    }

    public static boolean meetsPassiveGate(BondData bond, ServerPlayer player, int tier) {
        return switch (tier) {
            case 0 -> bond.getSisterBond() >= 20f;
            case 1 -> bond.getCocoEmotion() >= 30f && bond.getJennaEmotion() >= 30f;
            case 2 -> player.getInventory().countItem(ModItems.PURE_TEAR.get()) >= 1
                    || player.getInventory().countItem(ModItems.HIBISCUS_TEAR.get()) >= 1;
            case 3 -> bond.getSisterBond() >= 60f && bond.isEndgameUnlocked();
            default -> true;
        };
    }

    public static void grantReward(ServerPlayer player, BondData bond, int tier) {
        switch (tier) {
            case 0 -> bond.modifyCocoEmotion(5f);
            case 1 -> bond.modifyJennaEmotion(5f);
            case 2 -> {
                ItemStack tear = new ItemStack(ModItems.MEMORY_SHARD.get(), 3);
                if (!player.addItem(tear)) player.drop(tear, false);
            }
            case 3 -> {
                bond.modifySisterBond(10f);
                ItemStack shard = new ItemStack(ModItems.COCO_MEMORY_SHARD.get());
                if (!player.addItem(shard)) player.drop(shard, false);
            }
            default -> {}
        }
    }
}
