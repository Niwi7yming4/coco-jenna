package com.cocojenna.endgame;

import com.cocojenna.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

/** 特級貓飯主廚 — 烹飪配方（雨後擴充 + 季節解鎖）. */
public final class CookingRecipeRegistry {

    public record Recipe(
            String id,
            ItemStack output,
            Map<Item, Integer> ingredients,
            int preferenceStars,
            int season,
            boolean starter
    ) {
        public net.minecraft.network.chat.Component name() {
            return net.minecraft.network.chat.Component.translatable("food.cocojenna." + id);
        }

        public boolean isUnlocked(com.cocojenna.capability.BondData bond) {
            return starter || bond.hasCookingRecipe(id);
        }
    }

    /** -1 = 全年；0 春 1 夏 2 秋 3 冬 */
    private static final List<Recipe> RECIPES = new ArrayList<>();

    static {
        add("rainbow_dandelion_salad", ModItems.HIBISCUS_SASHIMI.get(), map(
                ModItems.SPORE_FRUIT.get(), 2, Items.DANDELION, 1, ModItems.CATNIP_ITEM.get(), 1), 4, -1, true);
        add("full_moon_fish_jelly", ModItems.GLOW_FISH_SOUP.get(), map(
                Items.COD, 2, ModItems.MOONSTONE.get(), 1, Items.SLIME_BALL, 1), 5, -1, true);
        add("stardust_catnip_tea", ModItems.SILVERVINE_BISCUIT.get(), map(
                ModItems.CATNIP_ITEM.get(), 3, ModItems.MOONSTONE.get(), 1, Items.GLASS_BOTTLE, 1), 3, -1, true);
        add("meat_puree_tower", ModItems.COCO_SPECIAL_MEAL.get(), map(
                ModItems.BASIC_FISH_PUREE.get(), 4, ModItems.PREMIUM_FISH_CAN.get(), 1,
                ModItems.VELVET_FUR.get(), 2), 5, -1, true);
        add("dawn_dew_soup", ModItems.GLOW_FISH_SOUP.get(), map(
                Items.GLASS_BOTTLE, 1, ModItems.HIBISCUS_TEAR.get(), 1, Items.KELP, 2), 4, -1, true);
        add("gear_town_set", ModItems.CRAB_DELUXE.get(), map(
                Items.IRON_INGOT, 2, ModItems.BASIC_FISH_PUREE.get(), 2, ModItems.MOONSTONE.get(), 1), 4, -1, true);
        add("blind_port_seafood", ModItems.DEEP_SEA_RISOTTO.get(), map(
                Items.SALMON, 2, ModItems.SALT.get(), 1, Items.KELP, 3), 5, -1, true);
        add("festival_moon_cake", ModItems.TWIN_STAR_MEAL.get(), map(
                Items.WHEAT, 3, ModItems.MOONSTONE.get(), 2, ModItems.HIBISCUS_TEAR.get(), 1), 5, -1, true);
        add("legendary_can", ModItems.PREMIUM_FISH_CAN.get(), map(
                Items.COD, 3, ModItems.PURR_CRYSTAL.get(), 1, Items.GOLD_INGOT, 1), 5, -1, true);
        add("taste_of_memory", ModItems.JENNA_SPECIAL_MEAL.get(), map(
                ModItems.MEMORY_SHARD.get(), 1, ModItems.BASIC_FISH_PUREE.get(), 2,
                ModItems.CATNIP_ITEM.get(), 2), 5, -1, true);
        add("spring_blossom_parfait", ModItems.HIBISCUS_SASHIMI.get(), map(
                ModItems.HIBISCUS_FLOWER_ITEM.get(), 4, Items.EGG, 2, ModItems.VELVET_FUR.get(), 2), 4, 0, false);
        add("summer_sun_sorbet", ModItems.GLOW_FISH_SOUP.get(), map(
                ModItems.CATNIP_ITEM.get(), 4, Items.SNOWBALL, 3, ModItems.MOONSTONE.get(), 1), 4, 1, false);
        add("autumn_harvest_stew", ModItems.CRAB_DELUXE.get(), map(
                ModItems.BASIC_FISH_PUREE.get(), 3, Items.PUMPKIN, 2, ModItems.SPORE_FRUIT.get(), 2), 5, 2, false);
        add("winter_warm_broth", ModItems.GLOW_FISH_SOUP.get(), map(
                ModItems.PURR_CRYSTAL.get(), 2, Items.BEETROOT, 3, ModItems.VELVET_FUR.get(), 3), 5, 3, false);
    }

    private static Map<Item, Integer> map(Object... kv) {
        Map<Item, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((Item) kv[i], (Integer) kv[i + 1]);
        }
        return m;
    }

    private static void add(String id, Item output, Map<Item, Integer> ing, int stars, int season, boolean starter) {
        RECIPES.add(new Recipe(id, new ItemStack(output), ing, stars, season, starter));
    }

    public static List<Recipe> all() { return List.copyOf(RECIPES); }

    public static List<Recipe> unlockedFor(com.cocojenna.capability.BondData bond) {
        List<Recipe> out = new ArrayList<>();
        for (Recipe r : RECIPES) {
            if (r.isUnlocked(bond)) out.add(r);
        }
        return out;
    }

    public static Recipe get(int index) {
        if (index < 0 || index >= RECIPES.size()) return null;
        return RECIPES.get(index);
    }

    public static Recipe byId(String id) {
        for (Recipe r : RECIPES) {
            if (r.id().equals(id)) return r;
        }
        return null;
    }

    public static void unlockSeasonal(com.cocojenna.capability.BondData bond, int season) {
        for (Recipe r : RECIPES) {
            if (r.season() == season && !r.starter()) {
                bond.unlockCookingRecipe(r.id());
            }
        }
    }

    private CookingRecipeRegistry() {}
}
