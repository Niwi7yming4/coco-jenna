package com.cocojenna.weapon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** 三大源力對武器技能的協同修正（設計書第八卷）. */
public final class SourceForceSynergy {

    private SourceForceSynergy() {}

    public static float powerMult(String force) {
        return switch (normalize(force)) {
            case "shadow" -> 1.12f;
            case "chaos" -> 1.05f;
            default -> 1.0f;
        };
    }

    public static float radiusMult(String force) {
        return switch (normalize(force)) {
            case "resonance" -> 1.15f;
            case "shadow" -> 1.08f;
            case "chaos" -> 0.95f;
            default -> 1.0f;
        };
    }

    public static void onHit(Player player, LivingEntity target, WeaponSkillContext ctx) {
        String force = normalize(ctx.sourceForce());
        switch (force) {
            case "shadow" -> target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
            case "chaos" -> {
                if (player.getRandom().nextFloat() < 0.25f) {
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0));
                }
            }
            case "resonance" -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 60, 0, false, false));
            default -> { }
        }
    }

    public static void onCastComplete(Player player, WeaponSkillContext ctx) {
        String force = normalize(ctx.sourceForce());
        switch (force) {
            case "resonance" -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false));
            case "shadow" -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 0, false, false));
            case "chaos" -> {
                if (player.getRandom().nextBoolean()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false));
                } else {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, false, false));
                }
            }
            default -> { }
        }
    }

    private static String normalize(String force) {
        if (force == null || force.isEmpty()) return "resonance";
        return force.toLowerCase();
    }
}
