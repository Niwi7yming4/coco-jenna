package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.TownNpcCompanionEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.society.CatFamilyManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

/** 城鎮 NPC 每日行程與家庭 lore 對話. */
public final class TownNpcScheduleManager {

    private TownNpcScheduleManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 200 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        CatFamilyManager.ensureFamilies(player);

        long dayTime = player.level().getDayTime() % 24000L;
        String phase = dayTime < 6000 ? "morning" : dayTime < 12000 ? "day" : dayTime < 18000 ? "evening" : "night";

        ServerLevel level = player.serverLevel();
        AABB box = player.getBoundingBox().inflate(48);
        for (TownNpcCompanionEntity npc : level.getEntitiesOfClass(TownNpcCompanionEntity.class, box)) {
            if (!player.getUUID().equals(npc.getOwnerUUID())) continue;
            applySchedule(npc, phase);
            if (player.distanceToSqr(npc) < 16 && player.tickCount % 400 == 0) {
                tryLoreChat(player, bond, npc.getNpcId(), phase);
            }
        }
    }

    private static void applySchedule(TownNpcCompanionEntity npc, String phase) {
        switch (phase) {
            case "morning", "day" -> npc.getNavigation().setSpeedModifier(0.55);
            case "evening" -> npc.getNavigation().setSpeedModifier(0.35);
            default -> npc.getNavigation().setSpeedModifier(0.2);
        }
    }

    private static void tryLoreChat(ServerPlayer player, BondData bond, String npcId, String phase) {
        String family = bond.getTownNpcFamily(npcId);
        String role = bond.getTownNpcFamilyRole(npcId);
        if (!family.isEmpty() && !role.isEmpty()) {
            player.displayClientMessage(Component.translatable(
                    "society.cocojenna.schedule.family_" + role,
                    bond.getTownNpcFavor(npcId), family), false);
            return;
        }
        player.displayClientMessage(Component.translatable(
                "society.cocojenna.schedule." + phase + "." + npcId), false);
    }
}
