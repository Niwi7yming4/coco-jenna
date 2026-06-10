package com.cocojenna.weapon;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Resolves per-weapon skill timing and resource costs from JSON definitions. */
public final class WeaponSkillHelper {

    private WeaponSkillHelper() {}

    public static int cooldownTicks(String variantId, ItemStack weapon, int fallback) {
        int base = WeaponSkillRegistry.get(variantId)
                .map(WeaponSkillDefinition::cooldownTicks)
                .orElse(fallback);
        return WeaponData.skillCooldownTicks(weapon, base);
    }

    public static int minChargeTicks(String variantId, int fallback) {
        return WeaponSkillRegistry.get(variantId)
                .map(WeaponSkillDefinition::minChargeTicks)
                .orElse(fallback);
    }

    /**
     * Maps design-doc mana to hunger saturation (5 mana = 1 food).
     * Creative players bypass the cost.
     */
    public static boolean tryConsumeMana(ServerPlayer player, String variantId) {
        float mana = WeaponSkillRegistry.get(variantId)
                .map(WeaponSkillDefinition::manaCost)
                .orElse(0f);
        if (mana <= 0f || player.getAbilities().instabuild) {
            return true;
        }
        int hungerCost = Math.max(1, (int) Math.ceil(mana / 5f));
        if (player.getFoodData().getFoodLevel() >= hungerCost) {
            player.getFoodData().eat(-hungerCost, 0f);
            return true;
        }
        player.displayClientMessage(
                Component.translatable("weapon.cocojenna.need_mana", (int) mana), true);
        return false;
    }
}
