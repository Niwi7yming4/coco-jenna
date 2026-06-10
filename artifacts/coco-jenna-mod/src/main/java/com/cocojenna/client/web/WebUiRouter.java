package com.cocojenna.client.web;

import com.cocojenna.client.gui.AbyssRunScreen;
import com.cocojenna.client.gui.GalgameDialogueScreen;
import com.cocojenna.client.gui.GuardianGuideScreen;
import com.cocojenna.client.gui.KingdomHubFallbackScreen;
import com.cocojenna.client.gui.RiverVoyageFallbackScreen;
import com.cocojenna.client.gui.UndercatHubFallbackScreen;
import com.cocojenna.dialogue.DialogueLine;
import com.cocojenna.network.OpenAbyssRunPacket;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.util.List;

/** Routes UI opens to vanilla Minecraft screens. */
public final class WebUiRouter {

    private WebUiRouter() {}

    public static void openGalgame(String sceneId, List<DialogueLine> lines) {
        GalgameDialogueScreen.open(sceneId, lines);
    }

    public static void openAbyss(OpenAbyssRunPacket state) {
        AbyssRunScreen.open(state);
    }

    public static void openKingdomHub(JsonObject state) {
        KingdomHubFallbackScreen.open(state);
    }

    public static void openRiverVoyage(long seed) {
        RiverVoyageFallbackScreen.open(seed);
    }

    public static void openCatnipPlant(long seed) {
        com.cocojenna.client.gui.CatnipPlantFallbackScreen.open(seed);
    }

    public static void openGuardianGuide() {
        GuardianGuideScreen.open(Minecraft.getInstance().player);
    }

    public static void openUndercatHub(JsonObject state) {
        UndercatHubFallbackScreen.open(state);
    }
}
