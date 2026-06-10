package com.cocojenna.dialogue;

import javax.annotation.Nullable;
import java.util.List;

/** One line in a visual-novel scene. */
public record DialogueLine(
        String speakerKey,
        String textKey,
        Portrait portrait,
        @Nullable List<DialogueChoice> choices,
        @Nullable String completeAction
) {
    public DialogueLine(String speakerKey, String textKey, Portrait portrait) {
        this(speakerKey, textKey, portrait, null, null);
    }

    public boolean hasChoices() {
        return choices != null && !choices.isEmpty();
    }
}
