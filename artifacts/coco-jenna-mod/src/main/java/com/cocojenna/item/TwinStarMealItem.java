package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.entity.SisterBondSystem;
import com.cocojenna.init.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 雙子星分享餐 🌟 — 必須對同時存在的可可和珍奶使用。
 * 效果：可可、珍奶情感各 +5，Sister Bond +3，滿足感 +15。
 * 若只有一貓在場，施放失敗。
 */
public class TwinStarMealItem extends Item {

    public TwinStarMealItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));

        // 尋找附近的可可和珍奶（都要在 5 格內）
        List<CocoEntity> cocos = level.getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(5.0),
                c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID()));
        List<JennaEntity> jennas = level.getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(5.0),
                j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID()));

        if (cocos.isEmpty() || jennas.isEmpty()) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("item.cocojenna.twin_star_meal.fail"),
                    true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        BondData bond = ModCapabilities.getOrDefault(player);
        bond.modifyCocoEmotion(5f);
        bond.modifyJennaEmotion(5f);
        bond.modifyJennaContentment(15f);
        SisterBondSystem.onSharedFood(player);

        // 播放特殊音效
        level.playSound(null, player.blockPosition(),
                ModSounds.WORLD_FULL_MOON_FESTIVAL.get(), SoundSource.NEUTRAL, 1.0f, 1.5f);

        // 雙色心形粒子
        for (int i = 0; i < 10; i++) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                    player.getX() + (player.getRandom().nextDouble() - 0.5) * 2,
                    player.getY() + 1.5,
                    player.getZ() + (player.getRandom().nextDouble() - 0.5) * 2,
                    0, 0.1, 0);
        }

        if (!player.isCreative()) player.getItemInHand(hand).shrink(1);

        if (player instanceof ServerPlayer sp) {
            com.cocojenna.network.ModNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                    new com.cocojenna.network.SyncBondDataPacket(bond.serializeNBT()));
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
