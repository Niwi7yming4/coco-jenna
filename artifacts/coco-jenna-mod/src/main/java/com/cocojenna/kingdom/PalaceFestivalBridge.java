package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.FestivalEventManager;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModSounds;
import com.cocojenna.kingdom.PalaceRegionManager.PalaceRegion;
import com.cocojenna.world.VelvetTailCastleGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 絨尾王宮 × 滿月祭典七階段聯動. */
public final class PalaceFestivalBridge {

    private static final int PALACE_RADIUS_SQ = 32 * 32;

    private PalaceFestivalBridge() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.blockPosition().distSqr(VelvetTailCastleGenerator.CENTER) > PALACE_RADIUS_SQ) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        int phase = bond.getFestivalPhase();
        if (phase <= FestivalEventManager.PHASE_IDLE || phase >= FestivalEventManager.PHASE_ENDED) return;

        if (player.tickCount % 60 != 0) return;
        ServerLevel level = player.serverLevel();
        BlockPos c = VelvetTailCastleGenerator.CENTER;

        switch (phase) {
            case FestivalEventManager.PHASE_SETUP -> level.sendParticles(ParticleTypes.END_ROD,
                    c.getX(), c.getY() + 2, c.getZ() + 20, 2, 2, 0.5, 2, 0.01);
            case FestivalEventManager.PHASE_OPENING -> player.addEffect(
                    new MobEffectInstance(ModEffects.MOON_BLESSING.get(), 100, 0, false, true, true));
            case FestivalEventManager.PHASE_DANCE -> level.sendParticles(ParticleTypes.NOTE,
                    player.getX(), player.getY() + 1, player.getZ(), 3, 1, 0.5, 1, 0.1);
            case FestivalEventManager.PHASE_COOKING -> level.sendParticles(ParticleTypes.SMOKE,
                    c.getX() + 5, c.getY() + 2, c.getZ(), 2, 1, 0.3, 1, 0.02);
            case FestivalEventManager.PHASE_FIREWORKS -> FestivalEventManager.spawnFireworkParticles(level, player);
            case FestivalEventManager.PHASE_WISHING -> level.sendParticles(ParticleTypes.ENCHANT,
                    c.getX(), c.getY() + 3, c.getZ() - 8, 4, 1.5, 0.8, 1.5, 0.05);
            default -> { }
        }
    }

    public static void onPhaseAdvance(ServerPlayer player, BondData bond, int phase) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        boolean nearPalace = player.blockPosition().distSqr(VelvetTailCastleGenerator.CENTER) <= PALACE_RADIUS_SQ;

        switch (phase) {
            case FestivalEventManager.PHASE_SETUP -> {
                bond.addFestivalPrepProgress(5);
                if (nearPalace) {
                    player.displayClientMessage(Component.translatable(
                            "kingdom.cocojenna.palace.festival.setup"), true);
                }
            }
            case FestivalEventManager.PHASE_OPENING -> {
                if (nearPalace) {
                    player.level().playSound(null, VelvetTailCastleGenerator.CENTER,
                            ModSounds.WORLD_FULL_MOON_FESTIVAL.get(), SoundSource.AMBIENT, 1f, 1f);
                    player.displayClientMessage(Component.translatable(
                            "kingdom.cocojenna.palace.festival.opening"), true);
                }
            }
            case FestivalEventManager.PHASE_DANCE -> {
                if (nearPalace) {
                    player.displayClientMessage(Component.translatable(
                            "kingdom.cocojenna.palace.festival.dance"), true);
                }
            }
            case FestivalEventManager.PHASE_COOKING -> {
                if (nearPalace && bond.isBuildingPlaced("cat_kitchen")) {
                    bond.addKingdomHappiness(5);
                }
            }
            case FestivalEventManager.PHASE_FIREWORKS -> {
                if (nearPalace) {
                    player.displayClientMessage(Component.translatable(
                            "kingdom.cocojenna.palace.festival.fireworks"), true);
                }
            }
            case FestivalEventManager.PHASE_WISHING -> {
                if (nearPalace) {
                    player.displayClientMessage(Component.translatable(
                            "kingdom.cocojenna.palace.festival.wish"), true);
                }
            }
            default -> { }
        }
    }

    public static boolean tryFestivalInteract(ServerPlayer player, BlockPos pos, BlockState state,
                                               PalaceRegion region) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int phase = bond.getFestivalPhase();
        if (phase <= FestivalEventManager.PHASE_IDLE || phase >= FestivalEventManager.PHASE_ENDED) {
            return false;
        }

        return switch (region) {
            case THRONE -> tryThronePhase(player, bond, state, phase);
            case GARDEN -> tryGardenPhase(player, bond, state, phase);
            case LIBRARY -> tryLibraryPhase(player, bond, state, phase);
            case BARRACKS -> tryBarracksPhase(player, bond, state, phase);
            case COURTYARD -> tryCourtyardPhase(player, bond, phase);
        };
    }

    private static boolean tryThronePhase(ServerPlayer player, BondData bond, BlockState state, int phase) {
        if (!state.is(ModBlocks.PURR_CRYSTAL_BLOCK.get()) && !state.is(ModBlocks.VELVET_CARPET.get())) return false;
        if (phase == FestivalEventManager.PHASE_SETUP) {
            FestivalEventManager.onHelpSetup(player);
            return true;
        }
        if (phase == FestivalEventManager.PHASE_OPENING) {
            player.displayClientMessage(Component.translatable(
                    "kingdom.cocojenna.palace.festival.throne"), true);
            bond.addKingdomProsperity(3);
            return true;
        }
        if (phase == FestivalEventManager.PHASE_WISHING) {
            FestivalEventManager.onSubmitWish(player, "prosperity");
            return true;
        }
        return false;
    }

    private static boolean tryGardenPhase(ServerPlayer player, BondData bond, BlockState state, int phase) {
        if (phase != FestivalEventManager.PHASE_SETUP && phase != FestivalEventManager.PHASE_FIREWORKS) return false;
        if (!state.is(ModBlocks.HIBISCUS_FLOWER.get())) return false;
        bond.addFestivalPrepProgress(4);
        bond.addKingdomHappiness(3);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.palace.festival.garden"), true);
        return true;
    }

    private static boolean tryLibraryPhase(ServerPlayer player, BondData bond, BlockState state, int phase) {
        if (phase != FestivalEventManager.PHASE_WISHING) return false;
        if (!state.is(Blocks.BOOKSHELF)) return false;
        bond.addKingdomProsperity(5);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.palace.festival.library"), true);
        return true;
    }

    private static boolean tryBarracksPhase(ServerPlayer player, BondData bond, BlockState state, int phase) {
        if (phase != FestivalEventManager.PHASE_DANCE) return false;
        if (!state.is(Blocks.IRON_BARS)) return false;
        FestivalEventManager.onDance(player);
        return true;
    }

    private static boolean tryCourtyardPhase(ServerPlayer player, BondData bond, int phase) {
        if (phase != FestivalEventManager.PHASE_COOKING) return false;
        if (player.tickCount % 40 != 0) return false;
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.palace.festival.courtyard"), true);
        return true;
    }
}
