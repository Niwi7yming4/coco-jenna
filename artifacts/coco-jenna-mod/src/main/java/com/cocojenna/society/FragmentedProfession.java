package com.cocojenna.society;

import java.util.Arrays;

/** 破碎序列 9 職業. */
public enum FragmentedProfession {

    CAT_COMPANION("cat_companion", Category.PLAY),
    VELVET_PLAYWORKER("velvet_playworker", Category.PLAY),
    MOON_PLAYMATE("moon_playmate", Category.PLAY),
    CAT_WARDER("cat_warder", Category.COMBAT),
    MUD_EXORCIST("mud_exorcist", Category.COMBAT),
    VELVET_SENTINEL("velvet_sentinel", Category.COMBAT),
    MEMORY_SCRIBE("memory_scribe", Category.RITUAL),
    BROKEN_SCHOLAR("broken_scholar", Category.RITUAL),
    MOON_PROPHET("moon_prophet", Category.RITUAL);

    public enum Category { PLAY, COMBAT, RITUAL }

    private final String id;
    private final Category category;

    FragmentedProfession(String id, Category category) {
        this.id = id;
        this.category = category;
    }

    public String id() { return id; }

    public Category category() { return category; }

    public static FragmentedProfession byId(String id) {
        return Arrays.stream(values())
                .filter(p -> p.id.equals(id))
                .findFirst()
                .orElse(CAT_COMPANION);
    }

    public static FragmentedProfession rollPlay(net.minecraft.util.RandomSource random) {
        FragmentedProfession[] pool = {CAT_COMPANION, VELVET_PLAYWORKER, MOON_PLAYMATE};
        return pool[random.nextInt(pool.length)];
    }

    public static FragmentedProfession rollCombat(net.minecraft.util.RandomSource random) {
        FragmentedProfession[] pool = {CAT_WARDER, MUD_EXORCIST, VELVET_SENTINEL};
        return pool[random.nextInt(pool.length)];
    }

    public static FragmentedProfession rollRitual(net.minecraft.util.RandomSource random) {
        FragmentedProfession[] pool = {MEMORY_SCRIBE, BROKEN_SCHOLAR, MOON_PROPHET};
        return pool[random.nextInt(pool.length)];
    }
}
