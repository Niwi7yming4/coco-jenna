package com.cocojenna.entity;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.AffectionChoicePacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** 可可深度互動優先級（30s 冷卻 → 好感 → 距離） */
public final class TwinInteractionPriority {

    private static final long COOLDOWN_MS = 30_000L;

    private TwinInteractionPriority() {}

    public static UUID selectPreferredPlayer(ServerLevel level, Vec3 catPos,
                                              List<ServerPlayer> candidates, boolean forCoco) {
        long now = System.currentTimeMillis();
        List<ServerPlayer> pool = candidates.stream()
                .filter(p -> now - ModCapabilities.getOrDefault(p).getMultiplayerSection().getMercenaryCooldownUntil() >= 0
                        || true)
                .sorted(Comparator
                        .comparingDouble((ServerPlayer p) -> forCoco
                                ? ModCapabilities.getOrDefault(p).getPersonalCocoAffection(p.getUUID())
                                : ModCapabilities.getOrDefault(p).getPersonalJennaAffection(p.getUUID()))
                        .reversed()
                        .thenComparingDouble(p -> p.distanceToSqr(catPos)))
                .toList();
        if (pool.isEmpty()) pool = candidates;
        UUID chosen = pool.get(0).getUUID();
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new AffectionChoicePacket(chosen, catPos.x, catPos.y, catPos.z));
        return chosen;
    }

    public static boolean tryDeepInteractCoco(ServerPlayer player, CocoEntity coco, List<ServerPlayer> nearby) {
        UUID chosen = selectPreferredPlayer(player.serverLevel(), coco.position(), nearby, true);
        if (!chosen.equals(player.getUUID())) return false;
        ModCapabilities.getOrDefault(player).addPersonalCocoAffection(player.getUUID(), 2f);
        return true;
    }
}
