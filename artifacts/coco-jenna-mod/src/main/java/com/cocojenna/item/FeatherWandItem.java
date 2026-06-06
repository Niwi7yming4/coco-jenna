package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.JennaEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * 逗貓棒 (Feather Wand) 🪄
 * 右鍵揮動：如果附近有珍奶且滿足感 > 80，觸發「邀請玩耍」行為。
 * 玩耍 30 秒後滿足感 +10。
 */
public class FeatherWandItem extends Item {

    public FeatherWandItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            JennaEntity jenna = level.getEntitiesOfClass(JennaEntity.class,
                    player.getBoundingBox().inflate(5.0),
                    j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID())
            ).stream().findFirst().orElse(null);

            if (jenna != null) {
                BondData bond = ModCapabilities.getOrDefault(player);
                if (bond.getJennaContentment() > 80) {
                    // 觸發玩耍行為（由 JennaEntity.tickBehaviors 處理逗貓棒偵測）
                    player.getItemInHand(hand).hurtAndBreak(1, player,
                            p -> p.broadcastBreakEvent(hand));
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
