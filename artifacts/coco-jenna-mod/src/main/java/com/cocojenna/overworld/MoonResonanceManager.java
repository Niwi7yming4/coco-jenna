package com.cocojenna.overworld;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** 滿月夜月光共鳴事件（設計書 為什麼要進貓之國 §3.3）. */
public final class MoonResonanceManager {

    private static final long CHECK_INTERVAL = 2400L;

    private MoonResonanceManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        long time = player.level().getGameTime();
        if (time % CHECK_INTERVAL != 0) return;
        if (!isFullMoon(player.serverLevel())) return;
        if (player.getRandom().nextFloat() > 0.12f) return;

        var bond = ModCapabilities.getOrDefault(player);
        if (bond.getMoonResonanceCount() > 0 && player.getRandom().nextFloat() > 0.35f) return;

        BlockPos center = findResonanceSite(player);
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        data.putResonanceSite(center);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.moon_resonance_hint",
                center.getX(), center.getZ()), true);
        bond.incrementMoonResonanceCount();
    }

    public static void tickNearby(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        if (!isFullMoon(player.serverLevel())) return;
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        BlockPos site = data.findResonanceNear(player.blockPosition(), 24);
        if (site == null) return;

        ServerLevel level = player.serverLevel();
        if (level.getGameTime() % 10 == 0) {
            level.sendParticles(ParticleTypes.END_ROD,
                    site.getX() + 0.5, site.getY() + 1.5, site.getZ() + 0.5,
                    6, 1.5, 0.5, 1.5, 0.02);
        }
        if (player.blockPosition().distSqr(site) > 36) return;
        if (level.getGameTime() % 200 != 0) return;

        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 3600, 0, false, true, true));
        ModCapabilities.getOrDefault(player).addOverworldInfluence(2);
        if (!player.getAbilities().instabuild) {
            ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get());
            if (!player.getInventory().add(shard)) {
                player.drop(shard, false);
            }
        }
        player.displayClientMessage(Component.translatable("penetration.cocojenna.moon_resonance_vision"), true);
        data.removeResonanceSite(site);
    }

    private static BlockPos findResonanceSite(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        int dx = player.getRandom().nextInt(80) - 40;
        int dz = player.getRandom().nextInt(80) - 40;
        BlockPos pos = player.blockPosition().offset(dx, 0, dz);
        return level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
    }

    public static boolean isFullMoon(ServerLevel level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime > 12000L && dayTime < 18000L;
    }
}
