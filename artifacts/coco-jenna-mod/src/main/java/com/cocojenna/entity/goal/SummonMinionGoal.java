package com.cocojenna.entity.goal;

import com.cocojenna.init.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;

import java.util.EnumSet;

/** 召喚型 AI — 週期性召喚小怪（設計書 4.2）. */
public class SummonMinionGoal extends Goal {

    private final Monster summoner;
    private int cooldown;

    public SummonMinionGoal(Monster summoner) {
        this.summoner = summoner;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = summoner.getTarget();
        return target != null && cooldown-- <= 0 && summoner.level().getEntitiesOfClass(
                Monster.class, summoner.getBoundingBox().inflate(16),
                m -> m != summoner && m.getPersistentData().getBoolean("cocojenna_summoned")).size() < 3;
    }

    @Override
    public void start() {
        cooldown = 200 + summoner.getRandom().nextInt(120);
        if (!(summoner.level() instanceof ServerLevel level)) return;
        var type = ModEntities.HEAT_LEECH.get();
        Monster minion = type.create(level);
        if (minion == null) return;
        minion.moveTo(summoner.getX() + 1, summoner.getY(), summoner.getZ() + 1, 0, 0);
        minion.getPersistentData().putBoolean("cocojenna_summoned", true);
        if (summoner.getTarget() != null) {
            minion.setTarget(summoner.getTarget());
        }
        level.addFreshEntity(minion);
    }
}
