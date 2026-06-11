package com.cocojenna.weapon;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.dialogue.DialogueScripts;
import net.minecraft.server.level.ServerPlayer;

/** 五條核心記憶任務完成時的劇情演出. */
public final class WeaponMemoryCinematicManager {

    private WeaponMemoryCinematicManager() {}

    private static final java.util.Map<String, String> NAMED_SCENES = buildNamedScenes();

    private static java.util.Map<String, String> buildNamedScenes() {
        var map = new java.util.HashMap<String, String>();
        map.put("fish_bone_tide_01", "memory_cinematic_fish_bone");
        map.put("iron_rust_01", "memory_cinematic_iron_rust");
        map.put("hibiscus_01", "memory_cinematic_hibiscus");
        map.put("moonlight_01", "memory_cinematic_moonlight");
        map.put("paper_crow_01", "memory_cinematic_paper_crow");
        map.put("tide_pact", "memory_cinematic_deep_sea_current");
        map.put("last_armor", "memory_cinematic_iron_rust_legion");
        map.put("red_paper_blood", "memory_cinematic_sanhua_thread");
        map.put("moon_mirror", "memory_cinematic_moonlight_clear");
        map.put("unsent_letter", "memory_cinematic_forgotten_page");
        map.put("neon_flash_01", "memory_cinematic_neon_flash");
        map.put("velvet_warmth_01", "memory_cinematic_velvet_warmth");
        map.put("blind_core_01", "memory_cinematic_blind_water_core");
        map.put("stardust_01", "memory_cinematic_stardust_step");
        map.put("first_cry_01", "memory_cinematic_first_cry_beginner");
        map.put("cheshire_01", "memory_cinematic_cheshire_grin");
        map.put("dark_tide_01", "memory_cinematic_dark_tide");
        map.put("iron_claw_01", "memory_cinematic_iron_claw_apprentice");
        map.put("calico_01", "memory_cinematic_calico_warmth");
        map.put("velvet_cradle_01", "memory_cinematic_velvet_cradle");
        map.put("daikata_tiger_iron", "memory_cinematic_tiger_iron");
        map.put("daikata_wind_cut", "memory_cinematic_wind_cut");
        map.put("daikata_crescent", "memory_cinematic_crescent");
        map.put("daikata_moon_verdict", "memory_cinematic_moon_verdict");
        map.put("daikata_royal_authority", "memory_cinematic_royal_authority");
        return map;
    }

    public static void onTaskComplete(ServerPlayer player, WeaponMemoryTaskRegistry.MemoryTask task) {
        String scene = NAMED_SCENES.get(task.id());
        if (scene == null) {
            scene = "memory_cinematic_" + task.weaponVariant();
        }
        if (DialogueScripts.get(scene) != null) {
            DialogueManager.play(player, scene);
        } else {
            DialogueManager.playMemoryShard(player, "weapon.memory.cocojenna." + task.weaponVariant());
        }
    }
}
