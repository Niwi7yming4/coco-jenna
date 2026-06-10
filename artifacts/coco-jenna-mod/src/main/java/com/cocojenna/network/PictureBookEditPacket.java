package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PictureBookEditPacket(String background, String caption, String sticker, String filter) {

    public static void encode(PictureBookEditPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.background);
        buf.writeUtf(msg.caption);
        buf.writeUtf(msg.sticker);
        buf.writeUtf(msg.filter);
    }

    public static PictureBookEditPacket decode(FriendlyByteBuf buf) {
        return new PictureBookEditPacket(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf());
    }

    public static void handle(PictureBookEditPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;
        ctx.get().enqueueWork(() -> {
            BondData bond = ModCapabilities.getOrDefault(player);
            if (!consumeMemoryShard(player)) {
                player.displayClientMessage(
                        Component.translatable("picturebook.cocojenna.need_shard"), true);
                return;
            }
            bond.addPictureBookPage(new BondData.PictureBookPage(
                    msg.background, msg.caption, msg.sticker, msg.filter));
            bond.addKingdomHappiness(2);
            ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
            player.displayClientMessage(Component.translatable("picturebook.cocojenna.page_added"), true);
        });
        ctx.get().setPacketHandled(true);
    }

    private static boolean consumeMemoryShard(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.MEMORY_SHARD.get())) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }
}
