#!/usr/bin/env python3
"""Generate ruin/first_cry loot tables and ruin matrix NBT files."""

from __future__ import annotations

import gzip
import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOOT_RUINS = ROOT / "src/main/resources/data/cocojenna/loot_tables/structures/ruins"
LOOT_VILLAGE = ROOT / "src/main/resources/data/cocojenna/loot_tables/structures/first_cry_village"
STRUCT_OUT = ROOT / "src/main/resources/data/cocojenna/structures/ruins"

RUIN_IDS = [
    "outpost", "war_ruins", "scratching_barricade", "velvet_tower", "mortar_position",
    "fallen_heroes_monument", "forgotten_altar", "black_mud_contaminated_temple",
    "moon_sealed_dungeon", "full_moon_stargazing_well", "memory_stone_circle",
    "holy_water_potion_lab", "black_mud_farm", "cardboard_refugee_camp",
    "catnip_smuggling_greenhouse", "abandoned_gear_workshop", "blind_water_sewer",
    "ironpaw_forge_ruins", "alienated_cat_tree_tower", "velvet_treehouse_remains",
    "royal_litter_box_basin", "giant_yarn_ball_nest", "stray_cat_canteen",
    "abandoned_toy_vault", "crashed_velvet_airship", "abyss_seal_anchor",
    "giant_cat_skeleton_ruins", "temporal_rift_portal", "great_wall_segment",
    "phantom_clock_tower", "velvet_altar_ruins",
]

VILLAGE_LOOT = [
    "mayor_secret", "forbidden_books", "shop_ryokatana", "forge_output",
    "kitchen_ingredients", "hotel_lost_and_found", "harbor_supply",
    "black_mud_clue", "moonlight_mural_reward",
]

# 全部 30 類遺跡均生成 NBT（Week 5 衝刺）
NBT_PRIORITY = set(RUIN_IDS)

BIOME_LANDMARKS = ROOT / "src/main/resources/data/cocojenna/structures/biome_landmarks"

TAG_END = 0
TAG_INT = 3
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11


def chest_loot(extra_item: str | None = None) -> dict:
    entries = [
        {"type": "minecraft:item", "name": "cocojenna:memory_shard", "weight": 8},
        {"type": "minecraft:item", "name": "cocojenna:velvet_fur", "weight": 6,
         "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 3}}]},
        {"type": "minecraft:item", "name": "cocojenna:purr_coin", "weight": 5,
         "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 6}}]},
    ]
    if extra_item:
        entries.append({"type": "minecraft:item", "name": extra_item, "weight": 3})
    return {"type": "minecraft:chest", "pools": [{"rolls": {"min": 1, "max": 3}, "entries": entries}]}


def write_loot_tables() -> None:
    LOOT_RUINS.mkdir(parents=True, exist_ok=True)
    LOOT_VILLAGE.mkdir(parents=True, exist_ok=True)
    extras = {
        "war_ruins": "cocojenna:warrior_last_letter",
        "forgotten_altar": "cocojenna:moonstone",
        "black_mud_contaminated_temple": "cocojenna:black_mud_remnant",
        "ironpaw_forge_ruins": "minecraft:iron_ingot",
        "velvet_altar_ruins": "cocojenna:red_paper",
    }
    for rid in RUIN_IDS:
        path = LOOT_RUINS / f"{rid}.json"
        path.write_text(json.dumps(chest_loot(extras.get(rid)), indent=2) + "\n", encoding="utf-8")
    village_extras = {
        "mayor_secret": "cocojenna:tiger_seal",
        "forbidden_books": "cocojenna:qin_chronicle",
        "shop_ryokatana": "cocojenna:ryokatana_first_cry_beginner",
        "forge_output": "minecraft:iron_ingot",
        "kitchen_ingredients": "cocojenna:catnip_item",
        "hotel_lost_and_found": "cocojenna:mint_milk_chocolate",
        "harbor_supply": "cocojenna:memory_particle",
        "black_mud_clue": "cocojenna:black_mud_remnant",
        "moonlight_mural_reward": "cocojenna:memory_shard",
    }
    for lid in VILLAGE_LOOT:
        path = LOOT_VILLAGE / f"{lid}.json"
        path.write_text(json.dumps(chest_loot(village_extras.get(lid)), indent=2) + "\n", encoding="utf-8")


def _write_tag_header(buf: bytearray, tag_type: int, name: str) -> None:
    buf.append(tag_type)
    name_bytes = name.encode("utf-8")
    buf.extend(struct.pack(">H", len(name_bytes)))
    buf.extend(name_bytes)


def _write_int(buf: bytearray, name: str, value: int) -> None:
    _write_tag_header(buf, TAG_INT, name)
    buf.extend(struct.pack(">i", value))


def _write_int_array(buf: bytearray, name: str, values: list[int]) -> None:
    _write_tag_header(buf, TAG_INT_ARRAY, name)
    buf.extend(struct.pack(">i", len(values)))
    for v in values:
        buf.extend(struct.pack(">i", v))


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
    c.extend(struct.pack(">H", len(data)))
    c.extend(data)
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
    xs = [p[0] for p, _ in entries]
    ys = [p[1] for p, _ in entries]
    zs = [p[2] for p, _ in entries]
    size = (max(xs) - min(xs) + 1, max(ys) - min(ys) + 1, max(zs) - min(zs) + 1)
    ox, oy, oz = min(xs), min(ys), min(zs)
    root = bytearray()
    _write_int_array(root, "size", list(size))
    palettes = bytearray()
    _write_tag_header(palettes, TAG_LIST, "palette")
    palettes.append(TAG_COMPOUND)
    palettes.extend(struct.pack(">i", len(palette_ids)))
    for bid in palette_ids:
        palettes.extend(_compound_palette(bid))
    _write_tag_header(root, TAG_LIST, "palettes")
    root.append(TAG_COMPOUND)
    root.extend(struct.pack(">i", 1))
    root.extend(palettes)
    root.append(TAG_END)
    block_list = bytearray()
    _write_tag_header(block_list, TAG_LIST, "blocks")
    block_list.append(TAG_COMPOUND)
    block_list.extend(struct.pack(">i", len(entries)))
    for (x, y, z), state in entries:
        block_list.extend(_compound_block((x - ox, y - oy, z - oz), state))
    root.extend(block_list)
    _write_tag_header(root, TAG_LIST, "entities")
    root.append(TAG_COMPOUND)
    root.extend(struct.pack(">i", 0))
    root.append(TAG_END)
    # NBT files require a named root compound (empty name for structure templates).
    wrapped = bytearray()
    wrapped.append(TAG_COMPOUND)
    wrapped.extend(struct.pack(">H", 0))
    wrapped.extend(root)
    wrapped.append(TAG_END)
    return bytes(wrapped)


def _platform(r: int, floor: str = "minecraft:stone_bricks") -> list[tuple[tuple[int, int, int], str]]:
    out: list[tuple[tuple[int, int, int], str]] = []
    for dx in range(-r, r + 1):
        for dz in range(-r, r + 1):
            if dx * dx + dz * dz <= r * r:
                out.append(((dx, 0, dz), floor))
    return out


def _chest_at(x: int, y: int, z: int) -> list[tuple[tuple[int, int, int], str]]:
    return [((x, y, z), "minecraft:chest")]


TIER1_RUINS = {
    "war_ruins",
    "forgotten_altar",
    "fallen_heroes_monument",
    "stray_cat_canteen",
    "abandoned_toy_vault",
}


def tier1_outpost() -> list[tuple[tuple[int, int, int], str]]:
    """二層骨架：底層宿舍/軍械庫、二層指揮室錨點（對照遺跡矩陣 outpost 23×17 過渡）."""
    blocks = _platform(5, "minecraft:stone_bricks")
    for dy in range(1, 4):
        for dx in range(-3, 4):
            blocks.append(((dx, dy, 0), "minecraft:stone_bricks"))
    for dx in (-2, 2):
        for dz in (-2, 2):
            blocks.append(((dx, 1, dz), "minecraft:oak_planks"))
    blocks.extend(_chest_at(0, 1, 2))
    blocks.append(((0, 2, 1), "minecraft:lectern"))
    blocks.append(((-2, 1, -2), "minecraft:oak_bed"))
    blocks.append(((2, 1, -2), "minecraft:barrel"))
    for dx in range(-2, 3):
        blocks.append(((dx, 4, 0), "minecraft:stone_bricks"))
    blocks.append(((0, 5, 0), "minecraft:lectern"))
    blocks.append(((0, 5, 1), "minecraft:redstone_torch"))
    blocks.append(((-1, 5, -1), "minecraft:oak_fence"))
    blocks.append(((1, 5, -1), "minecraft:oak_fence"))
    return blocks


def tier1_moon_dungeon() -> list[tuple[tuple[int, int, int], str]]:
    """多層月光封印地牢骨架（設計書 moon_sealed_dungeon 三層地下）."""
    blocks = _platform(4, "minecraft:deepslate_bricks")
    for dy in range(-6, 1):
        for dx in range(-3, 4):
            for dz in range(-3, 4):
                if abs(dx) == 3 or abs(dz) == 3:
                    blocks.append(((dx, dy, dz), "minecraft:deepslate_bricks"))
    blocks.append(((0, 0, 0), "minecraft:iron_door"))
    blocks.extend(_chest_at(0, -2, 0))
    blocks.extend(_chest_at(-2, -4, 2))
    blocks.append(((0, 1, 0), "minecraft:end_rod"))
    for dy in range(1, 5):
        blocks.append(((0, dy, 0), "minecraft:ladder"))
    for dx in range(-2, 3):
        blocks.append(((dx, 3, 0), "minecraft:deepslate_tiles"))
        blocks.append(((dx, -3, 0), "minecraft:deepslate_tiles"))
    for dz in (-2, 2):
        blocks.append(((0, -5, dz), "cocojenna:moonstone_brick"))
    blocks.append(((0, 4, 0), "minecraft:lectern"))
    blocks.append(((-2, 2, -2), "minecraft:soul_lantern"))
    blocks.append(((2, 2, 2), "minecraft:soul_lantern"))
    blocks.append(((0, -5, 0), "cocojenna:full_moon_altar"))
    return blocks


def tier1_mud_temple() -> list[tuple[tuple[int, int, int], str]]:
    """黑泥汙染神殿 + 側室（設計書 black_mud_contaminated_temple）."""
    blocks = _platform(5, "minecraft:stone_bricks")
    for dx in (-3, 0, 3):
        blocks.append(((dx, 1, 0), "cocojenna:black_mud"))
        blocks.append(((dx, 2, 0), "cocojenna:black_mud"))
    blocks.append(((0, 3, 0), "cocojenna:shadow_crystal_block"))
    blocks.extend(_chest_at(0, 1, 4))
    blocks.append(((2, 1, 1), "minecraft:lectern"))
    for dz in (-4, 4):
        for dy in range(0, 2):
            blocks.append(((-4, dy, dz), "minecraft:cracked_stone_bricks"))
            blocks.append(((4, dy, dz), "minecraft:cracked_stone_bricks"))
    blocks.extend(_chest_at(-3, 1, -3))
    blocks.append(((3, 1, -3), "cocojenna:black_mud"))
    return blocks


def tier1_war_ruins() -> list[tuple[tuple[int, int, int], str]]:
    """多層廢墟 + 四角二層角樓（設計書 war_ruins 23×17 過渡）."""
    blocks = _platform(5, "minecraft:cobblestone")
    for dx, dz in [(-3, -3), (3, -3), (-3, 3), (3, 3)]:
        for dy in range(1, 4):
            blocks.append(((dx, dy, dz), "minecraft:cracked_stone_bricks"))
        blocks.append(((dx, 4, dz), "minecraft:oak_fence"))
    for dx in range(-2, 3):
        blocks.append(((dx, 1, 0), "minecraft:cracked_stone_bricks"))
        blocks.append(((dx, 2, 0), "minecraft:stone_brick_stairs"))
    blocks.append(((0, 1, 0), "minecraft:iron_bars"))
    blocks.extend(_chest_at(0, 0, 2))
    blocks.append(((3, 2, 0), "minecraft:black_banner"))
    blocks.append(((-1, 1, 1), "minecraft:lectern"))
    blocks.append(((0, 3, -2), "minecraft:soul_lantern"))
    return blocks


def tier1_scratching_barricade() -> list[tuple[tuple[int, int, int], str]]:
    """貓抓防線 — 設計書 scratching_barricade."""
    blocks = _platform(5, "minecraft:stone_bricks")
    for dx in range(-4, 5):
        blocks.append(((dx, 1, -4), "cocojenna:scratching_post"))
        blocks.append(((dx, 1, 4), "cocojenna:cat_scratch_board"))
    for dz in (-3, 3):
        for dx in range(-3, 4):
            blocks.append(((dx, 1, dz), "minecraft:oak_fence"))
    blocks.append(((0, 1, 0), "minecraft:barrel"))
    blocks.extend(_chest_at(2, 1, 0))
    blocks.append(((-2, 1, 1), "minecraft:lectern"))
    return blocks


def tier1_mortar_position() -> list[tuple[tuple[int, int, int], str]]:
    """砲位遺跡 — 設計書 mortar_position."""
    blocks = _platform(4, "minecraft:stone_bricks")
    blocks.append(((0, 1, 0), "minecraft:dispenser"))
    blocks.append(((0, 2, 0), "minecraft:lever"))
    for dx in (-2, 2):
        blocks.append(((dx, 1, -2), "minecraft:cobblestone_wall"))
        blocks.append(((dx, 1, 2), "minecraft:cobblestone_wall"))
    blocks.append(((0, 1, -3), "minecraft:tnt"))
    blocks.extend(_chest_at(-2, 1, 2))
    blocks.append(((1, 1, 1), "minecraft:lectern"))
    return blocks


def tier1_forgotten_altar() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(3, "minecraft:smooth_stone")
    for dx in range(-3, 4):
        for dz in (-3, 3):
            blocks.append(((dx, 1, dz), "minecraft:stone_brick_wall"))
    for corner in [(-2, -2), (-2, 2), (2, -2), (2, 2)]:
        blocks.append(((corner[0], 1, corner[1]), "minecraft:end_rod"))
        blocks.append(((corner[0], 2, corner[1]), "minecraft:soul_lantern"))
    for dx in range(-1, 2):
        for dz in range(-1, 2):
            blocks.append(((dx, 1, dz), "minecraft:quartz_block"))
    blocks.append(((0, 2, 0), "cocojenna:moonstone_brick"))
    blocks.append(((0, 3, 0), "minecraft:end_rod"))
    blocks.append(((0, 1, -1), "minecraft:lectern"))
    blocks.extend(_chest_at(2, 1, 0))
    return blocks


def tier1_monument() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(3, "minecraft:polished_blackstone_bricks")
    for dx in range(-1, 2):
        for dz in range(-1, 2):
            blocks.append(((dx, 1, dz), "minecraft:polished_blackstone"))
    blocks.append(((0, 2, 0), "minecraft:polished_blackstone"))
    blocks.append(((0, 3, 0), "minecraft:soul_lantern"))
    blocks.extend(_chest_at(2, 1, 0))
    blocks.append(((-2, 1, 0), "minecraft:lectern"))
    return blocks


def tier1_canteen() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(3, "minecraft:oak_planks")
    for x in (-2, 2):
        blocks.append(((x, 1, 0), "minecraft:oak_fence"))
        blocks.append(((x, 2, 0), "minecraft:spruce_pressure_plate"))
    blocks.append(((0, 1, -2), "minecraft:campfire"))
    blocks.extend(_chest_at(0, 1, 2))
    blocks.append(((0, 1, -1), "minecraft:lectern"))
    return blocks


def tier1_toy_vault() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(2, "minecraft:oak_planks")
    for dx in (-1, 1):
        blocks.append(((dx, 1, 0), "cocojenna:cardboard_block"))
    blocks.append(((0, 2, 0), "minecraft:bell"))
    blocks.extend(_chest_at(0, 1, 1))
    blocks.append(((-1, 1, -1), "minecraft:lectern"))
    return blocks


def tier1_velvet_tower() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(3, "minecraft:oak_planks")
    for dy in range(1, 8):
        blocks.append(((0, dy, 0), "minecraft:stripped_oak_log"))
    for dy in range(1, 4):
        for dx in (-2, 2):
            blocks.append(((dx, dy, 0), "minecraft:oak_fence"))
    blocks.append(((0, 8, 0), "minecraft:campfire"))
    blocks.append(((1, 2, 1), "minecraft:lectern"))
    blocks.extend(_chest_at(-1, 1, 2))
    return blocks


def tier1_ironpaw_forge() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(4, "minecraft:stone_bricks")
    for dx in (-2, 2):
        blocks.append(((dx, 1, 0), "minecraft:anvil"))
    blocks.append(((0, 1, 0), "minecraft:blast_furnace"))
    blocks.append(((0, 1, 2), "minecraft:lectern"))
    blocks.extend(_chest_at(2, 1, -2))
    for dy in range(1, 6):
        blocks.append(((3, dy, -3), "minecraft:bricks"))
    blocks.append(((3, 6, -3), "minecraft:campfire"))
    blocks.append(((-3, 1, 0), "cocojenna:ironpaw_forge"))
    return blocks


def tier1_black_mud_farm() -> list[tuple[tuple[int, int, int], str]]:
    blocks = _platform(4, "minecraft:farmland")
    for dx in range(-2, 3):
        blocks.append(((dx, 1, 1), "cocojenna:black_mud"))
        blocks.append(((dx, 1, -1), "minecraft:wheat"))
    blocks.append(((0, 1, 3), "minecraft:lectern"))
    blocks.extend(_chest_at(3, 1, 0))
    return blocks


def ruin_blocks(rid: str) -> list[tuple[tuple[int, int, int], str]]:
    if rid == "outpost":
        return tier1_outpost()
    if rid == "war_ruins":
        return tier1_war_ruins()
    if rid == "forgotten_altar":
        return tier1_forgotten_altar()
    if rid == "fallen_heroes_monument":
        return tier1_monument()
    if rid == "stray_cat_canteen":
        return tier1_canteen()
    if rid == "abandoned_toy_vault":
        return tier1_toy_vault()
    if rid == "moon_sealed_dungeon":
        return tier1_moon_dungeon()
    if rid == "black_mud_contaminated_temple":
        return tier1_mud_temple()
    if rid == "velvet_tower":
        return tier1_velvet_tower()
    if rid == "ironpaw_forge_ruins":
        return tier1_ironpaw_forge()
    if rid == "scratching_barricade":
        return tier1_scratching_barricade()
    if rid == "mortar_position":
        return tier1_mortar_position()
    if rid == "black_mud_farm":
        return tier1_black_mud_farm()
    blocks: list[tuple[tuple[int, int, int], str]] = []
    for dx in range(-3, 4):
        for dz in range(-3, 4):
            if dx * dx + dz * dz <= 12:
                blocks.append(((dx, 0, dz), "minecraft:stone_bricks"))
    blocks.append(((0, 1, 0), "minecraft:chest"))
    if rid == "velvet_tower":
        for dy in range(1, 6):
            blocks.append(((0, dy, 0), "minecraft:oak_planks"))
        blocks.append(((0, 6, 0), "minecraft:campfire"))
    elif rid == "cardboard_refugee_camp":
        blocks.append(((-1, 1, 0), "minecraft:oak_planks"))
        blocks.append(((1, 1, 0), "minecraft:oak_planks"))
    elif rid == "full_moon_stargazing_well":
        blocks.append(((0, -1, 0), "minecraft:water"))
    return blocks


def write_nbt_structures() -> None:
    STRUCT_OUT.mkdir(parents=True, exist_ok=True)
    for rid in NBT_PRIORITY:
        data = build_structure(ruin_blocks(rid))
        out = STRUCT_OUT / f"{rid}.nbt"
        with gzip.open(out, "wb") as f:
            f.write(data)
        print(f"Wrote {out}")
    BIOME_LANDMARKS.mkdir(parents=True, exist_ok=True)
    landmark = build_structure(ruin_blocks("velvet_altar_ruins"))
    with gzip.open(BIOME_LANDMARKS / "first_cry_plains.nbt", "wb") as f:
        f.write(landmark)
    print(f"Wrote {BIOME_LANDMARKS / 'first_cry_plains.nbt'}")


MAUSOLEUM_OUT = ROOT / "src/main/resources/data/cocojenna/structures/mausoleum"
FIRST_CRY_OUT = ROOT / "src/main/resources/data/cocojenna/structures/first_cry_village"

MAUSOLEUM_IDS = ["paper_harem", "terracotta", "library", "tea_garden", "observatory", "sleeping_chamber", "ritual_hall"]
FIRST_CRY_DISTRICTS = [
    "sacred_tree", "council_library", "shop_district", "kitchen_market",
    "moon_plaza", "first_cry_harbor", "west_inn", "farm_outer_ring",
]
UNDERCAT_OUT = ROOT / "src/main/resources/data/cocojenna/structures/undercat"


def mausoleum_blocks(mid: str) -> list[tuple[tuple[int, int, int], str]]:
    shell = {
        "paper_harem": "minecraft:red_terracotta",
        "terracotta": "minecraft:terracotta",
        "library": "minecraft:deepslate_bricks",
        "tea_garden": "minecraft:moss_block",
        "observatory": "minecraft:smooth_quartz",
        "sleeping_chamber": "minecraft:purple_wool",
        "ritual_hall": "minecraft:deepslate_tiles",
    }.get(mid, "minecraft:deepslate_bricks")
    blocks = _platform(4, shell)
    core = {
        "paper_harem": "minecraft:red_wool",
        "terracotta": "minecraft:orange_terracotta",
        "library": "minecraft:bookshelf",
        "tea_garden": "cocojenna:catnip",
        "observatory": "minecraft:glass",
        "sleeping_chamber": "cocojenna:woven_wool",
        "ritual_hall": "cocojenna:moonstone_brick",
    }.get(mid, "minecraft:stone_bricks")
    height = {
        "paper_harem": 2,
        "terracotta": 4,
        "library": 3,
        "tea_garden": 1,
        "observatory": 6,
        "sleeping_chamber": 2,
        "ritual_hall": 3,
    }.get(mid, 3)
    for dy in range(1, height + 1):
        for dx in range(-2, 3):
            blocks.append(((dx, dy, -2), shell))
    blocks.append(((0, 1, 0), "cocojenna:suspicious_wall"))
    blocks.append(((0, -2, 0), core))
    if mid == "observatory":
        blocks.append(((0, height + 1, 0), "minecraft:beacon"))
    if mid == "library":
        blocks.append(((2, 1, 1), "minecraft:lectern"))
    if mid == "tea_garden":
        blocks.append(((-2, 1, 2), "minecraft:flower_pot"))
    if mid == "sleeping_chamber":
        blocks.append(((0, 2, 0), "cocojenna:cat_bed"))
        blocks.append(((-2, 1, 0), "minecraft:red_banner"))
    if mid == "ritual_hall":
        for dx in range(-3, 4):
            blocks.append(((dx, 0, -3), "cocojenna:moonstone_brick"))
            blocks.append(((dx, 0, 3), "cocojenna:moonstone_brick"))
        blocks.append(((0, 1, 0), "cocojenna:full_moon_altar"))
        blocks.append(((0, 2, 0), "minecraft:end_rod"))
    blocks.extend(_chest_at(1, -2, 0))
    return blocks


def first_cry_district_blocks(did: str) -> list[tuple[tuple[int, int, int], str]]:
    floor = {
        "sacred_tree": "cocojenna:velvet_grass",
        "moon_plaza": "cocojenna:stardust_soil",
        "farm_outer_ring": "cocojenna:stardust_soil",
    }.get(did, "cocojenna:stardust_brick")
    blocks = _platform(8, floor)
    if did == "sacred_tree":
        for dy in range(1, 36):
            rad = 3 if dy < 10 else (2 if dy < 25 else 1)
            for dx in range(-rad, rad + 1):
                for dz in range(-rad, rad + 1):
                    if dx * dx + dz * dz <= rad * rad and not (dx == 0 and dz == 0 and 0 < dy < 35):
                        blocks.append(((dx, dy, dz), "cocojenna:velvet_tree_log"))
        for dy in range(18, 36):
            r = 7 if dy < 26 else (5 if dy < 32 else 3)
            for dx in range(-r, r + 1):
                for dz in range(-r, r + 1):
                    if dx * dx + dz * dz <= r * r:
                        blocks.append(((dx, dy, dz), "cocojenna:velvet_tree_leaves"))
        for dx, dz in [(-3, 0), (3, 0), (0, 3), (0, -3), (-5, 5), (5, -5)]:
            blocks.append(((dx, 1, dz), "cocojenna:neon_mushroom"))
        blocks.append(((0, -2, 0), "cocojenna:undercat_tree_hole"))
        blocks.append(((0, -1, 0), "cocojenna:purr_crystal_block"))
        blocks.append(((0, 25, -2), "cocojenna:ancient_stone_tablet"))
        blocks.append(((0, 0, 0), "cocojenna:velvet_grass"))
        blocks.append(((4, 0, 4), "cocojenna:dungeon_entrance"))
        blocks.append(((4, -1, 4), "minecraft:water"))
        blocks.append(((-5, 1, 0), "cocojenna:scratching_post"))
        blocks.append(((5, 1, 0), "cocojenna:cat_bed"))
    elif did == "council_library":
        for dx in range(-4, 5):
            for dz in range(-3, 4):
                blocks.append(((dx, 1, dz), "cocojenna:velvet_planks"))
        for dx in range(-3, 4):
            blocks.append(((dx, 2, -3), "cocojenna:woven_wool"))
            blocks.append(((dx, 3, -3), "cocojenna:velvet_planks"))
        for dx in (-3, 3):
            blocks.append(((dx, 1, 2), "minecraft:bookshelf"))
        blocks.append(((0, 1, 0), "cocojenna:decree_pedestal"))
        blocks.append(((0, 2, 0), "minecraft:lectern"))
        blocks.append(((3, 1, 2), "cocojenna:picture_book_stand"))
        blocks.append(((-2, 1, -1), "cocojenna:ancient_stone_tablet"))
        blocks.extend(_chest_at(4, 1, 4))
    elif did == "shop_district":
        for dx in (-3, 0, 3):
            blocks.append(((dx, 1, -2), "cocojenna:velvet_planks"))
            blocks.append(((dx, 2, -2), "cocojenna:yarn_ball_lamp"))
        blocks.append(((0, 1, 0), "cocojenna:ironpaw_forge"))
        blocks.append(((-2, 1, 1), "cocojenna:ryokatana_shop_stand"))
        blocks.append(((2, 1, 1), "cocojenna:ryokatana_shop_stand"))
        blocks.append(((-4, 1, 2), "cocojenna:catnip"))
        blocks.append(((4, 1, -2), "cocojenna:neon_mushroom_pot"))
        blocks.extend(_chest_at(3, 1, 3))
    elif did == "kitchen_market":
        blocks.append(((0, 1, 0), "cocojenna:cat_kitchen"))
        blocks.append(((-2, 1, 2), "cocojenna:food_bowl"))
        blocks.append(((2, 1, -2), "cocojenna:catnip"))
        blocks.extend(_chest_at(4, 1, 0))
    elif did == "moon_plaza":
        for dx in range(-15, 16):
            for dz in range(-15, 16):
                if dx * dx + dz * dz <= 15 * 15:
                    blocks.append(((dx, 0, dz), "cocojenna:stardust_soil"))
        blocks.append(((0, 1, 0), "cocojenna:full_moon_altar"))
        for dx, dz in [(-8, 0), (8, 0), (0, 8), (0, -8), (-6, 6), (6, -6)]:
            blocks.append(((dx, 1, dz), "cocojenna:moonstone_lamp_post"))
        blocks.append(((0, 1, 12), "cocojenna:suspicious_wall"))
        blocks.append(((0, -3, 0), "cocojenna:moon_trial_altar"))
        blocks.append(((3, 1, 3), "minecraft:lectern"))
    elif did == "first_cry_harbor":
        for dx in range(-4, 5):
            blocks.append(((dx, 0, 0), "cocojenna:velvet_planks"))
        blocks.append(((0, 1, 1), "cocojenna:moonstone_lamp_post"))
        blocks.extend(_chest_at(2, 1, 2))
    elif did == "west_inn":
        for dx in (-3, 3):
            blocks.append(((dx, 1, 0), "cocojenna:cat_bed"))
        blocks.append(((0, 1, 0), "cocojenna:cat_climb_platform"))
        blocks.extend(_chest_at(5, 1, 2))
    elif did == "farm_outer_ring":
        for dx in range(-2, 3):
            blocks.append(((dx, 1, 2), "cocojenna:catnip"))
        blocks.append(((0, 1, -2), "cocojenna:aroma_distiller"))
    else:
        blocks.extend(_chest_at(2, 1, 2))
    return blocks


def undercat_cardboard_castle() -> list[tuple[tuple[int, int, int], str]]:
    """紙箱城堡 — 深淵與星光 DLC."""
    blocks = _platform(8, "cocojenna:cardboard_block")
    for dy in range(1, 5):
        for dx in range(-5, 6):
            blocks.append(((dx, dy, -5), "cocojenna:cardboard_block"))
            blocks.append(((dx, dy, 5), "cocojenna:cardboard_block"))
        for dz in range(-4, 5):
            blocks.append(((-5, dy, dz), "cocojenna:cardboard_block"))
            blocks.append(((5, dy, dz), "cocojenna:cardboard_block"))
    blocks.append(((0, 1, 0), "cocojenna:scratching_post"))
    blocks.append(((0, 2, 0), "cocojenna:cat_bed"))
    blocks.append(((-2, 1, 2), "minecraft:armor_stand"))
    blocks.append(((2, 1, 2), "minecraft:armor_stand"))
    blocks.append(((0, 4, 0), "minecraft:red_banner"))
    blocks.extend(_chest_at(3, 1, -3))
    return blocks


def write_extra_structures() -> None:
    MAUSOLEUM_OUT.mkdir(parents=True, exist_ok=True)
    for mid in MAUSOLEUM_IDS:
        out = MAUSOLEUM_OUT / f"{mid}.nbt"
        with gzip.open(out, "wb") as f:
            f.write(build_structure(mausoleum_blocks(mid)))
        print(f"Wrote {out}")
    FIRST_CRY_OUT.mkdir(parents=True, exist_ok=True)
    for did in FIRST_CRY_DISTRICTS:
        out = FIRST_CRY_OUT / f"{did}.nbt"
        with gzip.open(out, "wb") as f:
            f.write(build_structure(first_cry_district_blocks(did)))
        print(f"Wrote {out}")
    UNDERCAT_OUT.mkdir(parents=True, exist_ok=True)
    castle = UNDERCAT_OUT / "cardboard_castle.nbt"
    with gzip.open(castle, "wb") as f:
        f.write(build_structure(undercat_cardboard_castle()))
    print(f"Wrote {castle}")


if __name__ == "__main__":
    write_loot_tables()
    write_nbt_structures()
    write_extra_structures()
    print("Done.")
