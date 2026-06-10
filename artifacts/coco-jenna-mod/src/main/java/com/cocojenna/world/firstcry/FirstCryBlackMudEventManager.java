package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModEntities;
import com.cocojenna.quest.FirstCryProgress;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

/** 黑泥滲透事件（設計書 六.5）. */
public final class FirstCryBlackMudEventManager {

    private FirstCryBlackMudEventManager() {}

    public static void onEnterRuin(ServerPlayer player) {
        if (player.blockPosition().distSqr(FirstCryLayout.BLACK_MUD_RUIN) > 12 * 12) return;
        FirstCryProgress p = FirstCryProgress.get(player.serverLevel());
        if (p.getBlackMudStage() > 0 || p.isBlackMudPurified()) return;
        p.setBlackMudStage(1);
        spawnRing(player.serverLevel());
    }

    public static void tick(ServerLevel level) {
        FirstCryProgress p = FirstCryProgress.get(level);
        if (p.getBlackMudStage() < 1 || p.isBlackMudPurified()) return;
        if (level.getGameTime() % 200 != 0) return;
        long sludge = level.getEntities(ModEntities.WANDERING_SLUDGE.get(),
                new AABB(FirstCryLayout.BLACK_MUD_RUIN).inflate(48), e -> true).size();
        if (sludge < 4) spawnRing(level);
    }

    public static void onPurified(ServerLevel level) {
        FirstCryProgress p = FirstCryProgress.get(level);
        p.setBlackMudPurified(true);
        p.setBlackMudStage(5);
    }

    private static void spawnRing(ServerLevel level) {
        var ruin = FirstCryLayout.BLACK_MUD_RUIN;
        for (int i = 0; i < 3; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double r = 22 + level.random.nextInt(10);
            double x = ruin.getX() + Math.cos(angle) * r;
            double z = ruin.getZ() + Math.sin(angle) * r;
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                    (int) x, (int) z);
            var sludge = ModEntities.WANDERING_SLUDGE.get().create(level);
            if (sludge != null) {
                sludge.moveTo(x, y, z, level.random.nextFloat() * 360, 0);
                level.addFreshEntity(sludge);
            }
        }
    }
}
