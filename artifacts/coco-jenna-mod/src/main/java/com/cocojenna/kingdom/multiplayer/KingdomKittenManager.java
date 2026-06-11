package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.PersonalAffectionSyncPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** 王國幼貓祝福儀式 */
public final class KingdomKittenManager {

    private KingdomKittenManager() {}

    public static boolean tryBless(ServerPlayer player) {
        var bond = ModCapabilities.getOrDefault(player);
        if (bond.getMultiplayerSection().hasKingdomKitten()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.kitten_has"), true);
            return false;
        }
        int shards = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.MEMORY_SHARD.get())) shards += s.getCount();
        }
        if (shards < 3 || !player.getInventory().contains(new ItemStack(ModItems.HIBISCUS_TEAR.get()))) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.kitten_need_items"), true);
            return false;
        }
        player.getInventory().clearOrCountMatchingItems(
                st -> st.is(ModItems.MEMORY_SHARD.get()), 3, null);
        player.getInventory().removeItem(new ItemStack(ModItems.HIBISCUS_TEAR.get()));
        bond.getMultiplayerSection().setHasKingdomKitten(true);
        KingdomKittenSpawner.spawnFor(player);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.kitten_blessed"), true);
        syncAffection(player);
        return true;
    }

    public static void syncAffection(ServerPlayer player) {
        var bond = ModCapabilities.getOrDefault(player);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new PersonalAffectionSyncPacket(
                        bond.getPersonalCocoAffection(player.getUUID()),
                        bond.getPersonalJennaAffection(player.getUUID())));
    }
}
