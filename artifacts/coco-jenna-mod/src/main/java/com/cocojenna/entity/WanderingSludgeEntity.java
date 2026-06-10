package com.cocojenna.entity;

import com.cocojenna.init.ModItems;
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

/** 流浪黑泥 — 主世界專用，被動直到玩家靠近（設計書 主世界再多點 §3.1）. */
public class WanderingSludgeEntity extends Monster implements BlackMudMob {

    private static final double PROVOKE_RANGE = 2.5;

    @Override
    public int blackMudSequence() { return 9; }

    public WanderingSludgeEntity(EntityType<? extends WanderingSludgeEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0)
                .add(Attributes.MOVEMENT_SPEED, 0.14)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.9, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 1));
        if (random.nextFloat() < 0.05f + looting * 0.02f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
        if (random.nextFloat() < 0.3f) {
            var child = com.cocojenna.init.ModEntities.WANDERING_SLUDGE.get().create(level());
            if (child != null) {
                child.setPos(getX(), getY(), getZ());
                var health = child.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    health.setBaseValue(10.0);
                    child.setHealth(10.0f);
                }
                level().addFreshEntity(child);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount % 20 == 0) {
            LivingEntity target = getTarget();
            if (target instanceof Player player) {
                if (distanceToSqr(player) > PROVOKE_RANGE * PROVOKE_RANGE * 4) {
                    setTarget(null);
                }
            } else {
                Player nearest = level().getNearestPlayer(this, PROVOKE_RANGE);
                if (nearest != null) setTarget(nearest);
            }
        }
    }
}
