package com.cocojenna.overworld;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/** 主世界滲透主線 action-bar 提示. */
public final class PenetrationQuestHud {

    private PenetrationQuestHud() {}

    public static void tick(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        if (player.level().getGameTime() % 100 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getPenetrationQuestStage() >= PenetrationQuestManager.STAGE_COMPLETE) return;

        String key = switch (bond.getPenetrationQuestStage()) {
            case PenetrationQuestManager.STAGE_MOON_PAW -> "penetration.cocojenna.hud.moon_paw";
            case PenetrationQuestManager.STAGE_MEMORY_SHARDS ->
                    bond.getMemoryShardsTotal() >= 5
                            ? "penetration.cocojenna.hud.shards_ready"
                            : "penetration.cocojenna.hud.shards";
            case PenetrationQuestManager.STAGE_CAT_LANGUAGE ->
                    PenetrationQuestManager.isDungeonCleared(player)
                            ? "penetration.cocojenna.hud.cat_language_done"
                            : "penetration.cocojenna.hud.cat_language";
            case PenetrationQuestManager.STAGE_REPAIR_PORTAL -> "penetration.cocojenna.hud.repair_portal";
            case PenetrationQuestManager.STAGE_FIRST_ENTRY -> "penetration.cocojenna.hud.first_entry";
            default -> null;
        };
        if (key != null) {
            player.displayClientMessage(Component.translatable(key,
                    bond.getMoonPawTrailCount(),
                    bond.getMemoryShardsTotal(),
                    bond.getCatLanguageLevel(),
                    bond.getCatGraffitiRead()), true);
        }
    }
}
