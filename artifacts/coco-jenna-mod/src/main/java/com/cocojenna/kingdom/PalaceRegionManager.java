package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.world.VelvetTailCastleGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 絨尾王宮多區域互動（王座／花園／圖書館／軍營，設計書 §6）. */
public final class PalaceRegionManager {

    private static final int RADIUS_SQ = 32 * 32;

    private PalaceRegionManager() {}

    public static boolean tryInteract(ServerPlayer player, BlockPos pos, BlockState state) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        if (pos.distSqr(VelvetTailCastleGenerator.CENTER) > RADIUS_SQ) return false;

        PalaceRegion region = regionAt(pos);
        if (PalaceFestivalBridge.tryFestivalInteract(player, pos, state, region)) {
            return true;
        }
        if (region == PalaceRegion.THRONE) {
            return ThroneHallManager.tryInteract(player, pos, state);
        }

        BondData bond = ModCapabilities.getOrDefault(player);
        return switch (region) {
            case GARDEN -> interactGarden(player, bond, state);
            case LIBRARY -> interactLibrary(player, bond, state);
            case BARRACKS -> interactBarracks(player, bond, state);
            default -> false;
        };
    }

    public static PalaceRegion regionAt(BlockPos pos) {
        BlockPos c = VelvetTailCastleGenerator.CENTER;
        int dx = pos.getX() - c.getX();
        int dz = pos.getZ() - c.getZ();
        if (dz < -10 && Math.abs(dx) < 14) return PalaceRegion.THRONE;
        if (dz > 18) return PalaceRegion.GARDEN;
        if (dx > 10) return PalaceRegion.LIBRARY;
        if (dx < -10) return PalaceRegion.BARRACKS;
        return PalaceRegion.COURTYARD;
    }

    private static boolean interactGarden(ServerPlayer player, BondData bond, BlockState state) {
        if (!state.is(ModBlocks.HIBISCUS_FLOWER.get()) && !state.is(Blocks.POPPY)) return false;
        bond.addKingdomHappiness(2);
        if (player.getRandom().nextInt(4) == 0) {
            ItemStack flower = new ItemStack(ModItems.HIBISCUS_FLOWER_ITEM.get());
            if (!player.getInventory().add(flower)) player.drop(flower, false);
        }
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.palace.garden"), true);
        return true;
    }

    private static boolean interactLibrary(ServerPlayer player, BondData bond, BlockState state) {
        if (!state.is(Blocks.BOOKSHELF) && !state.is(Blocks.LECTERN)) return false;
        bond.addKingdomProsperity(2);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.palace.library"), true);
        return true;
    }

    private static boolean interactBarracks(ServerPlayer player, BondData bond, BlockState state) {
        if (!state.is(Blocks.IRON_BARS) && !state.is(Blocks.IRON_BLOCK)) return false;
        bond.addReputation("royal", 2);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.palace.barracks"), true);
        return true;
    }

    public enum PalaceRegion {
        THRONE, GARDEN, LIBRARY, BARRACKS, COURTYARD
    }
}
