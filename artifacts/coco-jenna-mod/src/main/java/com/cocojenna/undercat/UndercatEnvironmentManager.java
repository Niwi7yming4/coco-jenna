package com.cocojenna.undercat;

import com.cocojenna.init.ModDimensions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** 地下貓域環境 — 夜視與低光提示（DLC 設計書）. */
public final class UndercatEnvironmentManager {

    private UndercatEnvironmentManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.UNDERCAT_DOMAIN)) return;
        if (player.tickCount % 40 != 0) return;
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 120, 0, false, false, true));
        if (player.tickCount % 200 == 0) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("undercat.cocojenna.dark_hint"), true);
        }
    }
}
