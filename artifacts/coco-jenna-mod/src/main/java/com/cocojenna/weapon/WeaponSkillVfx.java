package com.cocojenna.weapon;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/** 武器技能專屬粒子與音效. */
public final class WeaponSkillVfx {

    private WeaponSkillVfx() {}

    public static void onCast(Player player, WeaponSkillContext ctx, WeaponSkillDefinition def) {
        if (!(player.level() instanceof ServerLevel sl)) return;
        int tier = def.chargeProfile().chargeTier(ctx.chargeTicks());
        int particleTier = def.particleTierForStage(ctx.stage());
        spawnParticles(sl, player, def.vfxTheme(), particleTier, tier);
        playSound(sl, player, def.soundKey(), tier, ctx.stage());
    }

    public static void onCharging(Player player, WeaponSkillDefinition def, int chargeTicks) {
        if (player.level().isClientSide || chargeTicks % 5 != 0) return;
        if (!(player.level() instanceof ServerLevel sl)) return;
        int tier = def.chargeProfile().chargeTier(chargeTicks);
        ParticleOptions p = chargeParticle(def.vfxTheme());
        sl.sendParticles(p, player.getX(), player.getY() + 1.1, player.getZ(),
                2 + tier, 0.25, 0.15, 0.25, 0.01);
    }

    private static void spawnParticles(ServerLevel level, Player player, String theme, int stageTier, int chargeTier) {
        ParticleOptions main = themeParticle(theme);
        int count = 8 + stageTier * 4 + chargeTier * 6;
        level.sendParticles(main, player.getX(), player.getY() + 1.0, player.getZ(),
                count, 0.45, 0.25, 0.45, 0.02);
        ParticleOptions accent = accentParticle(theme);
        if (accent != main) {
            level.sendParticles(accent, player.getX(), player.getY() + 0.6, player.getZ(),
                    count / 2, 0.35, 0.1, 0.35, 0.01);
        }
    }

    private static ParticleOptions themeParticle(String theme) {
        return switch (theme) {
            case "wave", "fish", "abyss" -> ParticleTypes.SPLASH;
            case "gear", "iron" -> ParticleTypes.ELECTRIC_SPARK;
            case "moon", "star" -> ParticleTypes.END_ROD;
            case "paper", "origami" -> ParticleTypes.WHITE_ASH;
            case "hibiscus", "blood" -> ParticleTypes.CRIMSON_SPORE;
            case "velvet", "heal" -> ParticleTypes.HEART;
            case "shadow", "stealth" -> ParticleTypes.SMOKE;
            case "neon" -> ParticleTypes.GLOW;
            case "stardust" -> ParticleTypes.FIREWORK;
            case "bind", "jelly" -> ParticleTypes.BUBBLE;
            case "cat", "first_cry" -> ParticleTypes.HAPPY_VILLAGER;
            case "gear_heavy" -> ParticleTypes.LAVA;
            default -> ParticleTypes.SWEEP_ATTACK;
        };
    }

    private static ParticleOptions accentParticle(String theme) {
        return switch (theme) {
            case "wave", "fish" -> ParticleTypes.FISHING;
            case "moon" -> ParticleTypes.GLOW;
            case "paper" -> ParticleTypes.ENCHANT;
            case "hibiscus" -> ParticleTypes.DAMAGE_INDICATOR;
            default -> themeParticle(theme);
        };
    }

    private static ParticleOptions chargeParticle(String theme) {
        return switch (theme) {
            case "moon", "star" -> ParticleTypes.ENCHANT;
            case "shadow" -> ParticleTypes.SOUL;
            default -> ParticleTypes.ENCHANT;
        };
    }

    private static void playSound(ServerLevel level, Player player, String soundKey, int chargeTier, int stage) {
        SoundEvent sound = resolveSound(soundKey);
        float pitch = 0.75f + chargeTier * 0.12f + stage * 0.05f;
        level.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS,
                0.7f + chargeTier * 0.1f, pitch);
    }

    private static SoundEvent resolveSound(String key) {
        if (key != null && key.contains(":")) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null) {
                SoundEvent evt = net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(id);
                if (evt != null) return evt;
            }
        }
        return switch (key) {
            case "bell" -> SoundEvents.BELL_BLOCK;
            case "trident" -> SoundEvents.TRIDENT_RIPTIDE_3;
            case "enchant" -> SoundEvents.ENCHANTMENT_TABLE_USE;
            case "anvil" -> SoundEvents.ANVIL_LAND;
            case "warden" -> SoundEvents.WARDEN_SONIC_BOOM;
            case "amethyst" -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case "cat" -> SoundEvents.CAT_PURREOW;
            case "paper" -> SoundEvents.BOOK_PAGE_TURN;
            case "splash" -> SoundEvents.PLAYER_SPLASH;
            default -> SoundEvents.PLAYER_ATTACK_SWEEP;
        };
    }

    public static ResourceLocation soundId(String key) {
        if (key != null && key.contains(":")) {
            return ResourceLocation.tryParse(key);
        }
        return new ResourceLocation(CocoJennaMod.MOD_ID, "skill." + (key == null ? "default" : key));
    }
}
