package com.cocojenna.network;

import com.cocojenna.block.entity.IronpawForgeBlockEntity;
import com.cocojenna.memforge.ForgeRepairHelper;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record IronpawForgeActionPacket(BlockPos pos, int action) {

    public static final int ENHANCE = 0;
    public static final int REPAIR = 1;
    public static final int REPAIR_BONES = 2;
    public static final int BUY_UNSHEATH = 3;

    public static void encode(IronpawForgeActionPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.action);
    }

    public static IronpawForgeActionPacket decode(FriendlyByteBuf buf) {
        return new IronpawForgeActionPacket(buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(IronpawForgeActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            BlockEntity be = player.level().getBlockEntity(msg.pos);
            if (!(be instanceof IronpawForgeBlockEntity forge) || !forge.stillValid(player)) return;
            switch (msg.action) {
                case ENHANCE -> {
                    var weapon = forge.getItem(0);
                    if (!weapon.isEmpty()) WeaponEnhanceHelper.tryEnhance(player, weapon);
                }
                case REPAIR -> ForgeRepairHelper.tryRepairWeapon(player, forge.getItem(0));
                case REPAIR_BONES -> ForgeRepairHelper.tryRepairBones(player);
                case BUY_UNSHEATH -> ForgeRepairHelper.tryBuyUnsheathStone(player);
                default -> { }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
