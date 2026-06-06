package com.cocojenna.entity.goal;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.JennaEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** 舔腳踝 Goal — 玩家靜止 8 秒後珍奶繞腳一圈並施加抗性效果 */
public class JennaLickAnkleGoal extends Goal {

    private final JennaEntity jenna;
    private Player target;
    private int stillTick = 0;

    public JennaLickAnkleGoal(JennaEntity jenna) {
        this.jenna = jenna;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player owner = jenna.level().getNearestPlayer(jenna, 3.0);
        if (owner == null) return false;

        BondData bond = ModCapabilities.getOrDefault(owner);
        if (bond.getJennaEmotionLevel().ordinal() < BondData.EmotionLevel.ATTACHED.ordinal()) return false;
        if (jenna.distanceTo(owner) > 2.0) return false;

        if (owner.getDeltaMovement().length() > 0.01) {
            stillTick = 0;
            return false;
        }

        stillTick++;
        if (stillTick < 160) return false; // 8 秒

        target = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() { return false; }

    @Override
    public void start() {
        if (target != null) {
            BondData bond = ModCapabilities.getOrDefault(target);
            jenna.performLickAnkle(target, bond);
            stillTick = 0;
        }
    }
}
