package com.cocojenna.weapon;

import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Shared combat helpers for per-weapon unique skills. */
public final class WeaponSkillEffects {

    private WeaponSkillEffects() {}

    public static void slashCone(Player player, Level level, double range, float dmg) {
        Vec3 look = player.getLookAngle().normalize();
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.2);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> t != player && t.isAlive())) {
            Vec3 to = e.position().subtract(player.position()).normalize();
            if (look.dot(to) > 0.35) {
                e.hurt(level.damageSources().playerAttack(player), dmg);
            }
        }
    }

    public static void hurtSphere(Player player, Level level, double radius, float dmg,
            java.util.function.Consumer<LivingEntity> extra) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> t != player && t.isAlive())) {
            e.hurt(level.damageSources().playerAttack(player), dmg);
            if (extra != null) extra.accept(e);
        }
    }

    public static void pullAndDamage(Player player, Level level, double radius, float dmg, double pull) {
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius), t -> t != player && t.isAlive())) {
            Vec3 toPlayer = player.position().subtract(e.position()).normalize().scale(pull);
            e.setDeltaMovement(toPlayer.x, 0.15, toPlayer.z);
            e.hurt(level.damageSources().playerAttack(player), dmg);
        }
    }

    public static void applyDebuffSphere(Player player, Level level, double radius, int duration, int amp) {
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius), t -> t != player && t.isAlive())) {
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amp));
        }
    }

    public static void healSelfAndCats(Player player, Level level, float amount) {
        player.heal(amount);
        player.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 80, 0));
        for (AbstractCatEntity cat : level.getEntitiesOfClass(AbstractCatEntity.class,
                player.getBoundingBox().inflate(4.0),
                c -> player.getUUID().equals(c.getOwnerUUID()))) {
            cat.heal(amount);
        }
    }

    public static void stealthBurst(Player player, int duration, int speedAmp) {
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, speedAmp, false, false));
    }

    public static void spawnFishSchool(Player player, Level level, int count, float dmg) {
        if (!(level instanceof ServerLevel sl)) return;
        for (int i = 0; i < count; i++) {
            double ox = (level.random.nextDouble() - 0.5) * 4;
            double oz = (level.random.nextDouble() - 0.5) * 4;
            sl.sendParticles(ParticleTypes.FISHING, player.getX() + ox, player.getY() + 0.5, player.getZ() + oz,
                    3, 0.2, 0.1, 0.2, 0.01);
        }
        hurtSphere(player, level, 4.5 + count * 0.3, dmg,
                e -> e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 0)));
    }

    public static void summonPaperCrow(Player player, Level level) {
        if (!(level instanceof ServerLevel sl)) return;
        var crow = ModEntities.ORIGAMI_CROW.get().create(sl);
        if (crow != null) {
            crow.moveTo(player.getX(), player.getY() + 1, player.getZ(), player.getYRot(), 0);
            crow.setTarget(player.getLastHurtMob());
            sl.addFreshEntity(crow);
        }
    }

    public static void particles(Level level, Player player, net.minecraft.core.particles.ParticleOptions type, int count) {
        if (!(level instanceof ServerLevel sl)) return;
        sl.sendParticles(type, player.getX(), player.getY() + 1.0, player.getZ(),
                count, 0.5, 0.3, 0.5, 0.02);
    }
}
