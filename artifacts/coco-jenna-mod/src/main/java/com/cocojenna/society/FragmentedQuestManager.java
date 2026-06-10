package com.cocojenna.society;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.FragmentedSequenceCapability;
import com.cocojenna.capability.FragmentedSequenceData;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;

/** 破碎序列引導任務「村莊裡的怪人」. */
public final class FragmentedQuestManager {

    private static final String QUEST_TAG = "cocojenna_fragmented_quest";

    private FragmentedQuestManager() {}

    public static void onMeetCarrier(ServerPlayer player, Villager villager, FragmentedSequenceData data) {
        if (data.getStrength() < 2) return;
        int stage = player.getPersistentData().getInt(QUEST_TAG);
        if (stage > 0) return;
        player.getPersistentData().putInt(QUEST_TAG, 1);
        player.displayClientMessage(
                Component.translatable("quest.cocojenna.fragmented.start"), false);
        net.minecraft.advancements.Advancement adv = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation(CocoJennaMod.MOD_ID, "fragmented_first_meet"));
        if (adv != null) {
            player.getAdvancements().award(adv, "carrier");
        }
    }

    public static void onMudKill(ServerPlayer player) {
        int stage = player.getPersistentData().getInt(QUEST_TAG);
        if (stage != 1) return;
        int kills = player.getPersistentData().getInt(QUEST_TAG + "_kills") + 1;
        player.getPersistentData().putInt(QUEST_TAG + "_kills", kills);
        if (kills < 5) {
            player.displayClientMessage(
                    Component.translatable("quest.cocojenna.fragmented.progress", kills, 5), true);
            return;
        }
        player.getPersistentData().putInt(QUEST_TAG, 2);
        ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get());
        if (!player.addItem(shard)) player.drop(shard, false);
        player.displayClientMessage(Component.translatable("quest.cocojenna.fragmented.complete"), false);
    }

    public static void onRitualWitness(ServerPlayer player, Villager villager) {
        int stage = player.getPersistentData().getInt(QUEST_TAG);
        if (stage != 2) return;
        player.getPersistentData().putInt(QUEST_TAG, 3);
        player.displayClientMessage(Component.translatable("quest.cocojenna.fragmented.ritual"), false);
        com.cocojenna.dialogue.DialogueManager.play(player, "fragmented_ritual_witness");
    }

    public static void onWeaponMemoryComplete(ServerPlayer player) {
        int stage = player.getPersistentData().getInt(QUEST_TAG);
        if (stage != 3) return;
        player.getPersistentData().putInt(QUEST_TAG, 4);
        ItemStack reward = new ItemStack(ModItems.MEMORY_SHARD.get(), 3);
        if (!player.addItem(reward)) player.drop(reward, false);
        player.displayClientMessage(Component.translatable("quest.cocojenna.fragmented.weapon_done"), false);
        net.minecraft.advancements.Advancement adv = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation(CocoJennaMod.MOD_ID, "fragmented_master"));
        if (adv != null) {
            player.getAdvancements().award(adv, "complete");
        }
    }

    public static void onShadowClawComplete(ServerPlayer player) {
        int stage = player.getPersistentData().getInt(QUEST_TAG);
        if (stage < 4) return;
        if (player.getPersistentData().getBoolean(QUEST_TAG + "_shadow")) return;
        player.getPersistentData().putBoolean(QUEST_TAG + "_shadow", true);
        player.displayClientMessage(Component.translatable("quest.cocojenna.fragmented.shadow_claw"), false);
    }

    public static boolean tryStartWeaponMemory(ServerPlayer player, Villager villager) {
        if (villager.getCapability(FragmentedSequenceCapability.FRAGMENTED)
                .map(d -> d.isActive() && d.getProfession().category()
                        == FragmentedProfession.Category.RITUAL)
                .orElse(false)) {
            ItemStack weapon = player.getMainHandItem();
            return com.cocojenna.weapon.WeaponMemoryTaskManager.tryStartFromNpc(player, weapon);
        }
        return false;
    }
}
