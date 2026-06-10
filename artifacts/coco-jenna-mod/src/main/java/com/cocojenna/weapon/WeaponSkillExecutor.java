package com.cocojenna.weapon;

import com.cocojenna.item.RyokatanaEffectHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 執行 JSON 定義的武器主動技能. */
public final class WeaponSkillExecutor {

    private WeaponSkillExecutor() {}

    public static boolean tryCast(Player player, Level level, String variantId, int chargeTicks, ItemStack weapon) {
        var defOpt = WeaponSkillRegistry.get(variantId);
        if (defOpt.isEmpty()) return false;
        if (player instanceof ServerPlayer sp && !WeaponSkillHelper.tryConsumeMana(sp, variantId)) {
            return false;
        }
        WeaponSkillDefinition def = defOpt.get();
        int minCharge = def.chargeProfile().minTicks();
        if (chargeTicks < minCharge) return false;
        int stage = WeaponData.getStage(weapon).id;
        float power = def.damageMultForStage(stage) * WeaponData.skillPowerMultiplier(weapon);
        power *= 1f + def.chargeProfile().powerBonus(chargeTicks);
        power *= 1f + Math.min(1.0f, (chargeTicks - minCharge) / 50f);
        float radius = def.radiusMultForStage(stage) * (1f + def.chargeProfile().chargeTier(chargeTicks) * 0.08f);
        String force = player instanceof ServerPlayer sp
                ? com.cocojenna.capability.ModCapabilities.getOrDefault(sp).getFelineForce()
                : "resonance";
        var ctx = new WeaponSkillContext(player, level, def.variantId(), def.skillId(),
                weapon, stage, chargeTicks, power, radius, force);
        WeaponSkillVfx.onCast(player, ctx, def);
        if (!WeaponUniqueSkillLibrary.tryCast(player, level, ctx)) {
            RyokatanaEffectHelper.castArchetype(player, level, def.archetype(), power, radius);
        }
        if (player instanceof ServerPlayer sp) {
            var vfx = com.cocojenna.combat.CombatVfxHelper.of(force);
            int tier = Math.min(4, stage + 1);
            com.cocojenna.combat.CombatVfxHelper.skillCast(sp.serverLevel(), sp, vfx, tier, false);
        }
        return true;
    }
}
