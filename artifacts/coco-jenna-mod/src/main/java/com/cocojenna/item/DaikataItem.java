package com.cocojenna.item;

import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.weapon.WeaponAwakeningStage;
import com.cocojenna.weapon.WeaponData;
import com.cocojenna.weapon.WeaponSkillExecutor;
import com.cocojenna.weapon.WeaponSkillHelper;
import com.cocojenna.weapon.WeaponSkillRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 大快刀 (Daikata) — 傳說武器基礎類別。
 *
 * <p>每把大快刀擁有獨特技能（{@link Skill}）。
 * 按住右鍵蓄力 40 tick 後釋放技能。
 */
public class DaikataItem extends SwordItem {

    public enum Skill {
        SUMMON_WARRIOR,
        DASH_SLASH,
        PHANTOM_STEP,
        STILL_DOMAIN,
        GROUND_SLAM,
        CRESCENT_SLASH,
        STAR_GUIDE,
        WATER_PRISON,
        NEON_BARRAGE,
        MECHANICAL_FRENZY,
        BLOOD_TIDE,
        GORGE_WIND,
        DAWN_BURST,
        ROYAL_DECREE,
        SHADOW_STRIKE,
        SILENT_SHIELD,
        TWILIGHT_SLASH,
        TRADE_ROUTE,
        TOWER_COLLAPSE,
        VILLAGE_BOND,
        STORM_CALL,
    }

    private static final int SKILL_COOLDOWN = 80;

    private final Skill skill;
    private final String variantId;

    public DaikataItem(Tier tier, int dmgBonus, float atkSpeed, Properties props,
            String variantId, Skill skill) {
        super(tier, dmgBonus, atkSpeed, props.rarity(Rarity.EPIC));
        this.variantId = variantId;
        this.skill = skill;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return WeaponChargeHelper.startCharge(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return WeaponChargeHelper.useOnBlock(ctx, this);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        int chargeTime = WeaponChargeHelper.chargeTicks(stack, timeLeft);
        int minCharge = WeaponSkillHelper.minChargeTicks(variantId, WeaponChargeHelper.MIN_CHARGE_TICKS);
        if (chargeTime < minCharge) {
            WeaponChargeHelper.notifyChargeTooShort(player);
            return;
        }
        WeaponAwakeningStage stage = WeaponData.getStage(stack);
        if (!stage.hasActiveSkill()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("weapon.cocojenna.skill_locked")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            return;
        }
        int cooldown = WeaponSkillHelper.cooldownTicks(variantId, stack, SKILL_COOLDOWN);
        if (!WeaponChargeHelper.tryConsumeCooldown(player, this, cooldown)) return;

        if (!level.isClientSide) {
            boolean cast = false;
            if (WeaponSkillRegistry.has(variantId)) {
                cast = WeaponSkillExecutor.tryCast(player, level, variantId, chargeTime, stack);
            } else {
                if (!(player instanceof ServerPlayer sp) || WeaponSkillHelper.tryConsumeMana(sp, variantId)) {
                    performSkill(player, level, stack);
                    cast = true;
                }
            }
            if (cast) {
                WeaponChargeHelper.applyCooldown(player, this, cooldown);
                WeaponChargeHelper.playReleaseSound(level, player);
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        WeaponChargeHelper.tickChargeFeedback(entity, stack,
                WeaponChargeHelper.chargeTicks(stack, remaining));
    }

    /** 供 WeaponUniqueSkillLibrary 調用的獨立大快刀技能. */
    public void executeStoredSkill(Player player, Level level, ItemStack stack) {
        performSkill(player, level, stack);
    }

    private void performSkill(Player player, Level level, ItemStack stack) {
        float dmg = getDamageValue() * WeaponData.skillPowerMultiplier(stack);
        AABB area5 = player.getBoundingBox().inflate(5.0);
        AABB area6 = player.getBoundingBox().inflate(6.0);
        AABB area8 = player.getBoundingBox().inflate(8.0);

        switch (skill) {
            case SUMMON_WARRIOR -> {
                if (level instanceof ServerLevel server) {
                    ModEntities.SAMURAI_CAT.get().spawn(server, player.blockPosition().above(),
                            MobSpawnType.MOB_SUMMONED);
                }
            }
            case DASH_SLASH -> {
                Vec3 dir = player.getLookAngle().normalize().scale(5.0);
                player.setDeltaMovement(dir.x, 0.1, dir.z);
                player.hurtMarked = true;
                level.getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().expandTowards(dir).inflate(1.0),
                                e -> e != player)
                        .forEach(e -> e.hurt(level.damageSources().playerAttack(player), dmg));
            }
            case GROUND_SLAM -> hurtInArea(level, player, area5, dmg * 0.8f, e -> e.setDeltaMovement(0, 1.5, 0));
            case PHANTOM_STEP -> {
                double angle = player.getYRot() * Math.PI / 180;
                player.teleportTo(player.getX() - Math.sin(angle) * 8,
                        player.getY(), player.getZ() + Math.cos(angle) * 8);
            }
            case STILL_DOMAIN -> level.getEntitiesOfClass(LivingEntity.class, area8, e -> e != player)
                    .forEach(e -> e.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 4)));
            case CRESCENT_SLASH -> {
                for (int i = -2; i <= 2; i++) {
                    double yaw = Math.toRadians(player.getYRot() + i * 18);
                    Vec3 slash = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw)).scale(4);
                    level.getEntitiesOfClass(LivingEntity.class,
                                    player.getBoundingBox().expandTowards(slash).inflate(1.5),
                                    e -> e != player)
                            .forEach(e -> e.hurt(level.damageSources().playerAttack(player), dmg * 0.7f));
                }
                spawnParticles(level, player, ParticleTypes.SWEEP_ATTACK, 12);
            }
            case STAR_GUIDE -> player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
            case WATER_PRISON -> level.getEntitiesOfClass(LivingEntity.class, area5, e -> e != player)
                    .forEach(e -> {
                        BlockPos feet = e.blockPosition();
                        for (int dy = 0; dy < 2; dy++) {
                            BlockPos p = feet.offset(0, dy, 0);
                            if (level.getBlockState(p).isAir()) {
                                level.setBlockAndUpdate(p, Blocks.WATER.defaultBlockState());
                            }
                        }
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
                    });
            case NEON_BARRAGE -> {
                for (int i = 0; i < 5; i++) {
                    level.getEntitiesOfClass(LivingEntity.class, area5, e -> e != player)
                            .forEach(e -> e.hurt(level.damageSources().playerAttack(player), dmg * 0.35f));
                }
                spawnParticles(level, player, ParticleTypes.ELECTRIC_SPARK, 20);
            }
            case MECHANICAL_FRENZY -> player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1));
            case BLOOD_TIDE -> {
                hurtInArea(level, player, area6, dmg * 1.2f, e ->
                        e.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0)));
                spawnParticles(level, player, ParticleTypes.DAMAGE_INDICATOR, 16);
            }
            case GORGE_WIND -> level.getEntitiesOfClass(LivingEntity.class, area6, e -> e != player)
                    .forEach(e -> {
                        Vec3 push = e.position().subtract(player.position()).normalize().scale(2.5);
                        e.setDeltaMovement(push.x, 0.6, push.z);
                        e.hurt(level.damageSources().playerAttack(player), dmg * 0.6f);
                    });
            case DAWN_BURST -> hurtInArea(level, player, area6, dmg * 1.5f, e ->
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0)));
            case ROYAL_DECREE -> level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, area8, mob -> true)
                    .forEach(e -> {
                        e.setTarget(null);
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
                    });
            case SHADOW_STRIKE -> {
                Vec3 behind = player.getLookAngle().normalize().scale(-3);
                player.teleportTo(player.getX() + behind.x, player.getY(), player.getZ() + behind.z);
                hurtInArea(level, player, player.getBoundingBox().inflate(3), dmg * 1.3f, e -> {});
            }
            case SILENT_SHIELD -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 1));
            case TWILIGHT_SLASH -> {
                for (int i = 0; i < 3; i++) {
                    hurtInArea(level, player, area5, dmg * 0.5f, e -> {});
                }
                spawnParticles(level, player, ParticleTypes.SOUL, 10);
            }
            case TRADE_ROUTE -> {
                if (!player.getInventory().add(new ItemStack(ModItems.PURR_COIN.get(), 3))) {
                    player.drop(new ItemStack(ModItems.PURR_COIN.get(), 3), false);
                }
            }
            case TOWER_COLLAPSE -> level.getEntitiesOfClass(LivingEntity.class, area8, e -> e != player)
                    .forEach(e -> {
                        e.hurt(level.damageSources().playerAttack(player), dmg * 1.4f);
                        e.setDeltaMovement(0, -1.2, 0);
                    });
            case VILLAGE_BOND -> {
                if (level instanceof ServerLevel server) {
                    ModEntities.SAMURAI_CAT.get().spawn(server, player.blockPosition().east(), MobSpawnType.MOB_SUMMONED);
                    ModEntities.MONK_CAT.get().spawn(server, player.blockPosition().west(), MobSpawnType.MOB_SUMMONED);
                }
            }
            case STORM_CALL -> {
                level.getEntitiesOfClass(LivingEntity.class, area8, e -> e != player)
                        .forEach(e -> {
                            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
                            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                            e.hurt(level.damageSources().playerAttack(player), dmg * 0.4f);
                        });
                spawnParticles(level, player, ParticleTypes.FALLING_WATER, 24);
            }
        }
    }

    private static void hurtInArea(Level level, Player player, AABB box, float dmg,
            java.util.function.Consumer<LivingEntity> extra) {
        level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player).forEach(e -> {
            e.hurt(level.damageSources().playerAttack(player), dmg);
            extra.accept(e);
        });
    }

    private static void spawnParticles(Level level, Player player,
            net.minecraft.core.particles.ParticleOptions type, int count) {
        for (int i = 0; i < count; i++) {
            level.addParticle(type,
                    player.getX() + (level.random.nextDouble() - 0.5) * 2,
                    player.getY() + 1 + level.random.nextDouble(),
                    player.getZ() + (level.random.nextDouble() - 0.5) * 2,
                    0, 0, 0);
        }
    }

    private float getDamageValue() {
        return (float) (getTier().getAttackDamageBonus() + 4);
    }

    @Override
    public int getUseDuration(ItemStack stack) { return WeaponChargeHelper.USE_DURATION; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return WeaponChargeHelper.bowAnim(); }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.daikata.skill",
                Component.translatable("skill.cocojenna." + skill.name().toLowerCase()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.cocojenna.daikata.charge_hint")
                .withStyle(ChatFormatting.GRAY));
        WeaponAwakeningStage stage = WeaponData.getStage(stack);
        tooltip.add(Component.translatable("weapon.cocojenna.stage." + stage.key)
                .withStyle(stage == WeaponAwakeningStage.RESONANCE ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.AQUA));
        tooltip.add(Component.translatable("weapon.cocojenna.resonance", WeaponData.getResonance(stack))
                .withStyle(ChatFormatting.DARK_AQUA));
        int enhance = WeaponEnhanceHelper.getLevel(stack);
        if (enhance > 0) {
            tooltip.add(Component.translatable("forge.cocojenna.current_level", enhance)
                    .withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public Skill getSkill() { return skill; }
    public String getVariantId() { return variantId; }
}
