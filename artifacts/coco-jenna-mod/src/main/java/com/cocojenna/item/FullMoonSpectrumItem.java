package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 滿月光譜 — 僅在滿月夜可採集，合成「滿月祭壇」和高級蒸餾材料必需。
 * 發出微弱輝光（視覺效果）。
 */
public class FullMoonSpectrumItem extends Item {

    public FullMoonSpectrumItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.full_moon_spectrum.tooltip")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("item.cocojenna.full_moon_spectrum.condition")
                .withStyle(ChatFormatting.GRAY));
    }
}
