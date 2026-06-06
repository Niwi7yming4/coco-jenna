package com.cocojenna.entity;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.goal.*;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * 珍奶 (Jenna) — 玳瑁貓・活潑的陪伴者
 *
 * <p>毛色：玳瑁色（黑 #2d1c15、橘 #c96823、棕 #8b5a2b）
 * <p>眼睛：亮檸檬黃 (#e6ff00)
 * <p>特徵：左耳尖端三角形缺口
 *
 * <p>核心數值：
 * <ul>
 *   <li>玩心 (Playfulness)：0‑100，初始 85</li>
 *   <li>好奇心 (Curiosity)：0‑100，初始 80</li>
 *   <li>滿足感 (Contentment)：0‑100，初始 60</li>
 * </ul>
 *
 * <p>專屬行為：
 * <ul>
 *   <li>舔腳踝 (Lick Ankle)：情感 ≥ 2 + 靜止 8 秒</li>
 *   <li>肚子枕頭 (Belly Pillow)：玩家躺下 + 情感 ≥ 3</li>
 *   <li>尾巴偷襲 (Tail Ambush)：可可閒置 + Bond > 40 + 玩心 > 60</li>
 *   <li>魚群召喚 (Fish Summon)：覺醒 ≥ 3 + 玩家釣魚</li>
 *   <li>二公主的好奇 (Princess Curiosity)：玩家使用 GUI + 好奇心 > 70</li>
 *   <li>蝴蝶追逐 (Butterfly Chase)：終局後，白天戶外</li>
 *   <li>禮物快遞 (Gift Delivery)：終局後，滿足感 > 70</li>
 *   <li>邀請玩耍 (Invite Play)：終局後，玩家持逗貓棒</li>
 * </ul>
 */
public class JennaEntity extends AbstractCatEntity {

    // ── 同步數據 ──────────────────────────────────────────────────────────

    private static final EntityDataAccessor<Float> DATA_PLAYFULNESS =
            SynchedEntityData.defineId(JennaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_CURIOSITY =
            SynchedEntityData.defineId(JennaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_CONTENTMENT =
            SynchedEntityData.defineId(JennaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SPECIAL_ANIMATION =
            SynchedEntityData.defineId(JennaEntity.class, EntityDataSerializers.INT);

    // 特殊行為動畫 ID
    public static final int ANIM_NONE              = 0;
    public static final int ANIM_LICK_ANKLE        = 1;  // 舔腳踝
    public static final int ANIM_BELLY_PILLOW      = 2;  // 肚子枕頭
    public static final int ANIM_TAIL_AMBUSH       = 3;  // 尾巴偷襲
    public static final int ANIM_FISH_SUMMON       = 4;  // 魚群召喚
    public static final int ANIM_CURIOUS_PEEK      = 5;  // 二公主的好奇
    public static final int ANIM_BUTTERFLY_CHASE   = 6;  // 蝴蝶追逐（終局）
    public static final int ANIM_GIFT_DELIVERY     = 7;  // 禮物快遞（終局）
    public static final int ANIM_INVITE_PLAY       = 8;  // 邀請玩耍（終局）
    public static final int ANIM_SUNBATH_BELLY     = 9;  // 陽光下翻肚（終局）

    // ── 冷卻計時器 ───────────────────────────────────────────────────────

    private int lickAnkleCooldown = 0;    // 每日 5 次
    private int bellyPillowCooldown = 0;
    private int tailAmbushCooldown = 0;   // 3 分鐘冷卻
    private int fishSummonCooldown = 0;
    private int curiousPeekCooldown = 0;  // 10 分鐘冷卻

    // 終局行為冷卻
    private int butterflyChaseCooldown = 0;
    private int giftDeliveryCooldown = 0;
    private int invitePlayCooldown = 0;
    private int sunbathBellyCooldown = 0;
    private int dailyLickCount = 0;

    // 玩家靜止計時器
    private int ownerStillTick = 0;

    public JennaEntity(EntityType<? extends JennaEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PLAYFULNESS, 85f);
        entityData.define(DATA_CURIOSITY, 80f);
        entityData.define(DATA_CONTENTMENT, 60f);
        entityData.define(DATA_SPECIAL_ANIMATION, ANIM_NONE);
    }

    // ── 屬性 ─────────────────────────────────────────────────────────────

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractCatEntity.baseCatAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38)  // 珍奶跑得比較快
                .add(Attributes.ATTACK_DAMAGE, 3.5)
                .add(Attributes.ARMOR, 1.5);
    }

    // ── AI 目標 ──────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new JennaLickAnkleGoal(this));
        goalSelector.addGoal(2, new JennaTailAmbushGoal(this));
        goalSelector.addGoal(3, new JennaFishSummonGoal(this));
        goalSelector.addGoal(4, new JennaButterflyChaseGoal(this));
    }

    // ── Tick 行為系統 ────────────────────────────────────────────────────

    @Override
    protected void tickBehaviors() {
        Player owner = findOwner();
        if (owner == null) return;

        BondData bond = ModCapabilities.getOrDefault(owner);

        // 冷卻計時器遞減
        if (lickAnkleCooldown > 0)  lickAnkleCooldown--;
        if (bellyPillowCooldown > 0) bellyPillowCooldown--;
        if (tailAmbushCooldown > 0) tailAmbushCooldown--;
        if (fishSummonCooldown > 0) fishSummonCooldown--;
        if (curiousPeekCooldown > 0) curiousPeekCooldown--;
        if (butterflyChaseCooldown > 0) butterflyChaseCooldown--;
        if (giftDeliveryCooldown > 0) giftDeliveryCooldown--;
        if (invitePlayCooldown > 0) invitePlayCooldown--;
        if (sunbathBellyCooldown > 0) sunbathBellyCooldown--;

        // 每日重置
        long day = level().getGameTime() / 24000;
        if (day != lastPetTick / 24000) {
            dailyLickCount = 0;
        }

        // ── 主人靜止計時 ─────────────────────────────────────────────────
        if (owner.getDeltaMovement().length() < 0.01) {
            ownerStillTick++;
        } else {
            ownerStillTick = 0;
            if (entityData.get(DATA_SPECIAL_ANIMATION) == ANIM_LICK_ANKLE) {
                entityData.set(DATA_SPECIAL_ANIMATION, ANIM_NONE);
            }
        }

        // ── 舔腳踝（情感 ≥ 2，靜止 8 秒 = 160 tick）────────────────────
        if (ownerStillTick >= 160 && lickAnkleCooldown <= 0 && dailyLickCount < 5
                && bond.getJennaEmotionLevel().ordinal() >= BondData.EmotionLevel.ATTACHED.ordinal()
                && distanceTo(owner) < 2.0) {
            performLickAnkle(owner, bond);
        }

        // ── 肚子枕頭（玩家躺下）────────────────────────────────────────
        if (owner.isSleeping() && bellyPillowCooldown <= 0
                && bond.getJennaEmotionLevel().ordinal() >= BondData.EmotionLevel.BONDED.ordinal()) {
            performBellyPillow(owner);
        }

        // ── 陽光翻肚（終局後）──────────────────────────────────────────
        if (isEndgame() && !level().isNight() && isInSunlight() && sunbathBellyCooldown <= 0) {
            entityData.set(DATA_SPECIAL_ANIMATION, ANIM_SUNBATH_BELLY);
            level().playSound(null, blockPosition(), ModSounds.JENNA_PURR_LIGHT.get(),
                    SoundSource.NEUTRAL, 1.2f, 0.8f);
            sunbathBellyCooldown = 12000;
        }

        // ── 終局禮物快遞 ─────────────────────────────────────────────────
        if (isEndgame() && bond.getJennaContentment() > 70 && giftDeliveryCooldown <= 0
                && ownerStillTick >= 100 && distanceTo(owner) > 3.0) {
            getNavigation().moveTo(owner, 1.3);
            if (distanceTo(owner) < 2.0) {
                performGiftDelivery(owner, bond);
            }
        }

        // ── 終局邀請玩耍（逗貓棒）───────────────────────────────────────
        if (isEndgame() && invitePlayCooldown <= 0) {
            boolean holdingWand = !owner.getMainHandItem().isEmpty()
                    && owner.getMainHandItem().getItem() == net.minecraft.world.item.Items.FEATHER;
            if (holdingWand && bond.getJennaContentment() > 80) {
                entityData.set(DATA_SPECIAL_ANIMATION, ANIM_INVITE_PLAY);
                level().playSound(null, blockPosition(), ModSounds.JENNA_MEOW_EXCITED.get(),
                        SoundSource.NEUTRAL, 1.0f, 1.3f);
                bond.modifyJennaContentment(10f);
                invitePlayCooldown = 18000;
            }
        }
    }

    // ── 行為執行方法 ─────────────────────────────────────────────────────

    public void performLickAnkle(Player owner, BondData bond) {
        entityData.set(DATA_SPECIAL_ANIMATION, ANIM_LICK_ANKLE);
        level().playSound(null, blockPosition(), ModSounds.JENNA_PURR_LIGHT.get(),
                SoundSource.NEUTRAL, 0.7f, 1.3f);
        owner.addEffect(new MobEffectInstance(ModEffects.JENNAS_CARE.get(), 400, 0, false, true));
        bond.modifyJennaEmotion(0.5f);
        dailyLickCount++;
        lickAnkleCooldown = 1200;
    }

    public void performBellyPillow(Player owner) {
        entityData.set(DATA_SPECIAL_ANIMATION, ANIM_BELLY_PILLOW);
        level().playSound(null, blockPosition(), ModSounds.JENNA_PURR_LIGHT.get(),
                SoundSource.NEUTRAL, 1.0f, 0.9f);
        bellyPillowCooldown = 24000;
    }

    public void performTailAmbush(@Nullable CocoEntity coco, BondData bond) {
        if (tailAmbushCooldown > 0 || coco == null) return;
        float playfulness = entityData.get(DATA_PLAYFULNESS);
        if (playfulness <= 60) return;

        entityData.set(DATA_SPECIAL_ANIMATION, ANIM_TAIL_AMBUSH);
        bond.modifySisterBond(1f);
        entityData.set(DATA_PLAYFULNESS, Math.max(0, playfulness - 10));
        tailAmbushCooldown = 3600;

        level().playSound(null, blockPosition(), ModSounds.JENNA_MEOW_EXCITED.get(),
                SoundSource.NEUTRAL, 0.8f, 1.2f);
    }

    public void performGiftDelivery(Player owner, BondData bond) {
        entityData.set(DATA_SPECIAL_ANIMATION, ANIM_GIFT_DELIVERY);
        // 生成隨機禮物（魚、花或奇怪的東西）
        int roll = random.nextInt(3);
        net.minecraft.world.item.ItemStack gift = switch (roll) {
            case 0 -> new net.minecraft.world.item.ItemStack(Items.SALMON);
            case 1 -> new net.minecraft.world.item.ItemStack(ModItems.HIBISCUS_FLOWER_ITEM.get());
            default -> new net.minecraft.world.item.ItemStack(
                    random.nextBoolean() ? Items.CLAY_BALL : Items.STRING);
        };
        owner.addItem(gift);
        bond.modifyJennaContentment(-5f);
        giftDeliveryCooldown = 8000;
        level().playSound(null, blockPosition(), ModSounds.JENNA_MEOW_QUESTION.get(),
                SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    // ── 封印物生成 ────────────────────────────────────────────────────────

    @Override
    protected void spawnSealOrb() {
        SealedEntity seal = ModEntities.SEALED_ENTITY.get().create(level());
        if (seal != null) {
            seal.setPos(position());
            seal.setOriginalEntity("jenna");
            level().addFreshEntity(seal);
        }
    }

    // ── 能力橋接 ─────────────────────────────────────────────────────────

    @Override
    protected void addEmotion(BondData bond, float amount) {
        bond.modifyJennaEmotion(amount);
    }

    @Override
    protected void playPetSound() {
        level().playSound(null, blockPosition(), ModSounds.JENNA_MEOW_QUESTION.get(),
                SoundSource.NEUTRAL, 0.7f, 1.0f + random.nextFloat() * 0.3f);
    }

    @Override
    protected float getEmotionValue(BondData bond) {
        return bond.getJennaEmotion();
    }

    // ── 對話觸發 ─────────────────────────────────────────────────────────

    public String getDialogueKey(DialogueTrigger trigger) {
        return switch (trigger) {
            case FIRST_BUTTERFLY -> "cocojenna.jenna.dialogue.first_butterfly";
            case GIFT_DELIVERED  -> "cocojenna.jenna.dialogue.gift_delivered";
            case CONTENTMENT_MAX -> "cocojenna.jenna.dialogue.contentment_max";
        };
    }

    public enum DialogueTrigger {
        FIRST_BUTTERFLY, GIFT_DELIVERED, CONTENTMENT_MAX
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public float getPlayfulness()  { return entityData.get(DATA_PLAYFULNESS); }
    public float getCuriosity()    { return entityData.get(DATA_CURIOSITY); }
    public float getContentment()  { return entityData.get(DATA_CONTENTMENT); }
    public int   getSpecialAnim()  { return entityData.get(DATA_SPECIAL_ANIMATION); }
}
