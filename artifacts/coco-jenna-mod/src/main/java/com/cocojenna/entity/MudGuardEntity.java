package com.cocojenna.entity;

import com.cocojenna.init.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 黑泥守衛 — 貓形雕像，免疫擊退，死亡爆炸（設計書 主世界再多點 §3.1）. */
public class MudGuardEntity extends Monster implements BlackMudMob {

    @Override
    public int blackMudSequence() { return 7; }

    public MudGuardEntity(EntityType<? extends MudGuardEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 65.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.45));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 1.2f, Level.ExplosionInteraction.MOB);
        }
        super.die(source);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 3));
        if (random.nextFloat() < 0.3f + looting * 0.05f) {
            spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()));
        }
        if (random.nextFloat() < 0.2f + looting * 0.04f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
    }
}
