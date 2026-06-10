package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenRiverVoyagePacket;
import com.google.gson.JsonArray;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 暗河航行小遊戲 — 取代一鍵傳送，處理成功／失敗分支. */
public final class RiverVoyageManager {

    private static final int MIN_DURATION_MS = 12_000;
    private static final int MIN_DISTANCE = 900;
    private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();

    private RiverVoyageManager() {}

    public static void begin(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 2 || bond.getUndercatStage() < 3) return;
        long seed = player.getRandom().nextLong();
        SESSIONS.put(player.getUUID(), new Session(seed, System.currentTimeMillis()));
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenRiverVoyagePacket(seed));
        player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_begin"), true);
    }

    public static void complete(ServerPlayer player, String outcome, int distance, int hull,
            JsonArray events, int durationMs, boolean fallback) {
        Session session = SESSIONS.remove(player.getUUID());
        if (session == null) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 2 || bond.getUndercatStage() < 3) return;

        boolean success = "success".equals(outcome)
                && durationMs >= (fallback ? 5_000 : MIN_DURATION_MS)
                && distance >= (fallback ? 500 : MIN_DISTANCE)
                && hull > 0;

        if (!success) {
            onFail(player, bond, hull);
            return;
        }

        int bonusCoins = 0;
        if (events != null) {
            for (var el : events) {
                if ("smuggler_signal".equals(el.getAsString())) bonusCoins += 10;
                if ("neon_glow".equals(el.getAsString())) bonusCoins += 5;
            }
        }
        if (hull >= 70) bonusCoins += 5;

        bond.addShadowCoins(15 + bonusCoins);
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 10);
        UndercatSideStoryManager.addOneEyeAffinity(player, 20);
        UndercatQuestManager.teleportToRegion(player, UndercatRegion.CATNIP_FARM);
        bond.setUndercatStage(4);
        UndercatQuestManager.unlockRegionProgress(bond, UndercatRegion.CATNIP_FARM);

        player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_arrived"), true);
        if (bonusCoins > 0) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_bonus", bonusCoins), true);
        }
        DialogueManager.play(player, "undercat_ch2_farm");
        UndercatQuestManager.openHub(player);
    }

    public static void abandon(ServerPlayer player) {
        SESSIONS.remove(player.getUUID());
        player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_abandon"), true);
        UndercatQuestManager.openHub(player);
    }

    private static void onFail(ServerPlayer player, BondData bond, int hull) {
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, -5);
        if (hull <= 0) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_sink"), true);
        } else {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_fail"), true);
        }
        UndercatQuestManager.openHub(player);
    }

    private record Session(long seed, long startMs) {}
}
