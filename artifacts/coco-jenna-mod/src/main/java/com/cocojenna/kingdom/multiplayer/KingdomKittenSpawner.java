package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.entity.KingdomKittenEntity;
import com.cocojenna.init.ModEntities;
import net.minecraft.server.level.ServerPlayer;

public final class KingdomKittenSpawner {

    private KingdomKittenSpawner() {}

    public static void spawnFor(ServerPlayer player) {
        KingdomKittenEntity kitten = ModEntities.KINGDOM_KITTEN.get().create(player.serverLevel());
        if (kitten == null) return;
        kitten.setPos(player.getX() + 1, player.getY(), player.getZ());
        kitten.setOwnerUUID(player.getUUID());
        player.serverLevel().addFreshEntity(kitten);
    }
}
