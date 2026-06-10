package com.cocojenna.item;

import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModEffects;
import com.cocojenna.util.MemoryShardUtil;
import com.cocojenna.weapon.WeaponData;
import com.cocojenna.weapon.WeaponUnsealManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** 良快刀 50 把被動效果（設計書第一章）. */
public final class RyokatanaEffectHelper {

    private RyokatanaEffectHelper() {}

    public static float damageMultiplier(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!(weapon.getItem() instanceof RyokatanaItem item)) return 1f;
        float mult = 1f;
        String id = item.getVariantId();
        mult *= switch (id) {
            case "hibiscus_blood" -> attacker.getHealth() / attacker.getMaxHealth() < 0.3f ? 1.5f : 1f;
            case "dawn_hope" -> attacker.level().isDay() ? 1.15f : 1f;
            case "dark_tide" -> isBehind(attacker, target) ? 1.4f : 1f;
            case "moon_shadow", "moonlight_glimmer", "moonlight_clear", "moonlight_ripple" ->
                    attacker.level().isNight() ? 1.2f : 1f;
            case "fish_bone_tide", "deep_sea_current", "blind_water_abyss", "blind_water_core" ->
                    attacker.isInWaterOrRain() || target.isInWaterOrRain() ? 1.2f : 1f;
            case "precision_gear", "gear_precision_2", "gear_windup", "gear_schedule" ->
                    target instanceof net.minecraft.world.entity.animal.IronGolem || isMechanical(target) ? 1.3f : 1f;
            case "iron_rust_armor_break", "iron_rust_legion", "iron_claw_apprentice" ->
                    target.getArmorValue() > 0 ? 1.25f : 1f;
            case "fallen_velvet_claw", "velvet_whisper", "velvet_cradle", "velvet_warmth" ->
                    isCatType(target) ? 1.35f : 1f;
            case "bronze_guard" -> 1.1f;
            case "royal_glory" -> isBlackMud(target) ? 1.35f : 1.1f;
            case "red_jade", "calico_warmth" -> attacker.hasEffect(MobEffects.REGENERATION) ? 1.2f : 1f;
            case "cheshire_grin" -> attacker.isShiftKeyDown() ? 1.35f : 1f;
            case "first_cry_beginner" -> 1.05f;
            case "white_glove_guide" -> target.isInWaterOrRain() ? 1.3f : 1f;
            case "alpha_observe" -> target.getMaxHealth() > 100 ? 1.25f : 1f;
            case "coco_guardian", "milk_tea_play" -> 1.1f;
            case "whisper_mud", "blind_water_stealth" -> attacker.isInvisible() ? 1.4f : 1f;
            case "silvervine_drunk" -> attacker.hasEffect(MobEffects.CONFUSION) ? 1.2f : 0.9f;
            case "sanhua_thread", "paper_crow_ink" -> 1.08f;
            case "stardust_step", "stardust_tread", "forgotten_page" -> 1.12f;
            case "origami_cut" -> target.isInvisible() ? 1.3f : 1f;
            case "screen_noise" -> attacker.getRandom().nextFloat() < 0.15f ? 1.25f : 1f;
            case "moth_scale" -> attacker.level().isNight() ? 1.15f : 0.95f;
            case "lament_split" -> target.getHealth() / target.getMaxHealth() < 0.5f ? 1.3f : 1f;
            case "memory_worm" -> 1.05f;
            case "neon_flash" -> attacker.level().isDay() ? 1.18f : 1f;
            case "jellyfish_bind" -> target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? 1.25f : 1f;
            case "copper_bell_soul" -> attacker.getHealth() / attacker.getMaxHealth() > 0.8f ? 1.12f : 1f;
            case "mimic_disguise" -> 1f;
            default -> 1f;
        };
        mult *= WeaponData.attackMultiplier(weapon);
        return mult;
    }

    public static void onHit(Player attacker, LivingEntity target, ItemStack weapon, float damage) {
        if (!(weapon.getItem() instanceof RyokatanaItem item)) return;
        String id = item.getVariantId();
        switch (id) {
            case "jellyfish_bind" -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            case "origami_cut" -> target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0));
            case "screen_noise" -> {
                if (attacker.getRandom().nextFloat() < 0.2f) {
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
                }
            }
            case "moth_scale" -> {
                if (attacker.getRandom().nextFloat() < 0.15f) {
                    attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0));
                }
            }
            case "blind_water_abyss", "blind_water_core" -> {
                if (target.isInWaterOrRain()) {
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
                }
            }
            case "lament_split" -> {
                if (damage > target.getMaxHealth() * 0.2f) {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                }
            }
            case "memory_worm" -> {
                if (attacker instanceof ServerPlayer sp && sp.getRandom().nextFloat() < 0.05f) {
                    var shard = MemoryShardUtil.create("memory_worm_hit");
                    if (!sp.addItem(shard)) sp.drop(shard, false);
                }
            }
            case "neon_flash" -> target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0));
            case "velvet_whisper" -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
            case "gear_windup" -> attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0));
            case "forgotten_page" -> {
                if (attacker.getRandom().nextFloat() < 0.1f) {
                    attacker.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 0));
                }
            }
            case "velvet_cradle" -> attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 0));
            case "iron_claw_apprentice" -> target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 0));
            case "calico_warmth" -> attacker.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0));
            case "milk_tea_play" -> attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
            case "velvet_warmth" -> attacker.heal(0.5f);
            case "paper_crow_ink" -> target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
            case "copper_bell_soul" -> {
                if (attacker.getRandom().nextFloat() < 0.12f) {
                    attacker.level().playSound(null, attacker.blockPosition(),
                            net.minecraft.sounds.SoundEvents.BELL_BLOCK, net.minecraft.sounds.SoundSource.PLAYERS,
                            0.4f, 1.2f);
                }
            }
            case "gear_schedule" -> {
                if (weapon.getTag() == null) {
                    weapon.setTag(new net.minecraft.nbt.CompoundTag());
                }
                weapon.getTag().putInt("ryo_hits", weapon.getTag().getInt("ryo_hits") + 1);
            }
            default -> {}
        }
    }

    public static float firstStrikeBonus(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!(weapon.getItem() instanceof RyokatanaItem item)) return 1f;
        String id = item.getVariantId();
        if (id.equals("cheshire_grin")) {
            String key = "ryo_cheshire_" + target.getUUID();
            if (attacker.getPersistentData().getBoolean(key)) return 1f;
            attacker.getPersistentData().putBoolean(key, true);
            return 1.4f;
        }
        if (!id.equals("mimic_disguise")) return 1f;
        String key = "ryo_first_" + target.getUUID();
        if (attacker.getPersistentData().getBoolean(key)) return 1f;
        attacker.getPersistentData().putBoolean(key, true);
        return 1.5f;
    }

    public static void onKill(Player attacker, LivingEntity target, ItemStack weapon) {
        if (!(weapon.getItem() instanceof RyokatanaItem item)) return;
        if (!(attacker instanceof ServerPlayer sp)) return;
        String id = item.getVariantId();
        if (id.equals("first_cry_memory") && sp.getRandom().nextFloat() < 0.1f) {
            var shard = MemoryShardUtil.create("ryokatana_kill");
            if (!sp.addItem(shard)) sp.drop(shard, false);
        }
        if (id.equals("royal_glory") && isBlackMud(target)) {
            var shard = MemoryShardUtil.create("royal_glory_kill");
            if (!sp.addItem(shard)) sp.drop(shard, false);
        }
        if (id.equals("lament_split") && sp.getRandom().nextFloat() < 0.08f) {
            var shard = MemoryShardUtil.create("lament_split");
            if (!sp.addItem(shard)) sp.drop(shard, false);
        }
    }

    private static boolean isBehind(Player attacker, LivingEntity target) {
        Vec3 toAttacker = attacker.position().subtract(target.position()).normalize();
        Vec3 look = target.getLookAngle().normalize();
        return look.dot(toAttacker) > 0.6;
    }

    private static boolean isMechanical(LivingEntity target) {
        return target instanceof net.minecraft.world.entity.animal.IronGolem
                || target.getType().getDescriptionId().contains("iron");
    }

    private static boolean isBlackMud(LivingEntity target) {
        String path = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES
                .getKey(target.getType()).getPath();
        return path.contains("heat_leech") || path.contains("forgotten_wisp")
                || path.contains("memory_moth") || path.contains("whispering_doll")
                || path.contains("mimic_cat") || path.contains("fallen_velvet")
                || path.contains("grief_amalgam") || path.contains("blind_water")
                || path.contains("primal_chaos") || path.contains("squall")
                || path.contains("phantom") || path.contains("overlord") || path.contains("wraith")
                || path.contains("warden") || path.contains("stitcher");
    }

    private static boolean isCatType(LivingEntity target) {
        String path = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES
                .getKey(target.getType()).getPath();
        return path.contains("cat") || path.contains("coco") || path.contains("jenna");
    }

    // ── 良快刀主動技（右鍵蓄力釋放）────────────────────────────────────

    public static boolean castSkill(Player player, Level level, String variantId, int chargeTicks) {
        return castSkill(player, level, variantId, chargeTicks, player.getMainHandItem());
    }

    public static boolean castSkill(Player player, Level level, String variantId, int chargeTicks, ItemStack weapon) {
        if (level.isClientSide) return false;
        if (com.cocojenna.weapon.WeaponSkillExecutor.tryCast(player, level, variantId, chargeTicks, weapon)) {
            return true;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer sp
                && !com.cocojenna.weapon.WeaponSkillHelper.tryConsumeMana(sp, variantId)) {
            return false;
        }
        float stagePower = WeaponData.skillPowerMultiplier(weapon);
        if (stagePower <= 0f) return false;
        float power = (1f + Math.min(1.5f, (chargeTicks - WeaponChargeHelper.MIN_CHARGE_TICKS) / 40f)) * stagePower;
        if (WeaponUnsealManager.supportsStageScaling(variantId)) {
            power *= pilotStageBonus(variantId, WeaponData.getStage(weapon));
        }
        SkillType type = skillType(variantId);
        switch (type) {
            case BIND -> bindSkill(player, level, power);
            case FLASH -> flashSkill(player, level, power);
            case HEAL -> healSkill(player, level, power);
            case STEALTH -> stealthSkill(player, level);
            case WAVE -> waveSkill(player, level, power);
            case GEAR -> gearSkill(player, level, power);
            case MOON -> moonSkill(player, level, power);
            default -> slashSkill(player, level, power);
        }
        spawnReleaseParticles(level, player, type);
        if (player instanceof ServerPlayer sp) {
            var force = com.cocojenna.combat.CombatVfxHelper.of(
                    com.cocojenna.capability.ModCapabilities.getOrDefault(sp).getFelineForce());
            int tier = Math.min(4, WeaponData.getStage(weapon).id + 1);
            com.cocojenna.combat.CombatVfxHelper.skillCast(
                    sp.serverLevel(), sp, force, tier, false);
        }
        return true;
    }

    private static float pilotStageBonus(String variantId, com.cocojenna.weapon.WeaponAwakeningStage stage) {
        return switch (variantId) {
            case "fish_bone_tide" -> switch (stage) {
                case AWAKENED -> 1.05f;
                case ENLIGHTENED -> 1.15f;
                case RESONANCE -> 1.35f;
                default -> 1f;
            };
            case "iron_rust_armor_break" -> switch (stage) {
                case AWAKENED -> 1.1f;
                case ENLIGHTENED -> 1.25f;
                case RESONANCE -> 1.5f;
                default -> 1f;
            };
            case "hibiscus_blood" -> switch (stage) {
                case AWAKENED -> 1.08f;
                case ENLIGHTENED -> 1.2f;
                case RESONANCE -> 1.4f;
                default -> 1f;
            };
            case "moonlight_ripple" -> switch (stage) {
                case AWAKENED -> 1.06f;
                case ENLIGHTENED -> 1.18f;
                case RESONANCE -> 1.3f;
                default -> 1f;
            };
            case "paper_crow_ink" -> switch (stage) {
                case AWAKENED -> 1.07f;
                case ENLIGHTENED -> 1.22f;
                case RESONANCE -> 1.45f;
                default -> 1f;
            };
            default -> 1f;
        };
    }

    private enum SkillType { SLASH, WAVE, BIND, FLASH, HEAL, STEALTH, GEAR, MOON }

    private static SkillType skillType(String id) {
        return switch (id) {
            case "jellyfish_bind", "blind_water_abyss", "blind_water_core", "lament_split",
                    "velvet_cradle", "forgotten_page" -> SkillType.BIND;
            case "neon_flash", "screen_noise", "paper_crow_ink", "stardust_step", "stardust_tread" -> SkillType.FLASH;
            case "red_jade", "calico_warmth", "velvet_warmth", "coco_guardian", "milk_tea_play" -> SkillType.HEAL;
            case "whisper_mud", "blind_water_stealth", "mimic_disguise", "cheshire_grin", "moon_shadow" -> SkillType.STEALTH;
            case "fish_bone_tide", "deep_sea_current", "dark_tide" -> SkillType.WAVE;
            case "precision_gear", "gear_precision_2", "gear_windup", "gear_schedule", "iron_claw_apprentice" ->
                    SkillType.GEAR;
            case "moonlight_glimmer", "moonlight_clear", "moonlight_ripple", "dawn_hope", "sanhua_thread" ->
                    SkillType.MOON;
            default -> SkillType.SLASH;
        };
    }

    /** JSON 技能管線入口 — 依 archetype 執行. */
    public static void castArchetype(Player player, Level level, String archetype, float power, float radius) {
        SkillType type = switch (archetype) {
            case "bind" -> SkillType.BIND;
            case "flash" -> SkillType.FLASH;
            case "heal" -> SkillType.HEAL;
            case "stealth" -> SkillType.STEALTH;
            case "wave" -> SkillType.WAVE;
            case "gear" -> SkillType.GEAR;
            case "moon" -> SkillType.MOON;
            default -> SkillType.SLASH;
        };
        float scaled = power * radius;
        switch (type) {
            case BIND -> bindSkill(player, level, scaled);
            case FLASH -> flashSkill(player, level, scaled);
            case HEAL -> healSkill(player, level, scaled);
            case STEALTH -> stealthSkill(player, level);
            case WAVE -> waveSkill(player, level, scaled);
            case GEAR -> gearSkill(player, level, scaled);
            case MOON -> moonSkill(player, level, scaled);
            default -> slashSkill(player, level, scaled);
        }
        spawnReleaseParticles(level, player, type);
    }

    private static void slashSkill(Player player, Level level, float power) {
        hurtCone(player, level, 4.5 + power, 4f + power * 3f);
    }

    private static void waveSkill(Player player, Level level, float power) {
        float dmg = 3f + power * 2f;
        AABB box = player.getBoundingBox().inflate(5.0 + power);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> t != player && t.isAlive())) {
            e.hurt(level.damageSources().playerAttack(player), dmg);
            if (player.isInWaterOrRain() || e.isInWaterOrRain()) {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            }
        }
    }

    private static void bindSkill(Player player, Level level, float power) {
        AABB box = player.getBoundingBox().inflate(4.0 + power * 0.5);
        int dur = (int) (50 + power * 20);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> t != player && t.isAlive())) {
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, dur, 2));
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, dur / 2, 0));
        }
    }

    private static void flashSkill(Player player, Level level, float power) {
        hurtCone(player, level, 5.0 + power, 5f + power * 2f);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1));
    }

    private static void healSkill(Player player, Level level, float power) {
        player.heal(2f + power);
        player.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 80, 0));
        AABB box = player.getBoundingBox().inflate(4.0);
        for (AbstractCatEntity cat : level.getEntitiesOfClass(AbstractCatEntity.class, box,
                c -> player.getUUID().equals(c.getOwnerUUID()))) {
            cat.heal(2f + power);
        }
    }

    private static void stealthSkill(Player player, Level level) {
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1));
    }

    private static void gearSkill(Player player, Level level, float power) {
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 100, 1));
        hurtCone(player, level, 3.5 + power, 6f + power * 2f);
    }

    private static void moonSkill(Player player, Level level, float power) {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, (int) (60 + power * 20), 0));
        hurtCone(player, level, 4.0 + power, 4f + power * 2.5f);
    }

    private static void hurtCone(Player player, Level level, double range, float dmg) {
        Vec3 look = player.getLookAngle().normalize();
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.2);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> t != player && t.isAlive())) {
            Vec3 to = e.position().subtract(player.position()).normalize();
            if (look.dot(to) > 0.35) {
                e.hurt(level.damageSources().playerAttack(player), dmg);
            }
        }
    }

    private static void spawnReleaseParticles(Level level, Player player, SkillType type) {
        if (!(level instanceof ServerLevel sl)) return;
        var particle = switch (type) {
            case WAVE -> ParticleTypes.SPLASH;
            case FLASH, MOON -> ParticleTypes.END_ROD;
            case HEAL -> ParticleTypes.HEART;
            case STEALTH -> ParticleTypes.SMOKE;
            case GEAR -> ParticleTypes.ELECTRIC_SPARK;
            default -> ParticleTypes.SWEEP_ATTACK;
        };
        sl.sendParticles(particle, player.getX(), player.getY() + 1.0, player.getZ(),
                12, 0.5, 0.3, 0.5, 0.02);
    }
}
