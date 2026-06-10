#!/usr/bin/env python3
"""Blob-detect cat animation frames from sheet 55n04c."""

from __future__ import annotations

from collections import deque
from pathlib import Path

from PIL import Image

ASSETS_DIR = Path(
    r"C:\Users\ASUS\.cursor\projects\c-Users-ASUS-Desktop-Cat-Country-Forge\assets"
)
MOD_ROOT = Path(__file__).resolve().parents[1]
OUT_COCO = MOD_ROOT / "src/main/resources/assets/cocojenna/textures/entity/coco"
OUT_JENNA = MOD_ROOT / "src/main/resources/assets/cocojenna/textures/entity/jenna"
SHEET_KEY = "55n04c55n04c55n0"

MIN_AREA = 180
MAX_AREA = 12000


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


def is_bg(r: int, g: int, b: int, a: int) -> bool:
    if a < 12:
        return True
    if abs(r - g) < 20 and abs(g - b) < 20 and 150 < r < 245:
        return True
    return False


def remove_sheet_bg(img: Image.Image) -> Image.Image:
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    corners = [(0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)]
    bg = [px[x, y][:3] for x, y in corners]
    stack = list(corners)
    seen = set(corners)
    while stack:
        x, y = stack.pop()
        r, g, b, a = px[x, y]
        if a < 12 or any(abs(r - br) <= 28 and abs(g - bg_) <= 28 and abs(b - bb) <= 28 for br, bg_, bb in bg):
            px[x, y] = (0, 0, 0, 0)
            for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
                if 0 <= nx < w and 0 <= ny < h and (nx, ny) not in seen:
                    seen.add((nx, ny))
                    stack.append((nx, ny))
    return img


def find_blobs(img: Image.Image) -> list[tuple[int, int, int, int, int]]:
    w, h = img.size
    px = img.load()
    visited = [[False] * w for _ in range(h)]
    blobs: list[tuple[int, int, int, int, int]] = []

    for sy in range(h):
        for sx in range(w):
            if visited[sy][sx] or px[sx, sy][3] < 20:
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
                    if 0 <= nx < w and 0 <= ny < h and not visited[ny][nx] and px[nx, ny][3] >= 20:
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
    sheet = remove_sheet_bg(Image.open(find_sheet()))
    w, _ = sheet.size
    blobs = find_blobs(sheet)
    blobs.sort(key=lambda b: (b[1], b[0]))

    OUT_COCO.mkdir(parents=True, exist_ok=True)
    OUT_JENNA.mkdir(parents=True, exist_ok=True)

    coco_idx = jenna_idx = 0
    saved: list[str] = []

    for min_x, min_y, max_x, max_y, _ in blobs:
        pad = 2
        cell = sheet.crop((max(0, min_x - pad), max(0, min_y - pad),
                           min(w, max_x + pad), min(sheet.size[1], max_y + pad)))
        cx = (min_x + max_x) / 2 / w
        if cx >= 0.84:
            name = "cuddle_pair"
            out = OUT_COCO / f"{name}.png"
            trim_resize(cell, 64).save(out)
            saved.append(str(out.relative_to(MOD_ROOT)))
            continue
        if cx < 0.56:
            folder = OUT_COCO
            idx = coco_idx
            coco_idx += 1
            prefix = "coco"
        else:
            folder = OUT_JENNA
            idx = jenna_idx
            jenna_idx += 1
            prefix = "jenna"

        frame = f"{prefix}_frame_{idx:02d}"
        trim_resize(cell, 32).save(folder / f"{frame}.png")
        saved.append(frame)

    import shutil

    if coco_idx > 0:
        shutil.copy2(OUT_COCO / "coco_frame_00.png", OUT_COCO / "coco_idle.png")
        saved.append("coco_idle")
    if jenna_idx > 0:
        shutil.copy2(OUT_JENNA / "jenna_frame_00.png", OUT_JENNA / "jenna_idle.png")
        saved.append("jenna_idle")

    print(f"Coco frames: {coco_idx}, Jenna frames: {jenna_idx}")
    for s in saved[:8]:
        print(" ", s)
    print(f"... total {len(saved)} outputs")


if __name__ == "__main__":
    main()
