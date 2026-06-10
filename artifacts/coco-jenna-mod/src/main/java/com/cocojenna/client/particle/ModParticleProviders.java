package com.cocojenna.client.particle;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.init.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModParticleProviders {

    private ModParticleProviders() {}

    @SubscribeEvent
    public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.PURR_WAVE.get(), DriftParticle::gold);
        event.registerSpriteSet(ModParticles.SHADOW_FEATHER.get(), DriftParticle::purple);
        event.registerSpriteSet(ModParticles.CHAOS_CONFETTI.get(), DriftParticle::rainbow);
        event.registerSpriteSet(ModParticles.VELVET_DRIFT.get(), DriftParticle::soft);
        event.registerSpriteSet(ModParticles.MOON_GLEAM.get(), DriftParticle::moon);
        event.registerSpriteSet(ModParticles.STARDUST_SPARK.get(), DriftParticle::gold);
        event.registerSpriteSet(ModParticles.FORGE_EMBER.get(), DriftParticle::ember);
    }

    private static class DriftParticle extends TextureSheetParticle {

        static ParticleProvider<SimpleParticleType> gold(SpriteSet sprites) {
            return (type, level, x, y, z, dx, dy, dz) -> new DriftParticle(level, x, y, z, sprites, 1.0f, 0.85f, 0.4f);
        }

        static ParticleProvider<SimpleParticleType> purple(SpriteSet sprites) {
            return (type, level, x, y, z, dx, dy, dz) -> new DriftParticle(level, x, y, z, sprites, 0.45f, 0.2f, 0.55f);
        }

        static ParticleProvider<SimpleParticleType> rainbow(SpriteSet sprites) {
            return (type, level, x, y, z, dx, dy, dz) -> {
                float h = level.random.nextFloat();
                return new DriftParticle(level, x, y, z, sprites,
                        0.5f + h, 0.4f + level.random.nextFloat() * 0.5f,
                        0.5f + level.random.nextFloat() * 0.4f);
            };
        }

        static ParticleProvider<SimpleParticleType> soft(SpriteSet sprites) {
            return (type, level, x, y, z, dx, dy, dz) -> new DriftParticle(level, x, y, z, sprites, 0.95f, 0.92f, 0.88f);
        }

        static ParticleProvider<SimpleParticleType> moon(SpriteSet sprites) {
            return (type, level, x, y, z, dx, dy, dz) -> new DriftParticle(level, x, y, z, sprites, 0.65f, 0.82f, 1.0f);
        }

        static ParticleProvider<SimpleParticleType> ember(SpriteSet sprites) {
            return (type, level, x, y, z, dx, dy, dz) -> new DriftParticle(level, x, y, z, sprites, 1.0f, 0.55f, 0.22f);
        }

        DriftParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites,
                float r, float g, float b) {
            super(level, x, y, z);
            this.pickSprite(sprites);
            this.rCol = r;
            this.gCol = g;
            this.bCol = b;
            this.alpha = 0.85f;
            this.quadSize = 0.12f + random.nextFloat() * 0.08f;
            this.lifetime = 20 + random.nextInt(16);
            this.xd = (random.nextDouble() - 0.5) * 0.02;
            this.yd = 0.01 + random.nextDouble() * 0.02;
            this.zd = (random.nextDouble() - 0.5) * 0.02;
            this.gravity = 0.002f;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }
    }
}
