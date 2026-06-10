package com.cocojenna.entity;

import com.cocojenna.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 遺忘之影 — 序列 8；接觸消除快捷欄一格 10 秒（§8.2）. */
public class ForgottenWispEntity extends Monster implements RangedAttackMob, BlackMudMob {

    private static final String STOLEN_SLOT = "cocojenna_stolen_slot";
    private static final String STOLEN_ITEM = "cocojenna_stolen_item";
    private static final String STOLEN_COUNT = "cocojenna_stolen_count";
    private static final String STOLEN_TICKS = "cocojenna_stolen_ticks";

    @Override
    public int blackMudSequence() { return 8; }

    public ForgottenWispEntity(EntityType<? extends ForgottenWispEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.SuicideExplosionGoal(this));
        goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 12.0f));
        goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof Player player) {
            stealHotbarSlot(player);
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.BLINDNESS, 60, 0, false, true));
        }
        return hit;
    }

    public static void stealHotbarSlot(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains(STOLEN_TICKS)) return;
        int slot = player.getInventory().selected;
        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.isEmpty()) return;
        tag.putInt(STOLEN_SLOT, slot);
        tag.putString(STOLEN_ITEM, net.minecraftforge.registries.ForgeRegistries.ITEMS
                .getKey(stack.getItem()).toString());
        tag.putInt(STOLEN_COUNT, stack.getCount());
        player.getInventory().setItem(slot, ItemStack.EMPTY);
        tag.putInt(STOLEN_TICKS, 200);
        player.displayClientMessage(Component.translatable("entity.cocojenna.forgotten_wisp.steal"), true);
    }

    public static void tickStolenRestore(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(STOLEN_TICKS)) return;
        int ticks = tag.getInt(STOLEN_TICKS) - 1;
        if (ticks > 0) {
            tag.putInt(STOLEN_TICKS, ticks);
            return;
        }
        int slot = tag.getInt(STOLEN_SLOT);
        var key = net.minecraft.resources.ResourceLocation.tryParse(tag.getString(STOLEN_ITEM));
        int count = tag.getInt(STOLEN_COUNT);
        if (key != null) {
            var item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(key);
            if (item != null) {
                ItemStack restore = new ItemStack(item, count);
                if (!player.addItem(restore)) player.drop(restore, false);
            }
        }
        tag.remove(STOLEN_SLOT);
        tag.remove(STOLEN_ITEM);
        tag.remove(STOLEN_COUNT);
        tag.remove(STOLEN_TICKS);
        player.displayClientMessage(Component.translatable("entity.cocojenna.forgotten_wisp.restore"), true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            setDeltaMovement(getDeltaMovement().x, Math.sin(level().getGameTime() * 0.08) * 0.02,
                    getDeltaMovement().z);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        Vec3 dir = target.position().subtract(position()).normalize();
        SmallFireball ball = new SmallFireball(level(), this, dir.x, dir.y + 0.2, dir.z);
        ball.setPos(getX(), getEyeY() - 0.2, getZ());
        level().addFreshEntity(ball);
        if (target instanceof Player player && distanceToSqr(player) < 9) {
            stealHotbarSlot(player);
        }
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        var nav = new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source,
            int looting, boolean recentlyHit) {
        if (random.nextFloat() < 0.35f + looting * 0.08f) {
            spawnAtLocation(new ItemStack(ModItems.MEMORY_SHARD.get()));
        }
    }
}
