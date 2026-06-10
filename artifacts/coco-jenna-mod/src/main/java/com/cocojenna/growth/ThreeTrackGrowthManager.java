package com.cocojenna.growth;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** 三軌成長深度：情感／獨立／覺醒（貓之國設計書 Ch.3–4）. */
public final class ThreeTrackGrowthManager {

    private static final float DAILY_PET_CAP = 8f;

    private ThreeTrackGrowthManager() {}

    public static void tickPlayer(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        if (bond.getGrowthTickDay() == day) return;
        bond.setGrowthTickDay(day);
        bond.setDailyEmotionGain(0f);

        if (day - bond.getLastPetCocoDay() >= 3) {
            bond.modifyCocoEmotion(-2f);
        }
        if (day - bond.getLastPetJennaDay() >= 3) {
            bond.modifyJennaEmotion(-2f);
        }

        applyAwakeningPerks(player, bond);
    }

    public static void onPet(ServerPlayer player, boolean coco) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getDailyEmotionGain() >= DAILY_PET_CAP) {
            player.displayClientMessage(Component.translatable("growth.cocojenna.pet_cap"), true);
            return;
        }
        long day = player.level().getDayTime() / 24000L;
        if (coco) {
            bond.modifyCocoEmotion(1.5f);
            bond.setLastPetCocoDay(day);
        } else {
            bond.modifyJennaEmotion(1.5f);
            bond.setLastPetJennaDay(day);
        }
        bond.setDailyEmotionGain(bond.getDailyEmotionGain() + 1.5f);
        bond.modifySisterBond(0.2f);
    }

    public static int followRadiusBlocks(BondData bond, boolean coco) {
        float indep = coco ? bond.getCocoIndependence() : bond.getJennaIndependence();
        for (BondData.IndependenceLevel lvl : BondData.IndependenceLevel.values()) {
            if (indep >= lvl.min && indep <= lvl.max) {
                return Math.min(lvl.radius, 128);
            }
        }
        return 16;
    }

    public static double followDistanceSq(BondData bond, boolean coco, int followMode) {
        int radius = followRadiusBlocks(bond, coco);
        if (followMode == 1) radius = Math.max(radius, 8);
        if (followMode == 2) radius = Math.max(radius * 2, 12);
        return (double) radius * radius;
    }

    public static void onMemoryShard(BondData bond, ServerPlayer player) {
        int total = bond.getMemoryShardsTotal();
        bond.setCocoAwakening(Math.min(50, total));
        bond.setJennaAwakening(Math.min(50, total));
        if (player.getRandom().nextFloat() < 0.25f) {
            bond.modifyCocoIndependence(0.3f);
            bond.modifyJennaIndependence(0.3f);
        }
    }

    private static void applyAwakeningPerks(ServerPlayer player, BondData bond) {
        int cocoPhase = awakeningPhase(bond.getCocoAwakening());
        int jennaPhase = awakeningPhase(bond.getJennaAwakening());

        if (cocoPhase >= 2 && player.tickCount % 100 == 0) {
            for (var e : player.level().getEntitiesOfClass(CocoEntity.class,
                    player.getBoundingBox().inflate(24))) {
                if (player.getUUID().equals(e.getOwnerUUID())) {
                    e.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, true, false));
                }
            }
        }
        if (jennaPhase >= 3 && player.tickCount % 100 == 0) {
            player.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 80, 0, true, false));
        }
        if (cocoPhase >= 4 && jennaPhase >= 4 && bond.getSisterBond() >= 60) {
            player.addEffect(new MobEffectInstance(ModEffects.WARM_SERENITY.get(), 100, 0, true, false));
        }
    }

    public static int awakeningPhase(int awakening) {
        if (awakening >= 50) return 6;
        if (awakening >= 40) return 5;
        if (awakening >= 30) return 4;
        if (awakening >= 20) return 3;
        if (awakening >= 12) return 2;
        if (awakening >= 5) return 1;
        return 0;
    }

    public static void tickCats(AbstractCatEntity cat, ServerPlayer owner) {
        BondData bond = ModCapabilities.getOrDefault(owner);
        boolean coco = cat instanceof CocoEntity;
        int phase = awakeningPhase(coco ? bond.getCocoAwakening() : bond.getJennaAwakening());
        if (phase >= 5 && cat.tickCount % 200 == 0 && cat.getRandom().nextFloat() < 0.05f) {
            cat.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false));
        }
    }
}
