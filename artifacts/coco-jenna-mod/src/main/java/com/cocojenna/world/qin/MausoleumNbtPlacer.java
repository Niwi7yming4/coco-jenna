package com.cocojenna.world.qin;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.world.ruin.ModStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** 皇陵 NBT 優先放置. */
public final class MausoleumNbtPlacer {

    private MausoleumNbtPlacer() {}

    public static boolean tryPlace(ServerLevel level, BlockPos origin, MausoleumVariant variant) {
        ResourceLocation id = new ResourceLocation(CocoJennaMod.MOD_ID, "mausoleum/" + variant.id());
        ResourceLocation fallback = new ResourceLocation(CocoJennaMod.MOD_ID, "mausoleum/" + variant.type().id());
        try {
            StructureTemplate template = level.getStructureManager().getOrCreate(id);
            if (template.getSize().equals(Vec3i.ZERO)) {
                template = level.getStructureManager().getOrCreate(fallback);
            }
            if (template.getSize().equals(Vec3i.ZERO)) return false;
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .addProcessor(new ModStructureProcessor());
            template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_ALL);
            return true;
        } catch (Exception e) {
            CocoJennaMod.LOGGER.warn("Mausoleum NBT {} failed: {}", variant.id(), e.toString());
            return false;
        }
    }
}
