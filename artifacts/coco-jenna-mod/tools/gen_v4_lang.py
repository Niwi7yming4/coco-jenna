#!/usr/bin/env python3
"""Append v4 deepening lang keys."""
import json
from pathlib import Path

LANG = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang"

ZH = {
    "ruin.cocojenna.war_ruins.lore": "戰爭遺跡的講台仍留著未寄出的軍令。",
    "ruin.cocojenna.velvet_tower.lore": "絨毛塔頂的營火在夜裡為迷途者指路。",
    "ruin.cocojenna.stray_cat_canteen.lore": "流浪貓食堂的灶火早已熄滅，碗卻仍溫熱。",
    "ruin.cocojenna.abandoned_toy_vault.lore": "玩具庫的鈴鐺一響，就有東西在暗處翻身。",
    "ruin.cocojenna.fallen_heroes_monument.lore": "英雄碑上的名字被風磨平，只剩貓爪印。",
    "dream.cocojenna.penetration.1": "月光腳印在夢裡延伸，指向一扇未開的門。",
    "dream.cocojenna.penetration.2": "灰鬚長老的低語混在風裡：「記憶會帶你回家。」",
    "dream.cocojenna.penetration.3": "你醒來時，掌心仍殘留月石的涼意。",
    "dream.cocojenna.moon.1": "滿月懸在初啼村上空，像一隻睜開的眼睛。",
    "dream.cocojenna.moon.2": "夢裡的鐘聲與現實重疊，你分不清哪邊才是清晨。",
    "dream.cocojenna.village.1": "村莊在夢中無限延伸，每條巷都通向同一棵聖樹。",
    "dream.cocojenna.village.2": "貓群從屋頂掠過，沒有聲音，只有尾巴劃過月光。",
    "dream.cocojenna.coco.1": "可可：「別怕，我在夢裡也會守著你。」",
    "dream.cocojenna.coco.2": "她的呼嚕聲把噩夢震成碎片。",
    "dream.cocojenna.npc.sanhua.1": "三花在夢裡縫補一張破紙，紙上寫著你的名字。",
    "dream.cocojenna.npc.sanhua.2": "針腳落下時，你聞到木槿與墨香。",
    "dream.cocojenna.npc.ironpaw.1": "鐵爪在夢中鍛造一把小刀，刀身映出你年幼的臉。",
    "dream.cocojenna.npc.ironpaw.2": "錘聲停時，他說：「還不夠硬，再磨一磨。」",
    "dream.cocojenna.npc.cheshire.1": "柴郡的笑在霧裡浮現又消失。",
    "dream.cocojenna.npc.cheshire.2": "「醒來也別太認真。」笑聲漸遠。",
    "dream.cocojenna.npc.alpha.1": "阿爾法的身影站在星空下，像一座沉默的塔。",
    "dream.cocojenna.npc.alpha.2": "「路還長。」他只說了這一句。",
    "dream.cocojenna.npc.samurai.1": "浪人武士在夢中拔刀，刀光卻停在半空。",
    "dream.cocojenna.npc.samurai.2": "「你的敵人不在夢裡。」他收刀入鞘。",
    "boss.cocojenna.dialogue.phase2.1": "影爪：「我還記得……曾經守護過的貓。」",
    "boss.cocojenna.dialogue.phase2.2": "黑泥從他甲冑的裂縫滲出，像無聲的淚。",
    "boss.cocojenna.dialogue.phase3.1": "影爪：「選吧。救贖我，或終結我。」",
    "boss.cocojenna.dialogue.redemption.1": "可可：「叔叔，回來吧。」",
    "boss.cocojenna.dialogue.redemption.2": "珍奶：「我們一起把黑泥洗掉！」",
    "boss.cocojenna.dialogue.purge.1": "影爪：「那就……讓黑暗吞噬一切。」",
    "boss.cocojenna.dialogue.purge.2": "他的氣息驟然暴漲，像漲潮的盲水。",
    "boss.cocojenna.dialogue.twin.1": "可可與珍奶的呼嚕共鳴，化作屏障。",
    "boss.cocojenna.dialogue.twin.2": "姊妹的溫度穿透黑泥，你重新站穩。",
    "boss.cocojenna.shadow_claw.need_happiness_redemption": "王國幸福度不足（需≥60）才能選擇救贖。",
    "boss.cocojenna.shadow_claw.too_happy_purge": "王國過於安樂（幸福≥70），難以選擇肅清。",
    "village.cocojenna.festival.deferred_palace": "王宮滿月祭進行中，村莊節日延後一日。",
    "village.cocojenna.festival.reward_hint.pastoral": "獎勵：糧食儲備 +8、幸福 +6",
    "village.cocojenna.festival.reward_hint.coastal": "獎勵：繁榮 +5、盲水港聲望 +3",
    "village.cocojenna.festival.reward_hint.industrial": "獎勵：繁榮 +8、齒輪鎮聲望 +4",
    "village.cocojenna.festival.reward_hint.scholarly": "獎勵：繁榮 +6",
    "village.cocojenna.festival.reward_hint.mystic": "獎勵：王宮聲望 +5",
    "village.cocojenna.festival.reward_hint.military": "獎勵：村莊防禦 +5",
    "village.cocojenna.festival.reward_hint.trade": "獎勵：繁榮 +10",
    "village.cocojenna.festival.reward_hint.festive": "獎勵：幸福 +12",
    "society.cocojenna.schedule.family_elder": "長輩貓談起家族 %2$s（好感 %1$s）",
    "society.cocojenna.schedule.family_parent": "父母貓照顧著家族 %2$s 的日常",
    "society.cocojenna.schedule.family_child": "小貓談起家裡的規矩與夢想",
    "society.cocojenna.schedule.morning.sanhua": "三花正在曬紙、準備今日的刺繡。",
    "society.cocojenna.schedule.day.ironpaw": "鐵爪巡視工坊，檢查學徒的鍛造。",
    "society.cocojenna.schedule.evening.cheshire": "柴郡坐在巷口，笑看行人來去。",
    "society.cocojenna.schedule.night.alpha": "阿爾法仰望星空，低聲念著古老的誓詞。",
    "society.cocojenna.schedule.morning.samurai": "浪人武士在廣場練刀，刀風清冽。",
}

EN = {
    "ruin.cocojenna.war_ruins.lore": "The war ruin lectern still holds unsent military orders.",
    "ruin.cocojenna.velvet_tower.lore": "Campfire atop the velvet tower guides the lost at night.",
    "ruin.cocojenna.stray_cat_canteen.lore": "The stray canteen fire is cold, yet the bowls feel warm.",
    "ruin.cocojenna.abandoned_toy_vault.lore": "When the toy vault bell rings, something stirs in the dark.",
    "ruin.cocojenna.fallen_heroes_monument.lore": "Hero names on the monument faded—only paw prints remain.",
    "dream.cocojenna.penetration.1": "Moon paw prints stretch through the dream toward an unopened door.",
    "dream.cocojenna.penetration.2": "Gray Whisker whispers on the wind: \"Memory will lead you home.\"",
    "dream.cocojenna.penetration.3": "You wake with moonstone chill still in your palm.",
    "dream.cocojenna.moon.1": "A full moon hangs over First Cry like an open eye.",
    "dream.cocojenna.moon.2": "Dream bells overlap reality—you cannot tell which is morning.",
    "dream.cocojenna.village.1": "The village extends endlessly; every alley leads to the sacred tree.",
    "dream.cocojenna.village.2": "Cats cross rooftops in silence, tails slicing moonlight.",
    "dream.cocojenna.coco.1": "Coco: \"Don't be afraid—I guard you even in dreams.\"",
    "dream.cocojenna.coco.2": "Her purr shatters the nightmare into shards.",
    "dream.cocojenna.npc.sanhua.1": "Sanhua mends torn paper in the dream; your name is written there.",
    "dream.cocojenna.npc.sanhua.2": "Each stitch carries hibiscus and ink.",
    "dream.cocojenna.npc.ironpaw.1": "Ironpaw forges a small blade reflecting your younger face.",
    "dream.cocojenna.npc.ironpaw.2": "When the hammer stops: \"Not hard enough—keep sharpening.\"",
    "dream.cocojenna.npc.cheshire.1": "Cheshire's grin appears and vanishes in the fog.",
    "dream.cocojenna.npc.cheshire.2": "\"Don't take waking life too seriously.\" Laughter fades.",
    "dream.cocojenna.npc.alpha.1": "Alpha stands under the stars like a silent tower.",
    "dream.cocojenna.npc.alpha.2": "\"The road is long.\" That is all he says.",
    "dream.cocojenna.npc.samurai.1": "The ronin draws in the dream—but the blade halts mid-air.",
    "dream.cocojenna.npc.samurai.2": "\"Your enemy is not in dreams.\" He sheathes the sword.",
    "boss.cocojenna.dialogue.phase2.1": "Shadow Claw: \"I still remember… the cats I once guarded.\"",
    "boss.cocojenna.dialogue.phase2.2": "Black mud seeps through his armor like silent tears.",
    "boss.cocojenna.dialogue.phase3.1": "Shadow Claw: \"Choose. Redeem me—or end me.\"",
    "boss.cocojenna.dialogue.redemption.1": "Coco: \"Uncle, come back.\"",
    "boss.cocojenna.dialogue.redemption.2": "Jenna: \"We'll wash the black mud away together!\"",
    "boss.cocojenna.dialogue.purge.1": "Shadow Claw: \"Then let darkness devour everything.\"",
    "boss.cocojenna.dialogue.purge.2": "His aura surges like a rising blind-water tide.",
    "boss.cocojenna.dialogue.twin.1": "Coco and Jenna's purr resonates into a barrier.",
    "boss.cocojenna.dialogue.twin.2": "Sister warmth pierces the mud—you stand firm again.",
    "boss.cocojenna.shadow_claw.need_happiness_redemption": "Kingdom happiness too low (need ≥60) for redemption.",
    "boss.cocojenna.shadow_claw.too_happy_purge": "Kingdom too content (happiness ≥70) to choose purge.",
    "village.cocojenna.festival.deferred_palace": "Palace full-moon festival active—village festival deferred.",
    "village.cocojenna.festival.reward_hint.pastoral": "Rewards: food stock +8, happiness +6",
    "village.cocojenna.festival.reward_hint.coastal": "Rewards: prosperity +5, Blind Port rep +3",
    "village.cocojenna.festival.reward_hint.industrial": "Rewards: prosperity +8, Gear Town rep +4",
    "village.cocojenna.festival.reward_hint.scholarly": "Rewards: prosperity +6",
    "village.cocojenna.festival.reward_hint.mystic": "Rewards: royal rep +5",
    "village.cocojenna.festival.reward_hint.military": "Rewards: village defense +5",
    "village.cocojenna.festival.reward_hint.trade": "Rewards: prosperity +10",
    "village.cocojenna.festival.reward_hint.festive": "Rewards: happiness +12",
    "society.cocojenna.schedule.family_elder": "Elder speaks of family %2$s (favor %1$s)",
    "society.cocojenna.schedule.family_parent": "Parent tends daily life for family %2$s",
    "society.cocojenna.schedule.family_child": "Kitten talks of house rules and dreams",
    "society.cocojenna.schedule.morning.sanhua": "Sanhua dries paper for today's embroidery.",
    "society.cocojenna.schedule.day.ironpaw": "Ironpaw inspects apprentices at the forge.",
    "society.cocojenna.schedule.evening.cheshire": "Cheshire sits in the alley, grinning at passersby.",
    "society.cocojenna.schedule.night.alpha": "Alpha watches the stars, murmuring old vows.",
    "society.cocojenna.schedule.morning.samurai": "The ronin drills in the plaza—blade wind sharp.",
}


def merge(path: Path, extra: dict):
    data = json.loads(path.read_text(encoding="utf-8"))
    data.update(extra)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main():
    merge(LANG / "zh_tw.json", ZH)
    merge(LANG / "en_us.json", EN)
    print(f"Added {len(ZH)} zh_tw keys")


if __name__ == "__main__":
    main()
