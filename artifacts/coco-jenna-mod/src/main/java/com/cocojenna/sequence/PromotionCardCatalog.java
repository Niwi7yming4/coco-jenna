package com.cocojenna.sequence;

import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 晉升卡牌目錄（81 張 — 3 途徑 × 9 階 × 3 選項）. */
public final class PromotionCardCatalog {

    public record CardDef(String id, String force, int tier, char variant, float bonus) {}

    private static final Map<String, CardDef> BY_ID = new LinkedHashMap<>();

    static {
        registerForce("resonance", new String[][]{
                {"t9", "踩奶節奏", "安撫領域", "呼嚕球"},
                {"t8", "暖爐光環", "守護共鳴", "貓掌印記"},
                {"t7", "呼喚同伴", "共振屏障", "低頻震盪"},
                {"t6", "絨尾領域", "治癒呼嚕", "肚皮陷阱"},
                {"t5", "共鳴巨砲", "生命連結", "王族威壓"},
                {"t4", "夢境編織", "記憶治癒", "絨尾擁抱"},
                {"t3", "永恆呼嚕", "共鳴連鎖", "貓之國領域"},
                {"t2", "夢境編織貓", "絨尾主宰", "記憶燈塔"},
                {"t1", "初晴共鳴", "永恆安撫", "王國之心"},
        });
        registerForce("shadow", new String[][]{
                {"t9", "巡尾追跡", "碎步潛行", "暗影殘留"},
                {"t8", "腳踝鎖定", "午夜狂奔", "瞳孔凝視"},
                {"t7", "半夜暴衝", "暗影分身", "虛弱凝視"},
                {"t6", "狩獵本能", "虛空凝視", "暗夜行者"},
                {"t5", "寂靜處刑", "影之穿梭", "死亡標記"},
                {"t4", "深淵凝視", "影刃風暴", "夜瞳主宰"},
                {"t3", "永夜裁斷", "暗影連鎖", "盲水領域"},
                {"t2", "影爪奧義", "虛空主宰", "深淵燈塔"},
                {"t1", "終末之瞳", "無聲處刑", "影之國度"},
        });
        registerForce("chaos", new String[][]{
                {"t9", "紙箱藏身", "隨機喵叫", "翻倒花瓶"},
                {"t8", "混沌翻滾", "驚嚇跳躍", "毛球風暴"},
                {"t7", "薛丁格爪", "概率撲擊", "混亂印記"},
                {"t6", "貓草狂歡", "混沌領域", "意外禮物"},
                {"t5", "混沌巨砲", "連鎖翻桌", "王國惡作劇"},
                {"t4", "夢境惡作劇", "記憶洗牌", "絨尾爆炸"},
                {"t3", "永恆混沌", "概率連鎖", "貓之國混亂"},
                {"t2", "混沌奧義", "分身狂歡", "混沌燈塔"},
                {"t1", "原始混沌", "萬物翻倒", "混沌之心"},
        });
    }

    private PromotionCardCatalog() {}

    private static void registerForce(String force, String[][] tiers) {
        for (String[] row : tiers) {
            String tierKey = row[0];
            int tier = Integer.parseInt(tierKey.substring(1));
            register(force, tier, 'a', row[1]);
            register(force, tier, 'b', row[2]);
            register(force, tier, 'c', row[3]);
        }
    }

    private static void register(String force, int tier, char variant, String unusedName) {
        String id = force + "_t" + tier + "_" + variant;
        float bonus = variant == 'a' ? 0.04f : variant == 'b' ? 0.03f : 0.05f;
        BY_ID.put(id, new CardDef(id, force, tier, variant, bonus));
    }

    public static List<String> pickThree(String force, int fromTier) {
        String f = force == null || force.isEmpty() ? "resonance" : force;
        return List.of(f + "_t" + fromTier + "_a", f + "_t" + fromTier + "_b", f + "_t" + fromTier + "_c");
    }

    public static float cardBonus(String cardId) {
        return Optional.ofNullable(BY_ID.get(cardId)).map(CardDef::bonus).orElse(0.04f);
    }

    public static Component displayName(String cardId) {
        return Component.translatable("promotion.cocojenna.card." + cardId);
    }

    public static Optional<CardDef> get(String cardId) {
        return Optional.ofNullable(BY_ID.get(cardId));
    }

    public static Map<String, CardDef> all() {
        return BY_ID;
    }
}
