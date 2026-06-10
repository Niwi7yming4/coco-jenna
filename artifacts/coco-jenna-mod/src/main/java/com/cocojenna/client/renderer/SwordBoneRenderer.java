package com.cocojenna.client.renderer;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.swordbone.SwordBoneEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

import java.util.List;

/** 劍骨外觀 — 以武器材質替換發光線條（設計書 1.4）. */
public class SwordBoneRenderer {

    public static final SwordBoneRenderer INSTANCE = new SwordBoneRenderer();
    private int flashTicks;

    private static final float[][] SLOT_OFFSETS = {
            {0.35f, 1.45f, 0.15f},
            {-0.35f, 1.45f, 0.15f},
            {0f, 1.1f, -0.25f},
            {0.28f, 0.95f, 0.05f},
            {-0.28f, 0.95f, 0.05f},
            {0.22f, 0.75f, -0.2f},
            {-0.22f, 0.75f, -0.2f},
            {0f, 1.55f, -0.18f},
            {0f, 1.85f, 0f},
    };

    private SwordBoneRenderer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void triggerFlash() {
        flashTicks = 4;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        var bond = ModCapabilities.get(player).orElse(null);
        if (bond == null) return;
        List<SwordBoneEntry> bones = bond.getSwordBones();
        if (bones.isEmpty()) return;

        if (flashTicks > 0) flashTicks--;

        PoseStack pose = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        int light = event.getPackedLight();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        pose.pushPose();
        float bodyYaw = player.yBodyRot;
        pose.mulPose(Axis.YP.rotationDegrees(-bodyYaw));

        for (int i = 0; i < bones.size() && i < SLOT_OFFSETS.length; i++) {
            SwordBoneEntry entry = bones.get(i);
            float[] off = SLOT_OFFSETS[i];
            ItemStack stack = weaponStack(entry.weaponId());
            if (stack.isEmpty()) continue;

            pose.pushPose();
            pose.translate(off[0], off[1], off[2]);
            pose.mulPose(Axis.YP.rotationDegrees((i * 37 + player.tickCount) % 360));
            float scale = entry.damaged() ? 0.22f : 0.28f;
            if (flashTicks > 0) scale += 0.04f;
            pose.scale(scale, scale, scale);
            itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, light,
                    OverlayTexture.NO_OVERLAY, pose, buffer, player.level(), 0);
            if (entry.damaged()) {
                drawCrackOverlay(pose, buffer);
            }
            pose.popPose();
        }

        if (bones.size() >= 9 && bones.stream().noneMatch(SwordBoneEntry::damaged)) {
            drawAura(pose, buffer);
        }
        pose.popPose();
    }

    private static ItemStack weaponStack(String weaponId) {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("cocojenna", weaponId));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static void drawCrackOverlay(PoseStack pose, MultiBufferSource buffer) {
        RenderSystem.enableBlend();
        Matrix4f matrix = pose.last().pose();
        var builder = buffer.getBuffer(RenderType.lines());
        builder.vertex(matrix, -0.1f, 0f, 0f).color(0.3f, 0.3f, 0.3f, 0.8f).endVertex();
        builder.vertex(matrix, 0.1f, 0.2f, 0f).color(0.3f, 0.3f, 0.3f, 0.8f).endVertex();
        RenderSystem.disableBlend();
    }

    private static void drawAura(PoseStack pose, MultiBufferSource buffer) {
        Matrix4f matrix = pose.last().pose();
        var builder = buffer.getBuffer(RenderType.lines());
        for (int i = 0; i < 12; i++) {
            double ang = i * Math.PI * 2 / 12;
            float x = (float) Math.cos(ang);
            float z = (float) Math.sin(ang);
            builder.vertex(matrix, x, 0.1f, z).color(0.9f, 0.85f, 1f, 0.25f).endVertex();
            builder.vertex(matrix, x * 0.9f, 0.1f, z * 0.9f).color(0.7f, 0.6f, 1f, 0.15f).endVertex();
        }
    }
}
