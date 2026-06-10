package com.cocojenna.world.ruin;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** 從 datapack NBT 放置遺跡結構. */
public final class RuinNbtPlacer {

    private RuinNbtPlacer() {}

    public static boolean tryPlace(ServerLevel level, BlockPos origin, RuinMatrixRegistry ruin,
            RuinVariant variant) {
        try {
            StructureTemplate template = level.getStructureManager().getOrCreate(ruin.structureId());
            Vec3i size = template.getSize();
            if (size.equals(Vec3i.ZERO)) {
                CocoJennaMod.LOGGER.warn("Ruin NBT missing or empty: {}", ruin.structureId());
                RuinMatrixSavedData.get(level).recordNbtFallback();
                return false;
            }
            StructurePlaceSettings settings = RuinVariantProcessor.settings(variant);
            template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_ALL);
            RuinLootHelper.fillChestsInStructure(level, origin, template, ruin);
            RuinInteractionRegistry.onRuinPlaced(level, origin, ruin, variant);
            CocoJennaMod.LOGGER.debug("Placed ruin NBT {} at {} variant {}", ruin.id(), origin, variant);
            return true;
        } catch (Exception e) {
            CocoJennaMod.LOGGER.warn("Failed NBT ruin {}: {}", ruin.id(), e.toString());
            RuinMatrixSavedData.get(level).recordNbtFallback();
            return false;
        }
    }
}
