#!/usr/bin/env python3
"""Proportional letterbox resize for Minecraft pixel-art textures (no stretch)."""

from __future__ import annotations

import math

import numpy as np
from PIL import Image


def trim_grid_bleed(img: Image.Image, min_content: int = 6, pad: int = 2) -> Image.Image:
    """Shrink a grid cell to visible sprite bounds (drops label strips / grid bleed)."""
    img = img.convert("RGBA")
    arr = np.array(img)
    alpha = arr[:, :, 3]
    rgb = arr[:, :, :3].astype(np.int16)
    lum = rgb.mean(axis=2)
    chroma = rgb.max(axis=2) - rgb.min(axis=2)

    fg = alpha > 35
    white = (rgb[:, :, 0] > 235) & (rgb[:, :, 1] > 235) & (rgb[:, :, 2] > 235)
    # Only treat light checker/grid as bleed — keep dark drop shadows
    checker = (chroma < 28) & (lum > 135) & (lum < 248)
    fg &= ~(white | checker)
    # Re-include dark pixels touching colorful sprite pixels (drop shadows)
    color_px = fg & (chroma > 30)
    if color_px.any():
        dil = color_px.copy()
        for _ in range(3):
            nxt = dil.copy()
            for y in range(1, img.height - 1):
                for x in range(1, img.width - 1):
                    if dil[y, x]:
                        nxt[y - 1 : y + 2, x - 1 : x + 2] = True
            dil = nxt
        shadow = dil & (lum < 130) & (chroma < 45) & (alpha > 20)
        fg |= shadow

    rows = fg.sum(axis=1)
    cols = fg.sum(axis=0)
    r_idx = np.where(rows >= min_content)[0]
    c_idx = np.where(cols >= min_content)[0]
    if len(r_idx) == 0 or len(c_idx) == 0:
        return img

    y0 = max(0, int(r_idx[0]) - pad)
    y1 = min(img.height, int(r_idx[-1]) + pad + 1)
    x0 = max(0, int(c_idx[0]) - pad)
    x1 = min(img.width, int(c_idx[-1]) + pad + 1)
    return img.crop((x0, y0, x1, y1))


def _pick_resample(src_w: int, src_h: int, dst_w: int, dst_h: int) -> Image.Resampling:
    scale = min(dst_w / src_w, dst_h / src_h)
    if scale >= 1.0:
        return Image.Resampling.NEAREST
    ratio = max(src_w / dst_w, src_h / dst_h)
    if abs(ratio - round(ratio)) < 0.02:
        return Image.Resampling.NEAREST
    return Image.Resampling.LANCZOS


def _integer_upscale(img: Image.Image, target_min: int) -> Image.Image:
    """Upscale pixel art by whole-number factors before final downscale."""
    w, h = img.size
    side = max(w, h)
    if side >= target_min:
        return img
    factor = max(2, math.ceil(target_min / side))
    factor = min(factor, 8)
    return img.resize((w * factor, h * factor), Image.Resampling.NEAREST)


def fit_rect(img: Image.Image, width: int, height: int, *, margin_ratio: float = 0.05) -> Image.Image:
    """Letterbox into width×height with proportional scale (never stretch)."""
    img = img.convert("RGBA")
    bbox = img.getbbox()
    if not bbox:
        return Image.new("RGBA", (width, height), (0, 0, 0, 0))

    cropped = img.crop(bbox)
    w, h = cropped.size
    side = max(w, h)
    margin = max(1, int(side * margin_ratio))
    inner = side + margin * 2

    canvas = Image.new("RGBA", (inner, inner), (0, 0, 0, 0))
    canvas.paste(cropped, (margin + (side - w) // 2, margin + (side - h) // 2))

    target_min = min(width, height)
    if inner < target_min:
        canvas = _integer_upscale(canvas, target_min)

    resample = _pick_resample(canvas.width, canvas.height, width, height)
    scaled = canvas.resize(
        (
            max(1, int(round(canvas.width * min(width / canvas.width, height / canvas.height)))),
            max(1, int(round(canvas.height * min(width / canvas.width, height / canvas.height)))),
        ),
        resample,
    )
    out = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    out.paste(scaled, ((width - scaled.width) // 2, (height - scaled.height) // 2))
    return out


def fit_square(img: Image.Image, size: int, *, margin_ratio: float = 0.05) -> Image.Image:
    return fit_rect(img, size, size, margin_ratio=margin_ratio)


def passthrough(img: Image.Image) -> Image.Image:
    """使用者原圖直出：不裁邊、不去背、不縮放（已符合 MC 格線尺寸時使用）。"""
    return img.convert("RGBA")


def fit_square_hq(img: Image.Image, size: int, *, margin_ratio: float = 0.04) -> Image.Image:
    """High-quality letterbox — for hand-cut / pre-cut assets (no matte, keep borders)."""
    img = img.convert("RGBA")
    bbox = img.getbbox()
    if not bbox:
        return Image.new("RGBA", (size, size), (0, 0, 0, 0))

    cropped = img.crop(bbox)
    w, h = cropped.size
    side = max(w, h)
    margin = max(1, int(side * margin_ratio))
    inner = side + margin * 2

    canvas = Image.new("RGBA", (inner, inner), (0, 0, 0, 0))
    canvas.paste(cropped, (margin + (side - w) // 2, margin + (side - h) // 2))

    target_min = size
    if inner < target_min:
        canvas = _integer_upscale(canvas, target_min)

    scale = min(size / canvas.width, size / canvas.height)
    nw = max(1, int(round(canvas.width * scale)))
    nh = max(1, int(round(canvas.height * scale)))
    scaled = canvas.resize((nw, nh), Image.Resampling.LANCZOS)

    out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    out.paste(scaled, ((size - nw) // 2, (size - nh) // 2))
    return out
