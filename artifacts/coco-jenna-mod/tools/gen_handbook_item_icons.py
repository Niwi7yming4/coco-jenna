#!/usr/bin/env python3
"""程式繪製材料／消耗品圖示（32×32），覆蓋切片錯位的物品貼圖。"""
from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
ITEM = ROOT / "src/main/resources/assets/cocojenna/textures/item"
MODELS = ROOT / "src/main/resources/assets/cocojenna/models/item"


def img() -> Image.Image:
    return Image.new("RGBA", (32, 32), (0, 0, 0, 0))


def noise(x: int, y: int, base: tuple, amp: int = 8) -> tuple:
    n = ((x * 17 + y * 31) ^ (x * y * 7)) & 0xFF
    d = (n % (amp * 2 + 1)) - amp
    return tuple(max(0, min(255, base[i] + d)) for i in range(3))


def save(name: str, im: Image.Image) -> None:
    ITEM.mkdir(parents=True, exist_ok=True)
    im.save(ITEM / f"{name}.png")
    print(f"  {name}.png")


def draw_orb(rgb: tuple, glow: tuple | None = None) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.ellipse([8, 8, 23, 23], fill=(*rgb, 255))
    if glow:
        d.ellipse([11, 11, 20, 20], fill=(*glow, 200))
        d.point((15, 14), fill=(255, 255, 255, 255))
    return im


def draw_gem(rgb: tuple) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.polygon([(16, 6), (24, 14), (16, 26), (8, 14)], fill=(*rgb, 255))
    d.polygon([(16, 9), (21, 14), (16, 22), (11, 14)], fill=tuple(min(255, c + 40) for c in rgb) + (220,))
    return im


def draw_fur_ball() -> Image.Image:
    im = img()
    for y in range(32):
        for x in range(32):
            if (x - 16) ** 2 + (y - 16) ** 2 < 100:
                im.putpixel((x, y), (*noise(x, y, (245, 235, 210), 6), 255))
    return im


def draw_leaf_cluster(rgb: tuple) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.ellipse([6, 14, 14, 22], fill=(*rgb, 255))
    d.ellipse([12, 10, 20, 18], fill=(*tuple(min(255, c + 20) for c in rgb), 255))
    d.ellipse([18, 14, 26, 22], fill=(*rgb, 255))
    return im


def draw_fish(blue: bool = False) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    body = (80, 160, 220) if blue else (100, 180, 120)
    d.ellipse([8, 12, 22, 20], fill=(*body, 255))
    d.polygon([(22, 16), (28, 12), (28, 20)], fill=(*body, 255))
    d.point((12, 15), fill=(255, 255, 255, 255) if blue else (20, 20, 20, 255))
    return im


def draw_can() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([10, 8, 21, 24], fill=(180, 185, 195, 255))
    d.rectangle([11, 9, 20, 14], fill=(220, 180, 120, 255))
    d.ellipse([10, 6, 21, 10], fill=(200, 205, 215, 255))
    return im


def draw_bowl_glow() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.polygon([(8, 18), (24, 18), (22, 24), (10, 24)], fill=(200, 200, 210, 255))
    d.ellipse([9, 14, 23, 20], fill=(120, 180, 255, 200))
    d.point((16, 17), fill=(200, 230, 255, 255))
    return im


def draw_mushroom_glow() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([14, 18, 17, 26], fill=(240, 240, 245, 255))
    d.ellipse([8, 8, 23, 18], fill=(120, 60, 220, 255))
    d.point((15, 12), fill=(200, 180, 255, 255))
    return im


def draw_flower_red() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([15, 18, 16, 26], fill=(40, 100, 40, 255))
    d.ellipse([8, 8, 23, 20], fill=(210, 40, 55, 255))
    d.line([(16, 8), (16, 4)], fill=(255, 220, 100, 255))
    return im


def draw_soil_star() -> Image.Image:
    im = img()
    for y in range(32):
        for x in range(32):
            im.putpixel((x, y), (*noise(x, y, (35, 32, 45), 8), 255))
    d = ImageDraw.Draw(im)
    for px, py in [(10, 12), (22, 10), (18, 22)]:
        d.point((px, py), fill=(255, 240, 180, 255))
    return im


def draw_salt_cube() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([10, 10, 21, 21], fill=(245, 245, 250, 255))
    d.line([(10, 10), (21, 21)], fill=(220, 220, 230, 200))
    d.point((10, 10), fill=(255, 255, 255, 255))
    return im


def draw_gear() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.ellipse([10, 10, 21, 21], fill=(180, 140, 70, 255))
    for i in range(8):
        ang = i * 3.14159 / 4
        x1 = int(15.5 + 9 * __import__("math").cos(ang))
        y1 = int(15.5 + 9 * __import__("math").sin(ang))
        d.line([(15, 15), (x1, y1)], fill=(140, 100, 50, 255))
    d.ellipse([13, 13, 18, 18], fill=(220, 180, 100, 255))
    return im


def draw_card() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([8, 6, 23, 26], fill=(240, 235, 225, 255))
    d.rectangle([9, 7, 22, 25], fill=(255, 250, 245, 255))
    d.ellipse([12, 11, 19, 18], fill=(255, 200, 220, 255))
    return im


def draw_golden_spoon_trophy() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([11, 22, 20, 28], fill=(200, 160, 60, 255))
    d.rectangle([12, 23, 19, 27], fill=(230, 190, 80, 255))
    d.polygon([(16, 6), (22, 12), (20, 20), (12, 20), (10, 12)], fill=(255, 215, 80, 255))
    d.ellipse([(13, 14), (19, 18)], fill=(255, 240, 160, 255))
    d.rectangle([(14, 8), (18, 18)], fill=(255, 220, 100, 255))
    d.point((16, 10), fill=(255, 255, 220, 255))
    return im


def draw_cloak_swatch(rgb: tuple) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.polygon([(16, 4), (26, 10), (24, 28), (8, 28), (6, 10)], fill=(*rgb, 255))
    d.line([(16, 8), (16, 24)], fill=tuple(max(0, c - 40) for c in rgb) + (200,))
    return im


# 材料／消耗品：覆蓋切片品質差的圖示
PROCEDURAL: dict[str, Image.Image] = {}


def register(name: str, im: Image.Image) -> None:
    PROCEDURAL[name] = im


def build() -> None:
    register("moonstone", draw_orb((140, 155, 185), (200, 220, 255)))
    register("velvet_fur", draw_fur_ball())
    register("coco_fur", draw_fur_ball())
    register("jenna_fur", draw_fur_ball())
    register("purr_crystal", draw_orb((255, 210, 80), (255, 245, 200)))
    register("shadow_crystal", draw_gem((50, 25, 70)))
    register("chaos_crystal", draw_gem((80, 20, 100)))
    register("memory_particle", draw_orb((100, 180, 255), (200, 240, 255)))
    register("memory_shard", draw_gem((120, 200, 230)))
    register("salt", draw_salt_cube())
    register("coarse_salt", draw_salt_cube())
    register("catnip_item", draw_leaf_cluster((60, 140, 60)))
    register("hibiscus_flower_item", draw_flower_red())
    register("neon_mushroom_item", draw_mushroom_glow())
    register("stardust_soil_item", draw_soil_star())
    register("spore_fruit", draw_orb((160, 165, 175), (220, 50, 50)))
    register("spore_powder", draw_orb((140, 145, 155), None))
    register("moth_scale_powder", draw_orb((200, 150, 255), (255, 220, 255)))
    register("dandelion_fluff", draw_fur_ball())
    register("glow_fish", draw_fish(True))
    register("deep_sea_fish", draw_fish(False))
    register("giant_green_fish", draw_fish(False))
    register("crab_meat", draw_orb((200, 60, 50), (255, 120, 100)))
    register("premium_fish_can", draw_can())
    register("night_glow_soup", draw_bowl_glow())
    register("holy_water", draw_orb((80, 140, 255), (180, 220, 255)))
    register("pure_tear", draw_orb((255, 200, 210), (255, 240, 245)))
    register("hibiscus_tear", draw_orb((255, 180, 190), (255, 220, 230)))
    register("blind_water_sample", draw_orb((8, 8, 15), (40, 45, 55)))
    register("blind_water_gel", draw_orb((15, 15, 25), (60, 65, 80)))
    register("black_mud_remnant", draw_orb((20, 15, 30), (60, 30, 80)))
    register("black_mud_sample", draw_orb((12, 8, 18), (45, 25, 55)))
    register("primal_chaos_core", draw_gem((255, 210, 80)))
    register("grief_gel", draw_orb((90, 85, 95), (140, 130, 150)))
    register("fallen_core", draw_gem((35, 10, 25)))
    register("primal_chaos_shard", draw_gem((15, 5, 10)))
    register("memory_clay", draw_soil_star())
    register("unsent_letter", draw_card())
    register("rusted_bell", draw_orb((160, 120, 60), (200, 160, 80)))
    register("half_scarf", draw_cloak_swatch((180, 140, 120)))
    register("faded_collar", draw_orb((90, 60, 45), None))
    register("pure_drop", draw_orb((220, 235, 255), (255, 230, 150)))
    register("precision_gear", draw_gear())
    register("copper_wire", draw_orb((180, 110, 70), None))
    register("rusty_iron", draw_orb((120, 90, 70), None))
    register("toy_squeak", draw_orb((240, 160, 50), (255, 220, 120)))
    register("rainbow_yarn_ball", draw_orb((240, 160, 50), (255, 200, 255)))
    register("blank_memory_card", draw_card())
    register("origami_scrap", draw_orb((220, 190, 140), None))
    register("silvervine", draw_leaf_cluster((80, 160, 70)))
    register("fiber_vine", draw_leaf_cluster((70, 120, 55)))
    register("nine_lives_catnip", draw_mushroom_glow())
    register("full_moon_spectrum", draw_orb((255, 220, 150), (255, 240, 200)))
    register("purr_coin", draw_orb((255, 210, 70), (255, 240, 180)))
    register("full_moon_coin", draw_orb((200, 210, 255), (240, 245, 255)))
    register("golden_spoon_trophy", draw_golden_spoon_trophy())
    register("sequence_badge", draw_gear())
    register("sequence_manual", draw_card())
    register("black_pearl", draw_orb((25, 22, 35), (80, 70, 100)))
    register("deep_sea_pearl", draw_orb((40, 90, 160), (120, 180, 255)))
    register("shadow_coin", draw_orb((35, 28, 55), (90, 60, 120)))
    register("tape_core", draw_orb((120, 115, 125), (180, 175, 190)))
    register("cardboard_badge", draw_orb((180, 140, 90), (220, 180, 130)))
    register("legend_catnip_seed", draw_leaf_cluster((50, 130, 45)))
    register("sealed_memory_book", draw_card())
    register("scarface_charm", draw_orb((160, 90, 70), (200, 130, 100)))
    register("silenced_silver_thread", draw_orb((200, 205, 215), (240, 245, 255)))
    register("twin_star_pendant", draw_gem((255, 220, 120)))
    register("squall_umbrella_bone", draw_orb((190, 195, 210), None))
    register("storm_cloud_fur", draw_fur_ball())
    register("coco_memory_shard", draw_gem((90, 85, 95)))
    register("jenna_memory_shard", draw_gem((180, 110, 60)))
    register("seal_orb", draw_orb((120, 180, 255), (200, 230, 255)))
    register("samurai_seal", draw_orb((180, 50, 45), (255, 120, 100)))
    register("general_seal", draw_orb((200, 160, 50), (255, 220, 120)))
    register("cloak_eternal", draw_cloak_swatch((180, 50, 45)))
    register("moonlight_footprint", draw_orb((200, 210, 255), (240, 245, 255)))
    register("save_anchor", draw_gem((100, 180, 220)))
    register("pure_cure", draw_orb((120, 200, 255), (200, 240, 255)))
    for name, rgb in [
        ("cloak_anti_corrosion", (80, 120, 160)),
        ("cloak_moonlight", (200, 210, 255)),
        ("cloak_memory", (160, 140, 200)),
        ("cloak_guardian", (100, 120, 180)),
        ("cloak_warm", (220, 160, 120)),
        ("cloak_traveler", (140, 160, 140)),
        ("cloak_thunder", (180, 180, 220)),
        ("cloak_hibiscus", (200, 80, 90)),
        ("cloak_purr", (240, 200, 220)),
    ]:
        register(name, draw_cloak_swatch(rgb))
    for i, rgb in enumerate([
        (100, 120, 180), (180, 140, 200), (220, 180, 120),
        (80, 160, 140), (200, 120, 140), (140, 160, 200),
        (160, 140, 100), (120, 100, 160), (200, 200, 180),
    ], start=1):
        register(f"cloak_{i}", draw_cloak_swatch(rgb))


def is_broken(path: Path) -> bool:
    if not path.is_file():
        return True
    im = Image.open(path).convert("RGBA")
    w, h = im.size
    px = im.load()
    opaque = 0
    bottom_white = 0
    bottom_total = 0
    for y in range(h):
        for x in range(w):
            p = px[x, y]
            a = p[3] if len(p) == 4 else 255
            if a > 40:
                opaque += 1
            if y >= int(h * 0.72):
                bottom_total += 1
                r, g, b = p[0], p[1], p[2]
                if a > 120 and r > 200 and g > 200 and b > 200:
                    bottom_white += 1
    if opaque < max(12, (w * h) // 40):
        return True
    if bottom_total and bottom_white / bottom_total > 0.45:
        return True
    return False


def item_names_from_models() -> set[str]:
    names: set[str] = set()
    for m in MODELS.glob("*.json"):
        names.add(m.stem)
    return names


def main() -> None:
    print("Procedural handbook item icons:")
    build()
    written = 0
    for name, im in sorted(PROCEDURAL.items()):
        save(name, im)
        written += 1
    print(f"Written/updated: {written}")

    broken = []
    for name in sorted(item_names_from_models()):
        p = ITEM / f"{name}.png"
        if is_broken(p):
            broken.append(name)
    print(f"Still broken/missing items: {len(broken)}")
    for b in broken[:20]:
        print(f"  - {b}")
    if len(broken) > 20:
        print(f"  ... +{len(broken) - 20} more")


if __name__ == "__main__":
    main()
