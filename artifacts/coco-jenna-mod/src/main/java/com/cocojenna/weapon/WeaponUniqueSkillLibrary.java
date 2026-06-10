package com.cocojenna.weapon;

import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.DaikataItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

/** 每把武器獨立技能邏輯（取代 8 種 archetype 共用）. */
public final class WeaponUniqueSkillLibrary {

    private static final Set<String> DAIKATA = Set.of(
            "tiger_iron", "wind_cut", "phantom", "suppression", "shockwave",
            "crescent", "moon_verdict", "star_map", "abyss", "neon_dance",
            "gear_king", "hibiscus_ultimate", "howling_gorge", "first_dawn",
            "royal_authority", "shadow_imitation", "silent_guard", "dusk_end",
            "white_glove", "forgotten_tower", "village_soul", "storm_umbrella",
            "salmon_king", "night_verdict", "toy_hammer", "hibiscus_fall"
    );

    private WeaponUniqueSkillLibrary() {}

    public static boolean tryCast(Player player, Level level, WeaponSkillContext ctx) {
        if (level.isClientSide) return false;
        if (DAIKATA.contains(ctx.variantId()) && ctx.weapon().getItem() instanceof DaikataItem item) {
            item.executeStoredSkill(player, level, ctx.weapon());
            finishCast(player, ctx);
            return true;
        }
        boolean cast = castRyokatana(player, level, ctx);
        if (cast) finishCast(player, ctx);
        return cast;
    }

    private static void finishCast(Player player, WeaponSkillContext ctx) {
        SourceForceSynergy.onCastComplete(player, ctx);
        if (player instanceof ServerPlayer sp) {
            WeaponResonanceVoice.trySpeak(sp, ctx);
        }
    }

    private static boolean castRyokatana(Player player, Level level, WeaponSkillContext ctx) {
        float p = ctx.scaledPower() * SourceForceSynergy.powerMult(ctx.sourceForce());
        double r = ctx.radius() * SourceForceSynergy.radiusMult(ctx.sourceForce());
        int stage = ctx.stage();

        switch (ctx.skillId()) {
            case "fish_bone_tide" -> {
                WeaponSkillEffects.spawnFishSchool(player, level, 3 + stage, 3f + p);
                if (player.isInWaterOrRain()) player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 80, 0));
            }
            case "deep_sea_current" -> {
                WeaponSkillEffects.spawnFishSchool(player, level, 5 + stage, 4f + p);
                WeaponSkillEffects.pullAndDamage(player, level, 5 + r, 2f + p, 0.35);
            }
            case "blind_water_abyss", "blind_water_core" -> {
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 4f + p, e -> {
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60 + stage * 20, 0));
                    if (e.isInWaterOrRain()) e.setSecondsOnFire(2);
                });
                WeaponSkillEffects.particles(level, player, ParticleTypes.SQUID_INK, 16);
            }
            case "blind_water_stealth" -> {
                WeaponSkillEffects.stealthBurst(player, 50 + stage * 20, 1);
                WeaponSkillEffects.particles(level, player, ParticleTypes.SMOKE, 10);
            }
            case "iron_rust_armor_break" -> {
                WeaponSkillEffects.slashCone(player, level, 4 + r, 5f + p * 1.2f);
                WeaponSkillEffects.hurtSphere(player, level, 3 + r, 0, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1)));
            }
            case "iron_rust_legion" -> {
                if (level instanceof ServerLevel sl) {
                    ModEntities.SAMURAI_CAT.get().spawn(sl, player.blockPosition().east(), MobSpawnType.MOB_SUMMONED);
                    if (stage >= 2) ModEntities.SAMURAI_CAT.get().spawn(sl, player.blockPosition().west(), MobSpawnType.MOB_SUMMONED);
                }
                WeaponSkillEffects.slashCone(player, level, 3.5 + r, 4f + p);
            }
            case "iron_claw_apprentice" -> {
                WeaponSkillEffects.slashCone(player, level, 4 + r, 4f + p);
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, stage >= 2 ? 1 : 0));
            }
            case "hibiscus_blood" -> {
                float selfCost = Math.min(player.getHealth() - 1f, 2f + stage);
                player.hurt(player.damageSources().magic(), selfCost);
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 6f + p * 1.5f, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0)));
                WeaponSkillEffects.particles(level, player, ParticleTypes.ANGRY_VILLAGER, 8);
            }
            case "moonlight_ripple", "moonlight_clear", "moonlight_glimmer" -> {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 160, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 + stage * 20, stage >= 2 ? 1 : 0));
                WeaponSkillEffects.slashCone(player, level, 4 + r, 3.5f + p);
                if (level.isNight()) WeaponSkillEffects.healSelfAndCats(player, level, 2f + stage);
            }
            case "paper_crow_ink" -> {
                WeaponSkillEffects.summonPaperCrow(player, level);
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 3f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.POISON, 60, stage >= 2 ? 1 : 0)));
            }
            case "origami_cut" -> {
                WeaponSkillEffects.slashCone(player, level, 5 + r, 4f + p);
                WeaponSkillEffects.hurtSphere(player, level, 3 + r, 0, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0)));
            }
            case "sanhua_thread" -> {
                WeaponSkillEffects.healSelfAndCats(player, level, 2f + p);
                WeaponSkillEffects.slashCone(player, level, 3.5 + r, 2.5f + p * 0.5f);
            }
            case "jellyfish_bind" -> {
                WeaponSkillEffects.applyDebuffSphere(player, level, 4 + r, 60 + stage * 20, 2);
                WeaponSkillEffects.particles(level, player, ParticleTypes.BUBBLE, 20);
            }
            case "lament_split" -> {
                WeaponSkillEffects.hurtSphere(player, level, 5 + r, 2f + p, e -> {
                    float drain = e.getHealth() * (0.02f + stage * 0.01f);
                    e.hurt(level.damageSources().magic(), drain);
                    player.heal(drain * 0.5f);
                });
            }
            case "memory_worm" -> {
                WeaponSkillEffects.hurtSphere(player, level, 3 + r, 3f + p, null);
                if (player instanceof ServerPlayer sp && sp.getRandom().nextFloat() < 0.15f + stage * 0.05f) {
                    var shard = com.cocojenna.util.MemoryShardUtil.create("memory_worm_skill");
                    if (!sp.addItem(shard)) sp.drop(shard, false);
                }
            }
            case "precision_gear", "gear_precision_2", "gear_windup", "gear_schedule" -> {
                int gears = 2 + stage;
                WeaponSkillEffects.slashCone(player, level, 3.5 + r, 4f + p);
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 80 + stage * 20, 1));
                if (stage >= 2) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0));
                WeaponSkillEffects.particles(level, player, ParticleTypes.ELECTRIC_SPARK, gears * 4);
            }
            case "neon_flash" -> {
                WeaponSkillEffects.slashCone(player, level, 5 + r, 4f + p);
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 0, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40 + stage * 10, 0)));
            }
            case "moth_scale" -> {
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 2f + p, e -> {
                    if (level.random.nextBoolean()) e.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
                    else e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
                });
                WeaponSkillEffects.particles(level, player, ParticleTypes.END_ROD, 24);
            }
            case "velvet_warmth", "velvet_cradle", "velvet_whisper" -> {
                WeaponSkillEffects.healSelfAndCats(player, level, 2f + p + stage);
                WeaponSkillEffects.hurtSphere(player, level, 3 + r, 0, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 50, 0)));
            }
            case "red_jade", "calico_warmth" -> {
                WeaponSkillEffects.healSelfAndCats(player, level, 3f + p);
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60 + stage * 20, 0));
            }
            case "coco_guardian", "milk_tea_play" -> {
                WeaponSkillEffects.healSelfAndCats(player, level, 2.5f + p);
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, stage >= 2 ? 1 : 0));
            }
            case "first_cry_beginner", "first_cry_memory" -> {
                WeaponSkillEffects.hurtSphere(player, level, 3 + r, 2f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 2)));
                for (AbstractCatEntity cat : level.getEntitiesOfClass(AbstractCatEntity.class,
                        player.getBoundingBox().inflate(8))) {
                    cat.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0));
                }
            }
            case "dawn_hope" -> {
                if (level.isDay()) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1));
                WeaponSkillEffects.slashCone(player, level, 4.5 + r, 4f + p);
            }
            case "dark_tide" -> {
                WeaponSkillEffects.hurtSphere(player, level, 5 + r, 3f + p * 1.2f, e -> {
                    if (player.isInWaterOrRain()) e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
                });
            }
            case "whisper_mud", "mimic_disguise", "moon_shadow" -> {
                WeaponSkillEffects.stealthBurst(player, 50 + stage * 15, stage >= 2 ? 2 : 1);
            }
            case "cheshire_grin" -> {
                WeaponSkillEffects.stealthBurst(player, 70 + stage * 20, stage >= 2 ? 2 : 1);
                WeaponSkillEffects.hurtSphere(player, level, 3 + r, 2.5f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 50 + stage * 15, 0)));
                WeaponSkillEffects.particles(level, player, ParticleTypes.PORTAL, 12 + stage * 4);
            }
            case "wind_cut", "crescent" -> {
                WeaponSkillEffects.slashCone(player, level, 5.5 + r, 4.5f + p);
                Vec3 dash = player.getLookAngle().normalize().scale(2 + stage * 0.5);
                player.setDeltaMovement(dash.x, 0.15, dash.z);
            }
            case "shockwave", "suppression" -> {
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 4f + p * 1.1f, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, stage >= 2 ? 1 : 0)));
                WeaponSkillEffects.particles(level, player, ParticleTypes.EXPLOSION, 4 + stage);
            }
            case "howling_gorge", "storm_umbrella" -> {
                WeaponSkillEffects.hurtSphere(player, level, 5 + r, 3.5f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 50, 0)));
                level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.ELYTRA_FLYING,
                        net.minecraft.sounds.SoundSource.PLAYERS, 0.6f, 0.7f);
            }
            case "salmon_king" -> {
                WeaponSkillEffects.spawnFishSchool(player, level, 4 + stage, 3.5f + p);
                WeaponSkillEffects.slashCone(player, level, 4 + r, 3f + p);
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100, 0));
            }
            case "village_soul", "first_dawn" -> {
                WeaponSkillEffects.healSelfAndCats(player, level, 2f + stage);
                WeaponSkillEffects.slashCone(player, level, 4 + r, 3.5f + p);
                for (AbstractCatEntity cat : level.getEntitiesOfClass(AbstractCatEntity.class,
                        player.getBoundingBox().inflate(10))) {
                    cat.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0));
                }
            }
            case "night_verdict", "moon_verdict" -> {
                WeaponSkillEffects.slashCone(player, level, 5 + r, 5f + p * 1.2f);
                if (level.isNight()) {
                    WeaponSkillEffects.hurtSphere(player, level, 4 + r, 2f + p, null);
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 120, 0));
                }
            }
            case "neon_dance" -> {
                WeaponSkillEffects.slashCone(player, level, 5 + r, 4f + p);
                WeaponSkillEffects.hurtSphere(player, level, 3.5 + r, 0, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0)));
            }
            case "star_map", "royal_authority" -> {
                WeaponSkillEffects.hurtSphere(player, level, 5 + r, 4f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0)));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, stage >= 2 ? 1 : 0));
            }
            case "abyss" -> {
                WeaponSkillEffects.hurtSphere(player, level, 5 + r, 4f + p * 1.3f, null);
                WeaponSkillEffects.particles(level, player, ParticleTypes.SQUID_INK, 20);
            }
            case "hibiscus_fall", "hibiscus_ultimate" -> {
                WeaponSkillEffects.hurtSphere(player, level, 5 + r, 5f + p * 1.4f, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, stage >= 2 ? 1 : 0)));
                WeaponSkillEffects.particles(level, player, ParticleTypes.CRIMSON_SPORE, 16);
            }
            case "phantom", "shadow_imitation" -> {
                WeaponSkillEffects.stealthBurst(player, 40 + stage * 10, 1);
                WeaponSkillEffects.slashCone(player, level, 4.5 + r, 4f + p);
            }
            case "silent_guard", "white_glove" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, stage >= 2 ? 1 : 0));
                WeaponSkillEffects.slashCone(player, level, 3.5 + r, 3f + p);
            }
            case "forgotten_tower", "dusk_end" -> {
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 3f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0)));
                WeaponSkillEffects.particles(level, player, ParticleTypes.ASH, 18);
            }
            case "toy_hammer", "gear_king" -> {
                WeaponSkillEffects.hurtSphere(player, level, 3.5 + r, 2f + p, e ->
                        e.knockback(0.5 + stage * 0.15, player.getX() - e.getX(), player.getZ() - e.getZ()));
                WeaponSkillEffects.particles(level, player, ParticleTypes.CRIT, 10);
            }
            case "tiger_iron" -> {
                WeaponSkillEffects.slashCone(player, level, 4 + r, 6f + p * 1.4f);
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, stage >= 2 ? 2 : 1));
            }
            case "bronze_guard" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, stage >= 2 ? 2 : 1));
                WeaponSkillEffects.slashCone(player, level, 3 + r, 3f + p);
            }
            case "royal_glory" -> {
                WeaponSkillEffects.slashCone(player, level, 5 + r, 5f + p * 1.3f);
                WeaponSkillEffects.particles(level, player, ParticleTypes.ENCHANT, 12);
            }
            case "copper_bell_soul" -> {
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 3f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)));
                level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.BELL_BLOCK,
                        net.minecraft.sounds.SoundSource.PLAYERS, 1f, 0.8f);
            }
            case "fallen_velvet_claw" -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, 1));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
                WeaponSkillEffects.slashCone(player, level, 4 + r, 5f + p);
            }
            case "stardust_step", "stardust_tread" -> {
                Vec3 dash = player.getLookAngle().normalize().scale(4 + stage);
                player.setDeltaMovement(dash.x, 0.2, dash.z);
                WeaponSkillEffects.slashCone(player, level, 4 + r, 4f + p);
                WeaponSkillEffects.particles(level, player, ParticleTypes.END_ROD, 14);
            }
            case "forgotten_page" -> {
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 3f + p, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0)));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, stage >= 2 ? 1 : 0));
            }
            case "screen_noise" -> {
                WeaponSkillEffects.slashCone(player, level, 4 + r, 3.5f + p);
                WeaponSkillEffects.hurtSphere(player, level, 4 + r, 0, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0)));
            }
            case "silvervine_drunk" -> {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0));
                WeaponSkillEffects.slashCone(player, level, 5 + r, 4f + p * 1.2f);
            }
            case "alpha_observe" -> {
                WeaponSkillEffects.hurtSphere(player, level, 6 + r, 4f + p, e -> {
                    if (e.getMaxHealth() > 80) e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
                });
            }
            case "white_glove_guide" -> {
                WeaponSkillEffects.slashCone(player, level, 4 + r, 4f + p);
                if (player.isInWaterOrRain()) WeaponSkillEffects.healSelfAndCats(player, level, 2f);
            }
            default -> {
                // 未列舉的良快刀：仍保留差異化參數，但不回退 archetype 共用邏輯
                WeaponSkillEffects.slashCone(player, level, 3.5 + r + stage * 0.3, 3f + p + stage * 0.5f);
                WeaponSkillEffects.particles(level, player, ParticleTypes.SWEEP_ATTACK, 6 + stage);
            }
        }
        return true;
    }
}
