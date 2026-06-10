package com.cocojenna.client.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.BlackMudBossEntity;
import com.cocojenna.entity.ThousandFaceStitcherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** 區域首領 — 貓骨架 + 64×32 貓 UV 材質（非 PlayerModel）. */
public class BlackMudBossRenderer<T extends Mob> extends EnhancedCatRenderer<T> {

    private final float modelScale;

    public BlackMudBossRenderer(EntityRendererProvider.Context ctx, float modelScale) {
        super(ctx, EnhancedCatRenderer.CatStyle.boss(modelScale));
        this.modelScale = modelScale;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(modelScale, modelScale, modelScale);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof BlackMudBossEntity boss) {
            return bossTexture(boss.bossKind());
        }
        if (entity instanceof ThousandFaceStitcherEntity) {
            return bossTexture(BlackMudBossEntity.BossKind.THOUSAND_FACE);
        }
        return bossTexture(BlackMudBossEntity.BossKind.FIRST_CRY_WARDEN);
    }

    private static ResourceLocation bossTexture(BlackMudBossEntity.BossKind kind) {
        return new ResourceLocation(CocoJennaMod.MOD_ID, "textures/entity/boss/" + kind.id + ".png");
    }
}
