package com.cocojenna.weapon;

/** 多段蓄力檔位（設計書第三卷）. */
public enum WeaponChargeProfile {
    INSTANT(8, 12, 16, 0.0f, 0.08f, 0.15f),
    QUICK(12, 18, 24, 0.0f, 0.12f, 0.22f),
    STANDARD(16, 24, 32, 0.0f, 0.15f, 0.30f),
    HEAVY(24, 36, 48, 0.05f, 0.20f, 0.40f),
    RITUAL(36, 52, 72, 0.10f, 0.25f, 0.55f);

    private final int minTicks;
    private final int tier2Ticks;
    private final int tier3Ticks;
    private final float tier1Bonus;
    private final float tier2Bonus;
    private final float tier3Bonus;

    WeaponChargeProfile(int min, int t2, int t3, float b1, float b2, float b3) {
        this.minTicks = min;
        this.tier2Ticks = t2;
        this.tier3Ticks = t3;
        this.tier1Bonus = b1;
        this.tier2Bonus = b2;
        this.tier3Bonus = b3;
    }

    public static WeaponChargeProfile fromId(String id) {
        if (id == null || id.isEmpty()) return STANDARD;
        try {
            return valueOf(id.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STANDARD;
        }
    }

    public int minTicks() { return minTicks; }

    public int tier2Ticks() { return tier2Ticks; }

    public int tier3Ticks() { return tier3Ticks; }

    /** 0=短蓄, 1=標準, 2=長蓄 */
    public int chargeTier(int chargeTicks) {
        if (chargeTicks >= tier3Ticks) return 2;
        if (chargeTicks >= tier2Ticks) return 1;
        return 0;
    }

    public float powerBonus(int chargeTicks) {
        int tier = chargeTier(chargeTicks);
        return switch (tier) {
            case 2 -> tier3Bonus;
            case 1 -> tier2Bonus;
            default -> tier1Bonus;
        };
    }
}
