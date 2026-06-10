package com.cocojenna.overworld;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** 融合建築程序化生成. */
public final class FusionBuildingGenerator {

    private FusionBuildingGenerator() {}

    public static void build(ServerLevel level, BlockPos center, FusionBuildingType type) {
        switch (type) {
            case EMBASSY -> buildEmbassy(level, center);
            case TWIN_STATUE -> buildTwinStatue(level, center);
            case CATNIP_EXCHANGE -> buildCatnipExchange(level, center);
        }
    }

    private static void buildEmbassy(ServerLevel level, BlockPos c) {
        BlockState stone = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState plank = Blocks.SPRUCE_PLANKS.defaultBlockState();
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                level.setBlock(c.offset(dx, 0, dz), stone, 2);
            }
        }
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    boolean wall = Math.abs(dx) == 5 || Math.abs(dz) == 5;
                    if (wall) level.setBlock(c.offset(dx, dy, dz), plank, 2);
                }
            }
        }
        level.setBlock(c.offset(0, 1, 0), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 2);
        level.setBlock(c.offset(3, 1, 0), Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, c.offset(3, 1, 0), new ItemStack[]{
                new ItemStack(ModItems.CATNIP_ITEM.get(), 8),
                new ItemStack(ModItems.MOONSTONE.get(), 2),
                new ItemStack(Items.EMERALD, 4)
        });
        spawnDiplomat(level, c.offset(-2, 1, 2));
    }

    private static void buildTwinStatue(ServerLevel level, BlockPos c) {
        BlockState gold = Blocks.GOLD_BLOCK.defaultBlockState();
        BlockState quartz = Blocks.QUARTZ_BLOCK.defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(c.offset(dx, 0, dz), gold, 2);
            }
        }
        level.setBlock(c.offset(-1, 1, 0), Blocks.BLACK_WOOL.defaultBlockState(), 2);
        level.setBlock(c.offset(1, 1, 0), Blocks.ORANGE_WOOL.defaultBlockState(), 2);
        level.setBlock(c.offset(-1, 2, 0), quartz, 2);
        level.setBlock(c.offset(1, 2, 0), quartz, 2);
        level.setBlock(c, Blocks.SEA_LANTERN.defaultBlockState(), 2);
    }

    private static void buildCatnipExchange(ServerLevel level, BlockPos c) {
        BlockState oak = Blocks.OAK_PLANKS.defaultBlockState();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(c.offset(dx, 0, dz), oak, 2);
            }
        }
        for (int dy = 1; dy <= 2; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    boolean wall = Math.abs(dx) == 3 || Math.abs(dz) == 3;
                    if (wall) level.setBlock(c.offset(dx, dy, dz), oak, 2);
                }
            }
        }
        level.setBlock(c, Blocks.BARREL.defaultBlockState(), 2);
        level.setBlock(c.offset(2, 1, 0), Blocks.CHEST.defaultBlockState(), 2);
        fillChest(level, c.offset(2, 1, 0), new ItemStack[]{
                new ItemStack(ModItems.CATNIP_ITEM.get(), 16),
                new ItemStack(Items.EMERALD, 8)
        });
        spawnMerchant(level, c.offset(-1, 1, 1));
    }

    private static void spawnDiplomat(ServerLevel level, BlockPos pos) {
        var npc = ModEntities.OVERWORLD_CAT.get().create(level);
        if (npc == null) return;
        npc.setRole(OverworldCatNpcEntity.Role.SCOUT);
        npc.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        npc.setCustomName(net.minecraft.network.chat.Component.translatable("fusion.cocojenna.diplomat"));
        level.addFreshEntity(npc);
    }

    private static void spawnMerchant(ServerLevel level, BlockPos pos) {
        var npc = ModEntities.OVERWORLD_CAT.get().create(level);
        if (npc == null) return;
        npc.setRole(OverworldCatNpcEntity.Role.SMUGGLER);
        npc.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        npc.setCustomName(net.minecraft.network.chat.Component.translatable("fusion.cocojenna.merchant"));
        level.addFreshEntity(npc);
    }

    private static void fillChest(ServerLevel level, BlockPos pos, ItemStack[] loot) {
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) return;
        int slot = 0;
        for (ItemStack stack : loot) {
            if (!stack.isEmpty()) chest.setItem(slot++, stack);
        }
    }
}
