package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** 月光祭壇序列晉升 — 滿月夜可免鑰匙（設計書 六.2）. */
public final class FirstCryMoonAltarHandler {

    private FirstCryMoonAltarHandler() {}

    public static boolean trySequenceTrial(ServerPlayer player, BlockState state, BlockPos pos) {
        if (!state.is(ModBlocks.FULL_MOON_ALTAR.get())) return false;
        if (pos.distSqr(FirstCryLayout.MOON_PLAZA) > 20 * 20) return false;
        boolean fullMoon = player.level().getMoonBrightness() >= 1.0f;
        if (fullMoon) {
            player.displayClientMessage(Component.translatable("first_cry.cocojenna.moon_altar_free"), false);
            return true;
        }
        if (!consumeKey(player)) {
            player.displayClientMessage(Component.translatable("first_cry.cocojenna.moon_altar_need_key"), true);
            return false;
        }
        player.displayClientMessage(Component.translatable("first_cry.cocojenna.moon_altar_trial"), false);
        return true;
    }

    private static boolean consumeKey(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.MEMORY_SHARD.get())) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }
}
