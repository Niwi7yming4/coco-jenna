package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 深淵／星光 DLC 劇情線擴寫. */
public final class StarlightChapterManager {

    public static final int FLAG_BEACON = 64;
    public static final int FLAG_CONVERGENCE = 128;
    public static final int FLAG_FINALE = 256;

    private StarlightChapterManager() {}

    public static void onEnterStardust(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() < 2) return;
        if (hasFlag(bond, FLAG_BEACON)) return;
        setFlag(bond, FLAG_BEACON);
        DialogueManager.play(player, "starlight_ch1_beacon");
        player.displayClientMessage(Component.translatable("starlight.cocojenna.ch1_hint"), true);
    }

    public static boolean onUndercatHubInteract(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() < 3 || !hasFlag(bond, FLAG_BEACON)) return false;
        if (hasFlag(bond, FLAG_CONVERGENCE)) return false;
        if (bond.getShadowCoins() < 40) {
            player.displayClientMessage(Component.translatable("starlight.cocojenna.need_coins", 40), true);
            return true;
        }
        bond.addShadowCoins(-40);
        setFlag(bond, FLAG_CONVERGENCE);
        DialogueManager.play(player, "starlight_ch2_convergence");
        giveOrDrop(player, new ItemStack(ModItems.STARDUST_SOIL_ITEM.get(), 3));
        return true;
    }

    public static void onArenaVictory(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!hasFlag(bond, FLAG_CONVERGENCE) || hasFlag(bond, FLAG_FINALE)) return;
        if (bond.getUndercatGladiators() < 3) return;
        setFlag(bond, FLAG_FINALE);
        DialogueManager.play(player, "starlight_ch3_finale");
        bond.modifySisterBond(8f);
        giveOrDrop(player, new ItemStack(ModBlocks.STARLIGHT_MARBLE.get().asItem(), 1));
        player.displayClientMessage(Component.translatable("starlight.cocojenna.finale"), true);
    }

    public static void onDialogueChoice(ServerPlayer player, String actionId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        switch (actionId) {
            case "starlight_follow" -> {
                bond.addShadowCoins(15);
                bond.modifySisterBond(5f);
            }
            case "starlight_wait" -> bond.addShadowCoins(5);
            case "starlight_pledge" -> {
                bond.addShadowCoins(25);
                bond.setUndercatChapter(Math.max(bond.getUndercatChapter(), 4));
            }
            default -> { }
        }
    }

    public static boolean hasFlag(BondData bond, int flag) {
        return (bond.getUndercatSideFlags() & flag) != 0;
    }

    private static void setFlag(BondData bond, int flag) {
        bond.setUndercatSideFlags(bond.getUndercatSideFlags() | flag);
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
