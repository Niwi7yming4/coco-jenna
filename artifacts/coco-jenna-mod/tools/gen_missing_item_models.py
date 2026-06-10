#!/usr/bin/env python3
"""Create item/generated models for textures that lack JSON models."""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
TEX = ROOT / "src/main/resources/assets/cocojenna/textures/item"
MODELS = ROOT / "src/main/resources/assets/cocojenna/models/item"
BLOCK_MODELS = ROOT / "src/main/resources/assets/cocojenna/models/block"


def write_generated(name: str) -> None:
    path = MODELS / f"{name}.json"
    if path.exists():
        return
    model = {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"cocojenna:item/{name}"},
    }
    path.write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8")
    print(f"  item/{name}.json")


def write_block_item(name: str) -> None:
    path = MODELS / f"{name}.json"
    if path.exists():
        return
    block = BLOCK_MODELS / f"{name}.json"
    if not block.is_file():
        return
    model = {"parent": f"cocojenna:block/{name}"}
    path.write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8")
    print(f"  item/{name}.json (block)")


def ensure_elixir_assets() -> None:
    """藥劑有註冊但可能缺貼圖／模型。"""
    try:
        from PIL import Image, ImageDraw
    except ImportError:
        return
    specs = {
        "strength_elixir": (210, 70, 70),
        "speed_elixir": (70, 190, 110),
        "life_elixir": (190, 90, 200),
    }
    TEX.mkdir(parents=True, exist_ok=True)
    for name, color in specs.items():
        tex = TEX / f"{name}.png"
        if not tex.exists():
            im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
            d = ImageDraw.Draw(im)
            d.rectangle([4, 2, 11, 13], fill=(*color, 255))
            d.rectangle([5, 1, 10, 3], fill=(220, 220, 230, 255))
            im.save(tex)
            print(f"  texture/item/{name}.png")
        write_generated(name)


def main() -> None:
    MODELS.mkdir(parents=True, exist_ok=True)
    ensure_elixir_assets()
    print("Item models from textures:")
    for tex in sorted(TEX.glob("*.png")):
        write_generated(tex.stem)
    print("Block item models:")
    for block in sorted(BLOCK_MODELS.glob("*.json")):
        write_block_item(block.stem)
    print("Done.")


if __name__ == "__main__":
    main()
