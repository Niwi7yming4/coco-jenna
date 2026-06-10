package com.cocojenna.weapon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Runtime parameters for a single weapon skill cast. */
public record WeaponSkillContext(
        Player player,
        Level level,
        String variantId,
        String skillId,
        ItemStack weapon,
        int stage,
        int chargeTicks,
        float power,
        float radius,
        String sourceForce
) {
    public float scaledPower() {
        return power * radius;
    }
}
