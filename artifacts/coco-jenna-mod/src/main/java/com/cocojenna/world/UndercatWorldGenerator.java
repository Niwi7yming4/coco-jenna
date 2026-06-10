package com.cocojenna.world;

import com.cocojenna.init.ModDimensions;
import com.cocojenna.undercat.UndercatRegion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 地下貓域多區域 POI 生成協調器. */
public final class UndercatWorldGenerator {

    private static final Set<String> BUILT = ConcurrentHashMap.newKeySet();

    private UndercatWorldGenerator() {}

    public static void decorateChunk(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(ModDimensions.UNDERCAT_DOMAIN)) return;
        for (UndercatRegion region : UndercatRegion.values()) {
            if (!region.overlaps(chunk.getPos())) continue;
            if (!BUILT.add(region.buildKey())) continue;
            switch (region) {
                case CARDBOARD_SLUMS -> CardboardSlumsGenerator.build(level, region.center);
                case SMUGGLER_DOCK -> SmugglerDockGenerator.build(level, region.center);
                case CATNIP_FARM -> CatnipFarmGenerator.build(level, region.center);
                case SCRATCH_ARENA -> ScratchArenaGenerator.build(level, region.center);
                case SILENT_LIBRARY -> SilentLibraryGenerator.build(level, region.center);
                case SERVANT_CAMP -> ServantCampGenerator.build(level, region.center);
            }
        }
    }
}
