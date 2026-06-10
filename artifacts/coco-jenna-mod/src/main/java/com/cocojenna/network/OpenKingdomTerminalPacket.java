package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.client.gui.KingdomTerminalScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenKingdomTerminalPacket(CompoundTag bondNbt) {

    public static void encode(OpenKingdomTerminalPacket msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.bondNbt);
    }

    public static OpenKingdomTerminalPacket decode(FriendlyByteBuf buf) {
        return new OpenKingdomTerminalPacket(buf.readNbt());
    }

    public static void handle(OpenKingdomTerminalPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BondData bond = new BondData();
            bond.deserializeNBT(msg.bondNbt);
            KingdomTerminalScreen.open(bond);
        });
        ctx.get().setPacketHandled(true);
    }
}
