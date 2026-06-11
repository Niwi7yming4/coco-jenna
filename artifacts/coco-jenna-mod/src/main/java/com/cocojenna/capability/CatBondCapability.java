package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 可可／珍奶核心三軌與姊妹羈絆（BondData 委派儲存）.
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

    private final Map<UUID, Float> personalCocoAffection = new HashMap<>();
    private final Map<UUID, Float> personalJennaAffection = new HashMap<>();
    private final Map<UUID, Long> lastDeepInteractMs = new HashMap<>();

    public float getCocoEmotion() { return cocoEmotion; }
    public void setCocoEmotion(float v) { cocoEmotion = v; }
    public float getCocoIndependence() { return cocoIndependence; }
    public void setCocoIndependence(float v) { cocoIndependence = v; }
    public int getCocoAwakening() { return cocoAwakening; }
    public void setCocoAwakening(int v) { cocoAwakening = v; }
    public float getCocoProtectiveness() { return cocoProtectiveness; }
    public void setCocoProtectiveness(float v) { cocoProtectiveness = v; }
    public float getCocoMoonAffinity() { return cocoMoonAffinity; }
    public void setCocoMoonAffinity(float v) { cocoMoonAffinity = v; }
    public float getCocoAttachment() { return cocoAttachment; }
    public void setCocoAttachment(float v) { cocoAttachment = v; }
    public float getCocoSunbathing() { return cocoSunbathing; }
    public void setCocoSunbathing(float v) { cocoSunbathing = v; }
    public float getJennaEmotion() { return jennaEmotion; }
    public void setJennaEmotion(float v) { jennaEmotion = v; }
    public float getJennaIndependence() { return jennaIndependence; }
    public void setJennaIndependence(float v) { jennaIndependence = v; }
    public int getJennaAwakening() { return jennaAwakening; }
    public void setJennaAwakening(int v) { jennaAwakening = v; }
    public float getJennaPlayfulness() { return jennaPlayfulness; }
    public void setJennaPlayfulness(float v) { jennaPlayfulness = v; }
    public float getJennaCuriosity() { return jennaCuriosity; }
    public void setJennaCuriosity(float v) { jennaCuriosity = v; }
    public float getJennaContentment() { return jennaContentment; }
    public void setJennaContentment(float v) { jennaContentment = v; }
    public float getSisterBond() { return sisterBond; }
    public void setSisterBond(float v) { sisterBond = v; }
    public int getOnboardingQuestStep() { return onboardingQuestStep; }
    public void setOnboardingQuestStep(int v) { onboardingQuestStep = v; }

    public float getPersonalCocoAffection(UUID playerId) {
        return personalCocoAffection.getOrDefault(playerId, 0f);
    }

    public float getPersonalJennaAffection(UUID playerId) {
        return personalJennaAffection.getOrDefault(playerId, 0f);
    }

    public void addPersonalCocoAffection(UUID playerId, float delta) {
        float next = Math.max(0, Math.min(100, getPersonalCocoAffection(playerId) + delta));
        personalCocoAffection.put(playerId, next);
    }

    public void addPersonalJennaAffection(UUID playerId, float delta) {
        float next = Math.max(0, Math.min(100, getPersonalJennaAffection(playerId) + delta));
        personalJennaAffection.put(playerId, next);
    }

    public long getLastDeepInteractMs(UUID playerId) {
        return lastDeepInteractMs.getOrDefault(playerId, 0L);
    }

    public void setLastDeepInteractMs(UUID playerId, long ms) {
        lastDeepInteractMs.put(playerId, ms);
    }

    public Map<UUID, Float> snapshotPersonalCoco() { return new HashMap<>(personalCocoAffection); }
    public Map<UUID, Float> snapshotPersonalJenna() { return new HashMap<>(personalJennaAffection); }

    public void copyFrom(CatBondCapability other) {
        cocoEmotion = other.cocoEmotion;
        cocoIndependence = other.cocoIndependence;
        cocoAwakening = other.cocoAwakening;
        cocoProtectiveness = other.cocoProtectiveness;
        cocoMoonAffinity = other.cocoMoonAffinity;
        cocoAttachment = other.cocoAttachment;
        cocoSunbathing = other.cocoSunbathing;
        jennaEmotion = other.jennaEmotion;
        jennaIndependence = other.jennaIndependence;
        jennaAwakening = other.jennaAwakening;
        jennaPlayfulness = other.jennaPlayfulness;
        jennaCuriosity = other.jennaCuriosity;
        jennaContentment = other.jennaContentment;
        sisterBond = other.sisterBond;
        onboardingQuestStep = other.onboardingQuestStep;
        personalCocoAffection.clear();
        personalCocoAffection.putAll(other.personalCocoAffection);
        personalJennaAffection.clear();
        personalJennaAffection.putAll(other.personalJennaAffection);
        lastDeepInteractMs.clear();
        lastDeepInteractMs.putAll(other.lastDeepInteractMs);
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
        tag.put("personalCoco", serializeMap(personalCocoAffection));
        tag.put("personalJenna", serializeMap(personalJennaAffection));
        return tag;
    }

    private static CompoundTag serializeMap(Map<UUID, Float> map) {
        CompoundTag tag = new CompoundTag();
        map.forEach((uuid, val) -> tag.putFloat(uuid.toString(), val));
        return tag;
    }

    private static void deserializeMap(CompoundTag tag, Map<UUID, Float> map) {
        map.clear();
        if (tag == null || tag.isEmpty()) return;
        for (String key : tag.getAllKeys()) {
            map.put(UUID.fromString(key), tag.getFloat(key));
        }
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
        if (tag.contains("personalCoco")) deserializeMap(tag.getCompound("personalCoco"), personalCocoAffection);
        if (tag.contains("personalJenna")) deserializeMap(tag.getCompound("personalJenna"), personalJennaAffection);
    }
}
