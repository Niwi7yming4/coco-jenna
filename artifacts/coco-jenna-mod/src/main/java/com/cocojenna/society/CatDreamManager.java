package com.cocojenna.society;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.overworld.PenetrationQuestManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 城鎮 NPC 夢想進度（設計書 §4.7）. */
public final class CatDreamManager {

    private CatDreamManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (player.isSleeping() && player.tickCount % 40 == 0) {
            BondData sleepBond = ModCapabilities.getOrDefault(player);
            if (sleepBond.getMemoryShardsTotal() > 0 && player.getRandom().nextFloat() < 0.12f) {
                tryDreamScene(player, sleepBond);
            }
            if (sleepBond.getMemoryShardsTotal() > 0 && player.getRandom().nextFloat() < 0.06f) {
                var shard = com.cocojenna.util.MemoryShardUtil.create("cat_dream");
                if (!player.addItem(shard)) player.drop(shard, false);
                player.displayClientMessage(Component.translatable("society.cocojenna.dream.shard"), true);
            }
        }
        if (player.tickCount % 200 != 0) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        for (TownNpcProfile profile : TownNpcProfile.ALL) {
            if (!bond.isTownNpcRecruited(profile.id())) continue;
            int stage = bond.getTownNpcDreamStage(profile.id());
            int favor = bond.getTownNpcFavor(profile.id());
            if (stage == 0 && favor >= 25) {
                bond.setTownNpcDreamStage(profile.id(), 1);
                player.displayClientMessage(Component.translatable("society.cocojenna.dream.start",
                        profile.nameZh(), dreamText(player, profile.id())), true);
            } else if (stage == 1 && favor >= 60) {
                bond.setTownNpcDreamStage(profile.id(), 2);
                player.displayClientMessage(Component.translatable("society.cocojenna.dream.progress",
                        profile.nameZh()), true);
            } else if (stage == 2 && favor >= 90) {
                bond.setTownNpcDreamStage(profile.id(), 3);
                bond.addKingdomHappiness(3);
                player.displayClientMessage(Component.translatable("society.cocojenna.dream.complete",
                        profile.nameZh()), true);
            } else if (stage == 3 && favor >= 95) {
                bond.setTownNpcDreamStage(profile.id(), 4);
                bond.addKingdomProsperity(2);
                player.displayClientMessage(Component.translatable("society.cocojenna.dream.transcendent",
                        profile.nameZh()), true);
            }
        }
    }

    private static void tryDreamScene(ServerPlayer player, BondData bond) {
        String scene = pickDreamScene(player, bond);
        if (com.cocojenna.dialogue.DialogueScripts.get(scene) != null) {
            DialogueManager.play(player, scene);
        }
    }

    private static String pickDreamScene(ServerPlayer player, BondData bond) {
        if (bond.getPenetrationQuestStage() < PenetrationQuestManager.STAGE_COMPLETE) {
            return "cat_dream_penetration";
        }
        for (TownNpcProfile profile : TownNpcProfile.ALL) {
            if (bond.isTownNpcRecruited(profile.id()) && bond.getTownNpcDreamStage(profile.id()) >= 1) {
                return "cat_dream_npc_" + profile.id();
            }
        }
        for (var npc : com.cocojenna.world.firstcry.FirstCryAnchorTable.npcs()) {
            if (player.blockPosition().distSqr(npc.pos()) < 48 * 48) {
                return "cat_dream_first_cry_" + npc.npcId();
            }
        }
        int roll = player.getRandom().nextInt(3);
        return switch (roll) {
            case 0 -> "cat_dream_moon";
            case 1 -> "cat_dream_village";
            default -> "cat_dream_coco";
        };
    }

    public static String dreamText(ServerPlayer player, String npcId) {
        int seed = npcId.hashCode() ^ player.getUUID().hashCode();
        return CatNpcNamePool.randomDream(net.minecraft.util.RandomSource.create(seed));
    }

    public static Component dreamLabel(BondData bond, String npcId, String nameZh) {
        int stage = bond.getTownNpcDreamStage(npcId);
        if (stage == 0) {
            return Component.translatable("society.cocojenna.dream.locked", nameZh);
        }
        if (stage >= 4) {
            return Component.translatable("society.cocojenna.dream.transcendent_label", nameZh);
        }
        if (stage >= 3) {
            return Component.translatable("society.cocojenna.dream.done", nameZh);
        }
        return Component.translatable("society.cocojenna.dream.active", nameZh, stage);
    }
}
