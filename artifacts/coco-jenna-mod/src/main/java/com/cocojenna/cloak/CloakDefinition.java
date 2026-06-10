package com.cocojenna.cloak;

import com.cocojenna.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public enum CloakDefinition {

    ANTI_CORROSION("cloak_anti_corrosion", 500, mats(
            stack(ModItems.BLACK_MUD_REMNANT.get(), 10), stack(ModItems.VELVET_FUR.get(), 5))),
    MOONLIGHT("cloak_moonlight", 400, mats(
            stack(ModItems.MOONSTONE.get(), 10), stack(ModItems.VELVET_FUR.get(), 5))),
    MEMORY("cloak_memory", 350, mats(
            stack(ModItems.MEMORY_SHARD.get(), 20), stack(ModItems.VELVET_FUR.get(), 5))),
    GUARDIAN("cloak_guardian", 600, mats(
            stack(ModItems.PURR_CRYSTAL.get(), 3), stack(ModItems.VELVET_FUR.get(), 5))),
    WARM("cloak_warm", 400, mats(
            stack(Items.WHITE_WOOL, 10), stack(ModItems.VELVET_FUR.get(), 20))),
    TRAVELER("cloak_traveler", 300, mats(
            stack(Items.LEATHER, 5), stack(ModItems.VELVET_FUR.get(), 10))),
    THUNDER("cloak_thunder", 550, mats(
            stack(ModItems.VELVET_FUR.get(), 10), stack(ModItems.PURR_CRYSTAL.get(), 2))),
    HIBISCUS("cloak_hibiscus", 400, mats(
            stack(ModItems.HIBISCUS_FLOWER_ITEM.get(), 10),
            stack(ModItems.HIBISCUS_TEAR.get(), 1),
            stack(ModItems.VELVET_FUR.get(), 15))),
    PURR("cloak_purr", 700, mats(
            stack(ModItems.PURR_CRYSTAL.get(), 10), stack(ModItems.VELVET_FUR.get(), 20))),
    ETERNAL("cloak_eternal", 9999, mats(
            stack(ModItems.VELVET_FUR.get(), 8), stack(ModItems.STARDUST_SOIL_ITEM.get(), 4),
            stack(ModItems.PURR_CRYSTAL.get(), 5), stack(Items.DANDELION, 3)));

    public final String itemId;
    public final int durability;
    public final ItemStack[] cost;

    CloakDefinition(String itemId, int durability, ItemStack[] cost) {
        this.itemId = itemId;
        this.durability = durability;
        this.cost = cost;
    }

    private static ItemStack[] mats(ItemStack... stacks) { return stacks; }
    private static ItemStack stack(Item item, int count) { return new ItemStack(item, count); }

    public static Optional<CloakDefinition> byIndex(int idx) {
        var vals = values();
        if (idx < 0 || idx >= vals.length) return Optional.empty();
        return Optional.of(vals[idx]);
    }

    public static List<CloakDefinition> basicAndAdvanced() {
        return List.of(values());
    }

    public boolean canCraft(net.minecraft.server.level.ServerPlayer player) {
        for (ItemStack need : cost) {
            if (countItem(player, need.getItem()) < need.getCount()) return false;
        }
        return true;
    }

    public ItemStack craft(net.minecraft.server.level.ServerPlayer player) {
        if (!canCraft(player)) return ItemStack.EMPTY;
        for (ItemStack need : cost) {
            removeItem(player, need.getItem(), need.getCount());
        }
        Item result = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cocojenna", itemId));
        if (result == null) result = ModItems.VELVET_TAIL_CAPE.get();
        return craftWithoutCost();
    }

    public ItemStack craftWithoutCost() {
        Item result = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cocojenna", itemId));
        if (result == null) result = ModItems.VELVET_TAIL_CAPE.get();
        ItemStack stack = new ItemStack(result);
        stack.getOrCreateTag().putInt("CloakDurability", durability);
        stack.getOrCreateTag().putInt("CloakMaxDurability", durability);
        stack.getOrCreateTag().putString("CloakId", itemId);
        return stack;
    }

    private static int countItem(net.minecraft.server.level.ServerPlayer player, Item item) {
        int t = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == item) t += s.getCount();
        }
        return t;
    }

    private static void removeItem(net.minecraft.server.level.ServerPlayer player, Item item, int n) {
        int left = n;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.getItem() != item) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }
}
