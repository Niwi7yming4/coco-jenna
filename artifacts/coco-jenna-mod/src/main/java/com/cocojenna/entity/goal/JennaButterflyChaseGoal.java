package com.cocojenna.entity.goal;

import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 蝴蝶追逐 Goal（終局後）
 *
 * <p>白天戶外，珍奶追逐虛擬蝴蝶，跑到遠處再跑回來，
 * 嘴裡叼著一朵花給玩家。
 */
public class JennaButterflyChaseGoal extends Goal {

    private final JennaEntity jenna;
    private Vec3 chaseTarget;
    private boolean returning = false;
    private int chaseTick = 0;
    private Player owner;

    public JennaButterflyChaseGoal(JennaEntity jenna) {
        this.jenna = jenna;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!jenna.isEndgame()) return false;
        if (jenna.level().isNight()) return false;
        if (!jenna.isInSunlight()) return false;
        if (jenna.getRandom().nextFloat() > 0.002f) return false; // 低觸發率

        owner = jenna.level().getNearestPlayer(jenna, 30.0);
        if (owner == null) return false;

        // 選一個隨機目標位置（15‑25 格外）
        double angle = jenna.getRandom().nextDouble() * Math.PI * 2;
        double dist = 15 + jenna.getRandom().nextDouble() * 10;
        chaseTarget = jenna.position().add(
                Math.cos(angle) * dist, 0, Math.sin(angle) * dist);

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return chaseTick < 600 && owner != null;
    }

    @Override
    public void start() {
        returning = false;
        chaseTick = 0;
        if (chaseTarget != null) {
            jenna.getNavigation().moveTo(chaseTarget.x, chaseTarget.y, chaseTarget.z, 1.5);
        }
    }

    @Override
    public void tick() {
        chaseTick++;

        // 蝴蝶粒子效果
        if (chaseTick % 5 == 0) {
            jenna.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                    jenna.getX() + (jenna.getRandom().nextDouble() - 0.5),
                    jenna.getY() + 1,
                    jenna.getZ() + (jenna.getRandom().nextDouble() - 0.5),
                    0, 0.1, 0);
        }

        if (!returning) {
            if (chaseTarget != null && jenna.distanceToSqr(chaseTarget.x, chaseTarget.y, chaseTarget.z) < 4.0) {
                // 到達目標，準備返回
                returning = true;
                if (owner != null) {
                    jenna.getNavigation().moveTo(owner, 1.5);
                }
            }
        } else {
            // 返回玩家身邊，給予花朵
            if (owner != null && jenna.distanceTo(owner) < 2.0) {
                ItemStack flower = new ItemStack(ModItems.HIBISCUS_FLOWER_ITEM.get());
                owner.addItem(flower);
                jenna.level().addParticle(ParticleTypes.HEART,
                        jenna.getX(), jenna.getY() + 1, jenna.getZ(),
                        0, 0.2, 0);
                chaseTick = 600; // 結束行為
            }
        }
    }

    @Override
    public void stop() {
        returning = false;
        chaseTick = 0;
        owner = null;
    }
}
