package com.cocojenna.client.entity;

import com.cocojenna.entity.QinKemuEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** 秦可沐人形態 — GeckoLib 骨骼動畫. */
public class QinKemuGeoRenderer extends GeoEntityRenderer<QinKemuEntity> {

    public QinKemuGeoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new QinKemuGeoModel());
        this.shadowRadius = 0.45f;
    }

    @Override
    public void render(QinKemuEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.05f, 1.05f, 1.05f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
