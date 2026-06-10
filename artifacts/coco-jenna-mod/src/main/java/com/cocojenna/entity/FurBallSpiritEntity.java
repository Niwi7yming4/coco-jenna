package com.cocojenna.entity;

import com.cocojenna.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 毛球精靈 (Fur Ball Spirit) ☁️
 *
 * <p>終局後黑泥怪物轉化而成的無害小精靈。
 * 行為：捉迷藏、解謎、收集，不攻擊玩家。
 *
 * <p>外觀：白色圓滾滾的毛球，有一對閃閃發光的眼睛，
 * 腳踩隱形（浮空移動）。
 *
 * <p>互動：
 * <ul>
 *   <li>右鍵觸摸 → 發出啾啾聲 + 給予隨機小禮物（稀有花、記憶微粒）</li>
 *   <li>餵食貓薄荷 → 開始旋轉跳舞（15 秒）→ 掉落稀有材料</li>
 *   <li>捉迷藏：隨機消失並在玩家找到時給予獎勵</li>
 * </ul>
 */
public class FurBallSpiritEntity extends PathfinderMob {

    private static final EntityDataAccessor<Boolean> DATA_HAPPY =
            SynchedEntityData.defineId(FurBallSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HIDING =
            SynchedEntityData.defineId(FurBallSpiritEntity.class, EntityDataSerializers.BOOLEAN);

    private int hideTick = 0;
    private int danceTick = 0;

    public FurBallSpiritEntity(EntityType<? extends FurBallSpiritEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_HAPPY, false);
        entityData.define(DATA_HIDING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 4.0f, 1.2, 1.4));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0f));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // 輕微浮動
        double wave = Math.sin(level().getGameTime() * 0.05) * 0.01;
        setDeltaMovement(getDeltaMovement().x, wave, getDeltaMovement().z);

        // 粒子效果
        if (level().isClientSide && level().getGameTime() % 10 == 0) {
            level().addParticle(ParticleTypes.SNOWFLAKE,
                    getX() + (random.nextDouble() - 0.5) * 0.8,
                    getY() + random.nextDouble() * 0.5,
                    getZ() + (random.nextDouble() - 0.5) * 0.8,
                    0, 0.02, 0);
        }

        // 跳舞計時
        if (danceTick > 0) {
            danceTick--;
            setYRot(getYRot() + 10);
            if (danceTick == 0) {
                dropDanceLoot();
            }
        }
    }

    /** 被玩家餵食貓薄荷後跳舞 */
    public void startDance() {
        danceTick = 300; // 15 秒
        level().playSound(null, blockPosition(),
                ModSounds.JENNA_MEOW_EXCITED.get(), SoundSource.NEUTRAL, 1.0f, 1.5f);
        entityData.set(DATA_HAPPY, true);
    }

    private void dropDanceLoot() {
        // 掉落稀有材料
        net.minecraft.world.item.ItemStack drop = switch (random.nextInt(4)) {
            case 0 -> new net.minecraft.world.item.ItemStack(com.cocojenna.init.ModItems.MEMORY_PARTICLE.get(), 3);
            case 1 -> new net.minecraft.world.item.ItemStack(com.cocojenna.init.ModItems.HIBISCUS_FLOWER_ITEM.get());
            case 2 -> new net.minecraft.world.item.ItemStack(com.cocojenna.init.ModItems.SILVERVINE.get());
            default -> new net.minecraft.world.item.ItemStack(com.cocojenna.init.ModItems.MOONSTONE.get());
        };
        spawnAtLocation(drop);
        entityData.set(DATA_HAPPY, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public boolean isInvulnerable() { return true; } // 毛球精靈無敵
    public boolean isHappy() { return entityData.get(DATA_HAPPY); }
    public boolean isHiding() { return entityData.get(DATA_HIDING); }
}
