package com.cocojenna.entity.goal;

import com.cocojenna.entity.CocoEntity;
import com.cocojenna.exploration.ExplorationGuideManager;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 可可危險感知 — 尾巴豎起、指向隱藏入口（設計書 5.1）. */
public class CocoDangerSenseGoal extends Goal {

    private final CocoEntity coco;
    private int senseCooldown;

    public CocoDangerSenseGoal(CocoEntity coco) {
        this.coco = coco;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (senseCooldown > 0) {
            senseCooldown--;
            return false;
        }
        Player owner = coco.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return false;
        var bond = ModCapabilities.getOrDefault(owner);
        if (bond.getCocoProtectiveness() < 35f) return false;
        return ExplorationGuideManager.nearestHidden(sp, 20).isPresent();
    }

    @Override
    public void tick() {
        Player owner = coco.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return;
        ExplorationGuideManager.nearestHidden(sp, 20).ifPresent(pos -> {
            coco.getLookControl().setLookAt(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            if (coco.distanceToSqr(owner) > 16) {
                coco.getNavigation().moveTo(owner, 1.0);
            }
            coco.setSpecialAnimation(CocoEntity.ANIM_STILL_GUARD);
            ExplorationGuideManager.tickPlayer(sp);
            senseCooldown = 80;
        });
    }
}
