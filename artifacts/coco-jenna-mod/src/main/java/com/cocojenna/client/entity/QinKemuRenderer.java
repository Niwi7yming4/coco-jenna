package com.cocojenna.client.entity;

import com.cocojenna.entity.QinKemuEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** 秦可沐 — 人形態時放大並以金紅高光區分. */
public class QinKemuRenderer extends EnhancedCatRenderer<QinKemuEntity> {

    private static final CatStyle CAT = CatStyle.layered(
            new net.minecraft.resources.ResourceLocation(com.cocojenna.CocoJennaMod.MOD_ID, "textures/entity/qin_kemu.png"),
            0.9f, 0.95f, 0.85f, 0.8f);
    private static final CatStyle HUMANOID = CatStyle.layered(
            new net.minecraft.resources.ResourceLocation(com.cocojenna.CocoJennaMod.MOD_ID, "textures/entity/qin_kemu.png"),
            1.15f, 1.0f, 0.75f, 0.55f);

    public QinKemuRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, CAT);
    }

    @Override
    protected CatStyle resolveStyle(QinKemuEntity entity) {
        return entity.isHumanoidForm() ? HUMANOID : CAT;
    }
}
