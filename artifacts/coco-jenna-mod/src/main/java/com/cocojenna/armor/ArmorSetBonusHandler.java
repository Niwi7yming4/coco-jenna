package com.cocojenna.armor;

import com.cocojenna.CocoJennaMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class ArmorSetBonusHandler {

    private static final UUID VELVET_EMOTION = UUID.fromString("c0c0a1a2-b3c4-5678-9abc-def012345601");
    private static final UUID MOON_SPEED = UUID.fromString("c0c0a1a2-b3c4-5678-9abc-def012345602");

    private ArmorSetBonusHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (event.player.tickCount % 40 != 0) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        clear(player, VELVET_EMOTION);
        clear(player, MOON_SPEED);

        if (CatArmorSet.VELVET_BEGINNER.isWearingFullSet(player)) {
            add(player, Attributes.LUCK, VELVET_EMOTION, 0.5);
        }
        if (CatArmorSet.MOONLIGHT.isWearingFullSet(player)) {
            long time = player.level().getDayTime() % 24000L;
            boolean night = time > 13000 && time < 23000;
            if (night) {
                add(player, Attributes.MOVEMENT_SPEED, MOON_SPEED, 0.2);
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0, true, false, false));
            }
        }
    }

    private static void add(ServerPlayer player, net.minecraft.world.entity.ai.attributes.Attribute attr,
            UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst != null && inst.getModifier(id) == null) {
            inst.addTransientModifier(new AttributeModifier(id, "cat_armor_set", amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private static void clear(ServerPlayer player, UUID id) {
        for (var attr : new net.minecraft.world.entity.ai.attributes.Attribute[]{
                Attributes.LUCK, Attributes.MOVEMENT_SPEED}) {
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null) inst.removeModifier(id);
        }
    }
}
