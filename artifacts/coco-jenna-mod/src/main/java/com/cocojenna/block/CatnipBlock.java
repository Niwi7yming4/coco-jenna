package com.cocojenna.block;

import com.cocojenna.economy.CatnipMarketManager;
import com.cocojenna.economy.CatnipQuality;
import com.cocojenna.item.CatnipItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** 貓薄荷 — 採集時依機率帶品質等級。 */
public class CatnipBlock extends BushBlock {
    public CatnipBlock(Properties props) { super(props); }

    @Override
    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        CatnipQuality quality = CatnipMarketManager.rollHarvestQuality(params.getLevel().getRandom());
        return List.of(CatnipItem.createStack(quality, 1));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
            net.minecraft.world.level.block.entity.BlockEntity blockEntity,
            ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.01);
        }
    }
}
