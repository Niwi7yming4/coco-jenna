package com.cocojenna.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 心靈同步視角渲染器（客戶端）。
 *
 * <p>效果：畫面四角加上琥珀色框（可可）或黃綠色框（珍奶），
 * 並短暫以貓的視角（低矮視點）顯示。持續 100 tick（5 秒）。
 */
@OnlyIn(Dist.CLIENT)
public class MindSyncRenderer {

    private static int syncTick = -1;
    private static boolean isCoco = false;
    private static double catX, catY, catZ;
    private static float catYaw;

    public static void startSync(boolean coco, double x, double y, double z, float yaw) {
        isCoco = coco;
        catX = x; catY = y; catZ = z; catYaw = yaw;
        syncTick = 0;
    }

    public static boolean isActive() { return syncTick >= 0 && syncTick < 100; }
    public static boolean isSyncingCoco() { return isCoco; }

    public static void tick() {
        if (syncTick >= 0) syncTick++;
        if (syncTick >= 100) syncTick = -1;
    }

    /** 取得貓視角 Y 偏移（貓比玩家矮 0.35 格） */
    public static double getCatViewY() {
        return catY - 0.35;
    }

    /** 取得邊框顏色（可可：琥珀金，珍奶：黃綠）*/
    public static int getBorderColor() {
        return isCoco ? 0xFFBF00 : 0xE6FF00;
    }

    /** 取得邊框 alpha（漸入漸出）*/
    public static int getBorderAlpha() {
        if (syncTick < 0) return 0;
        if (syncTick < 10) return (int) (200 * syncTick / 10f);
        if (syncTick > 90) return (int) (200 * (100 - syncTick) / 10f);
        return 200;
    }
}
