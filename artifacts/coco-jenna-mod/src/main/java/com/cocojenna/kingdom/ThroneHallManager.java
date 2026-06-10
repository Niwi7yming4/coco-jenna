package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModSounds;
import com.cocojenna.world.VelvetTailCastleGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.BlockState;

/** 絨尾王宮王權大廳互動（設計書 Ch.8）. */
public final class ThroneHallManager {

    private static final int HALL_RADIUS_SQ = 28 * 28;
    private static final long BUFF_COOLDOWN = 6000L;

    private ThroneHallManager() {}

    public static boolean tryInteract(ServerPlayer player, BlockPos pos, BlockState state) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        if (pos.distSqr(VelvetTailCastleGenerator.CENTER) > HALL_RADIUS_SQ) return false;

        boolean throne = state.is(ModBlocks.PURR_CRYSTAL_BLOCK.get())
                || state.is(ModBlocks.CAT_BED.get())
                || state.is(ModBlocks.VELVET_CARPET.get());
        if (!throne) return false;

        BondData bond = ModCapabilities.getOrDefault(player);
        long now = player.level().getGameTime();
        if (now - bond.getLastThroneBlessingTick() < BUFF_COOLDOWN) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.throne_cooldown"), true);
            return true;
        }
        bond.setLastThroneBlessingTick(now);

        player.addEffect(new MobEffectInstance(ModEffects.MOON_BLESSING.get(), 7200, 1, false, true, true));
        player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 3600, 0, false, true, true));
        bond.addKingdomProsperity(3);
        bond.addKingdomHappiness(5);
        bond.modifySisterBond(2f);

        inviteTwins(player);
        if (player.level() instanceof ServerLevel sl) {
            BlockPos thronePos = VelvetTailCastleGenerator.CENTER.offset(0, 1, -8);
            sl.sendParticles(ParticleTypes.END_ROD,
                    thronePos.getX() + 0.5, thronePos.getY() + 1.0, thronePos.getZ() + 0.5,
                    24, 1.5, 0.8, 1.5, 0.02);
        }
        player.level().playSound(null, pos, ModSounds.WORLD_FULL_MOON_FESTIVAL.get(),
                SoundSource.BLOCKS, 0.8f, 1.1f);
        DialogueManager.play(player, "kingdom_throne_blessing");
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.throne_blessing"), true);
        return true;
    }

    private static void inviteTwins(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        for (var entity : level.getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(64))) {
            if (player.getUUID().equals(entity.getOwnerUUID())) {
                entity.getNavigation().moveTo(
                        VelvetTailCastleGenerator.CENTER.getX() - 2.5,
                        VelvetTailCastleGenerator.CENTER.getY() + 1,
                        VelvetTailCastleGenerator.CENTER.getZ() - 7, 0.9);
            }
        }
        for (var entity : level.getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(64))) {
            if (player.getUUID().equals(entity.getOwnerUUID())) {
                entity.getNavigation().moveTo(
                        VelvetTailCastleGenerator.CENTER.getX() + 2.5,
                        VelvetTailCastleGenerator.CENTER.getY() + 1,
                        VelvetTailCastleGenerator.CENTER.getZ() - 7, 0.9);
            }
        }
    }

    public static void tickAura(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.blockPosition().distSqr(VelvetTailCastleGenerator.CENTER) > 20 * 20) return;
        if (player.level().getGameTime() % 100 != 0) return;
        player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 120, 0, false, true, true));
    }
}
