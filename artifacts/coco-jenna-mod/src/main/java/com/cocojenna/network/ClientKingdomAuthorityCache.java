package com.cocojenna.network;

import net.minecraft.nbt.CompoundTag;

/** 客戶端快取王國內閣狀態（供 Hub UI） */
public final class ClientKingdomAuthorityCache {

    private static CompoundTag cached = new CompoundTag();

    private ClientKingdomAuthorityCache() {}

    public static void apply(CompoundTag tag) {
        if (tag != null) cached = tag.copy();
    }

    public static CompoundTag get() { return cached; }
}
