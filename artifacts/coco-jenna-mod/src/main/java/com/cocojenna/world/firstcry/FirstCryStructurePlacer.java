package com.cocojenna.world.firstcry;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.world.ruin.ModStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** 初啼村八分區 NBT 模板放置. */
public final class FirstCryStructurePlacer {

    private FirstCryStructurePlacer() {}

    public static void placeDistricts(ServerLevel level) {
        for (FirstCryAnchorTable.DistrictAnchor district : FirstCryAnchorTable.districts()) {
            ResourceLocation id = new ResourceLocation(CocoJennaMod.MOD_ID,
                    "first_cry_village/" + district.districtId());
            BlockPos origin = district.origin();
            try {
                StructureTemplate template = level.getStructureManager().getOrCreate(id);
                if (template.getSize().equals(Vec3i.ZERO)) continue;
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .addProcessor(new ModStructureProcessor());
                template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_ALL);
            } catch (Exception e) {
                CocoJennaMod.LOGGER.warn("First cry district {} failed: {}", district.districtId(), e.toString());
            }
        }
    }
}
