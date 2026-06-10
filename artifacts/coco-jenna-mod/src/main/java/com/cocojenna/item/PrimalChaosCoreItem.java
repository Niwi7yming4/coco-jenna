package com.cocojenna.item;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.world.KingdomBuildSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 純淨之核 — 鑲嵌於記憶紀念碑頂層（設計書 卷六 §7.4）. */
public class PrimalChaosCoreItem extends Item {

    public PrimalChaosCoreItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.MEMORY_MONUMENT_TOP.get())) {
            if (ctx.getPlayer() != null) {
                ctx.getPlayer().displayClientMessage(
                        Component.translatable("blackmud.cocojenna.core.need_top"), true);
            }
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(ctx.getPlayer() instanceof ServerPlayer sp)) return InteractionResult.FAIL;

        ServerLevel server = (ServerLevel) level;
        KingdomBuildSavedData data = KingdomBuildSavedData.get(server);
        if (data.isPrimalCoreAnchored()) {
            return InteractionResult.CONSUME;
        }
        data.setPrimalCoreAnchored(true);
        ctx.getItemInHand().shrink(1);
        sp.displayClientMessage(Component.translatable("blackmud.cocojenna.core.placed"), true);
        return InteractionResult.CONSUME;
    }
}
