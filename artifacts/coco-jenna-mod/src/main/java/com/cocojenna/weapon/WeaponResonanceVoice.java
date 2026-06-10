package com.cocojenna.weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 共鳴階段武器釋放技能時的「共鳴語音」. */
public final class WeaponResonanceVoice {

    private WeaponResonanceVoice() {}

    public static void trySpeak(ServerPlayer player, WeaponSkillContext ctx) {
        if (ctx.stage() < WeaponAwakeningStage.RESONANCE.id) return;
        String key = "weapon.cocojenna.resonance_voice." + ctx.variantId();
        if (player.level().getRandom().nextFloat() > 0.35f) return;
        Component line = Component.translatable(key);
        String resolved = line.getString();
        if (resolved.equals(key) || resolved.startsWith("weapon.cocojenna.resonance_voice.")) {
            line = Component.translatable("weapon.cocojenna.resonance_voice._generic");
        }
        player.displayClientMessage(
                line.copy().withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC),
                true);
    }
}
