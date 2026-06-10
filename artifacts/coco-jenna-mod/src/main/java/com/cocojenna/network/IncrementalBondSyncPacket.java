package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：BondData 分區增量同步. */
public class IncrementalBondSyncPacket {

    public enum Section { CAT_BOND, SEQUENCE, KINGDOM }

    private final Section section;
    private final CompoundTag nbt;

    public IncrementalBondSyncPacket(CompoundTag nbt) {
        this(nbt, Section.CAT_BOND);
    }

    public IncrementalBondSyncPacket(CompoundTag nbt, Section section) {
        this.section = section;
        this.nbt = nbt;
    }

    public static void encode(IncrementalBondSyncPacket pkt, FriendlyByteBuf buf) {
        buf.writeEnum(pkt.section);
        CompoundTag payload = pkt.section == Section.KINGDOM
                ? SyncBondDataPacket.pruneForNetwork(pkt.nbt)
                : (pkt.nbt == null ? new CompoundTag() : pkt.nbt);
        buf.writeNbt(payload);
    }

    public static IncrementalBondSyncPacket decode(FriendlyByteBuf buf) {
        Section section = buf.readEnum(Section.class);
        CompoundTag nbt = buf.readNbt();
        return new IncrementalBondSyncPacket(nbt == null ? new CompoundTag() : nbt, section);
    }

    public static void handle(IncrementalBondSyncPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        if (!PacketAuthGuard.requireClientBound(ctx, "IncrementalBondSyncPacket")) return;
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player == null || pkt.nbt == null) return;
            ModCapabilities.get(player).ifPresent(bond -> {
                switch (pkt.section) {
                    case CAT_BOND -> bond.deserializeCatBondSection(pkt.nbt);
                    case SEQUENCE -> bond.deserializeSequenceSection(pkt.nbt);
                    case KINGDOM -> bond.deserializeKingdomSection(pkt.nbt);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
