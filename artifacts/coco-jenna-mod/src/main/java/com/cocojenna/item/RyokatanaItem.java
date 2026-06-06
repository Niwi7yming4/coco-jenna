package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 良快刀 (Ryokatana) — 貓之國傳說武器，共 50 把。
 * 每把有輕微被動特效，透過 variantId 區分。
 */
public class RyokatanaItem extends SwordItem {

    private final String variantId;

    public RyokatanaItem(Tier tier, int dmgBonus, float atkSpeed, Properties props, String variantId) {
        super(tier, dmgBonus, atkSpeed, props.rarity(Rarity.RARE));
        this.variantId = variantId;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.ryokatana." + variantId + ".effect")
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.cocojenna.ryokatana.lore")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public String getVariantId() { return variantId; }
}
