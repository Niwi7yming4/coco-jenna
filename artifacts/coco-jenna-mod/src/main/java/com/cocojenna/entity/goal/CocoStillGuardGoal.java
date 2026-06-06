package com.cocojenna.entity.goal;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 靜止守護 Goal — 玩家 HP < 40% 時可可趴在玩家身邊提供被動回血 */
public class CocoStillGuardGoal extends Goal {

    private final CocoEntity coco;
    private Player guardTarget;

    public CocoStillGuardGoal(CocoEntity coco) {
        this.coco = coco;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player owner = coco.level().getNearestPlayer(coco, 20.0);
        if (owner == null) return false;
        BondData bond = ModCapabilities.getOrDefault(owner);
        if (bond.getCocoEmotionLevel().ordinal() < BondData.EmotionLevel.ATTACHED.ordinal()) return false;
        if (owner.getHealth() / owner.getMaxHealth() >= 0.4f) return false;
        guardTarget = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (guardTarget == null) return false;
        return guardTarget.getHealth() / guardTarget.getMaxHealth() < 0.6f;
    }

    @Override
    public void start() {
        if (guardTarget != null) {
            coco.getNavigation().moveTo(guardTarget, 1.2);
        }
    }

    @Override
    public void tick() {
        if (guardTarget == null) return;
        if (coco.distanceTo(guardTarget) < 2.0) {
            coco.getNavigation().stop();
        } else {
            coco.getNavigation().moveTo(guardTarget, 1.2);
        }
    }

    @Override
    public void stop() { guardTarget = null; }
}
