package com.cocojenna.village;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.AfterRainKingdomManager;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 村莊養成面板邏輯（設計書第四章）. */
public final class VillageManager {

    private VillageManager() {}

    public static boolean isInTown(ServerPlayer player) {
        BlockPos p = player.blockPosition();
        return p.distSqr(BlockPos.ZERO) < 120 * 120
                || player.level().dimension().equals(ModDimensions.CAT_KINGDOM);
    }

    public static int computeHappiness(BondData bond) {
        int h = 50;
        if (bond.getVillageFoodStock() >= 10) h += 10;
        if (bond.getVillagePopulation() <= bond.getVillageHousingCapacity()) h += 5;
        h += Math.min(20, bond.getPlacedBuildingCount());
        h += 15; // 雙子星在國度內（簡化：玩家在貓之國即視為同在）
        if (bond.getVillageDefense() < 15) h -= 5;
        h += bond.getKingdomHappiness() / 10;
        return Math.max(0, Math.min(100, h));
    }

    public static void syncHappiness(BondData bond) {
        bond.setKingdomHappiness(computeHappiness(bond));
    }

    public static void showPanel(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        syncHappiness(bond);
        player.displayClientMessage(Component.translatable("village.cocojenna.header"), false);
        player.displayClientMessage(Component.translatable("village.cocojenna.happiness",
                bond.getKingdomHappiness()), false);
        player.displayClientMessage(Component.translatable("village.cocojenna.population",
                bond.getVillagePopulation(), bond.getVillageHousingCapacity()), false);
        player.displayClientMessage(Component.translatable("village.cocojenna.food",
                bond.getVillageFoodStock()), false);
        player.displayClientMessage(Component.translatable("village.cocojenna.defense",
                bond.getVillageDefense()), false);
        player.displayClientMessage(Component.translatable("village.cocojenna.buildings",
                bond.getPlacedBuildingCount()), false);
        if (com.cocojenna.endgame.AfterRainGameplayManager.isPeaceMode(player)) {
            player.displayClientMessage(Component.translatable("village.cocojenna.hub_hint"), true);
            AfterRainKingdomManager.openHub(player);
        }
    }

    public static void tickDaily(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        if (bond.getLastVillageTickDay() == day) return;
        bond.setLastVillageTickDay(day);

        int houses = countHousing(bond);
        bond.setVillageHousingCapacity(Math.max(4, houses * 2));
        bond.addVillageFoodStock(Math.max(1, bond.getVillagePopulation() / 2));
        syncHappiness(bond);

        if (day > 0 && day % 7 == 0 && player.getRandom().nextFloat() < 0.4f) {
            if (!com.cocojenna.endgame.AfterRainGameplayManager.isPeaceMode(player)) {
                tryDefenseEvent(player, bond);
            }
        }
        if (com.cocojenna.endgame.AfterRainGameplayManager.isPeaceMode(player)) {
            com.cocojenna.society.CatSocietyManager.tryStrayRecruit(player);
        }
    }

    private static int countHousing(BondData bond) {
        int n = 0;
        if (bond.isBuildingPlaced("small_cat_house")) n++;
        if (bond.isBuildingPlaced("cat_paradise")) n++;
        return n + bond.getPlacedBuildingCount() / 3;
    }

    private static void tryDefenseEvent(ServerPlayer player, BondData bond) {
        int defense = bond.getVillageDefense();
        boolean win = defense + player.getRandom().nextInt(30) > 20;
        if (win) {
            bond.addKingdomHappiness(10);
            player.displayClientMessage(Component.translatable("village.cocojenna.defense_win"), true);
            if (!player.addItem(new net.minecraft.world.item.ItemStack(
                    com.cocojenna.init.ModItems.BLACK_MUD_REMNANT.get(), 4))) {
                player.drop(new net.minecraft.world.item.ItemStack(
                        com.cocojenna.init.ModItems.BLACK_MUD_REMNANT.get(), 4), false);
            }
        } else {
            bond.addKingdomHappiness(-20);
            bond.addVillageFoodStock(-5);
            player.displayClientMessage(Component.translatable("village.cocojenna.defense_lose"), true);
        }
    }

    public static void onBuildingPlaced(BondData bond, String blueprintId) {
        switch (blueprintId) {
            case "small_cat_house", "cat_paradise" -> bond.addVillageHousingCapacity(2);
            case "distiller_station" -> bond.addVillageFoodStock(20);
            case "pure_light_tower_build" -> bond.addVillageDefense(10);
            default -> {}
        }
        syncHappiness(bond);
    }
}
