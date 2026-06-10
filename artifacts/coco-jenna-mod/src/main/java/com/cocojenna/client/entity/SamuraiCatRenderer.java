package com.cocojenna.client.entity;

import com.cocojenna.entity.SamuraiCatEntity;
import com.cocojenna.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** 貓武士 — 原版貓模型 + 口中叼刀. */
public class SamuraiCatRenderer extends EnhancedCatRenderer<SamuraiCatEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack bladeStack = new ItemStack(ModItems.RYOKATANA_MOON_SHADOW.get());

    public SamuraiCatRenderer(EntityRendererProvider.Context ctx, CatStyle style) {
        super(ctx, style);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(SamuraiCatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.pushPose();
        float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot) - entityYaw;
        float headPitch = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.translate(0.0, 0.40 * style.scale(), 0.14 * style.scale());
        poseStack.mulPose(Axis.YP.rotationDegrees(headYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(headPitch * 0.45f - 8f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(82f));
        float bladeScale = 0.42f * style.scale();
        poseStack.scale(bladeScale, bladeScale, bladeScale);
        itemRenderer.renderStatic(bladeStack, ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
    }
}
