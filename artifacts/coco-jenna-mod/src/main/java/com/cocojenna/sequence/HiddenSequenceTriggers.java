package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.world.BlindPortGenerator;
import com.cocojenna.world.FirstCryVillageGenerator;
import com.cocojenna.world.GearTownGenerator;
import com.cocojenna.world.MoonAlleyGenerator;
import com.cocojenna.world.RegionGenerators;
import com.cocojenna.world.VelvetForestPoiGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 隱藏序列解鎖觸發（設計書 1.7 節）. */
public final class HiddenSequenceTriggers {

    private static final Map<UUID, MeditationState> MEDITATION = new ConcurrentHashMap<>();

    private HiddenSequenceTriggers() {}

    public static void onPlayerTickRegion(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 80 != 0) return;

        BlockPos p = player.blockPosition();
        BondData bond = ModCapabilities.getOrDefault(player);

        if (near(p, FirstCryVillageGenerator.CENTER, 96)) {
            if (bond.getReputation("first_cry") >= 10) {
                HiddenSequenceRegistry.tryUnlock(player, "first_cry_oracle");
            }
        }
        if (near(p, GearTownGenerator.CENTER, 80)) {
            if (bond.getTownNpcFavor("ironpaw") >= 30 || bond.isMetIronpaw()) {
                HiddenSequenceRegistry.tryUnlock(player, "gear_orphan");
            }
        }
        if (near(p, MoonAlleyGenerator.CENTER, 80)) {
            HiddenSequenceRegistry.tryUnlock(player, "moon_alley_thief");
        }
        if (near(p, BlindPortGenerator.CENTER, 96)) {
            if (bond.getReputation("blind_port") >= 20) {
                HiddenSequenceRegistry.tryUnlock(player, "blind_ferryman");
            }
        }
        if (near(p, RegionGenerators.SLEEP_SANCTUARY, 64)) {
            tickMidnightMeditation(player, bond, p);
        }
        if (near(p, RegionGenerators.HOWLING_GORGE, 80)) {
            if (bond.getReputation("howling_gorge") >= 15) {
                HiddenSequenceRegistry.tryUnlock(player, "howling_wind_rider");
            }
        }
        if (near(p, RegionGenerators.LABYRINTH, 64)) {
            if (bond.getMemoryShardsTotal() >= 12) {
                HiddenSequenceRegistry.tryUnlock(player, "labyrinth_cartographer");
            }
        }
        if (near(p, VelvetForestPoiGenerator.CENTER, 96) && bond.getMemoryShardsTotal() >= 5) {
            HiddenSequenceRegistry.tryUnlock(player, "velvet_dreamer");
        }
        if (bond.getRemnantBurned() >= 100 || bond.getMemoryShardsTotal() >= 80) {
            HiddenSequenceRegistry.tryUnlock(player, "primal_survivor");
        }
        if (bond.getReputation("blind_port") >= 40) {
            HiddenSequenceRegistry.tryUnlock(player, "defeated_stray");
        }
    }

    private static void tickMidnightMeditation(ServerPlayer player, BondData bond, BlockPos pos) {
        long time = player.level().getDayTime() % 24000L;
        boolean midnight = time >= 18000 && time <= 22000;
        if (!midnight) {
            MEDITATION.remove(player.getUUID());
            return;
        }
        MeditationState state = MEDITATION.computeIfAbsent(player.getUUID(), u -> new MeditationState());
        if (state.lastPos != null && state.lastPos.equals(pos)
                && player.getDeltaMovement().lengthSqr() < 0.001) {
            state.stillTicks += 80;
        } else {
            state.stillTicks = 0;
            state.lastPos = pos.immutable();
        }
        if (state.stillTicks >= 240) {
            HiddenSequenceRegistry.tryUnlock(player, "sleep_cathedral_ghost");
            HiddenSequenceRegistry.tryUnlock(player, "imaginary_walker");
            MEDITATION.remove(player.getUUID());
        }
    }

    public static void onDistillHibiscusTear(ServerPlayer player) {
        HiddenSequenceRegistry.tryUnlock(player, "hibiscus_distiller");
    }

    public static void onMemoryBookOpen(ServerPlayer player) {
        if (ModCapabilities.getOrDefault(player).getMemoryShardsTotal() >= 20) {
            HiddenSequenceRegistry.tryUnlock(player, "imaginary_walker");
        }
    }

    public static void onItemCrafted(ServerPlayer player, ItemStack result) {
        if (result.is(ModBlocks.CAT_BED.get().asItem())) {
            HiddenSequenceRegistry.tryUnlock(player, "imaginary_walker");
        }
    }

    public static void onRemnantBurned(ServerPlayer player, int count) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addRemnantBurned(count);
        if (bond.getRemnantBurned() >= 100) {
            HiddenSequenceRegistry.tryUnlock(player, "primal_survivor");
        }
    }

    private static boolean near(BlockPos a, BlockPos b, int radius) {
        return a.distSqr(b) <= (long) radius * radius;
    }

    private static final class MeditationState {
        BlockPos lastPos;
        int stillTicks;
    }
}
