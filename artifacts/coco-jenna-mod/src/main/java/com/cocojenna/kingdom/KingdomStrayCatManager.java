package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import com.cocojenna.overworld.OverworldCatNpcEntity;
import com.cocojenna.society.CatNpcNamePool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

/** 貓之國維度路邊迷路幼貓（設計書 §5）. */
public final class KingdomStrayCatManager {

    private static final int SPAWN_INTERVAL = 6000;
    private static final int SPAWN_RADIUS = 48;

    private KingdomStrayCatManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % SPAWN_INTERVAL != 0) return;
        if (player.getRandom().nextInt(4) != 0) return;

        ServerLevel level = player.serverLevel();
        long nearby = level.getEntitiesOfClass(OverworldCatNpcEntity.class,
                player.getBoundingBox().inflate(SPAWN_RADIUS)).stream()
                .filter(e -> e.getRole() == OverworldCatNpcEntity.Role.LOST_KITTEN)
                .count();
        if (nearby >= 2) return;

        BlockPos spawn = findSpawn(level, player.blockPosition());
        if (spawn == null) return;

        var entity = ModEntities.OVERWORLD_CAT.get().create(level);
        if (entity == null) return;
        entity.setRole(OverworldCatNpcEntity.Role.LOST_KITTEN);
        entity.setPos(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
        var prof = com.cocojenna.society.CatProfessionRegistry.random(player.getRandom());
        var personality = CatNpcNamePool.randomPersonality(player.getRandom());
        entity.setCustomName(Component.literal(CatNpcNamePool.randomName(player.getRandom())));
        entity.setCustomNameVisible(true);
        entity.getPersistentData().putBoolean("cocojenna_kingdom_stray", true);
        entity.getPersistentData().putString("cocojenna_profession", prof.id());
        entity.getPersistentData().putString("cocojenna_personality", personality.name());
        entity.getPersistentData().putString("cocojenna_dream", CatNpcNamePool.randomDream(player.getRandom()));
        level.addFreshEntity(entity);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.stray_spawn"), true);
    }

    public static void onReturnedHome(ServerPlayer player, OverworldCatNpcEntity kitten) {
        if (!kitten.getPersistentData().getBoolean("cocojenna_kingdom_stray")) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addKingdomStrayCatsReturned(1);
        bond.addReputation("royal", 3);
        bond.addKingdomHappiness(2);
        String prof = kitten.getPersistentData().getString("cocojenna_profession");
        if (!prof.isEmpty()) {
            player.displayClientMessage(com.cocojenna.society.CatProfessionRegistry.displayName(
                    com.cocojenna.society.CatProfessionRegistry.byId(prof)), true);
        }
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.stray_returned",
                kitten.getCustomName() != null ? kitten.getCustomName().getString() : ""), true);
    }

    public static boolean isKingdomStray(OverworldCatNpcEntity npc) {
        return npc.getPersistentData().getBoolean("cocojenna_kingdom_stray");
    }

    private static BlockPos findSpawn(ServerLevel level, BlockPos origin) {
        for (int i = 0; i < 12; i++) {
            int dx = level.random.nextInt(SPAWN_RADIUS * 2) - SPAWN_RADIUS;
            int dz = level.random.nextInt(SPAWN_RADIUS * 2) - SPAWN_RADIUS;
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE,
                    origin.offset(dx, 0, dz));
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolid()) {
                return pos;
            }
        }
        return null;
    }
}
