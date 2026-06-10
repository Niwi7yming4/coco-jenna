package com.cocojenna.kingdom;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 星塵沙漠夜景與許願柱（設計書 §2.1 生態域）. */
public final class StardustDesertManager {

    private StardustDesertManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 20 != 0) return;
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        if (!biome.is(ModBiomes.STARDUST_DESERT)) return;
        if (!player.level().isNight()) return;
        if (player.getRandom().nextInt(3) != 0) return;
        player.serverLevel().sendParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1.2, player.getZ(),
                2, 0.4, 0.3, 0.4, 0.01);
    }

    public static boolean tryWish(ServerPlayer player, BlockPos pos, BlockState state) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        if (!state.is(Blocks.END_ROD) && !state.is(Blocks.OBSIDIAN)) return false;
        if (!player.level().getBiome(pos).is(ModBiomes.STARDUST_DESERT)) return false;
        if (!player.level().isNight()) {
            player.displayClientMessage(Component.translatable("biome.cocojenna.stardust_desert.wish_night"), true);
            return true;
        }
        var bond = ModCapabilities.getOrDefault(player);
        long day = bond.getKingdomCalendarDay();
        if (day >= 0 && day == bond.getLastStardustWishDay()) {
            player.displayClientMessage(Component.translatable("biome.cocojenna.stardust_desert.wish_done"), true);
            return true;
        }
        bond.setLastStardustWishDay(day);
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 6000, 0, false, true, true));
        player.displayClientMessage(Component.translatable("biome.cocojenna.stardust_desert.wish_ok"), true);
        return true;
    }
}
