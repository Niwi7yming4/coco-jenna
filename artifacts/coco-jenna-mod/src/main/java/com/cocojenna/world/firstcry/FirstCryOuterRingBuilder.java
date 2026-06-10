package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/** 外環圍籬、四入口、村內主路（設計書 3.9）. */
public final class FirstCryOuterRingBuilder {

    private static final BlockPos[] GATES = {
            FirstCryLayout.GATE_NORTH,
            FirstCryLayout.GATE_EAST,
            FirstCryLayout.GATE_SOUTH,
            FirstCryLayout.GATE_WEST
    };
    private static final Direction[] GATE_DIRS = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    private FirstCryOuterRingBuilder() {}

    public static void build(ServerLevel level) {
        BlockPos center = FirstCryLayout.CENTER;
        int radius = FirstCryLayout.RADIUS - 2;
        for (int a = 0; a < 40; a++) {
            double ang = a * Math.PI * 2 / 40;
            int px = (int) Math.round(Math.cos(ang) * radius);
            int pz = (int) Math.round(Math.sin(ang) * radius);
            if (a % 5 != 0) {
                FirstCryBuildHelper.set(level, center.offset(px, 0, pz),
                        ModBlocks.VELVET_TREE_LOG.get().defaultBlockState());
            }
            if (a % 2 == 0) {
                FirstCryBuildHelper.set(level, center.offset(px, 1, pz),
                        ModBlocks.VELVET_VINE.get().defaultBlockState());
            }
            if (a % 4 == 1) {
                FirstCryBuildHelper.set(level, center.offset(px, 0, pz + 1),
                        ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState());
            }
            if (a % 8 == 3) {
                FirstCryBuildHelper.set(level, center.offset(px, 1, pz),
                        ModBlocks.WOVEN_WOOL.get().defaultBlockState());
            }
        }
        for (int i = 0; i < GATES.length; i++) {
            buildGate(level, GATES[i], GATE_DIRS[i]);
            buildMainRoad(level, center, GATE_DIRS[i], radius - 6);
        }
    }

    private static void buildGate(ServerLevel level, BlockPos gate, Direction outward) {
        Direction side = outward.getClockWise();
        FirstCryBuildHelper.set(level, gate.relative(side, 2), ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
        FirstCryBuildHelper.set(level, gate.relative(side.getOpposite(), 2),
                ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
        for (int i = -1; i <= 1; i++) {
            FirstCryBuildHelper.set(level, gate.relative(outward.getOpposite(), 1).relative(side, i),
                    ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        }
        FirstCryBuildHelper.tablet(level, gate.relative(outward, 2), 9);
        FirstCryBuildHelper.set(level, gate.relative(side, 3), ModBlocks.CAT_BED.get().defaultBlockState());
    }

    private static void buildMainRoad(ServerLevel level, BlockPos center, Direction dir, int length) {
        int dx = dir.getStepX();
        int dz = dir.getStepZ();
        int sideX = dir.getClockWise().getStepX();
        int sideZ = dir.getClockWise().getStepZ();
        for (int i = 8; i <= length; i++) {
            for (int w = -2; w <= 2; w++) {
                BlockPos p = center.offset(dx * i + sideX * w, 0, dz * i + sideZ * w);
                FirstCryBuildHelper.set(level, p, FirstCryBuildHelper.brick());
                if (Math.abs(w) == 2) {
                    FirstCryBuildHelper.set(level, p.above(), ModBlocks.SALT_BLOCK.get().defaultBlockState());
                }
            }
            if (i % 8 == 0) {
                FirstCryBuildHelper.set(level, center.offset(dx * i, 1, dz * i),
                        ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
            }
            if (i % 5 == 0) {
                FirstCryBuildHelper.set(level, center.offset(dx * i, 0, dz * i),
                        ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
            }
        }
    }
}
