package com.cocojenna.guide;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** 觀測者・阿爾法兌換所（設計書 14.3）. */
public final class AlphaExchangeManager {

    public enum Offer {
        SAVE_ANCHOR("save_anchor", 30),
        DATA_REPAIR("data_repair", 10),
        SYSTEM_SCAN("system_scan", 5),
        TIME_SKIP("time_skip", 15);

        private final String id;
        private final int shardCost;

        Offer(String id, int shardCost) {
            this.id = id;
            this.shardCost = shardCost;
        }

        public String id() { return id; }
        public int shardCost() { return shardCost; }
    }

    private AlphaExchangeManager() {}

    public static boolean purchase(ServerPlayer player, String offerId) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) {
            player.displayClientMessage(Component.translatable("alpha.cocojenna.need_peace"), true);
            return false;
        }
        Offer offer = find(offerId);
        if (offer == null) return false;

        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getMemoryShardsTotal() < offer.shardCost()) {
            player.displayClientMessage(Component.translatable("alpha.cocojenna.need_shards",
                    offer.shardCost()), true);
            return false;
        }
        if (!consumeShards(player, offer.shardCost())) {
            player.displayClientMessage(Component.translatable("alpha.cocojenna.need_shards",
                    offer.shardCost()), true);
            return false;
        }

        boolean ok = switch (offer) {
            case SAVE_ANCHOR -> grantSaveAnchor(player);
            case DATA_REPAIR -> repairShard(player);
            case SYSTEM_SCAN -> activateScan(player, bond);
            case TIME_SKIP -> skipTime(player);
        };
        if (ok) {
            player.displayClientMessage(Component.translatable(
                    "alpha.cocojenna.purchased", Component.translatable("alpha.cocojenna.offer." + offer.id())), true);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
        }
        return ok;
    }

    private static Offer find(String id) {
        for (Offer o : Offer.values()) {
            if (o.id().equals(id)) return o;
        }
        return null;
    }

    private static boolean consumeShards(ServerPlayer player, int need) {
        int left = need;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.is(ModItems.MEMORY_SHARD.get())) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
        return left <= 0;
    }

    private static boolean grantSaveAnchor(ServerPlayer player) {
        ItemStack anchor = new ItemStack(ModItems.SAVE_ANCHOR.get());
        anchor.getOrCreateTag().putInt("AnchorCharges", 1);
        if (!player.addItem(anchor)) player.drop(anchor, false);
        return true;
    }

    private static boolean repairShard(ServerPlayer player) {
        ItemStack repaired = new ItemStack(ModItems.MEMORY_SHARD.get(), 2);
        if (!player.addItem(repaired)) player.drop(repaired, false);
        return true;
    }

    private static boolean activateScan(ServerPlayer player, BondData bond) {
        bond.setSystemScanUntil(player.level().getGameTime() + 1200);
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.GLOWING, 1200, 0, false, false, true));
        return true;
    }

    private static boolean skipTime(ServerPlayer player) {
        if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.setDayTime(sl.getDayTime() + 24000L * 3);
        }
        return true;
    }
}
