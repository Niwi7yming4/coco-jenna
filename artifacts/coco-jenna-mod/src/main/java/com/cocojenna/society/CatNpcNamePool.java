package com.cocojenna.society;

import net.minecraft.util.RandomSource;

/** MCA 風格名字／個性池 — 200+ 組合（設計書 §4.2）. */
public final class CatNpcNamePool {

    public enum Personality {
        PLAYFUL, SHY, BOLD, LAZY, CURIOUS, GENTLE, PROUD, MISCHIEVOUS
    }

    private static final String[] STANDALONE = {
            "小橘", "雪球", "黑豆", "奶茶", "虎斑", "布丁", "麻糬", "年糕", "泡芙", "銀灰",
            "可可", "芝麻", "花卷", "團子", "豆包", "小灰", "米米", "露露", "阿花", "大橘",
            "小狸", "絨絨", "跳跳", "呼呼", "夜影", "晨曦", "星點", "月芽", "雲朵", "雨點",
            "小爪", "肉球", "尾巴", "鬍鬚", "喵喵", "咕嚕", "小夜", "小光", "棉花", "焦糖",
            "薄荷", "檸檬", "蜜桃", "櫻桃", "葡萄", "藍莓", "小星", "小月", "阿黃", "阿白",
            "阿黑", "阿棕", "小虎", "小豹", "小獅", "小狐", "絨尾", "絨耳", "絨爪", "暖陽",
            "微風", "細雨", "薄霧", "晨光", "暮色", "初啼", "再會", "長夜", "黎明", "星塵",
            "月影", "彩虹", "毛線球", "紙箱王", "絨心", "小詩", "小畫", "小歌", "小舞", "小廚"
    };

    private static final String[] PREFIX = {
            "小", "大", "阿", "老", "嫩", "絨", "月", "星", "雲", "雨", "風", "雪", "花", "草", "葉",
            "糖", "蜜", "奶", "豆", "米", "茶", "櫻", "桃", "梅", "竹", "松", "楓", "海", "山", "林"
    };

    private static final String[] SUFFIX = {
            "橘", "白", "灰", "黑", "斑", "球", "丸", "仔", "妹", "哥", "姐", "弟", "爺", "婆",
            "爪", "尾", "耳", "鬍", "瞳", "影", "光", "芽", "苗", "果", "花", "草", "葉", "羽", "鈴",
            "糖", "糕", "餅", "卷", "包", "糰", "露", "霜", "霧", "霞", "虹", "星", "月", "辰", "夜"
    };

    private static final String[] DREAMS_ZH = {
            "想開一家貓飯店", "想環遊貓之國", "想學會唱搖籃曲", "想找到失蹤的兄弟",
            "想成為抓鼠冠軍", "想蓋一座紙箱城堡", "想種滿貓薄荷田", "想寫一本回憶之書",
            "想參加絨尾祭典", "想學會釣月光魚", "想找到彩虹盡頭", "想成為王宮侍衛",
            "想開雜貨鋪", "想當流浪詩人", "想找到永恆的午睡地", "想成為霓虹菇獵人",
            "想編織最長的毛線", "想登上遺忘高塔", "想與雙子星同台演出", "想培育九命靈草"
    };

    private CatNpcNamePool() {}

    public static String randomName(RandomSource random) {
        if (random.nextInt(4) == 0) {
            return STANDALONE[random.nextInt(STANDALONE.length)];
        }
        return PREFIX[random.nextInt(PREFIX.length)] + SUFFIX[random.nextInt(SUFFIX.length)];
    }

    public static int namePoolSize() {
        return STANDALONE.length + PREFIX.length * SUFFIX.length;
    }

    public static Personality randomPersonality(RandomSource random) {
        return Personality.values()[random.nextInt(Personality.values().length)];
    }

    public static String randomDream(RandomSource random) {
        return DREAMS_ZH[random.nextInt(DREAMS_ZH.length)];
    }

    public static String personalityKey(Personality p) {
        return "society.cocojenna.personality." + p.name().toLowerCase();
    }
}
