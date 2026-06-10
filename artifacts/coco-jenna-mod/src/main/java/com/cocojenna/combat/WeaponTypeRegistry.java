package com.cocojenna.combat;

import com.cocojenna.swordbone.SwordBoneManager;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class WeaponTypeRegistry {

    private static final Map<String, WeaponType> BY_WEAPON = new HashMap<>();

    static {
        put("mimic_disguise", WeaponType.MORPH);
        put("cheshire_grin", WeaponType.MORPH);
        put("screen_noise", WeaponType.MORPH);
        put("origami_cut", WeaponType.MORPH);
        put("paper_crow_ink", WeaponType.MORPH);

        put("gear_windup", WeaponType.RHYTHM);
        put("gear_schedule", WeaponType.RHYTHM);
        put("gear_precision_2", WeaponType.RHYTHM);
        put("precision_gear", WeaponType.RHYTHM);
        put("copper_bell_soul", WeaponType.RHYTHM);
        put("iron_claw_apprentice", WeaponType.RHYTHM);

        put("fallen_velvet_claw", WeaponType.CURSE);
        put("whisper_mud", WeaponType.CURSE);
        put("dark_tide", WeaponType.CURSE);
        put("lament_split", WeaponType.CURSE);
        put("hibiscus_blood", WeaponType.CURSE);
        put("memory_worm", WeaponType.CURSE);

        put("fish_bone_tide", WeaponType.ENVIRONMENT);
        put("deep_sea_current", WeaponType.ENVIRONMENT);
        put("stardust_tread", WeaponType.ENVIRONMENT);
        put("neon_flash", WeaponType.ENVIRONMENT);
        put("moonlight_ripple", WeaponType.ENVIRONMENT);
        put("blind_water_core", WeaponType.ENVIRONMENT);
        put("blind_water_abyss", WeaponType.ENVIRONMENT);
    }

    private WeaponTypeRegistry() {}

    private static void put(String ryokatanaSuffix, WeaponType type) {
        BY_WEAPON.put("ryokatana_" + ryokatanaSuffix, type);
    }

    public static WeaponType of(ItemStack stack) {
        String id = SwordBoneManager.weaponId(stack);
        if (id.isEmpty()) return null;
        return BY_WEAPON.get(id);
    }
}
