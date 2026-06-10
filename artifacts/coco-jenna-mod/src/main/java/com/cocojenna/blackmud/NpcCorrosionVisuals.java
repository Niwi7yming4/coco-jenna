package com.cocojenna.blackmud;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

/** NPC 腐蝕階段視覺：粒子、泥跡、同步藥水供客戶端著色. */
public final class NpcCorrosionVisuals {

    private NpcCorrosionVisuals() {}

    public static void onStageChanged(LivingEntity npc, int stage) {
        NpcCorrosionManager.applyStageEffects(npc, stage);
        if (!(npc.level() instanceof ServerLevel level)) return;
        switch (stage) {
            case 1 -> level.sendParticles(ParticleTypes.ASH,
                    npc.getX(), npc.getY() + 0.8, npc.getZ(), 8, 0.3, 0.2, 0.3, 0.01);
            case 2 -> level.sendParticles(ParticleTypes.SMOKE,
                    npc.getX(), npc.getY() + 1.0, npc.getZ(), 10, 0.35, 0.25, 0.35, 0.02);
            case 3 -> {
                npc.addEffect(new MobEffectInstance(ModEffects.CORROSION_MARK.get(), 600, 1, false, false, true));
                level.sendParticles(ParticleTypes.SQUID_INK,
                        npc.getX(), npc.getY() + 0.6, npc.getZ(), 6, 0.2, 0.15, 0.2, 0.01);
            }
            case 4 -> level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    npc.getX(), npc.getY() + 1.2, npc.getZ(), 16, 0.4, 0.5, 0.4, 0.04);
            default -> {}
        }
    }

    public static void tickAmbient(ServerLevel level, LivingEntity npc, int stage) {
        if (stage <= 0) return;
        if (level.getGameTime() % 20 != (npc.getId() & 15)) return;
        double y = npc.getY() + 0.5 + stage * 0.15;
        switch (stage) {
            case 1 -> level.sendParticles(ParticleTypes.WHITE_ASH,
                    npc.getX(), y, npc.getZ(), 2, 0.15, 0.1, 0.15, 0.005);
            case 2 -> level.sendParticles(ParticleTypes.SMOKE,
                    npc.getX(), y + 0.3, npc.getZ(), 2, 0.12, 0.08, 0.12, 0.004);
            case 3 -> {
                level.sendParticles(ParticleTypes.SQUID_INK,
                        npc.getX(), y, npc.getZ(), 1, 0.1, 0.05, 0.1, 0.002);
                leaveMudTrail(level, npc);
            }
            default -> {}
        }
    }

    private static void leaveMudTrail(ServerLevel level, LivingEntity npc) {
        if (npc.getDeltaMovement().horizontalDistanceSqr() < 0.001) return;
        BlockPos below = npc.blockPosition().below();
        BlockState ground = level.getBlockState(below);
        if (ground.isAir() || ground.is(ModBlocks.BLACK_MUD.get())) return;
        if (level.random.nextFloat() > 0.35f) return;
        if (ground.getDestroySpeed(level, below) >= 0 && ground.getDestroySpeed(level, below) < 50) {
            level.setBlock(below, ModBlocks.BLACK_MUD.get().defaultBlockState(), 3);
        }
    }
}
