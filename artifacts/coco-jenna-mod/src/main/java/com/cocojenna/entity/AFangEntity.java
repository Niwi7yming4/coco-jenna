package com.cocojenna.entity;

import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

/** 阿房 — 侍女：跟隨秦可沐、沏茶交易、安慰 buff. */
public class AFangEntity extends PathfinderMob {

    private int comfortCooldown;

    public AFangEntity(EntityType<? extends AFangEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.QinMaidScheduleGoal(this));
        goalSelector.addGoal(2, new FollowQinKemuGoal(this, 1.05, 8f, 2.5f));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.45));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && comfortCooldown > 0) comfortCooldown--;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            if (player.isShiftKeyDown()) {
                openTrade(sp);
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (comfortCooldown <= 0) {
                player.addEffect(new MobEffectInstance(ModEffects.AT_EASE.get(), 200, 0));
                comfortCooldown = 600;
                player.displayClientMessage(Component.translatable("qin.cocojenna.afang.comfort"), true);
            } else {
                player.displayClientMessage(Component.translatable("qin.cocojenna.afang.guide"), false);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private void openTrade(ServerPlayer player) {
        var offers = new MerchantOffers();
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY, new ItemStack(ModItems.MINT_MILK_CHOCOLATE.get(), 1), 8, 5, 0.05f));
        offers.add(new MerchantOffer(new ItemStack(ModItems.RED_PAPER.get(), 3),
                ItemStack.EMPTY, new ItemStack(Items.GOLDEN_APPLE, 1), 4, 10, 0.1f));
        player.sendSystemMessage(Component.translatable("qin.cocojenna.afang.trade"));
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** 跟隨最近的秦可沐. */
    static class FollowQinKemuGoal extends Goal {
        private final AFangEntity mob;
        private final double speed;
        private final float stopDistance;
        private final float startDistance;
        private QinKemuEntity target;

        FollowQinKemuGoal(AFangEntity mob, double speed, float stop, float start) {
            this.mob = mob;
            this.speed = speed;
            this.stopDistance = stop;
            this.startDistance = start;
        }

        @Override
        public boolean canUse() {
            var list = mob.level().getEntitiesOfClass(QinKemuEntity.class,
                    mob.getBoundingBox().inflate(32));
            target = list.isEmpty() ? null : list.get(0);
            return target != null && mob.distanceToSqr(target) > stopDistance * stopDistance;
        }

        @Override
        public void tick() {
            if (target != null) mob.getNavigation().moveTo(target, speed);
        }
    }
}
