package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.List;

/** 珍奶的舊鈴鐺 — 不可複製、不可附魔、帶有故事性描述。 */
public class UniqueItem extends Item {

    public UniqueItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.jennas_old_bell.lore1")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.cocojenna.jennas_old_bell.lore2")
                .withStyle(ChatFormatting.GRAY));
    }
}
