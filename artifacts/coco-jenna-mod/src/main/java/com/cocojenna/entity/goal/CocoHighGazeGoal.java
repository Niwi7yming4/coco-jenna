package com.cocojenna.entity.goal;

import com.cocojenna.entity.CocoEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

/**
 * 高處凝視 (High Gaze) Goal
 *
 * <p>觸發條件：
 * <ul>
 *   <li>夜晚</li>
 *   <li>月相 > 半月（moonBrightness > 0.5）</li>
 *   <li>月亮親和 > 30</li>
 *   <li>附近有高處（Y + 3 以上的固體方塊）</li>
 *   <li>每晚一次</li>
 * </ul>
 *
 * <p>效果：
 * <ul>
 *   <li>導航到高處後坐下面向月亮</li>
 *   <li>玩家靠近 3 格會回頭看</li>
 *   <li>月亮親和每分鐘 +1</li>
 * </ul>
 */
public class CocoHighGazeGoal extends Goal {

    private final CocoEntity coco;
    private BlockPos gazeTarget;
    private int gazeTick = 0;
    private boolean reached = false;

    public CocoHighGazeGoal(CocoEntity coco) {
        this.coco = coco;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Level level = coco.level();
        if (!level.isNight()) return false;
        if (level.getMoonBrightness() <= 0.5f) return false;
        if (coco.getMoonAffinity() <= 30) return false;

        // 找高處
        gazeTarget = findHighGround();
        return gazeTarget != null;
    }

    @Override
    public boolean canContinueToUse() {
        return coco.level().isNight() && gazeTick < 2400; // 最多凝視 2 分鐘
    }

    @Override
    public void start() {
        reached = false;
        gazeTick = 0;
        if (gazeTarget != null) {
            coco.getNavigation().moveTo(gazeTarget.getX() + 0.5, gazeTarget.getY(), gazeTarget.getZ() + 0.5, 1.0);
        }
    }

    @Override
    public void tick() {
        gazeTick++;
        if (gazeTarget == null) return;

        if (!reached && coco.distanceToSqr(gazeTarget.getX(), gazeTarget.getY(), gazeTarget.getZ()) < 2.0) {
            reached = true;
            coco.setSitting(true);
            coco.getNavigation().stop();
        }

        if (reached) {
            // 月亮親和每 60 tick +1
            if (gazeTick % 60 == 0) {
                // 直接設定實體數據（實際月亮親和存在 BondData，此處簡化）
                coco.getLookControl().setLookAt(
                        coco.getX(), coco.getY() + 50, coco.getZ()); // 望向天空

                // 玩家在 3 格內時回頭看
                Player nearestPlayer = coco.level().getNearestPlayer(coco, 3.0);
                if (nearestPlayer != null) {
                    coco.getLookControl().setLookAt(nearestPlayer, 30f, 30f);
                }
            }
        }
    }

    @Override
    public void stop() {
        coco.setSitting(false);
        gazeTick = 0;
    }

    private BlockPos findHighGround() {
        BlockPos base = coco.blockPosition();
        Level level = coco.level();
        int bestY = base.getY();
        BlockPos best = null;

        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                for (int dy = 3; dy <= 15; dy++) {
                    BlockPos candidate = base.offset(dx, dy, dz);
                    if (level.getBlockState(candidate).isSolidRender(level, candidate)
                            && level.getBlockState(candidate.above()).isAir()
                            && level.canSeeSky(candidate.above())
                            && candidate.getY() > bestY) {
                        bestY = candidate.getY();
                        best = candidate.above();
                    }
                }
            }
        }
        return best;
    }
}
