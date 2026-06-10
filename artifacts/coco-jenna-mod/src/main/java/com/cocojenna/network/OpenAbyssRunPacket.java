package com.cocojenna.network;

import com.cocojenna.abyss.AbyssRunManager;
import com.cocojenna.client.gui.AbyssRunScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record OpenAbyssRunPacket(
        int floor, int playerHp, int playerBlock, int energy,
        int enemyHp, int enemyBlock, int intentOrd, int roomOrd,
        List<String> hand, int rewardPending, boolean playerTurn, boolean shopPending) {

    public OpenAbyssRunPacket(AbyssRunManager.Session s) {
        this(s.floor, s.playerHp, s.playerBlock, s.energy,
                s.enemyHp, s.enemyBlock, s.intent.ordinal(), s.room.ordinal(),
                new ArrayList<>(s.hand), s.rewardPending, s.playerTurn, s.shopPending);
    }

    public static void encode(OpenAbyssRunPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.floor);
        buf.writeVarInt(msg.playerHp);
        buf.writeVarInt(msg.playerBlock);
        buf.writeVarInt(msg.energy);
        buf.writeVarInt(msg.enemyHp);
        buf.writeVarInt(msg.enemyBlock);
        buf.writeVarInt(msg.intentOrd);
        buf.writeVarInt(msg.roomOrd);
        buf.writeVarInt(msg.hand.size());
        for (String c : msg.hand) buf.writeUtf(c);
        buf.writeVarInt(msg.rewardPending);
        buf.writeBoolean(msg.playerTurn);
        buf.writeBoolean(msg.shopPending);
    }

    public static OpenAbyssRunPacket decode(FriendlyByteBuf buf) {
        int floor = buf.readVarInt();
        int php = buf.readVarInt();
        int pblk = buf.readVarInt();
        int en = buf.readVarInt();
        int ehp = buf.readVarInt();
        int eblk = buf.readVarInt();
        int intent = buf.readVarInt();
        int room = buf.readVarInt();
        int n = buf.readVarInt();
        List<String> hand = new ArrayList<>();
        for (int i = 0; i < n; i++) hand.add(buf.readUtf());
        int reward = buf.readVarInt();
        boolean turn = buf.readBoolean();
        boolean shop = buf.readBoolean();
        return new OpenAbyssRunPacket(floor, php, pblk, en, ehp, eblk, intent, room, hand, reward, turn, shop);
    }

    public static void handle(OpenAbyssRunPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> com.cocojenna.client.web.WebUiRouter.openAbyss(msg));
        ctx.get().setPacketHandled(true);
    }
}
