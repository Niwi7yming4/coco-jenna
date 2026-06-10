package com.cocojenna.block;

import com.cocojenna.undercat.UndercatEntrance;
import com.cocojenna.undercat.UndercatEntranceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 初啼村樹洞入口 — 通往地下貓域. */
public class UndercatTreeHoleBlock extends Block {

    public UndercatTreeHoleBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                               InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide || !(player instanceof ServerPlayer sp)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.is(com.cocojenna.init.ModItems.ANCIENT_CAT_PAW.get())) {
            if (!player.getAbilities().instabuild) held.shrink(1);
            com.cocojenna.quest.FirstCryMainQuestManager.onSacredTreePlace(sp);
            return InteractionResult.CONSUME;
        }
        if (UndercatEntranceManager.tryEnter(sp, UndercatEntrance.TREE_HOLE, held)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
}
