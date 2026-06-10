package com.cocojenna.dialogue;

/** Portrait slot colors for GAL-style UI (placeholder until custom art). */
public enum Portrait {
    NARRATOR(0xFF5D3A3A, 0x00000000),
    ELDER(0xFFE8C4A0, 0xFF8B6914),
    SAMURAI(0xFFC44D4D, 0xFF4A1515),
    COCO(0xFF1A1A2E, 0xFFFFBF00),
    JENNA(0xFFC96823, 0xFFE6FF00),
    IRONPAW(0xFF8B7355, 0xFFB87333),
    MERCHANT(0xFF6B4C8A, 0xFFCCAAFF),
    SANHUA(0xFFFFCCAA, 0xFFFFFFFF);

    public final int bodyColor;
    public final int accentColor;

    Portrait(int bodyColor, int accentColor) {
        this.bodyColor = bodyColor;
        this.accentColor = accentColor;
    }

    /** GUI portrait texture id (fallback to narrator art if missing). */
    public String textureId() {
        return switch (this) {
            case IRONPAW -> "portrait_ironpaw";
            case ELDER -> "portrait_calico";
            case COCO -> "portrait_coco";
            case SAMURAI -> "portrait_squall";
            case JENNA -> "portrait_jenna";
            case MERCHANT -> "portrait_cheshire";
            case SANHUA -> "portrait_sanhua";
            case NARRATOR -> "portrait_narrator";
            default -> "portrait_narrator";
        };
    }
}
