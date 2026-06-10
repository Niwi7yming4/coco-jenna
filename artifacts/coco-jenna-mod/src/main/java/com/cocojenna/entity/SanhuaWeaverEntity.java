package com.cocojenna.entity;

import com.cocojenna.cloak.CloakDefinition;
import com.cocojenna.endgame.schedule.AfterRainNpcRole;
import com.cocojenna.entity.goal.PeacefulNpcScheduleGoal;
import com.cocojenna.dialogue.DialogueManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/** 記憶紡織娘・三花 — 披風縫製 NPC. */
public class SanhuaWeaverEntity extends PathfinderMob {

    public SanhuaWeaverEntity(EntityType<? extends SanhuaWeaverEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PeacefulNpcScheduleGoal(this, AfterRainNpcRole.WEAVER));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            if (player.isShiftKeyDown()
                    && player.getMainHandItem().is(com.cocojenna.init.ModItems.PURR_CRYSTAL.get())) {
                com.cocojenna.quest.SanhuaEternalCloakManager.tryTurnInMaterials(sp);
            } else if (player.isShiftKeyDown()) {
                DialogueManager.play(sp, "sanhua_greeting");
            } else {
                NetworkHooks.openScreen(sp, new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new com.cocojenna.cloak.CloakWeaverMenu(id, inv),
                        Component.translatable("container.cocojenna.cloak_weaver")), blockPosition());
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
