package com.cocojenna.entity;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 姊妹羈絆系統 (Sister Bond System) — 靜態工具類。
 *
 * <p>Sister Bond 範圍：0‑100，初始 65
 *
 * <p>分段名稱：
 * <ul>
 *   <li>0‑19  疏遠</li>
 *   <li>20‑39 容忍</li>
 *   <li>40‑59 普通</li>
 *   <li>60‑79 親近</li>
 *   <li>80‑100 形影不離</li>
 * </ul>
 *
 * <p>每 tick 由 {@link com.cocojenna.event.ModEventHandler} 呼叫 {@link #tick}。
 */
public class SisterBondSystem {

    // ── 常數 ────────────────────────────────────────────────────────────

    private static final float BOND_MUTUAL_PET     = 1.0f;
    private static final float BOND_SLEEP_TOGETHER = 2.0f;
    private static final float BOND_SHARED_FOOD    = 3.0f;
    private static final float BOND_INJURED_REACT  = 5.0f;
    private static final float BOND_SEPARATION_PEN = -3.0f / 60f; // 每 tick（30 tick = 1.5 秒）近似
    private static final float BOND_RESOURCE_COMP  = -2.0f;
    private static final float BOND_PLAYER_BIAS    = -1.0f;

    // ── 每 tick 更新 ─────────────────────────────────────────────────────

    public static void tick(Level level, Player owner, CocoEntity coco, JennaEntity jenna) {
        if (level.isClientSide) return;

        BondData bond = ModCapabilities.getOrDefault(owner);
        long gametime = level.getGameTime();

        // 長時間分離（距離 > 50 格，超過 600 tick = 10 分鐘）
        double dist = coco.distanceTo(jenna);
        if (dist > 50) {
            // 每 600 tick 扣 -3
            if (gametime % 600 == 0) {
                bond.modifySisterBond(BOND_SEPARATION_PEN * 600);
            }
        }

        // 兩貓互相靠近 + 互相理毛（Bond > 70，距離 < 2）
        if (bond.getSisterBond() > 70 && dist < 2.0) {
            triggerMutualGroom(level, owner, coco, jenna, bond, gametime);
        }

        // 姊妹同步曬太陽（終局後，Bond > 80）
        if (bond.isEndgameUnlocked() && bond.getSisterBond() > 80) {
            if (!level.isNight() && coco.isInSunlight() && jenna.isInSunlight() && dist < 3.0) {
                if (gametime % 600 == 0) {
                    // 同步曬太陽計時
                    triggerSyncSunbath(level, owner, coco, jenna, bond);
                }
            }
        }

        // Bond > 90 → 姊妹同步行為（偶爾同時轉頭、伸懶腰）
        if (bond.getSisterBond() > 90 && gametime % 200 == 0) {
            triggerSisterSync(coco, jenna);
        }
    }

    // ── 互相理毛 ─────────────────────────────────────────────────────────

    private static long lastGroomTime = 0;

    public static void triggerMutualGroom(Level level, Player owner,
            CocoEntity coco, JennaEntity jenna, BondData bond, long gametime) {
        if (gametime - lastGroomTime < 1200) return; // 冷卻 1 分鐘
        lastGroomTime = gametime;

        bond.modifySisterBond(BOND_MUTUAL_PET);
        level.playSound(null, coco.blockPosition(),
                ModSounds.COCO_PURR_DEEP.get(), SoundSource.NEUTRAL, 0.5f, 1.0f);
        level.playSound(null, jenna.blockPosition(),
                ModSounds.JENNA_PURR_LIGHT.get(), SoundSource.NEUTRAL, 0.5f, 1.2f);
    }

    // ── 同步曬太陽 ───────────────────────────────────────────────────────

    private static boolean syncSunbathTriggered = false;

    private static void triggerSyncSunbath(Level level, Player owner,
            CocoEntity coco, JennaEntity jenna, BondData bond) {
        // 粒子效果
        level.addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                (coco.getX() + jenna.getX()) / 2, coco.getY() + 1,
                (coco.getZ() + jenna.getZ()) / 2, 0, 0.2, 0);

        // 玩家在旁邊觀看 30 秒後解鎖成就
        double ownerDist = owner.distanceTo(coco);
        if (ownerDist < 5.0) {
            // TODO: 觸發成就「完美的對稱」
        }
    }

    // ── 姊妹同步 ─────────────────────────────────────────────────────────

    private static void triggerSisterSync(CocoEntity coco, JennaEntity jenna) {
        // 兩貓同時看向同一個方向（視覺上的同步）
        coco.getLookControl().setLookAt(jenna);
        jenna.getLookControl().setLookAt(coco);
    }

    // ── Bond 修改事件（對外 API）────────────────────────────────────────

    /** 兩貓同時被玩家撫摸 → +1 */
    public static void onBothPetted(Player owner) {
        ModCapabilities.get(owner).ifPresent(b -> b.modifySisterBond(BOND_MUTUAL_PET));
    }

    /** 共享食物（同一碗進食）→ +3 */
    public static void onSharedFood(Player owner) {
        ModCapabilities.get(owner).ifPresent(b -> b.modifySisterBond(BOND_SHARED_FOOD));
    }

    /** 其中一隻受傷 → +5 */
    public static void onOneInjured(Player owner) {
        ModCapabilities.get(owner).ifPresent(b -> b.modifySisterBond(BOND_INJURED_REACT));
    }

    /** 資源競爭（只有一個空食物碗）→ -2 */
    public static void onResourceCompetition(Player owner) {
        ModCapabilities.get(owner).ifPresent(b -> b.modifySisterBond(BOND_RESOURCE_COMP));
    }

    /** 玩家只撫摸其中一隻超過 1 分鐘 → 另一隻嫉妒 -1 */
    public static void onPlayerBias(Player owner) {
        ModCapabilities.get(owner).ifPresent(b -> b.modifySisterBond(BOND_PLAYER_BIAS));
    }

    /** 一起睡覺 → +2 */
    public static void onSleepTogether(Player owner) {
        ModCapabilities.get(owner).ifPresent(b -> b.modifySisterBond(BOND_SLEEP_TOGETHER));
    }

    // ── 稱號取得 ────────────────────────────────────────────────────────

    public static String getBondTitle(float bond) {
        if (bond >= 80) return "形影不離";
        if (bond >= 60) return "親近";
        if (bond >= 40) return "普通";
        if (bond >= 20) return "容忍";
        return "疏遠";
    }
}
