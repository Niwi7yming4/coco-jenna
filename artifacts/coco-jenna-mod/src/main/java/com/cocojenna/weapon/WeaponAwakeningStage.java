package com.cocojenna.weapon;

/** 武器解封四階段（設計書 §1.3）. */
public enum WeaponAwakeningStage {
    DORMANT(0, "dormant", 1.0f, 0),
    AWAKENED(1, "awakened", 1.2f, 30),
    ENLIGHTENED(2, "enlightened", 1.5f, 80),
    RESONANCE(3, "resonance", 2.0f, 150);

    public final int id;
    public final String key;
    /** 基礎攻擊力倍率 */
    public final float attackMultiplier;
    /** 進入此階段所需共鳴值 */
    public final int resonanceThreshold;

    WeaponAwakeningStage(int id, String key, float attackMultiplier, int resonanceThreshold) {
        this.id = id;
        this.key = key;
        this.attackMultiplier = attackMultiplier;
        this.resonanceThreshold = resonanceThreshold;
    }

    public static WeaponAwakeningStage fromId(int id) {
        for (WeaponAwakeningStage s : values()) {
            if (s.id == id) return s;
        }
        return DORMANT;
    }

    public WeaponAwakeningStage next() {
        return id < RESONANCE.id ? fromId(id + 1) : RESONANCE;
    }

    public boolean hasActiveSkill() {
        return id >= AWAKENED.id;
    }
}
