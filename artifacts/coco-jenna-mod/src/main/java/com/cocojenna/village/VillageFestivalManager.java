package com.cocojenna.village;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 村莊獨立節日（依文化類型，設計書 §6）. */
public final class VillageFestivalManager {

    private VillageFestivalManager() {}

    public static void tickDaily(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        int fPhase = bond.getFestivalPhase();
        if (fPhase > com.cocojenna.endgame.kingdom.FestivalEventManager.PHASE_IDLE
                && fPhase < com.cocojenna.endgame.kingdom.FestivalEventManager.PHASE_ENDED) {
            if (day % 21 == 0 && day != bond.getLastVillageFestivalDay()) {
                player.displayClientMessage(Component.translatable(
                        "village.cocojenna.festival.deferred_palace"), true);
            }
            return;
        }
        if (day % 21 != 0) return;
        if (day == bond.getLastVillageFestivalDay()) return;

        VillageCultureManager.CultureType culture = VillageCultureManager.cultureAt(player);
        bond.setLastVillageFestivalDay(day);
        bond.setActiveVillageFestival(culture.key);

        runFestival(player, bond, culture);
    }

    private static void runFestival(ServerPlayer player, BondData bond,
                                    VillageCultureManager.CultureType culture) {
        switch (culture) {
            case PASTORAL -> {
                bond.addVillageFoodStock(8);
                bond.addKingdomHappiness(6);
            }
            case COASTAL -> {
                bond.addKingdomProsperity(5);
                bond.addReputation("blind_port", 3);
            }
            case INDUSTRIAL -> {
                bond.addKingdomProsperity(8);
                bond.addReputation("gear_town", 4);
            }
            case SCHOLARLY -> bond.addKingdomProsperity(6);
            case MYSTIC -> bond.addReputation("royal", 5);
            case MILITARY -> bond.addVillageDefense(5);
            case TRADE -> bond.addKingdomProsperity(10);
            case FESTIVE -> bond.addKingdomHappiness(12);
        }
        player.serverLevel().sendParticles(ParticleTypes.HAPPY_VILLAGER,
                player.getX(), player.getY() + 1, player.getZ(), 12, 1.5, 0.5, 1.5, 0.02);
        player.displayClientMessage(Component.translatable(
                "village.cocojenna.festival." + culture.key, culture.displayName()), true);
        player.displayClientMessage(Component.translatable(
                "village.cocojenna.festival.reward_hint." + culture.key), true);
    }
}
