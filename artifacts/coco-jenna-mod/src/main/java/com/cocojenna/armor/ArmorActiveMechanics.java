package com.cocojenna.armor;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/** 防具主動機制 — 瀕死覺醒、充能護盾、變形防具. */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class ArmorActiveMechanics {

    private static final UUID AWAKEN_SPEED = UUID.fromString("c0c0a1a2-b3c4-5678-9abc-def012345701");
    private static final UUID MORPH_ARMOR = UUID.fromString("c0c0a1a2-b3c4-5678-9abc-def012345702");
    private static final long AWAKEN_COOLDOWN = 24000;
    private static final int SHIELD_MAX = 100;

    private ArmorActiveMechanics() {}

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        float after = player.getHealth() - event.getAmount();
        if (after > 0 && after / player.getMaxHealth() > 0.15f) return;
        if (bond.getArmorShieldCharge() >= 30) {
            int absorb = Math.min((int) event.getAmount(), bond.getArmorShieldCharge() / 3);
            bond.setArmorShieldCharge(bond.getArmorShieldCharge() - absorb * 3);
            event.setAmount(Math.max(0, event.getAmount() - absorb));
        }
        if (after <= 0 && tryAwaken(player, bond)) {
            event.setAmount(Math.max(0, player.getHealth() - 1f));
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        clear(player, AWAKEN_SPEED);
        clear(player, MORPH_ARMOR);

        boolean velvet = CatArmorSet.VELVET_BEGINNER.isWearingFullSet(player);
        boolean moon = CatArmorSet.MOONLIGHT.isWearingFullSet(player);
        if (!velvet && !moon) return;

        if (player.isCrouching() && player.tickCount % 20 == 0) {
            bond.setArmorShieldCharge(Math.min(SHIELD_MAX, bond.getArmorShieldCharge() + 4));
        }
        if (bond.getArmorShieldUntil() > player.level().getGameTime()) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, true, false, true));
        }
        if (bond.isArmorAwakened()) {
            add(player, Attributes.MOVEMENT_SPEED, AWAKEN_SPEED, 0.15);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, false, true));
        }
        if (bond.getArmorMorphForm() == 1) {
            add(player, Attributes.ARMOR, MORPH_ARMOR, 4);
        } else if (velvet || moon) {
            add(player, Attributes.ARMOR_TOUGHNESS, MORPH_ARMOR, 2);
        }
        if (player.tickCount % 20 == 0 && player.isCrouching()
                && !player.onGround() && player.getDeltaMovement().y > 0.08) {
            bond.setArmorMorphForm(bond.getArmorMorphForm() == 0 ? 1 : 0);
            player.displayClientMessage(Component.translatable(
                    bond.getArmorMorphForm() == 1
                            ? "armor.cocojenna.morph_offense"
                            : "armor.cocojenna.morph_guard"), true);
        }
    }

    public static boolean tryAwaken(ServerPlayer player, BondData bond) {
        if (bond.isArmorAwakened()) return false;
        if (player.level().getGameTime() < bond.getArmorAwakenCd()) return false;
        if (!CatArmorSet.VELVET_BEGINNER.isWearingFullSet(player)
                && !CatArmorSet.MOONLIGHT.isWearingFullSet(player)) {
            return false;
        }
        bond.setArmorAwakened(true);
        bond.setArmorAwakenCd(player.level().getGameTime() + AWAKEN_COOLDOWN);
        bond.setArmorShieldUntil(player.level().getGameTime() + 200);
        bond.setArmorShieldCharge(SHIELD_MAX);
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
        player.displayClientMessage(Component.translatable("armor.cocojenna.near_death_awaken"), true);
        player.level().playSound(null, player.blockPosition(),
                net.minecraft.sounds.SoundEvents.TOTEM_USE, net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 1.1f);
        return true;
    }

    public static void resetAwaken(ServerPlayer player, BondData bond) {
        if (!bond.isArmorAwakened()) return;
        if (player.level().getGameTime() > bond.getArmorShieldUntil()) {
            bond.setArmorAwakened(false);
        }
    }

    private static void add(ServerPlayer player, net.minecraft.world.entity.ai.attributes.Attribute attr,
                            UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst != null && inst.getModifier(id) == null) {
            inst.addTransientModifier(new AttributeModifier(id, "armor_active", amount,
                    AttributeModifier.Operation.ADDITION));
        }
    }

    private static void clear(ServerPlayer player, UUID id) {
        for (var attr : new net.minecraft.world.entity.ai.attributes.Attribute[]{
                Attributes.MOVEMENT_SPEED, Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS}) {
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null) inst.removeModifier(id);
        }
    }
}
