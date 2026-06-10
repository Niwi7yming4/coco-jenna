package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;

/** 露天劇場演出系統（設計書 貓之國再深化 / 雨後王國）. */
public final class OpenAirTheaterManager {

    private static final long PERFORMANCE_COOLDOWN = 72000L;

    private OpenAirTheaterManager() {}

    public static boolean tryInteract(ServerPlayer player, BlockPos pos, BlockState state) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isBuildingPlaced("open_air_theater") && !bond.isBuildingPlaced("festival_stage")) {
            return false;
        }
        boolean stageBlock = state.is(ModBlocks.YARN_BALL_LAMP.get())
                || state.is(ModBlocks.CAT_SCRATCH_BOARD.get())
                || state.is(ModBlocks.PURR_CRYSTAL_BLOCK.get());
        if (!stageBlock) return false;

        long now = player.level().getGameTime();
        if (now - bond.getLastTheaterPerformanceTick() < PERFORMANCE_COOLDOWN) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.theater_cooldown"), true);
            return true;
        }
        bond.setLastTheaterPerformanceTick(now);
        runPerformance(player, bond);
        return true;
    }

    private static void runPerformance(ServerPlayer player, BondData bond) {
        bond.addKingdomHappiness(8);
        bond.addKingdomProsperity(4);
        player.addEffect(new MobEffectInstance(MobEffects.LUCK, 4800, 0, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2400, 0, false, true, true));

        if (bond.isTownNpcRecruited("court_lady") || bond.getTownNpcRomanceStage("court_lady") >= 1) {
            bond.addTownNpcFavor("court_lady", 5);
            DialogueManager.play(player, "kingdom_theater_court_lady");
        } else {
            DialogueManager.play(player, "kingdom_theater_generic");
        }

        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (bond.isTownNpcRecruited(p.id())) {
                bond.addTownNpcFavor(p.id(), 2);
            }
        }

        if (player.level() instanceof ServerLevel sl) {
            BlockPos p = player.blockPosition();
            sl.sendParticles(ParticleTypes.NOTE, p.getX() + 0.5, p.getY() + 1.5, p.getZ() + 0.5,
                    24, 4.0, 0.5, 4.0, 0.1);
            sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, p.getX(), p.getY() + 1.0, p.getZ(),
                    16, 5.0, 1.0, 5.0, 0.02);
        }
        player.level().playSound(null, player.blockPosition(), ModSounds.WORLD_FULL_MOON_FESTIVAL.get(),
                SoundSource.BLOCKS, 0.9f, 1.3f);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.theater_performance"), true);
    }

    public static void tickWeeklyGathering(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isBuildingPlaced("open_air_theater")) return;
        long day = player.level().getDayTime() / 24000L;
        if (day % 7 != 0 || player.level().getGameTime() % 24000 > 200) return;
        if (day == bond.getLastTheaterGatheringDay()) return;
        bond.setLastTheaterGatheringDay(day);
        bond.addVillageFoodStock(6);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.theater_gathering"), true);
    }
}
