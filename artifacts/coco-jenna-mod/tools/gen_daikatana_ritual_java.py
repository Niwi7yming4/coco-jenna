#!/usr/bin/env python3
"""Generate DaikatanaRitualRecipe.java from crafting recipe JSON files."""

from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RECIPES = ROOT / "src/main/resources/data/cocojenna/recipes"
OUT = ROOT / "src/main/java/com/cocojenna/memforge/DaikatanaRitualRecipe.java"

ITEM_MAP = {
    "minecraft:netherite_ingot": "Items.NETHERITE_INGOT",
    "minecraft:stick": "Items.STICK",
    "minecraft:iron_ingot": "Items.IRON_INGOT",
    "minecraft:gold_ingot": "Items.GOLD_INGOT",
    "minecraft:bone": "Items.BONE",
    "minecraft:leather": "Items.LEATHER",
    "minecraft:glass": "Items.GLASS",
    "minecraft:paper": "Items.PAPER",
    "minecraft:emerald": "Items.EMERALD",
    "minecraft:diamond": "Items.DIAMOND",
    "minecraft:feather": "Items.FEATHER",
    "minecraft:blaze_rod": "Items.BLAZE_ROD",
    "minecraft:ender_pearl": "Items.ENDER_PEARL",
    "minecraft:ghast_tear": "Items.GHAST_TEAR",
    "minecraft:prismarine_shard": "Items.PRISMARINE_SHARD",
    "minecraft:string": "Items.STRING",
    "minecraft:coal": "Items.COAL",
    "minecraft:redstone": "Items.REDSTONE",
    "minecraft:lapis_lazuli": "Items.LAPIS_LAZULI",
    "minecraft:amethyst_shard": "Items.AMETHYST_SHARD",
    "minecraft:copper_ingot": "Items.COPPER_INGOT",
}


def to_java_item(item_id: str) -> str:
    if item_id in ITEM_MAP:
        return ITEM_MAP[item_id]
    if item_id.startswith("minecraft:"):
        name = item_id.split(":")[1].upper()
        return f"Items.{name}"
    if item_id.startswith("cocojenna:"):
        short = item_id.split(":")[1]
        field = re.sub(r"[^A-Za-z0-9_]", "_", short).upper()
        if short.startswith("ryokatana_"):
            return f'ForgeRegistries.ITEMS.getValue(new ResourceLocation("cocojenna", "{short}"))'
        return f"ModItems.{field}.get()"
    raise ValueError(item_id)


def parse_recipe(path: Path) -> tuple[str, list[tuple[str, int]], str]:
    data = json.loads(path.read_text(encoding="utf-8"))
    result = data["result"]["item"].replace("cocojenna:daikatana_", "")
    counts: dict[str, int] = {}
    if data["type"] == "minecraft:crafting_shaped":
        for ch, spec in data["key"].items():
            item = spec["item"]
            counts[item] = counts.get(item, 0) + data["pattern"].count(ch)
    else:
        for ing in data["ingredients"]:
            item = ing["item"]
            counts[item] = counts.get(item, 0) + 1

    # ritual doubles crafting mats + adds remnant
    mats: list[tuple[str, int]] = []
    catalyst = ""
    for item, cnt in sorted(counts.items(), key=lambda x: x[0]):
        ritual_cnt = max(1, cnt * 2)
        if item.startswith("cocojenna:") and not catalyst:
            catalyst = item
        mats.append((item, ritual_cnt))
    if "cocojenna:black_mud_remnant" not in counts:
        mats.append(("cocojenna:black_mud_remnant", 4))
    if "minecraft:netherite_ingot" not in counts:
        mats.append(("minecraft:netherite_ingot", 2))
    if not catalyst:
        catalyst = "cocojenna:purr_crystal"
        mats.append((catalyst, 1))
    return result, mats, catalyst


def main() -> None:
    paths = sorted(RECIPES.glob("daikatana_*.json"))
    entries = []
    for idx, path in enumerate(paths):
        result_id, mats, catalyst = parse_recipe(path)
        enum_name = result_id.upper()
        mat_lines = ",\n            ".join(
            f"new Mat({to_java_item(i)}, {c})" for i, c in mats
        )
        suffix = ";" if idx == len(paths) - 1 else ","
        entries.append(
            f"    {enum_name}(\n"
            f'        "{result_id}",\n'
            f"        ModItems.DAIKATANA_{enum_name}.get(),\n"
            f"        {to_java_item(catalyst)},\n"
            f"        new Mat[]{{\n            {mat_lines}\n        }}){suffix}"
        )

    java = f"""package com.cocojenna.memforge;

import com.cocojenna.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;

/** Auto-generated from data/cocojenna/recipes/daikatana_*.json — altar forging requirements. */
public enum DaikatanaRitualRecipe {{
{chr(10).join(entries)}

    public final String id;
    public final Item result;
    public final Item catalyst;
    public final Mat[] materials;

    public record Mat(Item item, int count) {{}}

    DaikatanaRitualRecipe(String id, Item result, Item catalyst, Mat[] materials) {{
        this.id = id;
        this.result = result;
        this.catalyst = catalyst;
        this.materials = materials;
    }}

    @Nullable
    public static DaikatanaRitualRecipe forCatalyst(Item held) {{
        for (DaikatanaRitualRecipe r : values()) {{
            if (r.catalyst == held) return r;
        }}
        return null;
    }}

    public boolean hasMaterials(net.minecraft.world.entity.player.Inventory inv) {{
        for (Mat m : materials) {{
            if (countItem(inv, m.item) < m.count) return false;
        }}
        return true;
    }}

    public void consumeMaterials(net.minecraft.world.entity.player.Inventory inv) {{
        for (Mat m : materials) {{
            int left = m.count;
            for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {{
                var stack = inv.getItem(i);
                if (stack.is(m.item)) {{
                    int take = Math.min(left, stack.getCount());
                    stack.shrink(take);
                    left -= take;
                }}
            }}
        }}
    }}

    private static int countItem(net.minecraft.world.entity.player.Inventory inv, Item item) {{
        int total = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {{
            var stack = inv.getItem(i);
            if (stack.is(item)) total += stack.getCount();
        }}
        return total;
    }}
}}
"""
    OUT.write_text(java, encoding="utf-8")
    print(f"Wrote {OUT.name} ({len(entries)} recipes)")


if __name__ == "__main__":
    main()
