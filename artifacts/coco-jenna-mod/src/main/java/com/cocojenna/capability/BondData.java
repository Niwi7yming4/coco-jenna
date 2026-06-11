package com.cocojenna.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 儲存玩家與可可、珍奶的所有關係數值。
 *
 * <p>三軌成長系統：
 * <ol>
 *   <li>情感線 (Emotion) — 對玩家的信賴，0‑100</li>
 *   <li>獨立性 (Independence) — 自主行動傾向，0‑100</li>
 *   <li>覺醒線 (Awakening) — 貓之國血脈記憶，0‑50 (對應碎片數)</li>
 * </ol>
 *
 * <p>額外數值：
 * <ul>
 *   <li>保護慾 (Protectiveness) — 可可：0‑100</li>
 *   <li>月亮親和 (Moon Affinity) — 可可：0‑100</li>
 *   <li>玩心 (Playfulness) — 珍奶：0‑100</li>
 *   <li>好奇心 (Curiosity) — 珍奶：0‑100</li>
 *   <li>滿足感 (Contentment) — 珍奶：0‑100</li>
 *   <li>姊妹羈絆 (Sister Bond) — 兩貓共用：0‑100</li>
 * </ul>
 *
 * <p>終局數值（終局事件「初晴」觸發後轉化）：
 * <ul>
 *   <li>依戀值 (Attachment) — 可可：取代保護慾</li>
 *   <li>日照享受 (Sunbathing) — 可可：取代月亮親和</li>
 * </ul>
 */
public class BondData {

    private final CatBondCapability catBondSection = new CatBondCapability();
    private final KingdomMultiplayerCapability multiplayerSection = new KingdomMultiplayerCapability();
    private final SequenceCapability sequenceSection = new SequenceCapability();

    // ── 進度旗標 ─────────────────────────────────────────────────────────
    private boolean endgameUnlocked = false;
    private int firstCryQuestStage = 0;
    /** 第六章到達引導（0=可可凝視 … 6=完成）. */
    private int arrivalTutorialStage = 0;
    private int onboardingWoodCollected = 0;
    private int onboardingStoneCollected = 0;
    /** 主世界滲透主線（0=月光腳印 … 5=完成）. */
    private int penetrationQuestStage = 0;
    private String activeWeaponMemoryTaskId = "";
    private int weaponMemoryTaskProgress = 0;
    private String shadowClawEnding = "";
    private int grayWhiskerFavor = 0;
    private int overworldInfluence = 0;
    private int catKingdomInfluence = 0;
    private int moonPawTrailCount = 0;
    private int catLanguageLevel = 0;
    private int catGraffitiRead = 0;
    private boolean grayWhiskerMet = false;
    private boolean caravanEscortActive = false;
    private int caravanStartX = 0;
    private int caravanStartZ = 0;
    private int caravanDestX = 0;
    private int caravanDestZ = 0;
    private long lastBlackMudLeakTick = 0;
    private int ruinMapFragmentBits = 0;
    private int overworldDungeonStage = 0;
    private int fusionBuildingsPlaced = 0;
    private int moonResonanceCount = 0;
    private int catSocietyInteractions = 0;
    private int catSocietyPeakFavor = 0;
    private int catnipTradedTotal = 0;
    private long lastEmbassyTeleportTick = 0L;
    private long lastThroneBlessingTick = 0L;
    private long moonCoreBlessingUntil = 0L;
    private int overworldRuinCorrosion = 0;
    private long lastTheaterPerformanceTick = 0L;
    private long lastTheaterGatheringDay = -1L;
    private long lastFamilyGatheringDay = -1L;
    private boolean catFamiliesSeeded = false;
    private final Map<String, Integer> overworldNpcFavor = new HashMap<>();
    private final Set<String> overworldSoulCompanions = new HashSet<>();
    private final Map<String, Integer> townNpcRomanceStage = new HashMap<>();
    private final Map<String, String> townNpcFamily = new HashMap<>();
    private final Map<String, String> townNpcFamilyRole = new HashMap<>();
    private String marriagePartnerNpcId = "";
    private long engagementDay = -1L;
    private long weddingScheduledDay = -1L;
    private long pregnancyDueDay = -1L;
    private int kittenCount = 0;
    private final Set<Long> revealedHiddenPos = new HashSet<>();
    private int forceQuestStage = 0;
    private int forceTrialsMask = 0;
    private int forceResetCount = 0;
    private String activeTrialForce = "";

    // ── 秦可沐 / 皇陵 ────────────────────────────────────────────────────
    private int qinKemuFavor = 0;
    private long qinWeaponAwakenCooldownUntil = 0L;
    private int qinKemuQuestStage = 0;
    /** Bitmask for MausoleumType bits 0–5. */
    private long discoveredMausoleums = 0L;

    // ── 記憶之書設定（第十章）────────────────────────────────────────────
    private int followDistance = 0;
    private boolean allowAffection = true;
    private boolean allowExplore = true;
    private boolean allowCombat = true;
    private boolean muteMode = false;
    private boolean metIronpaw = false;
    private boolean metBlindMerchant = false;
    private boolean guardian = false;
    private int repGearTown = 0;
    private int repRoyal = 0;
    private int repDawn = 0;
    private int repBlindPort = 0;
    private int repFirstCry = 0;
    private final Set<String> purchasedRepOffers = new HashSet<>();
    private long lastOnlineTick = 0L;
    private boolean showSkillCooldown = true;
    private int preferredSkillSlot = 0;
    private int activeSkillPreset = 0;
    private final int[] skillPresetSlots = new int[]{0, 0, 0};
    private int kingdomProsperity = 0;
    private int kingdomHappiness = 50;
    private int kingdomStability = 50;
    private int kingdomReputation = 10;
    private int buildCreativity = 0;
    private int ironpawForgeLevel = 1;
    private String kingdomDecree = "peace";
    private final List<ActiveDecree> activeDecrees = new ArrayList<>();
    private final List<PictureBookPage> pictureBookPages = new ArrayList<>();
    private final Map<String, Integer> buildingProgress = new HashMap<>();
    private final java.util.Set<String> buildingsPlaced = new java.util.HashSet<>();
    private final Set<String> peaceScenesSeen = new HashSet<>();
    // ── 雨後王國：職階／MPS／節慶／NPC 故事 ───────────────────────────────
    private final Map<String, Integer> townNpcFavor = new HashMap<>();
    private final Map<String, String> townNpcJob = new HashMap<>();
    private final Map<String, Integer> townNpcStory = new HashMap<>();
    private final Set<String> townNpcRecruited = new HashSet<>();
    private final Map<String, Integer> townNpcDreamStage = new HashMap<>();
    private final Map<Long, Long> kingdomMicroCooldown = new HashMap<>();
    private int kingdomStrayCatsReturned = 0;
    private long lastStardustWishDay = -1;
    private final java.util.Set<String> familyLifeEvents = new java.util.HashSet<>();
    private String lastFamilyEvent = "";
    private long lastVillageFestivalDay = -1;
    private String activeVillageFestival = "";
    private int mpsDayIndex = 0;
    private final String[] mpsSchedule = new String[28];
    private int festivalPrepDay = 7;
    private int festivalPrepProgress = 0;
    private int festivalPhase = 0;
    private long festivalStartTick = 0;
    private int festivalContestScore = 0;
    private boolean festivalContestSubmitted = false;
    private boolean festivalSetupHelped = false;
    private boolean festivalDanceDone = false;
    private int festivalDanceScore = 0;
    private String festivalWish = "";
    private String pendingFestivalWish = "";
    // ── 三軌成長／日曆／圖書館／法令副作用 ─────────────────────────────
    private long growthTickDay = -1;
    private float dailyEmotionGain = 0f;
    private long lastPetCocoDay = 0;
    private long lastPetJennaDay = 0;
    private long kingdomCalendarDay = -1;
    private int kingdomSeason = 0;
    private String lastSeasonalFestival = "";
    private int npcFatigue = 0;
    private int warehouseBonus = 0;
    private final List<PictureBookPage> libraryShelfPages = new ArrayList<>();
    private boolean libraryCurator = false;
    private long twinBlessingLastEnact = 0;
    // ── 地下貓域 DLC「深淵與星光」────────────────────────────────────────
    private int shadowCoins = 0;
    private int undercatChapter = 0;
    private int undercatStage = 0;
    private int undercatCommissions = 0;
    private int undercatTrials = 0;
    private int undercatGladiators = 0;
    private int undercatEnding = 0;
    private int undercatRegions = 0;
    private int undercatLeechKills = 0;
    private int undercatEntranceSeen = 0;
    private int corrugataAffinity = 0;
    private int oneEyeAffinity = 0;
    private int undercatSideFlags = 0;
    private long undercatDailyDay = -1;
    private int undercatDailyQuest = -1;
    private boolean undercatDailyDone = false;
    private int arenaBetAmount = 0;
    private int catnipPlantStreak = 0;
    private long pawStampCocoDay = -1;
    private long pawStampJennaDay = -1;
    private int guardianDiscoveries = 0;
    /** 傳說條目發現 bitmask（設計書 2.2）. */
    private long loreDiscovered = 0L;
    /** 怪貓貓圖鑑 bitmask（設計書 4.3）. */
    private int wildCatsDiscovered = 0;
    /** 區域傳說集齊 bitmask. */
    private int loreRegionsComplete = 0;
    /** 地牢通關 bitmask（設計書 3.2）. */
    private int dungeonsCleared = 0;
    private final List<String> explorationJournal = new ArrayList<>();
    private int remnantBurned = 0;
    private boolean fallenVelvetRedeemed = false;
    private boolean sanhuaEternalMaterials = false;
    private final Set<String> unlockedCookingRecipes = new HashSet<>();
    private final Set<String> nightSecretsSeen = new HashSet<>();
    private long systemScanUntil = 0;
    private long lastNightSecretDay = -1;
    private long lastTapePrayerDay = -1;
    private final Map<String, Integer> undercatRep = new HashMap<>();
    // ── 守護者指南／村莊養成 ─────────────────────────────────────────────
    private boolean receivedGuardianGuide = false;
    private long catKingdomEnterDay = 0;
    private long lastCozyEventDay = -1;
    private int cozyEventsToday = 0;
    private long groomCooldownUntil = 0;
    private long napCooldownUntil = 0;
    private int villagePopulation = 2;
    private int villageHousingCapacity = 6;
    private int villageFoodStock = 40;
    private int villageDefense = 20;
    private long lastVillageTickDay = -1;
    // ── 劍骨系統（設計書 劍骨／武器進化）────────────────────────────────
    private boolean swordBoneAwakened = false;
    private boolean swordBoneSupreme = false;
    private final List<com.cocojenna.swordbone.SwordBoneEntry> swordBones = new ArrayList<>();
    private final Set<String> collectedRyokatana = new HashSet<>();
    private final Set<String> unlockedWeaponMemories = new HashSet<>();
    private long swordBoneDeathSaveCd = 0;
    private long swordBoneResonanceCd = 0;
    private long swordBoneResonanceUntil = 0;
    // ── 防具主動機制 ─────────────────────────────────────────────────────
    private boolean armorAwakened = false;
    private long armorAwakenCd = 0;
    private int armorShieldCharge = 0;
    private long armorShieldUntil = 0;
    private int armorMorphForm = 0;

    public record ActiveDecree(String id, long expiresAt) {}
    public record PictureBookPage(String background, String caption, String sticker, String filter) {}

    // ── 計時器（遊戲刻） ─────────────────────────────────────────────────
    private long lastInteractCoco = 0L;
    private long lastInteractJenna = 0L;
    private long lastFeedCoco = 0L;
    private long lastFeedJenna = 0L;

    // ─────────────────────────────────────────────────────────────────────
    // 情感等級計算
    // ─────────────────────────────────────────────────────────────────────

    public enum EmotionLevel {
        STRANGER(0, 9),
        CURIOUS(10, 24),
        ATTACHED(25, 44),
        BONDED(45, 69),
        DEVOTED(70, 89),
        TRANSCENDENT(90, 100);

        public final int min, max;
        EmotionLevel(int min, int max) { this.min = min; this.max = max; }

        public static EmotionLevel of(float value) {
            for (EmotionLevel l : values())
                if (value >= l.min && value <= l.max) return l;
            return STRANGER;
        }
    }

    public enum IndependenceLevel {
        ALWAYS_FOLLOWS(0, 19, 5),
        OCCASIONAL(20, 39, 16),
        AUTONOMOUS(40, 59, 32),
        INDEPENDENT(60, 79, 64),
        TEMPORARY_AWAY(80, 94, 128),
        FREE_ROAM(95, 100, Integer.MAX_VALUE);

        public final int min, max, radius;
        IndependenceLevel(int min, int max, int radius) {
            this.min = min; this.max = max; this.radius = radius;
        }
    }

    public EmotionLevel getCocoEmotionLevel() { return EmotionLevel.of(getCocoEmotion()); }
    public EmotionLevel getJennaEmotionLevel() { return EmotionLevel.of(getJennaEmotion()); }

    // ─────────────────────────────────────────────────────────────────────
    // 修改方法（帶限幅與冷卻檢查）
    // ─────────────────────────────────────────────────────────────────────

    public void modifyCocoEmotion(float delta) {
        setCocoEmotion(getCocoEmotion() + delta);
    }

    public void modifyJennaEmotion(float delta) {
        setJennaEmotion(getJennaEmotion() + delta);
    }

    public void modifySisterBond(float delta) {
        setSisterBond(getSisterBond() + delta);
    }

    public void modifyCocoProtectiveness(float delta) {
        setCocoProtectiveness(getCocoProtectiveness() + delta);
    }

    public void modifyJennaPlayfulness(float delta) {
        setJennaPlayfulness(getJennaPlayfulness() + delta);
    }

    public void modifyJennaContentment(float delta) {
        setJennaContentment(getJennaContentment() + delta);
    }

    public void modifyCocoIndependence(float delta) {
        setCocoIndependence(getCocoIndependence() + delta);
    }

    public void modifyJennaIndependence(float delta) {
        setJennaIndependence(getJennaIndependence() + delta);
    }

    public void modifyCocoEmotion(float delta, boolean temp) { modifyCocoEmotion(delta); }

    public void addMemoryShard(int amount) {
        setMemoryShardsTotal(getMemoryShardsTotal() + amount);
        setCocoAwakening(Math.min(50, getMemoryShardsTotal()));
        setJennaAwakening(Math.min(50, getMemoryShardsTotal()));
    }

    /** 伺服器端：碎片增加後同步紀念碑（由 ModEventHandler 呼叫）. */
    public void notifyShardGrowth(net.minecraft.server.level.ServerPlayer player) {
        if (player != null) {
            com.cocojenna.world.MonumentGrowthManager.onShardsUpdated(
                    player.serverLevel(), getMemoryShardsTotal());
            com.cocojenna.integration.FallenAbyssLinkage.trySpawnMirror(
                    player.serverLevel(), this, player);
        }
    }

    private static int sequenceBit(String id) {
        return switch (id) {
            case "a" -> 0;
            case "b" -> 1;
            case "c" -> 2;
            case "d" -> 3;
            case "e" -> 4;
            case "f" -> 5;
            case "g" -> 6;
            default -> -1;
        };
    }

    public boolean hasSequence(String id) {
        int bit = sequenceBit(id);
        long mask = getUnlockedSequencesRaw();
        return bit >= 0 && (mask & (1L << bit)) != 0;
    }

    public boolean unlockSequence(String id) {
        int bit = sequenceBit(id);
        if (bit < 0 || hasSequence(id)) {
            return false;
        }
        setUnlockedSequencesRaw(getUnlockedSequencesRaw() | (1L << bit));
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────
    // NBT 序列化
    // ─────────────────────────────────────────────────────────────────────

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("cocoEmotion", getCocoEmotion());
        tag.putFloat("cocoIndependence", getCocoIndependence());
        tag.putInt("cocoAwakening", getCocoAwakening());
        tag.putFloat("cocoProtectiveness", getCocoProtectiveness());
        tag.putFloat("cocoMoonAffinity", getCocoMoonAffinity());
        tag.putFloat("cocoAttachment", getCocoAttachment());
        tag.putFloat("cocoSunbathing", getCocoSunbathing());
        tag.putFloat("jennaEmotion", getJennaEmotion());
        tag.putFloat("jennaIndependence", getJennaIndependence());
        tag.putInt("jennaAwakening", getJennaAwakening());
        tag.putFloat("jennaPlayfulness", getJennaPlayfulness());
        tag.putFloat("jennaCuriosity", getJennaCuriosity());
        tag.putFloat("jennaContentment", getJennaContentment());
        tag.putFloat("sisterBond", getSisterBond());
        tag.putBoolean("endgameUnlocked", endgameUnlocked);
        tag.putInt("memoryShardsTotal", getMemoryShardsTotal());
        tag.putLong("lastInteractCoco", lastInteractCoco);
        tag.putLong("lastInteractJenna", lastInteractJenna);
        tag.putLong("lastFeedCoco", lastFeedCoco);
        tag.putLong("lastFeedJenna", lastFeedJenna);
        tag.putLong("unlockedSequences", getUnlockedSequencesRaw());
        tag.putInt("firstCryQuestStage", firstCryQuestStage);
        tag.putInt("arrivalTutorialStage", arrivalTutorialStage);
        tag.putInt("onboardingQuestStep", getOnboardingQuestStep());
        tag.putInt("onboardingWoodCollected", onboardingWoodCollected);
        tag.putInt("onboardingStoneCollected", onboardingStoneCollected);
        tag.putInt("penetrationQuestStage", penetrationQuestStage);
        tag.putString("activeWeaponMemoryTaskId", activeWeaponMemoryTaskId);
        tag.putInt("weaponMemoryTaskProgress", weaponMemoryTaskProgress);
        tag.putString("shadowClawEnding", shadowClawEnding);
        tag.putInt("grayWhiskerFavor", grayWhiskerFavor);
        tag.putInt("overworldInfluence", overworldInfluence);
        tag.putInt("catKingdomInfluence", catKingdomInfluence);
        tag.putInt("moonPawTrailCount", moonPawTrailCount);
        tag.putInt("catLanguageLevel", catLanguageLevel);
        tag.putInt("catGraffitiRead", catGraffitiRead);
        tag.putBoolean("grayWhiskerMet", grayWhiskerMet);
        tag.putBoolean("caravanEscort", caravanEscortActive);
        tag.putInt("caravanStartX", caravanStartX);
        tag.putInt("caravanStartZ", caravanStartZ);
        tag.putInt("caravanDestX", caravanDestX);
        tag.putInt("caravanDestZ", caravanDestZ);
        tag.putLong("lastBlackMudLeakTick", lastBlackMudLeakTick);
        tag.putInt("ruinMapFragmentBits", ruinMapFragmentBits);
        tag.putInt("overworldDungeonStage", overworldDungeonStage);
        tag.putInt("fusionBuildingsPlaced", fusionBuildingsPlaced);
        tag.putInt("moonResonanceCount", moonResonanceCount);
        tag.putInt("catSocietyInteractions", catSocietyInteractions);
        tag.putInt("catSocietyPeakFavor", catSocietyPeakFavor);
        tag.putInt("catnipTradedTotal", catnipTradedTotal);
        tag.putLong("lastEmbassyTeleportTick", lastEmbassyTeleportTick);
        tag.putLong("lastThroneBlessingTick", lastThroneBlessingTick);
        tag.putLong("moonCoreBlessingUntil", moonCoreBlessingUntil);
        tag.putInt("overworldRuinCorrosion", overworldRuinCorrosion);
        tag.putLong("lastTheaterPerformanceTick", lastTheaterPerformanceTick);
        tag.putLong("lastTheaterGatheringDay", lastTheaterGatheringDay);
        tag.putLong("lastFamilyGatheringDay", lastFamilyGatheringDay);
        tag.putBoolean("catFamiliesSeeded", catFamiliesSeeded);
        ListTag owFav = new ListTag();
        for (var e : overworldNpcFavor.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("fav", e.getValue());
            owFav.add(c);
        }
        tag.put("overworldNpcFavor", owFav);
        ListTag soulComp = new ListTag();
        for (String id : overworldSoulCompanions) {
            soulComp.add(StringTag.valueOf(id));
        }
        tag.put("overworldSoulCompanions", soulComp);
        ListTag romance = new ListTag();
        for (var e : townNpcRomanceStage.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("st", e.getValue());
            romance.add(c);
        }
        tag.put("townNpcRomanceStage", romance);
        ListTag families = new ListTag();
        for (var e : townNpcFamily.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putString("fam", e.getValue());
            c.putString("role", townNpcFamilyRole.getOrDefault(e.getKey(), ""));
            families.add(c);
        }
        tag.put("townNpcFamily", families);
        tag.putString("marriagePartnerNpcId", marriagePartnerNpcId == null ? "" : marriagePartnerNpcId);
        tag.putLong("engagementDay", engagementDay);
        tag.putLong("weddingScheduledDay", weddingScheduledDay);
        tag.putLong("pregnancyDueDay", pregnancyDueDay);
        tag.putInt("kittenCount", kittenCount);
        ListTag revealed = new ListTag();
        for (long p : revealedHiddenPos) {
            revealed.add(net.minecraft.nbt.LongTag.valueOf(p));
        }
        tag.put("revealedHidden", revealed);
        tag.putString("felineForce", getFelineForce());
        tag.putInt("felineTier", getFelineTier());
        tag.putLong("felineSkillCooldownUntil", getFelineSkillCooldownUntil());
        tag.putInt("forceQuestStage", forceQuestStage);
        tag.putInt("forceTrialsMask", forceTrialsMask);
        tag.putInt("forceResetCount", forceResetCount);
        tag.putString("activeTrialForce", activeTrialForce);
        tag.putInt("followDistance", followDistance);
        tag.putBoolean("allowAffection", allowAffection);
        tag.putBoolean("allowExplore", allowExplore);
        tag.putBoolean("allowCombat", allowCombat);
        tag.putBoolean("muteMode", muteMode);
        tag.putBoolean("metIronpaw", metIronpaw);
        tag.putBoolean("metBlindMerchant", metBlindMerchant);
        tag.putBoolean("guardian", guardian);
        tag.putInt("repGearTown", repGearTown);
        tag.putInt("repRoyal", repRoyal);
        tag.putInt("repDawn", repDawn);
        tag.putInt("repBlindPort", repBlindPort);
        tag.putInt("repFirstCry", repFirstCry);
        ListTag repShop = new ListTag();
        for (String id : purchasedRepOffers) repShop.add(StringTag.valueOf(id));
        tag.put("purchasedRepOffers", repShop);
        tag.putInt("promotionCardCount", getPromotionCardCount());
        tag.putFloat("promotionCardBonus", getPromotionCardBonus());
        ListTag cards = new ListTag();
        for (String id : getOwnedPromotionCards()) cards.add(StringTag.valueOf(id));
        tag.put("ownedPromotionCards", cards);
        tag.putInt("pendingPromotionTier", getPendingPromotionTier());
        tag.putInt("ceremonyStage", getCeremonyStage());
        tag.putInt("ceremonyTimeout", getCeremonyTimeout());
        tag.putInt("markLevel", getMarkLevel());
        tag.putString("markForce", getMarkForce());
        tag.putBoolean("simplifiedCeremony", isSimplifiedCeremony());
        tag.putLong("hiddenSequences", getHiddenSequences());
        tag.putLong("lastOnlineTick", lastOnlineTick);
        tag.putBoolean("showSkillCooldown", showSkillCooldown);
        tag.putInt("preferredSkillSlot", preferredSkillSlot);
        tag.putInt("activeSkillPreset", activeSkillPreset);
        ListTag presetSlots = new ListTag();
        for (int slot : skillPresetSlots) presetSlots.add(net.minecraft.nbt.IntTag.valueOf(slot));
        tag.put("skillPresetSlots", presetSlots);
        tag.putInt("awakeningTrialTier", getAwakeningTrialTier());
        tag.putBoolean("awakeningTrialActive", isAwakeningTrialActive());
        tag.putInt("awakeningTrialIndex", getAwakeningTrialIndex());
        tag.putInt("awakeningTrialKills", getAwakeningTrialKills());
        tag.putInt("awakeningTrialGoal", getAwakeningTrialGoal());
        tag.putLong("awakeningTrialDeadline", getAwakeningTrialDeadline());
        tag.putInt("kingdomProsperity", kingdomProsperity);
        tag.putInt("kingdomHappiness", kingdomHappiness);
        tag.putInt("kingdomStability", kingdomStability);
        tag.putInt("kingdomReputation", kingdomReputation);
        tag.putInt("buildCreativity", buildCreativity);
        tag.putInt("ironpawForgeLevel", ironpawForgeLevel);
        tag.putString("kingdomDecree", kingdomDecree);
        ListTag decrees = new ListTag();
        for (ActiveDecree d : activeDecrees) {
            CompoundTag c = new CompoundTag();
            c.putString("id", d.id());
            c.putLong("expires", d.expiresAt());
            decrees.add(c);
        }
        tag.put("activeDecrees", decrees);
        ListTag pages = new ListTag();
        for (PictureBookPage p : pictureBookPages) {
            CompoundTag c = new CompoundTag();
            c.putString("bg", p.background());
            c.putString("cap", p.caption());
            c.putString("stk", p.sticker());
            c.putString("flt", p.filter());
            pages.add(c);
        }
        tag.put("pictureBookPages", pages);
        ListTag builds = new ListTag();
        for (var e : buildingProgress.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("prog", e.getValue());
            builds.add(c);
        }
        tag.put("buildingProgress", builds);
        ListTag placed = new ListTag();
        for (String id : buildingsPlaced) {
            placed.add(net.minecraft.nbt.StringTag.valueOf(id));
        }
        tag.put("buildingsPlaced", placed);
        ListTag peace = new ListTag();
        for (String id : peaceScenesSeen) {
            peace.add(StringTag.valueOf(id));
        }
        tag.put("peaceScenesSeen", peace);
        ListTag npcFav = new ListTag();
        for (var e : townNpcFavor.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("fav", e.getValue());
            npcFav.add(c);
        }
        tag.put("townNpcFavor", npcFav);
        ListTag npcJobs = new ListTag();
        for (var e : townNpcJob.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putString("job", e.getValue());
            npcJobs.add(c);
        }
        tag.put("townNpcJob", npcJobs);
        ListTag npcStory = new ListTag();
        for (var e : townNpcStory.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("ch", e.getValue());
            npcStory.add(c);
        }
        tag.put("townNpcStory", npcStory);
        ListTag recruited = new ListTag();
        for (String id : townNpcRecruited) recruited.add(StringTag.valueOf(id));
        tag.put("townNpcRecruited", recruited);
        ListTag dreams = new ListTag();
        for (var e : townNpcDreamStage.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("st", e.getValue());
            dreams.add(c);
        }
        tag.put("townNpcDreamStage", dreams);
        ListTag microCd = new ListTag();
        for (var e : kingdomMicroCooldown.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putLong("pos", e.getKey());
            c.putLong("tick", e.getValue());
            microCd.add(c);
        }
        tag.put("kingdomMicroCooldown", microCd);
        tag.putInt("kingdomStrayCatsReturned", kingdomStrayCatsReturned);
        tag.putLong("lastStardustWishDay", lastStardustWishDay);
        ListTag lifeEv = new ListTag();
        for (String ev : familyLifeEvents) lifeEv.add(StringTag.valueOf(ev));
        tag.put("familyLifeEvents", lifeEv);
        tag.putString("lastFamilyEvent", lastFamilyEvent == null ? "" : lastFamilyEvent);
        tag.putLong("lastVillageFestivalDay", lastVillageFestivalDay);
        tag.putString("activeVillageFestival", activeVillageFestival == null ? "" : activeVillageFestival);
        tag.putInt("mpsDayIndex", mpsDayIndex);
        ListTag mps = new ListTag();
        for (String cell : mpsSchedule) mps.add(StringTag.valueOf(cell == null ? "" : cell));
        tag.put("mpsSchedule", mps);
        tag.putInt("festivalPrepDay", festivalPrepDay);
        tag.putInt("festivalPrepProgress", festivalPrepProgress);
        tag.putInt("festivalPhase", festivalPhase);
        tag.putLong("festivalStartTick", festivalStartTick);
        tag.putInt("festivalContestScore", festivalContestScore);
        tag.putBoolean("festivalContestSubmitted", festivalContestSubmitted);
        tag.putBoolean("festivalSetupHelped", festivalSetupHelped);
        tag.putBoolean("festivalDanceDone", festivalDanceDone);
        tag.putInt("festivalDanceScore", festivalDanceScore);
        tag.putString("festivalWish", festivalWish == null ? "" : festivalWish);
        tag.putString("pendingFestivalWish", pendingFestivalWish == null ? "" : pendingFestivalWish);
        tag.putLong("growthTickDay", growthTickDay);
        tag.putFloat("dailyEmotionGain", dailyEmotionGain);
        tag.putLong("lastPetCocoDay", lastPetCocoDay);
        tag.putLong("lastPetJennaDay", lastPetJennaDay);
        tag.putLong("kingdomCalendarDay", kingdomCalendarDay);
        tag.putInt("kingdomSeason", kingdomSeason);
        tag.putString("lastSeasonalFestival", lastSeasonalFestival == null ? "" : lastSeasonalFestival);
        tag.putInt("npcFatigue", npcFatigue);
        tag.putInt("warehouseBonus", warehouseBonus);
        tag.putBoolean("libraryCurator", libraryCurator);
        tag.putLong("twinBlessingLastEnact", twinBlessingLastEnact);
        ListTag shelf = new ListTag();
        for (PictureBookPage p : libraryShelfPages) {
            CompoundTag c = new CompoundTag();
            c.putString("bg", p.background());
            c.putString("cap", p.caption());
            c.putString("stk", p.sticker());
            c.putString("flt", p.filter());
            shelf.add(c);
        }
        tag.put("libraryShelf", shelf);
        tag.putInt("shadowCoins", shadowCoins);
        tag.putInt("undercatChapter", undercatChapter);
        tag.putInt("undercatStage", undercatStage);
        tag.putInt("undercatCommissions", undercatCommissions);
        tag.putInt("undercatTrials", undercatTrials);
        tag.putInt("undercatGladiators", undercatGladiators);
        tag.putInt("undercatEnding", undercatEnding);
        tag.putInt("undercatRegions", undercatRegions);
        tag.putInt("undercatLeechKills", undercatLeechKills);
        tag.putInt("undercatEntranceSeen", undercatEntranceSeen);
        tag.putInt("corrugataAffinity", corrugataAffinity);
        tag.putInt("oneEyeAffinity", oneEyeAffinity);
        tag.putInt("undercatSideFlags", undercatSideFlags);
        tag.putLong("undercatDailyDay", undercatDailyDay);
        tag.putInt("undercatDailyQuest", undercatDailyQuest);
        tag.putBoolean("undercatDailyDone", undercatDailyDone);
        tag.putInt("arenaBetAmount", arenaBetAmount);
        tag.putInt("catnipPlantStreak", catnipPlantStreak);
        tag.putLong("pawStampCocoDay", pawStampCocoDay);
        tag.putLong("pawStampJennaDay", pawStampJennaDay);
        tag.putInt("guardianDiscoveries", guardianDiscoveries);
        tag.putLong("loreDiscovered", loreDiscovered);
        tag.putInt("wildCatsDiscovered", wildCatsDiscovered);
        tag.putInt("loreRegionsComplete", loreRegionsComplete);
        tag.putInt("dungeonsCleared", dungeonsCleared);
        ListTag journal = new ListTag();
        for (String line : explorationJournal) {
            journal.add(StringTag.valueOf(line));
        }
        tag.put("explorationJournal", journal);
        tag.putInt("remnantBurned", remnantBurned);
        tag.putBoolean("fallenVelvetRedeemed", fallenVelvetRedeemed);
        tag.putBoolean("sanhuaEternalMaterials", sanhuaEternalMaterials);
        ListTag cookingUnlock = new ListTag();
        for (String id : unlockedCookingRecipes) {
            cookingUnlock.add(StringTag.valueOf(id));
        }
        tag.put("unlockedCookingRecipes", cookingUnlock);
        ListTag nightSecrets = new ListTag();
        for (String id : nightSecretsSeen) {
            nightSecrets.add(StringTag.valueOf(id));
        }
        tag.put("nightSecretsSeen", nightSecrets);
        tag.putLong("systemScanUntil", systemScanUntil);
        tag.putLong("lastNightSecretDay", lastNightSecretDay);
        tag.putLong("lastTapePrayerDay", lastTapePrayerDay);
        ListTag urep = new ListTag();
        for (var e : undercatRep.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.getKey());
            c.putInt("rep", e.getValue());
            urep.add(c);
        }
        tag.put("undercatRep", urep);
        tag.putBoolean("guardianGuide", receivedGuardianGuide);
        tag.putLong("catKingdomEnterDay", catKingdomEnterDay);
        tag.putLong("lastCozyDay", lastCozyEventDay);
        tag.putInt("cozyToday", cozyEventsToday);
        tag.putLong("groomCd", groomCooldownUntil);
        tag.putLong("napCd", napCooldownUntil);
        tag.putInt("villagePop", villagePopulation);
        tag.putInt("villageHousing", villageHousingCapacity);
        tag.putInt("villageFood", villageFoodStock);
        tag.putInt("villageDefense", villageDefense);
        tag.putLong("lastVillageDay", lastVillageTickDay);
        tag.putBoolean("swordBoneAwakened", swordBoneAwakened);
        tag.putBoolean("swordBoneSupreme", swordBoneSupreme);
        ListTag bones = new ListTag();
        for (com.cocojenna.swordbone.SwordBoneEntry e : swordBones) {
            CompoundTag c = new CompoundTag();
            c.putString("id", e.weaponId());
            c.putBoolean("damaged", e.damaged());
            bones.add(c);
        }
        tag.put("swordBones", bones);
        ListTag ryokatana = new ListTag();
        for (String id : collectedRyokatana) ryokatana.add(StringTag.valueOf(id));
        tag.put("collectedRyokatana", ryokatana);
        ListTag memories = new ListTag();
        for (String id : unlockedWeaponMemories) memories.add(StringTag.valueOf(id));
        tag.put("weaponMemories", memories);
        tag.putLong("swordBoneDeathSaveCd", swordBoneDeathSaveCd);
        tag.putLong("swordBoneResonanceCd", swordBoneResonanceCd);
        tag.putLong("swordBoneResonanceUntil", swordBoneResonanceUntil);
        tag.putBoolean("armorAwakened", armorAwakened);
        tag.putLong("armorAwakenCd", armorAwakenCd);
        tag.putInt("armorShieldCharge", armorShieldCharge);
        tag.putLong("armorShieldUntil", armorShieldUntil);
        tag.putInt("armorMorphForm", armorMorphForm);
        tag.putInt("qinKemuFavor", qinKemuFavor);
        tag.putInt("qinKemuQuestStage", qinKemuQuestStage);
        tag.putLong("qinWeaponAwakenCooldownUntil", qinWeaponAwakenCooldownUntil);
        tag.putLong("discoveredMausoleums", discoveredMausoleums);
        tag.put("catBond", catBondSection.serialize());
        tag.put("multiplayer", multiplayerSection.serialize());
        tag.put("sequence", sequenceSection.serialize());
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        setCocoEmotion(tag.getFloat("cocoEmotion"));
        setCocoIndependence(tag.getFloat("cocoIndependence"));
        setCocoAwakening(tag.getInt("cocoAwakening"));
        setCocoProtectiveness(tag.getFloat("cocoProtectiveness"));
        setCocoMoonAffinity(tag.getFloat("cocoMoonAffinity"));
        setCocoAttachment(tag.getFloat("cocoAttachment"));
        setCocoSunbathing(tag.getFloat("cocoSunbathing"));
        setJennaEmotion(tag.getFloat("jennaEmotion"));
        setJennaIndependence(tag.getFloat("jennaIndependence"));
        setJennaAwakening(tag.getInt("jennaAwakening"));
        setJennaPlayfulness(tag.getFloat("jennaPlayfulness"));
        setJennaCuriosity(tag.getFloat("jennaCuriosity"));
        setJennaContentment(tag.getFloat("jennaContentment"));
        setSisterBond(tag.getFloat("sisterBond"));
        endgameUnlocked = tag.getBoolean("endgameUnlocked");
        setMemoryShardsTotal(tag.getInt("memoryShardsTotal"));
        lastInteractCoco = tag.getLong("lastInteractCoco");
        lastInteractJenna = tag.getLong("lastInteractJenna");
        lastFeedCoco = tag.getLong("lastFeedCoco");
        lastFeedJenna = tag.getLong("lastFeedJenna");
        setUnlockedSequencesRaw(tag.getLong("unlockedSequences"));
        firstCryQuestStage = tag.getInt("firstCryQuestStage");
        arrivalTutorialStage = tag.contains("arrivalTutorialStage") ? tag.getInt("arrivalTutorialStage") : 0;
        setOnboardingQuestStep(tag.contains("onboardingQuestStep") ? tag.getInt("onboardingQuestStep") : 0);
        onboardingWoodCollected = tag.contains("onboardingWoodCollected") ? tag.getInt("onboardingWoodCollected") : 0;
        onboardingStoneCollected = tag.contains("onboardingStoneCollected") ? tag.getInt("onboardingStoneCollected") : 0;
        penetrationQuestStage = tag.contains("penetrationQuestStage") ? tag.getInt("penetrationQuestStage") : 0;
        activeWeaponMemoryTaskId = tag.getString("activeWeaponMemoryTaskId");
        weaponMemoryTaskProgress = tag.contains("weaponMemoryTaskProgress") ? tag.getInt("weaponMemoryTaskProgress") : 0;
        shadowClawEnding = tag.getString("shadowClawEnding");
        grayWhiskerFavor = tag.contains("grayWhiskerFavor") ? tag.getInt("grayWhiskerFavor") : 0;
        overworldInfluence = tag.contains("overworldInfluence") ? tag.getInt("overworldInfluence") : 0;
        catKingdomInfluence = tag.contains("catKingdomInfluence") ? tag.getInt("catKingdomInfluence") : 0;
        moonPawTrailCount = tag.contains("moonPawTrailCount") ? tag.getInt("moonPawTrailCount") : 0;
        catLanguageLevel = tag.contains("catLanguageLevel") ? tag.getInt("catLanguageLevel") : 0;
        catGraffitiRead = tag.contains("catGraffitiRead") ? tag.getInt("catGraffitiRead") : 0;
        grayWhiskerMet = tag.getBoolean("grayWhiskerMet");
        caravanEscortActive = tag.getBoolean("caravanEscort");
        caravanStartX = tag.contains("caravanStartX") ? tag.getInt("caravanStartX") : 0;
        caravanStartZ = tag.contains("caravanStartZ") ? tag.getInt("caravanStartZ") : 0;
        caravanDestX = tag.contains("caravanDestX") ? tag.getInt("caravanDestX") : 0;
        caravanDestZ = tag.contains("caravanDestZ") ? tag.getInt("caravanDestZ") : 0;
        lastBlackMudLeakTick = tag.contains("lastBlackMudLeakTick") ? tag.getLong("lastBlackMudLeakTick") : 0L;
        ruinMapFragmentBits = tag.contains("ruinMapFragmentBits") ? tag.getInt("ruinMapFragmentBits") : 0;
        overworldDungeonStage = tag.contains("overworldDungeonStage") ? tag.getInt("overworldDungeonStage") : 0;
        fusionBuildingsPlaced = tag.contains("fusionBuildingsPlaced") ? tag.getInt("fusionBuildingsPlaced") : 0;
        moonResonanceCount = tag.contains("moonResonanceCount") ? tag.getInt("moonResonanceCount") : 0;
        catSocietyInteractions = tag.contains("catSocietyInteractions") ? tag.getInt("catSocietyInteractions") : 0;
        catSocietyPeakFavor = tag.contains("catSocietyPeakFavor") ? tag.getInt("catSocietyPeakFavor") : 0;
        catnipTradedTotal = tag.contains("catnipTradedTotal") ? tag.getInt("catnipTradedTotal") : 0;
        lastEmbassyTeleportTick = tag.contains("lastEmbassyTeleportTick") ? tag.getLong("lastEmbassyTeleportTick") : 0L;
        lastThroneBlessingTick = tag.contains("lastThroneBlessingTick") ? tag.getLong("lastThroneBlessingTick") : 0L;
        moonCoreBlessingUntil = tag.contains("moonCoreBlessingUntil") ? tag.getLong("moonCoreBlessingUntil") : 0L;
        overworldRuinCorrosion = tag.contains("overworldRuinCorrosion") ? tag.getInt("overworldRuinCorrosion") : 0;
        lastTheaterPerformanceTick = tag.contains("lastTheaterPerformanceTick") ? tag.getLong("lastTheaterPerformanceTick") : 0L;
        lastTheaterGatheringDay = tag.contains("lastTheaterGatheringDay") ? tag.getLong("lastTheaterGatheringDay") : -1L;
        lastFamilyGatheringDay = tag.contains("lastFamilyGatheringDay") ? tag.getLong("lastFamilyGatheringDay") : -1L;
        catFamiliesSeeded = tag.getBoolean("catFamiliesSeeded");
        overworldNpcFavor.clear();
        if (tag.contains("overworldNpcFavor")) {
            for (Tag t : tag.getList("overworldNpcFavor", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                overworldNpcFavor.put(c.getString("id"), c.getInt("fav"));
            }
        }
        overworldSoulCompanions.clear();
        if (tag.contains("overworldSoulCompanions")) {
            for (Tag t : tag.getList("overworldSoulCompanions", Tag.TAG_STRING)) {
                overworldSoulCompanions.add(t.getAsString());
            }
        }
        townNpcRomanceStage.clear();
        if (tag.contains("townNpcRomanceStage")) {
            for (Tag t : tag.getList("townNpcRomanceStage", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                townNpcRomanceStage.put(c.getString("id"), c.getInt("st"));
            }
        }
        townNpcFamily.clear();
        townNpcFamilyRole.clear();
        if (tag.contains("townNpcFamily")) {
            for (Tag t : tag.getList("townNpcFamily", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                townNpcFamily.put(c.getString("id"), c.getString("fam"));
                townNpcFamilyRole.put(c.getString("id"), c.getString("role"));
            }
        }
        marriagePartnerNpcId = tag.getString("marriagePartnerNpcId");
        engagementDay = tag.contains("engagementDay") ? tag.getLong("engagementDay") : -1L;
        weddingScheduledDay = tag.contains("weddingScheduledDay") ? tag.getLong("weddingScheduledDay") : -1L;
        pregnancyDueDay = tag.contains("pregnancyDueDay") ? tag.getLong("pregnancyDueDay") : -1L;
        kittenCount = tag.contains("kittenCount") ? tag.getInt("kittenCount") : 0;
        revealedHiddenPos.clear();
        if (tag.contains("revealedHidden")) {
            for (net.minecraft.nbt.Tag t : tag.getList("revealedHidden", net.minecraft.nbt.Tag.TAG_LONG)) {
                revealedHiddenPos.add(((net.minecraft.nbt.LongTag) t).getAsLong());
            }
        }
        setFelineForce(tag.getString("felineForce"));
        setFelineTier(tag.contains("felineTier") ? tag.getInt("felineTier") : 9);
        setFelineSkillCooldownUntil(tag.getLong("felineSkillCooldownUntil"));
        forceQuestStage = tag.contains("forceQuestStage") ? tag.getInt("forceQuestStage") : 0;
        forceTrialsMask = tag.contains("forceTrialsMask") ? tag.getInt("forceTrialsMask") : 0;
        forceResetCount = tag.contains("forceResetCount") ? tag.getInt("forceResetCount") : 0;
        activeTrialForce = tag.contains("activeTrialForce") ? tag.getString("activeTrialForce") : "";
        followDistance = tag.contains("followDistance") ? tag.getInt("followDistance") : 0;
        allowAffection = !tag.contains("allowAffection") || tag.getBoolean("allowAffection");
        allowExplore = !tag.contains("allowExplore") || tag.getBoolean("allowExplore");
        allowCombat = !tag.contains("allowCombat") || tag.getBoolean("allowCombat");
        muteMode = tag.getBoolean("muteMode");
        metIronpaw = tag.getBoolean("metIronpaw");
        metBlindMerchant = tag.getBoolean("metBlindMerchant");
        guardian = tag.getBoolean("guardian");
        repGearTown = tag.contains("repGearTown") ? tag.getInt("repGearTown") : 0;
        repRoyal = tag.contains("repRoyal") ? tag.getInt("repRoyal") : 0;
        repDawn = tag.contains("repDawn") ? tag.getInt("repDawn") : 0;
        repBlindPort = tag.contains("repBlindPort") ? tag.getInt("repBlindPort") : 0;
        repFirstCry = tag.contains("repFirstCry") ? tag.getInt("repFirstCry") : 0;
        purchasedRepOffers.clear();
        if (tag.contains("purchasedRepOffers")) {
            for (Tag t : tag.getList("purchasedRepOffers", Tag.TAG_STRING)) {
                purchasedRepOffers.add(t.getAsString());
            }
        }
        setPromotionCardCount(tag.contains("promotionCardCount") ? tag.getInt("promotionCardCount") : 0);
        setPromotionCardBonus(tag.contains("promotionCardBonus") ? tag.getFloat("promotionCardBonus") : 0f);
        if (tag.contains("ownedPromotionCards")) {
            List<String> cards = new ArrayList<>();
            for (var e : tag.getList("ownedPromotionCards", 8)) {
                cards.add(e.getAsString());
            }
            replaceOwnedPromotionCards(cards);
        } else {
            replaceOwnedPromotionCards(List.of());
        }
        setPendingPromotionTier(tag.contains("pendingPromotionTier") ? tag.getInt("pendingPromotionTier") : 0);
        setCeremonyStage(tag.contains("ceremonyStage") ? tag.getInt("ceremonyStage") : 0);
        setCeremonyTimeout(tag.contains("ceremonyTimeout") ? tag.getInt("ceremonyTimeout") : 0);
        setMarkLevel(tag.contains("markLevel") ? tag.getInt("markLevel") : 0);
        setMarkForce(tag.contains("markForce") ? tag.getString("markForce") : "");
        setSimplifiedCeremony(tag.contains("simplifiedCeremony") && tag.getBoolean("simplifiedCeremony"));
        setHiddenSequences(tag.contains("hiddenSequences") ? tag.getLong("hiddenSequences") : 0L);
        lastOnlineTick = tag.contains("lastOnlineTick") ? tag.getLong("lastOnlineTick") : 0L;
        showSkillCooldown = !tag.contains("showSkillCooldown") || tag.getBoolean("showSkillCooldown");
        preferredSkillSlot = tag.contains("preferredSkillSlot") ? tag.getInt("preferredSkillSlot") : 0;
        activeSkillPreset = tag.contains("activeSkillPreset") ? tag.getInt("activeSkillPreset") : 0;
        if (tag.contains("skillPresetSlots")) {
            var list = tag.getList("skillPresetSlots", 3);
            for (int i = 0; i < skillPresetSlots.length; i++) {
                skillPresetSlots[i] = i < list.size() ? ((net.minecraft.nbt.IntTag) list.get(i)).getAsInt() : 0;
            }
        }
        if (activeSkillPreset >= 0 && activeSkillPreset < skillPresetSlots.length) {
            preferredSkillSlot = skillPresetSlots[activeSkillPreset];
        }
        setAwakeningTrialTier(tag.getInt("awakeningTrialTier"));
        setAwakeningTrialActive(tag.contains("awakeningTrialActive") && tag.getBoolean("awakeningTrialActive"));
        setAwakeningTrialIndex(tag.contains("awakeningTrialIndex") ? tag.getInt("awakeningTrialIndex") : 0);
        setAwakeningTrialKills(tag.contains("awakeningTrialKills") ? tag.getInt("awakeningTrialKills") : 0);
        setAwakeningTrialGoal(tag.contains("awakeningTrialGoal") ? tag.getInt("awakeningTrialGoal") : 0);
        setAwakeningTrialDeadline(tag.contains("awakeningTrialDeadline") ? tag.getLong("awakeningTrialDeadline") : 0);
        kingdomProsperity = tag.getInt("kingdomProsperity");
        kingdomHappiness = tag.contains("kingdomHappiness") ? tag.getInt("kingdomHappiness") : 50;
        kingdomStability = tag.contains("kingdomStability") ? tag.getInt("kingdomStability") : 50;
        kingdomReputation = tag.contains("kingdomReputation") ? tag.getInt("kingdomReputation") : 10;
        buildCreativity = tag.getInt("buildCreativity");
        ironpawForgeLevel = tag.contains("ironpawForgeLevel") ? tag.getInt("ironpawForgeLevel") : 1;
        kingdomDecree = tag.contains("kingdomDecree") ? tag.getString("kingdomDecree") : "peace";
        activeDecrees.clear();
        if (tag.contains("activeDecrees")) {
            for (Tag t : tag.getList("activeDecrees", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                activeDecrees.add(new ActiveDecree(c.getString("id"), c.getLong("expires")));
            }
        }
        pictureBookPages.clear();
        if (tag.contains("pictureBookPages")) {
            for (Tag t : tag.getList("pictureBookPages", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                pictureBookPages.add(new PictureBookPage(
                        c.getString("bg"), c.getString("cap"), c.getString("stk"), c.getString("flt")));
            }
        }
        buildingProgress.clear();
        if (tag.contains("buildingProgress")) {
            for (Tag t : tag.getList("buildingProgress", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                buildingProgress.put(c.getString("id"), c.getInt("prog"));
            }
        }
        buildingsPlaced.clear();
        if (tag.contains("buildingsPlaced")) {
            for (Tag t : tag.getList("buildingsPlaced", 8)) {
                buildingsPlaced.add(t.getAsString());
            }
        }
        peaceScenesSeen.clear();
        if (tag.contains("peaceScenesSeen")) {
            for (Tag t : tag.getList("peaceScenesSeen", 8)) {
                peaceScenesSeen.add(t.getAsString());
            }
        }
        townNpcFavor.clear();
        if (tag.contains("townNpcFavor")) {
            for (Tag t : tag.getList("townNpcFavor", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                townNpcFavor.put(c.getString("id"), c.getInt("fav"));
            }
        }
        townNpcJob.clear();
        if (tag.contains("townNpcJob")) {
            for (Tag t : tag.getList("townNpcJob", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                townNpcJob.put(c.getString("id"), c.getString("job"));
            }
        }
        townNpcStory.clear();
        if (tag.contains("townNpcStory")) {
            for (Tag t : tag.getList("townNpcStory", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                townNpcStory.put(c.getString("id"), c.getInt("ch"));
            }
        }
        townNpcRecruited.clear();
        if (tag.contains("townNpcRecruited")) {
            for (Tag t : tag.getList("townNpcRecruited", 8)) {
                townNpcRecruited.add(t.getAsString());
            }
        }
        townNpcDreamStage.clear();
        if (tag.contains("townNpcDreamStage")) {
            for (Tag t : tag.getList("townNpcDreamStage", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                townNpcDreamStage.put(c.getString("id"), c.getInt("st"));
            }
        }
        kingdomMicroCooldown.clear();
        if (tag.contains("kingdomMicroCooldown")) {
            for (Tag t : tag.getList("kingdomMicroCooldown", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                kingdomMicroCooldown.put(c.getLong("pos"), c.getLong("tick"));
            }
        }
        kingdomStrayCatsReturned = tag.contains("kingdomStrayCatsReturned")
                ? tag.getInt("kingdomStrayCatsReturned") : 0;
        lastStardustWishDay = tag.contains("lastStardustWishDay") ? tag.getLong("lastStardustWishDay") : -1;
        familyLifeEvents.clear();
        if (tag.contains("familyLifeEvents")) {
            for (Tag t : tag.getList("familyLifeEvents", 8)) familyLifeEvents.add(t.getAsString());
        }
        lastFamilyEvent = tag.contains("lastFamilyEvent") ? tag.getString("lastFamilyEvent") : "";
        lastVillageFestivalDay = tag.contains("lastVillageFestivalDay") ? tag.getLong("lastVillageFestivalDay") : -1;
        activeVillageFestival = tag.contains("activeVillageFestival") ? tag.getString("activeVillageFestival") : "";
        mpsDayIndex = tag.contains("mpsDayIndex") ? tag.getInt("mpsDayIndex") : 0;
        if (tag.contains("mpsSchedule")) {
            var list = tag.getList("mpsSchedule", 8);
            for (int i = 0; i < mpsSchedule.length; i++) {
                mpsSchedule[i] = i < list.size() ? list.getString(i) : "";
            }
        }
        festivalPrepDay = tag.contains("festivalPrepDay") ? tag.getInt("festivalPrepDay") : 7;
        festivalPrepProgress = tag.contains("festivalPrepProgress") ? tag.getInt("festivalPrepProgress") : 0;
        festivalPhase = tag.contains("festivalPhase") ? tag.getInt("festivalPhase") : 0;
        festivalStartTick = tag.contains("festivalStartTick") ? tag.getLong("festivalStartTick") : 0;
        festivalContestScore = tag.contains("festivalContestScore") ? tag.getInt("festivalContestScore") : 0;
        festivalContestSubmitted = tag.contains("festivalContestSubmitted") && tag.getBoolean("festivalContestSubmitted");
        festivalSetupHelped = tag.contains("festivalSetupHelped") && tag.getBoolean("festivalSetupHelped");
        festivalDanceDone = tag.contains("festivalDanceDone") && tag.getBoolean("festivalDanceDone");
        festivalDanceScore = tag.contains("festivalDanceScore") ? tag.getInt("festivalDanceScore") : 0;
        festivalWish = tag.contains("festivalWish") ? tag.getString("festivalWish") : "";
        pendingFestivalWish = tag.contains("pendingFestivalWish") ? tag.getString("pendingFestivalWish") : "";
        growthTickDay = tag.contains("growthTickDay") ? tag.getLong("growthTickDay") : -1;
        dailyEmotionGain = tag.contains("dailyEmotionGain") ? tag.getFloat("dailyEmotionGain") : 0;
        lastPetCocoDay = tag.contains("lastPetCocoDay") ? tag.getLong("lastPetCocoDay") : 0;
        lastPetJennaDay = tag.contains("lastPetJennaDay") ? tag.getLong("lastPetJennaDay") : 0;
        kingdomCalendarDay = tag.contains("kingdomCalendarDay") ? tag.getLong("kingdomCalendarDay") : -1;
        kingdomSeason = tag.contains("kingdomSeason") ? tag.getInt("kingdomSeason") : 0;
        lastSeasonalFestival = tag.contains("lastSeasonalFestival") ? tag.getString("lastSeasonalFestival") : "";
        npcFatigue = tag.contains("npcFatigue") ? tag.getInt("npcFatigue") : 0;
        warehouseBonus = tag.contains("warehouseBonus") ? tag.getInt("warehouseBonus") : 0;
        libraryCurator = tag.contains("libraryCurator") && tag.getBoolean("libraryCurator");
        twinBlessingLastEnact = tag.contains("twinBlessingLastEnact") ? tag.getLong("twinBlessingLastEnact") : 0;
        libraryShelfPages.clear();
        if (tag.contains("libraryShelf")) {
            for (Tag t : tag.getList("libraryShelf", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                libraryShelfPages.add(new PictureBookPage(
                        c.getString("bg"), c.getString("cap"), c.getString("stk"), c.getString("flt")));
            }
        }
        shadowCoins = tag.contains("shadowCoins") ? tag.getInt("shadowCoins") : 0;
        undercatChapter = tag.contains("undercatChapter") ? tag.getInt("undercatChapter") : 0;
        undercatStage = tag.contains("undercatStage") ? tag.getInt("undercatStage") : 0;
        undercatCommissions = tag.contains("undercatCommissions") ? tag.getInt("undercatCommissions") : 0;
        undercatTrials = tag.contains("undercatTrials") ? tag.getInt("undercatTrials") : 0;
        undercatGladiators = tag.contains("undercatGladiators") ? tag.getInt("undercatGladiators") : 0;
        undercatEnding = tag.contains("undercatEnding") ? tag.getInt("undercatEnding") : 0;
        undercatRegions = tag.contains("undercatRegions") ? tag.getInt("undercatRegions") : 0;
        undercatLeechKills = tag.contains("undercatLeechKills") ? tag.getInt("undercatLeechKills") : 0;
        undercatEntranceSeen = tag.contains("undercatEntranceSeen") ? tag.getInt("undercatEntranceSeen") : 0;
        corrugataAffinity = tag.contains("corrugataAffinity") ? tag.getInt("corrugataAffinity") : 0;
        oneEyeAffinity = tag.contains("oneEyeAffinity") ? tag.getInt("oneEyeAffinity") : 0;
        undercatSideFlags = tag.contains("undercatSideFlags") ? tag.getInt("undercatSideFlags") : 0;
        undercatDailyDay = tag.contains("undercatDailyDay") ? tag.getLong("undercatDailyDay") : -1;
        undercatDailyQuest = tag.contains("undercatDailyQuest") ? tag.getInt("undercatDailyQuest") : -1;
        undercatDailyDone = tag.contains("undercatDailyDone") && tag.getBoolean("undercatDailyDone");
        arenaBetAmount = tag.contains("arenaBetAmount") ? tag.getInt("arenaBetAmount") : 0;
        catnipPlantStreak = tag.contains("catnipPlantStreak") ? tag.getInt("catnipPlantStreak") : 0;
        pawStampCocoDay = tag.contains("pawStampCocoDay") ? tag.getLong("pawStampCocoDay") : -1;
        pawStampJennaDay = tag.contains("pawStampJennaDay") ? tag.getLong("pawStampJennaDay") : -1;
        guardianDiscoveries = tag.contains("guardianDiscoveries") ? tag.getInt("guardianDiscoveries") : 0;
        loreDiscovered = tag.contains("loreDiscovered") ? tag.getLong("loreDiscovered") : 0L;
        wildCatsDiscovered = tag.contains("wildCatsDiscovered") ? tag.getInt("wildCatsDiscovered") : 0;
        loreRegionsComplete = tag.contains("loreRegionsComplete") ? tag.getInt("loreRegionsComplete") : 0;
        dungeonsCleared = tag.contains("dungeonsCleared") ? tag.getInt("dungeonsCleared") : 0;
        explorationJournal.clear();
        if (tag.contains("explorationJournal")) {
            for (Tag t : tag.getList("explorationJournal", 8)) {
                explorationJournal.add(t.getAsString());
            }
        }
        remnantBurned = tag.contains("remnantBurned") ? tag.getInt("remnantBurned") : 0;
        fallenVelvetRedeemed = tag.contains("fallenVelvetRedeemed") && tag.getBoolean("fallenVelvetRedeemed");
        sanhuaEternalMaterials = tag.contains("sanhuaEternalMaterials") && tag.getBoolean("sanhuaEternalMaterials");
        unlockedCookingRecipes.clear();
        if (tag.contains("unlockedCookingRecipes")) {
            for (Tag t : tag.getList("unlockedCookingRecipes", 8)) {
                unlockedCookingRecipes.add(t.getAsString());
            }
        }
        nightSecretsSeen.clear();
        if (tag.contains("nightSecretsSeen")) {
            for (Tag t : tag.getList("nightSecretsSeen", 8)) {
                nightSecretsSeen.add(t.getAsString());
            }
        }
        systemScanUntil = tag.contains("systemScanUntil") ? tag.getLong("systemScanUntil") : 0;
        lastNightSecretDay = tag.contains("lastNightSecretDay") ? tag.getLong("lastNightSecretDay") : -1;
        lastTapePrayerDay = tag.contains("lastTapePrayerDay") ? tag.getLong("lastTapePrayerDay") : -1;
        undercatRep.clear();
        if (tag.contains("undercatRep")) {
            for (Tag t : tag.getList("undercatRep", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                undercatRep.put(c.getString("id"), c.getInt("rep"));
            }
        }
        receivedGuardianGuide = tag.getBoolean("guardianGuide");
        catKingdomEnterDay = tag.getLong("catKingdomEnterDay");
        lastCozyEventDay = tag.getLong("lastCozyDay");
        cozyEventsToday = tag.getInt("cozyToday");
        groomCooldownUntil = tag.getLong("groomCd");
        napCooldownUntil = tag.getLong("napCd");
        villagePopulation = tag.contains("villagePop") ? tag.getInt("villagePop") : 2;
        villageHousingCapacity = tag.contains("villageHousing") ? tag.getInt("villageHousing") : 6;
        villageFoodStock = tag.contains("villageFood") ? tag.getInt("villageFood") : 40;
        villageDefense = tag.contains("villageDefense") ? tag.getInt("villageDefense") : 20;
        lastVillageTickDay = tag.getLong("lastVillageDay");
        swordBoneAwakened = tag.getBoolean("swordBoneAwakened");
        swordBoneSupreme = tag.getBoolean("swordBoneSupreme");
        swordBones.clear();
        if (tag.contains("swordBones")) {
            for (Tag t : tag.getList("swordBones", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                swordBones.add(new com.cocojenna.swordbone.SwordBoneEntry(
                        c.getString("id"), c.getBoolean("damaged")));
            }
        }
        collectedRyokatana.clear();
        if (tag.contains("collectedRyokatana")) {
            for (Tag t : tag.getList("collectedRyokatana", Tag.TAG_STRING)) {
                collectedRyokatana.add(t.getAsString());
            }
        }
        unlockedWeaponMemories.clear();
        if (tag.contains("weaponMemories")) {
            for (Tag t : tag.getList("weaponMemories", Tag.TAG_STRING)) {
                unlockedWeaponMemories.add(t.getAsString());
            }
        }
        swordBoneDeathSaveCd = tag.contains("swordBoneDeathSaveCd") ? tag.getLong("swordBoneDeathSaveCd") : 0;
        swordBoneResonanceCd = tag.contains("swordBoneResonanceCd") ? tag.getLong("swordBoneResonanceCd") : 0;
        swordBoneResonanceUntil = tag.contains("swordBoneResonanceUntil") ? tag.getLong("swordBoneResonanceUntil") : 0;
        armorAwakened = tag.getBoolean("armorAwakened");
        armorAwakenCd = tag.contains("armorAwakenCd") ? tag.getLong("armorAwakenCd") : 0;
        armorShieldCharge = tag.contains("armorShieldCharge") ? tag.getInt("armorShieldCharge") : 0;
        armorShieldUntil = tag.contains("armorShieldUntil") ? tag.getLong("armorShieldUntil") : 0;
        armorMorphForm = tag.contains("armorMorphForm") ? tag.getInt("armorMorphForm") : 0;
        qinKemuFavor = tag.contains("qinKemuFavor") ? tag.getInt("qinKemuFavor") : 0;
        qinKemuQuestStage = tag.contains("qinKemuQuestStage") ? tag.getInt("qinKemuQuestStage") : 0;
        qinWeaponAwakenCooldownUntil = tag.contains("qinWeaponAwakenCooldownUntil")
                ? tag.getLong("qinWeaponAwakenCooldownUntil") : 0L;
        discoveredMausoleums = tag.contains("discoveredMausoleums") ? tag.getLong("discoveredMausoleums") : 0L;
        if (tag.contains("catBond")) {
            catBondSection.deserialize(tag.getCompound("catBond"));
        }
        if (tag.contains("multiplayer")) {
            multiplayerSection.deserialize(tag.getCompound("multiplayer"));
        }
        if (tag.contains("sequence")) {
            sequenceSection.deserialize(tag.getCompound("sequence"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Getters / Setters
    // ─────────────────────────────────────────────────────────────────────

    public float getCocoEmotion()          { return catBondSection.getCocoEmotion(); }
    public float getCocoIndependence()     { return catBondSection.getCocoIndependence(); }
    public int   getCocoAwakening()        { return catBondSection.getCocoAwakening(); }
    public float getCocoProtectiveness()   { return catBondSection.getCocoProtectiveness(); }
    public float getCocoMoonAffinity()     { return catBondSection.getCocoMoonAffinity(); }
    public float getCocoAttachment()       { return catBondSection.getCocoAttachment(); }
    public float getCocoSunbathing()       { return catBondSection.getCocoSunbathing(); }
    public float getJennaEmotion()         { return catBondSection.getJennaEmotion(); }
    public float getJennaIndependence()    { return catBondSection.getJennaIndependence(); }
    public int   getJennaAwakening()       { return catBondSection.getJennaAwakening(); }
    public float getJennaPlayfulness()     { return catBondSection.getJennaPlayfulness(); }
    public float getJennaCuriosity()       { return catBondSection.getJennaCuriosity(); }
    public float getJennaContentment()     { return catBondSection.getJennaContentment(); }
    public float getSisterBond()           { return catBondSection.getSisterBond(); }
    public boolean isEndgameUnlocked()     { return endgameUnlocked; }
    public int   getMemoryShardsTotal()    { return sequenceSection.getMemoryShardsTotal(); }
    public long  getLastInteractCoco()     { return lastInteractCoco; }
    public long  getLastInteractJenna()    { return lastInteractJenna; }
    public long  getLastFeedCoco()         { return lastFeedCoco; }
    public long  getLastFeedJenna()        { return lastFeedJenna; }

    public void setCocoEmotion(float v)        { catBondSection.setCocoEmotion(Math.max(0, Math.min(100, v))); }
    public void setCocoIndependence(float v)   { catBondSection.setCocoIndependence(Math.max(0, Math.min(100, v))); }
    public void setJennaEmotion(float v)       { catBondSection.setJennaEmotion(Math.max(0, Math.min(100, v))); }
    public void setJennaIndependence(float v)  { catBondSection.setJennaIndependence(Math.max(0, Math.min(100, v))); }
    public void setSisterBond(float v)         { catBondSection.setSisterBond(Math.max(0, Math.min(100, v))); }
    public void setEndgameUnlocked(boolean v)  { endgameUnlocked = v; }
    public void setLastInteractCoco(long t)    { lastInteractCoco = t; }
    public void setLastInteractJenna(long t)   { lastInteractJenna = t; }
    public void setLastFeedCoco(long t)        { lastFeedCoco = t; }
    public void setLastFeedJenna(long t)       { lastFeedJenna = t; }

    public void triggerEndgame() {
        endgameUnlocked = true;
        setCocoAttachment(50f + getCocoProtectiveness() * 0.3f);
        setCocoSunbathing(0f);
        setJennaPlayfulness(100f);
        setJennaCuriosity(100f);
    }

    public int getFirstCryQuestStage() { return firstCryQuestStage; }
    public void setFirstCryQuestStage(int stage) { firstCryQuestStage = stage; }

    public int getQinKemuFavor() { return qinKemuFavor; }
    public void setQinKemuFavor(int favor) { qinKemuFavor = Math.max(0, Math.min(100, favor)); }
    public void addQinKemuFavor(int amount) { setQinKemuFavor(qinKemuFavor + amount); }
    public int getQinKemuQuestStage() { return qinKemuQuestStage; }
    public void setQinKemuQuestStage(int stage) { qinKemuQuestStage = Math.max(0, Math.min(8, stage)); }
    public long getQinWeaponAwakenCooldownUntil() { return qinWeaponAwakenCooldownUntil; }
    public void setQinWeaponAwakenCooldownUntil(long tick) { qinWeaponAwakenCooldownUntil = tick; }
    public boolean isMausoleumDiscovered(int bit) { return (discoveredMausoleums & (1L << bit)) != 0; }
    public void markMausoleumDiscovered(int bit) { discoveredMausoleums |= (1L << bit); }
    public int getArrivalTutorialStage() { return arrivalTutorialStage; }
    public void setArrivalTutorialStage(int stage) { arrivalTutorialStage = Math.max(0, Math.min(6, stage)); }
    public boolean hasRevealedHidden(BlockPos pos) { return revealedHiddenPos.contains(pos.asLong()); }
    public void markRevealedHidden(BlockPos pos) { revealedHiddenPos.add(pos.asLong()); }
    public String getFelineForce() { return sequenceSection.getFelineForce(); }
    public void setFelineForce(String force) { sequenceSection.setFelineForce(force); }
    public int getForceQuestStage() { return forceQuestStage; }
    public void setForceQuestStage(int s) { forceQuestStage = s; }
    public int getForceTrialsMask() { return forceTrialsMask; }
    public void addForceTrialMask(int mask) { forceTrialsMask |= mask; }
    public int getForceResetCount() { return forceResetCount; }
    public void setForceResetCount(int c) { forceResetCount = c; }
    public String getActiveTrialForce() { return activeTrialForce; }
    public void setActiveTrialForce(String f) { activeTrialForce = f; }
    public int getFelineTier() { return sequenceSection.getFelineTier(); }
    public void setFelineTier(int tier) { sequenceSection.setFelineTier(Math.max(1, Math.min(9, tier))); }
    public long getFelineSkillCooldownUntil() { return sequenceSection.getFelineSkillCooldownUntil(); }
    public void setFelineSkillCooldownUntil(long tick) { sequenceSection.setFelineSkillCooldownUntil(tick); }

    public int getFollowDistance() { return followDistance; }
    public void setFollowDistance(int v) { followDistance = Math.max(0, Math.min(2, v)); }
    public boolean isAllowAffection() { return allowAffection; }
    public void setAllowAffection(boolean v) { allowAffection = v; }
    public boolean isAllowExplore() { return allowExplore; }
    public void setAllowExplore(boolean v) { allowExplore = v; }
    public boolean isAllowCombat() { return allowCombat; }
    public void setAllowCombat(boolean v) { allowCombat = v; }
    public boolean isMuteMode() { return muteMode; }
    public void setMuteMode(boolean v) { muteMode = v; }
    public boolean isMetIronpaw() { return metIronpaw; }
    public void setMetIronpaw(boolean v) { metIronpaw = v; }
    public boolean isMetBlindMerchant() { return metBlindMerchant; }
    public void setMetBlindMerchant(boolean v) { metBlindMerchant = v; }

    public boolean isGuardian() { return guardian; }
    public void setGuardian(boolean v) { guardian = v; }

    public int getReputation(String region) {
        return switch (region) {
            case "gear_town" -> repGearTown;
            case "royal" -> repRoyal;
            case "dawn" -> repDawn;
            case "blind_port" -> repBlindPort;
            case "first_cry" -> repFirstCry;
            default -> 0;
        };
    }

    public void setReputation(String region, int value) {
        int v = Math.max(0, Math.min(100, value));
        switch (region) {
            case "gear_town" -> repGearTown = v;
            case "royal" -> repRoyal = v;
            case "dawn" -> repDawn = v;
            case "blind_port" -> repBlindPort = v;
            case "first_cry" -> repFirstCry = v;
            default -> {}
        }
    }

    public void addReputation(String region, int delta) {
        setReputation(region, getReputation(region) + delta);
    }

    public boolean hasPurchasedRepOffer(String offerId) {
        return purchasedRepOffers.contains(offerId);
    }

    public void markRepOfferPurchased(String offerId) {
        purchasedRepOffers.add(offerId);
    }

    public int getPromotionCardCount() { return sequenceSection.getPromotionCardCount(); }
    public float getPromotionCardBonus() { return sequenceSection.getPromotionCardBonus(); }
    public int getPendingPromotionTier() { return sequenceSection.getPendingPromotionTier(); }
    public void setPendingPromotionTier(int tier) { sequenceSection.setPendingPromotionTier(Math.max(0, tier)); }

    // ── 晉升儀式 getter/setter ───────────────────────────────────────────
    public int getCeremonyStage() { return sequenceSection.getCeremonyStage(); }
    public void setCeremonyStage(int stage) { sequenceSection.setCeremonyStage(Math.max(0, Math.min(6, stage))); }
    public int getCeremonyTimeout() { return sequenceSection.getCeremonyTimeout(); }
    public void setCeremonyTimeout(int ticks) { sequenceSection.setCeremonyTimeout(Math.max(0, ticks)); }
    public long getCeremonyStageStartGameTime() { return sequenceSection.getCeremonyStageStartGameTime(); }
    public void setCeremonyStageStartGameTime(long t) { sequenceSection.setCeremonyStageStartGameTime(t); }

    // ── 序列印記 getter/setter ───────────────────────────────────────────
    public int getMarkLevel() { return sequenceSection.getMarkLevel(); }
    public void setMarkLevel(int level) { sequenceSection.setMarkLevel(Math.max(0, Math.min(3, level))); }
    public String getMarkForce() { return sequenceSection.getMarkForce(); }
    public void setMarkForce(String force) { sequenceSection.setMarkForce(force); }

    // ── 簡化儀式設定 ─────────────────────────────────────────────────────
    public boolean isSimplifiedCeremony() { return sequenceSection.isSimplifiedCeremony(); }
    public void setSimplifiedCeremony(boolean v) { sequenceSection.setSimplifiedCeremony(v); }

    public void addPromotionCard(String cardId) {
        if (sequenceSection.getOwnedPromotionCards().contains(cardId)) return;
        sequenceSection.getOwnedPromotionCards().add(cardId);
        sequenceSection.setPromotionCardCount(sequenceSection.getOwnedPromotionCards().size());
        sequenceSection.setPromotionCardBonus(sequenceSection.getPromotionCardBonus()
                + com.cocojenna.sequence.PromotionCardCatalog.cardBonus(cardId));
    }

    public List<String> getOwnedPromotionCards() {
        return Collections.unmodifiableList(sequenceSection.getOwnedPromotionCards());
    }

    public boolean spendMemoryShards(int amount) {
        if (getMemoryShardsTotal() < amount) return false;
        setMemoryShardsTotal(getMemoryShardsTotal() - amount);
        return true;
    }

    public long getHiddenSequences() { return sequenceSection.getHiddenSequences(); }
    public void setHiddenSequences(long v) { sequenceSection.setHiddenSequences(v); }
    public long getLastOnlineTick() { return lastOnlineTick; }
    public void setLastOnlineTick(long t) { lastOnlineTick = t; }
    public boolean isShowSkillCooldown() { return showSkillCooldown; }
    public void setShowSkillCooldown(boolean v) { showSkillCooldown = v; }
    public int getPreferredSkillSlot() { return preferredSkillSlot; }
    public void setPreferredSkillSlot(int v) {
        preferredSkillSlot = Math.max(0, Math.min(15, v));
        if (activeSkillPreset >= 0 && activeSkillPreset < skillPresetSlots.length) {
            skillPresetSlots[activeSkillPreset] = preferredSkillSlot;
        }
    }
    public int getActiveSkillPreset() { return activeSkillPreset; }
    public void setActiveSkillPreset(int v) {
        activeSkillPreset = Math.max(0, Math.min(2, v));
        preferredSkillSlot = skillPresetSlots[activeSkillPreset];
    }
    public int getSkillPresetSlot(int preset) {
        if (preset < 0 || preset >= skillPresetSlots.length) return 0;
        return skillPresetSlots[preset];
    }
    public int getAwakeningTrialTier() { return sequenceSection.getAwakeningTrialTier(); }
    public void setAwakeningTrialTier(int v) { sequenceSection.setAwakeningTrialTier(Math.max(0, Math.min(4, v))); }
    public boolean isAwakeningTrialActive() { return sequenceSection.isAwakeningTrialActive(); }
    public void setAwakeningTrialActive(boolean v) { sequenceSection.setAwakeningTrialActive(v); }
    public int getAwakeningTrialIndex() { return sequenceSection.getAwakeningTrialIndex(); }
    public void setAwakeningTrialIndex(int v) { sequenceSection.setAwakeningTrialIndex(v); }
    public int getAwakeningTrialKills() { return sequenceSection.getAwakeningTrialKills(); }
    public void setAwakeningTrialKills(int v) { sequenceSection.setAwakeningTrialKills(v); }
    public int getAwakeningTrialGoal() { return sequenceSection.getAwakeningTrialGoal(); }
    public void setAwakeningTrialGoal(int v) { sequenceSection.setAwakeningTrialGoal(v); }
    public long getAwakeningTrialDeadline() { return sequenceSection.getAwakeningTrialDeadline(); }
    public void setAwakeningTrialDeadline(long t) { sequenceSection.setAwakeningTrialDeadline(t); }
    public int getKingdomProsperity() { return kingdomProsperity; }
    public void addKingdomProsperity(int amount) { kingdomProsperity = Math.max(0, kingdomProsperity + amount); }
    public int getKingdomHappiness() { return kingdomHappiness; }
    public void setKingdomHappiness(int v) { kingdomHappiness = Math.max(0, Math.min(100, v)); }
    public void addKingdomHappiness(int v) { kingdomHappiness = Math.max(0, Math.min(100, kingdomHappiness + v)); }
    public int getKingdomStability() { return kingdomStability; }
    public void addKingdomStability(int v) { kingdomStability = Math.max(0, Math.min(100, kingdomStability + v)); }
    public int getKingdomReputation() { return kingdomReputation; }
    public void addKingdomReputation(int v) { kingdomReputation = Math.max(-100, Math.min(100, kingdomReputation + v)); }
    public int getBuildCreativity() { return buildCreativity; }
    public void addBuildCreativity(int v) { buildCreativity = Math.max(0, buildCreativity + v); }
    public String getKingdomDecree() { return kingdomDecree; }
    public void setKingdomDecree(String decree) { kingdomDecree = decree; }
    public List<ActiveDecree> getActiveDecrees() { return activeDecrees; }
    public void addActiveDecree(ActiveDecree d) { activeDecrees.add(d); }
    public void setActiveDecrees(List<ActiveDecree> list) {
        activeDecrees.clear();
        activeDecrees.addAll(list);
    }
    public List<PictureBookPage> getPictureBookPages() { return pictureBookPages; }
    public void addPictureBookPage(PictureBookPage page) { pictureBookPages.add(page); }
    public int getBuildingProgress(String id) { return buildingProgress.getOrDefault(id, 0); }
    public void setBuildingProgress(String id, int prog) { buildingProgress.put(id, Math.max(0, prog)); }
    public void addBuildingProgress(String id, int delta) {
        setBuildingProgress(id, getBuildingProgress(id) + delta);
    }
    public boolean isBuildingPlaced(String id) { return buildingsPlaced.contains(id); }
    public void setBuildingPlaced(String id) { buildingsPlaced.add(id); }
    public int getPlacedBuildingCount() { return buildingsPlaced.size(); }
    public int getIronpawForgeLevel() { return ironpawForgeLevel; }
    public void setIronpawForgeLevel(int level) { ironpawForgeLevel = Math.max(1, Math.min(3, level)); }
    public boolean hasPeaceScene(String id) { return peaceScenesSeen.contains(id); }
    public void markPeaceScene(String id) { peaceScenesSeen.add(id); }
    public java.util.Set<String> getPeaceSceneIds() { return java.util.Set.copyOf(peaceScenesSeen); }
    public int getRemnantBurned() { return remnantBurned; }
    public void addRemnantBurned(int count) { remnantBurned += Math.max(0, count); }
    public boolean isFallenVelvetRedeemed() { return fallenVelvetRedeemed; }
    public void setFallenVelvetRedeemed(boolean v) { fallenVelvetRedeemed = v; }
    public boolean isSanhuaEternalMaterials() { return sanhuaEternalMaterials; }
    public void setSanhuaEternalMaterials(boolean v) { sanhuaEternalMaterials = v; }
    public boolean hasCookingRecipe(String id) { return unlockedCookingRecipes.contains(id); }
    public void unlockCookingRecipe(String id) { unlockedCookingRecipes.add(id); }
    public Set<String> getUnlockedCookingRecipes() { return Set.copyOf(unlockedCookingRecipes); }
    public boolean hasNightSecret(String id) { return nightSecretsSeen.contains(id); }
    public void markNightSecret(String id) { nightSecretsSeen.add(id); }
    public int getNightSecretCount() { return nightSecretsSeen.size(); }
    public long getSystemScanUntil() { return systemScanUntil; }
    public void setSystemScanUntil(long t) { systemScanUntil = t; }
    public long getLastNightSecretDay() { return lastNightSecretDay; }
    public void setLastNightSecretDay(long d) { lastNightSecretDay = d; }
    public int getShadowCoins() { return shadowCoins; }
    public void addShadowCoins(int v) { shadowCoins = Math.max(0, shadowCoins + v); }
    public int getUndercatChapter() { return undercatChapter; }
    public void setUndercatChapter(int v) { undercatChapter = Math.max(0, v); }
    public int getUndercatStage() { return undercatStage; }
    public void setUndercatStage(int v) { undercatStage = Math.max(0, v); }
    public int getUndercatRep(com.cocojenna.undercat.UndercatFaction faction) {
        return undercatRep.getOrDefault(faction.name(), 0);
    }
    public void addUndercatRep(com.cocojenna.undercat.UndercatFaction faction, int v) {
        undercatRep.put(faction.name(), Math.max(-100, Math.min(100,
                getUndercatRep(faction) + v)));
    }
    public int getUndercatCommissions() { return undercatCommissions; }
    public void setUndercatCommissions(int v) { undercatCommissions = v; }
    public int getUndercatTrials() { return undercatTrials; }
    public void setUndercatTrials(int v) { undercatTrials = v; }
    public int getUndercatGladiators() { return undercatGladiators; }
    public void setUndercatGladiators(int v) { undercatGladiators = v; }
    public int getUndercatEnding() { return undercatEnding; }
    public void setUndercatEnding(int v) { undercatEnding = v; }
    public int getUndercatRegions() { return undercatRegions; }
    public void setUndercatRegions(int v) { undercatRegions = v; }
    public int getUndercatLeechKills() { return undercatLeechKills; }
    public void addUndercatLeechKills(int v) { undercatLeechKills += v; }

    public boolean hasSeenUndercatEntrance(com.cocojenna.undercat.UndercatEntrance entrance) {
        return (undercatEntranceSeen & entrance.flag()) != 0;
    }

    public void markUndercatEntranceSeen(com.cocojenna.undercat.UndercatEntrance entrance) {
        undercatEntranceSeen |= entrance.flag();
    }

    public int getCorrugataAffinity() { return corrugataAffinity; }
    public void setCorrugataAffinity(int v) { corrugataAffinity = v; }
    public int getOneEyeAffinity() { return oneEyeAffinity; }
    public void setOneEyeAffinity(int v) { oneEyeAffinity = v; }
    public int getUndercatSideFlags() { return undercatSideFlags; }
    public void setUndercatSideFlags(int v) { undercatSideFlags = v; }

    public long getUndercatDailyDay() { return undercatDailyDay; }
    public void setUndercatDailyDay(long d) { undercatDailyDay = d; }
    public int getUndercatDailyQuest() { return undercatDailyQuest; }
    public void setUndercatDailyQuest(int v) { undercatDailyQuest = v; }
    public boolean isUndercatDailyDone() { return undercatDailyDone; }
    public void setUndercatDailyDone(boolean v) { undercatDailyDone = v; }
    public int getArenaBetAmount() { return arenaBetAmount; }
    public void setArenaBetAmount(int v) { arenaBetAmount = v; }
    public int getCatnipPlantStreak() { return catnipPlantStreak; }
    public void setCatnipPlantStreak(int v) { catnipPlantStreak = v; }
    public long getPawStampCocoDay() { return pawStampCocoDay; }
    public void setPawStampCocoDay(long d) { pawStampCocoDay = d; }
    public long getPawStampJennaDay() { return pawStampJennaDay; }
    public void setPawStampJennaDay(long d) { pawStampJennaDay = d; }
    public int getGuardianDiscoveries() { return guardianDiscoveries; }
    public void setGuardianDiscoveries(int v) { guardianDiscoveries = v; }
    public void addGuardianDiscovery(int flag) { guardianDiscoveries |= flag; }
    public boolean hasGuardianDiscovery(int flag) { return (guardianDiscoveries & flag) != 0; }

    public boolean hasLore(int id) { return id >= 0 && id < 64 && (loreDiscovered & (1L << id)) != 0; }
    public void discoverLore(int id) { if (id >= 0 && id < 64) loreDiscovered |= (1L << id); }
    public int getLoreDiscoveryCount() {
        int n = 0;
        for (int i = 0; i < 64; i++) if (hasLore(i)) n++;
        return n;
    }

    public boolean hasWildCat(int id) { return id >= 0 && id < 15 && (wildCatsDiscovered & (1 << id)) != 0; }
    public void discoverWildCat(int id) { if (id >= 0 && id < 15) wildCatsDiscovered |= (1 << id); }
    public int getWildCatDiscoveryCount() {
        int n = 0;
        for (int i = 0; i < 15; i++) if (hasWildCat(i)) n++;
        return n;
    }

    public boolean hasLoreRegionComplete(int flag) { return (loreRegionsComplete & flag) != 0; }
    public void markLoreRegionComplete(int flag) { loreRegionsComplete |= flag; }

    public boolean hasDungeonCleared(int flag) { return (dungeonsCleared & flag) != 0; }
    public void markDungeonCleared(int flag) { dungeonsCleared |= flag; }

    public List<String> getExplorationJournal() { return Collections.unmodifiableList(explorationJournal); }
    public void addJournalEntry(String key) {
        explorationJournal.add(0, key);
        while (explorationJournal.size() > 32) explorationJournal.remove(explorationJournal.size() - 1);
    }
    public long getLastTapePrayerDay() { return lastTapePrayerDay; }
    public void setLastTapePrayerDay(long d) { lastTapePrayerDay = d; }

    public boolean hasReceivedGuardianGuide() { return receivedGuardianGuide; }
    public void setReceivedGuardianGuide(boolean v) { receivedGuardianGuide = v; }
    public long getCatKingdomEnterDay() { return catKingdomEnterDay; }
    public void setCatKingdomEnterDay(long d) { catKingdomEnterDay = d; }
    public long getLastCozyEventDay() { return lastCozyEventDay; }
    public void setLastCozyEventDay(long d) { lastCozyEventDay = d; }
    public int getCozyEventsToday() { return cozyEventsToday; }
    public void setCozyEventsToday(int v) { cozyEventsToday = v; }
    public long getGroomCooldownUntil() { return groomCooldownUntil; }
    public void setGroomCooldownUntil(long t) { groomCooldownUntil = t; }
    public long getNapCooldownUntil() { return napCooldownUntil; }
    public void setNapCooldownUntil(long t) { napCooldownUntil = t; }
    public int getVillagePopulation() { return villagePopulation; }
    public void setVillagePopulation(int v) { villagePopulation = Math.max(0, v); }
    public int getVillageHousingCapacity() { return villageHousingCapacity; }
    public void setVillageHousingCapacity(int v) { villageHousingCapacity = Math.max(0, v); }
    public void addVillageHousingCapacity(int v) { villageHousingCapacity = Math.max(0, villageHousingCapacity + v); }
    public int getVillageFoodStock() { return villageFoodStock; }
    public void setVillageFoodStock(int v) { villageFoodStock = Math.max(0, v); }
    public void addVillageFoodStock(int v) { setVillageFoodStock(villageFoodStock + v); }
    public int getVillageDefense() { return villageDefense; }
    public void addVillageDefense(int v) { villageDefense = Math.max(0, villageDefense + v); }
    public long getLastVillageTickDay() { return lastVillageTickDay; }
    public void setLastVillageTickDay(long d) { lastVillageTickDay = d; }

    public int getTownNpcFavor(String npcId) { return townNpcFavor.getOrDefault(npcId, 0); }
    public void addTownNpcFavor(String npcId, int v) {
        townNpcFavor.put(npcId, Math.max(0, Math.min(100, getTownNpcFavor(npcId) + v)));
    }
    public String getTownNpcJob(String npcId) { return townNpcJob.getOrDefault(npcId, ""); }
    public void setTownNpcJob(String npcId, String job) { townNpcJob.put(npcId, job); }
    public int getTownNpcStoryChapter(String npcId) { return townNpcStory.getOrDefault(npcId, 0); }
    public void setTownNpcStoryChapter(String npcId, int ch) { townNpcStory.put(npcId, ch); }
    public int getTownNpcDreamStage(String npcId) { return townNpcDreamStage.getOrDefault(npcId, 0); }
    public void setTownNpcDreamStage(String npcId, int stage) {
        townNpcDreamStage.put(npcId, Math.max(0, Math.min(3, stage)));
    }
    public long getKingdomMicroCooldown(long posKey) { return kingdomMicroCooldown.getOrDefault(posKey, 0L); }
    public void setKingdomMicroCooldown(long posKey, long tick) { kingdomMicroCooldown.put(posKey, tick); }
    public int getKingdomStrayCatsReturned() { return kingdomStrayCatsReturned; }
    public void addKingdomStrayCatsReturned(int n) { kingdomStrayCatsReturned += Math.max(0, n); }
    public long getLastStardustWishDay() { return lastStardustWishDay; }
    public void setLastStardustWishDay(long d) { lastStardustWishDay = d; }
    public boolean hasFamilyLifeEvent(String key) { return familyLifeEvents.contains(key); }
    public void markFamilyLifeEvent(String key) { familyLifeEvents.add(key); }
    public String getLastFamilyEvent() { return lastFamilyEvent; }
    public void setLastFamilyEvent(String ev) { lastFamilyEvent = ev == null ? "" : ev; }
    public long getLastVillageFestivalDay() { return lastVillageFestivalDay; }
    public void setLastVillageFestivalDay(long d) { lastVillageFestivalDay = d; }
    public String getActiveVillageFestival() { return activeVillageFestival; }
    public void setActiveVillageFestival(String id) { activeVillageFestival = id == null ? "" : id; }
    public boolean isTownNpcRecruited(String npcId) { return townNpcRecruited.contains(npcId); }
    public void recruitTownNpc(String npcId) { townNpcRecruited.add(npcId); }
    public Set<String> getTownNpcRecruited() { return townNpcRecruited; }
    public int getMpsDayIndex() { return mpsDayIndex; }
    public void setMpsDayIndex(int v) { mpsDayIndex = Math.max(0, Math.min(6, v)); }
    public String getMpsCell(int day, int block) {
        int idx = day * 4 + block;
        if (idx < 0 || idx >= mpsSchedule.length) return "";
        return mpsSchedule[idx] == null ? "" : mpsSchedule[idx];
    }
    public void setMpsCell(int day, int block, String taskId) {
        int idx = day * 4 + block;
        if (idx >= 0 && idx < mpsSchedule.length) mpsSchedule[idx] = taskId == null ? "" : taskId;
    }
    public int getFestivalPrepDay() { return festivalPrepDay; }
    public void setFestivalPrepDay(int v) { festivalPrepDay = Math.max(0, v); }
    public int getFestivalPrepProgress() { return festivalPrepProgress; }
    public void addFestivalPrepProgress(int v) {
        festivalPrepProgress = Math.max(0, Math.min(100, festivalPrepProgress + v));
    }
    public void resetFestivalPrepProgress() { festivalPrepProgress = 0; }
    public int getFestivalPhase() { return festivalPhase; }
    public void setFestivalPhase(int v) { festivalPhase = v; }
    public long getFestivalStartTick() { return festivalStartTick; }
    public void setFestivalStartTick(long t) { festivalStartTick = t; }
    public int getFestivalContestScore() { return festivalContestScore; }
    public void setFestivalContestScore(int v) { festivalContestScore = v; }
    public boolean isFestivalContestSubmitted() { return festivalContestSubmitted; }
    public void setFestivalContestSubmitted(boolean v) { festivalContestSubmitted = v; }
    public boolean isFestivalSetupHelped() { return festivalSetupHelped; }
    public void setFestivalSetupHelped(boolean v) { festivalSetupHelped = v; }
    public boolean isFestivalDanceDone() { return festivalDanceDone; }
    public void setFestivalDanceDone(boolean v) { festivalDanceDone = v; }
    public int getFestivalDanceScore() { return festivalDanceScore; }
    public void setFestivalDanceScore(int v) { festivalDanceScore = v; }
    public String getFestivalWish() { return festivalWish == null ? "" : festivalWish; }
    public void setFestivalWish(String v) { festivalWish = v == null ? "" : v; }
    public String getPendingFestivalWish() { return pendingFestivalWish == null ? "" : pendingFestivalWish; }
    public void setPendingFestivalWish(String v) { pendingFestivalWish = v == null ? "" : v; }
    public long getGrowthTickDay() { return growthTickDay; }
    public void setGrowthTickDay(long v) { growthTickDay = v; }
    public float getDailyEmotionGain() { return dailyEmotionGain; }
    public void setDailyEmotionGain(float v) { dailyEmotionGain = v; }
    public long getLastPetCocoDay() { return lastPetCocoDay; }
    public void setLastPetCocoDay(long v) { lastPetCocoDay = v; }
    public long getLastPetJennaDay() { return lastPetJennaDay; }
    public void setLastPetJennaDay(long v) { lastPetJennaDay = v; }
    public long getKingdomCalendarDay() { return kingdomCalendarDay; }
    public void setKingdomCalendarDay(long v) { kingdomCalendarDay = v; }
    public int getKingdomSeason() { return kingdomSeason; }
    public void setKingdomSeason(int v) { kingdomSeason = v; }
    public String getLastSeasonalFestival() { return lastSeasonalFestival; }
    public void setLastSeasonalFestival(String v) { lastSeasonalFestival = v == null ? "" : v; }
    public int getNpcFatigue() { return npcFatigue; }
    public void setNpcFatigue(int v) { npcFatigue = Math.max(0, Math.min(100, v)); }
    public void addNpcFatigue(int d) { setNpcFatigue(npcFatigue + d); }
    public int getWarehouseBonus() { return warehouseBonus; }
    public void setWarehouseBonus(int v) { warehouseBonus = Math.max(0, v); }
    public List<PictureBookPage> getLibraryShelfPages() { return libraryShelfPages; }
    public boolean isLibraryCurator() { return libraryCurator; }
    public void setLibraryCurator(boolean v) { libraryCurator = v; }
    public long getTwinBlessingLastEnact() { return twinBlessingLastEnact; }
    public void setTwinBlessingLastEnact(long t) { twinBlessingLastEnact = t; }
    public void setCocoAwakening(int v) { catBondSection.setCocoAwakening(Math.max(0, Math.min(50, v))); }
    public void setJennaAwakening(int v) { catBondSection.setJennaAwakening(Math.max(0, Math.min(50, v))); }

    public boolean isSwordBoneAwakened() { return swordBoneAwakened; }
    public void setSwordBoneAwakened(boolean v) { swordBoneAwakened = v; }
    public boolean isSwordBoneSupreme() { return swordBoneSupreme; }
    public void setSwordBoneSupreme(boolean v) { swordBoneSupreme = v; }
    public List<com.cocojenna.swordbone.SwordBoneEntry> getSwordBones() { return swordBones; }
    public void addSwordBone(com.cocojenna.swordbone.SwordBoneEntry e) { swordBones.add(e); }
    public void setSwordBoneAt(int i, com.cocojenna.swordbone.SwordBoneEntry e) { swordBones.set(i, e); }
    public void removeSwordBone(int i) { swordBones.remove(i); }
    public Set<String> getCollectedRyokatana() { return collectedRyokatana; }
    public void collectRyokatana(String shortId) { collectedRyokatana.add(shortId); }
    public boolean hasWeaponMemory(String id) { return unlockedWeaponMemories.contains(id); }
    public void unlockWeaponMemory(String id) { unlockedWeaponMemories.add(id); }
    public Set<String> getUnlockedWeaponMemories() { return unlockedWeaponMemories; }
    public long getSwordBoneDeathSaveCd() { return swordBoneDeathSaveCd; }
    public void setSwordBoneDeathSaveCd(long t) { swordBoneDeathSaveCd = t; }
    public long getSwordBoneResonanceCd() { return swordBoneResonanceCd; }
    public void setSwordBoneResonanceCd(long t) { swordBoneResonanceCd = t; }
    public long getSwordBoneResonanceUntil() { return swordBoneResonanceUntil; }
    public void setSwordBoneResonanceUntil(long t) { swordBoneResonanceUntil = t; }

    public boolean isArmorAwakened() { return armorAwakened; }
    public void setArmorAwakened(boolean v) { armorAwakened = v; }
    public long getArmorAwakenCd() { return armorAwakenCd; }
    public void setArmorAwakenCd(long t) { armorAwakenCd = t; }
    public int getArmorShieldCharge() { return armorShieldCharge; }
    public void setArmorShieldCharge(int v) { armorShieldCharge = Math.max(0, Math.min(100, v)); }
    public long getArmorShieldUntil() { return armorShieldUntil; }
    public void setArmorShieldUntil(long t) { armorShieldUntil = t; }
    public int getArmorMorphForm() { return armorMorphForm; }
    public void setArmorMorphForm(int v) { armorMorphForm = v & 1; }

    // ── 入門任務 ─────────────────────────────────────────────────────────
    public int getOnboardingQuestStep() { return catBondSection.getOnboardingQuestStep(); }
    public void setOnboardingQuestStep(int step) { catBondSection.setOnboardingQuestStep(Math.max(0, Math.min(7, step))); }
    public int getOnboardingWoodCollected() { return onboardingWoodCollected; }
    public void setOnboardingWoodCollected(int v) { onboardingWoodCollected = Math.max(0, v); }
    public int getOnboardingStoneCollected() { return onboardingStoneCollected; }
    public void setOnboardingStoneCollected(int v) { onboardingStoneCollected = Math.max(0, v); }

    public int getPenetrationQuestStage() { return penetrationQuestStage; }

    public String getActiveWeaponMemoryTaskId() { return activeWeaponMemoryTaskId; }
    public void setActiveWeaponMemoryTaskId(String id) { activeWeaponMemoryTaskId = id == null ? "" : id; }
    public int getWeaponMemoryTaskProgress() { return weaponMemoryTaskProgress; }
    public void setWeaponMemoryTaskProgress(int v) { weaponMemoryTaskProgress = Math.max(0, v); }

    public String getShadowClawEnding() { return shadowClawEnding; }
    public void setShadowClawEnding(String ending) { shadowClawEnding = ending == null ? "" : ending; }
    public void setPenetrationQuestStage(int v) { penetrationQuestStage = Math.max(0, Math.min(5, v)); }
    public int getGrayWhiskerFavor() { return grayWhiskerFavor; }
    public void addGrayWhiskerFavor(int d) { grayWhiskerFavor = Math.max(0, Math.min(100, grayWhiskerFavor + d)); }
    public int getOverworldInfluence() { return overworldInfluence; }
    public void addOverworldInfluence(int d) { overworldInfluence = Math.max(0, overworldInfluence + d); }
    public int getCatKingdomInfluence() { return catKingdomInfluence; }
    public void addCatKingdomInfluence(int d) { catKingdomInfluence = Math.max(0, catKingdomInfluence + d); }
    public int getMoonPawTrailCount() { return moonPawTrailCount; }
    public void incrementMoonPawTrail() { moonPawTrailCount++; }
    public int getCatLanguageLevel() { return catLanguageLevel; }
    public void incrementCatLanguageDialogue() {
        catLanguageLevel = Math.min(3, catLanguageLevel + 1);
    }
    public int getCatGraffitiRead() { return catGraffitiRead; }
    public void incrementCatGraffitiRead() { catGraffitiRead++; }
    public boolean isGrayWhiskerMet() { return grayWhiskerMet; }
    public void setGrayWhiskerMet(boolean v) { grayWhiskerMet = v; }

    public int getRuinMapFragmentBits() { return ruinMapFragmentBits; }
    public int getOverworldDungeonStage() { return overworldDungeonStage; }
    public void setOverworldDungeonStage(int stage) { overworldDungeonStage = Math.max(0, Math.min(4, stage)); }
    public boolean hasRuinMapFragment(com.cocojenna.overworld.RuinMapFragmentType type) {
        return (ruinMapFragmentBits & type.bit()) != 0;
    }
    public void collectRuinMapFragment(com.cocojenna.overworld.RuinMapFragmentType type) {
        ruinMapFragmentBits |= type.bit();
    }
    public int getFusionBuildingsPlaced() { return fusionBuildingsPlaced; }
    public void markFusionBuilding(com.cocojenna.overworld.FusionBuildingType type) {
        fusionBuildingsPlaced |= type.bit;
    }
    public int getMoonResonanceCount() { return moonResonanceCount; }
    public void incrementMoonResonanceCount() { moonResonanceCount++; }
    public int getCatSocietyInteractions() { return catSocietyInteractions; }
    public void addCatSocietyInteractions(int d) { catSocietyInteractions += d; }
    public int getCatSocietyPeakFavor() { return catSocietyPeakFavor; }
    public void setCatSocietyPeakFavor(int v) { catSocietyPeakFavor = Math.max(catSocietyPeakFavor, v); }
    public int getCatnipTradedTotal() { return catnipTradedTotal; }
    public void addCatnipTraded(int n) { catnipTradedTotal += n; }
    public long getLastEmbassyTeleportTick() { return lastEmbassyTeleportTick; }
    public void setLastEmbassyTeleportTick(long t) { lastEmbassyTeleportTick = t; }
    public long getLastThroneBlessingTick() { return lastThroneBlessingTick; }
    public void setLastThroneBlessingTick(long t) { lastThroneBlessingTick = t; }
    public int getOverworldNpcFavor(String key) { return overworldNpcFavor.getOrDefault(key, 0); }
    public void setOverworldNpcFavor(String key, int v) {
        overworldNpcFavor.put(key, Math.max(0, Math.min(100, v)));
    }
    public boolean hasOverworldSoulCompanion(String key) { return overworldSoulCompanions.contains(key); }
    public void markOverworldSoulCompanion(String key) { overworldSoulCompanions.add(key); }
    public int getTownNpcRomanceStage(String npcId) { return townNpcRomanceStage.getOrDefault(npcId, 0); }
    public void setTownNpcRomanceStage(String npcId, int stage) {
        townNpcRomanceStage.put(npcId, Math.max(0, Math.min(6, stage)));
    }
    public String getMarriagePartnerNpcId() { return marriagePartnerNpcId == null ? "" : marriagePartnerNpcId; }
    public void setMarriagePartnerNpcId(String id) { marriagePartnerNpcId = id == null ? "" : id; }
    public long getEngagementDay() { return engagementDay; }
    public void setEngagementDay(long d) { engagementDay = d; }
    public long getWeddingScheduledDay() { return weddingScheduledDay; }
    public void setWeddingScheduledDay(long d) { weddingScheduledDay = d; }
    public long getPregnancyDueDay() { return pregnancyDueDay; }
    public void setPregnancyDueDay(long d) { pregnancyDueDay = d; }
    public int getKittenCount() { return kittenCount; }
    public void setKittenCount(int v) { kittenCount = Math.max(0, v); }
    public long getMoonCoreBlessingUntil() { return moonCoreBlessingUntil; }
    public void setMoonCoreBlessingUntil(long t) { moonCoreBlessingUntil = t; }
    public int getOverworldRuinCorrosion() { return overworldRuinCorrosion; }
    public void setOverworldRuinCorrosion(int v) { overworldRuinCorrosion = Math.max(0, Math.min(100, v)); }
    public void addOverworldRuinCorrosion(int d) { setOverworldRuinCorrosion(overworldRuinCorrosion + d); }
    public long getLastTheaterPerformanceTick() { return lastTheaterPerformanceTick; }
    public void setLastTheaterPerformanceTick(long t) { lastTheaterPerformanceTick = t; }
    public long getLastTheaterGatheringDay() { return lastTheaterGatheringDay; }
    public void setLastTheaterGatheringDay(long d) { lastTheaterGatheringDay = d; }
    public long getLastFamilyGatheringDay() { return lastFamilyGatheringDay; }
    public void setLastFamilyGatheringDay(long d) { lastFamilyGatheringDay = d; }
    public boolean isCatFamiliesSeeded() { return catFamiliesSeeded; }
    public void setCatFamiliesSeeded(boolean v) { catFamiliesSeeded = v; }
    public String getTownNpcFamily(String npcId) { return townNpcFamily.getOrDefault(npcId, ""); }
    public void setTownNpcFamily(String npcId, String familyId) { townNpcFamily.put(npcId, familyId); }
    public String getTownNpcFamilyRole(String npcId) { return townNpcFamilyRole.getOrDefault(npcId, ""); }
    public void setTownNpcFamilyRole(String npcId, String role) { townNpcFamilyRole.put(npcId, role == null ? "" : role); }

    public boolean isCaravanEscortActive() { return caravanEscortActive; }
    public void setCaravanEscortActive(boolean v) { caravanEscortActive = v; }
    public int getCaravanStartX() { return caravanStartX; }
    public int getCaravanStartZ() { return caravanStartZ; }
    public int getCaravanDestX() { return caravanDestX; }
    public int getCaravanDestZ() { return caravanDestZ; }
    public void setCaravanRoute(int startX, int startZ, int destX, int destZ) {
        caravanStartX = startX;
        caravanStartZ = startZ;
        caravanDestX = destX;
        caravanDestZ = destZ;
        caravanEscortActive = true;
    }
    public void clearCaravanEscort() { caravanEscortActive = false; }
    public long getLastBlackMudLeakTick() { return lastBlackMudLeakTick; }
    public void setLastBlackMudLeakTick(long tick) { lastBlackMudLeakTick = tick; }

    // ── Capability 分區同步 API ───────────────────────────────────────────
    public long getUnlockedSequencesRaw() { return sequenceSection.getUnlockedSequences(); }
    public void setUnlockedSequencesRaw(long mask) { sequenceSection.setUnlockedSequences(mask); }

    public void setCocoProtectiveness(float v) { catBondSection.setCocoProtectiveness(Math.max(0, Math.min(100, v))); }
    public void setCocoMoonAffinity(float v) { catBondSection.setCocoMoonAffinity(Math.max(0, Math.min(100, v))); }
    public void setCocoAttachment(float v) { catBondSection.setCocoAttachment(Math.max(0, Math.min(100, v))); }
    public void setCocoSunbathing(float v) { catBondSection.setCocoSunbathing(Math.max(0, Math.min(100, v))); }
    public void setJennaPlayfulness(float v) { catBondSection.setJennaPlayfulness(Math.max(0, Math.min(100, v))); }
    public void setJennaCuriosity(float v) { catBondSection.setJennaCuriosity(Math.max(0, Math.min(100, v))); }
    public void setJennaContentment(float v) { catBondSection.setJennaContentment(Math.max(0, Math.min(100, v))); }
    public void setPromotionCardCount(int v) { sequenceSection.setPromotionCardCount(Math.max(0, v)); }
    public void setPromotionCardBonus(float v) { sequenceSection.setPromotionCardBonus(Math.max(0, v)); }
    public void setMemoryShardsTotal(int v) { sequenceSection.setMemoryShardsTotal(Math.max(0, v)); }
    public void replaceOwnedPromotionCards(java.util.List<String> cards) {
        sequenceSection.replaceOwnedPromotionCards(cards);
    }

    public void setKingdomProsperity(int v) { kingdomProsperity = Math.max(0, v); }
    public void setKingdomStabilityDirect(int v) { kingdomStability = Math.max(0, Math.min(100, v)); }
    public void setKingdomReputationDirect(int v) { kingdomReputation = Math.max(-100, Math.min(100, v)); }
    public void setBuildCreativityDirect(int v) { buildCreativity = Math.max(0, v); }
    public void setRemnantBurned(int v) { remnantBurned = Math.max(0, v); }
    public void setShadowCoins(int v) { shadowCoins = Math.max(0, v); }

    public java.util.Set<String> getPurchasedRepOfferIds() {
        return java.util.Collections.unmodifiableSet(purchasedRepOffers);
    }
    public void replacePurchasedRepOffers(java.util.Set<String> ids) {
        purchasedRepOffers.clear();
        purchasedRepOffers.addAll(ids);
    }
    public java.util.Map<String, Integer> snapshotBuildingProgress() {
        return new HashMap<>(buildingProgress);
    }
    public java.util.Set<String> snapshotBuildingsPlaced() {
        return new java.util.HashSet<>(buildingsPlaced);
    }
    public void replaceBuildingProgress(java.util.Map<String, Integer> map) {
        buildingProgress.clear();
        buildingProgress.putAll(map);
    }
    public void replaceBuildingsPlaced(java.util.Set<String> ids) {
        buildingsPlaced.clear();
        buildingsPlaced.addAll(ids);
    }

    public CompoundTag serializeCatBondSection() {
        return catBondSection.serialize();
    }

    public CompoundTag serializeSequenceSection() {
        return sequenceSection.serialize();
    }

    public CompoundTag serializeKingdomSection() {
        KingdomProgressCapability cap = new KingdomProgressCapability();
        cap.copyFrom(this);
        return cap.serialize();
    }

    public void deserializeCatBondSection(CompoundTag tag) {
        catBondSection.deserialize(tag);
    }

    public void deserializeSequenceSection(CompoundTag tag) {
        sequenceSection.deserialize(tag);
    }

    public void deserializeKingdomSection(CompoundTag tag) {
        KingdomProgressCapability cap = new KingdomProgressCapability();
        cap.deserialize(tag);
        cap.applyTo(this);
    }

    public KingdomMultiplayerCapability getMultiplayerSection() { return multiplayerSection; }

    public float getPersonalCocoAffection(java.util.UUID id) {
        return catBondSection.getPersonalCocoAffection(id);
    }

    public float getPersonalJennaAffection(java.util.UUID id) {
        return catBondSection.getPersonalJennaAffection(id);
    }

    public void addPersonalCocoAffection(java.util.UUID id, float delta) {
        catBondSection.addPersonalCocoAffection(id, delta);
    }

    public void addPersonalJennaAffection(java.util.UUID id, float delta) {
        catBondSection.addPersonalJennaAffection(id, delta);
    }

    public CompoundTag serializeMultiplayerSection() {
        return multiplayerSection.serialize();
    }

    public void deserializeMultiplayerSection(CompoundTag tag) {
        multiplayerSection.deserialize(tag);
    }
}
