#!/usr/bin/env python3
"""Generate GAL UI placeholder textures, lore illustrations, and expression portrait variants."""

from __future__ import annotations

import math
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parent.parent
GAL = ROOT / "src/main/resources/assets/cocojenna/textures/gui/gal"
LORE = ROOT / "src/main/resources/assets/cocojenna/textures/gui/lore"
PATCH = ROOT / "src/main/resources/assets/cocojenna/textures/gui/patchouli"
PORTRAITS = ROOT / "src/main/resources/assets/cocojenna/textures/gui/portraits"

CORE_CHARACTERS = [
    "portrait_coco", "portrait_jenna", "portrait_narrator", "portrait_calico",
    "portrait_gray_whisker", "portrait_qin_kemu", "portrait_moon_priest",
    "portrait_squall", "portrait_ironpaw", "portrait_cheshire", "portrait_sanhua",
    "portrait_alpha",
]
EXPRESSIONS = ["normal", "happy", "sad", "surprised"]

BACKGROUNDS = {
    "default": ((42, 32, 48), (255, 182, 197)),
    "first_cry_sunset": ((255, 140, 100), (255, 210, 180)),
    "moon_plaza": ((40, 50, 90), (180, 190, 255)),
    "black_mud": ((20, 10, 25), (60, 40, 70)),
    "mausoleum": ((50, 35, 70), (140, 120, 180)),
    "paper_box": ((120, 80, 40), (220, 190, 140)),
    "undercat": ((15, 15, 40), (80, 70, 120)),
    "overworld": ((80, 120, 80), (180, 220, 180)),
}


def gradient(w: int, h: int, top: tuple[int, int, int], bottom: tuple[int, int, int]) -> Image.Image:
    img = Image.new("RGBA", (w, h))
    draw = ImageDraw.Draw(img)
    for y in range(h):
        t = y / max(h - 1, 1)
        color = tuple(int(top[i] * (1 - t) + bottom[i] * t) for i in range(3)) + (255,)
        draw.line([(0, y), (w, y)], fill=color)
    return img


def nine_slice_dialog(size: int = 256, border: int = 24) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    fill = (255, 240, 245, 240)
    edge = (200, 150, 160, 255)
    accent = (255, 182, 193, 255)
    draw.rounded_rectangle((4, 4, size - 4, size - 4), radius=16, fill=fill, outline=edge, width=3)
    draw.rounded_rectangle((border, border, size - border, size - border), radius=8, outline=accent, width=2)
    for cx, cy in [(border // 2, border // 2), (size - border // 2, border // 2),
                   (border // 2, size - border // 2), (size - border // 2, size - border // 2)]:
        draw.ellipse((cx - 6, cy - 6, cx + 6, cy + 6), fill=accent)
    return img


def choice_panel() -> Image.Image:
    img = Image.new("RGBA", (128, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle((0, 0, 127, 31), radius=6, fill=(255, 248, 240, 230), outline=(180, 140, 130, 255), width=2)
    return img


def name_plate() -> Image.Image:
    img = Image.new("RGBA", (128, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle((0, 0, 127, 31), radius=4, fill=(255, 182, 193, 240), outline=(160, 100, 110, 255), width=2)
    return img


def make_background(name: str, colors: tuple[tuple[int, int, int], tuple[int, int, int]]) -> None:
    img = gradient(854, 480, colors[0], colors[1])
    draw = ImageDraw.Draw(img)
    if "moon" in name:
        draw.ellipse((620, 40, 780, 200), fill=(255, 255, 230, 200))
    if "sunset" in name or "first_cry" in name:
        draw.ellipse((100, 280, 400, 480), fill=(255, 200, 120, 80))
    GAL.mkdir(parents=True, exist_ok=True)
    img.save(GAL / f"bg_{name}.png")


def make_lore_thumb(key: str, hue: int) -> None:
    img = Image.new("RGBA", (128, 96), (30, 25, 35, 255))
    draw = ImageDraw.Draw(img)
    for i in range(5):
        y = 20 + i * 14
        w = 80 - i * 8
        draw.rounded_rectangle((24, y, 24 + w, y + 10), radius=3,
                               fill=(hue, 140, 180, 200))
    LORE.mkdir(parents=True, exist_ok=True)
    img.save(LORE / f"lore_{key}.png")


def make_patchouli_banner(name: str, top: tuple, bottom: tuple) -> None:
    img = gradient(854, 480, top, bottom)
    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle((40, 40, 814, 440), radius=20, outline=(255, 255, 255, 120), width=4)
    PATCH.mkdir(parents=True, exist_ok=True)
    img.save(PATCH / f"{name}.png")


def tint_expression(base_path: Path, expr: str, out_path: Path) -> None:
    if not base_path.exists():
        return
    img = Image.open(base_path).convert("RGBA")
    if expr == "normal":
        img.save(out_path)
        return
    r, g, b, a = img.split()
    gray = Image.merge("RGBA", (r, g, b, a)).convert("L")
    overlay = Image.new("RGBA", img.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    if expr == "happy":
        tint = (255, 240, 180, 40)
    elif expr == "sad":
        tint = (100, 120, 180, 55)
    else:
        tint = (255, 200, 220, 50)
    draw.rectangle((0, 0, img.width, img.height), fill=tint)
    out = Image.alpha_composite(img, overlay)
    if expr == "surprised":
        out = out.filter(ImageFilter.SHARPEN)
    out.save(out_path)


def generate_portrait_expressions() -> int:
    count = 0
    for char in CORE_CHARACTERS:
        base = PORTRAITS / f"{char}.png"
        if not base.exists() and char != "portrait_gray_whisker":
            continue
        if not base.exists():
            img = Image.new("RGBA", (128, 128), (140, 140, 150, 255))
            draw = ImageDraw.Draw(img)
            draw.ellipse((20, 20, 108, 108), fill=(180, 180, 190, 255))
            img.save(base)
        for expr in EXPRESSIONS:
            out = PORTRAITS / f"{char}_{expr}.png"
            if expr == "normal":
                continue
            tint_expression(base, expr, out)
            count += 1
    for missing in ("portrait_gray_whisker", "portrait_qin_kemu", "portrait_moon_priest", "portrait_shadow_claw"):
        path = PORTRAITS / f"{missing}.png"
        if path.exists():
            continue
        img = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)
        colors = {
            "portrait_gray_whisker": (158, 158, 158),
            "portrait_qin_kemu": (232, 200, 120),
            "portrait_moon_priest": (107, 127, 215),
            "portrait_shadow_claw": (40, 20, 50),
        }
        c = colors.get(missing, (128, 128, 128))
        draw.ellipse((16, 16, 112, 112), fill=c + (255,))
        img.save(path)
        count += 1
    return count


def make_promotion_card(name: str, top: tuple, bottom: tuple, accent: tuple) -> None:
    """晉升卡牌正面/背面占位圖."""
    cards = ROOT / "src/main/resources/assets/cocojenna/textures/gui/cards"
    cards.mkdir(parents=True, exist_ok=True)
    img = gradient(64, 96, top, bottom)
    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle((4, 4, 59, 91), radius=6, outline=accent + (255,), width=2)
    draw.ellipse((22, 28, 42, 48), fill=accent + (180,))
    img.save(cards / f"{name}.png")


def main() -> None:
    GAL.mkdir(parents=True, exist_ok=True)
    nine_slice_dialog().save(GAL / "dialog_box.png")
    choice_panel().save(GAL / "choice_panel.png")
    name_plate().save(GAL / "name_plate.png")
    for name, colors in BACKGROUNDS.items():
        make_background(name, colors)
    lore_keys = [
        "velvet_dynasty", "moon_first_king", "first_cry_elder", "first_cry_canopy_secret",
        "black_mud_omen", "catnip_festival", "forgotten_wastes_depth", "cardboard_slums",
    ]
    for i, key in enumerate(lore_keys):
        make_lore_thumb(key, 180 + i * 8)
    patchouli_banners = {
        "coco_jenna_porch_sunset": ((255, 160, 120), (255, 220, 200)),
        "coco_window_moonlight": ((40, 50, 90), (120, 140, 200)),
        "sacred_tree_overview": ((100, 160, 100), (200, 240, 180)),
        "black_mud_corruption": ((30, 15, 40), (80, 50, 90)),
        "sequence_promotion": ((255, 215, 0), (155, 89, 182)),
        "first_cry_market": ((220, 180, 140), (255, 230, 200)),
        "moon_plaza_night": ((25, 35, 80), (100, 120, 200)),
        "ironpaw_forge_glow": ((140, 100, 60), (220, 180, 100)),
        "shadow_claw_boss": ((20, 10, 30), (80, 40, 100)),
        "undercat_neon": ((10, 30, 50), (0, 220, 200)),
        "penetration_overworld": ((90, 110, 70), (180, 200, 150)),
        "four_mad_arena": ((180, 60, 40), (255, 140, 80)),
    }
    for name, (top, bottom) in patchouli_banners.items():
        make_patchouli_banner(name, top, bottom)
    make_promotion_card("card_back_common", (200, 190, 210), (160, 150, 170), (180, 170, 190))
    make_promotion_card("card_back_rare", (120, 80, 180), (80, 50, 140), (200, 160, 255))
    make_promotion_card("card_back_legend", (220, 180, 60), (160, 120, 30), (255, 230, 120))
    make_promotion_card("card_front_resonance", (255, 240, 200), (255, 200, 80), (255, 215, 0))
    make_promotion_card("card_front_shadow", (60, 30, 90), (30, 15, 50), (155, 89, 182))
    make_promotion_card("card_front_chaos", (255, 180, 220), (255, 100, 180), (255, 105, 180))
    n = generate_portrait_expressions()
    print(f"GAL textures written to {GAL}")
    print(f"Expression variants: {n}")
    print("Promotion cards + patchouli banners generated")


if __name__ == "__main__":
    main()
