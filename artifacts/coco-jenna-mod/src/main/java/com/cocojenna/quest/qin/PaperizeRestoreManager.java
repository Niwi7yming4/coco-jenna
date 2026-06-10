package com.cocojenna.quest.qin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 紙化方塊到期還原. */
public final class PaperizeRestoreManager {

    private static final Map<BlockPos, Entry> PENDING = new ConcurrentHashMap<>();

    private record Entry(ServerLevel level, BlockState original, long restoreAt) {}

    private PaperizeRestoreManager() {}

    public static void schedule(ServerLevel level, BlockPos pos, BlockState original, int ticks) {
        PENDING.put(pos.immutable(), new Entry(level, original, level.getGameTime() + ticks));
    }

    public static void tick(ServerLevel level) {
        long now = level.getGameTime();
        Iterator<Map.Entry<BlockPos, Entry>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            Entry entry = e.getValue();
            if (entry.level != level) continue;
            if (now >= entry.restoreAt) {
                level.setBlock(e.getKey(), entry.original, 3);
                it.remove();
            }
        }
    }
}
