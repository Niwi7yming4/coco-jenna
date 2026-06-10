package com.cocojenna.entity.goal;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** 防禦型 AI — 低血量後退並獲得抗性（設計書 4.2）. */
public class DefensiveGoal extends Goal {

    private final PathfinderMob mob;
    private int defensiveTicks;

    public DefensiveGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && mob.getHealth() / mob.getMaxHealth() < 0.45f;
    }

    @Override
    public void start() {
        defensiveTicks = 50 + mob.getRandom().nextInt(30);
        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, defensiveTicks, 1));
        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, defensiveTicks, 0));
    }

    @Override
    public boolean canContinueToUse() {
        return defensiveTicks > 0 && mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public void tick() {
        defensiveTicks--;
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        mob.getNavigation().moveTo(
                mob.getX() + (mob.getX() - target.getX()) * 1.5,
                mob.getY(),
                mob.getZ() + (mob.getZ() - target.getZ()) * 1.5,
                1.1);
        mob.getLookControl().setLookAt(target, 30f, 30f);
    }
}
