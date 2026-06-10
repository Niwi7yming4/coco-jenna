package com.cocojenna.world.qin;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.world.ruin.RuinMatrixSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** 主世界皇陵放置 — 2% chunk 機率；沉睡室每世界唯一. */
public final class MausoleumPlacer {

    private static final int CHUNK_CHANCE = 2;

    private MausoleumPlacer() {}

    public static void tryPlace(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(Level.OVERWORLD)) return;

        RuinMatrixSavedData data = RuinMatrixSavedData.get(level);
        long key = chunk.getPos().toLong();
        RandomSource random = RandomSource.create(level.getSeed() ^ key ^ 0x5EEDCA78L);
        if (random.nextInt(100) >= CHUNK_CHANCE) return;

        int x = chunk.getPos().getMinBlockX() + random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + random.nextInt(16);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos entrance = new BlockPos(x, y, z);

        boolean allowSleeping = !data.isSleepingMausoleumPlaced();
        MausoleumVariant variant = MausoleumVariant.roll(random, allowSleeping);
        if (variant.type() == MausoleumType.SLEEPING_CHAMBER) {
            data.setSleepingMausoleumPlaced();
        }

        if (!MausoleumNbtPlacer.tryPlace(level, entrance, variant)) {
            buildMausoleum(level, entrance, variant);
        }
        RuinMatrixSavedData.get(level).registerPlacedRuin(entrance, "mausoleum", variant.id());
    }

    public static void buildMausoleum(ServerLevel level, BlockPos entrance, MausoleumVariant variant) {
        MausoleumType type = variant.type();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(entrance.offset(dx, 0, dz), Blocks.STONE_BRICKS.defaultBlockState(), 2);
            }
        }
        level.setBlock(entrance.offset(0, 1, 0), ModBlocks.SUSPICIOUS_WALL.get().defaultBlockState(), 2);

        BlockPos inner = entrance.below(4);
        for (int dy = -3; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    level.setBlock(inner.offset(dx, dy, dz), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                }
            }
        }

        switch (type) {
            case PAPER_HAREM -> {
                level.setBlock(inner, Blocks.RED_WOOL.defaultBlockState(), 2);
                level.setBlock(inner.above(), ModBlocks.SCRATCHING_POST.get().defaultBlockState(), 2);
            }
            case TERRACOTTA -> level.setBlock(inner, Blocks.TERRACOTTA.defaultBlockState(), 2);
            case LIBRARY -> {
                level.setBlock(inner, Blocks.BOOKSHELF.defaultBlockState(), 2);
                level.setBlock(inner.offset(1, 0, 0), Blocks.BOOKSHELF.defaultBlockState(), 2);
            }
            case TEA_GARDEN -> level.setBlock(inner, ModBlocks.CATNIP.get().defaultBlockState(), 2);
            case OBSERVATORY -> level.setBlock(inner.above(2), Blocks.GLASS.defaultBlockState(), 2);
            case SLEEPING_CHAMBER -> {
                level.setBlock(inner, ModBlocks.WOVEN_WOOL.get().defaultBlockState(), 2);
                var qin = ModEntities.QIN_KEMU.get().create(level);
                if (qin != null) {
                    qin.moveTo(inner.getX() + 0.5, inner.getY() + 1, inner.getZ() + 0.5, 0, 0);
                    qin.setAwake(false);
                    level.addFreshEntity(qin);
                }
            }
        }

        level.setBlock(inner.offset(0, 1, 2), Blocks.CHEST.defaultBlockState(), 2);
        decorateRegion(level, inner, variant.region());
    }

    private static void decorateRegion(ServerLevel level, BlockPos inner, MausoleumVariant.Region region) {
        var wool = switch (region) {
            case YELLOW_RIVER -> Blocks.YELLOW_WOOL;
            case JIANGNAN -> Blocks.PINK_WOOL;
            case WESTERN -> Blocks.ORANGE_WOOL;
            case NORTHERN -> Blocks.WHITE_WOOL;
            case COASTAL -> Blocks.LIGHT_BLUE_WOOL;
            case MOUNTAIN -> Blocks.GRAY_WOOL;
        };
        level.setBlock(inner.offset(-2, 1, 0), wool.defaultBlockState(), 2);
        level.setBlock(inner.offset(2, 1, 0), wool.defaultBlockState(), 2);
        if (region == MausoleumVariant.Region.COASTAL) {
            level.setBlock(inner.below(), Blocks.SAND.defaultBlockState(), 2);
        } else if (region == MausoleumVariant.Region.MOUNTAIN) {
            level.setBlock(inner.below(), Blocks.STONE.defaultBlockState(), 2);
        }
    }
}
