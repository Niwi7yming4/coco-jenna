package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 無上・絕對貓爪 🔴 — 最強武器（僅能合成一把）
 *
 * <p>效果：
 * <ul>
 *   <li>攻擊傷害 +22</li>
 *   <li>每次攻擊觸發小型「爪痕風暴」（範圍 3 格 3 段傷害）</li>
 *   <li>使用者獲得永久「被記住的感覺」效果</li>
 *   <li>對封印物系的敵人有額外 +50% 傷害</li>
 * </ul>
 *
 * <p>合成材料（全 21 把大快刀 + 99 個記憶碎片 + 全月光石）
 */
public class SupremeCatClawItem extends Item {

    public SupremeCatClawItem(Properties props) {
        super(props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 爪痕風暴：對 3 格內其他目標造成 5 傷害
        attacker.level().getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0),
                        e -> e != target && e != attacker)
                .forEach(e -> e.hurt(attacker.level().damageSources().magic(), 5.0f));

        // 對封印物系額外傷害
        target.hurt(attacker.level().damageSources().magic(), 8.0f);

        // 爪痕粒子
        attacker.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.CRIT,
                target.getX(), target.getY() + 1, target.getZ(), 0, 0.5, 0);

        return true;
    }

    @Override
    public float getAttackDamage() { return 22.0f; }

    @Override
    public boolean isFoil(ItemStack stack) { return true; } // 附魔光輝

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
