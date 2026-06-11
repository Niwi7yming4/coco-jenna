package com.cocojenna.kingdom.multiplayer;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.CatBondAggregator;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.AfterRainManager;
import com.cocojenna.endgame.kingdom.AfterRainKingdomManager;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.kingdom.multiplayer.Permission;
import com.cocojenna.trade.PlayerTradeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 王國多人系統 — 登入、tick、方塊權限、領地 */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class MultiplayerKingdomEvents {

    private static long lastDailyTickDay = -1;

    private MultiplayerKingdomEvents() {}

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        KingdomAuthorityManager.onPlayerLogin(player);
        KingdomLeaderboardManager.syncFromBond(player);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        OfflineTaskManager.tickLogout(player);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) continue;
            CatBondAggregator.tick(level);
            long day = level.getDayTime() / 24000L;
            if (day != lastDailyTickDay && level.getGameTime() % 100 == 0) {
                lastDailyTickDay = day;
                KingdomAuthorityManager.tickDaily(level);
                GlobalEventManager.tickDaily(level);
                for (ServerPlayer p : level.players()) {
                    AfterRainKingdomManager.tickDaily(p);
                    OfflineTaskManager.tickDaily(p);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        PersonalClaimSavedData claims = PersonalClaimSavedData.get(player.serverLevel());
        PersonalClaimSavedData.Claim claim = claims.at(event.getPos());
        if (claim != null) {
            if (!claim.owner().equals(player.getUUID()) && !claim.guests().contains(player.getUUID())) {
                event.setCanceled(true);
            }
            return;
        }
        if (!KingdomPermissionGuard.check(player, Permission.DESTROY)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        PersonalClaimSavedData claims = PersonalClaimSavedData.get(player.serverLevel());
        PersonalClaimSavedData.Claim claim = claims.at(event.getPos());
        if (claim != null) {
            if (!claim.owner().equals(player.getUUID()) && !claim.guests().contains(player.getUUID())) {
                event.setCanceled(true);
            }
            return;
        }
        if (!KingdomPermissionGuard.check(player, Permission.BUILD)) {
            event.setCanceled(true);
        }
    }

    public static boolean hasLoneWolfMedal(ServerPlayer player) {
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.LONE_WOLF_MEDAL.get())) return true;
        }
        return false;
    }
}
