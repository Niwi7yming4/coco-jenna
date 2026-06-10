package com.cocojenna.entity.goal;

import com.cocojenna.endgame.schedule.AfterRainActivity;
import com.cocojenna.endgame.schedule.AfterRainSchedules;
import com.cocojenna.endgame.schedule.ScheduleWaypoints;
import com.cocojenna.entity.AbstractCatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.function.LongFunction;

/** 雨後定時活動（巡邏／曬太陽／釣魚／睡眠／高處凝視）. */
public class AfterRainActivityGoal extends Goal {

    private final PathfinderMob mob;
    private final LongFunction<AfterRainActivity> activityResolver;
    private AfterRainActivity activity = AfterRainActivity.IDLE;
    private List<BlockPos> patrol;
    private int patrolIndex;
    private BlockPos targetPos;
    private int actionTick;
    private Player followTarget;

    public AfterRainActivityGoal(PathfinderMob mob, LongFunction<AfterRainActivity> activityResolver) {
        this.mob = mob;
        this.activityResolver = activityResolver;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!(mob.level() instanceof ServerLevel level) || !AfterRainSchedules.isActive(level)) {
            return false;
        }
        if (mob.getTarget() != null) return false;
        activity = activityResolver.apply(AfterRainSchedules.dayTime(level));
        return activity != AfterRainActivity.IDLE && activity != AfterRainActivity.EXPLORE
                && activity != AfterRainActivity.FOLLOW_OWNER;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(mob.level() instanceof ServerLevel level) || !AfterRainSchedules.isActive(level)) {
            return false;
        }
        AfterRainActivity now = activityResolver.apply(AfterRainSchedules.dayTime(level));
        return now == activity && mob.getTarget() == null;
    }

    @Override
    public void start() {
        actionTick = 0;
        patrolIndex = 0;
        targetPos = null;
        setResting(false);
        prepareTarget();
    }

    @Override
    public void tick() {
        actionTick++;
        switch (activity) {
            case PATROL -> tickPatrol();
            case SUNBATHE -> tickSunbathe();
            case FISH -> tickFish();
            case SLEEP -> tickSleep();
            case HIGH_GAZE -> tickHighGaze();
            default -> { }
        }
    }

    @Override
    public void stop() {
        setResting(false);
        mob.getNavigation().stop();
    }

    private void setResting(boolean resting) {
        if (mob instanceof AbstractCatEntity cat) {
            cat.setSitting(resting);
        } else if (resting) {
            mob.getNavigation().stop();
        }
    }

    private void prepareTarget() {
        switch (activity) {
            case PATROL -> {
                patrol = ScheduleWaypoints.patrolNear(mob.blockPosition());
                if (!patrol.isEmpty()) {
                    targetPos = patrol.get(0);
                    mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(),
                            targetPos.getZ() + 0.5, 0.75);
                }
            }
            case SUNBATHE -> {
                targetPos = ScheduleWaypoints.findSunSpot(mob.level(), mob.blockPosition());
                if (targetPos != null) {
                    mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(),
                            targetPos.getZ() + 0.5, 0.6);
                }
            }
            case FISH -> {
                targetPos = ScheduleWaypoints.findFishSpot(mob.level(), mob.blockPosition());
                if (targetPos != null) {
                    mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(),
                            targetPos.getZ() + 0.5, 0.55);
                }
            }
            case SLEEP, HIGH_GAZE -> setResting(true);
            default -> { }
        }
    }

    private void tickPatrol() {
        if (patrol == null || patrol.isEmpty()) return;
        BlockPos wp = patrol.get(patrolIndex);
        if (mob.distanceToSqr(wp.getX() + 0.5, wp.getY(), wp.getZ() + 0.5) < 2.5) {
            patrolIndex = (patrolIndex + 1) % patrol.size();
            wp = patrol.get(patrolIndex);
            mob.getNavigation().moveTo(wp.getX() + 0.5, wp.getY(), wp.getZ() + 0.5, 0.75);
        }
    }

    private void tickSunbathe() {
        if (targetPos == null) return;
        if (mob.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5) < 2.0) {
            mob.getNavigation().stop();
            setResting(true);
        }
    }

    private void tickFish() {
        if (targetPos == null) return;
        if (mob.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5) < 2.5) {
            mob.getNavigation().stop();
            setResting(true);
            if (actionTick % 40 == 0 && mob.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.FISHING, targetPos.getX() + 0.5, targetPos.getY() + 0.2,
                        targetPos.getZ() + 0.5, 4, 0.2, 0.05, 0.2, 0.01);
            }
        }
    }

    private void tickSleep() {
        mob.getNavigation().stop();
        setResting(true);
    }

    private void tickHighGaze() {
        mob.getNavigation().stop();
        setResting(true);
        mob.getLookControl().setLookAt(mob.getX(), mob.getY() + 30, mob.getZ() + 20);
    }
}
