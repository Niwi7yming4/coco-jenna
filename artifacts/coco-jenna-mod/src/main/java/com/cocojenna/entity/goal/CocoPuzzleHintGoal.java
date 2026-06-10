package com.cocojenna.entity.goal;

import com.cocojenna.entity.CocoEntity;
import com.cocojenna.exploration.DungeonPuzzleManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 可可感知謎題石碑 — 豎耳指向序列提示（設計書 3.3）. */
public class CocoPuzzleHintGoal extends Goal {

    private final CocoEntity coco;
    private int cooldown;

    public CocoPuzzleHintGoal(CocoEntity coco) {
        this.coco = coco;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        Player owner = coco.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return false;
        if (com.cocojenna.capability.ModCapabilities.getOrDefault(owner).getCocoProtectiveness() < 38f) {
            return false;
        }
        return DungeonPuzzleManager.nearestUnsolvedHint(sp, 24).isPresent();
    }

    @Override
    public void tick() {
        Player owner = coco.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return;
        DungeonPuzzleManager.nearestUnsolvedHint(sp, 24).ifPresent(pos -> {
            coco.getLookControl().setLookAt(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            coco.setSpecialAnimation(CocoEntity.ANIM_STILL_GUARD);
            if (coco.distanceToSqr(owner) > 20) {
                coco.getNavigation().moveTo(owner.getX(), owner.getY(), owner.getZ(), 0.9);
            }
            cooldown = 60;
        });
    }
}
