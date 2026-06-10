package com.cocojenna.memforge;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.world.BlindPortGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class RitualConditions {

    private RitualConditions() {}

    public static boolean needsCocoAndFullMoon(MemoryForgeRitual ritual) {
        return hasCatNearby(ritual, CocoEntity.class) && isFullMoon(ritual.level());
    }

    public static boolean needsJennaAndDay(MemoryForgeRitual ritual) {
        return hasCatNearby(ritual, JennaEntity.class) && ritual.level().isDay();
    }

    public static boolean needsBondAndFullMoon(MemoryForgeRitual ritual) {
        var player = ritual.player();
        if (player == null) return false;
        float bond = ModCapabilities.getOrDefault(player).getSisterBond();
        return bond > 80f && isFullMoon(ritual.level());
    }

    public static boolean needsBlindPort(MemoryForgeRitual ritual) {
        if (!ritual.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        var core = ritual.corePos();
        var port = BlindPortGenerator.CENTER;
        return core.distSqr(port) < 60 * 60;
    }

    private static boolean isFullMoon(Level level) {
        int phase = level.getMoonPhase();
        return phase == 0 || phase == 1;
    }

    private static boolean hasCatNearby(MemoryForgeRitual ritual, Class<?> type) {
        var player = ritual.player();
        ServerLevel level = ritual.level();
        if (player == null || level == null) return false;
        if (type == CocoEntity.class) {
            return !level.getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(10.0),
                    c -> player.getUUID().equals(c.getOwnerUUID())).isEmpty();
        }
        if (type == JennaEntity.class) {
            return !level.getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(10.0),
                    j -> player.getUUID().equals(j.getOwnerUUID())).isEmpty();
        }
        return false;
    }
}
