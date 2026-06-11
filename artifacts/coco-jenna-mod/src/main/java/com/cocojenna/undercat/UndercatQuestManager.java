package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.ArenaGladiatorEntity;
import com.cocojenna.entity.UndercatHubNpcEntity;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.world.FirstCryVillageGenerator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;

/** 主線「深淵的迴聲」與地下貓域進度. */
public final class UndercatQuestManager {

    public static final int TRIAL_PIRATES = 1;
    public static final int TRIAL_LEECHES = 2;
    public static final int LEECHES_NEEDED = 8;

    private UndercatQuestManager() {}

    public static boolean canTriggerMainQuest(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() > 0) return false;
        if (bond.getCocoEmotion() < 40 || bond.getJennaEmotion() < 40) return false;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        return player.blockPosition().distSqr(FirstCryVillageGenerator.CENTER) < 80 * 80;
    }

    public static void tryTriggerAtNight(ServerPlayer player) {
        if (!canTriggerMainQuest(player)) return;
        long time = player.level().getDayTime() % 24000;
        if (time < 18000 || time > 22000) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setUndercatChapter(1);
        bond.setUndercatStage(0);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.quest_start"), true);
        DialogueManager.play(player, "undercat_ch1_trigger");
    }

    public static void acceptChapter(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() <= 0) bond.setUndercatChapter(1);
        bond.setUndercatStage(1);
        unlockRegion(bond, UndercatRegion.CARDBOARD_SLUMS);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.ch1_accepted"), true);
        syncHub(player);
    }

    public static void completeCommission(ServerPlayer player, UndercatCommission commission) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != commission.chapter) return;
        if ((bond.getUndercatCommissions() & commission.flag) != 0) return;
        bond.setUndercatCommissions(bond.getUndercatCommissions() | commission.flag);
        bond.addUndercatRep(UndercatFaction.CARDBOARD_KINGDOM, commission.repReward);
        bond.addShadowCoins(5);
        UndercatSideStoryManager.addCorrugataAffinity(player, 5);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.commission_done",
                Component.translatable("undercat.cocojenna.commission." + commission.name().toLowerCase())), true);
        if (UndercatCommission.countCompletedForChapter(bond.getUndercatCommissions(), 1) >= 3
                && bond.getUndercatChapter() == 1 && bond.getUndercatStage() < 3) {
            bond.setUndercatStage(3);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.boss_unlocked"), true);
        }
        syncHub(player);
    }

    public static void onTapeColossusDefeated(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 1) return;
        if (UndercatCommission.countCompletedForChapter(bond.getUndercatCommissions(), 1) < 3) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_commissions"), true);
            return;
        }
        bond.addUndercatRep(UndercatFaction.CARDBOARD_KINGDOM, 30);
        UndercatSideStoryManager.addCorrugataAffinity(player, 20);
        bond.setUndercatStage(5);
        bond.addShadowCoins(50);
        unlockRegion(bond, UndercatRegion.SMUGGLER_DOCK);
        giveOrDrop(player, new ItemStack(ModItems.TAPE_CORE.get()));
        giveOrDrop(player, new ItemStack(ModItems.CARDBOARD_BADGE.get()));
        bond.setUndercatChapter(2);
        bond.setUndercatStage(0);
        unlockRegion(bond, UndercatRegion.SERVANT_CAMP);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.tape_boss_done"), true);
        DialogueManager.play(player, "undercat_ch1_complete");
        syncHub(player);
    }

    public static void onHubNpcInteract(ServerPlayer player, UndercatHubNpcEntity.Role role) {
        BondData bond = ModCapabilities.getOrDefault(player);
        switch (role) {
            case CORRUGATA -> {
                openHub(player);
                if (!StarlightChapterManager.onUndercatHubInteract(player)
                        && !UndercatSideStoryManager.onCorrugataInteract(player)
                        && bond.getUndercatChapter() == 1 && bond.getUndercatStage() == 1) {
                    DialogueManager.play(player, "undercat_hub_fallback");
                }
            }
            case ONE_EYE -> handleOneEye(player, bond);
            case GREENPAW -> handleGreenpaw(player, bond);
            case SCARFACE -> handleScarface(player, bond);
            case ABBESS -> handleAbbess(player, bond);
            case HEAD_SERVANT -> {
                bond.addUndercatRep(UndercatFaction.SERVANT_CULT, 5);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.servant_greet"), true);
                openHub(player);
            }
        }
    }

    private static void handleOneEye(ServerPlayer player, BondData bond) {
        if (player.isShiftKeyDown() && bond.getUndercatChapter() >= 1) {
            com.cocojenna.shop.ShopOpener.openCatnipMarket(player);
            return;
        }
        if (bond.getUndercatChapter() < 2) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_ch2"), true);
            return;
        }
        if (bond.getUndercatStage() == 0) {
            bond.setUndercatStage(1);
            DialogueManager.play(player, "undercat_ch2_one_eye");
        } else if (!UndercatSideStoryManager.onOneEyeInteract(player)) {
            // no milestone dialogue this visit
        }
        openHub(player);
    }

    private static void handleGreenpaw(ServerPlayer player, BondData bond) {
        if (bond.getUndercatChapter() < 2 || bond.getUndercatStage() < 4) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_farm"), true);
            return;
        }
        DialogueManager.play(player, "undercat_ch2_greenpaw");
        openHub(player);
    }

    private static void handleScarface(ServerPlayer player, BondData bond) {
        if (bond.getUndercatChapter() < 3) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_ch3"), true);
            return;
        }
        if (bond.getUndercatStage() == 0) {
            bond.setUndercatStage(1);
            DialogueManager.play(player, "undercat_ch3_scarface");
        }
        openHub(player);
    }

    private static void handleAbbess(ServerPlayer player, BondData bond) {
        if (bond.getUndercatChapter() < 4) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.need_ch4"), true);
            return;
        }
        if (bond.getUndercatStage() == 0) {
            bond.setUndercatStage(1);
            DialogueManager.play(player, "undercat_ch4_abbess");
        }
        openHub(player);
    }

    public static void startTrial(ServerPlayer player, String trialId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 2) return;
        ServerLevel level = player.serverLevel();
        if ("pirates".equals(trialId)) {
            if ((bond.getUndercatTrials() & TRIAL_PIRATES) != 0) return;
            for (int i = 0; i < 4; i++) {
                var pirate = ModEntities.ARENA_GLADIATOR.get().create(level);
                if (pirate != null) {
                    pirate.setKind(ArenaGladiatorEntity.Kind.SHADOW_STEP);
                    pirate.setPos(player.getX() + i * 2, player.getY(), player.getZ() + 4);
                    pirate.finalizeSpawn(level, level.getCurrentDifficultyAt(player.blockPosition()),
                            MobSpawnType.TRIGGERED, null, null);
                    level.addFreshEntity(pirate);
                }
            }
            bond.setUndercatTrials(bond.getUndercatTrials() | TRIAL_PIRATES);
            completeCommission(player, UndercatCommission.PIRATE_TRIAL);
            bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 15);
            UndercatSideStoryManager.addOneEyeAffinity(player, 10);
            bond.addShadowCoins(30);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.trial_pirates"), true);
        } else if ("leeches".equals(trialId)) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.trial_leeches_hint"), true);
        }
        checkCh2Trials(player, bond);
        syncHub(player);
    }

    public static void onLeechKilled(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 2) return;
        bond.addUndercatLeechKills(1);
        if (bond.getUndercatLeechKills() >= LEECHES_NEEDED && (bond.getUndercatTrials() & TRIAL_LEECHES) == 0) {
            bond.setUndercatTrials(bond.getUndercatTrials() | TRIAL_LEECHES);
            completeCommission(player, UndercatCommission.LEECH_BOUNTY);
            bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 15);
            UndercatSideStoryManager.addOneEyeAffinity(player, 10);
            bond.addShadowCoins(20);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.trial_leeches_done"), true);
            checkCh2Trials(player, bond);
            syncHub(player);
        }
    }

    private static void checkCh2Trials(ServerPlayer player, BondData bond) {
        if ((bond.getUndercatTrials() & (TRIAL_PIRATES | TRIAL_LEECHES)) == (TRIAL_PIRATES | TRIAL_LEECHES)
                && bond.getUndercatStage() < 3) {
            bond.setUndercatStage(3);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.voyage_unlocked"), true);
        }
    }

    public static void startRiverVoyage(ServerPlayer player) {
        completeCommission(player, UndercatCommission.RIVER_CHART);
        RiverVoyageManager.begin(player);
    }

    public static void onCatnipDragonDefeated(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 2) return;
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, 30);
        bond.addShadowCoins(200);
        giveOrDrop(player, new ItemStack(ModItems.LEGEND_CATNIP_SEED.get()));
        bond.setUndercatChapter(3);
        bond.setUndercatStage(0);
        unlockRegion(bond, UndercatRegion.SCRATCH_ARENA);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.dragon_done"), true);
        DialogueManager.play(player, "undercat_ch2_complete");
        syncHub(player);
    }

    public static void spawnNextGladiator(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 3) return;
        int beaten = bond.getUndercatGladiators();
        ArenaGladiatorEntity.Kind next = null;
        if ((beaten & ArenaGladiatorEntity.Kind.IRON_FIST.flag) == 0) next = ArenaGladiatorEntity.Kind.IRON_FIST;
        else if ((beaten & ArenaGladiatorEntity.Kind.SHADOW_STEP.flag) == 0) next = ArenaGladiatorEntity.Kind.SHADOW_STEP;
        else if ((beaten & ArenaGladiatorEntity.Kind.POISON_FANG.flag) == 0) next = ArenaGladiatorEntity.Kind.POISON_FANG;
        if (next == null) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.arena_done"), true);
            return;
        }
        ServerLevel level = player.serverLevel();
        var gladiator = ModEntities.ARENA_GLADIATOR.get().create(level);
        if (gladiator != null) {
            gladiator.setKind(next);
            gladiator.setPos(player.getX() + 3, player.getY(), player.getZ());
            gladiator.finalizeSpawn(level, level.getCurrentDifficultyAt(player.blockPosition()),
                    MobSpawnType.TRIGGERED, null, null);
            level.addFreshEntity(gladiator);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.arena_spawn",
                    Component.translatable("undercat.cocojenna.gladiator." + next.name().toLowerCase())), true);
        }
    }

    public static void onGladiatorDefeated(ServerPlayer player, ArenaGladiatorEntity.Kind kind) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 3) return;
        if ((bond.getUndercatGladiators() & kind.flag) != 0) return;
        bond.setUndercatGladiators(bond.getUndercatGladiators() | kind.flag);
        if (kind == ArenaGladiatorEntity.Kind.IRON_FIST) completeCommission(player, UndercatCommission.ARENA_IRON);
        if (kind == ArenaGladiatorEntity.Kind.SHADOW_STEP) completeCommission(player, UndercatCommission.ARENA_SHADOW);
        if (kind == ArenaGladiatorEntity.Kind.POISON_FANG) completeCommission(player, UndercatCommission.ARENA_POISON);
        bond.addShadowCoins(25);
        bond.addUndercatRep(UndercatFaction.ARENA_BROTHERHOOD, 10);
        int all = ArenaGladiatorEntity.Kind.IRON_FIST.flag
                | ArenaGladiatorEntity.Kind.SHADOW_STEP.flag
                | ArenaGladiatorEntity.Kind.POISON_FANG.flag;
        if ((bond.getUndercatGladiators() & all) == all) {
            bond.addUndercatRep(UndercatFaction.ARENA_BROTHERHOOD, 30);
            giveOrDrop(player, new ItemStack(ModItems.SCARFACE_CHARM.get()));
            completeCommission(player, UndercatCommission.SCARFACE_FAVOR);
            bond.setUndercatChapter(4);
            bond.setUndercatStage(0);
            unlockRegion(bond, UndercatRegion.SILENT_LIBRARY);
            player.displayClientMessage(Component.translatable("undercat.cocojenna.ch3_complete"), true);
            DialogueManager.play(player, "undercat_ch3_complete");
        }
        ArenaBettingManager.onGladiatorWin(player);
        StarlightChapterManager.onArenaVictory(player);
        syncHub(player);
    }

    public static void startSilentTrial(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 4 || bond.getUndercatStage() < 1) return;
        bond.setUndercatStage(2);
        DialogueManager.play(player, "undercat_ch4_trial");
        syncHub(player);
    }

    public static void onSilencedOneDefeated(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 4) return;
        bond.addUndercatRep(UndercatFaction.SILENT_SISTERHOOD, 30);
        giveOrDrop(player, new ItemStack(ModItems.SEALED_MEMORY_BOOK.get()));
        giveOrDrop(player, new ItemStack(ModItems.SILENCED_SILVER_THREAD.get()));
        bond.setUndercatChapter(5);
        bond.setUndercatStage(0);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.ch4_complete"), true);
        DialogueManager.play(player, "undercat_ch4_complete");
        syncHub(player);
    }

    public static void chooseEnding(ServerPlayer player, int ending) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() != 5 || bond.getUndercatEnding() > 0) return;
        completeCommission(player, UndercatCommission.STARLIGHT_OATH);
        completeCommission(player, UndercatCommission.TWIN_PACT);
        completeCommission(player, UndercatCommission.FINALE_PREP);
        bond.setUndercatEnding(ending);
        switch (ending) {
            case 1 -> {
                bond.modifyCocoEmotion(15f);
                bond.modifyJennaEmotion(15f);
                bond.modifySisterBond(30f);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.ending_coco"), true);
            }
            case 2 -> {
                bond.modifyCocoEmotion(10f);
                bond.modifyJennaEmotion(10f);
                bond.modifySisterBond(20f);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.ending_jenna"), true);
            }
            case 3 -> {
                bond.modifyCocoEmotion(8f);
                bond.modifyJennaEmotion(8f);
                bond.modifySisterBond(25f);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.ending_symbiosis"), true);
            }
            case 4 -> {
                bond.modifyCocoEmotion(-5f);
                bond.modifyJennaEmotion(-5f);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.ending_sacrifice"), true);
            }
            case 5 -> {
                bond.modifyCocoEmotion(5f);
                bond.modifyJennaEmotion(5f);
                bond.addShadowCoins(100);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.ending_secret"), true);
            }
            default -> {
                bond.modifyCocoEmotion(10f);
                bond.modifyJennaEmotion(10f);
                player.displayClientMessage(Component.translatable("undercat.cocojenna.ending_reconcile"), true);
            }
        }
        giveOrDrop(player, new ItemStack(ModItems.TWIN_STAR_PENDANT.get()));
        for (UndercatRegion region : UndercatRegion.values()) {
            unlockRegion(bond, region);
        }
        DialogueManager.play(player, "undercat_finale");
        syncHub(player);
    }

    public static void donateShadowCoins(ServerPlayer player, UndercatFaction faction, int amount) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (amount <= 0 || bond.getShadowCoins() < amount) return;
        bond.addShadowCoins(-amount);
        bond.addUndercatRep(faction, amount / 5);
        if (faction == UndercatFaction.CARDBOARD_KINGDOM) {
            UndercatSideStoryManager.addCorrugataAffinity(player, amount / 10);
        }
        player.displayClientMessage(Component.translatable("undercat.cocojenna.donated", amount), true);
        syncHub(player);
    }

    public static void openWaystoneMenu(ServerPlayer player, UndercatRegion current) {
        openHub(player);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.waystone",
                Component.translatable("undercat.cocojenna.region." + current.name().toLowerCase())), true);
    }

    public static void teleportToRegion(ServerPlayer player, UndercatRegion region) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!isRegionUnlocked(bond, region)) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.region_locked"), true);
            return;
        }
        ServerLevel dest = player.server.getLevel(ModDimensions.UNDERCAT_DOMAIN);
        if (dest == null) return;
        BlockPos spawn = region.center;
        player.teleportTo(dest, spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        player.displayClientMessage(Component.translatable("undercat.cocojenna.teleport",
                Component.translatable("undercat.cocojenna.region." + region.name().toLowerCase())), true);
    }

    public static void teleportToUndercat(ServerPlayer player) {
        teleportToRegion(player, UndercatRegion.CARDBOARD_SLUMS);
        BondData bond = bond(player);
        bond.setUndercatStage(Math.max(1, bond.getUndercatStage()));
        player.displayClientMessage(Component.translatable("undercat.cocojenna.entered"), true);
    }

    public static void openHub(ServerPlayer player) {
        com.cocojenna.network.ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new com.cocojenna.network.OpenUndercatHubPacket(buildHubState(player).toString()));
    }

    private static void syncHub(ServerPlayer player) {
        openHub(player);
    }

    public static JsonObject buildHubState(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        JsonObject o = new JsonObject();
        o.addProperty("type", "undercat");
        o.addProperty("chapter", bond.getUndercatChapter());
        o.addProperty("stage", bond.getUndercatStage());
        o.addProperty("shadowCoins", bond.getShadowCoins());
        o.addProperty("commissions", bond.getUndercatCommissions());
        o.addProperty("commissionCount", UndercatCommission.countCompleted(bond.getUndercatCommissions()));
        o.addProperty("trials", bond.getUndercatTrials());
        o.addProperty("leechKills", bond.getUndercatLeechKills());
        o.addProperty("gladiators", bond.getUndercatGladiators());
        o.addProperty("ending", bond.getUndercatEnding());
        o.addProperty("regions", bond.getUndercatRegions());
        o.addProperty("corrugataAffinity", bond.getCorrugataAffinity());
        o.addProperty("oneEyeAffinity", bond.getOneEyeAffinity());
        o.addProperty("dailyDone", bond.isUndercatDailyDone());
        o.addProperty("dailyQuest", bond.getUndercatDailyQuest());
        o.addProperty("arenaBet", bond.getArenaBetAmount());
        o.addProperty("guardianDiscoveries", bond.getGuardianDiscoveries());
        JsonArray factions = new JsonArray();
        for (UndercatFaction f : UndercatFaction.values()) {
            JsonObject fo = new JsonObject();
            fo.addProperty("id", f.name());
            fo.addProperty("rep", bond.getUndercatRep(f));
            fo.addProperty("title", repTitle(bond.getUndercatRep(f)));
            factions.add(fo);
        }
        o.add("factions", factions);
        JsonArray regionList = new JsonArray();
        for (UndercatRegion region : UndercatRegion.values()) {
            JsonObject ro = new JsonObject();
            ro.addProperty("id", region.name());
            ro.addProperty("unlocked", isRegionUnlocked(bond, region));
            regionList.add(ro);
        }
        o.add("regionList", regionList);
        return o;
    }

    private static String repTitle(int rep) {
        if (rep <= -51) return "enemy";
        if (rep < 0) return "distrusted";
        if (rep < 20) return "stranger";
        if (rep < 50) return "friend";
        if (rep < 80) return "ally";
        return "legend";
    }

    public static void unlockRegionProgress(BondData bond, UndercatRegion region) {
        unlockRegion(bond, region);
    }

    private static void unlockRegion(BondData bond, UndercatRegion region) {
        bond.setUndercatRegions(bond.getUndercatRegions() | (1 << region.ordinal()));
    }

    private static boolean isRegionUnlocked(BondData bond, UndercatRegion region) {
        return (bond.getUndercatRegions() & (1 << region.ordinal())) != 0;
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }

    private static BondData bond(ServerPlayer player) {
        return ModCapabilities.getOrDefault(player);
    }
}
