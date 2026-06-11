package com.cocojenna.dialogue;

/** GAL 立繪表情差分. */
public enum DialogueExpression {
    NORMAL,
    HAPPY,
    SAD,
    SURPRISED;

    public static DialogueExpression fromOrdinal(int ord) {
        DialogueExpression[] values = values();
        if (ord < 0 || ord >= values.length) return NORMAL;
        return values[ord];
    }
}
