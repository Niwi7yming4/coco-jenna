package com.cocojenna.exploration;

import com.cocojenna.block.DungeonEntranceBlock;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.world.DungeonGenerators;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 追蹤地表遺跡入口並同步通關外觀（設計書 3.1）. */
public final class DungeonEntranceRegistry {

    private static final Map<Integer, List<BlockPos>> BY_INDEX = new HashMap<>();

    private DungeonEntranceRegistry() {}

    public static void register(BlockPos pos, int dungeonIndex) {
        BY_INDEX.computeIfAbsent(dungeonIndex, i -> new ArrayList<>()).add(pos.immutable());
    }

    public static void markCleared(ServerLevel level, String dungeonId) {
        int idx = DungeonGenerators.indexOf(dungeonId);
        if (idx < 0) return;
        for (BlockPos pos : BY_INDEX.getOrDefault(idx, List.of())) {
            BlockState st = level.getBlockState(pos);
            if (st.is(ModBlocks.DUNGEON_ENTRANCE.get()) && !st.getValue(DungeonEntranceBlock.CLEARED)) {
                level.setBlock(pos, st.setValue(DungeonEntranceBlock.CLEARED, true), 2);
            }
        }
    }

    public static void syncForPlayer(ServerLevel level, ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        for (int i = 0; i < 15; i++) {
            String id = DungeonGenerators.idAt(i);
            int flag = DungeonRegistry.flag(id);
            if (flag != 0 && bond.hasDungeonCleared(flag)) {
                for (BlockPos pos : BY_INDEX.getOrDefault(i, List.of())) {
                    BlockState st = level.getBlockState(pos);
                    if (st.is(ModBlocks.DUNGEON_ENTRANCE.get()) && !st.getValue(DungeonEntranceBlock.CLEARED)) {
                        level.setBlock(pos, st.setValue(DungeonEntranceBlock.CLEARED, true), 2);
                    }
                }
            }
        }
    }
}
