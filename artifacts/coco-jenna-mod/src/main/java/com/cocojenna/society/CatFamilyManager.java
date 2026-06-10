package com.cocojenna.society;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 貓家族樹（設計書 貓之國再深化 §4.6 簡化版）. */
public final class CatFamilyManager {

    private static final String[][] FAMILIES = {
            {"ironpaw", "sanhua", "monk"},
            {"cheshire", "white_glove", "court_lady"},
            {"alpha", "samurai", "ironpaw"},
    };

    private CatFamilyManager() {}

    public static void ensureFamilies(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isCatFamiliesSeeded()) return;
        bond.setCatFamiliesSeeded(true);
        for (int i = 0; i < FAMILIES.length; i++) {
            String familyId = "family_" + (i + 1);
            String[] members = FAMILIES[i];
            for (int m = 0; m < members.length; m++) {
                bond.setTownNpcFamily(members[m], familyId);
                bond.setTownNpcFamilyRole(members[m], m == 0 ? "elder" : m == 1 ? "parent" : "child");
            }
        }
    }

    public static void showFamilyTree(ServerPlayer player) {
        ensureFamilies(player);
        BondData bond = ModCapabilities.getOrDefault(player);
        player.displayClientMessage(Component.translatable("society.cocojenna.family.header"), false);
        for (int i = 0; i < FAMILIES.length; i++) {
            String familyId = "family_" + (i + 1);
            StringBuilder line = new StringBuilder();
            for (TownNpcProfile p : TownNpcProfile.ALL) {
                if (!familyId.equals(bond.getTownNpcFamily(p.id()))) continue;
                if (line.length() > 0) line.append(" · ");
                line.append(p.nameZh());
                String role = bond.getTownNpcFamilyRole(p.id());
                if ("elder".equals(role)) line.append("（長輩）");
            }
            player.displayClientMessage(Component.literal("  " + line), false);
        }
    }

    public static void tickWeeklyGathering(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isCatFamiliesSeeded()) return;
        long day = player.level().getDayTime() / 24000L;
        if (day % 7 != 3 || player.level().getGameTime() % 24000 > 200) return;
        if (day == bond.getLastFamilyGatheringDay()) return;
        bond.setLastFamilyGatheringDay(day);
        bond.addKingdomHappiness(5);
        bond.addVillageFoodStock(4);
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (!bond.getTownNpcFamily(p.id()).isEmpty()) {
                bond.addTownNpcFavor(p.id(), 3);
            }
        }
        player.displayClientMessage(Component.translatable("society.cocojenna.family.gathering"), true);
    }

    public static String familyDialogueSuffix(BondData bond, String npcId) {
        String family = bond.getTownNpcFamily(npcId);
        if (family.isEmpty()) return "";
        return switch (bond.getTownNpcFamilyRole(npcId)) {
            case "elder" -> "elder";
            case "child" -> "child";
            default -> "member";
        };
    }
}
