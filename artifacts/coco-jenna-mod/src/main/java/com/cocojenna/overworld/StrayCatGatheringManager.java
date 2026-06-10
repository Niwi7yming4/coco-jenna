package com.cocojenna.overworld;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEntities;
import com.cocojenna.society.CatSocietyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** 流浪貓集會滿月/夜晚動態事件. */
public final class StrayCatGatheringManager {

    private static final long CHECK_INTERVAL = 3600L;

    private StrayCatGatheringManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        if (player.level().isDay()) return;
        long time = player.level().getGameTime();
        if (time % CHECK_INTERVAL != 0) return;
        if (player.getRandom().nextFloat() > 0.1f) return;

        ServerLevel level = player.serverLevel();
        BlockPos center = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE,
                player.blockPosition().offset(player.getRandom().nextInt(48) - 24, 0,
                        player.getRandom().nextInt(48) - 24));

        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        if (data.hasActiveGathering()) return;
        data.startGathering(center, level.getGameTime());

        int cats = 5 + player.getRandom().nextInt(6);
        for (int i = 0; i < cats; i++) {
            spawnGatheringCat(level, center, player.getRandom().nextInt(360));
        }
        player.displayClientMessage(Component.translatable("penetration.cocojenna.stray_gathering_nearby",
                center.getX(), center.getZ()), true);
    }

    public static void tickGatherings(ServerLevel level) {
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        BlockPos center = data.gatheringCenter();
        if (center == null) return;
        if (level.getGameTime() % 20 != 0) return;

        level.sendParticles(ParticleTypes.ENCHANT,
                center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                3, 2.0, 0.3, 2.0, 0.5);

        if (level.getGameTime() - data.gatheringStartTick() > 7200L) {
            data.endGathering();
        }
    }

    public static void onPlayerObserve(ServerPlayer player) {
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        BlockPos center = data.gatheringCenter();
        if (center == null) return;
        if (player.blockPosition().distSqr(center) > 144) return;
        if (data.isGatheringBlessed(player.getUUID())) return;

        data.markGatheringBlessed(player.getUUID());
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 72000, 0, false, true, true));
        ModCapabilities.getOrDefault(player).addOverworldInfluence(3);
        CatSocietyManager.onRitualObserved(player);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.stray_gathering_blessing"), true);
    }

    private static void spawnGatheringCat(ServerLevel level, BlockPos center, int angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double r = 2.5 + level.random.nextDouble() * 2.0;
        double x = center.getX() + 0.5 + Math.cos(rad) * r;
        double z = center.getZ() + 0.5 + Math.sin(rad) * r;
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) x, (int) z);

        var ow = ModEntities.OVERWORLD_CAT.get().create(level);
        if (ow != null) {
            ow.setRole(OverworldCatNpcEntity.Role.POET);
            ow.setPos(x, y, z);
            level.addFreshEntity(ow);
            return;
        }
        Cat cat = new Cat(net.minecraft.world.entity.EntityType.CAT, level);
        cat.setPos(x, y, z);
        level.addFreshEntity(cat);
    }
}
