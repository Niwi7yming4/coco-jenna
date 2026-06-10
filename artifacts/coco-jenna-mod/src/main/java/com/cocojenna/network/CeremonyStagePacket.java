package com.cocojenna.network;

import com.cocojenna.client.gui.CeremonyHudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 伺服器 → 客戶端：晉升儀式階段同步封包
 * 客戶端根據階段顯示對應的UI提示、鎖定視角等
 */
public class CeremonyStagePacket {

    /**
     * 0=NONE, 1=SUMMONING, 2=SACRIFICE, 3=RESONANCE, 4=REVELATION, 5=MARKING, 6=COMPLETE
     */
    private final int stage;
    private final int tier;
    /** 伺服器 gameTime 戳記；0 表示無倒數進度條. */
    private final long endGameTime;

    public CeremonyStagePacket(int stage, int tier) {
        this(stage, tier, 0L);
    }

    public CeremonyStagePacket(int stage, int tier, long endGameTime) {
        this.stage = stage;
        this.tier = tier;
        this.endGameTime = endGameTime;
    }

    public static void encode(CeremonyStagePacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.stage);
        buf.writeVarInt(pkt.tier);
        buf.writeVarLong(pkt.endGameTime);
    }

    public static CeremonyStagePacket decode(FriendlyByteBuf buf) {
        return new CeremonyStagePacket(
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readVarLong()
        );
    }

    public static void handle(CeremonyStagePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "CeremonyStagePacket")) return;
        ctx.get().enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // 更新HUD疊層顯示儀式階段
            CeremonyHudOverlay.INSTANCE.onStageChanged(pkt.stage, pkt.tier, pkt.endGameTime);

            // 階段3（共鳴）：鎖定玩家視角（不鎖移動）
            if (pkt.stage == 3) {
                // 設定無法移動標記，但可以轉動視角
                mc.player.setNoGravity(true);
            } else {
                mc.player.setNoGravity(false);
            }

            // 更新階段標題提示
            String stageKey = "ceremony.cocojenna.stage." + switch (pkt.stage) {
                case 1 -> "summoning";
                case 2 -> "sacrifice";
                case 3 -> "resonance";
                case 4 -> "revelation";
                case 5 -> "marking";
                case 6 -> "complete";
                default -> "none";
            };
            mc.gui.setTitle(net.minecraft.network.chat.Component.translatable(stageKey, pkt.tier));
        });
        ctx.get().setPacketHandled(true);
    }
}