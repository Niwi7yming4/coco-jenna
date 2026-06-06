package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 記憶之書 📖 — 呼出 UI，閱讀記憶碎片，並與可可、珍奶共享記憶。
 *
 * <p>材料：書 x1 + 可可的毛 x1 + 珍奶的毛 x1
 *
 * <p>功能：
 * <ul>
 *   <li>右鍵開啟「記憶之書 GUI」</li>
 *   <li>顯示三軌成長數值（情感/獨立性/覺醒）</li>
 *   <li>顯示已收集的記憶碎片文本</li>
 *   <li>每讀一個新碎片 → 情感 +3</li>
 * </ul>
 */
public class MemoryBookItem extends Item {

    public MemoryBookItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // 客戶端：開啟 GUI（由 OpenMemoryBookPacket 驅動，此處直接呼叫）
            com.cocojenna.client.gui.MemoryBookScreen.open(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.memory_book.tooltip"));
        tooltip.add(Component.translatable("item.cocojenna.memory_book.tooltip2").withStyle(
                net.minecraft.ChatFormatting.GRAY));
    }
}
