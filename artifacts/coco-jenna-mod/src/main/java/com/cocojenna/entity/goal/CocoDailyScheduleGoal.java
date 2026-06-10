package com.cocojenna.entity.goal;

import com.cocojenna.endgame.schedule.AfterRainActivity;
import com.cocojenna.endgame.schedule.AfterRainSchedules;
import com.cocojenna.entity.CocoEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 可可雨後作息：早晨巡邏、上午曬太陽、下午小憩、黃昏高處凝視. */
public class CocoDailyScheduleGoal extends Goal {

    private final CocoEntity coco;
    private final AfterRainActivityGoal activityGoal;

    public CocoDailyScheduleGoal(CocoEntity coco) {
        this.coco = coco;
        this.activityGoal = new AfterRainActivityGoal(coco, AfterRainSchedules::forCoco);
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(coco.level() instanceof ServerLevel level) || !AfterRainSchedules.isActive(level)) {
            return false;
        }
        if (!coco.isEndgame() && !scheduleFallback(level)) return false;
        AfterRainActivity act = AfterRainSchedules.forCoco(AfterRainSchedules.dayTime(level));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = coco.findOwner();
            return owner != null && coco.distanceTo(owner) > 6.0;
        }
        return activityGoal.canUse();
    }

    private boolean scheduleFallback(ServerLevel level) {
        Player owner = coco.findOwner();
        if (owner == null) return false;
        return com.cocojenna.capability.ModCapabilities.getOrDefault(owner).isEndgameUnlocked();
    }

    @Override
    public boolean canContinueToUse() {
        if (!(coco.level() instanceof ServerLevel level)) return false;
        AfterRainActivity act = AfterRainSchedules.forCoco(AfterRainSchedules.dayTime(level));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = coco.findOwner();
            return owner != null && coco.distanceTo(owner) > 3.0;
        }
        return activityGoal.canContinueToUse();
    }

    @Override
    public void start() {
        AfterRainActivity act = AfterRainSchedules.forCoco(
                AfterRainSchedules.dayTime((ServerLevel) coco.level()));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = coco.findOwner();
            if (owner != null) {
                coco.getNavigation().moveTo(owner, 1.0);
            }
        } else {
            activityGoal.start();
        }
    }

    @Override
    public void tick() {
        AfterRainActivity act = AfterRainSchedules.forCoco(
                AfterRainSchedules.dayTime((ServerLevel) coco.level()));
        if (act == AfterRainActivity.FOLLOW_OWNER) {
            Player owner = coco.findOwner();
            if (owner != null) {
                coco.getNavigation().moveTo(owner, 1.0);
            }
        } else {
            activityGoal.tick();
        }
    }

    @Override
    public void stop() {
        activityGoal.stop();
    }
}
