package com.cocojenna.combat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.FallenVelvetEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** 敵方弱點與情感反應 — 設計書 4.3–4.5. */
public final class EnemyWeaknessHelper {

    private EnemyWeaknessHelper() {}

    public static float damageMultiplier(Player attacker, LivingEntity target, float baseDamage) {
        float mult = 1f;
        mult *= backstabMultiplier(attacker, target);
        mult *= emotionalMultiplier(attacker, target);
        mult *= coreExposureMultiplier(target, baseDamage);
        return mult;
    }

    private static float backstabMultiplier(Player attacker, LivingEntity target) {
        Vec3 look = target.getViewVector(1f).normalize();
        Vec3 toAttacker = attacker.position().subtract(target.position()).normalize();
        double dot = look.dot(toAttacker);
        if (dot > 0.35) return 1f;
        String path = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES
                .getKey(target.getType()).getPath();
        if (path.contains("rust") || path.contains("bronze") || path.contains("golem")) {
            return 2f;
        }
        return 1.25f;
    }

    private static float emotionalMultiplier(Player attacker, LivingEntity target) {
        if (!(target instanceof FallenVelvetEntity)) return 1f;
        boolean catsNear = attacker.level().getEntitiesOfClass(AbstractCatEntity.class,
                target.getBoundingBox().inflate(10), e -> e instanceof CocoEntity || e.isAlive()).size() > 0;
        if (!catsNear) return 1f;
        BondData bond = ModCapabilities.getOrDefault(attacker);
        if (bond.getCocoEmotionLevel().ordinal() >= BondData.EmotionLevel.ATTACHED.ordinal()) {
            return 1.3f;
        }
        return 1f;
    }

    private static float coreExposureMultiplier(LivingEntity target, float damage) {
        if (target instanceof BlackMudMob && DistillCombatManager.isCoreExposed(target)) {
            return 3f;
        }
        return 1f;
    }

    public static boolean shouldPauseForLowHp(LivingEntity mob, Player player) {
        return player.getHealth() / player.getMaxHealth() < 0.1f
                && mob instanceof BlackMudMob bm && bm.blackMudSequence() >= 6;
    }
}
