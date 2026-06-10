package com.cocojenna.weapon;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.dialogue.DialogueScripts;
import net.minecraft.server.level.ServerPlayer;

/** 五條核心記憶任務完成時的劇情演出. */
public final class WeaponMemoryCinematicManager {

    private WeaponMemoryCinematicManager() {}

    private static final java.util.Map<String, String> NAMED_SCENES = java.util.Map.of(
            "fish_bone_tide_01", "memory_cinematic_fish_bone",
            "iron_rust_01", "memory_cinematic_iron_rust",
            "hibiscus_01", "memory_cinematic_hibiscus",
            "moonlight_01", "memory_cinematic_moonlight",
            "paper_crow_01", "memory_cinematic_paper_crow"
    );

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
