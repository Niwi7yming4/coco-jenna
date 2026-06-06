package com.cocojenna.entity;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.goal.*;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * 可可 (Coco) — 黑貓・沉默的守護者
 *
 * <p>毛色：純黑 (#1a1a1a)，帶極細微深藍色光澤 (#2a2a3a)
 * <p>眼睛：琥珀金 (#ffbf00)
 * <p>特徵：尾巴尖端純白毛 (#ffffff)
 *
 * <p>核心數值：
 * <ul>
 *   <li>保護慾 (Protectiveness)：0‑100，初始 60</li>
 *   <li>月亮親和 (Moon Affinity)：0‑100，初始 40</li>
 * </ul>
 *
 * <p>專屬行為：
 * <ul>
 *   <li>抿你一口 (Bite of Trust)：情感 ≥ 3 + 靜止 5 秒</li>
 *   <li>高處凝視 (High Gaze)：夜晚 + 月相 > 半月</li>
 *   <li>靜止守護 (Still Guard)：玩家 HP < 40%</li>
 *   <li>空間拉回 (Spatial Pull)：覺醒 ≥ 4 + 玩家墜落 HP < 10%</li>
 *   <li>日光浴夥伴 (Sun Bath Companion)：終局後，白天陽光下</li>
 *   <li>額頭碰觸 (Forehead Touch)：終局後，蹲下對視 5 秒</li>
 *   <li>尾巴纏繞 (Tail Wrap)：終局後，玩家坐下 + 依戀值 > 70</li>
 * </ul>
 */
public class CocoEntity extends AbstractCatEntity {

    // ── 同步數據 ──────────────────────────────────────────────────────────

    private static final EntityDataAccessor<Float> DATA_PROTECTIVENESS =
            SynchedEntityData.defineId(CocoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_MOON_AFFINITY =
            SynchedEntityData.defineId(CocoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_PERFORMING_SPECIAL =
            SynchedEntityData.defineId(CocoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SPECIAL_ANIMATION =
            SynchedEntityData.defineId(CocoEntity.class, EntityDataSerializers.INT);

    // 特殊行為動畫 ID
    public static final int ANIM_NONE          = 0;
    public static final int ANIM_BITE_TRUST    = 1;  // 抿你一口
    public static final int ANIM_HIGH_GAZE     = 2;  // 高處凝視
    public static final int ANIM_STILL_GUARD   = 3;  // 靜止守護
    public static final int ANIM_SUNBATH       = 4;  // 日光浴夥伴（終局）
    public static final int ANIM_FOREHEAD      = 5;  // 額頭碰觸（終局）
    public static final int ANIM_TAIL_WRAP     = 6;  // 尾巴纏繞（終局）

    // ── 冷卻計時器（遊戲刻）─────────────────────────────────────────────

    private int biteTrustCooldown = 0;    // 每日 3 次 = 每次 8000 tick
    private int highGazeCooldown = 0;     // 每晚一次
    private int stillGuardTimer = 0;      // 守護持續時間
    private int spatialPullCooldown = 0;  // 每遊戲日 1 次

    // 終局行為計時器
    private int sunbathCooldown = 0;
    private int foreheadCooldown = 0;
    private int tailWrapCooldown = 0;
    private int windowWaitTick = 0;       // 窗邊等待計時

    // 凝視計時器（玩家蹲下對視）
    private int gazeAtPlayerTick = 0;

    public CocoEntity(EntityType<? extends CocoEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PROTECTIVENESS, 60f);
        entityData.define(DATA_MOON_AFFINITY, 40f);
        entityData.define(DATA_PERFORMING_SPECIAL, false);
        entityData.define(DATA_SPECIAL_ANIMATION, ANIM_NONE);
    }

    // ── 屬性 ─────────────────────────────────────────────────────────────

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractCatEntity.baseCatAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)       // 可可比較強壯
                .add(Attributes.MOVEMENT_SPEED, 0.33)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 3.0);
    }

    // ── AI 目標 ──────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 可可不主動攻擊，但會對威脅珍奶的對象嘶嘶
        goalSelector.addGoal(1, new CocoStillGuardGoal(this));
        goalSelector.addGoal(2, new CocoHighGazeGoal(this));
        goalSelector.addGoal(3, new CocoBiteTrustGoal(this));
    }

    // ── Tick 行為系統 ────────────────────────────────────────────────────

    @Override
    protected void tickBehaviors() {
        Player owner = findOwner();
        if (owner == null) return;

        BondData bond = ModCapabilities.getOrDefault(owner);

        // 冷卻計時器遞減
        if (biteTrustCooldown > 0)  biteTrustCooldown--;
        if (highGazeCooldown > 0)   highGazeCooldown--;
        if (spatialPullCooldown > 0) spatialPullCooldown--;
        if (sunbathCooldown > 0)    sunbathCooldown--;
        if (foreheadCooldown > 0)   foreheadCooldown--;
        if (tailWrapCooldown > 0)   tailWrapCooldown--;

        // ── 月亮親和增長 ─────────────────────────────────────────────────
        if (level().isNight() && isInMoonlight()) {
            bond.modifyCocoEmotion(0f);
            float moonAffinity = entityData.get(DATA_MOON_AFFINITY);
            entityData.set(DATA_MOON_AFFINITY, Math.min(100f, moonAffinity + 0.0007f)); // 每 tick +1/分
        }

        // ── 靜止守護 ─────────────────────────────────────────────────────
        if (bond.getCocoEmotionLevel().ordinal() >= BondData.EmotionLevel.ATTACHED.ordinal()) {
            float ownerHpFraction = owner.getHealth() / owner.getMaxHealth();
            if (ownerHpFraction < 0.4f && !owner.isUnderWater()) {
                if (distanceTo(owner) > 2.0) {
                    getNavigation().moveTo(owner, 1.2);
                } else {
                    // 在玩家旁邊趴下守護
                    entityData.set(DATA_SPECIAL_ANIMATION, ANIM_STILL_GUARD);
                    owner.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 40, 0, false, false));
                }
            } else {
                if (entityData.get(DATA_SPECIAL_ANIMATION) == ANIM_STILL_GUARD) {
                    entityData.set(DATA_SPECIAL_ANIMATION, ANIM_NONE);
                }
            }
        }

        // ── 空間拉回（覺醒 ≥ 4）────────────────────────────────────────
        if (getAwakeningPhase() >= 4 && spatialPullCooldown <= 0) {
            float ownerHpFraction = owner.getHealth() / owner.getMaxHealth();
            if (ownerHpFraction < 0.1f && owner.fallDistance > 5.0f) {
                performSpatialPull(owner, bond);
            }
        }

        // ── 終局行為 ─────────────────────────────────────────────────────
        if (isEndgame()) {
            tickEndgameBehaviors(owner, bond);
        }

        // ── 玩家長時間離開 → 窗邊等待 ───────────────────────────────────
        double distToOwner = distanceTo(owner);
        if (distToOwner > 100) {
            windowWaitTick++;
        } else {
            if (windowWaitTick > 0) {
                // 玩家回來了！跑向玩家
                getNavigation().moveTo(owner, 1.5);
                level().playSound(null, blockPosition(), ModSounds.COCO_MEOW_SHORT.get(),
                        SoundSource.NEUTRAL, 1.0f, 1.0f);
                windowWaitTick = 0;
            }
        }
    }

    private void tickEndgameBehaviors(Player owner, BondData bond) {
        boolean ownerStill = owner.getDeltaMovement().length() < 0.01;
        boolean daytime = !level().isNight();

        // 日光浴夥伴
        if (daytime && isInSunlight() && ownerStill && sunbathCooldown <= 0 && distanceTo(owner) < 5) {
            entityData.set(DATA_SPECIAL_ANIMATION, ANIM_SUNBATH);
            owner.addEffect(new MobEffectInstance(ModEffects.WARM_SERENITY.get(), 600, 0, false, false));
            sunbathCooldown = 24000;
        }

        // 額頭碰觸（玩家蹲下對視 5 秒 = 100 tick）
        if (owner.isCrouching() && distanceTo(owner) < 2.0 && foreheadCooldown <= 0) {
            if (getLookAngle().dot(owner.getLookAngle()) < -0.7) { // 面對面
                gazeAtPlayerTick++;
                if (gazeAtPlayerTick >= 100) {
                    entityData.set(DATA_SPECIAL_ANIMATION, ANIM_FOREHEAD);
                    owner.addEffect(new MobEffectInstance(ModEffects.MIND_SYNC.get(), 100, 0, false, false));
                    foreheadCooldown = 12000;
                    gazeAtPlayerTick = 0;
                }
            } else {
                gazeAtPlayerTick = 0;
            }
        }

        // 尾巴纏繞（玩家坐下 + 依戀值 > 70）
        if (bond.getCocoAttachment() > 70 && owner.isCrouching() && tailWrapCooldown <= 0
                && distanceTo(owner) < 2.0) {
            entityData.set(DATA_SPECIAL_ANIMATION, ANIM_TAIL_WRAP);
            owner.addEffect(new MobEffectInstance(ModEffects.REMEMBERED.get(), 12000, 0, false, false));
            tailWrapCooldown = 18000;
        }
    }

    /** 空間拉回：玩家即將墜落死亡時傳送到安全點 */
    private void performSpatialPull(Player owner, BondData bond) {
        // 尋找安全落點
        BlockPos safePos = findSafeGround(owner.blockPosition());
        if (safePos != null) {
            owner.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
            owner.fallDistance = 0;
            owner.addEffect(new MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.SLOW_FALLING, 60, 0, false, false));
            bond.modifyCocoEmotion(5f);
            level().playSound(null, blockPosition(), ModSounds.COCO_PURR_DEEP.get(),
                    SoundSource.NEUTRAL, 1.5f, 0.8f);
            spatialPullCooldown = 24000;
        }
    }

    private BlockPos findSafeGround(BlockPos start) {
        for (int y = start.getY(); y >= start.getY() - 30; y--) {
            BlockPos check = new BlockPos(start.getX(), y, start.getZ());
            if (level().getBlockState(check).isSolidRender(level(), check)
                    && level().getBlockState(check.above()).isAir()) {
                return check.above();
            }
        }
        return null;
    }

    private boolean isInMoonlight() {
        BlockPos pos = blockPosition();
        return level().canSeeSky(pos) && level().getMoonBrightness() > 0.5f;
    }

    // ── 抿你一口（公開供 Goal 呼叫）────────────────────────────────────

    public void performBiteTrust(Player player) {
        if (biteTrustCooldown > 0 || dailyBiteCount >= 3) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getCocoEmotionLevel().ordinal() < BondData.EmotionLevel.BONDED.ordinal()) return;

        entityData.set(DATA_SPECIAL_ANIMATION, ANIM_BITE_TRUST);
        level().playSound(null, blockPosition(), ModSounds.COCO_PURR_DEEP.get(),
                SoundSource.NEUTRAL, 0.8f, 1.2f);

        player.addEffect(new MobEffectInstance(ModEffects.COCOS_MARK.get(), 600, 0, false, true));
        bond.modifyCocoEmotion(0.5f);
        dailyBiteCount++;
        biteTrustCooldown = 8000;

        // 排程動畫結束
        scheduleAnimReset(40);
    }

    private void scheduleAnimReset(int ticks) {
        // 簡化：直接在下一次 tick 重置（完整實作應用 ScheduledExecutorService 或 ServerLevel 的 tick 排程）
        biteTrustCooldown = Math.max(biteTrustCooldown, ticks);
    }

    // ── 封印物生成 ────────────────────────────────────────────────────────

    @Override
    protected void spawnSealOrb() {
        SealedEntity seal = ModEntities.SEALED_ENTITY.get().create(level());
        if (seal != null) {
            seal.setPos(position());
            seal.setOriginalEntity("coco");
            level().addFreshEntity(seal);
        }
    }

    // ── 能力橋接 ─────────────────────────────────────────────────────────

    @Override
    protected void addEmotion(BondData bond, float amount) {
        bond.modifyCocoEmotion(amount);
    }

    @Override
    protected void playPetSound() {
        level().playSound(null, blockPosition(), ModSounds.COCO_MEOW_SHORT.get(),
                SoundSource.NEUTRAL, 0.6f, 1.0f + random.nextFloat() * 0.2f);
    }

    @Override
    protected float getEmotionValue(BondData bond) {
        return bond.getCocoEmotion();
    }

    // ── 對話觸發（字幕系統）────────────────────────────────────────────

    /**
     * 對話文本節點（用於字幕顯示）：
     * <ul>
     *   <li>第一次曬太陽：translation key {@code cocojenna.coco.dialogue.first_sunbath}</li>
     *   <li>依戀值 > 80：{@code cocojenna.coco.dialogue.attachment_high}</li>
     *   <li>玩家離開太久後回來：{@code cocojenna.coco.dialogue.return_after_away}</li>
     * </ul>
     */
    public String getDialogueKey(DialogueTrigger trigger) {
        return switch (trigger) {
            case FIRST_SUNBATH -> "cocojenna.coco.dialogue.first_sunbath";
            case ATTACHMENT_HIGH -> "cocojenna.coco.dialogue.attachment_high";
            case RETURN_AFTER_AWAY -> "cocojenna.coco.dialogue.return_after_away";
        };
    }

    public enum DialogueTrigger {
        FIRST_SUNBATH, ATTACHMENT_HIGH, RETURN_AFTER_AWAY
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public float getProtectiveness() { return entityData.get(DATA_PROTECTIVENESS); }
    public float getMoonAffinity()   { return entityData.get(DATA_MOON_AFFINITY); }
    public int   getSpecialAnim()    { return entityData.get(DATA_SPECIAL_ANIMATION); }
    public boolean isPerformingSpecial() { return entityData.get(DATA_PERFORMING_SPECIAL); }
}
