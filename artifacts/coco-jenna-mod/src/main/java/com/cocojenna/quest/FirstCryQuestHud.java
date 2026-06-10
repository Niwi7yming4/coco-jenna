package com.cocojenna.quest;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 初啼村主支線任務提示（行動列 HUD）. */
public final class FirstCryQuestHud {

    private FirstCryQuestHud() {}

    public static void showCallingHint(ServerPlayer player, int stage) {
        String key = switch (stage) {
            case 0 -> "quest.cocojenna.first_cry_calling.hud_start";
            case 1 -> "quest.cocojenna.first_cry_calling.stage1";
            case 2 -> "quest.cocojenna.first_cry_calling.stage2";
            case 3 -> "quest.cocojenna.first_cry_calling.pagepaw";
            case 4 -> "quest.cocojenna.first_cry_calling.pagepaw";
            default -> "quest.cocojenna.first_cry_calling.complete";
        };
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.AQUA), true);
    }

    public static void showBlackMudHint(ServerPlayer player, FirstCryProgress p) {
        if (p.isBlackMudPurified()) {
            player.displayClientMessage(
                    Component.translatable("quest.cocojenna.black_mud_secret.complete")
                            .withStyle(ChatFormatting.GREEN), true);
            return;
        }
        String key = switch (p.getBlackMudStage()) {
            case 0 -> null;
            case 1 -> "quest.cocojenna.black_mud_secret.hud_start";
            default -> "quest.cocojenna.black_mud_secret.hud_purify";
        };
        if (key != null) {
            player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.DARK_PURPLE), true);
        }
    }

    public static void tick(ServerPlayer player) {
        if (player.tickCount % 200 != 0) return;
        if (!player.level().dimension().equals(com.cocojenna.init.ModDimensions.CAT_KINGDOM)) return;
        if (player.blockPosition().distSqr(com.cocojenna.world.firstcry.FirstCryLayout.CENTER) > 85 * 85) {
            return;
        }
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        if (p.getCallingStage() < 5) {
            showCallingHint(player, p.getCallingStage());
        }
        if (p.getBlackMudStage() > 0 && !p.isBlackMudPurified()) {
            showBlackMudHint(player, p);
        }
    }
}
