package com.cocojenna.entity.goal;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.exploration.ExplorationGuideManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 珍奶好奇心 — 跑向隱藏寶箱／鬆動牆壁並回頭叫（設計書 5.1）. */
public class JennaCuriosityGoal extends Goal {

    private final JennaEntity jenna;
    private int cooldown;

    public JennaCuriosityGoal(JennaEntity jenna) {
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
        float curiosity = ModCapabilities.getOrDefault(owner).getJennaCuriosity();
        if (curiosity < 45f) return false;
        return ExplorationGuideManager.nearestHidden(sp, 28).isPresent();
    }

    @Override
    public void tick() {
        Player owner = jenna.getOwner();
        if (!(owner instanceof ServerPlayer sp)) return;
        ExplorationGuideManager.nearestHidden(sp, 28).ifPresent(pos -> {
            jenna.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.15);
            jenna.setSpecialAnimation(JennaEntity.ANIM_CURIOUS_PEEK);
            if (jenna.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 9) {
                jenna.getLookControl().setLookAt(owner, 30f, 30f);
                ExplorationGuideManager.tickPlayer(sp);
                cooldown = 100;
            }
        });
    }
}
