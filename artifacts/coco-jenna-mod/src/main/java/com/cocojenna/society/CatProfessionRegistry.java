package com.cocojenna.society;

import com.cocojenna.endgame.kingdom.TownJobRank;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

/** 15+ 貓職業（MCA 風格，設計書 §4.3）. */
public final class CatProfessionRegistry {

    public record Profession(String id, String nameZh, TownJobRank job, String culture) {}

    private static final Profession[] ALL = {
            new Profession("fisher", "漁夫", TownJobRank.FISHER, "coastal"),
            new Profession("gardener", "園丁", TownJobRank.GARDENER, "pastoral"),
            new Profession("chef", "廚師", TownJobRank.CHEF, "pastoral"),
            new Profession("craftsman", "工匠", TownJobRank.CRAFTSMAN, "industrial"),
            new Profession("merchant", "行商", TownJobRank.MERCHANT, "trade"),
            new Profession("scholar", "學者", TownJobRank.SCHOLAR, "scholarly"),
            new Profession("performer", "藝人", TownJobRank.PERFORMER, "festive"),
            new Profession("guard", "衛士", TownJobRank.SHADOW_GUARD, "military"),
            new Profession("architect", "建築師", TownJobRank.ARCHITECT, "industrial"),
            new Profession("scribe", "抄寫員", TownJobRank.SCHOLAR, "scholarly"),
            new Profession("herbalist", "藥草師", TownJobRank.GARDENER, "mystic"),
            new Profession("weaver", "織匠", TownJobRank.CRAFTSMAN, "pastoral"),
            new Profession("scout", "斥候", TownJobRank.SHADOW_GUARD, "military"),
            new Profession("bard", "吟遊詩人", TownJobRank.PERFORMER, "festive"),
            new Profession("alchemist", "煉金貓", TownJobRank.SCHOLAR, "mystic"),
            new Profession("scrapper", "拾荒者", TownJobRank.SCRAPPER, "industrial"),
            new Profession("beekeeper", "養蜂貓", TownJobRank.GARDENER, "pastoral"),
            new Profession("astrologer", "星象師", TownJobRank.SCHOLAR, "mystic"),
    };

    private CatProfessionRegistry() {}

    public static Profession random(RandomSource random) {
        return ALL[random.nextInt(ALL.length)];
    }

    public static Profession byId(String id) {
        for (Profession p : ALL) {
            if (p.id.equals(id)) return p;
        }
        return ALL[0];
    }

    public static Component displayName(Profession p) {
        return Component.translatable("society.cocojenna.profession." + p.id);
    }

    public static Profession[] all() { return ALL; }
}
