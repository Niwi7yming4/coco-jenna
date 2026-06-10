package com.cocojenna.client;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.PlayerActionPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    private static boolean dashWasDown;
    private static long lastShiftPress;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        SkillWheelOverlay.render(event.getGuiGraphics(), w, h);
        CatInteractRadialOverlay.render(event.getGuiGraphics(), w, h);
        com.cocojenna.client.gui.TutorialHudOverlay.INSTANCE.render(event.getGuiGraphics(), w, h);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (ModKeyBindings.SKILL_EQUIP != null && ModKeyBindings.SKILL_EQUIP.consumeClick()) {
            com.cocojenna.client.gui.SkillEquipmentScreen.open();
        }
        if (ModKeyBindings.OPEN_MEMORY_BOOK != null && ModKeyBindings.OPEN_MEMORY_BOOK.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new PlayerActionPacket(PlayerActionPacket.Action.OPEN_MEMORY_BOOK));
        }
        if (ModKeyBindings.TOGGLE_FOLLOW != null && ModKeyBindings.TOGGLE_FOLLOW.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new PlayerActionPacket(PlayerActionPacket.Action.TOGGLE_FOLLOW));
        }
        if (ModKeyBindings.RECALL_CATS != null && ModKeyBindings.RECALL_CATS.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new PlayerActionPacket(PlayerActionPacket.Action.RECALL_CATS));
        }
        if (ModKeyBindings.DISTILL_STRIKE != null && ModKeyBindings.DISTILL_STRIKE.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new com.cocojenna.network.DistillStrikePacket());
        }

        RegionalAmbientParticles.tick();

        if (ModKeyBindings.DASH != null) {
            boolean down = ModKeyBindings.DASH.isDown();
            long now = System.currentTimeMillis();
            if (down && !dashWasDown) {
                if (now - lastShiftPress < 350) {
                    ModNetwork.CHANNEL.sendToServer(new PlayerActionPacket(PlayerActionPacket.Action.DASH));
                }
                lastShiftPress = now;
            }
            dashWasDown = down;
        }
    }
}
