#!/usr/bin/env python3
"""Wave 3 bulk content: Patchouli text entries, lang keys, dungeon loot tables."""
import json
from pathlib import Path

MOD = Path(__file__).resolve().parents[1]
RES = MOD / "src/main/resources"
ASSETS = RES / "assets/cocojenna"
DATA = RES / "data/cocojenna"

LANGS = {
    "en_us": {"suffix": "", "weapon_prefix": "Weapon lore: ", "wildcat_prefix": "Wild cat: ",
              "map_prefix": "Region: ", "qin_prefix": "Qin chapter: "},
    "zh_tw": {"suffix": "（繁）", "weapon_prefix": "武器傳說：", "wildcat_prefix": "怪貓：",
              "map_prefix": "區域：", "qin_prefix": "秦章："},
    "zh_cn": {"suffix": "（简）", "weapon_prefix": "武器传说：", "wildcat_prefix": "怪猫：",
              "map_prefix": "区域：", "qin_prefix": "秦章："},
}

WEAPONS = [
    "fish_bone_tide", "iron_rust_armor_break", "hibiscus_blood", "moonlight_ripple", "paper_crow_ink",
    "deep_sea_current", "iron_rust_legion", "sanhua_thread", "moonlight_clear", "forgotten_page",
    "neon_flash", "velvet_warmth", "blind_water_core", "stardust_step", "first_cry_beginner",
    "cheshire_grin", "dark_tide", "iron_claw_apprentice", "calico_warmth", "velvet_cradle",
    "tiger_iron", "wind_cut", "phantom", "suppression", "shockwave", "crescent", "moon_verdict",
    "star_map", "abyss", "neon_dance",
]

WILDCATS = [
    "moon_whisker", "storm_paw", "velvet_shadow", "neon_spirit", "blind_diver",
    "stardust_runner", "gear_rat", "hibiscus_guard", "dawn_crier", "gorge_wind",
    "maze_phantom", "tower_echo", "cardboard_king", "beach_tide", "catnip_dream",
]

MAP_REGIONS = [
    "velvet_forest", "moon_alley", "gear_town", "sleep_sanctuary", "blind_port",
    "dawn_highlands", "howling_gorge", "phantom_maze", "forgotten_tower", "rainbow_canyon", "stardust_desert",
]

QIN_CHAPTERS = ["ch1_awake", "ch2_hungry", "ch3_maids", "ch4_paper", "ch5_tomb",
                "ch6_star", "ch7_human", "ch8_finale"]

FIRST_CRY_NPCS = [
    "ryokatsu", "pagepaw", "blade_mark", "molten_paw", "miso", "mint_ear",
    "moon_whisper", "soft_pad", "tide_tail", "mud_bean", "wander_stray",
]

TOWN_SCHEDULE = [
    ("guard_captain", "morning"), ("guard_captain", "day"), ("guard_captain", "evening"), ("guard_captain", "night"),
    ("librarian", "morning"), ("librarian", "day"), ("librarian", "evening"), ("librarian", "night"),
    ("merchant", "morning"), ("merchant", "day"), ("merchant", "evening"), ("merchant", "night"),
    ("healer", "morning"), ("healer", "day"), ("healer", "evening"), ("healer", "night"),
    ("blacksmith", "morning"), ("blacksmith", "day"), ("blacksmith", "evening"), ("blacksmith", "night"),
    ("innkeeper", "morning"), ("innkeeper", "day"), ("innkeeper", "evening"), ("innkeeper", "night"),
    ("fisher", "morning"), ("fisher", "day"), ("fisher", "evening"), ("fisher", "night"),
    ("farmer", "morning"), ("farmer", "day"), ("farmer", "evening"), ("farmer", "night"),
]

DUNGEON_LOOT_MISSING = [
    "stardust_tomb", "forgotten_vault", "cardboard_depth", "moonlight_grotto", "catnip_mine",
]

MAUSOLEUM_TYPES = [
    "velvet", "moon", "gear", "blind", "dawn", "chaos",
]


def patchouli_entry(category, entry_id, icon, sortnum, title_key, p1_key, p2_key=None):
    pages = [{"type": "patchouli:text", "text": p1_key}]
    if p2_key:
        pages.append({"type": "patchouli:text", "text": p2_key})
    return {
        "name": title_key,
        "icon": icon,
        "category": f"cocojenna:{category}",
        "sortnum": sortnum,
        "pages": pages,
    }


def write_patchouli():
    for lang in LANGS:
        base = ASSETS / "patchouli_books/guardian_guide" / lang
        # categories
        for cat_id, name_key in [
            ("weapons", "patchouli.cocojenna.category.weapons"),
            ("wildcat", "patchouli.cocojenna.category.wildcat"),
            ("qin_kemu", "patchouli.cocojenna.category.qin_kemu"),
        ]:
            cat_path = base / "categories" / f"{cat_id}.json"
            cat_path.parent.mkdir(parents=True, exist_ok=True)
            cat_path.write_text(json.dumps({
                "name": name_key,
                "description": name_key + ".desc",
                "icon": "cocojenna:guardian_guide",
                "sortnum": 12 if cat_id == "weapons" else 13 if cat_id == "wildcat" else 14,
            }, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

        for i, wid in enumerate(WEAPONS):
            ep = base / "entries/weapons" / f"{wid}.json"
            ep.parent.mkdir(parents=True, exist_ok=True)
            tk = f"patchouli.cocojenna.weapon.{wid}"
            ep.write_text(json.dumps(patchouli_entry(
                "weapons", wid, "cocojenna:ryokatana_velvet_whisper", i,
                tk, f"{tk}.p1", f"{tk}.p2"
            ), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

        for i, wc in enumerate(WILDCATS):
            ep = base / "entries/wildcat" / f"{wc}.json"
            ep.parent.mkdir(parents=True, exist_ok=True)
            tk = f"patchouli.cocojenna.wildcat.{wc}"
            ep.write_text(json.dumps(patchouli_entry(
                "wildcat", wc, "cocojenna:toy_squeak", i,
                tk, f"{tk}.p1", f"{tk}.p2"
            ), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

        for i, region in enumerate(MAP_REGIONS):
            ep = base / "entries/map" / f"{region}.json"
            ep.parent.mkdir(parents=True, exist_ok=True)
            tk = f"patchouli.cocojenna.map.{region}"
            ep.write_text(json.dumps(patchouli_entry(
                "map", region, "cocojenna:memory_lighthouse", i + 2,
                tk, f"{tk}.p1", f"{tk}.p2"
            ), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

        for i, ch in enumerate(QIN_CHAPTERS):
            ep = base / "entries/qin_kemu" / f"{ch}.json"
            ep.parent.mkdir(parents=True, exist_ok=True)
            tk = f"patchouli.cocojenna.qin.{ch}"
            ep.write_text(json.dumps(patchouli_entry(
                "qin_kemu", ch, "cocojenna:origami_scrap", i,
                tk, f"{tk}.p1", f"{tk}.p2"
            ), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def merge_lang():
    for lang, meta in LANGS.items():
        path = ASSETS / "lang" / f"{lang}.json"
        data = json.loads(path.read_text(encoding="utf-8")) if path.exists() else {}

        for wid in WEAPONS:
            tk = f"patchouli.cocojenna.weapon.{wid}"
            data[tk] = meta["weapon_prefix"] + wid.replace("_", " ") + meta["suffix"]
            data[f"{tk}.p1"] = f"Memory trial and acquisition hints for {wid}."
            data[f"{tk}.p2"] = f"Force synergy and unseal stage notes for {wid}."

        for wc in WILDCATS:
            tk = f"patchouli.cocojenna.wildcat.{wc}"
            data[tk] = meta["wildcat_prefix"] + wc.replace("_", " ") + meta["suffix"]
            data[f"{tk}.p1"] = f"Habitat, behavior trigger, and befriending tip for {wc}."
            data[f"{tk}.p2"] = f"Lore connection and hunter journal entry for {wc}."

        for region in MAP_REGIONS:
            tk = f"patchouli.cocojenna.map.{region}"
            data[tk] = meta["map_prefix"] + region.replace("_", " ") + meta["suffix"]
            data[f"{tk}.p1"] = f"Landmark coordinates and exploration tier for {region}."
            data[f"{tk}.p2"] = f"Dungeon, lore shards, and region completion reward for {region}."

        for ch in QIN_CHAPTERS:
            tk = f"patchouli.cocojenna.qin.{ch}"
            data[tk] = meta["qin_prefix"] + ch + meta["suffix"]
            data[f"{tk}.p1"] = f"Main quest beats and mausoleum tie-in for {ch}."
            data[f"{tk}.p2"] = f"Combat pattern and favor gates for {ch}."

        data["patchouli.cocojenna.category.weapons"] = "Flagship Weapons" if lang == "en_us" else "旗艦武器"
        data["patchouli.cocojenna.category.weapons.desc"] = "Ryokatana and daikatana lore (text only)."
        data["patchouli.cocojenna.category.wildcat"] = "Wild Cat Codex" if lang == "en_us" else "怪貓圖鑑"
        data["patchouli.cocojenna.category.wildcat.desc"] = "Fifteen regional wild cats."
        data["patchouli.cocojenna.category.qin_kemu"] = "Qin Kemu" if lang == "en_us" else "秦可沐專章"
        data["patchouli.cocojenna.category.qin_kemu.desc"] = "Eight-chapter side story."

        for npc in FIRST_CRY_NPCS:
            data[f"dream.cocojenna.first_cry.{npc}.1"] = f"Dream scene line 1 for {npc}."
            data[f"dream.cocojenna.first_cry.{npc}.2"] = f"Dream scene line 2 for {npc}."

        for npc, slot in TOWN_SCHEDULE:
            data[f"schedule.cocojenna.{npc}.{slot}"] = f"{npc} {slot} schedule activity."

        data["ecology.cocojenna.event.meteor"] = "Meteor shower over the kingdom."
        data["ecology.cocojenna.event.phantom_cat"] = "A phantom cat crosses the path."
        data["ecology.cocojenna.event.wind_chime"] = "Wind chimes echo from a ruin."
        data["ecology.cocojenna.event.memory_echo"] = "Memory shards shimmer in the air."
        data["ecology.cocojenna.event.neon_bloom"] = "Neon mushrooms bloom briefly."
        data["ecology.cocojenna.event.moon_whisper"] = "The moon whispers an old name."
        data["explore.cocojenna.wildcat.master_title"] = "Wild Cat Master title earned!"
        data["item.cocojenna.wildcat_hunter_trophy"] = "Hunter's Squeak Trophy"
        data["tutorial.cocojenna.band.early"] = "Early kingdom tutorial (0–5 min)."
        data["tutorial.cocojenna.band.mid"] = "Mid tutorial (5–20 min)."
        data["tutorial.cocojenna.band.late"] = "Late tutorial (20–40 min)."
        data["tutorial.cocojenna.band.expert"] = "Expert hints (40–60 min)."
        data["society.cocojenna.gal.proposal"] = "Will you walk this path with me?"
        data["society.cocojenna.gal.wedding"] = "Under the moon plaza, we are family."
        data["society.cocojenna.gal.pregnancy"] = "A new heartbeat joins our home."
        data["society.cocojenna.gal.birth"] = "Welcome, little paw."
        data["quest.cocojenna.li_qingzhao.step1"] = "Find the plum blossom near the mausoleum."
        data["quest.cocojenna.li_qingzhao.step5"] = "Li Qingzhao's poem is complete."

        for i, ch in enumerate(range(2, 6)):
            for j in range(5):
                name = f"CH{ch}_QUEST_{j+1}"
                key = f"undercat.cocojenna.commission.{name.lower()}"
                data[key] = f"Undercat Ch{ch} commission {j+1}"

        path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def write_dungeon_loot():
    template = {
        "type": "minecraft:chest",
        "pools": [{
            "rolls": {"min": 2, "max": 4},
            "entries": [
                {"type": "minecraft:item", "name": "cocojenna:memory_shard", "weight": 8},
                {"type": "minecraft:item", "name": "cocojenna:map_fragment", "weight": 4},
                {"type": "minecraft:item", "name": "minecraft:emerald", "weight": 3,
                 "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 3}}]},
            ],
        }],
    }
    for did in DUNGEON_LOOT_MISSING:
        p = DATA / "loot_tables/chests" / f"dungeon_{did}.json"
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_text(json.dumps(template, indent=2) + "\n", encoding="utf-8")


def write_mausoleum_loot():
    for mt in MAUSOLEUM_TYPES:
        for suffix in ("", "_corrupted"):
            p = DATA / "loot_tables/mausoleum" / f"{mt}{suffix}.json"
            p.parent.mkdir(parents=True, exist_ok=True)
            p.write_text(json.dumps({
                "type": "minecraft:chest",
                "pools": [{
                    "rolls": 3,
                    "entries": [
                        {"type": "minecraft:item", "name": "cocojenna:origami_scrap", "weight": 5},
                        {"type": "minecraft:item", "name": "cocojenna:memory_shard", "weight": 4},
                        {"type": "minecraft:item", "name": "cocojenna:map_fragment", "weight": 2},
                    ],
                }],
            }, indent=2) + "\n", encoding="utf-8")


def main():
    write_patchouli()
    merge_lang()
    write_dungeon_loot()
    write_mausoleum_loot()
    total = len(WEAPONS) + len(WILDCATS) + len(MAP_REGIONS) + len(QIN_CHAPTERS)
    print(f"Wave 3 content: {total} patchouli entries x3 langs, loot tables, lang keys")


if __name__ == "__main__":
    main()
