#!/usr/bin/env python3
"""Emit Patchouli lang keys for guardian guide (zh_tw)."""
from pathlib import Path

# entry_id -> (title, p1, p2 optional)
TEXTS = {
    "patchouli.cocojenna.category.first_cry": ("第一章：初啼村的日常", "村莊、小屋與情感值"),
    "patchouli.cocojenna.category.sequence": ("第三章：序列之道", "源力、技能與晉升"),
    "patchouli.cocojenna.category.map": ("第四章：貓之國地圖", "區域與探索順序"),
    "patchouli.cocojenna.category.memory": ("第五章：記憶與羈絆", "碎片、紀念碑與 Sister Bond"),
    "patchouli.cocojenna.category.forge": ("第六章：鍛造與強化", "鐵爪鍛造舖與強化"),
    "patchouli.cocojenna.category.town": ("第七章：城鎮復興", "建造、招募與幸福度"),
    "patchouli.cocojenna.category.four_mad": ("第八章：絨都四狂", "傳說挑戰與和平路線"),
    "patchouli.cocojenna.category.final": ("第九章：終焉之塔", "影爪與結局"),
    "patchouli.cocojenna.category.appendix": ("附錄：悄悄話", "可可日記與珍奶塗鴉"),
    "patchouli.cocojenna.entry.meet_jenna": ("與珍奶的第一次見面", "玳瑁色的貓永遠停不下來。陪她玩，當她露出肚子時——她信任你了。$(br2)她們是姐妹。黑貓守護，玳瑁陪伴。她們只需要你在這裡。", "珍奶塗鴉：一隻歪歪扭扭的蝴蝶"),
    "patchouli.cocojenna.entry.basics": ("基礎操作", "右鍵撫摸、蹲下抱起、餵食梳毛玩耍。WASD 移動，雙擊 Shift 閃避，Alt 技能圓盤。", "珍奶塗鴉：一條魚，寫著「好吃！」"),
    "patchouli.cocojenna.entry.alpha_rules": ("這個世界的規則", "黑泥是悲傷的凝結；記憶是力量；雙子星是夥伴；失敗不會真正失去她們。", "可可：阿爾法總是說太多。"),
    "patchouli.cocojenna.entry.first_cry_village": ("初啼村——第一個家", "生命線球樹是起點。村長老絨尾、商店貓小麥。樹根小屋是可可、珍奶與你的家。", "珍奶塗鴉：樹下三個火柴人"),
    "patchouli.cocojenna.entry.morning": ("小屋的早晨", "可可賴床，珍奶叼玩具叫醒你。兩個貓床、壁爐、食物碗與儲物箱。", "可可：我沒有賴床。我只是在思考。"),
    "patchouli.cocojenna.entry.bond": ("情感值", "撫摸、餵食、玩耍、並肩作戰提升羈絆。達到階段解鎖新互動——不要著急。", "珍奶：我喜歡你！"),
    "patchouli.cocojenna.entry.gathering": ("採集與資源", "絨毛草、月光石、薄荷花、貓薄荷。初啼村商店可購工具，工坊提升效率。", "可可：工具記得修理。"),
    "patchouli.cocojenna.entry.what_is_mud": ("什麼是黑泥？", "黑泥不是怪物。它們是沒能說出口的再見，是眼淚的凝結。記住它們，讓它們安息。", "可可：……"),
    "patchouli.cocojenna.entry.erosion": ("黑泥的侵蝕", "四階段：沾染→蔓延→吞噬→深淵化。肉球印章、光塔、擊敗首領可阻止。", "珍奶：髒髒！"),
    "patchouli.cocojenna.entry.parasite": ("寄生", "四階段腐蝕：嗜睡、抗拒、同化邊緣、完全腐蝕。定期檢查雙子星毛色是否出現灰斑。", "可可：我不會讓這發生在珍奶身上。"),
    "patchouli.cocojenna.entry.bestiary": ("黑泥圖鑑", "失溫者、遺忘之影、低語泥偶、模仿者、盲水領主、原始混沌。遭遇後解鎖完整圖鑑。", "可可：序列3以上不要單挑。"),
    "patchouli.cocojenna.entry.what_sequence": ("什麼是序列？", "序列9到序列1的力量體系。每10級晉升選卡牌。槽位隨序列增加。", "珍奶：變強！"),
    "patchouli.cocojenna.entry.three_paths": ("三大源力", "呼嚕共鳴（防禦治癒）、夜瞳暗影（暴擊潛行）、混沌惡作劇（機率閃避）。", "可可：我選呼嚕。"),
    "patchouli.cocojenna.entry.skill_wheel": ("技能圓盤", "按住 Alt 展開圓盤，滑鼠選槽位後放開施放。可連續施放。", "珍奶畫了追尾巴的圓圈"),
    "patchouli.cocojenna.entry.promotion": ("晉升", "三選一永久卡牌。踩奶節奏、安撫領域、呼嚕球等——選最適合風格的。", "可可：想清楚再選。"),
    "patchouli.cocojenna.entry.world_map": ("貓之國全圖", "11 區域：初啼村至遺忘高塔。中央紀念碑為樞紐。", "珍奶在終點畫了一條魚"),
    "patchouli.cocojenna.entry.gear_town": ("齒輪鎮", "工業心臟。鐵爪鍛造、礦坑。夜晚黑泥襲擊需協防。", "可可：鐵爪人很好。"),
    "patchouli.cocojenna.entry.fragments": ("記憶碎片", "黑勾玉（可可）、橘爪印（珍奶）、金王冠（國度）、藍愛心（你的記憶）。", "珍奶：我也有故事喔！"),
    "patchouli.cocojenna.entry.monument": ("記憶紀念碑", "解鎖故事篇章。50 碎片後塔頂結晶點亮，迎來永久黎明。", "可可：有些記憶我寧願忘記。"),
    "patchouli.cocojenna.entry.sister_bond": ("Sister Bond", "姊妹羈絆>80 觸發雙子星連攜。同時撫摸、一起餵食可提升。", "珍奶：姐姐最好了"),
    "patchouli.cocojenna.entry.iron_claw": ("鐵爪的鍛造舖", "鍛造、強化、修理、鑲嵌。傳說武器需設計圖與儀式。", "可可：武器是夥伴。"),
    "patchouli.cocojenna.entry.enhance": ("武器強化", "+0 至 +10，成功率遞減。高階失敗可能降級。準備護符再挑戰。", "珍奶：砰！"),
    "patchouli.cocojenna.entry.build": ("建設家園", "小屋、光塔、樂園、工坊、花園。每座建築可升 3 級。", "珍奶的夢想樂園設計圖"),
    "patchouli.cocojenna.entry.recruit": ("招募村民", "流浪貓 + 空小屋 + 食物。入住後依特長選職業。", "可可：善待牠們。"),
    "patchouli.cocojenna.entry.happiness": ("幸福度", "床位、食物、裝飾、雙子星在村、防禦成功提升。90+ 有驚喜禮物。", "珍奶：大家都開心！"),
    "patchouli.cocojenna.entry.four_mad_legend": ("四狂的傳說", "斑鳩、三花子、阿修羅、大橘。挑戰影爪前需獲認可。", "可可：不想和阿修羅打。"),
    "patchouli.cocojenna.entry.four_mad_banjou": ("暴雨斑鳩", "帶珍奶撒嬌可和平收服。成為天氣管理員。", ""),
    "patchouli.cocojenna.entry.four_mad_sanwa": ("鐵算盤三花子", "幾何解謎對決。薛丁格玩具錘可破計算。", ""),
    "patchouli.cocojenna.entry.four_mad_ashura": ("無影阿修羅", "全程隱身。為可可擋致命一擊可獲認可。", ""),
    "patchouli.cocojenna.entry.four_mad_orange": ("睡神大橘", "無法傷害。頂級料理香氣讓他翻身讓路。", "珍奶：好吃到醒來！"),
    "patchouli.cocojenna.entry.shadow_claw": ("篡位者的故事", "影爪——叔父。黑泥注入心臟，悲傷無法被馴服。", "可可：我們會帶你回家。"),
    "patchouli.cocojenna.entry.prepare_final": ("準備最終決戰", "武器+5、防具套裝、聖水、雙子星情感>80、Sister Bond>70。", "珍奶：三個人一起，不怕！"),
    "patchouli.cocojenna.entry.final_phases": ("戰鬥階段", "墮落將軍→悔恨叔父（可對話救贖）→完全墮落→雙子星共鳴。", "可可：無論選什麼我都不怪你。"),
    "patchouli.cocojenna.entry.endings": ("結局", "救贖：影爪化封印物。肅清：天空放晴。雨停了，她們不再離開。", "珍奶：雨停了！"),
    "patchouli.cocojenna.entry.coco_diary": ("可可的日記", "「魚還不錯，讓他摸頭。」「珍奶頭上頂著花，很可愛。」「滿月，謝謝你。」", "不要告訴珍奶"),
    "patchouli.cocojenna.entry.jenna_doodles": ("珍奶的塗鴉牆", "鮭魚、皇冠守護者、姐姐與我、永遠一起、謝謝你來找我們。", "可可：笨蛋，我會收著。"),
    "patchouli.cocojenna.entry.alpha_letter": ("阿爾法的最後一頁", "貓之國因你迎來雨後黎明。午夜塔頂，可可有話想說——別說是我說的。", "可可：阿爾法！！！！"),
}

def main():
    lines = []
    for key, parts in TEXTS.items():
        lines.append(f'  "{key}": "{parts[0]}",')
        if len(parts) > 1 and parts[1]:
            lines.append(f'  "{key}.desc": "{parts[1]}",' if key.startswith("patchouli.cocojenna.category") else f'  "{key}.p1": "{parts[1]}",')
        if len(parts) > 2 and parts[2]:
            lines.append(f'  "{key}.p2": "{parts[2]}",')
    out = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang/guardian_guide_patchouli_zh_tw.json.fragment"
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print("Wrote", out, len(lines), "lines")

if __name__ == "__main__":
    main()
