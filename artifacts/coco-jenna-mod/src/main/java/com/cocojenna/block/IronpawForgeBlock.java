package com.cocojenna.block;

import com.cocojenna.block.entity.IronpawForgeBlockEntity;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

/** 鐵爪鍛造舖 — 傳說武器強化 +0～+10. */
public class IronpawForgeBlock extends BaseMachineBlock<IronpawForgeBlockEntity> {

    public IronpawForgeBlock(Properties props) {
        super(props, (level, pos) -> {});
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            var bond = ModCapabilities.getOrDefault(sp);
            if (!bond.isMetIronpaw()) {
                bond.setMetIronpaw(true);
                DialogueManager.play(sp, "gear_town_ironpaw");
            }
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof net.minecraft.world.MenuProvider menuProvider) {
                NetworkHooks.openScreen(sp, menuProvider, pos);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IronpawForgeBlockEntity(pos, state);
    }
}
