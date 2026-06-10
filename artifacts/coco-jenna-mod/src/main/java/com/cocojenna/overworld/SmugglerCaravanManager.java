package com.cocojenna.overworld;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** 走私貓車隊動態事件（主世界白天隨機 + 護送任務）. */
public final class SmugglerCaravanManager {

    private static final String CARAVAN_TAG = "cocojenna_caravan";

    private SmugglerCaravanManager() {}

    public static void trySpawn(ServerPlayer anchor) {
        if (!anchor.level().isDay()) return;
        var bond = ModCapabilities.getOrDefault(anchor);
        if (bond.isCaravanEscortActive()) return;

        var level = anchor.serverLevel();
        BlockPos pos = anchor.blockPosition().offset(
                level.random.nextInt(40) - 20, 0, level.random.nextInt(40) - 20);
        pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);

        for (int i = 0; i < 3; i++) {
            var cat = ModEntities.OVERWORLD_CAT.get().create(level);
            if (cat == null) continue;
            cat.setRole(OverworldCatNpcEntity.Role.SMUGGLER);
            cat.setPos(pos.getX() + i * 2 + 0.5, pos.getY(), pos.getZ() + 0.5);
            if (i == 0) cat.getPersistentData().putBoolean(CARAVAN_TAG, true);
            level.addFreshEntity(cat);
        }

        anchor.displayClientMessage(Component.translatable("penetration.cocojenna.caravan_spotted"), true);
        anchor.displayClientMessage(Component.translatable("penetration.cocojenna.caravan_escort_hint"), true);
    }

    public static void tryStartEscort(ServerPlayer player, OverworldCatNpcEntity npc) {
        if (!npc.getPersistentData().getBoolean(CARAVAN_TAG)) return;
        var bond = ModCapabilities.getOrDefault(player);
        if (bond.isCaravanEscortActive()) return;

        int destX = player.getBlockX() + player.getRandom().nextInt(200) - 100;
        int destZ = player.getBlockZ() + player.getRandom().nextInt(200) - 100;
        bond.setCaravanRoute(player.getBlockX(), player.getBlockZ(), destX, destZ);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.caravan_escort_start",
                destX, destZ), true);
    }

    public static void tickEscort(ServerPlayer player) {
        var bond = ModCapabilities.getOrDefault(player);
        if (!bond.isCaravanEscortActive()) return;

        double traveled = Math.hypot(
                player.getX() - bond.getCaravanStartX(),
                player.getZ() - bond.getCaravanStartZ());
        double toDest = Math.hypot(
                player.getX() - bond.getCaravanDestX(),
                player.getZ() - bond.getCaravanDestZ());

        if (traveled >= 100 && toDest <= 40) {
            onEscortComplete(player);
            bond.clearCaravanEscort();
        }
    }

    public static void onEscortComplete(ServerPlayer player) {
        ModCapabilities.getOrDefault(player).addShadowCoins(8);
        ModCapabilities.getOrDefault(player).addOverworldInfluence(5);
        player.getInventory().add(new net.minecraft.world.item.ItemStack(ModItems.CATNIP_ITEM.get(), 5));
        player.displayClientMessage(Component.translatable("penetration.cocojenna.caravan_reward"), true);
    }
}
