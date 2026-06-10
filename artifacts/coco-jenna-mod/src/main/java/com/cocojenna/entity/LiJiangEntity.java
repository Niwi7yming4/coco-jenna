package com.cocojenna.entity;

import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** 黎姜 — 侍女：惡作劇隱身、紅紙交易、指引皇陵. */
public class LiJiangEntity extends PathfinderMob {

    private int prankCooldown;

    public LiJiangEntity(EntityType<? extends LiJiangEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new com.cocojenna.entity.goal.QinMaidScheduleGoal(this));
        goalSelector.addGoal(2, new FollowQinKemuGoal(this));
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.35f));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.55));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && prankCooldown > 0) prankCooldown--;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) {
            if (player.isShiftKeyDown()) {
                if (!player.getAbilities().instabuild) {
                    ItemStack paper = new ItemStack(ModItems.RED_PAPER.get(), 2);
                    if (!player.addItem(paper)) player.drop(paper, false);
                }
                player.displayClientMessage(Component.translatable("qin.cocojenna.lijiang.trade"), false);
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (prankCooldown <= 0) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false));
                prankCooldown = 800;
                player.displayClientMessage(Component.translatable("qin.cocojenna.lijiang.prank"), true);
            } else {
                player.displayClientMessage(Component.translatable("qin.cocojenna.lijiang.rumor"), false);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** 共用跟隨秦可沐邏輯. */
    static class FollowQinKemuGoal extends Goal {
        private final PathfinderMob mob;
        private QinKemuEntity target;

        FollowQinKemuGoal(PathfinderMob mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            var list = mob.level().getEntitiesOfClass(QinKemuEntity.class,
                    mob.getBoundingBox().inflate(32));
            target = list.isEmpty() ? null : list.get(0);
            return target != null && mob.distanceToSqr(target) > 9;
        }

        @Override
        public void tick() {
            if (target != null) mob.getNavigation().moveTo(target, 1.1);
        }
    }
}
