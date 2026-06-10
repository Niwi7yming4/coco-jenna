package com.cocojenna.world.firstcry;

import com.cocojenna.init.ModItems;
import com.cocojenna.quest.FirstCryProgress;
import com.cocojenna.world.BlindPortGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 碼頭快速旅行 — 消耗記憶碎片傳送至已解鎖碼頭（設計書 六.4）. */
public final class HarborTravelManager {

    private static final BlockPos[] HARBORS = {
            FirstCryLayout.HARBOR.offset(7, 0, 3),
            BlindPortGenerator.CENTER,
            FirstCryLayout.offset(0, 0, -40)
    };

    private HarborTravelManager() {}

    public static boolean tryTravel(ServerPlayer player, int harborIndex) {
        if (harborIndex < 0 || harborIndex >= HARBORS.length) return false;
        if (player.blockPosition().distSqr(FirstCryLayout.HARBOR) > 20 * 20) return false;
        FirstCryProgress progress = FirstCryProgress.get(player.serverLevel());
        if (!progress.isHarborUnlocked() || !progress.isHarborBitUnlocked(harborIndex)) {
            player.displayClientMessage(Component.translatable("first_cry.cocojenna.harbor_locked"), true);
            return false;
        }
        if (!consumeShard(player)) {
            player.displayClientMessage(Component.translatable("first_cry.cocojenna.harbor_need_shard"), true);
            return false;
        }
        BlockPos dest = HARBORS[harborIndex];
        int y = player.serverLevel().getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, dest.getX(), dest.getZ());
        player.teleportTo(dest.getX() + 0.5, y + 1, dest.getZ() + 0.5);
        player.displayClientMessage(Component.translatable("first_cry.cocojenna.harbor_travel"), false);
        return true;
    }

    public static void unlockDefaultHarbor(ServerLevel level) {
        FirstCryProgress p = FirstCryProgress.get(level);
        p.setHarborUnlocked(true);
        p.unlockHarbor(0);
    }

    private static boolean consumeShard(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.MEMORY_SHARD.get())) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }
}
