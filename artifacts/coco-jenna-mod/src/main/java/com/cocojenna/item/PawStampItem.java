package com.cocojenna.item;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import com.cocojenna.blackmud.BlackMudCorruptionManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 肉球印章 — 蓋章武器 +1 攻擊、防具 +1 護甲；每日每貓一次。
 */
public class PawStampItem extends Item {

    public static final String TAG_STAMPED = "PawStamped";
    private static final UUID STAMP_ATK = UUID.fromString("c1a2b3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID STAMP_ARM = UUID.fromString("d2b3c4d5-e6f7-8901-bcde-f12345678901");

    public PawStampItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));

        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack target = player.getItemInHand(other);
        if (!target.isEmpty() && stampItem(player, target, hand)) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        long day = level.getDayTime() / 24000L;
        var bond = ModCapabilities.getOrDefault(player);

        boolean cocoStamped = level.getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(3.0),
                c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID()))
                .stream().findFirst().map(coco -> {
                    if (bond.getPawStampCocoDay() == day) return false;
                    bond.setPawStampCocoDay(day);
                    player.displayClientMessage(
                            Component.translatable("item.cocojenna.paw_stamp.coco_stamped")
                                    .withStyle(ChatFormatting.GOLD), true);
                    return true;
                }).orElse(false);

        boolean jennaStamped = level.getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(3.0),
                j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID()))
                .stream().findFirst().map(jenna -> {
                    if (bond.getPawStampJennaDay() == day) return false;
                    bond.setPawStampJennaDay(day);
                    player.displayClientMessage(
                            Component.translatable("item.cocojenna.paw_stamp.jenna_stamped")
                                    .withStyle(ChatFormatting.YELLOW), true);
                    return true;
                }).orElse(false);

        if (cocoStamped || jennaStamped) {
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    private boolean stampItem(Player player, ItemStack target, InteractionHand stampHand) {
        if (target.getTag() != null && target.getTag().getBoolean(TAG_STAMPED)) {
            player.displayClientMessage(Component.translatable("item.cocojenna.paw_stamp.already"), true);
            return false;
        }
        CompoundTag tag = target.getOrCreateTag();
        tag.putBoolean(TAG_STAMPED, true);
        if (target.getItem() instanceof SwordItem || target.getItem() instanceof TieredItem) {
            tag.putBoolean("PawStampAttack", true);
            player.displayClientMessage(Component.translatable("item.cocojenna.paw_stamp.weapon"), true);
        } else if (target.getItem() instanceof ArmorItem) {
            tag.putBoolean("PawStampArmor", true);
            player.displayClientMessage(Component.translatable("item.cocojenna.paw_stamp.armor"), true);
        } else {
            player.displayClientMessage(Component.translatable("item.cocojenna.paw_stamp.invalid"), true);
            tag.remove(TAG_STAMPED);
            return false;
        }
        if (!player.isCreative()) player.getItemInHand(stampHand).shrink(1);
        return true;
    }

    public static float attackBonus(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().getBoolean("PawStampAttack") ? 1f : 0f;
    }

    public static int armorBonus(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().getBoolean("PawStampArmor") ? 1 : 0;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.isClientSide || player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return InteractionResult.FAIL;
        }
        BlackMudCorruptionManager.purifyRegion(
                (ServerLevel) level, ctx.getClickedPos(), 64, (ServerPlayer) player);
        if (!player.isCreative()) ctx.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.paw_stamp.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.cocojenna.paw_stamp.stamp_hint")
                .withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("item.cocojenna.paw_stamp.purify_hint")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
