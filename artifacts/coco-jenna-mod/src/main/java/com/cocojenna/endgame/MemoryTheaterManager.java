package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenMemoryTheaterPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 記憶劇場 — 重播雨後已解鎖的和平場景. */
public final class MemoryTheaterManager {

    private static final List<TheaterScene> CATALOG = List.of(
            new TheaterScene("afterrain_first_cry", 10),
            new TheaterScene("afterrain_gear_town", 20),
            new TheaterScene("afterrain_blind_port", 30),
            new TheaterScene("afterrain_moon_alley", 40),
            new TheaterScene("afterrain_velvet", 50),
            new TheaterScene("kingdom_festival_moon_start", 60),
            new TheaterScene("kingdom_festival_cooking", 70)
    );

    private MemoryTheaterManager() {}

    public static void open(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) {
            player.displayClientMessage(Component.translatable("memory_theater.cocojenna.need_peace"), true);
            return;
        }
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isBuildingPlaced("memory_theater")) {
            player.displayClientMessage(Component.translatable("memory_theater.cocojenna.need_building"), true);
            return;
        }

        List<String> unlocked = new ArrayList<>();
        for (TheaterScene scene : CATALOG) {
            if (bond.hasPeaceScene(scene.id()) || bond.getMemoryShardsTotal() >= scene.shardGate()) {
                unlocked.add(scene.id());
            }
        }
        if (unlocked.isEmpty()) {
            player.displayClientMessage(Component.translatable("memory_theater.cocojenna.empty"), true);
            return;
        }
        unlocked.sort(Comparator.comparingInt(MemoryTheaterManager::sortKey));
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenMemoryTheaterPacket(unlocked));
    }

    public static void replay(ServerPlayer player, String sceneId) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isBuildingPlaced("memory_theater")) return;
        if (!isReplayable(bond, sceneId)) return;
        DialogueManager.play(player, sceneId);
    }

    private static boolean isReplayable(BondData bond, String sceneId) {
        for (TheaterScene scene : CATALOG) {
            if (scene.id().equals(sceneId)) {
                return bond.hasPeaceScene(sceneId) || bond.getMemoryShardsTotal() >= scene.shardGate();
            }
        }
        return false;
    }

    private static int sortKey(String id) {
        for (TheaterScene scene : CATALOG) {
            if (scene.id().equals(id)) return scene.order();
        }
        return 999;
    }

    private record TheaterScene(String id, int order) {
        int shardGate() {
            return switch (id) {
                case "kingdom_festival_moon_start" -> 40;
                case "kingdom_festival_cooking" -> 55;
                default -> 0;
            };
        }
    }
}
