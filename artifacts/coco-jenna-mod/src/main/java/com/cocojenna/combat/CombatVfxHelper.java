package com.cocojenna.combat;

import com.cocojenna.init.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 三源力戰鬥特效 — 設計書第二、三章
 * 
 * 分層設計（設計書 3.1）：
 * L1 基礎層：普通攻擊、基本移動
 * L2 技能層：主動技能釋放
 * L3 暴擊/狀態層：暴擊、狀態觸發
 * L4 終極層：序列奧義、雙子星連攜、Boss擊殺
 */
public final class CombatVfxHelper {

    public enum Force { RESONANCE, SHADOW, CHAOS }

    private CombatVfxHelper() {}

    public static Force of(String forceId) {
        if ("shadow".equals(forceId)) return Force.SHADOW;
        if ("chaos".equals(forceId)) return Force.CHAOS;
        return Force.RESONANCE;
    }

    // ========================================================================
    // L1 基礎層
    // ========================================================================

    /**
     * L1 普通攻擊命中反饋
     * 敵方短暫閃白（0.05秒）+ 微小粒子爆發 + 輕微螢幕震動
     */
    public static void basicHit(ServerLevel level, LivingEntity target, String weaponType) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.6, 0);

        // 根據武器類型選擇粒子
        ParticleOptions particle = switch (weaponType) {
            case "sword" -> ParticleTypes.SWEEP_ATTACK;
            case "blunt" -> ParticleTypes.CRIT;
            case "sonic" -> ModParticles.PURR_WAVE.get();
            default -> ParticleTypes.CRIT;
        };
        level.sendParticles(particle, p.x, p.y, p.z, 5, 0.15, 0.15, 0.15, 0.03);
    }

    /**
     * L1 第三下普攻強化（連擊終結）
     */
    public static void thirdHitCombo(ServerLevel level, ServerPlayer player, Force force) {
        Vec3 p = player.position().add(0, 0.5, 0);
        // 武器殘影
        level.sendParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 8, 0.4, 0.2, 0.4, 0.02);
        // 根據途徑的強化特效
        switch (force) {
            case RESONANCE -> level.sendParticles(ModParticles.PURR_WAVE.get(), 
                p.x, p.y, p.z, 6, 0.3, 0.1, 0.3, 0.02);
            case SHADOW -> level.sendParticles(ModParticles.SHADOW_FEATHER.get(),
                p.x, p.y, p.z, 6, 0.3, 0.1, 0.3, 0.02);
            case CHAOS -> level.sendParticles(ModParticles.CHAOS_CONFETTI.get(),
                p.x, p.y, p.z, 6, 0.3, 0.1, 0.3, 0.05);
        }
    }

    // ========================================================================
    // L2 技能層（原有skillCast強化）
    // ========================================================================

    public static void skillCast(ServerLevel level, ServerPlayer player, Force force, int variant, boolean outer) {
        CombatSoundHelper.play(level, player.position(), CombatSoundHelper.Layer.BASE, force);
        double r = outer ? 5.5 : 3.5;

        switch (force) {
            case RESONANCE -> {
                // 安撫聲波：同心圓聲波擴散 + 音符粒子
                spawnRing(level, player.position(), r, ModParticles.PURR_WAVE.get(), 28);
                spawnRising(level, player, ModParticles.VELVET_DRIFT.get(), 12);
                if (variant == 2) {
                    // 暖爐光環：金色光圈 + 暖色粒子上升
                    spawnRing(level, player.position(), r * 0.6, ParticleTypes.FLAME, 16);
                    spawnRising(level, player, ParticleTypes.END_ROD, 8);
                }
                if (variant == 1) {
                    // 共鳴護盾：半透明金色球形
                    spawnRing(level, player.position(), 2.0, ModParticles.PURR_WAVE.get(), 24);
                }
            }
            case SHADOW -> {
                // 影刃投擲 + 暗影潛行
                spawnRing(level, player.position(), r, ModParticles.SHADOW_FEATHER.get(), 20);
                spawnRising(level, player, ParticleTypes.SMOKE, 10);
                if (variant == 1) {
                    // 虛空凝視：直線紫色光束效果
                    spawnBurst(level, player.position(), ParticleTypes.REVERSE_PORTAL, 24);
                    spawnBurst(level, player.position().add(0, 0.5, 0), ModParticles.SHADOW_FEATHER.get(), 12);
                }
                if (variant == 3) {
                    // 暗夜分身：本體與分身之間的連線
                    spawnRing(level, player.position(), 1.5, ModParticles.SHADOW_FEATHER.get(), 16);
                }
            }
            case CHAOS -> {
                // 紙箱投擲 + 液態閃避 + 薛丁格之擊
                spawnRing(level, player.position(), r, ModParticles.CHAOS_CONFETTI.get(), 18);
                spawnBurst(level, player.position(), ParticleTypes.FIREWORK, 16);
                spawnBurst(level, player.position(), ModParticles.CHAOS_CONFETTI.get(), 8);
                if (variant == 1) {
                    // 液態閃避：彩虹色液體粒子
                    spawnBurst(level, player.position(), ParticleTypes.INSTANT_EFFECT, 12);
                }
                if (variant == 3) {
                    // 九命試煉：8個貓咪虛影（粒子版本）
                    for (int i = 0; i < 8; i++) {
                        double ang = i * Math.PI / 4;
                        level.sendParticles(ModParticles.CHAOS_CONFETTI.get(),
                            player.getX() + Math.cos(ang) * 2.5,
                            player.getY() + 0.5 + Math.sin(ang * 2) * 0.3,
                            player.getZ() + Math.sin(ang) * 2.5,
                            3, 0.1, 0.1, 0.1, 0.04);
                    }
                }
            }
        }
        // 播放相關音效由 CombatSoundHelper 處理
    }

    // ========================================================================
    // L3 暴擊/狀態層
    // ========================================================================

    /**
     * L3 暴擊特效
     * 金色閃光 + 金色大字 + 擊退 + 武器殘影軌跡 + 連續暴擊邊框
     */
    public static void critHit(ServerLevel level, LivingEntity target, Force force, boolean consecutive) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.6, 0);

        // 金色閃光（螢幕效果由客戶端處理）
        CombatSoundHelper.play(level, p, CombatSoundHelper.Layer.CRIT, force);

        // 暴擊粒子爆發
        level.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 20, 0.3, 0.3, 0.3, 0.1);
        level.sendParticles(ParticleTypes.ENCHANTED_HIT, p.x, p.y, p.z, 12, 0.2, 0.2, 0.2, 0.08);

        // 武器殘影軌跡
        level.sendParticles(ParticleTypes.SWEEP_ATTACK, p.x, p.y, p.z, 8, 0.4, 0.2, 0.4, 0.02);

        // 連續暴擊時對應途徑顏色邊框
        if (consecutive) {
            ParticleOptions borderParticle = switch (force) {
                case RESONANCE -> ModParticles.PURR_WAVE.get();
                case SHADOW -> ModParticles.SHADOW_FEATHER.get();
                case CHAOS -> ModParticles.CHAOS_CONFETTI.get();
            };
            // 環繞敵方的粒子環
            for (int i = 0; i < 16; i++) {
                double ang = i * Math.PI * 2 / 16;
                level.sendParticles(borderParticle,
                    p.x + Math.cos(ang) * 1.5, p.y + 1.0 + Math.sin(ang * 2) * 0.3, p.z + Math.sin(ang) * 1.5,
                    1, 0, 0.05, 0, 0.01);
            }
        }
    }

    /**
     * L3 狀態觸發特效
     */
    public static void statusEffect(ServerLevel level, LivingEntity target, String status, boolean apply) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.5, 0);

        switch (status) {
            case "slow" -> {
                // 藍色冰晶 + 冰裂紋
                int color = 0x44CCFF;
                level.sendParticles(ParticleTypes.ITEM_SNOWBALL, p.x, p.y, p.z,
                    apply ? 10 : 3, 0.3, 0.1, 0.3, 0.02);
            }
            case "burn" -> {
                // 橘紅色火焰粒子
                level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z,
                    apply ? 8 : 2, 0.2, 0.3, 0.2, 0.03);
            }
            case "poison" -> {
                // 綠色泡泡
                level.sendParticles(ParticleTypes.ITEM_SLIME, p.x, p.y + 0.5, p.z,
                    apply ? 6 : 2, 0.2, 0.1, 0.2, 0.02);
            }
            case "confusion" -> {
                // 旋轉星星和問號
                level.sendParticles(ParticleTypes.END_ROD, p.x, p.y + 0.8, p.z,
                    apply ? 12 : 4, 0.3, 0.2, 0.3, 0.04);
                level.sendParticles(ParticleTypes.FIREWORK, p.x, p.y + 0.3, p.z,
                    apply ? 6 : 2, 0.2, 0.1, 0.2, 0.03);
            }
            case "mark" -> {
                // 貓掌印標記
                level.sendParticles(ModParticles.PURR_WAVE.get(), p.x, p.y + 1.0, p.z,
                    apply ? 5 : 1, 0.2, 0.1, 0.2, 0.02);
            }
        }
    }

    /**
     * L3 元素反應特效
     */
    public static void elementReaction(ServerLevel level, LivingEntity target, String reaction) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.5, 0);

        switch (reaction) {
            case "freeze" -> {
                // 凍結：冰封 + 裂紋冰晶
                level.sendParticles(ParticleTypes.ITEM_SNOWBALL, p.x, p.y, p.z, 30, 0.5, 0.5, 0.5, 0.05);
                level.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 15, 0.3, 0.3, 0.3, 0.08);
            }
            case "explosion" -> {
                // 爆燃：火焰瞬間變大 + 範圍傷害
                level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 40, 1.0, 0.5, 1.0, 0.06);
                level.sendParticles(ParticleTypes.LAVA, p.x, p.y, p.z, 10, 0.3, 0.3, 0.3, 0.04);
                level.sendParticles(ParticleTypes.EXPLOSION, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
            case "corrosion" -> {
                // 腐蝕：綠色煙霧 + 護甲下降
                level.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 25, 0.5, 0.3, 0.5, 0.03);
                level.sendParticles(ParticleTypes.ITEM_SLIME, p.x, p.y + 0.5, p.z, 10, 0.3, 0.1, 0.3, 0.04);
            }
        }
    }

    // ========================================================================
    // L4 終極層
    // ========================================================================

    /**
     * L4 序列奧義釋放（序列1專屬技能）
     * 
     * 設計書 3.5：
     * - 釋放前畫面邊緣出現對應顏色光暈，時間流速短暫降低（0.3秒慢動作）
     * - 玩家身體被對應顏色的光芒包裹
     * - 全螢幕爆發對應途徑的主題粒子
     */
    public static void sequenceUltimate(ServerLevel level, ServerPlayer player, Force force, int tier) {
        Vec3 pos = player.position().add(0, 1.0, 0);

        // 慢動作效果由客戶端透過 packet 處理

        switch (force) {
            case RESONANCE -> {
                // 金色聲波巨浪：從玩家為中心席捲全螢幕
                for (int ring = 1; ring <= 6; ring++) {
                    double radius = ring * 2.5;
                    int points = 20 + ring * 8;
                    for (int i = 0; i < points; i++) {
                        double ang = i * Math.PI * 2 / points;
                        level.sendParticles(ModParticles.PURR_WAVE.get(),
                            pos.x + Math.cos(ang) * radius,
                            pos.y + 0.15,
                            pos.z + Math.sin(ang) * radius,
                            2, 0, 0.05, 0, 0.02);
                    }
                }
                // 金色光柱
                for (int i = 0; i < 40; i++) {
                    level.sendParticles(ParticleTypes.END_ROD,
                        pos.x + (level.random.nextDouble() - 0.5) * 2,
                        pos.y + level.random.nextDouble() * 3,
                        pos.z + (level.random.nextDouble() - 0.5) * 2,
                        1, 0, 0.1, 0, 0.03);
                }
                // 震懾所有敵方（停滯1秒）
            }
            case SHADOW -> {
                // 螢幕變暗 + 無數紫色影刃
                // 暗影爆發
                for (int i = 0; i < 50; i++) {
                    double ang = level.random.nextDouble() * Math.PI * 2;
                    double dist = 1.0 + level.random.nextDouble() * 4.0;
                    level.sendParticles(ModParticles.SHADOW_FEATHER.get(),
                        pos.x + Math.cos(ang) * dist,
                        pos.y + level.random.nextDouble() * 2.0,
                        pos.z + Math.sin(ang) * dist,
                        3, 0.1, 0.1, 0.1, 0.04);
                }
                // 紫色閃電（使用END_ROD模擬）
                for (int i = 0; i < 20; i++) {
                    level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        pos.x + (level.random.nextDouble() - 0.5) * 6,
                        pos.y + level.random.nextDouble() * 3,
                        pos.z + (level.random.nextDouble() - 0.5) * 6,
                        1, 0.2, 0.3, 0.2, 0.02);
                }
            }
            case CHAOS -> {
                // 螢幕變成彩虹色調 + 大量紙箱星星問號從天而降
                for (int burst = 0; burst < 8; burst++) {
                    double offsetX = (level.random.nextDouble() - 0.5) * 8;
                    double offsetZ = (level.random.nextDouble() - 0.5) * 8;
                    level.sendParticles(ModParticles.CHAOS_CONFETTI.get(),
                        pos.x + offsetX, pos.y + 0.5 + level.random.nextDouble() * 2, pos.z + offsetZ,
                        20, 0.3, 0.3, 0.3, 0.08);
                }
                level.sendParticles(ParticleTypes.FIREWORK,
                    pos.x, pos.y + 1.5, pos.z, 30, 2.0, 0.5, 2.0, 0.06);
                level.sendParticles(ParticleTypes.INSTANT_EFFECT,
                    pos.x, pos.y, pos.z, 25, 1.5, 0.3, 1.5, 0.04);
            }
        }

        // 釋放後：玩家身上殘留對應顏色光點（持續10秒）
        for (int i = 0; i < 30; i++) {
            level.sendParticles(ParticleTypes.FIREWORK,
                pos.x + (level.random.nextDouble() - 0.5) * 1.5,
                pos.y + level.random.nextDouble() * 2,
                pos.z + (level.random.nextDouble() - 0.5) * 1.5,
                1, 0, 0.01, 0, 0.01);
        }

        CombatSoundHelper.play(level, pos, CombatSoundHelper.Layer.BOSS, force);
    }

    /**
     * L4 雙子星連攜（Sister Bond > 80觸發）
     * 
     * 設計書 3.5：
     * - 可可化為黑色影之守護環繞玩家
     * - 珍奶化為彩色光球在敵方之間彈跳
     * - 連攜結束時兩貓回到玩家身邊回復20%生命
     */
    public static void twinStarBond(ServerLevel level, ServerPlayer player) {
        Vec3 pos = player.position().add(0, 1.0, 0);

        // 剪影浮現（由客戶端處理畫面中央效果）
        // 這裡處理粒子效果

        // 可可守護環：黑色暗影粒子環繞
        for (int ring = 1; ring <= 3; ring++) {
            double radius = ring * 1.0;
            for (int i = 0; i < 12; i++) {
                double ang = i * Math.PI * 2 / 12;
                level.sendParticles(ParticleTypes.SQUID_INK,
                    pos.x + Math.cos(ang) * radius,
                    pos.y + 1.0 + Math.sin(ang * 3) * 0.3,
                    pos.z + Math.sin(ang) * radius,
                    1, 0, 0.05, 0, 0.02);
            }
        }

        // 珍奶彩色光球在敵方之間彈跳（簡化為彩色粒子爆發）
        for (int burst = 0; burst < 5; burst++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 6;
            double offsetZ = (level.random.nextDouble() - 0.5) * 6;
            level.sendParticles(ParticleTypes.FIREWORK,
                pos.x + offsetX, pos.y + 0.5, pos.z + offsetZ,
                12, 0.3, 0.2, 0.3, 0.05);
        }

        // 回復效果：金色光點從兩貓回到玩家
        for (int i = 0; i < 20; i++) {
            double ang = level.random.nextDouble() * Math.PI * 2;
            double dist = 1.0 + level.random.nextDouble() * 2.0;
            level.sendParticles(ParticleTypes.HEART,
                pos.x + Math.cos(ang) * dist,
                pos.y + 0.3 + level.random.nextDouble() * 0.5,
                pos.z + Math.sin(ang) * dist,
                1, 0, 0.01, 0, 0.01);
        }

        CombatSoundHelper.play(level, pos, CombatSoundHelper.Layer.BOSS, Force.RESONANCE);
    }

    /**
     * L4 Boss擊殺特效
     * 
     * 設計書 3.5：
     * - 身體從核心開始崩解（金色裂縫向外擴散）
     * - 化為金色星塵向上飄升形成光柱
     * - 留下記憶之花
     * - 全螢幕短暫閃白
     */
    public static void bossKillEffect(ServerLevel level, LivingEntity boss) {
        Vec3 p = boss.position().add(0, boss.getBbHeight() * 0.5, 0);

        // 金色裂縫（使用粒子爆發）
        level.sendParticles(ParticleTypes.ENCHANTED_HIT,
            p.x, p.y, p.z, 60, 0.8, 0.8, 0.8, 0.06);

        // 身體崩解為金色星塵
        for (int i = 0; i < 50; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                p.x + (level.random.nextDouble() - 0.5) * 2,
                p.y + level.random.nextDouble() * 2.5,
                p.z + (level.random.nextDouble() - 0.5) * 2,
                2, 0.1, 0.1, 0.1, 0.04);
        }

        // 光柱
        for (int i = 0; i < 30; i++) {
            level.sendParticles(ParticleTypes.FIREWORK,
                p.x + (level.random.nextDouble() - 0.5) * 0.5,
                p.y + level.random.nextDouble() * 4.0,
                p.z + (level.random.nextDouble() - 0.5) * 0.5,
                2, 0, 0.1, 0, 0.03);
        }

        // 全螢幕閃白由客戶端處理
        CombatSoundHelper.play(level, p, CombatSoundHelper.Layer.KILL, Force.RESONANCE);
    }

    /**
     * L4 區域淨化特效（使用肉球印章時）
     * 
     * 設計書 3.5：
     * - 巨大金色貓掌印從天而降
     * - 金色衝擊波向四周擴散
     * - 黑泥方塊轉化為星塵土壤
     * - 天空烏雲短暫散開
     */
    public static void regionPurify(ServerLevel level, Vec3 center) {
        // 巨大貓掌印衝擊
        for (int ring = 1; ring <= 5; ring++) {
            double radius = ring * 2.0;
            int points = 12 + ring * 4;
            for (int i = 0; i < points; i++) {
                double ang = i * Math.PI * 2 / points;
                level.sendParticles(ParticleTypes.END_ROD,
                    center.x + Math.cos(ang) * radius,
                    center.y + 1.0,
                    center.z + Math.sin(ang) * radius,
                    2, 0, 0.1, 0, 0.02);
            }
        }

        // 金色衝擊波
        level.sendParticles(ParticleTypes.FIREWORK,
            center.x, center.y + 0.5, center.z,
            40, 2.5, 0.2, 2.5, 0.05);

        // 淨化完成後的陽光粒子
        for (int i = 0; i < 20; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                center.x + (level.random.nextDouble() - 0.5) * 8,
                center.y + 0.2 + level.random.nextDouble() * 2.0,
                center.z + (level.random.nextDouble() - 0.5) * 8,
                1, 0, 0.02, 0, 0.01);
        }

        CombatSoundHelper.play(level, center, CombatSoundHelper.Layer.BOSS, Force.RESONANCE);
    }

    /**
     * L4 蒸餾打擊特效
     */
    public static void distillStrike(ServerLevel level, LivingEntity target) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.5, 0);
        level.sendParticles(ParticleTypes.FIREWORK, p.x, p.y, p.z, 30, 0.5, 0.5, 0.5, 0.06);
        level.sendParticles(ParticleTypes.END_ROD, p.x, p.y + 0.5, p.z, 20, 0.3, 0.3, 0.3, 0.04);
    }

    // ========================================================================
    // 原有方法保留
    // ========================================================================

    public static void onHit(ServerLevel level, LivingEntity target, Force force, boolean crit) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.6, 0);
        if (crit) CombatSoundHelper.play(level, p, CombatSoundHelper.Layer.CRIT, force);
        ParticleOptions main = hitParticle(force, crit);
        level.sendParticles(main, p.x, p.y, p.z, crit ? 20 : 10, 0.3, 0.3, 0.3, 0.04);
        if (crit) {
            level.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 8, 0.2, 0.2, 0.2, 0.1);
        }
    }

    public static void onKill(ServerLevel level, LivingEntity target, Force force) {
        Vec3 p = target.position().add(0, target.getBbHeight() * 0.5, 0);
        CombatSoundHelper.play(level, p, CombatSoundHelper.Layer.KILL, force);
        switch (force) {
            case RESONANCE -> {
                level.sendParticles(ModParticles.PURR_WAVE.get(), p.x, p.y, p.z, 30, 0.4, 0.6, 0.4, 0.08);
                level.sendParticles(ModParticles.VELVET_DRIFT.get(), p.x, p.y + 1, p.z, 20, 0.2, 0.5, 0.2, 0.05);
            }
            case SHADOW -> level.sendParticles(ModParticles.SHADOW_FEATHER.get(), p.x, p.y, p.z, 40, 0.5, 0.5, 0.5, 0.06);
            case CHAOS -> level.sendParticles(ModParticles.CHAOS_CONFETTI.get(), p.x, p.y, p.z, 35, 0.5, 0.5, 0.5, 0.12);
        }
    }

    public static void bossIntro(ServerLevel level, Vec3 pos, String kind) {
        CombatSoundHelper.play(level, pos, CombatSoundHelper.Layer.BOSS, Force.SHADOW);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 1, pos.z, 80, 1.2, 2.0, 1.2, 0.02);
        switch (kind) {
            case "shadow_claw", "primal_chaos" ->
                level.sendParticles(ParticleTypes.SQUID_INK, pos.x, pos.y, pos.z, 50, 1.0, 0.5, 1.0, 0.04);
            case "howling_squall" ->
                level.sendParticles(ParticleTypes.SPLASH, pos.x, pos.y + 2, pos.z, 60, 1.5, 0.3, 1.5, 0.1);
            default ->
                level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 40, 0.8, 0.4, 0.8, 0.03);
        }
    }

    public static void bossPhaseShift(ServerLevel level, Vec3 pos, int phase) {
        level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1, pos.z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1, pos.z, 15 + phase * 10, 0.6, 0.8, 0.6, 0.06);
    }

    public static void blackMudExplosion(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.SQUID_INK, pos.x, pos.y, pos.z, 45, 2.0, 0.3, 2.0, 0.05);
        level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.5, pos.z, 30, 1.5, 0.5, 1.5, 0.02);
    }

    private static ParticleOptions hitParticle(Force force, boolean crit) {
        if (force == Force.RESONANCE) return crit ? ModParticles.PURR_WAVE.get() : ModParticles.VELVET_DRIFT.get();
        if (force == Force.SHADOW) return crit ? ParticleTypes.REVERSE_PORTAL : ModParticles.SHADOW_FEATHER.get();
        return crit ? ModParticles.CHAOS_CONFETTI.get() : ParticleTypes.WITCH;
    }

    private static void spawnRing(ServerLevel level, Vec3 center, double radius, ParticleOptions particle, int points) {
        for (int i = 0; i < points; i++) {
            double ang = i * Math.PI * 2 / points;
            level.sendParticles(particle,
                    center.x + Math.cos(ang) * radius, center.y + 0.15, center.z + Math.sin(ang) * radius,
                    2, 0, 0.05, 0, 0.01);
        }
    }

    private static void spawnRising(ServerLevel level, ServerPlayer player, ParticleOptions particle, int count) {
        level.sendParticles(particle, player.getX(), player.getY() + 0.2, player.getZ(),
                count, 0.4, 0.1, 0.4, 0.03);
    }

    private static void spawnBurst(ServerLevel level, Vec3 center, ParticleOptions particle, int count) {
        level.sendParticles(particle, center.x, center.y + 0.5, center.z, count, 0.5, 0.4, 0.5, 0.05);
    }
}