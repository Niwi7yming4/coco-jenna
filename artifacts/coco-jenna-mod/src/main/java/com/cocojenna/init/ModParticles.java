package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CocoJennaMod.MOD_ID);

    /** 呼嚕共鳴 — 暖金聲波 */
    public static final RegistryObject<SimpleParticleType> PURR_WAVE =
            PARTICLE_TYPES.register("purr_wave", () -> new SimpleParticleType(false));

    /** 夜瞳暗影 — 暗紫羽毛 */
    public static final RegistryObject<SimpleParticleType> SHADOW_FEATHER =
            PARTICLE_TYPES.register("shadow_feather", () -> new SimpleParticleType(false));

    /** 混沌惡作劇 — 彩色紙屑 */
    public static final RegistryObject<SimpleParticleType> CHAOS_CONFETTI =
            PARTICLE_TYPES.register("chaos_confetti", () -> new SimpleParticleType(false));

    /** 環境 — 絨毛飄落 */
    public static final RegistryObject<SimpleParticleType> VELVET_DRIFT =
            PARTICLE_TYPES.register("velvet_drift", () -> new SimpleParticleType(false));

    /** 月光石 / 月巷 — 藍白微光 */
    public static final RegistryObject<SimpleParticleType> MOON_GLEAM =
            PARTICLE_TYPES.register("moon_gleam", () -> new SimpleParticleType(false));

    /** 星塵廣場 — 金色星點 */
    public static final RegistryObject<SimpleParticleType> STARDUST_SPARK =
            PARTICLE_TYPES.register("stardust_spark", () -> new SimpleParticleType(false));

    /** 鍛造台 / 壁爐 — 暖色餘燼 */
    public static final RegistryObject<SimpleParticleType> FORGE_EMBER =
            PARTICLE_TYPES.register("forge_ember", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
