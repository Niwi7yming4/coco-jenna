package com.cocojenna.entity;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.quest.QinKemuQuestManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/** 始皇貓 · 秦可沐 — 貓形互動；人形態由 GeckoLib 渲染. */
public class QinKemuEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private boolean awake;
    private int favor;
    private int taskStage;
    private boolean companionMode;
    private java.util.UUID companionOwner;
    private boolean humanoid;

    public QinKemuEntity(EntityType<? extends QinKemuEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.QinMaidScheduleGoal(this));
        goalSelector.addGoal(2, new PanicGoal(this, 1.2));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0f));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public boolean isAwake() { return awake; }

    public void setAwake(boolean awake) { this.awake = awake; }

    public int getFavor() { return favor; }

    public void setFavor(int favor) { this.favor = Math.max(0, Math.min(100, favor)); }

    public int getTaskStage() { return taskStage; }

    public void setTaskStage(int taskStage) { this.taskStage = taskStage; }

    public void setCompanionMode(boolean on, java.util.UUID owner) {
        this.companionMode = on;
        this.companionOwner = owner;
    }

    public boolean isHumanoidForm() { return humanoid; }

    public void triggerHumanoidTransform() {
        if (humanoid) return;
        humanoid = true;
        if (level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    getX(), getY() + 1, getZ(), 40, 0.4, 0.6, 0.4, 0.02);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Awake", awake);
        tag.putInt("Favor", favor);
        tag.putInt("TaskStage", taskStage);
        tag.putBoolean("Companion", companionMode);
        tag.putBoolean("Humanoid", humanoid);
        if (companionOwner != null) tag.putUUID("CompanionOwner", companionOwner);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        awake = tag.getBoolean("Awake");
        favor = tag.contains("Favor") ? tag.getInt("Favor") : 0;
        taskStage = tag.contains("TaskStage") ? tag.getInt("TaskStage") : 0;
        companionMode = tag.getBoolean("Companion");
        humanoid = tag.getBoolean("Humanoid");
        companionOwner = tag.hasUUID("CompanionOwner") ? tag.getUUID("CompanionOwner") : null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && companionMode && companionOwner != null && tickCount % 20 == 0) {
            Player owner = level().getPlayerByUUID(companionOwner);
            if (owner != null && distanceToSqr(owner) > 144) {
                getNavigation().moveTo(owner, 1.1);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            BondData bond = ModCapabilities.getOrDefault(sp);
            if (!awake) {
                QinKemuQuestManager.onSleepingChamberInteract(sp, bond, this);
            } else {
                if (player.isShiftKeyDown()) {
                    com.cocojenna.quest.qin.QinTriangleDialogueManager.tryManual(sp, this);
                } else if (bond.getQinKemuQuestStage() >= 2) {
                    com.cocojenna.quest.qin.QinKemuPoetryPool.speakRandom(sp, sp.getRandom());
                }
                QinKemuQuestManager.onTalk(sp, bond, this);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if (!humanoid) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("cat_idle"));
            }
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("humanoid_walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("humanoid_idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
