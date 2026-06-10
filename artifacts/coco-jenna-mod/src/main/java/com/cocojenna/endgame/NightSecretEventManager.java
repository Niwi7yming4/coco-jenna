package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/** 雨後夜間秘密探險 — 8 種午夜事件（設計書 Ch.15）. */
public final class NightSecretEventManager {

    private static final String[] ALL_EVENTS = {
            "rooftop_meeting", "secret_fish_share", "moonlight_dance", "furball_king_play",
            "npc_dream_guard", "portal_gaze", "stardust_bath", "secret_base"
    };

    private NightSecretEventManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 100 != 0) return;

        long time = player.level().getDayTime() % 24000L;
        if (time < 18000 && time > 2000) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        if (bond.getLastNightSecretDay() == day) return;
        if (player.getRandom().nextFloat() > 0.18f) return;
        if (!catsAwayFromPlayer(player)) return;

        List<String> pool = new ArrayList<>();
        for (String id : ALL_EVENTS) {
            if (!bond.hasNightSecret(id)) pool.add(id);
        }
        if (pool.isEmpty()) return;

        String pick = pool.get(player.getRandom().nextInt(pool.size()));
        trigger(player, bond, pick, day);
    }

    private static boolean catsAwayFromPlayer(ServerPlayer player) {
        boolean cocoNear = !player.level().getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(12)).isEmpty();
        boolean jennaNear = !player.level().getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(12)).isEmpty();
        return !cocoNear || !jennaNear;
    }

    private static void trigger(ServerPlayer player, BondData bond, String eventId, long day) {
        bond.setLastNightSecretDay(day);
        bond.markNightSecret(eventId);
        DialogueManager.play(player, "night_secret_" + eventId);
        applyReward(player, bond, eventId);
        player.displayClientMessage(Component.translatable("night.cocojenna.discovered",
                Component.translatable("night.cocojenna.event." + eventId)), true);

        if (bond.getNightSecretCount() >= 8 && !bond.hasNightSecret("all_complete_reward")) {
            bond.markNightSecret("all_complete_reward");
            ItemStack trinket = new ItemStack(ModItems.MOONLIGHT_FOOTPRINT.get());
            if (!player.addItem(trinket)) player.drop(trinket, false);
            player.displayClientMessage(Component.translatable("night.cocojenna.all_complete"), true);
        }
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    private static void applyReward(ServerPlayer player, BondData bond, String eventId) {
        switch (eventId) {
            case "moonlight_dance" -> player.addEffect(new MobEffectInstance(
                    MobEffects.LUCK, 24000, 0));
            case "stardust_bath" -> player.addEffect(new MobEffectInstance(
                    ModEffects.WARM_SERENITY.get(), 3600, 1));
            case "secret_base" -> {
                bond.modifySisterBond(5f);
                bond.modifyCocoEmotion(3f);
                bond.modifyJennaEmotion(3f);
            }
            case "furball_king_play" -> bond.addKingdomHappiness(5);
            case "portal_gaze" -> bond.addKingdomStability(4);
            default -> bond.modifySisterBond(2f);
        }
        bond.addPictureBookPage(new BondData.PictureBookPage(
                "night_" + eventId, "night.cocojenna.shard." + eventId, "moon", "warm"));
    }
}
