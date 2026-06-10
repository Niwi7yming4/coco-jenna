package com.cocojenna.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/** 遊擊型 AI — 打一擊後拉開距離（設計書 4.2）. */
public class HitAndRunMeleeGoal extends MeleeAttackGoal {

    private int retreatTicks;

    public HitAndRunMeleeGoal(PathfinderMob mob, double speed, boolean pauseWhenIdle) {
        super(mob, speed, pauseWhenIdle);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            super.tick();
            return;
        }
        if (retreatTicks > 0) {
            retreatTicks--;
            mob.getNavigation().moveTo(
                    mob.getX() + (mob.getX() - target.getX()),
                    mob.getY(),
                    mob.getZ() + (mob.getZ() - target.getZ()), 1.2);
            mob.getLookControl().setLookAt(target, 30f, 30f);
            return;
        }
        super.tick();
        if (mob.distanceToSqr(target) < 2.5 && mob.getRandom().nextInt(3) == 0) {
            retreatTicks = 30 + mob.getRandom().nextInt(20);
        }
    }
}
