package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 封印物 (Seal Orb) — 敵人 HP 歸零後凝結的物品。
 *
 * <p>NBT 格式：{@code {SealedEntity: "samurai_cat", Revived: false}}
 *
 * <p>用途：
 * <ul>
 *   <li>放置在「封印物展示台」上展示（雕塑公園系統）</li>
 *   <li>使用「聖水」或「朱槿花之淚」在展示台上復活為友好 NPC</li>
 *   <li>放入「蒸餾台」提取記憶微粒（不可逆）</li>
 * </ul>
 */
public class SealOrbItem extends Item {

    public SealOrbItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            String entity = tag.getString("SealedEntity");
            boolean revived = tag.getBoolean("Revived");

            if (!entity.isEmpty()) {
                tooltip.add(Component.translatable("cocojenna.seal_orb.entity",
                        Component.translatable("entity.cocojenna." + entity))
                        .withStyle(ChatFormatting.GOLD));
            }
            if (revived) {
                tooltip.add(Component.translatable("cocojenna.seal_orb.revived")
                        .withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(Component.translatable("cocojenna.seal_orb.dormant")
                        .withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable("cocojenna.seal_orb.hint")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    /** 設定封印物所封的實體 ID */
    public static ItemStack of(String entityId) {
        ItemStack stack = new ItemStack(com.cocojenna.init.ModItems.SEAL_ORB.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("SealedEntity", entityId);
        tag.putBoolean("Revived", false);
        return stack;
    }
}
