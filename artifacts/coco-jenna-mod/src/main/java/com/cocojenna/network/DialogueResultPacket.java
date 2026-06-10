package com.cocojenna.network;

import com.cocojenna.dialogue.DialogueManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: GAL scene finished with optional action id. */
public class DialogueResultPacket {

    private final String sceneId;
    private final String actionId;

    public DialogueResultPacket(String sceneId, String actionId) {
        this.sceneId = sceneId;
        this.actionId = actionId;
    }

    public static void encode(DialogueResultPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.sceneId);
        buf.writeUtf(pkt.actionId != null ? pkt.actionId : "");
    }

    public static DialogueResultPacket decode(FriendlyByteBuf buf) {
        String scene = buf.readUtf();
        String action = buf.readUtf();
        return new DialogueResultPacket(scene, action.isEmpty() ? null : action);
    }

    public static void handle(DialogueResultPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                DialogueManager.onSceneComplete(player, pkt.sceneId, pkt.actionId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
