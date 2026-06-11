#!/usr/bin/env python3
"""Lang keys for Patchouli illustration captions."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang"

KEYS_TW = {
    "patchouli.cocojenna.entry.prologue.img": "雨後廊下的可可與珍奶。",
    "patchouli.cocojenna.entry.meet_jenna.img": "月光窗邊的珍奶。",
    "patchouli.cocojenna.entry.first_cry_village.img": "初啼村聖樹與八區輪廓。",
    "patchouli.cocojenna.entry.bond.img": "滿月廣場下的羈絆之夜。",
    "patchouli.cocojenna.entry.penetration_gray_whisker.img": "主世界滲透：灰鬚小屋。",
    "patchouli.cocojenna.entry.what_is_mud.img": "黑泥腐化蔓延示意。",
    "patchouli.cocojenna.entry.promotion.img": "序列晉升三選一卡牌。",
    "patchouli.cocojenna.entry.iron_claw.img": "鐵爪鍛造爐火光。",
    "patchouli.cocojenna.entry.four_mad_legend.img": "四狂傳說競技場。",
    "patchouli.cocojenna.entry.shadow_claw.img": "終局：影爪降臨。",
}

KEYS_EN = {
    "patchouli.cocojenna.entry.prologue.img": "Coco and Jenna on the porch after rain.",
    "patchouli.cocojenna.entry.meet_jenna.img": "Jenna by the moonlit window.",
    "patchouli.cocojenna.entry.first_cry_village.img": "First Cry Village sacred tree.",
    "patchouli.cocojenna.entry.bond.img": "Bond night at the full-moon plaza.",
    "patchouli.cocojenna.entry.penetration_gray_whisker.img": "Gray Whisker's hut in the overworld.",
    "patchouli.cocojenna.entry.what_is_mud.img": "Black mud corruption spread.",
    "patchouli.cocojenna.entry.promotion.img": "Sequence promotion card choice.",
    "patchouli.cocojenna.entry.iron_claw.img": "Ironpaw forge glow.",
    "patchouli.cocojenna.entry.four_mad_legend.img": "Four Madmen arena legend.",
    "patchouli.cocojenna.entry.shadow_claw.img": "Endgame: Shadow Claw arrives.",
}

for lang_file, keys in [("zh_tw.json", KEYS_TW), ("en_us.json", KEYS_EN), ("zh_cn.json", {k: v.replace("絨", "绒") for k, v in KEYS_TW.items()})]:
    p = ROOT / lang_file
    data = json.loads(p.read_text(encoding="utf-8"))
    data.update(keys)
    p.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
print("Added", len(KEYS_TW), "patchouli illustration lang keys")
