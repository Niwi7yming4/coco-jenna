#!/usr/bin/env python3
"""Generate weapon_skills JSON with vfx_theme, sound_key, charge_profile per weapon."""
from __future__ import annotations
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "src/main/resources/data/cocojenna/weapon_skills"

RYOKATANA = [
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

DAIKATA = [
    "tiger_iron", "wind_cut", "phantom", "suppression", "shockwave", "crescent", "moon_verdict",
    "star_map", "abyss", "neon_dance", "gear_king", "hibiscus_ultimate", "howling_gorge",
    "first_dawn", "royal_authority", "shadow_imitation", "silent_guard", "dusk_end",
    "white_glove", "forgotten_tower", "village_soul", "storm_umbrella",
    "salmon_king", "night_verdict", "toy_hammer", "hibiscus_fall",
]

# variant -> (archetype, vfx_theme, sound_key, charge_profile)
META: dict[str, tuple[str, str, str, str]] = {
    "fish_bone_tide": ("wave", "wave", "splash", "standard"),
    "deep_sea_current": ("wave", "fish", "splash", "standard"),
    "dark_tide": ("wave", "wave", "trident", "standard"),
    "blind_water_abyss": ("bind", "abyss", "warden", "heavy"),
    "blind_water_core": ("bind", "abyss", "splash", "heavy"),
    "blind_water_stealth": ("stealth", "shadow", "splash", "quick"),
    "iron_rust_armor_break": ("gear", "iron", "anvil", "heavy"),
    "iron_rust_legion": ("gear", "iron", "anvil", "standard"),
    "iron_claw_apprentice": ("gear", "iron", "anvil", "quick"),
    "precision_gear": ("gear", "gear", "anvil", "standard"),
    "gear_precision_2": ("gear", "gear", "anvil", "standard"),
    "gear_windup": ("gear", "gear_heavy", "anvil", "heavy"),
    "gear_schedule": ("gear", "gear", "anvil", "standard"),
    "hibiscus_blood": ("heal", "hibiscus", "amethyst", "heavy"),
    "hibiscus_ultimate": ("slash", "hibiscus", "amethyst", "heavy"),
    "hibiscus_fall": ("slash", "blood", "amethyst", "ritual"),
    "moonlight_ripple": ("moon", "moon", "amethyst", "ritual"),
    "moonlight_clear": ("moon", "moon", "amethyst", "standard"),
    "moonlight_glimmer": ("moon", "moon", "amethyst", "standard"),
    "moon_shadow": ("stealth", "shadow", "enchant", "quick"),
    "moon_verdict": ("slash", "moon", "bell", "heavy"),
    "night_verdict": ("slash", "moon", "bell", "ritual"),
    "paper_crow_ink": ("flash", "paper", "paper", "quick"),
    "origami_cut": ("flash", "paper", "paper", "quick"),
    "sanhua_thread": ("moon", "velvet", "enchant", "standard"),
    "jellyfish_bind": ("bind", "jelly", "splash", "quick"),
    "lament_split": ("bind", "blood", "warden", "ritual"),
    "memory_worm": ("slash", "shadow", "enchant", "instant"),
    "mimic_disguise": ("stealth", "shadow", "cat", "quick"),
    "whisper_mud": ("stealth", "shadow", "splash", "quick"),
    "cheshire_grin": ("stealth", "shadow", "cat", "instant"),
    "neon_flash": ("flash", "neon", "amethyst", "quick"),
    "neon_dance": ("slash", "neon", "amethyst", "standard"),
    "moth_scale": ("slash", "neon", "enchant", "quick"),
    "screen_noise": ("flash", "neon", "enchant", "quick"),
    "velvet_warmth": ("heal", "velvet", "enchant", "quick"),
    "velvet_cradle": ("bind", "velvet", "enchant", "standard"),
    "velvet_whisper": ("bind", "velvet", "enchant", "standard"),
    "fallen_velvet_claw": ("slash", "shadow", "warden", "standard"),
    "red_jade": ("heal", "velvet", "bell", "quick"),
    "calico_warmth": ("heal", "cat", "cat", "quick"),
    "coco_guardian": ("heal", "cat", "cat", "standard"),
    "milk_tea_play": ("heal", "cat", "cat", "quick"),
    "first_cry_beginner": ("slash", "first_cry", "cat", "quick"),
    "first_cry_memory": ("slash", "first_cry", "cat", "standard"),
    "first_dawn": ("slash", "star", "bell", "standard"),
    "dawn_hope": ("moon", "star", "bell", "standard"),
    "forgotten_page": ("bind", "paper", "paper", "standard"),
    "stardust_step": ("flash", "stardust", "enchant", "quick"),
    "stardust_tread": ("flash", "stardust", "enchant", "quick"),
    "royal_glory": ("slash", "iron", "bell", "standard"),
    "royal_authority": ("slash", "iron", "bell", "heavy"),
    "bronze_guard": ("slash", "iron", "bell", "standard"),
    "copper_bell_soul": ("slash", "iron", "bell", "instant"),
    "alpha_observe": ("slash", "star", "enchant", "standard"),
    "white_glove_guide": ("slash", "wave", "splash", "standard"),
    "white_glove": ("slash", "paper", "paper", "standard"),
    "silvervine_drunk": ("slash", "neon", "enchant", "instant"),
    "tiger_iron": ("slash", "iron", "anvil", "heavy"),
    "wind_cut": ("slash", "slash", "sweep", "quick"),
    "phantom": ("stealth", "shadow", "enchant", "instant"),
    "shadow_imitation": ("stealth", "shadow", "enchant", "quick"),
    "suppression": ("bind", "bind", "warden", "standard"),
    "shockwave": ("slash", "gear_heavy", "anvil", "heavy"),
    "crescent": ("slash", "moon", "sweep", "standard"),
    "star_map": ("slash", "star", "enchant", "ritual"),
    "abyss": ("wave", "abyss", "warden", "heavy"),
    "gear_king": ("gear", "gear_heavy", "anvil", "heavy"),
    "howling_gorge": ("slash", "wave", "trident", "standard"),
    "silent_guard": ("slash", "iron", "bell", "standard"),
    "dusk_end": ("slash", "shadow", "bell", "standard"),
    "forgotten_tower": ("slash", "gear_heavy", "anvil", "heavy"),
    "village_soul": ("slash", "cat", "cat", "standard"),
    "storm_umbrella": ("wave", "wave", "trident", "standard"),
    "salmon_king": ("slash", "fish", "splash", "ritual"),
    "toy_hammer": ("slash", "neon", "anvil", "heavy"),
}

SUPREME = {"salmon_king", "night_verdict", "toy_hammer", "hibiscus_fall"}


def meta_for(variant: str) -> tuple[str, str, str, str]:
    if variant in META:
        return META[variant]
    if "moon" in variant:
        return ("moon", "moon", "amethyst", "standard")
    if "gear" in variant or "iron" in variant:
        return ("gear", "gear", "anvil", "standard")
    if "velvet" in variant:
        return ("heal", "velvet", "enchant", "quick")
    if "blind" in variant or "water" in variant:
        return ("wave", "wave", "splash", "standard")
    return ("slash", "slash", "sweep", "standard")


def skill_json(variant: str) -> dict:
    arch, vfx, sound, charge = meta_for(variant)
    from_charge = {
        "instant": 8, "quick": 12, "standard": 16, "heavy": 24, "ritual": 36
    }
    return {
        "variant_id": variant,
        "skill_id": variant,
        "archetype": arch,
        "vfx_theme": vfx,
        "sound_key": sound,
        "charge_profile": charge,
        "min_charge_ticks": from_charge.get(charge, 16),
        "cooldown_ticks": 80 if variant in SUPREME else 60,
        "mana_cost": 0,
        "stage_effects": [
            {"stage": 0, "damage_mult": 1.0, "radius_mult": 1.0, "particle_tier": 1},
            {"stage": 1, "damage_mult": 1.15, "radius_mult": 1.1, "particle_tier": 2},
            {"stage": 2, "damage_mult": 1.35, "radius_mult": 1.2, "particle_tier": 3},
            {"stage": 3, "damage_mult": 1.6, "radius_mult": 1.35, "particle_tier": 4},
        ],
    }


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    count = 0
    for v in RYOKATANA + DAIKATA:
        data = skill_json(v)
        (OUT / f"{v}.json").write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
        count += 1
    print(f"Wrote {count} weapon skill JSON files")


if __name__ == "__main__":
    main()
