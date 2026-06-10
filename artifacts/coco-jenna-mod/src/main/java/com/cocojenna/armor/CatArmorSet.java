package com.cocojenna.armor;

import com.cocojenna.init.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 全套防具效果（設計書 §2.2）. */
public enum CatArmorSet {
    VELVET_BEGINNER(
            ModItems.VELVET_BEGINNER_HELMET.get(),
            ModItems.VELVET_BEGINNER_CHESTPLATE.get(),
            ModItems.VELVET_BEGINNER_LEGGINGS.get(),
            ModItems.VELVET_BEGINNER_BOOTS.get()),
    MOONLIGHT(
            ModItems.MOONLIGHT_HELMET.get(),
            ModItems.MOONLIGHT_CHESTPLATE.get(),
            ModItems.MOONLIGHT_LEGGINGS.get(),
            ModItems.MOONLIGHT_BOOTS.get());

    private final Item helmet;
    private final Item chest;
    private final Item legs;
    private final Item boots;

    CatArmorSet(Item helmet, Item chest, Item legs, Item boots) {
        this.helmet = helmet;
        this.chest = chest;
        this.legs = legs;
        this.boots = boots;
    }

    public boolean isWearingFullSet(Player player) {
        return matches(player.getItemBySlot(EquipmentSlot.HEAD), helmet)
                && matches(player.getItemBySlot(EquipmentSlot.CHEST), chest)
                && matches(player.getItemBySlot(EquipmentSlot.LEGS), legs)
                && matches(player.getItemBySlot(EquipmentSlot.FEET), boots);
    }

    private static boolean matches(ItemStack stack, Item expected) {
        return !stack.isEmpty() && stack.getItem() == expected;
    }
}
