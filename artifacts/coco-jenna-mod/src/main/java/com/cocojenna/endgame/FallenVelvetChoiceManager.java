package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.guide.GuardianGuideProgress;
import com.cocojenna.init.ModItems;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 墮落絨尾抉擇 — 蒸餾 vs 安撫. */
public final class FallenVelvetChoiceManager {

    private FallenVelvetChoiceManager() {}

    public static void offerChoice(ServerPlayer player) {
        DialogueManager.play(player, "fallen_velvet_choice");
    }

    public static void onChoice(ServerPlayer player, String actionId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isFallenVelvetRedeemed()) return;
        player.getPersistentData().remove("cocojenna_velvet_choice_pending");
        if (!hasItem(player, ModItems.HIBISCUS_TEAR.get()) || !hasItem(player, ModItems.PURE_TEAR.get())) {
            player.displayClientMessage(Component.translatable("redemption.cocojenna.fallen_velvet.need_items"), true);
            return;
        }
        consumeOne(player, ModItems.HIBISCUS_TEAR.get());
        consumeOne(player, ModItems.PURE_TEAR.get());
        bond.setFallenVelvetRedeemed(true);
        if ("velvet_distill".equals(actionId)) {
            bond.addTownNpcFavor("sanhua", 10);
            bond.addKingdomHappiness(15);
            bond.modifySisterBond(5f);
            give(player, new ItemStack(ModItems.MEMORY_CLAY.get(), 8));
            player.displayClientMessage(Component.translatable("redemption.cocojenna.fallen_velvet.distill"), true);
        } else {
            bond.addTownNpcFavor("sanhua", 25);
            bond.addKingdomHappiness(8);
            bond.modifySisterBond(12f);
            bond.modifyJennaEmotion(8f);
            give(player, new ItemStack(ModItems.PURE_TEAR.get(), 2));
            give(player, new ItemStack(ModItems.MEMORY_CLAY.get(), 4));
            player.displayClientMessage(Component.translatable("redemption.cocojenna.fallen_velvet.soothe"), true);
        }
        DialogueManager.play(player, "fallen_velvet_redemption");
    }

    public static boolean eligible(BondData bond) {
        return bond.hasGuardianDiscovery(GuardianGuideProgress.FALLEN_VELVET)
                || HiddenSequenceRegistry.has(bond, "fallen_velvet");
    }

    private static boolean hasItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().countItem(item) > 0;
    }

    private static void consumeOne(ServerPlayer player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) {
                s.shrink(1);
                return;
            }
        }
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
