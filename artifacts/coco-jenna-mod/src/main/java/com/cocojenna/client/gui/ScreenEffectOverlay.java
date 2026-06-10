package com.cocojenna.client.gui;

import com.cocojenna.sequence.PromotionCeremonyHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 螢幕特效疊層
 * 管理：螢幕閃白、終極技邊框、震動、Boss擊殺、雙子星連攜、區域淨化
 * 
 * 設計書 3.4-3.5, 第五章
 */
public class ScreenEffectOverlay {

    public static final ScreenEffectOverlay INSTANCE = new ScreenEffectOverlay();

    // ── 閃白效果 ──────────────────────────────────────────────────────────
    private int flashAlpha = 0;          // 0~255
    private int flashDuration = 0;       // ticks remaining
    private static final int FLASH_MAX = 4; // 0.2秒

    // ── 終極技邊框 ────────────────────────────────────────────────────────
    private int vignetteAlpha = 0;
    private int vignetteDuration = 0;
    private String vignetteForce = "";
    private static final int VIGNETTE_MAX = 20; // 1秒

    // ── 螢幕震動 ──────────────────────────────────────────────────────────
    public float shakeIntensity = 0f;
    private int shakeDuration = 0;

    // ── Boss擊殺特效 ──────────────────────────────────────────────────────
    private int bossKillAlpha = 0;
    private int bossKillDuration = 0;
    private String bossKillForce = "";

    // ── 雙子星連攜 ────────────────────────────────────────────────────────
    private boolean twinStarActive = false;
    private int twinStarAlpha = 0;
    private int twinStarTimer = 0;

    // ── 區域淨化 ──────────────────────────────────────────────────────────
    private boolean purifyActive = false;
    private int purifyAlpha = 0;
    private int purifyTimer = 0;
    private String purifyForce = "";

    // ── 低血量警示 ────────────────────────────────────────────────────────
    private boolean lowHealthWarning = false;

    private ScreenEffectOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // ======================================================================
    // 觸發方法
    // ======================================================================

    public void triggerFlash(int intensityTicks) {
        flashAlpha = 255;
        flashDuration = Math.max(1, intensityTicks);
    }

    public void triggerUltimateVignette(String force, int durationTicks) {
        vignetteAlpha = 180;
        vignetteDuration = Math.max(10, durationTicks);
        vignetteForce = force;
    }

    public void triggerScreenShake(int intensityTicks) {
        shakeIntensity = 1.0f;
        shakeDuration = Math.max(5, intensityTicks);
    }

    public void triggerBossKillEffect(String force) {
        bossKillAlpha = 255;
        bossKillDuration = 60; // 3秒
        bossKillForce = force;
        // 觸發全螢幕閃白
        triggerFlash(4);
    }

    public void triggerRegionPurify(String force) {
        purifyActive = true;
        purifyAlpha = 200;
        purifyTimer = 100; // 5秒
        purifyForce = force;
    }

    public void triggerTwinStarBond() {
        twinStarActive = true;
        twinStarAlpha = 200;
        twinStarTimer = 60; // 3秒
    }

    public void setLowHealthWarning(boolean warning) {
        lowHealthWarning = warning;
    }

    // ======================================================================
    // Tick 更新（每幀由渲染循環調用）
    // ======================================================================

    public void tick() {
        if (flashDuration > 0) {
            flashDuration--;
            flashAlpha = Math.max(0, flashAlpha - (255 / FLASH_MAX));
        }
        if (vignetteDuration > 0) {
            vignetteDuration--;
            vignetteAlpha = Math.max(0, vignetteAlpha - (180 / Math.max(1, VIGNETTE_MAX)));
        }
        if (shakeDuration > 0) {
            shakeDuration--;
            shakeIntensity = shakeDuration * 0.05f;
        }
        if (bossKillDuration > 0) {
            bossKillDuration--;
            bossKillAlpha = Math.max(0, bossKillAlpha - (255 / 60));
        }
        if (purifyActive) {
            purifyTimer--;
            purifyAlpha = Math.max(0, purifyAlpha - (200 / 100));
            if (purifyTimer <= 0) purifyActive = false;
        }
        if (twinStarActive) {
            twinStarTimer--;
            twinStarAlpha = Math.max(0, twinStarAlpha - (200 / 60));
            if (twinStarTimer <= 0) twinStarActive = false;
        }
    }

    // ======================================================================
    // 渲染
    // ======================================================================

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;

        var mc = Minecraft.getInstance();
        var g = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        tick();

        // 1. 低血量紅色脈動邊框
        if (lowHealthWarning) {
            renderLowHealthVignette(g, w, h);
        }

        // 2. 終極技邊框
        if (vignetteAlpha > 0) {
            renderVignette(g, w, h, vignetteForce, vignetteAlpha);
        }

        // 3. 螢幕閃白
        if (flashAlpha > 0) {
            renderFlash(g, w, h, flashAlpha);
        }

        // 4. Boss擊殺特效
        if (bossKillAlpha > 0) {
            renderBossKillEffect(g, w, h, bossKillForce, bossKillAlpha);
        }

        // 5. 區域淨化特效
        if (purifyActive && purifyAlpha > 0) {
            renderPurifyEffect(g, w, h, purifyForce, purifyAlpha);
        }

        // 6. 雙子星連攜
        if (twinStarActive && twinStarAlpha > 0) {
            renderTwinStarEffect(g, w, h, twinStarAlpha);
        }
    }

    private void renderFlash(GuiGraphics g, int w, int h, int alpha) {
        g.fill(0, 0, w, h, (alpha << 24) | 0xFFFFFF);
    }

    private void renderVignette(GuiGraphics g, int w, int h, String force, int alpha) {
        int color = forceColor(force, alpha);
        int size = Math.min(w, h) / 5;

        // 上
        g.fill(0, 0, w, size, color);
        // 下
        g.fill(0, h - size, w, h, color);
        // 左
        g.fill(0, 0, size, h, color);
        // 右
        g.fill(w - size, 0, w, h, color);
    }

    private void renderLowHealthVignette(GuiGraphics g, int w, int h) {
        int alpha = (int)(80 + Math.sin(System.currentTimeMillis() / 200.0) * 40);
        int color = (alpha << 24) | 0xFF0000;
        int size = Math.min(w, h) / 6;
        g.fill(0, 0, w, size, color);
        g.fill(0, h - size, w, h, color);
        g.fill(0, 0, size, h, color);
        g.fill(w - size, 0, w, h, color);
    }

    private void renderBossKillEffect(GuiGraphics g, int w, int h, String force, int alpha) {
        // 金色星塵從中心向上飄升形成光柱
        int centerX = w / 2;
        int centerY = h / 2;
        int size = Math.min(w, h) / 8;

        // 中心光柱
        int glowColor = (alpha << 24) | 0xFFD700;
        g.fill(centerX - size / 2, 0, centerX + size / 2, centerY, glowColor);

        // 記憶之花標記（短暫存在）
        ResourceLocation flowerTex = new ResourceLocation("cocojenna:textures/gui/memory_flower.png");
        RenderSystem.enableBlend();
        g.blit(flowerTex, centerX - 16, centerY - 16, 32, 32, 0, 0, 32, 32, 32, 32);
        RenderSystem.disableBlend();
    }

    private void renderPurifyEffect(GuiGraphics g, int w, int h, String force, int alpha) {
        // 巨大金色貓掌印從天而降的衝擊波
        int centerX = w / 2;
        int centerY = h / 2;

        // 貓掌印UI疊層
        ResourceLocation pawTex = new ResourceLocation("cocojenna:textures/gui/purify_paw.png");
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 0.5f, alpha / 255.0f);
        g.blit(pawTex, centerX - 32, centerY - 32, 64, 64, 0, 0, 64, 64, 64, 64);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private void renderTwinStarEffect(GuiGraphics g, int w, int h, int alpha) {
        // 可可與珍奶的半透明剪影
        int centerX = w / 2;
        int centerY = h / 2 - 20;

        ResourceLocation twinTex = new ResourceLocation("cocojenna:textures/gui/twin_star_bond.png");
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        g.blit(twinTex, centerX - 48, centerY - 24, 96, 48, 0, 0, 96, 48, 96, 48);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private static int forceColor(String force, int alpha) {
        int base = switch (force) {
            case "resonance" -> 0xFFD700;
            case "shadow"    -> 0x9B30FF;
            case "chaos"     -> 0xFF69B4;
            default          -> 0xFFFFFF;
        };
        return (alpha << 24) | (base & 0xFFFFFF);
    }
}