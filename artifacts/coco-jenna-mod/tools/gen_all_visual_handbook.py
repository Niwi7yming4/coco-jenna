#!/usr/bin/env python3
"""從《貓之國相關圖片》切片 + 程式生成，覆蓋視覺辨識手冊全物件材質。"""
from __future__ import annotations

import subprocess
import sys
from pathlib import Path

TOOLS = Path(__file__).resolve().parent
MOD = TOOLS.parent
TEXTURES = MOD / "src/main/resources/assets/cocojenna/textures"


def run(script: str) -> None:
    path = TOOLS / script
    print(f"\n=== {script} ===")
    subprocess.check_call([sys.executable, str(path)], cwd=MOD)


def audit() -> None:
    item_tex = TEXTURES / "item"
    models = MOD / "src/main/resources/assets/cocojenna/models/item"
    missing: list[str] = []
    for m in sorted(models.glob("*.json")):
        name = m.stem
        if name.startswith(("ryokatana_", "daikatana_", "musou_", "cloak_")):
            if not (item_tex / f"{name}.png").exists():
                missing.append(name)
    egg_missing = [
        e for e in [
            "coco_spawn_egg", "jenna_spawn_egg", "cheshire_spawn_egg",
            "white_glove_spawn_egg", "alpha_spawn_egg",
        ]
        if not (item_tex / f"{e}.png").exists()
    ]
    print(f"\n=== Audit ===")
    print(f"Textures total: {len(list(TEXTURES.rglob('*.png')))}")
    print(f"Missing weapon/cloak: {len(missing)}")
    for x in missing[:15]:
        print(f"  - {x}")
    if egg_missing:
        print(f"Missing NPC spawn eggs: {', '.join(egg_missing)}")
    gui = ["dialog_frame", "button_normal", "cooldown_arc"]
    for g in gui:
        p = TEXTURES / "gui" / f"{g}.png"
        print(f"  GUI {g}: {'OK' if p.exists() else 'MISSING'}")


def main() -> None:
    # 順序：切片(僅物品) → 實體 → 方塊(程式) → 材料圖示(程式) → GUI 覆蓋
    run("slice_sprites_v3.py")
    run("gen_entity_textures.py")
    run("gen_all_block_textures.py")
    run("gen_handbook_item_icons.py")
    run("import_ref_textures.py")
    run("gen_spawn_egg_models.py")
    run("gen_missing_handbook_assets.py")
    run("gen_missing_item_models.py")
    run("gen_layered_assets.py")
    run("generate_placeholders.py")
    audit()


if __name__ == "__main__":
    main()
