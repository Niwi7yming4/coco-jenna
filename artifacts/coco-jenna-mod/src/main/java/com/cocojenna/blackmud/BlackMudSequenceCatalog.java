package com.cocojenna.blackmud;

import net.minecraft.network.chat.Component;

/** 黑泥反向序列 9→1（設計書 卷二 §2.2）. */
public final class BlackMudSequenceCatalog {

    public record Tier(int sequence, String entityKey, String formKey) {}

    private static final Tier[] TIERS = {
            new Tier(9, "hypothermia", "amorphous"),
            new Tier(8, "forgotten_shadow", "smoke"),
            new Tier(7, "whisper_puppet", "doll"),
            new Tier(6, "memory_worm", "moth"),
            new Tier(5, "mimic", "cat_shape"),
            new Tier(4, "grief_amalgam", "amalgam"),
            new Tier(3, "blind_water_lord", "semi_human"),
            new Tier(2, "fallen_velvet", "velvet"),
            new Tier(1, "primal_chaos", "heart")
    };

    private BlackMudSequenceCatalog() {}

    public static Component name(int sequence) {
        int seq = clamp(sequence);
        return Component.translatable("blackmud.cocojenna.seq." + seq + ".name");
    }

    public static Component consciousness(int sequence) {
        return Component.translatable("blackmud.cocojenna.seq." + clamp(sequence) + ".consciousness");
    }

    public static Component form(int sequence) {
        Tier t = tier(clamp(sequence));
        return Component.translatable("blackmud.cocojenna.form." + t.formKey);
    }

    public static Tier tier(int sequence) {
        int seq = clamp(sequence);
        return TIERS[9 - seq];
    }

    public static boolean isHighSequence(int sequence) {
        return sequence <= 5;
    }

    public static boolean isLowSequence(int sequence) {
        return sequence >= 8;
    }

    private static int clamp(int sequence) {
        return Math.max(1, Math.min(9, sequence));
    }
}
