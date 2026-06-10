package com.cocojenna.world.recipe;

import com.cocojenna.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public final class DistillerRecipes {

    private static final List<ProcessingRecipe> RECIPES = List.of(
            ProcessingRecipe.of(ModItems.BLACK_MUD_REMNANT.get(), ModItems.PURE_TEAR.get(), 160),
            ProcessingRecipe.of(ModItems.HIBISCUS_FLOWER_ITEM.get(), ModItems.HIBISCUS_TEAR.get(), 120),
            ProcessingRecipe.of(ModItems.HIBISCUS_FLOWER_ITEM.get(), ModItems.GLASS_VIAL.get(),
                    ModItems.HIBISCUS_TEAR.get(), 100),
            ProcessingRecipe.of(ModItems.BLIND_WATER_GEL.get(), ModItems.BLIND_WATER_SAMPLE.get(), 140),
            ProcessingRecipe.of(ModItems.MEMORY_PARTICLE.get(), ModItems.GLASS_VIAL.get(),
                    ModItems.MEMORY_SHARD.get(), 200),
            ProcessingRecipe.of(ModItems.SHADOW_CRYSTAL.get(), ModItems.CHAOS_CRYSTAL.get(), 180)
    );

    private DistillerRecipes() {}

    public static Optional<ProcessingRecipe> find(ItemStack input, ItemStack catalyst) {
        return RECIPES.stream().filter(r -> r.matches(input, catalyst)).findFirst();
    }
}
