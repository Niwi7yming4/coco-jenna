package com.cocojenna.endgame.kingdom;

import net.minecraft.network.chat.Component;

/** 可排程的生產任務. */
public enum MpsTask {
    REST("rest", 0, false, false),
    GATHER_WOOD("gather_wood", 8, true, false),
    PROCESS_WOOD("process_wood", 12, false, true),
    GATHER_MOONSTONE("gather_moonstone", 10, true, false),
    GATHER_FUR("gather_fur", 6, true, false),
    WEAVE_CARPET("weave_carpet", 14, false, true),
    FISH_NIGHT("fish_night", 9, true, false),
    GATHER_NEON("gather_neon", 7, true, false),
    BUILD_STAGE("build_stage", 16, false, true),
    BUILD_LIGHT("build_light", 12, false, true),
    MAKE_WREATH("make_wreath", 10, false, true),
    COOK_PREP("cook_prep", 11, false, true),
    DECORATE("decorate", 13, false, true),
    FESTIVAL("festival", 20, false, false);

    public final String id;
    public final int baseYield;
    public final boolean gather;
    public final boolean craft;

    MpsTask(String id, int baseYield, boolean gather, boolean craft) {
        this.id = id;
        this.baseYield = baseYield;
        this.gather = gather;
        this.craft = craft;
    }

    public Component label() {
        return Component.translatable("kingdom.cocojenna.mps." + id);
    }

    public static MpsTask byId(String id) {
        if (id == null || id.isEmpty()) return REST;
        for (MpsTask t : values()) {
            if (t.id.equals(id)) return t;
        }
        return REST;
    }
}
