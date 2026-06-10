#!/usr/bin/env python3
"""Generate item model JSONs for custom spawn egg textures."""

import json
from pathlib import Path

MODEL_DIR = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/models/item"

EGGS = [
    "heat_leech_spawn_egg",
    "forgotten_wisp_spawn_egg",
    "whispering_doll_spawn_egg",
    "memory_moth_spawn_egg",
    "mimic_cat_spawn_egg",
    "grief_amalgam_spawn_egg",
    "blind_water_lord_spawn_egg",
    "fallen_velvet_spawn_egg",
    "primal_chaos_spawn_egg",
    "coco_spawn_egg",
    "jenna_spawn_egg",
    "cheshire_spawn_egg",
    "white_glove_spawn_egg",
    "alpha_spawn_egg",
    "samurai_cat_spawn_egg",
    "sumo_cat_spawn_egg",
    "monk_cat_spawn_egg",
    "general_cat_spawn_egg",
    "shadow_claw_spawn_egg",
    "sanhua_weaver_spawn_egg",
]

def main():
    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    for name in EGGS:
        model = {
            "parent": "minecraft:item/generated",
            "textures": {"layer0": f"cocojenna:item/{name}"},
        }
        path = MODEL_DIR / f"{name}.json"
        path.write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8")
        print(f"Wrote {path.name}")

if __name__ == "__main__":
    main()
