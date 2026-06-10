package com.cocojenna.network;

import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class BondSettingsPacket {

    public static final int FOLLOW = 0;
    public static final int AFFECTION = 1;
    public static final int EXPLORE = 2;
    public static final int COMBAT = 3;
    public static final int MUTE = 4;
    public static final int SHOW_COOLDOWN = 5;
    public static final int SKILL_SLOT = 6;
    public static final int SKILL_PRESET = 7;

    private final int settingId;
    private final int value;

    public BondSettingsPacket(int settingId, int value) {
        this.settingId = settingId;
        this.value = value;
    }

    public static void encode(BondSettingsPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.settingId);
        buf.writeVarInt(pkt.value);
    }

    public static BondSettingsPacket decode(FriendlyByteBuf buf) {
        return new BondSettingsPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(BondSettingsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            var bond = ModCapabilities.getOrDefault(player);
            switch (pkt.settingId) {
                case FOLLOW -> bond.setFollowDistance(pkt.value);
                case AFFECTION -> bond.setAllowAffection(pkt.value != 0);
                case EXPLORE -> bond.setAllowExplore(pkt.value != 0);
                case COMBAT -> bond.setAllowCombat(pkt.value != 0);
                case MUTE -> bond.setMuteMode(pkt.value != 0);
                case SHOW_COOLDOWN -> bond.setShowSkillCooldown(pkt.value != 0);
                case SKILL_SLOT -> bond.setPreferredSkillSlot(pkt.value);
                case SKILL_PRESET -> bond.setActiveSkillPreset(pkt.value);
                default -> { return; }
            }
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
        });
        ctx.get().setPacketHandled(true);
    }
}
