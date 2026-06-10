package com.cocojenna.network;

import com.cocojenna.block.entity.IronpawForgeBlockEntity;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnhanceWeaponPacket {

    private final BlockPos pos;

    public EnhanceWeaponPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(EnhanceWeaponPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
    }

    public static EnhanceWeaponPacket decode(FriendlyByteBuf buf) {
        return new EnhanceWeaponPacket(buf.readBlockPos());
    }

    public static void handle(EnhanceWeaponPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            BlockEntity be = player.level().getBlockEntity(pkt.pos);
            if (!(be instanceof IronpawForgeBlockEntity forge)) return;
            if (!forge.stillValid(player)) return;
            var weapon = forge.getItem(0);
            if (!weapon.isEmpty()) {
                WeaponEnhanceHelper.tryEnhance(player, weapon);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
