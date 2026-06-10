#!/usr/bin/env python3
"""Generate Patchouli JSON for guardian_guide from handbook outline."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/patchouli_books/guardian_guide/zh_tw"

CATEGORIES = [
    ("welcome", "patchouli.cocojenna.category.welcome", "patchouli.cocojenna.category.welcome.desc", 0),
    ("first_cry", "patchouli.cocojenna.category.first_cry", "patchouli.cocojenna.category.first_cry.desc", 1),
    ("penetration", "patchouli.cocojenna.category.penetration", "patchouli.cocojenna.category.penetration.desc", 2),
    ("combat", "patchouli.cocojenna.category.combat", "patchouli.cocojenna.category.combat.desc", 3),
    ("sequence", "patchouli.cocojenna.category.sequence", "patchouli.cocojenna.category.sequence.desc", 4),
    ("map", "patchouli.cocojenna.category.map", "patchouli.cocojenna.category.map.desc", 5),
    ("memory", "patchouli.cocojenna.category.memory", "patchouli.cocojenna.category.memory.desc", 6),
    ("forge", "patchouli.cocojenna.category.forge", "patchouli.cocojenna.category.forge.desc", 7),
    ("town", "patchouli.cocojenna.category.town", "patchouli.cocojenna.category.town.desc", 8),
    ("four_mad", "patchouli.cocojenna.category.four_mad", "patchouli.cocojenna.category.four_mad.desc", 9),
    ("final", "patchouli.cocojenna.category.final", "patchouli.cocojenna.category.final.desc", 10),
    ("appendix", "patchouli.cocojenna.category.appendix", "patchouli.cocojenna.category.appendix.desc", 11),
]

ENTRIES = {
    "welcome": [
        ("prologue", "patchouli.cocojenna.entry.prologue", "coco_fur", 0, 2),
        ("meet_jenna", "patchouli.cocojenna.entry.meet_jenna", "jenna_fur", 1, 2),
        ("basics", "patchouli.cocojenna.entry.basics", "guardian_guide", 2, 2),
        ("alpha_rules", "patchouli.cocojenna.entry.alpha_rules", "memory_fragment", 3, 2),
    ],
    "first_cry": [
        ("first_cry_village", "patchouli.cocojenna.entry.first_cry_village", "velvet_wood", 0, 2),
        ("morning", "patchouli.cocojenna.entry.morning", "cat_bed", 1, 2),
        ("bond", "patchouli.cocojenna.entry.bond", "pure_purr_crystal", 2, 2),
        ("gathering", "patchouli.cocojenna.entry.gathering", "catnip", 3, 2),
    ],
    "penetration": [
        ("moon_paw", "patchouli.cocojenna.entry.penetration_moon_paw", "moonlight_stone", 0, 2),
        ("gray_whisker", "patchouli.cocojenna.entry.penetration_gray_whisker", "guardian_guide", 1, 2),
        ("moon_dungeon", "patchouli.cocojenna.entry.penetration_moon_dungeon", "sealed_relic", 2, 2),
        ("repair_portal", "patchouli.cocojenna.entry.penetration_repair_portal", "memory_lighthouse", 3, 2),
        ("first_entry", "patchouli.cocojenna.entry.penetration_first_entry", "coco_fur", 4, 2),
    ],
    "combat": [
        ("what_is_mud", "patchouli.cocojenna.entry.what_is_mud", "black_mud_sample", 0, 2),
        ("erosion", "patchouli.cocojenna.entry.erosion", "abyss_black_mud", 1, 2),
        ("parasite", "patchouli.cocojenna.entry.parasite", "hibiscus_tear", 2, 2),
        ("distill", "patchouli.cocojenna.entry.distill", "abyss_distiller", 3, 2),
        ("bestiary", "patchouli.cocojenna.entry.bestiary", "sealed_relic", 4, 2),
    ],
    "sequence": [
        ("what_sequence", "patchouli.cocojenna.entry.what_sequence", "meme_badge", 0, 2),
        ("three_paths", "patchouli.cocojenna.entry.three_paths", "ryokatana_velvet_whisper", 1, 2),
        ("skill_wheel", "patchouli.cocojenna.entry.skill_wheel", "velvet_beginner_helmet", 2, 2),
        ("promotion", "patchouli.cocojenna.entry.promotion", "memory_essence", 3, 2),
    ],
    "map": [
        ("world_map", "patchouli.cocojenna.entry.world_map", "memory_lighthouse", 0, 2),
        ("gear_town", "patchouli.cocojenna.entry.gear_town", "iron_claw_hammer", 1, 2),
    ],
    "memory": [
        ("fragments", "patchouli.cocojenna.entry.fragments", "memory_fragment", 0, 2),
        ("monument", "patchouli.cocojenna.entry.monument", "memory_lighthouse", 1, 2),
        ("sister_bond", "patchouli.cocojenna.entry.sister_bond", "coco_fur", 2, 2),
    ],
    "forge": [
        ("iron_claw", "patchouli.cocojenna.entry.iron_claw", "iron_claw_hammer", 0, 2),
        ("enhance", "patchouli.cocojenna.entry.enhance", "ryokatana_coco_guardian", 1, 2),
    ],
    "town": [
        ("build", "patchouli.cocojenna.entry.build", "purification_tower", 0, 2),
        ("recruit", "patchouli.cocojenna.entry.recruit", "catnip", 1, 2),
        ("happiness", "patchouli.cocojenna.entry.happiness", "velvet_beginner_chestplate", 2, 2),
    ],
    "four_mad": [
        ("legend", "patchouli.cocojenna.entry.four_mad_legend", "sealed_relic", 0, 2),
        ("banjou", "patchouli.cocojenna.entry.four_mad_banjou", "moonlight_stone", 1, 2),
        ("sanwa", "patchouli.cocojenna.entry.four_mad_sanwa", "catnip_coin", 2, 2),
        ("ashura", "patchouli.cocojenna.entry.four_mad_ashura", "ryokatana_velvet_whisper", 3, 2),
        ("orange", "patchouli.cocojenna.entry.four_mad_orange", "hibiscus_tear", 4, 2),
    ],
    "final": [
        ("shadow_claw", "patchouli.cocojenna.entry.shadow_claw", "black_mud_sample", 0, 2),
        ("prepare", "patchouli.cocojenna.entry.prepare_final", "velvet_beginner_leggings", 1, 2),
        ("phases", "patchouli.cocojenna.entry.final_phases", "sealed_relic", 2, 2),
        ("endings", "patchouli.cocojenna.entry.endings", "pure_purr_crystal", 3, 2),
    ],
    "appendix": [
        ("coco_diary", "patchouli.cocojenna.entry.coco_diary", "coco_fur", 0, 2),
        ("jenna_doodles", "patchouli.cocojenna.entry.jenna_doodles", "jenna_fur", 1, 2),
        ("alpha_letter", "patchouli.cocojenna.entry.alpha_letter", "guardian_guide", 2, 2),
    ],
}

def main():
    cat_dir = ROOT / "categories"
    cat_dir.mkdir(parents=True, exist_ok=True)
    for cid, name, desc, order in CATEGORIES:
        path = cat_dir / f"{cid}.json"
        data = {
            "name": name,
            "description": desc,
            "icon": "cocojenna:guardian_guide",
            "sortnum": order,
        }
        path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    for cat, entries in ENTRIES.items():
        entry_dir = ROOT / "entries" / cat
        entry_dir.mkdir(parents=True, exist_ok=True)
        for eid, name, icon, sortnum, pages in entries:
            pages_arr = [
                {"type": "patchouli:text", "text": f"{name}.p{i}"}
                for i in range(1, pages + 1)
            ]
            data = {
                "name": name,
                "icon": f"cocojenna:{icon}",
                "category": f"cocojenna:{cat}",
                "sortnum": sortnum,
                "pages": pages_arr,
            }
            (entry_dir / f"{eid}.json").write_text(
                json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
            )
    book_root = ROOT.parent
    import shutil
    for lang in ("zh_cn", "en_us"):
        dest = book_root / lang
        if dest.exists():
            shutil.rmtree(dest)
        shutil.copytree(ROOT, dest)
    print(f"Generated {len(CATEGORIES)} categories, {sum(len(v) for v in ENTRIES.values())} entries")
    print("Synced zh_cn + en_us from zh_tw")

if __name__ == "__main__":
    main()
