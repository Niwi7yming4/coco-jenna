#!/usr/bin/env python3
"""Append resonance voice lines for all weapon variants to lang JSON."""
from __future__ import annotations
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SKILLS = ROOT / "src/main/resources/data/cocojenna/weapon_skills"
LANG_ZH = ROOT / "src/main/resources/assets/cocojenna/lang/zh_tw.json"
LANG_EN = ROOT / "src/main/resources/assets/cocojenna/lang/en_us.json"

VOICE_ZH: dict[str, str] = {
    "fish_bone_tide": "「潮風記得你。」",
    "iron_rust_armor_break": "「鏽甲之下，仍是勇氣。」",
    "hibiscus_blood": "「花開在傷口上。」",
    "moonlight_ripple": "「月照萬里，只為一人。」",
    "paper_crow_ink": "「墨未乾，憶未冷。」",
    "deep_sea_current": "「深海的魚群為你讓路。」",
    "blind_water_abyss": "「深淵在低語你的名字。」",
    "gear_windup": "「齒輪咬合，命運轉動。」",
    "first_cry_beginner": "「初啼之聲，貓之國仍在。」",
    "royal_glory": "「王者的榮光，不會褪色。」",
    "salmon_king": "「鮭魚之王，躍過浪潮。」",
    "tiger_iron": "「虎符在握，軍心自定。」",
    "phantom": "「影步無聲，刃已有痕。」",
    "sanhua_thread": "「三花線牽著舊日溫柔。」",
    "lament_split": "「悲傷也是一種力量。」",
    "memory_worm": "「蛀蟲啃食記憶，你卻更清醒。」",
    "neon_flash": "「霓虹亮起，黑夜退場。」",
    "stardust_step": "「星塵落腳，便是歸途。」",
}

VOICE_EN: dict[str, str] = {
    "fish_bone_tide": "\"The tide remembers you.\"",
    "iron_rust_armor_break": "\"Under rust, courage remains.\"",
    "hibiscus_blood": "\"Flowers bloom on wounds.\"",
    "moonlight_ripple": "\"Moonlight for one soul.\"",
    "paper_crow_ink": "\"Ink wet, memory warm.\"",
    "deep_sea_current": "\"Deep fish part for you.\"",
    "blind_water_abyss": "\"The abyss whispers your name.\"",
    "gear_windup": "\"Gears bite—fate turns.\"",
    "first_cry_beginner": "\"First cry—the kingdom hears.\"",
    "royal_glory": "\"Royal glory never fades.\"",
    "salmon_king": "\"Salmon king leaps the tide.\"",
    "tiger_iron": "\"With the tally, armies steady.\"",
    "phantom": "\"Silent step, cut already made.\"",
    "sanhua_thread": "\"Sanhua's thread holds old warmth.\"",
    "lament_split": "\"Sorrow is power too.\"",
    "memory_worm": "\"The worm gnaws—you remember clearer.\"",
    "neon_flash": "\"Neon flares; night retreats.\"",
    "stardust_step": "\"Stardust underfoot is the way home.\"",
}


def generic_zh(variant: str) -> str:
    name = variant.replace("_", " · ")
    return f"「{name}」在共鳴中低語。"


def generic_en(variant: str) -> str:
    name = variant.replace("_", " · ")
    return f"\"{name}\" murmurs in resonance."


def merge_lang(path: Path, entries: dict[str, str], generic_fn) -> None:
    data = json.loads(path.read_text(encoding="utf-8"))
    for f in sorted(SKILLS.glob("*.json")):
        variant = f.stem
        key = f"weapon.cocojenna.resonance_voice.{variant}"
        if key not in data:
            data[key] = entries.get(variant, generic_fn(variant))
    data["weapon.cocojenna.resonance_voice._generic"] = data.get(
        "weapon.cocojenna.resonance_voice._generic",
        "「武器的記憶與你共鳴。」" if path == LANG_ZH else "\"The weapon's memory resonates with you.\"",
    )
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    merge_lang(LANG_ZH, VOICE_ZH, generic_zh)
    merge_lang(LANG_EN, VOICE_EN, generic_en)
    print("Updated resonance voice lang keys")


if __name__ == "__main__":
    main()
