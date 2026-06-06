package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBiomes {

    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, CocoJennaMod.MOD_ID);

    /** 絨毛森林 (Velvet Forest) */
    public static final RegistryObject<Biome> VELVET_FOREST = BIOMES.register(
            "velvet_forest", ModBiomes::makeVelvetForest);

    /** 月色小巷 (Moon Alley) */
    public static final RegistryObject<Biome> MOON_ALLEY = BIOMES.register(
            "moon_alley", ModBiomes::makeMoonAlley);

    /** 初啼平原 (First Cry Plains) */
    public static final RegistryObject<Biome> FIRST_CRY_PLAINS = BIOMES.register(
            "first_cry_plains", ModBiomes::makeFirstCryPlains);

    /** 嚎風峽谷 (Howling Gorge) */
    public static final RegistryObject<Biome> HOWLING_GORGE = BIOMES.register(
            "howling_gorge", ModBiomes::makeHowlingGorge);

    /** 盲水之河 (Blind Water River) */
    public static final RegistryObject<Biome> BLIND_WATER_RIVER = BIOMES.register(
            "blind_water_river", ModBiomes::makeBlindWaterRiver);

    /** 黎明高地 (Dawn Highlands) */
    public static final RegistryObject<Biome> DAWN_HIGHLANDS = BIOMES.register(
            "dawn_highlands", ModBiomes::makeDawnHighlands);

    /** 遺忘高塔周邊 (Forgotten Tower Wastes) */
    public static final RegistryObject<Biome> FORGOTTEN_WASTES = BIOMES.register(
            "forgotten_wastes", ModBiomes::makeForgottenWastes);

    // ── Biome factories ────────────────────────────────────────────────────

    private static Biome makeVelvetForest() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3A7A5A)
                        .waterFogColor(0x1A4A3A)
                        .fogColor(0xC0D8B0)
                        .skyColor(0x77B577)
                        .grassColorOverride(0x8FBC5A)
                        .foliageColorOverride(0x5DA83C)
                        .build())
                .build();
    }

    private static Biome makeMoonAlley() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x1A1A5A)
                        .waterFogColor(0x0A0A2A)
                        .fogColor(0x202050)
                        .skyColor(0x101040)
                        .build())
                .build();
    }

    private static Biome makeFirstCryPlains() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3FA3D0)
                        .waterFogColor(0x1A6A90)
                        .fogColor(0xC6E0F5)
                        .skyColor(0x78A8E0)
                        .build())
                .build();
    }

    private static Biome makeHowlingGorge() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x606060)
                        .waterFogColor(0x303030)
                        .fogColor(0x8A8A9A)
                        .skyColor(0x606090)
                        .build())
                .build();
    }

    private static Biome makeBlindWaterRiver() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x000020)
                        .waterFogColor(0x000010)
                        .fogColor(0x101020)
                        .skyColor(0x050520)
                        .build())
                .build();
    }

    private static Biome makeDawnHighlands() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4FA8E8)
                        .waterFogColor(0x2A78A8)
                        .fogColor(0xFFD080)
                        .skyColor(0xFFA040)
                        .build())
                .build();
    }

    private static Biome makeForgottenWastes() {
        return baseBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x303040)
                        .waterFogColor(0x101020)
                        .fogColor(0x404060)
                        .skyColor(0x202040)
                        .build())
                .build();
    }

    private static Biome.BiomeBuilder baseBuilder() {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7f)
                .downfall(0.5f)
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings.PlainBuilder().build());
    }
}
