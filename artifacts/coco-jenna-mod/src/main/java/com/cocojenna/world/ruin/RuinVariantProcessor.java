package com.cocojenna.world.ruin;

import com.cocojenna.init.ModBlocks;
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

/** 依變體替換遺跡方塊（附錄 A 映射）. */
public class RuinVariantProcessor extends StructureProcessor {

    public static final Codec<RuinVariantProcessor> CODEC =
            Codec.unit(() -> new RuinVariantProcessor(RuinVariant.DEFAULT));

    private final RuinVariant variant;

    public RuinVariantProcessor(RuinVariant variant) {
        this.variant = variant == null ? RuinVariant.DEFAULT : variant;
    }

    public static StructurePlaceSettings settings(RuinVariant variant) {
        return new StructurePlaceSettings()
                .addProcessor(new ModStructureProcessor())
                .addProcessor(new RuinVariantProcessor(variant));
    }

    private static final Map<Block, Block> BASE = Map.ofEntries(
            Map.entry(Blocks.STONE_BRICKS, ModBlocks.STARDUST_BRICK.get()),
            Map.entry(Blocks.CRACKED_STONE_BRICKS, ModBlocks.STARDUST_BRICK.get()),
            Map.entry(Blocks.MOSSY_STONE_BRICKS, ModBlocks.MOONSTONE_BRICK.get()),
            Map.entry(Blocks.OAK_PLANKS, ModBlocks.VELVET_PLANKS.get()),
            Map.entry(Blocks.TORCH, Blocks.LANTERN),
            Map.entry(Blocks.COBBLESTONE, ModBlocks.SALT_BLOCK.get())
    );

    private Block mapVariant(Block block) {
        return switch (variant) {
            case OVERGROWN -> block == Blocks.STONE_BRICKS ? Blocks.MOSSY_STONE_BRICKS
                    : block == Blocks.TORCH ? Blocks.LANTERN : block;
            case BURIED -> block == Blocks.STONE_BRICKS ? Blocks.SANDSTONE
                    : block == Blocks.TORCH ? Blocks.SOUL_TORCH : block;
            case VELVET -> block == Blocks.OAK_PLANKS ? ModBlocks.VELVET_PLANKS.get()
                    : block == Blocks.STONE_BRICKS ? ModBlocks.VELVET_BLOCK.get() : block;
            case MOONSTONE -> block == Blocks.STONE_BRICKS ? ModBlocks.MOONSTONE_BRICK.get()
                    : block == Blocks.TORCH ? Blocks.END_ROD : block;
            case BLACK_MUD -> block == Blocks.STONE_BRICKS ? ModBlocks.BLACK_MUD.get()
                    : block == Blocks.TORCH ? Blocks.SOUL_LANTERN : block;
            case CARDBOARD -> block == Blocks.OAK_PLANKS ? ModBlocks.CARDBOARD_BLOCK.get()
                    : block == Blocks.STONE_BRICKS ? ModBlocks.REINFORCED_CARDBOARD.get() : block;
            case CRYSTAL -> block == Blocks.STONE_BRICKS ? ModBlocks.PURR_CRYSTAL_BLOCK.get()
                    : block == Blocks.TORCH ? Blocks.SEA_LANTERN : block;
            case RUINED -> block == Blocks.STONE_BRICKS ? Blocks.CRACKED_STONE_BRICKS
                    : block == Blocks.OAK_PLANKS ? Blocks.AIR : block;
            default -> block;
        };
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level, BlockPos offset, BlockPos pos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeInfo,
            StructurePlaceSettings settings) {
        BlockState state = blockInfo.state();
        Block mapped = BASE.getOrDefault(state.getBlock(), state.getBlock());
        mapped = mapVariant(mapped);
        if (mapped != state.getBlock()) {
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
