package com.cocojenna.economy;

/** 貓薄荷品質等級（批次 A 經濟）. */
public enum CatnipQuality {
    COMMON(4, 0xFF88CC66),
    RARE(12, 0xFF66AADD),
    LEGENDARY(40, 0xFFFFD966);

    private final int basePrice;
    private final int color;

    CatnipQuality(int basePrice, int color) {
        this.basePrice = basePrice;
        this.color = color;
    }

    public int basePrice() { return basePrice; }
    public int color() { return color; }

    public static CatnipQuality fromOrdinal(int i) {
        CatnipQuality[] vals = values();
        return vals[Math.max(0, Math.min(i, vals.length - 1))];
    }
}
