package com.cocojenna.world;

import com.cocojenna.blackmud.BlackMudCorruptionManager;
import com.cocojenna.combat.CombatVfxHelper;
import com.cocojenna.entity.BlackMudBossEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public final class BlackMudBossHelper {

    private BlackMudBossHelper() {}

    public static boolean trySpawnBoss(ServerLevel level, EntityType<?> type, BlockPos pos) {
        if (!(type.create(level) instanceof BlackMudBossEntity probe)) {
            return false;
        }
        String bossId = probe.bossKind().id;
        probe.discard();
        if (!BlackMudCorruptionManager.isBossAlive(level, bossId)) return false;
        var mob = type.create(level);
        if (mob == null) return false;
        mob.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (level.addFreshEntity(mob)) {
            CombatVfxHelper.bossIntro(level, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), bossId);
            return true;
        }
        return false;
    }
}
