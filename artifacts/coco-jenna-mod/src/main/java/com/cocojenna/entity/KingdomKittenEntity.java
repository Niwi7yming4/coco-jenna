package com.cocojenna.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/** 王國幼貓 — 弱化版夥伴，不參戰 */
public class KingdomKittenEntity extends PathfinderMob {

    @Nullable
    private UUID ownerUuid;
    private long pullCooldownUntil;

    public KingdomKittenEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUuid = uuid;
        setCustomName(Component.literal("§e王國幼貓"));
        setCustomNameVisible(true);
    }

    @Nullable
    public UUID getOwnerUUID() { return ownerUuid; }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || ownerUuid == null) return;
        Player owner = level().getPlayerByUUID(ownerUuid);
        if (owner == null) return;
        if (owner.fallDistance > 4 && owner.getHealth() / owner.getMaxHealth() < 0.2f
                && level().getGameTime() > pullCooldownUntil) {
            owner.teleportTo(getX() + 1, getY(), getZ());
            pullCooldownUntil = level().getGameTime() + 36000L;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUuid != null) tag.putUUID("owner", ownerUuid);
        tag.putLong("pullCd", pullCooldownUntil);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("owner")) ownerUuid = tag.getUUID("owner");
        pullCooldownUntil = tag.getLong("pullCd");
    }

    @Override
    protected void registerGoals() {
        // passive kitten
    }
}
