package com.cocojenna.overworld;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 主世界四階段地牢探索鏈（主世界再多點.md）.
 */
public final class OverworldDungeonQuestManager {

    private OverworldDungeonQuestManager() {}

    public static void onTraceFound(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOverworldDungeonStage() > 0) return;
        bond.setOverworldDungeonStage(1);
        player.displayClientMessage(Component.translatable("dungeon.cocojenna.stage1"), true);
    }

    public static void onClueCollected(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOverworldDungeonStage() != 1) return;
        bond.setOverworldDungeonStage(2);
        player.displayClientMessage(Component.translatable("dungeon.cocojenna.stage2"), true);
    }

    public static void onDungeonCleared(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOverworldDungeonStage() != 2) return;
        bond.setOverworldDungeonStage(3);
        ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get(), 2);
        if (!player.addItem(shard)) player.drop(shard, false);
        player.displayClientMessage(Component.translatable("dungeon.cocojenna.stage3"), true);
    }

    public static void onTruthRevealed(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOverworldDungeonStage() != 3) return;
        bond.setOverworldDungeonStage(4);
        bond.addOverworldInfluence(15);
        player.displayClientMessage(Component.translatable("dungeon.cocojenna.stage4"), true);
    }
}
