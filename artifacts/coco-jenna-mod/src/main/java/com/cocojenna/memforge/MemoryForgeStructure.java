package com.cocojenna.memforge;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 檢測記憶鑄造祭壇多方塊結構（設計書 3.1）. */
public final class MemoryForgeStructure {

    private MemoryForgeStructure() {}

    public static boolean isValidAltar(Level level, BlockPos enchantPos) {
        return findCore(level, enchantPos).isPresent();
    }

    public static Optional<BlockPos> findCore(Level level, BlockPos enchantPos) {
        if (!level.getBlockState(enchantPos).is(Blocks.ENCHANTING_TABLE)) {
            return Optional.empty();
        }
        BlockPos core = enchantPos.below(2);
        if (!level.getBlockState(core).is(Blocks.DIAMOND_BLOCK)) {
            return Optional.empty();
        }
        if (!checkLayer1(level, core)) return Optional.empty();
        if (!checkLayer2(level, core.above(1))) return Optional.empty();
        if (!checkLayer3(level, core, enchantPos)) return Optional.empty();
        return Optional.of(core);
    }

    private static boolean checkLayer1(Level level, BlockPos core) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = core.offset(dx, 0, dz);
                BlockState s = level.getBlockState(p);
                if (dx == 0 && dz == 0) continue;
                boolean corner = Math.abs(dx) == 2 && Math.abs(dz) == 2;
                boolean edgeGold = (Math.abs(dx) == 2 && dz == 0) || (Math.abs(dz) == 2 && dx == 0);
                if (corner) {
                    if (!s.is(Blocks.OBSIDIAN)) return false;
                } else if (edgeGold) {
                    if (!s.is(Blocks.GOLD_BLOCK)) return false;
                } else if (!s.is(Blocks.STONE_BRICKS)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkLayer2(Level level, BlockPos layerBase) {
        BlockPos core = layerBase;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = core.offset(dx, 0, dz);
                boolean corner = Math.abs(dx) == 2 && Math.abs(dz) == 2;
                boolean edgeMoon = (Math.abs(dx) == 2 && dz == 0) || (Math.abs(dz) == 2 && dx == 0);
                boolean inner = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
                if (inner) {
                    if (!level.getBlockState(p).isAir()) return false;
                } else if (corner) {
                    if (!level.getBlockState(p).is(Blocks.CRYING_OBSIDIAN)) return false;
                } else if (edgeMoon) {
                    if (!level.getBlockState(p).is(ModBlocks.MOONSTONE_BRICK.get())) return false;
                } else if (!level.getBlockState(p).is(Blocks.STONE_BRICKS)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** 祭壇結構中可被黑泥破壞的方塊位置. */
    public static List<BlockPos> altarBlocks(Level level, BlockPos core, BlockPos enchantPos) {
        List<BlockPos> blocks = new ArrayList<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos p = core.offset(dx, 0, dz);
                if (!level.getBlockState(p).isAir()) {
                    blocks.add(p.immutable());
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = core.offset(dx, 2, dz);
                if (!level.getBlockState(p).isAir()) {
                    blocks.add(p.immutable());
                }
            }
        }
        return blocks;
    }

    private static boolean checkLayer3(Level level, BlockPos core, BlockPos enchantPos) {
        if (!enchantPos.equals(core.above(2))) return false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = core.offset(dx, 2, dz);
                if (dx == 0 && dz == 0) {
                    if (!level.getBlockState(p).is(Blocks.ENCHANTING_TABLE)) return false;
                } else if (dx == 0 || dz == 0) {
                    if (!level.getBlockState(p).is(ModBlocks.PURR_CRYSTAL_BLOCK.get())) return false;
                } else if (!level.getBlockState(p).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }
}
