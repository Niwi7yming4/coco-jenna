package com.cocojenna.dialogue;

import javax.annotation.Nullable;
import java.util.List;

/** One line in a visual-novel scene. */
public record DialogueLine(
        String speakerKey,
        String textKey,
        Portrait portrait,
        @Nullable List<DialogueChoice> choices,
        @Nullable String completeAction,
        String backgroundId,
        DialogueExpression expression,
        PortraitSide portraitSide,
        String voiceKey,
        int autoDelayTicks
) {
    public DialogueLine(String speakerKey, String textKey, Portrait portrait) {
        this(speakerKey, textKey, portrait, null, null, "", DialogueExpression.NORMAL,
                PortraitSide.LEFT, "", 0);
    }

    public DialogueLine(String speakerKey, String textKey, Portrait portrait,
            @Nullable List<DialogueChoice> choices, @Nullable String completeAction) {
        this(speakerKey, textKey, portrait, choices, completeAction, "",
                DialogueExpression.NORMAL, PortraitSide.LEFT, "", 0);
    }

    public DialogueLine withGal(String backgroundId, DialogueExpression expression, PortraitSide side) {
        return new DialogueLine(speakerKey, textKey, portrait, choices, completeAction,
                backgroundId == null ? "" : backgroundId,
                expression == null ? DialogueExpression.NORMAL : expression,
                side == null ? PortraitSide.LEFT : side,
                voiceKey, autoDelayTicks);
    }

    public DialogueLine withBackground(String backgroundId) {
        return withGal(backgroundId, expression, portraitSide);
    }

    public boolean hasChoices() {
        return choices != null && !choices.isEmpty();
    }
}
