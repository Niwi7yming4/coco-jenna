package com.cocojenna.entity.goal;

import com.cocojenna.entity.JennaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

/**
 * 魚群召喚 Goal — 珍奶覺醒 ≥ 3 時，玩家釣魚中在附近坐下，
 * 把尾巴放進水中搖晃，提升釣魚效率。
 */
public class JennaFishSummonGoal extends Goal {

    private final JennaEntity jenna;
    private BlockPos waterTarget;
    private int summonTick = 0;

    public JennaFishSummonGoal(JennaEntity jenna) {
        this.jenna = jenna;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (jenna.getAwakeningPhase() < 3) return false;

        Player owner = jenna.level().getNearestPlayer(jenna, 10.0);
        if (owner == null) return false;

        // 檢查玩家是否正在釣魚
        if (owner.fishing == null) return false;

        // 找附近水塊
        waterTarget = findNearbyWater();
        return waterTarget != null;
    }

    @Override
    public boolean canContinueToUse() {
        Player owner = jenna.level().getNearestPlayer(jenna, 15.0);
        return owner != null && owner.fishing != null && summonTick < 600;
    }

    @Override
    public void start() {
        summonTick = 0;
        if (waterTarget != null) {
            jenna.getNavigation().moveTo(waterTarget.getX() + 0.5, waterTarget.getY(), waterTarget.getZ() + 0.5, 1.0);
        }
    }

    @Override
    public void tick() {
        summonTick++;

        if (waterTarget != null && jenna.distanceToSqr(waterTarget.getX(), waterTarget.getY(), waterTarget.getZ()) < 2.0) {
            jenna.getNavigation().stop();
            jenna.setSitting(true);

            // 發光粒子效果
            if (summonTick % 10 == 0) {
                Level level = jenna.level();
                level.addParticle(ParticleTypes.FISHING,
                        waterTarget.getX() + 0.5, waterTarget.getY() + 0.5, waterTarget.getZ() + 0.5,
                        (jenna.getRandom().nextDouble() - 0.5) * 0.3,
                        0.1,
                        (jenna.getRandom().nextDouble() - 0.5) * 0.3);
            }
        }
    }

    @Override
    public void stop() {
        jenna.setSitting(false);
        summonTick = 0;
    }

    private BlockPos findNearbyWater() {
        BlockPos base = jenna.blockPosition();
        Level level = jenna.level();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos check = base.offset(dx, 0, dz);
                if (level.isWaterAt(check) && level.getBlockState(check.above()).isAir()) {
                    return check;
                }
            }
        }
        return null;
    }
}
