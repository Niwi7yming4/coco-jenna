package com.cocojenna.guide;

import com.cocojenna.CocoJennaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

/** 可選 Patchouli 整合 — 未安裝時由 GUI 後備. */
public final class PatchouliHelper {

    public static final ResourceLocation BOOK_ID = new ResourceLocation(CocoJennaMod.MOD_ID, "guardian_guide");

    private PatchouliHelper() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded("patchouli");
    }

    /** 僅在客戶端開啟 Patchouli 書本 GUI. */
    public static boolean openBookClient() {
        if (!isLoaded()) return false;
        try {
            Class<?> api = Class.forName("vazkii.patchouli.api.PatchouliAPI");
            Object instance = api.getMethod("get").invoke(null);
            api.getMethod("openBookGUI", ResourceLocation.class).invoke(instance, BOOK_ID);
            return true;
        } catch (ReflectiveOperationException e) {
            CocoJennaMod.LOGGER.warn("Patchouli open failed: {}", e.getMessage());
            return false;
        }
    }
}
