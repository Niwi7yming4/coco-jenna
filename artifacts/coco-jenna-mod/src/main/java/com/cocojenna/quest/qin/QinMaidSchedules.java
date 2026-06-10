package com.cocojenna.quest.qin;

/** 秦可沐與侍女一日作息（設計書第四部）. */
public final class QinMaidSchedules {

    public enum MaidActivity {
        SLEEP, TEA, PAPER_FOLD, WANDER, GUARD, PRANK, STARGAZE
    }

    private QinMaidSchedules() {}

    public static MaidActivity forQin(long time) {
        if (time < 2000 || time >= 22000) return MaidActivity.SLEEP;
        if (time < 6000) return MaidActivity.TEA;
        if (time < 10000) return MaidActivity.PAPER_FOLD;
        if (time < 14000) return MaidActivity.WANDER;
        if (time < 18000) return MaidActivity.STARGAZE;
        return MaidActivity.PAPER_FOLD;
    }

    public static MaidActivity forAFang(long time) {
        if (time < 2000 || time >= 23000) return MaidActivity.SLEEP;
        if (time < 8000) return MaidActivity.TEA;
        if (time < 12000) return MaidActivity.GUARD;
        if (time < 16000) return MaidActivity.TEA;
        if (time < 20000) return MaidActivity.GUARD;
        return MaidActivity.SLEEP;
    }

    public static MaidActivity forLiJiang(long time) {
        if (time < 3000 || time >= 23000) return MaidActivity.SLEEP;
        if (time < 9000) return MaidActivity.PRANK;
        if (time < 14000) return MaidActivity.WANDER;
        if (time < 18000) return MaidActivity.PRANK;
        return MaidActivity.WANDER;
    }
}
