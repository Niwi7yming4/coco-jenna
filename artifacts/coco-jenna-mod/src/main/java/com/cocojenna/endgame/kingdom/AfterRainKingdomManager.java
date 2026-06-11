package com.cocojenna.endgame.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenKingdomHubPacket;
import com.cocojenna.network.WebUiStatePacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** 雨後王國深化：職階招募、MPS、節慶、NPC 故事. */
public final class AfterRainKingdomManager {

    private AfterRainKingdomManager() {}

    public static void openHub(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) {
            player.displayClientMessage(Component.translatable("afterrain.cocojenna.not_ready"), true);
            return;
        }
        autoRecruitEligible(player);
        com.cocojenna.society.CatFamilyManager.ensureFamilies(player);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenKingdomHubPacket(buildHubState(player).toString()));
    }

    public static void syncHub(ServerPlayer player) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new WebUiStatePacket("kingdom", buildHubState(player).toString()));
    }

    public static JsonObject buildHubState(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        JsonObject root = new JsonObject();
        root.addProperty("type", "kingdom");
        root.addProperty("prosperity", bond.getKingdomProsperity());
        root.addProperty("happiness", bond.getKingdomHappiness());
        root.addProperty("reputation", bond.getKingdomReputation());
        root.addProperty("mpsDay", bond.getMpsDayIndex());
        root.addProperty("festivalDay", bond.getFestivalPrepDay());
        root.addProperty("festivalProgress", bond.getFestivalPrepProgress());
        root.addProperty("festivalPhase", bond.getFestivalPhase());
        root.addProperty("festivalContestScore", bond.getFestivalContestScore());
        root.addProperty("festivalContestSubmitted", bond.isFestivalContestSubmitted());
        root.addProperty("festivalSetupHelped", bond.isFestivalSetupHelped());
        root.addProperty("festivalDanceDone", bond.isFestivalDanceDone());
        root.addProperty("festivalDanceScore", bond.getFestivalDanceScore());
        root.addProperty("festivalWish", bond.getFestivalWish());
        root.addProperty("pendingFestivalWish", bond.getPendingFestivalWish());
        root.addProperty("kingdomSeason", bond.getKingdomSeason());
        root.addProperty("npcFatigue", bond.getNpcFatigue());
        root.addProperty("libraryShelf", bond.getLibraryShelfPages().size());
        root.addProperty("libraryCurator", bond.isLibraryCurator());
        root.addProperty("gameTime", player.level().getDayTime() % 24000L);

        JsonArray jobs = new JsonArray();
        for (TownJobRank rank : TownJobRank.values()) {
            JsonObject j = new JsonObject();
            j.addProperty("id", rank.name());
            j.addProperty("zh", rank.zh);
            j.addProperty("cap", rank.cap);
            j.addProperty("assigned", countJob(bond, rank));
            j.addProperty("unlocked", rank.meetsRequirements(bond));
            jobs.add(j);
        }
        root.add("jobs", jobs);

        JsonArray npcs = new JsonArray();
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            JsonObject n = new JsonObject();
            n.addProperty("id", p.id());
            n.addProperty("name", p.nameZh());
            n.addProperty("favor", bond.getTownNpcFavor(p.id()));
            n.addProperty("job", bond.getTownNpcJob(p.id()));
            n.addProperty("story", bond.getTownNpcStoryChapter(p.id()));
            n.addProperty("recruited", bond.isTownNpcRecruited(p.id()) || isAutoRecruited(bond, p));
            n.addProperty("gift", p.preferredGift().getDescriptionId());
            npcs.add(n);
        }
        root.add("npcs", npcs);

        JsonArray schedule = new JsonArray();
        for (int d = 0; d < 7; d++) {
            JsonArray day = new JsonArray();
            for (int b = 0; b < 4; b++) {
                day.add(bond.getMpsCell(d, b));
            }
            schedule.add(day);
        }
        root.add("schedule", schedule);

        JsonArray tasks = new JsonArray();
        for (MpsTask t : MpsTask.values()) {
            if (t == MpsTask.REST) continue;
            JsonObject o = new JsonObject();
            o.addProperty("id", t.id);
            o.addProperty("yield", t.baseYield);
            tasks.add(o);
        }
        root.add("tasks", tasks);

        if (player.level().dimension().equals(com.cocojenna.init.ModDimensions.CAT_KINGDOM)) {
            JsonObject cabinet = new JsonObject();
            var auth = com.cocojenna.kingdom.multiplayer.KingdomAuthoritySavedData.get(player.serverLevel());
            if (auth.getMonarch() != null) {
                cabinet.addProperty("monarch", auth.getMonarch().toString());
            }
            cabinet.addProperty("myRole", auth.getRole(player.getUUID()).name());
            root.add("cabinet", cabinet);
        }
        return root;
    }

    public static void handleAction(ServerPlayer player, String action, JsonObject msg) {
        switch (action) {
            case "assign_job" -> assignJob(player, msg.get("npc").getAsString(), msg.get("job").getAsString());
            case "recruit_npc" -> recruitNpc(player, msg.get("npc").getAsString());
            case "gift_npc" -> giftNpc(player, msg.get("npc").getAsString());
            case "read_story" -> readStory(player, msg.get("npc").getAsString());
            case "set_mps" -> setMps(player, msg.get("day").getAsInt(), msg.get("block").getAsInt(),
                    msg.get("task").getAsString());
            case "apply_preset" -> applyFestivalPreset(player);
            case "run_mps_day" -> runMpsDay(player);
            case "start_festival" -> tryStartFestival(player);
            case "festival_help_setup" -> FestivalEventManager.onHelpSetup(player);
            case "festival_dance" -> FestivalEventManager.onDance(player);
            case "festival_wish" -> FestivalEventManager.onSubmitWish(player,
                    msg.has("wish") ? msg.get("wish").getAsString() : "peace");
            case "shelve_book" -> CatLibraryManager.shelvePage(player, msg.get("index").getAsInt());
            case "read_shelf" -> CatLibraryManager.readShelf(player, msg.get("index").getAsInt());
            case "show_family" -> com.cocojenna.society.CatFamilyManager.showFamilyTree(player);
            case "propose_npc" -> com.cocojenna.society.CatMarriageManager.tryPropose(
                    player, msg.get("npc").getAsString());
            case "team_bond" -> com.cocojenna.kingdom.multiplayer.TeamBondUltimateManager.tryActivate(player);
            case "kitten_bless" -> com.cocojenna.kingdom.multiplayer.KingdomKittenManager.tryBless(player);
            case "mercenary_set" -> com.cocojenna.kingdom.multiplayer.MercenaryManager.setProfile(
                    player, msg.get("price").getAsInt());
            default -> { }
        }
    }

    public static void tickDaily(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        SeasonalFestivalManager.tickDaily(player);
        CatLibraryManager.tickScholarResearch(player);
        BondData bond = ModCapabilities.getOrDefault(player);
        int phase = bond.getFestivalPhase();
        if (phase > FestivalEventManager.PHASE_IDLE && phase < FestivalEventManager.PHASE_ENDED) {
            return;
        }
        if (bond.getFestivalPrepDay() > 0) {
            runMpsDay(player);
            bond.setFestivalPrepDay(bond.getFestivalPrepDay() - 1);
            if (bond.getFestivalPrepDay() <= 0 || bond.getFestivalPrepProgress() >= 100) {
                tryStartFestival(player);
            }
        }
    }

    private static void assignJob(ServerPlayer player, String npcId, String jobName) {
        BondData bond = ModCapabilities.getOrDefault(player);
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile == null || !bond.isTownNpcRecruited(npcId) && !isAutoRecruited(bond, profile)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.not_recruited"), true);
            return;
        }
        TownJobRank job;
        try {
            job = TownJobRank.valueOf(jobName);
        } catch (IllegalArgumentException e) {
            return;
        }
        if (!job.meetsRequirements(bond)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.job_locked"), true);
            return;
        }
        if (countJob(bond, job) >= job.cap) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.job_full"), true);
            return;
        }
        bond.setTownNpcJob(npcId, job.name());
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.job_assigned",
                profile.nameZh(), job.displayName()), true);
    }

    private static void recruitNpc(ServerPlayer player, String npcId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile == null) return;
        if (bond.isTownNpcRecruited(npcId)) return;
        if (bond.getKingdomHappiness() < 40 && profile.recruitFavor() > 0) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.recruit_need_happiness"), true);
            return;
        }
        if (profile.recruitFavor() > 0 && bond.getTownNpcFavor(npcId) < profile.recruitFavor()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.recruit_need_favor",
                    profile.recruitFavor()), true);
            return;
        }
        bond.recruitTownNpc(npcId);
        if (bond.getTownNpcJob(npcId).isEmpty()) {
            bond.setTownNpcJob(npcId, profile.defaultJob().name());
        }
        bond.addKingdomHappiness(2);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.recruited", profile.nameZh()), true);
    }

    private static void giftNpc(ServerPlayer player, String npcId) {
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile == null) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!consumeGift(player, profile.preferredGift())) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.gift_wrong"), true);
            return;
        }
        bond.addTownNpcFavor(npcId, 8);
        bond.addKingdomHappiness(1);
        com.cocojenna.society.CatSocietyManager.onTownNpcGift(player, npcId, new ItemStack(profile.preferredGift()));
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.gift_ok", profile.nameZh()), true);
    }

    private static void readStory(ServerPlayer player, String npcId) {
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        if (profile == null) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        int next = bond.getTownNpcStoryChapter(npcId) + 1;
        if (next > profile.storyScenes().length) return;
        int need = profile.favorForChapter(next);
        if (bond.getTownNpcFavor(npcId) < need) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.story_need_favor", need), true);
            return;
        }
        if (!com.cocojenna.quest.SanhuaEternalCloakManager.canAdvanceStory(player, npcId, next)) {
            return;
        }
        bond.setTownNpcStoryChapter(npcId, next);
        DialogueManager.play(player, profile.storyScenes()[next - 1]);
        com.cocojenna.quest.SanhuaEternalCloakManager.onStoryChapterComplete(player, npcId, next);
    }

    private static void setMps(ServerPlayer player, int day, int block, String task) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (day < 0 || day > 6 || block < 0 || block > 3) return;
        bond.setMpsCell(day, block, task);
    }

    private static void applyFestivalPreset(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        String[][] preset = com.cocojenna.society.ProfessionBuildingBinder.generateMpsPreset(bond);
        for (int d = 0; d < 7; d++) {
            for (int b = 0; b < 4; b++) {
                bond.setMpsCell(d, b, preset[d][b]);
            }
        }
        bond.setFestivalPrepDay(7);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.mps_preset"), true);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.mps_profession_bound"), true);
    }

    private static void runMpsDay(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int day = bond.getMpsDayIndex();
        int yield = 0;
        for (int b = 0; b < 4; b++) {
            MpsTask task = MpsTask.byId(bond.getMpsCell(day, b));
            MpsTimeBlock block = MpsTimeBlock.of(b);
            float mult = task.gather ? block.gatherMult : block.craftMult;
            yield += Math.round(task.baseYield * mult);
        }
        yield = Math.round(yield * com.cocojenna.endgame.KingdomDecreeWorldEffects.mpsYieldMultiplier(bond));
        bond.addFestivalPrepProgress(Math.max(1, yield / 4));
        bond.addKingdomProsperity(yield / 8);
        MpsProductionManager.applyDayProduction(player, bond);
        bond.setNpcFatigue(Math.max(0, bond.getNpcFatigue() - 12));
        bond.setMpsDayIndex((day + 1) % 7);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.mps_day_done", yield), true);
    }

    private static void tryStartFestival(ServerPlayer player) {
        if (!com.cocojenna.kingdom.multiplayer.KingdomPermissionGuard.check(
                player, com.cocojenna.kingdom.multiplayer.Permission.START_FESTIVAL)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        int phase = bond.getFestivalPhase();
        if (phase > FestivalEventManager.PHASE_IDLE && phase < FestivalEventManager.PHASE_ENDED) return;
        if (!bond.isBuildingPlaced("festival_stage") && bond.getBuildingProgress("festival_stage") < 50) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival_need_stage"), true);
            return;
        }
        if (bond.getFestivalPrepProgress() < 60 && bond.getFestivalPrepDay() > 0) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival_not_ready"), true);
            return;
        }
        bond.addKingdomHappiness(10);
        bond.addKingdomReputation(5);
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (bond.isTownNpcRecruited(p.id()) || isAutoRecruited(bond, p)) {
                bond.addTownNpcFavor(p.id(), 10);
            }
        }
        FestivalEventManager.beginFestival(player);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival_started"), true);
    }

    private static void autoRecruitEligible(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isMetIronpaw()) bond.recruitTownNpc("ironpaw");
        if (bond.hasPeaceScene("afterrain_velvet")) bond.recruitTownNpc("sanhua");
        if (bond.isMetBlindMerchant()) bond.recruitTownNpc("cheshire");
        if (bond.hasPeaceScene("afterrain_blind_port")) bond.recruitTownNpc("white_glove");
        if (bond.isEndgameUnlocked()) bond.recruitTownNpc("alpha");
        if (bond.getFirstCryQuestStage() >= com.cocojenna.quest.FirstCryQuestManager.STAGE_DUEL_DONE) {
            bond.recruitTownNpc("samurai");
        }
        if (bond.isBuildingPlaced("cat_library") || bond.hasPeaceScene("afterrain_gear_town")) {
            bond.recruitTownNpc("monk");
        }
        if (bond.isBuildingPlaced("open_air_theater") || bond.getKingdomHappiness() >= 75) {
            bond.recruitTownNpc("court_lady");
        }
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

    private static int countJob(BondData bond, TownJobRank rank) {
        int n = 0;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (rank.name().equals(bond.getTownNpcJob(p.id()))) n++;
        }
        return n;
    }

    private static boolean consumeGift(ServerPlayer player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

}
