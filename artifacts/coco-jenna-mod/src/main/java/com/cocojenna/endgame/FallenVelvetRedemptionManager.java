package com.cocojenna.endgame;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.world.ForgottenTowerGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 墮落絨尾救贖線 — 雨後遺忘高塔（設計書 卷六 §7.3）. */
public final class FallenVelvetRedemptionManager {

    private FallenVelvetRedemptionManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 40 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isFallenVelvetRedeemed()) return;
        if (!FallenVelvetChoiceManager.eligible(bond)) return;

        BlockPos pos = player.blockPosition();
        if (pos.distSqr(ForgottenTowerGenerator.CENTER) > 40L * 40L) return;

        if (!hasItem(player, ModItems.HIBISCUS_TEAR.get()) || !hasItem(player, ModItems.PURE_TEAR.get())) {
            if (player.tickCount % 200 == 0) {
                player.displayClientMessage(
                        Component.translatable("redemption.cocojenna.fallen_velvet.need_items"), true);
            }
            return;
        }

        if (player.getPersistentData().getBoolean("cocojenna_velvet_choice_pending")) return;
        player.getPersistentData().putBoolean("cocojenna_velvet_choice_pending", true);
        FallenVelvetChoiceManager.offerChoice(player);
    }

    private static boolean hasItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        return player.getInventory().countItem(item) > 0;
    }
}
