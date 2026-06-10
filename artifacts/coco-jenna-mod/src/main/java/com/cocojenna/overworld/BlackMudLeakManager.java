package com.cocojenna.overworld;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** 主世界黑泥洩漏動態事件（每 3 遊戲日，玩家附近）. */
public final class BlackMudLeakManager {

    private static final long LEAK_INTERVAL = 72000L;

    private BlackMudLeakManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        var bond = ModCapabilities.getOrDefault(player);
        long now = player.level().getGameTime();
        if (now - bond.getLastBlackMudLeakTick() < LEAK_INTERVAL) return;
        if (player.getRandom().nextFloat() > 0.35f) return;

        bond.setLastBlackMudLeakTick(now);
        ServerLevel level = player.serverLevel();
        BlockPos center = player.blockPosition().offset(
                player.getRandom().nextInt(80) - 40, 0, player.getRandom().nextInt(80) - 40);
        center = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, center);

        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                level.setBlock(p, ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
                data.putTrace(p, OverworldTraceType.BLACK_MUD_RESIDUE);
            }
        }
        level.setBlock(center.above(), ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState(), 2);
        data.markLeakCore(center);

        for (int i = 0; i < 2 + player.getRandom().nextInt(2); i++) {
            var mob = ModEntities.WANDERING_SLUDGE.get().create(level);
            if (mob == null) continue;
            mob.setPos(center.getX() + 0.5 + i, center.getY() + 1, center.getZ() + 0.5);
            level.addFreshEntity(mob);
        }

        bond.addOverworldInfluence(3);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.mud_leak"), true);
    }

    public static boolean trySealLeak(ServerPlayer player, BlockPos pos) {
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        BlockPos core = resolveLeakCore(data, pos);
        if (core == null) return false;

        var hand = player.getMainHandItem();
        if (!hand.is(ModItems.HOLY_WATER.get()) && !hand.is(ModItems.PURE_TEAR.get())) {
            player.displayClientMessage(Component.translatable("penetration.cocojenna.leak_need_holy"), true);
            return true;
        }
        if (!player.getAbilities().instabuild) hand.shrink(1);

        ServerLevel level = player.serverLevel();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = core.offset(dx, 0, dz);
                data.removeTrace(p);
                level.setBlock(p, net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(), 2);
            }
        }
        level.setBlock(core.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
        data.removeLeakCore(core);
        ModCapabilities.getOrDefault(player).addOverworldInfluence(5);
        player.getInventory().add(new net.minecraft.world.item.ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 5));
        if (player.getRandom().nextFloat() < 0.2f) {
            player.getInventory().add(new net.minecraft.world.item.ItemStack(ModItems.MEMORY_SHARD.get()));
        }
        player.displayClientMessage(Component.translatable("penetration.cocojenna.leak_sealed"), true);
        return true;
    }

    private static BlockPos resolveLeakCore(OverworldPenetrationSavedData data, BlockPos pos) {
        if (data.isLeakCore(pos)) return pos;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos near = pos.offset(dx, 0, dz);
                if (data.isLeakCore(near)) return near;
            }
        }
        if (data.isLeakCore(pos.below())) return pos.below();
        if (data.isLeakCore(pos.above())) return pos.above();
        return null;
    }
}
