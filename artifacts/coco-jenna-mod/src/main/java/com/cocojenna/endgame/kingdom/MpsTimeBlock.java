package com.cocojenna.endgame.kingdom;

/** MPS 和平版四時間塊. */
public enum MpsTimeBlock {
    MORNING(0, 6000, 1.2f, 1.0f),
    AFTERNOON(6000, 12000, 1.0f, 1.2f),
    DUSK(12000, 18000, 0.9f, 1.0f),
    MIDNIGHT(18000, 24000, 0.5f, 1.0f);

    public final int start;
    public final int end;
    public final float gatherMult;
    public final float craftMult;

    MpsTimeBlock(int start, int end, float gatherMult, float craftMult) {
        this.start = start;
        this.end = end;
        this.gatherMult = gatherMult;
        this.craftMult = craftMult;
    }

    public static MpsTimeBlock of(int index) {
        return values()[Math.max(0, Math.min(3, index))];
    }
}
