package com.cocojenna.entity;

import com.cocojenna.combat.SpecialMobCombat;
import com.cocojenna.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 未完稿・折紙鴉 — 墨彈干擾視野，需朱槿落華／摺紙刀重寫（§10.2）. */
public class OrigamiCrowEntity extends Monster implements RangedAttackMob {

    public OrigamiCrowEntity(EntityType<? extends OrigamiCrowEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 35, 18.0f));
        goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 0.9));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (SpecialMobCombat.blocksDamage(this, source, amount)) {
            if (source.getEntity() instanceof Player p) {
                p.displayClientMessage(net.minecraft.network.chat.Component
                        .translatable("entity.cocojenna.origami_crow.immune"), true);
            }
            return false;
        }
        return super.hurt(source, amount * SpecialMobCombat.bonusDamage(this, source));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        Snowball ink = new Snowball(level(), this);
        Vec3 dir = target.getEyePosition().subtract(getEyePosition()).normalize();
        ink.shoot(dir.x, dir.y + 0.1, dir.z, 1.1f, 2.0f);
        level().addFreshEntity(ink);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() instanceof Player player && tickCount % 45 == 0
                && distanceToSqr(player) < 256) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, true));
        }
        if (level().isClientSide && tickCount % 8 == 0) {
            level().addParticle(ParticleTypes.SQUID_INK,
                    getX(), getY() + 0.4, getZ(), 0, -0.02, 0);
        }
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        var nav = new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(ModItems.ORIGAMI_SCRAP.get(), 2 + random.nextInt(2)));
        if (random.nextFloat() < 0.25f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
    }
}
