package com.cocojenna.entity;

import com.cocojenna.init.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** 失溫者 — 黑泥序列 9；吸收周圍溫度（設計書 §8.2）. */
public class HeatLeechEntity extends Monster implements BlackMudMob {

    private static final double CHILL_RADIUS = 6.0;

    @Override
    public int blackMudSequence() { return 9; }

    public HeatLeechEntity(EntityType<? extends HeatLeechEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 22.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.DefensiveGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || tickCount % 20 != 0) return;

        AABB box = getBoundingBox().inflate(CHILL_RADIUS);
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && e != this && !(e instanceof HeatLeechEntity))) {
            living.addEffect(new MobEffectInstance(ModEffects.HEAT_LEECH_CHILL.get(), 40, 0, false, true));
        }
        if (tickCount % 40 == 0) {
            level().addParticle(ParticleTypes.SNOWFLAKE,
                    getX(), getY() + 0.2, getZ(), 0, 0.02, 0);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE1.get(), 120, 0));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        if (random.nextFloat() < 0.45f + looting * 0.1f) {
            spawnAtLocation(new ItemStack(com.cocojenna.init.ModItems.BLACK_MUD_REMNANT.get(),
                    1 + random.nextInt(2)));
        }
    }
}
