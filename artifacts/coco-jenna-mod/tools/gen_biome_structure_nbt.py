#!/usr/bin/env python3
"""Generate art-quality StructureTemplate NBT (Structure Block compatible gzip)."""

from __future__ import annotations

import gzip
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT_LARGE = ROOT / "src/main/resources/data/cocojenna/structures/biome_landmarks"
OUT_MEDIUM = OUT_LARGE / "medium"

TAG_END = 0
TAG_INT = 3
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11


def _write_bytes(buf: bytearray, data: bytes) -> None:
    buf.extend(data)


def _write_tag_header(buf: bytearray, tag_type: int, name: str) -> None:
    buf.append(tag_type)
    name_bytes = name.encode("utf-8")
    _write_bytes(buf, struct.pack(">H", len(name_bytes)))
    _write_bytes(buf, name_bytes)


def _write_int(buf: bytearray, name: str, value: int) -> None:
    _write_tag_header(buf, TAG_INT, name)
    _write_bytes(buf, struct.pack(">i", value))


def _write_int_array(buf: bytearray, name: str, values: list[int]) -> None:
    _write_tag_header(buf, TAG_INT_ARRAY, name)
    _write_bytes(buf, struct.pack(">i", len(values)))
    for v in values:
        _write_bytes(buf, struct.pack(">i", v))


def _write_list_compound(buf: bytearray, name: str, compounds: list[bytearray]) -> None:
    _write_tag_header(buf, TAG_LIST, name)
    buf.append(TAG_COMPOUND)
    _write_bytes(buf, struct.pack(">i", len(compounds)))
    for c in compounds:
        _write_bytes(buf, c)
        buf.append(TAG_END)


def _compound_block(pos: tuple[int, int, int], state: int) -> bytearray:
    c = bytearray()
    _write_int_array(c, "pos", list(pos))
    _write_int(c, "state", state)
    c.append(TAG_END)
    return c


def _compound_palette(block_id: str) -> bytearray:
    c = bytearray()
    _write_tag_header(c, TAG_STRING, "Name")
    data = block_id.encode("utf-8")
    _write_bytes(c, struct.pack(">H", len(data)))
    _write_bytes(c, data)
    c.append(TAG_END)
    return c


def build_structure(blocks: list[tuple[tuple[int, int, int], str]]) -> bytes:
    palette_ids: list[str] = []
    palette_index: dict[str, int] = {}
    entries: list[tuple[tuple[int, int, int], int]] = []
    for pos, bid in blocks:
        if bid not in palette_index:
            palette_index[bid] = len(palette_ids)
            palette_ids.append(bid)
        entries.append((pos, palette_index[bid]))
    max_x = max(p[0] for p, _ in entries) + 1
    max_y = max(p[1] for p, _ in entries) + 1
    max_z = max(p[2] for p, _ in entries) + 1
    root = bytearray()
    _write_int_array(root, "size", [max_x, max_y, max_z])
    _write_list_compound(root, "palette", [_compound_palette(b) for b in palette_ids])
    _write_list_compound(root, "blocks", [_compound_block(p, s) for p, s in entries])
    _write_list_compound(root, "entities", [])
    root.append(TAG_END)
    payload = bytearray()
    payload.append(TAG_COMPOUND)
    _write_bytes(payload, struct.pack(">H", 0))
    _write_bytes(payload, root)
    return gzip.compress(bytes(payload))


def set_block(blocks: list, x: int, y: int, z: int, mat: str) -> None:
    blocks.append(((x, y, z), mat))


def fill_box(blocks: list, x0: int, y0: int, z0: int, x1: int, y1: int, z1: int, mat: str) -> None:
    for x in range(min(x0, x1), max(x0, x1) + 1):
        for y in range(min(y0, y1), max(y0, y1) + 1):
            for z in range(min(z0, z1), max(z0, z1) + 1):
                set_block(blocks, x, y, z, mat)


def lollipop_tree(blocks: list, x: int, y: int, z: int, color: str) -> None:
    for h in range(4):
        set_block(blocks, x, y + h, z, "minecraft:white_concrete")
    for dx in range(-1, 2):
        for dz in range(-1, 2):
            for dy in range(2):
                if abs(dx) + abs(dz) + dy <= 2:
                    set_block(blocks, x + dx, y + 4 + dy, z + dz, color)


def wool_house(blocks: list, ox: int, oy: int, oz: int, wall: str, roof: str) -> None:
    fill_box(blocks, ox, oy, oz, ox + 4, oy, oz + 4, "minecraft:oak_planks")
    for y in range(1, 4):
        for x in range(5):
            for z in range(5):
                if x in (0, 4) or z in (0, 4):
                    set_block(blocks, ox + x, oy + y, oz + z, wall)
    fill_box(blocks, ox + 1, oy + 1, oz, ox + 1, oy + 2, oz, "minecraft:air")
    fill_box(blocks, ox, oy + 4, oz, ox + 4, oy + 4, oz + 4, roof)
    set_block(blocks, ox + 2, oy + 1, oz + 2, "cocojenna:food_bowl")


def torii(blocks: list, x: int, y: int, z: int) -> None:
    for h in range(5):
        set_block(blocks, x, y + h, z, "minecraft:stone_bricks")
        set_block(blocks, x + 6, y + h, z, "minecraft:stone_bricks")
    fill_box(blocks, x, y + 4, z, x + 6, y + 4, z, "minecraft:red_wool")
    fill_box(blocks, x, y + 5, z, x + 6, y + 5, z, "minecraft:red_wool")


def cat_village() -> list:
    blocks: list = []
    fill_box(blocks, 0, 0, 0, 28, 0, 28, "minecraft:grass_block")
    for i in range(0, 29, 4):
        fill_box(blocks, i, 0, 0, i, 0, 28, "minecraft:gravel")
        fill_box(blocks, 0, 0, i, 28, 0, i, "minecraft:gravel")
    wool_house(blocks, 2, 1, 2, "minecraft:white_wool", "minecraft:pink_wool")
    wool_house(blocks, 14, 1, 2, "minecraft:brown_wool", "minecraft:orange_wool")
    wool_house(blocks, 2, 1, 14, "minecraft:light_blue_wool", "minecraft:cyan_wool")
    wool_house(blocks, 14, 1, 14, "minecraft:red_wool", "minecraft:yellow_wool")
    wool_house(blocks, 8, 1, 8, "minecraft:lime_wool", "minecraft:green_wool")
    wool_house(blocks, 20, 1, 20, "minecraft:magenta_wool", "minecraft:purple_wool")
    set_block(blocks, 13, 1, 13, "minecraft:campfire")
    set_block(blocks, 12, 1, 13, "minecraft:barrel")
    set_block(blocks, 14, 1, 13, "cocojenna:yarn_ball_lamp")
    lollipop_tree(blocks, 6, 1, 22, "minecraft:pink_wool")
    lollipop_tree(blocks, 22, 1, 6, "minecraft:light_blue_wool")
    lollipop_tree(blocks, 24, 1, 24, "minecraft:yellow_wool")
    for dx in range(-2, 3):
        set_block(blocks, 13 + dx, 1, 10, "cocojenna:velvet_grass")
    return blocks


def nine_lives_shrine() -> list:
    blocks: list = []
    fill_box(blocks, 0, 0, 0, 16, 0, 16, "minecraft:stone_bricks")
    for y in range(1, 6):
        for x in range(17):
            for z in range(17):
                if x in (0, 16) or z in (0, 16):
                    set_block(blocks, x, y, z, "minecraft:stone_bricks")
    torii(blocks, 4, 1, 0)
    for i in range(9):
        dx = 3 + (i % 3) * 3
        dz = 5 + (i // 3) * 3
        set_block(blocks, dx, 1, dz, "minecraft:candle")
        set_block(blocks, dx, 2, dz, "cocojenna:neon_mushroom")
    set_block(blocks, 8, 1, 8, "cocojenna:cat_scratch_board")
    set_block(blocks, 8, 2, 8, "cocojenna:purr_crystal_block")
    set_block(blocks, 8, 3, 8, "minecraft:end_rod")
    fill_box(blocks, 6, 1, 14, 10, 1, 14, "cocojenna:velvet_carpet")
    return blocks


def velvet_palace_wing() -> list:
    blocks: list = []
    fill_box(blocks, 0, 0, 0, 22, 0, 16, "cocojenna:moonstone_brick")
    for y in range(1, 10):
        for x in range(23):
            for z in range(17):
                if x in (0, 22) or z in (0, 16) or y == 9:
                    set_block(blocks, x, y, z, "cocojenna:stardust_brick")
    for x in range(2, 21):
        for z in range(2, 15):
            set_block(blocks, x, 1, z, "cocojenna:velvet_carpet")
    set_block(blocks, 11, 1, 4, "cocojenna:cat_bed")
    set_block(blocks, 11, 2, 4, "cocojenna:purr_crystal_block")
    set_block(blocks, 11, 1, 12, "minecraft:bookshelf")
    set_block(blocks, 10, 1, 12, "minecraft:bookshelf")
    set_block(blocks, 12, 1, 12, "minecraft:bookshelf")
    set_block(blocks, 2, 1, 8, "minecraft:iron_bars")
    set_block(blocks, 20, 1, 8, "minecraft:iron_bars")
    for x in range(4, 19, 4):
        set_block(blocks, x, 8, 8, "minecraft:lantern")
    for x in range(3, 20, 3):
        set_block(blocks, x, 1, 15, "cocojenna:hibiscus_flower")
    set_block(blocks, 11, 1, 14, "cocojenna:yarn_ball_lamp")
    lollipop_tree(blocks, 1, 1, 1, "minecraft:pink_wool")
    lollipop_tree(blocks, 20, 1, 14, "minecraft:light_blue_wool")
    return blocks


def biome_landmark(wall: str, accent: str, floor: str, decor: str) -> list:
    blocks: list = []
    fill_box(blocks, 0, 0, 0, 20, 0, 20, floor)
    for y in range(1, 14):
        for x in range(21):
            for z in range(21):
                edge = x in (0, 20) or z in (0, 20) or y == 13
                if edge:
                    set_block(blocks, x, y, z, wall)
    for x in range(4, 17, 4):
        for z in range(4, 17, 4):
            set_block(blocks, x, 1, z, decor)
    set_block(blocks, 10, 12, 10, accent)
    set_block(blocks, 10, 1, 10, "cocojenna:cat_scratch_board")
    lollipop_tree(blocks, 3, 1, 3, "minecraft:pink_wool")
    lollipop_tree(blocks, 17, 1, 17, "minecraft:yellow_wool")
    fill_box(blocks, 8, 1, 0, 12, 3, 0, "minecraft:oak_planks")
    set_block(blocks, 10, 4, 0, "minecraft:lantern")
    return blocks


BIOME_BLOCKS = {
    "velvet_forest": ("minecraft:pink_terracotta", "cocojenna:cat_scratch_board", "minecraft:white_concrete", "cocojenna:velvet_grass"),
    "moon_alley": ("minecraft:stone_bricks", "cocojenna:moonstone_cluster", "minecraft:gray_concrete", "cocojenna:velvet_vine"),
    "first_cry_plains": ("minecraft:white_wool", "minecraft:campfire", "minecraft:pink_concrete", "cocojenna:cotton_candy_shrub"),
    "howling_gorge": ("minecraft:stone_bricks", "minecraft:bell", "minecraft:coarse_dirt", "minecraft:iron_bars"),
    "blind_water_river": ("minecraft:oak_planks", "minecraft:sea_lantern", "minecraft:light_blue_concrete", "minecraft:lantern"),
    "dawn_highlands": ("minecraft:grass_block", "minecraft:poppy", "minecraft:green_concrete", "cocojenna:catnip"),
    "forgotten_wastes": ("cocojenna:black_mud", "minecraft:candle", "minecraft:black_concrete", "cocojenna:neon_mushroom"),
    "cardboard_slums": ("minecraft:brown_wool", "minecraft:barrel", "minecraft:terracotta", "cocojenna:spore_fruit_node"),
    "moonlight_beach": ("minecraft:oak_planks", "minecraft:lantern", "minecraft:light_blue_concrete_powder", "minecraft:turtle_egg"),
    "rainbow_canyon": ("minecraft:red_terracotta", "cocojenna:purr_crystal_block", "minecraft:orange_terracotta", "minecraft:yellow_terracotta"),
    "catnip_highlands": ("minecraft:moss_block", "cocojenna:catnip", "minecraft:green_concrete", "cocojenna:velvet_grass"),
    "stardust_desert": ("minecraft:obsidian", "minecraft:end_rod", "minecraft:black_concrete_powder", "minecraft:glowstone"),
}


def main() -> None:
    OUT_LARGE.mkdir(parents=True, exist_ok=True)
    OUT_MEDIUM.mkdir(parents=True, exist_ok=True)
    for name, (wall, accent, floor, decor) in BIOME_BLOCKS.items():
        data = build_structure(biome_landmark(wall, accent, floor, decor))
        (OUT_LARGE / f"{name}.nbt").write_bytes(data)
        print(f"wrote large {name}.nbt ({len(data)} bytes)")
    for name, fn in [
        ("cat_village", cat_village),
        ("nine_lives_shrine", nine_lives_shrine),
        ("velvet_palace_wing", velvet_palace_wing),
    ]:
        data = build_structure(fn())
        (OUT_MEDIUM / f"{name}.nbt").write_bytes(data)
        print(f"wrote medium {name}.nbt ({len(data)} bytes)")


if __name__ == "__main__":
    main()
