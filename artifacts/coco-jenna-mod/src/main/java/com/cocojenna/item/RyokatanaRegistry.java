package com.cocojenna.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** 良快刀 50 把 — 統一註冊與查詢. */
public final class RyokatanaRegistry {

    private static final Map<String, RegistryObject<Item>> BY_ID = new LinkedHashMap<>();

    private record Def(String id, Tier tier, int dmg, float speed) {}

    private static final Def[] ALL = {
            new Def("fish_bone_tide", Tiers.IRON, 3, -2.2f),
            new Def("copper_bell_soul", Tiers.IRON, 0, -2.0f),
            new Def("iron_rust_armor_break", Tiers.IRON, 4, -2.4f),
            new Def("origami_cut", Tiers.IRON, 3, -1.8f),
            new Def("jellyfish_bind", Tiers.WOOD, 0, -2.0f),
            new Def("screen_noise", Tiers.IRON, 2, -1.6f),
            new Def("precision_gear", Tiers.IRON, 3, -2.6f),
            new Def("moth_scale", Tiers.WOOD, 0, -2.0f),
            new Def("blind_water_abyss", Tiers.IRON, 1, -2.0f),
            new Def("lament_split", Tiers.IRON, 2, -2.0f),
            new Def("fallen_velvet_claw", Tiers.IRON, 5, -1.8f),
            new Def("whisper_mud", Tiers.IRON, 1, -2.0f),
            new Def("memory_worm", Tiers.WOOD, 0, -2.0f),
            new Def("mimic_disguise", Tiers.IRON, 2, -1.8f),
            new Def("bronze_guard", Tiers.IRON, 8, -3.0f),
            new Def("moon_shadow", Tiers.IRON, 1, -2.2f),
            new Def("silvervine_drunk", Tiers.WOOD, 0, -2.0f),
            new Def("neon_flash", Tiers.IRON, 2, -1.8f),
            new Def("velvet_whisper", Tiers.IRON, 0, -2.0f),
            new Def("moonlight_glimmer", Tiers.IRON, 3, -2.4f),
            new Def("first_cry_memory", Tiers.IRON, 3, -2.4f),
            new Def("blind_water_stealth", Tiers.IRON, 2, -1.8f),
            new Def("gear_windup", Tiers.WOOD, 0, -2.0f),
            new Def("dawn_hope", Tiers.DIAMOND, 5, -2.4f),
            new Def("forgotten_page", Tiers.IRON, 0, -2.0f),
            new Def("stardust_tread", Tiers.IRON, 0, -2.0f),
            new Def("velvet_cradle", Tiers.IRON, 0, -2.0f),
            new Def("red_jade", Tiers.IRON, 0, -1.8f),
            new Def("iron_claw_apprentice", Tiers.IRON, 3, -2.4f),
            new Def("calico_warmth", Tiers.IRON, 0, -2.0f),
            new Def("cheshire_grin", Tiers.IRON, 4, -1.8f),
            new Def("white_glove_guide", Tiers.IRON, 0, -2.0f),
            new Def("alpha_observe", Tiers.IRON, 1, -2.0f),
            new Def("coco_guardian", Tiers.IRON, 0, -2.0f),
            new Def("milk_tea_play", Tiers.WOOD, 0, -2.0f),
            new Def("gear_precision_2", Tiers.IRON, 5, -2.6f),
            new Def("dark_tide", Tiers.DIAMOND, 3, -2.0f),
            new Def("velvet_warmth", Tiers.IRON, 0, -2.0f),
            new Def("moonlight_clear", Tiers.IRON, 2, -2.0f),
            new Def("first_cry_beginner", Tiers.IRON, 1, -2.4f),
            new Def("hibiscus_blood", Tiers.DIAMOND, 4, -2.4f),
            new Def("stardust_step", Tiers.IRON, 5, -2.0f),
            new Def("iron_rust_legion", Tiers.IRON, 10, -3.2f),
            new Def("paper_crow_ink", Tiers.IRON, 4, -2.0f),
            new Def("blind_water_core", Tiers.IRON, 2, -2.0f),
            new Def("deep_sea_current", Tiers.IRON, 9, -3.0f),
            new Def("moonlight_ripple", Tiers.IRON, 2, -2.0f),
            new Def("royal_glory", Tiers.DIAMOND, 7, -2.4f),
            new Def("gear_schedule", Tiers.IRON, 0, -2.0f),
            new Def("sanhua_thread", Tiers.IRON, 2, -2.0f),
    };

    private RyokatanaRegistry() {}

    public static void registerAll(DeferredRegister<Item> items) {
        for (Def def : ALL) {
            String regName = "ryokatana_" + def.id;
            RegistryObject<Item> ro = items.register(regName, () ->
                    new RyokatanaItem(def.tier, def.dmg, def.speed,
                            new Item.Properties(), def.id));
            BY_ID.put(def.id, ro);
        }
    }

    public static RegistryObject<Item> get(String id) {
        return BY_ID.get(id);
    }

    public static Optional<RegistryObject<Item>> find(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static Map<String, RegistryObject<Item>> all() {
        return BY_ID;
    }
}
