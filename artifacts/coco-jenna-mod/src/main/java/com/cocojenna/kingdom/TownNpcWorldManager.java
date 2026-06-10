package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.entity.TownNpcCompanionEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Set;

/** 將 BondData 招募的城鎮 NPC 同步為世界實體. */
public final class TownNpcWorldManager {

    private TownNpcWorldManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 100 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        Set<String> need = new HashSet<>();
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (bond.isTownNpcRecruited(p.id()) || isAutoRecruited(bond, p)) {
                need.add(p.id());
            }
        }
        if (need.isEmpty()) return;

        ServerLevel level = player.serverLevel();
        AABB box = player.getBoundingBox().inflate(48);
        var existing = level.getEntitiesOfClass(TownNpcCompanionEntity.class, box);

        Set<String> present = new HashSet<>();
        for (TownNpcCompanionEntity e : existing) {
            if (player.getUUID().equals(e.getOwnerUUID())) {
                if (!need.contains(e.getNpcId())) {
                    e.discard();
                } else {
                    present.add(e.getNpcId());
                }
            }
        }

        int spawned = 0;
        for (String id : need) {
            if (present.contains(id) || spawned >= 4) continue;
            spawnCompanion(level, player, id);
            spawned++;
        }
    }

    private static void spawnCompanion(ServerLevel level, ServerPlayer player, String npcId) {
        var entity = ModEntities.TOWN_NPC_COMPANION.get().create(level);
        if (entity == null) return;
        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double dist = 4 + player.getRandom().nextDouble() * 4;
        entity.setPos(player.getX() + Math.cos(angle) * dist,
                player.getY(), player.getZ() + Math.sin(angle) * dist);
        entity.setNpcId(npcId);
        entity.setOwnerUUID(player.getUUID());
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile != null) {
            entity.setCustomName(net.minecraft.network.chat.Component.literal(profile.nameZh()));
            entity.setCustomNameVisible(true);
        }
        level.addFreshEntity(entity);
    }

    private static boolean isAutoRecruited(BondData bond, TownNpcProfile p) {
        return switch (p.id()) {
            case "ironpaw" -> bond.isMetIronpaw();
            case "sanhua" -> bond.hasPeaceScene("afterrain_velvet");
            case "cheshire" -> bond.isMetBlindMerchant();
            case "white_glove" -> bond.hasPeaceScene("afterrain_blind_port");
            case "alpha" -> bond.isEndgameUnlocked();
            case "samurai" -> bond.getFirstCryQuestStage() >= com.cocojenna.quest.FirstCryQuestManager.STAGE_DUEL_DONE;
            case "monk" -> bond.isBuildingPlaced("cat_library") || bond.hasPeaceScene("afterrain_gear_town");
            case "court_lady" -> bond.isBuildingPlaced("open_air_theater") || bond.getKingdomHappiness() >= 75;
            default -> false;
        };
    }
}
