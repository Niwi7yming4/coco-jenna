package com.cocojenna.kingdom;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

/** 彩虹峽谷元素祝福（設計書 貓之國再深化 §2.1 生態域3）. */
public final class RainbowCanyonManager {

    private RainbowCanyonManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 40 != 0) return;

        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        if (!biome.is(ModBiomes.RAINBOW_CANYON)) return;

        ElementZone zone = zoneAt(player.blockPosition());
        applyZoneBuff(player, zone);
    }

    public static ElementZone zoneAt(BlockPos pos) {
        int band = Math.floorMod((pos.getX() / 8) + (pos.getZ() / 8), 6);
        return ElementZone.values()[band];
    }

    private static void applyZoneBuff(ServerPlayer player, ElementZone zone) {
        switch (zone) {
            case RED -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, 0, false, true, true));
            case ORANGE -> player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 80, 0, false, true, true));
            case YELLOW -> player.addEffect(new MobEffectInstance(MobEffects.JUMP, 80, 0, false, true, true));
            case GREEN -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, true, true));
            case BLUE -> player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 80, 0, false, true, true));
            case PURPLE -> player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 80, 0, false, true, true));
        }
        if (player.tickCount % 200 == 0) {
            player.displayClientMessage(Component.translatable("biome.cocojenna.rainbow_canyon.zone." + zone.name().toLowerCase()), true);
        }
    }

    public static net.minecraft.world.level.block.state.BlockState stripeBlock(int wx, int wz, int surfaceY) {
        ElementZone zone = zoneAt(new BlockPos(wx, surfaceY, wz));
        return switch (zone) {
            case RED -> Blocks.RED_TERRACOTTA.defaultBlockState();
            case ORANGE -> Blocks.ORANGE_TERRACOTTA.defaultBlockState();
            case YELLOW -> Blocks.YELLOW_TERRACOTTA.defaultBlockState();
            case GREEN -> Blocks.GREEN_TERRACOTTA.defaultBlockState();
            case BLUE -> Blocks.BLUE_TERRACOTTA.defaultBlockState();
            case PURPLE -> Blocks.PURPLE_TERRACOTTA.defaultBlockState();
        };
    }

    public enum ElementZone {
        RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE
    }
}
