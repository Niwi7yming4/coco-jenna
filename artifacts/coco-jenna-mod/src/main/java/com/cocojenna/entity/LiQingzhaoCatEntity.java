package com.cocojenna.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** 清照貓 — 梅樹下降噪光環（設計書附錄）. */
public class LiQingzhaoCatEntity extends PathfinderMob {

    public LiQingzhaoCatEntity(EntityType<? extends LiQingzhaoCatEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || tickCount % 40 != 0) return;
        AABB box = getBoundingBox().inflate(16);
        for (Player p : level().getEntitiesOfClass(Player.class, box)) {
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 0, true, false));
            if (p instanceof ServerPlayer sp && random.nextFloat() < 0.02f) {
                var page = new net.minecraft.world.item.ItemStack(
                        com.cocojenna.init.ModItems.ORIGAMI_SCRAP.get());
                if (!sp.addItem(page)) sp.drop(page, false);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp && !player.isShiftKeyDown()) {
            com.cocojenna.quest.qin.LiQingzhaoQuestManager.onPoetryInteract(sp);
            int idx = 1 + sp.getRandom().nextInt(5);
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("qingzhao.cocojenna.poetry." + idx), false);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    public static void trySpawnNearPlum(ServerLevel level, BlockPos pos) {
        if (level.random.nextInt(200) != 0) return;
        var entity = com.cocojenna.init.ModEntities.LI_QINGZHAO_CAT.get().create(level);
        if (entity != null) {
            entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(entity);
        }
    }
}
