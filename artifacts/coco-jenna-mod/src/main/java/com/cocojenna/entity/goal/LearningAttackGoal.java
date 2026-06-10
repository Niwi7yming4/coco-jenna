package com.cocojenna.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

/** 學習型 AI — 記憶玩家武器並減傷同類攻擊（設計書 4.2）. */
public class LearningAttackGoal extends MeleeAttackGoal {

    private static final String LEARNED_WEAPON = "cocojenna_learned_weapon";
    private static final String LEARN_COUNT = "cocojenna_learn_count";

    public LearningAttackGoal(PathfinderMob mob, double speed, boolean pauseWhenIdle) {
        super(mob, speed, pauseWhenIdle);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target instanceof Player player && mob.tickCount % 10 == 0) {
            String weapon = ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem()).getPath();
            if (!weapon.isEmpty()) {
                var data = mob.getPersistentData();
                String learned = data.getString(LEARNED_WEAPON);
                if (weapon.equals(learned)) {
                    data.putInt(LEARN_COUNT, Math.min(5, data.getInt(LEARN_COUNT) + 1));
                } else {
                    data.putString(LEARNED_WEAPON, weapon);
                    data.putInt(LEARN_COUNT, 1);
                }
            }
        }
        super.tick();
    }

    public static float learnedDamageReduction(LivingEntity mob, String weaponId) {
        if (weaponId.isEmpty()) return 1f;
        var data = mob.getPersistentData();
        if (!weaponId.equals(data.getString(LEARNED_WEAPON))) return 1f;
        int count = data.getInt(LEARN_COUNT);
        return Math.max(0.55f, 1f - count * 0.08f);
    }
}
