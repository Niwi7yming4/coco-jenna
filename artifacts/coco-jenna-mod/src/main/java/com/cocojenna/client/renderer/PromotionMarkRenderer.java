package com.cocojenna.client.renderer;

import com.cocojenna.sequence.PromotionCeremonyHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

/**
 * 序列印記渲染器
 * 
 * 設計書 2.1 第五階段：
 * 根據玩家當前序列等級，在武器或身體上出現永久性微小發光印記：
 * 序列9-7：無印記
 * 序列6-4：手背上出現微弱的對應顏色光點
 * 序列3-2：光點擴大為小型紋路，武器上出現對應顏色的光澤
 * 序列1：紋路完整浮現，在黑暗中會微微發光
 */
public class PromotionMarkRenderer {

    public static final PromotionMarkRenderer INSTANCE = new PromotionMarkRenderer();

    // 當前玩家印記設定
    private int currentMarkLevel = 0;       // 0=無, 1=光點, 2=紋路, 3=完整
    private String currentMarkForce = "";    // resonance/shadow/chaos
    private int glowTicks = 0;              // 身體發光計時器

    // 身體發光效果
    private boolean bodyGlowActive = false;
    private int bodyGlowDuration = 0;
    private String bodyGlowForce = "";

    // 印記紋理
    private static final ResourceLocation MARK_TEXTURE = 
        new ResourceLocation("cocojenna:textures/entity/promotion_mark.png");

    private PromotionMarkRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 觸發短暫身體發光（儀式第三階段）
     */
    public void triggerBodyGlow(Player player, String force, int duration) {
        bodyGlowActive = true;
        bodyGlowDuration = duration;
        bodyGlowForce = force;
        glowTicks = 0;
    }

    /**
     * 設定永久印記
     */
    public void setPermanentMark(Player player, String force, int markLevel) {
        currentMarkLevel = markLevel;
        currentMarkForce = force;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        var player = event.getEntity();
        var poseStack = event.getPoseStack();
        var buffer = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();

        // 更新計時器
        if (bodyGlowActive) {
            glowTicks++;
            if (glowTicks > bodyGlowDuration) {
                bodyGlowActive = false;
            }
        }

        // 渲染身體發光
        if (bodyGlowActive) {
            renderBodyGlow(poseStack, player, buffer, packedLight);
        }

        // 渲染永久印記
        if (currentMarkLevel > 0) {
            renderPermanentMark(poseStack, player, buffer, packedLight);
        }
    }

    private void renderBodyGlow(PoseStack poseStack, Player player,
                                 MultiBufferSource buffer, int packedLight) {
        float progress = 1.0f - (float) glowTicks / bodyGlowDuration;
        int alpha = (int)(progress * 150);

        if (alpha <= 0) return;

        int color = switch (bodyGlowForce) {
            case "resonance" -> (alpha << 24) | 0xFFD700;
            case "shadow"    -> (alpha << 24) | 0x9B30FF;
            case "chaos"     -> (alpha << 24) | 0xFF69B4;
            default          -> (alpha << 24) | 0xFFFFFF;
        };

        // 使用玩家模型渲染透明外光
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);

        // 這裡可以使用全螢幕著色器效果，或簡單的透明覆蓋
        // 簡化版：在玩家位置渲染一個光暈
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private void renderPermanentMark(PoseStack poseStack, Player player,
                                      MultiBufferSource buffer, int packedLight) {
        if (currentMarkLevel <= 0 || currentMarkForce.isEmpty()) return;

        // 印記顏色
        int color = switch (currentMarkForce) {
            case "resonance" -> 0xFFFFD700;
            case "shadow"    -> 0xFF9B30FF;
            case "chaos"     -> 0xFFFF69B4;
            default          -> 0xFFFFFFFF;
        };

        // 序列1：在黑暗中會微微發光
        boolean glowInDark = currentMarkLevel >= 3;

        // 根據印記等級決定透明度
        int alpha = switch (currentMarkLevel) {
            case 1 -> 60;  // 微弱光點
            case 2 -> 120; // 明顯紋路
            case 3 -> 200; // 完整紋路
            default -> 0;
        };

        // 浮空展示印記在玩家右手上方
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        var matrix = poseStack.last().pose();
        var builder = buffer.getBuffer(RenderType.lines());

        // 渲染小型印記符號（簡化為粒子點）
        float yOffset = currentMarkLevel >= 3 ? 0.8f : 0.6f;
        float size = 0.05f + currentMarkLevel * 0.03f;

        // 使用頂點渲染印記位置
        builder.vertex(matrix, 0, yOffset, 0)
            .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, alpha)
            .endVertex();

        RenderSystem.disableBlend();
    }

    /**
     * 獲取玩家目前印記等級（由其他系統查詢）
     */
    public int getCurrentMarkLevel() { return currentMarkLevel; }
    public String getCurrentMarkForce() { return currentMarkForce; }

    /**
     * 發送NPC反應（序列1專屬）
     */
    public boolean hasFullMark(int markLevel) {
        return markLevel >= 3;
    }
}