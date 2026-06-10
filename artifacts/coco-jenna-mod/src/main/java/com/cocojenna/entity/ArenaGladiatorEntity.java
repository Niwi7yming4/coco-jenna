package com.cocojenna.entity;

import com.cocojenna.undercat.UndercatQuestManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** 競技場角鬥士. */
public class ArenaGladiatorEntity extends Monster {

    private static final EntityDataAccessor<Integer> KIND =
            SynchedEntityData.defineId(ArenaGladiatorEntity.class, EntityDataSerializers.INT);

    public enum Kind {
        IRON_FIST(1, 80, 14, 0.18),
        SHADOW_STEP(2, 50, 8, 0.42),
        POISON_FANG(4, 60, 7, 0.32);

        public final int flag;
        public final double health;
        public final double damage;
        public final double speed;

        Kind(int flag, double health, double damage, double speed) {
            this.flag = flag;
            this.health = health;
            this.damage = damage;
            this.speed = speed;
        }
    }

    public ArenaGladiatorEntity(EntityType<? extends ArenaGladiatorEntity> type, Level level) {
        super(type, level);
        this.xpReward = 40;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 8);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(KIND, 0);
    }

    public void setKind(Kind kind) {
        entityData.set(KIND, kind.ordinal());
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(kind.health);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(kind.damage);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(kind.speed);
        setHealth((float) kind.health);
    }

    public Kind getKind() {
        return Kind.values()[Math.min(entityData.get(KIND), Kind.values().length - 1)];
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            switch (getKind()) {
                case IRON_FIST -> living.knockback(0.8, -getLookAngle().x, -getLookAngle().z);
                case SHADOW_STEP -> {
                    if (random.nextFloat() < 0.35f) {
                        teleportTo(target.getX() + random.nextDouble() - 0.5,
                                target.getY(), target.getZ() + random.nextDouble() - 0.5);
                    }
                }
                case POISON_FANG -> living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            }
        }
        return hit;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer sp) {
            UndercatQuestManager.onGladiatorDefeated(sp, getKind());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Kind", entityData.get(KIND));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Kind")) {
            setKind(Kind.values()[tag.getInt("Kind")]);
        }
    }
}
