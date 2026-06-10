package com.cocojenna.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** 區域黑泥首領 — 以 BossKind 區分行為與掉落. */
public class RegionalBossEntity extends BlackMudBossEntity {

    public RegionalBossEntity(EntityType<? extends RegionalBossEntity> type, Level level, BossKind kind) {
        super(type, level, kind);
    }

    public static AttributeSupplier.Builder attributesFor(BossKind kind) {
        var builder = GeneralCatEntity.createAttributes();
        return switch (kind) {
            case FALLEN_GENERAL -> builder
                    .add(Attributes.MAX_HEALTH, 200.0)
                    .add(Attributes.ATTACK_DAMAGE, 13.0);
            case HOWLING_SQUALL -> builder
                    .add(Attributes.MAX_HEALTH, 240.0)
                    .add(Attributes.ATTACK_DAMAGE, 15.0)
                    .add(Attributes.MOVEMENT_SPEED, 0.35);
            case ASHURA_PHANTOM -> builder
                    .add(Attributes.MAX_HEALTH, 280.0)
                    .add(Attributes.ATTACK_DAMAGE, 17.0);
            case GEAR_OVERLORD -> builder
                    .add(Attributes.MAX_HEALTH, 320.0)
                    .add(Attributes.ATTACK_DAMAGE, 18.0)
                    .add(Attributes.ARMOR, 12.0);
            case MOON_ALLEY_WRAITH -> builder
                    .add(Attributes.MAX_HEALTH, 220.0)
                    .add(Attributes.ATTACK_DAMAGE, 14.0);
            case MOON_GUARDIAN -> builder
                    .add(Attributes.MAX_HEALTH, 180.0)
                    .add(Attributes.ATTACK_DAMAGE, 12.0)
                    .add(Attributes.ARMOR, 8.0);
            case PLAZA_SENTINEL -> builder
                    .add(Attributes.MAX_HEALTH, 260.0)
                    .add(Attributes.ATTACK_DAMAGE, 16.0)
                    .add(Attributes.ARMOR, 8.0);
            case FIRST_CRY_WARDEN -> builder
                    .add(Attributes.MAX_HEALTH, 180.0)
                    .add(Attributes.ATTACK_DAMAGE, 11.0);
            default -> builder
                    .add(Attributes.MAX_HEALTH, 200.0)
                    .add(Attributes.ATTACK_DAMAGE, 12.0);
        };
    }
}
