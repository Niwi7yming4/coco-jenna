package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 序列徽章 — 解鎖特定「序列」（技能系統）的憑證。
 *
 * <p>序列系統（7 條）：
 * <ul>
 *   <li>序列 A：黑刃之道（可可情感線）</li>
 *   <li>序列 B：月光舞者（覺醒線）</li>
 *   <li>序列 C：貓步自由（獨立性線）</li>
 *   <li>序列 D：初晴之誓（終局後）</li>
 *   <li>序列 E：竹籃打水（珍奶情感線）</li>
 *   <li>序列 F：魚躍門（覺醒線）</li>
 *   <li>序列 G：姊妹同心（Sister Bond）</li>
 * </ul>
 */
public class SequenceBadgeItem extends Item {

    public SequenceBadgeItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
            List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SequenceId")) {
            String seq = tag.getString("SequenceId");
            tooltip.add(Component.translatable("cocojenna.sequence." + seq + ".name")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            tooltip.add(Component.translatable("cocojenna.sequence." + seq + ".desc")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.cocojenna.sequence_badge.unknown")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static ItemStack of(String sequenceId) {
        ItemStack stack = new ItemStack(com.cocojenna.init.ModItems.SEQUENCE_BADGE.get());
        stack.getOrCreateTag().putString("SequenceId", sequenceId);
        return stack;
    }
}
