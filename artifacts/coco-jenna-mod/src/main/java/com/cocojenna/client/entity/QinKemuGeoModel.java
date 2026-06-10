package com.cocojenna.client.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.QinKemuEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class QinKemuGeoModel extends GeoModel<QinKemuEntity> {

    private static final ResourceLocation MODEL =
            new ResourceLocation(CocoJennaMod.MOD_ID, "geo/qin_kemu.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CocoJennaMod.MOD_ID, "textures/entity/qin_kemu.png");
    private static final ResourceLocation ANIMATION =
            new ResourceLocation(CocoJennaMod.MOD_ID, "animations/qin_kemu.animation.json");

    @Override
    public ResourceLocation getModelResource(QinKemuEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(QinKemuEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(QinKemuEntity animatable) {
        return ANIMATION;
    }
}
