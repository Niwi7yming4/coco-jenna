package com.cocojenna.item;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.quest.OnboardingQuestManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

/** 貓鈴鐺（副手裝備）— 降低貓之國敵人初始攻擊性；入門任務可於滿月祭壇旁開啟傳送門. */
public class CatBellOffhandItem extends Item {

    public CatBellOffhandItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockState clicked = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (!clicked.is(ModBlocks.FULL_MOON_ALTAR.get())) {
            return InteractionResult.PASS;
        }
        Player player = ctx.getPlayer();
        if (player == null || ctx.getLevel().isClientSide) {
            return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            OnboardingQuestManager.onBellAtAltar(sp, ctx.getClickedPos());
        }
        return InteractionResult.CONSUME;
    }
}
