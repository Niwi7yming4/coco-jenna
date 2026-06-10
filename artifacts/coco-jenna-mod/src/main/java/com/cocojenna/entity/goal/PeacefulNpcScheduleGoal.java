package com.cocojenna.entity.goal;

import com.cocojenna.endgame.schedule.AfterRainNpcRole;
import com.cocojenna.endgame.schedule.AfterRainSchedules;
import com.cocojenna.entity.SamuraiCatEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** 城鎮 NPC 雨後職階排程. */
public class PeacefulNpcScheduleGoal extends Goal {

    private final PathfinderMob mob;
    private final AfterRainNpcRole role;
    private final AfterRainActivityGoal activityGoal;

    public PeacefulNpcScheduleGoal(PathfinderMob mob, AfterRainNpcRole role) {
        this.mob = mob;
        this.role = role;
        this.activityGoal = new AfterRainActivityGoal(mob, t -> AfterRainSchedules.forNpc(role, t));
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob instanceof SamuraiCatEntity samurai && samurai.isInDuel()) return false;
        return activityGoal.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return activityGoal.canContinueToUse();
    }

    @Override
    public void start() {
        activityGoal.start();
    }

    @Override
    public void tick() {
        activityGoal.tick();
    }

    @Override
    public void stop() {
        activityGoal.stop();
    }
}
