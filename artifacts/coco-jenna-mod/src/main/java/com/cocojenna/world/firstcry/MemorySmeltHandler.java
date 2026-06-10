package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 記憶熔爐 — socketing_table + aroma_distiller 組合，碎片兌換 soul_dust. */
public final class MemorySmeltHandler {

    private MemorySmeltHandler() {}

    public static boolean trySmelt(ServerPlayer player, Level level, BlockPos pos, BlockState state) {
        if (!state.is(ModBlocks.SOCKETING_TABLE.get()) && !state.is(ModBlocks.AROMA_DISTILLER.get())) {
            return false;
        }
        BlockPos other = state.is(ModBlocks.SOCKETING_TABLE.get())
                ? pos.relative(player.getDirection().getClockWise())
                : pos.relative(player.getDirection().getCounterClockWise());
        BlockState adj = level.getBlockState(other);
        boolean paired = (state.is(ModBlocks.SOCKETING_TABLE.get()) && adj.is(ModBlocks.AROMA_DISTILLER.get()))
                || (state.is(ModBlocks.AROMA_DISTILLER.get()) && adj.is(ModBlocks.SOCKETING_TABLE.get()));
        if (!paired) return false;
        if (!consumeShards(player, 3)) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("first_cry.cocojenna.smelt_need_shards"),
                    true);
            return false;
        }
        ItemStack dust = new ItemStack(ModItems.SOUL_DUST.get(), 1);
        if (!player.addItem(dust)) player.drop(dust, false);
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("first_cry.cocojenna.smelt_success"), false);
        com.cocojenna.quest.KingdomTutorialManager.onFirstDistill(player);
        return true;
    }

    private static boolean consumeShards(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.MEMORY_SHARD.get())) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
        return left == 0;
    }
}
