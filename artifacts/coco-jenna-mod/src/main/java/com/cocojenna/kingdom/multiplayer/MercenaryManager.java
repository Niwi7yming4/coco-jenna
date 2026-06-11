package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;

public final class MercenaryManager {

    private MercenaryManager() {}

    public static void setProfile(ServerPlayer player, int price) {
        MercenaryProfileSavedData.get(player.serverLevel()).saveProfile(player, price);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.merc_set", price), true);
    }

    public static boolean summon(ServerPlayer hirer, java.util.UUID ownerId) {
        CompoundTag prof = MercenaryProfileSavedData.get(hirer.serverLevel()).getProfile(ownerId);
        if (prof == null) return false;
        int price = prof.getInt("price");
        var bond = ModCapabilities.getOrDefault(hirer);
        if (bond.getShadowCoins() < price) {
            hirer.displayClientMessage(Component.translatable("kingdom.cocojenna.merc_no_coin"), true);
            return false;
        }
        bond.setShadowCoins(bond.getShadowCoins() - price);
        int payOwner = (int) (price * 0.7);
        ServerPlayer owner = hirer.server.getPlayerList().getPlayer(ownerId);
        if (owner != null) {
            ModCapabilities.getOrDefault(owner).setShadowCoins(
                    ModCapabilities.getOrDefault(owner).getShadowCoins() + payOwner);
        }
        Zombie merc = new Zombie(hirer.serverLevel());
        merc.setPos(hirer.getX() + 1, hirer.getY(), hirer.getZ());
        merc.setCustomName(Component.literal("§7傭兵"));
        merc.setCustomNameVisible(true);
        hirer.serverLevel().addFreshEntity(merc);
        hirer.displayClientMessage(Component.translatable("kingdom.cocojenna.merc_summoned"), true);
        return true;
    }
}
