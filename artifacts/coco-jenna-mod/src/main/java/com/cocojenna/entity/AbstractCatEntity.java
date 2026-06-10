package com.cocojenna.entity;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModSounds;
import com.cocojenna.item.GroomingBrushItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

/**
 * 可可與珍奶的共同基底實體。
 *
 * <p>實作：
 * <ul>
 *   <li>玩家跟隨 AI</li>
 *   <li>情感系統連接</li>
 *   <li>覺醒等級效果</li>
 *   <li>無死亡 → 封印物機制</li>
 * </ul>
 */
public abstract class AbstractCatEntity extends PathfinderMob {

    // ── 同步數據 ──────────────────────────────────────────────────────────

    protected static final EntityDataAccessor<Integer> DATA_AWAKENING =
            SynchedEntityData.defineId(AbstractCatEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(AbstractCatEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_ENDGAME =
            SynchedEntityData.defineId(AbstractCatEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> DATA_EMOTION =
            SynchedEntityData.defineId(AbstractCatEntity.class, EntityDataSerializers.FLOAT);

    @Nullable
    protected UUID ownerUUID;

    /** 最後一次互動的遊戲刻 */
    protected long lastPetTick = 0L;
    protected long lastGroomTick = 0L;
    /** 當日梳毛次數 */
    protected int dailyGroomCount = 0;
    /** 當日撫摸次數（可可上限 3 次，珍奶 5 次） */
    protected int dailyBiteCount = 0;

    // ── 行為計時器 ────────────────────────────────────────────────────────

    protected int guardCooldown = 0;
    protected int rescueCooldown = 0;
    protected int idleTick = 0;

    protected AbstractCatEntity(EntityType<? extends AbstractCatEntity> type, Level level) {
        super(type, level);
        this.setMaxUpStep(0.6f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_AWAKENING, 0);
        entityData.define(DATA_SITTING, false);
        entityData.define(DATA_ENDGAME, false);
        entityData.define(DATA_EMOTION, 0f);
    }

    // ── 屬性 ─────────────────────────────────────────────────────────────

    public static AttributeSupplier.Builder baseCatAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, 2.0);
    }

    // ── AI 目標 ──────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new Goal() {
            {
                setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                return entityData.get(DATA_SITTING);
            }

            @Override
            public void start() {
                getNavigation().stop();
            }

            @Override
            public boolean canContinueToUse() {
                return canUse();
            }
        });
        goalSelector.addGoal(2, new Goal() {
            @Override
            public boolean canUse() {
                Player owner = findOwner();
                if (owner == null || entityData.get(DATA_SITTING)) return false;
                BondData bond = ModCapabilities.getOrDefault(owner);
                if (!bond.isAllowExplore()) return false;
                boolean coco = AbstractCatEntity.this instanceof CocoEntity;
                double followDist = com.cocojenna.growth.ThreeTrackGrowthManager
                        .followDistanceSq(bond, coco, bond.getFollowDistance());
                return distanceToSqr(owner) > followDist;
            }

            @Override
            public void tick() {
                Player owner = findOwner();
                if (owner != null) {
                    getNavigation().moveTo(owner, 1.1);
                }
            }
        });
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
    }

    // ── 無死亡系統 → 封印物 ───────────────────────────────────────────────

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getHealth() - amount <= 0) {
            // 不播放死亡動畫，轉化為封印物
            spawnSealOrb();
            this.discard();
            return false;
        }
        return super.hurt(source, amount);
    }

    protected abstract void spawnSealOrb();

    // ── 互動 ─────────────────────────────────────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof GroomingBrushItem brush) {
            if (!level().isClientSide) {
                BondData bond = ModCapabilities.getOrDefault(player);
                BondData.EmotionLevel emotion = this instanceof CocoEntity
                        ? bond.getCocoEmotionLevel()
                        : bond.getJennaEmotionLevel();
                if (emotion.ordinal() < BondData.EmotionLevel.BONDED.ordinal()) {
                    player.displayClientMessage(
                            Component.translatable("message.cocojenna.groom_emotion_too_low"), true);
                    return InteractionResult.FAIL;
                }
                if (!canGroomToday()) {
                    player.displayClientMessage(
                            Component.translatable("message.cocojenna.groom_daily_limit"), true);
                    return InteractionResult.FAIL;
                }
                brush.groom(this, player, stack);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND && onPet(player)) {
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    /** 每日梳毛上限 3 次（設計書） */
    public boolean canGroomToday() {
        refreshDailyGroomCount();
        return dailyGroomCount < 3;
    }

    public void recordGroom() {
        refreshDailyGroomCount();
        dailyGroomCount++;
        lastGroomTick = level().getGameTime();
    }

    private void refreshDailyGroomCount() {
        long currentTick = level().getGameTime();
        long daysPassed = (currentTick / 24000) - (lastGroomTick / 24000);
        if (daysPassed >= 1 || lastGroomTick == 0) {
            dailyGroomCount = 0;
        }
    }

    /**
     * 玩家右鍵撫摸。
     *
     * @param player 互動玩家
     * @return 是否成功互動
     */
    public boolean onPet(Player player) {
        if (level().isClientSide) return false;

        BondData bond = ModCapabilities.getOrDefault(player);
        long currentTick = level().getGameTime();

        // 每日首次撫摸 +1（以 24000 tick 為一天）
        long daysPassed = (currentTick / 24000) - (lastPetTick / 24000);
        if (daysPassed >= 1) {
            dailyBiteCount = 0;
            addEmotion(bond, 1.0f);
            lastPetTick = currentTick;
        }

        // 播放撫摸反應音效
        playPetSound();

        // 同步能力
        syncBondToClient(player);
        return true;
    }

    protected abstract void addEmotion(BondData bond, float amount);

    protected abstract void playPetSound();

    protected void syncBondToClient(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModCapabilities.get(player).ifPresent(bond ->
                    entityData.set(DATA_EMOTION, getEmotionValue(bond)));
        }
    }

    protected abstract float getEmotionValue(BondData bond);

    // ── 覺醒等級效果 ──────────────────────────────────────────────────────

    public int getAwakeningPhase() {
        int shards = entityData.get(DATA_AWAKENING);
        if (shards >= 50) return 6;
        if (shards >= 40) return 5;
        if (shards >= 30) return 4;
        if (shards >= 20) return 3;
        if (shards >= 12) return 2;
        if (shards >= 5)  return 1;
        return 0;
    }

    // ── Tick ─────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        idleTick++;
        if (!level().isClientSide) {
            tickBehaviors();
        }
    }

    protected void tickBehaviors() {
        // 子類實作各自的行為 tick
    }

    /** 尋找最近的玩家（作為主人） */
    @Nullable
    public Player getOwner() {
        return findOwner();
    }

    public Player findOwner() {
        if (ownerUUID == null) return null;
        return level().getPlayerByUUID(ownerUUID);
    }

    // ── 視覺工具 ─────────────────────────────────────────────────────────

    /** 檢查是否在陽光下（用於終局行為） */
    public boolean isInSunlight() {
        if (level().isNight()) return false;
        BlockPos pos = blockPosition();
        return level().canSeeSky(pos) && level().getBrightness(net.minecraft.world.level.LightLayer.SKY, pos) >= 13;
    }

    /** 檢查是否靠近水邊（用於珍奶魚群技能） */
    protected boolean isNearWater(double radius) {
        Vec3 pos = position();
        for (int x = (int) -radius; x <= radius; x++) {
            for (int z = (int) -radius; z <= radius; z++) {
                if (level().isWaterAt(BlockPos.containing(pos.x + x, pos.y, pos.z + z))) return true;
            }
        }
        return false;
    }

    // ── NBT ──────────────────────────────────────────────────────────────

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID);
        tag.putLong("LastPetTick", lastPetTick);
        tag.putLong("LastGroomTick", lastGroomTick);
        tag.putInt("DailyBiteCount", dailyBiteCount);
        tag.putInt("DailyGroomCount", dailyGroomCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) ownerUUID = tag.getUUID("OwnerUUID");
        lastPetTick = tag.getLong("LastPetTick");
        lastGroomTick = tag.getLong("LastGroomTick");
        dailyBiteCount = tag.getInt("DailyBiteCount");
        dailyGroomCount = tag.getInt("DailyGroomCount");
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public boolean isSitting() { return entityData.get(DATA_SITTING); }
    public void setSitting(boolean sitting) { entityData.set(DATA_SITTING, sitting); }
    public boolean isEndgame() { return entityData.get(DATA_ENDGAME); }
    public void setEndgame(boolean endgame) { entityData.set(DATA_ENDGAME, endgame); }
    public float getEmotionSync() { return entityData.get(DATA_EMOTION); }
    @Nullable public UUID getOwnerUUID() { return ownerUUID; }
    public void setOwnerUUID(UUID uuid) { ownerUUID = uuid; }
}
