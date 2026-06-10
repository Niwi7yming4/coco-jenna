package com.cocojenna.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class GriefAmalgamEntity extends BlackMudBossEntity {

    public GriefAmalgamEntity(EntityType<? extends GriefAmalgamEntity> type, Level level) {
        super(type, level, BossKind.GRIEF_AMALGAM);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 180.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0);
    }
}
