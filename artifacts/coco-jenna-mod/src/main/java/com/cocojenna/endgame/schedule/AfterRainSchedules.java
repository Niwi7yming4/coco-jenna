package com.cocojenna.endgame.schedule;

import com.cocojenna.endgame.AfterRainManager;
import net.minecraft.server.level.ServerLevel;

/** 依遊戲時間解析雨後作息. */
public final class AfterRainSchedules {

    private AfterRainSchedules() {}

    public static boolean isActive(ServerLevel level) {
        return AfterRainManager.isAfterRain(level);
    }

    public static long dayTime(ServerLevel level) {
        return level.getDayTime() % 24000L;
    }

    public static AfterRainActivity forCoco(long time) {
        if (time < 1000) return AfterRainActivity.SLEEP;
        if (time < 6000) return AfterRainActivity.PATROL;
        if (time < 12000) return AfterRainActivity.SUNBATHE;
        if (time < 14000) return AfterRainActivity.FOLLOW_OWNER;
        if (time < 16000) return AfterRainActivity.SLEEP;
        if (time < 18000) return AfterRainActivity.HIGH_GAZE;
        if (time < 22000) return AfterRainActivity.FOLLOW_OWNER;
        return AfterRainActivity.SLEEP;
    }

    public static AfterRainActivity forJenna(long time) {
        if (time < 1000) return AfterRainActivity.SLEEP;
        if (time < 6000) return AfterRainActivity.EXPLORE;
        if (time < 12000) return AfterRainActivity.FOLLOW_OWNER;
        if (time < 16000) return AfterRainActivity.EXPLORE;
        if (time < 18000) return AfterRainActivity.EXPLORE;
        if (time < 22000) return AfterRainActivity.FOLLOW_OWNER;
        return AfterRainActivity.SLEEP;
    }

    public static AfterRainActivity forNpc(AfterRainNpcRole role, long time) {
        return switch (role) {
            case SAMURAI_GUARD -> {
                if (time < 6000 || (time >= 12000 && time < 18000)) yield AfterRainActivity.PATROL;
                if (time < 12000) yield AfterRainActivity.SUNBATHE;
                yield AfterRainActivity.SLEEP;
            }
            case MONK -> {
                if (time < 6000) yield AfterRainActivity.SUNBATHE;
                if (time < 12000) yield AfterRainActivity.SLEEP;
                yield AfterRainActivity.PATROL;
            }
            case COURT_LADY -> {
                if (time < 6000) yield AfterRainActivity.PATROL;
                if (time < 14000) yield AfterRainActivity.SUNBATHE;
                yield AfterRainActivity.PATROL;
            }
            case WEAVER -> {
                if (time < 6000) yield AfterRainActivity.PATROL;
                if (time < 18000) yield AfterRainActivity.IDLE;
                yield AfterRainActivity.SLEEP;
            }
            case FISHER -> {
                if (time >= 18000) yield AfterRainActivity.SLEEP;
                if (time < 12000) yield AfterRainActivity.FISH;
                yield AfterRainActivity.SUNBATHE;
            }
            case MERCHANT -> {
                if (time < 6000 || time >= 18000) yield AfterRainActivity.SLEEP;
                yield AfterRainActivity.PATROL;
            }
        };
    }
}
