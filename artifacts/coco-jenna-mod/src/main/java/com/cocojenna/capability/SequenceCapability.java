package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/** 序列、儀式、印記、晉升卡牌（從 BondData 水平拆分）. */
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

    public void copyFrom(BondData bond) {
        unlockedSequences = bond.getUnlockedSequencesRaw();
        felineForce = bond.getFelineForce();
        felineTier = bond.getFelineTier();
        felineSkillCooldownUntil = bond.getFelineSkillCooldownUntil();
        promotionCardCount = bond.getPromotionCardCount();
        promotionCardBonus = bond.getPromotionCardBonus();
        ownedPromotionCards.clear();
        ownedPromotionCards.addAll(bond.getOwnedPromotionCards());
        pendingPromotionTier = bond.getPendingPromotionTier();
        ceremonyStage = bond.getCeremonyStage();
        ceremonyTimeout = bond.getCeremonyTimeout();
        markLevel = bond.getMarkLevel();
        markForce = bond.getMarkForce();
        simplifiedCeremony = bond.isSimplifiedCeremony();
        hiddenSequences = bond.getHiddenSequences();
        awakeningTrialTier = bond.getAwakeningTrialTier();
        awakeningTrialActive = bond.isAwakeningTrialActive();
        awakeningTrialIndex = bond.getAwakeningTrialIndex();
        awakeningTrialKills = bond.getAwakeningTrialKills();
        awakeningTrialGoal = bond.getAwakeningTrialGoal();
        awakeningTrialDeadline = bond.getAwakeningTrialDeadline();
        memoryShardsTotal = bond.getMemoryShardsTotal();
    }

    public void applyTo(BondData bond) {
        bond.setUnlockedSequencesRaw(unlockedSequences);
        bond.setFelineForce(felineForce);
        bond.setFelineTier(felineTier);
        bond.setFelineSkillCooldownUntil(felineSkillCooldownUntil);
        bond.setPromotionCardCount(promotionCardCount);
        bond.setPromotionCardBonus(promotionCardBonus);
        bond.replaceOwnedPromotionCards(ownedPromotionCards);
        bond.setPendingPromotionTier(pendingPromotionTier);
        bond.setCeremonyStage(ceremonyStage);
        bond.setCeremonyTimeout(ceremonyTimeout);
        bond.setMarkLevel(markLevel);
        bond.setMarkForce(markForce);
        bond.setSimplifiedCeremony(simplifiedCeremony);
        bond.setHiddenSequences(hiddenSequences);
        bond.setAwakeningTrialTier(awakeningTrialTier);
        bond.setAwakeningTrialActive(awakeningTrialActive);
        bond.setAwakeningTrialIndex(awakeningTrialIndex);
        bond.setAwakeningTrialKills(awakeningTrialKills);
        bond.setAwakeningTrialGoal(awakeningTrialGoal);
        bond.setAwakeningTrialDeadline(awakeningTrialDeadline);
        bond.setMemoryShardsTotal(memoryShardsTotal);
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
}
