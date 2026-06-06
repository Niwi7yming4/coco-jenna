package com.cocojenna.entity.goal;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/**
 * 抿你一口 (Bite of Trust) Goal
 *
 * <p>觸發條件：
 * <ul>
 *   <li>情感等級 ≥ Bonded（45‑69）</li>
 *   <li>玩家靜止 > 5 秒（100 tick）</li>
 *   <li>距離 < 1.5 格</li>
 *   <li>保護慾 > 50</li>
 *   <li>每日上限 3 次</li>
 * </ul>
 */
public class CocoBiteTrustGoal extends Goal {

    private final CocoEntity coco;
    private Player target;
    private int stillTick = 0;

    public CocoBiteTrustGoal(CocoEntity coco) {
        this.coco = coco;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player owner = (Player) coco.level().getNearestPlayer(coco, 2.0);
        if (owner == null) return false;

        BondData bond = ModCapabilities.getOrDefault(owner);

        // 情感等級檢查
        if (bond.getCocoEmotionLevel().ordinal() < BondData.EmotionLevel.BONDED.ordinal()) return false;

        // 保護慾檢查
        if (bond.getCocoProtectiveness() <= 50) return false;

        // 距離 < 1.5
        if (coco.distanceTo(owner) > 1.5) return false;

        // 玩家靜止
        if (owner.getDeltaMovement().length() > 0.01) {
            stillTick = 0;
            return false;
        }

        stillTick++;
        if (stillTick < 100) return false; // 5 秒

        target = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return false; // 單次觸發
    }

    @Override
    public void start() {
        if (target != null) {
            coco.performBiteTrust(target);
            stillTick = 0;
        }
    }
}
