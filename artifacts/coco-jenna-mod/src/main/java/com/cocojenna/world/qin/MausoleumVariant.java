package com.cocojenna.world.qin;

import net.minecraft.util.RandomSource;

/** 36 種皇陵變體 = 6 風格 × 6 地域. */
public record MausoleumVariant(MausoleumType type, Region region, String id) {

    public enum Region {
        YELLOW_RIVER("yellow_river"),
        JIANGNAN("jiangnan"),
        WESTERN("western"),
        NORTHERN("northern"),
        COASTAL("coastal"),
        MOUNTAIN("mountain");

        private final String id;

        Region(String id) { this.id = id; }

        public String id() { return id; }
    }

    public static final MausoleumVariant[] ALL = buildAll();

    private static MausoleumVariant[] buildAll() {
        MausoleumVariant[] arr = new MausoleumVariant[36];
        int i = 0;
        for (MausoleumType type : MausoleumType.values()) {
            for (Region region : Region.values()) {
                arr[i++] = new MausoleumVariant(type, region, type.id() + "_" + region.id());
            }
        }
        return arr;
    }

    public static MausoleumVariant roll(RandomSource random, boolean allowSleeping) {
        if (allowSleeping && random.nextInt(48) == 0) {
            return byTypeAndRegion(MausoleumType.SLEEPING_CHAMBER, Region.values()[random.nextInt(Region.values().length)]);
        }
        MausoleumType type = MausoleumType.roll(random, false);
        Region region = Region.values()[random.nextInt(Region.values().length)];
        return byTypeAndRegion(type, region);
    }

    public static MausoleumVariant byId(String id) {
        for (MausoleumVariant v : ALL) {
            if (v.id.equals(id)) return v;
        }
        return ALL[0];
    }

    private static MausoleumVariant byTypeAndRegion(MausoleumType type, Region region) {
        for (MausoleumVariant v : ALL) {
            if (v.type == type && v.region == region) return v;
        }
        return new MausoleumVariant(type, region, type.id() + "_" + region.id());
    }
}
