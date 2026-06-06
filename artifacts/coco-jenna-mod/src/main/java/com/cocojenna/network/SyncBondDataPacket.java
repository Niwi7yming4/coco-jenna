package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 伺服器 → 客戶端：同步玩家的 BondData */
public class SyncBondDataPacket {

    private final CompoundTag nbt;

    public SyncBondDataPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public static void encode(SyncBondDataPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.nbt);
    }

    public static SyncBondDataPacket decode(FriendlyByteBuf buf) {
        return new SyncBondDataPacket(buf.readNbt());
    }

    public static void handle(SyncBondDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null && packet.nbt != null) {
                ModCapabilities.get(player).ifPresent(bond -> bond.deserializeNBT(packet.nbt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
