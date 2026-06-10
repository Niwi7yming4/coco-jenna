#!/usr/bin/env python3
"""Generate 36 mausoleum StructureTemplate NBT files (6 types x 6 regions)."""
from __future__ import annotations
import importlib.util
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "src/main/resources/data/cocojenna/structures/mausoleum"

TYPES = ["paper_harem", "terracotta", "library", "tea_garden", "observatory", "sleeping_chamber"]
REGIONS = ["yellow_river", "jiangnan", "western", "northern", "coastal", "mountain"]

REGION_WOOL = {
    "yellow_river": "minecraft:yellow_wool",
    "jiangnan": "minecraft:pink_wool",
    "western": "minecraft:orange_wool",
    "northern": "minecraft:white_wool",
    "coastal": "minecraft:light_blue_wool",
    "mountain": "minecraft:gray_wool",
}

REGION_FLOOR = {
    "yellow_river": "minecraft:packed_mud",
    "jiangnan": "minecraft:bamboo_planks",
    "western": "minecraft:terracotta",
    "northern": "minecraft:stone_bricks",
    "coastal": "minecraft:sand",
    "mountain": "minecraft:deepslate_bricks",
}


def load_builder():
    spec = importlib.util.spec_from_file_location(
        "gen_biome", ROOT / "tools" / "gen_biome_structure_nbt.py")
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


def build_mausoleum(g, mtype: str, region: str) -> list:
    b = g
    blocks: list = []
    floor = REGION_FLOOR[region]
    accent = REGION_WOOL[region]
    b.fill_box(blocks, 0, 0, 0, 12, 0, 12, floor)
    for y in range(1, 5):
        for x in range(13):
            for z in range(13):
                if x in (0, 12) or z in (0, 12) or y == 4:
                    b.set_block(blocks, x, y, z, "minecraft:deepslate_bricks")
    b.set_block(blocks, 6, 1, 0, "cocojenna:suspicious_wall")

    if mtype == "paper_harem":
        b.fill_box(blocks, 3, 1, 3, 9, 1, 9, "minecraft:red_wool")
        b.set_block(blocks, 6, 2, 6, "cocojenna:scratching_post")
        b.set_block(blocks, 4, 2, 4, accent)
        b.set_block(blocks, 8, 2, 8, accent)
    elif mtype == "terracotta":
        for x in range(2, 11, 2):
            b.set_block(blocks, x, 1, 6, "minecraft:terracotta")
            b.set_block(blocks, x, 2, 6, "minecraft:armor_stand")
    elif mtype == "library":
        for x in range(2, 11):
            b.set_block(blocks, x, 1, 3, "minecraft:bookshelf")
            b.set_block(blocks, x, 2, 3, "minecraft:bookshelf")
        b.set_block(blocks, 6, 1, 9, "cocojenna:picture_book_stand")
    elif mtype == "tea_garden":
        b.set_block(blocks, 5, 1, 5, "cocojenna:catnip")
        b.set_block(blocks, 7, 1, 5, "minecraft:cocoa")
        b.set_block(blocks, 6, 1, 7, "minecraft:campfire")
    elif mtype == "observatory":
        b.fill_box(blocks, 4, 4, 4, 8, 4, 8, "minecraft:glass")
        b.set_block(blocks, 6, 1, 6, "cocojenna:moonstone_cluster")
        b.set_block(blocks, 6, 5, 6, "minecraft:end_rod")
    elif mtype == "sleeping_chamber":
        b.set_block(blocks, 6, 1, 6, "cocojenna:woven_wool")
        b.set_block(blocks, 6, 2, 6, "cocojenna:cat_bed")

    b.set_block(blocks, 6, 1, 10, "minecraft:chest")
    b.set_block(blocks, 2, 1, 2, accent)
    b.set_block(blocks, 10, 1, 10, accent)
    return blocks


def main() -> None:
    g = load_builder()
    OUT.mkdir(parents=True, exist_ok=True)
    count = 0
    for mtype in TYPES:
        for region in REGIONS:
            vid = f"{mtype}_{region}"
            blocks = build_mausoleum(g, mtype, region)
            payload = g.build_structure(blocks)
            (OUT / f"{vid}.nbt").write_bytes(payload)
            count += 1
        # fallback base type NBT
        blocks = build_mausoleum(g, mtype, "yellow_river")
        (OUT / f"{mtype}.nbt").write_bytes(g.build_structure(blocks))
        count += 1
    print(f"Wrote {count} mausoleum NBT files to {OUT}")


if __name__ == "__main__":
    main()
