package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/** Resource keys for datapack-defined cat kingdom biomes (12). */
public final class ModBiomes {

    public static final ResourceKey<Biome> VELVET_FOREST = key("velvet_forest");
    public static final ResourceKey<Biome> MOON_ALLEY = key("moon_alley");
    public static final ResourceKey<Biome> FIRST_CRY_PLAINS = key("first_cry_plains");
    public static final ResourceKey<Biome> HOWLING_GORGE = key("howling_gorge");
    public static final ResourceKey<Biome> BLIND_WATER_RIVER = key("blind_water_river");
    public static final ResourceKey<Biome> DAWN_HIGHLANDS = key("dawn_highlands");
    public static final ResourceKey<Biome> FORGOTTEN_WASTES = key("forgotten_wastes");
    public static final ResourceKey<Biome> CARDBOARD_SLUMS = key("cardboard_slums");
    public static final ResourceKey<Biome> MOONLIGHT_BEACH = key("moonlight_beach");
    public static final ResourceKey<Biome> RAINBOW_CANYON = key("rainbow_canyon");
    public static final ResourceKey<Biome> CATNIP_HIGHLANDS = key("catnip_highlands");
    public static final ResourceKey<Biome> STARDUST_DESERT = key("stardust_desert");

    private ModBiomes() {}

    private static ResourceKey<Biome> key(String name) {
        return ResourceKey.create(Registries.BIOME,
                new ResourceLocation(CocoJennaMod.MOD_ID, name));
    }
}
