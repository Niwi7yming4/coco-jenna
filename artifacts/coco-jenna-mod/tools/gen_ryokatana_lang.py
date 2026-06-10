"""Generate zh_tw / en_us lang entries for all 50 Ryokatana weapons."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ZH = ROOT / "src/main/resources/assets/cocojenna/lang/zh_tw.json"
EN = ROOT / "src/main/resources/assets/cocojenna/lang/en_us.json"

# id -> (zh_name, zh_effect, en_name, en_effect)
WEAPONS = {
    "fish_bone_tide": ("魚骨・潮風", "水中或雨天攻擊力 +20%", "Fishbone · Tide", "+20% damage in water or rain"),
    "copper_bell_soul": ("銅鈴・鎮魂", "滿血時傷害 +12%，命中偶爾鳴鈴", "Copper Bell · Soul Ward", "+12% damage at high HP; occasional bell chime"),
    "iron_rust_armor_break": ("鐵鏽・破甲", "對有護甲敵人傷害 +25%", "Iron Rust · Armor Break", "+25% damage vs armored foes"),
    "origami_cut": ("摺紙・裁斷", "對隱形敵人傷害 +30%，命中發光", "Origami · Cut", "+30% vs invisible; hit applies glow"),
    "jellyfish_bind": ("水母・縛靈", "緩速敵人傷害 +25%，命中緩速", "Jellyfish · Bind", "+25% vs slowed; hit slows target"),
    "screen_noise": ("螢幕・雜訊", "15% 機率額外傷害，命中混亂", "Screen · Noise", "15% bonus damage; hit may confuse"),
    "precision_gear": ("齒輪・精工", "對機械敵人傷害 +30%", "Gear · Precision", "+30% damage vs mechanical foes"),
    "moth_scale": ("絨蛾・鱗粉", "夜晚傷害 +15%，命中可能回復", "Velvet Moth · Scale", "+15% damage at night; hit may heal"),
    "blind_water_abyss": ("盲水・深淵", "水中攻擊 +20%，水中命中致盲", "Blind Water · Abyss", "+20% in water; blind wet targets"),
    "lament_split": ("悲嘆・分裂", "低血敵人傷害 +30%，重擊虛弱", "Lament · Split", "+30% vs low HP; heavy hits weaken"),
    "fallen_velvet_claw": ("墮落・絨爪", "對貓類敵人傷害 +35%", "Fallen Velvet · Claw", "+35% damage vs cat-type foes"),
    "whisper_mud": ("竊語・黑泥", "隱身時傷害 +40%", "Whisper · Black Mud", "+40% damage while invisible"),
    "memory_worm": ("記憶・蠕蟲", "擊殺有 5% 機率掉落記憶碎片", "Memory · Worm", "5% chance to drop memory shard on hit"),
    "mimic_disguise": ("擬態・假面", "被動：迷惑敵人（主動技：隱身突進）", "Mimic · Disguise", "Passive misdirection; active stealth dash"),
    "bronze_guard": ("青銅・守護", "傷害 +10%，重武防禦強化", "Bronze · Guardian", "+10% damage; heavy guard stance"),
    "moon_shadow": ("爪痕・月影", "夜晚傷害 +20%", "Claw Mark · Moon Shadow", "+20% damage at night"),
    "silvervine_drunk": ("木天蓼・酔仙", "混亂時傷害 +20%（否則略減）", "Silvervine · Drunk", "+20% while confused (else slightly reduced)"),
    "neon_flash": ("霓虹・閃光", "白天傷害 +18%，命中發光", "Neon · Flash", "+18% damage by day; hit glows target"),
    "velvet_whisper": ("絨毛・低語", "命中虛弱敵人，對貓類 +35%", "Velvet · Whisper", "Hits weaken; +35% vs cat-type"),
    "moonlight_glimmer": ("月光・微光", "夜晚傷害 +20%", "Moonlight · Glimmer", "+20% damage at night"),
    "first_cry_memory": ("初啼・回憶", "傷害 +5%，擊殺可能掉碎片", "First Cry · Memory", "+5% damage; kills may drop shards"),
    "blind_water_stealth": ("盲水・潛行", "隱身時傷害 +40%", "Blind Water · Stealth", "+40% damage while invisible"),
    "gear_windup": ("齒輪・發條", "對機械 +30%，命中力量提升", "Gear · Windup", "+30% vs mechanical; hit grants strength"),
    "dawn_hope": ("黎明・希望", "白天攻擊力 +15%", "Dawn · Hope", "+15% damage during daytime"),
    "forgotten_page": ("遺忘・書頁", "傷害 +12%，命中可能獲得吸收", "Forgotten · Page", "+12% damage; hit may grant absorption"),
    "stardust_tread": ("星塵・踏步", "傷害 +12%，移動更靈巧", "Stardust · Tread", "+12% damage; agile movement"),
    "velvet_cradle": ("絨毛・搖籃", "命中回復生命，對貓類 +35%", "Velvet · Cradle", "Hits heal; +35% vs cat-type"),
    "red_jade": ("線球・紅玉", "拉扯與治療時傷害 +20%", "Yarn Ball · Red Jade", "+20% damage while regenerating"),
    "iron_claw_apprentice": ("鐵爪・學徒", "破甲 +25%，命中凋零", "Iron Claw · Apprentice", "+25% vs armor; hit withers"),
    "calico_warmth": ("玳瑁・暖絨", "命中獲得抗火，治療時 +20%", "Calico · Warmth", "Fire resistance on hit; +20% while healing"),
    "cheshire_grin": ("柴郡・笑臉", "潛行時傷害 +35%", "Cheshire · Grin", "+35% damage while sneaking"),
    "white_glove_guide": ("白手套・引航", "水中攻擊 +30%", "White Glove · Guide", "+30% damage in water"),
    "alpha_observe": ("Alpha・觀測", "對高生命 Boss +25%", "Alpha · Observe", "+25% damage vs high-HP bosses"),
    "coco_guardian": ("可可・守護", "傷害 +10%，主動技治療同伴", "Coco · Guardian", "+10% damage; active heals allies"),
    "milk_tea_play": ("珍奶・玩心", "傷害 +10%，命中加速", "Milk Tea · Play", "+10% damage; hit grants speed"),
    "gear_precision_2": ("齒輪・精密Ⅱ", "對機械敵人傷害 +30%", "Gear · Precision II", "+30% damage vs mechanical foes"),
    "dark_tide": ("無明港・暗潮", "從背後攻擊傷害 +40%", "Blind Port · Dark Tide", "+40% damage from behind"),
    "velvet_warmth": ("絨尾・暖絨", "命中回復生命，對貓類 +35%", "Velvet Tail · Warmth", "Hits heal; +35% vs cat-type"),
    "moonlight_clear": ("月光・澄明", "夜晚傷害 +20%", "Moonlight · Clear", "+20% damage at night"),
    "first_cry_beginner": ("初啼・新手", "傷害 +5%", "First Cry · Beginner", "+5% damage"),
    "hibiscus_blood": ("朱槿・泣血", "血量低於 30% 時攻擊 +50%", "Hibiscus · Weeping Blood", "+50% attack below 30% HP"),
    "stardust_step": ("星塵・步影", "傷害 +12%，主動技加速斬擊", "Stardust · Step", "+12% damage; active speed slash"),
    "iron_rust_legion": ("鐵鏽・軍團", "破甲 +25%，高攻擊重武", "Iron Rust · Legion", "+25% vs armor; heavy striker"),
    "paper_crow_ink": ("紙鴉・墨痕", "傷害 +8%，命中中毒", "Paper Crow · Ink", "+8% damage; hit poisons"),
    "blind_water_core": ("盲水・核心", "水中 +20%，水中致盲", "Blind Water · Core", "+20% in water; blind wet targets"),
    "deep_sea_current": ("深海・潮流", "水中攻擊力 +20%", "Deep Sea · Current", "+20% damage in water"),
    "moonlight_ripple": ("月光・漣漪", "夜晚傷害 +20%", "Moonlight · Ripple", "+20% damage at night"),
    "royal_glory": ("王權・榮光", "對黑泥敵人 +35%，一般 +10%", "Royal · Glory", "+35% vs black mud; +10% otherwise"),
    "gear_schedule": ("齒輪鎮・排程", "技能冷卻縮短，對機械 +30%", "Gear Town · Schedule", "Shorter cooldowns; +30% vs mechanical"),
    "sanhua_thread": ("三花・紡線", "傷害 +8%，編織記憶之力", "Sanhua · Thread", "+8% damage; woven memory power"),
}

SHARED = {
    "zh": {
        "item.cocojenna.ryokatana.lore": "貓之國流傳的良快刀，刃上帶著絨尾王朝的舊日記憶。",
    },
    "en": {
        "item.cocojenna.ryokatana.lore": "A legendary ryokatana of the Cat Kingdom, etched with memories of the Velvet Dynasty.",
    },
}


def build_entries(lang: str) -> dict:
    out = dict(SHARED[lang])
    for wid, (zh_name, zh_eff, en_name, en_eff) in WEAPONS.items():
        if lang == "zh":
            out[f"item.cocojenna.ryokatana_{wid}"] = zh_name
            out[f"item.cocojenna.ryokatana.{wid}.effect"] = zh_eff
        else:
            out[f"item.cocojenna.ryokatana_{wid}"] = en_name
            out[f"item.cocojenna.ryokatana.{wid}.effect"] = en_eff
    return out


def merge(path: Path, entries: dict) -> None:
    data = json.loads(path.read_text(encoding="utf-8"))
    data.update(entries)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Updated {path.name} (+{len(entries)} ryokatana keys)")


if __name__ == "__main__":
    merge(ZH, build_entries("zh"))
    merge(EN, build_entries("en"))
