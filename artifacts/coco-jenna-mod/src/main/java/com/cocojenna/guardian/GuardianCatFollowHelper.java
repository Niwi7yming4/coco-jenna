package com.cocojenna.guardian;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

/** 雙子星優先跟隨守護者（多人序列設計書）. */
public final class GuardianCatFollowHelper {

    private GuardianCatFollowHelper() {}

    public static void tick(ServerLevel level) {
        if (level.getGameTime() % 40 != 0) return;
        GuardianWorldData data = GuardianWorldData.get(level.getServer());
        if (!data.hasGuardian()) return;
        UUID guardianId = data.guardianUuid();
        ServerPlayer guardian = level.getServer().getPlayerList().getPlayer(guardianId);
        if (guardian == null || !guardian.level().dimension().equals(level.dimension())) return;

        AABB box = guardian.getBoundingBox().inflate(128);
        for (AbstractCatEntity cat : level.getEntitiesOfClass(AbstractCatEntity.class, box)) {
            if (!(cat instanceof CocoEntity) && !(cat instanceof JennaEntity)) continue;
            UUID owner = cat.getOwnerUUID();
            if (owner == null || owner.equals(guardianId)) continue;
            ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
            if (ownerPlayer != null && ownerPlayer.level().dimension().equals(level.dimension())
                    && ownerPlayer.distanceToSqr(cat) < guardian.distanceToSqr(cat)) {
                continue;
            }
            if (cat.distanceToSqr(guardian) > 12 * 12) {
                cat.getNavigation().moveTo(guardian, 1.0);
            }
        }
    }
}
