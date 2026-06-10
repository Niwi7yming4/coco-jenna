package com.cocojenna.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class BlindWaterLordEntity extends BlackMudBossEntity {

    public BlindWaterLordEntity(EntityType<? extends BlindWaterLordEntity> type, Level level) {
        super(type, level, BossKind.BLIND_WATER_LORD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }
}
