package com.cocojenna.client.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.WildCatEntity;
import net.minecraft.resources.ResourceLocation;

/** 怪貓貓渲染. */
public class WildCatRenderer extends EnhancedCatRenderer<WildCatEntity> {

    public WildCatRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context ctx) {
        super(ctx, CatStyle.layered(
                new ResourceLocation(CocoJennaMod.MOD_ID, "textures/entity/coco.png"),
                0.85f, 0.92f, 0.88f, 1.0f));
    }
}
