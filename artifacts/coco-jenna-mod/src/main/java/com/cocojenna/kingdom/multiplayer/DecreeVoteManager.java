package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.network.KingdomDecreeResultPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

/** 法案提案 → 24h 投票 → 2/3 內閣 + 君主否決權 */
public final class DecreeVoteManager {

    private static final long VOTE_DURATION_MS = 24L * 60 * 60 * 1000;

    private DecreeVoteManager() {}

    public static boolean propose(ServerPlayer player, String proposal) {
        ServerLevel level = player.serverLevel();
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(level);
        KingdomRole role = auth.getRole(player.getUUID());
        if (role != KingdomRole.MONARCH && role != KingdomRole.DECREE_ADVISOR) {
            if (!KingdomPermissionGuard.require(player, Permission.DECREE_PROPOSE)) return false;
        }
        String id = "decree_" + System.currentTimeMillis();
        DecreeVote vote = new DecreeVote(id, player.getUUID(), proposal,
                System.currentTimeMillis() + VOTE_DURATION_MS);
        auth.getActiveVotes().add(vote);
        auth.addAudit("propose:" + player.getUUID() + ":" + proposal);
        KingdomAuthorityManager.syncToAll(level);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.decree_proposed"), true);
        return true;
    }

    public static boolean castVote(ServerPlayer player, String voteId, boolean yes) {
        ServerLevel level = player.serverLevel();
        KingdomAuthoritySavedData auth = KingdomAuthoritySavedData.get(level);
        UUID id = player.getUUID();
        for (DecreeVote v : auth.getActiveVotes()) {
            if (!v.id.equals(voteId) || v.resolved) continue;
            if (auth.getMonarch() != null && auth.getMonarch().equals(id)) {
                if (yes) v.monarchApproved = true;
                else v.monarchVeto = true;
            } else if (auth.getCabinetMembers().contains(id)) {
                v.votes.put(id, yes);
            } else if (!KingdomPermissionGuard.check(player, Permission.DECREE_VOTE)) {
                return false;
            }
            KingdomAuthorityManager.syncToAll(level);
            return true;
        }
        return false;
    }

    public static void resolveVote(ServerLevel level, KingdomAuthoritySavedData auth, DecreeVote v) {
        v.resolved = true;
        if (v.monarchVeto) {
            broadcastResult(level, v, false, "veto");
            return;
        }
        int yes = 0, total = auth.getCabinetMembers().size();
        for (UUID cab : auth.getCabinetMembers()) {
            if (Boolean.TRUE.equals(v.votes.get(cab))) yes++;
        }
        boolean passed = v.monarchApproved && total > 0 && yes * 3 >= total * 2;
        broadcastResult(level, v, passed, passed ? "passed" : "failed");
        if (passed) {
            auth.addAudit("decree_passed:" + v.proposal);
        }
    }

    private static void broadcastResult(ServerLevel level, DecreeVote v, boolean passed, String reason) {
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new KingdomDecreeResultPacket(v.id, v.proposal, passed, reason));
        for (ServerPlayer p : level.players()) {
            p.displayClientMessage(Component.translatable(
                    passed ? "kingdom.cocojenna.decree_passed" : "kingdom.cocojenna.decree_failed",
                    v.proposal), true);
        }
    }
}
