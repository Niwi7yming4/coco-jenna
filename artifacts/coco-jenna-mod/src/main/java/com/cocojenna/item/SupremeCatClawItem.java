package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 無上・絕對貓爪 — 最強武器（僅能合成一把）
 */
public class SupremeCatClawItem extends SwordItem {

    public SupremeCatClawItem(Properties props) {
        super(Tiers.NETHERITE, 18, -2.0f, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.level().getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0),
                        e -> e != target && e != attacker)
                .forEach(e -> e.hurt(attacker.level().damageSources().magic(), 5.0f));

        target.hurt(attacker.level().damageSources().magic(), 8.0f);

        attacker.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.CRIT,
                target.getX(), target.getY() + 1, target.getZ(), 0, 0.5, 0);

        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.supreme_cat_claw.lore1")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltip.add(Component.translatable("item.cocojenna.supreme_cat_claw.lore2")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.cocojenna.supreme_cat_claw.lore3")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
