package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.FestivalEventManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class CookingManager {

    private CookingManager() {}

    public static boolean cook(ServerPlayer player, int recipeIndex) {
        CookingRecipeRegistry.Recipe recipe = CookingRecipeRegistry.get(recipeIndex);
        if (recipe == null) return false;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!recipe.isUnlocked(bond)) {
            player.displayClientMessage(Component.translatable("cooking.cocojenna.locked"), true);
            return false;
        }
        if (FestivalEventManager.isCookingContest(bond)) {
            return submitContest(player, recipeIndex);
        }
        if (!consumeIngredients(player, recipe)) return false;
        grantOutput(player, recipe);
        applyCookRewards(player, bond, recipe);
        player.displayClientMessage(Component.translatable("cooking.cocojenna.done", recipe.name()), true);
        return true;
    }

    public static boolean submitContest(ServerPlayer player, int recipeIndex) {
        CookingRecipeRegistry.Recipe recipe = CookingRecipeRegistry.get(recipeIndex);
        if (recipe == null) return false;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!FestivalEventManager.isCookingContest(bond)) return false;
        if (bond.isFestivalContestSubmitted()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.contest.already"), true);
            return false;
        }
        if (!consumeIngredients(player, recipe)) return false;
        grantOutput(player, recipe);
        int score = computeContestScore(bond, recipe);
        FestivalEventManager.onContestSubmit(player, score);
        return true;
    }

    private static int computeContestScore(BondData bond, CookingRecipeRegistry.Recipe recipe) {
        int score = 40 + recipe.preferenceStars() * 10;
        score += Math.min(20, bond.getKingdomHappiness() / 5);
        score += Math.min(15, countChefCats(bond) * 5);
        if ("festival_moon_cake".equals(recipe.id()) || "taste_of_memory".equals(recipe.id())) {
            score += 10;
        }
        return Math.min(100, score);
    }

    private static int countChefCats(BondData bond) {
        int n = 0;
        for (var p : com.cocojenna.endgame.kingdom.TownNpcProfile.ALL) {
            if ("CHEF".equals(bond.getTownNpcJob(p.id()))) n++;
        }
        return n;
    }

    private static boolean consumeIngredients(ServerPlayer player, CookingRecipeRegistry.Recipe recipe) {
        Inventory inv = player.getInventory();
        for (Map.Entry<Item, Integer> e : recipe.ingredients().entrySet()) {
            if (countItem(inv, e.getKey()) < e.getValue()) {
                player.displayClientMessage(Component.translatable("cooking.cocojenna.missing"), true);
                return false;
            }
        }
        for (Map.Entry<Item, Integer> e : recipe.ingredients().entrySet()) {
            removeItems(inv, e.getKey(), e.getValue());
        }
        return true;
    }

    private static void grantOutput(ServerPlayer player, CookingRecipeRegistry.Recipe recipe) {
        if (!player.getInventory().add(recipe.output().copy())) {
            player.drop(recipe.output().copy(), false);
        }
    }

    private static void applyCookRewards(ServerPlayer player, BondData bond, CookingRecipeRegistry.Recipe recipe) {
        bond.modifyCocoEmotion(0.3f * recipe.preferenceStars());
        bond.modifyJennaEmotion(0.3f * recipe.preferenceStars());
        bond.addKingdomHappiness(1);
    }

    private static int countItem(Inventory inv, Item item) {
        int n = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) n += s.getCount();
        }
        return n;
    }

    private static void removeItems(Inventory inv, Item item, int amount) {
        int left = amount;
        for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (!s.is(item)) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }
}
