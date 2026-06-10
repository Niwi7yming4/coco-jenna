# 🐾 Coco & Jenna: Memories of the Cat Kingdom — 完整模組分析文件（v3.0）

> **模組 ID**: `cocojenna`  
> **Forge 版本**: 1.20.1-47.2.0  
> **開發框架**: Minecraft Forge (Mojang mappings)  
> **依賴**: Patchouli (選擇性，用於守護者指南書)  
> **核心主題**: 貓之國的兩隻命運之貓 — 可可 (Coco) 與 珍奶 (Jenna) 的陪伴、探索、戰鬥與王國重建  
> **最後更新**：2026-06-07 | 文件版本：v3.0（v2.0 序列晉升／戰鬥特效 + v3.0 貓之國再深化：12 生態域、貓社會、NBT 地標、王宮祭典七階段、MPS 職業綁定）

### 2.7 羈絆資料同步 (Data Synchronization)
模組使用 Forge Capability 系統掛載 `BondData` 於玩家實體。為了確保 UI 與渲染同步，採用了以下機制：
- **懶加載同步**：僅在玩家登入或切換維度時同步全量資料。
- **增量同步**：透過 `SyncBondDataPacket` 僅在數值變動（如情感 +1）時傳送特定欄位。
- **執行緒安全**：所有網路處理邏輯皆封裝於 `context.enqueueWork()`，避免存取非安全執行緒的 Level 資料。

```java
// BondData 序列化範例
@Override
public CompoundTag serializeNBT() {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt("felineTier", this.felineTier);
    nbt.putInt("emotion", this.emotion);
    nbt.putLong("lastPromotionTick", this.lastPromotionTick);
    // v2.0 新增：記錄待處理的提升卡
    ListTag cards = new ListTag();
    this.pendingCards.forEach(id -> cards.add(IntTag.valueOf(id)));
    nbt.put("pendingCards", cards);
    return nbt;
}
```

### 3.10 儀式狀態機邏輯
`PromotionCeremonyHandler` 維護一個內部的階段計時器。當玩家進入 `RESONANCE` 階段時，伺服器會計算一個結束時間戳記：

```java
// 伺服器端計時邏輯
public void tick(ServerPlayer player) {
    BondData bond = getBond(player);
    if (bond.getCeremonyStage() != Stage.NONE) {
        if (player.level().getGameTime() > bond.getTimeoutTick()) {
            interruptCeremony(player, bond, "儀式能量不穩定導致崩解...");
        }
    }
}
```

#### 4.10 技術實作範例：L4 終極層粒子
```java
// CombatVfxHelper.java 實作片段
public static void sequenceUltimate(Level level, Player player, ForceType force, int tier) {
    if (level.isClientSide) {
        // 觸發螢幕邊框與震動
        ClientPacketHandler.triggerUltimateVignette(force);
        
        // 根據源力生成主題粒子 (以極座標分佈)
        BlockPos pos = player.blockPosition();
        for (int i = 0; i < 360; i += 10) {
            double rx = Math.cos(Math.toRadians(i)) * 3;
            double rz = Math.sin(Math.toRadians(i)) * 3;
            level.addParticle(getForceParticle(force), pos.getX() + rx, pos.getY() + 1, pos.getZ() + rz, 0, 0.2, 0);
        }
        // 播放 L4 專屬音效
        CombatSoundHelper.playUltimateSound(player, force);
    }
}
```

### 21.5 v2.0 穩定性建議

*   **計時器漂移補償**：在 `CeremonyHudOverlay` 中，應使用伺服器傳來的 `endTimestamp` 減去當前系統時間，而非依賴客戶端 tick 自減，以防止因 FPS 波動造成的進度條不同步。
*   **渲染快取優化**：`PromotionMarkRenderer` 應在類別初始化時預先加載 `mark_tier_1.png` 等資源，嚴禁在 `onRenderPlayer` 方法中調用任何 IO 或資源定位運算。

---

## 📦 擴展：Capability 序列化結構

```java
// BondData.java 核心結構
public class BondData {
    private int felineTier = 9; // 9 到 1
    private SequenceForce forceType = SequenceForce.RESONANCE;
    private Map<Integer, Boolean> unlockedMemories = new HashMap<>();
    
    // v2.0 新增序列卡牌庫
    private final Set<Integer> activePromotionCards = new HashSet<>();
}
```

---

## 📑 目錄

1. [模組概述](#1-模組概述)
2. [核心玩法系統](#2-核心玩法系統)
3. [序列晉升系統（五階段沉浸式）](#3-序列晉升系統五階段沉浸式) ⭐ v2.0
4. [三源力戰鬥特效系統](#4-三源力戰鬥特效系統) ⭐ v2.0
5. [儀式特效與印記渲染](#5-儀式特效與印記渲染) ⭐ v2.0
6. [維度與世界生成](#6-維度與世界生成)
7. [實體大全](#7-實體大全)
8. [物品與裝備系統](#8-物品與裝備系統)
9. [區塊系統與環境機制](#9-區塊系統與環境機制)
10. [黑泥腐化系統](#10-黑泥腐化系統)
11. [經濟與聲望系統](#11-經濟與聲望系統)
12. [任務與進度系統](#12-任務與進度系統)
13. [終局內容：雨後的絨尾之鄉](#13-終局內容雨後的絨尾之鄉)
14. [地下貓域 DLC](#14-地下貓域-dlc)
15. [戰鬥與技能系統](#15-戰鬥與技能系統)
16. [狀態效果一覽](#16-狀態效果一覽)
17. [網路通訊層（v2.0 擴充）](#17-網路通訊層v20-擴充) ⭐ v2.0
18. [小遊戲系統](#18-小遊戲系統)
19. [玩法攻略](#19-玩法攻略)
20. [模組前途與未來方向](#20-模組前途與未來方向)
21. [待打磨清單](#21-待打磨清單)
22. [Blockbench 劣勢避免策略](#22-blockbench-劣勢避免策略)
23. [v1.0 → v2.0 變更摘要](#23-v10--v20-變更摘要) ⭐ v2.0
24. [貓之國再深化系統（v3.0）](#24-貓之國再深化系統v30) ⭐ v3.0

---

## 1. 模組概述

### 1.1 故事背景

你在一個雨天遇見了兩隻流浪貓——**可可 (Coco)** 與 **珍奶 (Jenna)**。可可是一隻純黑帶深藍光澤、琥珀金眼睛的沉默守護者；珍奶是一隻玳瑁色、亮檸檬黃眼睛的活潑陪伴者。隨著與牠們建立羈絆，你將發現牠們與傳說中的**貓之國**有著古老的聯繫，並逐步揭開貓之國被「黑泥」腐化的真相。

### 1.2 核心循環

```
探索世界 → 收集記憶碎片 → 解鎖貓之回憶 → 提升覺醒等級
    ↑                                                │
    │                                                ↓
    └──── 陪伴養成 ← 建立羈絆 ← 完成任務 ← 挑戰BOSS
```

### 1.3 兩大女主角

| 特性 | 可可 (Coco) | 珍奶 (Jenna) |
|------|------------|-------------|
| 貓種 | 黑貓 | 玳瑁貓 |
| 毛色 | 純黑 #1a1a1a, 深藍光澤 #2a2a3a | 黑 #2d1c15, 橘 #c96823, 棕 #8b5a2b |
| 眼睛 | 琥珀金 #ffbf00 | 亮檸檬黃 #e6ff00 |
| 特徵 | 尾巴尖端純白 | 左耳尖端三角形缺口 |
| HP | 50 | 35 |
| 速度 | 0.33 | 0.38 |
| 攻擊力 | 5.0 | 3.5 |
| 核心數值 | 保護慾 / 月亮親和 | 玩心 / 好奇心 / 滿足感 |
| 初始情感 | 60 / 40 | 85 / 80 / 60 |
| 角色定位 | 沉默守護者 | 活潑陪伴者 |

### 1.4 三大序列與三源力（v2.0 補充）

| 序列 | 主題 | 對應源力 | 顏色 |
|------|------|---------|------|
| A-G | 貓之國 7 隱藏序列 | 共鳴/暗影/混沌 | 0xFFD700/0x9B30FF/0xFF69B4 |

三源力（**v2.0 設計書 第二章/第三章**）決定了：
- 晉升儀式特效的視覺表現
- 戰鬥特效（技能/暴擊/狀態/終極層）
- 印記渲染的紋路與發光

---

## 2. 核心玩法系統

### 2.1 三軌成長系統 (Three-Track Growth)

#### 軌道一：情感線 (Emotion) 0-100
- STRANGER (0-9) / CURIOUS (10-24) / ATTACHED (25-44) / BONDED (45-69) / DEVOTED (70-89) / TRANSCENDENT (90-100)

#### 軌道二：獨立性 (Independence) 0-100
- ALWAYS_FOLLOWS (0-19) / OCCASIONAL (20-39) / AUTONOMOUS (40-59) / INDEPENDENT (60-79) / TEMPORARY_AWAY (80-94) / FREE_ROAM (95-100)

#### 軌道三：覺醒線 (Awakening) 0-50
- 對應記憶碎片數量
- 覺醒 ≥ 3：解鎖魚群召喚
- 覺醒 ≥ 4：解鎖空間拉回

### 2.2 姊妹羈絆 (Sister Bond) 0-100
- 初始 65，影響協同行為
- **v2.0 設計書 3.5**：> 80 觸發「雙子星連攜」終極效果

### 2.3 可可專屬行為

| 行為 | 條件 | 效果 |
|------|------|------|
| 抿你一口 | 情感 ≥ BONDED + 靜止 5 秒 | 獲得 COCOS_MARK，每日 3 次 |
| 高處凝視 | 夜晚 + 月相 > 半月 | 月亮親和 + |
| 靜止守護 | 玩家 HP < 40% | 獲得 AT_EASE |
| 空間拉回 | 覺醒 ≥ 4 + 墜落 HP < 10% | 傳送到安全點 + 緩降 |
| 日光浴夥伴 | 終局後白天 | 獲得 WARM_SERENITY |
| 額頭碰觸 | 終局後蹲下對視 5 秒 | 獲得 MIND_SYNC |
| 尾巴纏繞 | 終局後依戀值 > 70 | 獲得 REMEMBERED (10 分鐘) |

### 2.4 珍奶專屬行為

| 行為 | 條件 | 效果 |
|------|------|------|
| 舔腳踝 | 情感 ≥ ATTACHED + 靜止 8 秒 | 獲得 JENNAS_CARE，每日 5 次 |
| 肚子枕頭 | 玩家躺下 + 情感 ≥ BONDED | 舒適效果 |
| 尾巴偷襲 | 可可閒置 + Bond > 40 + 玩心 > 60 | 姊妹羈絆 +1 |
| 魚群召喚 | 覺醒 ≥ 3 + 玩家釣魚 | 召喚魚群 |
| 二公主的好奇 | GUI + 好奇心 > 70 | 窺視效果 |
| 蝴蝶追逐 | 終局後白天戶外 | 情感提升 |
| 禮物快遞 | 終局後滿足感 > 70 | 隨機贈禮 |
| 邀請玩耍 | 終局後玩家持羽毛 | 滿足感 +10 |

### 2.5 記憶碎片系統

- 取得方式：探索寶箱、擊敗 BOSS、完成任務
- 用途：提升覺醒等級、在記憶熔爐中合成、製作記憶之書、提升紀念碑
- 最大值：50

### 2.6 記憶之書 (Memory Book) 設定

第十章以後解鎖：跟隨距離、親密互動、探索、戰鬥、靜音、技能冷卻顯示、偏好技能欄位

---

## 3. 序列晉升系統（五階段沉浸式） ⭐ v2.0

> **核心類別**：
> - `com.cocojenna.sequence.SequencePromotionHelper`（晉升入口）
> - `com.cocojenna.sequence.PromotionCeremonyHandler`（完整五階段儀式）
> **移植/設計來源**：《抽象深淵：迷因神墜》第一章 1.2、第二章 2.1

### 3.1 概述

序列晉升是本模組最重要的長期目標系統之一。玩家從較高序列開始，逐步降階到序列 1，最終達到「貓之國守護者」的境界。

### 3.2 兩種晉升模式（v2.0 設計書 2.4）

本模組提供 **簡化模式** 與 **完整五階段模式** 兩種晉升流程：

```java
// 檢查晉升條件並依設定啟動
SequencePromotionHelper.tryPromote(player, bond);

// 簡化模式：跳過動畫，直接卡牌選擇
tryPromoteSimplified(player, bond);

// 完整五階段：召喚 → 獻祭 → 共鳴 → 啟示 → 印記
PromotionCeremonyHandler.startSummoning(player, bond);
```

### 3.3 晉升條件表

| 從 → 到 | 玩家等級 | 記憶碎片 | 需試煉階數 |
|---------|---------|---------|----------|
| 9 → 8 | 10 | 3 | - |
| 8 → 7 | 20 | 8 | - |
| 7 → 6 | 30 | 15 | - |
| 6 → 5 | 40 | 22 | - |
| 5 → 4 | 50 | 30 | - |
| 4 → 3 | 60 | 40 | - |
| 3 → 2 | 70 | 50 | 覺醒試煉 ≥ 2 階 |
| 2 → 1 | 80 | 65 | 覺醒試煉 ≥ 3 階 |

### 3.4 完整五階段沉浸式儀式

```
SUMMONING → SACRIFICE → RESONANCE → REVELATION → MARKING
   10 秒         玩家互動           30 秒          3 選 1            永久
```

| 階段 | 持續時間 | 主要內容 | 視覺特效 |
|------|---------|---------|---------|
| SUMMONING | 10 秒 | 阿爾法聲音引導 | stage=NONE→SUMMONING |
| SACRIFICE | 玩家互動 | 消耗：碎片+結晶+途徑結晶 | END_ROD + 途徑祭壇發光 |
| RESONANCE | 30 秒 | 材料化光點→兩貓叫聲→途徑圖案→身體發光 | 聲波/暗影羽毛/紙屑 |
| REVELATION | 客戶端 | 三張 3D 浮空卡牌浮現 | CeremonyCardSelectionScreen |
| MARKING | 永久 | 印記烙印 + 序列 1 NPC 反應 | Flash + 廣播 |

### 3.5 三途徑源力

| 途徑 | 顏色代碼 | 結晶 |
|------|---------|------|
| 🟡 RESONANCE 共鳴 | 0xFFFFD700 | PURR_CRYSTAL |
| 🟣 SHADOW 暗影 | 0xFF9B30FF | SHADOW_CRYSTAL |
| 🩷 CHAOS 混沌 | 0xFFFF69B4 | CHAOS_CRYSTAL |

### 3.6 印記等級系統

| 序列範圍 | 印記等級 | 視覺表現 |
|---------|---------|---------|
| 9-7 | 0 | 無印記 |
| 6-4 | 1 | 微弱光點（手背） |
| 3-2 | 2 | 小型紋路 + 武器光澤 |
| 1 | 3 | 完整紋路 + 黑暗中發光 + NPC 反應 |

### 3.7 簡化晉升模式（v2.0 設計書 2.4）

`bond.isSimplifiedCeremony()` 為 true 時：
- 跳過 SUMMONING/SACRIFICE/RESONANCE 動畫
- 直接進入啟示階段顯示 3 張卡牌
- 仍會消耗記憶碎片並應用印記

### 3.8 完整 API 列表

```java
// SequencePromotionHelper
public static int requiredLevel(int currentTier);
public static int requiredShards(int currentTier);
public static boolean awakeningTrialMet(BondData bond, int currentTier);
public static float cardDamageBonus(BondData bond);
public static void tryPromote(ServerPlayer player, BondData bond);
public static void confirmPromotion(ServerPlayer player, BondData bond, int cardIndex);

// PromotionCeremonyHandler
public static boolean canStartCeremony(ServerPlayer player, BondData bond);
public static void startSummoning(ServerPlayer player, BondData bond);
public static boolean checkSacrificeItems(Player player, BondData bond);
public static void performSacrifice(ServerPlayer player, BondData bond, BlockPos altarPos);
public static void startResonance(ServerPlayer player, BondData bond);
public static void startRevelation(ServerPlayer player, BondData bond);
public static void confirmCardSelection(ServerPlayer player, BondData bond, int cardIndex);
public static int getMarkLevelStatic(int tier);
public static boolean checkTimeout(BondData bond, int currentTick);
public static void interruptCeremony(ServerPlayer player, BondData bond);
public static boolean checkSmallAltar(Level level, BlockPos corePos);
```

### 3.9 中斷與超時

- **超時**（30/60 秒未操作）：stage=NONE，pending=0
- **中斷**（被攻擊）：顯示中斷訊息，**材料不返還**（設計書 2.3）

---

## 4. 三源力戰鬥特效系統 ⭐ v2.0

> **核心類別**：`com.cocojenna.combat.CombatVfxHelper`、`CombatSoundHelper`  
> **設計來源**：《抽象深淵：迷因神墜》設計書 3.1-3.5

### 4.1 系統概述

本系統實現 **「戰鬥特效分層設計」**，根據事件重要性分為 4 層。

### 4.2 L1 基礎層（普通攻擊）

```java
CombatVfxHelper.basicHit(level, target, weaponType);
```

- 武器類型 → 粒子：sword/SWEEP_ATTACK, blunt/CRIT, sonic/PURR_WAVE
- 5 個粒子爆發 + 輕微螢幕震動

### 4.3 L2 技能層（主動技能）

```java
CombatVfxHelper.skillCast(level, player, force, variant, outer);
```

| 途徑 | 特效 | 變體 |
|------|------|------|
| 🟡 RESONANCE | 聲波同心圓 + 音符粒子 | 1: 共鳴護盾 / 2: 暖爐光環 |
| 🟣 SHADOW | 影刃投擲 + 暗影潛行 | 1: 虛空凝視 / 3: 暗夜分身 |
| 🩷 CHAOS | 紙箱投擲 + 液態閃避 | 1: 液態閃避 / 3: 九命試煉 |

### 4.4 L3 暴擊/狀態層

**暴擊特效**：
```java
CombatVfxHelper.critHit(level, target, force, consecutive);
```
- 20 CRIT + 12 ENCHANTED_HIT + 8 SWEEP_ATTACK
- 連續暴擊環繞 16 個對應途徑邊框

**狀態特效**：slow/burn/poison/confusion/mark

**元素反應**：freeze/explosion/corrosion

### 4.5 L4 終極層（序列奧義）

```java
CombatVfxHelper.sequenceUltimate(level, player, force, tier);
```

- 釋放前：畫面邊緣光暈 + 0.3 秒慢動作
- 玩家身體發光 5 秒
- 全螢幕爆發主題粒子

**三途徑終極特效**：
- 🟡 RESONANCE：6 環金色聲波巨浪 + 金色光柱 + 震懾敵方 1 秒
- 🟣 SHADOW：螢幕變暗 + 50 紫色影刃 + 20 紫色閃電
- 🩷 CHAOS：彩虹色調 + 8 爆紙箱星星 + 30 FIREWORK

### 4.6 L4 雙子星連攜（Sister Bond > 80）

```java
CombatVfxHelper.twinStarBond(level, player);
```

- 可可化為黑色影之守護環繞玩家（3 環 SQUID_INK）
- 珍奶化為彩色光球在敵方之間彈跳（5 爆 FIREWORK）
- 結束時兩貓回到玩家身邊回復 20% 生命

### 4.7 L4 Boss 擊殺特效

```java
CombatVfxHelper.bossKillEffect(level, boss);
```
- 60 ENCHANTED_HIT + 50 END_ROD + 30 FIREWORK
- 播放 BOSS 層級音效

### 4.8 L4 區域淨化特效

```java
CombatVfxHelper.regionPurify(level, center);
```
- 5 環 END_ROD + 40 FIREWORK + 20 陽光粒子

### 4.9 戰鬥音效分層

`CombatSoundHelper` 提供 4 層音效：BASE / CRIT / KILL / BOSS

---

## 5. 儀式特效與印記渲染 ⭐ v2.0

> **核心類別**：
> - `com.cocojenna.network.CeremonyEffectPacket`
> - `com.cocojenna.network.CeremonyStagePacket`
> - `com.cocojenna.client.gui.ScreenEffectOverlay`
> - `com.cocojenna.client.gui.CeremonyCardSelectionScreen`
> - `com.cocojenna.client.gui.CeremonyHudOverlay`
> - `com.cocojenna.client.renderer.PromotionMarkRenderer`
> - `com.cocojenna.network.CameraShakePacket`

### 5.1 CeremonyEffectPacket — 9 種儀式特效

```java
public enum EffectType {
    BODY_GLOW,          // 玩家身體短暫變為對應源力顏色
    FLASH,              // 畫面短暫閃白
    PERMANENT_MARK,     // 永久視覺印記
    CARD_SELECTION,     // 三張浮空卡牌選擇
    ULTIMATE_VIGNETTE,  // 終極技螢幕邊框
    SCREEN_SHAKE,       // 螢幕震動
    BOSS_KILL_EFFECT,   // Boss 擊殺特效
    TWIN_STAR_BOND,     // 雙子星連攜
    REGION_PURIFY       // 區域淨化
}
```

### 5.2 同步流程

```
PromotionCeremonyHandler 切階段
  → CeremonyStagePacket(階段)  → CeremonyHudOverlay 更新
  → CeremonyEffectPacket(BODY_GLOW) → PromotionMarkRenderer.triggerBodyGlow
  → CeremonyEffectPacket(CARD_SELECTION) → CeremonyCardSelectionScreen.open
  → CeremonyEffectPacket(PERMANENT_MARK) → PromotionMarkRenderer.setPermanentMark
```

### 5.3 PromotionMarkRenderer

- 監聽 `RenderPlayerEvent.Post`
- 根據印記等級（0-3）渲染不同透明度紋路
- **序列 1**：在黑暗中會微微發光
- 印記顏色：共鳴金 / 暗影紫 / 混沌粉

---

## 6. 維度與世界生成

### 6.1 貓之國 (Cat Kingdom)

- 維度 ID: `cocojenna:cat_kingdom`
- **12 個生態域**（`data/cocojenna/worldgen/biome/` + 標籤 `cat_kingdom`）：

| ID | 中文主題 | 地形調色板重點 | 探索 seed |
|----|---------|--------------|----------|
| `velvet_forest` | 絨林 | 絨毛草、抓板、粉陶 | `ExplorationMarkers.placeVelvetForest` |
| `moon_alley` | 月巷 | 月石簇、爪印玻璃 | `placeMoonAlley` |
| `first_cry_plains` | 初啼平原 | 棉花糖灌木、營火 | `placeFirstCry` |
| `howling_gorge` | 嚎叫峽谷 | 鐘、鐵欄 | `placeHowlingGorge` |
| `blind_water_river` | 盲水河 | 海燈籠、木板碼頭 | `placeBlindPort` |
| `dawn_highlands` | 黎明高地 | 貓薄荷、罌粟壁畫 | `placeDawnHighlands` |
| `forgotten_wastes` | 遺忘荒原 | 黑泥、霓虹菇 | `placeForgottenWastes` |
| `cardboard_slums` | 紙板貧民窟 | 孢子果節點、木桶 | `placeCardboardSlums` |
| `moonlight_beach` | 月光海灘 | 海龜蛋、燈籠棧道 | `placeMoonlightBeach` |
| `rainbow_canyon` | 彩虹峽谷 | 呼嚕結晶塊、彩陶 | `placeRainbowCanyon` |
| `catnip_highlands` | 貓薄荷高地 | 苔蘚、絨毛草 | `CatnipHighlandsManager` |
| `stardust_desert` | 星塵荒漠 | 黑曜石、終界燭 | `StardustDesertManager` |

- **結構放置分級**（`ModEventHandler` chunk decorate）：
  - **大型地標**（`BiomeLargeStructurePlacer` / `BiomeDatapackStructurePlacer`）：每 biome 1 座，讀取 `structures/biome_landmarks/<biome>.nbt`
  - **中型地標**（`BiomeMediumStructurePlacer`，2%）：`medium/cat_village.nbt`、`nine_lives_shrine.nbt`、`velvet_palace_wing.nbt`
  - **棒棒糖樹**（`LollipopTreePlacer`，2%）：絨林／初啼平原／黎明高地
  - **NBT 生成器**：`tools/gen_biome_structure_nbt.py`（Structure Block 相容 gzip，執行後寫入上述路徑）
- **地形混合**：`BiomePaletteMixer` + `CatKingdomTerrainDecorator` 多方塊調色板過渡

### 6.2 地下貓域 (Undercat Domain)

- 維度 ID: `cocojenna:undercat_domain`
- 6 個區域：盲水裂縫 / 齒輪豎井 / 燈塔井 / 聖所池 / 樹洞 / 路石
- DLC 級擴展內容

---

## 7. 實體大全

### 7.1 友善實體（14 種）

coco, jenna, alpha, cheshire, general_cat, samurai_cat, monk_cat, court_lady_cat, sumo_cat, sanhua_weaver, white_glove, undercat_hub_npc, sealed_entity, fur_ball_spirit

### 7.2 敵對實體

**黑泥生物**（7 種）：heat_leech, forgotten_wisp, whispering_doll, memory_moth, mimic_cat, glitch_cat, origami_crow

**BOSS**（13 種）：shadow_claw, blind_water_lord, fallen_velvet, grief_amalgam, primal_chaos, catnip_dragon, tape_colossus, silenced_one, thousand_face_stitcher, corrugata_queen, black_mud_boss, regional_boss, arena_gladiator

---

## 8. 物品與裝備系統

### 8.1 良快刀系統 (Ryokatana)

- 50 把不同變種武器
- 15 大類別：基礎型 / 月光系 / 黑泥系 / 絨尾系 / 齒輪系 / 深水系 / 盲水系 / 特殊系 / 記憶系 / 星塵系 / 紅玉系 / 白手套系 / 三花系 / 紙系 / 霓虹系
- 冷卻 50 ticks (2.5 秒)

### 8.2 大快刀系統 (Daikatana)

- 史詩武器，儀式合成
- 例：daikatana_first_dawn, daikatana_abyss, daikatana_phantom 等

### 8.3 披風系統 (Cloak System)

11 種披風：warm / moonlight / purr / thunder / hibiscus / memory / guardian / traveler / anti_corrosion / eternal / cloak_1~9

### 8.4 套裝系統

- 月光套裝 (Moonlight Armor)
- 絨尾初學者套裝 (Velvet Beginner Armor)

### 8.5 重要物品

記憶碎片、月之石、黑泥、黑泥殘渣、聖水、純淨之淚、生命藥水、貓薄荷、銀藤、星塵戒指、雷霆石、守護者指南、記憶之書、序列徽章、塔羅牌組、陰影幣、滿月硬幣、黃金算盤珠

🆕 **v2.0 新增**：呼嚕結晶 / 暗影結晶 / 混沌結晶

---

## 9. 區塊系統與環境機制

### 9.1 v3.0 新增環境方塊

| 方塊 ID | 類別 | 機制 |
|---------|------|------|
| `velvet_grass` | `VelvetGrassBlock` | 踩踏時櫻花粒子（`stepOn`） |
| `cotton_candy_shrub` | `CottonCandyShrubBlock` | 初啼平原灌木，可互動採集 |
| `neon_mushroom` | 方塊 + worldgen | 遺忘荒原生態裝飾 |
| `spore_fruit_node` | worldgen | 紙板貧民窟孢子果 |
| `fiber_vine` | worldgen | 絨林藤蔓 patch |

### 9.2 黑泥擴散演算法 (Spread Logic)

黑泥不再是簡單的方塊，而是「區塊級災難」。
1.  **資料儲存**：使用 `WorldCapability` 儲存 `Map<ChunkPos, Integer>`。
2.  **擴散權重**：
    - 相鄰區塊腐化度 > 2：擴散機率 +20%。
    - 滿月：擴散判定頻率由 3 日改為 1.5 日。
    - 降雨（盲水之雨）：擴散速度 +15%。
3.  **視覺呈現**：
    - 階段 1-2：地表出現黑泥微粒，草地轉變為暗紫色。
    - 階段 3-4：生成 `black_mud` 覆蓋層方塊，並開始刷出 `glitch_cat`。

### 10.2 性能優化建議
為了避免大地圖下的 Map 查詢開銷，建議實作 `BlackMudTracker`：
```java
// 使用 fastutil 減少裝箱開銷
private final Long2IntOpenHashMap corruptionMap = new Long2IntOpenHashMap();

public void updateChunk(ChunkPos pos, int level) {
    if (level <= 0) corruptionMap.remove(pos.toLong());
    else corruptionMap.put(pos.toLong(), level);
}
```

### 10.3 盲水之雨 (Blind Water Rain)
觸發時，全維度天空變為深藍色（#0A0A2A），所有未穿戴「抗腐蝕披風」的玩家每 200 ticks 獲得一次 `corrosion_mark`。

### 9.3 月相影響

- 滿月：黑泥擴散 ×2
- 可可高處凝視在月相 > 半月時觸發

---

## 10. 黑泥腐化系統

### 10.1 生態系統
- 悲傷之海：黑泥生成率提升
- 道路表面不刷怪

### 10.2 區塊方塊
- black_mud / erosion_black_mud

### 10.3 NPC 腐化系統
- NPC 視覺變化、可被淨化

---

## 11. 經濟與聲望系統

### 11.1 多重貨幣（6 種）

記憶碎片 / 呼嚕水晶 / 陰影幣 / 滿月硬幣 / 黑傑克籌碼 / 黃金算盤珠

### 11.2 五大區域聲望

齒輪鎮 / 王室 / 黎明 / 盲水港 / 初啼（各 0-100）

### 11.3 貓薄荷市場

種植 → 收成 → 市場出售

### 11.4 商店系統（4 種）

良快刀商店 / 聲望商店 / 黑市 / 走私者黑市

---

## 12. 任務與進度系統

### 12.1 主線任務：初啼任務

7 個階段（0-6）：初始 → 進入貓之國 → 收集碎片 → 擊敗影爪 → 取得大快刀 → 完美對稱 → 第一次黎明

### 12.2 序列系統（A-G）

7 個隱藏序列：初始 / 探索 / 記憶 / 羈絆 / 覺醒 / 命運 / 終局

### 12.3 覺醒試煉

0-4 階，擊殺指定數量敵人

### 12.4 地下貓域日常任務

每日重置，類型：委託 / 試煉 / 競技場

### 12.5 成就 (Advancements)

主要進度樹 + 守護者指南進度

### 12.6 提升卡系統 v2.0

- 卡牌收集 / 戰鬥卡、技能卡
- 層級 1-9
- 每張卡提供 bonus 屬性

---

## 13. 終局內容：雨後的絨尾之鄉

### 13.1 觸發條件

完成主線「第一次黎明」後：
- 所有黑泥腐化清除
- 兩貓數值轉換
- 解鎖王國管理

### 13.2 王國管理四大數值

繁榮度 / 幸福感 / 穩定性 / 聲望

### 13.3 職階與貓社會（v3.0 擴充）

**18 種貓職業**（`CatProfessionRegistry`）：漁夫、園丁、廚師、工匠、行商、學者、藝人、衛士、建築師、抄寫員、藥草師、織匠、斥候、吟遊詩人、煉金貓、拾荒者、養蜂貓、星象師

**貓社會子系統**：
| 類別 | 核心類別 | 功能 |
|------|---------|------|
| 名字池 | `CatNpcNamePool` | 200+ 隨機貓名 |
| 職業分配 | `CatProfessionRegistry` | MCA 風格職業 + 文化標籤 |
| 人生事件 | `CatLifeEventManager` | 婚嫁、懷孕、幼貓（`BondData.familyLifeEvents`） |
| 夢境 | `CatDreamManager` | 夜間隨機敘事 stub |
| 路邊幼貓 | `KingdomStrayCatManager` | 終局後每 6000 tick 嘗試生成流浪幼貓 |

### 13.4 MPS 生產排程與職業綁定（v3.0）

- 28 天 × 4 週 × 每天 4 時段，任務類型見 `MpsTask`
- **`ProfessionBuildingBinder`**：9 建築 ↔ 18 職業 ↔ MPS 任務雙向綁定
  - 建築加成：已放置建築對應任務 **+20%/座**
  - `MpsProductionManager.buildingTaskMultiplier()` 套用加成
  - `AfterRainKingdomManager.applyFestivalPreset()` 改用 `generateMpsPreset()` 動態生成
- **Web Hub 雙向同步**：`KingdomHubFallbackScreen` → `WebUiActionPacket` → `AfterRainKingdomManager`

### 13.5 節慶系統（七階段，v3.0）

`FestivalEventManager` 滿月祭典時間軸（取代舊版 5 階段描述）：

| 階段 | 常數 | 持續 tick | 內容 |
|------|------|----------|------|
| 0 | `PHASE_IDLE` | — | 閒置 |
| 1 | `PHASE_SETUP` | 2400 | 黃昏布置 |
| 2 | `PHASE_OPENING` | 2400 | 入夜開幕 |
| 3 | `PHASE_DANCE` | 3600 | 夜晚跳舞 + 對話 |
| 4 | `PHASE_COOKING` | 4800 | 午夜料理 |
| 5 | `PHASE_FIREWORKS` | 3600 | 深夜煙火粒子 |
| 6 | `PHASE_WISHING` | 2400 | 黎明許願 |
| 7 | `PHASE_ENDED` | — | 結束 |

**王宮聯動**：`PalaceFestivalBridge` 在階段推進時觸發王宮粒子／互動；`PalaceRegionManager` 優先處理祭典互動（王座／花園／圖書館／軍營／中庭）

**村莊獨立節日**：`VillageFestivalManager` + `VillageCultureManager`；滿月祭期間（`festivalPhase` 進行中）暫停村莊節日

### 13.6 建築 / 法令 / 烹飪 / 圖書館 / 夜晚秘密事件

---

## 14. 地下貓域 DLC

### 14.1 概述

獨立故事線 + 派系系統 + 競技場 + 每日任務

### 14.2 派系（4 種）

盲水派 / 齒輪派 / 紙板派 / 陰影派

### 14.3 競技場 / 貓薄荷種植 / 河流航行

---

## 15. 戰鬥與技能系統

### 15.1 戰鬥基礎

可可/珍奶協助戰鬥，可透過記憶之書設定

### 15.2 技能輪盤

40+ 種技能圖示，可設定偏好

### 15.3 隱藏序列技能

各序列解鎖不同被動/主動技能

---

## 16. 狀態效果一覽

| 效果 ID | 名稱 | 效果 |
|---------|------|------|
| cocos_mark | 可可之印 | 情感互動 |
| jennas_care | 珍奶的關懷 | 持續恢復 |
| at_ease | 安心 | 防禦提升 |
| warm_serenity | 溫暖寧靜 | 持續恢復 |
| mind_sync | 心靈同步 | 特殊視覺 |
| remembered | 被記住了 | 長期 10 分鐘 |
| corrosion_mark | 腐蝕印記 | 黑泥疊加 |
| black_mud_stage1-4 | 黑泥階段 1-4 | 腐化狀態 |

---

## 17. 網路通訊層（v2.0 擴充） ⭐ v2.0

### 17.1 ModNetwork 註冊的封包（共 50+ 種）

`ModNetwork.CHANNEL` 協議版本 `"1"`，使用 `CHANNEL.registerMessage(id++, ...)` 註冊。

| 分類 | 封包 | 方向 | 用途 |
|------|------|------|------|
| **羈絆同步** | SyncBondDataPacket | S→C | 全量 BondData |
| | IncrementalBondSyncPacket | S→C | 增量欄位同步 |
| | BondSettingsPacket | C→S | 記憶之書設定 |
| | MarkUpdatePacket | S→C | 印記等級更新 |
| **晉升儀式** | OpenPromotionPacket | S→C | 開啟晉升 UI |
| | SelectPromotionCardPacket | C→S | 卡牌確認 |
| | CeremonyEffectPacket | S→C | 9 種儀式特效 |
| | CeremonyStagePacket | S→C | 階段切換 |
| | CeremonySpectatorPacket | S→C | 旁觀者同步 |
| | CameraShakePacket | S→C | 螢幕震動 |
| | ScreenFilterPacket | S→C | 畫面濾鏡 |
| | OpenForceSelectionPacket / ConfirmForceSelectionPacket | S↔C | 源力選擇 |
| **王國終端** | OpenKingdomTerminalPacket | S→C | 王國終端 |
| | OpenKingdomHubPacket | S→C | Web Hub |
| | KingdomDecreeActionPacket | C→S | 法令操作 |
| | MpsSchedulePacket | C→S | MPS 月曆排程 |
| | WebUiActionPacket / WebUiStatePacket | C↔S | Hub 雙向同步 |
| | BuildingContributePacket / BuildingPlacePacket | C→S | 建築貢獻／放置 |
| **戰鬥／技能** | CastSequenceSkillPacket | C→S | 序列技能 |
| | OpenSkillSettingsPacket | S→C | 技能裝備 UI |
| | CatRadialActionPacket | C→S | 貓輪盤互動 |
| **經濟／商店** | BuyRyokatanaPacket / BuyReputationShopPacket | C→S | 武器／聲望商店 |
| | BlackjackGamblePacket | C→S | 黑傑克 |
| | SellCatnipPacket | C→S | 貓薄荷市場 |
| **地下貓域** | OpenUndercatHubPacket / OpenAbyssRunPacket | S→C | Hub／深淵跑 |
| | AbyssRunActionPacket | C→S | 跑酷操作 |
| | OpenRiverVoyagePacket | S→C | 河流航行 |
| **記憶之書** | OpenMemoryBookPacket | S→C | 開啟 GUI |
| | OpenMemoryTheaterPacket 等 | S↔C | 記憶劇場 |
| **其他** | TriggerFirstDawnPacket / MindSyncViewPacket | S→C | 終局事件 |
| | OnboardingHintPacket | S→C | 新手提示 |

### 17.2 封包註冊與設計模式

```java
// ModNetwork.java
public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
    new ResourceLocation(MOD_ID, "main"),
    () -> "1.0",
    "1.0"::equals,
    "1.0"::equals
);

// 統一註冊邏輯
INSTANCE.messageBuilder(CeremonyEffectPacket.class, id++)
    .encoder(CeremonyEffectPacket::encode).decoder(CeremonyEffectPacket::new)
    .consumerMainThread(CeremonyEffectPacket::handle).add();
```

統一遵循：`encode/decode/handle` + `enqueueWork` 確保執行緒安全 + `setPacketHandled(true)`

### 17.3 同步策略

- BondData：每個關鍵節點同步
- 儀式階段：階段切換時立即同步
- 印記：晉升完成後一次性同步

---

## 18. 小遊戲系統

- **黑傑克賭博**：影響世界狀態、極夜、黑泥擴散倍率
- **河流航行**：地下貓域跑酷/避障
- **貓薄荷種植**：品質系統、連續獎勵

---

## 19. 玩法攻略

### 19.1 新手入門流程

1. 探索世界 → 遇見可可與珍奶
2. 撫摸/餵食/互動 → 提升情感
3. 收集材料 → 製作貓之國傳送門
4. 進入貓之國 → 開始主線
5. 探索初啼村 → 接取任務
6. 收集記憶碎片 → 提升覺醒
7. 擊敗影爪 → 取得大快刀
8. 探索各區域 → 擊敗 BOSS
9. 收集 50 良快刀
10. 完成序列任務 → 解鎖能力
11. 觸發第一次黎明 → 終局
12. 管理王國 + 探索 DLC

### 19.2 情感值快速提升

撫摸 (+0.5) / 餵貓食 (+2) / 餵特製餐 (+5) / 梳毛刷 (+1) / 一起戰鬥 (+0.1/殺) / 任務獎勵 (+3~10)

### 19.3 良快刀收集

50 把分散：寶箱 20 / BOSS 10 / 任務 10 / 商店 10

### 19.4 序列晉升攻略

1. 早期序列（9→6）：累積經驗/碎片/擊敗對應 Boss
2. 中序列（6→3）：挑戰覺醒試煉
3. 深序列（3→1）：收集 50 碎片 + 完成試煉
4. 簡化模式：開啟後跳過動畫

### 19.5 經濟策略

初期種貓薄荷 → 中期探寶箱 → 後期聲望 → 終局王國

---

## 20. 模組前途與未來方向

### 20.1 已完成的核心系統

- ✅ 兩隻貓的完整 AI
- ✅ 三軌成長數值
- ✅ 50 良快刀 + 多把大快刀
- ✅ 貓之國 + **12 生態域**（v3.0）
- ✅ 地下貓域 DLC（星光章 ch4–6）
- ✅ 黑泥腐化系統
- ✅ 終局王國管理 + MPS 月曆 + Web Hub 雙向同步
- ✅ 多重商店/經濟
- ✅ 任務/進度/成就
- ✅ 披風/裝備/套裝
- ✅ MCEF 網頁 UI
- ✅ Patchouli 守護者指南
- ✅ **v2.0** 序列晉升五階段沉浸式儀式
- ✅ **v2.0** 三源力戰鬥特效分層
- ✅ **v2.0** 印記渲染系統（3 級）
- ✅ **v2.0** 提升卡系統
- ✅ **v2.0** 9 種儀式特效封包
- ✅ **v3.0** 12 biome 探索 seed + 微型探索 6 種互動
- ✅ **v3.0** NBT 地標生成器 + 大型／中型結構放置器
- ✅ **v3.0** 貓社會（18 職業、名字池、人生事件、路邊幼貓）
- ✅ **v3.0** 職業↔建築↔MPS 雙向綁定
- ✅ **v3.0** 滿月祭典七階段 + 王宮／村莊節日聯動
- ✅ **v3.0** 絨毛草踩踏粒子、棒棒糖樹、棉花糖灌木

### 20.2 潛在發展方向

1. 多人模式優化
2. 更多貓種
3. 跨維度內容
4. UI/UX 完善
5. 音樂/音效
6. 本地化
7. 效能優化

### 20.3 潛在問題

- BondData 資料膨脹
- 世界生成衝突
- 黑泥效能
- Patchouli 相依

---

## 21. 待打磨清單

### 21.1 程式碼層面（工程優化）

- **BondData 解耦**：考慮將 `BondData` 拆分為 `EmotionCap` 與 `SequenceCap`，避免單一類別超過 2000 行（高）
- **黑泥效能**：改用 `it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap` 儲存 ChunkPos 到腐化等級的映射，減少 NBT 讀取開銷（高）
- 封包合併為批次（高）
- AI 行為排程器（中）
- Lombok @Data（中）
- Annotation 自動序列化（中）

### 21.2 遊戲內容

- 新手引導 UI（高）
- 良快刀平衡（高）
- 黑泥視覺回饋（中）
- 可可/珍奶互動多樣性（中）
- BOSS 機制性階段（中）
- **v3.0 stub**：NBT 地標美術替換為手建 Structure Block 版本（中）
- **v3.0 stub**：MCA 戀愛流程完整化、王宮建築細節（中）
- **v3.0 stub**：`EcologyDeepeningManager` 霓虹菇等生態互動深化（低）
- **v3.0 stub**：`CatDreamManager` 夢境敘事完整化（低）

### 21.3 UI/UX

- MCEF 快取機制（高）
- 技能輪盤自訂快捷鍵（中）
- 記憶之書分類標籤（中）

### 21.4 建議新功能

1. 貓咪互動多樣化
2. 貓咪收集圖鑑（類寶可夢）
3. 貓咪競賽（賽跑/跳高/捕獵）
4. 自訂貓裝備（蝴蝶結/圍巾/帽子）
5. 貓咖啡廳經營
6. 節日活動
7. 寵物對戰（表演賽）
8. 拍照系統
9. 貓語翻譯
10. 記憶迴廊 3D 探索

### 21.5 v2.0 穩定性建議

*   **儀式計時器同步**：建議在 `BondData` 中使用 `long ceremonyStartTime` 記錄伺服器 Tick 數，而非每 tick 減 1 的 `int timer`，以避免伺服器 Lag 導致的客戶端與伺服器時間不同步。
*   **印記渲染快取**：`PromotionMarkRenderer` 在 `RenderPlayerEvent` 中應快取 `ResourceLocation`，避免每一幀都進行 `new ResourceLocation(...)` 的物件分配。

---

## 📦 擴展：Capability 序列化結構

```java
// BondData.java 提供給開發者參考
public class BondData implements INBTSerializable<CompoundTag> {
    // 序列相關
    private int felineTier = 9;
    private int markLevel = 0;
    private Set<Integer> ownedPromotionCards = new HashSet<>();
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("felineTier", felineTier);
        nbt.putIntArray("ownedCards", new ArrayList<>(ownedPromotionCards));
        return nbt;
    }
}
```

---

## 22. Blockbench 劣勢避免策略

### 22.1 模型設計原則

```
紋理優先於模型
  ├── 方塊 → Blockstate JSON + 簡單 Cube + 自訂紋理
  ├── 物品 → Item Model JSON (layer0) + 自訂紋理
  ├── 實體 → 繼承原版模型 + 自訂紋理層
  └── GUI → 原版 GUI 系統 + 自訂 2D 紋理
```

### 22.2 具體實施方案

| 組件 | 做法 | 理由 |
|------|------|------|
| 方塊 | BlockModel 或 BlockBench 但限制面數 < 24 | 保持原版風格 |
| 物品 | 99% 使用 2D 紋理 + `item/generated` | 無需 3D 模型 |
| 盔甲 | 原版盔甲紋理層 (`layer_1.png`, `layer_2.png`) | 免模型 |
| 實體 | 繼承 `CatModel` 等原版模型只改紋理 | 避免自訂骨架動畫 |
| 粒子 | `SpriteRegistryEntry` + 自訂紋理 | 粒子系統已優化 |
| GUI | `GuiGraphics` + `ResourceLocation` | 純 2D |

### 22.3 效能監控指標

- 實體數量：每區塊 > 20 需優化
- Block Entity：< 50 個載入中
- 模型面數：總計 < 10,000
- 紋理解析度：16x16，必要時 32x32

### 22.4 渲染強化手段（v2.0）

以 `CombatVfxHelper` 與 `CeremonyEffectPacket` 提供視覺強化：
- L1-L4 戰鬥特效分層
- 9 種儀式特效
- 3 級印記渲染
- 終極技螢幕邊框
- Boss 擊殺特效
- 區域淨化

### 22.5 紋理狀況

- 傳統 16x16/32x32 PNG 方塊/物品紋理
- 可能的客製化實體模型但多基於原版骨架
- 自訂 2D sprite GUI 元素

---

## 23. v1.0 → v2.0 變更摘要 ⭐ v2.0

### 23.1 新增章節

| 章節 | 主題 | 重要性 |
|------|------|--------|
| §3 | 序列晉升系統（五階段沉浸式） | ⭐ 大擴充 |
| §4 | 三源力戰鬥特效系統 | ⭐ 新章節 |
| §5 | 儀式特效與印記渲染 | ⭐ 新章節 |
| §17 | 網路通訊層（v2.0 擴充） | ⭐ 新章節 |
| §23 | v1.0 → v2.0 變更摘要 | ⭐ 新章節 |

### 23.2 新增類別（v2.0）

| 類別 | 路徑 | 用途 |
|------|------|------|
| SequencePromotionHelper | com.cocojenna.sequence | 序列晉升入口 |
| PromotionCeremonyHandler | com.cocojenna.sequence | 完整五階段儀式 |
| PromotionCardCatalog | com.cocojenna.sequence | 晉升卡牌系統 |
| CeremonyEffectPacket | com.cocojenna.network | 9 種儀式特效 |
| CeremonyStagePacket | com.cocojenna.network | 階段同步 |
| CameraShakePacket | com.cocojenna.network | 螢幕震動 |
| ScreenEffectOverlay | com.cocojenna.client.gui | 螢幕特效覆蓋層 |
| CeremonyCardSelectionScreen | com.cocojenna.client.gui | 3D 卡牌選擇 UI |
| CeremonyHudOverlay | com.cocojenna.client.gui | 儀式 HUD |
| PromotionMarkRenderer | com.cocojenna.client.renderer | 印記渲染 |
| CombatVfxHelper | com.cocojenna.combat | 戰鬥特效分層 |
| CombatSoundHelper | com.cocojenna.combat | 戰鬥音效分層 |

### 23.3 設計書對應關係

| 設計書章節 | 模組實現類別 | 狀態 |
|----------|------------|------|
| 第一章 1.2 | SequencePromotionHelper | ✅ 完整 |
| 第一章 1.3 | awakeningTrialMet() | ✅ 完整 |
| 第二章 2.1 | PromotionCeremonyHandler | ✅ 完整 |
| 第二章 2.2 | requiredShards() | ✅ 完整 |
| 第二章 2.3 | getMarkLevelStatic() + 印記效果 | ✅ 完整 |
| 第二章 2.4 | isSimplifiedCeremony() + tryPromoteSimplified() | ✅ 完整 |
| 第三章 3.1-3.4 | CombatVfxHelper (L1-L3) | ✅ 完整 |
| 第三章 3.5 | sequenceUltimate/twinStarBond | ✅ 完整 |
| 第三章 3.6 | CombatSoundHelper (4 層) | ✅ 完整 |
| 第四章 3.4 | BondData.getOwnedPromotionCards() | ✅ 基礎 |
| 第五章 2.4 | OpenPromotionPacket (3 卡牌) | ✅ 完整 |
| 第六章 2.5 | BondData (pending/ceremony state) | ✅ 完整 |

### 23.4 移植共享（與 fallen-abyss）

| 系統 | coco-jenna 實作 | fallen-abyss 實作 |
|------|----------------|------------------|
| 序列晉升 | SequencePromotionHelper | AbyssPromotionCeremony |
| 完整五階段儀式 | PromotionCeremonyHandler | AbyssPromotionCeremony |
| 印記渲染 | PromotionMarkRenderer | （未使用，可加） |
| 戰鬥特效 | CombatVfxHelper (三源力) | （未使用） |
| 儀式封包 | CeremonyEffectPacket | （未使用，本地） |
| 序列被動 | （未實作） | SequencePassiveAbility |
| 器官系統 | （未實作） | OrganPassiveManager 20+20 |

### 23.5 統計數據

- **本模組原始碼總行數**：預估 15,000+ 行
- **新增 v2.0 類別數**：12 個
- **新增 v2.0 封包數**：3 個主要封包
- **新粒子類型**：PURR_WAVE / SHADOW_FEATHER / CHAOS_CONFETTI / VELVET_DRIFT
- **新物品**：3 種結晶 + 提升卡相關

### 23.6 v2.0 完成度

| 系統 | v1.0 | v2.0 | 進度 |
|------|------|------|------|
| 序列晉升 | 框架 | 完整五階段 | +80% |
| 印記系統 | 無 | 3 級渲染 | +100% |
| 戰鬥特效 | 基礎 | 4 層分層 | +200% |
| 儀式封包 | 無 | 9 種特效 | +100% |
| 卡牌選擇 | 2D UI | 3D 客戶端 | +50% |
| 提升卡 | 無 | Tier 1-9 框架 | +70% |
| 印記視覺 | 無 | alpha 漸變發光 | +100% |
| 雙子星連攜 | 無 | L4 終極層 | +100% |

### 23.7 v2.0 → v3.0 變更摘要 ⭐ v3.0

| 設計書章節（貓之國再深化） | 模組實現類別 | 狀態 |
|--------------------------|------------|------|
| 12 biome 地形調色板 | `BiomePaletteMixer`, `CatKingdomTerrainDecorator` | ✅ |
| 探索分級（石碑／壁畫／地牢／隱藏牆） | `ExplorationMarkers`, `BiomeExplorationPlacer` | ✅ |
| 微型探索 6 種 | `KingdomMicroMarkers`, `KingdomMicroInteractHandler` | ✅ |
| NBT 大型／中型地標 | `gen_biome_structure_nbt.py`, `Biome*StructurePlacer` | ✅ 程序生成 |
| 棒棒糖樹 | `LollipopTreePlacer` | ✅ |
| 絨毛草粒子 | `VelvetGrassBlock.stepOn()` | ✅ |
| 棉花糖灌木 | `CottonCandyShrubBlock` | ✅ |
| 貓社會 MCA | `CatNpcNamePool`, `CatProfessionRegistry`, `CatLifeEventManager` | ✅ 框架 |
| 路邊幼貓 | `KingdomStrayCatManager` | ✅ |
| 王宮多區域 | `PalaceRegionManager`, `PalaceFestivalBridge` | ✅ |
| 村莊文化／節日 | `VillageCultureManager`, `VillageFestivalManager` | ✅ |
| 職業↔MPS 綁定 | `ProfessionBuildingBinder`, `MpsProductionManager` | ✅ |
| 滿月祭七階段 | `FestivalEventManager` | ✅ |
| 記憶之書 12 biome | `MemoryBookScreen` lore 分頁 | ✅ |
| 生態深化 stub | `EcologyDeepeningManager` + worldgen patches | 🟡 stub |

---

## 24. 貓之國再深化系統（v3.0） ⭐ v3.0

> **設計來源**：《貓之國再深化.md》  
> **掛載點**：`ModEventHandler`（chunk decorate、玩家 tick、方塊互動）

### 24.1 探索分級體系

```
大型地標 (100%) ── biome_landmarks/<biome>.nbt
    │
中型地標 (2%)  ── medium/{cat_village, nine_lives_shrine, velvet_palace_wing}.nbt
    │
探索 seed    ── 石碑 / 壁畫碎片 / 地牢入口 / 隱藏牆 / 野生怪貓
    │
微型探索 (6) ── 爪印地毯 / 毛線球 / 玩具鼠 / 魚骨 / 貓薄荷 / 呼嚕石（冷卻 6000 tick）
```

**微型探索互動**（`KingdomMicroInteractHandler`）：
| 類型 | 偵測方塊 | 效果 |
|------|---------|------|
| PAW | 紅地毯 | 王室聲望 +1 |
| YARN | 白／紅羊毛 | 機率掉落彩虹毛線球 |
| TOY_MOUSE | 灰色羊毛 | 幸福感 + |
| FISH_BONE | 骨粉塊 | 隨機小獎勵 |
| CATNIP | 貓薄荷方塊 | 狀態增益 |
| PURR_STONE | 月石簇 | 呼嚕粒子 |

冷卻記錄於 `BondData.kingdomMicroCooldown`（`Map<posLong, tick>`）。

### 24.2 環境方塊與生態

| 方塊 | 類別 | 特性 |
|------|------|------|
| `velvet_grass` | `VelvetGrassBlock` | 踩踏櫻花粒子 |
| `cotton_candy_shrub` | `CottonCandyShrubBlock` | 初啼平原裝飾，可採集 |
| `neon_mushroom` | worldgen patch | 遺忘荒原生成 |
| `spore_fruit_node` | worldgen patch | 紙板貧民窟生成 |
| `fiber_vine` | worldgen patch | 絨林生成 |

`EcologyDeepeningManager`：採收互動 stub，掛載於 `ModEventHandler` 方塊右鍵與玩家 tick。

### 24.3 王宮多區域（絨尾城堡）

`PalaceRegionManager.regionAt()` 依 `VelvetTailCastleGenerator.CENTER` 偏移劃分：

| 區域 | 位置條件 | 互動 |
|------|---------|------|
| THRONE | dz < -10 | `ThroneHallManager` |
| GARDEN | dz > 18 | 木槿採集、幸福感 |
| LIBRARY | dx > 10 | 書架／碎片 |
| BARRACKS | dx < -10 | 軍營訓練 |
| COURTYARD | 其他 | 中庭 |

祭典期間 `PalaceFestivalBridge.tryFestivalInteract()` 優先於區域互動。

### 24.4 記憶之書 v3.0 擴充

`MemoryBookScreen` 七分頁：情感 / 記憶 / 王國 / 傳說 / 怪貓 / 日誌 / 設定

- **傳說分頁**：12 biome 對應 `LoreRegistry` 條目
- **王國分頁**：MPS、建築、節慶狀態
- **設定分頁**：`BondSettingsPacket` 同步跟隨距離、戰鬥、技能預設

### 24.5 BondData v3.0 新增欄位

```java
// 微型探索冷卻
Map<Long, Long> kingdomMicroCooldown;
// 貓家庭人生事件（婚嫁、幼貓等）
Set<String> familyLifeEvents;
// 村莊獨立節日上次觸發日
long lastVillageFestivalDay;
// 滿月祭典（原有欄位擴充使用）
int festivalPhase;      // 0-7，見 FestivalEventManager
long festivalStartTick;
```

### 24.6 部署與建置

```powershell
Set-Location artifacts/coco-jenna-mod
.\gradlew.bat build
# 輸出：build/libs/coco-jenna-1.0.1.jar
# 部署至 CurseForge 實例 mods 資料夾
```

NBT 重新生成（可選）：

```powershell
python tools/gen_biome_structure_nbt.py
```

---

## 📝 技術備註

### Capability 資料

玩家資料儲存在 `BondData`（Forge Capability）：
- ~200+ 欄位涵蓋所有關係數值、序列、晉升狀態、卡牌等
- 可序列化為 NBT（persistent）
- v2.0 新增欄位：pendingPromotionTier、ceremonyStage、ceremonyTimeout、markLevel、markForce、pendingCards、simplifiedCeremony 等

### 網路協議

- 使用 Forge `SimpleChannel`
- v2.0 新增 3 種主要封包類型
- 客戶端使用 `enqueueWork` 確保執行緒安全

### 依賴關係

| 依賴 | 類型 | 版本 |
|------|------|------|
| Minecraft | 必要 | 1.20.1 |
| Forge | 必要 | 47.2.0+ |
| Patchouli | 可選 | 最新版 |
| Fallen Abyss | 聯動 | 1.0+ |

### 重要錯誤修復

- v2.0 修正了 `AbyssPromotionCeremony` 中 `setEnding()` 被重用作計時器的問題
- v2.0 為儀式添加 timestamp-based 計時檢查
- v2.0 統一了所有儀式封包的 `enqueueWork` 模式

---

## 📊 與 Fallen Abyss 的設計對應

| 設計書概念 | coco-jenna 實現 | fallen-abyss 實現 |
|----------|----------------|------------------|
| 序列 9→1 | ✅ BondData.felineTier | ✅ FallenAbyssData.sequenceLevel |
| 好感度/羈絆 | ✅ BondData.emotion/independence | ✅ FavorStorySystem |
| 記憶碎片 | ✅ BondData.memoryShards | ✅ MemoryFragmentRegistry (50 個) |
| 源力 (3 種) | ✅ 共鳴/暗影/混沌 | ✅ 深淵/盲水/迷因 |
| 五階段儀式 | ✅ 完整實現 | ✅ 移植實現 |
| 印記 | ✅ 3 級渲染 | ✅ 印記邏輯（無渲染） |
| 提升卡 | ✅ 框架 | （未實作） |
| 器官系統 | （未實作） | ✅ 20+20 種 |
| 融合卡 | （未實作） | ✅ 16 種 |
| 終極融合 | （未實作） | ✅ 19 種 |
| 詛咒卡 | （未實作） | ✅ 15 種 |
| 徵兆 | （未實作） | ✅ 21 種 |

---

## 🐾 模組評分（滿分 10）

| 類別 | 評分 | 說明 |
|------|------|------|
| 創意 | 9.5 | 三軌成長 + 序列 + 晉升儀式設計精良 |
| 內容深度 | 9.0 | 50 良快刀 + 序列 + DLC + 終局王國 |
| 技術實現 | 8.5 | Forge + Capability + 完整網路層 |
| 視覺效果 | 8.0 | v2.0 新增戰鬥特效分層 |
| 音效 | 7.0 | 基本戰鬥音效，未擴充 BGM |
| 性能 | 7.5 | BondData 偏大，黑泥追蹤需優化 |
| 多人支援 | 6.0 | 框架支援但內容多為單人 |
| 文檔 | 9.0 | 守護者指南 + 本分析文件 |
| **整體** | **8.0** | 優秀的貓咪陪伴養成模組 |

---

## 🎯 總結

### 模組核心優勢

1. **三軌成長系統** 創造豐富的培養深度
2. **50 把良快刀** 提供豐富的武器選擇
3. **完整五階段晉升儀式**（v2.0）帶來史詩般的遊戲體驗
4. **三源力戰鬥特效** 增強視覺反饋
5. **三級印記渲染** 提供長期目標成就感
6. **地下貓域 DLC** 延長遊戲壽命
7. **Patchouli 守護者指南** 友善的教程系統

### 主要待改進

1. 多人模式支援
2. 自動保存
3. 更多 NPC 故事
4. 完整實作的提升卡系統
5. MCEF 性能優化

### 移植與聯動

- 與 Fallen Abyss 共享序列晉升設計
- 可無縫聯動（盲水之鏡、腐蝕速度調整）
- 未來可考慮 Boss 跨模組戰

---

> 📅 最後更新：2026-06-07  
> 📝 文件版本：v3.0  
> 🔗 相關模組：神墜深淵（Fallen Abyss Mod）  
> 📊 變更摘要：§23 v1.0→v2.0 | §23.7 v2.0→v3.0 | §24 再深化完整說明
