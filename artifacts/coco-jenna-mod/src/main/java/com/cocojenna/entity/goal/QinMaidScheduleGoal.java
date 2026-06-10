package com.cocojenna.entity.goal;

import com.cocojenna.entity.AFangEntity;
import com.cocojenna.entity.LiJiangEntity;
import com.cocojenna.entity.QinKemuEntity;
import com.cocojenna.quest.qin.QinMaidSchedules;
import com.cocojenna.quest.qin.QinTriangleDialogueManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.Function;

/** 始皇貓一行日程：沏茶、摺紙、守陵、惡作劇. */
public class QinMaidScheduleGoal extends Goal {

    private final net.minecraft.world.entity.PathfinderMob mob;
    private final Function<Long, QinMaidSchedules.MaidActivity> resolver;
    private QinMaidSchedules.MaidActivity current = QinMaidSchedules.MaidActivity.WANDER;
    private BlockPos target;
    private int repathCooldown;

    public QinMaidScheduleGoal(QinKemuEntity qin) {
        this.mob = qin;
        this.resolver = QinMaidSchedules::forQin;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public QinMaidScheduleGoal(AFangEntity afang) {
        this.mob = afang;
        this.resolver = QinMaidSchedules::forAFang;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public QinMaidScheduleGoal(LiJiangEntity lijiang) {
        this.mob = lijiang;
        this.resolver = QinMaidSchedules::forLiJiang;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(mob.level() instanceof ServerLevel sl)) return false;
        current = resolver.apply(dayTime(sl));
        return current != QinMaidSchedules.MaidActivity.SLEEP;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        if (!(mob.level() instanceof ServerLevel sl)) return;
        current = resolver.apply(dayTime(sl));
        if (--repathCooldown > 0) return;
        repathCooldown = 40;
        switch (current) {
            case TEA, PAPER_FOLD, GUARD -> wanderNear(6);
            case PRANK -> {
                if (mob.getRandom().nextInt(8) == 0) {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(0, 0.42, 0));
                }
                wanderNear(10);
            }
            case STARGAZE -> {
                mob.getLookControl().setLookAt(mob.getX(), mob.getY() + 20, mob.getZ());
                mob.getNavigation().stop();
            }
            default -> wanderNear(8);
        }
        if (mob instanceof QinKemuEntity qin && qin.isAwake()) {
            QinTriangleDialogueManager.tryAmbient(sl, qin);
        }
    }

    private void wanderNear(double radius) {
        if (target == null || mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) < 2) {
            double ox = (mob.getRandom().nextDouble() - 0.5) * radius * 2;
            double oz = (mob.getRandom().nextDouble() - 0.5) * radius * 2;
            target = mob.blockPosition().offset((int) ox, 0, (int) oz);
        }
        mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0.55);
    }

    private static long dayTime(ServerLevel level) {
        return level.getDayTime() % 24000L;
    }
}
