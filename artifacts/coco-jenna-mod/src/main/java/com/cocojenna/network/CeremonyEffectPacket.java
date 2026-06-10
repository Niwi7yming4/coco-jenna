package com.cocojenna.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 伺服器 → 客戶端：晉升儀式特效同步封包
 * 包含身體發光、螢幕閃白、永久印記、卡牌選擇3D效果
 */
public class CeremonyEffectPacket {

    public enum EffectType {
        BODY_GLOW,          // 玩家身體短暫變為對應源力顏色（半透明發光）
        FLASH,              // 畫面短暫閃白
        PERMANENT_MARK,     // 永久視覺印記
        CARD_SELECTION,     // 三張浮空卡牌選擇（3D效果）
        ULTIMATE_VIGNETTE,  // 終極技螢幕邊框
        SCREEN_SHAKE,       // 螢幕震動
        BOSS_KILL_EFFECT,   // Boss擊殺特效
        TWIN_STAR_BOND,     // 雙子星連攜
        REGION_PURIFY       // 區域淨化
    }

    private final EffectType effectType;
    private final String force;         // resonance/shadow/chaos
    private final int intensity;        // 強度/等級
    private final List<String> data;    // 額外數據（如卡牌ID）

    public CeremonyEffectPacket(EffectType effectType, String force, int intensity) {
        this(effectType, force, intensity, List.of());
    }

    public CeremonyEffectPacket(EffectType effectType, String force, int intensity, List<String> data) {
        this.effectType = effectType;
        this.force = force == null ? "" : force;
        this.intensity = intensity;
        this.data = data;
    }

    public static void encode(CeremonyEffectPacket pkt, FriendlyByteBuf buf) {
        buf.writeEnum(pkt.effectType);
        buf.writeUtf(pkt.force);
        buf.writeVarInt(pkt.intensity);
        buf.writeVarInt(pkt.data.size());
        for (String s : pkt.data) buf.writeUtf(s);
    }

    public static CeremonyEffectPacket decode(FriendlyByteBuf buf) {
        EffectType type = buf.readEnum(EffectType.class);
        String force = buf.readUtf();
        int intensity = buf.readVarInt();
        int n = buf.readVarInt();
        List<String> data = new ArrayList<>();
        for (int i = 0; i < n; i++) data.add(buf.readUtf());
        return new CeremonyEffectPacket(type, force, intensity, data);
    }

    public static void handle(CeremonyEffectPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "CeremonyEffectPacket")) return;
        ctx.get().enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;

            switch (pkt.effectType) {
                case BODY_GLOW -> {
                    // 玩家身體短暫發光（由渲染器處理）
                    com.cocojenna.client.renderer.PromotionMarkRenderer.INSTANCE
                        .triggerBodyGlow(mc.player, pkt.force, pkt.intensity);
                }
                case FLASH -> {
                    // 螢幕短暫閃白（由後處理著色器或疊層處理）
                    com.cocojenna.client.gui.ScreenEffectOverlay.INSTANCE
                        .triggerFlash(pkt.intensity);
                }
                case PERMANENT_MARK -> {
                    // 應用永久印記
                    int markLevel = pkt.intensity;
                    com.cocojenna.client.renderer.PromotionMarkRenderer.INSTANCE
                        .setPermanentMark(mc.player, pkt.force, markLevel);
                }
                case CARD_SELECTION -> {
                    // 開啟3D浮空卡牌選擇
                    int tier = pkt.intensity;
                    com.cocojenna.client.gui.CeremonyCardSelectionScreen.open(
                        tier, pkt.force, pkt.data);
                }
                case ULTIMATE_VIGNETTE -> {
                    // 終極技螢幕邊框
                    com.cocojenna.client.gui.ScreenEffectOverlay.INSTANCE
                        .triggerUltimateVignette(pkt.force, pkt.intensity);
                }
                case SCREEN_SHAKE -> {
                    // 螢幕震動
                    com.cocojenna.client.gui.ScreenEffectOverlay.INSTANCE
                        .triggerScreenShake(pkt.intensity);
                }
                case BOSS_KILL_EFFECT -> {
                    // Boss擊殺特效
                    com.cocojenna.client.gui.ScreenEffectOverlay.INSTANCE
                        .triggerBossKillEffect(pkt.force);
                }
                case REGION_PURIFY -> {
                    // 區域淨化特效
                    com.cocojenna.client.gui.ScreenEffectOverlay.INSTANCE
                        .triggerRegionPurify(pkt.force);
                }
                case TWIN_STAR_BOND -> {
                    // 雙子星連攜
                    com.cocojenna.client.gui.ScreenEffectOverlay.INSTANCE
                        .triggerTwinStarBond();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}