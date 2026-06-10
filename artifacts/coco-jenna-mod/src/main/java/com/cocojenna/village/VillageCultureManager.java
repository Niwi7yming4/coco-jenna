package com.cocojenna.village;

import com.cocojenna.init.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

/** 村莊文化類型（設計書 §6 王宮／村莊文化）. */
public final class VillageCultureManager {

    public enum CultureType {
        PASTORAL("first_cry_plains", "pastoral"),
        COASTAL("moonlight_beach", "coastal"),
        INDUSTRIAL("cardboard_slums", "industrial"),
        SCHOLARLY("dawn_highlands", "scholarly"),
        MYSTIC("moon_alley", "mystic"),
        MILITARY("howling_gorge", "military"),
        TRADE("blind_water_river", "trade"),
        FESTIVE("rainbow_canyon", "festive");

        public final String biomeId;
        public final String key;

        CultureType(String biomeId, String key) {
            this.biomeId = biomeId;
            this.key = key;
        }

        public Component displayName() {
            return Component.translatable("village.cocojenna.culture." + key);
        }
    }

    private VillageCultureManager() {}

    public static CultureType cultureAt(ServerPlayer player) {
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        if (biome.is(ModBiomes.FIRST_CRY_PLAINS) || biome.is(ModBiomes.VELVET_FOREST)) return CultureType.PASTORAL;
        if (biome.is(ModBiomes.MOONLIGHT_BEACH)) return CultureType.COASTAL;
        if (biome.is(ModBiomes.CARDBOARD_SLUMS)) return CultureType.INDUSTRIAL;
        if (biome.is(ModBiomes.DAWN_HIGHLANDS) || biome.is(ModBiomes.CATNIP_HIGHLANDS)) return CultureType.SCHOLARLY;
        if (biome.is(ModBiomes.MOON_ALLEY) || biome.is(ModBiomes.STARDUST_DESERT)) return CultureType.MYSTIC;
        if (biome.is(ModBiomes.HOWLING_GORGE)) return CultureType.MILITARY;
        if (biome.is(ModBiomes.BLIND_WATER_RIVER)) return CultureType.TRADE;
        if (biome.is(ModBiomes.RAINBOW_CANYON)) return CultureType.FESTIVE;
        return CultureType.PASTORAL;
    }

    public static CultureType cultureNear(BlockPos pos, Holder<Biome> biome) {
        if (biome.is(ModBiomes.MOONLIGHT_BEACH)) return CultureType.COASTAL;
        if (biome.is(ModBiomes.CARDBOARD_SLUMS)) return CultureType.INDUSTRIAL;
        if (biome.is(ModBiomes.RAINBOW_CANYON)) return CultureType.FESTIVE;
        return CultureType.PASTORAL;
    }
}
