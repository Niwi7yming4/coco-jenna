package com.cocojenna.world.firstcry;

import com.cocojenna.world.FirstCryVillageGenerator;
import net.minecraft.core.BlockPos;

/**
 * 初啼村八分區錨點（設計書第二章鳥瞰）.
 * MC 座標：北 -Z、南 +Z、東 +X、西 -X.
 */
public final class FirstCryLayout {

    public static final int Y = FirstCryVillageGenerator.FLOOR_Y;
    public static final int RADIUS = 50;
    public static final BlockPos CENTER = FirstCryVillageGenerator.CENTER;

    /** 中央聖樹 */
    public static final BlockPos SACRED_TREE = CENTER;

    /** 東北：村長議事廳、記憶圖書館 */
    public static final BlockPos MAYOR_HALL = offset(14, -34);
    public static final BlockPos LIBRARY = offset(14, -18);

    /** 東：良快刀、鐵匠、小巷 */
    public static final BlockPos RYOKATANA_SHOP = offset(30, 0);
    public static final BlockPos BLACKSMITH = offset(30, -14);
    public static final BlockPos SHOP_ALLEY = offset(24, 10);

    /** 東南：廚房、食堂、市場 */
    public static final BlockPos KITCHEN = offset(20, 22);
    public static final BlockPos CANTEEN = offset(4, 28);
    public static final BlockPos CATNIP_MARKET = offset(-10, 28);

    /** 南：月光祭壇 */
    public static final BlockPos MOON_PLAZA = offset(0, 34);

    /** 西南：初啼港 */
    public static final BlockPos HARBOR = offset(-28, 36);

    /** 西：訪客小屋、貓旅館、秘密通道 */
    public static final BlockPos GUEST_COTTAGE_1 = offset(-36, -12);
    public static final BlockPos GUEST_COTTAGE_2 = offset(-36, -2);
    public static final BlockPos GUEST_COTTAGE_3 = offset(-36, 8);
    public static final BlockPos CAT_HOTEL = offset(-36, 20);
    public static final BlockPos SECRET_PASSAGE = offset(-28, 6);

    /** 西北：農牧區、黑泥廢屋 */
    public static final BlockPos FARM = offset(-32, -32);
    public static final BlockPos BLACK_MUD_RUIN = offset(-42, -42);

    /** 四門（外環） */
    public static final BlockPos GATE_NORTH = offset(0, -46);
    public static final BlockPos GATE_SOUTH = offset(0, 46);
    public static final BlockPos GATE_EAST = offset(46, 0);
    public static final BlockPos GATE_WEST = offset(-46, 0);

    /** 樹冠秘密平台 Y=25、月光密室 */
    public static final BlockPos CANOPY_TABLET = offset(0, 25, -2);
    public static final BlockPos MOON_CHAMBER_WALL = MOON_PLAZA.offset(0, 1, 12);
    public static final BlockPos MOON_CHAMBER_ALTAR = MOON_PLAZA.offset(0, -3, 0);

    private FirstCryLayout() {}

    public static BlockPos offset(int x, int z) {
        return CENTER.offset(x, 0, z);
    }

    public static BlockPos offset(int x, int y, int z) {
        return CENTER.offset(x, y, z);
    }
}
