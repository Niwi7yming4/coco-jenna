package com.cocojenna.client.entity;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.entity.*;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** 黑泥怪物 — 序列、變體配色與專屬材質. */
public final class BlackMudMobStyles {

    public enum Variant {
        HEAT_LEECH("heat_leech", 9),
        FORGOTTEN_WISP("forgotten_wisp", 8),
        WHISPERING_DOLL("whispering_doll", 7),
        MEMORY_MOTH("memory_moth", 6),
        VELVET_MOTH("memory_moth", 6),
        MIMIC_CAT("mimic_cat", 5),
        GLITCH_CAT("glitch_cat", 0),
        ORIGAMI_CROW("origami_crow", 0),
        GENERIC("generic", 9);

        public final String texId;
        public final int sequence;

        Variant(String texId, int sequence) {
            this.texId = texId;
            this.sequence = sequence;
        }

        public ResourceLocation texture() {
            return new ResourceLocation(CocoJennaMod.MOD_ID, "textures/entity/black_mud/" + texId + ".png");
        }
    }

    public record Palette(float outerR, float outerG, float outerB,
                          float innerR, float innerG, float innerB,
                          float accentR, float accentG, float accentB, float alpha) {}

    public record ParticleFx(ParticleOptions primary, ParticleOptions secondary, int interval) {}

    private BlackMudMobStyles() {}

    public static Variant variantOf(Mob mob) {
        if (mob instanceof HeatLeechEntity) return Variant.HEAT_LEECH;
        if (mob instanceof ForgottenWispEntity) return Variant.FORGOTTEN_WISP;
        if (mob instanceof WhisperingDollEntity) return Variant.WHISPERING_DOLL;
        if (mob instanceof MemoryMothEntity) return Variant.MEMORY_MOTH;
        if (mob instanceof VelvetMothEntity) return Variant.VELVET_MOTH;
        if (mob instanceof MimicCatEntity) return Variant.MIMIC_CAT;
        if (mob instanceof GlitchCatEntity) return Variant.GLITCH_CAT;
        if (mob instanceof OrigamiCrowEntity) return Variant.ORIGAMI_CROW;
        if (mob instanceof WanderingSludgeEntity) return Variant.HEAT_LEECH;
        if (mob instanceof MudFarmerEntity) return Variant.WHISPERING_DOLL;
        return Variant.GENERIC;
    }

    public static int sequenceOf(Mob mob) {
        if (mob instanceof BlackMudMob bm) return bm.blackMudSequence();
        return variantOf(mob).sequence;
    }

    public static float scaleFor(int sequence) {
        int seq = Math.max(5, Math.min(9, sequence));
        return 0.52f + (10 - seq) * 0.13f;
    }

    public static ResourceLocation textureFor(Mob mob) {
        return variantOf(mob).texture();
    }

    public static Palette paletteFor(Mob mob) {
        return paletteFor(sequenceOf(mob), variantOf(mob));
    }

    public static Palette paletteFor(int sequence, Variant variant) {
        Palette base = switch (Math.max(5, Math.min(9, sequence))) {
            case 9 -> new Palette(0.10f, 0.10f, 0.16f, 0.22f, 0.24f, 0.38f, 0.55f, 0.62f, 0.85f, 0.95f);
            case 8 -> new Palette(0.20f, 0.20f, 0.24f, 0.38f, 0.38f, 0.46f, 0.72f, 0.75f, 0.82f, 0.82f);
            case 7 -> new Palette(0.14f, 0.10f, 0.08f, 0.28f, 0.20f, 0.14f, 0.65f, 0.45f, 0.30f, 0.92f);
            case 6 -> new Palette(0.12f, 0.06f, 0.16f, 0.26f, 0.12f, 0.32f, 0.48f, 0.35f, 0.72f, 0.90f);
            default -> new Palette(0.06f, 0.08f, 0.06f, 0.16f, 0.20f, 0.14f, 0.35f, 0.42f, 0.38f, 0.94f);
        };
        return switch (variant) {
            case HEAT_LEECH -> new Palette(0.08f, 0.10f, 0.18f, 0.18f, 0.22f, 0.35f, 0.45f, 0.75f, 0.95f, 0.92f);
            case FORGOTTEN_WISP -> new Palette(0.05f, 0.05f, 0.08f, 0.15f, 0.12f, 0.18f, 0.55f, 0.50f, 0.65f, 0.55f);
            case WHISPERING_DOLL -> new Palette(0.18f, 0.14f, 0.12f, 0.32f, 0.26f, 0.22f, 0.45f, 0.35f, 0.30f, 0.94f);
            case MEMORY_MOTH -> new Palette(0.12f, 0.06f, 0.22f, 0.28f, 0.12f, 0.45f, 0.75f, 0.45f, 0.90f, 0.88f);
            case MIMIC_CAT -> new Palette(0.20f, 0.18f, 0.16f, 0.35f, 0.30f, 0.28f, 0.08f, 0.06f, 0.10f, 0.96f);
            default -> base;
        };
    }

    public static ParticleFx particlesFor(Variant variant) {
        return switch (variant) {
            case HEAT_LEECH -> new ParticleFx(ParticleTypes.SNOWFLAKE, ModParticles.SHADOW_FEATHER.get(), 5);
            case FORGOTTEN_WISP -> new ParticleFx(ModParticles.SHADOW_FEATHER.get(), ParticleTypes.SMOKE, 4);
            case WHISPERING_DOLL -> new ParticleFx(ParticleTypes.SQUID_INK, ParticleTypes.SMOKE, 6);
            case MEMORY_MOTH -> new ParticleFx(ModParticles.CHAOS_CONFETTI.get(), ModParticles.SHADOW_FEATHER.get(), 5);
            case MIMIC_CAT -> new ParticleFx(ParticleTypes.SMOKE, ParticleTypes.SQUID_INK, 8);
            case GLITCH_CAT -> new ParticleFx(ParticleTypes.INSTANT_EFFECT, ParticleTypes.ENCHANT, 4);
            case ORIGAMI_CROW -> new ParticleFx(ParticleTypes.CLOUD, ParticleTypes.WHITE_ASH, 6);
            default -> new ParticleFx(ParticleTypes.SMOKE, ParticleTypes.SQUID_INK, 6);
        };
    }
}
