package com.cocojenna.world.firstcry;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.world.FirstCryVillageGenerator;
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

    private static final String[] DISTRICTS = {
            "sacred_tree", "council_library", "shop_district", "kitchen_market",
            "moon_plaza", "first_cry_harbor", "west_inn", "farm_outer_ring"
    };

    private static final int[][] OFFSETS = {
            {0, 0},
            {20, -26},
            {33, -2},
            {5, 25},
            {0, 34},
            {-21, 38},
            {-33, 20},
            {-32, -28}
    };

    private FirstCryStructurePlacer() {}

    public static void placeDistricts(ServerLevel level) {
        BlockPos center = FirstCryVillageGenerator.CENTER;
        for (int i = 0; i < DISTRICTS.length; i++) {
            ResourceLocation id = new ResourceLocation(CocoJennaMod.MOD_ID,
                    "first_cry_village/" + DISTRICTS[i]);
            BlockPos origin = center.offset(OFFSETS[i][0], 0, OFFSETS[i][1]);
            try {
                StructureTemplate template = level.getStructureManager().getOrCreate(id);
                if (template.getSize().equals(Vec3i.ZERO)) continue;
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .addProcessor(new ModStructureProcessor());
                template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_ALL);
            } catch (Exception e) {
                CocoJennaMod.LOGGER.warn("First cry district {} failed: {}", DISTRICTS[i], e.toString());
            }
        }
    }
}
