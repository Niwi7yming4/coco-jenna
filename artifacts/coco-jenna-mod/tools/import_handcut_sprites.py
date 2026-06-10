#!/usr/bin/env python3
"""Import hand-cut / matted sprites from 手切圖 into cocojenna textures."""

from __future__ import annotations

import importlib.util
import re
import sys
from collections import defaultdict
from pathlib import Path

from PIL import Image

TOOLS = Path(__file__).resolve().parent
sys.path.insert(0, str(TOOLS))

from texture_fit import fit_square_hq, passthrough

spec = importlib.util.spec_from_file_location("slice_v2", TOOLS / "slice_sprites_v2.py")
v2 = importlib.util.module_from_spec(spec)
sys.modules["slice_v2"] = v2
spec.loader.exec_module(v2)

DEFAULT_HANDCUT = Path(r"C:\Users\ASUS\Downloads\貓之國相關圖片\手切圖")
DEFAULT_PRECUT = Path(
    r"C:\Users\ASUS\Downloads\貓之國相關圖片\切好未去背(小心邊框的黑色線條)"
)
ROOT = TOOLS.parent

# 使用者提供圖：僅格線切割匯入，不去背、不縮放（已符合 MC 尺寸）
SOURCE_DIRECT = "direct"
SOURCE_PRECUT = "direct"  # 別名：切好未去背
SOURCE_HANDCUT = "direct"  # 別名：手切圖
TEXTURES = ROOT / "src/main/resources/assets/cocojenna/textures"

# Sheets not in slice_sprites_v2.SHEETS but present in 手切圖
EXTRA_SHEETS: list[v2.Sheet] = [
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
    # 60gb6e = 呼嚕共鳴晉升卡 T9→T1（非主角立繪）
    v2.Sheet(
        "60gb6e60gb6e60gb", 3, 3, 0.10, 0.10, 0.03, 0.03,
        [
            v2.c("promo_t9_soothing_field", "gui_cards", 64),
            v2.c("promo_t8_guardian_resonance", "gui_cards", 64),
            v2.c("promo_t7_pawprint_mark", "gui_cards", 64),
            v2.c("promo_t6_calling_companion", "gui_cards", 64),
            v2.c("promo_t5_low_freq_quake", "gui_cards", 64),
            v2.c("promo_t4_memory_heal", "gui_cards", 64),
            v2.c("promo_t3_purr_cannon", "gui_cards", 64),
            v2.c("promo_t2_life_link", "gui_cards", 64),
            v2.c("promo_t1_kingdom_heart", "gui_cards", 64),
        ],
        cell_pad_x=0.10, cell_pad_top=0.08, cell_pad_bottom=0.22,
    ),
    v2.Sheet(
        "rekc96rekc96rekc", 3, 3, 0.08, 0.10, 0.03, 0.03,
        [
            v2.c("schrodingers_box", size=32),
            v2.c("coco_special_meal", size=32),
            v2.c("jenna_special_meal", size=32),
            v2.c("twin_star_meal", size=32),
            v2.c("purr_crystal", size=32),
            v2.c("memory_clay", size=32),
            v2.c("rainbow_yarn_ball", size=32),
            v2.c("tarot_deck", size=32),
            v2.c("blackjack_chip", size=32),
        ],
        cell_pad_x=0.10, cell_pad_top=0.10, cell_pad_bottom=0.22,
    ),
]

CELL_RE = re.compile(
    r"^Gemini_Generated_Image_(?P<key>.+?)_(?P<idx>\d+)_\d+x\d+(?:\s*\(\d+\))?\s*\.(?:png|jpg|jpeg)$",
    re.IGNORECASE,
)
FULL_SHEET_RE = re.compile(
    r"^Gemini_Generated_Image_(?P<key>[a-z0-9]+)\.(?:png|jpg|jpeg)$",
    re.IGNORECASE,
)

# Hand-cut tool row index -> flat 3×3 cell (used by most portrait/skill sheets).
IDX_3X3 = {9: 0, 11: 1, 13: 2, 23: 3, 25: 4, 27: 5, 37: 6, 39: 7, 41: 8}

# Precut idx numbers do not match grid order; keep v3/v2 grid slices for these weapon sheets.
PRECUT_SKIP_KEYS = frozenset({
    "vnmtulvnmtulvnmt",  # precut idx 與 3×7 格位不對齊，用網格切片
})

# Sheets with explicit idx→cell rules; unmatched idx values are skipped (no sequential fallback).
EXPLICIT_IDX_SHEETS = frozenset({
    "675fli675fli675f", "e1yawte1yawte1ya", "t163vut163vut163",
    "oqs4zjoqs4zjoqs4", "gdjn3ogdjn3ogdjn", "uftfsbuftfsbuftf",
    "r4bugpr4bugpr4bu", "7nh6gs7nh6gs7nh6", "uivdm8uivdm8uivd",
    "9ayips9ayips9ayi", "vnmtulvnmtulvnmt", "6mg4qf6mg4qf6mg4",
})


def normalize_key(raw: str) -> str:
    return raw.replace(" (1)", "").replace(" ", "").lower()


def find_sheet_config(key: str) -> v2.Sheet | None:
    norm = normalize_key(key)
    all_sheets = list(v2.SHEETS) + EXTRA_SHEETS
    for cfg in all_sheets:
        if norm == cfg.key.lower() or norm.startswith(cfg.key.lower()[:12]):
            return cfg
        if cfg.key.lower().startswith(norm[:12]):
            return cfg
    return None


def process_image(img: Image.Image, size: int, *, source: str = SOURCE_DIRECT) -> Image.Image:
    """等比 letterbox 至格線尺寸（保留黑框，不裁切內容）。"""
    if source == SOURCE_DIRECT and max(img.size) <= size:
        return passthrough(img)
    return fit_square_hq(img, size, margin_ratio=0.04)


def pick_best(files: list[Path]) -> Path:
    pngs = [f for f in files if f.suffix.lower() == ".png"]
    pool = pngs if pngs else files
    return max(pool, key=lambda p: p.stat().st_size)


def idx_to_cell(cfg: v2.Sheet, idx: int) -> int | None:
    if cfg.key == "675fli675fli675f":
        return 2 if idx == 13 else None
    if cfg.key == "e1yawte1yawte1ya":
        return 8 if idx == 41 else None
    if cfg.key == "t163vut163vut163":
        return 8 if idx in (41, 42) else None
    if cfg.key == "oqs4zjoqs4zjoqs4":
        # 手切索引 → v2 格位（對照參考圖 Gemini oqs4z 標籤，非流水序）
        # 僅生怪蛋格位；種子／道具改由 g550e1 圖集 + gen_handbook_item_icons
        order = {
            1: 0,    # coco
            3: 6,    # alpha
            5: 12,   # general_cat
            7: 1,    # jenna
            9: 7,    # samurai
            13: 2,   # cheshire
            15: 8,   # sumo
            19: 3,   # white_glove
        }
        return order.get(idx)
    if cfg.key == "gdjn3ogdjn3ogdjn":
        order = {9: 0, 12: 1, 14: 2, 17: 3, 22: 4, 24: 5, 27: 6, 29: 7, 7: 8, 19: 9}
        return order.get(idx)
    if cfg.key == "uftfsbuftfsbuftf":
        order = {9: 0, 11: 1, 13: 2, 23: 3, 25: 4, 27: 5, 37: 6, 39: 7, 41: 8, 51: 9, 53: 10}
        return order.get(idx)
    if cfg.key == "r4bugpr4bugpr4bu":
        # 手切 idx → 格位（對照 r4bugp 參考圖標籤，非流水序）
        order = {7: 0, 17: 1, 9: 3, 19: 2, 21: 4}
        return order.get(idx)
    if cfg.key == "7nh6gs7nh6gs7nh6":
        return (idx - 2) // 3 if idx >= 2 else None
    if cfg.key == "uivdm8uivdm8uivd":
        # 3×2 sheet order: salmon, night_verdict, toy_hammer / hibiscus_fall, abyss_depth, mad_card
        order = {7: 0, 9: 3, 17: 1, 19: 4, 27: 2, 29: 5}
        return order.get(idx)
    if cfg.key == "9ayips9ayips9ayi":
        # 2×4 披風正面（對照 9ayips 參考圖標籤）
        order = {7: 0, 9: 1, 17: 2, 19: 3, 22: 4, 24: 5, 12: 6, 14: 7}
        return order.get(idx)
    if cfg.key == "vnmtulvnmtulvnmt":
        # 3×7 傳說大太刀 — precut idx 與格位非線性（手切覆蓋用）
        order = {
            2: 0, 4: 1, 6: 2, 9: 7, 11: 3, 13: 4, 16: 5, 18: 6, 20: 16, 23: 8,
            25: 9, 27: 10, 30: 11, 32: 12, 34: 13, 37: 14, 39: 15, 41: 18,
            44: 14, 46: 19, 48: 20,
        }
        return order.get(idx)
    if cfg.key == "6mg4qf6mg4qf6mg4":
        # 4×4 涼太刀（跳過格 7 = chop_dagger）
        order = {
            11: 0, 13: 1, 15: 2, 29: 3, 31: 4, 33: 5, 35: 6,
            47: 8, 49: 9, 51: 10, 53: 11, 65: 12, 67: 13,
        }
        return order.get(idx)
    if cfg.cols == 3 and cfg.rows == 3:
        return IDX_3X3.get(idx)
    return None


def import_full_sheet(path: Path, cfg: v2.Sheet, *, source: str = SOURCE_HANDCUT) -> int:
    sheet = Image.open(path).convert("RGBA")
    saved = 0
    idx = 0
    for row in range(cfg.rows):
        for col in range(cfg.cols):
            if idx >= len(cfg.cells):
                break
            cell_cfg = cfg.cells[idx]
            idx += 1
            if cell_cfg.skip or not cell_cfg.name or cell_cfg.folder == "block":
                continue
            cell = v2.slice_cell(sheet, col, row, cfg)
            cell = process_image(cell, cell_cfg.size, source=source)
            dest = v2.out_path(cell_cfg.name, cell_cfg.folder)
            cell.save(dest)
            print(f"  {dest.relative_to(ROOT)}  <= {path.name}[{col},{row}]")
            saved += 1
    return saved


def import_handcut(handcut_dir: Path, *, source: str = SOURCE_HANDCUT) -> tuple[int, list[str]]:
    groups: dict[str, dict[int, list[Path]]] = defaultdict(lambda: defaultdict(list))
    full_sheets: list[tuple[Path, str]] = []
    skipped: list[str] = []

    for path in sorted(handcut_dir.iterdir()):
        if not path.is_file():
            continue
        m = CELL_RE.match(path.name)
        if m:
            key = m.group("key")
            idx = int(m.group("idx"))
            groups[normalize_key(key)][idx].append(path)
            continue
        fm = FULL_SHEET_RE.match(path.name)
        if fm:
            full_sheets.append((path, normalize_key(fm.group("key"))))
            continue
        if path.suffix.lower() in (".png", ".jpg", ".jpeg"):
            skipped.append(path.name)

    saved = 0
    for key, by_idx in sorted(groups.items()):
        cfg = find_sheet_config(key)
        if cfg is None:
            skipped.append(f"[no sheet] {key} ({len(by_idx)} cells)")
            continue
        if cfg.key in PRECUT_SKIP_KEYS and source == SOURCE_PRECUT:
            skipped.append(f"[grid slice only] {cfg.key} ({len(by_idx)} cells)")
            continue

        for idx, paths in sorted(by_idx.items()):
            cell_pos = idx_to_cell(cfg, idx)
            if cell_pos is not None and cell_pos < len(cfg.cells):
                cell_cfg = cfg.cells[cell_pos]
                if cell_cfg.skip or not cell_cfg.name:
                    skipped.append(f"{pick_best(paths).name}: skip cell")
                    continue
            elif cfg.key in EXPLICIT_IDX_SHEETS:
                skipped.append(f"{pick_best(paths).name}: no cell slot")
                continue
            else:
                targets = [c for c in cfg.cells if not c.skip and c.name]
                pos = sorted(by_idx.keys()).index(idx)
                if pos >= len(targets):
                    skipped.append(f"{pick_best(paths).name}: no cell slot")
                    continue
                cell_cfg = targets[pos]

            src_path = pick_best(paths)
            try:
                raw = Image.open(src_path)
                if getattr(raw, "is_animated", False):
                    raw.seek(0)
                out = process_image(raw, cell_cfg.size, source=source)
            except OSError as e:
                skipped.append(f"{src_path.name}: {e}")
                continue
            if cell_cfg.folder == "block":
                continue
            dest = v2.out_path(cell_cfg.name, cell_cfg.folder)
            out.save(dest)
            print(f"  {dest.relative_to(ROOT)}  <= {src_path.name}")
            saved += 1

    for path, key in full_sheets:
        cfg = find_sheet_config(key)
        if cfg is None:
            skipped.append(path.name)
            continue
        print(f"Full sheet {cfg.key}:")
        saved += import_full_sheet(path, cfg, source=source)

    return saved, skipped


def import_both_sources(
    precut_dir: Path | None = None,
    handcut_dir: Path | None = None,
) -> tuple[int, list[str]]:
    """先匯入切好未去背，再以手切圖覆蓋（手切優先）。"""
    total = 0
    all_skipped: list[str] = []
    precut_dir = precut_dir or DEFAULT_PRECUT
    handcut_dir = handcut_dir or DEFAULT_HANDCUT

    if precut_dir.is_dir():
        print(f"User pre-cut (direct, no matte/resize):\n  {precut_dir}\n")
        n, skip = import_handcut(precut_dir, source=SOURCE_DIRECT)
        print(f"  -> {n} pre-cut textures")
        total += n
        all_skipped.extend(skip)
    else:
        print(f"Pre-cut folder missing, skip: {precut_dir}")

    if handcut_dir.is_dir():
        print(f"\nHand-cut overrides (direct):\n  {handcut_dir}\n")
        n, skip = import_handcut(handcut_dir, source=SOURCE_DIRECT)
        print(f"  -> {n} hand-cut overrides")
        total += n
        all_skipped.extend(skip)
    else:
        print(f"Hand-cut folder missing, skip: {handcut_dir}")

    return total, all_skipped


def main() -> None:
    if len(sys.argv) > 1 and sys.argv[1] in ("--both", "-a"):
        precut = Path(sys.argv[2]) if len(sys.argv) > 2 else DEFAULT_PRECUT
        handcut = Path(sys.argv[3]) if len(sys.argv) > 3 else DEFAULT_HANDCUT
        count, skipped = import_both_sources(precut, handcut)
    else:
        handcut = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_HANDCUT
        if not handcut.is_dir():
            print(f"Missing folder: {handcut}")
            sys.exit(1)
        print(f"Importing hand-cut sprites from:\n  {handcut}\n")
        count, skipped = import_handcut(handcut, source=SOURCE_HANDCUT)

    print(f"\nImported {count} textures -> {TEXTURES}")

    if skipped:
        print(f"\nSkipped / unmatched ({len(skipped)}):")
        for s in skipped[:20]:
            print(f"  {s}")
        if len(skipped) > 20:
            print(f"  ... +{len(skipped) - 20} more")

    print("\nRunning slime/boss UV conversion...")
    spec2 = importlib.util.spec_from_file_location("import_ref", TOOLS / "import_ref_textures.py")
    ref = importlib.util.module_from_spec(spec2)
    spec2.loader.exec_module(ref)
    ref.main()


if __name__ == "__main__":
    main()
