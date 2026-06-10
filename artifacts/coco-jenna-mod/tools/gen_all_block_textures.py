#!/usr/bin/env python3
"""
Generate 16×16 pixel-art block textures per design spec.
Run: python tools/gen_all_block_textures.py
"""
from __future__ import annotations

import json
import math
import random
from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: pip install pillow")

ROOT = Path(__file__).resolve().parent.parent
TEX = ROOT / "src/main/resources/assets/cocojenna/textures/block"
MODELS = ROOT / "src/main/resources/assets/cocojenna/models/block"
ITEMS = ROOT / "src/main/resources/assets/cocojenna/models/item"
BLOCKSTATES = ROOT / "src/main/resources/assets/cocojenna/blockstates"

RNG = random.Random(42)


def img(w=16, h=16) -> Image.Image:
    return Image.new("RGBA", (w, h), (0, 0, 0, 0))


def save(name: str, im: Image.Image) -> None:
    TEX.mkdir(parents=True, exist_ok=True)
    path = TEX / f"{name}.png"
    im.save(path)
    print(" ", path.name)


def blend(a: tuple, b: tuple, t: float) -> tuple:
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def noise_px(x: int, y: int, base: tuple, amp: int = 12) -> tuple:
    n = ((x * 17 + y * 31) ^ (x * y * 7)) & 0xFF
    d = (n % (amp * 2 + 1)) - amp
    return tuple(max(0, min(255, base[i] + d)) for i in range(3))


# ── I. 自然地形 ──────────────────────────────────────────────────────────

def draw_velvet_grass_cross() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    cream, brown, green = (245, 235, 210), (120, 85, 55), (180, 210, 160)
    for y in range(16):
        for x in range(16):
            if abs(x - 7) <= 1 or abs(y - 7) <= 1:
                c = cream
                if (x + y) % 3 == 0:
                    c = blend(c, (255, 250, 240), 0.4)
                im.putpixel((x, y), (*c, 255))
            elif abs(x - 7) == 2 or abs(y - 7) == 2:
                im.putpixel((x, y), (*blend(cream, green, 0.35), 200))
    for i in range(6):
        px, py = 4 + i * 2, 3 + (i % 2)
        d.line([(px, py), (px, py + 3)], fill=(*blend(cream, (255, 255, 255), 0.3), 255), width=1)
    return im


def draw_stardust_soil() -> Image.Image:
    im = img()
    base = (35, 32, 42)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, base, 8), 255))
    stars = [(2, 3), (7, 1), (11, 5), (4, 9), (13, 11), (8, 14)]
    for sx, sy in stars:
        for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1), (0, 0)]:
            if 0 <= sx + dx < 16 and 0 <= sy + dy < 16:
                im.putpixel((sx + dx, sy + dy), (220, 225, 255, 255 if dx == 0 and dy == 0 else 180))
    return im


def draw_petal_grass_top() -> Image.Image:
    im = img()
    soil = (100, 120, 80)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, soil, 6), 255))
    pink = (255, 190, 210)
    flowers = [(4, 5), (10, 4), (7, 9), (12, 11)]
    d = ImageDraw.Draw(im)
    for fx, fy in flowers:
        d.ellipse([fx - 2, fy - 2, fx + 2, fy + 2], fill=(*pink, 255))
        d.point((fx, fy), fill=(255, 140, 170, 255))
    return im


def draw_catnip_cross() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    green = (60, 150, 70)
    for y in range(16):
        for x in range(16):
            if abs(x - 8) + abs(y - 8) <= 6:
                c = noise_px(x, y, green, 15)
                if (x + y) % 4 == 0:
                    c = blend(c, (90, 190, 90), 0.3)
                im.putpixel((x, y), (*c, 220 if abs(x - 8) + abs(y - 8) > 4 else 255))
    for lx in range(5, 12):
        d.point((lx, 6), fill=(40, 120, 50, 255))
        d.point((lx, 10), fill=(40, 120, 50, 255))
    return im


def draw_velvet_log_side() -> Image.Image:
    im = img()
    bark = (130, 90, 60)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, bark, 10)
            if y % 4 == 0:
                c = blend(c, (110, 75, 50), 0.3)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_velvet_log_top() -> Image.Image:
    im = img()
    ring = (150, 105, 70)
    for y in range(16):
        for x in range(16):
            dx, dy = x - 8, y - 8
            dist = (dx * dx + dy * dy) ** 0.5
            c = blend(ring, (180, 130, 90), min(1, dist / 8))
            if int(dist) % 3 == 0:
                c = blend(c, (120, 80, 55), 0.2)
            if dist > 7:
                c = (0, 0, 0)
                a = 0
            else:
                a = 255
            im.putpixel((x, y), (*c, a))
    return im


def draw_velvet_leaves() -> Image.Image:
    im = img()
    cream = (255, 245, 225)
    for y in range(16):
        for x in range(16):
            if (x - 8) ** 2 + (y - 8) ** 2 < 49:
                c = blend(cream, (255, 220, 230), ((x + y) % 5) / 5)
                a = 180 if (x + y) % 3 == 0 else 140
                im.putpixel((x, y), (*c, a))
    d = ImageDraw.Draw(im)
    d.ellipse([2, 2, 14, 14], outline=(255, 250, 240, 100))
    return im


def draw_moonstone_ore() -> Image.Image:
    im = img()
    stone = (70, 72, 80)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, stone, 10), 255))
    crystals = [(5, 6), (10, 4), (8, 11)]
    d = ImageDraw.Draw(im)
    for cx, cy in crystals:
        d.polygon([(cx, cy - 3), (cx + 2, cy), (cx, cy + 3), (cx - 2, cy)], fill=(200, 220, 255, 255))
        d.point((cx, cy), fill=(255, 255, 255, 255))
    return im


def draw_salt_crystal_ore() -> Image.Image:
    im = img()
    rock = (100, 100, 105)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, rock, 8), 255))
    d = ImageDraw.Draw(im)
    for cx, cy in [(4, 5), (10, 8), (7, 12)]:
        d.rectangle([cx - 2, cy - 2, cx + 2, cy + 2], fill=(245, 245, 250, 255))
        d.point((cx - 2, cy - 2), fill=(255, 255, 255, 200))
    return im


def draw_hibiscus_cross() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([7, 10, 8, 15], fill=(40, 100, 40, 255))
    d.ellipse([3, 2, 13, 10], fill=(210, 40, 55, 255))
    d.ellipse([5, 4, 11, 9], fill=(230, 60, 70, 255))
    d.line([(8, 5), (8, 1)], fill=(255, 220, 100, 255))
    d.point((8, 1), fill=(255, 240, 150, 255))
    return im


def draw_neon_mushroom() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([7, 9, 8, 15], fill=(240, 240, 245, 255))
    d.ellipse([3, 3, 12, 9], fill=(120, 60, 220, 255))
    d.arc([2, 2, 13, 10], 200, 340, fill=(180, 140, 255, 180))
    d.point((7, 5), fill=(200, 180, 255, 255))
    return im


def draw_spore_node() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    wood = (90, 70, 50)
    for y in range(16):
        for x in range(16):
            if 6 <= x <= 9:
                im.putpixel((x, y), (*noise_px(x, y, wood, 6), 255))
    metal = (160, 165, 175)
    d.ellipse([4, 5, 11, 11], fill=(*metal, 255))
    for i in range(8):
        ang = i * 3.14159 / 4
        x1, y1 = 7 + int(3 * __import__("math").cos(ang)), 8 + int(3 * __import__("math").sin(ang))
        d.line([(7, 8), (x1, y1)], fill=(130, 135, 145, 255))
    d.point((7, 8), fill=(220, 50, 50, 255))
    return im


# ── II. 建築方塊 ─────────────────────────────────────────────────────────

def draw_velvet_planks() -> Image.Image:
    im = img()
    base = (245, 230, 200)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, base, 6)
            if y % 4 == 0:
                c = blend(c, (220, 200, 175), 0.15)
            if (x + y * 2) % 5 == 0:
                c = blend(c, (255, 245, 230), 0.2)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_stardust_brick() -> Image.Image:
    im = img()
    brick = (55, 58, 68)
    mortar = (40, 38, 48)
    for y in range(16):
        for x in range(16):
            row, col = y // 4, x // 8 + (1 if (y // 4) % 2 else 0)
            in_mortar = y % 4 == 0 or (x + (8 if (y // 4) % 2 else 0)) % 8 == 0
            c = mortar if in_mortar else noise_px(x, y, brick, 6)
            if not in_mortar and (x + y) % 7 == 0:
                c = blend(c, (200, 170, 80), 0.5)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_moonstone_brick() -> Image.Image:
    im = img()
    brick = (140, 155, 185)
    for y in range(16):
        for x in range(16):
            in_mortar = y % 4 == 0 or x % 8 == 0
            c = (110, 125, 155) if in_mortar else noise_px(x, y, brick, 8)
            if not in_mortar and (x - 4) ** 2 + (y - 4) ** 2 < 6:
                c = blend(c, (200, 220, 255), 0.4)
            im.putpixel((x, y), (*c, 255))
    d = ImageDraw.Draw(im)
    d.arc([2, 2, 8, 8], 45, 200, fill=(180, 200, 240, 120))
    return im


def draw_woven_wool(rgb: tuple) -> Image.Image:
    im = img()
    dark = blend(rgb, (0, 0, 0), 0.15)
    for y in range(16):
        for x in range(16):
            c = rgb if (x // 2 + y // 2) % 2 == 0 else dark
            if x % 4 == 0 or y % 4 == 0:
                c = blend(c, dark, 0.25)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_scratch_board() -> Image.Image:
    im = img()
    base = (235, 225, 205)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, base, 5), 255))
    d = ImageDraw.Draw(im)
    for i in range(8):
        d.line([(1, 2 + i * 2), (14, 3 + i * 2)], fill=(180, 165, 145, 200), width=1)
    for i in range(5):
        d.line([(3 + i, 1), (2 + i, 14)], fill=(200, 185, 165, 150), width=1)
    return im


def draw_velvet_carpet() -> Image.Image:
    im = img()
    base = (250, 240, 220)
    for y in range(16):
        for x in range(16):
            wave = int(8 * __import__("math").sin(x * 0.8) * __import__("math").cos(y * 0.6))
            c = blend(base, (255, 250, 235), abs(wave) / 16)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_starlight_marble() -> Image.Image:
    im = img()
    base = (245, 245, 250)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, base, 4)
            if (x * 3 + y * 5) % 11 == 0:
                c = blend(c, (255, 220, 100), 0.6)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_thatch_roof() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    brown = (100, 70, 40)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, brown, 12)
            if y % 3 == 0:
                c = blend(c, (80, 55, 30), 0.2)
            im.putpixel((x, y), (*c, 255))
    for x in range(0, 16, 3):
        d.line([(x, 0), (x + 1, 15)], fill=(70, 50, 25, 100), width=1)
    return im


# ── III. 裝飾家具 ────────────────────────────────────────────────────────

def draw_yarn_lantern() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.line([(8, 0), (8, 3)], fill=(120, 80, 40, 255))
    d.ellipse([3, 3, 12, 12], fill=(240, 160, 50, 255))
    for i in range(6):
        d.arc([4, 3, 11, 11], 20 + i * 28, 48 + i * 28, fill=(200, 140, 40, 180))
    d.ellipse([5, 4, 10, 10], fill=(255, 210, 90, 255))
    d.ellipse([6, 5, 9, 8], fill=(255, 245, 200, 220))
    d.point((7, 6), fill=(255, 255, 240, 255))
    return im


def draw_moonstone_lantern() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([6, 5, 9, 15], fill=(100, 105, 125, 255))
    d.rectangle([7, 4, 8, 5], fill=(80, 85, 100, 255))
    d.polygon([(8, 0), (12, 5), (8, 8), (4, 5)], fill=(160, 200, 255, 255))
    d.polygon([(8, 2), (10, 5), (8, 6), (6, 5)], fill=(220, 240, 255, 255))
    d.point((8, 3), fill=(255, 255, 255, 255))
    for y in range(6, 14):
        if y % 2 == 0:
            d.point((6, y), fill=(140, 150, 170, 255))
            d.point((9, y), fill=(140, 150, 170, 255))
    return im


def draw_velvet_vine() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(16):
        im.putpixel((0, y), (*noise_px(0, y, (90, 110, 85), 8), 255))
    d.line([(2, 0), (3, 15)], fill=(70, 100, 60, 255), width=2)
    d.line([(5, 2), (6, 14)], fill=(80, 110, 70, 255), width=1)
    for vy in range(2, 15, 3):
        d.ellipse([1, vy, 4, vy + 2], fill=(245, 245, 240, 200))
    return im


def draw_cat_climb() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for col in (3, 11):
        for y in range(16):
            c = noise_px(col, y, (150, 115, 75), 8)
            d.rectangle([col, y, col + 1, y], fill=(*c, 255))
        for y in range(0, 16, 2):
            d.point((col, y), fill=(90, 70, 45, 255))
    for py, shade in [(3, (210, 185, 155)), (8, (195, 170, 140)), (13, (185, 160, 130))]:
        d.rectangle([1, py, 14, py + 2], fill=(*shade, 255))
        d.line([(1, py), (14, py)], fill=(160, 135, 105, 255))
    d.line([(2, 15), (13, 15)], fill=(120, 95, 65, 255))
    return im


def draw_cat_bed(dark: bool) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    rim = (30, 30, 35) if dark else (220, 140, 60)
    inner = (60, 30, 90) if dark else (255, 200, 150)
    d.ellipse([1, 4, 14, 14], fill=(*rim, 255))
    d.ellipse([3, 6, 12, 12], fill=(*inner, 255))
    if not dark:
        for px, py, c in [(4, 7, (255, 100, 50)), (9, 8, (40, 40, 40)), (6, 10, (255, 255, 255))]:
            d.point((px, py), fill=(*c, 255))
    return im


def draw_food_bowl() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.polygon([(3, 8), (13, 8), (12, 13), (4, 13)], fill=(200, 200, 210, 255))
    d.arc([4, 6, 12, 10], 0, 180, fill=(180, 180, 195, 255))
    d.line([(6, 9), (9, 11)], fill=(150, 150, 160, 255))
    d.line([(9, 11), (10, 9)], fill=(150, 150, 160, 255))
    return im


def draw_fireplace() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(2, 15):
        for x in range(2, 14):
            im.putpixel((x, y), (*noise_px(x, y, (95, 92, 98), 6), 255))
    d.rectangle([4, 6, 11, 13], fill=(25, 22, 28, 255))
    d.polygon([(5, 12), (8, 4), (11, 12)], fill=(255, 120, 30, 255))
    d.polygon([(6, 12), (8, 7), (10, 12)], fill=(255, 200, 70, 255))
    d.point((8, 9), fill=(255, 255, 200, 255))
    d.rectangle([3, 14, 12, 15], fill=(235, 215, 185, 255))
    d.line([(4, 14), (11, 14)], fill=(200, 180, 150, 255))
    return im


# ── IV. 黑泥腐化 ─────────────────────────────────────────────────────────

def draw_black_mud_stage(stage: int) -> Image.Image:
    im = img()
    base_orig = (120, 100, 80)
    for y in range(16):
        for x in range(16):
            if stage == 1:
                c = noise_px(x, y, base_orig, 8)
                if (x * 7 + y * 11) % 23 < 3:
                    c = (25, 20, 30)
                im.putpixel((x, y), (*c, 255))
            elif stage == 2:
                c = base_orig if (x + y) % 3 != 0 else (20, 18, 25)
                if (x + y) % 3 == 0:
                    c = blend(c, (40, 35, 50), 0.5)
                im.putpixel((x, y), (*noise_px(x, y, c, 6), 255))
            elif stage == 3:
                c = noise_px(x, y, (15, 12, 20), 8)
                if (x + y) % 5 == 0:
                    c = blend(c, (30, 25, 40), 0.4)
                im.putpixel((x, y), (*c, 255))
            else:
                c = noise_px(x, y, (8, 5, 15), 6)
                if (x + y) % 4 == 0:
                    c = blend(c, (50, 20, 60), 0.35)
                im.putpixel((x, y), (*c, 255))
    if stage >= 4:
        d = ImageDraw.Draw(im)
        d.line([(4, 14), (4, 12)], fill=(5, 5, 10, 255))
        d.line([(11, 13), (11, 11)], fill=(5, 5, 10, 255))
    if stage >= 3:
        d = ImageDraw.Draw(im)
        d.point((7, 5), fill=(50, 45, 60, 200))
        d.point((9, 8), fill=(40, 35, 55, 200))
    return im


def draw_blind_water(thin: bool = False) -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            if thin and y > 2:
                continue
            c = (5, 5, 12)
            if (x + y) % 6 == 0:
                c = (40, 45, 55)
            a = 120 if thin else 255
            im.putpixel((x, y), (*c, a))
    return im


# ── V. 功能方塊 ──────────────────────────────────────────────────────────

def draw_distiller() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([4, 10, 11, 15], fill=(180, 150, 80, 255))
    d.rectangle([6, 4, 9, 10], fill=(200, 230, 255, 180))
    d.line([(7, 2), (7, 4)], fill=(160, 140, 70, 255))
    d.arc([5, 5, 10, 9], 0, 180, fill=(150, 180, 220, 200))
    return im


def draw_seal_pedestal() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([3, 10, 12, 15], fill=(110, 110, 120, 255))
    d.rectangle([5, 6, 10, 10], fill=(130, 130, 145, 255))
    d.ellipse([5, 4, 10, 8], fill=(80, 140, 220, 255))
    d.ellipse([6, 5, 9, 7], fill=(120, 180, 255, 200))
    return im


def draw_socketing_table() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([2, 11, 13, 14], fill=(80, 80, 85, 255))
    d.rectangle([4, 7, 11, 11], fill=(100, 100, 110, 255))
    d.ellipse([6, 5, 9, 8], fill=(255, 220, 80, 255))
    d.rectangle([12, 6, 14, 9], fill=(140, 140, 150, 255))
    return im


def draw_pure_light_tower() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(5, 16):
        for x in range(5, 10):
            im.putpixel((x, y), (*noise_px(x, y, (225, 228, 238), 6), 255))
    d.rectangle([4, 14, 10, 15], fill=(180, 185, 200, 255))
    d.ellipse([3, 0, 12, 9], fill=(255, 210, 70, 255))
    d.ellipse([5, 2, 10, 7], fill=(255, 255, 210, 230))
    for ang in range(0, 360, 45):
        tx = int(7.5 + math.cos(math.radians(ang)) * 5)
        ty = int(4 + math.sin(math.radians(ang)) * 3)
        d.point((tx, ty), fill=(255, 250, 200, 180))
    d.line([(7, 9), (7, 5)], fill=(255, 255, 240, 120))
    return im


def draw_memory_lighthouse() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(4, 16):
        for x in range(6, 9):
            c = noise_px(x, y, (165, 158, 150), 8)
            if x == 6:
                c = blend(c, (130, 120, 115), 0.35)
            im.putpixel((x, y), (*c, 255))
    d.polygon([(8, 0), (11, 4), (5, 4)], fill=(210, 200, 190, 255))
    d.rectangle([6, 10, 8, 11], fill=(90, 85, 80, 255))
    d.ellipse([4, 0, 11, 6], fill=(255, 215, 90, 255))
    d.ellipse([6, 1, 9, 4], fill=(255, 245, 200, 220))
    d.line([(8, 0), (8, 15)], fill=(255, 230, 150, 80))
    d.point((2, 3), fill=(255, 240, 180, 160))
    d.point((13, 4), fill=(255, 240, 180, 140))
    return im


def draw_portal_frame() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 3, 15], fill=(160, 180, 220, 255))
    d.rectangle([12, 0, 15, 15], fill=(160, 180, 220, 255))
    d.rectangle([0, 0, 15, 3], fill=(160, 180, 220, 255))
    d.ellipse([1, 0, 4, 4], fill=(180, 200, 240, 255))
    d.ellipse([11, 0, 14, 4], fill=(180, 200, 240, 255))
    return im


def draw_portal_core() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            t = ((x - 8) ** 2 + (y - 8) ** 2) ** 0.5
            if t < 7:
                c = blend((80, 40, 140), (140, 80, 200), t / 7)
                a = int(220 - t * 20)
                im.putpixel((x, y), (*c, max(80, a)))
    return im


# ── VI. 礦物資源 ─────────────────────────────────────────────────────────

def draw_purr_crystal() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            c = blend((255, 210, 80), (255, 240, 150), ((x + y) % 6) / 6)
            if (x * 5 + y * 3) % 9 == 0:
                c = (255, 255, 220)
            im.putpixel((x, y), (*c, 220))
    return im


def draw_shadow_crystal() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, (50, 25, 70), 12)
            if (x + y) % 5 == 0:
                c = blend(c, (30, 10, 50), 0.3)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_salt_block() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, (245, 245, 250), 8)
            if x == 0 or y == 0:
                c = blend(c, (255, 255, 255), 0.3)
            im.putpixel((x, y), (*c, 230))
    return im


def draw_spore_metal() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    base = (170, 175, 185)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, base, 6), 255))
    for i in range(4):
        cx, cy = 4 + i * 3, 4 + (i % 2) * 4
        d.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], outline=(130, 135, 145, 200))
    return im


# ── VII. Boss 祭壇 ───────────────────────────────────────────────────────

def draw_boss_altar(kind: str) -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    if kind == "squall":
        for y in range(16):
            for x in range(16):
                im.putpixel((x, y), (*noise_px(x, y, (100, 100, 105), 10), 255))
        d.line([(3, 4), (6, 8)], fill=(255, 255, 200, 255))
        d.point((7, 5), fill=(200, 200, 220, 255))
    elif kind == "ashura":
        for y in range(16):
            for x in range(16):
                im.putpixel((x, y), (*noise_px(x, y, (25, 25, 30), 6), 255))
        d.line([(2, 6), (13, 6)], fill=(60, 55, 70, 200))
        d.line([(4, 8), (11, 10)], fill=(50, 45, 60, 200))
    elif kind == "orange":
        d.rectangle([2, 8, 13, 14], fill=(120, 80, 50, 255))
        d.ellipse([5, 5, 10, 9], fill=(200, 200, 210, 255))
    else:  # primal heart
        d.ellipse([3, 4, 12, 13], fill=(15, 10, 20, 255))
        d.line([(5, 7), (10, 9)], fill=(180, 40, 50, 255))
        d.line([(6, 10), (9, 7)], fill=(160, 30, 40, 255))
    return im


def draw_decree_pedestal() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([3, 11, 12, 15], fill=(120, 95, 50, 255))
    d.rectangle([4, 7, 11, 11], fill=(180, 145, 70, 255))
    d.ellipse([5, 3, 10, 8], fill=(255, 220, 100, 255))
    d.line([(6, 5), (9, 5)], fill=(90, 60, 30, 255))
    d.point((7, 6), fill=(255, 80, 60, 255))
    return im


def draw_cat_kitchen() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([1, 10, 14, 15], fill=(140, 90, 50, 255))
    d.rectangle([3, 6, 12, 10], fill=(200, 120, 60, 255))
    d.ellipse([5, 2, 10, 7], fill=(240, 200, 120, 255))
    d.point((6, 4), fill=(255, 100, 80, 255))
    d.point((9, 4), fill=(255, 100, 80, 255))
    d.rectangle([11, 7, 14, 9], fill=(180, 180, 190, 255))
    return im


def draw_picture_book_stand() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([4, 10, 11, 15], fill=(150, 110, 80, 255))
    d.rectangle([5, 3, 10, 10], fill=(255, 240, 220, 255))
    d.line([(6, 5), (9, 5)], fill=(200, 120, 140, 255))
    d.line([(6, 7), (9, 7)], fill=(180, 160, 200, 255))
    d.point((7, 8), fill=(255, 180, 200, 255))
    return im


def draw_blueprint_table() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([1, 11, 14, 15], fill=(100, 75, 45, 255))
    d.rectangle([2, 7, 13, 11], fill=(170, 130, 80, 255))
    d.rectangle([4, 3, 11, 7], fill=(240, 230, 200, 255))
    d.line([(5, 4), (10, 4)], fill=(80, 120, 180, 255))
    d.line([(5, 6), (9, 6)], fill=(80, 120, 180, 255))
    d.rectangle([11, 4, 14, 8], fill=(200, 180, 140, 255))
    return im


def draw_cat_core() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, (60, 55, 65), 8), 255))
    d.rectangle([3, 3, 12, 12], fill=(255, 200, 60, 255))
    d.line([(5, 6), (10, 6)], fill=(80, 60, 30, 255))
    d.line([(5, 8), (10, 8)], fill=(80, 60, 30, 255))
    d.point((7, 10), fill=(255, 100, 80, 255))
    return im


def draw_velvet_block() -> Image.Image:
    im = img()
    base = (230, 210, 180)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, base, 8)
            if (x + y) % 3 == 0:
                c = blend(c, (255, 245, 230), 0.25)
            im.putpixel((x, y), (*c, 255))
    return im


def draw_pawprint_glass() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            c = blend((200, 220, 240), (170, 195, 225), ((x + y) % 4) / 4)
            im.putpixel((x, y), (*c, 160))
    d = ImageDraw.Draw(im)
    for cx, cy in [(5, 6), (11, 10)]:
        d.ellipse([cx - 2, cy - 1, cx + 2, cy + 2], fill=(255, 255, 255, 200))
        d.point((cx - 1, cy + 2), fill=(255, 255, 255, 180))
        d.point((cx, cy + 3), fill=(255, 255, 255, 180))
        d.point((cx + 1, cy + 2), fill=(255, 255, 255, 180))
    return im


def draw_memory_monument_base() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    stone = (150, 145, 140)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, stone, 8), 255))
    d.rectangle([5, 2, 10, 14], fill=(130, 125, 120, 255))
    d.rectangle([6, 4, 9, 12], fill=(110, 105, 115, 255))
    for ly in (6, 8, 10):
        d.line([(6, ly), (9, ly)], fill=(180, 170, 200, 200))
    d.rectangle([4, 14, 11, 15], fill=(100, 95, 90, 255))
    return im


def draw_memory_monument_top() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(8, 16):
        for x in range(6, 10):
            im.putpixel((x, y), (*noise_px(x, y, (155, 148, 142), 6), 255))
    d.polygon([(8, 0), (12, 5), (8, 8), (4, 5)], fill=(255, 220, 90, 255))
    d.polygon([(8, 2), (10, 5), (8, 6), (6, 5)], fill=(255, 245, 210, 230))
    d.point((8, 3), fill=(255, 255, 255, 255))
    d.line([(8, 0), (8, 15)], fill=(255, 230, 150, 100))
    d.point((3, 4), fill=(255, 240, 180, 140))
    d.point((13, 5), fill=(255, 240, 180, 120))
    return im


def draw_altar_foundation() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, (18, 18, 28), 6)
            if (x + y) % 4 == 0:
                c = blend(c, (40, 35, 55), 0.3)
            im.putpixel((x, y), (*c, 255))
    for px, py in [(4, 4), (11, 4), (4, 11), (11, 11)]:
        d.rectangle([px, py, px + 1, py + 1], fill=(120, 200, 255, 255))
    d.line([(7, 6), (8, 6), (8, 9), (7, 9), (7, 6)], fill=(255, 210, 70, 255))
    d.point((7, 7), fill=(255, 240, 150, 255))
    d.point((8, 8), fill=(255, 240, 150, 255))
    return im


def draw_neon_mushroom_pot() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([4, 11, 11, 15], fill=(180, 100, 70, 255))
    d.rectangle([5, 10, 10, 11], fill=(160, 85, 55, 255))
    d.rectangle([7, 9, 8, 15], fill=(245, 245, 250, 255))
    d.ellipse([3, 3, 12, 9], fill=(120, 60, 220, 255))
    d.arc([2, 2, 13, 10], 200, 340, fill=(180, 140, 255, 200))
    d.point((7, 5), fill=(220, 200, 255, 255))
    return im


def draw_neon_mush_lamp() -> Image.Image:
    im = draw_neon_mushroom_pot()
    d = ImageDraw.Draw(im)
    d.line([(8, 0), (8, 2)], fill=(100, 70, 40, 255))
    d.point((8, 1), fill=(255, 240, 180, 255))
    return im


def draw_toy_box() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    wood = (140, 100, 60)
    for y in range(16):
        for x in range(16):
            if y >= 5:
                im.putpixel((x, y), (*noise_px(x, y, wood, 8), 255))
    d.rectangle([2, 5, 13, 14], outline=(100, 70, 40, 255))
    for x in range(3, 13, 2):
        d.line([(x, 6), (x, 13)], fill=(110, 75, 45, 180))
    d.ellipse([4, 7, 7, 10], fill=(240, 160, 50, 255))
    d.line([(9, 8), (11, 8)], fill=(180, 140, 100, 255))
    d.point((10, 9), fill=(200, 60, 60, 255))
    d.rectangle([2, 4, 13, 6], fill=(120, 85, 50, 255))
    return im


def draw_cardboard(corrugated: bool = False) -> Image.Image:
    im = img()
    base = (175, 130, 85) if corrugated else (160, 120, 75)
    for y in range(16):
        for x in range(16):
            c = noise_px(x, y, base, 6)
            if y % 3 == 1:
                c = blend(c, (130, 95, 55), 0.25)
            im.putpixel((x, y), (*c, 255))
    if corrugated:
        d = ImageDraw.Draw(im)
        d.line([(1, 4), (14, 4)], fill=(220, 140, 50, 255))
        d.line([(2, 10), (13, 10)], fill=(200, 120, 40, 255))
    return im


def draw_rope_net() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, (200, 175, 130), 4), 255))
    d = ImageDraw.Draw(im)
    for i in range(-16, 20, 4):
        d.line([(i, 0), (i + 15, 15)], fill=(140, 110, 70, 200), width=1)
        d.line([(i + 15, 0), (i, 15)], fill=(140, 110, 70, 200), width=1)
    return im


def draw_tape_block() -> Image.Image:
    im = img()
    gray = (130, 130, 135)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, gray, 5), 255))
    d = ImageDraw.Draw(im)
    d.rectangle([0, 6, 15, 9], fill=(160, 160, 168, 255))
    d.line([(0, 7), (15, 7)], fill=(200, 200, 210, 200))
    d.line([(0, 8), (15, 8)], fill=(90, 90, 95, 180))
    return im


def draw_tape_temple() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    purple = (90, 45, 120)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, purple, 8), 255))
    d.polygon([(8, 1), (13, 6), (8, 8), (3, 6)], fill=(120, 70, 160, 255))
    d.rectangle([5, 7, 10, 14], fill=(70, 35, 95, 255))
    d.line([(2, 10), (13, 10)], fill=(180, 180, 190, 220))
    d.line([(4, 12), (11, 12)], fill=(160, 160, 170, 200))
    d.point((8, 4), fill=(255, 220, 120, 255))
    return im


def draw_undercat_tree_hole() -> Image.Image:
    im = img()
    bark = (95, 70, 45)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, bark, 10), 255))
    d = ImageDraw.Draw(im)
    d.ellipse([5, 6, 11, 12], fill=(25, 18, 15, 255))
    d.arc([5, 6, 11, 12], 30, 330, fill=(60, 45, 30, 255))
    d.point((7, 9), fill=(180, 160, 120, 200))
    return im


def draw_undercat_blind_rift() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            t = abs(x - 8) / 8
            c = blend((40, 20, 60), (10, 5, 20), t)
            if (x + y) % 5 == 0:
                c = blend(c, (80, 50, 120), 0.4)
            im.putpixel((x, y), (*c, 255))
    d = ImageDraw.Draw(im)
    d.line([(8, 0), (8, 15)], fill=(120, 80, 180, 180))
    return im


def draw_undercat_gear_shaft() -> Image.Image:
    im = img()
    copper = (180, 110, 70)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, copper, 8), 255))
    d = ImageDraw.Draw(im)
    d.ellipse([4, 4, 11, 11], outline=(120, 75, 45, 255))
    for i in range(6):
        ang = i * math.pi / 3
        x1 = int(7.5 + 4 * math.cos(ang))
        y1 = int(7.5 + 4 * math.sin(ang))
        d.line([(7, 7), (x1, y1)], fill=(140, 90, 55, 255))
    d.point((7, 7), fill=(220, 180, 120, 255))
    return im


def draw_undercat_lighthouse_well() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    gold = (210, 175, 60)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, gold, 6), 255))
    d.ellipse([4, 5, 11, 12], fill=(40, 35, 30, 255))
    d.ellipse([5, 6, 10, 10], fill=(255, 230, 120, 220))
    d.point((7, 8), fill=(255, 255, 200, 255))
    d.rectangle([6, 2, 9, 5], fill=(180, 150, 50, 255))
    return im


def draw_undercat_sanctuary_pool() -> Image.Image:
    im = img()
    for y in range(16):
        for x in range(16):
            c = blend((140, 200, 230), (100, 170, 210), y / 16)
            if (x + y) % 6 == 0:
                c = blend(c, (200, 240, 255), 0.35)
            im.putpixel((x, y), (*c, 255))
    d = ImageDraw.Draw(im)
    d.arc([3, 8, 12, 15], 0, 180, fill=(80, 140, 180, 200))
    return im


def draw_undercat_waystone() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    base = (120, 80, 160)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, base, 10), 255))
    d.rectangle([5, 3, 10, 14], fill=(150, 110, 190, 255))
    d.polygon([(7, 1), (10, 4), (7, 5), (5, 4)], fill=(200, 170, 255, 255))
    d.point((7, 8), fill=(255, 240, 200, 255))
    d.line([(7, 6), (7, 12)], fill=(180, 140, 230, 180))
    return im


def draw_ryokatana_shop() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), (*noise_px(x, y, (245, 235, 215), 5), 255))
    d.rectangle([1, 10, 14, 14], fill=(120, 85, 55, 255))
    d.rectangle([3, 4, 12, 10], fill=(180, 150, 110, 255))
    d.line([(4, 5), (11, 5)], fill=(220, 200, 170, 255))
    d.line([(8, 4), (8, 10)], fill=(200, 60, 60, 255))
    d.point((6, 7), fill=(200, 180, 140, 255))
    d.point((10, 7), fill=(200, 180, 140, 255))
    return im


def draw_ironpaw_forge() -> Image.Image:
    im = img()
    d = ImageDraw.Draw(im)
    d.rectangle([2, 9, 13, 14], fill=(65, 65, 72, 255))
    d.rectangle([4, 5, 11, 9], fill=(45, 45, 50, 255))
    d.polygon([(4, 5), (11, 5), (8, 1)], fill=(100, 100, 108, 255))
    d.point((7, 11), fill=(255, 120, 30, 255))
    d.point((8, 10), fill=(255, 180, 60, 255))
    d.point((9, 11), fill=(255, 100, 20, 255))
    d.rectangle([6, 12, 9, 13], fill=(40, 38, 42, 255))
    return im


# ── 輸出映射：設計書 ID → mod 檔名 ───────────────────────────────────────

TEXTURES: dict[str, Image.Image] = {}


def register(mod_name: str, im: Image.Image) -> None:
    TEXTURES[mod_name] = im


def build_all() -> None:
    # I 自然
    register("velvet_grass", draw_velvet_grass_cross())
    register("stardust_soil", draw_stardust_soil())
    register("petal_grass", draw_petal_grass_top())
    register("catnip", draw_catnip_cross())
    register("velvet_tree_log", draw_velvet_log_side())
    register("velvet_tree_log_top", draw_velvet_log_top())
    register("velvet_tree_leaves", draw_velvet_leaves())
    register("moonstone_cluster", draw_moonstone_ore())
    register("salt_crystal", draw_salt_crystal_ore())
    register("hibiscus_flower", draw_hibiscus_cross())
    register("neon_mushroom", draw_neon_mushroom())
    register("spore_fruit_node", draw_spore_node())

    # II 建築
    register("velvet_planks", draw_velvet_planks())
    register("stardust_brick", draw_stardust_brick())
    register("moonstone_brick", draw_moonstone_brick())
    register("woven_wool", draw_woven_wool((255, 200, 210)))
    register("woven_wool_white", draw_woven_wool((245, 245, 250)))
    register("cat_scratch_board", draw_scratch_board())
    register("velvet_carpet", draw_velvet_carpet())
    register("starlight_marble", draw_starlight_marble())
    register("thatch_roof", draw_thatch_roof())
    register("velvet_block", draw_velvet_block())

    # III 裝飾
    register("yarn_ball_lamp", draw_yarn_lantern())
    register("moonstone_lamp_post", draw_moonstone_lantern())
    register("neon_mushroom_pot", draw_neon_mushroom_pot())
    register("neon_mush_lamp", draw_neon_mush_lamp())
    register("pawprint_glass", draw_pawprint_glass())
    register("velvet_vine", draw_velvet_vine())
    register("cat_climb_platform", draw_cat_climb())
    register("cat_bed", draw_cat_bed(True))
    register("cat_bed_calico", draw_cat_bed(False))
    register("food_bowl", draw_food_bowl())
    register("fireplace", draw_fireplace())
    register("scratching_post", draw_scratch_board())

    # IV 黑泥
    register("black_mud_stage1", draw_black_mud_stage(1))
    register("black_mud_stage2", draw_black_mud_stage(2))
    register("black_mud", draw_black_mud_stage(3))
    register("black_mud_stage4", draw_black_mud_stage(4))
    register("blind_water", draw_blind_water(False))
    register("blind_water_puddle", draw_blind_water(True))
    register("sludge_remains", draw_black_mud_stage(2))

    # V 功能
    register("distiller", draw_distiller())
    register("aroma_distiller", draw_distiller())
    register("altar_foundation", draw_altar_foundation())
    register("memory_monument_base", draw_memory_monument_base())
    register("memory_monument_top", draw_memory_monument_top())
    register("seal_pedestal", draw_seal_pedestal())
    register("socketing_table", draw_socketing_table())
    register("pure_light_tower", draw_pure_light_tower())
    register("memory_lighthouse", draw_memory_lighthouse())
    register("full_moon_altar", draw_seal_pedestal())
    register("cat_kingdom_portal_frame", draw_portal_frame())
    register("cat_kingdom_portal", draw_portal_core())
    register("cat_core_engineering", draw_cat_core())
    register("ironpaw_forge", draw_ironpaw_forge())
    register("ryokatana_shop_stand", draw_ryokatana_shop())
    register("decree_pedestal", draw_decree_pedestal())
    register("cat_kitchen", draw_cat_kitchen())
    register("picture_book_stand", draw_picture_book_stand())
    register("blueprint_table", draw_blueprint_table())
    register("toy_box", draw_toy_box())

    # 地下貓域 DLC
    register("cardboard_block", draw_cardboard(False))
    register("reinforced_cardboard", draw_cardboard(True))
    register("rope_net", draw_rope_net())
    register("tape_block", draw_tape_block())
    register("tape_temple", draw_tape_temple())
    register("undercat_tree_hole", draw_undercat_tree_hole())
    register("undercat_blind_rift", draw_undercat_blind_rift())
    register("undercat_gear_shaft", draw_undercat_gear_shaft())
    register("undercat_lighthouse_well", draw_undercat_lighthouse_well())
    register("undercat_sanctuary_pool", draw_undercat_sanctuary_pool())
    register("undercat_waystone", draw_undercat_waystone())

    # VI 礦物
    register("purr_crystal_block", draw_purr_crystal())
    register("shadow_crystal_block", draw_shadow_crystal())
    register("salt_block", draw_salt_block())
    register("spore_metal_block", draw_spore_metal())
    register("moonstone_block", draw_moonstone_brick())

    # VII Boss
    register("boss_altar_squall", draw_boss_altar("squall"))
    register("boss_altar_ashura", draw_boss_altar("ashura"))
    register("boss_altar_orange", draw_boss_altar("orange"))
    register("boss_heart_primal", draw_boss_altar("primal"))


def write_models() -> None:
    MODELS.mkdir(parents=True, exist_ok=True)
    ITEMS.mkdir(parents=True, exist_ok=True)

    cube_all = [
        "stardust_soil", "velvet_planks", "stardust_brick", "moonstone_brick",
        "woven_wool", "cat_scratch_board", "velvet_carpet", "velvet_block",
        "yarn_ball_lamp", "moonstone_lamp_post", "velvet_vine", "cat_climb_platform",
        "black_mud", "distiller", "seal_pedestal", "socketing_table",
        "pure_light_tower", "memory_lighthouse", "purr_crystal_block",
        "shadow_crystal_block", "cat_core_engineering", "ironpaw_forge",
        "altar_foundation", "scratching_post", "food_bowl", "cat_bed",
        "moonstone_cluster", "salt_crystal", "spore_fruit_node", "starlight_marble",
        "thatch_roof", "fireplace", "salt_block", "spore_metal_block",
        "black_mud_stage1", "black_mud_stage2", "black_mud_stage4",
        "boss_altar_squall", "boss_altar_ashura", "boss_altar_orange", "boss_heart_primal",
        "decree_pedestal", "cat_kitchen", "picture_book_stand", "blueprint_table",
        "moonstone_block", "neon_mush_lamp", "neon_mushroom_pot", "pawprint_glass",
        "memory_monument_base", "memory_monument_top", "aroma_distiller",
        "full_moon_altar", "ryokatana_shop_stand", "cardboard_block",
        "reinforced_cardboard", "rope_net", "tape_block", "tape_temple",
        "undercat_tree_hole", "undercat_blind_rift", "undercat_gear_shaft",
        "undercat_lighthouse_well", "undercat_sanctuary_pool", "undercat_waystone",
        "sludge_remains", "blind_water", "blind_water_puddle", "toy_box",
    ]
    cross = [
        "velvet_grass", "catnip", "hibiscus_flower", "neon_mushroom",
        "velvet_tree_leaves",
    ]
    portal = {
        "cat_kingdom_portal_frame": "cat_kingdom_portal_frame",
        "cat_kingdom_portal": "cat_kingdom_portal",
    }

    for name in cube_all:
        parent = "minecraft:block/carpet" if name == "velvet_carpet" else "minecraft:block/cube_all"
        tex_key = "wool" if name == "velvet_carpet" else "all"
        model = {"parent": parent, "textures": {tex_key: f"cocojenna:block/{name}"}}
        (MODELS / f"{name}.json").write_text(json.dumps(model, indent=2), encoding="utf-8")

    for name in cross:
        model = {
            "render_type": "cutout",
            "parent": "minecraft:block/cross",
            "textures": {"cross": f"cocojenna:block/{name}"},
        }
        (MODELS / f"{name}.json").write_text(json.dumps(model, indent=2), encoding="utf-8")

    for name, tex in portal.items():
        model = {"parent": "minecraft:block/cube_all", "textures": {"all": f"cocojenna:block/{tex}"}}
        (MODELS / f"{name}.json").write_text(json.dumps(model, indent=2), encoding="utf-8")

    # 絨毛草方塊：頂部/側面（未來草方塊用）
    for suffix in ["_top", "_side", "_bottom"]:
        pass  # textures saved as petal_grass style if needed

    log_model = {
        "parent": "minecraft:block/cube_column",
        "textures": {
            "end": "cocojenna:block/velvet_tree_log_top",
            "side": "cocojenna:block/velvet_tree_log",
        },
    }
    (MODELS / "velvet_tree_log.json").write_text(json.dumps(log_model, indent=2), encoding="utf-8")


def write_blockstates() -> None:
    BLOCKSTATES.mkdir(parents=True, exist_ok=True)
    cross_blocks = {
        "velvet_grass", "catnip", "hibiscus_flower", "neon_mushroom", "velvet_tree_leaves",
    }
    log_model = "cocojenna:block/velvet_tree_log"
    log_state = {
        "variants": {
            "axis=x": {"model": log_model, "x": 90, "y": 90},
            "axis=y": {"model": log_model},
            "axis=z": {"model": log_model, "x": 90},
        }
    }
    (BLOCKSTATES / "velvet_tree_log.json").write_text(json.dumps(log_state, indent=2), encoding="utf-8")

    for model_path in sorted(MODELS.glob("*.json")):
        name = model_path.stem
        if name.endswith(("_top", "_side", "_bottom")) or name == "velvet_tree_log":
            continue
        state = {
            "variants": {
                "": {
                    "model": f"cocojenna:block/{name}",
                    **({"render_type": "cutout"} if name in cross_blocks else {}),
                }
            }
        }
        (BLOCKSTATES / f"{name}.json").write_text(json.dumps(state, indent=2), encoding="utf-8")


def patch_vanilla_placeholder_models() -> None:
    """Replace minecraft:block/* wool placeholders with cocojenna textures."""
    fixes = [
        "neon_mush_lamp", "cardboard_block", "reinforced_cardboard", "rope_net",
        "tape_block", "tape_temple", "undercat_tree_hole", "undercat_blind_rift",
        "undercat_gear_shaft", "undercat_lighthouse_well", "undercat_sanctuary_pool",
        "undercat_waystone",
    ]
    for name in fixes:
        model = {
            "parent": "minecraft:block/cube_all",
            "textures": {"all": f"cocojenna:block/{name}"},
        }
        (MODELS / f"{name}.json").write_text(json.dumps(model, indent=2), encoding="utf-8")
        item_path = ITEMS / f"{name}.json"
        if not item_path.exists():
            item_path.write_text(
                json.dumps({"parent": f"cocojenna:block/{name}"}, indent=2), encoding="utf-8")


def main() -> None:
    print("Generating block textures...")
    build_all()
    for name, im in sorted(TEXTURES.items()):
        save(name, im)
    print(f"Total: {len(TEXTURES)} textures")
    write_models()
    write_blockstates()
    patch_vanilla_placeholder_models()
    print("Models and blockstates updated.")
    import subprocess
    import sys
    layered = ROOT / "tools" / "gen_layered_assets.py"
    if layered.is_file():
        print("Applying layered faces + item icons...")
        subprocess.check_call([sys.executable, str(layered)], cwd=ROOT)


if __name__ == "__main__":
    main()
