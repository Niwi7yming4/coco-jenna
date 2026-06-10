package com.cocojenna.item;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.world.portal.CatKingdomPortalShape;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 滿月光譜 — 滿月祭壇採集，或用於點燃貓之國傳送門。
 */
public class FullMoonSpectrumItem extends Item {

    public FullMoonSpectrumItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockState clicked = level.getBlockState(ctx.getClickedPos());
        if (!clicked.is(ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get())) {
            return InteractionResult.PASS;
        }
        Player player = ctx.getPlayer();
        if (CatKingdomPortalShape.tryIgnite(level, ctx.getClickedPos(), player, ctx.getHand())) {
            if (player != null && !level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.cocojenna.portal_ignited"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (player != null && !level.isClientSide) {
            player.displayClientMessage(
                    Component.translatable("message.cocojenna.portal_invalid"), true);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.full_moon_spectrum.tooltip")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("item.cocojenna.full_moon_spectrum.condition")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.cocojenna.full_moon_spectrum.portal_use")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
