#!/usr/bin/env python3
"""Layered pixel-art textures for black mud mobs and NPC cats (16px style on 64x32/64x64)."""
from __future__ import annotations

import math
from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("pip install pillow")

ROOT = Path(__file__).resolve().parent.parent
CAT_OUT = ROOT / "src/main/resources/assets/cocojenna/textures/entity/cat"
MUD_OUT = ROOT / "src/main/resources/assets/cocojenna/textures/entity/black_mud"
BOSS_OUT = ROOT / "src/main/resources/assets/cocojenna/textures/entity/boss"


def rgba(r, g, b, a=255):
    return (int(r), int(g), int(b), int(a))


def noise(x, y, base, amp=10):
    n = ((x * 17 + y * 31) ^ (x * y * 7)) & 0xFF
    d = (n % (amp * 2 + 1)) - amp
    return tuple(max(0, min(255, base[i] + d)) for i in range(3))


def fill_ellipse(im: Image.Image, cx, cy, rx, ry, color):
    px = im.load()
    for y in range(im.height):
        for x in range(im.width):
            if ((x - cx) / max(0.1, rx)) ** 2 + ((y - cy) / max(0.1, ry)) ** 2 <= 1:
                if len(color) == 4:
                    px[x, y] = color
                else:
                    px[x, y] = (*color, 255)


def shade(base, factor):
    return tuple(max(0, min(255, int(c * factor))) for c in base[:3])


# ── Ocelot 64×32 base ────────────────────────────────────────────────────

def draw_ocelot_base(im: Image.Image, body, belly, dark, eye, accent=None):
    d = ImageDraw.Draw(im)
    # 身體主體
    for y in range(7, 17):
        for x in range(18, 34):
            if 20 <= x <= 32:
                c = belly if y > 12 else body
                im.putpixel((x, y), (*noise(x, y, c, 6), 255))
    # 頭部（左）
    for y in range(2, 9):
        for x in range(2, 12):
            if (x - 7) ** 2 + (y - 5) ** 2 < 20:
                im.putpixel((x, y), (*noise(x, y, body, 5), 255))
    # 頭部（右側貼圖）
    for y in range(2, 9):
        for x in range(40, 50):
            if (x - 45) ** 2 + (y - 5) ** 2 < 18:
                im.putpixel((x, y), (*noise(x, y, body, 5), 255))
    # 腿
    for lx, ly in [(20, 17), (28, 17), (36, 1), (47, 1)]:
        for dy in range(6):
            for dx in range(-1, 2):
                im.putpixel((lx + dx, ly + dy), (*dark, 255))
    # 尾巴
    for i in range(10):
        tx, ty = 54 + i // 2, 8 + i
        im.putpixel((tx, ty), (*noise(tx, ty, body, 4), 255))
        if accent and i > 7:
            im.putpixel((tx, ty), (*accent, 255))
    # 眼
    d.point((6, 5), fill=(*eye, 255))
    d.point((8, 5), fill=(*eye, 255))
    d.point((43, 5), fill=(*eye, 255))


def patch_rect(im, x1, y1, x2, y2, color):
    c = color if len(color) == 4 else (*color, 255)
    for y in range(y1, y2):
        for x in range(x1, x2):
            if 0 <= x < 64 and 0 <= y < 32:
                im.putpixel((x, y), c)


def make_cat(name: str, **kwargs) -> Image.Image:
    im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    draw_ocelot_base(im, **kwargs)
    return im


def customize_coco(im):
    """原版全黑貓為底，只加尾尖白毛與琥珀眼."""
    d = ImageDraw.Draw(im)
    for x, y in [(58, 16), (59, 17), (60, 17), (61, 18)]:
        if 0 <= x < 64 and 0 <= y < 32:
            im.putpixel((x, y), (245, 242, 235, 255))
    d.point((6, 5), fill=(255, 191, 0, 255))
    d.point((8, 5), fill=(255, 191, 0, 255))
    d.point((43, 5), fill=(255, 191, 0, 255))


def customize_jenna(im):
    patch_rect(im, 22, 8, 28, 12, (35, 28, 22))
    patch_rect(im, 24, 10, 30, 14, (210, 120, 45))
    patch_rect(im, 4, 4, 7, 6, (0, 0, 0, 0))  # 左耳缺口
    ImageDraw.Draw(im).point((6, 5), fill=(230, 230, 60, 255))


def customize_sanhua(im):
    patch_rect(im, 20, 9, 26, 13, (200, 100, 50))
    patch_rect(im, 4, 3, 8, 7, (240, 230, 210))
    patch_rect(im, 44, 3, 48, 7, (50, 40, 35))


def customize_cheshire(im):
    for y in range(32):
        for x in range(64):
            if im.getpixel((x, y))[3] > 0:
                im.putpixel((x, y), (*noise(x, y, (55, 35, 85), 8), 255))
    d = ImageDraw.Draw(im)
    d.arc([4, 4, 10, 8], 0, 180, fill=(240, 240, 250, 255))


def customize_white_glove(im):
    for y in range(32):
        for x in range(64):
            if im.getpixel((x, y))[3] > 0:
                im.putpixel((x, y), (*noise(x, y, (30, 30, 35), 4), 255))
    patch_rect(im, 18, 10, 32, 16, (25, 25, 32))
    patch_rect(im, 19, 11, 31, 13, (240, 240, 245))
    for lx in (20, 28, 36, 47):
        patch_rect(im, lx, 16 if lx < 36 else 0, lx + 2, 20 if lx < 36 else 4, (250, 250, 255))
    ImageDraw.Draw(im).point((6, 5), fill=(235, 232, 228, 255))
    ImageDraw.Draw(im).point((8, 5), fill=(235, 232, 228, 255))


def customize_blackjack(im):
    for y in range(32):
        for x in range(64):
            if im.getpixel((x, y))[3] > 0:
                im.putpixel((x, y), (*noise(x, y, (22, 22, 28), 5), 255))
    patch_rect(im, 18, 10, 32, 16, (18, 18, 24))
    patch_rect(im, 3, 1, 11, 4, (15, 15, 20))
    d = ImageDraw.Draw(im)
    d.point((6, 5), fill=(220, 180, 40, 255))
    d.point((8, 5), fill=(160, 80, 220, 255))


def customize_alpha(im):
    for y in range(32):
        for x in range(64):
            p = im.getpixel((x, y))
            if p[3] > 0:
                im.putpixel((x, y), (80, 150, 255, 90))
    for y in range(32):
        for x in range(64):
            if (x + y) % 5 == 0 and im.getpixel((x, y))[3] > 0:
                im.putpixel((x, y), (140, 200, 255, 140))


def customize_mimic(im):
    patch_rect(im, 5, 4, 9, 7, (40, 38, 42))
    for y in range(8, 16):
        for x in range(20, 32):
            if (x + y) % 4 == 0:
                im.putpixel((x, y), (15, 12, 18, 255))


def customize_samurai(im):
    patch_rect(im, 18, 6, 32, 9, (120, 120, 130))
    patch_rect(im, 20, 8, 30, 10, (180, 50, 45))


def customize_big_orange(im):
    ImageDraw.Draw(im).ellipse([5, 4, 9, 7], fill=(200, 220, 255, 180))


def customize_court_lady(im):
    patch_rect(im, 18, 10, 32, 16, (180, 60, 90))
    patch_rect(im, 6, 6, 10, 8, (240, 200, 210))


def customize_monk(im):
    patch_rect(im, 17, 8, 33, 17, (120, 90, 60))
    patch_rect(im, 5, 3, 11, 9, (90, 70, 50))


def customize_general(im):
    patch_rect(im, 16, 7, 34, 12, (140, 130, 120))
    patch_rect(im, 19, 9, 31, 11, (200, 180, 60))


def customize_ironpaw(im):
    body = (70, 72, 78)
    dark = (45, 48, 52)
    for y in range(32):
        for x in range(64):
            if im.getpixel((x, y))[3] > 0:
                c = dark if (x + y) % 5 == 0 else body
                im.putpixel((x, y), (*noise(x, y, c, 5), 255))
    d = ImageDraw.Draw(im)
    d.line([(5, 4), (7, 6)], fill=(120, 40, 40, 255))
    patch_rect(im, 28, 17, 31, 20, (180, 175, 165))


def customize_shadow_claw(im):
    for y in range(32):
        for x in range(64):
            p = im.getpixel((x, y))
            if p[3] == 0:
                continue
            if x >= 30:
                im.putpixel((x, y), (*noise(x, y, (12, 8, 18), 6), 255))
            else:
                im.putpixel((x, y), (*noise(x, y, (22, 18, 28), 5), 255))
    d = ImageDraw.Draw(im)
    patch_rect(im, 16, 8, 28, 14, (50, 35, 70, 200))
    d.point((6, 5), fill=(180, 60, 220, 255))


# ── Slime 64×32 ──────────────────────────────────────────────────────────

def slime_blob(cx, cy, rx, ry, core, edge, cracks=None, extra=None):
    im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    px = im.load()
    for y in range(32):
        for x in range(64):
            if ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2 <= 1:
                t = (y - cy + rx) / (2 * ry)
                c = blend(core, edge, t)
                px[x, y] = (*noise(x, y, c, 8), 255)
    if cracks:
        d = ImageDraw.Draw(im)
        for line in cracks:
            d.line(line, fill=cracks[0] if isinstance(cracks[0], tuple) and len(cracks[0]) == 3 else (100, 180, 255, 200), width=1)
    if extra:
        extra(im)
    return im


def blend(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def make_slime_pair(left_fn, right_fn) -> Image.Image:
    im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    im.paste(left_fn(), (0, 0))
    im.paste(right_fn(), (0, 0))
    return im


def draw_slime_variant(kind: str) -> Image.Image:
    if kind == "heat_leech":
        def blob(cx, cy, rx, ry):
            im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
            fill_ellipse(im, cx, cy, rx, ry * 0.55, (12, 14, 22))
            d = ImageDraw.Draw(im)
            for i in range(4):
                d.line([(cx - 3 + i * 2, cy - 2), (cx - 1 + i * 2, cy + 2)], fill=(120, 200, 255, 220))
            for sx, sy in [(cx - 2, cy), (cx + 1, cy - 1)]:
                d.point((sx, sy - 3), fill=(240, 245, 255, 180))
            return im
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        out.alpha_composite(blob(16, 14, 12, 10))
        out.alpha_composite(blob(48, 14, 10, 9))
        return out

    if kind == "forgotten_wisp":
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        fill_ellipse(out, 16, 12, 11, 9, (8, 8, 12, 140))
        fill_ellipse(out, 48, 12, 9, 8, (20, 18, 28, 120))
        d = ImageDraw.Draw(out)
        d.ellipse([14, 10, 18, 13], fill=(40, 35, 50, 80))
        d.point((15, 11), fill=(180, 170, 200, 200))
        return out

    if kind == "whispering_doll":
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        fill_ellipse(out, 16, 13, 10, 11, (180, 155, 130))
        fill_ellipse(out, 48, 13, 9, 10, (140, 110, 90))
        d = ImageDraw.Draw(out)
        d.line([(15, 10), (17, 12)], fill=(30, 20, 15, 255))
        d.line([(16, 9), (18, 11)], fill=(20, 15, 10, 255))
        d.point((15, 11), fill=(50, 40, 35, 255))
        return out

    if kind == "memory_moth":
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        fill_ellipse(out, 16, 12, 12, 8, (90, 40, 140, 200))
        fill_ellipse(out, 48, 12, 10, 7, (120, 60, 180, 180))
        d = ImageDraw.Draw(out)
        for i in range(3):
            d.line([(12 + i * 3, 8), (20, 14)], fill=(200, 150, 255, 150))
            d.rectangle([46 + i, 10, 48 + i, 12], fill=(255, 200, 220, 180))
        return out

    if kind == "mimic_cat":
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        fill_ellipse(out, 16, 13, 10, 9, (160, 140, 120))
        fill_ellipse(out, 48, 13, 9, 8, (130, 110, 95))
        d = ImageDraw.Draw(out)
        d.point((14, 11), fill=(20, 18, 22, 255))
        d.point((17, 12), fill=(20, 18, 22, 255))
        return out

    if kind == "glitch_cat":
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        fill_ellipse(out, 16, 13, 10, 9, (40, 40, 48))
        fill_ellipse(out, 48, 13, 9, 8, (25, 25, 32))
        d = ImageDraw.Draw(out)
        for px, py, c in [(13, 10, (255, 255, 255)), (15, 11, (0, 0, 0)),
                          (16, 9, (200, 200, 210)), (18, 12, (80, 80, 90)),
                          (46, 10, (255, 255, 255)), (49, 11, (0, 0, 0))]:
            d.point((px, py), fill=(*c, 255))
        return out

    if kind == "origami_crow":
        out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(out)
        for cx in (16, 48):
            d.polygon([(cx - 6, 14), (cx + 6, 10), (cx + 4, 16), (cx - 4, 16)], fill=(200, 170, 120, 255))
            d.polygon([(cx - 2, 12), (cx + 8, 8), (cx + 2, 14)], fill=(220, 190, 140, 255))
            d.line([(cx - 4, 16), (cx + 2, 18)], fill=(120, 90, 60, 255))
        return out

    out = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    fill_ellipse(out, 16, 14, 11, 10, (18, 16, 28))
    fill_ellipse(out, 48, 14, 10, 9, (35, 30, 48))
    return out


# ── Boss 64×32 貓 UV（對應 OcelotModel / ModelLayers.CAT）────────────────

def draw_boss(name: str) -> Image.Image:
    """Legacy 64×64 — 僅供預覽；實際輸出改用 make_boss_cat_uv。"""
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    themes = {
        "grief_amalgam": ((25, 15, 35), (180, 50, 70), "eyes"),
        "blind_water_lord": ((8, 10, 18), (60, 100, 160), "water"),
        "fallen_velvet": ((190, 185, 180), (40, 35, 38), "knight"),
        "primal_chaos": ((12, 8, 10), (200, 40, 30), "heart"),
        "howling_squall": ((70, 80, 100), (150, 200, 255), "storm"),
        "ashura_phantom": ((30, 28, 32), (90, 85, 95), "bandage"),
        "shadow_claw": ((15, 10, 20), (120, 40, 160), "mud"),
        "thousand_face_stitcher": ((40, 35, 45), (200, 180, 160), "face"),
        "first_cry_warden": ((50, 45, 55), (180, 160, 140), "ward"),
        "fallen_general": ((45, 40, 50), (200, 170, 80), "general"),
        "gear_overlord": ((55, 50, 58), (200, 160, 60), "gear"),
        "moon_alley_wraith": ((25, 30, 55), (180, 200, 255), "moon"),
        "plaza_sentinel": ((60, 58, 65), (220, 200, 120), "stone"),
        "generic": ((28, 24, 36), (180, 40, 220), "gen"),
    }
    base, accent, tag = themes.get(name, themes["generic"])
    for y in range(64):
        for x in range(64):
            if 8 <= x < 56 and 16 <= y < 56:
                im.putpixel((x, y), (*noise(x, y, base, 10), 255))
    if name == "grief_amalgam":
        for ex, ey in [(14, 20), (26, 18), (38, 22), (46, 19), (22, 32), (34, 30)]:
            d.ellipse([ex, ey, ex + 4, ey + 4], fill=(255, 220, 80, 255))
            d.point((ex + 1, ey + 1), fill=(20, 10, 10, 255))
        d.rectangle([18, 38, 46, 44], fill=(180, 60, 80, 200))
    if tag == "water":
        for y in range(40, 64):
            for x in range(8, 56):
                if (x + y) % 3 == 0:
                    im.putpixel((x, y), (5, 8, 15, 220))
    if tag == "knight":
        d.rectangle([20, 10, 44, 18], fill=(100, 100, 110, 255))
        for y in range(30, 64, 4):
            d.line([(22, y), (42, y)], fill=(20, 15, 18, 200))
    if name == "primal_chaos":
        d.ellipse([20, 22, 44, 50], fill=(8, 4, 6, 255))
        for i in range(5):
            ang = i * 1.25
            tx = int(32 + math.cos(ang) * 18)
            ty = int(36 + math.sin(ang) * 14)
            d.line([(32, 36), (tx, ty)], fill=(25, 10, 12, 255), width=2)
        d.line([(24, 30), (40, 38)], fill=(*accent, 255))
        d.line([(26, 36), (38, 28)], fill=(*accent, 255))
    if name == "shadow_claw":
        for y in range(16, 64):
            for x in range(8, 56):
                if x > 32 and (x + y) % 2 == 0:
                    im.putpixel((x, y), (10, 6, 14, 255))
        d.rectangle([18, 12, 46, 22], fill=(40, 30, 55, 180))
    if tag == "storm":
        d.line([(10, 8), (30, 20), (50, 6)], fill=(*accent, 255))
        d.arc([30, 0, 58, 20], 0, 90, fill=(255, 255, 255, 200))
    if tag == "bandage":
        for i in range(6):
            d.line([(14, 20 + i * 5), (50, 22 + i * 5)], fill=(50, 48, 55, 200))
    d.point((20, 22), fill=(*accent, 255))
    d.point((42, 22), fill=(*accent, 255))
    return im


def save_img(path: Path, im: Image.Image, *, force: bool = False):
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.exists() and not force:
        print("  skip (exists)", path.relative_to(ROOT))
        return
    im.save(path)
    print(" ", path.relative_to(ROOT))


def load_vanilla_cat(name: str) -> Image.Image | None:
    """以原版 64×32 貓 UV 為底，確保 CatModel 對位。"""
    import io
    import zipfile

    jars: list[Path] = []
    forge_mc = Path.home() / ".gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1"
    for fname in ("client-extra.jar", "client.jar"):
        p = forge_mc / fname
        if p.is_file():
            jars.append(p)
    for base in (Path.home() / ".gradle/caches/forge_gradle", Path.home() / ".gradle/caches"):
        jars.extend(sorted(base.rglob("client-1.20.1*.jar")))
    seen: set[str] = set()
    for jar in jars:
        key = str(jar.resolve())
        if key in seen:
            continue
        seen.add(key)
        if "sources" in jar.name.lower():
            continue
        try:
            with zipfile.ZipFile(jar) as z:
                tex = f"assets/minecraft/textures/entity/cat/{name}.png"
                if tex not in z.namelist():
                    continue
                im = Image.open(io.BytesIO(z.read(tex))).convert("RGBA")
                if im.size == (64, 32):
                    return im
        except Exception:
            continue
    return None


def make_boss_cat_uv(name: str) -> Image.Image:
    """首領貼圖 — 原版貓 UV 底圖 + 黑泥主題覆蓋."""
    base = load_vanilla_cat("black") or load_vanilla_cat("all_black")
    if base is None:
        im = make_cat(name, body=(22, 20, 28), belly=(32, 28, 36), dark=(12, 10, 16), eye=(200, 60, 80))
    else:
        im = base.copy()

    themes = {
        "grief_amalgam": ((25, 15, 35), (180, 50, 70)),
        "blind_water_lord": ((8, 10, 18), (60, 100, 160)),
        "fallen_velvet": ((190, 185, 180), (40, 35, 38)),
        "primal_chaos": ((12, 8, 10), (200, 40, 30)),
        "howling_squall": ((70, 80, 100), (150, 200, 255)),
        "ashura_phantom": ((30, 28, 32), (90, 85, 95)),
        "shadow_claw": ((15, 10, 20), (120, 40, 160)),
        "thousand_face_stitcher": ((40, 35, 45), (200, 180, 160)),
        "first_cry_warden": ((50, 45, 55), (180, 160, 140)),
        "fallen_general": ((45, 40, 50), (200, 170, 80)),
        "gear_overlord": ((55, 50, 58), (200, 160, 60)),
        "moon_alley_wraith": ((25, 30, 55), (180, 200, 255)),
        "plaza_sentinel": ((60, 58, 65), (220, 200, 120)),
        "generic": ((28, 24, 36), (180, 40, 220)),
    }
    body, accent = themes.get(name, themes["generic"])
    d = ImageDraw.Draw(im)

    for y in range(32):
        for x in range(64):
            p = im.getpixel((x, y))
            if p[3] == 0:
                continue
            if 18 <= x <= 34 and 7 <= y <= 16:
                im.putpixel((x, y), (*noise(x, y, body, 8), 255))
            if x >= 30 and 7 <= y <= 16:
                im.putpixel((x, y), (*noise(x, y, shade(body, 0.55), 6), 255))

    d.point((6, 5), fill=(*accent, 255))
    d.point((8, 5), fill=(*accent, 255))
    d.point((43, 5), fill=(*accent, 255))

    if name == "grief_amalgam":
        patch_rect(im, 20, 9, 32, 13, (180, 60, 80, 200))
    elif name == "blind_water_lord":
        patch_rect(im, 18, 10, 34, 15, (5, 8, 18, 220))
    elif name == "primal_chaos":
        patch_rect(im, 22, 8, 30, 14, (*accent, 255))
        d.line([(24, 11), (28, 12)], fill=(255, 80, 60, 255))
    elif name == "thousand_face_stitcher":
        for px in range(20, 32, 3):
            d.point((px, 10), fill=(200, 180, 160, 255))
    elif name == "gear_overlord":
        patch_rect(im, 26, 8, 32, 12, (200, 160, 60, 255))
    elif name == "shadow_claw":
        customize_shadow_claw(im)

    return im


def main():
    print("NPC cat textures (64x32 ocelot):")
    base_args = dict(
        body=(160, 130, 100), belly=(190, 160, 130),
        dark=(100, 75, 55), eye=(60, 180, 60), accent=None,
    )
    cats = {
        "coco": customize_coco,
        "jenna": customize_jenna,
        "sanhua": customize_sanhua,
        "cheshire": customize_cheshire,
        "white_glove": customize_white_glove,
        "blackjack": customize_blackjack,
        "alpha": customize_alpha,
        "mimic_cat": customize_mimic,
        "samurai": customize_samurai,
        "big_orange": customize_big_orange,
        "court_lady": customize_court_lady,
        "monk": customize_monk,
        "general": customize_general,
        "ironpaw": customize_ironpaw,
        "shadow_claw": customize_shadow_claw,
        "fur_ball": lambda im: fill_ellipse(im, 32, 16, 14, 10, (250, 245, 240)),
        "velvet_moth": lambda im: patch_rect(im, 10, 8, 54, 20, (140, 80, 180)),
        "qin_kemu": customize_samurai,
        "a_fang": customize_sanhua,
        "li_jiang": customize_court_lady,
    }
    vanilla_base = {
        "coco": "all_black",
        "jenna": "calico",
        "cheshire": "red",
        "white_glove": "white",
        "samurai": "british_shorthair",
        "big_orange": "red",
        "monk": "jellie",
        "general": "ragdoll",
        "sanhua": "tabby",
        "shadow_claw": "black",
        "mimic_cat": "siamese",
        "court_lady": "calico",
        "blackjack": "black",
        "ironpaw": "british_shorthair",
        "alpha": "british_shorthair",
        "qin_kemu": "british_shorthair",
        "a_fang": "tabby",
        "li_jiang": "calico",
    }
    for name, fn in cats.items():
        base_name = vanilla_base.get(name)
        base_im = load_vanilla_cat(base_name) if base_name else None
        im = base_im.copy() if base_im is not None else make_cat(name, **base_args)
        fn(im)
        save_img(CAT_OUT / f"{name}.png", im, force=True)

    print("Black mud slime textures:")
    for kind in ["heat_leech", "forgotten_wisp", "whispering_doll", "memory_moth", "mimic_cat", "generic"]:
        save_img(MUD_OUT / f"{kind}.png", draw_slime_variant(kind))
    for kind in ["glitch_cat", "origami_crow"]:
        save_img(MUD_OUT / f"{kind}.png", draw_slime_variant(kind), force=True)

    print("Boss textures:")
    bosses = [
        "grief_amalgam", "blind_water_lord", "fallen_velvet", "primal_chaos",
        "howling_squall", "ashura_phantom", "shadow_claw", "thousand_face_stitcher",
        "first_cry_warden", "fallen_general", "gear_overlord", "moon_alley_wraith",
        "plaza_sentinel", "generic",
    ]
    for b in bosses:
        save_img(BOSS_OUT / f"{b}.png", make_boss_cat_uv(b), force=True)

    print("Done.")


if __name__ == "__main__":
    main()
