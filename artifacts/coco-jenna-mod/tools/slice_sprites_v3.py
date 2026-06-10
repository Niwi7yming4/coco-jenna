#!/usr/bin/env python3
"""Slice sprite sheets with shadow-preserving matting (v3)."""

from __future__ import annotations

import importlib.util
import sys
from pathlib import Path

import numpy as np
from PIL import Image

from sprite_matte import (
    clean_alpha_soft,
    crop_label_strip,
    matte_dark_subject,
    matte_sprite_cell,
    score_matte,
)
from texture_fit import fit_square, trim_grid_bleed

TOOLS = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("slice_v2", TOOLS / "slice_sprites_v2.py")
v2 = importlib.util.module_from_spec(spec)
sys.modules["slice_v2"] = v2
spec.loader.exec_module(v2)

Cell = v2.Cell
Sheet = v2.Sheet
FOLDERS = dict(v2.FOLDERS)
FOLDERS.setdefault("gui_skills", v2.TEXTURES / "gui/skills")
FOLDERS.setdefault("gui_regions", v2.TEXTURES / "gui/regions")
find_sheet = v2.find_sheet
slice_cell = v2.slice_cell
out_path = v2.out_path
c = v2.c


def score_rgba(img: Image.Image) -> tuple[float, list[str]]:
    return score_matte(img)


CROSS_BLOCK_NAMES = frozenset({
    "hibiscus_flower", "catnip", "neon_mushroom", "moonstone_cluster",
    "spore_fruit_node", "velvet_grass",
})

DARK_SUBJECT_NAMES = frozenset({
    "primal_chaos_spawn_egg", "blind_water_lord_spawn_egg", "grief_amalgam_spawn_egg",
    "heat_leech_spawn_egg", "coco_spawn_egg", "shadow_claw_spawn_egg",
    "neon_mushroom", "neon_mushroom_item", "mud_primal_chaos",
})

# 手切圖已去背 — 僅縮放，不做 matte/clean（避免二次去背毀邊）
RESIZE_ONLY_FOLDERS = frozenset({
    "gui_portraits", "gui_tabs", "gui_cards", "gui_skills", "gui_regions", "gui",
})


def process_cell(
    cell: Image.Image,
    size: int,
    *,
    label_fraction: float = 0.22,
    dark_subject: bool = False,
) -> Image.Image:
    candidates: list[Image.Image] = []
    candidates.append(matte_sprite_cell(cell, label_fraction=label_fraction))
    if dark_subject:
        candidates.append(matte_dark_subject(cell, label_fraction=label_fraction + 0.04))
    best = candidates[0]
    best_score = -999.0
    for img in candidates:
        sc, _ = score_matte(img)
        if sc > best_score:
            best_score = sc
            best = img

    out = clean_alpha_soft(best)
    return fit_square(out, size)


def process_sheet(cfg: Sheet) -> list[str]:
    path = find_sheet(cfg.key)
    sheet = Image.open(path).convert("RGBA")
    saved: list[str] = []
    idx = 0
    for row in range(cfg.rows):
        for col in range(cfg.cols):
            if idx >= len(cfg.cells):
                break
            cell_cfg = cfg.cells[idx]
            idx += 1
            if cell_cfg.skip or not cell_cfg.name:
                continue
            # 方塊貼圖一律由 gen_all_block_textures.py 程式生成，避免圖集切片錯位
            if cell_cfg.folder == "block":
                continue
            label_fraction = 0.28 if cell_cfg.name.endswith("_spawn_egg") else 0.22
            if cell_cfg.folder in ("gui_portraits", "gui_cards"):
                label_fraction = 0.26
            raw = trim_grid_bleed(slice_cell(sheet, col, row, cfg))
            if cell_cfg.folder in RESIZE_ONLY_FOLDERS:
                out = fit_square(raw.convert("RGBA"), cell_cfg.size)
            else:
                dark = cell_cfg.name in DARK_SUBJECT_NAMES
                out = process_cell(
                    raw,
                    cell_cfg.size,
                    label_fraction=label_fraction,
                    dark_subject=dark,
                )
            dest = out_path(cell_cfg.name, cell_cfg.folder)
            out.save(dest)
            saved.append(f"{cell_cfg.folder}/{cell_cfg.name}.png ({cell_cfg.size})")
    return saved


def extra_sheets() -> list[Sheet]:
    """圖集僅在手切/額外匯入腳本定義、但參考圖在 Downloads 內。"""
    try:
        spec = importlib.util.spec_from_file_location(
            "handcut", Path(__file__).resolve().parent / "import_handcut_sprites.py"
        )
        hc = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(hc)
        return list(hc.EXTRA_SHEETS)
    except Exception:
        return []


def patch_sheets(sheets: list[Sheet]) -> list[Sheet]:
    """Per-sheet layout fixes only — keep v2 grid where it matches source art."""
    out: list[Sheet] = []
    for cfg in list(sheets) + extra_sheets():
        if cfg.key.startswith("g550e1"):
            cells = [
                c("hibiscus_flower_item", size=32),
                c("neon_mushroom_item", size=32),
                c("full_moon_spectrum", size=32),
                c("dandelion_fluff", size=32),
                c("catnip_item", size=32),
                c("stardust_soil_item", size=32),
                c("spore_fruit", size=32),
            ]
            while len(cells) < cfg.cols * cfg.rows:
                cells.append(Cell("", "item", skip=True))
            cfg = Sheet(
                cfg.key, cfg.cols, cfg.rows, cfg.top, cfg.bottom, cfg.left, cfg.right,
                cells, cfg.cell_pad_x, cfg.cell_pad_y, cfg.cell_pad_top, cfg.cell_pad_bottom,
            )
        if cfg.key.startswith("zcc175"):
            cells = [Cell(c.name, c.folder, 32, c.skip) for c in cfg.cells]
            cfg = Sheet(
                cfg.key, cfg.cols, cfg.rows, cfg.top, cfg.bottom, cfg.left, cfg.right,
                cells, 0.08, 0.10, 0.08, 0.14,
            )
        if cfg.key.startswith("ohesjaohes"):
            cfg = Sheet(
                cfg.key, cfg.cols, cfg.rows, 0.05, 0.08, 0.04, 0.04,
                cfg.cells, 0.10, 0.16, 0.22, 0.08,
            )
        out.append(cfg)
    return out


SHEETS = patch_sheets(v2.SHEETS)


def audit_outputs() -> list[dict]:
    issues = []
    for cfg in SHEETS:
        for cell in cfg.cells:
            if cell.skip or not cell.name:
                continue
            dest = out_path(cell.name, cell.folder)
            if not dest.exists():
                issues.append({"file": str(dest.name), "flags": ["missing"]})
                continue
            _, flags = score_rgba(Image.open(dest))
            if flags:
                issues.append({"file": str(dest.relative_to(v2.TEXTURES)), "flags": flags})
    return issues


def main() -> None:
    total = 0
    for cfg in SHEETS:
        try:
            saved = process_sheet(cfg)
            print(f"[{cfg.key[:18]}] -> {len(saved)} files")
            total += len(saved)
        except FileNotFoundError as e:
            print(f"SKIP {cfg.key}: {e}")
    print(f"\nDone: {total} textures")
    bad = audit_outputs()
    print(f"Quality issues remaining: {len(bad)}")
    for item in bad[:25]:
        print(f"  {item['file']}: {','.join(item['flags'])}")
    if len(bad) > 25:
        print(f"  ... +{len(bad) - 25} more")


if __name__ == "__main__":
    main()
