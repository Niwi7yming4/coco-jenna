package com.cocojenna.entity;

import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** 黑泥農夫 — 主世界腐化農場菁英（設計書 主世界再多點 §3.1）. */
public class MudFarmerEntity extends Monster implements BlackMudMob {

    private int vineCooldown;

    @Override
    public int blackMudSequence() { return 8; }

    public MudFarmerEntity(EntityType<? extends MudFarmerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.55));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || vineCooldown > 0) {
            if (vineCooldown > 0) vineCooldown--;
            return;
        }
        LivingEntity target = getTarget();
        if (target != null && distanceToSqr(target) < 64 && tickCount % 20 == 0) {
            vineCooldown = 300;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            target.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE1.get(), 60, 0));
        }
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 2));
        spawnAtLocation(new ItemStack(Items.WHEAT, 2 + random.nextInt(4)));
        if (random.nextFloat() < 0.1f + looting * 0.03f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
    }
}
