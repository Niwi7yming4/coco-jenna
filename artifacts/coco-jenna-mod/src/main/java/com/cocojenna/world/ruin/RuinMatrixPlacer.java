package com.cocojenna.world.ruin;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** 遺跡矩陣 chunk 放置整合. */
public final class RuinMatrixPlacer {

    private static final int OVERWORLD_CHANCE = 2;
    private static final int CAT_KINGDOM_CHANCE = 3;

    private RuinMatrixPlacer() {}

    public static void tryPlaceOverworld(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        tryPlace(level, chunk, false, OVERWORLD_CHANCE);
    }

    public static void tryPlaceCatKingdom(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        tryPlace(level, chunk, true, CAT_KINGDOM_CHANCE);
    }

    private static void tryPlace(ServerLevel level, LevelChunk chunk, boolean catKingdom, int chance) {
        RuinMatrixSavedData data = RuinMatrixSavedData.get(level);
        long key = chunk.getPos().toLong();
        if (data.isChunkSeeded(key)) return;

        RandomSource random = RandomSource.create(level.getSeed() ^ key ^ 0x5EEDCA77L);
        data.markChunkSeeded(key);
        if (random.nextInt(100) >= chance) return;

        int x = chunk.getPos().getMinBlockX() + random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + random.nextInt(16);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos origin = new BlockPos(x, y, z);

        RuinMatrixRegistry ruin = RuinMatrixRegistry.roll(random, catKingdom);
        if (data.isRuinAt(origin)) return;

        RuinVariant variant = RuinVariant.roll(random, catKingdom);
        boolean placed = false;
        boolean usedNbt = false;
        if (ruin.source() == RuinMatrixRegistry.RuinSource.NBT) {
            placed = RuinNbtPlacer.tryPlace(level, origin, ruin, variant);
            usedNbt = placed;
        }
        if (!placed) {
            if (ruin.source() == RuinMatrixRegistry.RuinSource.NBT) {
                CocoJennaMod.LOGGER.info("Ruin {} fell back to procedural at {}", ruin.id(), origin);
            }
            ProceduralRuinBuilder.build(level, origin, ruin, random);
            ProceduralRuinBuilder.fillLootChest(level, origin.above(), ruin);
            RuinInteractionRegistry.onRuinPlaced(level, origin, ruin, variant);
            placed = true;
        }
        if (placed) {
            data.registerPlacedRuin(origin, ruin.id(), variant.name());
            if (usedNbt) {
                CocoJennaMod.LOGGER.debug("Ruin placed via NBT: {} at {}", ruin.id(), origin);
            }
        }
    }
}
