package com.cocojenna.society;

import com.cocojenna.capability.BondData;
import com.cocojenna.endgame.kingdom.MpsTask;
import com.cocojenna.endgame.kingdom.TownJobRank;
import com.cocojenna.endgame.kingdom.TownNpcProfile;

import java.util.*;

/** 職業 ↔ 村莊建築 ↔ MPS 任務雙向綁定. */
public final class ProfessionBuildingBinder {

    public record Binding(String buildingId, MpsTask[] boostedTasks, String[] professions) {}

    private static final Map<String, Binding> BUILDINGS = new LinkedHashMap<>();
    private static final Map<String, MpsTask[]> PROFESSION_TASKS = new HashMap<>();
    private static final Map<MpsTask, String> TASK_BUILDING = new HashMap<>();

    static {
        regBuilding("cat_library", new MpsTask[]{MpsTask.GATHER_MOONSTONE, MpsTask.BUILD_LIGHT},
                "scholar", "scribe", "alchemist", "astrologer");
        regBuilding("open_air_theater", new MpsTask[]{MpsTask.MAKE_WREATH, MpsTask.DECORATE},
                "performer", "bard");
        regBuilding("festival_stage", new MpsTask[]{MpsTask.BUILD_STAGE, MpsTask.DECORATE, MpsTask.MAKE_WREATH},
                "performer", "architect", "craftsman");
        regBuilding("market_square", new MpsTask[]{MpsTask.PROCESS_WOOD, MpsTask.WEAVE_CARPET},
                "merchant", "weaver", "craftsman");
        regBuilding("hot_spring", new MpsTask[]{MpsTask.GATHER_FUR, MpsTask.FISH_NIGHT},
                "fisher", "gardener", "beekeeper");
        regBuilding("cat_school", new MpsTask[]{MpsTask.GATHER_MOONSTONE, MpsTask.COOK_PREP},
                "scholar", "chef", "scribe");
        regBuilding("ironpaw_forge_upgrade", new MpsTask[]{MpsTask.PROCESS_WOOD, MpsTask.GATHER_WOOD},
                "craftsman", "scrapper", "architect");
        regBuilding("puree_fountain", new MpsTask[]{MpsTask.GATHER_NEON, MpsTask.GATHER_FUR},
                "herbalist", "alchemist", "gardener");
        regBuilding("small_cat_house", new MpsTask[]{MpsTask.GATHER_FUR, MpsTask.WEAVE_CARPET},
                "weaver", "gardener", "beekeeper");

        PROFESSION_TASKS.put("fisher", new MpsTask[]{MpsTask.FISH_NIGHT, MpsTask.GATHER_FUR});
        PROFESSION_TASKS.put("gardener", new MpsTask[]{MpsTask.GATHER_FUR, MpsTask.GATHER_NEON});
        PROFESSION_TASKS.put("chef", new MpsTask[]{MpsTask.COOK_PREP, MpsTask.GATHER_FUR});
        PROFESSION_TASKS.put("craftsman", new MpsTask[]{MpsTask.GATHER_WOOD, MpsTask.PROCESS_WOOD});
        PROFESSION_TASKS.put("merchant", new MpsTask[]{MpsTask.PROCESS_WOOD, MpsTask.WEAVE_CARPET});
        PROFESSION_TASKS.put("scholar", new MpsTask[]{MpsTask.GATHER_MOONSTONE, MpsTask.BUILD_LIGHT});
        PROFESSION_TASKS.put("performer", new MpsTask[]{MpsTask.MAKE_WREATH, MpsTask.DECORATE});
        PROFESSION_TASKS.put("guard", new MpsTask[]{MpsTask.BUILD_LIGHT, MpsTask.GATHER_WOOD});
        PROFESSION_TASKS.put("architect", new MpsTask[]{MpsTask.BUILD_STAGE, MpsTask.BUILD_LIGHT});
        PROFESSION_TASKS.put("herbalist", new MpsTask[]{MpsTask.GATHER_NEON, MpsTask.GATHER_FUR});
        PROFESSION_TASKS.put("weaver", new MpsTask[]{MpsTask.WEAVE_CARPET, MpsTask.GATHER_FUR});
        PROFESSION_TASKS.put("alchemist", new MpsTask[]{MpsTask.GATHER_NEON, MpsTask.GATHER_MOONSTONE});
        PROFESSION_TASKS.put("astrologer", new MpsTask[]{MpsTask.GATHER_MOONSTONE, MpsTask.BUILD_LIGHT});
        PROFESSION_TASKS.put("beekeeper", new MpsTask[]{MpsTask.GATHER_FUR, MpsTask.MAKE_WREATH});
        PROFESSION_TASKS.put("bard", new MpsTask[]{MpsTask.MAKE_WREATH, MpsTask.DECORATE});
        PROFESSION_TASKS.put("scribe", new MpsTask[]{MpsTask.GATHER_MOONSTONE, MpsTask.BUILD_LIGHT});
        PROFESSION_TASKS.put("scout", new MpsTask[]{MpsTask.GATHER_WOOD, MpsTask.FISH_NIGHT});
        PROFESSION_TASKS.put("scrapper", new MpsTask[]{MpsTask.GATHER_WOOD, MpsTask.PROCESS_WOOD});
    }

    private ProfessionBuildingBinder() {}

    private static void regBuilding(String id, MpsTask[] tasks, String... professions) {
        BUILDINGS.put(id, new Binding(id, tasks, professions));
        for (MpsTask t : tasks) {
            TASK_BUILDING.putIfAbsent(t, id);
        }
    }

    public static float buildingTaskMultiplier(BondData bond, MpsTask task) {
        float mult = 1f;
        for (Binding b : BUILDINGS.values()) {
            if (!bond.isBuildingPlaced(b.buildingId())) continue;
            for (MpsTask t : b.boostedTasks()) {
                if (t == task) mult += 0.2f;
            }
        }
        return mult;
    }

    public static int countWorkersForTask(BondData bond, MpsTask task) {
        TownJobRank needed = taskJobRank(task);
        if (needed == null) return 0;
        int workers = 0;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (!isActiveNpc(bond, p)) continue;
            String job = bond.getTownNpcJob(p.id());
            if (job.isEmpty()) job = p.defaultJob().name();
            if (needed.name().equals(job)) {
                workers++;
                continue;
            }
            MpsTask[] prefs = PROFESSION_TASKS.get(job.toLowerCase());
            if (prefs != null && Arrays.asList(prefs).contains(task)) workers++;
        }
        return workers;
    }

    public static String primaryBuildingForTask(MpsTask task) {
        return TASK_BUILDING.getOrDefault(task, "");
    }

    public static String[][] generateMpsPreset(BondData bond) {
        String[][] base = {
                {"gather_wood", "process_wood", "rest", "gather_moonstone"},
                {"gather_fur", "weave_carpet", "fish_night", "gather_neon"},
                {"build_stage", "build_light", "rest", "gather_neon"},
                {"make_wreath", "build_light", "decorate", "gather_moonstone"},
                {"decorate", "build_stage", "rest", "gather_fur"},
                {"cook_prep", "cook_prep", "decorate", "rest"},
                {"festival", "festival", "festival", "rest"},
        };
        if (bond.isBuildingPlaced("cat_library")) {
            base[0][3] = "gather_moonstone";
            base[3][3] = "build_light";
        }
        if (bond.isBuildingPlaced("open_air_theater") || bond.isBuildingPlaced("festival_stage")) {
            base[2][0] = "build_stage";
            base[4][0] = "decorate";
            base[5][2] = "make_wreath";
        }
        if (bond.isBuildingPlaced("hot_spring") || bond.isBuildingPlaced("market_square")) {
            base[1][2] = "fish_night";
            base[1][0] = "weave_carpet";
        }
        if (bond.isBuildingPlaced("puree_fountain")) {
            base[1][3] = "gather_neon";
            base[2][3] = "gather_neon";
        }
        tailorForRecruits(bond, base);
        return base;
    }

    private static void tailorForRecruits(BondData bond, String[][] preset) {
        int slot = 0;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (!isActiveNpc(bond, p)) continue;
            String job = bond.getTownNpcJob(p.id());
            if (job.isEmpty()) job = p.defaultJob().name();
            MpsTask[] prefs = PROFESSION_TASKS.get(job.toLowerCase());
            if (prefs == null) {
                for (CatProfessionRegistry.Profession prof : CatProfessionRegistry.all()) {
                    if (prof.job().name().equals(job)) {
                        prefs = PROFESSION_TASKS.get(prof.id());
                        break;
                    }
                }
            }
            if (prefs == null || prefs.length == 0) continue;
            preset[slot % 6][slot % 4] = prefs[0].id;
            slot++;
        }
    }

    private static boolean isActiveNpc(BondData bond, TownNpcProfile p) {
        return bond.isTownNpcRecruited(p.id());
    }

    private static TownJobRank taskJobRank(MpsTask task) {
        return switch (task) {
            case GATHER_WOOD, PROCESS_WOOD, WEAVE_CARPET, BUILD_LIGHT -> TownJobRank.CRAFTSMAN;
            case GATHER_MOONSTONE -> TownJobRank.SCHOLAR;
            case GATHER_FUR, GATHER_NEON -> TownJobRank.GARDENER;
            case FISH_NIGHT -> TownJobRank.FISHER;
            case BUILD_STAGE -> TownJobRank.ARCHITECT;
            case MAKE_WREATH, DECORATE -> TownJobRank.PERFORMER;
            case COOK_PREP -> TownJobRank.CHEF;
            default -> null;
        };
    }

    public static List<String> unlockedProfessions(BondData bond) {
        List<String> out = new ArrayList<>();
        for (Binding b : BUILDINGS.values()) {
            if (!bond.isBuildingPlaced(b.buildingId())) continue;
            out.addAll(Arrays.asList(b.professions()));
        }
        return out;
    }
}
