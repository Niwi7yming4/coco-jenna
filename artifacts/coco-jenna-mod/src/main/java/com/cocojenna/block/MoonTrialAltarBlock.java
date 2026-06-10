package com.cocojenna.block;

import com.cocojenna.sequence.MoonCrossroadsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

/** 月光三岔路試煉祭壇 — 共鳴／暗影／混沌. */
public class MoonTrialAltarBlock extends Block {

    public enum ForcePath implements net.minecraft.util.StringRepresentable {
        RESONANCE("resonance"),
        SHADOW("shadow"),
        CHAOS("chaos");

        private final String id;

        ForcePath(String id) { this.id = id; }

        @Override
        public String getSerializedName() { return id; }
    }

    public static final EnumProperty<ForcePath> FORCE =
            EnumProperty.create("force", ForcePath.class);

    public MoonTrialAltarBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FORCE, ForcePath.RESONANCE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORCE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            MoonCrossroadsManager.onAltarInteract(sp, state.getValue(FORCE).getSerializedName(), pos);
        }
        return InteractionResult.CONSUME;
    }
}
