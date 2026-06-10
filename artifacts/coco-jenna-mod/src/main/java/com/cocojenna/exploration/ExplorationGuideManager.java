package com.cocojenna.exploration;

import com.cocojenna.block.SuspiciousWallBlock;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/** 可可危險感知／珍奶好奇心 — 隱藏入口引導（設計書 5.1 / 6.1）. */
public final class ExplorationGuideManager {

    private static final List<BlockPos> HIDDEN_WALLS = new ArrayList<>();
    private static final int MAX_HIDDEN = 64;

    private ExplorationGuideManager() {}

    public static void registerHiddenWall(BlockPos pos) {
        if (HIDDEN_WALLS.size() >= MAX_HIDDEN) return;
        if (!HIDDEN_WALLS.contains(pos)) HIDDEN_WALLS.add(pos.immutable());
    }

    public static Optional<BlockPos> nearestHidden(ServerPlayer player, double range) {
        BlockPos best = null;
        double bestDist = range * range;
        Vec3 p = player.position();
        for (BlockPos pos : HIDDEN_WALLS) {
            if (!player.level().getBlockState(pos).is(ModBlocks.SUSPICIOUS_WALL.get())) continue;
            double d = p.distanceToSqr(Vec3.atCenterOf(pos));
            if (d < bestDist) {
                bestDist = d;
                best = pos;
            }
        }
        return Optional.ofNullable(best);
    }

    public static boolean canRevealHidden(ServerPlayer player, BlockPos pos) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.hasRevealedHidden(pos)) return true;
        boolean cocoSense = bond.getCocoProtectiveness() >= 45
                && player.level().getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(14))
                .stream().anyMatch(c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID()));
        boolean jennaCurious = bond.getJennaCuriosity() >= 55
                && player.level().getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(14))
                .stream().anyMatch(j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID()));
        return cocoSense || jennaCurious;
    }

    public static void onHiddenRevealed(ServerPlayer player, BlockPos pos) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.markRevealedHidden(pos);
        ExplorationManager.logExploration(player, "explore.cocojenna.hidden.revealed");
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    public static void tickPlayer(ServerPlayer player) {
        if (player.tickCount % 40 != 0) return;
        nearestHidden(player, 24).ifPresent(pos -> {
            BondData bond = ModCapabilities.getOrDefault(player);
            if (bond.hasRevealedHidden(pos)) return;

            boolean cocoNear = player.level().getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(16))
                    .stream().anyMatch(c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID())
                            && c.distanceToSqr(Vec3.atCenterOf(pos)) < 20 * 20);
            if (cocoNear && bond.getCocoProtectiveness() >= 40) {
                player.displayClientMessage(Component.translatable("explore.cocojenna.coco.danger_sense"), true);
                if (player.level() instanceof ServerLevel sl) {
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            6, 0.3, 0.2, 0.3, 0.02);
                }
            }

            boolean jennaNear = player.level().getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(20))
                    .stream().anyMatch(j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID()));
            if (jennaNear && bond.getJennaCuriosity() >= 50) {
                player.level().getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(20))
                        .forEach(j -> {
                            if (j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID())) {
                                j.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.1);
                                j.setSpecialAnimation(JennaEntity.ANIM_CURIOUS_PEEK);
                            }
                        });
                player.displayClientMessage(Component.translatable("explore.cocojenna.jenna.curiosity"), true);
            }
        });
    }

    public static void autoRevealIfReady(ServerPlayer player) {
        Iterator<BlockPos> it = HIDDEN_WALLS.iterator();
        while (it.hasNext()) {
            BlockPos pos = it.next();
            BlockState st = player.level().getBlockState(pos);
            if (!st.is(ModBlocks.SUSPICIOUS_WALL.get())) continue;
            if (player.distanceToSqr(Vec3.atCenterOf(pos)) > 6 * 6) continue;
            if (canRevealHidden(player, pos)) {
                SuspiciousWallBlock.reveal(player.level(), pos);
                onHiddenRevealed(player, pos);
            }
        }
    }
}
