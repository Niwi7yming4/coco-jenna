package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 貓食物基礎類別。
 *
 * <p>右鍵使用時：
 * <ul>
 *   <li>如果對著可可或珍奶右鍵 → 餵食，提升情感值</li>
 *   <li>如果對著食物碗右鍵 → 放入食物碗</li>
 * </ul>
 */
public class CatFoodItem extends Item {

    /** 對可可的好感度（0-5 星） */
    private final int cocoPreference;
    /** 對珍奶的好感度（0-5 星） */
    private final int jennaPreference;

    public CatFoodItem(Properties props, int cocoPreference, int jennaPreference) {
        super(props);
        this.cocoPreference = cocoPreference;
        this.jennaPreference = jennaPreference;
    }

    /** 餵食可可，返回是否成功 */
    public boolean feedCoco(CocoEntity coco, Player player, ItemStack stack) {
        if (!player.level().isClientSide) {
            BondData bond = ModCapabilities.getOrDefault(player);

            // 計算情感值增益
            float emotionGain = 1.0f + (cocoPreference - 3) * 0.5f;
            bond.modifyCocoEmotion(emotionGain);
            bond.setLastFeedCoco(player.level().getGameTime());

            // 可可喜歡的食物額外 +1
            if (cocoPreference >= 4) {
                bond.modifyCocoEmotion(1.0f);
                player.level().playSound(null, coco.blockPosition(),
                        ModSounds.COCO_PURR_DEEP.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
            }

            // 消耗物品
            if (!player.isCreative()) stack.shrink(1);

            // 同步到客戶端
            if (player instanceof ServerPlayer sp) {
                com.cocojenna.network.ModNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                        new com.cocojenna.network.SyncBondDataPacket(bond.serializeNBT()));
            }
        }
        return true;
    }

    /** 餵食珍奶，返回是否成功 */
    public boolean feedJenna(JennaEntity jenna, Player player, ItemStack stack) {
        if (!player.level().isClientSide) {
            BondData bond = ModCapabilities.getOrDefault(player);

            float emotionGain = 1.0f + (jennaPreference - 3) * 0.5f;
            bond.modifyJennaEmotion(emotionGain);
            bond.modifyJennaContentment(3f);
            bond.setLastFeedJenna(player.level().getGameTime());

            if (jennaPreference >= 4) {
                bond.modifyJennaEmotion(1.0f);
                player.level().playSound(null, jenna.blockPosition(),
                        ModSounds.JENNA_PURR_LIGHT.get(), SoundSource.NEUTRAL, 1.0f, 1.2f);
            }

            if (!player.isCreative()) stack.shrink(1);

            if (player instanceof ServerPlayer sp) {
                com.cocojenna.network.ModNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                        new com.cocojenna.network.SyncBondDataPacket(bond.serializeNBT()));
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
            List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable(
                "item.cocojenna.cat_food.coco_preference",
                "★".repeat(cocoPreference) + "☆".repeat(5 - cocoPreference)));
        tooltip.add(net.minecraft.network.chat.Component.translatable(
                "item.cocojenna.cat_food.jenna_preference",
                "★".repeat(jennaPreference) + "☆".repeat(5 - jennaPreference)));
    }

    public int getCocoPreference() { return cocoPreference; }
    public int getJennaPreference() { return jennaPreference; }
}
