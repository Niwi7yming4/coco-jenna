package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 玩家級多人資料（幼貓、離線任務、排行榜積分） */
public final class KingdomMultiplayerCapability {

    private boolean hasKingdomKitten;
    private float kittenAffection;
    private String offlineTaskType = "";
    private int offlineTaskParam;
    private long offlineTaskEndTime;
    private int contributionPoints;
    private int arenaScore;
    private int catnipPlantStreak;
    private int blackMudPurified;
    private boolean soloBossClear;
    private boolean soloCeremonyFlawless;
    private boolean soloRuinClear;
    private int mercenaryPrice;
    private long mercenaryCooldownUntil;

    public boolean hasKingdomKitten() { return hasKingdomKitten; }
    public void setHasKingdomKitten(boolean v) { hasKingdomKitten = v; }
    public float getKittenAffection() { return kittenAffection; }
    public void setKittenAffection(float v) { kittenAffection = Math.max(0, Math.min(100, v)); }
    public String getOfflineTaskType() { return offlineTaskType; }
    public void setOfflineTaskType(String v) { offlineTaskType = v == null ? "" : v; }
    public int getOfflineTaskParam() { return offlineTaskParam; }
    public void setOfflineTaskParam(int v) { offlineTaskParam = v; }
    public long getOfflineTaskEndTime() { return offlineTaskEndTime; }
    public void setOfflineTaskEndTime(long v) { offlineTaskEndTime = v; }
    public int getContributionPoints() { return contributionPoints; }
    public void addContributionPoints(int n) { contributionPoints = Math.max(0, contributionPoints + n); }
    public int getArenaScore() { return arenaScore; }
    public void addArenaScore(int n) { arenaScore = Math.max(0, arenaScore + n); }
    public int getCatnipPlantStreak() { return catnipPlantStreak; }
    public void setCatnipPlantStreak(int v) { catnipPlantStreak = v; }
    public int getBlackMudPurified() { return blackMudPurified; }
    public void addBlackMudPurified(int n) { blackMudPurified += n; }
    public boolean isSoloBossClear() { return soloBossClear; }
    public void setSoloBossClear(boolean v) { soloBossClear = v; }
    public boolean isSoloCeremonyFlawless() { return soloCeremonyFlawless; }
    public void setSoloCeremonyFlawless(boolean v) { soloCeremonyFlawless = v; }
    public boolean isSoloRuinClear() { return soloRuinClear; }
    public void setSoloRuinClear(boolean v) { soloRuinClear = v; }
    public int getMercenaryPrice() { return mercenaryPrice; }
    public void setMercenaryPrice(int v) { mercenaryPrice = Math.max(0, v); }
    public long getMercenaryCooldownUntil() { return mercenaryCooldownUntil; }
    public void setMercenaryCooldownUntil(long v) { mercenaryCooldownUntil = v; }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("hasKitten", hasKingdomKitten);
        tag.putFloat("kittenAff", kittenAffection);
        tag.putString("offlineType", offlineTaskType);
        tag.putInt("offlineParam", offlineTaskParam);
        tag.putLong("offlineEnd", offlineTaskEndTime);
        tag.putInt("contrib", contributionPoints);
        tag.putInt("arena", arenaScore);
        tag.putInt("catnipStreak", catnipPlantStreak);
        tag.putInt("mudPurified", blackMudPurified);
        tag.putBoolean("soloBoss", soloBossClear);
        tag.putBoolean("soloCeremony", soloCeremonyFlawless);
        tag.putBoolean("soloRuin", soloRuinClear);
        tag.putInt("mercPrice", mercenaryPrice);
        tag.putLong("mercCd", mercenaryCooldownUntil);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return;
        hasKingdomKitten = tag.getBoolean("hasKitten");
        kittenAffection = tag.getFloat("kittenAff");
        offlineTaskType = tag.getString("offlineType");
        offlineTaskParam = tag.getInt("offlineParam");
        offlineTaskEndTime = tag.getLong("offlineEnd");
        contributionPoints = tag.getInt("contrib");
        arenaScore = tag.getInt("arena");
        catnipPlantStreak = tag.getInt("catnipStreak");
        blackMudPurified = tag.getInt("mudPurified");
        soloBossClear = tag.getBoolean("soloBoss");
        soloCeremonyFlawless = tag.getBoolean("soloCeremony");
        soloRuinClear = tag.getBoolean("soloRuin");
        mercenaryPrice = tag.getInt("mercPrice");
        mercenaryCooldownUntil = tag.getLong("mercCd");
    }
}
