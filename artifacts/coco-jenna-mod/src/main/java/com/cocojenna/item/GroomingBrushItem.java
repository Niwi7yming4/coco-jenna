package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * 梳子 (Grooming Brush) — 對貓咪使用可梳毛，一定機率掉落貓毛。
 * 情感值 ≥ 3 才能梳毛，每日梳毛上限 3 次。
 */
public class GroomingBrushItem extends Item {

    public GroomingBrushItem(Properties props) {
        super(props);
    }

    /**
     * 梳毛成功後：
     * <ul>
     *   <li>5% 機率掉落對應貓毛（可可/珍奶）</li>
     *   <li>情感 +0.5</li>
     *   <li>耐久 -1</li>
     * </ul>
     */
    public boolean groom(AbstractCatEntity cat, Player player, ItemStack brush) {
        if (player.level().isClientSide) return false;

        BondData bond = ModCapabilities.getOrDefault(player);

        // 播放音效
        player.level().playSound(null, cat.blockPosition(),
                ModSounds.COCO_PURR_DEEP.get(), SoundSource.NEUTRAL, 0.7f, 1.1f);

        // 機率掉落貓毛
        if (player.level().random.nextFloat() < 0.05f) {
            ItemStack fur = cat instanceof com.cocojenna.entity.CocoEntity
                    ? new ItemStack(ModItems.COCO_FUR.get())
                    : new ItemStack(ModItems.JENNA_FUR.get());
            if (!player.addItem(fur)) {
                cat.spawnAtLocation(fur);
            }
        }

        // 情感 +0.5
        if (cat instanceof com.cocojenna.entity.CocoEntity) {
            bond.modifyCocoEmotion(0.5f);
        } else {
            bond.modifyJennaEmotion(0.5f);
        }

        // 耐久 -1
        if (!player.isCreative()) {
            brush.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
        }

        return true;
    }
}
