#!/usr/bin/env python3
"""
方塊／物品視覺修復：
1. 建築方塊 — cube_bottom_top + 立體物品模型。
2. 裝飾／燈具／功能方塊 — 自訂低輪廓世界模型 + 平面物品圖示（非方塊）。
3. 修正 item/generated → minecraft:item/generated。
"""
from __future__ import annotations

import json
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
BLOCK_TEX = ROOT / "src/main/resources/assets/cocojenna/textures/block"
ITEM_TEX = ROOT / "src/main/resources/assets/cocojenna/textures/item"
BLOCK_MODELS = ROOT / "src/main/resources/assets/cocojenna/models/block"
ITEM_MODELS = ROOT / "src/main/resources/assets/cocojenna/models/item"
BLOCKSTATES = ROOT / "src/main/resources/assets/cocojenna/blockstates"

CROSS_BLOCKS = {
    "velvet_grass", "catnip", "hibiscus_flower", "neon_mushroom", "velvet_tree_leaves",
}
CARPET_BLOCKS = {"velvet_carpet"}
COLUMN_BLOCKS = {"velvet_tree_log"}
SKIP_FACES = {"velvet_tree_log_top", "velvet_tree_log", "petal_grass"}

# 保持完整方塊外觀（建材）
FULL_CUBE_BLOCKS = {
    "velvet_planks", "woven_wool", "stardust_brick", "moonstone_brick", "moonstone_block",
    "spore_metal_block", "salt_block", "thatch_roof", "pawprint_glass", "velvet_block",
    "stardust_soil", "cardboard_block", "reinforced_cardboard", "tape_block", "starlight_marble",
    "purr_crystal_block", "shadow_crystal_block", "altar_foundation", "cat_scratch_board",
    "black_mud", "black_mud_stage1", "black_mud_stage2", "black_mud_stage4",
    "sludge_remains", "blind_water", "blind_water_puddle",
    "boss_altar_ashura", "boss_altar_orange", "boss_altar_squall", "boss_heart_primal",
    "memory_lighthouse", "pure_light_tower", "cat_kingdom_portal_frame",
    "black_mud_stage3",
}

# 裝飾／燈具／功能 — 平面物品圖示 + 自訂世界模型
DECOR_BLOCKS = {
    "yarn_ball_lamp": "hanging_lamp",
    "neon_mush_lamp": "hanging_lamp",
    "moonstone_lamp_post": "floor_lamp",
    "cat_bed": "floor_item",
    "cat_bed_calico": "floor_item",
    "memory_monument_top": "monument_top",
    "food_bowl": "floor_item",
    "scratching_post": "post",
    "distiller": "machine",
    "aroma_distiller": "machine",
    "ironpaw_forge": "machine",
    "socketing_table": "table",
    "cat_kitchen": "table",
    "decree_pedestal": "pedestal",
    "picture_book_stand": "table",
    "blueprint_table": "table",
    "cat_core_engineering": "machine",
    "seal_pedestal": "pedestal",
    "full_moon_altar": "altar",
    "ryokatana_shop_stand": "table",
    "memory_monument_base": "monument_base",
    "memory_monument_top": "monument_top",
    "cat_climb_platform": "platform",
    "toy_box": "floor_item",
    "neon_mushroom_pot": "pot",
    "spore_fruit_node": "cross",
    "moonstone_cluster": "cluster",
    "salt_crystal": "cluster",
    "fireplace": "fireplace",
    "tape_temple": "machine",
    "rope_net": "cross",
    "undercat_tree_hole": "floor_item",
    "undercat_blind_rift": "portal_decor",
    "undercat_gear_shaft": "portal_decor",
    "undercat_lighthouse_well": "portal_decor",
    "undercat_sanctuary_pool": "portal_decor",
    "undercat_waystone": "pedestal",
    "cat_kingdom_portal": "portal_decor",
}


def darken(rgb: tuple, factor: float) -> tuple:
    return tuple(max(0, int(c * factor)) for c in rgb[:3])


def avg_color(im: Image.Image) -> tuple:
    px = im.convert("RGBA").load()
    w, h = im.size
    rs = gs = bs = n = 0
    for y in range(h):
        for x in range(w):
            p = px[x, y]
            if p[3] < 40:
                continue
            rs += p[0]
            gs += p[1]
            bs += p[2]
            n += 1
    if n == 0:
        return (120, 110, 100)
    return (rs // n, gs // n, bs // n)


def noise_px(x: int, y: int, base: tuple, amp: int = 8) -> tuple:
    n = ((x * 17 + y * 31) ^ (x * y * 7)) & 0xFF
    d = (n % (amp * 2 + 1)) - amp
    return tuple(max(0, min(255, base[i] + d)) for i in range(3))


def make_side_face(base: tuple, streak: bool = True) -> Image.Image:
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, base, 7)
            if streak and x % 4 == 0:
                c = darken(c, 0.92)
            im.putpixel((x, y), (*c, 255))
    return im


def make_bottom_face(base: tuple) -> Image.Image:
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    dark = darken(base, 0.55)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, dark, 5), 255))
    return im


def layered_item_icon(face: Image.Image, *, cross: bool = False) -> Image.Image:
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse([6, 24, 26, 30], fill=(0, 0, 0, 45))
    if cross:
        f = face.resize((22, 22), Image.Resampling.NEAREST)
        im.paste(f, (5, 3), f)
        return im
    f = face.resize((22, 22), Image.Resampling.NEAREST)
    im.paste(f, (5, 4), f)
    d.ellipse([8, 26, 24, 30], fill=(0, 0, 0, 30))
    return im


def write_json(path: Path, data: dict) -> None:
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def elem(from_xyz, to_xyz, tex="#all"):
    faces = {k: {"texture": tex} for k in ("north", "south", "east", "west", "up", "down")}
    return {"from": list(from_xyz), "to": list(to_xyz), "faces": faces}


def write_decor_block_model(name: str, kind: str) -> None:
    tex = f"cocojenna:block/{name}"
    particle = tex
    models = {
        "hanging_lamp": {
            "ambientocclusion": False,
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([7, 14, 7], [9, 16, 9]),
                elem([4, 6, 4], [12, 14, 12]),
            ],
        },
        "floor_lamp": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([7, 0, 7], [9, 13, 9]),
                elem([5, 13, 5], [11, 16, 11]),
            ],
        },
        "floor_item": {
            "textures": {"particle": particle, "all": tex},
            "elements": [elem([3, 0, 3], [13, 5, 13])],
        },
        "post": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([6, 0, 6], [10, 14, 10]),
                elem([4, 14, 4], [12, 16, 12]),
            ],
        },
        "machine": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([2, 0, 2], [14, 10, 14]),
                elem([4, 10, 4], [12, 14, 12]),
            ],
        },
        "table": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([1, 0, 1], [15, 9, 15]),
                elem([2, 9, 2], [14, 12, 14]),
            ],
        },
        "pedestal": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([4, 0, 4], [12, 3, 12]),
                elem([3, 3, 3], [13, 10, 13]),
                elem([2, 10, 2], [14, 12, 14]),
            ],
        },
        "altar": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([2, 0, 2], [14, 4, 14]),
                elem([4, 4, 4], [12, 8, 12]),
                elem([5, 8, 5], [11, 11, 11]),
            ],
        },
        "monument_base": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([2, 0, 2], [14, 6, 14]),
                elem([3, 6, 3], [13, 10, 13]),
            ],
        },
        "monument_top": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([4, 0, 4], [12, 8, 12]),
                elem([5, 8, 5], [11, 14, 11]),
            ],
        },
        "platform": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([1, 0, 1], [15, 3, 15]),
                elem([3, 3, 3], [13, 6, 13]),
            ],
        },
        "pot": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([4, 0, 4], [12, 6, 12]),
                elem([3, 6, 3], [13, 11, 13]),
            ],
        },
        "cluster": {
            "textures": {"particle": particle, "all": tex},
            "elements": [elem([5, 0, 5], [11, 7, 11])],
        },
        "fireplace": {
            "textures": {"particle": particle, "all": tex},
            "elements": [
                elem([2, 0, 2], [14, 12, 14]),
                elem([4, 12, 4], [12, 14, 12]),
            ],
        },
        "portal_decor": {
            "ambientocclusion": False,
            "textures": {"particle": particle, "all": tex},
            "elements": [elem([3, 0, 0], [13, 16, 3])],
        },
    }
    if kind == "cross":
        write_json(BLOCK_MODELS / f"{name}.json", {
            "render_type": "cutout",
            "parent": "minecraft:block/cross",
            "textures": {"cross": tex},
        })
        return
    data = models.get(kind)
    if data:
        write_json(BLOCK_MODELS / f"{name}.json", data)


def write_block_model(name: str) -> None:
    if name in DECOR_BLOCKS:
        write_decor_block_model(name, DECOR_BLOCKS[name])
        return
    if name in CROSS_BLOCKS:
        write_json(BLOCK_MODELS / f"{name}.json", {
            "render_type": "cutout",
            "parent": "minecraft:block/cross",
            "textures": {"cross": f"cocojenna:block/{name}"},
        })
        return
    if name in CARPET_BLOCKS:
        write_json(BLOCK_MODELS / f"{name}.json", {
            "parent": "minecraft:block/carpet",
            "textures": {"wool": f"cocojenna:block/{name}"},
        })
        return
    if name in COLUMN_BLOCKS:
        return
    if name == "black_mud_stage3":
        write_json(BLOCK_MODELS / f"{name}.json", {
            "parent": "minecraft:block/cube_all",
            "textures": {"all": "cocojenna:block/black_mud"},
        })
        return

    top_tex = BLOCK_TEX / f"{name}.png"
    if not top_tex.is_file():
        return

    top = Image.open(top_tex).convert("RGBA")
    base = avg_color(top)
    side = make_side_face(darken(base, 0.88))
    bottom = make_bottom_face(base)
    side.save(BLOCK_TEX / f"{name}_side.png")
    bottom.save(BLOCK_TEX / f"{name}_bottom.png")

    write_json(BLOCK_MODELS / f"{name}.json", {
        "parent": "minecraft:block/cube_bottom_top",
        "textures": {
            "top": f"cocojenna:block/{name}",
            "side": f"cocojenna:block/{name}_side",
            "bottom": f"cocojenna:block/{name}_bottom",
        },
    })


def write_flat_item_model(name: str, icon: Image.Image) -> None:
    ITEM_TEX.mkdir(parents=True, exist_ok=True)
    icon.save(ITEM_TEX / f"{name}.png")
    write_json(ITEM_MODELS / f"{name}.json", {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"cocojenna:item/{name}"},
    })


def write_block_item_model(name: str) -> None:
    write_json(ITEM_MODELS / f"{name}.json", {
        "parent": f"cocojenna:block/{name}",
    })


def write_item_model(name: str) -> None:
    top_tex = BLOCK_TEX / f"{name}.png"
    cross = name in CROSS_BLOCKS or DECOR_BLOCKS.get(name) == "cross"
    use_flat = name in DECOR_BLOCKS or name in CROSS_BLOCKS or name in CARPET_BLOCKS

    if (use_flat or name not in FULL_CUBE_BLOCKS) and top_tex.is_file():
        top = Image.open(top_tex).convert("RGBA")
        icon = layered_item_icon(top, cross=cross)
        write_flat_item_model(name, icon)
        return
    write_block_item_model(name)


def block_names() -> list[str]:
    names = []
    for p in sorted(BLOCK_TEX.glob("*.png")):
        stem = p.stem
        if stem.endswith(("_side", "_bottom", "_top")):
            continue
        if stem in SKIP_FACES:
            continue
        names.append(stem)
    return names


def update_decor_blockstates() -> None:
    cutout = set(DECOR_BLOCKS) | CROSS_BLOCKS
    for name in cutout:
        path = BLOCKSTATES / f"{name}.json"
        if not path.is_file():
            continue
        data = json.loads(path.read_text(encoding="utf-8-sig"))
        variants = data.get("variants", {})
        for key, val in variants.items():
            if isinstance(val, dict):
                val["render_type"] = "cutout"
        path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def fix_item_model_parents() -> int:
    fixed = 0
    for path in ITEM_MODELS.glob("*.json"):
        try:
            raw = path.read_bytes()
            if raw.startswith(b"\xef\xbb\xbf"):
                raw = raw[3:]
            data = json.loads(raw.decode("utf-8"))
        except (json.JSONDecodeError, UnicodeDecodeError):
            continue
        parent = data.get("parent", "")
        new_parent = None
        if parent == "item/generated":
            new_parent = "minecraft:item/generated"
        elif parent == "item/handheld":
            new_parent = "minecraft:item/handheld"
        if new_parent:
            data["parent"] = new_parent
            write_json(path, data)
            fixed += 1
    return fixed


def fix_remaining_items() -> None:
    aliases = {
        "black_mud_stage3": "black_mud",
    }
    for item, src in aliases.items():
        src_path = BLOCK_TEX / f"{src}.png"
        if src_path.is_file():
            top = Image.open(src_path).convert("RGBA")
            if item in FULL_CUBE_BLOCKS:
                write_block_item_model(item)
            else:
                write_flat_item_model(item, layered_item_icon(top))

    if (BLOCK_TEX / "velvet_tree_log.png").is_file():
        top = Image.open(BLOCK_TEX / "velvet_tree_log.png").convert("RGBA")
        im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        d.ellipse([6, 24, 26, 30], fill=(0, 0, 0, 45))
        bark = top.resize((14, 22), Image.Resampling.NEAREST)
        im.paste(bark, (9, 5), bark)
        top_ring_path = BLOCK_TEX / "velvet_tree_log_top.png"
        top_ring = Image.open(top_ring_path).convert("RGBA") if top_ring_path.is_file() else top
        ring = top_ring.resize((14, 6), Image.Resampling.NEAREST)
        im.paste(ring, (9, 3), ring)
        write_flat_item_model("velvet_tree_log", im)

    if (BLOCK_TEX / "memory_monument_top.png").is_file():
        top = Image.open(BLOCK_TEX / "memory_monument_top.png").convert("RGBA")
        write_flat_item_model("memory_monument_top", layered_item_icon(top))


def ensure_elixir_icons() -> None:
    specs = {
        "strength_elixir": (210, 70, 70),
        "speed_elixir": (70, 190, 110),
        "life_elixir": (190, 90, 200),
        "legend_catnip_seed": (90, 170, 70),
    }
    for name, rgb in specs.items():
        im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        d.ellipse([7, 23, 25, 29], fill=(0, 0, 0, 40))
        d.rectangle([11, 10, 20, 26], fill=(*rgb, 255))
        d.rectangle([12, 7, 19, 11], fill=(230, 230, 240, 255))
        d.point((14, 14), fill=(255, 255, 255, 200))
        write_flat_item_model(name, im)


def main() -> None:
    print("Layered block faces + decor models:")
    names = block_names()
    for name in names:
        write_block_model(name)
        write_item_model(name)
        kind = DECOR_BLOCKS.get(name, "cube")
        print(f"  {name} ({kind})")

    ensure_elixir_icons()
    fix_remaining_items()
    update_decor_blockstates()
    n = fix_item_model_parents()
    print(f"  fixed {n} item model parents")

    if not (ITEM_TEX / "guardian_guide.png").is_file():
        im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        d.rectangle([9, 6, 22, 26], fill=(140, 90, 60, 255))
        d.rectangle([10, 7, 21, 25], fill=(255, 240, 220, 255))
        d.line([(12, 10), (19, 10)], fill=(200, 120, 140, 255))
        write_flat_item_model("guardian_guide", im)

    print(f"Done: {len(names)} blocks processed.")


if __name__ == "__main__":
    main()
