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

/** 混沌試煉氣球 — 飄浮移動. */
public class TrialBalloonEntity extends Mob {

    @Nullable
    private UUID trialOwner;

    public TrialBalloonEntity(EntityType<? extends TrialBalloonEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.12);
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
        setDeltaMovement((random.nextDouble() - 0.5) * 0.04, 0.02, (random.nextDouble() - 0.5) * 0.04);
        move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof ServerPlayer sp && trialOwner != null
                && !trialOwner.equals(sp.getUUID())) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer sp) {
            MoonCrossroadsManager.onTrialTargetDefeated(sp, this);
        }
    }
}
