#!/usr/bin/env python3
"""Slice Gemini sprite sheets into Minecraft textures (16/32/64) with smart cropping."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

from PIL import Image

from texture_fit import fit_square, trim_grid_bleed

ASSETS_DIR = Path(
    r"C:\Users\ASUS\.cursor\projects\c-Users-ASUS-Desktop-Cat-Country-Forge\assets"
)
MOD_ROOT = Path(__file__).resolve().parents[1]
TEXTURES = MOD_ROOT / "src/main/resources/assets/cocojenna/textures"

FOLDERS = {
    "item": TEXTURES / "item",
    "block": TEXTURES / "block",
    "gui": TEXTURES / "gui",
    "gui_portraits": TEXTURES / "gui/portraits",
    "gui_cards": TEXTURES / "gui/cards",
    "gui_tabs": TEXTURES / "gui/tabs",
    "gui_skills": TEXTURES / "gui/skills",
    "gui_regions": TEXTURES / "gui/regions",
    "entity_npc": TEXTURES / "entity/npc",
    "entity_blackmud": TEXTURES / "entity/blackmud",
}


def reference_image_dirs() -> list[Path]:
    """《貓之國相關圖片》Downloads 資料夾優先，其次 Cursor assets。"""
    dirs: list[Path] = []
    downloads = Path(r"C:\Users\ASUS\Downloads")
    if downloads.is_dir():
        for d in downloads.iterdir():
            if d.is_dir() and any(d.glob("Gemini_Generated_Image_*.png")):
                dirs.append(d)
    if ASSETS_DIR.is_dir():
        dirs.append(ASSETS_DIR)
    return dirs


@dataclass
class Cell:
    name: str
    folder: str
    size: int = 32
    skip: bool = False


@dataclass
class Sheet:
    key: str
    cols: int
    rows: int
    top: float
    bottom: float
    left: float
    right: float
    cells: list[Cell]
    cell_pad_x: float = 0.05
    cell_pad_y: float = 0.07
    cell_pad_top: float | None = None
    cell_pad_bottom: float | None = None


def find_sheet(key: str) -> Path:
    seen: set[str] = set()
    for base in reference_image_dirs():
        for p in sorted(base.glob("Gemini_Generated_Image_*.png")):
            if key not in p.name:
                continue
            data = p.read_bytes()
            h = str(hash(data))
            if h in seen:
                continue
            seen.add(h)
            return p
    raise FileNotFoundError(f"Sheet not found: {key}")


def is_background(r: int, g: int, b: int, a: int) -> bool:
    if a < 12:
        return True
    if r > 238 and g > 238 and b > 238:
        return True
    if abs(r - g) < 22 and abs(g - b) < 22:
        if 145 < r < 242:
            return True
    if r < 28 and g < 28 and b < 28:
        return True
    # blue / purple checker grids
    if b > r + 18 and b > g + 8 and 160 < b < 245:
        return True
    if r > 200 and b > 200 and g < 190:
        return True
    return False


def _matches_bg(r: int, g: int, b: int, bg: tuple[int, int, int], tol: int = 28) -> bool:
    return abs(r - bg[0]) <= tol and abs(g - bg[1]) <= tol and abs(b - bg[2]) <= tol


def remove_background(img: Image.Image) -> Image.Image:
    """Flood-fill from corners using sampled sheet background colors."""
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    if w < 2 or h < 2:
        return img

    corners = [(0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)]
    bg_samples: list[tuple[int, int, int]] = []
    for x, y in corners:
        r, g, b, a = px[x, y]
        if a > 0:
            bg_samples.append((r, g, b))
    if not bg_samples:
        return img

    visited = [[False] * w for _ in range(h)]
    stack: list[tuple[int, int]] = list(corners)

    while stack:
        x, y = stack.pop()
        if x < 0 or y < 0 or x >= w or y >= h or visited[y][x]:
            continue
        visited[y][x] = True
        r, g, b, a = px[x, y]
        if a < 12:
            px[x, y] = (0, 0, 0, 0)
            for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
                stack.append((nx, ny))
            continue
        # Only flood-fill corner-sampled sheet background; do not erase dark weapon pixels.
        if not any(_matches_bg(r, g, b, bg) for bg in bg_samples):
            continue
        px[x, y] = (0, 0, 0, 0)
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            stack.append((nx, ny))

    return img


def trim_and_resize(img: Image.Image, size: int) -> Image.Image:
    return fit_square(img, size)


def slice_cell(
    sheet: Image.Image,
    col: int,
    row: int,
    cfg: Sheet,
) -> Image.Image:
    w, h = sheet.size
    x0 = int(w * cfg.left)
    x1 = int(w * (1 - cfg.right))
    y0 = int(h * cfg.top)
    y1 = int(h * (1 - cfg.bottom))
    gw = x1 - x0
    gh = y1 - y0
    cw = gw // cfg.cols
    ch = gh // cfg.rows
    cx = x0 + col * cw
    cy = y0 + row * ch
    pad_x = int(cw * cfg.cell_pad_x)
    pad_top = int(ch * (cfg.cell_pad_top if cfg.cell_pad_top is not None else cfg.cell_pad_y))
    pad_bottom = int(ch * (cfg.cell_pad_bottom if cfg.cell_pad_bottom is not None else cfg.cell_pad_y))
    return sheet.crop((cx + pad_x, cy + pad_top, cx + cw - pad_x, cy + ch - pad_bottom))


def out_path(name: str, folder: str) -> Path:
    dest_dir = FOLDERS[folder]
    dest_dir.mkdir(parents=True, exist_ok=True)
    return dest_dir / f"{name}.png"


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
            if cell_cfg.skip:
                continue
            cell = trim_grid_bleed(slice_cell(sheet, col, row, cfg))
            cell = remove_background(cell)
            cell = trim_and_resize(cell, cell_cfg.size)
            dest = out_path(cell_cfg.name, cell_cfg.folder)
            cell.save(dest)
            saved.append(f"{cell_cfg.folder}/{cell_cfg.name}.png ({cell_cfg.size})")
    return saved


def c(name: str, folder: str = "item", size: int = 32, skip: bool = False) -> Cell:
    return Cell(name, folder, size, skip)


SHEETS: list[Sheet] = [
    # --- 18 ryokatana (3x6) — file: dasqo1 ---
    Sheet(
        "dasqo1dasqo1dasq",
        6,
        3,
        0.10,
        0.10,
        0.01,
        0.01,
        [
            c("ryokatana_stardust_tread", size=64),
            c("ryokatana_velvet_cradle", size=64),
            c("ryokatana_iron_claw_apprentice", size=64),
            c("ryokatana_calico_warmth", size=64),
            c("ryokatana_cheshire_grin", size=64),
            c("ryokatana_white_glove_guide", size=64),
            c("ryokatana_alpha_observe", size=64),
            c("ryokatana_coco_guardian", size=64),
            c("ryokatana_velvet_warmth", size=64),
            c("ryokatana_moonlight_clear", size=64),
            c("ryokatana_first_cry_beginner", size=64),
            c("ryokatana_stardust_step", size=64),
            c("ryokatana_iron_rust_legion", size=64),
            c("ryokatana_paper_crow_ink", size=64),
            c("ryokatana_blind_water_core", size=64),
            c("ryokatana_deep_sea_current", size=64),
            c("ryokatana_royal_glory", size=64),
            c("ryokatana_sanhua_thread", size=64),
        ],
        cell_pad_x=0.04,
        cell_pad_top=0.05,
        cell_pad_bottom=0.14,
    ),
    # --- missing cloaks (2x4, pick front views) — file: 9ayips ---
    Sheet(
        "9ayips9ayips9ayi",
        4,
        2,
        0.12,
        0.08,
        0.02,
        0.02,
        [
            c("cloak_traveler", size=32),
            c("cloak_thunder", size=32),
            c("cloak_anti_corrosion", size=32),
            c("cloak_moonlight", size=32),
            c("cloak_memory", size=32),
            c("cloak_guardian", size=32),
            c("cloak_hibiscus", size=32),
            c("cloak_purr", size=32),
        ],
        cell_pad_x=0.08,
        cell_pad_top=0.10,
        cell_pad_bottom=0.22,
    ),
    # --- musou 1x6 (horizontal) — file: b1wta5 ---
    Sheet(
        "b1wta5b1wta5b1wt",
        6,
        1,
        0.14,
        0.14,
        0.02,
        0.02,
        [
            c("musou_salmon_king", size=64),
            c("musou_night_verdict", size=64),
            c("musou_toy_hammer", size=32),
            c("musou_hibiscus_fall", size=64),
            c("musou_abyss_depth", size=32),
            c("musou_mad_card", size=32),
        ],
        cell_pad_x=0.06,
        cell_pad_top=0.12,
        cell_pad_bottom=0.18,
    ),
    # --- legendary daikatana 3x7 — file: vnmtul ---
    Sheet(
        "vnmtulvnmtulvnmt",
        7,
        3,
        0.08,
        0.06,
        0.01,
        0.01,
        [
            c("daikatana_tiger_iron", size=64),
            c("daikatana_wind_cut", size=64),
            c("daikatana_phantom", size=64),
            c("daikatana_suppression", size=64),
            c("daikatana_shockwave", size=64),
            c("daikatana_crescent", size=64),
            c("daikatana_star_map", size=64),
            c("daikatana_silent_guard", size=64),
            c("daikatana_village_soul", size=64),
            c("daikatana_abyss", size=64),
            c("daikatana_neon_dance", size=64),  # grief amalgam art
            c("daikatana_dusk_end", size=64),
            c("daikatana_first_dawn", size=64),
            c("daikatana_howling_gorge", size=64),
            c("daikatana_gear_king", size=64),
            c("daikatana_white_glove_contract", size=64),
            c("daikatana_hibiscus_ultimate", size=64),
            Cell("", "item", skip=True),  # stardust step (ryokatana only)
            c("daikatana_forgotten_tower", size=64),
            c("daikatana_shadow_claw_imitation", size=64),
            c("daikatana_royal_authority", size=64),
        ],
        cell_pad_x=0.06,
        cell_pad_top=0.08,
        cell_pad_bottom=0.20,
    ),
    # --- VN portraits 3x3 — file: 684ym0 ---
    Sheet(
        "684ym0684ym0684y",
        3,
        3,
        0.17,
        0.06,
        0.06,
        0.02,
        [
            c("portrait_ironpaw", "gui_portraits", 128),
            c("portrait_calico", "gui_portraits", 128),
            c("portrait_cheshire", "gui_portraits", 128),
            c("portrait_white_glove", "gui_portraits", 128),
            c("portrait_blackjack", "gui_portraits", 128),
            c("portrait_alpha", "gui_portraits", 128),
            c("portrait_squall", "gui_portraits", 128),
            c("portrait_ashura", "gui_portraits", 128),
            c("portrait_orange", "gui_portraits", 128),
        ],
        cell_pad_x=0.10,
        cell_pad_top=0.06,
        cell_pad_bottom=0.26,
    ),
    # --- promotion cards 2x3 — file: 85mky6 ---
    Sheet(
        "85mky685mky685mk",
        3,
        2,
        0.14,
        0.10,
        0.03,
        0.03,
        [
            c("card_back_common", "gui_cards", 64),
            c("card_back_rare", "gui_cards", 64),
            c("card_back_legend", "gui_cards", 64),
            c("card_front_resonance", "gui_cards", 64),
            c("card_front_shadow", "gui_cards", 64),
            c("card_front_chaos", "gui_cards", 64),
        ],
        cell_pad_x=0.10,
        cell_pad_top=0.10,
        cell_pad_bottom=0.22,
    ),
    # --- world blocks & structures 4x4 — file: 3mm1ju ---
    Sheet(
        "3mm1ju3mm1ju3mm1",
        4,
        4,
        0.10,
        0.05,
        0.02,
        0.02,
        [
            c("black_mud", "block", 32),
            c("hibiscus_flower", "block", 32),
            c("catnip", "block", 32),
            c("neon_mushroom", "block", 32),
            c("moonstone_cluster", "block", 32),
            c("salt_crystal", "block", 32),
            c("spore_fruit_node", "block", 32),
            c("full_moon_altar", "block", 32),
            c("seal_pedestal", "block", 32),
            c("socketing_table", "block", 32),
            c("ryokatana_shop_stand", "block", 32),
            Cell("", "block", skip=True),
            c("altar_foundation", "block", 32),
            c("cat_kingdom_portal", "block", 32),
        ],
        cell_pad_x=0.08,
        cell_pad_top=0.08,
        cell_pad_bottom=0.24,
    ),
    # --- misc items 8x4 — file: l5m1ju ---
    Sheet(
        "l5m1jul5m1jul5m1",
        8,
        4,
        0.10,
        0.06,
        0.10,
        0.02,
        [
            c("memory_shard", size=32),
            Cell("", "item", skip=True),
            c("coco_memory_shard", size=32),
            c("glass_vial", size=32),
            c("feather_wand", size=32),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            c("grooming_brush", size=32),
            c("golden_abacus_bead", size=32),
            c("ironpaw_charm", size=32),
            c("deep_sea_fish", size=32),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            c("giant_green_fish", size=32),
            c("crab_meat", size=32),
            c("catnip_item", size=32),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            c("moonstone", size=32),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
        ],
        cell_pad_x=0.06,
        cell_pad_top=0.08,
        cell_pad_bottom=0.22,
    ),
    # --- consumables 5x2 ---
    Sheet(
        "igjvu2igjvu2igjv",
        5,
        2,
        0.11,
        0.12,
        0.02,
        0.02,
        [
            c("premium_fish_can", size=32),
            c("basic_fish_puree", size=32),
            c("glow_fish_soup", size=32),
            c("crab_deluxe", size=32),
            c("hibiscus_sashimi", size=32),
            c("silvervine_biscuit", size=32),
            c("holy_water", size=32),
            c("hibiscus_tear", size=32),
            c("nine_lives_catnip", size=32),
            c("deep_sea_risotto", size=32),
        ],
    ),
    # --- weapons / ryokatana 5x3 ---
    Sheet(
        "ofmb8gofmb8gofmb",
        5,
        3,
        0.11,
        0.07,
        0.02,
        0.02,
        [
            c("ryokatana_lament_split", size=32),
            c("ryokatana_iron_rust_armor_break", size=32),
            c("ryokatana_blind_water_abyss", size=32),
            c("ryokatana_neon_flash", size=32),
            c("ryokatana_velvet_whisper", size=32),
            c("tarot_deck", size=32),
            c("ryokatana_origami_cut", size=32),
            c("rainbow_yarn_ball", size=32),
            c("ryokatana_screen_noise", size=32),
            c("ryokatana_moonlight_ripple", size=32),
            c("ryokatana_gear_precision_2", size=32),
            c("ryokatana_gear_schedule", size=32),
            c("ryokatana_gear_windup", size=32),
            c("ryokatana_fish_bone_tide", size=32),
            c("ryokatana_milk_tea_play", size=32),
        ],
    ),
    # --- armor & accessories 3x5 ---
    Sheet(
        "cbo4bxcbo4bxcbo4",
        5,
        3,
        0.11,
        0.07,
        0.02,
        0.02,
        [
            c("cloak_anti_corrosion", size=32),
            c("cloak_moonlight", size=32),
            c("cloak_memory", size=32),
            c("cloak_guardian", size=32),
            c("cloak_warm", size=32),
            c("moonlight_collar", size=32),
            c("jennas_old_bell", size=32),
            c("stardust_ring", size=32),
            c("blackjack_chip", size=32),
            c("purr_coin", size=32),
            c("velvet_tail_cape", size=32),
            c("blind_water_gel", size=32),
            c("spore_powder", size=32),
        ],
    ),
    # --- utility blocks/items 2x4 ---
    Sheet(
        "1w6pa51w6pa51w6p",
        4,
        2,
        0.11,
        0.10,
        0.02,
        0.02,
        [
            c("memory_book", size=32),
            c("paw_stamp", size=32),
            c("distiller", "block", 32),
            c("cat_bed", "block", 32),
            c("food_bowl", "block", 32),
            c("scratching_post", "block", 32),
            c("aroma_distiller", "block", 32),
            c("ironpaw_forge", "block", 64),
        ],
    ),
    # --- plants & seeds 3x3 ---
    Sheet(
        "g550e1g550e1g550",
        3,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            c("hibiscus_flower_item", size=16),
            c("neon_mushroom_item", size=16),
            c("full_moon_spectrum", size=16),
            c("dandelion_fluff", size=16),
            c("catnip_item", size=16),
            c("stardust_soil_item", size=16),
            c("spore_fruit", size=16),
        ],
    ),
    # --- spawn eggs 3x3 ---
    Sheet(
        "zcc175zcc175zcc1",
        3,
        3,
        0.12,
        0.08,
        0.03,
        0.03,
        [
            c("heat_leech_spawn_egg", size=16),
            c("forgotten_wisp_spawn_egg", size=16),
            c("whispering_doll_spawn_egg", size=16),
            c("memory_moth_spawn_egg", size=16),
            c("mimic_cat_spawn_egg", size=16),
            c("grief_amalgam_spawn_egg", size=16),
            c("blind_water_lord_spawn_egg", size=16),
            c("fallen_velvet_spawn_egg", size=16),
            c("primal_chaos_spawn_egg", size=16),
        ],
        cell_pad_x=0.12,
        cell_pad_y=0.16,
    ),
    # --- building blocks 3x3 ---
    Sheet(
        "xggq9mxggq9mxggq",
        3,
        3,
        0.11,
        0.08,
        0.02,
        0.02,
        [
            c("velvet_grass", "block", 16),
            c("moonstone_brick", "block", 16),
            c("stardust_soil", "block", 16),
            c("scratching_post", "block", 32),
            c("purr_crystal_block", "block", 32),
            c("schrodingers_box", size=32),
            c("velvet_block", "block", 16),
            c("blind_water_sample", size=32),
        ],
    ),
    # --- horizontal 5 starter weapons ---
    Sheet(
        "7nh6gs7nh6gs7nh6",
        5,
        1,
        0.14,
        0.14,
        0.02,
        0.02,
        [
            c("fish_bone_blade", size=32),
            c("yarn_ball_staff", size=32),
            c("pawprint_dagger", size=32),
            c("cat_bell_offhand", size=32),
            c("silvervine_bomb", size=32),
        ],
        cell_pad_x=0.08,
        cell_pad_y=0.12,
    ),
    # --- remaining ryokatana 4x4 (14 weapons) ---
    Sheet(
        "6mg4qf6mg4qf6mg4",
        4,
        4,
        0.08,
        0.05,
        0.02,
        0.02,
        [
            c("ryokatana_copper_bell_soul", size=32),
            c("ryokatana_jellyfish_bind", size=32),
            c("ryokatana_moth_scale", size=32),
            c("ryokatana_fallen_velvet_claw", size=32),
            c("ryokatana_whisper_mud", size=32),
            c("ryokatana_memory_worm", size=32),
            c("ryokatana_mimic_disguise", size=32),
            Cell("", "item", skip=True),  # chop_dagger (no registry id)
            c("ryokatana_bronze_guard", size=32),
            c("ryokatana_silvervine_drunk", size=32),
            c("ryokatana_moonlight_glimmer", size=32),
            c("ryokatana_first_cry_memory", size=32),
            c("ryokatana_blind_water_stealth", size=32),
            c("ryokatana_forgotten_page", size=32),
        ],
        cell_pad_x=0.08,
        cell_pad_y=0.14,
    ),
    # --- black mud mob art 3x3 — file: xoao53 ---
    Sheet(
        "xoao53xoao53xoao",
        3,
        3,
        0.10,
        0.06,
        0.02,
        0.28,
        [
            c("mud_heat_leech", "entity_blackmud", 128),
            c("mud_forgotten_wisp", "entity_blackmud", 128),
            c("mud_whispering_doll", "entity_blackmud", 128),
            c("mud_memory_moth", "entity_blackmud", 128),
            c("mud_mimic_cat", "entity_blackmud", 128),
            c("mud_grief_amalgam", "entity_blackmud", 128),
            c("mud_blind_water_lord", "entity_blackmud", 128),
            c("mud_fallen_velvet", "entity_blackmud", 128),
            c("mud_primal_chaos", "entity_blackmud", 128),
        ],
        cell_pad_x=0.04,
        cell_pad_top=0.05,
        cell_pad_bottom=0.12,
    ),
    # --- 黑泥生怪蛋 HQ 3x3 — ohesjaohes ---
    Sheet(
        "ohesjaohesjaohes",
        3,
        3,
        0.05,
        0.08,
        0.04,
        0.04,
        [
            c("heat_leech_spawn_egg", size=32),
            c("forgotten_wisp_spawn_egg", size=32),
            c("whispering_doll_spawn_egg", size=32),
            c("memory_moth_spawn_egg", size=32),
            c("mimic_cat_spawn_egg", size=32),
            c("grief_amalgam_spawn_egg", size=32),
            c("blind_water_lord_spawn_egg", size=32),
            c("fallen_velvet_spawn_egg", size=32),
            c("primal_chaos_spawn_egg", size=32),
        ],
        cell_pad_x=0.10,
        cell_pad_top=0.20,
        cell_pad_bottom=0.06,
    ),
    # --- NPC 生怪蛋 + 種子 6x3 — oqs4zjoqs4 ---
    Sheet(
        "oqs4zjoqs4zjoqs4",
        6,
        3,
        0.07,
        0.10,
        0.02,
        0.02,
        [
            c("coco_spawn_egg", size=32),
            c("jenna_spawn_egg", size=32),
            c("cheshire_spawn_egg", size=32),
            c("white_glove_spawn_egg", size=32),
            Cell("", "item", skip=True),   # ref: alpha 爪印（無對應物品）
            Cell("", "item", skip=True),   # ref: alpha 綠紋（無對應物品）
            c("alpha_spawn_egg", size=32),
            c("samurai_cat_spawn_egg", size=32),
            c("sumo_cat_spawn_egg", size=32),
            c("monk_cat_spawn_egg", size=32),
            Cell("", "item", skip=True),
            Cell("", "item", skip=True),
            c("general_cat_spawn_egg", size=32),
            c("daikatana_storm_umbrella", size=32),
            c("catnip_item", size=32),
            c("hibiscus_flower_item", size=32),
            c("neon_mushroom_item", size=32),
            c("stardust_soil_item", size=32),
        ],
        cell_pad_x=0.08,
        cell_pad_top=0.10,
        cell_pad_bottom=0.24,
    ),
    # --- UI 元件 5x2 — gdjn3ogdjn3 ---
    Sheet(
        "gdjn3ogdjn3ogdjn",
        5,
        2,
        0.10,
        0.14,
        0.02,
        0.02,
        [
            c("dialog_frame", "gui", 256),
            c("button_normal", "gui", 64),
            c("button_hover", "gui", 64),
            c("cooldown_arc", "gui", 64),
            c("sequence_badge_9", "item", 32),
            c("sequence_badge_8", "item", 32),
            c("sequence_badge_1", "item", 32),
            c("card_back_common", "gui_cards", 64),
            c("card_back_rare", "gui_cards", 64),
            c("card_back_legend", "gui_cards", 64),
        ],
        cell_pad_x=0.08,
        cell_pad_top=0.08,
        cell_pad_bottom=0.24,
    ),
    # --- 區域圖示 4x3 — uftfsbuftfsbuftf ---
    Sheet(
        "uftfsbuftfsbuftf",
        4,
        3,
        0.10,
        0.12,
        0.02,
        0.02,
        [
            c("region_first_meow", "gui_regions", 32),
            c("region_velvet_forest", "gui_regions", 32),
            c("region_moon_alley", "gui_regions", 32),
            c("region_central_plaza", "gui_regions", 32),
            c("region_gearpaw_town", "gui_regions", 32),
            c("region_sleep_cathedral", "gui_regions", 32),
            c("region_port_blind", "gui_regions", 32),
            c("region_dawn_highlands", "gui_regions", 32),
            c("region_howling_canyon", "gui_regions", 32),
            c("region_illusion_labyrinth", "gui_regions", 32),
            c("region_forgotten_tower", "gui_regions", 32),
            Cell("", "gui_regions", skip=True),
        ],
        cell_pad_x=0.10,
        cell_pad_top=0.08,
        cell_pad_bottom=0.22,
    ),
    # --- 技能圖示 6x4 — 2apf1e2apf1e2apf ---
    Sheet(
        "2apf1e2apf1e2apf",
        6,
        4,
        0.08,
        0.08,
        0.02,
        0.02,
        [
            c("skill_soundwave", "gui_skills", 32),
            c("skill_shadow_blade", "gui_skills", 32),
            c("skill_box_toss", "gui_skills", 32),
            c("skill_purr_heal", "gui_skills", 32),
            c("skill_warm_aura", "gui_skills", 32),
            c("skill_summon_kitten", "gui_skills", 32),
            c("skill_invisibility", "gui_skills", 32),
            c("skill_critical_strike", "gui_skills", 32),
            c("skill_confusion", "gui_skills", 32),
            c("skill_knockback", "gui_skills", 32),
            c("skill_teleport", "gui_skills", 32),
            c("skill_lifesteal", "gui_skills", 32),
            c("skill_barrier", "gui_skills", 32),
            c("skill_explosion", "gui_skills", 32),
            c("skill_lightning", "gui_skills", 32),
            c("skill_freeze", "gui_skills", 32),
            c("skill_poison", "gui_skills", 32),
            c("skill_haste", "gui_skills", 32),
            c("skill_magnet", "gui_skills", 32),
            c("skill_luck", "gui_skills", 32),
            c("skill_dash", "gui_skills", 32),
            c("skill_whirlwind", "gui_skills", 32),
            c("skill_sleep", "gui_skills", 32),
            c("skill_dream", "gui_skills", 32),
        ],
        cell_pad_x=0.08,
        cell_pad_top=0.10,
        cell_pad_bottom=0.22,
    ),
    # --- 無上大快刀 3x2 — uivdm8uivdm8 ---
    Sheet(
        "uivdm8uivdm8uivd",
        3,
        2,
        0.10,
        0.12,
        0.02,
        0.02,
        [
            c("musou_salmon_king", size=64),
            c("musou_night_verdict", size=64),
            c("musou_toy_hammer", size=32),
            c("musou_hibiscus_fall", size=64),
            c("musou_abyss_depth", size=32),
            c("musou_mad_card", size=32),
        ],
        cell_pad_x=0.06,
        cell_pad_top=0.12,
        cell_pad_bottom=0.20,
    ),
    # --- 卡面：呼嚕/夜瞳/混沌 — 675fli + e1yawte + t163vu ---
    Sheet(
        "675fli675fli675f",
        3,
        3,
        0.10,
        0.10,
        0.03,
        0.03,
        [
            Cell("", "gui_cards", skip=True),
            Cell("", "gui_cards", skip=True),
            c("card_front_resonance", "gui_cards", 64),  # T7_echo_shield
            Cell("", "gui_cards", skip=True),
            Cell("", "gui_cards", skip=True),
            Cell("", "gui_cards", skip=True),
            Cell("", "gui_cards", skip=True),
            Cell("", "gui_cards", skip=True),
            Cell("", "gui_cards", skip=True),
        ],
        cell_pad_x=0.12,
        cell_pad_top=0.10,
        cell_pad_bottom=0.24,
    ),
    Sheet(
        "e1yawte1yawte1ya",
        3,
        3,
        0.10,
        0.10,
        0.03,
        0.03,
        [Cell("", "gui_cards", skip=True)] * 8 + [
            c("card_front_shadow", "gui_cards", 64),  # T1_silent_executioner
        ],
        cell_pad_x=0.12,
        cell_pad_top=0.10,
        cell_pad_bottom=0.24,
    ),
    Sheet(
        "t163vut163vut163",
        3,
        3,
        0.08,
        0.08,
        0.03,
        0.03,
        [Cell("", "gui_cards", skip=True)] * 8 + [
            c("card_front_chaos", "gui_cards", 64),  # T1_probability_cloud
        ],
        cell_pad_x=0.12,
        cell_pad_top=0.08,
        cell_pad_bottom=0.20,
    ),
    # --- memory book UI tabs 3x2 — file: r4bugp ---
    Sheet(
        "r4bugpr4bugpr4bu",
        3,
        2,
        0.14,
        0.08,
        0.02,
        0.02,
        [
            c("tab_emotion", "gui_tabs", 32),
            c("tab_memory", "gui_tabs", 32),
            c("tab_settings", "gui_tabs", 32),
            c("tab_cat_kingdom", "gui_tabs", 32),
            c("tab_settings_bell", "gui_tabs", 32),
            Cell("", "gui_tabs", skip=True),
        ],
        cell_pad_x=0.10,
        cell_pad_top=0.10,
        cell_pad_bottom=0.22,
    ),
]


def main() -> None:
    total = 0
    for cfg in SHEETS:
        try:
            saved = process_sheet(cfg)
            print(f"[{cfg.key[:18]}] -> {len(saved)} files")
            for s in saved:
                print(f"  {s}")
            total += len(saved)
        except FileNotFoundError as e:
            print(f"SKIP {cfg.key}: {e}")
    print(f"\nDone: {total} textures written under {TEXTURES}")


if __name__ == "__main__":
    main()
