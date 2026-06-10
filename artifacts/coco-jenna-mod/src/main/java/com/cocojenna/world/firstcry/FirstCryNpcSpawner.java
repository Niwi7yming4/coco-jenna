package com.cocojenna.world.firstcry;

import com.cocojenna.entity.TownNpcCompanionEntity;
import com.cocojenna.entity.WildCatEntity;
import com.cocojenna.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** 初啼村 10 名固定 NPC + 四門守門貓（設計書 四）. */
public final class FirstCryNpcSpawner {

    public record NpcSpawn(String id, int x, int y, int z, float yaw) {}

    public static final NpcSpawn[] NPCS = {
            new NpcSpawn("ryokatsu", FirstCryLayout.MAYOR_HALL.getX(), FirstCryLayout.Y + 1,
                    FirstCryLayout.MAYOR_HALL.getZ() + 2, 180),
            new NpcSpawn("pagepaw", FirstCryLayout.LIBRARY.getX() + 2, FirstCryLayout.Y + 1,
                    FirstCryLayout.LIBRARY.getZ(), 90),
            new NpcSpawn("blade_mark", 36, FirstCryLayout.Y, 4, 270),
            new NpcSpawn("molten_paw", 33, FirstCryLayout.Y, -8, 90),
            new NpcSpawn("miso", 24, FirstCryLayout.Y, 26, 0),
            new NpcSpawn("mint_ear", -4, FirstCryLayout.Y, 32, 180),
            new NpcSpawn("moon_whisper", 0, FirstCryLayout.Y, 36, 0),
            new NpcSpawn("soft_pad", -30, FirstCryLayout.Y, 24, 45),
            new NpcSpawn("tide_tail", -21, FirstCryLayout.Y, 39, 180),
            new NpcSpawn("mud_bean", -26, FirstCryLayout.Y, -26, 135),
            new NpcSpawn("wander_stray", 8, FirstCryLayout.Y, -18, 220),
    };

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
