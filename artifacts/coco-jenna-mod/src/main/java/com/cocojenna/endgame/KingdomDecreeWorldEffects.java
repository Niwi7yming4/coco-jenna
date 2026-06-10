package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.CatLibraryManager;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** 法令對世界的實際影響（雨後 Ch.9 完整效果）. */
public final class KingdomDecreeWorldEffects {

    private KingdomDecreeWorldEffects() {}

    public static float blackMudSpreadMultiplier(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return 1f;
        float mult = 1f;
        mult *= com.cocojenna.sequence.HiddenSequenceBonuses.corruptionSpreadMultiplier(level);
        if (!AfterRainManager.isAfterRain(level)) return mult;
        for (ServerPlayer player : level.players()) {
            mult *= decreeSpreadMult(ModCapabilities.getOrDefault(player));
        }
        return mult;
    }

    private static float decreeSpreadMult(BondData bond) {
        float m = 1f;
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            switch (d.id()) {
                case "eternal_spring", "garden_bloom" -> m *= 0.85f;
                case "peace" -> m *= 0.90f;
                default -> {}
            }
        }
        return m;
    }

    public static float mpsYieldMultiplier(BondData bond) {
        float m = 1f;
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            if ("harvest_boost".equals(d.id())) m *= 1.30f;
            if ("festival_night".equals(d.id())) m *= 0.80f;
            if ("furball_play".equals(d.id())) m *= 0.50f;
            if ("twin_blessing".equals(d.id())) m *= 1.50f;
        }
        return m;
    }

    public static float buildingCostMultiplier(BondData bond) {
        float m = 1f;
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            if ("builder_rush".equals(d.id())) m *= 0.80f;
            if ("rest_day".equals(d.id())) m *= 1.10f;
        }
        if (hasDecree(bond, "twin_blessing")) m *= 0.85f;
        return m;
    }

    public static int warehouseExtraCapacity(BondData bond) {
        int bonus = bond.getWarehouseBonus();
        if (hasDecree(bond, "open_market")) bonus += 25;
        if (hasDecree(bond, "trade_fair")) bonus += 15;
        return bonus;
    }

    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (!AfterRainManager.isAfterRain(level)) return;
        if (level.getGameTime() % 1200 != 0) return;

        for (ServerPlayer player : level.players()) {
            tickPlayerDecrees(player, ModCapabilities.getOrDefault(player));
        }
    }

    public static void tickPlayerDecrees(ServerPlayer player, BondData bond) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;

        boolean music = false;
        boolean harvest = false;
        boolean festival = false;
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            switch (d.id()) {
                case "harvest_boost" -> {
                    harvest = true;
                    bond.addNpcFatigue(2);
                }
                case "festival_night" -> {
                    festival = true;
                    music = true;
                    bond.addKingdomHappiness(1);
                    bond.addNpcFatigue(1);
                }
                case "curfew" -> bond.addKingdomStability(1);
                case "isolation" -> bond.addKingdomHappiness(-1);
                case "open_market" -> {
                    bond.setWarehouseBonus(50);
                    tryImmigration(player, bond);
                }
                case "trade_fair" -> bond.addKingdomProsperity(1);
                case "memory_archive", "scholar_hour" -> bond.addBuildCreativity(1);
                case "picture_week" -> bond.modifyJennaEmotion(0.1f);
                case "chef_praise" -> bond.addVillageFoodStock(1);
                case "star_gazing", "music_decree" -> {
                    music = true;
                    if ("star_gazing".equals(d.id())) bond.modifyCocoEmotion(0.05f);
                    else bond.addKingdomHappiness(1);
                }
                case "garden_bloom" -> bond.addKingdomHappiness(1);
                case "furball_play" -> {
                    bond.setKingdomHappiness(Math.max(bond.getKingdomHappiness(), 95));
                    applyJennaPlayDecree(player, bond);
                }
                case "twin_blessing" -> applyTwinBlessing(player, bond);
                case "royal_parade" -> {
                    bond.addKingdomReputation(3);
                    bond.addKingdomHappiness(5);
                }
                case "immigration_decree" -> tryImmigration(player, bond);
                default -> {}
            }
        }

        if (harvest && bond.getNpcFatigue() > 80) {
            bond.addKingdomHappiness(-1);
        }
        if (music && player.level().getGameTime() % 2400 == 0) {
            player.level().playSound(null, player.blockPosition(),
                    ModSounds.WORLD_FULL_MOON_FESTIVAL.get(), SoundSource.AMBIENT, 0.6f, 1.1f);
            bond.addKingdomHappiness(1);
        }
        if (festival && player.getRandom().nextFloat() < 0.15f) {
            bond.addKingdomHappiness(1);
        }

        applyCocoGuardianDecree(player, bond);
    }

    private static void applyCocoGuardianDecree(ServerPlayer player, BondData bond) {
        if (!hasDecree(bond, "eternal_spring")) return;
        if (!CatLibraryManager.catsPresentForDecree(player)) return;
        for (var e : player.level().getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(20))) {
            if (player.getUUID().equals(e.getOwnerUUID())) {
                player.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 200, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, true, false));
            }
        }
    }

    private static void applyJennaPlayDecree(ServerPlayer player, BondData bond) {
        for (var e : player.level().getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(20))) {
            if (player.getUUID().equals(e.getOwnerUUID())) {
                e.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 1, true, false));
            }
        }
    }

    private static void applyTwinBlessing(ServerPlayer player, BondData bond) {
        if (!CatLibraryManager.bothCatsPresent(player)) return;
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 400, 0, true, false));
        bond.modifyCocoEmotion(0.2f);
        bond.modifyJennaEmotion(0.2f);
    }

    private static void tryImmigration(ServerPlayer player, BondData bond) {
        if (player.getRandom().nextFloat() > 0.08f) return;
        if (bond.getVillagePopulation() >= bond.getVillageHousingCapacity()) return;
        bond.setVillagePopulation(bond.getVillagePopulation() + 1);
        bond.addKingdomHappiness(3);
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                "decree.cocojenna.immigration_arrived"), true);
    }

    public static boolean hasDecree(BondData bond, String id) {
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            if (d.id().equals(id)) return true;
        }
        return false;
    }
}
