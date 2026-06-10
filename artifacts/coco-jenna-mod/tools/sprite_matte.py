#!/usr/bin/env python3
"""Sprite-sheet cell matting: preserve drop shadows, remove grid / uniform fills."""

from __future__ import annotations

import numpy as np
from PIL import Image


def crop_label_strip(cell: Image.Image, label_fraction: float = 0.22) -> Image.Image:
    cell = cell.convert("RGBA")
    w, h = cell.size
    return cell.crop((0, 0, w, max(12, int(h * (1.0 - label_fraction)))))


def _dilate(mask: np.ndarray, iters: int = 1) -> np.ndarray:
    out = mask
    h, w = mask.shape
    for _ in range(iters):
        nxt = out.copy()
        for dy in (-1, 0, 1):
            for dx in (-1, 0, 1):
                if dy == 0 and dx == 0:
                    continue
                sy = slice(max(0, -dy), h - max(0, dy))
                sx = slice(max(0, -dx), w - max(0, dx))
                dy2 = slice(max(0, dy), h - max(0, -dy))
                dx2 = slice(max(0, dx), w - max(0, -dx))
                nxt[dy2, dx2] |= out[sy, sx]
        out = nxt
    return out


def _local_std_fast(rgb: np.ndarray) -> np.ndarray:
    """Fast 5×5 local RGB std via summed-area style shifts."""
    h, w = rgb.shape[:2]
    acc = np.zeros((h, w), dtype=np.float32)
    cnt = 0
    for dy in range(-2, 3):
        for dx in range(-2, 3):
            sy = slice(max(0, -dy), h - max(0, dy))
            sx = slice(max(0, -dx), w - max(0, dx))
            dy2 = slice(max(0, dy), h - max(0, -dy))
            dx2 = slice(max(0, dx), w - max(0, -dx))
            acc[dy2, dx2] += rgb[sy, sx].std(axis=2)
            cnt += 1
    return acc / cnt


def _border_bg_samples(rgb: np.ndarray, alpha: np.ndarray) -> list[tuple[float, float, float]]:
    h, w = rgb.shape[:2]
    border = np.zeros((h, w), dtype=bool)
    border[0, :] = border[-1, :] = border[:, 0] = border[:, -1] = True
    pts = rgb[border & (alpha > 12)]
    if pts.size == 0:
        return []
    med = tuple(np.median(pts, axis=0))
    return [med]


def _matches_bg(r: float, g: float, b: float, lum: float, chroma: float, bg_med) -> bool:
    if r > 238 and g > 238 and b > 238:
        return True
    if chroma < 32 and 128 < lum < 252:
        return True
    if b > r + 20 and b > g + 10 and 150 < b < 248:
        return True
    if r > 195 and b > 195 and g < 185:
        return True
    if bg_med is not None:
        br, bg, bb = bg_med
        if abs(r - br) <= 36 and abs(g - bg) <= 36 and abs(b - bb) <= 36:
            return True
    return False


def _flood_from_edges(mask: np.ndarray) -> np.ndarray:
    h, w = mask.shape
    out = mask.copy()
    seen = np.zeros((h, w), dtype=bool)
    stack: list[tuple[int, int]] = []
    for x in range(w):
        stack.extend([(x, 0), (x, h - 1)])
    for y in range(h):
        stack.extend([(0, y), (w - 1, y)])
    while stack:
        x, y = stack.pop()
        if x < 0 or y < 0 or x >= w or y >= h or seen[y, x]:
            continue
        seen[y, x] = True
        if not out[y, x]:
            continue
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if 0 <= nx < w and 0 <= ny < h and not seen[ny, nx]:
                stack.append((nx, ny))
    return out


def _keep_largest_component(mask: np.ndarray, core: np.ndarray) -> np.ndarray:
    h, w = mask.shape
    labels = np.zeros((h, w), dtype=np.int32)
    cur = 0
    best_id = 0
    best_score = -1
    for sy in range(h):
        for sx in range(w):
            if not mask[sy, sx] or labels[sy, sx]:
                continue
            cur += 1
            stack = [(sx, sy)]
            size = 0
            core_hits = 0
            while stack:
                x, y = stack.pop()
                if x < 0 or y < 0 or x >= w or y >= h or labels[y, x] != 0 or not mask[y, x]:
                    continue
                labels[y, x] = cur
                size += 1
                if core[y, x]:
                    core_hits += 1
                stack.extend([(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)])
            score = core_hits * 1000 + size
            if score > best_score:
                best_score = score
                best_id = cur
    return labels == best_id


def matte_sprite_cell(cell: Image.Image, *, label_fraction: float = 0.22) -> Image.Image:
    img = crop_label_strip(cell, label_fraction=label_fraction).convert("RGBA")
    arr = np.array(img)
    h, w = arr.shape[:2]
    if h < 4 or w < 4:
        return img

    rgb = arr[:, :, :3].astype(np.float32)
    alpha = arr[:, :, 3].astype(np.float32)
    lum = rgb.mean(axis=2)
    chroma = rgb.max(axis=2) - rgb.min(axis=2)
    lstd = _local_std_fast(rgb)

    bg_samples = _border_bg_samples(rgb, alpha)
    bg_med = bg_samples[0] if bg_samples else None

    r, g, b = rgb[:, :, 0], rgb[:, :, 1], rgb[:, :, 2]
    obvious = alpha < 14
    obvious |= (r > 238) & (g > 238) & (b > 238)
    obvious |= (chroma < 32) & (lum > 128) & (lum < 252)
    obvious |= (b > r + 20) & (b > g + 10) & (b > 150) & (b < 248)
    obvious |= (r > 195) & (b > 195) & (g < 185)
    if bg_med is not None:
        br, bg, bb = bg_med
        obvious |= (np.abs(r - br) <= 36) & (np.abs(g - bg) <= 36) & (np.abs(b - bb) <= 36)

    flooded = _flood_from_edges(obvious)
    remaining = ~flooded

    core = remaining & ((chroma > 34) | (lstd > 12) | ((chroma > 20) & (lstd > 7)))
    uniform_fill = remaining & (lstd < 8) & (chroma < 40) & (lum > 35)
    core &= ~uniform_fill
    if not core.any():
        core = remaining & (lstd > 6)
    if not core.any():
        core = remaining

    shadow_zone = (lum < 120) & (chroma < 50) & remaining
    grown = _dilate(core, 2)
    for _ in range(6):
        grown = _dilate(grown, 1) & (grown | shadow_zone | core)
    fg = _keep_largest_component(grown | core, core)

    out = arr.copy()
    out[~fg, 3] = 0
    return Image.fromarray(out)


def matte_dark_subject(cell: Image.Image, label_fraction: float = 0.18) -> Image.Image:
    img = crop_label_strip(cell, label_fraction=label_fraction).convert("RGBA")
    arr = np.array(img)
    lum = arr[:, :, :3].mean(axis=2)
    chroma = arr[:, :, :3].max(axis=2) - arr[:, :, :3].min(axis=2)
    lstd = _local_std_fast(arr[:, :, :3].astype(np.float32))
    fg = ~((chroma < 24) & (lum > 140) & (lum < 248))
    fg &= ~((arr[:, :, 0] > 235) & (arr[:, :, 1] > 235) & (arr[:, :, 2] > 235))
    fg &= (lum > 16) | (lstd > 8)
    arr[~fg, 3] = 0
    return Image.fromarray(arr)


def score_matte(img: Image.Image) -> tuple[float, list[str]]:
    arr = np.array(img.convert("RGBA"))
    alpha = arr[:, :, 3]
    rgb = arr[:, :, :3][alpha > 30]
    opaque = alpha > 30
    ratio = float(opaque.mean())
    flags: list[str] = []
    if opaque.sum() < 30:
        flags.append("empty")
        return -100.0, flags
    if ratio > 0.88:
        flags.append("solid")
    elif ratio < 0.04:
        flags.append("speck")
    color_diversity = 0.0
    if rgb.size:
        chroma = rgb.max(axis=1) - rgb.min(axis=1)
        color_diversity = float((chroma > 20).mean())
    coverage_score = 1.0 - abs(ratio - 0.28) / 0.55
    score = coverage_score * 50 + color_diversity * 40 + min(opaque.sum() / 800, 1.0) * 10
    if "solid" in flags:
        score -= 60
    if "speck" in flags:
        score -= 50
    return score, flags


def matte_preserve_outline(cell: Image.Image, *, label_fraction: float = 0.18) -> Image.Image:
    """Matte checker/grid fills but keep dark outline strokes on the subject."""
    src = crop_label_strip(cell, label_fraction=label_fraction).convert("RGBA")
    base = matte_sprite_cell(src, label_fraction=0.0)
    sarr = np.array(src)
    barr = np.array(base)
    lum = sarr[:, :, :3].mean(axis=2)
    chroma = sarr[:, :, :3].max(axis=2) - sarr[:, :, :3].min(axis=2)
    fg = barr[:, :, 3] > 28
    touch = _dilate(fg, 3)
    outline = (lum < 68) & (chroma < 50) & (sarr[:, :, 3] > 16) & touch
    out = barr.copy()
    out[outline] = sarr[outline]
    out[outline, 3] = np.maximum(out[outline, 3], 220)
    return Image.fromarray(out)


def clean_alpha_soft(img: Image.Image) -> Image.Image:
    arr = np.array(img.convert("RGBA"))
    a = arr[:, :, 3].astype(np.float32)
    a[a < 10] = 0
    arr[:, :, 3] = np.clip(a, 0, 255).astype(np.uint8)
    return Image.fromarray(arr)
