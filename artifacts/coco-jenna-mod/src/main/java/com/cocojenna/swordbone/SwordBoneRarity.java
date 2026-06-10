package com.cocojenna.swordbone;

/** 劍骨增益依武器稀有度分級（設計書 1.5）. */
public enum SwordBoneRarity {
    COMMON(0.03f, 0f),
    RARE(0.03f, 0.05f),
    LEGENDARY(0.05f, 0.10f),
    DAIKATANA(0.08f, 0.15f),
    MUSOU(0.12f, 0.25f);

    public final float attackBonus;
    public final float specialBonus;

    SwordBoneRarity(float attackBonus, float specialBonus) {
        this.attackBonus = attackBonus;
        this.specialBonus = specialBonus;
    }
}
