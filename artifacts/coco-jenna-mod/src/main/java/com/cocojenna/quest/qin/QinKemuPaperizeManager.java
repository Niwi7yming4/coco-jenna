package com.cocojenna.quest.qin;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 策封序列 · 紙化 — 暫時將方塊變為可穿透的紙. */
public final class QinKemuPaperizeManager {

    private static final int DURATION_TICKS = 200;

    private QinKemuPaperizeManager() {}

    public static boolean tryPaperize(ServerPlayer player, InteractionHand hand, BlockHitResult hit) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getQinKemuQuestStage() < 4) return false;
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(ModItems.RED_PAPER.get()) && !held.is(ModItems.ORIGAMI_SCRAP.get())) return false;
        if (!(player.level() instanceof ServerLevel level)) return false;

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(ModBlocks.CARDBOARD_BLOCK.get())) return false;

        if (!player.getAbilities().instabuild) held.shrink(1);
        PaperizeRestoreManager.schedule(level, pos, state, DURATION_TICKS);
        level.setBlock(pos, ModBlocks.TAPE_BLOCK.get().defaultBlockState(), 3);

        player.displayClientMessage(Component.translatable("qin.cocojenna.paperize.success")
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return true;
    }

}
