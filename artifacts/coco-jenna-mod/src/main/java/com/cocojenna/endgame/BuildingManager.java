package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.sequence.HiddenSequenceBonuses;
import com.cocojenna.world.BuildingPlacer;
import com.cocojenna.world.KingdomBuildSavedData;
import com.cocojenna.world.MonumentGrowthManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

/** 建造藍圖進度 — 消耗創意值 + BOM，貓之國全域共享（多人）. */
public final class BuildingManager {

    private BuildingManager() {}

    public static void openCoreEngineering(ServerPlayer player) {
        syncKingdomBuildToPlayer(player);
        var bond = ModCapabilities.getOrDefault(player);
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new com.cocojenna.network.OpenCatCoreEngineeringPacket(bond.serializeNBT()));
    }

    public static void syncKingdomBuildToPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        KingdomBuildSavedData world = KingdomBuildSavedData.get(player.serverLevel());
        BondData bond = ModCapabilities.getOrDefault(player);
        for (BuildingBlueprintCatalog.Blueprint bp : BuildingBlueprintCatalog.all()) {
            world.mergeProgress(bp.id(), bond.getBuildingProgress(bp.id()));
            if (bond.isBuildingPlaced(bp.id())) {
                world.markBuildingPlaced(bp.id());
            }
        }
        world.syncToBond(bond);
    }

    public static void syncKingdomBuildToAll(ServerLevel level) {
        KingdomBuildSavedData world = KingdomBuildSavedData.get(level);
        for (ServerPlayer p : level.players()) {
            BondData bond = ModCapabilities.getOrDefault(p);
            world.syncToBond(bond);
            ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> p),
                    new SyncBondDataPacket(bond.serializeNBT()));
        }
    }

    public static boolean contribute(ServerPlayer player, String buildingId) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return false;
        BuildingBlueprintCatalog.Blueprint bp = BuildingBlueprintCatalog.get(buildingId);
        if (bp == null) return false;

        ServerLevel level = player.serverLevel();
        KingdomBuildSavedData world = KingdomBuildSavedData.get(level);
        int prog = world.getBuildingProgress(buildingId);
        if (prog >= bp.requiredProgress()) {
            player.displayClientMessage(Component.translatable("building.cocojenna.complete"), true);
            return false;
        }

        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getBuildCreativity() < 5) {
            player.displayClientMessage(Component.translatable("building.cocojenna.need_creativity"), true);
            return false;
        }

        int steps = Math.max(1, bp.requiredProgress() / 8);
        for (Map.Entry<String, Integer> e : bp.bom().entrySet()) {
            int perStepBase = Math.max(1, (e.getValue() + steps - 1) / steps);
            int perStep = Math.max(1, Math.round(perStepBase * KingdomDecreeWorldEffects.buildingCostMultiplier(bond)));
            if (!BuildingMaterialHelper.has(player, e.getKey(), perStep)) {
                player.displayClientMessage(Component.translatable(
                        "building.cocojenna.need_material",
                        Component.translatable("material.cocojenna." + e.getKey())), true);
                return false;
            }
        }
        for (Map.Entry<String, Integer> e : bp.bom().entrySet()) {
            int perStepBase = Math.max(1, (e.getValue() + steps - 1) / steps);
            int perStep = Math.max(1, Math.round(perStepBase * KingdomDecreeWorldEffects.buildingCostMultiplier(bond)));
            BuildingMaterialHelper.consume(player, e.getKey(), perStep);
        }

        bond.addBuildCreativity(-5);
        int gain = 8 + HiddenSequenceBonuses.buildProgressBonus(bond);
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            if ("builder_rush".equals(d.id())) gain += 2;
        }
        world.setBuildingProgress(buildingId, prog + gain);
        bond.setBuildingProgress(buildingId, world.getBuildingProgress(buildingId));
        bond.addKingdomProsperity(1);

        if (world.getBuildingProgress(buildingId) >= bp.requiredProgress()) {
            onBlueprintFinished(player, bond, bp);
        } else {
            player.displayClientMessage(Component.translatable("building.cocojenna.progress",
                    world.getBuildingProgress(buildingId), bp.requiredProgress()), true);
        }
        syncKingdomBuildToAll(level);
        return true;
    }

    public static boolean place(ServerPlayer player, String buildingId) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return false;
        BuildingBlueprintCatalog.Blueprint bp = BuildingBlueprintCatalog.get(buildingId);
        if (bp == null) return false;

        ServerLevel level = player.serverLevel();
        KingdomBuildSavedData world = KingdomBuildSavedData.get(level);
        if (world.getBuildingProgress(buildingId) < bp.requiredProgress()) {
            player.displayClientMessage(Component.translatable("build.cocojenna.not_ready",
                    world.getBuildingProgress(buildingId), bp.requiredProgress()), true);
            return false;
        }
        if (world.isBuildingPlaced(buildingId)) {
            player.displayClientMessage(Component.translatable("build.cocojenna.already"), true);
            return false;
        }
        if (!BuildingPlacer.place(player, buildingId)) return false;

        world.markBuildingPlaced(buildingId);
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setBuildingPlaced(buildingId);
        com.cocojenna.village.VillageManager.onBuildingPlaced(bond, buildingId);
        syncKingdomBuildToAll(level);
        return true;
    }

    private static void onBlueprintFinished(ServerPlayer player, BondData bond,
            BuildingBlueprintCatalog.Blueprint bp) {
        player.displayClientMessage(Component.translatable("building.cocojenna.finished", bp.name()), true);
        player.displayClientMessage(Component.translatable("build.cocojenna.hint", bp.id()), true);
        bond.addKingdomHappiness(5);
        if (bp.forgeLevelGain() > 0) {
            int next = Math.min(3, bond.getIronpawForgeLevel() + bp.forgeLevelGain());
            bond.setIronpawForgeLevel(next);
            player.displayClientMessage(Component.translatable(
                    "building.cocojenna.forge_upgraded", next), true);
        }
        if (bp.id().equals("memory_monument_tier2")) {
            MonumentGrowthManager.onShardsUpdated(player.serverLevel(), bond.getMemoryShardsTotal());
        }
        if (bp.id().equals("memory_theater")) {
            player.displayClientMessage(Component.translatable("building.cocojenna.memory_theater.hint"), true);
        }
    }
}
