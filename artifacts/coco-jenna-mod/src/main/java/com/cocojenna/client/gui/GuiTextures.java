package com.cocojenna.client.gui;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.dialogue.Portrait;
import net.minecraft.resources.ResourceLocation;

/** Client GUI texture atlas paths. */
public final class GuiTextures {

    private GuiTextures() {}

    public static ResourceLocation portrait(Portrait portrait) {
        return id("textures/gui/portraits/" + portrait.textureId() + ".png");
    }

    /** Prefer speaker-specific portrait art when available. */
    public static ResourceLocation portraitForLine(String speakerKey, Portrait portrait) {
        if (speakerKey != null) {
            if (speakerKey.contains("coco")) return id("textures/gui/portraits/portrait_coco.png");
            if (speakerKey.contains("jenna")) return id("textures/gui/portraits/portrait_jenna.png");
            if (speakerKey.contains("narrator")) return id("textures/gui/portraits/portrait_narrator.png");
            if (speakerKey.contains("cheshire")) return id("textures/gui/portraits/portrait_cheshire.png");
            if (speakerKey.contains("white_glove")) return id("textures/gui/portraits/portrait_white_glove.png");
            if (speakerKey.contains("alpha")) return id("textures/gui/portraits/portrait_alpha.png");
            if (speakerKey.contains("sanhua")) return id("textures/gui/portraits/portrait_sanhua.png");
            if (speakerKey.contains("ironpaw")) return id("textures/gui/portraits/portrait_ironpaw.png");
            if (speakerKey.contains("elder")) return id("textures/gui/portraits/portrait_calico.png");
            if (speakerKey.contains("samurai")) return id("textures/gui/portraits/portrait_squall.png");
            if (speakerKey.contains("monk")) return id("textures/gui/portraits/portrait_monk.png");
            if (speakerKey.contains("court_lady")) return id("textures/gui/portraits/portrait_court_lady.png");
            if (speakerKey.contains("general")) return id("textures/gui/portraits/portrait_general.png");
        }
        return portrait(portrait);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(CocoJennaMod.MOD_ID, path);
    }

    public static ResourceLocation cardBack(int fromTier) {
        String tier = fromTier >= 7 ? "legend" : fromTier >= 4 ? "rare" : "common";
        return id("textures/gui/cards/card_back_" + tier + ".png");
    }

    public static ResourceLocation cardFront(String force) {
        String f = force == null || force.isEmpty() ? "resonance" : force;
        return id("textures/gui/cards/card_front_" + f + ".png");
    }

    public static ResourceLocation worldMap() {
        return id("textures/gui/world_map.png");
    }

    public static ResourceLocation worldMapThumb() {
        return id("textures/gui/world_map_thumb.png");
    }

    public static ResourceLocation memoryBookTab(int tab) {
        String name = switch (tab) {
            case 0 -> "tab_emotion";
            case 1 -> "tab_memory";
            case 2 -> "tab_cat_kingdom";
            default -> "tab_settings";
        };
        return id("textures/gui/tabs/" + name + ".png");
    }
}
