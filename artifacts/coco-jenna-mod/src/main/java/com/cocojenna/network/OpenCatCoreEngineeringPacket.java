package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.client.gui.CatCoreEngineeringScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenCatCoreEngineeringPacket(CompoundTag bondNbt) {

    public static void encode(OpenCatCoreEngineeringPacket msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.bondNbt);
    }

    public static OpenCatCoreEngineeringPacket decode(FriendlyByteBuf buf) {
        return new OpenCatCoreEngineeringPacket(buf.readNbt());
    }

    public static void handle(OpenCatCoreEngineeringPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BondData bond = new BondData();
            bond.deserializeNBT(msg.bondNbt);
            CatCoreEngineeringScreen.open(bond);
        });
        ctx.get().setPacketHandled(true);
    }
}
