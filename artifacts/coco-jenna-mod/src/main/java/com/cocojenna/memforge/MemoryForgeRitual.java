package com.cocojenna.memforge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemoryForgeRitual {

    public enum Phase { AWAKEN, DEFEND, INJECT, RESONANCE, DONE, FAILED }

    public static final float BLOCK_MAX_HP = 50f;

    private final BlockPos altarPos;
    private final BlockPos corePos;
    private final UUID playerId;
    private final MemoryForgeRecipe recipe;
    private Phase phase = Phase.AWAKEN;
    private long phaseEndTick;
    private boolean catalystInjected;
    private boolean bonusApplied;
    private boolean penaltyApplied;
    private final Map<Long, Float> blockHp = new HashMap<>();

    public MemoryForgeRitual(BlockPos altarPos, BlockPos corePos, UUID playerId,
                             MemoryForgeRecipe recipe, long startTick) {
        this.altarPos = altarPos;
        this.corePos = corePos;
        this.playerId = playerId;
        this.recipe = recipe;
        this.phaseEndTick = startTick + MemoryForgeManager.AWAKEN_TICKS;
    }

    public BlockPos altarPos() { return altarPos; }
    public BlockPos corePos() { return corePos; }
    public UUID playerId() { return playerId; }
    public MemoryForgeRecipe recipe() { return recipe; }
    public Phase phase() { return phase; }
    public long phaseEndTick() { return phaseEndTick; }
    public boolean catalystInjected() { return catalystInjected; }
    public boolean bonusApplied() { return bonusApplied; }
    public boolean penaltyApplied() { return penaltyApplied; }
    public Map<Long, Float> blockHp() { return blockHp; }

    public void setPhase(Phase phase, long endTick) {
        this.phase = phase;
        this.phaseEndTick = endTick;
    }

    public void markCatalystInjected() { catalystInjected = true; }
    public void markBonusApplied() { bonusApplied = true; }
    public void markPenaltyApplied() { penaltyApplied = true; }

    public void initBlockHp(ServerLevel level) {
        blockHp.clear();
        for (BlockPos pos : MemoryForgeStructure.altarBlocks(level, corePos, altarPos)) {
            blockHp.put(pos.asLong(), BLOCK_MAX_HP);
        }
    }

    public float getBlockHp(BlockPos pos) {
        return blockHp.getOrDefault(pos.asLong(), BLOCK_MAX_HP);
    }

    public float damageBlock(BlockPos pos, float amount) {
        float hp = Math.max(0f, getBlockHp(pos) - amount);
        blockHp.put(pos.asLong(), hp);
        return hp;
    }

    @Nullable
    public BlockPos damageRandomBlock(ServerLevel level, float amount) {
        List<BlockPos> blocks = MemoryForgeStructure.altarBlocks(level, corePos, altarPos);
        if (blocks.isEmpty()) return null;
        BlockPos target = blocks.get(level.random.nextInt(blocks.size()));
        damageBlock(target, amount);
        return target;
    }

    public boolean isCoreDestroyed() {
        return getBlockHp(corePos) <= 0f;
    }

    public float altarHpRatio() {
        if (blockHp.isEmpty()) return 1f;
        float sum = 0f;
        for (float hp : blockHp.values()) {
            sum += hp;
        }
        return sum / (blockHp.size() * BLOCK_MAX_HP);
    }

    public boolean containsBlock(BlockPos pos) {
        return blockHp.containsKey(pos.asLong());
    }

    @Nullable
    public ServerPlayer player() {
        ServerLevel level = level();
        if (level == null) return null;
        return level.getServer().getPlayerList().getPlayer(playerId);
    }

    @Nullable
    public ServerLevel level() {
        return MemoryForgeManager.levelFor(altarPos);
    }
}
