package com.cocojenna.quest.qin;

import com.cocojenna.capability.BondData;
import com.cocojenna.entity.QinKemuEntity;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 策封考驗 — 記憶碎片 + 流浪貓. */
public final class SealingRitualHandler {

    private SealingRitualHandler() {}

    public static boolean tryRitual(ServerPlayer player, BondData bond, QinKemuEntity qin) {
        if (bond.getQinKemuQuestStage() != 5) return false;
        if (countShards(player) < 3) {
            player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.need_shards"), false);
            return false;
        }
        long strays = player.serverLevel().getEntitiesOfClass(AbstractCatEntity.class,
                qin.getBoundingBox().inflate(16), e -> e.getOwnerUUID() == null).size();
        if (strays < 1) {
            player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.need_stray"), false);
            return false;
        }
        consumeShards(player, 3);
        bond.setQinKemuQuestStage(6);
        bond.addQinKemuFavor(20);
        qin.setFavor(bond.getQinKemuFavor());
        player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.sealing_done"), false);
        return true;
    }

    private static int countShards(ServerPlayer player) {
        int n = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.MEMORY_SHARD.get())) n += s.getCount();
        }
        return n;
    }

    private static void consumeShards(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.is(ModItems.MEMORY_SHARD.get())) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }
}
