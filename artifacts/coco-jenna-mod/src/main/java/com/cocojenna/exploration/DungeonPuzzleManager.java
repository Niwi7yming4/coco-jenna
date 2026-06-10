package com.cocojenna.exploration;

import com.cocojenna.block.DungeonSequencePlateBlock;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 地牢謎題房 — 序列鎖、連鎖門、提示石碑（設計書 3.3）. */
public final class DungeonPuzzleManager {

    private static final Map<Long, Puzzle> PUZZLES = new HashMap<>();
    private static final Map<BlockPos, Long> HINT_TO_PUZZLE = new HashMap<>();
    private static final Map<Integer, Chain> CHAINS = new HashMap<>();

    private DungeonPuzzleManager() {}

    public static long key(int dungeonIndex, BlockPos center) {
        return ((long) dungeonIndex << 32) ^ center.asLong();
    }

    /** 於多個中層房間放置連鎖謎題；全部解開後才開啟通往 Boss 的主閘門。 */
    public static void placePuzzleChain(ServerLevel level, int dungeonIndex, List<int[]> middleRooms,
            int[] bossRoom) {
        if (middleRooms == null || middleRooms.isEmpty()) return;

        int count = middleRooms.size() >= 5 ? 2 : 1;
        List<int[]> puzzleRooms = new ArrayList<>();
        if (count == 2) {
            puzzleRooms.add(middleRooms.get(middleRooms.size() / 3));
            puzzleRooms.add(middleRooms.get((middleRooms.size() * 2) / 3));
        } else {
            puzzleRooms.add(middleRooms.get(middleRooms.size() / 2));
        }

        Chain chain = new Chain(dungeonIndex);
        for (int i = 0; i < puzzleRooms.size(); i++) {
            Puzzle puzzle = buildPuzzleRoom(level, dungeonIndex, puzzleRooms.get(i), i, puzzleRooms.size());
            chain.puzzles.add(puzzle);
        }

        if (bossRoom != null && !puzzleRooms.isEmpty()) {
            int[] lastPuzzle = puzzleRooms.get(puzzleRooms.size() - 1);
            chain.masterGates = placeMasterGate(level, lastPuzzle, bossRoom);
        }

        CHAINS.put(dungeonIndex, chain);
    }

    private static Puzzle buildPuzzleRoom(ServerLevel level, int dungeonIndex, int[] room,
            int chainIndex, int chainSize) {
        int cx = room[0];
        int cy = room[1];
        int cz = room[2];
        BlockPos center = new BlockPos(cx, cy, cz);
        long puzzleKey = key(dungeonIndex, center);

        int length = 3 + level.random.nextInt(2);
        int[] solution = new int[length];
        for (int i = 0; i < length; i++) {
            solution[i] = level.random.nextInt(4);
        }

        List<BlockPos> gates = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            BlockPos gate = new BlockPos(cx + x, cy + 1, cz + 4);
            level.setBlock(gate, Blocks.IRON_BARS.defaultBlockState(), 2);
            gates.add(gate);
        }

        List<BlockPos> plates = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            BlockPos platePos = new BlockPos(cx - 3 + i * 2, cy + 1, cz - 1);
            level.setBlock(platePos,
                    ModBlocks.DUNGEON_SEQUENCE_PLATE.get().defaultBlockState()
                            .setValue(DungeonSequencePlateBlock.SYMBOL, i), 2);
            plates.add(platePos);
        }

        BlockPos hintPos = new BlockPos(cx - 4, cy + 1, cz - 2);
        level.setBlock(hintPos, ModBlocks.DUNGEON_PUZZLE_HINT.get().defaultBlockState(), 2);

        Puzzle puzzle = new Puzzle(puzzleKey, dungeonIndex, chainIndex, chainSize, solution, gates, plates, hintPos);
        PUZZLES.put(puzzleKey, puzzle);
        HINT_TO_PUZZLE.put(hintPos.immutable(), puzzleKey);
        return puzzle;
    }

    private static List<BlockPos> placeMasterGate(ServerLevel level, int[] fromRoom, int[] bossRoom) {
        List<BlockPos> gates = new ArrayList<>();
        int mx = (fromRoom[0] + bossRoom[0]) / 2;
        int my = fromRoom[1];
        int mz = (fromRoom[2] + bossRoom[2]) / 2;
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                BlockPos g = new BlockPos(mx + x, my + y, mz);
                level.setBlock(g, Blocks.IRON_BARS.defaultBlockState(), 2);
                gates.add(g);
            }
        }
        return gates;
    }

    public static void onPlatePressed(Level level, ServerPlayer player, BlockPos pos, int symbol) {
        if (!(level instanceof ServerLevel sl)) return;
        Puzzle puzzle = findPuzzleByPlate(pos);
        if (puzzle == null || puzzle.solved) return;

        if (puzzle.solution[puzzle.progress] == symbol) {
            puzzle.progress++;
            level.setBlock(pos, level.getBlockState(pos).setValue(DungeonSequencePlateBlock.LIT, true), 2);
            sl.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS,
                    0.7f, 0.9f + puzzle.progress * 0.1f);
            if (puzzle.progress >= puzzle.solution.length) {
                solve(sl, player, puzzle);
            } else {
                player.displayClientMessage(
                        Component.translatable("explore.cocojenna.puzzle.progress",
                                puzzle.progress, puzzle.solution.length), true);
            }
        } else {
            puzzle.progress = 0;
            resetPlates(sl, puzzle);
            sl.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 0.8f, 0.5f);
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.wrong"), true);
        }
    }

    public static void tryRevealHint(ServerPlayer player, BlockPos hintPos) {
        Long puzzleKey = HINT_TO_PUZZLE.get(hintPos);
        if (puzzleKey == null) return;
        Puzzle puzzle = PUZZLES.get(puzzleKey);
        if (puzzle == null || puzzle.solved) {
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.hint.solved"), true);
            return;
        }

        BondData bond = ModCapabilities.getOrDefault(player);
        boolean cocoNear = player.level().getEntitiesOfClass(CocoEntity.class, player.getBoundingBox().inflate(14))
                .stream().anyMatch(c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID()));
        boolean jennaNear = player.level().getEntitiesOfClass(JennaEntity.class, player.getBoundingBox().inflate(14))
                .stream().anyMatch(j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID()));

        if (cocoNear && bond.getCocoProtectiveness() >= 40f) {
            int reveal = Math.max(1, puzzle.solution.length / 2);
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.hint.coco",
                    formatSymbols(puzzle.solution, reveal)), false);
            player.level().playSound(null, hintPos, SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.BLOCKS, 0.6f, 1.2f);
            return;
        }
        if (jennaNear && bond.getJennaCuriosity() >= 45f) {
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.hint.jenna",
                    formatSymbols(puzzle.solution, puzzle.solution.length)), false);
            player.level().playSound(null, hintPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.7f, 1.1f);
            return;
        }
        player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.hint.need_cats"), true);
    }

    public static Optional<BlockPos> nearestUnsolvedHint(ServerPlayer player, double range) {
        BlockPos best = null;
        double bestDist = range * range;
        for (var entry : HINT_TO_PUZZLE.entrySet()) {
            Puzzle p = PUZZLES.get(entry.getValue());
            if (p == null || p.solved) continue;
            double d = player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(entry.getKey()));
            if (d < bestDist) {
                bestDist = d;
                best = entry.getKey();
            }
        }
        return Optional.ofNullable(best);
    }

    private static MutableComponent formatSymbols(int[] solution, int count) {
        MutableComponent out = Component.empty();
        for (int i = 0; i < Math.min(count, solution.length); i++) {
            if (i > 0) out.append(" → ");
            out.append(Component.translatable("explore.cocojenna.puzzle.symbol." + solution[i]));
        }
        return out;
    }

    private static void solve(ServerLevel level, ServerPlayer player, Puzzle puzzle) {
        puzzle.solved = true;
        openGates(level, puzzle.gates);
        level.playSound(null, player.blockPosition(), SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.9f, 1.0f);

        Chain chain = CHAINS.get(puzzle.dungeonIndex);
        if (chain != null && chain.allSolved()) {
            openGates(level, chain.masterGates);
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.chain_complete"), false);
        } else if (puzzle.chainSize > 1) {
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.chain_part",
                    puzzle.chainIndex + 1, puzzle.chainSize), false);
        } else {
            player.displayClientMessage(Component.translatable("explore.cocojenna.puzzle.solved"), false);
        }
        ExplorationManager.logExploration(player, "explore.cocojenna.journal.puzzle");
    }

    private static void openGates(ServerLevel level, List<BlockPos> gates) {
        for (BlockPos gate : gates) {
            level.setBlock(gate, Blocks.AIR.defaultBlockState(), 2);
            level.sendParticles(ParticleTypes.END_ROD,
                    gate.getX() + 0.5, gate.getY() + 0.5, gate.getZ() + 0.5,
                    8, 0.2, 0.2, 0.2, 0.02);
        }
    }

    private static void resetPlates(ServerLevel level, Puzzle puzzle) {
        for (BlockPos plate : puzzle.plates) {
            BlockState st = level.getBlockState(plate);
            if (st.is(ModBlocks.DUNGEON_SEQUENCE_PLATE.get())) {
                level.setBlock(plate, st.setValue(DungeonSequencePlateBlock.LIT, false), 2);
            }
        }
    }

    private static Puzzle findPuzzleByPlate(BlockPos platePos) {
        for (Puzzle p : PUZZLES.values()) {
            if (p.plates.contains(platePos)) return p;
        }
        return null;
    }

    private static final class Puzzle {
        final long key;
        final int dungeonIndex;
        final int chainIndex;
        final int chainSize;
        final int[] solution;
        final List<BlockPos> gates;
        final List<BlockPos> plates;
        final BlockPos hintPos;
        int progress;
        boolean solved;

        Puzzle(long key, int dungeonIndex, int chainIndex, int chainSize, int[] solution,
                List<BlockPos> gates, List<BlockPos> plates, BlockPos hintPos) {
            this.key = key;
            this.dungeonIndex = dungeonIndex;
            this.chainIndex = chainIndex;
            this.chainSize = chainSize;
            this.solution = solution;
            this.gates = gates;
            this.plates = plates;
            this.hintPos = hintPos;
        }
    }

    private static final class Chain {
        final int dungeonIndex;
        final List<Puzzle> puzzles = new ArrayList<>();
        List<BlockPos> masterGates = List.of();

        Chain(int dungeonIndex) {
            this.dungeonIndex = dungeonIndex;
        }

        boolean allSolved() {
            return !puzzles.isEmpty() && puzzles.stream().allMatch(p -> p.solved);
        }
    }
}
