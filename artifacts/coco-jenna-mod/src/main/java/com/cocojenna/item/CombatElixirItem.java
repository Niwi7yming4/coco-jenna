package com.cocojenna.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** 戰鬥輔助藥水（設計書第五章）. */
public class CombatElixirItem extends Item {

    private final MobEffect effect;
    private final int duration;
    private final int amplifier;

    public CombatElixirItem(Properties props, MobEffect effect, int duration) {
        this(props, effect, duration, 0);
    }

    public CombatElixirItem(Properties props, MobEffect effect, int duration, int amplifier) {
        super(props);
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }
        stack.shrink(1);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) { return 24; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
}
