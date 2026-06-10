package com.cocojenna.item;

import com.cocojenna.economy.CatnipQuality;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** 帶品質的貓薄荷物品. */
public class CatnipItem extends Item {

    public static final String TAG_QUALITY = "CatnipQuality";

    public CatnipItem(Properties props) {
        super(props);
    }

    public static ItemStack createStack(CatnipQuality quality, int count) {
        ItemStack stack = new ItemStack(com.cocojenna.init.ModItems.CATNIP_ITEM.get(), count);
        setQuality(stack, quality);
        return stack;
    }

    public static CatnipQuality getQuality(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_QUALITY)) {
            return CatnipQuality.fromOrdinal(tag.getInt(TAG_QUALITY));
        }
        return CatnipQuality.COMMON;
    }

    public static void setQuality(ItemStack stack, CatnipQuality quality) {
        stack.getOrCreateTag().putInt(TAG_QUALITY, quality.ordinal());
    }

    public static boolean isQuality(ItemStack stack, CatnipQuality quality) {
        return stack.getItem() instanceof CatnipItem && getQuality(stack) == quality;
    }

    public static String qualityKey(CatnipQuality q) {
        return "economy.cocojenna.catnip.quality." + q.name().toLowerCase();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getQuality(stack) == CatnipQuality.LEGENDARY;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CatnipQuality q = getQuality(stack);
        ChatFormatting fmt = q == CatnipQuality.LEGENDARY ? ChatFormatting.GOLD
                : q == CatnipQuality.RARE ? ChatFormatting.AQUA : ChatFormatting.GRAY;
        tooltip.add(Component.translatable(qualityKey(q)).withStyle(fmt));
    }
}
