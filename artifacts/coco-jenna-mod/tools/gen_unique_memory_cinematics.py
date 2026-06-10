#!/usr/bin/env python3
"""Unique weapon memory cinematic lang for 20 task-linked ryokatana variants."""
import json
from pathlib import Path

LANG_DIR = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang"

# variant -> (zh line1, zh line2, zh line3, en line1, en line2, en line3)
UNIQUE = {
    "fish_bone_tide": (
        "盲水河的潮汐把魚骨推上岸，刀鋒記住了鹹味。",
        "你想起某隻貓在碼頭等漁獲的日子。",
        "潮退後，記憶像骨刺一樣留在刃上。",
        "Blind-water tides wash fish bones ashore; the blade remembers salt.",
        "You recall a cat waiting on the pier for the catch.",
        "When the tide recedes, memory clings to the edge like bone.",
    ),
    "iron_rust_armor_break": (
        "鐵鏽的鎧甲碎裂時，發出像貓抓鐵欄的聲音。",
        "黑泥從裂縫滲出，又被你的刀光斬斷。",
        "這把刀學會了在腐蝕中仍保持鋒利。",
        "Rusty armor shatters with a sound like claws on iron.",
        "Black mud seeps through the cracks—then your blade cuts it away.",
        "This sword learned to stay sharp amid corrosion.",
    ),
    "hibiscus_blood": (
        "木槿花瓣落在刀身，像未乾的胭脂。",
        "花香裡混著一絲鐵味——是誰的淚？",
        "刀刃吸收花色，綻出守護的紅。",
        "Hibiscus petals fall on the steel like wet rouge.",
        "Perfume mingles with iron—whose tears are these?",
        "The blade drinks the color and blooms guardian red.",
    ),
    "moonlight_ripple": (
        "滿月下的水面泛起銀色漣漪。",
        "刀尖輕點，漣漪化作通往記憶的門。",
        "月光在刃上寫下誓言：守護到底。",
        "Silver ripples spread under the full moon.",
        "The tip touches water; ripples become a door to memory.",
        "Moonlight etches a vow on the blade: protect to the end.",
    ),
    "paper_crow_ink": (
        "烏鴉銜著浸墨的紙片掠過夜空。",
        "紙上字跡模糊，卻有一句話格外清晰。",
        "「別讓故事斷在這裡。」",
        "A crow carries ink-stained paper across the night.",
        "The script blurs—except one line, painfully clear.",
        "\"Don't let the story end here.\"",
    ),
    "deep_sea_current": (
        "深海暗流托住下沉的記憶碎片。",
        "你聽見遙遠的貓鳴，像從海底傳來。",
        "刀身與潮汐同頻，記起未完成的盟約。",
        "Abyss currents cradle sinking memory shards.",
        "Distant cat-calls seem to rise from the seabed.",
        "Blade and tide resonate—an unfinished pact returns.",
    ),
    "iron_rust_legion": (
        "最後一副鐵鏽鎧甲倒下，戰場歸於寂靜。",
        "風捲起塵埃，露出底下貓爪印的泥痕。",
        "這把刀記住勝利，也記住代價。",
        "The last rusted armor falls; the field goes still.",
        "Wind lifts dust, revealing paw prints in the mud.",
        "The blade remembers victory—and its price.",
    ),
    "sanhua_thread": (
        "紅紙上的血線尚未乾透。",
        "三花師父的針腳穿過記憶，縫合破碎的名字。",
        "刀鋒沿著紅線游走，像守護一道咒語。",
        "Blood threads on red paper have not yet dried.",
        "Master Sanhua's stitches mend shattered names in memory.",
        "The edge follows the red line like guarding a spell.",
    ),
    "moonlight_clear": (
        "鏡湖映出兩輪月亮——一輪在天，一輪在刃。",
        "水面清澈得能看見沉在底部的舊日。",
        "你伸手觸碰倒影，記憶隨漣漪擴散。",
        "Mirror lake shows two moons—one sky, one steel.",
        "Water so clear you see yesterdays on the bottom.",
        "You touch the reflection; memory ripples outward.",
    ),
    "forgotten_page": (
        "風翻開書頁，字句已褪色。",
        "只有信封角落的貓爪印仍清晰。",
        "未寄出的信，終於被這把刀讀懂。",
        "Wind turns a page; the ink has faded.",
        "Only a paw print in the corner stays sharp.",
        "The unsent letter is finally read by this blade.",
    ),
    "neon_flash": (
        "霓虹在雨夜閃爍，像貓眼在暗巷反光。",
        "刀光與招牌同頻閃爍，劃破迷霧。",
        "城市記憶被定格在一瞬的亮色裡。",
        "Neon flickers in the rainy night like cat eyes in alleys.",
        "Blade-light pulses with the signs, cutting fog.",
        "City memory freezes in one bright instant.",
    ),
    "velvet_warmth": (
        "絨毛蹭過刀鞘，留下溫熱的觸感。",
        "你想起營火旁打盹的同伴們。",
        "刃上殘留的體溫，是活下去的理由。",
        "Velvet brushes the scabbard, leaving warmth.",
        "You recall companions dozing by the campfire.",
        "Heat on the blade—reason enough to keep living.",
    ),
    "blind_water_core": (
        "盲水深處有顆脈動的核心，像沉睡的貓心。",
        "刀尖探入暗流，核心回應以微光。",
        "兩者共鳴，記起被水淹沒的故鄉。",
        "In blind depths, a pulsing core like a sleeping cat heart.",
        "The tip enters the dark current; the core answers with light.",
        "They resonate—homeland drowned in water returns.",
    ),
    "stardust_step": (
        "腳步踏過星塵，留下發光的爪印。",
        "夜空低垂，仿佛伸手就能撫摸記憶。",
        "刀身承接星屑，照亮前方的路。",
        "Footsteps cross stardust, leaving glowing paw prints.",
        "The sky hangs low—as if memory is within reach.",
        "The blade catches star-dust and lights the way.",
    ),
    "first_cry_beginner": (
        "初啼村的晨鐘還在遠處迴盪。",
        "你握著這把入門刀，像握著第一次選擇。",
        "記憶輕聲說：歡迎回家。",
        "First Cry's morning bell still echoes afar.",
        "You hold this beginner blade like your first choice.",
        "Memory whispers: welcome home.",
    ),
    "cheshire_grin": (
        "柴郡貓的笑容在霧裡一閃即逝。",
        "刀鋒追著笑聲，卻只剪下一片霧。",
        "霧裡傳來話：「別太認真，記得開心。」",
        "Cheshire's grin flashes once in the fog.",
        "The blade chases laughter but shears only mist.",
        "From the fog: \"Don't take it too hard—stay playful.\"",
    ),
    "dark_tide": (
        "暗潮捲走岸邊的腳印，卻捲不走刀上的決意。",
        "黑泥在潮頭翻湧，像無數低語。",
        "你立於潮前，記憶成為堤壩。",
        "Dark tide washes footprints away—not the resolve on your blade.",
        "Black mud rolls at the crest like countless whispers.",
        "You stand before the surge; memory becomes the levee.",
    ),
    "iron_claw_apprentice": (
        "鐵爪工坊的錘聲還在耳邊迴響。",
        "學徒期的每一道劃痕，都刻在刀身。",
        "師父說：「刀是手的延伸。」你終於懂了。",
        "Ironpaw forge hammers still ring in your ears.",
        "Every apprentice scratch is etched on the steel.",
        "Master said: \"The blade is your hand extended.\" Now you understand.",
    ),
    "calico_warmth": (
        "三花貓蜷在窗台曬太陽，斑紋像碎掉的彩虹。",
        "刀鞘吸收那份慵懶的暖，變得柔軟。",
        "再冷的戰場，也能想起這一刻。",
        "A calico curls on the sill; patches like a broken rainbow.",
        "The scabbard drinks that lazy warmth and softens.",
        "Even the coldest battlefield recalls this moment.",
    ),
    "velvet_cradle": (
        "絨毛搖籃輕輕晃動，哼著古老的貓謠。",
        "刀身貼著搖籃邊緣，學會了溫柔。",
        "記憶說：守護，有時只是靜靜陪伴。",
        "A velvet cradle sways, humming an old cat-lullaby.",
        "Steel rests on the rim and learns gentleness.",
        "Memory says: guarding is sometimes simply staying close.",
    ),
}


def merge(path: Path, en: bool):
    data = json.loads(path.read_text(encoding="utf-8"))
    for variant, lines in UNIQUE.items():
        for i in range(3):
            key = f"weapon.cinematic.cocojenna.{variant}.{i + 1}"
            data[key] = lines[i + 3] if en else lines[i]
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main():
    merge(LANG_DIR / "zh_tw.json", False)
    merge(LANG_DIR / "en_us.json", True)
    print(f"Wrote unique cinematics for {len(UNIQUE)} variants")


if __name__ == "__main__":
    main()
