package com.cocojenna.entity;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.guardian.GuardianTransferHelper;
import com.cocojenna.item.RyokatanaRegistry;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenAlphaExchangePacket;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import net.minecraftforge.network.PacketDistributor;
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

/** Alpha — 系統觀察者，守護者轉移儀式. */
public class AlphaEntity extends PathfinderMob {

    private boolean gifted;

    public AlphaEntity(EntityType<? extends AlphaEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 999.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 16.0f));
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
            if (player.isShiftKeyDown() && player.isSecondaryUseActive()
                    && AfterRainGameplayManager.isPeaceMode(sp)) {
                AfterRainGameplayManager.openKingdomTerminal(sp);
            } else if (player.isShiftKeyDown()) {
                com.cocojenna.quest.KingdomTutorialManager.onAlphaTalk(sp);
                DialogueManager.play(sp, "alpha_greeting");
            } else if (player.isSecondaryUseActive()) {
                if (GuardianTransferHelper.canVoluntaryTransfer(sp)) {
                    ServerPlayer nearest = findNearestOtherPlayer(sp);
                    if (nearest != null) {
                        GuardianTransferHelper.transfer(sp, nearest);
                    }
                } else if (GuardianTransferHelper.canClaimInactiveGuardianship(sp)) {
                    GuardianTransferHelper.claimInactiveGuardianship(sp);
                }
            } else {
                var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(sp);
                if (com.cocojenna.sequence.MoonCrossroadsManager.canStartQuest(sp, bond)
                        || bond.getForceQuestStage()
                        == com.cocojenna.sequence.MoonCrossroadsManager.STAGE_CHOOSE) {
                    com.cocojenna.sequence.MoonCrossroadsManager.tryStartFromAlpha(sp);
                } else if (!gifted) {
                    var ro = RyokatanaRegistry.get("alpha_observe");
                    ItemStack blade = new ItemStack(ro.get());
                    if (!sp.addItem(blade)) sp.drop(blade, false);
                    gifted = true;
                    HiddenSequenceRegistry.tryUnlock(sp, "alpha_observer");
                } else if (AfterRainGameplayManager.isPeaceMode(sp)) {
                    ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                            new OpenAlphaExchangePacket());
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    private ServerPlayer findNearestOtherPlayer(ServerPlayer self) {
        ServerPlayer best = null;
        double bestDist = Double.MAX_VALUE;
        for (ServerPlayer p : self.server.getPlayerList().getPlayers()) {
            if (p == self || !p.level().dimension().equals(self.level().dimension())) continue;
            double d = p.distanceToSqr(self);
            if (d < bestDist && d < 64) {
                bestDist = d;
                best = p;
            }
        }
        return best;
    }
}
