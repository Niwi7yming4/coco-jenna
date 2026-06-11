package com.cocojenna.kingdom;

import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 環境事件 tick（設計書 深化 §2.3）— 流星雨、幻影貓、風鈴等. */
public final class EnvironmentalEventManager {

    private EnvironmentalEventManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 200 != 0) return;
        int roll = player.getRandom().nextInt(600);
        if (roll == 0) triggerMeteorShower(player);
        else if (roll == 1) triggerPhantomCat(player);
        else if (roll == 2) triggerWindChime(player);
        else if (roll == 3) triggerMemoryEcho(player);
        else if (roll == 4) triggerNeonBloom(player);
        else if (roll == 5) triggerMoonWhisper(player);
    }

    private static void triggerMeteorShower(ServerPlayer player) {
        player.serverLevel().sendParticles(ParticleTypes.FALLING_NECTAR,
                player.getX(), player.getY() + 12, player.getZ(), 20, 8, 2, 8, 0.02);
        player.displayClientMessage(Component.translatable("ecology.cocojenna.event.meteor"), true);
    }

    private static void triggerPhantomCat(ServerPlayer player) {
        player.displayClientMessage(Component.translatable("ecology.cocojenna.event.phantom_cat"), true);
    }

    private static void triggerWindChime(ServerPlayer player) {
        player.displayClientMessage(Component.translatable("ecology.cocojenna.event.wind_chime"), true);
    }

    private static void triggerMemoryEcho(ServerPlayer player) {
        ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get());
        if (player.getRandom().nextFloat() < 0.25f && !player.addItem(shard)) {
            player.drop(shard, false);
        }
        player.displayClientMessage(Component.translatable("ecology.cocojenna.event.memory_echo"), true);
    }

    private static void triggerNeonBloom(ServerPlayer player) {
        player.serverLevel().sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                player.getX(), player.getY() + 1, player.getZ(), 8, 2, 1, 2, 0.01);
        player.displayClientMessage(Component.translatable("ecology.cocojenna.event.neon_bloom"), true);
    }

    private static void triggerMoonWhisper(ServerPlayer player) {
        if (player.level().isNight()) {
            player.displayClientMessage(Component.translatable("ecology.cocojenna.event.moon_whisper"), true);
        }
    }
}
