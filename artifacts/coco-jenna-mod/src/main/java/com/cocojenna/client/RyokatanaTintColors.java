package com.cocojenna.client;

import java.util.LinkedHashMap;
import java.util.Map;

/** 良快刀佔位染色 — 鐵劍模型 × 主題色，待正式貼圖後可移除. */
public final class RyokatanaTintColors {

    private static final Map<String, Integer> BY_VARIANT = new LinkedHashMap<>();

    static {
        put("fish_bone_tide", 0xFF4A90D9);
        put("copper_bell_soul", 0xFFB87333);
        put("iron_rust_armor_break", 0xFF8B4513);
        put("origami_cut", 0xFFF0E6D2);
        put("jellyfish_bind", 0xFF9B59B6);
        put("screen_noise", 0xFF2ECC71);
        put("precision_gear", 0xFF7F8C8D);
        put("moth_scale", 0xFFD4A574);
        put("blind_water_abyss", 0xFF1A5276);
        put("lament_split", 0xFF6C3483);
        put("fallen_velvet_claw", 0xFF8E44AD);
        put("whisper_mud", 0xFF5D4E37);
        put("memory_worm", 0xFFE67E22);
        put("mimic_disguise", 0xFF95A5A6);
        put("bronze_guard", 0xFFCD7F32);
        put("moon_shadow", 0xFF5D6D7E);
        put("silvervine_drunk", 0xFF58D68D);
        put("neon_flash", 0xFF00E5CC);
        put("velvet_whisper", 0xFFC39BD3);
        put("moonlight_glimmer", 0xFFBDC3C7);
        put("first_cry_memory", 0xFF85C1E9);
        put("blind_water_stealth", 0xFF2C3E50);
        put("gear_windup", 0xFF85929E);
        put("dawn_hope", 0xFFFFD700);
        put("forgotten_page", 0xFFFDF2E9);
        put("stardust_tread", 0xFFE8DAEF);
        put("velvet_cradle", 0xFFD7BDE2);
        put("red_jade", 0xFFC0392B);
        put("iron_claw_apprentice", 0xFF707B7C);
        put("calico_warmth", 0xFFE59866);
        put("cheshire_grin", 0xFF58D68D);
        put("white_glove_guide", 0xFFECF0F1);
        put("alpha_observe", 0xFF34495E);
        put("coco_guardian", 0xFFD35400);
        put("milk_tea_play", 0xFFD4AC6E);
        put("gear_precision_2", 0xFF566573);
        put("dark_tide", 0xFF1B2631);
        put("velvet_warmth", 0xFFAF7AC5);
        put("moonlight_clear", 0xFFAED6F1);
        put("first_cry_beginner", 0xFF5DADE2);
        put("hibiscus_blood", 0xFFE74C3C);
        put("stardust_step", 0xFFF4D03F);
        put("iron_rust_legion", 0xFF922B21);
        put("paper_crow_ink", 0xFF212F3C);
        put("blind_water_core", 0xFF2874A6);
        put("deep_sea_current", 0xFF154360);
        put("moonlight_ripple", 0xFF7FB3D5);
        put("royal_glory", 0xFFF1C40F);
        put("gear_schedule", 0xFFBFC9CA);
        put("sanhua_thread", 0xFFE8B4B8);
    }

    private RyokatanaTintColors() {}

    private static void put(String id, int color) {
        BY_VARIANT.put(id, color);
    }

    public static int colorFor(String variantId) {
        return BY_VARIANT.getOrDefault(variantId, 0xFFB0BEC5);
    }
}
