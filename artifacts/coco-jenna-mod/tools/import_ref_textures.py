#!/usr/bin/env python3
"""Import sliced reference art into renderer texture paths (slime UV + boss)."""

from __future__ import annotations

import sys
from pathlib import Path

from PIL import Image

TOOLS = Path(__file__).resolve().parent
sys.path.insert(0, str(TOOLS))
from texture_fit import fit_rect, fit_square

ROOT = Path(__file__).resolve().parent.parent
BLACKMUD = ROOT / "src/main/resources/assets/cocojenna/textures/entity/blackmud"
MUD_OUT = ROOT / "src/main/resources/assets/cocojenna/textures/entity/black_mud"
BOSS_OUT = ROOT / "src/main/resources/assets/cocojenna/textures/entity/boss"

SLIME_MOBS = {
    "mud_heat_leech": "heat_leech",
    "mud_forgotten_wisp": "forgotten_wisp",
    "mud_whispering_doll": "whispering_doll",
    "mud_memory_moth": "memory_moth",
    "mud_mimic_cat": "mimic_cat",
}

BOSS_MOBS = {
    "mud_grief_amalgam": "grief_amalgam",
    "mud_blind_water_lord": "blind_water_lord",
    "mud_fallen_velvet": "fallen_velvet",
    "mud_primal_chaos": "primal_chaos",
}


def to_slime_uv(src: Image.Image) -> Image.Image | None:
    """Place reference sprite on both slime blobs (64×32 UV)."""
    src = src.convert("RGBA")
    if not src.getbbox():
        return None
    # HQ 128 slice → proportional fit into slime blob area, then letterbox 64×32
    sprite = fit_rect(src, 30, 26, margin_ratio=0.04)
    out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))

    def paste_blob(cx: int, cy_center: int) -> None:
        x = int(cx - sprite.width / 2)
        y = int(cy_center - sprite.height / 2)
        out.paste(sprite, (x, y), sprite)

    paste_blob(16, 14)
    paste_blob(48, 13)
    return out


def to_boss_cat_uv(src: Image.Image) -> Image.Image | None:
    """參考圖 → 64×32 貓 UV（OcelotModel），避免 PlayerModel 破圖."""
    src = src.convert("RGBA")
    if not src.getbbox():
        return None
    sprite = fit_rect(src, 24, 16, margin_ratio=0.04)
    out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    # 身體主區（Ocelot UV 軀幹）
    out.paste(sprite, (20, 6), sprite)
    # 頭部左側
    head = fit_rect(src, 10, 8, margin_ratio=0.06)
    out.paste(head, (3, 2), head)
    # 頭部右側貼圖
    out.paste(head, (41, 2), head)
    return out


def save_converted(dest: Path, image: Image.Image) -> None:
    dest.parent.mkdir(parents=True, exist_ok=True)
    image.save(dest)
    print(f"  {dest.relative_to(ROOT)}")


def main() -> None:
    count = 0
    print("Slime mob textures (black_mud/):")
    for src_name, dest_name in SLIME_MOBS.items():
        src = BLACKMUD / f"{src_name}.png"
        if not src.is_file():
            print(f"  skip missing {src_name}")
            continue
        out = to_slime_uv(Image.open(src))
        if out is None:
            continue
        save_converted(MUD_OUT / f"{dest_name}.png", out)
        count += 1

    print("Boss textures (boss/):")
    for src_name, dest_name in BOSS_MOBS.items():
        src = BLACKMUD / f"{src_name}.png"
        if not src.is_file():
            print(f"  skip missing {src_name}")
            continue
        out = to_boss_cat_uv(Image.open(src))
        if out is None:
            continue
        save_converted(BOSS_OUT / f"{dest_name}.png", out)
        count += 1

    print(f"Imported {count} entity textures from reference sheets.")


if __name__ == "__main__":
    main()
