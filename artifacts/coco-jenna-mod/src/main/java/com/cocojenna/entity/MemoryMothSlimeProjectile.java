package com.cocojenna.entity;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** 回憶蛀蟲粘液彈 — 降低序列 1 級 30 秒. */
public class MemoryMothSlimeProjectile extends ThrowableItemProjectile {

    public MemoryMothSlimeProjectile(EntityType<? extends MemoryMothSlimeProjectile> type, Level level) {
        super(type, level);
    }

    public MemoryMothSlimeProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.MEMORY_MOTH_SLIME.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLIND_WATER_GEL.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!(result.getEntity() instanceof LivingEntity living)) return;
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        if (living instanceof net.minecraft.world.entity.player.Player player) {
            var bond = ModCapabilities.getOrDefault(player);
            int debuff = Math.min(9, bond.getFelineTier() + 1);
            player.getPersistentData().putInt("cocojenna_seq_debuff", debuff);
            player.getPersistentData().putInt("cocojenna_seq_debuff_ticks", 600);
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
        }
        discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) discard();
    }
}
