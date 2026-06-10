#!/usr/bin/env python3
"""Generate placeholder PNG textures and missing crafting recipes for cocojenna mod."""
import hashlib
import json
import re
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "src" / "main" / "resources"
ASSETS = RES / "assets" / "cocojenna"
RECIPES = RES / "data" / "cocojenna" / "recipes"

# Category hints for color coding
COLOR_HINTS = {
    "coco": (26, 26, 26),
    "jenna": (204, 136, 34),
    "daikatana": (180, 160, 80),
    "ryokatana": (140, 140, 160),
    "fish": (80, 140, 200),
    "food": (220, 140, 80),
    "meal": (220, 140, 80),
    "mud": (40, 30, 50),
    "shadow": (60, 20, 80),
    "moon": (200, 200, 255),
    "memory": (100, 200, 220),
    "seal": (160, 120, 200),
    "hibiscus": (220, 60, 100),
    "neon": (0, 220, 200),
    "spore": (100, 180, 80),
    "portal": (120, 60, 200),
    "altar": (240, 220, 160),
    "distiller": (140, 140, 150),
    "spawn_egg": (200, 200, 200),
}


def png_rgb(w: int, h: int, r: int, g: int, b: int) -> bytes:
    def chunk(tag: bytes, data: bytes) -> bytes:
        c = tag + data
        return struct.pack(">I", len(data)) + c + struct.pack(">I", zlib.crc32(c) & 0xFFFFFFFF)

    raw = b""
    for y in range(h):
        raw += b"\x00"
        for x in range(w):
            # subtle checker for visibility
            shade = 12 if (x + y) % 2 else 0
            raw += bytes((min(255, r + shade), min(255, g + shade), min(255, b + shade)))

    ihdr = struct.pack(">IIBBBBB", w, h, 8, 2, 0, 0, 0)
    return (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", ihdr)
        + chunk(b"IDAT", zlib.compress(raw, 9))
        + chunk(b"IEND", b"")
    )


def color_for(name: str) -> tuple[int, int, int]:
    lower = name.lower()
    for key, rgb in COLOR_HINTS.items():
        if key in lower:
            return rgb
    h = hashlib.md5(name.encode()).digest()
    return (80 + h[0] % 120, 80 + h[1] % 120, 80 + h[2] % 120)


def collect_texture_paths() -> set[str]:
    paths: set[str] = set()
    pattern = re.compile(r"cocojenna:(item|block)/([a-z0-9_]+)")
    for model in (ASSETS / "models").rglob("*.json"):
        text = model.read_text(encoding="utf-8")
        for kind, name in pattern.findall(text):
            paths.add(f"textures/{kind}/{name}.png")
    paths.add("textures/cocojenna.png")  # mods.toml logo
    return paths


def generate_textures() -> int:
    tex_root = ASSETS
    count = 0
    for rel in sorted(collect_texture_paths()):
        out = tex_root / rel
        out.parent.mkdir(parents=True, exist_ok=True)
        name = out.stem
        r, g, b = color_for(name)
        out.write_bytes(png_rgb(16, 16, r, g, b))
        count += 1
    return count


NO_RECIPE = {
    "coco_spawn_egg", "jenna_spawn_egg", "samurai_cat_spawn_egg", "shadow_claw_spawn_egg",
    "memory_shard", "coco_memory_shard", "jenna_memory_shard",
    "cat_kingdom_portal", "full_moon_spectrum", "jennas_old_bell",
    "nine_lives_catnip", "schrodingers_box", "supreme_cat_claw",
    "purr_coin", "full_moon_coin", "sequence_badge",
    # 採集 / 掉落 / 釣魚 — 見《貓之國物件》
    "velvet_fur", "moonstone", "silvervine", "neon_mushroom_item", "fiber_vine",
    "stardust_soil_item", "hibiscus_flower_item", "dandelion_fluff", "catnip_item",
    "blind_water_sample", "salt", "coarse_salt", "glow_fish", "deep_sea_fish",
    "giant_green_fish", "crab_meat", "black_mud_remnant", "shadow_crystal",
    "chaos_crystal", "blind_water_gel", "black_pearl", "memory_particle",
    "pure_tear", "deep_sea_pearl", "coco_fur", "jenna_fur", "purr_crystal",
    "toy_squeak", "rainbow_yarn_ball", "copper_wire", "precision_gear",
    "rusty_iron", "broken_circuit", "spore_fruit", "spore_powder", "moth_scale_powder",
}

BLOCK_ITEMS = {
    "distiller", "aroma_distiller", "cat_bed", "food_bowl", "scratching_post",
    "memory_monument_base", "memory_monument_top", "cat_kingdom_portal_frame",
    "stardust_soil", "black_mud", "hibiscus_flower", "catnip", "neon_mushroom",
    "velvet_grass", "spore_fruit_node", "full_moon_altar", "seal_pedestal",
}

INGREDIENT_POOL = [
    "minecraft:stick", "minecraft:iron_ingot", "minecraft:gold_ingot",
    "minecraft:diamond", "minecraft:emerald", "minecraft:glass",
    "minecraft:leather", "minecraft:string", "minecraft:feather",
    "minecraft:bone", "minecraft:flint", "minecraft:coal",
    "cocojenna:velvet_fur", "cocojenna:moonstone", "cocojenna:salt",
    "cocojenna:coco_fur", "cocojenna:jenna_fur", "cocojenna:spore_fruit",
]


def pick_ingredients(name: str) -> list[dict]:
    h = int(hashlib.md5(name.encode()).hexdigest(), 16)
    n = 2 + h % 3
    items = []
    for i in range(n):
        ing = INGREDIENT_POOL[(h + i * 7) % len(INGREDIENT_POOL)]
        items.append({"item": ing})
    return items


def existing_recipe_outputs() -> set[str]:
    outputs: set[str] = set()
    if not RECIPES.exists():
        return outputs
    for f in RECIPES.glob("*.json"):
        data = json.loads(f.read_text(encoding="utf-8"))
        result = data.get("result", {})
        if isinstance(result, dict):
            item = result.get("item", "")
            if item.startswith("cocojenna:"):
                outputs.add(item.split(":")[1])
        elif isinstance(result, str) and result.startswith("cocojenna:"):
            outputs.add(result.split(":")[1])
    return outputs


def all_item_ids() -> set[str]:
    text = (ROOT / "src/main/java/com/cocojenna/init/ModItems.java").read_text(encoding="utf-8")
    ids = set(re.findall(r'register\("([a-z0-9_]+)"', text))
    ids.update(re.findall(r'simple\("([a-z0-9_]+)"\)', text))
    return ids


def generate_missing_item_models() -> int:
    """Create item/generated models for registered items that lack a model JSON."""
    models_dir = ASSETS / "models" / "item"
    models_dir.mkdir(parents=True, exist_ok=True)
    existing = {p.stem for p in models_dir.glob("*.json")}
    count = 0
    for item_id in sorted(all_item_ids()):
        if item_id in existing:
            continue
        model = {
            "parent": "minecraft:item/generated",
            "textures": {"layer0": f"cocojenna:item/{item_id}"},
        }
        (models_dir / f"{item_id}.json").write_text(
            json.dumps(model, indent=2) + "\n", encoding="utf-8"
        )
        count += 1
    return count


def generate_recipes() -> int:
    RECIPES.mkdir(parents=True, exist_ok=True)
    have = existing_recipe_outputs()
    count = 0
    for item_id in sorted(all_item_ids()):
        if item_id in have or item_id in NO_RECIPE:
            continue
        recipe = {
            "type": "minecraft:crafting_shapeless",
            "ingredients": pick_ingredients(item_id),
            "result": {"item": f"cocojenna:{item_id}", "count": 1},
        }
        out = RECIPES / f"{item_id}.json"
        out.write_text(json.dumps(recipe, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        count += 1
    return count


if __name__ == "__main__":
    m = generate_missing_item_models()
    t = generate_textures()
    r = generate_recipes()
    print(f"Generated {m} item models, {t} placeholder textures, {r} recipes")
