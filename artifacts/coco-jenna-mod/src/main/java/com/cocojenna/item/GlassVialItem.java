package com.cocojenna.item;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

/** 玻璃瓶 — 滿月祭壇採集光譜、盲水之河取水樣本。 */
public class GlassVialItem extends Item {

    public GlassVialItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        boolean water = level.getFluidState(pos).is(Fluids.WATER)
                || level.getFluidState(pos.above()).is(Fluids.WATER)
                || level.getBlockState(pos).is(Blocks.WATER);
        if (!water) {
            return InteractionResult.PASS;
        }

        boolean blindRiver = level.getBiome(pos).is(ModBiomes.BLIND_WATER_RIVER);
        if (!blindRiver) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        InteractionHand hand = ctx.getHand();
        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        ItemStack sample = new ItemStack(ModItems.BLIND_WATER_SAMPLE.get());
        if (!player.addItem(sample)) {
            player.drop(sample, false);
        }
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 0.8F);
        return InteractionResult.CONSUME;
    }
}
