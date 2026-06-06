package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 專屬特餐 — 對特定貓使用，情感 +5、獨立性 +2、滿足感 +10。
 * 如果餵錯貓會被無視（貓不感興趣）。
 */
public class SpecialCatMealItem extends Item {

    private final boolean isForCoco; // true = 可可的特餐，false = 珍奶的特餐

    public SpecialCatMealItem(Properties props, boolean isForCoco) {
        super(props);
        this.isForCoco = isForCoco;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 右鍵使用時說明用法
        if (level.isClientSide) {
            player.displayClientMessage(
                    Component.translatable(isForCoco
                            ? "item.cocojenna.coco_special_meal.hint"
                            : "item.cocojenna.jenna_special_meal.hint"),
                    true);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    /** 由 ModEventHandler 的 EntityInteract 呼叫 */
    public boolean feedTo(Object cat, Player player, ItemStack stack) {
        if (player.level().isClientSide) return false;

        if (isForCoco && !(cat instanceof CocoEntity)) return false;
        if (!isForCoco && !(cat instanceof JennaEntity)) return false;

        BondData bond = ModCapabilities.getOrDefault(player);

        if (isForCoco) {
            bond.modifyCocoEmotion(5f);
            bond.setLastFeedCoco(player.level().getGameTime());
            player.level().playSound(null, ((CocoEntity) cat).blockPosition(),
                    ModSounds.COCO_PURR_DEEP.get(), SoundSource.NEUTRAL, 1.5f, 0.9f);
        } else {
            bond.modifyJennaEmotion(5f);
            bond.modifyJennaContentment(10f);
            bond.setLastFeedJenna(player.level().getGameTime());
            player.level().playSound(null, ((JennaEntity) cat).blockPosition(),
                    ModSounds.JENNA_PURR_LIGHT.get(), SoundSource.NEUTRAL, 1.5f, 1.2f);
        }

        if (!player.isCreative()) stack.shrink(1);

        if (player instanceof ServerPlayer sp) {
            com.cocojenna.network.ModNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                    new com.cocojenna.network.SyncBondDataPacket(bond.serializeNBT()));
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(isForCoco
                ? "item.cocojenna.coco_special_meal.tooltip"
                : "item.cocojenna.jenna_special_meal.tooltip"));
    }
}
