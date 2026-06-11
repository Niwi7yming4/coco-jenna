package com.cocojenna.client;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 貓之國各生態域天空／霧色 client 覆寫（Wave 2 Track C）. */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class CatKingdomBiomeEffects {

    private CatKingdomBiomeEffects() {}

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        var level = Minecraft.getInstance().level;
        if (level == null || !level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        Holder<Biome> biome = level.getBiome(event.getCamera().getBlockPosition());
        int[] rgb = fogRgb(biome);
        if (rgb == null) return;
        event.setRed(rgb[0] / 255f);
        event.setGreen(rgb[1] / 255f);
        event.setBlue(rgb[2] / 255f);
    }

    private static int[] fogRgb(Holder<Biome> biome) {
        ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
        if (key == null) return null;
        if (key.equals(ModBiomes.VELVET_FOREST)) return rgb(0xFFB7C5);
        if (key.equals(ModBiomes.FIRST_CRY_PLAINS)) return rgb(0xFFD8B0);
        if (key.equals(ModBiomes.MOON_ALLEY)) return rgb(0x8899CC);
        if (key.equals(ModBiomes.MOONLIGHT_BEACH)) return rgb(0xB0D8FF);
        if (key.equals(ModBiomes.FORGOTTEN_WASTES)) return rgb(0x664466);
        if (key.equals(ModBiomes.CARDBOARD_SLUMS)) return rgb(0xAA8866);
        if (key.equals(ModBiomes.STARDUST_DESERT)) return rgb(0x554455);
        if (key.equals(ModBiomes.DAWN_HIGHLANDS)) return rgb(0xAADDAA);
        if (key.equals(ModBiomes.CATNIP_HIGHLANDS)) return rgb(0x88CC88);
        if (key.equals(ModBiomes.RAINBOW_CANYON)) return rgb(0xFFBBDD);
        if (key.equals(ModBiomes.HOWLING_GORGE)) return rgb(0x99AABB);
        if (key.equals(ModBiomes.BLIND_WATER_RIVER)) return rgb(0x88BBDD);
        return null;
    }

    private static int[] rgb(int hex) {
        return new int[]{(hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF};
    }
}
