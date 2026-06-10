package com.cocojenna.gear;

import com.cocojenna.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/** 套裝效果（設計書第九章）— 依裝備欄與背包內套裝件數套用. */
public final class SetBonusHelper {

    private SetBonusHelper() {}

    public static void refresh(Player player) {
        if (player.level().isClientSide) return;
        Map<String, Integer> counts = countSets(player);
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.removeEffect(MobEffects.DAMAGE_BOOST);
        player.removeEffect(MobEffects.WATER_BREATHING);

        int dawn = counts.getOrDefault("dawn", 0);
        if (dawn >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0, true, false));
        }
        int blind = counts.getOrDefault("blind_water", 0);
        if (blind >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1, true, false));
        }
        int velvet = counts.getOrDefault("velvet_tail", 0);
        if (velvet >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, true, false));
        }
        int rust = counts.getOrDefault("rust", 0);
        if (rust >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, true, false));
        }
        if (dawn >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, true, false));
        }
        if (velvet >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 0, true, false));
        }
        if (blind >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 200, 0, true, false));
        }
        if (rust >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, true, false));
        }
    }

    public static void appendSetTooltip(ItemStack stack, java.util.List<Component> tooltip) {
        String set = getSetName(stack);
        if (set != null) {
            tooltip.add(Component.translatable("set.cocojenna.name." + set));
        }
    }

    private static Map<String, Integer> countSets(Player player) {
        Map<String, Integer> map = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            add(map, player.getItemBySlot(slot));
        }
        for (ItemStack stack : player.getInventory().items) {
            add(map, stack);
        }
        return map;
    }

    private static void add(Map<String, Integer> map, ItemStack stack) {
        String set = getSetName(stack);
        if (set != null) {
            map.merge(set, 1, Integer::sum);
        }
    }

    public static String getSetName(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.is(ModItems.RYOKATANA_DAWN_HOPE.get())
                || stack.is(ModItems.DAIKATANA_FIRST_DAWN.get())) {
            return "dawn";
        }
        if (stack.is(ModItems.DAIKATANA_ABYSS.get())
                || stack.is(ModItems.MUSOU_ABYSS_DEPTH.get())) {
            return "blind_water";
        }
        if (stack.is(ModItems.VELVET_TAIL_CAPE.get())) {
            return "velvet_tail";
        }
        if (stack.is(ModItems.RYOKATANA_PRECISION_GEAR.get())) {
            return "rust";
        }
        return null;
    }

    public static float damageMultiplier(Player attacker, net.minecraft.world.entity.LivingEntity target) {
        Map<String, Integer> counts = countSets(attacker);
        float mult = 1.0f;
        if (counts.getOrDefault("dawn", 0) >= 2 && attacker.level().isDay()) {
            mult += 0.15f;
        }
        if (counts.getOrDefault("blind_water", 0) >= 4 && attacker.isInWater()) {
            mult += 0.20f;
        }
        if (counts.getOrDefault("rust", 0) >= 4) {
            mult += 0.30f;
        }
        if (counts.getOrDefault("velvet_tail", 0) >= 4 && attacker.isSprinting()) {
            mult += 0.10f;
        }
        if (counts.getOrDefault("dawn", 0) >= 4 && !attacker.level().isDay()) {
            mult += 0.12f;
        }
        return mult;
    }

    public static void notifyBonusHud(ServerPlayer player) {
        // Reserved for future HUD icons (design 9.4)
    }
}
