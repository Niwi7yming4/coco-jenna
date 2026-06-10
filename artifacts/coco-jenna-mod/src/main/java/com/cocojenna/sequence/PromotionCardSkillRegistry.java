package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

/**
 * 晉升卡技能效果 — 將 81 張卡牌從純數值加成擴展為施法修正與觸發效果.
 */
public final class PromotionCardSkillRegistry {

    public record SkillModifiers(float powerMult, float cooldownMult, float damageBonus) {}

    private PromotionCardSkillRegistry() {}

    public static SkillModifiers modifiersFor(BondData bond, int castVariant) {
        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) {
            force = "resonance";
        }
        float power = 1f;
        float cd = 1f;
        float dmg = 0f;
        for (String id : bond.getOwnedPromotionCards()) {
            var opt = PromotionCardCatalog.get(id);
            if (opt.isEmpty()) continue;
            PromotionCardCatalog.CardDef card = opt.get();
            if (!card.force().equals(force)) continue;

            int cardVar = card.variant() - 'a';
            boolean slotMatch = cardVar == castVariant || (castVariant == 3 && cardVar == 2);
            if (slotMatch) {
                power += card.bonus();
                dmg += card.bonus() * 0.5f;
            }
            cd -= card.bonus() * 0.25f;
            int potency = 10 - card.tier();
            if (potency > 0) {
                power += potency * 0.005f;
            }
        }
        return new SkillModifiers(power, Math.max(0.45f, cd), dmg);
    }

    public static void onCast(ServerPlayer player, BondData bond, String force,
            int tier, int variant, boolean outer) {
        for (String id : bond.getOwnedPromotionCards()) {
            var opt = PromotionCardCatalog.get(id);
            if (opt.isEmpty()) continue;
            PromotionCardCatalog.CardDef card = opt.get();
            if (!card.force().equals(force)) continue;
            applyCardIdCast(player, player.serverLevel(), card, tier, variant, outer, outer ? 1.4f : 1f);
            applyCardCast(player, card, tier, variant, outer);
        }
    }

    /** 依卡牌 ID 追加分化效果（同 force 內 a/b/c 不同機制）. */
    private static void applyCardIdCast(ServerPlayer player, ServerLevel level,
            PromotionCardCatalog.CardDef card, int tier, int variant, boolean outer, float scale) {
        if (card.variant() - 'a' != variant && !(variant == 3 && card.variant() - 'a' == 2)) return;
        int pot = Math.max(1, 10 - card.tier());
        switch (card.id()) {
            case "resonance_t9_a" -> player.heal(0.6f * pot);
            case "shadow_t5_b" -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 1));
            case "chaos_t7_c" -> knockbackEnemies(player, 5.0 + pot * 0.2);
            case "resonance_t3_a" -> healCats(player, 8.0, 3f * scale);
            case "shadow_t2_c" -> weakenEnemies(player, 10.0, 80);
            default -> { }
        }
    }

    private static void applyCardCast(ServerPlayer player, PromotionCardCatalog.CardDef card,
            int tier, int variant, boolean outer) {
        int cv = card.variant() - 'a';
        boolean primary = cv == variant;
        boolean tertiary = variant == 3 && cv == 2;
        if (!primary && !tertiary && card.tier() > tier + 1) return;

        int pot = Math.max(1, 10 - card.tier());
        float scale = outer ? 1.4f : 1f;
        ServerLevel level = player.serverLevel();

        switch (card.force()) {
            case "resonance" -> applyResonance(player, level, cv, pot, scale, primary);
            case "shadow" -> applyShadow(player, level, cv, pot, scale, primary);
            case "chaos" -> applyChaos(player, level, cv, pot, scale, primary);
            default -> {}
        }
    }

    private static void applyResonance(ServerPlayer player, ServerLevel level,
            int cv, int pot, float scale, boolean primary) {
        switch (cv) {
            case 0 -> {
                player.heal(0.4f * pot * scale);
                if (primary) {
                    healCats(player, 2.5, 1.5f * pot * scale);
                }
            }
            case 1 -> player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                    (int) (60 * pot * scale), primary ? 1 : 0));
            case 2 -> {
                AABB box = player.getBoundingBox().inflate(5.0);
                for (Mob mob : level.getEntitiesOfClass(Mob.class, box,
                        m -> m.isAlive() && !m.isAlliedTo(player))) {
                    mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                            30 * pot, primary ? 1 : 0));
                }
                if (primary) {
                    player.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 80, 0));
                }
            }
            default -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                    (int) (40 * pot * scale), 0));
        }
        if (primary && pot >= 5) {
            spawnRing(level, player, ParticleTypes.HEART, 3.5);
        }
    }

    private static void applyShadow(ServerPlayer player, ServerLevel level,
            int cv, int pot, float scale, boolean primary) {
        switch (cv) {
            case 0 -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                    (int) (80 * pot * scale / 3), primary ? 1 : 0));
            case 1 -> {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,
                        (int) (60 * pot * scale / 2), 0));
                if (primary) {
                    blindEnemies(player, 6.0, 40);
                }
            }
            case 2 -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                    (int) (80 * pot * scale / 3), primary ? 1 : 0));
            default -> {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0));
                if (primary && pot >= 4) {
                    weakenEnemies(player, 8.0, 60);
                }
            }
        }
        if (primary) {
            spawnRing(level, player, ParticleTypes.SMOKE, 2.5);
        }
    }

    private static void applyChaos(ServerPlayer player, ServerLevel level,
            int cv, int pot, float scale, boolean primary) {
        switch (cv) {
            case 0 -> {
                if (player.getRandom().nextFloat() < 0.5f) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1));
                } else {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 120, 1));
                }
            }
            case 1 -> knockbackEnemies(player, 4.0 + pot * 0.3);
            case 2 -> {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
                slowEnemies(player, 5.0, 6);
            }
            default -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                    (int) (60 * pot * scale / 2), 0));
        }
        if (primary) {
            spawnRing(level, player, ParticleTypes.WITCH, 3.0 + pot * 0.2);
        }
    }

    private static void healCats(ServerPlayer player, double radius, float amount) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (AbstractCatEntity cat : player.level().getEntitiesOfClass(AbstractCatEntity.class, box,
                c -> c.isAlive() && player.getUUID().equals(c.getOwnerUUID()))) {
            cat.heal(amount);
        }
    }

    private static void blindEnemies(ServerPlayer player, double radius, int ticks) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box,
                t -> t.isAlive() && !t.isAlliedTo(player) && !(t instanceof ServerPlayer))) {
            e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ticks, 0));
        }
    }

    private static void weakenEnemies(ServerPlayer player, double radius, int ticks) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, box,
                m -> m.isAlive() && !m.isAlliedTo(player))) {
            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, 0));
        }
    }

    private static void slowEnemies(ServerPlayer player, double radius, int seconds) {
        AABB box = player.getBoundingBox().inflate(radius);
        int dur = seconds * 20;
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, box,
                m -> m.isAlive() && !m.isAlliedTo(player))) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, dur, 1));
        }
    }

    private static void knockbackEnemies(ServerPlayer player, double radius) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, box,
                m -> m.isAlive() && !m.isAlliedTo(player))) {
            mob.knockback(1.0, player.getX() - mob.getX(), player.getZ() - mob.getZ());
        }
    }

    private static void spawnRing(ServerLevel level, ServerPlayer player,
            net.minecraft.core.particles.ParticleOptions particle, double radius) {
        for (int i = 0; i < 16; i++) {
            double angle = i * Math.PI * 2 / 16;
            level.sendParticles(particle,
                    player.getX() + Math.cos(angle) * radius,
                    player.getY() + 0.3,
                    player.getZ() + Math.sin(angle) * radius,
                    1, 0, 0.05, 0, 0.01);
        }
    }
}
