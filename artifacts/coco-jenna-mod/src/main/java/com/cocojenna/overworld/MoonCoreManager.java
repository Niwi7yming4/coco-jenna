package com.cocojenna.overworld;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 月光核心儀式：召喚月之事件與祝福（設計書 主世界再多點 §5.3 / §6.2）. */
public final class MoonCoreManager {

    private static final long BLESSING_DURATION = 12000L;

    private MoonCoreManager() {}

    public static boolean tryActivate(ServerPlayer player, boolean atMoonSeal) {
        boolean fullMoon = MoonResonanceManager.isFullMoon(player.serverLevel());
        boolean nearSeal = atMoonSeal || isNearMoonSeal(player);
        if (!fullMoon && !nearSeal) {
            player.displayClientMessage(Component.translatable("penetration.cocojenna.moon_core_need_moon"), true);
            return false;
        }

        var bond = ModCapabilities.getOrDefault(player);
        long until = player.level().getGameTime() + BLESSING_DURATION;
        bond.setMoonCoreBlessingUntil(until);
        bond.addOverworldInfluence(5);
        bond.incrementMoonResonanceCount();

        player.addEffect(new MobEffectInstance(ModEffects.MOON_BLESSING.get(), (int) BLESSING_DURATION, 1, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, (int) BLESSING_DURATION, 0, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, (int) BLESSING_DURATION, 0, false, true, true));

        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                64, 2.5, 1.0, 2.5, 0.03);
        level.playSound(null, pos, ModSounds.WORLD_FULL_MOON_FESTIVAL.get(), SoundSource.PLAYERS, 1.0f, 1.15f);

        if (!player.getAbilities().instabuild) {
            ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get(), 2);
            if (!player.getInventory().add(shard)) {
                player.drop(shard, false);
            }
        }

        if (nearSeal) {
            spawnResonanceSite(player);
        }

        player.displayClientMessage(Component.translatable("penetration.cocojenna.moon_core_activated"), true);
        return true;
    }

    private static boolean isNearMoonSeal(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return false;
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        return data.findRuinNear(player.blockPosition(), 32) == OverworldRuinType.MOON_SEAL;
    }

    private static void spawnResonanceSite(ServerPlayer player) {
        BlockPos site = player.blockPosition().offset(
                player.getRandom().nextInt(40) - 20, 0, player.getRandom().nextInt(40) - 20);
        site = player.serverLevel().getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, site);
        OverworldPenetrationSavedData.get(player.serverLevel()).putResonanceSite(site);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.moon_resonance_hint",
                site.getX(), site.getZ()), true);
    }

    public static boolean hasActiveBlessing(ServerPlayer player) {
        return ModCapabilities.getOrDefault(player).getMoonCoreBlessingUntil()
                > player.level().getGameTime();
    }
}
