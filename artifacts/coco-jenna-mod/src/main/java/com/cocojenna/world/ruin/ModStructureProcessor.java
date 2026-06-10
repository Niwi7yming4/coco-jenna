package com.cocojenna.world.ruin;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Map;

/** 遺跡 NBT 方塊替換 — 原版建材 → 貓之國 mod 方塊. */
public class ModStructureProcessor extends StructureProcessor {

    public static final Codec<ModStructureProcessor> CODEC =
            Codec.unit(ModStructureProcessor::new);

    private static final Map<Block, Block> REPLACE = Map.ofEntries(
            Map.entry(Blocks.OAK_PLANKS, com.cocojenna.init.ModBlocks.VELVET_PLANKS.get()),
            Map.entry(Blocks.SPRUCE_PLANKS, com.cocojenna.init.ModBlocks.VELVET_PLANKS.get()),
            Map.entry(Blocks.STONE_BRICKS, com.cocojenna.init.ModBlocks.STARDUST_BRICK.get()),
            Map.entry(Blocks.CRACKED_STONE_BRICKS, com.cocojenna.init.ModBlocks.STARDUST_BRICK.get()),
            Map.entry(Blocks.MOSSY_STONE_BRICKS, com.cocojenna.init.ModBlocks.MOONSTONE_BRICK.get()),
            Map.entry(Blocks.GRASS_BLOCK, com.cocojenna.init.ModBlocks.VELVET_GRASS.get()),
            Map.entry(Blocks.DIRT, com.cocojenna.init.ModBlocks.STARDUST_SOIL.get()),
            Map.entry(Blocks.GLASS, com.cocojenna.init.ModBlocks.PAWPRINT_GLASS.get()),
            Map.entry(Blocks.RED_WOOL, com.cocojenna.init.ModBlocks.WOVEN_WOOL.get()),
            Map.entry(Blocks.WHITE_WOOL, com.cocojenna.init.ModBlocks.VELVET_CARPET.get()),
            Map.entry(Blocks.COBBLESTONE, com.cocojenna.init.ModBlocks.SALT_BLOCK.get()),
            Map.entry(Blocks.OAK_LOG, com.cocojenna.init.ModBlocks.VELVET_TREE_LOG.get())
    );

    public static StructurePlaceSettings catKingdomSettings() {
        return new StructurePlaceSettings().addProcessor(new ModStructureProcessor());
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level, BlockPos offset, BlockPos pos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeInfo,
            StructurePlaceSettings settings) {
        BlockState state = blockInfo.state();
        Block mapped = REPLACE.get(state.getBlock());
        if (mapped != null) {
            return new StructureTemplate.StructureBlockInfo(
                    blockInfo.pos(), mapped.defaultBlockState(), blockInfo.nbt());
        }
        return blockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }
}
