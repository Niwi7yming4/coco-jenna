package com.cocojenna.entity;

import com.cocojenna.event.ModEventHandler;
import com.cocojenna.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 篡位者・影爪 (Shadow Claw) — 最終 Boss 🌑
 *
 * <p>多階段戰鬥系統：
 * <ul>
 *   <li>第一階段（100% ‑ 70% HP）：基礎攻擊，緩慢移動</li>
 *   <li>第二階段（70% ‑ 40% HP）：黑泥爆炸，召喚黑泥爬行者</li>
 *   <li>第三階段（40% ‑ 0% HP）：全場黑泥侵蝕，狂暴模式</li>
 * </ul>
 *
 * <p>擊敗後：觸發「初晴」終局事件。
 */
public class GeneralCatEntity extends PathfinderMob {

    public enum Phase { ONE, TWO, THREE }

    private Phase currentPhase = Phase.ONE;
    private boolean phaseTransitioning = false;
    private int phaseTransitionTick = 0;
    private int specialAttackCooldown = 0;

    public GeneralCatEntity(EntityType<? extends GeneralCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new ShadowClawAttackGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            updatePhase();
            if (specialAttackCooldown > 0) specialAttackCooldown--;
        }
    }

    private void updatePhase() {
        float hpFraction = getHealth() / getMaxHealth();
        Phase newPhase = hpFraction > 0.70f ? Phase.ONE
                : hpFraction > 0.40f ? Phase.TWO : Phase.THREE;

        if (newPhase != currentPhase && !phaseTransitioning) {
            currentPhase = newPhase;
            phaseTransitioning = true;
            phaseTransitionTick = 60; // 3 秒過渡

            // 相變音效
            level().playSound(null, blockPosition(),
                    ModSounds.WORLD_BLACK_MUD_SPREAD.get(), SoundSource.HOSTILE, 2.0f,
                    currentPhase == Phase.TWO ? 0.8f : 0.5f);
        }

        if (phaseTransitioning) {
            phaseTransitionTick--;
            if (phaseTransitionTick <= 0) phaseTransitioning = false;
        }
    }

    /** HP = 0 → 觸發初晴事件 */
    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (getHealth() - amount <= 0) {
            // 觸發終局
            if (source.getEntity() instanceof ServerPlayer player) {
                ModEventHandler.triggerFirstDawn(player);
            }

            // 發光爆炸粒子
            level().playSound(null, blockPosition(),
                    ModSounds.WORLD_FIRST_DAWN.get(), SoundSource.MASTER, 3.0f, 1.0f);

            discard();
            return;
        }
        super.actuallyHurt(source, amount);
    }

    // 第二階段特殊攻擊：黑泥爆炸
    public void performBlackMudExplosion() {
        if (specialAttackCooldown > 0) return;
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            double dx = Math.cos(angle) * 3;
            double dz = Math.sin(angle) * 3;
            // 在附近設置黑泥方塊
            net.minecraft.core.BlockPos pos = blockPosition().offset((int) dx, 0, (int) dz);
            if (level().getBlockState(pos).isAir()) {
                level().setBlock(pos, com.cocojenna.init.ModBlocks.BLACK_MUD.get().defaultBlockState(),
                        3);
            }
        }
        specialAttackCooldown = 200;
    }

    public Phase getCurrentPhase() { return currentPhase; }
    public boolean isPhaseTransitioning() { return phaseTransitioning; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Phase", currentPhase.name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Phase")) {
            try { currentPhase = Phase.valueOf(tag.getString("Phase")); }
            catch (Exception ignored) {}
        }
    }

    // ── 內部 Goal ──────────────────────────────────────────────────────

    private static class ShadowClawAttackGoal extends Goal {
        private final GeneralCatEntity boss;
        ShadowClawAttackGoal(GeneralCatEntity b) { this.boss = b; }

        @Override
        public boolean canUse() {
            return boss.getTarget() != null && boss.getCurrentPhase() == Phase.TWO;
        }

        @Override
        public void tick() {
            if (boss.getCurrentPhase() == Phase.TWO && !boss.isPhaseTransitioning()) {
                boss.performBlackMudExplosion();
            }
        }
    }
}
