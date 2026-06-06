package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 九命貓薄荷 🔴 — 受到致命傷害時自動消耗，時間倒流 5 秒滿血復活。
 * 每個存檔僅 3 株（遺忘高塔頂端採集）。
 */
@Mod.EventBusSubscriber(modid = com.cocojenna.CocoJennaMod.MOD_ID)
public class NineLivesCatnipItem extends Item {

    public NineLivesCatnipItem(Properties props) {
        super(props);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // 檢查物品欄是否有九命貓薄荷
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof NineLivesCatnipItem) {
                // 取消死亡，復活滿血
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth());

                // 清除負面效果
                player.removeAllEffects();

                // 消耗一個
                stack.shrink(1);

                // 特效
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                            player.getX(), player.getY() + 1, player.getZ(),
                            30, 0.5, 1.0, 0.5, 0.3);
                }

                player.displayClientMessage(
                        Component.translatable("cocojenna.nine_lives.triggered")
                                .withStyle(ChatFormatting.GOLD),
                        false);
                break;
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.nine_lives_catnip.tooltip")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.cocojenna.nine_lives_catnip.tooltip2")
                .withStyle(ChatFormatting.RED));
    }
}
