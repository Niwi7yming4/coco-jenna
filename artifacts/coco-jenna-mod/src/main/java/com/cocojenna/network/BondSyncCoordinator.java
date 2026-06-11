package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分層 BondData 同步：高頻情感增量、中頻儀式／印記、低頻王國完整同步.
 */
public final class BondSyncCoordinator {

    private static final int DEBOUNCE_TICKS = 40;
    private static final int BATCH_INTERVAL_TICKS = 200;

    private static final Map<UUID, PendingHighFreq> HIGH_FREQ = new ConcurrentHashMap<>();

    private BondSyncCoordinator() {}

    private static final class PendingHighFreq {
        long lastChangeTick;
        long lastSentTick;
        boolean dirty;
    }

    /** 情感、撫摸、餵食等高頻變動 — 防抖 2 秒或每 10 秒打包. */
    public static void onHighFrequencyChange(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        HIGH_FREQ.compute(player.getUUID(), (id, pending) -> {
            if (pending == null) pending = new PendingHighFreq();
            pending.dirty = true;
            pending.lastChangeTick = now;
            return pending;
        });
    }

    public static void tick(ServerPlayer player) {
        PendingHighFreq pending = HIGH_FREQ.get(player.getUUID());
        if (pending == null || !pending.dirty) return;

        long now = player.serverLevel().getGameTime();
        boolean debounced = now - pending.lastChangeTick >= DEBOUNCE_TICKS;
        boolean periodic = now - pending.lastSentTick >= BATCH_INTERVAL_TICKS;
        if (!debounced && !periodic) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeCatBondSection()));
        pending.dirty = false;
        pending.lastSentTick = now;
    }

    /** 儀式階段切換 — 立即輕量封包. */
    public static void syncCeremonyStage(ServerPlayer player, BondData bond) {
        long end = bond.getCeremonyTimeout() > 0 ? bond.getCeremonyTimeout() : 0L;
        long start = bond.getCeremonyStageStartGameTime();
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new CeremonyStagePacket(bond.getCeremonyStage(), bond.getFelineTier(), end, start));
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new MarkUpdatePacket(bond.getMarkLevel(), bond.getMarkForce(),
                        bond.getCeremonyStage(), bond.getFelineTier()));
    }

    /** 印記解鎖／晉升卡牌 — 立即序列區段同步. */
    public static void syncSequenceImmediate(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeSequenceSection(), IncrementalBondSyncPacket.Section.SEQUENCE));
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new MarkUpdatePacket(bond.getMarkLevel(), bond.getMarkForce(),
                        bond.getCeremonyStage(), bond.getFelineTier()));
    }

    /** 登入／換維度 — 王國與貨幣完整區段. */
    public static void syncKingdomLowFrequency(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeKingdomSection(), IncrementalBondSyncPacket.Section.KINGDOM));
    }

    /** 登入／換維度：分區同步，避免單封包 NBT 過大導致斷線. */
    public static void syncLogin(ServerPlayer player, BondData bond) {
        HIGH_FREQ.remove(player.getUUID());
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeCatBondSection(), IncrementalBondSyncPacket.Section.CAT_BOND));
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeSequenceSection(), IncrementalBondSyncPacket.Section.SEQUENCE));
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeKingdomSection(), IncrementalBondSyncPacket.Section.KINGDOM));
    }

    /** 重大變更：完整 NBT（已裁剪）. */
    public static void syncFull(ServerPlayer player, BondData bond) {
        HIGH_FREQ.remove(player.getUUID());
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    public static void flushHighFrequency(ServerPlayer player) {
        PendingHighFreq pending = HIGH_FREQ.get(player.getUUID());
        if (pending == null || !pending.dirty) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new IncrementalBondSyncPacket(bond.serializeCatBondSection()));
        pending.dirty = false;
        pending.lastSentTick = player.serverLevel().getGameTime();
    }

    public static void onLogout(ServerPlayer player) {
        flushHighFrequency(player);
        HIGH_FREQ.remove(player.getUUID());
    }
}
