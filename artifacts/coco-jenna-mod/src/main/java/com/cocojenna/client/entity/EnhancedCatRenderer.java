package com.cocojenna.client.entity;

import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;

/**
 * 貓咪渲染 — 原版 CatModel + 64×32 貓 UV；僅在腐蝕／泥染時加半透明覆蓋（避免頭部重影）.
 */
public class EnhancedCatRenderer<T extends Mob> extends MobRenderer<T, OcelotModel<T>> {

    public record CatStyle(
            ResourceLocation texture,
            float scale,
            float shadowMul,
            float bodyAlpha,
            boolean hologram,
            float highlightR, float highlightG, float highlightB,
            boolean mudVeins
    ) {
        public CatStyle(ResourceLocation texture, float scale, float shadowMul) {
            this(texture, scale, shadowMul, 1f, false, 1f, 1f, 1f, false);
        }

        public static CatStyle layered(ResourceLocation tex, float scale, float hr, float hg, float hb) {
            return new CatStyle(tex, scale, 1f, 1f, false, hr, hg, hb, false);
        }

        public static CatStyle hologram(ResourceLocation tex, float scale) {
            return new CatStyle(tex, scale, 0.6f, 0.42f, true, 0.55f, 0.75f, 1f, false);
        }

        public static CatStyle mimic(ResourceLocation tex, float scale) {
            return new CatStyle(tex, scale, 1f, 1f, false, 0.9f, 0.85f, 0.8f, true);
        }

        public static CatStyle shadowClaw(ResourceLocation tex, float scale) {
            return new CatStyle(tex, scale, 0.82f, 1f, false, 0.35f, 0.25f, 0.50f, true);
        }

        public static CatStyle boss(float scale) {
            return new CatStyle(
                    new ResourceLocation(com.cocojenna.CocoJennaMod.MOD_ID, "textures/entity/boss/generic.png"),
                    scale, 0.82f, 1f, false, 0.55f, 0.42f, 0.72f, true);
        }
    }

    protected final CatStyle style;

    public EnhancedCatRenderer(EntityRendererProvider.Context ctx, CatStyle style) {
        super(ctx, new OcelotModel<>(ctx.bakeLayer(ModelLayers.CAT)), 0.35f * style.scale * style.shadowMul);
        this.style = style;
        this.shadowRadius = 0.25f * style.scale * style.shadowMul;
    }

    protected CatStyle resolveStyle(T entity) {
        return style;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        CatStyle s = resolveStyle(entity);
        poseStack.scale(s.scale, s.scale, s.scale);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        CatStyle s = resolveStyle(entity);
        if (s.hologram) {
            poseStack.pushPose();
            scale(entity, poseStack, partialTicks);
            float pulse = 0.85f + Mth.sin((entity.tickCount + partialTicks) * 0.15f) * 0.08f;
            ResourceLocation tex = getTextureLocation(entity);
            renderTintedModel(entity, partialTicks, poseStack, buffer, packedLight,
                    0.55f, 0.72f, 0.98f, 0.88f, RenderType.entityCutoutNoCull(tex));
            renderTintedModel(entity, partialTicks, poseStack, buffer, packedLight,
                    0.35f, 0.55f, 0.95f, s.bodyAlpha * pulse, RenderType.entityTranslucent(tex));
            poseStack.popPose();
            if (entity.level().isClientSide && entity.tickCount % 4 == 0) {
                entity.level().addParticle(ModParticles.PURR_WAVE.get(),
                        entity.getX(), entity.getY() + 0.5, entity.getZ(), 0, 0.02, 0);
            }
            renderNameMaybe(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        float corrosion = corrosionStrength(entity);
        if (s.mudVeins || corrosion > 0.12f) {
            poseStack.pushPose();
            scale(entity, poseStack, partialTicks);
            ResourceLocation tex = getTextureLocation(entity);
            if (s.mudVeins) {
                renderTintedModel(entity, partialTicks, poseStack, buffer, packedLight,
                        0.05f, 0.04f, 0.08f, 0.28f, RenderType.entityTranslucent(tex));
            }
            if (corrosion > 0.12f) {
                float a = 0.15f + corrosion * 0.35f;
                renderTintedModel(entity, partialTicks, poseStack, buffer, packedLight,
                        0.04f, 0.03f, 0.08f, a, RenderType.entityTranslucent(tex));
            }
            poseStack.popPose();
        }
    }

    private void setupCatAnim(T entity, float partialTicks) {
        float limbSwing = entity.walkAnimation.position(partialTicks);
        float limbSwingAmount = entity.walkAnimation.speed(partialTicks);
        float age = entity.tickCount + partialTicks;
        float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot)
                - Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headPitch = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch);
    }

    private void renderTintedModel(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, float r, float g, float b, float a, RenderType type) {
        VertexConsumer vc = buffer.getBuffer(type);
        setupCatAnim(entity, partialTicks);
        this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, a);
    }

    private void renderNameMaybe(T entity, float yaw, float partial, PoseStack poseStack,
            MultiBufferSource buffer, int light) {
        if (this.shouldShowName(entity)) {
            this.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, light);
        }
    }

    private static float corrosionStrength(Mob entity) {
        if (entity.hasEffect(ModEffects.CORROSION_MARK.get())) return 0.85f;
        if (entity.hasEffect(MobEffects.CONFUSION)) return 0.6f;
        if (entity.hasEffect(MobEffects.WEAKNESS)) return 0.38f;
        if (entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) return 0.22f;
        return 0f;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return resolveStyle(entity).texture;
    }
}
