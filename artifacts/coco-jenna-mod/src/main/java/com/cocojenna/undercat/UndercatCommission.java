package com.cocojenna.undercat;

/** 地下貓域各章委託（Ch.1 五項 + Ch.2–5 各五項，共 25）. */
public enum UndercatCommission {
    // Ch.1 — 貧民窟
    ORPHAN_CAT(1, 1, 20),
    TAPE_SHORTAGE(2, 1, 20),
    NEON_MUSHROOM(4, 1, 20),
    REPAIR_TOWER(8, 1, 15),
    STOLEN_HEIRLOOM(16, 1, 15),
    // Ch.2 — 走私碼頭
    SMUGGLER_RUN(32, 2, 20),
    DOCK_REPAIR(64, 2, 20),
    LEECH_BOUNTY(128, 2, 20),
    PIRATE_TRIAL(256, 2, 15),
    RIVER_CHART(512, 2, 15),
    // Ch.3 — 競技場
    ARENA_IRON(1024, 3, 20),
    ARENA_SHADOW(2048, 3, 20),
    ARENA_POISON(4096, 3, 20),
    ARENA_BET(8192, 3, 15),
    SCARFACE_FAVOR(16384, 3, 15),
    // Ch.4 — 靜默圖書館
    LIBRARY_PLATE(32768, 4, 20),
    LIBRARY_LECTERN(65536, 4, 20),
    LIBRARY_SEAL(131072, 4, 20),
    SILENT_TRIAL(262144, 4, 15),
    ABBESS_FAVOR(524288, 4, 15),
    // Ch.5 — 終章前
    STARLIGHT_OATH(1048576, 5, 20),
    TWIN_PACT(2097152, 5, 20),
    ABYSS_PROBE(4194304, 5, 20),
    FACTION_UNITE(8388608, 5, 15),
    FINALE_PREP(16777216, 5, 15);

    public final int flag;
    public final int chapter;
    public final int repReward;

    UndercatCommission(int flag, int chapter, int repReward) {
        this.flag = flag;
        this.chapter = chapter;
        this.repReward = repReward;
    }

    public static int countCompleted(int mask) {
        int n = 0;
        for (UndercatCommission c : values()) {
            if ((mask & c.flag) != 0) n++;
        }
        return n;
    }

    public static int countCompletedForChapter(int mask, int chapter) {
        int n = 0;
        for (UndercatCommission c : values()) {
            if (c.chapter == chapter && (mask & c.flag) != 0) n++;
        }
        return n;
    }

    public static UndercatCommission[] forChapter(int chapter) {
        return java.util.Arrays.stream(values()).filter(c -> c.chapter == chapter).toArray(UndercatCommission[]::new);
    }
}
