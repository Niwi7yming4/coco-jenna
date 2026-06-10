package com.cocojenna.undercat;

/** 地下貓域五大陣營. */
public enum UndercatFaction {
    CARDBOARD_KINGDOM("cardboard"),
    SMUGGLER_UNION("smuggler"),
    ARENA_BROTHERHOOD("arena"),
    SERVANT_CULT("servant"),
    SILENT_SISTERHOOD("silent");

    public final String id;

    UndercatFaction(String id) {
        this.id = id;
    }

    public static UndercatFaction byId(String id) {
        for (UndercatFaction f : values()) {
            if (f.id.equals(id)) return f;
        }
        return CARDBOARD_KINGDOM;
    }
}
