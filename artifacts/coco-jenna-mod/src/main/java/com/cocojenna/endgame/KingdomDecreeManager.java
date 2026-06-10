package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenKingdomTerminalPacket;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 王國法令頒布、撤銷、到期與效果. */
public final class KingdomDecreeManager {

    public static final int MAX_ACTIVE = 3;

    private KingdomDecreeManager() {}

    public static void openTerminal(ServerPlayer player) {
        com.cocojenna.endgame.kingdom.AfterRainKingdomManager.openHub(player);
    }

    public static void openLegacyTerminal(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) {
            player.displayClientMessage(Component.translatable("afterrain.cocojenna.not_ready"), true);
            return;
        }
        BondData bond = ModCapabilities.getOrDefault(player);
        ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new OpenKingdomTerminalPacket(bond.serializeNBT()));
    }

    public static void tick(ServerLevel level) {
        if (!AfterRainManager.isAfterRain(level)) return;
        if (level.getGameTime() % 200 != 0) return;
        for (ServerPlayer player : level.players()) {
            BondData bond = ModCapabilities.getOrDefault(player);
            boolean changed = expireDecrees(bond, level.getGameTime());
            applyPassiveEffects(player, bond);
            if (changed) {
                ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                        new SyncBondDataPacket(bond.serializeNBT()));
            }
        }
    }

    public static boolean enact(ServerPlayer player, String decreeId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!AfterRainGameplayManager.isPeaceMode(player)) return false;
        KingdomDecreeCatalog.DecreeDef def = KingdomDecreeCatalog.get(decreeId);
        if (def.throneRequired() && bond.getKingdomProsperity() < 30) {
            player.displayClientMessage(Component.translatable("decree.cocojenna.need_throne"), true);
            return false;
        }
        if ("twin_blessing".equals(decreeId)) {
            long elapsed = player.serverLevel().getGameTime() - bond.getTwinBlessingLastEnact();
            if (elapsed < 30L * 24000L) {
                player.displayClientMessage(Component.translatable("decree.cocojenna.twin_cooldown"), true);
                return false;
            }
            if (!com.cocojenna.endgame.kingdom.CatLibraryManager.bothCatsPresent(player)) {
                player.displayClientMessage(Component.translatable("decree.cocojenna.need_both_cats"), true);
                return false;
            }
        }
        if ("furball_play".equals(decreeId) || "eternal_spring".equals(decreeId)) {
            if (!com.cocojenna.endgame.kingdom.CatLibraryManager.catsPresentForDecree(player)) {
                player.displayClientMessage(Component.translatable("decree.cocojenna.need_cat_present"), true);
                return false;
            }
        }
        if (bond.getActiveDecrees().size() >= MAX_ACTIVE) {
            player.displayClientMessage(Component.translatable("decree.cocojenna.slots_full"), true);
            return false;
        }
        for (BondData.ActiveDecree active : bond.getActiveDecrees()) {
            KingdomDecreeCatalog.DecreeDef existing = KingdomDecreeCatalog.get(active.id());
            if (existing.conflicts().contains(decreeId) || def.conflicts().contains(active.id())) {
                player.displayClientMessage(Component.translatable("decree.cocojenna.conflict"), true);
                return false;
            }
        }
        if (def.shardCost() > 0 && !bond.spendMemoryShards(def.shardCost())) {
            player.displayClientMessage(Component.translatable("decree.cocojenna.need_shards"), true);
            return false;
        }
        if (bond.getKingdomReputation() < def.repCost()) {
            player.displayClientMessage(Component.translatable("decree.cocojenna.need_rep"), true);
            return false;
        }
        long expires = player.serverLevel().getGameTime() + def.durationTicks();
        bond.addActiveDecree(new BondData.ActiveDecree(decreeId, expires));
        if ("twin_blessing".equals(decreeId)) {
            bond.setTwinBlessingLastEnact(player.serverLevel().getGameTime());
        }
        bond.setKingdomDecree(decreeId);
        player.displayClientMessage(Component.translatable("decree.cocojenna.enacted", def.name()), true);
        ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
        return true;
    }

    public static boolean revoke(ServerPlayer player, int slot) {
        BondData bond = ModCapabilities.getOrDefault(player);
        List<BondData.ActiveDecree> list = new ArrayList<>(bond.getActiveDecrees());
        if (slot < 0 || slot >= list.size()) return false;
        list.remove(slot);
        bond.setActiveDecrees(list);
        bond.addKingdomReputation(-5);
        player.displayClientMessage(Component.translatable("decree.cocojenna.revoked"), true);
        ModNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
        return true;
    }

    private static boolean expireDecrees(BondData bond, long now) {
        boolean changed = false;
        Iterator<BondData.ActiveDecree> it = bond.getActiveDecrees().iterator();
        while (it.hasNext()) {
            if (it.next().expiresAt() <= now) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    private static void applyPassiveEffects(ServerPlayer player, BondData bond) {
        KingdomDecreeWorldEffects.tickPlayerDecrees(player, bond);
        for (BondData.ActiveDecree d : bond.getActiveDecrees()) {
            switch (d.id()) {
                case "builder_rush" -> bond.addBuildCreativity(1);
                case "rest_day" -> bond.addKingdomStability(1);
                default -> {}
            }
        }
        if (bond.getKingdomHappiness() > 70 && player.serverLevel().getGameTime() % 1200 == 0) {
            bond.addKingdomProsperity(1);
        }
    }
}
