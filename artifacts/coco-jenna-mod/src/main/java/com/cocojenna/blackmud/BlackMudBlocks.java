package com.cocojenna.blackmud;

import com.cocojenna.init.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** 黑泥 0–4 分級方塊對照（chunk stage → 放置方塊）. */
public final class BlackMudBlocks {

    private BlackMudBlocks() {}

    /** chunk stage 1–4 對應方塊；stage 0 無方塊. */
    public static Block blockForStage(int chunkStage) {
        return switch (Math.max(1, Math.min(4, chunkStage))) {
            case 1 -> ModBlocks.BLACK_MUD_STAGE1.get();
            case 2 -> ModBlocks.BLACK_MUD.get();
            case 3 -> ModBlocks.BLACK_MUD_STAGE3.get();
            default -> ModBlocks.BLACK_MUD_STAGE4.get();
        };
    }

    public static BlockState blockStateForStage(int chunkStage) {
        return blockForStage(chunkStage).defaultBlockState();
    }

    public static boolean isBlackMud(BlockState state) {
        return state.is(ModBlocks.BLACK_MUD.get())
                || state.is(ModBlocks.BLACK_MUD_STAGE1.get())
                || state.is(ModBlocks.BLACK_MUD_STAGE3.get())
                || state.is(ModBlocks.BLACK_MUD_STAGE4.get());
    }

    public static String stageNameKey(int chunkStage) {
        return switch (Math.max(0, Math.min(4, chunkStage))) {
            case 0 -> "blackmud.cocojenna.stage.0";
            case 1 -> "blackmud.cocojenna.stage.1";
            case 2 -> "blackmud.cocojenna.stage.2";
            case 3 -> "blackmud.cocojenna.stage.3";
            default -> "blackmud.cocojenna.stage.4";
        };
    }
}
