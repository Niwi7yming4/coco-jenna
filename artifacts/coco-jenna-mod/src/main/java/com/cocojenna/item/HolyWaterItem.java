package com.cocojenna.item;

import com.cocojenna.init.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * 聖水 — 移除黑泥寄生效果，也可用來復活封印物。
 */
public class HolyWaterItem extends Item {

    public HolyWaterItem(Properties props) {
        super(props);
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
            if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                com.cocojenna.blackmud.BlackMudCorruptionManager.purifyRegion(
                        sp.serverLevel(), player.blockPosition(), 8, sp);
            }
        }
        stack.shrink(1);
        return stack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) { return 32; }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
}
