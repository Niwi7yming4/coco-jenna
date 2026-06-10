package com.cocojenna.entity.goal;

import com.cocojenna.combat.CombatVfxHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

/** 自爆型 AI — 低血量衝刺並在接觸時引爆（設計書 4.2）. */
public class SuicideExplosionGoal extends Goal {

    private final PathfinderMob mob;
    private final float triggerHpRatio;
    private boolean primed;

    public SuicideExplosionGoal(PathfinderMob mob) {
        this(mob, 0.28f);
    }

    public SuicideExplosionGoal(PathfinderMob mob, float triggerHpRatio) {
        this.mob = mob;
        this.triggerHpRatio = triggerHpRatio;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        return mob.getHealth() / mob.getMaxHealth() <= triggerHpRatio
                || (primed && mob.distanceToSqr(target) < 9);
    }

    @Override
    public void start() {
        primed = true;
        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 2));
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        mob.getNavigation().moveTo(target, 1.35);
        if (mob.distanceToSqr(target) < 2.5) {
            explode();
        }
    }

    private void explode() {
        if (mob.level().isClientSide) return;
        if (mob.level() instanceof net.minecraft.server.level.ServerLevel server) {
            CombatVfxHelper.blackMudExplosion(server, mob.position());
        }
        List<LivingEntity> nearby = mob.level().getEntitiesOfClass(LivingEntity.class,
                mob.getBoundingBox().inflate(4),
                e -> e != mob && e.isAlive() && e instanceof Player);
        DamageSource src = mob.damageSources().magic();
        for (LivingEntity victim : nearby) {
            victim.hurt(src, 6f + mob.getRandom().nextFloat() * 4f);
        }
        mob.hurt(mob.damageSources().magic(), mob.getMaxHealth());
    }
}
