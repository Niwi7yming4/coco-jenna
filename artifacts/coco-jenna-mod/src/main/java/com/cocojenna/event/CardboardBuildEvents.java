package com.cocojenna.event;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.block.CardboardStructuralBlock;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 紙箱拆除回收率與結構坍塌（批次 B）. */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class CardboardBuildEvents {

    private CardboardBuildEvents() {}

    @SubscribeEvent
    public static void onDrops(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        BlockState state = event.getState();
        float salvage = salvageRate(state);
        if (salvage <= 0) return;
        event.setCanceled(true);
        BlockPos pos = event.getPos();
        event.getLevel().removeBlock(pos, false);
        if (event.getLevel().getRandom().nextFloat() < salvage) {
            ItemStack drop = new ItemStack(state.getBlock());
            if (event.getPlayer() != null) {
                if (!event.getPlayer().addItem(drop)) {
                    event.getPlayer().drop(drop, false);
                }
            } else {
                net.minecraft.world.Containers.dropItemStack(
                        (ServerLevel) event.getLevel(), pos.getX(), pos.getY(), pos.getZ(), drop);
            }
        }
        if (event.getLevel() instanceof ServerLevel sl) {
            for (BlockPos neighbor : new BlockPos[]{
                    pos.above(), pos.below(),
                    pos.north(), pos.south(), pos.east(), pos.west()}) {
                CardboardStructuralBlock.collapseUnsupported(sl, neighbor);
            }
        }
    }

    private static float salvageRate(BlockState state) {
        if (state.is(ModBlocks.CARDBOARD_BLOCK.get())) return 0.75f;
        if (state.is(ModBlocks.REINFORCED_CARDBOARD.get())) return 0.9f;
        if (state.is(ModBlocks.ROPE_NET.get())) return 0.85f;
        return 0f;
    }
}
