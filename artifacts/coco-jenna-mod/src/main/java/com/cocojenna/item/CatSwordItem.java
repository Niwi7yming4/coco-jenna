package com.cocojenna.item;

import net.minecraft.world.item.*;

/** 貓之國劍型武器基底類別。每種劍有特定被動效果，透過子類覆寫實現。 */
public class CatSwordItem extends SwordItem {

    private final String effectId;

    public CatSwordItem(Tier tier, int dmgBonus, float atkSpeed, Properties props, String effectId) {
        super(tier, dmgBonus, atkSpeed, props);
        this.effectId = effectId;
    }

    /** 魚骨刃：命中後敵人減速 15%，3 秒 */
    @Override
    public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target,
            net.minecraft.world.entity.LivingEntity attacker) {
        if ("fish_bone".equals(effectId)) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, true));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public String getEffectId() { return effectId; }
}
