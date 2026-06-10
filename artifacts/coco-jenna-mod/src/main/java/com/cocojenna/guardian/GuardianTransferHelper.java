package com.cocojenna.guardian;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

/** 守護者 7 日離線轉移儀式（Alpha NPC）. */
public final class GuardianTransferHelper {

    private static final long SEVEN_DAYS = 24000L * 7;

    private GuardianTransferHelper() {}

    public static void recordLogout(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setLastOnlineTick(player.level().getGameTime());
        if (bond.isGuardian()) {
            GuardianWorldData data = GuardianWorldData.get(player.server);
            data.setGuardian(player.getUUID(), player.level().getGameTime());
        }
    }

    public static void onPlayerLogin(ServerPlayer player) {
        GuardianWorldData data = GuardianWorldData.get(player.server);
        UUID pending = data.pendingStripGuardian();
        if (pending != null && pending.equals(player.getUUID())) {
            BondData bond = ModCapabilities.getOrDefault(player);
            bond.setGuardian(false);
            data.clearPendingStrip();
            player.displayClientMessage(Component.translatable("guardian.cocojenna.stripped"), true);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
        }
        if (ModCapabilities.getOrDefault(player).isGuardian()) {
            data.setGuardian(player.getUUID(), data.guardianLastLogoutTick());
        }
    }

    public static boolean canVoluntaryTransfer(ServerPlayer from) {
        BondData bond = ModCapabilities.getOrDefault(from);
        if (!bond.isGuardian()) return false;
        long now = from.level().getGameTime();
        return now - bond.getLastOnlineTick() >= SEVEN_DAYS;
    }

    public static boolean canClaimInactiveGuardianship(ServerPlayer claimant) {
        if (ModCapabilities.getOrDefault(claimant).isGuardian()) return false;
        return findOfflineInactiveGuardian(claimant.server) != null;
    }

    @Deprecated
    public static void recordOnline(ServerPlayer player) {}

    public static void transfer(ServerPlayer from, ServerPlayer to) {
        if (!canVoluntaryTransfer(from)) {
            from.displayClientMessage(Component.translatable("guardian.cocojenna.transfer_not_ready"), true);
            return;
        }
        applyTransfer(from, to);
    }

    public static void claimInactiveGuardianship(ServerPlayer claimant) {
        UUID inactiveId = findOfflineInactiveGuardian(claimant.server);
        if (inactiveId == null) {
            claimant.displayClientMessage(Component.translatable("guardian.cocojenna.no_inactive_guardian"), true);
            return;
        }
        GuardianWorldData data = GuardianWorldData.get(claimant.server);
        data.setPendingStrip(inactiveId);
        data.clearGuardian();
        BondData toBond = ModCapabilities.getOrDefault(claimant);
        toBond.setGuardian(true);
        toBond.setLastOnlineTick(claimant.level().getGameTime());
        data.setGuardian(claimant.getUUID(), claimant.level().getGameTime());
        reassignCatsFromUuid(inactiveId, claimant);
        claimant.displayClientMessage(Component.translatable("guardian.cocojenna.transferred_to"), true);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> claimant),
                new SyncBondDataPacket(toBond.serializeNBT()));
    }

    private static void applyTransfer(ServerPlayer from, ServerPlayer to) {
        BondData fromBond = ModCapabilities.getOrDefault(from);
        BondData toBond = ModCapabilities.getOrDefault(to);
        fromBond.setGuardian(false);
        toBond.setGuardian(true);
        toBond.setLastOnlineTick(to.level().getGameTime());
        GuardianWorldData data = GuardianWorldData.get(from.server);
        data.setGuardian(to.getUUID(), to.level().getGameTime());
        reassignCats(from, to);
        from.displayClientMessage(Component.translatable("guardian.cocojenna.transferred_from"), true);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> from),
                new SyncBondDataPacket(fromBond.serializeNBT()));
        to.displayClientMessage(Component.translatable("guardian.cocojenna.transferred_to"), true);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> to),
                new SyncBondDataPacket(toBond.serializeNBT()));
    }

    @Nullable
    private static UUID findOfflineInactiveGuardian(MinecraftServer server) {
        GuardianWorldData data = GuardianWorldData.get(server);
        if (!data.hasGuardian()) return null;
        UUID gid = data.guardianUuid();
        if (gid == null) return null;
        if (server.getPlayerList().getPlayer(gid) != null) return null;
        long now = server.overworld().getGameTime();
        if (now - data.guardianLastLogoutTick() < SEVEN_DAYS) return null;
        return gid;
    }

    private static void reassignCats(ServerPlayer from, ServerPlayer to) {
        reassignCatsFromUuid(from.getUUID(), to);
    }

    private static void reassignCatsFromUuid(UUID fromId, ServerPlayer to) {
        AABB world = new AABB(-3.0E7, -256, -3.0E7, 3.0E7, 256, 3.0E7);
        UUID toId = to.getUUID();
        for (ServerLevel sl : to.server.getAllLevels()) {
            for (AbstractCatEntity cat : sl.getEntitiesOfClass(AbstractCatEntity.class, world)) {
                if (fromId.equals(cat.getOwnerUUID())) {
                    cat.setOwnerUUID(toId);
                }
            }
        }
    }
}
