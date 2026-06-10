package com.cocojenna.entity;

import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.entity.goal.CatSocializeGoal;
import com.cocojenna.society.CatMarriageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/** 雨後王國招募 NPC 的世界實體（MCA 社會伴侶）. */
public class TownNpcCompanionEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> NPC_ID =
            SynchedEntityData.defineId(TownNpcCompanionEntity.class, EntityDataSerializers.STRING);

    @Nullable
    private UUID ownerUuid;

    public TownNpcCompanionEntity(EntityType<? extends TownNpcCompanionEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(NPC_ID, "sanhua");
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new CatSocializeGoal(this));
        goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 10.0f, 3.0f));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.55));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public void setNpcId(String id) {
        entityData.set(NPC_ID, id == null ? "" : id);
    }

    public String getNpcId() {
        return entityData.get(NPC_ID);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        ownerUuid = uuid;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUuid;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("TownNpcId", getNpcId());
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setNpcId(tag.getString("TownNpcId"));
        ownerUuid = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            if (com.cocojenna.world.firstcry.FirstCryVillageNpcHandler.onInteract(sp, getNpcId())) {
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (ownerUuid != null && ownerUuid.equals(player.getUUID())) {
                TownNpcProfile profile = TownNpcProfile.byId(getNpcId());
                if (profile != null) {
                    CatMarriageManager.onCompanionInteract(sp, profile.id());
                }
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    private static class FollowOwnerGoal extends Goal {
        private final TownNpcCompanionEntity cat;
        private final double speed;
        private final float minDist;
        private final float maxDist;
        @Nullable
        private Player owner;

        FollowOwnerGoal(TownNpcCompanionEntity cat, double speed, float minDist, float maxDist) {
            this.cat = cat;
            this.speed = speed;
            this.minDist = minDist;
            this.maxDist = maxDist;
        }

        @Override
        public boolean canUse() {
            if (cat.getOwnerUUID() == null) return false;
            Player p = cat.level().getPlayerByUUID(cat.getOwnerUUID());
            if (p == null || !p.isAlive()) return false;
            owner = p;
            double dist = cat.distanceToSqr(owner);
            return dist > minDist * minDist && dist < maxDist * maxDist;
        }

        @Override
        public void tick() {
            if (owner != null) {
                cat.getNavigation().moveTo(owner, speed);
            }
        }
    }
}
