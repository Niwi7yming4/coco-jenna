package com.cocojenna.client.gui;

import com.cocojenna.CocoJennaMod;
import net.minecraft.resources.ResourceLocation;

/** GAL 對話 UI 貼圖路徑（Wave 2）. */
public final class GalgameUiAssets {

    public static final int DIALOG_TEX = 256;
    public static final int DIALOG_BORDER = 24;
    public static final int BACKGROUND_W = 854;
    public static final int BACKGROUND_H = 480;
    public static final int NAME_PLATE_W = 128;
    public static final int NAME_PLATE_H = 32;

    private GalgameUiAssets() {}

    public static ResourceLocation dialogBox() {
        return id("textures/gui/gal/dialog_box.png");
    }

    public static ResourceLocation choicePanel() {
        return id("textures/gui/gal/choice_panel.png");
    }

    public static ResourceLocation namePlate() {
        return id("textures/gui/gal/name_plate.png");
    }

    public static ResourceLocation background(String backgroundId) {
        if (backgroundId == null || backgroundId.isEmpty()) {
            return id("textures/gui/gal/bg_default.png");
        }
        return id("textures/gui/gal/bg_" + backgroundId + ".png");
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(CocoJennaMod.MOD_ID, path);
    }
}
