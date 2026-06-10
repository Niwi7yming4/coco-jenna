package com.cocojenna.entity;

import com.cocojenna.exploration.ExplorationManager;
import com.cocojenna.exploration.WildCatType;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 怪貓貓 — 15 變體獨特機制（設計書第四章）. */
public class WildCatEntity extends PathfinderMob implements RangedAttackMob {

    private static final EntityDataAccessor<Integer> DATA_TYPE =
            SynchedEntityData.defineId(WildCatEntity.class, EntityDataSerializers.INT);

    private int abilityCooldown;

    public WildCatEntity(EntityType<? extends WildCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_TYPE, 0);
    }

    public WildCatType catType() {
        return WildCatType.fromId(getCatTypeId());
    }

    public int getCatTypeId() {
        return entityData.get(DATA_TYPE);
    }

    public void setCatTypeId(int id) {
        entityData.set(DATA_TYPE, Math.max(0, Math.min(14, id)));
        if (level() != null && !level().isClientSide) refreshGoals();
    }

    private void refreshGoals() {
        goalSelector.removeAllGoals(g -> true);
        targetSelector.removeAllGoals(g -> true);
        registerGoals();
    }

    @Override
    protected void registerGoals() {
        WildCatType type = catType();
        goalSelector.addGoal(0, new FloatGoal(this));
        if (type == WildCatType.VOID_GAZER) {
            goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 16));
            targetSelector.addGoal(1, new HurtByTargetGoal(this));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        } else if (type.hostile()) {
            goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, false));
            targetSelector.addGoal(1, new HurtByTargetGoal(this));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        } else {
            goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0f, 1.0, 1.2));
        }
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.55));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (abilityCooldown > 0) abilityCooldown--;

        if (tickCount % 40 == 0) {
            level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(12), p -> true)
                    .forEach(p -> {
                        if (p instanceof ServerPlayer sp) {
                            ExplorationManager.discoverWildCat(sp, catType());
                        }
                    });
        }
        tickTypeAbility();
    }

    private void tickTypeAbility() {
        switch (catType()) {
            case MOON_TABBY -> {
                if (level().isNight() && level().getMoonBrightness() > 0.85f && abilityCooldown <= 0) {
                    Player near = level().getNearestPlayer(this, 8);
                    if (near != null && (near.getMainHandItem().is(Items.GLOW_INK_SAC)
                            || near.getOffhandItem().is(Items.GLOW_INK_SAC))) {
                        getNavigation().moveTo(near, 0.9);
                        abilityCooldown = 200;
                    }
                }
            }
            case BOX_LURKER -> {
                Player near = level().getNearestPlayer(this, 4);
                if (near != null && abilityCooldown <= 0) {
                    near.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 20, 0, false, true));
                    abilityCooldown = 300;
                }
            }
            case MIRAGE_SHADE -> {
                LivingEntity target = getTarget();
                if (target != null && abilityCooldown <= 0 && distanceTo(target) < 6) {
                    teleportTo(target.getX() + 2, target.getY(), target.getZ() + 2);
                    abilityCooldown = 80;
                }
            }
            case STORM_RIDER -> {
                if (level().isRaining() && abilityCooldown <= 0 && getTarget() != null) {
                    getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.18);
                    abilityCooldown = 40;
                } else if (!level().isRaining()) {
                    getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
                }
            }
            default -> { }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (catType() != WildCatType.VOID_GAZER) return;
        Vec3 dir = target.getEyePosition().subtract(getEyePosition()).normalize();
        SmallFireball bolt = new SmallFireball(level(), this,
                dir.x, dir.y, dir.z);
        bolt.setPos(getX(), getEyeY() - 0.1, getZ());
        level().addFreshEntity(bolt);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && catType() == WildCatType.SONG_CAT) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            return InteractionResult.SUCCESS;
        }
        if (!level().isClientSide && catType() == WildCatType.TIME_SKIP && abilityCooldown <= 0) {
            Vec3 back = player.position().subtract(player.getLookAngle().scale(8));
            player.teleportTo(back.x, back.y, back.z);
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0));
            abilityCooldown = 600;
            return InteractionResult.SUCCESS;
        }
        if (!level().isClientSide && catType() == WildCatType.MOON_TABBY
                && level().isNight() && level().getMoonBrightness() > 0.85f
                && player.getMainHandItem().is(Items.GLOW_INK_SAC)) {
            if (!player.addItem(new ItemStack(ModItems.MOONSTONE.get()))) {
                player.drop(new ItemStack(ModItems.MOONSTONE.get()), false);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (catType() == WildCatType.SONG_CAT || catType() == WildCatType.TIME_SKIP) {
            return false;
        }
        if (catType() == WildCatType.VOID_GAZER && source.getDirectEntity() instanceof Player) {
            amount *= 0.15f;
            if (source.getEntity() instanceof ServerPlayer sp) {
                sp.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("explore.cocojenna.wildcat.void_melee"), true);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("WildCatType", getCatTypeId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("WildCatType")) {
            entityData.set(DATA_TYPE, Math.max(0, Math.min(14, tag.getInt("WildCatType"))));
            refreshGoals();
        }
    }
}
