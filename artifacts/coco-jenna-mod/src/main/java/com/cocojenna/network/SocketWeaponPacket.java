package com.cocojenna.network;

import com.cocojenna.block.entity.SocketingTableBlockEntity;
import com.cocojenna.memforge.SocketHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SocketWeaponPacket {

    private final BlockPos pos;

    public SocketWeaponPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(SocketWeaponPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static SocketWeaponPacket decode(FriendlyByteBuf buf) {
        return new SocketWeaponPacket(buf.readBlockPos());
    }

    public static void handle(SocketWeaponPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            BlockEntity be = player.level().getBlockEntity(pkt.pos);
            if (!(be instanceof SocketingTableBlockEntity table)) return;
            if (!table.stillValid(player)) return;
            ItemStack weapon = table.getItem(0);
            ItemStack gem = table.getItem(1);
            SocketHelper.trySocket(player, weapon, gem);
        });
        ctx.get().setPacketHandled(true);
    }
}
