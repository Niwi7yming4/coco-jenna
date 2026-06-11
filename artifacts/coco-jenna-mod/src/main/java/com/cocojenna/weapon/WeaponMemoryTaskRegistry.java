package com.cocojenna.weapon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** 武器記憶任務定義（設計書第六卷代表任務）. */
public final class WeaponMemoryTaskRegistry {

    public record MemoryTask(
            String id,
            String weaponVariant,
            String descriptionKey,
            int goal,
            TaskType type
    ) {}

    public enum TaskType {
        KILL_BLACK_MUD,
        FISH_IN_BLIND_WATER,
        COLLECT_HIBISCUS,
        FULL_MOON_ALTAR,
        DELIVER_PAPER,
        TIDE_PACT,
        LAST_ARMOR,
        RED_PAPER_BLOOD,
        MOON_MIRROR_LAKE,
        UNSENT_LETTER,
        BOSS_KILL,
        DUNGEON_CLEAR,
        WILDCAT_BEFRIEND,
        MAUSOLEUM_VISIT,
        RUIN_LECTERN,
        REGION_LORE,
        NPC_DREAM,
        UNDERCAT_COMMISSION,
        ARENA_WIN,
        TRIAL_COMPLETE
    }

    private static final Map<String, MemoryTask> BY_WEAPON = new HashMap<>();
    private static final Map<String, MemoryTask> BY_ID = new HashMap<>();

    static {
        register(new MemoryTask("fish_bone_tide_01", "fish_bone_tide",
                "weapon.task.cocojenna.fish_bone", 5, TaskType.FISH_IN_BLIND_WATER));
        register(new MemoryTask("iron_rust_01", "iron_rust_armor_break",
                "weapon.task.cocojenna.iron_rust", 8, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("hibiscus_01", "hibiscus_blood",
                "weapon.task.cocojenna.hibiscus", 3, TaskType.COLLECT_HIBISCUS));
        register(new MemoryTask("moonlight_01", "moonlight_ripple",
                "weapon.task.cocojenna.moonlight", 1, TaskType.FULL_MOON_ALTAR));
        register(new MemoryTask("paper_crow_01", "paper_crow_ink",
                "weapon.task.cocojenna.paper_crow", 5, TaskType.DELIVER_PAPER));
        register(new MemoryTask("tide_pact", "deep_sea_current",
                "weapon.task.cocojenna.tide_pact", 3, TaskType.TIDE_PACT));
        register(new MemoryTask("last_armor", "iron_rust_legion",
                "weapon.task.cocojenna.last_armor", 1, TaskType.LAST_ARMOR));
        register(new MemoryTask("red_paper_blood", "sanhua_thread",
                "weapon.task.cocojenna.red_paper_blood", 10, TaskType.RED_PAPER_BLOOD));
        register(new MemoryTask("moon_mirror", "moonlight_clear",
                "weapon.task.cocojenna.moon_mirror", 1, TaskType.MOON_MIRROR_LAKE));
        register(new MemoryTask("unsent_letter", "forgotten_page",
                "weapon.task.cocojenna.unsent_letter", 1, TaskType.UNSENT_LETTER));
        register(new MemoryTask("royal_glory_trial", "royal_glory",
                "weapon.task.cocojenna.royal_glory", 1, TaskType.BOSS_KILL));
        register(new MemoryTask("fallen_velvet_trial", "fallen_velvet_claw",
                "weapon.task.cocojenna.fallen_velvet", 1, TaskType.DUNGEON_CLEAR));
        register(new MemoryTask("gear_schedule_trial", "gear_schedule",
                "weapon.task.cocojenna.gear_schedule", 3, TaskType.RUIN_LECTERN));
        register(new MemoryTask("whisper_mud_trial", "whisper_mud",
                "weapon.task.cocojenna.whisper_mud", 5, TaskType.MAUSOLEUM_VISIT));
        register(new MemoryTask("deep_sea_trial", "deep_sea_current",
                "weapon.task.cocojenna.deep_sea", 1, TaskType.WILDCAT_BEFRIEND));
        register(new MemoryTask("sanhua_trial", "sanhua_thread",
                "weapon.task.cocojenna.sanhua", 17, TaskType.REGION_LORE));
        register(new MemoryTask("moon_ripple_trial", "moonlight_ripple",
                "weapon.task.cocojenna.moon_ripple", 1, TaskType.NPC_DREAM));
        register(new MemoryTask("workshop_trial", "sanhua_thread",
                "weapon.task.cocojenna.workshop", 3, TaskType.UNDERCAT_COMMISSION));
        register(new MemoryTask("storm_trial", "storm_umbrella",
                "weapon.task.cocojenna.storm", 3, TaskType.ARENA_WIN));
        register(new MemoryTask("velvet_sentinel_trial", "velvet_whisper",
                "weapon.task.cocojenna.velvet_sentinel", 1, TaskType.TRIAL_COMPLETE));
        register(new MemoryTask("neon_flash_01", "neon_flash",
                "weapon.task.cocojenna.neon_flash", 6, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("velvet_warmth_01", "velvet_warmth",
                "weapon.task.cocojenna.velvet_warmth", 4, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("blind_core_01", "blind_water_core",
                "weapon.task.cocojenna.blind_core", 3, TaskType.FISH_IN_BLIND_WATER));
        register(new MemoryTask("stardust_01", "stardust_step",
                "weapon.task.cocojenna.stardust", 1, TaskType.FULL_MOON_ALTAR));
        register(new MemoryTask("first_cry_01", "first_cry_beginner",
                "weapon.task.cocojenna.first_cry", 3, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("cheshire_01", "cheshire_grin",
                "weapon.task.cocojenna.cheshire", 5, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("dark_tide_01", "dark_tide",
                "weapon.task.cocojenna.dark_tide", 8, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("iron_claw_01", "iron_claw_apprentice",
                "weapon.task.cocojenna.iron_claw", 5, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("calico_01", "calico_warmth",
                "weapon.task.cocojenna.calico", 4, TaskType.KILL_BLACK_MUD));
        register(new MemoryTask("velvet_cradle_01", "velvet_cradle",
                "weapon.task.cocojenna.velvet_cradle", 4, TaskType.KILL_BLACK_MUD));
        // 21 把大快刀通用記憶試煉
        String[] daikataVariants = {
                "tiger_iron", "wind_cut", "phantom", "suppression", "shockwave",
                "crescent", "moon_verdict", "star_map", "abyss", "neon_dance",
                "gear_king", "hibiscus_ultimate", "howling_gorge", "first_dawn",
                "royal_authority", "shadow_imitation", "silent_guard", "dusk_end",
                "white_glove", "forgotten_tower", "village_soul", "storm_umbrella"
        };
        for (String v : daikataVariants) {
            register(new MemoryTask("daikata_" + v, v,
                    "weapon.task.cocojenna.generic_daikata", 5, TaskType.KILL_BLACK_MUD));
        }
        for (String variant : com.cocojenna.item.RyokatanaRegistry.all().keySet()) {
            if (!BY_WEAPON.containsKey(variant)) {
                register(new MemoryTask(variant + "_memory", variant,
                        "weapon.task.cocojenna.generic_ryokatana", 5, TaskType.KILL_BLACK_MUD));
            }
        }
    }

    private WeaponMemoryTaskRegistry() {}

    private static void register(MemoryTask task) {
        BY_WEAPON.put(task.weaponVariant(), task);
        BY_ID.put(task.id(), task);
    }

    public static Optional<MemoryTask> forWeapon(String variantId) {
        return Optional.ofNullable(BY_WEAPON.get(variantId));
    }

    public static Optional<MemoryTask> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static java.util.Collection<MemoryTask> allTasks() {
        return BY_ID.values();
    }
}
