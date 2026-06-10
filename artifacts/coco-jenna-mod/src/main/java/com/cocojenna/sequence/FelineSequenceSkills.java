package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.combat.CombatVfxHelper;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

/**
 * 3×9 貓之序列 — 實戰技能（呼嚕／夜瞳／液態 × 序列 9–7）.
 */
public final class FelineSequenceSkills {

    private FelineSequenceSkills() {}

    public static int wheelSlotCount(int tier) {
        if (tier <= 2) return 16;
        if (tier <= 6) return 8;
        return 4;
    }

    public static boolean cast(ServerPlayer player, BondData bond) {
        return castSlot(player, bond, 0);
    }

    public static boolean castSlot(ServerPlayer player, BondData bond, int slot) {
        long now = player.level().getGameTime();
        if (now < bond.getFelineSkillCooldownUntil()) {
            return false;
        }

        String force = bond.getFelineForce();
        int tier = bond.getFelineTier();
        int variant = slot % 4;
        boolean outer = slot >= 4;
        PromotionCardSkillRegistry.SkillModifiers mods =
                PromotionCardSkillRegistry.modifiersFor(bond, variant);
        float power = (outer ? 1.5f : 1.0f) * mods.powerMult()
                * HiddenSequenceBonuses.skillPowerMultiplier(bond);
        int cooldown = (int) (cooldownFor(tier, outer) * mods.cooldownMult()
                * HiddenSequenceBonuses.cooldownMultiplier(bond));
        boolean ok = switch (force) {
            case "shadow" -> castShadow(player, tier, variant, outer, power);
            case "chaos" -> castChaos(player, tier, variant, outer, power);
            default -> castResonance(player, tier, variant, outer, power);
        };
        if (ok) {
            CombatVfxHelper.skillCast(player.serverLevel(), player, CombatVfxHelper.of(force), variant, outer);
            PromotionCardSkillRegistry.onCast(player, bond, force, tier, variant, outer);
            bond.setFelineSkillCooldownUntil(now + cooldown);
        }
        return ok;
    }

    private static boolean castResonance(ServerPlayer player, int tier, int variant, boolean outer, float power) {
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.blockPosition(), ModSounds.COCO_PURR_DEEP.get(),
                SoundSource.PLAYERS, 1.0f, 0.8f);

        return switch (variant) {
            case 0 -> {
                healAllies(player, 3.0 * power, 2.0f * power);
                spawnZone(level, player, ParticleTypes.HEART, 3.0);
                yield true;
            }
            case 1 -> {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, (int) (200 * power), outer ? 1 : 0));
                spawnZone(level, player, ParticleTypes.END_ROD, 4.0);
                yield true;
            }
            case 2 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, (int) (300 * power), outer ? 1 : 0));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, (int) (300 * power), 0));
                spawnZone(level, player, ParticleTypes.FLAME, 5.0);
                yield true;
            }
            default -> {
                slowEnemies(player, 6.0 * power, (int) (8 * power));
                yield tier <= 8;
            }
        };
    }

    private static boolean castShadow(ServerPlayer player, int tier, int variant, boolean outer, float power) {
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.blockPosition(), ModSounds.ENTITY_SEAL_FORM.get(),
                SoundSource.PLAYERS, 0.6f, 1.4f);

        return switch (variant) {
            case 0 -> {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                        (int) ((outer ? 240 : 160) * power), outer ? 2 : 1));
                spawnZone(level, player, ParticleTypes.SMOKE, 2.0 * power);
                yield true;
            }
            case 1 -> {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,
                        (int) ((outer ? 280 : 200) * power), 0));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, (int) (200 * power), outer ? 1 : 0));
                yield true;
            }
            case 2 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                        (int) ((outer ? 160 : 100) * power), outer ? 2 : 1));
                yield true;
            }
            default -> {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0));
                yield tier <= 6;
            }
        };
    }

    private static boolean castChaos(ServerPlayer player, int tier, int variant, boolean outer, float power) {
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.blockPosition(), ModSounds.JENNA_MEOW_EXCITED.get(),
                SoundSource.PLAYERS, 1.0f, 1.3f);

        return switch (variant) {
            case 0 -> {
                if (player.getRandom().nextFloat() < 0.5f) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, outer ? 3 : 2));
                } else {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 160, outer ? 2 : 1));
                }
                spawnZone(level, player, ParticleTypes.WITCH, 3.0);
                yield true;
            }
            case 1 -> {
                knockbackEnemies(player, outer ? 7.0 : 5.0);
                yield true;
            }
            case 2 -> {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, outer ? 2 : 1));
                slowEnemies(player, outer ? 8.0 : 6.0, 8);
                yield true;
            }
            default -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, outer ? 120 : 80, outer ? 1 : 0));
                yield tier <= 6;
            }
        };
    }

    private static void healAllies(ServerPlayer player, double radius, float amount) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (LivingEntity ally : player.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && (e == player || e.isAlliedTo(player)))) {
            float heal = ally instanceof net.minecraft.world.entity.player.Player ? amount * 0.65f : amount;
            ally.heal(heal);
        }
        for (AbstractCatEntity cat : player.level().getEntitiesOfClass(AbstractCatEntity.class, box,
                c -> c.isAlive() && player.getUUID().equals(c.getOwnerUUID()))) {
            cat.heal(amount * 1.25f);
        }
    }

    private static void slowEnemies(ServerPlayer player, double radius, int seconds) {
        AABB box = player.getBoundingBox().inflate(radius);
        int dur = seconds * 20;
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, box,
                m -> m.isAlive() && !m.isAlliedTo(player))) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, dur, 1));
        }
        for (net.minecraft.world.entity.player.Player p : player.level().getEntitiesOfClass(
                net.minecraft.world.entity.player.Player.class, box,
                p -> p.isAlive() && p != player)) {
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, dur / 3, 0));
        }
    }

    private static void knockbackEnemies(ServerPlayer player, double radius) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, box,
                m -> m.isAlive() && !m.isAlliedTo(player))) {
            mob.knockback(1.2, player.getX() - mob.getX(), player.getZ() - mob.getZ());
        }
    }

    private static void spawnZone(ServerLevel level, ServerPlayer player,
            net.minecraft.core.particles.ParticleOptions particle, double radius) {
        for (int i = 0; i < 24; i++) {
            double angle = i * Math.PI * 2 / 24;
            level.sendParticles(particle,
                    player.getX() + Math.cos(angle) * radius,
                    player.getY() + 0.2,
                    player.getZ() + Math.sin(angle) * radius,
                    2, 0, 0.1, 0, 0.02);
        }
    }

    private static int cooldownFor(int tier, boolean outer) {
        int base = switch (tier) {
            case 9 -> 600;
            case 8 -> 900;
            case 7 -> 1200;
            case 6, 5 -> 1500;
            default -> 1800;
        };
        return outer ? base + 200 : base;
    }

    public static void tryAdvanceTier(BondData bond) {
        int shards = bond.getMemoryShardsTotal();
        int tier = bond.getFelineTier();
        if (tier == 9 && shards >= 3) bond.setFelineTier(8);
        else if (tier == 8 && shards >= 8) bond.setFelineTier(7);
        else if (tier == 7 && shards >= 15) bond.setFelineTier(6);
        else if (tier == 6 && shards >= 22) bond.setFelineTier(3);
        else if (tier == 3 && shards >= 30) bond.setFelineTier(2);
    }
}
