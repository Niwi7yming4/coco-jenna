#!/usr/bin/env python3
"""
一鍵貼圖匯入（預設：使用者切圖直出，不去背不縮放）：
1. [可選 --auto-slice] 參考圖 v3 自動切片（舊流程，易毀邊）
2. 方塊貼圖從參考圖切片（3mm1ju / 1w6pa5 / xggq9m）
3. 《切好未去背》→ 直接匯入；《手切圖》→ 覆蓋同名格位
4. 黑泥實體 UV 轉換 + 方塊立體面
"""
from __future__ import annotations

import importlib.util
import subprocess
import sys
from pathlib import Path

TOOLS = Path(__file__).resolve().parent
ROOT = TOOLS.parent
sys.path.insert(0, str(TOOLS))

# ── v2 / v3 ─────────────────────────────────────────────────────────────
spec_v2 = importlib.util.spec_from_file_location("slice_v2", TOOLS / "slice_sprites_v2.py")
v2 = importlib.util.module_from_spec(spec_v2)
sys.modules["slice_v2"] = v2
spec_v2.loader.exec_module(v2)

spec_v3 = importlib.util.spec_from_file_location("slice_v3", TOOLS / "slice_sprites_v3.py")
v3 = importlib.util.module_from_spec(spec_v3)
sys.modules["slice_v3"] = v3
spec_v3.loader.exec_module(v3)

# 與 import_handcut_sprites 相同的額外圖集
spec_hc = importlib.util.spec_from_file_location("handcut", TOOLS / "import_handcut_sprites.py")
handcut = importlib.util.module_from_spec(spec_hc)
spec_hc.loader.exec_module(handcut)

BLOCK_SHEET_KEYS = frozenset({
    "3mm1ju3mm1ju3mm1", "1w6pa51w6pa51w6p", "xggq9mxggq9mxggq",
})

# 僅在手切腳本定義、不應由 v3 自動去背的圖集
HANDCUT_ONLY_KEYS = frozenset(cfg.key for cfg in handcut.EXTRA_SHEETS)


def all_sheet_configs() -> list[v2.Sheet]:
    seen: set[str] = set()
    out: list[v2.Sheet] = []
    for cfg in list(v3.SHEETS) + handcut.EXTRA_SHEETS:
        if cfg.key not in seen:
            seen.add(cfg.key)
            out.append(cfg)
    return out


def slice_blocks_from_reference() -> int:
    """方塊格從參考圖切片（v3 略過 block 資料夾）。"""
    from PIL import Image

    count = 0
    for cfg in all_sheet_configs():
        if cfg.key not in BLOCK_SHEET_KEYS:
            continue
        try:
            path = v2.find_sheet(cfg.key)
        except FileNotFoundError as e:
            print(f"  skip blocks {cfg.key}: {e}")
            continue
        sheet = Image.open(path).convert("RGBA")
        idx = 0
        for row in range(cfg.rows):
            for col in range(cfg.cols):
                if idx >= len(cfg.cells):
                    break
                cell_cfg = cfg.cells[idx]
                idx += 1
                if cell_cfg.skip or not cell_cfg.name or cell_cfg.folder != "block":
                    continue
                cell = v2.trim_grid_bleed(v2.slice_cell(sheet, col, row, cfg))
                cell = v2.remove_background(cell)
                cell = v2.trim_and_resize(cell, cell_cfg.size)
                dest = v2.out_path(cell_cfg.name, cell_cfg.folder)
                cell.save(dest)
                print(f"  block/{cell_cfg.name}.png")
                count += 1
    return count


def restore_narrator_portrait() -> int:
    """旁白立繪 — 程式生成剪影，避免誤用 NPC 圖或被二次去背."""
    spec = importlib.util.spec_from_file_location(
        "gen_missing", TOOLS / "gen_missing_handbook_assets.py"
    )
    gen = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(gen)
    tex = ROOT / "src/main/resources/assets/cocojenna/textures"
    gen.save(tex / "gui" / "portraits" / "portrait_narrator.png", gen.gen_portrait_narrator())
    return 1


def restore_protagonist_portraits() -> int:
    """可可／珍奶立繪：60gb6e 是晉升卡而非立繪，改回專用主角頭像。"""
    spec = importlib.util.spec_from_file_location(
        "gen_missing", TOOLS / "gen_missing_handbook_assets.py"
    )
    gen = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(gen)
    tex = ROOT / "src/main/resources/assets/cocojenna/textures"
    gen.save(tex / "gui" / "portraits" / "portrait_coco.png", gen.gen_portrait_coco())
    gen.save(tex / "gui" / "portraits" / "portrait_jenna.png", gen.gen_portrait_jenna())
    return 2


def restore_special_spawn_eggs() -> int:
    """影爪／三花子不在 oqs4z 參考格內，避免被錯格切片覆蓋。"""
    spec = importlib.util.spec_from_file_location(
        "gen_missing", TOOLS / "gen_missing_handbook_assets.py"
    )
    gen = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(gen)
    tex = ROOT / "src/main/resources/assets/cocojenna/textures"
    gen.save(tex / "item" / "shadow_claw_spawn_egg.png", gen.gen_shadow_claw_egg())
    gen.save(tex / "item" / "sanhua_weaver_spawn_egg.png", gen.gen_sanhua_egg())
    return 2


def slice_reference_v3() -> tuple[int, list[str]]:
    """所有有參考圖的圖集 → v3 切片（非方塊格）。"""
    saved = 0
    skipped: list[str] = []
    for cfg in all_sheet_configs():
        if cfg.key in HANDCUT_ONLY_KEYS:
            continue
        try:
            v2.find_sheet(cfg.key)
        except FileNotFoundError:
            skipped.append(cfg.key)
            continue
        try:
            files = v3.process_sheet(cfg)
            print(f"[v3 {cfg.key[:18]}] {len(files)} textures")
            saved += len(files)
        except FileNotFoundError as e:
            skipped.append(f"{cfg.key}: {e}")
    return saved, skipped


def main() -> None:
    ref_dir = Path(r"C:\Users\ASUS\Downloads\貓之國相關圖片")
    handcut_dir = handcut.DEFAULT_HANDCUT
    auto_slice = "--auto-slice" in sys.argv

    if auto_slice:
        print("=== 1/4 參考圖自動切片 (slice_sprites_v3，含去背) ===")
        n_v3, miss = slice_reference_v3()
        print(f"  -> {n_v3} textures")
        if miss:
            print(f"  無參考圖 ({len(miss)}): {', '.join(miss[:8])}")
            if len(miss) > 8:
                print(f"    ... +{len(miss) - 8}")
    else:
        print("=== 1/4 跳過 v3 自動去背切片（使用使用者切圖；加 --auto-slice 可啟用舊流程）===")

    print("\n=== 2/4 方塊貼圖切片 ===")
    n_blk = slice_blocks_from_reference()
    print(f"  -> {n_blk} block textures")

    precut_dir = handcut.DEFAULT_PRECUT
    print("\n=== 3/4 使用者切圖匯入（直出，不去背不縮放）===")
    print("  ① 切好未去背 — 格位對照後直接寫入 textures")
    print("  ② 手切圖 — 覆蓋同名格位")
    n_hc, hc_skip = handcut.import_both_sources(precut_dir, handcut_dir)
    print(f"  -> 共 {n_hc} 張（手切優先覆蓋）")
    if hc_skip:
        print(f"  略過 {len(hc_skip)} 檔")

    print("\n=== 3b/4 entity cat UV textures (gen_entity_textures) ===")
    gen_tex = TOOLS / "gen_entity_textures.py"
    if gen_tex.is_file():
        subprocess.check_call([sys.executable, str(gen_tex)], cwd=ROOT)

    print("\n=== 3c/4 旁白立繪（剪影，避免二次去背）===")
    n_narrator = restore_narrator_portrait()
    print(f"  -> {n_narrator} narrator portrait")

    print("\n=== 3d/4 可可／珍奶立繪（主角專用，非晉升卡）===")
    n_portraits = restore_protagonist_portraits()
    print(f"  -> {n_portraits} portrait textures")

    print("\n=== 3e/4 shadow_claw + sanhua spawn eggs (generated) ===")
    n_special = restore_special_spawn_eggs()
    print(f"  -> {n_special} spawn egg textures")

    print("\n=== 4/4 後處理 ===")
    spec_ref = importlib.util.spec_from_file_location("import_ref", TOOLS / "import_ref_textures.py")
    ref = importlib.util.module_from_spec(spec_ref)
    spec_ref.loader.exec_module(ref)
    ref.main()

    armor_tex = TOOLS / "gen_armor_textures.py"
    if armor_tex.is_file():
        print("\n=== 4b/5 防具層貼圖 + 物品圖示 (gen_armor_textures) ===")
        subprocess.check_call([sys.executable, str(armor_tex)], cwd=ROOT)

    layered = TOOLS / "gen_layered_assets.py"
    if layered.is_file():
        print("  方塊立體面 + 物品模型…")
        subprocess.check_call([sys.executable, str(layered)], cwd=ROOT)

    handbook = TOOLS / "gen_handbook_item_icons.py"
    if handbook.is_file():
        print("\n=== 5/5 材料／消耗品圖示校正 (gen_handbook_item_icons) ===")
        subprocess.check_call([sys.executable, str(handbook)], cwd=ROOT)

    optimize = TOOLS / "optimize_item_gui_textures.py"
    if optimize.is_file():
        print("\n=== 6/6 item/GUI 等比縮放 + PNG 壓縮 ===")
        subprocess.check_call([sys.executable, str(optimize)], cwd=ROOT)

    print(f"\n完成。參考圖目錄: {ref_dir}")
    print("有新增手切圖時再跑一次即可自動覆蓋對應格位。")


if __name__ == "__main__":
    main()
