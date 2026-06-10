# Coco-Jenna mod tools

## Build prerequisites

Before `./gradlew build` on a fresh clone, generate structure NBT assets:

```bash
cd artifacts/coco-jenna-mod
python tools/gen_loot_and_ruin_nbt.py
python tools/gen_mausoleum_nbt.py
python tools/gen_biome_structure_nbt.py
python tools/gen_guardian_patchouli.py
```

Generated files land under `src/main/resources/data/cocojenna/structures/` and Patchouli book JSON. The Gradle `validateRuinNbt` task checks that priority ruins, mausoleum variants, and first-cry village NBT exist and are non-empty.

## Optional texture pipeline

```bash
python tools/optimize_item_gui_textures.py
```

Uses Pillow by default; installs `oxipng` on PATH for extra lossless compression when available.
