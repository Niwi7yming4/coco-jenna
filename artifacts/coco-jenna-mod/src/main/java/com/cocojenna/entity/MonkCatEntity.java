package com.cocojenna.entity;

import com.cocojenna.init.ModEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 貓僧 (Monk Cat) 🧘
 * 防守型、會讀取玩家行為。靜止時反擊，透過氣場減速玩家。
 * 技能：靜止反制（玩家攻擊時反擊）、氣場壓制（範圍緩速）
 */
public class MonkCatEntity extends PathfinderMob {

    private boolean isStill = false;
    private int stillTick = 0;
    private int auraRadius = 4;
    private int counterWindow = 0;

    public MonkCatEntity(EntityType<? extends MonkCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MonkMeditateGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.9, true));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (counterWindow > 0) counterWindow--;

        // 氣場壓制：對範圍內玩家施加緩速
        if (!level().isClientSide && level().getGameTime() % 20 == 0) {
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(auraRadius))
                    .forEach(p -> p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false)));
        }
    }

    /** 靜止反制 — 靜止時玩家攻擊有機率反擊 */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isStill && source.getEntity() instanceof Player attacker && counterWindow <= 0) {
            // 反擊
            attacker.hurt(damageSources().mobAttack(this),
                    (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5f);
            attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
            counterWindow = 40;
        }
        if (getHealth() - amount <= 0) {
            SealedEntity seal = com.cocojenna.init.ModEntities.SEALED_ENTITY.get().create(level());
            if (seal != null) {
                seal.setPos(position());
                seal.setOriginalEntity("monk_cat");
                level().addFreshEntity(seal);
            }
            discard();
            return false;
        }
        return super.hurt(source, amount);
    }

    public void setStill(boolean still) { isStill = still; }
    public boolean isStillMeditating() { return isStill; }

    private static class MonkMeditateGoal extends Goal {
        private final MonkCatEntity monk;
        private int meditateTick = 0;

        MonkMeditateGoal(MonkCatEntity m) {
            this.monk = m;
            setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return monk.getTarget() != null && monk.random.nextFloat() < 0.005f;
        }

        @Override
        public boolean canContinueToUse() {
            return meditateTick > 0;
        }

        @Override
        public void start() {
            meditateTick = 60;
            monk.setStill(true);
            monk.getNavigation().stop();
        }

        @Override
        public void tick() { meditateTick--; }

        @Override
        public void stop() { monk.setStill(false); }
    }
}
