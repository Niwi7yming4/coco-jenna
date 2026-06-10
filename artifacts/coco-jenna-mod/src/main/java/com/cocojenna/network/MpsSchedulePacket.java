package com.cocojenna.network;

import com.cocojenna.endgame.kingdom.AfterRainKingdomManager;
import com.cocojenna.endgame.kingdom.MpsTask;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 王國 MPS 月曆排程. */
public class MpsSchedulePacket {

    public static final int SET_CELL = 0;
    public static final int CYCLE_CELL = 1;
    public static final int RUN_DAY = 2;
    public static final int APPLY_PRESET = 3;

    private final int action;
    private final int day;
    private final int block;
    private final String task;

    public MpsSchedulePacket(int action, int day, int block, String task) {
        this.action = action;
        this.day = day;
        this.block = block;
        this.task = task == null ? "" : task;
    }

    public static void encode(MpsSchedulePacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.action);
        buf.writeVarInt(pkt.day);
        buf.writeVarInt(pkt.block);
        buf.writeUtf(pkt.task, 64);
    }

    public static MpsSchedulePacket decode(FriendlyByteBuf buf) {
        return new MpsSchedulePacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(64));
    }

    public static void handle(MpsSchedulePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            JsonObject msg = new JsonObject();
            switch (pkt.action) {
                case SET_CELL -> {
                    msg.addProperty("day", pkt.day);
                    msg.addProperty("block", pkt.block);
                    msg.addProperty("task", pkt.task);
                    AfterRainKingdomManager.handleAction(player, "set_mps", msg);
                }
                case CYCLE_CELL -> {
                    var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
                    String cur = bond.getMpsCell(pkt.day, pkt.block);
                    MpsTask[] tasks = MpsTask.values();
                    int idx = 0;
                    for (int i = 0; i < tasks.length; i++) {
                        if (tasks[i].id.equals(cur)) {
                            idx = (i + 1) % tasks.length;
                            break;
                        }
                    }
                    msg.addProperty("day", pkt.day);
                    msg.addProperty("block", pkt.block);
                    msg.addProperty("task", tasks[idx].id);
                    AfterRainKingdomManager.handleAction(player, "set_mps", msg);
                }
                case RUN_DAY -> AfterRainKingdomManager.handleAction(player, "run_mps_day", msg);
                case APPLY_PRESET -> AfterRainKingdomManager.handleAction(player, "apply_preset", msg);
                default -> { }
            }
            var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
            ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
            AfterRainKingdomManager.syncHub(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
