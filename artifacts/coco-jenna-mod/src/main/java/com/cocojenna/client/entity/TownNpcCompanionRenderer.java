package com.cocojenna.client.entity;

import com.cocojenna.entity.TownNpcCompanionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** 城鎮同伴 — 依 NPC ID 切換外觀. */
public class TownNpcCompanionRenderer extends EnhancedCatRenderer<TownNpcCompanionEntity> {

    public TownNpcCompanionRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, styleFor("sanhua"));
    }

    @Override
    protected CatStyle resolveStyle(TownNpcCompanionEntity entity) {
        return styleFor(entity.getNpcId());
    }

    private static EnhancedCatRenderer.CatStyle styleFor(String npcId) {
        return switch (npcId) {
            case "ironpaw" -> layered("samurai", 1.05f, 0.9f, 0.85f, 0.8f);
            case "sanhua" -> layered("sanhua", 0.96f, 1f, 0.94f, 0.88f);
            case "cheshire" -> layered("cheshire", 1.0f, 0.85f, 0.7f, 0.95f);
            case "white_glove" -> layered("white_glove", 1.06f, 1f, 1f, 1f);
            case "alpha" -> CatStyle.hologram(cat("alpha"), 0.98f);
            case "samurai" -> layered("samurai", 1.0f, 0.85f, 0.7f, 0.65f);
            case "monk" -> layered("monk", 0.96f, 0.95f, 0.92f, 0.85f);
            case "court_lady" -> layered("court_lady", 0.92f, 1f, 0.9f, 0.95f);
            default -> layered("jenna", 0.9f, 0.88f, 0.85f, 0.9f);
        };
    }

    private static EnhancedCatRenderer.CatStyle layered(String tex, float scale, float hr, float hg, float hb) {
        return CatStyle.layered(cat(tex), scale, hr, hg, hb);
    }

    private static net.minecraft.resources.ResourceLocation cat(String tex) {
        return new net.minecraft.resources.ResourceLocation(
                com.cocojenna.CocoJennaMod.MOD_ID, "textures/entity/cat/" + tex + ".png");
    }
}
