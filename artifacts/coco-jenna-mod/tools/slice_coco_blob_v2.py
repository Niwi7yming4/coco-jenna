#!/usr/bin/env python3
"""Blob-detect Coco/Jenna animation frames with rembg matting."""

from __future__ import annotations

from collections import deque
from io import BytesIO
from pathlib import Path

import numpy as np
from PIL import Image
from rembg import new_session, remove

ASSETS_DIR = Path(
    r"C:\Users\ASUS\.cursor\projects\c-Users-ASUS-Desktop-Cat-Country-Forge\assets"
)
MOD_ROOT = Path(__file__).resolve().parents[1]
OUT_COCO = MOD_ROOT / "src/main/resources/assets/cocojenna/textures/entity/coco"
OUT_JENNA = MOD_ROOT / "src/main/resources/assets/cocojenna/textures/entity/jenna"
SHEET_KEY = "55n04c55n04c55n0"
MIN_AREA = 180
MAX_AREA = 12000

_SESSION = None


def session():
    global _SESSION
    if _SESSION is None:
        _SESSION = new_session("u2net")
    return _SESSION


def find_sheet() -> Path:
    seen: set[str] = set()
    for p in sorted(ASSETS_DIR.glob("*.png")):
        if SHEET_KEY not in p.name:
            continue
        h = str(hash(p.read_bytes()))
        if h in seen:
            continue
        seen.add(h)
        return p
    raise FileNotFoundError(SHEET_KEY)


def matte(img: Image.Image) -> Image.Image:
    out = remove(img.convert("RGBA"), session=session())
    if isinstance(out, bytes):
        out = Image.open(BytesIO(out))
    arr = np.array(out.convert("RGBA"))
    arr[arr[:, :, 3] < 20, 3] = 0
    return Image.fromarray(arr)


def find_blobs(img: Image.Image) -> list[tuple[int, int, int, int, int]]:
    w, h = img.size
    px = img.load()
    visited = [[False] * w for _ in range(h)]
    blobs: list[tuple[int, int, int, int, int]] = []
    for sy in range(h):
        for sx in range(w):
            if visited[sy][sx] or px[sx, sy][3] < 25:
                continue
            q: deque[tuple[int, int]] = deque([(sx, sy)])
            visited[sy][sx] = True
            min_x = max_x = sx
            min_y = max_y = sy
            area = 0
            while q:
                x, y = q.popleft()
                area += 1
                min_x = min(min_x, x)
                max_x = max(max_x, x)
                min_y = min(min_y, y)
                max_y = max(max_y, y)
                for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
                    if 0 <= nx < w and 0 <= ny < h and not visited[ny][nx] and px[nx, ny][3] >= 25:
                        visited[ny][nx] = True
                        q.append((nx, ny))
            if MIN_AREA <= area <= MAX_AREA:
                blobs.append((min_x, min_y, max_x + 1, max_y + 1, area))
    return blobs


def trim_resize(cell: Image.Image, size: int) -> Image.Image:
    bbox = cell.getbbox()
    if not bbox:
        return Image.new("RGBA", (size, size), (0, 0, 0, 0))
    cropped = cell.crop(bbox)
    bw, bh = cropped.size
    side = max(bw, bh)
    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.paste(cropped, ((side - bw) // 2, (side - bh) // 2))
    return square.resize((size, size), Image.Resampling.NEAREST)


def main() -> None:
    sheet = matte(Image.open(find_sheet()))
    w, _ = sheet.size
    blobs = find_blobs(sheet)
    blobs.sort(key=lambda b: (b[1], b[0]))

    OUT_COCO.mkdir(parents=True, exist_ok=True)
    OUT_JENNA.mkdir(parents=True, exist_ok=True)

    coco_idx = jenna_idx = 0
    for min_x, min_y, max_x, max_y, _ in blobs:
        pad = 3
        cell = sheet.crop((
            max(0, min_x - pad), max(0, min_y - pad),
            min(w, max_x + pad), min(sheet.size[1], max_y + pad),
        ))
        cx = (min_x + max_x) / 2 / w
        if cx >= 0.84:
            trim_resize(cell, 64).save(OUT_COCO / "cuddle_pair.png")
            continue
        if cx < 0.56:
            folder, idx, prefix = OUT_COCO, coco_idx, "coco"
            coco_idx += 1
        else:
            folder, idx, prefix = OUT_JENNA, jenna_idx, "jenna"
            jenna_idx += 1
        trim_resize(cell, 32).save(folder / f"{prefix}_frame_{idx:02d}.png")

    import shutil
    if coco_idx:
        shutil.copy2(OUT_COCO / "coco_frame_00.png", OUT_COCO / "coco_idle.png")
    if jenna_idx:
        shutil.copy2(OUT_JENNA / "jenna_frame_00.png", OUT_JENNA / "jenna_idle.png")
    print(f"Coco: {coco_idx}, Jenna: {jenna_idx}")


if __name__ == "__main__":
    main()
