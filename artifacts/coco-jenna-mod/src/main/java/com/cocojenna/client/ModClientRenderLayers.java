package com.cocojenna.client;

import com.cocojenna.init.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

/** Cutout layers for cross-model plants and other alpha-tested blocks. */
public final class ModClientRenderLayers {

    private ModClientRenderLayers() {}

    public static void register() {
        cutout(ModBlocks.HIBISCUS_FLOWER.get());
        cutout(ModBlocks.CATNIP.get());
        cutout(ModBlocks.NEON_MUSHROOM.get());
        cutout(ModBlocks.VELVET_GRASS.get());
        cutout(ModBlocks.MOONSTONE_CLUSTER.get());
        cutout(ModBlocks.SPORE_FRUIT_NODE.get());
        cutout(ModBlocks.COTTON_CANDY_SHRUB.get());
    }

    private static void cutout(Block block) {
        ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
    }
}
