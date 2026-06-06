package com.cocojenna.capability;

import net.minecraft.nbt.CompoundTag;

/**
 * 儲存玩家與可可、珍奶的所有關係數值。
 *
 * <p>三軌成長系統：
 * <ol>
 *   <li>情感線 (Emotion) — 對玩家的信賴，0‑100</li>
 *   <li>獨立性 (Independence) — 自主行動傾向，0‑100</li>
 *   <li>覺醒線 (Awakening) — 貓之國血脈記憶，0‑50 (對應碎片數)</li>
 * </ol>
 *
 * <p>額外數值：
 * <ul>
 *   <li>保護慾 (Protectiveness) — 可可：0‑100</li>
 *   <li>月亮親和 (Moon Affinity) — 可可：0‑100</li>
 *   <li>玩心 (Playfulness) — 珍奶：0‑100</li>
 *   <li>好奇心 (Curiosity) — 珍奶：0‑100</li>
 *   <li>滿足感 (Contentment) — 珍奶：0‑100</li>
 *   <li>姊妹羈絆 (Sister Bond) — 兩貓共用：0‑100</li>
 * </ul>
 *
 * <p>終局數值（終局事件「初晴」觸發後轉化）：
 * <ul>
 *   <li>依戀值 (Attachment) — 可可：取代保護慾</li>
 *   <li>日照享受 (Sunbathing) — 可可：取代月亮親和</li>
 * </ul>
 */
public class BondData {

    // ── 可可三軌 ─────────────────────────────────────────────────────────
    private float cocoEmotion = 0f;        // 初始 Stranger
    private float cocoIndependence = 20f;
    private int   cocoAwakening = 0;       // 記憶碎片數

    // ── 可可專屬 ─────────────────────────────────────────────────────────
    private float cocoProtectiveness = 60f;
    private float cocoMoonAffinity = 40f;
    private float cocoAttachment = 0f;     // 終局後使用
    private float cocoSunbathing = 0f;     // 終局後使用

    // ── 珍奶三軌 ─────────────────────────────────────────────────────────
    private float jennaEmotion = 0f;
    private float jennaIndependence = 25f;
    private int   jennaAwakening = 0;

    // ── 珍奶專屬 ─────────────────────────────────────────────────────────
    private float jennaPlayfulness = 85f;
    private float jennaCuriosity = 80f;
    private float jennaContentment = 60f;

    // ── 姊妹羈絆 ─────────────────────────────────────────────────────────
    private float sisterBond = 65f;

    // ── 進度旗標 ─────────────────────────────────────────────────────────
    private boolean endgameUnlocked = false;
    private int memoryShardsTotal = 0;

    // ── 計時器（遊戲刻） ─────────────────────────────────────────────────
    private long lastInteractCoco = 0L;
    private long lastInteractJenna = 0L;
    private long lastFeedCoco = 0L;
    private long lastFeedJenna = 0L;

    // ─────────────────────────────────────────────────────────────────────
    // 情感等級計算
    // ─────────────────────────────────────────────────────────────────────

    public enum EmotionLevel {
        STRANGER(0, 9),
        CURIOUS(10, 24),
        ATTACHED(25, 44),
        BONDED(45, 69),
        DEVOTED(70, 89),
        TRANSCENDENT(90, 100);

        public final int min, max;
        EmotionLevel(int min, int max) { this.min = min; this.max = max; }

        public static EmotionLevel of(float value) {
            for (EmotionLevel l : values())
                if (value >= l.min && value <= l.max) return l;
            return STRANGER;
        }
    }

    public enum IndependenceLevel {
        ALWAYS_FOLLOWS(0, 19, 5),
        OCCASIONAL(20, 39, 16),
        AUTONOMOUS(40, 59, 32),
        INDEPENDENT(60, 79, 64),
        TEMPORARY_AWAY(80, 94, 128),
        FREE_ROAM(95, 100, Integer.MAX_VALUE);

        public final int min, max, radius;
        IndependenceLevel(int min, int max, int radius) {
            this.min = min; this.max = max; this.radius = radius;
        }
    }

    public EmotionLevel getCocoEmotionLevel() { return EmotionLevel.of(cocoEmotion); }
    public EmotionLevel getJennaEmotionLevel() { return EmotionLevel.of(jennaEmotion); }

    // ─────────────────────────────────────────────────────────────────────
    // 修改方法（帶限幅與冷卻檢查）
    // ─────────────────────────────────────────────────────────────────────

    public void modifyCocoEmotion(float delta) {
        cocoEmotion = Math.max(0f, Math.min(100f, cocoEmotion + delta));
    }

    public void modifyJennaEmotion(float delta) {
        jennaEmotion = Math.max(0f, Math.min(100f, jennaEmotion + delta));
    }

    public void modifySisterBond(float delta) {
        sisterBond = Math.max(0f, Math.min(100f, sisterBond + delta));
    }

    public void modifyCocoProtectiveness(float delta) {
        cocoProtectiveness = Math.max(0f, Math.min(100f, cocoProtectiveness + delta));
    }

    public void modifyJennaPlayfulness(float delta) {
        jennaPlayfulness = Math.max(0f, Math.min(100f, jennaPlayfulness + delta));
    }

    public void modifyJennaContentment(float delta) {
        jennaContentment = Math.max(0f, Math.min(100f, jennaContentment + delta));
    }

    public void modifyCocoIndependence(float delta) {
        cocoIndependence = Math.max(0f, Math.min(100f, cocoIndependence + delta));
    }

    public void modifyJennaIndependence(float delta) {
        jennaIndependence = Math.max(0f, Math.min(100f, jennaIndependence + delta));
    }

    public void modifyCocoEmotion(float delta, boolean temp) { modifyCocoEmotion(delta); }

    public void addMemoryShard(int amount) {
        memoryShardsTotal += amount;
        // 覺醒以每 5 碎片進一階段（最高 50 碎片 = 階段 6）
        cocoAwakening = Math.min(50, memoryShardsTotal);
        jennaAwakening = Math.min(50, memoryShardsTotal);
    }

    // ─────────────────────────────────────────────────────────────────────
    // NBT 序列化
    // ─────────────────────────────────────────────────────────────────────

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("cocoEmotion", cocoEmotion);
        tag.putFloat("cocoIndependence", cocoIndependence);
        tag.putInt("cocoAwakening", cocoAwakening);
        tag.putFloat("cocoProtectiveness", cocoProtectiveness);
        tag.putFloat("cocoMoonAffinity", cocoMoonAffinity);
        tag.putFloat("cocoAttachment", cocoAttachment);
        tag.putFloat("cocoSunbathing", cocoSunbathing);
        tag.putFloat("jennaEmotion", jennaEmotion);
        tag.putFloat("jennaIndependence", jennaIndependence);
        tag.putInt("jennaAwakening", jennaAwakening);
        tag.putFloat("jennaPlayfulness", jennaPlayfulness);
        tag.putFloat("jennaCuriosity", jennaCuriosity);
        tag.putFloat("jennaContentment", jennaContentment);
        tag.putFloat("sisterBond", sisterBond);
        tag.putBoolean("endgameUnlocked", endgameUnlocked);
        tag.putInt("memoryShardsTotal", memoryShardsTotal);
        tag.putLong("lastInteractCoco", lastInteractCoco);
        tag.putLong("lastInteractJenna", lastInteractJenna);
        tag.putLong("lastFeedCoco", lastFeedCoco);
        tag.putLong("lastFeedJenna", lastFeedJenna);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        cocoEmotion = tag.getFloat("cocoEmotion");
        cocoIndependence = tag.getFloat("cocoIndependence");
        cocoAwakening = tag.getInt("cocoAwakening");
        cocoProtectiveness = tag.getFloat("cocoProtectiveness");
        cocoMoonAffinity = tag.getFloat("cocoMoonAffinity");
        cocoAttachment = tag.getFloat("cocoAttachment");
        cocoSunbathing = tag.getFloat("cocoSunbathing");
        jennaEmotion = tag.getFloat("jennaEmotion");
        jennaIndependence = tag.getFloat("jennaIndependence");
        jennaAwakening = tag.getInt("jennaAwakening");
        jennaPlayfulness = tag.getFloat("jennaPlayfulness");
        jennaCuriosity = tag.getFloat("jennaCuriosity");
        jennaContentment = tag.getFloat("jennaContentment");
        sisterBond = tag.getFloat("sisterBond");
        endgameUnlocked = tag.getBoolean("endgameUnlocked");
        memoryShardsTotal = tag.getInt("memoryShardsTotal");
        lastInteractCoco = tag.getLong("lastInteractCoco");
        lastInteractJenna = tag.getLong("lastInteractJenna");
        lastFeedCoco = tag.getLong("lastFeedCoco");
        lastFeedJenna = tag.getLong("lastFeedJenna");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Getters / Setters
    // ─────────────────────────────────────────────────────────────────────

    public float getCocoEmotion()          { return cocoEmotion; }
    public float getCocoIndependence()     { return cocoIndependence; }
    public int   getCocoAwakening()        { return cocoAwakening; }
    public float getCocoProtectiveness()   { return cocoProtectiveness; }
    public float getCocoMoonAffinity()     { return cocoMoonAffinity; }
    public float getCocoAttachment()       { return cocoAttachment; }
    public float getCocoSunbathing()       { return cocoSunbathing; }
    public float getJennaEmotion()         { return jennaEmotion; }
    public float getJennaIndependence()    { return jennaIndependence; }
    public int   getJennaAwakening()       { return jennaAwakening; }
    public float getJennaPlayfulness()     { return jennaPlayfulness; }
    public float getJennaCuriosity()       { return jennaCuriosity; }
    public float getJennaContentment()     { return jennaContentment; }
    public float getSisterBond()           { return sisterBond; }
    public boolean isEndgameUnlocked()     { return endgameUnlocked; }
    public int   getMemoryShardsTotal()    { return memoryShardsTotal; }
    public long  getLastInteractCoco()     { return lastInteractCoco; }
    public long  getLastInteractJenna()    { return lastInteractJenna; }
    public long  getLastFeedCoco()         { return lastFeedCoco; }
    public long  getLastFeedJenna()        { return lastFeedJenna; }

    public void setCocoEmotion(float v)        { cocoEmotion = Math.max(0, Math.min(100, v)); }
    public void setCocoIndependence(float v)   { cocoIndependence = Math.max(0, Math.min(100, v)); }
    public void setJennaEmotion(float v)       { jennaEmotion = Math.max(0, Math.min(100, v)); }
    public void setJennaIndependence(float v)  { jennaIndependence = Math.max(0, Math.min(100, v)); }
    public void setSisterBond(float v)         { sisterBond = Math.max(0, Math.min(100, v)); }
    public void setEndgameUnlocked(boolean v)  { endgameUnlocked = v; }
    public void setLastInteractCoco(long t)    { lastInteractCoco = t; }
    public void setLastInteractJenna(long t)   { lastInteractJenna = t; }
    public void setLastFeedCoco(long t)        { lastFeedCoco = t; }
    public void setLastFeedJenna(long t)       { lastFeedJenna = t; }

    public void triggerEndgame() {
        endgameUnlocked = true;
        cocoAttachment = 50f + cocoProtectiveness * 0.3f;
        cocoSunbathing = 0f;
        jennaPlayfulness = 100f;
        jennaCuriosity = 100f;
    }
}
