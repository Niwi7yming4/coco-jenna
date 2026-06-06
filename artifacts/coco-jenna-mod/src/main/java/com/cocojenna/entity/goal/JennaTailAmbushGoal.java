package com.cocojenna.entity.goal;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

/** 尾巴偷襲 Goal — 珍奶找到閒置的可可後飛撲 */
public class JennaTailAmbushGoal extends Goal {

    private final JennaEntity jenna;
    private CocoEntity cocotarget;

    public JennaTailAmbushGoal(JennaEntity jenna) {
        this.jenna = jenna;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // 找附近的可可
        List<CocoEntity> cocos = jenna.level().getEntitiesOfClass(CocoEntity.class,
                jenna.getBoundingBox().inflate(5.0));
        if (cocos.isEmpty()) return false;

        CocoEntity coco = cocos.get(0);

        // 可可需要閒置（沒有使用 AI goal）
        Player owner = jenna.level().getNearestPlayer(jenna, 50.0);
        if (owner == null) return false;

        BondData bond = ModCapabilities.getOrDefault(owner);
        if (bond.getSisterBond() < 40) return false;
        if (bond.getJennaPlayfulness() < 60) return false;
        if (jenna.getPlayfulness() < 60) return false;

        // 珍奶不餓（情感 ≥ 2）
        if (bond.getJennaEmotionLevel().ordinal() < BondData.EmotionLevel.ATTACHED.ordinal()) return false;

        cocotarget = coco;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return cocotarget != null && !cocotarget.isRemoved()
                && jenna.distanceTo(cocotarget) > 1.0;
    }

    @Override
    public void start() {
        if (cocotarget != null) {
            jenna.getNavigation().moveTo(cocotarget, 1.4);
        }
    }

    @Override
    public void tick() {
        if (cocotarget == null) return;
        jenna.getNavigation().moveTo(cocotarget, 1.4);

        if (jenna.distanceTo(cocotarget) < 1.5) {
            Player owner = jenna.level().getNearestPlayer(jenna, 50.0);
            if (owner != null) {
                BondData bond = ModCapabilities.getOrDefault(owner);
                jenna.performTailAmbush(cocotarget, bond);
            }
            cocotarget = null;
        }
    }

    @Override
    public void stop() { cocotarget = null; }
}
