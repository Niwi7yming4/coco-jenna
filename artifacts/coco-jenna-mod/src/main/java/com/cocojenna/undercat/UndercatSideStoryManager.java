package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 瓦楞／獨眼支線好感與分支對話（批次 C）. */
public final class UndercatSideStoryManager {

    public static final int FLAG_CORRUGATA_M1 = 1;
    public static final int FLAG_CORRUGATA_M2 = 2;
    public static final int FLAG_CORRUGATA_BOND = 4;
    public static final int FLAG_ONE_EYE_M1 = 8;
    public static final int FLAG_ONE_EYE_M2 = 16;
    public static final int FLAG_ONE_EYE_ROMANCE = 32;

    private UndercatSideStoryManager() {}

    public static boolean onCorrugataInteract(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getCorrugataAffinity() >= 20 && !hasFlag(bond, FLAG_CORRUGATA_M1)) {
            setFlag(bond, FLAG_CORRUGATA_M1);
            DialogueManager.play(player, "undercat_corrugata_m1");
            return true;
        }
        if (bond.getUndercatChapter() >= 2 && bond.getCorrugataAffinity() >= 50
                && !hasFlag(bond, FLAG_CORRUGATA_M2)) {
            setFlag(bond, FLAG_CORRUGATA_M2);
            DialogueManager.play(player, "undercat_corrugata_m2");
            return true;
        }
        if (bond.getUndercatChapter() >= 3 && bond.getCorrugataAffinity() >= 80
                && !hasFlag(bond, FLAG_CORRUGATA_BOND)) {
            setFlag(bond, FLAG_CORRUGATA_BOND);
            DialogueManager.play(player, "undercat_corrugata_bond");
            return true;
        }
        return false;
    }

    public static boolean onOneEyeInteract(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() < 2) return false;
        if (bond.getOneEyeAffinity() >= 25 && !hasFlag(bond, FLAG_ONE_EYE_M1)) {
            setFlag(bond, FLAG_ONE_EYE_M1);
            DialogueManager.play(player, "undercat_one_eye_m1");
            return true;
        }
        if (bond.getUndercatStage() >= 4 && bond.getOneEyeAffinity() >= 55
                && !hasFlag(bond, FLAG_ONE_EYE_M2)) {
            setFlag(bond, FLAG_ONE_EYE_M2);
            DialogueManager.play(player, "undercat_one_eye_m2");
            return true;
        }
        if (bond.getUndercatChapter() >= 3 && bond.getOneEyeAffinity() >= 85
                && !hasFlag(bond, FLAG_ONE_EYE_ROMANCE)) {
            setFlag(bond, FLAG_ONE_EYE_ROMANCE);
            DialogueManager.play(player, "undercat_one_eye_romance");
            return true;
        }
        return false;
    }

    public static void addCorrugataAffinity(ServerPlayer player, int amount) {
        if (amount <= 0) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setCorrugataAffinity(Math.min(100, bond.getCorrugataAffinity() + amount));
    }

    public static void addOneEyeAffinity(ServerPlayer player, int amount) {
        if (amount <= 0) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setOneEyeAffinity(Math.min(100, bond.getOneEyeAffinity() + amount));
    }

    public static void onDialogueChoice(ServerPlayer player, String actionId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        switch (actionId) {
            case "corrugata_promise" -> {
                addCorrugataAffinity(player, 15);
                bond.modifySisterBond(5f);
                player.displayClientMessage(
                        Component.translatable("undercat.cocojenna.side.corrugata_promise"), true);
            }
            case "corrugata_distance" -> addCorrugataAffinity(player, 5);
            case "corrugata_accept_crown" -> {
                addCorrugataAffinity(player, 20);
                giveOrDrop(player, new ItemStack(ModItems.CARDBOARD_BADGE.get()));
                player.displayClientMessage(
                        Component.translatable("undercat.cocojenna.side.corrugata_crown"), true);
            }
            case "corrugata_refuse_crown" -> addCorrugataAffinity(player, 8);
            case "one_eye_trust" -> {
                addOneEyeAffinity(player, 15);
                bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 10);
                player.displayClientMessage(
                        Component.translatable("undercat.cocojenna.side.one_eye_trust"), true);
            }
            case "one_eye_business" -> addOneEyeAffinity(player, 5);
            case "one_eye_sail" -> {
                addOneEyeAffinity(player, 20);
                giveOrDrop(player, new ItemStack(ModItems.SHADOW_COIN.get(), 30));
                player.displayClientMessage(
                        Component.translatable("undercat.cocojenna.side.one_eye_sail"), true);
            }
            case "one_eye_decline" -> addOneEyeAffinity(player, 8);
            case "starlight_follow", "starlight_wait", "starlight_pledge" ->
                    StarlightChapterManager.onDialogueChoice(player, actionId);
            default -> { }
        }
    }

    private static boolean hasFlag(BondData bond, int flag) {
        return (bond.getUndercatSideFlags() & flag) != 0;
    }

    private static void setFlag(BondData bond, int flag) {
        bond.setUndercatSideFlags(bond.getUndercatSideFlags() | flag);
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
