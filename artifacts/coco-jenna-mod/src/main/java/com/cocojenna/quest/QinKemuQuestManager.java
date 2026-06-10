package com.cocojenna.quest;

import com.cocojenna.capability.BondData;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.QinKemuEntity;
import com.cocojenna.init.ModItems;
import com.cocojenna.quest.qin.QinKemuEndTripManager;
import com.cocojenna.quest.qin.SealingRitualHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 秦可沐八章劇情（Dialogue 驅動）. */
public final class QinKemuQuestManager {

    private QinKemuQuestManager() {}

    public static void onSleepingChamberInteract(ServerPlayer player, BondData bond, QinKemuEntity qin) {
        if (bond.getQinKemuQuestStage() > 0) {
            onTalk(player, bond, qin);
            return;
        }
        qin.setAwake(true);
        bond.setQinKemuQuestStage(1);
        bond.setQinKemuFavor(10);
        bond.markMausoleumDiscovered(5);
        DialogueManager.play(player, "qin_kemu_ch1_awake");
        grantIfMissing(player, new ItemStack(ModItems.RED_PAPER.get()));
        qin.triggerHumanoidTransform();
    }

    public static void onTalk(ServerPlayer player, BondData bond, QinKemuEntity qin) {
        if (player.isShiftKeyDown()) {
            ItemStack held = player.getMainHandItem();
            if (com.cocojenna.weapon.WeaponUnsealManager.tryQinForceAwaken(player, held, bond)) {
                qin.setFavor(bond.getQinKemuFavor());
                return;
            }
            com.cocojenna.weapon.WeaponMemoryTaskManager.onPaperDeliver(player);
            com.cocojenna.weapon.WeaponMemoryTaskManager.onNarrativeEvent(
                    player, com.cocojenna.weapon.WeaponMemoryTaskRegistry.TaskType.RED_PAPER_BLOOD, 1);
        }
        int stage = bond.getQinKemuQuestStage();
        switch (stage) {
            case 1 -> {
                bond.setQinKemuQuestStage(2);
                DialogueManager.play(player, "qin_kemu_ch2_hungry");
            }
            case 2 -> {
                if (countItem(player, ModItems.MINT_MILK_CHOCOLATE.get()) >= 3) {
                    consume(player, ModItems.MINT_MILK_CHOCOLATE.get(), 3);
                    bond.setQinKemuQuestStage(3);
                    bond.addQinKemuFavor(15);
                    DialogueManager.play(player, "qin_kemu_ch3_maids");
                    spawnCompanions(player, qin);
                } else {
                    player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.need_chocolate"), false);
                }
            }
            case 3 -> {
                bond.setQinKemuQuestStage(4);
                bond.addQinKemuFavor(10);
                DialogueManager.play(player, "qin_kemu_ch4_paper");
            }
            case 4 -> {
                if (countItem(player, ModItems.RED_PAPER.get()) >= 100
                        && countItem(player, net.minecraft.world.item.Items.FEATHER) >= 20
                        && countItem(player, ModItems.MEMORY_SHARD.get()) >= 5) {
                    consume(player, ModItems.RED_PAPER.get(), 100);
                    consume(player, net.minecraft.world.item.Items.FEATHER, 20);
                    consume(player, ModItems.MEMORY_SHARD.get(), 5);
                    bond.setQinKemuQuestStage(5);
                    bond.addQinKemuFavor(20);
                    DialogueManager.play(player, "qin_kemu_ch5_sealing");
                } else {
                    player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.need_paper_harem"), false);
                }
            }
            case 5 -> {
                if (player.isShiftKeyDown()) {
                    SealingRitualHandler.tryRitual(player, bond, qin);
                } else {
                    player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.sealing_hint"), false);
                }
            }
            case 6 -> {
                if (bond.getQinKemuFavor() >= 70) {
                    DialogueManager.play(player, "qin_kemu_ch6_past");
                }
                if (player.isShiftKeyDown() && bond.getQinKemuFavor() >= 70) {
                    bond.setQinKemuQuestStage(7);
                    bond.addQinKemuFavor(10);
                }
            }
            case 7 -> QinKemuEndTripManager.tryStartTrip(player, bond, qin);
            case 8 -> {
                if (bond.getQinKemuFavor() >= 100 && player.isShiftKeyDown()) {
                    qin.setCompanionMode(true, player.getUUID());
                    player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.companion"), false);
                } else if (bond.getQinKemuFavor() >= 100) {
                    player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.complete"), false);
                } else {
                    player.displayClientMessage(
                            Component.translatable("quest.cocojenna.qin_kemu.need_favor_100"), false);
                }
            }
            default -> player.displayClientMessage(
                    Component.translatable("quest.cocojenna.qin_kemu.complete"), false);
        }
        qin.setFavor(bond.getQinKemuFavor());
        qin.setTaskStage(bond.getQinKemuQuestStage());
    }

    public static void onMausoleumFound(ServerPlayer player, BondData bond, int typeBit) {
        if (!bond.isMausoleumDiscovered(typeBit)) {
            bond.markMausoleumDiscovered(typeBit);
            bond.addQinKemuFavor(5);
            player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.mausoleum_found"), false);
        }
    }

    private static int countItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().countItem(item);
    }

    private static void consume(ServerPlayer player, net.minecraft.world.item.Item item, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.is(item)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }

    private static void grantIfMissing(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }

    private static void spawnCompanions(ServerPlayer player, QinKemuEntity qin) {
        var level = player.serverLevel();
        var afang = com.cocojenna.init.ModEntities.A_FANG.get().create(level);
        var lijiang = com.cocojenna.init.ModEntities.LI_JIANG.get().create(level);
        if (afang != null) {
            afang.setPos(qin.getX() + 1, qin.getY(), qin.getZ());
            level.addFreshEntity(afang);
        }
        if (lijiang != null) {
            lijiang.setPos(qin.getX() - 1, qin.getY(), qin.getZ());
            level.addFreshEntity(lijiang);
        }
    }
}
