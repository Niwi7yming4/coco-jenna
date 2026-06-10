package com.cocojenna.guide;

import com.cocojenna.CocoJennaMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** 守護者指南 Patchouli 解鎖橋接 — 以 impossible 進度手動授予. */
public final class GuideAdvancementHelper {

    private GuideAdvancementHelper() {}

    public static void onDiscovery(ServerPlayer player, int flag) {
        String id = switch (flag) {
            case GuardianGuideProgress.HEAT_LEECH -> "guide/heat_leech";
            case GuardianGuideProgress.GRIEF_AMALGAM -> "guide/grief_amalgam";
            case GuardianGuideProgress.BLIND_WATER_LORD -> "guide/blind_water_lord";
            case GuardianGuideProgress.FALLEN_VELVET -> "guide/fallen_velvet";
            case GuardianGuideProgress.PRIMAL_CHAOS -> "guide/primal_chaos";
            case GuardianGuideProgress.TAPE_COLOSSUS -> "guide/tape_colossus";
            case GuardianGuideProgress.CATNIP_DRAGON -> "guide/catnip_dragon";
            case GuardianGuideProgress.SILENCED_ONE -> "guide/silenced_one";
            case GuardianGuideProgress.MOON_GUARDIAN -> "guide/moon_guardian";
            case GuardianGuideProgress.WANDERING_SLUDGE -> "guide/wandering_sludge";
            case GuardianGuideProgress.MUD_FARMER -> "guide/mud_farmer";
            case GuardianGuideProgress.MUD_GUARD -> "guide/mud_guard";
            case GuardianGuideProgress.MUD_PRIEST -> "guide/mud_priest";
            default -> null;
        };
        if (id != null) {
            award(player, id);
        }
        award(player, "guide/first_boss");
    }

    private static void award(ServerPlayer player, String path) {
        ResourceLocation loc = new ResourceLocation(CocoJennaMod.MOD_ID, path);
        Advancement adv = player.server.getAdvancements().getAdvancement(loc);
        if (adv != null && !player.getAdvancements().getOrStartProgress(adv).isDone()) {
            player.getAdvancements().award(adv, "discovered");
        }
    }
}
