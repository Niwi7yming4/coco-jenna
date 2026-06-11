# Coco-Jenna mod tools

## Build prerequisites

Before `./gradlew build` on a fresh clone, generate structure NBT assets and optimize item/GUI textures:

```bash
cd artifacts/coco-jenna-mod
python tools/gen_loot_and_ruin_nbt.py
python tools/gen_mausoleum_nbt.py
python tools/gen_biome_structure_nbt.py
python tools/gen_guardian_patchouli.py
python tools/generate_placeholders.py
python tools/gen_gal_ui_textures.py
python tools/gen_missing_handbook_assets.py
python tools/gen_patchouli_illustrations.py
python tools/gen_patchouli_img_lang.py
python tools/optimize_item_gui_textures.py
./gradlew build
./gradlew runClient
```

Generated files land under `src/main/resources/data/cocojenna/structures/` and Patchouli book JSON. The Gradle `validateRuinNbt` task checks that priority ruins, mausoleum variants, and first-cry village NBT exist and are non-empty.

### Texture optimizer (`optimize_item_gui_textures.py`)

Letterboxes oversized PNGs to Minecraft-friendly sizes (no stretch/crop) and losslessly recompresses them. Install [oxipng](https://github.com/shssoichiro/oxipng) on PATH for best compression; Pillow zlib is used as a fallback.

| Path | Target size | Notes |
|------|-------------|-------|
| `textures/item/` | 16×16 (most) or 32×32 (daikatana, ryokatana, cloaks, etc.) | Proportional letterbox |
| `textures/gui/skills/` | 32×32 | |
| `textures/gui/cards/` | 64×64 | |
| `textures/gui/portraits/` | 128×128 | Keep size; compress only |
| `textures/gui/regions/` | 128×74 | |
| `textures/gui/tabs/` | 64×64 | |
| `textures/gui/world_map.png` | 256×160 | |
| `textures/gui/world_map_thumb.png` | 128×80 | |
| `textures/gui/gal/` | **854×480** (backgrounds) | **Compress only — do not downscale** |
| `textures/gui/lore/` | 128×96 | Compress only |
| `textures/gui/patchouli/` | 854×480 | Compress only |
| Other `textures/gui/*.png` | 32×32 | Root-level HUD icons |

Optional Gradle wrapper: `./gradlew optimizeTextures` (runs the same script).

Verify nothing is pending: `python tools/optimize_item_gui_textures.py --dry-run` should report `Resized: 0 files`.
