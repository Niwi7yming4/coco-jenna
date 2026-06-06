package com.cocojenna.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 仕女貓 (Court Lady Cat) 💃
 * 戰鬥像舞蹈、優雅輸出。不主動攻擊，會迴避，利用環境戰鬥。
 * 技能：幻影步（殘影瞬移 8 格）、魅惑（玩家短暫攻擊友軍）
 */
public class CourtLadyCatEntity extends PathfinderMob {

    private int phantomCooldown = 0;
    private int charmCooldown = 0;

    public CourtLadyCatEntity(EntityType<? extends CourtLadyCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.40)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 6.0f, 1.3, 1.5));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.7));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (phantomCooldown > 0) phantomCooldown--;
        if (charmCooldown > 0) charmCooldown--;
    }

    /** 幻影步 — 留下殘影，瞬移 8 格 */
    public void performPhantomStep() {
        if (phantomCooldown > 0) return;
        double angle = Math.random() * Math.PI * 2;
        double dist = 6 + Math.random() * 2;
        teleportTo(getX() + Math.cos(angle) * dist, getY(), getZ() + Math.sin(angle) * dist);
        // 生成殘影粒子
        level().addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL,
                getX(), getY() + 1, getZ(), 0, 0, 0);
        phantomCooldown = 80;
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (getHealth() - amount <= 0) {
            SealedEntity seal = com.cocojenna.init.ModEntities.SEALED_ENTITY.get().create(level());
            if (seal != null) {
                seal.setPos(position());
                seal.setOriginalEntity("court_lady");
                level().addFreshEntity(seal);
            }
            discard();
            return;
        }
        // 受傷時機率觸發幻影步
        if (random.nextFloat() < 0.4f) performPhantomStep();
        super.actuallyHurt(source, amount);
    }
}
