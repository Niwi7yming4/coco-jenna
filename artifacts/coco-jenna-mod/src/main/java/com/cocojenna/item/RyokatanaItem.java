package com.cocojenna.item;

import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.weapon.WeaponAwakeningStage;
import com.cocojenna.weapon.WeaponData;
import com.cocojenna.weapon.WeaponSkillHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 良快刀 (Ryokatana) — 貓之國傳說武器，共 50 把。
 * 每把有被動特效 + 右鍵蓄力主動技。
 */
public class RyokatanaItem extends SwordItem {

    private static final int SKILL_COOLDOWN = 50;

    private final String variantId;

    public RyokatanaItem(Tier tier, int dmgBonus, float atkSpeed, Properties props, String variantId) {
        super(tier, dmgBonus, atkSpeed, props.rarity(Rarity.RARE));
        this.variantId = variantId;
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
        int charge = WeaponChargeHelper.chargeTicks(stack, timeLeft);
        int minCharge = WeaponSkillHelper.minChargeTicks(variantId, WeaponChargeHelper.MIN_CHARGE_TICKS);
        if (charge < minCharge) {
            WeaponChargeHelper.notifyChargeTooShort(player);
            return;
        }
        WeaponAwakeningStage stage = WeaponData.getStage(stack);
        if (!stage.hasActiveSkill()) {
            if (!level.isClientSide) {
                player.displayClientMessage(net.minecraft.network.chat.Component
                        .translatable("weapon.cocojenna.skill_locked")
                        .withStyle(net.minecraft.ChatFormatting.GRAY), true);
            }
            return;
        }
        int cooldown = WeaponSkillHelper.cooldownTicks(variantId, stack, SKILL_COOLDOWN);
        if (!WeaponChargeHelper.tryConsumeCooldown(player, this, cooldown)) return;

        if (!level.isClientSide
                && RyokatanaEffectHelper.castSkill(player, level, variantId, charge, stack)) {
            WeaponChargeHelper.applyCooldown(player, this, cooldown);
            WeaponChargeHelper.playReleaseSound(level, player);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        WeaponChargeHelper.tickChargeFeedback(entity, stack,
                WeaponChargeHelper.chargeTicks(stack, remaining));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return WeaponChargeHelper.USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return WeaponChargeHelper.bowAnim();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.ryokatana." + variantId + ".effect")
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.cocojenna.ryokatana.active_hint")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.cocojenna.ryokatana.lore")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        int enhance = WeaponEnhanceHelper.getLevel(stack);
        if (enhance > 0) {
            tooltip.add(Component.translatable("forge.cocojenna.current_level", enhance)
                    .withStyle(ChatFormatting.GOLD));
        }
        WeaponAwakeningStage stage = WeaponData.getStage(stack);
        tooltip.add(Component.translatable("weapon.cocojenna.stage." + stage.key)
                .withStyle(stage == WeaponAwakeningStage.RESONANCE ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.AQUA));
        tooltip.add(Component.translatable("weapon.cocojenna.resonance", WeaponData.getResonance(stack))
                .withStyle(ChatFormatting.DARK_AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public String getVariantId() { return variantId; }
}
