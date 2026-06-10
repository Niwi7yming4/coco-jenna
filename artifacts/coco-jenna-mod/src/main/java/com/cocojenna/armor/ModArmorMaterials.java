package com.cocojenna.armor;

import com.cocojenna.init.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** 貓咪主題防具材質（設計書第二章）. */
public enum ModArmorMaterials implements ArmorMaterial {
    VELVET_BEGINNER("velvet_beginner", 12, new int[]{1, 4, 3, 1}, 20,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f,
            () -> Ingredient.of(ModItems.VELVET_FUR.get())),
    MOONLIGHT("moonlight", 18, new int[]{2, 5, 4, 2}, 22,
            SoundEvents.ARMOR_EQUIP_CHAIN, 1f, 0f,
            () -> Ingredient.of(ModItems.MOONSTONE.get()));

    private static final int[] DURABILITY = {13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protection;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockback;
    private final java.util.function.Supplier<Ingredient> repair;

    ModArmorMaterials(String name, int durMult, int[] prot, int ench, SoundEvent sound,
            float tough, float kb, java.util.function.Supplier<Ingredient> repair) {
        this.name = name;
        this.durabilityMultiplier = durMult;
        this.protection = prot;
        this.enchantability = ench;
        this.equipSound = sound;
        this.toughness = tough;
        this.knockback = kb;
        this.repair = repair;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()] * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return protection[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() { return enchantability; }

    @Override
    public SoundEvent getEquipSound() { return equipSound; }

    @Override
    public Ingredient getRepairIngredient() { return repair.get(); }

    @Override
    public String getName() { return "cocojenna:" + name; }

    @Override
    public float getToughness() { return toughness; }

    @Override
    public float getKnockbackResistance() { return knockback; }
}
