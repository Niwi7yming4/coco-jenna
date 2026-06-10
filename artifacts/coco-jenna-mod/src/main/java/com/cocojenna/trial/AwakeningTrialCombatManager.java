package com.cocojenna.trial;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModEntities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.network.PacketDistributor;

/** 覺醒試煉實例戰 — 碎片里程碑觸發波次戰鬥（序列設計書）. */
public final class AwakeningTrialCombatManager {

    private static final int[] THRESHOLDS = {10, 25, 40, 50};
    private static final int TRIAL_TICKS = 4800;

    private AwakeningTrialCombatManager() {}

    public static void offerTrial(ServerPlayer player, BondData bond) {
        int tier = bond.getAwakeningTrialTier();
        if (tier >= THRESHOLDS.length) return;
        if (bond.getMemoryShardsTotal() < THRESHOLDS[tier]) return;
        if (bond.isAwakeningTrialActive()) return;

        if (!AwakeningTrialManager.meetsPassiveGate(bond, player, tier)) {
            player.displayClientMessage(Component.translatable("trial.cocojenna.failed")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }
        beginCombat(player, bond, tier);
    }

    public static void beginCombat(ServerPlayer player, BondData bond, int tier) {
        bond.setAwakeningTrialActive(true);
        bond.setAwakeningTrialIndex(tier);
        bond.setAwakeningTrialKills(0);
        bond.setAwakeningTrialGoal(killGoal(tier));
        bond.setAwakeningTrialDeadline(player.level().getGameTime() + TRIAL_TICKS);
        spawnWave(player, tier);
        player.displayClientMessage(Component.translatable("trial.cocojenna.combat_start", tier + 1)
                .withStyle(ChatFormatting.GOLD), true);
        sync(player, bond);
    }

    public static void tick(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isAwakeningTrialActive()) return;
        long now = player.level().getGameTime();
        if (now > bond.getAwakeningTrialDeadline()) {
            failTrial(player, bond);
            return;
        }
        if (bond.getAwakeningTrialKills() >= bond.getAwakeningTrialGoal()) {
            completeTrial(player, bond);
        }
    }

    public static void onKill(ServerPlayer killer, Monster dead) {
        BondData bond = ModCapabilities.getOrDefault(killer);
        if (!bond.isAwakeningTrialActive()) return;
        if (!(dead instanceof BlackMudMob)) return;
        if (killer.distanceToSqr(dead) > 48 * 48) return;
        bond.setAwakeningTrialKills(bond.getAwakeningTrialKills() + 1);
        killer.displayClientMessage(Component.translatable("trial.cocojenna.kill_progress",
                bond.getAwakeningTrialKills(), bond.getAwakeningTrialGoal()), true);
        sync(killer, bond);
    }

    private static void completeTrial(ServerPlayer player, BondData bond) {
        int tier = bond.getAwakeningTrialIndex();
        bond.setAwakeningTrialActive(false);
        bond.setAwakeningTrialTier(tier + 1);
        AwakeningTrialManager.grantReward(player, bond, tier);
        player.displayClientMessage(Component.translatable("trial.cocojenna.passed", tier + 1)
                .withStyle(ChatFormatting.GOLD), true);
        sync(player, bond);
    }

    private static void failTrial(ServerPlayer player, BondData bond) {
        bond.setAwakeningTrialActive(false);
        player.displayClientMessage(Component.translatable("trial.cocojenna.combat_failed")
                .withStyle(ChatFormatting.RED), true);
        sync(player, bond);
    }

    private static int killGoal(int tier) {
        return switch (tier) {
            case 0 -> 3;
            case 1 -> 4;
            case 2 -> 5;
            case 3 -> 6;
            default -> 3;
        };
    }

    private static void spawnWave(ServerPlayer player, int tier) {
        ServerLevel level = player.serverLevel();
        int count = killGoal(tier);
        for (int i = 0; i < count; i++) {
            var type = switch (tier) {
                case 0 -> ModEntities.HEAT_LEECH.get();
                case 1 -> i % 2 == 0 ? ModEntities.HEAT_LEECH.get() : ModEntities.FORGOTTEN_WISP.get();
                case 2 -> i % 3 == 0 ? ModEntities.MEMORY_MOTH.get() : ModEntities.HEAT_LEECH.get();
                default -> i % 2 == 0 ? ModEntities.WHISPERING_DOLL.get() : ModEntities.MEMORY_MOTH.get();
            };
            BlockPos pos = findSpawn(level, player.blockPosition(), player.getRandom());
            if (pos == null) continue;
            var mob = type.create(level);
            if (mob == null) continue;
            mob.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
            level.addFreshEntity(mob);
        }
    }

    private static BlockPos findSpawn(ServerLevel level, BlockPos near, net.minecraft.util.RandomSource random) {
        for (int i = 0; i < 12; i++) {
            int x = near.getX() + random.nextInt(14) - 7;
            int z = near.getZ() + random.nextInt(14) - 7;
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    new BlockPos(x, 0, z));
            if (level.getBlockState(surface).isAir()) return surface;
        }
        return null;
    }

    private static void sync(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }
}
