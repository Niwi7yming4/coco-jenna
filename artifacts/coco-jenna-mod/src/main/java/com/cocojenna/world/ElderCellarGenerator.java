package com.cocojenna.world;

import com.cocojenna.world.firstcry.FirstCryAnchorTable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/** 初啼村儲藏室 — 委派至 {@link DungeonGenerators}. */
public final class ElderCellarGenerator {

    public static final BlockPos SURFACE_ENTRANCE = FirstCryAnchorTable.ELDER_CELLAR_ENTRANCE;
    public static final BlockPos ANCHOR = new BlockPos(2, 28, 0);

    private ElderCellarGenerator() {}

    public static BlockPos ensure(ServerLevel level, @Nullable net.minecraft.server.level.ServerPlayer player) {
        return DungeonGenerators.ensure(level, "elder_cellar", player);
    }

    public static boolean isGenerated(Level level) {
        return level instanceof ServerLevel sl && DungeonGenerators.isGenerated(sl, "elder_cellar");
    }
}
