package com.cocojenna.society;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** MCA 戀愛／求婚／育兒（設計書 貓之國再深化 §4.5–4.6）. */
public final class CatMarriageManager {

    public static final int ROMANCE_FRIEND = 1;
    public static final int ROMANCE_CLOSE = 2;
    public static final int ROMANCE_CONFIDANT = 3;
    public static final int ROMANCE_DATING = 4;
    public static final int ROMANCE_ENGAGED = 5;
    public static final int ROMANCE_MARRIED = 6;

    private static final int PREGNANCY_DAYS = 7;

    private CatMarriageManager() {}

    public static void onCompanionInteract(ServerPlayer player, String npcId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int stage = bond.getTownNpcRomanceStage(npcId);
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile == null) return;

        if (stage >= ROMANCE_CONFIDANT && stage < ROMANCE_DATING
                && bond.getTownNpcFavor(npcId) >= 80) {
            bond.setTownNpcRomanceStage(npcId, ROMANCE_DATING);
            player.displayClientMessage(Component.translatable(
                    "society.cocojenna.romance.dating", profile.nameZh()), true);
            return;
        }
        if (stage == ROMANCE_DATING && hasRing(player)) {
            tryPropose(player, bond, npcId, profile);
        } else if (stage >= ROMANCE_MARRIED) {
            player.displayClientMessage(Component.translatable(
                    "society.cocojenna.romance.spouse", profile.nameZh()), true);
        }
    }

    public static boolean tryPropose(ServerPlayer player, String npcId) {
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile == null) return false;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getTownNpcRomanceStage(npcId) < ROMANCE_DATING) return false;
        if (!hasRing(player)) return false;
        return tryPropose(player, bond, npcId, profile);
    }

    private static boolean tryPropose(ServerPlayer player, BondData bond, String npcId, TownNpcProfile profile) {
        if (bond.getTownNpcFavor(npcId) < 90) {
            player.displayClientMessage(Component.translatable("society.cocojenna.romance.need_favor"), true);
            return false;
        }
        if (!player.getAbilities().instabuild) {
            consumeRing(player);
        }
        bond.setTownNpcRomanceStage(npcId, ROMANCE_ENGAGED);
        bond.setMarriagePartnerNpcId(npcId);
        long day = player.level().getDayTime() / 24000L;
        bond.setEngagementDay(day);
        DialogueManager.play(player, "gal_marriage_proposal");
        player.displayClientMessage(Component.translatable(
                "society.cocojenna.romance.engaged", profile.nameZh()), true);
        scheduleWedding(player, bond, npcId, profile, day);
        return true;
    }

    private static void scheduleWedding(ServerPlayer player, BondData bond, String npcId,
                                        TownNpcProfile profile, long day) {
        bond.setWeddingScheduledDay(day + 3);
        player.displayClientMessage(Component.translatable(
                "society.cocojenna.romance.wedding_scheduled", profile.nameZh()), true);
    }

    public static void tryCompleteWedding(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        String partner = bond.getMarriagePartnerNpcId();
        if (partner.isEmpty()) return;
        long day = player.level().getDayTime() / 24000L;
        if (bond.getTownNpcRomanceStage(partner) != ROMANCE_ENGAGED) return;
        if (day < bond.getWeddingScheduledDay()) return;
        bond.setTownNpcRomanceStage(partner, ROMANCE_MARRIED);
        bond.addKingdomHappiness(10);
        DialogueManager.play(player, "gal_marriage_wedding");
        TownNpcProfile profile = TownNpcProfile.byId(partner);
        String name = profile != null ? profile.nameZh() : partner;
        player.displayClientMessage(Component.translatable("society.cocojenna.romance.wedding", name), true);
    }

    public static void tickDaily(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        tryCompleteWedding(player);
        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;

        String partner = bond.getMarriagePartnerNpcId();
        if (!partner.isEmpty()) {
            int stage = bond.getTownNpcRomanceStage(partner);
            if (stage == ROMANCE_ENGAGED && day - bond.getEngagementDay() >= 3) {
                bond.setTownNpcRomanceStage(partner, ROMANCE_MARRIED);
                bond.addKingdomHappiness(15);
                TownNpcProfile married = TownNpcProfile.byId(partner);
                if (married != null) {
                    player.displayClientMessage(Component.translatable(
                            "society.cocojenna.romance.married", married.nameZh()), true);
                }
            }
            if (stage >= ROMANCE_MARRIED && bond.getPregnancyDueDay() < 0
                    && bond.isBuildingPlaced("small_cat_house")
                    && player.getRandom().nextFloat() < 0.12f) {
                bond.setPregnancyDueDay(day + PREGNANCY_DAYS);
                DialogueManager.play(player, "gal_pregnancy");
                player.displayClientMessage(Component.translatable(
                        "society.cocojenna.family.expecting"), true);
            }
            if (bond.getPregnancyDueDay() >= 0 && day >= bond.getPregnancyDueDay()) {
                bond.setPregnancyDueDay(-1);
                bond.setKittenCount(bond.getKittenCount() + 1);
                bond.setVillagePopulation(bond.getVillagePopulation() + 1);
                bond.addKingdomHappiness(10);
                player.displayClientMessage(Component.translatable("society.cocojenna.family.born"), true);
            }
        }

        CatFamilyManager.tickWeeklyGathering(player);
    }

    private static boolean hasRing(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(ModItems.CAT_SOUL_RING.get())) return true;
        }
        return false;
    }

    private static void consumeRing(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.CAT_SOUL_RING.get())) {
                stack.shrink(1);
                return;
            }
        }
    }
}
