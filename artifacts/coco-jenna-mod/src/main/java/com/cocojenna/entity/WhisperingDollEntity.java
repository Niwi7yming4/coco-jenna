package com.cocojenna.entity;

import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.cocojenna.entity.goal.SummonMinionGoal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 低語泥偶 — 序列 7；模仿雙貓叫聲引誘（§8.2）. */
public class WhisperingDollEntity extends Monster implements BlackMudMob {

    private int lureCooldown;
    private boolean revealed;

    @Override
    public int blackMudSequence() { return 7; }

    public WhisperingDollEntity(EntityType<? extends WhisperingDollEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 28.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(3, new SummonMinionGoal(this));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.65));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || revealed) return;
        Player nearest = level().getNearestPlayer(this, 24.0);
        if (nearest == null || getTarget() != null) return;
        if (--lureCooldown > 0) return;
        lureCooldown = 100;
        boolean coco = random.nextBoolean();
        nearest.displayClientMessage(Component.translatable(
                coco ? "entity.cocojenna.whispering_doll.lure_coco" : "entity.cocojenna.whispering_doll.lure_jenna"), false);
        Vec3 toPlayer = nearest.position().subtract(position()).normalize().scale(0.04);
        setDeltaMovement(getDeltaMovement().add(toPlayer.x, 0, toPlayer.z));
        if (distanceToSqr(nearest) < 16) {
            setTarget(nearest);
            revealed = true;
            nearest.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE2.get(), 80, 0));
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE2.get(), 100, 0));
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        if (random.nextFloat() < 0.4f) {
            spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 2));
        }
        if (random.nextFloat() < 0.15f + looting * 0.05f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
    }
}
