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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 記憶蛾 — 序列 6；粘液彈降低序列 1 級 30 秒（§8.2）. */
public class MemoryMothEntity extends Monster implements RangedAttackMob, BlackMudMob {

    @Override
    public int blackMudSequence() { return 6; }

    public MemoryMothEntity(EntityType<? extends MemoryMothEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.42)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 18.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.LearningAttackGoal(this, 1.2, true));
        goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 30, 14.0f));
        goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        var nav = new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        MemoryMothSlimeProjectile slime = new MemoryMothSlimeProjectile(level(), this);
        slime.setPos(getX(), getEyeY() - 0.1, getZ());
        Vec3 dir = target.getEyePosition().subtract(getEyePosition()).normalize();
        slime.shoot(dir.x, dir.y, dir.z, 1.0f, 2.0f);
        level().addFreshEntity(slime);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof Player player) {
            var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
            player.getPersistentData().putInt("cocojenna_seq_debuff",
                    Math.min(9, bond.getFelineTier() + 1));
            player.getPersistentData().putInt("cocojenna_seq_debuff_ticks", 600);
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.WEAKNESS, 100, 0));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        if (random.nextFloat() < 0.55f + looting * 0.1f) {
            spawnAtLocation(new ItemStack(ModItems.MOTH_SCALE_POWDER.get(), 1 + random.nextInt(2)));
        }
    }
}
