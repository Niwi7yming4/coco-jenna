package com.cocojenna.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/** 良快刀商店攤位 — 齒輪鎮／無明港. */
public class RyokatanaShopStandBlock extends Block {

    public RyokatanaShopStandBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer sp) {
            if (player.isShiftKeyDown()) {
                com.cocojenna.shop.ShopOpener.openReputationShop(sp, pos);
            } else {
                NetworkHooks.openScreen(sp, new SimpleMenuProvider(
                        (id, inv, p) -> new com.cocojenna.shop.RyokatanaShopMenu(id, inv),
                        Component.translatable("shop.cocojenna.ryokatana.title")), pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
