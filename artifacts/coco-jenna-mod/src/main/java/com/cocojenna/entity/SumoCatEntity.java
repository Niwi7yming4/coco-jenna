package com.cocojenna.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 相撲貓 (Sumo Cat) 🏋️
 * 超高HP，移動慢，不殺人，只推倒玩家、護國型。
 * 技能：地面震擊（範圍擊退）、抱摔（抓住玩家投擲）
 */
public class SumoCatEntity extends PathfinderMob {

    private int groundSlamCooldown = 0;
    private int throwCooldown = 0;

    public SumoCatEntity(EntityType<? extends SumoCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.8, true));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (groundSlamCooldown > 0) groundSlamCooldown--;
        if (throwCooldown > 0) throwCooldown--;
    }

    /** 地面震擊 — 跳起後砸地，範圍 5 格擊退 */
    public void performGroundSlam() {
        if (groundSlamCooldown > 0) return;
        Vec3 pos = position();
        level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(5.0))
                .forEach(p -> {
                    Vec3 dir = p.position().subtract(pos).normalize();
                    p.setDeltaMovement(dir.x * 1.5, 0.5, dir.z * 1.5);
                    p.hurt(damageSources().mobAttack(this), 4.0f);
                });
        groundSlamCooldown = 120;
    }

    /** 不殺人：HP 降至 1 就停止攻擊 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            float newHp = getHealth() - amount;
            if (newHp <= 0) {
                spawnSealOrb();
                discard();
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    private void spawnSealOrb() {
        SealedEntity seal = com.cocojenna.init.ModEntities.SEALED_ENTITY.get().create(level());
        if (seal != null) {
            seal.setPos(position());
            seal.setOriginalEntity("sumo_cat");
            level().addFreshEntity(seal);
        }
    }
}
