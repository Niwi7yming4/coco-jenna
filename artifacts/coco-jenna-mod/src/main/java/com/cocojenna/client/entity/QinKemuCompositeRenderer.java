package com.cocojenna.client.entity;

import com.cocojenna.entity.QinKemuEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** 貓形／人形態切換：未變身用貓渲染，人形用 GeckoLib. */
public class QinKemuCompositeRenderer extends EntityRenderer<QinKemuEntity> {

    private final QinKemuRenderer catRenderer;
    private final QinKemuGeoRenderer geoRenderer;

    public QinKemuCompositeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.catRenderer = new QinKemuRenderer(ctx);
        this.geoRenderer = new QinKemuGeoRenderer(ctx);
    }

    @Override
    public void render(QinKemuEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isHumanoidForm()) {
            geoRenderer.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        } else {
            catRenderer.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(QinKemuEntity entity) {
        return entity.isHumanoidForm()
                ? geoRenderer.getTextureLocation(entity)
                : catRenderer.getTextureLocation(entity);
    }
}
