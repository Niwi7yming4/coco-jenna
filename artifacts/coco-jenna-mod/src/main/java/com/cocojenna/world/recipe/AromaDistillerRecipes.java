package com.cocojenna.world.recipe;

import com.cocojenna.init.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class AromaDistillerRecipes {

    private static final List<ProcessingRecipe> RECIPES = List.of(
            ProcessingRecipe.fueled(ModItems.SILVERVINE.get(), ModItems.MOTH_SCALE_POWDER.get(), 100, 80),
            ProcessingRecipe.fueled(ModItems.CATNIP_ITEM.get(), ModItems.SILVERVINE.get(), 80, 60),
            ProcessingRecipe.fueled(ModItems.NEON_MUSHROOM_ITEM.get(), ModItems.PURR_CRYSTAL.get(), 120, 100),
            ProcessingRecipe.fueled(ModItems.SPORE_FRUIT.get(), ModItems.SPORE_POWDER.get(), 90, 70),
            ProcessingRecipe.fueled(ModItems.COCO_FUR.get(), ModItems.JENNA_FUR.get(), 150, 120)
    );

    private AromaDistillerRecipes() {}

    public static Optional<ProcessingRecipe> find(ItemStack input, ItemStack fuel) {
        if (!fuel.isEmpty() && !ProcessingRecipe.isFuel(fuel)) return Optional.empty();
        return RECIPES.stream().filter(r -> r.matches(input, ItemStack.EMPTY)).findFirst();
    }
}
