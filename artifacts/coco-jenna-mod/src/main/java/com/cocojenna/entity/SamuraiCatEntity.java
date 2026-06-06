package com.cocojenna.entity;

import com.cocojenna.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 貓武士 (Samurai Cat) ⚔️
 *
 * <p>特性：喜歡單挑、不追擊逃跑者、決鬥前鞠躬。
 * <p>技能：
 * <ul>
 *   <li>拔刀斬 — 蓄力衝刺 5 格，造成高傷害</li>
 *   <li>反擊 — 玩家攻擊後 0.5 秒內有 30% 機率反擊</li>
 * </ul>
 *
 * <p>戰鬥規則：
 * <ul>
 *   <li>不主動攻擊，等待玩家進入 10 格範圍並做出進攻姿態</li>
 *   <li>玩家逃跑（距離 > 15 格）→ 停止追擊，回到原位</li>
 *   <li>HP 歸零 → 封印物（紅色光球）</li>
 * </ul>
 */
public class SamuraiCatEntity extends PathfinderMob {

    // 戰鬥狀態
    private boolean bowed = false;           // 已鞠躬
    private int dashCooldown = 0;
    private int counterCooldown = 0;
    private boolean inDuel = false;
    private Player duelTarget = null;
    private net.minecraft.world.phys.Vec3 homePos;

    public SamuraiCatEntity(EntityType<? extends SamuraiCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SamuraiDuelGoal(this));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new SamuraiDuelTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (homePos == null) homePos = position();
        if (dashCooldown > 0) dashCooldown--;
        if (counterCooldown > 0) counterCooldown--;

        // 玩家逃跑時停止追擊
        if (inDuel && duelTarget != null) {
            if (duelTarget.distanceTo(this) > 15.0) {
                inDuel = false;
                duelTarget = null;
                getNavigation().moveTo(homePos.x, homePos.y, homePos.z, 0.8);
            }
        }
    }

    /** 拔刀斬 — 衝刺攻擊 */
    public void performDashSlash(LivingEntity target) {
        if (dashCooldown > 0) return;
        net.minecraft.world.phys.Vec3 dir = target.position().subtract(position()).normalize().scale(0.8);
        setDeltaMovement(dir.x, 0.2, dir.z);
        dashCooldown = 100;
    }

    /** 反擊 — 被攻擊後觸發 */
    public boolean tryCounter(LivingEntity attacker) {
        if (counterCooldown > 0 || random.nextFloat() > 0.3f) return false;
        attacker.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
        level().playSound(null, blockPosition(), ModSounds.ENTITY_SEAL_FORM.get(),
                SoundSource.NEUTRAL, 1.0f, 1.5f);
        counterCooldown = 40;
        return true;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            tryCounter(attacker);
        }
        return super.hurt(source, amount);
    }

    /** HP 歸零 → 封印物（不死亡） */
    @Override
    protected void actuallyHurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (getHealth() - amount <= 0) {
            spawnSealOrb();
            discard();
            return;
        }
        super.actuallyHurt(source, amount);
    }

    private void spawnSealOrb() {
        SealedEntity seal = com.cocojenna.init.ModEntities.SEALED_ENTITY.get().create(level());
        if (seal != null) {
            seal.setPos(position());
            seal.setOriginalEntity("samurai_cat");
            level().addFreshEntity(seal);
        }
    }

    public void startDuel(Player player) {
        if (!bowed) {
            bowed = true;
            inDuel = true;
            duelTarget = player;
            // 鞠躬動畫由 Model 驅動（此處標記狀態）
            level().playSound(null, blockPosition(), ModSounds.COCO_MEOW_SHORT.get(),
                    SoundSource.NEUTRAL, 1.0f, 0.5f);
        }
    }

    public boolean isInDuel() { return inDuel; }
    public Player getDuelTarget() { return duelTarget; }

    // ── 內部 Goal ──────────────────────────────────────────────────────

    private static class SamuraiDuelGoal extends Goal {
        private final SamuraiCatEntity samurai;
        SamuraiDuelGoal(SamuraiCatEntity s) { this.samurai = s; }

        @Override
        public boolean canUse() {
            return samurai.isInDuel() && samurai.getDuelTarget() != null;
        }

        @Override
        public void tick() {
            Player t = samurai.getDuelTarget();
            if (t == null) return;
            samurai.getNavigation().moveTo(t, 1.2);
            if (samurai.distanceTo(t) < 2.0 && samurai.dashCooldown <= 0) {
                samurai.performDashSlash(t);
            }
        }
    }

    private static class SamuraiDuelTargetGoal extends Goal {
        private final SamuraiCatEntity samurai;
        SamuraiDuelTargetGoal(SamuraiCatEntity s) { this.samurai = s; }

        @Override
        public boolean canUse() {
            Player p = samurai.level().getNearestPlayer(samurai, 10.0);
            if (p == null) return false;
            if (samurai.isInDuel()) return false;
            samurai.startDuel(p);
            return false;
        }
    }
}
