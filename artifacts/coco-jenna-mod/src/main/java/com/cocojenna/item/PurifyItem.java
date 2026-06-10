package com.cocojenna.item;

import com.cocojenna.init.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * 淨化道具（朱槿花之淚、純淨治療液）
 * 除黑泥效果，並治療 HP。
 */
public class PurifyItem extends Item {

    private final int healAmount;

    public PurifyItem(Properties props, int healAmount) {
        super(props);
        this.healAmount = healAmount;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            player.removeEffect(ModEffects.BLACK_MUD_STAGE1.get());
            player.removeEffect(ModEffects.BLACK_MUD_STAGE2.get());
            player.removeEffect(ModEffects.BLACK_MUD_STAGE3.get());
            player.removeEffect(ModEffects.BLACK_MUD_STAGE4.get());
            player.removeEffect(ModEffects.CORROSION_MARK.get());
            player.getPersistentData().putInt("cocojenna_blackmud_stage", 0);
            player.heal(healAmount * 2.0f);
        }
        stack.shrink(1);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) { return 24; }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
}
