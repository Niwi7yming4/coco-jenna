package com.cocojenna.endgame;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/** 建造藍圖 BOM — 從玩家背包扣除材料. */
public final class BuildingMaterialHelper {

    private BuildingMaterialHelper() {}

    public static boolean has(ServerPlayer player, String key, int count) {
        return player.getInventory().countItem(resolve(key)) >= count;
    }

    public static boolean consume(ServerPlayer player, String key, int count) {
        Item item = resolve(key);
        if (player.getInventory().countItem(item) < count) return false;
        int left = count;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(item)) continue;
            int take = Math.min(left, stack.getCount());
            stack.shrink(take);
            left -= take;
        }
        return left == 0;
    }

    public static Item resolve(String key) {
        return switch (key) {
            case "velvet_planks" -> ModBlocks.VELVET_PLANKS.get().asItem();
            case "woven_wool" -> ModBlocks.WOVEN_WOOL.get().asItem();
            case "stardust_brick" -> ModBlocks.STARDUST_BRICK.get().asItem();
            case "velvet_block" -> ModBlocks.VELVET_BLOCK.get().asItem();
            case "velvet_grass" -> ModBlocks.VELVET_GRASS.get().asItem();
            case "moonstone_brick" -> ModBlocks.MOONSTONE_BRICK.get().asItem();
            case "cat_scratch_board" -> ModBlocks.CAT_SCRATCH_BOARD.get().asItem();
            case "stardust_soil" -> ModBlocks.STARDUST_SOIL.get().asItem();
            case "velvet_fur" -> ModItems.VELVET_FUR.get();
            case "purr_crystal" -> ModItems.PURR_CRYSTAL.get();
            case "precision_gear" -> ModItems.PRECISION_GEAR.get();
            case "memory_shard" -> ModItems.MEMORY_SHARD.get();
            case "moonstone" -> ModItems.MOONSTONE.get();
            case "hibiscus_flower" -> ModItems.HIBISCUS_FLOWER_ITEM.get();
            case "catnip" -> ModItems.CATNIP_ITEM.get();
            case "neon_mushroom" -> ModItems.NEON_MUSHROOM_ITEM.get();
            case "spore_fruit" -> ModItems.SPORE_FRUIT.get();
            case "salt_crystal" -> ModBlocks.SALT_CRYSTAL.get().asItem();
            case "iron_ingot" -> Items.IRON_INGOT;
            case "glass" -> Items.GLASS;
            case "oak_planks" -> Items.OAK_PLANKS;
            case "book" -> Items.BOOK;
            case "redstone" -> Items.REDSTONE;
            default -> {
                var item = ForgeRegistries.ITEMS.getValue(
                        new net.minecraft.resources.ResourceLocation("cocojenna", key));
                yield item != null ? item : Items.BARRIER;
            }
        };
    }
}
