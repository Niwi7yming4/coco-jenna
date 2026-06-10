package com.cocojenna.undercat;

/** 地下貓域每日任務池（批次 D）. */
public enum UndercatDailyQuest {
    SHADOW_PATROL(15, 8, "shadow_patrol"),
    TAPE_OFFERING(12, 6, "tape_offering"),
    ORPHAN_DONATE(20, 10, "orphan_donate"),
    ARENA_TIP(25, 12, "arena_tip"),
    CATNIP_TEND(18, 8, "catnip_tend");

    public final int coinReward;
    public final int repReward;
    public final String id;

    UndercatDailyQuest(int coinReward, int repReward, String id) {
        this.coinReward = coinReward;
        this.repReward = repReward;
        this.id = id;
    }

    public static UndercatDailyQuest forDay(long day) {
        return values()[(int) (Math.floorMod(day, values().length))];
    }
}
