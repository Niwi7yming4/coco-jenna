package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;

/**
 * 可可／珍奶核心三軌與姊妹羈絆（從 BondData 水平拆分）.
 */
public final class CatBondCapability {

    private float cocoEmotion;
    private float cocoIndependence = 20f;
    private int cocoAwakening;
    private float cocoProtectiveness = 60f;
    private float cocoMoonAffinity = 40f;
    private float cocoAttachment;
    private float cocoSunbathing;

    private float jennaEmotion;
    private float jennaIndependence = 25f;
    private int jennaAwakening;
    private float jennaPlayfulness = 85f;
    private float jennaCuriosity = 80f;
    private float jennaContentment = 60f;

    private float sisterBond = 65f;
    private int onboardingQuestStep;

    public void copyFrom(BondData bond) {
        cocoEmotion = bond.getCocoEmotion();
        cocoIndependence = bond.getCocoIndependence();
        cocoAwakening = bond.getCocoAwakening();
        cocoProtectiveness = bond.getCocoProtectiveness();
        cocoMoonAffinity = bond.getCocoMoonAffinity();
        cocoAttachment = bond.getCocoAttachment();
        cocoSunbathing = bond.getCocoSunbathing();
        jennaEmotion = bond.getJennaEmotion();
        jennaIndependence = bond.getJennaIndependence();
        jennaAwakening = bond.getJennaAwakening();
        jennaPlayfulness = bond.getJennaPlayfulness();
        jennaCuriosity = bond.getJennaCuriosity();
        jennaContentment = bond.getJennaContentment();
        sisterBond = bond.getSisterBond();
        onboardingQuestStep = bond.getOnboardingQuestStep();
    }

    public void applyTo(BondData bond) {
        bond.setCocoEmotion(cocoEmotion);
        bond.setCocoIndependence(cocoIndependence);
        bond.setCocoAwakening(cocoAwakening);
        bond.setCocoProtectiveness(cocoProtectiveness);
        bond.setCocoMoonAffinity(cocoMoonAffinity);
        bond.setCocoAttachment(cocoAttachment);
        bond.setCocoSunbathing(cocoSunbathing);
        bond.setJennaEmotion(jennaEmotion);
        bond.setJennaIndependence(jennaIndependence);
        bond.setJennaAwakening(jennaAwakening);
        bond.setJennaPlayfulness(jennaPlayfulness);
        bond.setJennaCuriosity(jennaCuriosity);
        bond.setJennaContentment(jennaContentment);
        bond.setSisterBond(sisterBond);
        bond.setOnboardingQuestStep(onboardingQuestStep);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("cocoEmotion", cocoEmotion);
        tag.putFloat("cocoIndependence", cocoIndependence);
        tag.putInt("cocoAwakening", cocoAwakening);
        tag.putFloat("cocoProtectiveness", cocoProtectiveness);
        tag.putFloat("cocoMoonAffinity", cocoMoonAffinity);
        tag.putFloat("cocoAttachment", cocoAttachment);
        tag.putFloat("cocoSunbathing", cocoSunbathing);
        tag.putFloat("jennaEmotion", jennaEmotion);
        tag.putFloat("jennaIndependence", jennaIndependence);
        tag.putInt("jennaAwakening", jennaAwakening);
        tag.putFloat("jennaPlayfulness", jennaPlayfulness);
        tag.putFloat("jennaCuriosity", jennaCuriosity);
        tag.putFloat("jennaContentment", jennaContentment);
        tag.putFloat("sisterBond", sisterBond);
        tag.putInt("onboardingQuestStep", onboardingQuestStep);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return;
        cocoEmotion = tag.getFloat("cocoEmotion");
        cocoIndependence = tag.getFloat("cocoIndependence");
        cocoAwakening = tag.getInt("cocoAwakening");
        cocoProtectiveness = tag.getFloat("cocoProtectiveness");
        cocoMoonAffinity = tag.getFloat("cocoMoonAffinity");
        cocoAttachment = tag.getFloat("cocoAttachment");
        cocoSunbathing = tag.getFloat("cocoSunbathing");
        jennaEmotion = tag.getFloat("jennaEmotion");
        jennaIndependence = tag.getFloat("jennaIndependence");
        jennaAwakening = tag.getInt("jennaAwakening");
        jennaPlayfulness = tag.getFloat("jennaPlayfulness");
        jennaCuriosity = tag.getFloat("jennaCuriosity");
        jennaContentment = tag.getFloat("jennaContentment");
        sisterBond = tag.getFloat("sisterBond");
        onboardingQuestStep = tag.getInt("onboardingQuestStep");
    }
}
