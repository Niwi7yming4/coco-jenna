package com.cocojenna.kingdom;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.biome.Biome;

/** 貓薄荷高地放鬆回復（設計書 §2.1 生態域4）. */
public final class CatnipHighlandsManager {

    private CatnipHighlandsManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 40 != 0) return;
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        if (!biome.is(ModBiomes.CATNIP_HIGHLANDS)) return;
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, true, true));
    }
}
