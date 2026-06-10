package com.cocojenna.weapon;

import java.util.List;

/** JSON 驅動的武器技能定義. */
public record WeaponSkillDefinition(
        String variantId,
        String skillId,
        String archetype,
        int minChargeTicks,
        int cooldownTicks,
        float manaCost,
        String vfxTheme,
        String soundKey,
        String chargeProfileId,
        List<StageEffect> stageEffects
) {
    public record StageEffect(int stage, float damageMult, float radiusMult, int particleTier) {}

    public WeaponChargeProfile chargeProfile() {
        return WeaponChargeProfile.fromId(chargeProfileId);
    }

    public int particleTierForStage(int stage) {
        return stageEffects.stream()
                .filter(e -> e.stage() == stage)
                .map(StageEffect::particleTier)
                .findFirst()
                .orElse(1 + stage);
    }

    public float damageMultForStage(int stage) {
        return stageEffects.stream()
                .filter(e -> e.stage() == stage)
                .map(StageEffect::damageMult)
                .findFirst()
                .orElse(1f + stage * 0.1f);
    }

    public float radiusMultForStage(int stage) {
        return stageEffects.stream()
                .filter(e -> e.stage() == stage)
                .map(StageEffect::radiusMult)
                .findFirst()
                .orElse(1f + stage * 0.05f);
    }
}
