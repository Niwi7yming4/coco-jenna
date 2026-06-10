package com.cocojenna.entity;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.schedule.AfterRainNpcRole;
import com.cocojenna.entity.goal.PeacefulNpcScheduleGoal;
import com.cocojenna.item.RyokatanaRegistry;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import com.cocojenna.shop.CheshireBlackMarketMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 柴郡 — 黑市商人（贈刀／交易）. */
public class CheshireEntity extends PathfinderMob {

    private boolean gifted;

    public CheshireEntity(EntityType<? extends CheshireEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PeacefulNpcScheduleGoal(this, AfterRainNpcRole.MERCHANT));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.55));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0f));
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
                DialogueManager.play(sp, "cheshire_greeting");
            } else if (player.isSecondaryUseActive() && !gifted && sp.experienceLevel >= 15) {
                var ro = RyokatanaRegistry.get("cheshire_grin");
                ItemStack blade = new ItemStack(ro.get());
                if (!sp.addItem(blade)) sp.drop(blade, false);
                gifted = true;
                HiddenSequenceRegistry.tryUnlock(sp, "cheshire_merchant");
            } else {
                NetworkHooks.openScreen(sp, new SimpleMenuProvider(
                        (id, inv, p) -> new CheshireBlackMarketMenu(id, inv),
                        Component.translatable("container.cocojenna.cheshire_black_market")));
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
