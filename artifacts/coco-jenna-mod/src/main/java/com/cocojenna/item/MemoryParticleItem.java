package com.cocojenna.item;

import com.cocojenna.util.MemoryShardUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/** 記憶微粒 — 100 個可合成隨機記憶碎片（設計書 卷四 §4.2）. */
public class MemoryParticleItem extends Item {

    private static final int COST = 100;
    private static final String[] POOL = {
            "first_cry_arrival", "gear_town_visit", "moon_alley_night", "velvet_forest_grief",
            "blind_port_rain", "sister_promise", "distill_first", "monument_seed"
    };

    public MemoryParticleItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.success(player.getItemInHand(hand));
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.pass(player.getItemInHand(hand));

        int total = countInInventory(player);
        if (total < COST) {
            sp.displayClientMessage(Component.translatable("blackmud.cocojenna.particle.need"), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        consume(player, COST);
        String id = POOL[player.getRandom().nextInt(POOL.length)];
        ItemStack shard = MemoryShardUtil.create(id);
        if (!player.addItem(shard)) player.drop(shard, false);
        sp.displayClientMessage(Component.translatable("blackmud.cocojenna.particle.synth"), true);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    private static int countInInventory(Player player) {
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(com.cocojenna.init.ModItems.MEMORY_PARTICLE.get())) {
                n += s.getCount();
            }
        }
        return n;
    }

    private static void consume(Player player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.is(com.cocojenna.init.ModItems.MEMORY_PARTICLE.get())) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }
}
