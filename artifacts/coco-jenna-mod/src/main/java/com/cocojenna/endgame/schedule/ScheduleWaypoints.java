package com.cocojenna.endgame.schedule;

import com.cocojenna.world.BlindPortGenerator;
import com.cocojenna.world.FirstCryVillageGenerator;
import com.cocojenna.world.GearTownGenerator;
import com.cocojenna.world.MoonAlleyGenerator;
import com.cocojenna.world.VelvetForestPoiGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;

/** 巡邏路點與環境搜尋. */
public final class ScheduleWaypoints {

    private record Region(BlockPos center, int radius) {}

    private static final Region[] REGIONS = {
            new Region(FirstCryVillageGenerator.CENTER, 20),
            new Region(GearTownGenerator.CENTER, 18),
            new Region(BlindPortGenerator.CENTER, 16),
            new Region(MoonAlleyGenerator.CENTER, 14),
            new Region(VelvetForestPoiGenerator.CENTER, 16),
    };

    private ScheduleWaypoints() {}

    public static List<BlockPos> patrolNear(BlockPos origin) {
        Region region = nearestRegion(origin);
        List<BlockPos> points = new ArrayList<>();
        int cx = region.center.getX();
        int cz = region.center.getZ();
        int y = region.center.getY();
        int r = region.radius;
        points.add(new BlockPos(cx + r, y, cz));
        points.add(new BlockPos(cx, y, cz + r));
        points.add(new BlockPos(cx - r, y, cz));
        points.add(new BlockPos(cx, y, cz - r));
        points.add(new BlockPos(cx + r / 2, y, cz + r / 2));
        return points;
    }

    public static BlockPos findSunSpot(Level level, BlockPos origin) {
        for (int dx = -12; dx <= 12; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                BlockPos ground = origin.offset(dx, 0, dz);
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos check = ground.offset(0, dy, 0);
                    BlockState below = level.getBlockState(check.below());
                    if (!below.isSolidRender(level, check.below())) continue;
                    if (!level.getBlockState(check).isAir()) continue;
                    if (level.canSeeSky(check) && level.getMaxLocalRawBrightness(check) >= 12) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    public static BlockPos findFishSpot(Level level, BlockPos origin) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -20; dx <= 20; dx++) {
            for (int dz = -20; dz <= 20; dz++) {
                for (int dy = -2; dy <= 1; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    FluidState fluid = level.getFluidState(pos);
                    if (!fluid.is(FluidTags.WATER)) continue;
                    BlockPos stand = pos.above();
                    if (!level.getBlockState(stand).isAir()) continue;
                    double d = origin.distSqr(stand);
                    if (d < bestDist) {
                        bestDist = d;
                        best = stand;
                    }
                }
            }
        }
        return best;
    }

    private static Region nearestRegion(BlockPos origin) {
        Region best = REGIONS[0];
        double bestDist = origin.distSqr(best.center);
        for (Region r : REGIONS) {
            double d = origin.distSqr(r.center);
            if (d < bestDist) {
                bestDist = d;
                best = r;
            }
        }
        return best;
    }
}
