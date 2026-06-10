package com.cocojenna.entity;

import com.cocojenna.combat.SpecialMobCombat;
import com.cocojenna.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 螢幕雜訊貓 — 中立，僅玩具錘／雜訊刀可擊破（§10.2）. */
public class GlitchCatEntity extends PathfinderMob {

    public GlitchCatEntity(EntityType<? extends GlitchCatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RandomStrollGoal(this, 0.55));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 10.0f));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (SpecialMobCombat.blocksDamage(this, source, amount)) {
            if (source.getEntity() instanceof Player p) {
                p.displayClientMessage(net.minecraft.network.chat.Component
                        .translatable("entity.cocojenna.glitch_cat.immune"), true);
            }
            return false;
        }
        return super.hurt(source, amount * SpecialMobCombat.bonusDamage(this, source));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && tickCount % 4 == 0) {
            level().addParticle(ParticleTypes.END_ROD,
                    getX() + (random.nextDouble() - 0.5) * 0.4,
                    getY() + 0.3 + random.nextDouble() * 0.4,
                    getZ() + (random.nextDouble() - 0.5) * 0.4,
                    0, 0.01, 0);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        spawnAtLocation(new ItemStack(ModItems.BLANK_MEMORY_CARD.get(), 1));
        if (random.nextFloat() < 0.35f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_PARTICLE.get(), 2 + random.nextInt(3)));
        }
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false;
    }
}
