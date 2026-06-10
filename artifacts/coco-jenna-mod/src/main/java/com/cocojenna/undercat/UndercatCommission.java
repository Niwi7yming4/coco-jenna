package com.cocojenna.undercat;

/** 第一章貧民窟委託（完成任意 3 個即可挑戰膠帶巨像）. */
public enum UndercatCommission {
    ORPHAN_CAT(1, 20),
    TAPE_SHORTAGE(2, 20),
    NEON_MUSHROOM(4, 20),
    REPAIR_TOWER(8, 15),
    STOLEN_HEIRLOOM(16, 15);

    public final int flag;
    public final int repReward;

    UndercatCommission(int flag, int repReward) {
        this.flag = flag;
        this.repReward = repReward;
    }

    public static int countCompleted(int mask) {
        int n = 0;
        for (UndercatCommission c : values()) {
            if ((mask & c.flag) != 0) n++;
        }
        return n;
    }
}
