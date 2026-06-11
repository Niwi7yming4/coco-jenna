package com.cocojenna.world.firstcry;

import com.cocojenna.entity.TownNpcCompanionEntity;
import com.cocojenna.entity.WildCatEntity;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** 初啼村 11 名固定 NPC + 四門守門貓（設計書 四）. */
public final class FirstCryNpcSpawner {

    public record NpcSpawn(String id, int x, int y, int z, float yaw) {}

    public static final NpcSpawn[] NPCS = buildFromAnchorTable();

    private static NpcSpawn[] buildFromAnchorTable() {
        FirstCryAnchorTable.NpcAnchor[] anchors = FirstCryAnchorTable.npcs();
        NpcSpawn[] out = new NpcSpawn[anchors.length];
        for (int i = 0; i < anchors.length; i++) {
            FirstCryAnchorTable.NpcAnchor a = anchors[i];
            out[i] = new NpcSpawn(a.npcId(), a.pos().getX(), a.pos().getY(), a.pos().getZ(), a.yaw());
        }
        return out;
    }

    private FirstCryNpcSpawner() {}

    public static void spawnAll(ServerLevel level) {
        for (NpcSpawn npc : NPCS) {
            if ("moon_whisper".equals(npc.id()) && level.getMoonBrightness() < 1.0f) {
                continue;
            }
            TownNpcCompanionEntity entity = ModEntities.TOWN_NPC_COMPANION.get().create(level);
            if (entity == null) continue;
            entity.setNpcId(npc.id());
            entity.moveTo(npc.x() + 0.5, npc.y(), npc.z() + 0.5, npc.yaw(), 0);
            level.addFreshEntity(entity);
        }
        BlockPos[] gates = {
                FirstCryLayout.GATE_NORTH, FirstCryLayout.GATE_EAST,
                FirstCryLayout.GATE_SOUTH, FirstCryLayout.GATE_WEST
        };
        for (BlockPos gate : gates) {
            WildCatEntity cat = ModEntities.WILD_CAT.get().create(level);
            if (cat == null) continue;
            cat.moveTo(gate.getX() + 0.5, gate.getY() + 1, gate.getZ() + 0.5, 0, 0);
            cat.setPersistenceRequired();
            level.addFreshEntity(cat);
        }
    }
}
