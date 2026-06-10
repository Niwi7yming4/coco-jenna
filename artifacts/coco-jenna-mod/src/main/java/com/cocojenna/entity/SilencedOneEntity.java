package com.cocojenna.entity;

import com.cocojenna.undercat.UndercatQuestManager;
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

/** 無聲者 — 第四章 Boss. */
public class SilencedOneEntity extends Monster {

    private int silenceCooldown;
    private int truthHits;

    public SilencedOneEntity(EntityType<? extends SilencedOneEntity> type, Level level) {
        super(type, level);
        this.xpReward = 250;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.ATTACK_DAMAGE, 11)
                .add(Attributes.ARMOR, 6);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        if (silenceCooldown > 0) silenceCooldown--;
        LivingEntity target = getTarget();
        if (target instanceof Player player && silenceCooldown <= 0 && distanceToSqr(player) < 49) {
            silenceCooldown = 200;
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            truthHits++;
            if (truthHits >= 3) {
                getAttribute(Attributes.ARMOR).setBaseValue(1);
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer sp) {
            UndercatQuestManager.onSilencedOneDefeated(sp);
        }
    }
}
