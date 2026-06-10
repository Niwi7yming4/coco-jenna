#!/usr/bin/env python3
"""Generate ryokatana entity loot pools and missing craft recipes."""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOOT = ROOT / "src/main/resources/data/cocojenna/loot_tables"
RECIPES = ROOT / "src/main/resources/data/cocojenna/recipes"

# 設計書 1.2 怪物掉落 + 區域對應
ENTITY_RYOKATANA = {
    "heat_leech": ("ryokatana_fish_bone_tide", 2),
    "forgotten_wisp": ("ryokatana_forgotten_page", 4),
    "whispering_doll": ("ryokatana_whisper_mud", 3),
    "memory_moth": ("ryokatana_memory_worm", 4),
    "mimic_cat": ("ryokatana_mimic_disguise", 5),
    "grief_amalgam": ("ryokatana_lament_split", 8),
    "blind_water_lord": ("ryokatana_blind_water_abyss", 10),
    "fallen_velvet": ("ryokatana_fallen_velvet_claw", 10),
    "primal_chaos": ("ryokatana_iron_rust_legion", 6),
    "monk_cat": ("ryokatana_copper_bell_soul", 8),
    "samurai_cat": ("ryokatana_iron_claw_apprentice", 5),
    "general_cat": ("ryokatana_bronze_guard", 6),
    "velvet_moth": ("ryokatana_moth_scale", 5),
    "sumo_cat": ("ryokatana_silvervine_drunk", 4),
    "court_lady_cat": ("ryokatana_velvet_whisper", 4),
}

CHEST_EXTRA = {
    "dungeon_common": [
        ("ryokatana_first_cry_beginner", 4),
        ("ryokatana_calico_warmth", 3),
        ("ryokatana_milk_tea_play", 2),
    ],
    "dungeon_elder_cellar": [
        ("ryokatana_copper_bell_soul", 4),
    ],
    "dungeon_ashura_trial": [
        ("ryokatana_red_jade", 3),
    ],
    "dungeon_wind_cavern": [
        ("ryokatana_stardust_tread", 4),
    ],
    "dungeon_saltwind_wreck": [
        ("ryokatana_blind_water_stealth", 3),
    ],
    "moon_alley": [
        ("ryokatana_neon_flash", 6),
        ("ryokatana_blind_water_stealth", 5),
    ],
    "blind_port": [
        ("ryokatana_blind_water_core", 8),
        ("ryokatana_deep_sea_current", 4),
    ],
    "gear_town": [
        ("ryokatana_precision_gear", 8),
        ("ryokatana_gear_schedule", 6),
        ("ryokatana_gear_windup", 5),
    ],
}

# 設計書 1.6 特殊合成
SPECIAL_RECIPES = {
    "ryokatana_hibiscus_blood": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["HFH", "HIH", " B "],
        "key": {
            "H": {"item": "cocojenna:hibiscus_flower_item"},
            "F": {"item": "cocojenna:black_mud_remnant"},
            "I": {"item": "minecraft:iron_ingot"},
            "B": {"item": "minecraft:iron_block"},
        },
        "result": {"item": "cocojenna:ryokatana_hibiscus_blood", "count": 1},
    },
    "ryokatana_stardust_step": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["SPS", "P P", " B "],
        "key": {
            "S": {"item": "cocojenna:stardust_soil_item"},
            "P": {"item": "cocojenna:purr_crystal"},
            "B": {"item": "cocojenna:moonstone"},
        },
        "result": {"item": "cocojenna:ryokatana_stardust_step", "count": 1},
    },
    "ryokatana_iron_rust_legion": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["RIR", "IRI", " S "],
        "key": {
            "R": {"item": "cocojenna:rusty_iron"},
            "I": {"item": "minecraft:iron_ingot"},
            "S": {"item": "minecraft:stick"},
        },
        "result": {"item": "cocojenna:ryokatana_iron_rust_legion", "count": 1},
    },
    "ryokatana_paper_crow_ink": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["FPF", " B ", " S "],
        "key": {
            "F": {"item": "cocojenna:feather_wand"},
            "P": {"item": "minecraft:paper"},
            "B": {"item": "cocojenna:black_mud_remnant"},
            "S": {"item": "minecraft:stick"},
        },
        "result": {"item": "cocojenna:ryokatana_paper_crow_ink", "count": 1},
    },
    "ryokatana_blind_water_core": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["GBG", "BPB", " M "],
        "key": {
            "G": {"item": "cocojenna:blind_water_gel"},
            "B": {"item": "cocojenna:blind_water_sample"},
            "P": {"item": "cocojenna:deep_sea_pearl"},
            "M": {"item": "cocojenna:moonstone"},
        },
        "result": {"item": "cocojenna:ryokatana_blind_water_core", "count": 1},
    },
}

# 通用模板：尚未有配方的良快刀
GENERIC_RYOKATANA = [
    "origami_cut", "jellyfish_bind", "screen_noise", "lament_split",
    "whisper_mud", "memory_worm", "mimic_disguise", "bronze_guard",
    "silvervine_drunk", "neon_flash", "velvet_whisper", "moonlight_glimmer",
    "first_cry_memory", "blind_water_stealth", "gear_windup", "forgotten_page",
    "stardust_tread", "velvet_cradle", "red_jade", "calico_warmth",
    "cheshire_grin", "white_glove_guide", "alpha_observe", "coco_guardian",
    "milk_tea_play", "gear_precision_2", "velvet_warmth", "moonlight_clear",
    "first_cry_beginner", "paper_crow_ink", "deep_sea_current", "moonlight_ripple",
    "royal_glory", "gear_schedule", "sanhua_thread", "copper_bell_soul",
    "iron_rust_armor_break", "fish_bone_tide", "moth_scale", "fallen_velvet_claw",
]


def entity_loot(entity: str, ryokatana: str, weight: int, remnant_max: int = 3) -> dict:
    return {
        "type": "minecraft:entity",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": "cocojenna:black_mud_remnant",
                        "weight": 40,
                        "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": remnant_max}}],
                    },
                    {"type": "minecraft:empty", "weight": 60},
                ],
                "conditions": [{"condition": "minecraft:killed_by_player"}],
            },
            {
                "rolls": 1,
                "entries": [
                    {"type": "minecraft:item", "name": f"cocojenna:{ryokatana}", "weight": weight},
                    {"type": "minecraft:empty", "weight": 100 - weight},
                ],
                "conditions": [{"condition": "minecraft:killed_by_player"}],
            },
        ],
    }


def npc_loot(entity: str, ryokatana: str, weight: int) -> dict:
    return {
        "type": "minecraft:entity",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {"type": "minecraft:item", "name": f"cocojenna:{ryokatana}", "weight": weight},
                    {"type": "minecraft:empty", "weight": 100 - weight},
                ],
                "conditions": [{"condition": "minecraft:killed_by_player"}],
            },
        ],
    }


def generic_recipe(name: str) -> dict:
    return {
        "type": "minecraft:crafting_shaped",
        "pattern": [" I ", " S ", " M "],
        "key": {
            "I": {"item": "minecraft:iron_ingot"},
            "S": {"item": "minecraft:stick"},
            "M": {"item": "cocojenna:black_mud_remnant"},
        },
        "result": {"item": f"cocojenna:ryokatana_{name}", "count": 1},
    }


def patch_chest(path: Path, extras: list[tuple[str, int]]) -> None:
    if not path.exists():
        return
    data = json.loads(path.read_text(encoding="utf-8"))
    pool = data["pools"][0]
    existing = {e["name"] for e in pool["entries"] if e.get("type") == "minecraft:item"}
    for item, weight in extras:
        full = f"cocojenna:{item}"
        if full in existing:
            continue
        pool["entries"].append({"type": "minecraft:item", "name": full, "weight": weight})
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def main() -> None:
    count_loot = 0
    for entity, (ryo, wt) in ENTITY_RYOKATANA.items():
        dest = LOOT / "entities" / f"{entity}.json"
        is_boss = entity in ("grief_amalgam", "blind_water_lord", "fallen_velvet", "primal_chaos")
        if entity.endswith("_cat") or entity in ("monk_cat", "samurai_cat", "velvet_moth"):
            data = npc_loot(entity, ryo, wt)
        else:
            data = entity_loot(entity, ryo, wt, remnant_max=6 if is_boss else 3)
        dest.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        count_loot += 1
        print(f"  loot entities/{entity}.json")

    for chest, extras in CHEST_EXTRA.items():
        patch_chest(LOOT / "chests" / f"{chest}.json", extras)
        print(f"  chest {chest}")

    count_rec = 0
    for name, recipe in SPECIAL_RECIPES.items():
        path = RECIPES / f"{name}.json"
        path.write_text(json.dumps(recipe, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        count_rec += 1

    existing = {p.stem for p in RECIPES.glob("ryokatana_*.json")}
    for name in GENERIC_RYOKATANA:
        fname = f"ryokatana_{name}"
        if fname in existing:
            continue
        path = RECIPES / f"{fname}.json"
        path.write_text(json.dumps(generic_recipe(name), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        count_rec += 1

    print(f"\nDone: {count_loot} entity loot tables, {count_rec} new recipes")


if __name__ == "__main__":
    main()
