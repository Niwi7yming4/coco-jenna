package com.cocojenna.exploration;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
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
        if (bond.getWildCatDiscoveryCount() >= 15) {
            player.displayClientMessage(Component.translatable("explore.cocojenna.wildcat.master"), false);
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
                player.displayClientMessage(
                        Component.translatable("explore.cocojenna.lore.region_complete",
                                Component.translatable("gui.cocojenna.kingdom." + region)), false);
            }
        }
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
