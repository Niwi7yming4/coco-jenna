package com.cocojenna.exploration;

/** 怪貓貓圖鑑種類（設計書 4.2，共 15 種）. */
public enum WildCatType {
    MOON_TABBY(0, "moon_alley", true, false),
    SALT_LIZARD(1, "howling_gorge", false, true),
    MOTH_CAT(2, "velvet_forest", true, false),
    BOX_LURKER(3, "gear_town", true, false),
    BLIND_LIGHT_KEEPER(4, "blind_port", true, false),
    RUST_GEAR(5, "gear_town", false, true),
    MIRAGE_SHADE(6, "phantom_maze", false, true),
    HONEY_CLAW(7, "dawn_highlands", true, false),
    SONG_CAT(8, "first_cry", true, false),
    STORM_RIDER(9, "howling_gorge", false, true),
    DUNE_STALKER(10, "forgotten_wastes", false, true),
    FROST_WHISKER(11, "howling_gorge", true, false),
    STING_TAIL(12, "velvet_forest", false, true),
    TIME_SKIP(13, "forgotten_tower", true, false),
    VOID_GAZER(14, "black_mud_zone", false, true);

    private final int id;
    private final String biome;
    private final boolean neutral;
    private final boolean hostile;

    WildCatType(int id, String biome, boolean neutral, boolean hostile) {
        this.id = id;
        this.biome = biome;
        this.neutral = neutral;
        this.hostile = hostile;
    }

    public int id() { return id; }
    public String biome() { return biome; }
    public boolean neutral() { return neutral; }
    public boolean hostile() { return hostile; }

    public static WildCatType fromId(int id) {
        for (WildCatType t : values()) {
            if (t.id == id) return t;
        }
        return MOON_TABBY;
    }

    public static int flag(int id) {
        return 1 << id;
    }
}
