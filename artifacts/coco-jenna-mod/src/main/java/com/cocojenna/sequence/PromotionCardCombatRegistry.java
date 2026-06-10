package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/**
 * 晉升卡戰鬥分化 — 每張卡牌依 force/tier/variant 提供獨特命中與傷害修正.
 */
public final class PromotionCardCombatRegistry {

    private PromotionCardCombatRegistry() {}

    public static float damageMultiplier(ServerPlayer player, BondData bond, LivingEntity target) {
        float mult = 1f;
        for (String id : bond.getOwnedPromotionCards()) {
            mult += situationalBonus(id, bond, target);
        }
        return mult;
    }

    public static void onHit(ServerPlayer player, BondData bond, LivingEntity target, float damage) {
        for (String id : bond.getOwnedPromotionCards()) {
            PromotionCardCatalog.get(id).ifPresent(card -> applyHitProc(player, card, target, damage));
        }
    }

    private static float situationalBonus(String cardId, BondData bond, LivingEntity target) {
        var opt = PromotionCardCatalog.get(cardId);
        if (opt.isEmpty()) return 0f;
        PromotionCardCatalog.CardDef card = opt.get();
        if (card.tier() > bond.getFelineTier() + 1) return 0f;

        float bonus = 0f;
        char v = card.variant();
        switch (card.force()) {
            case "resonance" -> {
                if (v == 'a' && target instanceof BlackMudMob) bonus += 0.05f;
                if (v == 'b' && playerAlliedCatsNearby(bond)) bonus += 0.04f;
                if (v == 'c' && target.getHealth() / target.getMaxHealth() > 0.75f) bonus += 0.05f;
            }
            case "shadow" -> {
                if (v == 'a' && !playerCanSeeTarget(target)) bonus += 0.06f;
                if (v == 'b' && target.getHealth() / target.getMaxHealth() < 0.35f) bonus += 0.08f;
                if (v == 'c' && target instanceof BlackMudMob bm && bm.blackMudSequence() <= 7) bonus += 0.05f;
            }
            case "chaos" -> {
                if (v == 'a') bonus += 0.02f;
                if (v == 'b' && target.hasEffect(MobEffects.POISON)) bonus += 0.06f;
                if (v == 'c' && target instanceof Monster) bonus += 0.04f;
            }
            default -> { }
        }
        bonus += (10 - card.tier()) * 0.002f;
        return bonus;
    }

    private static void applyHitProc(ServerPlayer player, PromotionCardCatalog.CardDef card,
            LivingEntity target, float damage) {
        if (card.tier() > player.getRandom().nextInt(12) + bondTier(player) + 3) return;

        char v = card.variant();
        int dur = Math.max(20, (12 - card.tier()) * 8);
        switch (card.force()) {
            case "resonance" -> {
                if (v == 'a' && player.getRandom().nextFloat() < 0.12f) {
                    player.heal(0.5f + damage * 0.05f);
                }
                if (v == 'b' && player.getRandom().nextFloat() < 0.1f) {
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, dur, 0));
                }
                if (v == 'c') {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, dur, 0));
                }
            }
            case "shadow" -> {
                if (v == 'a' && player.getRandom().nextFloat() < 0.08f) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, dur, 0));
                }
                if (v == 'b' && player.getRandom().nextFloat() < 0.1f) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, dur, 0));
                }
                if (v == 'c' && target.getHealth() / target.getMaxHealth() < 0.25f
                        && player.getRandom().nextFloat() < 0.15f) {
                    target.hurt(player.damageSources().playerAttack(player), damage * 0.25f);
                }
            }
            case "chaos" -> {
                if (v == 'a' && player.getRandom().nextFloat() < 0.1f) {
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, dur / 2, 0));
                }
                if (v == 'b') {
                    target.knockback(0.4, player.getX() - target.getX(), player.getZ() - target.getZ());
                }
                if (v == 'c' && player.getRandom().nextFloat() < 0.12f) {
                    target.addEffect(new MobEffectInstance(ModEffects.CORROSION_MARK.get(), dur, 1));
                }
            }
            default -> { }
        }
    }

    private static int bondTier(ServerPlayer player) {
        return com.cocojenna.capability.ModCapabilities.getOrDefault(player).getFelineTier();
    }

    private static boolean playerAlliedCatsNearby(BondData bond) {
        return bond.getCocoEmotion() > 25 || bond.getJennaEmotion() > 25;
    }

    private static boolean playerCanSeeTarget(LivingEntity target) {
        return !target.isInvisible() && !target.hasEffect(MobEffects.INVISIBILITY);
    }
}
