package com.cocojenna.quest.qin;

import com.cocojenna.capability.BondData;
import com.cocojenna.entity.QinKemuEntity;
import com.cocojenna.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/** 終界觀星章 — 傳送 End 並給予保護. */
public final class QinKemuEndTripManager {

    private QinKemuEndTripManager() {}

    public static boolean tryStartTrip(ServerPlayer player, BondData bond, QinKemuEntity qin) {
        if (bond.getQinKemuQuestStage() < 7) return false;
        ServerLevel end = player.server.getLevel(Level.END);
        if (end == null) return false;
        BlockPos spawn = end.getSharedSpawnPos();
        player.teleportTo(end, spawn.getX() + 0.5, spawn.getY() + 2, spawn.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 6000, 1));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.REGENERATION, 6000, 0));
        bond.setQinKemuQuestStage(8);
        bond.addQinKemuFavor(25);
        qin.setFavor(bond.getQinKemuFavor());
        ItemStack dagger = new ItemStack(ModItems.RED_PAPER_DAGGER.get());
        if (!player.addItem(dagger)) {
            player.drop(dagger, false);
        }
        player.displayClientMessage(Component.translatable("quest.cocojenna.qin_kemu.end_trip"), false);
        return true;
    }
}
