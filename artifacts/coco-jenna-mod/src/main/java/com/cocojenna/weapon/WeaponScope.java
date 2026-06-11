package com.cocojenna.weapon;

/**
 * 武器內容範圍決策（對照設計書 77 把）.
 * <p>
 * v1.0：50 良快刀 + 21 大快刀框架 + 6 至尊大快刀儀式鉤子；27 把擴充列 v1.1 DLC。
 */
public final class WeaponScope {

    /** 設計書總武器數 */
    public static final int DESIGN_TOTAL = 77;
    /** v1.0 良快刀（完整四階解封 + 獨立技能） */
    public static final int V1_RYOKATANA = 50;
    /** v1.0 大快刀 */
    public static final int V1_DAIKATANA = 21;
    /** v1.0 至尊大快刀 */
    public static final int V1_SUPREME = 6;
    /** v1.1 DLC 擴充（設計書剩餘產能） */
    public static final int DLC_EXPANSION = DESIGN_TOTAL - V1_RYOKATANA - V1_DAIKATANA - V1_SUPREME;

    private WeaponScope() {}

    public static boolean isV1Shipped(String variantId) {
        return variantId != null && WeaponUnsealManager.PILOT_VARIANTS.contains(variantId);
    }

    public static boolean isDlcExpansion(String variantId) {
        return !isV1Shipped(variantId);
    }
}
