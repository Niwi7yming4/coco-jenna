package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ModTags {

    public static final TagKey<Biome> CAT_KINGDOM_BIOMES = TagKey.create(
            Registries.BIOME,
            new ResourceLocation(CocoJennaMod.MOD_ID, "cat_kingdom"));

    private ModTags() {}
}
