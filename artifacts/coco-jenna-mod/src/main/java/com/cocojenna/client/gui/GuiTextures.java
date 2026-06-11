package com.cocojenna.client.gui;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.dialogue.DialogueExpression;
import com.cocojenna.dialogue.Portrait;
import net.minecraft.resources.ResourceLocation;

/** Client GUI texture atlas paths. */
public final class GuiTextures {

    private GuiTextures() {}

    public static ResourceLocation portrait(Portrait portrait) {
        return id("textures/gui/portraits/" + portrait.textureId() + ".png");
    }

    public static ResourceLocation portraitForLine(String speakerKey, Portrait portrait) {
        return portraitForLine(speakerKey, portrait, DialogueExpression.NORMAL);
    }

    /** Prefer speaker-specific portrait art; supports expression suffix. */
    public static ResourceLocation portraitForLine(String speakerKey, Portrait portrait,
            DialogueExpression expression) {
        String base = basePortraitId(speakerKey, portrait);
        if (expression != null && expression != DialogueExpression.NORMAL) {
            ResourceLocation expr = id("textures/gui/portraits/" + base + "_" + expression.name().toLowerCase() + ".png");
            if (hasResource(expr)) return expr;
        }
        ResourceLocation normal = id("textures/gui/portraits/" + base + ".png");
        if (hasResource(normal)) return normal;
        return portrait(portrait);
    }

    private static String basePortraitId(String speakerKey, Portrait portrait) {
        if (speakerKey != null) {
            if (speakerKey.contains("coco")) return "portrait_coco";
            if (speakerKey.contains("jenna")) return "portrait_jenna";
            if (speakerKey.contains("narrator")) return "portrait_narrator";
            if (speakerKey.contains("cheshire")) return "portrait_cheshire";
            if (speakerKey.contains("white_glove")) return "portrait_white_glove";
            if (speakerKey.contains("alpha")) return "portrait_alpha";
            if (speakerKey.contains("sanhua")) return "portrait_sanhua";
            if (speakerKey.contains("ironpaw")) return "portrait_ironpaw";
            if (speakerKey.contains("gray_whisker")) return "portrait_gray_whisker";
            if (speakerKey.contains("qin")) return "portrait_qin_kemu";
            if (speakerKey.contains("moon_priest") || speakerKey.contains("moon_whisper")) {
                return "portrait_moon_priest";
            }
            if (speakerKey.contains("shadow_claw")) return "portrait_shadow_claw";
            if (speakerKey.contains("elder")) return "portrait_calico";
            if (speakerKey.contains("samurai")) return "portrait_squall";
            if (speakerKey.contains("monk")) return "portrait_monk";
            if (speakerKey.contains("court_lady")) return "portrait_court_lady";
            if (speakerKey.contains("general")) return "portrait_general";
            if (speakerKey.contains("corrugata")) return "portrait_corrugata";
        }
        return portrait.textureId();
    }

    public static ResourceLocation loreIllustration(String loreKey) {
        return id("textures/gui/lore/lore_" + loreKey + ".png");
    }

    public static ResourceLocation patchouliIllustration(String name) {
        return id("textures/gui/patchouli/" + name + ".png");
    }

    private static boolean hasResource(ResourceLocation id) {
        return net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(id).isPresent();
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
            case 2 -> "tab_weapon";
            case 3 -> "tab_quest";
            case 4 -> "tab_lore";
            case 5 -> "tab_cat_kingdom";
            case 6 -> "tab_wildcat";
            case 7 -> "tab_journal";
            case 8 -> "tab_settings";
            default -> "tab_settings";
        };
        return id("textures/gui/tabs/" + name + ".png");
    }
}
