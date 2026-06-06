package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 記憶碎片 — 主線核心道具。
 *
 * <p>收集記憶碎片是推進主線的核心機制（非擊殺 Boss）。
 * 每個碎片都有獨特的文本內容，儲存在物品 NBT 中。
 *
 * <p>使用（右鍵）：
 * <ul>
 *   <li>顯示碎片文本（字幕）</li>
 *   <li>貓之國覺醒值 +1</li>
 *   <li>可可/珍奶情感 +3（首次閱讀）</li>
 * </ul>
 */
public class MemoryShardItem extends Item {

    public MemoryShardItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            BondData bond = ModCapabilities.getOrDefault(player);

            // 取得碎片 ID（NBT）
            String shardId = stack.getOrCreateTag().getString("ShardId");
            boolean firstRead = !stack.getOrCreateTag().getBoolean("Read");

            if (firstRead) {
                // 首次閱讀獎勵
                bond.modifyCocoEmotion(3f);
                bond.modifyJennaEmotion(3f);
                bond.addMemoryShard(1);
                stack.getOrCreateTag().putBoolean("Read", true);
            }

            // 顯示記憶文本
            String dialogueKey = shardId.isEmpty()
                    ? "cocojenna.memory_shard.generic"
                    : "cocojenna.memory_shard." + shardId;
            player.displayClientMessage(
                    Component.translatable(dialogueKey).withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA),
                    false);

            // 顯示覺醒進度
            player.displayClientMessage(
                    Component.translatable("cocojenna.awakening.progress",
                            bond.getCocoAwakening(), 50).withStyle(ChatFormatting.LIGHT_PURPLE),
                    true);

            // 同步到客戶端
            if (player instanceof ServerPlayer sp) {
                com.cocojenna.network.ModNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> sp),
                        new com.cocojenna.network.SyncBondDataPacket(bond.serializeNBT()));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        boolean read = stack.hasTag() && stack.getTag().getBoolean("Read");
        String shardId = stack.hasTag() ? stack.getTag().getString("ShardId") : "";

        if (!shardId.isEmpty()) {
            tooltip.add(Component.translatable("cocojenna.memory_shard.id", shardId)
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        tooltip.add(Component.translatable(read
                ? "item.cocojenna.memory_shard.read"
                : "item.cocojenna.memory_shard.unread")
                .withStyle(read ? ChatFormatting.GRAY : ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("item.cocojenna.memory_shard.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}
