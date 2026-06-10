import json
from pathlib import Path

ROOT = Path(r"c:\Users\ASUS\Desktop\Cat-Country-Forge\artifacts\coco-jenna-mod\src\main\resources")
extra = {}
for frag_name in (
    "guardian_guide_patchouli_zh_tw.json.fragment",
    "black_mud_abyss_patchouli_zh_tw.json.fragment",
):
    frag = ROOT / "assets/cocojenna/lang" / frag_name
    if not frag.is_file():
        continue
    for line in frag.read_text(encoding="utf-8").splitlines():
        line = line.strip().rstrip(",")
        if not line.startswith('"'):
            continue
        k, _, v = line.partition(": ")
        k = k.strip('"')
        v = v.strip().strip('"')
        extra[k] = v

ICON_FIX = {
    "iron_claw_hammer": "precision_gear",
    "cat_bed": "rainbow_yarn_ball",
    "abyss_distiller": "black_mud_remnant",
    "meme_badge": "memory_book",
    "memory_fragment": "memory_particle",
    "memory_essence": "memory_particle",
    "sealed_relic": "schrodingers_box",
    "velvet_wood": "velvet_fur",
    "catnip_coin": "catnip_item",
    "abyss_black_mud": "black_mud_remnant",
    "ryokatana_velvet_whisper": "grooming_brush",
    "ryokatana_coco_guardian": "purr_crystal",
}

for p in (ROOT / "assets/cocojenna/patchouli_books/guardian_guide/zh_tw/entries").rglob("*.json"):
    data = json.loads(p.read_text(encoding="utf-8"))
    icon = data.get("icon", "")
    if icon.startswith("cocojenna:"):
        name = icon.split(":", 1)[1]
        if name in ICON_FIX:
            data["icon"] = f"cocojenna:{ICON_FIX[name]}"
            p.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

for lang in ["zh_tw.json", "zh_cn.json"]:
    p = ROOT / "assets/cocojenna/lang" / lang
    data = json.loads(p.read_text(encoding="utf-8"))
    data.update(extra)
    hint_tw = "右鍵閱讀《守護者指南》；或安裝 Patchouli 獲得翻頁書體驗。"
    hint_cn = "右键阅读《守护者指南》；或安装 Patchouli 获得翻页书体验。"
    data["guide.cocojenna.open_hint"] = hint_tw if "tw" in lang else hint_cn
    p.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

print("merged", len(extra), "lang keys; icons fixed")
