package com.cocojenna.entity;

import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.world.damagesource.DamageSource;
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

/** 黑泥祭司 — 遠程黑泥球，治療周圍黑泥（設計書 主世界再多點 §3.1）. */
public class MudPriestEntity extends Monster implements BlackMudMob, RangedAttackMob {

    private int healCooldown;
    private int shootCooldown;

    @Override
    public int blackMudSequence() { return 6; }

    public MudPriestEntity(EntityType<? extends MudPriestEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 55.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 16.0f));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.4));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (healCooldown > 0) healCooldown--;
        if (shootCooldown > 0) shootCooldown--;
        if (tickCount % 20 == 0 && healCooldown <= 0) {
            healCooldown = 200;
            for (LivingEntity ally : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8))) {
                if (ally instanceof BlackMudMob && ally != this && ally.getHealth() < ally.getMaxHealth()) {
                    ally.heal(10.0f);
                }
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (shootCooldown > 0) return;
        shootCooldown = 30;
        Snowball ball = new Snowball(level(), this);
        ball.setItem(new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()));
        double dx = target.getX() - getX();
        double dy = target.getEyeY() - getEyeY();
        double dz = target.getZ() - getZ();
        ball.shoot(dx, dy + 0.2, dz, 1.1f, 4.0f);
        level().addFreshEntity(ball);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                var sludge = ModEntities.WANDERING_SLUDGE.get().create(level());
                if (sludge != null) {
                    sludge.setPos(getX() + random.nextGaussian() * 0.5,
                            getY(), getZ() + random.nextGaussian() * 0.5);
                    level().addFreshEntity(sludge);
                }
            }
        }
        super.die(source);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 2));
        if (random.nextFloat() < 0.5f) {
            spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()));
        }
        if (random.nextFloat() < 0.4f + looting * 0.05f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
    }
}
