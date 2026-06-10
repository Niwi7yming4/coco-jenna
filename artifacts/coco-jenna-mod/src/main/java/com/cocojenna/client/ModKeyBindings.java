package com.cocojenna.client;

import com.cocojenna.CocoJennaMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyBindings {

    public static final String CATEGORY = "key.categories.cocojenna";

    public static KeyMapping SKILL_WHEEL;
    public static KeyMapping SKILL_EQUIP;
    public static KeyMapping INTERACT_CAT;
    public static KeyMapping OPEN_MEMORY_BOOK;
    public static KeyMapping TOGGLE_FOLLOW;
    public static KeyMapping RECALL_CATS;
    public static KeyMapping DASH;

    public static KeyMapping DISTILL_STRIKE;

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        SKILL_WHEEL = new KeyMapping("key.cocojenna.skill_wheel",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
        SKILL_EQUIP = new KeyMapping("key.cocojenna.skill_equip",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
        INTERACT_CAT = new KeyMapping("key.cocojenna.interact_cat",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
        OPEN_MEMORY_BOOK = new KeyMapping("key.cocojenna.memory_book",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY);
        TOGGLE_FOLLOW = new KeyMapping("key.cocojenna.toggle_follow",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
        RECALL_CATS = new KeyMapping("key.cocojenna.recall_cats",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
        DASH = new KeyMapping("key.cocojenna.dash",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, CATEGORY);
        DISTILL_STRIKE = new KeyMapping("key.cocojenna.distill_strike",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
        event.register(SKILL_WHEEL);
        event.register(SKILL_EQUIP);
        event.register(INTERACT_CAT);
        event.register(OPEN_MEMORY_BOOK);
        event.register(TOGGLE_FOLLOW);
        event.register(RECALL_CATS);
        event.register(DASH);
        event.register(DISTILL_STRIKE);
    }
}
