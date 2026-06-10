package com.cocojenna.network;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/** 伺服器 → 客戶端：同步玩家的 BondData */
public class SyncBondDataPacket {

    private static final int MAX_MICRO_CD = 48;
    private static final int MAX_JOURNAL = 48;
    private static final int MAX_STRING = 256;

    private final CompoundTag nbt;

    public SyncBondDataPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    /** Trim unbounded lists/strings before network send to avoid encoder overflow. */
    public static CompoundTag pruneForNetwork(CompoundTag source) {
        if (source == null) return new CompoundTag();
        CompoundTag tag = source.copy();
        if (tag.contains("kingdomMicroCooldown", Tag.TAG_LIST)) {
            ListTag list = tag.getList("kingdomMicroCooldown", Tag.TAG_COMPOUND);
            while (list.size() > MAX_MICRO_CD) {
                list.remove(0);
            }
        }
        if (tag.contains("explorationJournal", Tag.TAG_LIST)) {
            ListTag list = tag.getList("explorationJournal", Tag.TAG_STRING);
            while (list.size() > MAX_JOURNAL) {
                list.remove(list.size() - 1);
            }
        }
        for (String key : List.of("festivalWish", "pendingFestivalWish", "kingdomDecree",
                "lastFamilyEvent", "activeVillageFestival", "lastSeasonalFestival")) {
            if (tag.contains(key, Tag.TAG_STRING)) {
                String v = tag.getString(key);
                if (v.length() > MAX_STRING) {
                    tag.putString(key, v.substring(0, MAX_STRING));
                }
            }
        }
        return tag;
    }

    public static void encode(SyncBondDataPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(pruneForNetwork(packet.nbt));
    }

    public static SyncBondDataPacket decode(FriendlyByteBuf buf) {
        return new SyncBondDataPacket(buf.readNbt());
    }

    public static void handle(SyncBondDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null && packet.nbt != null) {
                ModCapabilities.get(player).ifPresent(bond -> bond.deserializeNBT(packet.nbt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
