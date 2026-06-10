package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.sequence.FelineSequenceSkills;
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
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 貓之序列手冊 — 右鍵施放當前序列技能，Shift+右鍵切換源力（呼嚕／夜瞳／液態）.
 */
public class SequenceManualItem extends Item {

    private static final String[] FORCES = {"resonance", "shadow", "chaos"};

    public SequenceManualItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        BondData bond = ModCapabilities.getOrDefault(player);
        if (!com.cocojenna.sequence.MoonCrossroadsManager.hasChosenForce(bond)) {
            player.displayClientMessage(Component.translatable("force.cocojenna.need_choose"), true);
            return InteractionResultHolder.fail(stack);
        }

        if (player.isShiftKeyDown()) {
            player.displayClientMessage(Component.translatable("force.cocojenna.no_cycle"), true);
        } else if (player instanceof ServerPlayer sp) {
            if (FelineSequenceSkills.cast(sp, bond)) {
                player.displayClientMessage(Component.translatable(
                        "cocojenna.feline.cast",
                        Component.translatable("cocojenna.feline.force." + bond.getFelineForce()),
                        bond.getFelineTier()), true);
                ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                        new SyncBondDataPacket(bond.serializeNBT()));
            } else {
                player.displayClientMessage(
                        Component.translatable("cocojenna.feline.cooldown"), true);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    private static void cycleForce(Player player, BondData bond) {
        String current = bond.getFelineForce().isEmpty() ? "resonance" : bond.getFelineForce();
        int idx = 0;
        for (int i = 0; i < FORCES.length; i++) {
            if (FORCES[i].equals(current)) {
                idx = (i + 1) % FORCES.length;
                break;
            }
        }
        bond.setFelineForce(FORCES[idx]);
        player.displayClientMessage(Component.translatable(
                "cocojenna.feline.force_selected",
                Component.translatable("cocojenna.feline.force." + FORCES[idx])), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.sequence_manual.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.cocojenna.sequence_manual.shift")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
