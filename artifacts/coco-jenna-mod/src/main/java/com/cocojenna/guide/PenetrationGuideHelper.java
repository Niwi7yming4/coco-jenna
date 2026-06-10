package com.cocojenna.guide;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.overworld.PenetrationQuestManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Patchouli 滲透章節 — 以 impossible 進度解鎖. */
public final class PenetrationGuideHelper {

    private PenetrationGuideHelper() {}

    public static void syncForStage(ServerPlayer player, int stage) {
        if (stage >= PenetrationQuestManager.STAGE_MOON_PAW) {
            award(player, "guide/penetration_moon_paw");
        }
        if (stage >= PenetrationQuestManager.STAGE_MEMORY_SHARDS) {
            award(player, "guide/penetration_gray_whisker");
        }
        if (stage >= PenetrationQuestManager.STAGE_CAT_LANGUAGE) {
            award(player, "guide/penetration_moon_dungeon");
        }
        if (stage >= PenetrationQuestManager.STAGE_REPAIR_PORTAL) {
            award(player, "guide/penetration_repair_portal");
        }
        if (stage >= PenetrationQuestManager.STAGE_COMPLETE) {
            award(player, "guide/penetration_first_entry");
        }
    }

    private static void award(ServerPlayer player, String path) {
        ResourceLocation loc = new ResourceLocation(CocoJennaMod.MOD_ID, path);
        Advancement adv = player.server.getAdvancements().getAdvancement(loc);
        if (adv != null && !player.getAdvancements().getOrStartProgress(adv).isDone()) {
            player.getAdvancements().award(adv, "discovered");
        }
    }
}
