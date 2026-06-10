package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.entity.BlackMudBossEntity;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/** 隱藏序列解鎖後的實戰／建造加成（設計書隱藏序列專屬武器線）. */
public final class HiddenSequenceBonuses {

    private HiddenSequenceBonuses() {}

    public static float damageMultiplier(ServerPlayer player, BondData bond, LivingEntity target) {
        float mult = 1f;
        if (HiddenSequenceRegistry.has(bond, "primal_survivor") && target instanceof BlackMudMob) {
            mult += 0.10f;
        }
        if (HiddenSequenceRegistry.has(bond, "defeated_stray")
                && target.isInvertedHealAndHarm()) {
            mult += 0.08f;
        }
        if (target instanceof BlackMudBossEntity boss) {
            if (HiddenSequenceRegistry.has(bond, "grief_amalgam")
                    && boss.bossKind() == BlackMudBossEntity.BossKind.GRIEF_AMALGAM) {
                mult += 0.12f;
            }
            if (HiddenSequenceRegistry.has(bond, "blind_water_lord")
                    && boss.bossKind() == BlackMudBossEntity.BossKind.BLIND_WATER_LORD) {
                mult += 0.12f;
            }
            if (HiddenSequenceRegistry.has(bond, "fallen_velvet")
                    && boss.bossKind() == BlackMudBossEntity.BossKind.FALLEN_VELVET) {
                mult += 0.12f;
            }
            if (HiddenSequenceRegistry.has(bond, "primal_chaos")
                    && boss.bossKind() == BlackMudBossEntity.BossKind.PRIMAL_CHAOS) {
                mult += 0.15f;
            }
        }
        if (HiddenSequenceRegistry.has(bond, "sleep_cathedral_ghost")
                && player.level().isNight()) {
            mult += 0.05f;
        }
        if (HiddenSequenceRegistry.has(bond, "white_glove_soul")
                && player.isInWater()) {
            mult += 0.08f;
        }
        if (HiddenSequenceRegistry.has(bond, "moon_alley_thief")
                && player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) {
            mult += 0.04f;
        }
        return mult;
    }

    public static float skillPowerMultiplier(BondData bond) {
        float mult = 1f;
        if (HiddenSequenceRegistry.has(bond, "hibiscus_distiller")) mult += 0.10f;
        if (HiddenSequenceRegistry.has(bond, "imaginary_walker")) mult += 0.08f;
        if (HiddenSequenceRegistry.has(bond, "velvet_dreamer")) mult += 0.06f;
        if (HiddenSequenceRegistry.has(bond, "first_cry_oracle")) mult += 0.05f;
        return mult;
    }

    public static float cooldownMultiplier(BondData bond) {
        float mult = 1f;
        if (HiddenSequenceRegistry.has(bond, "imaginary_walker")) mult *= 0.90f;
        if (HiddenSequenceRegistry.has(bond, "labyrinth_cartographer")) mult *= 0.95f;
        return mult;
    }

    public static int buildProgressBonus(BondData bond) {
        int bonus = 0;
        if (HiddenSequenceRegistry.has(bond, "gear_orphan")) bonus += 2;
        return bonus;
    }

    public static float corruptionSpreadMultiplier(ServerLevel level) {
        float mult = 1f;
        for (ServerPlayer p : level.players()) {
            BondData bond = com.cocojenna.capability.ModCapabilities.getOrDefault(p);
            if (HiddenSequenceRegistry.has(bond, "primal_survivor")) mult *= 0.92f;
            if (HiddenSequenceRegistry.has(bond, "hibiscus_distiller")) mult *= 0.95f;
        }
        return mult;
    }
}
