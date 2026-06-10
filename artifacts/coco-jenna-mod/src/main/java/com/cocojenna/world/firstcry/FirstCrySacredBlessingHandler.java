package com.cocojenna.world.firstcry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** 聖樹祝福 — 黎明 0~200 tick 於樹洞/樹冠獲得 10 分鐘再生與飽食（設計書 六.1）. */
public final class FirstCrySacredBlessingHandler {

    private static final int DAWN_END_TICK = 200;
    /** 10 分鐘 = 12000 tick */
    private static final int BLESS_DURATION = 12000;

    private FirstCrySacredBlessingHandler() {}

    public static void tick(ServerPlayer player) {
        if (player.tickCount % 100 != 0) return;
        long dayTime = player.level().getDayTime() % 24000L;
        if (dayTime > DAWN_END_TICK) return;
        BlockPos center = FirstCryLayout.SACRED_TREE;
        BlockPos pos = player.blockPosition();
        if (pos.distSqr(center) > 45 * 45) return;
        boolean inCanopy = pos.getY() >= center.getY() + 24
                && pos.distSqr(center.atY(pos.getY())) <= 9;
        boolean inHole = pos.distSqr(center.below(2)) < 16 && pos.getY() <= center.getY() + 2;
        if (!inCanopy && !inHole) return;
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, BLESS_DURATION, 0));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, BLESS_DURATION, 0));
    }
}
