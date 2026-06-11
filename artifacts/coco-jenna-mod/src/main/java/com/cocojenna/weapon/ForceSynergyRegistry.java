package com.cocojenna.weapon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** 3×3 源力組合協同表 — 至少 9 條有效組合（Wave 3）. */
public final class ForceSynergyRegistry {

    public record Synergy(String primary, String secondary, String tertiary,
                          float powerMult, float radiusMult, SynergyEffect effect) {}

    public enum SynergyEffect {
        GLOW, CONFUSE, ABSORB, REGEN, SPEED, STRENGTH, WEAKNESS, LEVITATE, WITHER
    }

    private ForceSynergyRegistry() {}

    public static Synergy resolve(String a, String b, String c) {
        String[] sorted = new String[] {norm(a), norm(b), norm(c)};
        java.util.Arrays.sort(sorted);
        String key = sorted[0] + "+" + sorted[1] + "+" + sorted[2];
        return switch (key) {
            case "chaos+resonance+shadow" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.15f, 1.1f, SynergyEffect.WITHER);
            case "chaos+resonance+resonance" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.08f, 1.2f, SynergyEffect.REGEN);
            case "chaos+chaos+resonance" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.12f, 0.95f, SynergyEffect.STRENGTH);
            case "chaos+shadow+shadow" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.18f, 1.05f, SynergyEffect.CONFUSE);
            case "resonance+shadow+shadow" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.1f, 1.15f, SynergyEffect.ABSORB);
            case "chaos+chaos+shadow" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.06f, 1.0f, SynergyEffect.SPEED);
            case "chaos+chaos+chaos" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.2f, 0.9f, SynergyEffect.WEAKNESS);
            case "resonance+resonance+shadow" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.05f, 1.25f, SynergyEffect.GLOW);
            case "resonance+resonance+resonance" -> new Synergy(sorted[0], sorted[1], sorted[2], 1.0f, 1.3f, SynergyEffect.LEVITATE);
            default -> new Synergy(sorted[0], sorted[1], sorted[2], 1.0f, 1.0f, SynergyEffect.GLOW);
        };
    }

    public static void applyOnHit(Player player, LivingEntity target, String a, String b, String c) {
        Synergy s = resolve(a, b, c);
        switch (s.effect()) {
            case GLOW -> target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
            case CONFUSE -> target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
            case WITHER -> target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0));
            case LEVITATE -> target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 0));
            default -> { }
        }
    }

    public static void applyOnCast(Player player, String a, String b, String c) {
        Synergy s = resolve(a, b, c);
        switch (s.effect()) {
            case REGEN -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, false));
            case SPEED -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, false, false));
            case STRENGTH -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, false, false));
            case ABSORB -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, 0, false, false));
            case WEAKNESS -> player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
            default -> { }
        }
    }

    private static String norm(String force) {
        if (force == null || force.isEmpty()) return "resonance";
        return force.toLowerCase();
    }
}
