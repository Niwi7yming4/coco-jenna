package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 伺服器 → 客戶端：觸發「心靈同步」視角效果。
 * 畫面短暫切換到最近貓咪的第一人稱視角，持續 5 秒。
 */
public class MindSyncViewPacket {

    private final boolean isCoco;
    private final double catX, catY, catZ;
    private final float catYaw;

    public MindSyncViewPacket(boolean isCoco, double x, double y, double z, float yaw) {
        this.isCoco = isCoco;
        this.catX = x; this.catY = y; this.catZ = z;
        this.catYaw = yaw;
    }

    public static void encode(MindSyncViewPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isCoco);
        buf.writeDouble(packet.catX);
        buf.writeDouble(packet.catY);
        buf.writeDouble(packet.catZ);
        buf.writeFloat(packet.catYaw);
    }

    public static MindSyncViewPacket decode(FriendlyByteBuf buf) {
        return new MindSyncViewPacket(
                buf.readBoolean(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readFloat());
    }

    public static void handle(MindSyncViewPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            com.cocojenna.client.MindSyncRenderer.startSync(
                    packet.isCoco, packet.catX, packet.catY, packet.catZ, packet.catYaw);
        });
        ctx.get().setPacketHandled(true);
    }
}
