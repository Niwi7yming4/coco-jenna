package com.cocojenna.world;

import com.cocojenna.exploration.ExplorationMarkers;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 遺忘高塔 — 墮落絨尾 Boss 區域. */
public final class ForgottenTowerGenerator {

    public static final BlockPos CENTER = new BlockPos(-256, 72, 512);
    public static final BlockPos FERRY_LANDING = new BlockPos(-256, 63, 480);
    private static final BlockPos MARKER = new BlockPos(-256, 72, 512);

    private ForgottenTowerGenerator() {}

    public static void ensureTower(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getBlockState(MARKER).is(ModBlocks.SHADOW_CRYSTAL_BLOCK.get())) return;
        build(level);
    }

    private static void build(Level level) {
        int cx = CENTER.getX();
        int cy = CENTER.getY();
        int cz = CENTER.getZ();

        ArchitectureBuilders.buildForgottenTowerSpire(level, CENTER, 16);

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x * x + z * z <= 16) {
                    set(level, cx + x, cy - 1, cz + z, ModBlocks.SALT_BLOCK.get().defaultBlockState());
                }
            }
        }

        set(level, cx, cy - 2, cz, ModBlocks.BLACK_MUD.get().defaultBlockState());
        set(level, cx, cy - 3, cz, ModBlocks.UNDERCAT_LIGHTHOUSE_WELL.get().defaultBlockState());
        set(level, cx + 3, cy, cz + 3, ModBlocks.SEAL_PEDESTAL.get().defaultBlockState());
        set(level, cx - 3, cy + 1, cz - 2, ModBlocks.MEMORY_MONUMENT_BASE.get().defaultBlockState());

        for (int i = -3; i <= 3; i++) {
            set(level, cx + i, FERRY_LANDING.getY(), FERRY_LANDING.getZ(), ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
            set(level, cx + i, FERRY_LANDING.getY() + 1, FERRY_LANDING.getZ(), ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        }

        if (level instanceof ServerLevel server) {
            BlackMudBossHelper.trySpawnBoss(server, ModEntities.FALLEN_VELVET.get(),
                    new BlockPos(cx, cy, cz + 3));
            ExplorationMarkers.placeForgottenTower(server, CENTER);
        }
    }

    private static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }
}
