package com.cocojenna.entity.goal;

import com.cocojenna.endgame.schedule.AfterRainActivity;
import com.cocojenna.endgame.schedule.AfterRainSchedules;
import com.cocojenna.endgame.schedule.ScheduleWaypoints;
import com.cocojenna.entity.JennaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

/** 珍奶雨後作息：早晨探索、白天跟隨玩家、黃昏玩耍. */
public class JennaDailyScheduleGoal extends Goal {

    private final JennaEntity jenna;
    private final AfterRainActivityGoal activityGoal;
    private List<BlockPos> explorePoints;
    private int exploreIndex;

    public JennaDailyScheduleGoal(JennaEntity jenna) {
        this.jenna = jenna;
        this.activityGoal = new AfterRainActivityGoal(jenna, AfterRainSchedules::forJenna);
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(jenna.level() instanceof ServerLevel level) || !AfterRainSchedules.isActive(level)) {
            return false;
        }
        if (!jenna.isEndgame() && !fallback(level)) return false;
        AfterRainActivity act = AfterRainSchedules.forJenna(AfterRainSchedules.dayTime(level));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = jenna.findOwner();
            return owner != null && jenna.distanceTo(owner) > 5.0;
        }
        if (act == AfterRainActivity.EXPLORE) return true;
        return activityGoal.canUse();
    }

    private boolean fallback(ServerLevel level) {
        Player owner = jenna.findOwner();
        if (owner == null) return false;
        return com.cocojenna.capability.ModCapabilities.getOrDefault(owner).isEndgameUnlocked();
    }

    @Override
    public boolean canContinueToUse() {
        if (!(jenna.level() instanceof ServerLevel level)) return false;
        AfterRainActivity act = AfterRainSchedules.forJenna(AfterRainSchedules.dayTime(level));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = jenna.findOwner();
            return owner != null && jenna.distanceTo(owner) > 2.5;
        }
        if (act == AfterRainActivity.EXPLORE) return true;
        return activityGoal.canContinueToUse();
    }

    @Override
    public void start() {
        exploreIndex = 0;
        explorePoints = ScheduleWaypoints.patrolNear(jenna.blockPosition());
        AfterRainActivity act = AfterRainSchedules.forJenna(
                AfterRainSchedules.dayTime((ServerLevel) jenna.level()));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = jenna.findOwner();
            if (owner != null) jenna.getNavigation().moveTo(owner, 1.2);
        } else if (act == AfterRainActivity.EXPLORE) {
            moveExplore();
        } else {
            activityGoal.start();
        }
    }

    @Override
    public void tick() {
        AfterRainActivity act = AfterRainSchedules.forJenna(
                AfterRainSchedules.dayTime((ServerLevel) jenna.level()));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = jenna.findOwner();
            if (owner != null) jenna.getNavigation().moveTo(owner, 1.2);
        } else if (act == AfterRainActivity.EXPLORE) {
            if (explorePoints != null && !explorePoints.isEmpty()) {
                BlockPos wp = explorePoints.get(exploreIndex);
                if (jenna.distanceToSqr(wp.getX() + 0.5, wp.getY(), wp.getZ() + 0.5) < 3.0) {
                    exploreIndex = (exploreIndex + 1) % explorePoints.size();
                    moveExplore();
                }
            }
        } else {
            activityGoal.tick();
        }
    }

    private void moveExplore() {
        if (explorePoints == null || explorePoints.isEmpty()) return;
        BlockPos wp = explorePoints.get(exploreIndex);
        jenna.getNavigation().moveTo(wp.getX() + 0.5, wp.getY(), wp.getZ() + 0.5, 1.0);
    }

    @Override
    public void stop() {
        activityGoal.stop();
    }
}
