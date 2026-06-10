#!/usr/bin/env python3
"""Slice Gemini sprite sheets into 16x16 Minecraft textures with background removal."""

from __future__ import annotations

import os
from pathlib import Path

from PIL import Image

ASSETS_DIR = Path(
    r"C:\Users\ASUS\.cursor\projects\c-Users-ASUS-Desktop-Cat-Country-Forge\assets"
)
OUT_ITEM = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/textures/item"
OUT_BLOCK = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/textures/block"
OUT_SIZE = 16

# (partial filename key, cols, rows, top%, bottom%, left%, right%, [(name, folder), ...])
SHEETS: list[tuple] = [
    (
        "1w6pa51w6pa51w6p",
        4,
        2,
        0.11,
        0.10,
        0.02,
        0.02,
        [
            ("memory_book", "item"),
            ("paw_stamp", "item"),
            ("distiller", "block"),
            ("cat_bed", "block"),
            ("food_bowl", "block"),
            ("scratching_post", "block"),
            ("aroma_distiller", "block"),
            ("ironpaw_forge", "block"),
        ],
    ),
    (
        "g550e1g550e1g550",
        3,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            ("hibiscus_flower_item", "item"),
            ("neon_mushroom_item", "item"),
            ("dandelion_fluff", "item"),
            ("full_moon_spectrum", "item"),
            ("catnip_item", "item"),
            ("stardust_soil_item", "item"),
            ("spore_fruit", "item"),
        ],
    ),
    (
        "igjvu2igjvu2igjv",
        5,
        2,
        0.11,
        0.10,
        0.02,
        0.02,
        [
            ("premium_fish_can", "item"),
            ("basic_fish_puree", "item"),
            ("glow_fish_soup", "item"),
            ("crab_deluxe", "item"),
            ("hibiscus_sashimi", "item"),
            ("silvervine_biscuit", "item"),
            ("holy_water", "item"),
            ("hibiscus_tear", "item"),
            ("nine_lives_catnip", "item"),
            ("deep_sea_risotto", "item"),
        ],
    ),
    (
        "b1wta5b1wta5b1wt",
        6,
        1,
        0.14,
        0.12,
        0.02,
        0.02,
        [
            ("deep_sea_fish", "item"),
            ("ryokatana_dark_tide", "item"),
            ("toy_squeak", "item"),
            ("ryokatana_hibiscus_blood", "item"),
            ("rusted_anchor", "item"),
            ("sequence_manual", "item"),
        ],
    ),
    (
        "cbo4bxcbo4bxcbo4",
        5,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            ("cloak_anti_corrosion", "item"),
            ("cloak_moonlight", "item"),
            ("cloak_memory", "item"),
            ("cloak_guardian", "item"),
            ("cloak_warm", "item"),
            ("moonlight_collar", "item"),
            ("jennas_old_bell", "item"),
            ("stardust_ring", "item"),
            ("blackjack_chip", "item"),
            ("purr_coin", "item"),
            ("velvet_tail_cape", "item"),
            ("blind_water_gel", "item"),
            ("spore_powder", "item"),
        ],
    ),
    (
        "ofmb8gofmb8gofmb",
        5,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            ("ryokatana_lament_split", "item"),
            ("ryokatana_iron_rust_armor_break", "item"),
            ("ryokatana_blind_water_abyss", "item"),
            ("ryokatana_neon_flash", "item"),
            ("ryokatana_velvet_whisper", "item"),
            ("tarot_deck", "item"),
            ("ryokatana_origami_cut", "item"),
            ("rainbow_yarn_ball", "item"),
            ("ryokatana_screen_noise", "item"),
            ("ryokatana_moonlight_ripple", "item"),
            ("ryokatana_gear_precision_2", "item"),
            ("ryokatana_gear_schedule", "item"),
            ("ryokatana_gear_windup", "item"),
            ("ryokatana_fish_bone_tide", "item"),
            ("ryokatana_milk_tea_play", "item"),
        ],
    ),
    (
        "7nh6gs7nh6gs7nh6",
        5,
        1,
        0.14,
        0.12,
        0.02,
        0.02,
        [
            ("fish_bone_blade", "item"),
            ("yarn_ball_staff", "item"),
            ("pawprint_dagger", "item"),
            ("cat_bell_offhand", "item"),
            ("silvervine_bomb", "item"),
        ],
    ),
    (
        "ohesjaohesjaohes",
        3,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            ("heat_leech_spawn_egg", "item"),
            ("forgotten_wisp_spawn_egg", "item"),
            ("whispering_doll_spawn_egg", "item"),
            ("memory_moth_spawn_egg", "item"),
            ("mimic_cat_spawn_egg", "item"),
            ("grief_amalgam_spawn_egg", "item"),
            ("blind_water_lord_spawn_egg", "item"),
            ("fallen_velvet_spawn_egg", "item"),
            ("primal_chaos_spawn_egg", "item"),
        ],
    ),
    (
        "xggq9mxggq9mxggq",
        3,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            ("velvet_grass", "block"),
            ("moonstone_brick", "block"),
            ("stardust_soil", "block"),
            ("scratching_post", "block"),
            ("purr_crystal_block", "block"),
            ("schrodingers_box", "item"),
            ("velvet_block", "block"),
            ("blind_water_sample", "item"),
        ],
    ),
]


def find_sheet(key: str) -> Path:
    for p in ASSETS_DIR.glob("*.png"):
        if key in p.name:
            return p
    raise FileNotFoundError(f"Sheet not found for key: {key}")


def is_background(r: int, g: int, b: int, a: int) -> bool:
    if a < 10:
        return True
    # white borders / labels
    if r > 235 and g > 235 and b > 235:
        return True
    # light gray grid cells
    if abs(r - g) < 18 and abs(g - b) < 18 and 155 < r < 235:
        return True
    # checkerboard light
    if r > 210 and g > 210 and b > 210:
        return True
    # solid black backdrop (neon mushroom etc.)
    if r < 25 and g < 25 and b < 25:
        return True
    return False


def remove_background(img: Image.Image) -> Image.Image:
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if is_background(r, g, b, a):
                px[x, y] = (0, 0, 0, 0)
    return img


def trim_and_resize(img: Image.Image, size: int = OUT_SIZE) -> Image.Image:
    bbox = img.getbbox()
    if not bbox:
        return Image.new("RGBA", (size, size), (0, 0, 0, 0))
    cropped = img.crop(bbox)
    w, h = cropped.size
    side = max(w, h)
    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.paste(cropped, ((side - w) // 2, (side - h) // 2))
    return square.resize((size, size), Image.Resampling.NEAREST)


def slice_cell(
    sheet: Image.Image,
    col: int,
    row: int,
    cols: int,
    rows: int,
    top: float,
    bottom: float,
    left: float,
    right: float,
) -> Image.Image:
    w, h = sheet.size
    x0 = int(w * left)
    x1 = int(w * (1 - right))
    y0 = int(h * top)
    y1 = int(h * (1 - bottom))
    gw = x1 - x0
    gh = y1 - y0
    cw = gw // cols
    ch = gh // rows
    cx = x0 + col * cw
    cy = y0 + row * ch
    # shrink crop inward to skip cell borders
    pad_x = int(cw * 0.12)
    pad_y = int(ch * 0.14)
    return sheet.crop((cx + pad_x, cy + pad_y, cx + cw - pad_x, cy + ch - pad_y))


def out_path(name: str, folder: str) -> Path:
    base = OUT_BLOCK if folder == "block" else OUT_ITEM
    base.mkdir(parents=True, exist_ok=True)
    return base / f"{name}.png"


def process_sheet(
    key: str,
    cols: int,
    rows: int,
    top: float,
    bottom: float,
    left: float,
    right: float,
    mappings: list[tuple[str, str]],
) -> list[str]:
    path = find_sheet(key)
    sheet = Image.open(path).convert("RGBA")
    saved: list[str] = []
    idx = 0
    for row in range(rows):
        for col in range(cols):
            if idx >= len(mappings):
                break
            name, folder = mappings[idx]
            cell = slice_cell(sheet, col, row, cols, rows, top, bottom, left, right)
            cell = remove_background(cell)
            cell = trim_and_resize(cell)
            dest = out_path(name, folder)
            cell.save(dest)
            saved.append(f"{folder}/{name}.png")
            idx += 1
    return saved


def main() -> None:
    total = 0
    for entry in SHEETS:
        key, cols, rows, top, bottom, left, right, mappings = entry
        saved = process_sheet(key, cols, rows, top, bottom, left, right, mappings)
        print(f"[{key[:20]}...] -> {len(saved)} textures")
        for s in saved:
            print(f"  {s}")
        total += len(saved)
    print(f"\nDone: {total} textures written.")


if __name__ == "__main__":
    main()
