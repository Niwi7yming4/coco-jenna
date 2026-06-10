package com.cocojenna.client;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModParticles;
import com.cocojenna.world.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** 區域專屬環境粒子 + 建築裝飾粒子. */
public final class RegionalAmbientParticles {

    private RegionalAmbientParticles() {}

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!mc.level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;

        if (mc.player.tickCount % 4 == 0) {
            tickRegional(mc);
        }
        if (mc.player.tickCount % 5 == 0) {
            tickBuildingDecor(mc);
        }
    }

    private static void tickRegional(Minecraft mc) {
        Vec3 p = mc.player.position();
        RandomSource r = mc.level.random;
        Region region = nearest(p);
        if (region == null) return;

        double x = p.x + (r.nextDouble() - 0.5) * 14;
        double y = p.y + r.nextDouble() * 5;
        double z = p.z + (r.nextDouble() - 0.5) * 14;

        switch (region) {
            case FIRST_CRY -> {
                mc.level.addParticle(ModParticles.VELVET_DRIFT.get(), x, y, z, 0, 0.012, 0);
                if (r.nextInt(5) == 0) {
                    mc.level.addParticle(ModParticles.PURR_WAVE.get(), x, y + 0.5, z, 0, 0.02, 0);
                }
            }
            case PLAZA -> {
                mc.level.addParticle(ModParticles.STARDUST_SPARK.get(), x, y, z, 0, 0.015, 0);
                mc.level.addParticle(ParticleTypes.GLOW, x, y + 0.3, z, 0, 0.02, 0);
            }
            case VELVET_FOREST -> {
                mc.level.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0, 0.01, 0);
                mc.level.addParticle(ModParticles.VELVET_DRIFT.get(), x, y + 1, z, 0, 0.008, 0);
            }
            case MOON_ALLEY -> {
                mc.level.addParticle(ModParticles.MOON_GLEAM.get(), x, y, z, 0, 0.006, 0);
                if (r.nextInt(4) == 0) {
                    mc.level.addParticle(ModParticles.SHADOW_FEATHER.get(), x, y, z, 0, 0.01, 0);
                }
            }
            case GEAR_TOWN -> {
                if (r.nextInt(2) == 0) {
                    mc.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.04, 0);
                }
                mc.level.addParticle(ModParticles.FORGE_EMBER.get(), x, y + 0.5, z, 0, 0.03, 0);
            }
            case SLEEP_SANCTUARY -> {
                mc.level.addParticle(ParticleTypes.WAX_ON, x, y, z, 0, 0.01, 0);
                mc.level.addParticle(ModParticles.PURR_WAVE.get(), x, y, z, 0, 0.015, 0);
            }
            case BLIND_PORT -> {
                mc.level.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0, -0.02, 0);
                mc.level.addParticle(ModParticles.SHADOW_FEATHER.get(), x, y + 0.5, z, 0, -0.01, 0);
            }
            case DAWN_HIGHLANDS -> {
                mc.level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0.01, 0);
                mc.level.addParticle(ModParticles.STARDUST_SPARK.get(), x, y + 1, z, 0, 0.02, 0);
            }
            case HOWLING_GORGE -> {
                if (r.nextInt(3) == 0) {
                    mc.level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y + 2, z, 0, -0.1, 0);
                }
                mc.level.addParticle(ParticleTypes.CLOUD, x, y + 3, z, 0.02, 0, 0.02);
            }
            case LABYRINTH -> {
                mc.level.addParticle(ParticleTypes.REVERSE_PORTAL, x, y, z, 0, 0.01, 0);
                mc.level.addParticle(ModParticles.CHAOS_CONFETTI.get(), x, y + 0.5, z, 0, 0.02, 0);
            }
            case FORGOTTEN_TOWER -> {
                mc.level.addParticle(ParticleTypes.SQUID_INK, x, y, z, 0, -0.02, 0);
                mc.level.addParticle(ModParticles.SHADOW_FEATHER.get(), x, y + 0.8, z, 0, 0.01, 0);
            }
        }
    }

    private static void tickBuildingDecor(Minecraft mc) {
        BlockPos center = mc.player.blockPosition();
        RandomSource r = mc.level.random;
        for (int i = 0; i < 5; i++) {
            BlockPos pos = center.offset(r.nextInt(15) - 7, r.nextInt(6) - 2, r.nextInt(15) - 7);
            BlockState state = mc.level.getBlockState(pos);
            Block block = state.getBlock();
            double x = pos.getX() + 0.5 + (r.nextDouble() - 0.5) * 0.4;
            double y = pos.getY() + 0.85;
            double z = pos.getZ() + 0.5 + (r.nextDouble() - 0.5) * 0.4;

            if (block == ModBlocks.YARN_BALL_LAMP.get()) {
                mc.level.addParticle(ModParticles.PURR_WAVE.get(), x, y, z, 0, 0.02, 0);
                mc.level.addParticle(ModParticles.FORGE_EMBER.get(), x, y - 0.2, z, 0, 0.01, 0);
            } else if (block == ModBlocks.MOONSTONE_LAMP_POST.get()) {
                mc.level.addParticle(ModParticles.MOON_GLEAM.get(), x, y + 0.3, z, 0, 0.01, 0);
            } else if (block == ModBlocks.IRONPAW_FORGE.get()) {
                mc.level.addParticle(ModParticles.FORGE_EMBER.get(), x, y - 0.2, z, 0, 0.04, 0);
                mc.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.03, 0);
            } else if (block == ModBlocks.CAT_KINGDOM_PORTAL.get()) {
                mc.level.addParticle(ModParticles.SHADOW_FEATHER.get(), x, y, z, 0, 0.03, 0);
                mc.level.addParticle(ParticleTypes.REVERSE_PORTAL, x, y + 0.5, z, 0, 0.05, 0);
            } else if (block == ModBlocks.PURE_LIGHT_TOWER.get() || block == ModBlocks.MEMORY_LIGHTHOUSE.get()) {
                mc.level.addParticle(ModParticles.STARDUST_SPARK.get(), x, y + 0.5, z, 0, 0.02, 0);
            } else if (block == ModBlocks.NEON_MUSHROOM.get()) {
                mc.level.addParticle(ModParticles.CHAOS_CONFETTI.get(), x, y, z, 0, 0.01, 0);
            }
        }
    }

    private enum Region {
        FIRST_CRY, PLAZA, VELVET_FOREST, MOON_ALLEY, GEAR_TOWN,
        SLEEP_SANCTUARY, BLIND_PORT, DAWN_HIGHLANDS, HOWLING_GORGE, LABYRINTH, FORGOTTEN_TOWER
    }

    private static Region nearest(Vec3 p) {
        record Poi(Region r, BlockPos pos, double maxDist) {}
        Poi[] pois = {
                new Poi(Region.FIRST_CRY, FirstCryVillageGenerator.CENTER, 55),
                new Poi(Region.PLAZA, RegionGenerators.CENTRAL_PLAZA, 55),
                new Poi(Region.VELVET_FOREST, VelvetForestPoiGenerator.CENTER, 45),
                new Poi(Region.MOON_ALLEY, MoonAlleyGenerator.CENTER, 45),
                new Poi(Region.GEAR_TOWN, GearTownGenerator.CENTER, 45),
                new Poi(Region.SLEEP_SANCTUARY, RegionGenerators.SLEEP_SANCTUARY, 40),
                new Poi(Region.BLIND_PORT, BlindPortGenerator.CENTER, 45),
                new Poi(Region.DAWN_HIGHLANDS, DawnWeaverGenerator.CENTER, 50),
                new Poi(Region.HOWLING_GORGE, RegionGenerators.HOWLING_GORGE, 40),
                new Poi(Region.LABYRINTH, RegionGenerators.LABYRINTH, 45),
                new Poi(Region.FORGOTTEN_TOWER, ForgottenTowerGenerator.CENTER, 40)
        };
        Region best = null;
        double bestD = Double.MAX_VALUE;
        for (Poi poi : pois) {
            double d = p.distanceToSqr(poi.pos.getX() + 0.5, poi.pos.getY(), poi.pos.getZ() + 0.5);
            if (d < poi.maxDist * poi.maxDist && d < bestD) {
                bestD = d;
                best = poi.r;
            }
        }
        return best;
    }
}
