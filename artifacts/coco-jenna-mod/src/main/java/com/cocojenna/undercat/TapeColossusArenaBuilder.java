package com.cocojenna.undercat;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 膠帶巨像二階段戰場 — 膠帶柱與紙箱圍欄. */
public final class TapeColossusArenaBuilder {

    private TapeColossusArenaBuilder() {}

    public static void buildPhaseArena(ServerLevel level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            int px = cx + (int) Math.round(Math.cos(angle) * 10);
            int pz = cz + (int) Math.round(Math.sin(angle) * 10);
            for (int y = 0; y <= 3; y++) {
                set(level, px, cy + y, pz, ModBlocks.TAPE_BLOCK.get());
            }
            set(level, px, cy + 4, pz, ModBlocks.NEON_MUSH_LAMP.get());
        }

        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                if (Math.abs(dx) != 12 && Math.abs(dz) != 12) continue;
                if ((dx + dz) % 3 != 0) continue;
                set(level, cx + dx, cy + 1, cz + dz, ModBlocks.REINFORCED_CARDBOARD.get());
            }
        }

        for (int i = -1; i <= 1; i++) {
            level.setBlock(new BlockPos(cx + i, cy, cz - 12), Blocks.WATER.defaultBlockState(), 2);
            level.setBlock(new BlockPos(cx + i, cy, cz + 12), Blocks.WATER.defaultBlockState(), 2);
        }
    }

    private static void set(ServerLevel level, int x, int y, int z,
            net.minecraft.world.level.block.Block block) {
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 2);
    }
}
