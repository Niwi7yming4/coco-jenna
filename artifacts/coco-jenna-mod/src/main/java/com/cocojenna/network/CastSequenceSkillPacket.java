package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.sequence.FelineSequenceSkills;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client → server: cast skill from Alt wheel (slot index). */
public class CastSequenceSkillPacket {

    private final int slot;

    public CastSequenceSkillPacket(int slot) {
        this.slot = slot;
    }

    public static void encode(CastSequenceSkillPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.slot);
    }

    public static CastSequenceSkillPacket decode(FriendlyByteBuf buf) {
        return new CastSequenceSkillPacket(buf.readVarInt());
    }

    public static void handle(CastSequenceSkillPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            BondData bond = ModCapabilities.getOrDefault(player);
            if (!com.cocojenna.sequence.MoonCrossroadsManager.hasChosenForce(bond)) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "force.cocojenna.wheel_locked"), true);
                return;
            }
            int slots = FelineSequenceSkills.wheelSlotCount(Math.max(9, bond.getFelineTier()));
            if (pkt.slot < 0 || pkt.slot >= slots) return;
            FelineSequenceSkills.castSlot(player, bond, pkt.slot);
            ModNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
        });
        ctx.get().setPacketHandled(true);
    }
}
