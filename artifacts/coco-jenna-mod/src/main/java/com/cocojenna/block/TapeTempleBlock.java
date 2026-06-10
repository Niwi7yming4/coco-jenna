package com.cocojenna.block;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.undercat.UndercatDailyQuest;
import com.cocojenna.undercat.UndercatDailyQuestManager;
import com.cocojenna.undercat.UndercatFaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** 膠帶神廟 — 祈禱獲得隨機增益（DLC 設計書）. */
public class TapeTempleBlock extends Block {

    public TapeTempleBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide || !(player instanceof ServerPlayer sp)) {
            return InteractionResult.SUCCESS;
        }
        var bond = ModCapabilities.getOrDefault(sp);
        long day = level.getDayTime() / 24000L;
        if (bond.getLastTapePrayerDay() == day) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("undercat.cocojenna.temple.already"), true);
            return InteractionResult.FAIL;
        }
        bond.setLastTapePrayerDay(day);
        int roll = level.random.nextInt(4);
        switch (roll) {
            case 0 -> sp.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));
            case 1 -> sp.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));
            case 2 -> {
                bond.addShadowCoins(8);
                bond.addUndercatRep(UndercatFaction.CARDBOARD_KINGDOM, 3);
            }
            default -> sp.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
        }
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("undercat.cocojenna.temple.blessing"), true);
        if (!bond.isUndercatDailyDone()
                && UndercatDailyQuestManager.current(bond) == UndercatDailyQuest.TAPE_OFFERING) {
            UndercatDailyQuestManager.complete(sp);
        }
        return InteractionResult.CONSUME;
    }
}
