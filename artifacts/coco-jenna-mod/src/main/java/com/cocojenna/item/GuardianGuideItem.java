package com.cocojenna.item;

import com.cocojenna.client.gui.GuardianGuideScreen;
import com.cocojenna.guide.PatchouliHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 《守護者指南》— Patchouli 或原版 GUI 閱讀器. */
public class GuardianGuideItem extends Item {

    public GuardianGuideItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            if (!PatchouliHelper.isLoaded() || !PatchouliHelper.openBookClient()) {
                GuardianGuideScreen.open(player);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
