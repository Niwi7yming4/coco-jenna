#!/usr/bin/env python3
"""Generate armor layer textures + inventory icons for mod armor sets."""
from __future__ import annotations

import json
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
ARMOR_TEX = ROOT / "src/main/resources/assets/cocojenna/textures/models/armor"
ITEM_TEX = ROOT / "src/main/resources/assets/cocojenna/textures/item"
ITEM_MODELS = ROOT / "src/main/resources/assets/cocojenna/models/item"
REF_L1 = Path(__file__).resolve().parent / "_ref_leather_layer_1.png"
REF_L2 = Path(__file__).resolve().parent / "_ref_leather_layer_2.png"

SETS = {
    "velvet_beginner": {
        "base": (140, 118, 96),
        "highlight": (176, 152, 124),
        "shadow": (96, 78, 62),
    },
    "moonlight": {
        "base": (168, 178, 210),
        "highlight": (210, 218, 240),
        "shadow": (108, 118, 158),
    },
}

ARMOR_ITEMS = {
    "velvet_beginner_helmet": ("velvet_beginner", "helm"),
    "velvet_beginner_chestplate": ("velvet_beginner", "chest"),
    "velvet_beginner_leggings": ("velvet_beginner", "legs"),
    "velvet_beginner_boots": ("velvet_beginner", "boots"),
    "moonlight_helmet": ("moonlight", "helm"),
    "moonlight_chestplate": ("moonlight", "chest"),
    "moonlight_leggings": ("moonlight", "legs"),
    "moonlight_boots": ("moonlight", "boots"),
}


def lerp(a: int, b: int, t: float) -> int:
    return int(a + (b - a) * t)


def recolor_layer(src: Image.Image, palette: dict) -> Image.Image:
    """Recolor leather armor template while preserving alpha."""
    base = palette["base"]
    hi = palette["highlight"]
    lo = palette["shadow"]
    out = Image.new("RGBA", src.size, (0, 0, 0, 0))
    px = src.convert("RGBA").load()
    op = out.load()
    w, h = src.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if a < 8:
                continue
            lum = (r * 0.299 + g * 0.587 + b * 0.114) / 255.0
            if lum > 0.72:
                c = hi
            elif lum < 0.35:
                c = lo
            else:
                t = (lum - 0.35) / 0.37
                c = tuple(lerp(lo[i], base[i], t) if t < 0.5 else lerp(base[i], hi[i], (t - 0.5) * 2)
                          for i in range(3))
            op[x, y] = (*c, a)
    return out


def write_json(path: Path, data: dict) -> None:
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def draw_item_icon(kind: str, palette: dict) -> Image.Image:
    base = palette["base"]
    hi = palette["highlight"]
    lo = palette["shadow"]
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse([7, 23, 25, 29], fill=(0, 0, 0, 40))
    if kind == "helm":
        d.arc([8, 12, 24, 24], 200, 340, fill=(*base, 255), width=4)
        d.arc([10, 14, 22, 22], 210, 330, fill=(*hi, 255), width=2)
    elif kind == "chest":
        d.rectangle([10, 9, 22, 23], fill=(*base, 255))
        d.rectangle([11, 10, 21, 22], fill=(*hi, 255))
        d.line([(10, 12), (22, 12)], fill=(*lo, 255))
    elif kind == "legs":
        d.rectangle([10, 8, 15, 24], fill=(*base, 255))
        d.rectangle([17, 8, 22, 24], fill=(*base, 255))
        d.rectangle([11, 9, 14, 23], fill=(*hi, 180))
        d.rectangle([18, 9, 21, 23], fill=(*hi, 180))
    else:  # boots
        d.rectangle([9, 16, 15, 24], fill=(*base, 255))
        d.rectangle([17, 16, 23, 24], fill=(*base, 255))
        d.rectangle([8, 22, 16, 25], fill=(*lo, 255))
        d.rectangle([16, 22, 24, 25], fill=(*lo, 255))
    return im


def ensure_ref_templates() -> tuple[Image.Image, Image.Image]:
    if not REF_L1.is_file() or not REF_L2.is_file():
        raise SystemExit(
            "Missing leather armor reference — download to tools/_ref_leather_layer_1.png "
            "and _ref_leather_layer_2.png first."
        )
    return Image.open(REF_L1).convert("RGBA"), Image.open(REF_L2).convert("RGBA")


def main() -> None:
    ARMOR_TEX.mkdir(parents=True, exist_ok=True)
    ITEM_TEX.mkdir(parents=True, exist_ok=True)
    ITEM_MODELS.mkdir(parents=True, exist_ok=True)

    l1, l2 = ensure_ref_templates()
    for name, palette in SETS.items():
        recolor_layer(l1, palette).save(ARMOR_TEX / f"{name}_layer_1.png")
        recolor_layer(l2, palette).save(ARMOR_TEX / f"{name}_layer_2.png")
        print(f"  armor/{name}_layer_1.png + layer_2")

    for item_id, (set_name, kind) in ARMOR_ITEMS.items():
        icon = draw_item_icon(kind, SETS[set_name])
        icon.save(ITEM_TEX / f"{item_id}.png")
        write_json(ITEM_MODELS / f"{item_id}.json", {
            "parent": "minecraft:item/generated",
            "textures": {"layer0": f"cocojenna:item/{item_id}"},
        })
        print(f"  item/{item_id}.png")

    print(f"Done: {len(SETS)} armor sets, {len(ARMOR_ITEMS)} item icons.")


if __name__ == "__main__":
    main()
