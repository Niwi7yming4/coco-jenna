#!/usr/bin/env python3
"""Insert Patchouli image pages for guardian_guide entries (三語系)."""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "src/main/resources/assets/cocojenna/patchouli_books/guardian_guide"
LANGS = ("zh_tw", "en_us", "zh_cn")

# category/entry -> illustration asset (under textures/gui/patchouli/)
ILLUSTRATED = {
    ("welcome", "prologue"): ("coco_jenna_porch_sunset", "patchouli.cocojenna.entry.prologue.img"),
    ("welcome", "meet_jenna"): ("coco_window_moonlight", "patchouli.cocojenna.entry.meet_jenna.img"),
    ("first_cry", "first_cry_village"): ("sacred_tree_overview", "patchouli.cocojenna.entry.first_cry_village.img"),
    ("first_cry", "bond"): ("moon_plaza_night", "patchouli.cocojenna.entry.bond.img"),
    ("penetration", "gray_whisker"): ("penetration_overworld", "patchouli.cocojenna.entry.penetration_gray_whisker.img"),
    ("combat", "what_is_mud"): ("black_mud_corruption", "patchouli.cocojenna.entry.what_is_mud.img"),
    ("sequence", "promotion"): ("sequence_promotion", "patchouli.cocojenna.entry.promotion.img"),
    ("forge", "iron_claw"): ("ironpaw_forge_glow", "patchouli.cocojenna.entry.iron_claw.img"),
    ("four_mad", "legend"): ("four_mad_arena", "patchouli.cocojenna.entry.four_mad_legend.img"),
    ("final", "shadow_claw"): ("shadow_claw_boss", "patchouli.cocojenna.entry.shadow_claw.img"),
}


def image_page(img: str, text_key: str) -> dict:
    return {
        "type": "patchouli:image",
        "images": [f"cocojenna:textures/gui/patchouli/{img}.png"],
        "border": True,
        "text": text_key,
    }


def patch_entry(path: Path, img: str, text_key: str) -> bool:
    if not path.exists():
        return False
    data = json.loads(path.read_text(encoding="utf-8"))
    pages = data.get("pages", [])
    if pages and pages[0].get("type") == "patchouli:image":
        return False
    data["pages"] = [image_page(img, text_key)] + pages
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return True


def main() -> None:
    count = 0
    for lang in LANGS:
        for (cat, entry), (img, text_key) in ILLUSTRATED.items():
            path = BOOK / lang / "entries" / cat / f"{entry}.json"
            if patch_entry(path, img, text_key):
                count += 1
                print(f"Patched {lang}/{cat}/{entry}")
    print(f"Done: {count} entries updated with illustration pages")


if __name__ == "__main__":
    main()
