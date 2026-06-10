package com.cocojenna.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** 武器右鍵蓄力共用邏輯（對空氣／方塊皆可蓄力）. */
public final class WeaponChargeHelper {

    public static final int MIN_CHARGE_TICKS = 16;
    public static final int USE_DURATION = 72000;

    private WeaponChargeHelper() {}

    public static InteractionResultHolder<ItemStack> startCharge(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    public static InteractionResult useOnBlock(UseOnContext ctx, Item item) {
        Player player = ctx.getPlayer();
        if (player == null || player.getItemInHand(ctx.getHand()).getItem() != item) {
            return InteractionResult.PASS;
        }
        InteractionResultHolder<ItemStack> result = startCharge(ctx.getLevel(), player, ctx.getHand());
        return result.getResult();
    }

    public static int chargeTicks(ItemStack stack, int timeLeft) {
        return USE_DURATION - timeLeft;
    }

    public static boolean chargeReady(int chargeTicks) {
        return chargeTicks >= MIN_CHARGE_TICKS;
    }

    public static void tickChargeFeedback(LivingEntity entity, ItemStack stack, int chargeTicks) {
        if (!(entity instanceof Player player)) return;
        String variant = com.cocojenna.weapon.WeaponData.variantId(stack);
        var defOpt = com.cocojenna.weapon.WeaponSkillRegistry.get(variant);
        int min = defOpt.map(d -> d.chargeProfile().minTicks()).orElse(MIN_CHARGE_TICKS);
        if (chargeTicks < min) return;
        if (!player.level().isClientSide) {
            defOpt.ifPresent(def -> com.cocojenna.weapon.WeaponSkillVfx.onCharging(player, def, chargeTicks));
            return;
        }
        if (chargeTicks % 4 == 0) {
            player.level().addParticle(ParticleTypes.ENCHANT,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    (player.level().random.nextDouble() - 0.5) * 0.4, 0.1,
                    (player.level().random.nextDouble() - 0.5) * 0.4);
        }
    }

    public static boolean tryConsumeCooldown(Player player, Item item, int cooldownTicks) {
        if (!player.getCooldowns().isOnCooldown(item)) return true;
        if (player instanceof ServerPlayer sp) {
            sp.displayClientMessage(Component.translatable("weapon.cocojenna.skill_cooldown"), true);
        }
        return false;
    }

    public static void applyCooldown(Player player, Item item, int cooldownTicks) {
        player.getCooldowns().addCooldown(item, cooldownTicks);
    }

    public static void playReleaseSound(Level level, Player player) {
        level.playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.7f + level.random.nextFloat() * 0.2f);
    }

    public static void notifyChargeTooShort(Player player) {
        if (player instanceof ServerPlayer sp) {
            sp.displayClientMessage(Component.translatable("weapon.cocojenna.charge_too_short"), true);
        }
    }

    public static UseAnim bowAnim() {
        return UseAnim.BOW;
    }
}
