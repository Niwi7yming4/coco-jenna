package com.cocojenna.web;

import com.cocojenna.abyss.AbyssRunManager;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenAbyssRunPacket;
import com.cocojenna.network.WebUiStatePacket;
import com.cocojenna.undercat.RiverVoyageManager;
import com.cocojenna.undercat.UndercatFaction;
import com.cocojenna.undercat.UndercatQuestManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** Server-side handler for JSON actions from vanilla UI screens. */
public final class WebUiActionHandler {

    private WebUiActionHandler() {}

    public static void handle(ServerPlayer player, String page, String sessionId, String json) {
        JsonObject msg = JsonParser.parseString(json).getAsJsonObject();
        String action = msg.has("action") ? msg.get("action").getAsString() : "";

        switch (page) {
            case "galgame" -> handleGalgame(player, sessionId, action, msg);
            case "abyss" -> handleAbyss(player, sessionId, action, msg);
            case "undercat" -> handleUndercat(player, sessionId, action, msg);
            case "river" -> handleRiver(player, action, msg);
            case "catnip_plant" -> handleCatnipPlant(player, action, msg);
            case "kingdom" -> handleKingdom(player, sessionId, action, msg);
            default -> { }
        }
    }

    private static void handleGalgame(ServerPlayer player, String sessionId, String action, JsonObject msg) {
        if ("dialogue_choice".equals(action)) {
            String sceneId = msg.get("sceneId").getAsString();
            String choiceId = msg.get("choiceId").getAsString();
            DialogueManager.onSceneComplete(player, sceneId, choiceId);
            return;
        }
        if ("dialogue_advance".equals(action)) {
            String sceneId = msg.get("sceneId").getAsString();
            String completeAction = msg.has("completeAction") ? msg.get("completeAction").getAsString() : "";
            if (!completeAction.isEmpty()) {
                DialogueManager.onSceneComplete(player, sceneId, completeAction);
            }
        }
        if ("give_item".equals(action)) {
            String item = msg.get("item").getAsString();
            String target = msg.has("target") ? msg.get("target").getAsString() : "";
            if ("eternal_cloak".equals(item) && "jenna".equals(target)) {
                giveEternalCloakToJenna(player);
                push(player, sessionId, buildBondState(player));
            }
        }
    }

    private static void giveEternalCloakToJenna(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        boolean hasCloak = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.CLOAK_GUARDIAN.get())) {
                stack.shrink(1);
                hasCloak = true;
                break;
            }
        }
        if (!hasCloak) {
            player.displayClientMessage(Component.translatable("webui.cocojenna.no_cloak"), true);
            return;
        }
        bond.modifyJennaEmotion(10f);
        bond.modifySisterBond(3f);
        player.displayClientMessage(Component.translatable("webui.cocojenna.cloak_given"), true);
    }

    private static void handleAbyss(ServerPlayer player, String sessionId, String action, JsonObject msg) {
        int index = msg.has("index") ? msg.get("index").getAsInt() : 0;
        switch (action) {
            case "play_card" -> AbyssRunManager.playCard(player, index);
            case "end_turn" -> AbyssRunManager.endTurn(player);
            case "pick_reward" -> AbyssRunManager.pickReward(player, index);
            case "shop_buy" -> AbyssRunManager.shopAction(player, index);
            case "leave" -> AbyssRunManager.abandon(player);
            default -> { return; }
        }
        if ("leave".equals(action)) {
            return;
        }
        AbyssRunManager.Session s = AbyssRunManager.getSession(player);
        if (s != null) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new OpenAbyssRunPacket(s));
        }
    }

    private static void handleUndercat(ServerPlayer player, String sessionId, String action, JsonObject msg) {
        switch (action) {
            case "accept_quest" -> UndercatQuestManager.acceptChapter(player);
            case "faction_donate" -> {
                UndercatFaction faction = UndercatFaction.valueOf(msg.get("faction").getAsString());
                int amount = msg.get("amount").getAsInt();
                UndercatQuestManager.donateShadowCoins(player, faction, amount);
            }
            case "open_dialogue" -> DialogueManager.play(player, msg.get("sceneId").getAsString());
            case "complete_commission" -> {
                String id = msg.get("commission").getAsString();
                UndercatQuestManager.completeCommission(player,
                        com.cocojenna.undercat.UndercatCommission.valueOf(id));
            }
            case "start_trial" -> UndercatQuestManager.startTrial(player, msg.get("trial").getAsString());
            case "start_voyage" -> UndercatQuestManager.startRiverVoyage(player);
            case "spawn_gladiator" -> UndercatQuestManager.spawnNextGladiator(player);
            case "silent_trial" -> UndercatQuestManager.startSilentTrial(player);
            case "choose_ending" -> UndercatQuestManager.chooseEnding(player, msg.get("ending").getAsInt());
            case "complete_daily" -> com.cocojenna.undercat.UndercatDailyQuestManager.complete(player);
            case "arena_bet" -> com.cocojenna.undercat.ArenaBettingManager.placeBet(
                    player, msg.get("amount").getAsInt());
            case "plant_catnip" -> com.cocojenna.undercat.CatnipPlantingManager.plantWithSeed(player);
            case "smuggler_buy" -> com.cocojenna.shop.SmugglerBlackMarketOffers.tryBuyContraband(
                    player, msg.get("item").getAsString());
            case "smuggler_ryo" -> com.cocojenna.shop.SmugglerBlackMarketOffers.tryPurchase(
                    player, msg.get("index").getAsInt());
            case "teleport_region" -> UndercatQuestManager.teleportToRegion(player,
                    com.cocojenna.undercat.UndercatRegion.valueOf(msg.get("region").getAsString()));
            default -> { return; }
        }
        push(player, sessionId, UndercatQuestManager.buildHubState(player));
    }

    private static void handleRiver(ServerPlayer player, String action, JsonObject msg) {
        switch (action) {
            case "river_complete" -> {
                String outcome = msg.get("outcome").getAsString();
                int distance = msg.has("distance") ? msg.get("distance").getAsInt() : 0;
                int hull = msg.has("hull") ? msg.get("hull").getAsInt() : 0;
                int duration = msg.has("durationMs") ? msg.get("durationMs").getAsInt() : 0;
                boolean fallback = msg.has("fallback") && msg.get("fallback").getAsBoolean();
                JsonArray events = msg.has("events") ? msg.getAsJsonArray("events") : new JsonArray();
                RiverVoyageManager.complete(player, outcome, distance, hull, events, duration, fallback);
            }
            case "river_abandon" -> RiverVoyageManager.abandon(player);
            default -> { }
        }
    }

    private static void handleCatnipPlant(ServerPlayer player, String action, JsonObject msg) {
        switch (action) {
            case "plant_complete" -> {
                int hits = msg.has("hits") ? msg.get("hits").getAsInt() : 0;
                int misses = msg.has("misses") ? msg.get("misses").getAsInt() : 0;
                com.cocojenna.undercat.CatnipPlantingManager.finish(player, hits, misses);
            }
            case "plant_abandon" -> UndercatQuestManager.openHub(player);
            default -> { }
        }
    }

    private static void handleKingdom(ServerPlayer player, String sessionId, String action, JsonObject msg) {
        com.cocojenna.endgame.kingdom.AfterRainKingdomManager.handleAction(player, action, msg);
        if (!"read_story".equals(action)) {
            push(player, sessionId, com.cocojenna.endgame.kingdom.AfterRainKingdomManager.buildHubState(player));
        }
    }

    private static JsonObject buildBondState(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        JsonObject o = new JsonObject();
        o.addProperty("type", "galgame");
        o.addProperty("cocoEmotion", bond.getCocoEmotion());
        o.addProperty("jennaEmotion", bond.getJennaEmotion());
        return o;
    }

    public static void push(ServerPlayer player, String sessionId, JsonObject state) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new WebUiStatePacket(sessionId, state.toString()));
    }
}
