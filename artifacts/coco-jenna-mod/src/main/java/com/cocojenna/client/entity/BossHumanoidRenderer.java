package com.cocojenna.client.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.BlackMudBossEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/** 區域 Boss — 人形黑泥，依種類專屬材質與三層著色. */
public class BossHumanoidRenderer<T extends Mob> extends HumanoidMobRenderer<T, PlayerModel<T>> {

    private final float modelScale;

    public BossHumanoidRenderer(EntityRendererProvider.Context ctx, float modelScale) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.55f * modelScale);
        this.modelScale = modelScale;
        this.shadowRadius = 0.45f * modelScale;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(modelScale, modelScale, modelScale);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        BlackMudBossEntity.BossKind kind = bossKind(entity);
        BossPalette pal = BossPalette.forKind(kind);
        ResourceLocation tex = getTextureLocation(entity);
        this.model.prepareMobModel(entity, 0, 0, partialTicks);
        this.model.setupAnim(entity, entity.walkAnimation.position(partialTicks),
                entity.walkAnimation.speed(partialTicks), entity.tickCount + partialTicks,
                entity.getYRot(), entity.getXRot());
        VertexConsumer accent = buffer.getBuffer(RenderType.entityTranslucent(tex));
        this.model.renderToBuffer(poseStack, accent, packedLight, OverlayTexture.NO_OVERLAY,
                pal.ar, pal.ag, pal.ab, 0.28f);

        poseStack.popPose();
        spawnBossParticles(entity, kind, partialTicks);
    }

    private void spawnBossParticles(T entity, BlackMudBossEntity.BossKind kind, float partial) {
        if (!entity.level().isClientSide || entity.tickCount % 3 != 0) return;
        double x = Mth.lerp(partial, entity.xo, entity.getX());
        double y = Mth.lerp(partial, entity.yo, entity.getY()) + entity.getBbHeight() * 0.55;
        double z = Mth.lerp(partial, entity.zo, entity.getZ());
        entity.level().addParticle(ParticleTypes.SMOKE,
                x + (entity.getRandom().nextDouble() - 0.5) * 0.6, y,
                z + (entity.getRandom().nextDouble() - 0.5) * 0.6, 0, -0.02, 0);
        switch (kind) {
            case HOWLING_SQUALL -> entity.level().addParticle(ParticleTypes.ELECTRIC_SPARK, x, y + 0.3, z, 0, 0.05, 0);
            case BLIND_WATER_LORD -> entity.level().addParticle(ParticleTypes.SQUID_INK, x, y - 0.2, z, 0, -0.04, 0);
            case PRIMAL_CHAOS -> entity.level().addParticle(ParticleTypes.LAVA, x, y, z, 0, 0.02, 0);
            case GRIEF_AMALGAM -> entity.level().addParticle(ParticleTypes.SOUL, x, y + 0.2, z, 0, 0.03, 0);
            case FALLEN_VELVET -> entity.level().addParticle(ParticleTypes.ASH, x, y, z, 0, -0.01, 0);
            default -> entity.level().addParticle(ParticleTypes.REVERSE_PORTAL, x, y + 0.2, z, 0, 0.04, 0);
        }
    }

    private static BlackMudBossEntity.BossKind bossKind(Mob entity) {
        if (entity instanceof BlackMudBossEntity boss) return boss.bossKind();
        return BlackMudBossEntity.BossKind.FIRST_CRY_WARDEN;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof BlackMudBossEntity boss) {
            return new ResourceLocation(CocoJennaMod.MOD_ID,
                    "textures/entity/boss/" + boss.bossKind().id + ".png");
        }
        return new ResourceLocation(CocoJennaMod.MOD_ID, "textures/entity/boss/generic.png");
    }

    /** Boss 色調 — 具名靜態內部類，避免匿名類在 JAR 中遺失。 */
    private static final class BossPalette {
        final float ar, ag, ab;

        BossPalette(float ar, float ag, float ab) {
            this.ar = ar;
            this.ag = ag;
            this.ab = ab;
        }

        static BossPalette forKind(BlackMudBossEntity.BossKind kind) {
            return switch (kind) {
                case GRIEF_AMALGAM -> new BossPalette(0.85f, 0.25f, 0.35f);
                case BLIND_WATER_LORD -> new BossPalette(0.35f, 0.55f, 0.85f);
                case FALLEN_VELVET -> new BossPalette(0.75f, 0.72f, 0.70f);
                case PRIMAL_CHAOS -> new BossPalette(0.95f, 0.20f, 0.15f);
                case HOWLING_SQUALL -> new BossPalette(0.55f, 0.70f, 0.95f);
                case ASHURA_PHANTOM -> new BossPalette(0.50f, 0.45f, 0.55f);
                default -> new BossPalette(0.55f, 0.45f, 0.72f);
            };
        }
    }
}
