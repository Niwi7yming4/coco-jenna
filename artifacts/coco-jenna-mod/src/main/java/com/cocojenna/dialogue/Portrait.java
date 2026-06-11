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
    SANHUA(0xFFFFCCAA, 0xFFFFFFFF),
    QIN_KEMU(0xFFE8C878, 0xFFC44D4D),
    GRAY_WHISKER(0xFF9E9E9E, 0xFFE0E0E0),
    MOON_PRIEST(0xFF6B7FD7, 0xFFE8F4FF);

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
            case QIN_KEMU -> "portrait_qin_kemu";
            case GRAY_WHISKER -> "portrait_gray_whisker";
            case MOON_PRIEST -> "portrait_moon_priest";
            case NARRATOR -> "portrait_narrator";
            default -> "portrait_narrator";
        };
    }
}
