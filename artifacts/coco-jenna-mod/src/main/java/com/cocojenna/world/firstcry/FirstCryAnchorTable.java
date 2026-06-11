package com.cocojenna.world.firstcry;

import net.minecraft.core.BlockPos;

/**
 * 初啼村統一錨點表（Wave 3）— NPC、八區 NBT、隱藏點單一來源.
 */
public final class FirstCryAnchorTable {

    public record DistrictAnchor(String districtId, BlockPos origin) {}
    public record NpcAnchor(String npcId, BlockPos pos, float yaw) {}
    public record HiddenAnchor(String id, BlockPos pos) {}

    private static final DistrictAnchor[] DISTRICTS = {
            new DistrictAnchor("sacred_tree", FirstCryLayout.SACRED_TREE),
            new DistrictAnchor("council_library", FirstCryLayout.MAYOR_HALL.offset(0, 0, 8)),
            new DistrictAnchor("shop_district", FirstCryLayout.RYOKATANA_SHOP),
            new DistrictAnchor("kitchen_market", FirstCryLayout.KITCHEN),
            new DistrictAnchor("moon_plaza", FirstCryLayout.MOON_PLAZA),
            new DistrictAnchor("first_cry_harbor", FirstCryLayout.HARBOR),
            new DistrictAnchor("west_inn", FirstCryLayout.CAT_HOTEL),
            new DistrictAnchor("farm_outer_ring", FirstCryLayout.FARM),
    };

    private static final NpcAnchor[] NPCS = {
            new NpcAnchor("ryokatsu", FirstCryLayout.MAYOR_HALL.offset(0, 1, 2), 180),
            new NpcAnchor("pagepaw", FirstCryLayout.LIBRARY.offset(2, 1, 0), 90),
            new NpcAnchor("blade_mark", FirstCryLayout.RYOKATANA_SHOP.offset(2, 1, 0), 270),
            new NpcAnchor("molten_paw", FirstCryLayout.BLACKSMITH.offset(0, 1, 2), 90),
            new NpcAnchor("miso", FirstCryLayout.CANTEEN.offset(0, 1, 0), 0),
            new NpcAnchor("mint_ear", FirstCryLayout.CATNIP_MARKET.offset(0, 1, 0), 180),
            new NpcAnchor("moon_whisper", FirstCryLayout.MOON_PLAZA.offset(0, 1, 0), 0),
            new NpcAnchor("soft_pad", FirstCryLayout.CAT_HOTEL.offset(0, 1, 0), 45),
            new NpcAnchor("tide_tail", FirstCryLayout.HARBOR.offset(0, 1, 0), 180),
            new NpcAnchor("mud_bean", FirstCryLayout.FARM.offset(0, 1, 0), 135),
            new NpcAnchor("wander_stray", FirstCryLayout.SHOP_ALLEY.offset(0, 1, 0), 220),
    };

    private static final HiddenAnchor[] HIDDEN = {
            new HiddenAnchor("canopy_tablet", FirstCryLayout.CANOPY_TABLET),
            new HiddenAnchor("black_mud_ruin", FirstCryLayout.BLACK_MUD_RUIN),
            new HiddenAnchor("secret_passage", FirstCryLayout.SECRET_PASSAGE),
            new HiddenAnchor("moon_chamber_wall", FirstCryLayout.MOON_CHAMBER_WALL),
    };

    /** 老村長地下儲藏室枯井入口. */
    public static final BlockPos ELDER_CELLAR_ENTRANCE = new BlockPos(2, FirstCryLayout.Y, 0);

    private FirstCryAnchorTable() {}

    public static DistrictAnchor[] districts() {
        return DISTRICTS;
    }

    public static NpcAnchor[] npcs() {
        return NPCS;
    }

    public static HiddenAnchor[] hidden() {
        return HIDDEN;
    }

    public static BlockPos districtOrigin(String id) {
        for (DistrictAnchor d : DISTRICTS) {
            if (d.districtId().equals(id)) return d.origin();
        }
        return FirstCryLayout.CENTER;
    }

    public static BlockPos npcPos(String npcId) {
        for (NpcAnchor n : NPCS) {
            if (n.npcId().equals(npcId)) return n.pos();
        }
        return FirstCryLayout.CENTER;
    }
}
