package com.cocojenna.dialogue;

/** GAL 立繪顯示位置. */
public enum PortraitSide {
    LEFT,
    RIGHT,
    NONE;

    public static PortraitSide fromOrdinal(int ord) {
        PortraitSide[] values = values();
        if (ord < 0 || ord >= values.length) return LEFT;
        return values[ord];
    }
}
