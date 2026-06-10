package com.cocojenna.entity;

import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.cocojenna.entity.goal.HitAndRunMeleeGoal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 擬態貓 — 偽裝成野貓的黑泥怪物. */
public class MimicCatEntity extends Monster implements BlackMudMob {

    @Override
    public int blackMudSequence() { return 5; }

    private int disguiseTicks;

    public MimicCatEntity(EntityType<? extends MimicCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new HitAndRunMeleeGoal(this, 1.15, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() != null) {
            disguiseTicks = 0;
        } else if (!level().isClientSide) {
            disguiseTicks++;
        }
    }

    public boolean isDisguised() {
        return disguiseTicks < 80 && getTarget() == null;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE1.get(), 80, 1));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        if (random.nextFloat() < 0.5f) {
            spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 1 + random.nextInt(3)));
        }
    }
}
