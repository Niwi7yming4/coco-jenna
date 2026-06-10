package com.cocojenna.entity;

import com.cocojenna.sequence.MoonCrossroadsManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/** 暗影試煉幽靈標靶 — 會瞬移. */
public class GhostTargetEntity extends Mob {

    @Nullable
    private UUID trialOwner;
    private int teleportCooldown;

    public GhostTargetEntity(EntityType<? extends GhostTargetEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    public void bindTrialOwner(UUID owner) {
        this.trialOwner = owner;
    }

    @Nullable
    public UUID getTrialOwner() {
        return trialOwner;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (++teleportCooldown >= 30 && trialOwner != null) {
            teleportCooldown = 0;
            double ox = getX() + (random.nextDouble() - 0.5) * 10;
            double oz = getZ() + (random.nextDouble() - 0.5) * 10;
            moveTo(ox, getY(), oz, getYRot(), getXRot());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof ServerPlayer sp && trialOwner != null
                && !trialOwner.equals(sp.getUUID())) {
            return false;
        }
        boolean ok = super.hurt(source, amount);
        if (ok && source.getEntity() instanceof ServerPlayer sp) {
            MoonCrossroadsManager.onTrialTargetHit(sp, this, 1);
        }
        return ok;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer sp) {
            MoonCrossroadsManager.onTrialTargetDefeated(sp, this);
        }
    }
}
