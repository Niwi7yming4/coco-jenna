package com.cocojenna.world.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record ProcessingRecipe(Ingredient input, Optional<Ingredient> catalyst,
                               ItemStack result, int cookTime, int fuelCost) {

    public static ProcessingRecipe of(Item input, Item result, int cookTime) {
        return new ProcessingRecipe(Ingredient.of(input), Optional.empty(),
                new ItemStack(result), cookTime, 0);
    }

    public static ProcessingRecipe of(Item input, Item catalyst, Item result, int cookTime) {
        return new ProcessingRecipe(Ingredient.of(input), Optional.of(Ingredient.of(catalyst)),
                new ItemStack(result), cookTime, 0);
    }

    public static ProcessingRecipe fueled(Item input, Item result, int cookTime, int fuelCost) {
        return new ProcessingRecipe(Ingredient.of(input), Optional.empty(),
                new ItemStack(result), cookTime, fuelCost);
    }

    public boolean matches(ItemStack inputStack, ItemStack catalystStack) {
        if (!input.test(inputStack)) return false;
        if (catalyst.isEmpty()) return true;
        return catalyst.get().test(catalystStack);
    }

    public static boolean isFuel(ItemStack stack) {
        return net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.isFuel(stack)
                || stack.is(Items.BLAZE_POWDER);
    }
}
