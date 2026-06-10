package com.cocojenna.guide;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/** 守護者指南圖鑑解鎖進度（守護者.md）. */
public final class GuardianGuideProgress {

    public static final int HEAT_LEECH = 1;
    public static final int GRIEF_AMALGAM = 2;
    public static final int BLIND_WATER_LORD = 4;
    public static final int FALLEN_VELVET = 8;
    public static final int PRIMAL_CHAOS = 16;
    public static final int TAPE_COLOSSUS = 32;
    public static final int CATNIP_DRAGON = 64;
    public static final int SILENCED_ONE = 128;
    public static final int MOON_GUARDIAN = 256;
    public static final int WANDERING_SLUDGE = 512;
    public static final int MUD_FARMER = 1024;
    public static final int MUD_GUARD = 2048;
    public static final int MUD_PRIEST = 4096;

    private GuardianGuideProgress() {}

    public static void onMobKill(ServerPlayer player, Entity entity) {
        int flag = flagFor(entity);
        if (flag == 0) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.hasGuardianDiscovery(flag)) return;
        bond.addGuardianDiscovery(flag);
        GuideAdvancementHelper.onDiscovery(player, flag);
        player.displayClientMessage(Component.translatable(
                "guide.cocojenna.discovered", entity.getType().getDescription()), true);
    }

    private static int flagFor(Entity entity) {
        String name = entity.getType().getDescriptionId();
        if (name.contains("heat_leech")) return HEAT_LEECH;
        if (name.contains("grief_amalgam")) return GRIEF_AMALGAM;
        if (name.contains("blind_water_lord")) return BLIND_WATER_LORD;
        if (name.contains("fallen_velvet")) return FALLEN_VELVET;
        if (name.contains("primal_chaos")) return PRIMAL_CHAOS;
        if (name.contains("tape_colossus")) return TAPE_COLOSSUS;
        if (name.contains("catnip_dragon")) return CATNIP_DRAGON;
        if (name.contains("silenced_one")) return SILENCED_ONE;
        if (name.contains("moon_guardian")) return MOON_GUARDIAN;
        if (name.contains("wandering_sludge")) return WANDERING_SLUDGE;
        if (name.contains("mud_farmer")) return MUD_FARMER;
        if (name.contains("mud_guard")) return MUD_GUARD;
        if (name.contains("mud_priest")) return MUD_PRIEST;
        return 0;
    }
}
