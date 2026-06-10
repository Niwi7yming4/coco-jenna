package com.cocojenna.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class FallenVelvetEntity extends BlackMudBossEntity {

    public FallenVelvetEntity(EntityType<? extends FallenVelvetEntity> type, Level level) {
        super(type, level, BossKind.FALLEN_VELVET);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 260.0)
                .add(Attributes.ATTACK_DAMAGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.LearningAttackGoal(this, 1.1, false));
    }
}
