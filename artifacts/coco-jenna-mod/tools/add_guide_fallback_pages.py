import json
from pathlib import Path

ROOT = Path(r"c:\Users\ASUS\Desktop\Cat-Country-Forge\artifacts\coco-jenna-mod\src\main\resources\assets\cocojenna\lang")

PAGES_TW = {
    "guide.cocojenna.p.cover": "《守護者指南》\n\n「這不是一本攻略。這是一封寫給你的信。」——阿爾法",
    "guide.cocojenna.p.coco": "那是隻安靜的黑貓。別急著靠近。當她願意把尾巴放在你手上時——你就被選擇了。\n\n可可的註解：……謝謝。",
    "guide.cocojenna.p.welcome": "歡迎來到貓之國，守護者。這裡是所有被記住的貓咪最後的家。請保護公主們，直到她們想起自己是誰。",
    "guide.cocojenna.p.jenna": "玳瑁色的貓永遠停不下來。當她露出肚子時——她信任你了。珍奶塗鴉：蝴蝶 🦋",
    "guide.cocojenna.p.sisters": "可可是姐姐，珍奶是妹妹。黑貓守護，玳瑁陪伴。她們不需要你成為英雄，只需要你在這裡。",
    "guide.cocojenna.p.basics": "右鍵撫摸、蹲下抱起、餵食梳毛玩耍。WASD 移動，Alt 技能圓盤，雙擊 Shift 閃避（需解鎖序列）。",
    "guide.cocojenna.p.alpha": "黑泥是悲傷的凝結；記憶是力量；雙子星是夥伴；失敗不會真正失去她們。來廣場找阿爾法。",
    "guide.cocojenna.p.first_cry": "初啼村與生命線球樹。樹根小屋是可可、珍奶與你的家。村長老絨尾、商店貓小麥在此。",
    "guide.cocojenna.p.morning": "可可賴床，珍奶叼玩具叫醒你。兩個貓床、壁爐、食物碗、儲物箱。二樓臥室床很軟。",
    "guide.cocojenna.p.bond": "情感值是羈絆的量化。撫摸、餵食、玩耍、保護她們可提升。達到階段解鎖新互動——不要著急。",
    "guide.cocojenna.p.gather": "絨毛草、月光石、薄荷花、貓薄荷。初啼村商店購工具，工坊提升採集效率。",
    "guide.cocojenna.p.mud": "黑泥不是怪物。它們是沒能說出口的再見。記住它們，讓它們安息。",
    "guide.cocojenna.p.erosion": "侵蝕四階：沾染→蔓延→吞噬→深淵化。肉球印章、光塔、擊敗首領可阻止。",
    "guide.cocojenna.p.parasite": "寄生四階：嗜睡、抗拒、同化邊緣、完全腐蝕。雙子星毛色出現灰斑請立刻用聖水。",
    "guide.cocojenna.p.distill": "蒸餾不是殺戮而是淨化：剝殼→核心暴露 5-8 秒→按 R 蒸餾→記憶微粒。",
    "guide.cocojenna.p.bestiary": "失溫者、遺忘之影、低語泥偶、模仿者、盲水領主、原始混沌。遭遇後解鎖圖鑑。",
    "guide.cocojenna.p.sequence": "序列 9→1，每 10 級晉升三選一卡牌。槽位隨序列增加（4/8/16）。",
    "guide.cocojenna.p.paths": "呼嚕共鳴（防禦治癒）、夜瞳暗影（暴擊潛行）、混沌惡作劇（機率閃避）。",
    "guide.cocojenna.p.wheel": "按住 Alt 展開技能圓盤，滑鼠選方向後放開施放。可連續施放。",
    "guide.cocojenna.p.map": "11 區域：初啼村→Velvet Forest→Moon Alley→…→遺忘高塔。",
    "guide.cocojenna.p.memory": "記憶碎片：黑勾玉、橘爪印、金王冠、藍愛心。紀念碑解鎖故事篇章。",
    "guide.cocojenna.p.sister_bond": "Sister Bond>80 觸發雙子星連攜。同時撫摸、一起餵食可提升。",
    "guide.cocojenna.p.forge": "鐵爪鍛造舖：鍛造、強化 +0~+10、修理、鑲嵌。高階失敗可能降級。",
    "guide.cocojenna.p.town": "建造光塔、樂園、工坊。招募流浪貓。幸福度 90+ 有驚喜禮物。/village 查看狀態。",
    "guide.cocojenna.p.four_mad": "四狂：斑鳩、三花子、阿修羅、大橘。各有戰鬥與和平攻略。",
    "guide.cocojenna.p.final": "影爪三階段戰。可救贖或肅清。雙子星共鳴可在瀕死時重燃戰意。",
    "guide.cocojenna.p.appendix": "可可日記與珍奶塗鴉。阿爾法：謝謝你。PS：午夜塔頂，可可有話想說——別告訴她是我說的。",
}

for lang, pages in [("zh_tw.json", PAGES_TW), ("zh_cn.json", {k: v.replace("絨", "绒").replace("閱", "阅") for k, v in PAGES_TW.items()})]:
    p = ROOT / lang
    data = json.loads(p.read_text(encoding="utf-8"))
    data.update(pages)
    p.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
print("added", len(PAGES_TW), "fallback pages")
