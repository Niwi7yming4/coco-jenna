package com.cocojenna.entity;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.gamble.BlackjackGambleMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/** 牌局大師・黑傑克 — 命運博弈莊家（設計書 11.6）. */
public class BlackjackDealerEntity extends PathfinderMob {

    public BlackjackDealerEntity(EntityType<? extends BlackjackDealerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 12.0f));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            if (player.isShiftKeyDown()) {
                DialogueManager.play(sp, "blackjack_greeting");
            } else {
                NetworkHooks.openScreen(sp, new SimpleMenuProvider(
                        (id, inv, p) -> new BlackjackGambleMenu(id, inv),
                        Component.translatable("container.cocojenna.blackjack")));
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
