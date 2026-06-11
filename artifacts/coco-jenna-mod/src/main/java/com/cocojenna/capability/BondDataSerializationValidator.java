package com.cocojenna.capability;

import com.cocojenna.CocoJennaMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/** 登入時驗證 BondData NBT round-trip，避免欄位遺失. */
public final class BondDataSerializationValidator {

    private BondDataSerializationValidator() {}

    public static void validateOnLogin(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        try {
            CompoundTag original = bond.serializeNBT();
            BondData probe = new BondData();
            probe.deserializeNBT(original);
            CompoundTag roundTrip = probe.serializeNBT();

            if (original.getAllKeys().size() != roundTrip.getAllKeys().size()) {
                CocoJennaMod.LOGGER.warn(
                        "BondData key count mismatch for {}: {} vs {}",
                        player.getGameProfile().getName(),
                        original.getAllKeys().size(),
                        roundTrip.getAllKeys().size());
            }
            if (bond.getFelineTier() != probe.getFelineTier()
                    || bond.getMemoryShardsTotal() != probe.getMemoryShardsTotal()
                    || Math.abs(bond.getCocoEmotion() - probe.getCocoEmotion()) > 0.01f) {
                CocoJennaMod.LOGGER.warn("BondData round-trip drift detected for {}",
                        player.getGameProfile().getName());
            }
        } catch (Exception ex) {
            CocoJennaMod.LOGGER.error("BondData serialization failed for {}",
                    player.getGameProfile().getName(), ex);
        }
    }
}
