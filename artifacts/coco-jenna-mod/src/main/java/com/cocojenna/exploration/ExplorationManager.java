package com.cocojenna.exploration;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.exploration.DungeonRewardHelper;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/** 探索、傳說與圖鑑進度（設計書 1.3 / 2.2 / 4.3 / 5.4）. */
public final class ExplorationManager {

    private ExplorationManager() {}

    public static boolean discoverLore(ServerPlayer player, int loreId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.hasLore(loreId)) return false;
        LoreEntry entry = LoreRegistry.byId(loreId).orElse(null);
        if (entry == null) return false;
        if (entry.shardCost() > 0 && bond.getMemoryShardsTotal() < entry.shardCost()) {
            player.displayClientMessage(Component.translatable("explore.cocojenna.lore.need_shard"), true);
            return false;
        }
        bond.discoverLore(loreId);
        bond.addJournalEntry("lore:" + entry.key());
        player.displayClientMessage(
                Component.translatable("explore.cocojenna.lore.found",
                        Component.translatable("explore.cocojenna.lore." + entry.key() + ".title")), false);
        checkRegionLoreComplete(player, bond, entry.region());
        sync(player, bond);
        return true;
    }

    public static void discoverWildCat(ServerPlayer player, WildCatType type) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.hasWildCat(type.id())) return;
        bond.discoverWildCat(type.id());
        bond.addJournalEntry("wildcat:" + type.name().toLowerCase());
        player.displayClientMessage(
                Component.translatable("explore.cocojenna.wildcat.discovered",
                        Component.translatable("explore.cocojenna.wildcat." + type.name().toLowerCase())), false);
        if (bond.getWildCatDiscoveryCount() >= 15 && !bond.getExplorationJournal().contains("wildcat:master_awarded")) {
            bond.addJournalEntry("wildcat:master_awarded");
            com.cocojenna.init.ModItems.TOY_SQUEAK.ifPresent(item -> {
                net.minecraft.world.item.ItemStack trophy = new net.minecraft.world.item.ItemStack(item);
                trophy.setHoverName(Component.translatable("item.cocojenna.wildcat_hunter_trophy"));
                if (!player.addItem(trophy)) player.drop(trophy, false);
            });
            player.displayClientMessage(Component.translatable("explore.cocojenna.wildcat.master_title"), false);
        }
        sync(player, bond);
    }

    public static void logExploration(ServerPlayer player, String key, Object... args) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addJournalEntry(key);
        player.displayClientMessage(Component.translatable(key, args), false);
        sync(player, bond);
    }

    public static void clearDungeon(ServerPlayer player, String dungeonId) {
        DungeonRegistry.get(dungeonId).ifPresent(def -> {
            BondData bond = ModCapabilities.getOrDefault(player);
            int flag = DungeonRegistry.flag(dungeonId);
            if (flag == 0 || bond.hasDungeonCleared(flag)) return;
            bond.markDungeonCleared(flag);
            bond.addJournalEntry("dungeon:" + dungeonId);
            bond.addReputation(repKey(def.region()), 8);
            player.displayClientMessage(
                    Component.translatable("explore.cocojenna.dungeon.cleared",
                            Component.translatable("explore.cocojenna.dungeon.name." + dungeonId)), false);
            if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                DungeonEntranceRegistry.markCleared(sl, dungeonId);
                DungeonWorldData.get(sl).markBossDefeated(sl, dungeonId);
            }
            sync(player, bond);
        });
    }

    private static void checkRegionLoreComplete(ServerPlayer player, BondData bond, String region) {
        int total = LoreRegistry.regionEntryCount(region);
        int found = 0;
        for (LoreEntry e : LoreRegistry.all()) {
            if (e.region().equals(region) && bond.hasLore(e.id())) found++;
        }
        if (total > 0 && found >= total) {
            int flag = LoreRegistry.regionFlag(region);
            if (flag != 0 && !bond.hasLoreRegionComplete(flag)) {
                bond.markLoreRegionComplete(flag);
                bond.addReputation(repKey(region), 15);
                grantRegionCompletionReward(player, bond, region);
                player.displayClientMessage(
                        Component.translatable("explore.cocojenna.lore.region_complete",
                                Component.translatable("gui.cocojenna.kingdom." + region)), false);
            }
        }
    }

    private static void grantRegionCompletionReward(ServerPlayer player, BondData bond, String region) {
        switch (region) {
            case "first_cry" -> giveItem(player, ModItems.MAP_FRAGMENT.get(), 2);
            case "velvet_forest" -> giveItem(player, ModItems.VELVET_FUR.get(), 5);
            case "moon_alley" -> giveItem(player, ModItems.MOONSTONE.get(), 3);
            case "gear_town" -> giveItem(player, ModItems.PRECISION_GEAR.get(), 2);
            case "blind_port" -> giveItem(player, ModItems.DEEP_SEA_PEARL.get(), 1);
            case "dawn_highlands" -> DungeonRewardHelper.grant(player, "royal_glory");
            case "howling_gorge" -> giveItem(player, ModItems.STORM_CLOUD_FUR.get(), 2);
            case "phantom_maze" -> giveItem(player, ModItems.SEQUENCE_BADGE.get(), 1);
            case "forgotten_tower" -> giveItem(player, ModItems.MEMORY_SHARD.get(), 3);
            case "rainbow_canyon" -> giveItem(player, ModItems.RAINBOW_YARN_BALL.get(), 1);
            case "catnip_highlands" -> giveItem(player, ModItems.CATNIP_ITEM.get(), 8);
            case "cardboard_slums" -> giveItem(player, ModItems.CARDBOARD_BADGE.get(), 1);
            case "moonlight_beach" -> DungeonRewardHelper.grant(player, "moonlight_clear");
            case "stardust_desert" -> DungeonRewardHelper.grant(player, "stardust_step");
            case "forgotten_wastes" -> DungeonRewardHelper.grant(player, "dark_tide");
            default -> giveItem(player, ModItems.MEMORY_SHARD.get(), 1);
        }
        bond.addJournalEntry("region_reward:" + region);
    }

    private static void giveItem(ServerPlayer player, net.minecraft.world.item.Item item, int count) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, count);
        if (!player.addItem(stack)) player.drop(stack, false);
    }

    private static String repKey(String region) {
        return switch (region) {
            case "gear_town" -> "gear_town";
            case "dawn_highlands" -> "dawn";
            case "blind_port" -> "blind_port";
            case "first_cry" -> "first_cry";
            default -> "royal";
        };
    }

    private static void sync(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }
}
