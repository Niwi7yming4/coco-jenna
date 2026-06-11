#!/usr/bin/env python3
"""Downscale oversized item/GUI PNGs (proportional letterbox) + lossless PNG compression."""

from __future__ import annotations

import argparse
import shutil
import subprocess
import sys
from pathlib import Path

from PIL import Image

TOOLS = Path(__file__).resolve().parent
sys.path.insert(0, str(TOOLS))
from texture_fit import fit_rect, fit_square_hq  # noqa: E402

ROOT = TOOLS.parent
TEXTURES = ROOT / "src/main/resources/assets/cocojenna/textures"

ITEM_32_PREFIXES = (
    "ryokatana_", "daikata_", "daikatana_", "cloak_", "memory_book", "paw_stamp",
    "schrodingers_", "tarot_", "blackjack_", "musou_", "sealed_memory",
    "twin_star_", "rainbow_yarn", "promo_", "spawn_egg", "shadow_claw",
    "sanhua_", "qin_", "musou_mad",
)
ITEM_32_NAMES = frozenset({
    "coco_special_meal", "jenna_special_meal", "premium_fish_can", "memory_book",
    "paw_stamp", "tarot_deck", "blackjack_chip", "musou_mad_card",
})

REGION_W, REGION_H = 128, 74
WORLD_MAP_SIZE = (256, 160)
WORLD_MAP_THUMB = (128, 80)
# GUI subtrees that keep native resolution (gal 854×480, lore 128×96, patchouli 854×480).
COMPRESS_ONLY_GUI_PREFIXES = ("gal/", "lore/", "patchouli/")


def item_target(name: str) -> int:
    stem = Path(name).stem
    if stem in ITEM_32_NAMES:
        return 32
    if any(stem.startswith(p) for p in ITEM_32_PREFIXES):
        return 32
    return 16


def is_compress_only_gui(path: Path) -> bool:
    rel = path.relative_to(TEXTURES / "gui").as_posix()
    return rel.startswith(COMPRESS_ONLY_GUI_PREFIXES)


def gui_target(path: Path) -> tuple[int, int] | None:
    rel = path.relative_to(TEXTURES / "gui").as_posix()
    if is_compress_only_gui(path):
        return None
    if rel.startswith("skills/"):
        return (32, 32)
    if rel.startswith("cards/"):
        return (64, 64)
    if rel.startswith("portraits/"):
        return (128, 128)
    if rel.startswith("regions/"):
        return (REGION_W, REGION_H)
    if rel.startswith("tabs/"):
        return (64, 64)
    name = path.name
    if name == "world_map.png":
        return WORLD_MAP_SIZE
    if name == "world_map_thumb.png":
        return WORLD_MAP_THUMB
    if name in ("purify_paw.png",):
        return (64, 64)
    if path.parent == TEXTURES / "gui":
        return (32, 32)
    return None


def needs_resize(img: Image.Image, tw: int, th: int) -> bool:
    w, h = img.size
    if w == tw and h == th:
        return False
    return w > tw or h > th or max(w, h) > max(tw, th) * 1.5


def resize_image(img: Image.Image, tw: int, th: int) -> Image.Image:
    if tw == th:
        return fit_square_hq(img, tw, margin_ratio=0.04)
    return fit_rect(img, tw, th, margin_ratio=0.04)


def compress_png(path: Path, oxipng: str | None) -> int:
    before = path.stat().st_size
    if oxipng:
        r = subprocess.run(
            [oxipng, "-o", "4", "--strip", "safe", "--preserve", str(path)],
            capture_output=True,
            text=True,
        )
        if r.returncode == 0:
            return before - path.stat().st_size
    # Pillow fallback (lossless zlib)
    img = Image.open(path)
    img.save(path, format="PNG", optimize=True, compress_level=9)
    return before - path.stat().st_size


def find_oxipng() -> str | None:
    for name in ("oxipng", "oxipng.exe"):
        p = shutil.which(name)
        if p:
            return p
    return None


def process_file(path: Path, tw: int, th: int, *, dry_run: bool, oxipng: str | None) -> tuple[bool, int]:
    img = Image.open(path)
    changed = False
    saved = 0
    if needs_resize(img, tw, th):
        if dry_run:
            print(f"  resize {path.relative_to(ROOT)} {img.size} -> {tw}x{th}")
            return True, 0
        out = resize_image(img, tw, th)
        out.save(path, format="PNG", optimize=True, compress_level=9)
        changed = True
    if not dry_run:
        saved += compress_png(path, oxipng)
    return changed, saved


def folder_bytes(folder: Path) -> int:
    return sum(f.stat().st_size for f in folder.rglob("*.png"))


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    oxipng = find_oxipng()
    if oxipng:
        print(f"Using oxipng: {oxipng}")
    else:
        print("oxipng not found; using Pillow zlib (install oxipng for better compression)")

    before_item = folder_bytes(TEXTURES / "item")
    before_gui = folder_bytes(TEXTURES / "gui")

    resized = 0
    bytes_saved = 0

    for path in sorted((TEXTURES / "item").rglob("*.png")):
        t = item_target(path.name)
        c, s = process_file(path, t, t, dry_run=args.dry_run, oxipng=oxipng)
        if c:
            resized += 1
            bytes_saved += s

    for path in sorted((TEXTURES / "gui").rglob("*.png")):
        if is_compress_only_gui(path):
            continue
        target = gui_target(path)
        if target is None:
            print(f"  warn: no resize target for {path.relative_to(ROOT)}")
            continue
        tw, th = target
        c, s = process_file(path, tw, th, dry_run=args.dry_run, oxipng=oxipng)
        if c:
            resized += 1
            bytes_saved += s

    if not args.dry_run:
        for path in sorted((TEXTURES / "item").rglob("*.png")) + sorted(
            (TEXTURES / "gui").rglob("*.png")
        ):
            bytes_saved += compress_png(path, oxipng)

    after_item = folder_bytes(TEXTURES / "item") if not args.dry_run else before_item
    after_gui = folder_bytes(TEXTURES / "gui") if not args.dry_run else before_gui

    print(f"Resized: {resized} files")
    if not args.dry_run:
        print(f"item: {before_item/1e6:.2f} MB -> {after_item/1e6:.2f} MB")
        print(f"gui:  {before_gui/1e6:.2f} MB -> {after_gui/1e6:.2f} MB")
        print(f"Total saved ~ {(before_item+before_gui-after_item-after_gui)/1e6:.2f} MB")


if __name__ == "__main__":
    main()
