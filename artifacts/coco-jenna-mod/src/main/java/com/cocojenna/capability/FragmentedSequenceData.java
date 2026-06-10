package com.cocojenna.capability;

import com.cocojenna.society.FragmentedProfession;
import net.minecraft.nbt.CompoundTag;

/** 破碎序列村民 per-entity 資料. */
public class FragmentedSequenceData {

    private boolean active;
    private int strength = 1;
    private FragmentedProfession profession = FragmentedProfession.CAT_COMPANION;
    private int bondWithPlayer;
    private boolean metPlayer;
    private int ritualCooldown;
    private int behaviorTick;

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public int getStrength() { return strength; }

    public void setStrength(int strength) { this.strength = Math.max(1, Math.min(3, strength)); }

    public FragmentedProfession getProfession() { return profession; }

    public void setProfession(FragmentedProfession profession) {
        this.profession = profession == null ? FragmentedProfession.CAT_COMPANION : profession;
    }

    public int getBondWithPlayer() { return bondWithPlayer; }

    public void addBond(int amount) {
        bondWithPlayer = Math.min(100, Math.max(0, bondWithPlayer + amount));
    }

    public void addBondWithPlayer(int amount) { addBond(amount); }

    public boolean hasMetPlayer() { return metPlayer; }

    public void setMetPlayer(boolean metPlayer) { this.metPlayer = metPlayer; }

    public int getRitualCooldown() { return ritualCooldown; }

    public void setRitualCooldown(int ticks) { this.ritualCooldown = ticks; }

    public int getBehaviorTick() { return behaviorTick; }

    public void setBehaviorTick(int behaviorTick) { this.behaviorTick = behaviorTick; }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("active", active);
        tag.putInt("strength", strength);
        tag.putString("profession", profession.id());
        tag.putInt("bond", bondWithPlayer);
        tag.putBoolean("metPlayer", metPlayer);
        tag.putInt("ritualCd", ritualCooldown);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        active = tag.getBoolean("active");
        strength = tag.contains("strength") ? tag.getInt("strength") : 1;
        profession = FragmentedProfession.byId(tag.getString("profession"));
        bondWithPlayer = tag.contains("bond") ? tag.getInt("bond") : 0;
        metPlayer = tag.getBoolean("metPlayer");
        ritualCooldown = tag.contains("ritualCd") ? tag.getInt("ritualCd") : 0;
    }
}
