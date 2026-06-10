package com.cocojenna.entity.goal;

import com.cocojenna.entity.TownNpcCompanionEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

/** 城鎮 NPC 互相理毛／對坐社交（MCA 風格）. */
public class CatSocializeGoal extends Goal {

    private final TownNpcCompanionEntity mob;
    private TownNpcCompanionEntity partner;
    private int socializeTicks;

    public CatSocializeGoal(TownNpcCompanionEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.getRandom().nextInt(8) != 0) return false;
        List<TownNpcCompanionEntity> nearby = mob.level().getEntitiesOfClass(
                TownNpcCompanionEntity.class, mob.getBoundingBox().inflate(10));
        for (TownNpcCompanionEntity other : nearby) {
            if (other != mob && other.getOwnerUUID() != null
                    && other.getOwnerUUID().equals(mob.getOwnerUUID())) {
                partner = other;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        socializeTicks = 80 + mob.getRandom().nextInt(60);
    }

    @Override
    public void stop() {
        partner = null;
    }

    @Override
    public boolean canContinueToUse() {
        return partner != null && partner.isAlive() && socializeTicks > 0;
    }

    @Override
    public void tick() {
        socializeTicks--;
        if (partner == null) return;
        mob.getLookControl().setLookAt(partner, 20.0f, 20.0f);
        if (mob.distanceToSqr(partner) > 2.5) {
            mob.getNavigation().moveTo(partner, 0.6);
        } else {
            mob.getNavigation().stop();
        }
    }
}
