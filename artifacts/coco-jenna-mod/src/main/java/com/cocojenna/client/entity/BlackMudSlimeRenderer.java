package com.cocojenna.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/** 黑泥史萊姆 — 三層著色 + 變體專屬材質與粒子. */
public class BlackMudSlimeRenderer<T extends Mob> extends MobRenderer<T, SlimeModel<T>> {

    private final float extraScale;

    public BlackMudSlimeRenderer(EntityRendererProvider.Context ctx) {
        this(ctx, 1f);
    }

    public BlackMudSlimeRenderer(EntityRendererProvider.Context ctx, float extraScale) {
        super(ctx, new SlimeModel<>(ctx.bakeLayer(ModelLayers.SLIME)), 0.28f * extraScale);
        this.extraScale = extraScale;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        BlackMudMobStyles.Variant variant = BlackMudMobStyles.variantOf(entity);
        int seq = BlackMudMobStyles.sequenceOf(entity);
        float base = BlackMudMobStyles.scaleFor(seq);
        BlackMudMobStyles.Palette pal = BlackMudMobStyles.paletteFor(entity);

        float t = entity.tickCount + partialTicks;
        boolean coreExposed = com.cocojenna.combat.DistillCombatManager.isCoreExposed(entity);
        float pulse = 1.0f + Mth.sin(t * (coreExposed ? 0.55f : 0.22f)) * (coreExposed ? 0.18f : 0.07f);
        float squishY = variant == BlackMudMobStyles.Variant.HEAT_LEECH ? 0.55f / pulse : 1.0f / pulse;
        float squishXZ = variant == BlackMudMobStyles.Variant.HEAT_LEECH ? pulse * 1.15f : pulse;

        poseStack.pushPose();
        poseStack.scale(extraScale, extraScale, extraScale);
        poseStack.translate(0.0, (1.0f - (1.0f / pulse)) * 0.12f * base, 0.0);
        poseStack.scale(base * squishXZ, base * squishY, base * squishXZ);

        this.model.prepareMobModel(entity, 0, 0, partialTicks);
        this.model.setupAnim(entity, 0, 0, t, 0, 0);

        ResourceLocation tex = getTextureLocation(entity);
        renderLayer(poseStack, buffer, packedLight, tex, pal.outerR(), pal.outerG(), pal.outerB(), pal.alpha(),
                RenderType.entityCutoutNoCull(tex), 1.0f);

        if (coreExposed) {
            poseStack.pushPose();
            poseStack.scale(0.55f, 0.55f, 0.55f);
            renderLayer(poseStack, buffer, packedLight, tex,
                    pal.accentR() * 1.2f, pal.accentG() * 1.2f, Math.min(1f, pal.accentB() * 1.2f),
                    0.55f, RenderType.entityTranslucent(tex), 1.0f);
            poseStack.popPose();
        }

        poseStack.popPose();

        if (entity.level().isClientSide) {
            spawnVariantParticles(entity, variant, partialTicks);
        }

        this.shadowRadius = 0.22f * base;
        if (this.shouldShowName(entity)) {
            this.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, packedLight);
        }
    }

    private void renderLayer(PoseStack poseStack, MultiBufferSource buffer, int light,
            ResourceLocation tex, float r, float g, float b, float a,
            RenderType type, float scale) {
        VertexConsumer vc = buffer.getBuffer(type);
        this.model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, r, g, b, a);
    }

    private void spawnVariantParticles(T entity, BlackMudMobStyles.Variant variant, float partial) {
        if (entity.tickCount % BlackMudMobStyles.particlesFor(variant).interval() != 0) return;
        var fx = BlackMudMobStyles.particlesFor(variant);
        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() * 0.55;
        double z = entity.getZ();
        entity.level().addParticle(fx.primary(), x, y, z, 0, 0.02, 0);
        if (entity.tickCount % (fx.interval() * 2) == 0) {
            entity.level().addParticle(fx.secondary(), x, y + 0.1, z, 0, 0.01, 0);
        }
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {}

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return BlackMudMobStyles.textureFor(entity);
    }
}
