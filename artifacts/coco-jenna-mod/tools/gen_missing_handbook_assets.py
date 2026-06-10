#!/usr/bin/env python3
"""補強手冊缺漏：生怪蛋、立繪、世界地圖、夜瞳技能變體切片。"""
from __future__ import annotations

import importlib.util
import sys
from pathlib import Path

from PIL import Image, ImageDraw

TOOLS = Path(__file__).resolve().parent
MOD = TOOLS.parent
TEX = MOD / "src/main/resources/assets/cocojenna/textures"

spec = importlib.util.spec_from_file_location("slice_v2", TOOLS / "slice_sprites_v2.py")
v2 = importlib.util.module_from_spec(spec)
sys.modules["slice_v2"] = v2
spec.loader.exec_module(v2)


def noise(x, y, base, amp=8):
    n = ((x * 17 + y * 31) ^ (x * y * 7)) & 0xFF
    d = (n % (amp * 2 + 1)) - amp
    return tuple(max(0, min(255, base[i] + d)) for i in range(3))


def save(path: Path, im: Image.Image) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    im.save(path)
    print(" ", path.relative_to(MOD))


def portrait_128(im: Image.Image) -> Image.Image:
    if im.size != (128, 128):
        return im.resize((128, 128), Image.Resampling.LANCZOS)
    return im


def draw_egg_base(d: ImageDraw.Draw, cx=16, cy=18, rx=11, ry=13, fill=(220, 210, 195)):
    d.ellipse([cx - rx, cy - ry, cx + rx, cy + ry], fill=(*fill, 255))


def gen_shadow_claw_egg() -> Image.Image:
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    draw_egg_base(d, fill=(35, 32, 42))
    for x in range(16, 32):
        for y in range(32):
            if (x + y) % 2 == 0 and 8 < y < 28:
                d.point((x, y), fill=(12, 8, 18, 255))
    d.line([(10, 12), (12, 14)], fill=(80, 40, 120, 255))
    d.rectangle([11, 8, 20, 11], fill=(50, 35, 70, 200))
    d.point((13, 10), fill=(180, 60, 220, 255))
    d.line([(8, 6), (10, 8)], fill=(140, 120, 80, 255))
    return im


def gen_sanhua_egg() -> Image.Image:
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    draw_egg_base(d, fill=(235, 220, 200))
    patch_rect(d, 10, 10, 18, 16, (200, 110, 50))
    patch_rect(d, 14, 12, 22, 18, (45, 38, 35))
    patch_rect(d, 12, 16, 20, 22, (240, 230, 210))
    d.line([(22, 8), (26, 20)], fill=(180, 50, 50, 255), width=1)
    d.polygon([(25, 20), (27, 20), (26, 22)], fill=(200, 200, 210, 255))
    return im


def patch_rect(d, x1, y1, x2, y2, color):
    for y in range(y1, y2):
        for x in range(x1, x2):
            d.point((x, y), fill=(*color, 255))


def gen_portrait_ironpaw() -> Image.Image:
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for y in range(64):
        for x in range(64):
            if (x - 32) ** 2 + (y - 34) ** 2 < 380:
                im.putpixel((x, y), (*noise(x, y, (72, 76, 82), 6), 255))
    for y in range(64):
        for x in range(64):
            if (x + y) % 7 == 0 and (x - 32) ** 2 + (y - 34) ** 2 < 360:
                im.putpixel((x, y), (*noise(x, y, (52, 55, 60), 4), 255))
    d.ellipse([20, 24, 44, 40], fill=(60, 64, 70, 255))
    d.point((26, 30), fill=(220, 180, 40, 255))
    d.point((38, 30), fill=(220, 180, 40, 255))
    d.line([(22, 28), (26, 31)], fill=(120, 40, 40, 255), width=2)
    d.rectangle([46, 48, 58, 58], fill=(160, 165, 175, 255))
    d.rectangle([48, 50, 56, 56], fill=(120, 125, 135, 255))
    d.ellipse([18, 14, 28, 22], fill=(68, 72, 78, 255))
    d.ellipse([36, 14, 46, 22], fill=(68, 72, 78, 255))
    return im


def gen_portrait_narrator() -> Image.Image:
    """旁白 — 月光剪影，非角色特寫."""
    im = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for y in range(128):
        for x in range(128):
            t = y / 127
            im.putpixel((x, y), (int(28 + 18 * t), int(36 + 28 * t), int(58 + 42 * t), 255))
    d.ellipse([88, 18, 112, 42], fill=(240, 235, 210, 220))
    d.ellipse([78, 70, 118, 118], fill=(18, 16, 26, 200))
    d.ellipse([52, 58, 88, 96], fill=(14, 12, 22, 255))
    d.polygon([(44, 72), (52, 58), (60, 72)], fill=(14, 12, 22, 255))
    d.polygon([(80, 72), (88, 58), (96, 72)], fill=(14, 12, 22, 255))
    for i in range(6):
        px = 20 + i * 14
        d.point((px, 100 + (i % 2)), fill=(180, 200, 255, 120))
    return im


def gen_portrait_coco() -> Image.Image:
    """可可：純黑貓、琥珀金眼、尾尖白毛."""
    im = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for y in range(128):
        for x in range(128):
            if (x - 64) ** 2 + (y - 72) ** 2 < 2500:
                im.putpixel((x, y), (*noise(x, y, (20, 20, 26), 5), 255))
    d.ellipse([36, 34, 92, 96], fill=(14, 14, 20, 255))
    d.ellipse([40, 22, 58, 42], fill=(16, 16, 22, 255))
    d.ellipse([70, 22, 88, 42], fill=(16, 16, 22, 255))
    d.point((48, 30), fill=(45, 42, 55, 255))
    d.point((80, 30), fill=(45, 42, 55, 255))
    d.point((52, 54), fill=(230, 175, 45, 255))
    d.point((76, 54), fill=(230, 175, 45, 255))
    d.point((53, 55), fill=(255, 220, 80, 255))
    d.point((77, 55), fill=(255, 220, 80, 255))
    d.arc([54, 64, 74, 78], 15, 165, fill=(55, 50, 60, 255))
    d.ellipse([96, 92, 112, 108], fill=(245, 245, 252, 255))
    d.point((100, 98), fill=(255, 255, 255, 255))
    return im


def gen_portrait_jenna() -> Image.Image:
    """珍奶：玳瑁色、檸檬黃眼、左耳缺口、瞇眼."""
    im = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for y in range(128):
        for x in range(128):
            if (x - 64) ** 2 + (y - 74) ** 2 < 2400:
                if (x + y) % 7 < 2:
                    c = noise(x, y, (210, 110, 45), 6)
                elif (x + y) % 7 < 4:
                    c = noise(x, y, (45, 38, 32), 5)
                else:
                    c = noise(x, y, (235, 225, 205), 5)
                im.putpixel((x, y), (*c, 255))
    d.ellipse([38, 36, 90, 94], fill=(200, 105, 40, 255))
    d.rectangle([38, 36, 48, 46], fill=(0, 0, 0, 0))
    d.ellipse([42, 24, 58, 40], fill=(195, 100, 38, 255))
    d.ellipse([70, 24, 86, 40], fill=(50, 42, 36, 255))
    d.arc([50, 52, 78, 62], 0, 180, fill=(70, 55, 40, 255))
    d.point((54, 56), fill=(230, 220, 70, 255))
    d.point((74, 56), fill=(230, 220, 70, 255))
    d.line([(44, 30), (50, 34)], fill=(180, 50, 50, 255))
    return im


def gen_tab_icon(kind: str) -> Image.Image:
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    if kind == "emotion":
        d.ellipse([4, 6, 27, 26], fill=(230, 120, 150, 255))
        d.ellipse([6, 8, 25, 24], fill=(255, 170, 190, 255))
        for px, py in [(11, 13), (14, 16), (17, 13), (20, 16)]:
            d.point((px, py), fill=(255, 245, 250, 255))
    elif kind == "memory":
        d.rectangle([8, 6, 24, 26], fill=(240, 230, 210, 255))
        d.rectangle([10, 8, 22, 24], fill=(255, 248, 235, 255))
        d.line([(12, 12), (20, 12)], fill=(180, 140, 200, 255))
        d.line([(12, 16), (18, 16)], fill=(140, 180, 220, 255))
        d.point((14, 20), fill=(255, 180, 200, 255))
    elif kind == "kingdom":
        d.rectangle([6, 10, 25, 24], fill=(60, 90, 60, 255))
        d.ellipse([12, 4, 20, 12], fill=(255, 220, 100, 255))
        d.point((16, 16), fill=(200, 240, 255, 255))
    else:
        d.ellipse([8, 8, 24, 24], fill=(140, 130, 160, 255))
        d.ellipse([10, 10, 22, 22], fill=(180, 170, 200, 255))
        for i in range(8):
            ang = i * 3.14159 / 4
            x1 = int(16 + 6 * __import__("math").cos(ang))
            y1 = int(16 + 6 * __import__("math").sin(ang))
            d.line([(16, 16), (x1, y1)], fill=(90, 80, 110, 255))
        d.ellipse([13, 13, 19, 19], fill=(220, 215, 235, 255))
    return im


def gen_portrait_sanhua() -> Image.Image:
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for y in range(64):
        for x in range(64):
            if (x - 32) ** 2 + (y - 36) ** 2 < 340:
                c = (200, 120, 55) if (x + y) % 5 < 2 else (50, 42, 38) if (x + y) % 5 == 2 else (235, 225, 210)
                im.putpixel((x, y), (*noise(x, y, c, 5), 255))
    d.arc([22, 28, 42, 36], 0, 180, fill=(40, 35, 30, 255))
    d.point((27, 31), fill=(180, 180, 80, 200))
    d.point((37, 31), fill=(180, 180, 80, 200))
    for px, py, c in [(14, 50, (200, 90, 50)), (22, 54, (240, 230, 210)), (30, 52, (60, 50, 45)), (38, 55, (210, 100, 55))]:
        d.rectangle([px, py, px + 6, py + 4], fill=(*c, 255))
    d.line([(48, 44), (58, 52)], fill=(200, 60, 60, 255))
    return im


def export_world_map() -> None:
    path = v2.find_sheet("9qvfj39qvfj39qvf")
    im = Image.open(path).convert("RGBA")
    w, h = im.size
    cropped = im.crop((int(w * 0.04), int(h * 0.06), int(w * 0.96), int(h * 0.94)))
    out = cropped.resize((256, 160), Image.Resampling.LANCZOS)
    save(TEX / "gui" / "world_map.png", out)
    thumb = cropped.resize((128, 80), Image.Resampling.LANCZOS)
    save(TEX / "gui" / "world_map_thumb.png", thumb)


def slice_extra_skills() -> None:
    sheets = [
        v2.Sheet(
            "f6p944f6p944f6p9", 3, 3, 0.08, 0.10, 0.03, 0.03,
            [
                v2.c("skill_shadow_tag", "gui_skills", 32),
                v2.c("skill_whisper_step", "gui_skills", 32),
                v2.c("skill_shadow_strike", "gui_skills", 32),
                v2.c("skill_night_sprint", "gui_skills", 32),
                v2.c("skill_hypno_gaze", "gui_skills", 32),
                v2.c("skill_ambush_predator", "gui_skills", 32),
                v2.c("skill_abyss_gaze", "gui_skills", 32),
                v2.c("skill_phantom_step", "gui_skills", 32),
                v2.c("skill_shadow_reaper", "gui_skills", 32),
            ],
            cell_pad_x=0.10, cell_pad_top=0.10, cell_pad_bottom=0.22,
        ),
        v2.Sheet(
            "adyte4adyte4adyt", 3, 3, 0.08, 0.10, 0.03, 0.03,
            [
                v2.c("skill_circle_chase", "gui_skills", 32),
                v2.c("skill_velvet_step", "gui_skills", 32),
                v2.c("skill_surprise_bite", "gui_skills", 32),
                v2.c("skill_dark_dash", "gui_skills", 32),
                v2.c("skill_owl_eye", "gui_skills", 32),
                v2.c("skill_shadow_pounce", "gui_skills", 32),
                v2.c("skill_portal_gaze", "gui_skills", 32),
                v2.c("skill_ghost_walk", "gui_skills", 32),
                v2.c("skill_dark_harvest", "gui_skills", 32),
            ],
            cell_pad_x=0.10, cell_pad_top=0.10, cell_pad_bottom=0.22,
        ),
    ]
    for cfg in sheets:
        saved = v2.process_sheet(cfg)
        print(f"[{cfg.key[:12]}] -> {len(saved)} skills")


def main() -> None:
    print("Programmatic spawn eggs:")
    save(TEX / "item" / "shadow_claw_spawn_egg.png", gen_shadow_claw_egg())
    save(TEX / "item" / "sanhua_weaver_spawn_egg.png", gen_sanhua_egg())

    print("NPC portraits:")
    save(TEX / "gui" / "portraits" / "portrait_ironpaw.png", portrait_128(gen_portrait_ironpaw()))
    save(TEX / "gui" / "portraits" / "portrait_sanhua.png", portrait_128(gen_portrait_sanhua()))
    coco = gen_portrait_coco()
    jenna = gen_portrait_jenna()
    save(TEX / "gui" / "portraits" / "portrait_coco.png", coco)
    save(TEX / "gui" / "portraits" / "portrait_jenna.png", jenna)
    save(TEX / "gui" / "portraits" / "portrait_orange.png", jenna)

    print("Memory book tabs:")
    for name in ("emotion", "memory", "kingdom", "settings"):
        key = "cat_kingdom" if name == "kingdom" else name
        save(TEX / "gui" / "tabs" / f"tab_{key}.png", gen_tab_icon(name))

    print("World map:")
    export_world_map()

    print("Extra night/shadow skill sheets:")
    slice_extra_skills()
    print("Done.")


if __name__ == "__main__":
    main()
