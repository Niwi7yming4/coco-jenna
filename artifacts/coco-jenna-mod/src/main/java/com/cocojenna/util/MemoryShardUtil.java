package com.cocojenna.util;

import com.cocojenna.init.ModItems;
import net.minecraft.world.item.ItemStack;

/** Creates memory shard items with narrative ShardId tags. */
public final class MemoryShardUtil {

    private MemoryShardUtil() {}

    public static ItemStack create(String shardId) {
        ItemStack stack = new ItemStack(ModItems.MEMORY_SHARD.get());
        stack.getOrCreateTag().putString("ShardId", shardId);
        return stack;
    }
}
