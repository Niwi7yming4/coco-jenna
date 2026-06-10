package com.cocojenna.world;

import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** 記憶紀念碑 — 隨記憶碎片 30→100 格成長（設計書 3.2）. */
public final class MonumentGrowthManager {

    private MonumentGrowthManager() {}

    public static int heightForShards(int shards) {
        return Math.min(100, 30 + (shards / 5) * 10);
    }

    public static void onShardsUpdated(ServerLevel level, int totalShards) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        KingdomBuildSavedData data = KingdomBuildSavedData.get(level);
        data.updateMonumentShards(totalShards);
        int target = heightForShards(data.monumentShardRecord());
        if (target != data.monumentHeightBuilt()) {
            rebuildTower(level, RegionGenerators.CENTRAL_PLAZA, target);
            data.setMonumentHeightBuilt(target);
        }
    }

    public static void ensureMonument(ServerLevel level) {
        KingdomBuildSavedData data = KingdomBuildSavedData.get(level);
        int h = Math.max(30, data.monumentHeightBuilt());
        if (h == 0) h = 30;
        rebuildTower(level, RegionGenerators.CENTRAL_PLAZA, h);
        data.setMonumentHeightBuilt(h);
    }

    private static void rebuildTower(ServerLevel level, BlockPos base, int height) {
        int bx = base.getX();
        int by = base.getY();
        int bz = base.getZ();
        for (int y = 1; y <= 105; y++) {
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    if (x * x + z * z <= 25) {
                        level.setBlock(new BlockPos(bx + x, by + y, bz + z), Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
        ArchitectureBuilders.buildScratchMonumentTower(level, base, height);
    }

    /** 純淨之核鑲嵌後，紀念碑周圍飄落金色粒子（設計書 卷六 §7.4）. */
    public static void tickPrimalCoreAura(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (level.getGameTime() % 12 != 0) return;
        if (!KingdomBuildSavedData.get(level).isPrimalCoreAnchored()) return;

        BlockPos top = RegionGenerators.CENTRAL_PLAZA;
        int h = KingdomBuildSavedData.get(level).monumentHeightBuilt();
        if (h < 30) h = 30;
        BlockPos aura = top.above(h + 2);
        level.sendParticles(ModParticles.STARDUST_SPARK.get(),
                aura.getX() + 0.5, aura.getY() + 0.5, aura.getZ() + 0.5,
                6, 2.5, 1.2, 2.5, 0.02);
        for (int i = 0; i < 4; i++) {
            double ang = level.random.nextDouble() * Math.PI * 2;
            double r = 2 + level.random.nextDouble() * 4;
            level.sendParticles(ModParticles.STARDUST_SPARK.get(),
                    aura.getX() + 0.5 + Math.cos(ang) * r,
                    aura.getY() + level.random.nextDouble() * 2,
                    aura.getZ() + 0.5 + Math.sin(ang) * r,
                    1, 0, 0.05, 0, 0.01);
        }
    }
}
