package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.TeamBondUltimatePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.network.PacketDistributor;

/** 團隊共鳴 — 隊伍 ≥3 人且每人 coco 好感 ≥70 */
public final class TeamBondUltimateManager {

    private static final int EP_COST = 100;
    private static final long COOLDOWN_TICKS = 6000L;

    private TeamBondUltimateManager() {}

    public static boolean tryActivate(ServerPlayer leader) {
        Scoreboard sb = leader.getScoreboard();
        PlayerTeam team = sb.getPlayersTeam(leader.getScoreboardName());
        if (team == null) {
            leader.displayClientMessage(Component.translatable("kingdom.cocojenna.team_bond_no_team"), true);
            return false;
        }
        int count = 0;
        for (String member : team.getPlayers()) {
            ServerPlayer p = leader.server.getPlayerList().getPlayerByName(member);
            if (p == null) continue;
            if (ModCapabilities.getOrDefault(p).getPersonalCocoAffection(p.getUUID()) < 70f) {
                leader.displayClientMessage(Component.translatable("kingdom.cocojenna.team_bond_low_aff"), true);
                return false;
            }
            count++;
        }
        if (count < 3) {
            leader.displayClientMessage(Component.translatable("kingdom.cocojenna.team_bond_need_three"), true);
            return false;
        }
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new TeamBondUltimatePacket(leader.getUUID(), leader.getX(), leader.getY(), leader.getZ()));
        for (String member : team.getPlayers()) {
            ServerPlayer p = leader.server.getPlayerList().getPlayerByName(member);
            if (p == null) continue;
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0));
        }
        leader.displayClientMessage(Component.translatable("kingdom.cocojenna.team_bond_active"), true);
        return true;
    }
}
