package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/** 披風 — 裝備於背包欄生效（CloakEffectHelper 檢測）. */
public class CatCloakItem extends Item {

    public CatCloakItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
            List<Component> tooltip, TooltipFlag flag) {
        String id = stack.hasTag() && stack.getTag().contains("CloakId")
                ? stack.getTag().getString("CloakId")
                : net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();
        tooltip.add(Component.translatable("item.cocojenna." + id + ".effect")
                .withStyle(ChatFormatting.AQUA));
        if (stack.hasTag() && stack.getTag().contains("CloakDurability")) {
            tooltip.add(Component.translatable("cloak.cocojenna.durability",
                    stack.getTag().getInt("CloakDurability")).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
