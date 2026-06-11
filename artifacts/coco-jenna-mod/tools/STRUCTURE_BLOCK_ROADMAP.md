# Structure Block 手建路線圖（Wave 3 觀察）

對照《貓之國相關設計書》與現有程序生成資產，供美術／後續 sprint 分工。本文件**不阻塞**程式 build。

## 現況基線

| 類別 | 程式錨點 | 現有 NBT / 生成 | 與設計書差距 |
|------|----------|-----------------|------------|
| 初啼村 | `FirstCryStructurePlacer.java` | `gen_loot_and_ruin_nbt.py` 8×~17³ 區塊 | 設計 120×100 圓形、8 區精裝 |
| 9 優先遺跡 | `RuinInteractionRegistry.java` | tier1 程序平台 + lectern | 設計 23×17×11 多層內裝 |
| 6 陵墓 | `MausoleumVariant.java` | shell/core 程序占位 | 需手建風格差異 |

---

## 1. 初啼村（設計書：120×100）

### 1.1 核心四區對照

| 設計區 | 設計書章節要點 | 現有 NBT / 生成函式 | 目標尺寸 | Structure Block 步驟 |
|--------|----------------|---------------------|----------|----------------------|
| 聖樹核心 | 樹洞 `purr_crystal`、Y=0–35 | `FirstCrySacredTreeBuilder` | 直徑 ~35 柱 | 1) 遊戲內建 35 高聖樹 2) 樹洞內放 purr_crystal 3) 匯出 `first_cry_sacred_tree.nbt` |
| 市場區 | 攤位、貓窩簇 | `first_cry_market.nbt`（程序） | ~25×25 | 匯出後替換 OFFSET 錨點 |
| 市長廳 | 會議、石碑 | `first_cry_mayor_hall.nbt` | ~20×18 | lectern 對齊 `FirstCryVillageNpcHandler` |
| 外環聚落 | 8 區 OFFSET 放置 | `FirstCryStructurePlacer.OFFSETS` | 8×17³ | 每區獨立匯出，更新 OFFSETS 表 |

### 1.2 與地牢對齊

- `ElderCellarGenerator` 枯井座標需與聖樹 NBT 樹根 `(x,z)` 一致（設計書 elder_cellar 章節）。
- 匯出時在結構 JSON 註記 `cocojenna:elder_cellar_entrance` 方塊錨點。

### 1.3 優先級

1. 聖樹 + 樹洞（玩家第一印象）
2. 市場 + 市長廳（任務 NPC）
3. 其餘 6 外環區

---

## 2. 九優先遺跡（設計書：遺跡矩陣）

| 遺跡 ID | 設計尺寸 | tier1 函式 (`gen_loot_and_ruin_nbt.py`) | lectern 世界座標約定 | 二層骨架（本輪程序） |
|---------|----------|----------------------------------------|----------------------|----------------------|
| `outpost` | 23×17×11 | `tier1_outpost()` | 一層 `(0,2,1)`、二層 `(0,5,0)` | ✅ 已加宿舍地板 + 指揮室 |
| `war_ruins` | 多層廢墟 | `tier1_war_ruins()` | `(-1,1,1)` | 待加二層角樓 |
| `scratching_barricade` | 防線 | 平台占位 | TBD | 低 |
| `velvet_tower` | 高塔 | `tier1_velvet_tower()` | `(1,2,1)` | 已有 8 格塔身 |
| `mortar_position` | 砲位 | 平台占位 | TBD | 低 |
| `moon_sealed_dungeon` | 地下 | `tier1_moon_dungeon()` | `(0,1,0)` end_rod | 待加深層 |
| `black_mud_contaminated_temple` | 神殿 | `tier1_mud_temple()` | `(2,1,1)` | 待加側室 |
| `ironpaw_forge_ruins` | 鍛造 | `tier1_ironpaw_forge()` | `(0,1,2)` | 待加煙囪層 |
| `forgotten_altar` | 祭壇 | `tier1_forgotten_altar()` | `(0,1,-1)` | 待加圍欄 |

### Structure Block 匯出流程（通用）

1. 在創意模式用結構方塊框選，含 lectern 與寶箱。
2. 檔名：`data/cocojenna/structures/ruin_<id>.nbt`。
3. 更新 `RuinInteractionRegistry.tryRuinLore` 的 lectern 相對座標表。
4. 執行 `./gradlew validateRuinNbt`。

---

## 3. 六陵墓（`MausoleumVariant` / `MausoleumType`）

| 類型 | 設計差異 | 現有 shell/core | 手建優先級 |
|------|----------|-----------------|------------|
| SLEEPING_CHAMBER | 棺槨 + 秦可沐線 | 程序石室 | P0 |
| WEAPON_VAULT | 兵馬俑廊 | 程序占位 | P1 |
| LIBRARY | 竹簡架 | 程序占位 | P2 |
| GARDEN | 地下苑 | 程序占位 | P2 |
| RITUAL_HALL | 儀式圈 | 程序占位 | P1 |
| OUTER_GATE | 門闕 | 程序占位 | P1 |

匯出命名：`mausoleum_<type>_<region>.nbt`，bit 對照 `MausoleumType` 0–5。

---

## 4. 驗收檢查清單

- [ ] 初啼村 4 核心區 NBT 入庫並通過 `validateRuinNbt`
- [ ] 9 遺跡 lectern 座標與 `RuinInteractionRegistry` lore 觸發一致
- [ ] 6 陵墓至少 2 種風格手建完成（SLEEPING + RITUAL 建議）
- [ ] `FirstCryStructurePlacer` OFFSETS 更新為手建尺寸

---

## 5. 相關檔案

- 生成器：`tools/gen_loot_and_ruin_nbt.py`
- 初啼：`world/firstcry/FirstCryStructurePlacer.java`
- 遺跡互動：`world/ruin/RuinInteractionRegistry.java`
- 陵墓：`world/qin/MausoleumVariant.java`
