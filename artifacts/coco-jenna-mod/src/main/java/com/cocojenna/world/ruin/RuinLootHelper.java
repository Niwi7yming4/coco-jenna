package com.cocojenna.world.ruin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** 在已放置結構範圍內尋找寶箱並填入戰利品表. */
public final class RuinLootHelper {

    private RuinLootHelper() {}

    public static void fillChestsInStructure(ServerLevel level, BlockPos origin, StructureTemplate template,
            RuinMatrixRegistry ruin) {
        Vec3i size = template.getSize();
        int maxX = Math.max(1, size.getX());
        int maxY = Math.max(1, size.getY());
        int maxZ = Math.max(1, size.getZ());
        boolean filled = false;
        for (int dx = 0; dx < maxX; dx++) {
            for (int dy = 0; dy < maxY; dy++) {
                for (int dz = 0; dz < maxZ; dz++) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (level.getBlockState(p).is(Blocks.CHEST)) {
                        fillChest(level, p, ruin);
                        filled = true;
                    }
                }
            }
        }
        if (!filled) {
            ProceduralRuinBuilder.fillLootChest(level, origin.above(), ruin);
        }
    }

    public static void fillChest(ServerLevel level, BlockPos chestPos, RuinMatrixRegistry ruin) {
        ProceduralRuinBuilder.fillLootChest(level, chestPos, ruin);
    }
}
