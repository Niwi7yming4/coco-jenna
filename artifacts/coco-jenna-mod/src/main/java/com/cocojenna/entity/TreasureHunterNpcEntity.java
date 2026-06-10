package com.cocojenna.entity;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 寶藏獵人・鏽鼻 — 販售藏寶圖碎片（設計書 5.2）. */
public class TreasureHunterNpcEntity extends Mob {

    public TreasureHunterNpcEntity(EntityType<? extends TreasureHunterNpcEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            var bond = ModCapabilities.getOrDefault(sp);
            if (bond.getExplorationJournal().contains("treasure_hunter:gift")) {
                sp.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("explore.cocojenna.treasure_hunter.done"), true);
                return InteractionResult.sidedSuccess(false);
            }
            ItemStack map = new ItemStack(ModItems.MAP_FRAGMENT.get());
            if (!sp.addItem(map)) {
                sp.drop(map, false);
            }
            bond.addJournalEntry("treasure_hunter:gift");
            sp.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("explore.cocojenna.treasure_hunter.gift"), false);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
}
