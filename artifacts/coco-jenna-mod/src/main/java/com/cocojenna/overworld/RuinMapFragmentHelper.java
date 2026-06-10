package com.cocojenna.overworld;

import com.cocojenna.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** 地圖碎片 NBT 與合成輔助. */
public final class RuinMapFragmentHelper {

    public static final String TAG_TYPE = "RuinFragmentType";

    private RuinMapFragmentHelper() {}

    public static ItemStack typedStack(RuinMapFragmentType type, int count) {
        ItemStack stack = new ItemStack(ModItems.MAP_FRAGMENT.get(), count);
        setType(stack, type);
        return stack;
    }

    public static void setType(ItemStack stack, RuinMapFragmentType type) {
        stack.getOrCreateTag().putString(TAG_TYPE, type.name());
    }

    public static RuinMapFragmentType getType(ItemStack stack) {
        if (!stack.is(ModItems.MAP_FRAGMENT.get())) return RuinMapFragmentType.WAR_RUIN;
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_TYPE)) return RuinMapFragmentType.WAR_RUIN;
        try {
            return RuinMapFragmentType.valueOf(tag.getString(TAG_TYPE));
        } catch (IllegalArgumentException e) {
            return RuinMapFragmentType.WAR_RUIN;
        }
    }

    public static boolean sameType(ItemStack a, ItemStack b) {
        return getType(a) == getType(b);
    }

    public static Component typeName(RuinMapFragmentType type) {
        return Component.translatable("penetration.cocojenna.map_fragment." + type.name().toLowerCase());
    }
}
