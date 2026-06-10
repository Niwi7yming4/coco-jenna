package com.cocojenna.entity.goal;

import com.cocojenna.entity.JennaEntity;
import com.cocojenna.exploration.DungeonPuzzleManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 珍奶好奇謎題 — 跑向提示石碑（設計書 3.3）. */
public class JennaPuzzleHintGoal extends Goal {

    private final JennaEntity jenna;
    private int cooldown;

    public JennaPuzzleHintGoal(JennaEntity jenna) {
        this.jenna = jenna;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        Player owner = jenna.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return false;
        if (com.cocojenna.capability.ModCapabilities.getOrDefault(owner).getJennaCuriosity() < 42f) {
            return false;
        }
        return DungeonPuzzleManager.nearestUnsolvedHint(sp, 28).isPresent();
    }

    @Override
    public void tick() {
        Player owner = jenna.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return;
        DungeonPuzzleManager.nearestUnsolvedHint(sp, 28).ifPresent(pos -> {
            jenna.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.05);
            jenna.setSpecialAnimation(JennaEntity.ANIM_CURIOUS_PEEK);
            cooldown = 50;
        });
    }
}
