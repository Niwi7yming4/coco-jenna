package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import com.cocojenna.quest.FirstCryMainQuestManager;
import com.cocojenna.util.MemoryShardUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** 初啼村四隱藏點互動（設計書 3.10）. */
public final class FirstCryHiddenInteractionHandler {

    private static final int CANOPY_LORE = 26;

    private FirstCryHiddenInteractionHandler() {}

    public static void onTabletRead(ServerPlayer player, int lore) {
        if (lore != CANOPY_LORE) return;
        if (player.blockPosition().distSqr(FirstCryLayout.CANOPY_TABLET) > 36) return;
        ItemStack shard = MemoryShardUtil.create("first_cry_canopy");
        if (!player.addItem(shard)) player.drop(shard, false);
        FirstCryMainQuestManager.onCanopyTablet(player);
    }

    public static void onSuspiciousWall(ServerPlayer player, BlockPos pos) {
        if (!pos.equals(FirstCryLayout.MOON_CHAMBER_WALL)) return;
        player.level().setBlock(FirstCryLayout.MOON_CHAMBER_ALTAR,
                ModBlocks.MOON_TRIAL_ALTAR.get().defaultBlockState(), 2);
        FirstCryMainQuestManager.onMoonChamberOpened(player);
    }

    public static boolean trySneakPassage(ServerPlayer player) {
        BlockPos entry = FirstCryLayout.SECRET_PASSAGE;
        if (player.blockPosition().distSqr(entry) > 16) return false;
        if (!player.isShiftKeyDown()) return false;
        player.setInvisible(true);
        player.noPhysics = true;
        player.teleportTo(entry.getX() + 0.5, entry.getY(), entry.getZ() + 4.5);
        player.noPhysics = false;
        player.setInvisible(false);
        return true;
    }

    public static void onRuinChestLoot(ServerPlayer player, BlockPos pos) {
        if (pos.distSqr(FirstCryLayout.BLACK_MUD_RUIN.offset(3, 1, 3)) > 16) return;
        ItemStack diary = new ItemStack(ModItems.DAMAGED_DIARY.get());
        if (!player.addItem(diary)) player.drop(diary, false);
        FirstCryMainQuestManager.onDiaryFound(player);
        FirstCryBlackMudEventManager.onEnterRuin(player);
    }

    public static void onBlackMudBlock(ServerPlayer player, BlockState state, BlockPos pos) {
        if (!state.is(ModBlocks.BLACK_MUD.get())) return;
        if (pos.distSqr(FirstCryLayout.BLACK_MUD_RUIN) > 30) return;
        FirstCryBlackMudEventManager.onEnterRuin(player);
    }
}
