package com.cocojenna.entity;

import com.cocojenna.init.ModItems;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 絨蛾 (Velvet Moth) 🦋 — 被珍奶撲擊可獲得絨蛾鱗粉。
 * 無害，在 Velvet Forest 飛翔。
 */
public class VelvetMothEntity extends FlyingMob {

    public VelvetMothEntity(EntityType<? extends VelvetMothEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45)
                .add(Attributes.FOLLOW_RANGE, 8.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0f));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 4.0f, 1.3, 1.5));
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        net.minecraft.world.entity.ai.navigation.FlyingPathNavigation nav =
                new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        // 珍奶撲擊時掉落鱗粉（由 JennaEntity 的撲擊行為觸發，此處為普通死亡掉落）
        if (random.nextFloat() < 0.5f + looting * 0.1f) {
            spawnAtLocation(new ItemStack(ModItems.MOTH_SCALE_POWDER.get(),
                    1 + random.nextInt(2)));
        }
    }

    @Override
    public net.minecraft.world.entity.MoverType getMoverType() {
        return MoverType.SELF;
    }
}
