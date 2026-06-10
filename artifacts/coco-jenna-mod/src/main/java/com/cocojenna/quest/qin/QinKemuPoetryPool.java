package com.cocojenna.quest.qin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

/** 始皇貓詩意台詞池（《娶姑娘》意象 + 設計書語錄）. */
public final class QinKemuPoetryPool {

    private static final String[] KEYS = {
            "qin.cocojenna.poetry.01",
            "qin.cocojenna.poetry.02",
            "qin.cocojenna.poetry.03",
            "qin.cocojenna.poetry.04",
            "qin.cocojenna.poetry.05",
            "qin.cocojenna.poetry.06",
            "qin.cocojenna.poetry.07",
            "qin.cocojenna.poetry.08",
            "qin.cocojenna.poetry.09",
            "qin.cocojenna.poetry.10",
            "qin.cocojenna.poetry.11",
            "qin.cocojenna.poetry.12",
    };

    private QinKemuPoetryPool() {}

    public static void speakRandom(ServerPlayer player, RandomSource random) {
        String key = KEYS[random.nextInt(KEYS.length)];
        player.displayClientMessage(Component.translatable(key), false);
    }
}
