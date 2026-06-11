package com.cocojenna.quest.qin;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** 李清照支線 — 五步 quest + 1 GAL（設計書 始皇貓）. */
public final class LiQingzhaoQuestManager {

    private static final String TAG = "cocojenna_li_qingzhao_step";

    private LiQingzhaoQuestManager() {}

    public static int getStep(ServerPlayer player) {
        return player.getPersistentData().getInt(TAG);
    }

    private static void setStep(ServerPlayer player, int step) {
        player.getPersistentData().putInt(TAG, step);
    }

    public static void onPlumInteract(ServerPlayer player) {
        if (getStep(player) == 0) {
            setStep(player, 1);
            player.displayClientMessage(Component.translatable("quest.cocojenna.li_qingzhao.step1"), true);
        }
    }

    public static void onPoetryInteract(ServerPlayer player) {
        int step = getStep(player);
        if (step == 1) {
            setStep(player, 2);
            giveOrDrop(player, new ItemStack(ModItems.ORIGAMI_SCRAP.get(), 3));
            player.displayClientMessage(Component.translatable("quest.cocojenna.li_qingzhao.step2"), true);
        } else if (step == 2 && hasOrigami(player, 5)) {
            consumeOrigami(player, 5);
            setStep(player, 3);
            player.displayClientMessage(Component.translatable("quest.cocojenna.li_qingzhao.step3"), true);
        } else if (step == 3) {
            setStep(player, 4);
            giveOrDrop(player, new ItemStack(ModItems.MAP_FRAGMENT.get(), 3));
            player.displayClientMessage(Component.translatable("quest.cocojenna.li_qingzhao.step4"), true);
        } else if (step == 4) {
            setStep(player, 5);
            BondData bond = ModCapabilities.getOrDefault(player);
            bond.addJournalEntry("qin:li_qingzhao_complete");
            giveOrDrop(player, new ItemStack(ModItems.PURE_TEAR.get(), 2));
            DialogueManager.play(player, "li_qingzhao_finale");
            player.displayClientMessage(Component.translatable("quest.cocojenna.li_qingzhao.step5"), true);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
        }
    }

    private static boolean hasOrigami(ServerPlayer player, int need) {
        int n = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.ORIGAMI_SCRAP.get())) n += s.getCount();
        }
        return n >= need;
    }

    private static void consumeOrigami(ServerPlayer player, int need) {
        int left = need;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.ORIGAMI_SCRAP.get())) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
