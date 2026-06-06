package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 薛丁格盲盒 🎲
 * 每個存檔隨機包含以下之一：
 * <ul>
 *   <li>50% 機率：空氣（什麼都沒有）</li>
 *   <li>25% 機率：超稀有材料</li>
 *   <li>20% 機率：一把隨機良快刀</li>
 *   <li>5% 機率：九命貓薄荷 x1</li>
 * </ul>
 * 打開後消耗，且結果在世界生成時已決定（存在 NBT 中）。
 */
public class SchrodingersBoxItem extends Item {

    public SchrodingersBoxItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        // 讀取預定結果（或決定結果）
        String result = stack.getOrCreateTag().getString("SchrodingerResult");
        if (result.isEmpty()) {
            float roll = level.getRandom().nextFloat();
            if (roll < 0.50f)      result = "empty";
            else if (roll < 0.75f) result = "rare_material";
            else if (roll < 0.95f) result = "ryokatana";
            else                   result = "nine_lives";
            stack.getOrCreateTag().putString("SchrodingerResult", result);
        }

        // 給予結果物品
        ItemStack reward = switch (result) {
            case "rare_material" -> new ItemStack(com.cocojenna.init.ModItems.CHAOS_CRYSTAL.get(), 3);
            case "ryokatana" -> new ItemStack(com.cocojenna.init.ModItems.RYOKATANA_DAWN_HOPE.get());
            case "nine_lives" -> new ItemStack(com.cocojenna.init.ModItems.NINE_LIVES_CATNIP.get());
            default -> ItemStack.EMPTY;
        };

        if (!reward.isEmpty()) {
            if (!player.addItem(reward)) player.drop(reward, false);
            player.displayClientMessage(
                    Component.translatable("item.cocojenna.schrodingers_box.result." + result)
                            .withStyle(ChatFormatting.GOLD), false);
        } else {
            player.displayClientMessage(
                    Component.translatable("item.cocojenna.schrodingers_box.result.empty")
                            .withStyle(ChatFormatting.GRAY), false);
        }

        if (!player.isCreative()) stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        String result = stack.hasTag() ? stack.getTag().getString("SchrodingerResult") : "";
        if (result.isEmpty()) {
            tooltip.add(Component.translatable("item.cocojenna.schrodingers_box.tooltip.unknown")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Component.translatable("item.cocojenna.schrodingers_box.tooltip.known",
                    Component.translatable("item.cocojenna.schrodingers_box.result." + result))
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
