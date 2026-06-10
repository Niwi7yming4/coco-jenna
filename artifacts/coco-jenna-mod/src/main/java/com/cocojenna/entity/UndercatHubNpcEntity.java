package com.cocojenna.entity;

import com.cocojenna.undercat.UndercatQuestManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** 地下貓域陣營領袖與功能 NPC. */
public class UndercatHubNpcEntity extends PathfinderMob {

    private static final EntityDataAccessor<Integer> ROLE =
            SynchedEntityData.defineId(UndercatHubNpcEntity.class, EntityDataSerializers.INT);

    public enum Role {
        CORRUGATA, ONE_EYE, GREENPAW, SCARFACE, ABBESS, HEAD_SERVANT
    }

    public UndercatHubNpcEntity(EntityType<? extends UndercatHubNpcEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.MOVEMENT_SPEED, 0.28);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ROLE, 0);
    }

    public void setRole(Role role) {
        entityData.set(ROLE, role.ordinal());
    }

    public Role getRole() {
        return Role.values()[Math.min(entityData.get(ROLE), Role.values().length - 1)];
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            UndercatQuestManager.onHubNpcInteract(sp, getRole());
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Role", entityData.get(ROLE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Role")) {
            entityData.set(ROLE, tag.getInt("Role"));
        }
    }
}
