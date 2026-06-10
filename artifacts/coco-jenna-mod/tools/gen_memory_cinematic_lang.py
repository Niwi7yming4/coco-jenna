#!/usr/bin/env python3
"""Append weapon memory cinematic lang keys for all ryokatana variants."""
import json
from pathlib import Path

VARIANTS = [
    "fish_bone_tide", "copper_bell_soul", "iron_rust_armor_break", "origami_cut", "jellyfish_bind",
    "screen_noise", "precision_gear", "moth_scale", "blind_water_abyss", "lament_split",
    "fallen_velvet_claw", "whisper_mud", "memory_worm", "mimic_disguise", "bronze_guard",
    "moon_shadow", "silvervine_drunk", "neon_flash", "velvet_whisper", "moonlight_glimmer",
    "first_cry_memory", "blind_water_stealth", "gear_windup", "dawn_hope", "forgotten_page",
    "stardust_tread", "velvet_cradle", "red_jade", "iron_claw_apprentice", "calico_warmth",
    "cheshire_grin", "white_glove_guide", "alpha_observe", "coco_guardian", "milk_tea_play",
    "gear_precision_2", "dark_tide", "velvet_warmth", "moonlight_clear", "first_cry_beginner",
    "hibiscus_blood", "stardust_step", "iron_rust_legion", "paper_crow_ink", "blind_water_core",
    "deep_sea_current", "moonlight_ripple", "royal_glory", "gear_schedule", "sanhua_thread",
]

LANG_DIR = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang"

ZH_TEMPLATES = (
    "刀身微震，一段被塵封的記憶浮現……",
    "你聽見遙遠的貓鳴，像是有人在呼喚名字。",
    "碎片歸位。這把刀記住了你的選擇。",
)
EN_TEMPLATES = (
    "The blade trembles—a sealed memory surfaces…",
    "Distant cat-calls echo, as if someone calls a name.",
    "The shard settles. This blade remembers your choice.",
)


def merge_lang(path: Path, is_en: bool):
    data = json.loads(path.read_text(encoding="utf-8"))
    templates = EN_TEMPLATES if is_en else ZH_TEMPLATES
    for v in VARIANTS:
        for i, text in enumerate(templates, 1):
            key = f"weapon.cinematic.cocojenna.{v}.{i}"
            if key not in data:
                data[key] = text
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main():
    merge_lang(LANG_DIR / "zh_tw.json", False)
    merge_lang(LANG_DIR / "en_us.json", True)
    print(f"Ensured cinematic lang for {len(VARIANTS)} variants")


if __name__ == "__main__":
    main()
