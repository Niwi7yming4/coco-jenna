package com.cocojenna.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * ShadowClawEntity — 影爪是在貓之國維度觸發的特殊 Boss，
 * 實際使用 {@link GeneralCatEntity} 的多階段邏輯。
 *
 * <p>此類作為獨立的實體類型存在，以便用不同的紋理和體型渲染。
 * 所有戰鬥邏輯繼承自 {@link GeneralCatEntity}。
 */
public class ShadowClawEntity extends GeneralCatEntity {

    public ShadowClawEntity(EntityType<? extends ShadowClawEntity> type, Level level) {
        super(type, level);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 500.0) // 影爪更強
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 20.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, 15.0);
    }
}
