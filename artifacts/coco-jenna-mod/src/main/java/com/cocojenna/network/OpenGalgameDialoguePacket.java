package com.cocojenna.network;

import com.cocojenna.dialogue.DialogueChoice;
import com.cocojenna.dialogue.DialogueLine;
import com.cocojenna.dialogue.Portrait;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OpenGalgameDialoguePacket {

    private final String sceneId;
    private final List<DialogueLine> lines;

    public OpenGalgameDialoguePacket(String sceneId, List<DialogueLine> lines) {
        this.sceneId = sceneId;
        this.lines = lines;
    }

    public String sceneId() { return sceneId; }
    public List<DialogueLine> lines() { return lines; }

    public static void encode(OpenGalgameDialoguePacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.sceneId);
        buf.writeVarInt(pkt.lines.size());
        for (DialogueLine line : pkt.lines) {
            buf.writeUtf(line.speakerKey());
            buf.writeUtf(line.textKey());
            buf.writeVarInt(line.portrait().ordinal());
            buf.writeBoolean(line.hasChoices());
            if (line.hasChoices()) {
                buf.writeVarInt(line.choices().size());
                for (DialogueChoice c : line.choices()) {
                    buf.writeUtf(c.labelKey());
                    buf.writeUtf(c.actionId());
                }
            }
            buf.writeUtf(line.completeAction() != null ? line.completeAction() : "");
        }
    }

    public static OpenGalgameDialoguePacket decode(FriendlyByteBuf buf) {
        String sceneId = buf.readUtf();
        int count = buf.readVarInt();
        List<DialogueLine> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String speaker = buf.readUtf();
            String text = buf.readUtf();
            int portraitOrd = buf.readVarInt();
            Portrait[] portraits = Portrait.values();
            Portrait portrait = portraits[Math.max(0, Math.min(portraitOrd, portraits.length - 1))];
            boolean hasChoices = buf.readBoolean();
            List<DialogueChoice> choices = null;
            if (hasChoices) {
                int cc = buf.readVarInt();
                choices = new ArrayList<>();
                for (int j = 0; j < cc; j++) {
                    choices.add(new DialogueChoice(buf.readUtf(), buf.readUtf()));
                }
            }
            String action = buf.readUtf();
            lines.add(new DialogueLine(speaker, text, portrait, choices,
                    action.isEmpty() ? null : action));
        }
        return new OpenGalgameDialoguePacket(sceneId, lines);
    }

    public static void handle(OpenGalgameDialoguePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                com.cocojenna.client.web.WebUiRouter.openGalgame(pkt.sceneId(), pkt.lines()));
        ctx.get().setPacketHandled(true);
    }
}
