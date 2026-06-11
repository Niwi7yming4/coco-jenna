package com.cocojenna.entity;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.combat.CombatVfxHelper;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModSounds;
import com.cocojenna.undercat.TapeColossusArenaBuilder;
import com.cocojenna.undercat.UndercatCommission;
import com.cocojenna.undercat.UndercatQuestManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/** 膠帶巨像 — 三階段 Boss（批次 C）. */
public class TapeColossusEntity extends Monster {

    public enum Phase { ONE, TWO, THREE }

    private static final UUID RAGE_SPEED = UUID.fromString("a4e2c8f1-7b3d-4e9a-9c12-8f6d5e4a3b2c");
    private static final EntityDataAccessor<Integer> SYNC_PHASE =
            SynchedEntityData.defineId(TapeColossusEntity.class, EntityDataSerializers.INT);

    private Phase currentPhase = Phase.ONE;
    private boolean phaseTransitioning;
    private int phaseTransitionTick;
    private int specialCooldown;
    private int summonCooldown = 200;
    private boolean arenaBuilt;

    public TapeColossusEntity(EntityType<? extends TapeColossusEntity> type, Level level) {
        super(type, level);
        this.xpReward = 120;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.ARMOR, 6);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SYNC_PHASE, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(3, new TapeBurstGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) return;
        if (phaseTransitioning) {
            setDeltaMovement(Vec3.ZERO);
            phaseTransitionTick--;
            if (phaseTransitionTick <= 0) {
                phaseTransitioning = false;
                setInvulnerable(false);
            }
            return;
        }
        updatePhase();
        if (specialCooldown > 0) specialCooldown--;
        if (currentPhase == Phase.THREE) {
            if (--summonCooldown <= 0) {
                summonCooldown = 240;
                trySummonGhosts();
            }
        }
    }

    private void updatePhase() {
        float hp = getHealth() / getMaxHealth();
        Phase next = hp > 0.70f ? Phase.ONE : hp > 0.40f ? Phase.TWO : Phase.THREE;
        if (next != currentPhase) {
            enterPhase(next);
        }
    }

    private void enterPhase(Phase next) {
        currentPhase = next;
        entityData.set(SYNC_PHASE, next.ordinal());
        phaseTransitioning = true;
        phaseTransitionTick = 50;
        setInvulnerable(true);
        updatePhaseName();

        if (level() instanceof ServerLevel sl) {
            CombatVfxHelper.bossPhaseShift(sl, position(), next.ordinal() + 1);
            level().playSound(null, blockPosition(), ModSounds.WORLD_BLACK_MUD_SPREAD.get(),
                    SoundSource.HOSTILE, 1.5f, next == Phase.TWO ? 0.9f : 0.6f);
            if (next == Phase.TWO && !arenaBuilt) {
                arenaBuilt = true;
                TapeColossusArenaBuilder.buildPhaseArena(sl, blockPosition());
            }
            if (next == Phase.THREE) {
                applyRageSpeed();
                trySummonGhosts();
            }
        }
    }

    private void applyRageSpeed() {
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(RAGE_SPEED) == null) {
            speed.addTransientModifier(new AttributeModifier(RAGE_SPEED, "tape_rage",
                    0.12, AttributeModifier.Operation.ADDITION));
        }
        AttributeInstance dmg = getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            dmg.setBaseValue(16);
        }
    }

    private void updatePhaseName() {
        setCustomName(Component.translatable("entity.cocojenna.tape_colossus")
                .append(" · ")
                .append(Component.translatable("undercat.cocojenna.boss_phase." + currentPhase.name().toLowerCase())));
        setCustomNameVisible(true);
    }

    public void performTapeBurst() {
        if (specialCooldown > 0 || currentPhase == Phase.ONE) return;
        specialCooldown = currentPhase == Phase.THREE ? 80 : 120;
        if (!(level() instanceof ServerLevel sl)) return;

        BlockPos center = blockPosition();
        int radius = currentPhase == Phase.THREE ? 5 : 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                BlockPos p = center.offset(dx, 0, dz);
                if (level().getBlockState(p).isAir()) {
                    level().setBlock(p, ModBlocks.TAPE_BLOCK.get().defaultBlockState(), 3);
                }
            }
        }
        CombatVfxHelper.blackMudExplosion(sl, position());
        AABB box = getBoundingBox().inflate(radius + 1);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e instanceof Player)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            if (currentPhase == Phase.THREE) {
                target.knockback(0.6, center.getX() - target.getX(), center.getZ() - target.getZ());
            }
        }
    }

    private void trySummonGhosts() {
        if (!(level() instanceof ServerLevel sl)) return;
        for (int i = 0; i < 2; i++) {
            var ghost = ModEntities.BOX_GHOST.get().create(sl);
            if (ghost == null) continue;
            double ox = (random.nextDouble() - 0.5) * 6;
            double oz = (random.nextDouble() - 0.5) * 6;
            ghost.setPos(getX() + ox, getY(), getZ() + oz);
            ghost.finalizeSpawn(sl, sl.getCurrentDifficultyAt(blockPosition()),
                    MobSpawnType.TRIGGERED, null, null);
            sl.addFreshEntity(ghost);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        }
        return hit;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer sp) {
            BondData bond = ModCapabilities.getOrDefault(sp);
            if (bond.getUndercatChapter() != 1
                    || UndercatCommission.countCompletedForChapter(bond.getUndercatCommissions(), 1) < 3) {
                setHealth(getMaxHealth() * 0.6f);
                currentPhase = Phase.ONE;
                entityData.set(SYNC_PHASE, 0);
                arenaBuilt = false;
                phaseTransitioning = false;
                setInvulnerable(false);
                sp.displayClientMessage(Component.translatable("undercat.cocojenna.need_commissions"), true);
                return;
            }
        }
        super.die(source);
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer sp) {
            UndercatQuestManager.onTapeColossusDefeated(sp);
        }
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isPhaseTransitioning() {
        return phaseTransitioning;
    }

    private static class TapeBurstGoal extends Goal {
        private final TapeColossusEntity boss;

        TapeBurstGoal(TapeColossusEntity boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            return boss.getTarget() != null
                    && boss.getCurrentPhase() != Phase.ONE
                    && !boss.isPhaseTransitioning();
        }

        @Override
        public void tick() {
            if (boss.tickCount % 40 == 0) {
                boss.performTapeBurst();
            }
        }
    }
}
