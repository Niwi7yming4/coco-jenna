package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 序列、儀式、印記、晉升卡牌（BondData 委派儲存）. */
public final class SequenceCapability {

    private long unlockedSequences;
    private String felineForce = "";
    private int felineTier = 9;
    private long felineSkillCooldownUntil;
    private int promotionCardCount;
    private float promotionCardBonus;
    private final List<String> ownedPromotionCards = new ArrayList<>();
    private int pendingPromotionTier;
    private int ceremonyStage;
    private int ceremonyTimeout;
    private long ceremonyStageStartGameTime;
    private int markLevel;
    private String markForce = "";
    private boolean simplifiedCeremony;
    private long hiddenSequences;
    private int awakeningTrialTier;
    private boolean awakeningTrialActive;
    private int awakeningTrialIndex;
    private int awakeningTrialKills;
    private int awakeningTrialGoal;
    private long awakeningTrialDeadline;
    private int memoryShardsTotal;

    public long getUnlockedSequences() { return unlockedSequences; }
    public void setUnlockedSequences(long v) { unlockedSequences = v; }
    public String getFelineForce() { return felineForce; }
    public void setFelineForce(String v) { felineForce = v == null ? "" : v; }
    public int getFelineTier() { return felineTier; }
    public void setFelineTier(int v) { felineTier = v; }
    public long getFelineSkillCooldownUntil() { return felineSkillCooldownUntil; }
    public void setFelineSkillCooldownUntil(long v) { felineSkillCooldownUntil = v; }
    public int getPromotionCardCount() { return promotionCardCount; }
    public void setPromotionCardCount(int v) { promotionCardCount = v; }
    public float getPromotionCardBonus() { return promotionCardBonus; }
    public void setPromotionCardBonus(float v) { promotionCardBonus = v; }
    public List<String> getOwnedPromotionCards() { return ownedPromotionCards; }
    public void replaceOwnedPromotionCards(List<String> cards) {
        ownedPromotionCards.clear();
        ownedPromotionCards.addAll(cards);
        promotionCardCount = ownedPromotionCards.size();
    }
    public int getPendingPromotionTier() { return pendingPromotionTier; }
    public void setPendingPromotionTier(int v) { pendingPromotionTier = v; }
    public int getCeremonyStage() { return ceremonyStage; }
    public void setCeremonyStage(int v) { ceremonyStage = v; }
    public int getCeremonyTimeout() { return ceremonyTimeout; }
    public void setCeremonyTimeout(int v) { ceremonyTimeout = v; }
    public long getCeremonyStageStartGameTime() { return ceremonyStageStartGameTime; }
    public void setCeremonyStageStartGameTime(long v) { ceremonyStageStartGameTime = v; }
    public int getMarkLevel() { return markLevel; }
    public void setMarkLevel(int v) { markLevel = v; }
    public String getMarkForce() { return markForce; }
    public void setMarkForce(String v) { markForce = v == null ? "" : v; }
    public boolean isSimplifiedCeremony() { return simplifiedCeremony; }
    public void setSimplifiedCeremony(boolean v) { simplifiedCeremony = v; }
    public long getHiddenSequences() { return hiddenSequences; }
    public void setHiddenSequences(long v) { hiddenSequences = v; }
    public int getAwakeningTrialTier() { return awakeningTrialTier; }
    public void setAwakeningTrialTier(int v) { awakeningTrialTier = v; }
    public boolean isAwakeningTrialActive() { return awakeningTrialActive; }
    public void setAwakeningTrialActive(boolean v) { awakeningTrialActive = v; }
    public int getAwakeningTrialIndex() { return awakeningTrialIndex; }
    public void setAwakeningTrialIndex(int v) { awakeningTrialIndex = v; }
    public int getAwakeningTrialKills() { return awakeningTrialKills; }
    public void setAwakeningTrialKills(int v) { awakeningTrialKills = v; }
    public int getAwakeningTrialGoal() { return awakeningTrialGoal; }
    public void setAwakeningTrialGoal(int v) { awakeningTrialGoal = v; }
    public long getAwakeningTrialDeadline() { return awakeningTrialDeadline; }
    public void setAwakeningTrialDeadline(long v) { awakeningTrialDeadline = v; }
    public int getMemoryShardsTotal() { return memoryShardsTotal; }
    public void setMemoryShardsTotal(int v) { memoryShardsTotal = v; }

    public void copyFrom(SequenceCapability other) {
        unlockedSequences = other.unlockedSequences;
        felineForce = other.felineForce;
        felineTier = other.felineTier;
        felineSkillCooldownUntil = other.felineSkillCooldownUntil;
        promotionCardCount = other.promotionCardCount;
        promotionCardBonus = other.promotionCardBonus;
        ownedPromotionCards.clear();
        ownedPromotionCards.addAll(other.ownedPromotionCards);
        pendingPromotionTier = other.pendingPromotionTier;
        ceremonyStage = other.ceremonyStage;
        ceremonyTimeout = other.ceremonyTimeout;
        ceremonyStageStartGameTime = other.ceremonyStageStartGameTime;
        markLevel = other.markLevel;
        markForce = other.markForce;
        simplifiedCeremony = other.simplifiedCeremony;
        hiddenSequences = other.hiddenSequences;
        awakeningTrialTier = other.awakeningTrialTier;
        awakeningTrialActive = other.awakeningTrialActive;
        awakeningTrialIndex = other.awakeningTrialIndex;
        awakeningTrialKills = other.awakeningTrialKills;
        awakeningTrialGoal = other.awakeningTrialGoal;
        awakeningTrialDeadline = other.awakeningTrialDeadline;
        memoryShardsTotal = other.memoryShardsTotal;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("unlockedSequences", unlockedSequences);
        tag.putString("felineForce", felineForce);
        tag.putInt("felineTier", felineTier);
        tag.putLong("felineSkillCooldownUntil", felineSkillCooldownUntil);
        tag.putInt("promotionCardCount", promotionCardCount);
        tag.putFloat("promotionCardBonus", promotionCardBonus);
        ListTag cards = new ListTag();
        for (String id : ownedPromotionCards) cards.add(StringTag.valueOf(id));
        tag.put("ownedPromotionCards", cards);
        tag.putInt("pendingPromotionTier", pendingPromotionTier);
        tag.putInt("ceremonyStage", ceremonyStage);
        tag.putInt("ceremonyTimeout", ceremonyTimeout);
        tag.putLong("ceremonyStageStartGameTime", ceremonyStageStartGameTime);
        tag.putInt("markLevel", markLevel);
        tag.putString("markForce", markForce);
        tag.putBoolean("simplifiedCeremony", simplifiedCeremony);
        tag.putLong("hiddenSequences", hiddenSequences);
        tag.putInt("awakeningTrialTier", awakeningTrialTier);
        tag.putBoolean("awakeningTrialActive", awakeningTrialActive);
        tag.putInt("awakeningTrialIndex", awakeningTrialIndex);
        tag.putInt("awakeningTrialKills", awakeningTrialKills);
        tag.putInt("awakeningTrialGoal", awakeningTrialGoal);
        tag.putLong("awakeningTrialDeadline", awakeningTrialDeadline);
        tag.putInt("memoryShardsTotal", memoryShardsTotal);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return;
        unlockedSequences = tag.getLong("unlockedSequences");
        felineForce = tag.getString("felineForce");
        felineTier = tag.getInt("felineTier");
        felineSkillCooldownUntil = tag.getLong("felineSkillCooldownUntil");
        promotionCardCount = tag.getInt("promotionCardCount");
        promotionCardBonus = tag.getFloat("promotionCardBonus");
        ownedPromotionCards.clear();
        if (tag.contains("ownedPromotionCards")) {
            for (Tag t : tag.getList("ownedPromotionCards", Tag.TAG_STRING)) {
                ownedPromotionCards.add(t.getAsString());
            }
        }
        pendingPromotionTier = tag.getInt("pendingPromotionTier");
        ceremonyStage = tag.getInt("ceremonyStage");
        ceremonyTimeout = tag.getInt("ceremonyTimeout");
        ceremonyStageStartGameTime = tag.contains("ceremonyStageStartGameTime")
                ? tag.getLong("ceremonyStageStartGameTime") : 0L;
        markLevel = tag.getInt("markLevel");
        markForce = tag.getString("markForce");
        simplifiedCeremony = tag.getBoolean("simplifiedCeremony");
        hiddenSequences = tag.getLong("hiddenSequences");
        awakeningTrialTier = tag.getInt("awakeningTrialTier");
        awakeningTrialActive = tag.getBoolean("awakeningTrialActive");
        awakeningTrialIndex = tag.getInt("awakeningTrialIndex");
        awakeningTrialKills = tag.getInt("awakeningTrialKills");
        awakeningTrialGoal = tag.getInt("awakeningTrialGoal");
        awakeningTrialDeadline = tag.getLong("awakeningTrialDeadline");
        memoryShardsTotal = tag.getInt("memoryShardsTotal");
    }

    public List<String> ownedPromotionCardsView() {
        return Collections.unmodifiableList(ownedPromotionCards);
    }
}
