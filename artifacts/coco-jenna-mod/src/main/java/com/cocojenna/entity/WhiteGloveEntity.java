package com.cocojenna.entity;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.schedule.AfterRainNpcRole;
import com.cocojenna.entity.goal.PeacefulNpcScheduleGoal;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.item.RyokatanaRegistry;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import com.cocojenna.world.ForgottenTowerGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 白手套 — 盲水擺渡人. */
public class WhiteGloveEntity extends PathfinderMob {

    private boolean gifted;

    public WhiteGloveEntity(EntityType<? extends WhiteGloveEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PeacefulNpcScheduleGoal(this, AfterRainNpcRole.FISHER));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 12.0f));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Gifted", gifted);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        gifted = tag.getBoolean("Gifted");
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            if (player.isShiftKeyDown()) {
                DialogueManager.play(sp, "white_glove_greeting");
            } else {
                BlockPos dest = ForgottenTowerGenerator.FERRY_LANDING;
                if (sp.level().dimension().equals(ModDimensions.CAT_KINGDOM)) {
                    sp.teleportTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5);
                    HiddenSequenceRegistry.tryUnlock(sp, "white_glove_soul");
                    if (!gifted && sp.experienceLevel >= 20) {
                        var ro = RyokatanaRegistry.get("white_glove_guide");
                        ItemStack blade = new ItemStack(ro.get());
                        if (!sp.addItem(blade)) sp.drop(blade, false);
                        gifted = true;
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
