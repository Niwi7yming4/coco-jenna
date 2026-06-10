package com.cocojenna.overworld;

import com.cocojenna.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 主世界流亡貓 NPC（走私貓、詩人、偵察兵等）. */
public class OverworldCatNpcEntity extends PathfinderMob {

    private static final EntityDataAccessor<Integer> ROLE =
            SynchedEntityData.defineId(OverworldCatNpcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FAVOR =
            SynchedEntityData.defineId(OverworldCatNpcEntity.class, EntityDataSerializers.INT);

    public enum Role {
        SMUGGLER, POET, SCOUT, LOST_KITTEN, VETERAN, ARCHAEOLOGIST, BARKEEP, WHISPERER
    }

    public OverworldCatNpcEntity(EntityType<? extends OverworldCatNpcEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24)
                .add(Attributes.MOVEMENT_SPEED, 0.28);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ROLE, 0);
        entityData.define(FAVOR, 10);
    }

    public void setRole(Role role) {
        entityData.set(ROLE, role.ordinal());
    }

    public Role getRole() {
        return Role.values()[Math.min(entityData.get(ROLE), Role.values().length - 1)];
    }

    public int getNpcFavor() {
        return entityData.get(FAVOR);
    }

    public void addNpcFavor(int delta) {
        entityData.set(FAVOR, Math.max(0, Math.min(100, getNpcFavor() + delta)));
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            OverworldPenetrationManager.onOverworldCatInteract(sp, this, hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return getRole() != Role.LOST_KITTEN && dist > 96;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity.cocojenna.overworld_cat." + getRole().name().toLowerCase());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Role", entityData.get(ROLE));
        tag.putInt("Favor", entityData.get(FAVOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Role")) entityData.set(ROLE, tag.getInt("Role"));
        if (tag.contains("Favor")) entityData.set(FAVOR, tag.getInt("Favor"));
    }
}
