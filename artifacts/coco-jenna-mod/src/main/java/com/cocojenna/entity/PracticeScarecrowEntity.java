package com.cocojenna.entity;

import com.cocojenna.sequence.MoonCrossroadsManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/** 源力試煉稻草人 — 不反擊. */
public class PracticeScarecrowEntity extends Monster {

    @Nullable
    private UUID trialOwner;

    public PracticeScarecrowEntity(EntityType<? extends PracticeScarecrowEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public void bindTrialOwner(UUID owner) {
        this.trialOwner = owner;
    }

    @Nullable
    public UUID getTrialOwner() {
        return trialOwner;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
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
