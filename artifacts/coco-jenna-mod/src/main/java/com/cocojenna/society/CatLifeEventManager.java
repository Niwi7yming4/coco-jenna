package com.cocojenna.society;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.init.ModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 家族生命週期事件：出生／成年／離世／聚會（設計書 §4.6 完整流程）. */
public final class CatLifeEventManager {

    private CatLifeEventManager() {}

    public static void tickDaily(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;

        tickBirth(player, bond, day);
        tickAdulthood(player, bond, day);
        tickFarewell(player, bond, day);
        CatFamilyManager.tickWeeklyGathering(player);
    }

    private static void tickBirth(ServerPlayer player, BondData bond, long day) {
        if (bond.getPregnancyDueDay() < 0 || day < bond.getPregnancyDueDay()) return;
        String partner = bond.getMarriagePartnerNpcId();
        if (partner.isEmpty()) {
            bond.setPregnancyDueDay(-1);
            return;
        }
        TownNpcProfile profile = TownNpcProfile.byId(partner);
        bond.setPregnancyDueDay(-1);
        bond.setKittenCount(bond.getKittenCount() + 1);
        bond.addKingdomHappiness(8);
        bond.setVillagePopulation(bond.getVillagePopulation() + 1);
        String kittenName = CatNpcNamePool.randomName(player.getRandom());
        bond.setLastFamilyEvent("birth_" + kittenName);
        player.displayClientMessage(Component.translatable("society.cocojenna.life.birth",
                kittenName, profile != null ? profile.nameZh() : partner), true);
    }

    private static void tickAdulthood(ServerPlayer player, BondData bond, long day) {
        if (day % 14 != 0) return;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (!bond.isTownNpcRecruited(p.id())) continue;
            if (bond.getTownNpcFavor(p.id()) < 40) continue;
            String key = "adult_" + p.id();
            if (bond.hasFamilyLifeEvent(key)) continue;
            if (bond.getTownNpcFamilyRole(p.id()).equals("child")) {
                bond.markFamilyLifeEvent(key);
                bond.setTownNpcFamilyRole(p.id(), "adult");
                bond.addTownNpcFavor(p.id(), 5);
                player.displayClientMessage(Component.translatable("society.cocojenna.life.adulthood", p.nameZh()), true);
            }
        }
    }

    private static void tickFarewell(ServerPlayer player, BondData bond, long day) {
        if (day % 28 != 0 || player.getRandom().nextInt(5) != 0) return;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (bond.isTownNpcRecruited(p.id())) continue;
            if (bond.getTownNpcFavor(p.id()) < 5) continue;
            String key = "farewell_" + p.id();
            if (bond.hasFamilyLifeEvent(key)) continue;
            bond.markFamilyLifeEvent(key);
            bond.addTownNpcFavor(p.id(), -3);
            player.displayClientMessage(Component.translatable("society.cocojenna.life.farewell", p.nameZh()), true);
            return;
        }
    }
}
