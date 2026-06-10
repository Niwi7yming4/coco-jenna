package com.cocojenna.memforge;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/** Active daikatana forging ritual at an altar foundation. */
public final class DaikatanaRitual {

    public enum Phase { FORGING, QUENCH, DONE, FAILED }

    private final BlockPos altarPos;
    private final UUID playerId;
    private final DaikatanaRitualRecipe recipe;
    private Phase phase = Phase.FORGING;
    private long phaseEndTick;

    public DaikatanaRitual(BlockPos altarPos, UUID playerId, DaikatanaRitualRecipe recipe, long startTick) {
        this.altarPos = altarPos;
        this.playerId = playerId;
        this.recipe = recipe;
        this.phaseEndTick = startTick + DaikatanaRitualManager.FORGE_TICKS;
    }

    public BlockPos altarPos() { return altarPos; }
    public UUID playerId() { return playerId; }
    public DaikatanaRitualRecipe recipe() { return recipe; }
    public Phase phase() { return phase; }
    public long phaseEndTick() { return phaseEndTick; }

    public void setPhase(Phase phase, long endTick) {
        this.phase = phase;
        this.phaseEndTick = endTick;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("x", altarPos.getX());
        tag.putLong("y", altarPos.getY());
        tag.putLong("z", altarPos.getZ());
        tag.putUUID("player", playerId);
        tag.putString("recipe", recipe.name());
        tag.putString("phase", phase.name());
        tag.putLong("end", phaseEndTick);
        return tag;
    }

    public static DaikatanaRitual load(CompoundTag tag) {
        BlockPos pos = new BlockPos((int) tag.getLong("x"), (int) tag.getLong("y"), (int) tag.getLong("z"));
        DaikatanaRitualRecipe recipe = DaikatanaRitualRecipe.valueOf(tag.getString("recipe"));
        DaikatanaRitual ritual = new DaikatanaRitual(pos, tag.getUUID("player"), recipe, 0);
        ritual.phase = Phase.valueOf(tag.getString("phase"));
        ritual.phaseEndTick = tag.getLong("end");
        return ritual;
    }
}
