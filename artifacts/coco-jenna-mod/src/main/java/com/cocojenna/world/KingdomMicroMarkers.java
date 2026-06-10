package com.cocojenna.world;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 貓之國維度微型探索點（設計書 §3.2）：貓爪印、毛線、玩具鼠、魚骨、貓薄荷殘渣、呼嚕石.
 */
public final class KingdomMicroMarkers {

    /** ~100 格間距：每 chunk 約 16% 機率放置 1–2 個微 POI. */
    private static final int MARKER_CHANCE = 16;

    private KingdomMicroMarkers() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;

        long seed = level.getSeed() ^ chunk.getPos().toLong();
        RandomSource random = RandomSource.create(seed);
        if (random.nextInt(100) >= MARKER_CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + random.nextInt(16);
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
        if (!level.getBlockState(surface).isAir()) return;

        int extra = random.nextInt(3) == 0 ? 1 : 0;
        placeOneMarker(level, chunk, surface, random);
        if (extra > 0) {
            int x2 = chunk.getPos().getMinBlockX() + random.nextInt(16);
            int z2 = chunk.getPos().getMinBlockZ() + random.nextInt(16);
            BlockPos surface2 = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x2, 0, z2));
            if (level.getBlockState(surface2).isAir()) {
                placeOneMarker(level, chunk, surface2, random);
            }
        }
        chunk.setUnsaved(true);
    }

    private static void placeOneMarker(ServerLevel level, LevelChunk chunk, BlockPos surface, RandomSource random) {
        int roll = random.nextInt(8);
        switch (roll) {
            case 0 -> level.setBlock(surface, Blocks.RED_CARPET.defaultBlockState(), 2);
            case 1 -> level.setBlock(surface, random.nextBoolean()
                    ? Blocks.WHITE_WOOL.defaultBlockState() : Blocks.RED_WOOL.defaultBlockState(), 2);
            case 2 -> level.setBlock(surface, Blocks.BROWN_WOOL.defaultBlockState(), 2);
            case 3 -> level.setBlock(surface.below(), Blocks.BONE_BLOCK.defaultBlockState(), 2);
            case 4 -> {
                if (random.nextBoolean()) {
                    level.setBlock(surface, Blocks.FERN.defaultBlockState(), 2);
                } else {
                    level.setBlock(surface, ModBlocks.CATNIP.get().defaultBlockState(), 2);
                }
            }
            case 6 -> level.setBlock(surface, Blocks.STONE_BUTTON.defaultBlockState(), 2);
            default -> level.setBlock(surface, random.nextBoolean()
                    ? ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState()
                    : Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        }
        if (random.nextInt(6) == 0) {
            var item = new ItemEntity(level, surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5,
                    new ItemStack(ModItems.RAINBOW_YARN_BALL.get()));
            level.addFreshEntity(item);
        } else if (random.nextInt(10) == 0) {
            var item = new ItemEntity(level, surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5,
                    new ItemStack(ModItems.MEMORY_SHARD.get()));
            level.addFreshEntity(item);
        }
    }
}
