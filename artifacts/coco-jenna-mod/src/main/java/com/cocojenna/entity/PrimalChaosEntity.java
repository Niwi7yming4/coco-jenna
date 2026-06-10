package com.cocojenna.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class PrimalChaosEntity extends BlackMudBossEntity {

    public PrimalChaosEntity(EntityType<? extends PrimalChaosEntity> type, Level level) {
        super(type, level, BossKind.PRIMAL_CHAOS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.ARMOR, 18.0);
    }
}
