package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.client.gui.PictureBookScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenPictureBookPacket(CompoundTag bondNbt) {

    public static void encode(OpenPictureBookPacket msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.bondNbt);
    }

    public static OpenPictureBookPacket decode(FriendlyByteBuf buf) {
        return new OpenPictureBookPacket(buf.readNbt());
    }

    public static void handle(OpenPictureBookPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BondData bond = new BondData();
            bond.deserializeNBT(msg.bondNbt);
            PictureBookScreen.open(bond);
        });
        ctx.get().setPacketHandled(true);
    }
}
