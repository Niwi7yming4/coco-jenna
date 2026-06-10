package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.LinkedHashMap;
import java.util.Map;

/** 15 種隱藏序列（設計書）. */
public final class HiddenSequenceRegistry {

    private static final Map<String, Integer> INDEX = new LinkedHashMap<>();

    static {
        String[] ids = {
                "hibiscus_distiller", "defeated_stray", "imaginary_walker", "velvet_dreamer",
                "blind_ferryman", "gear_orphan", "moon_alley_thief", "sleep_cathedral_ghost",
                "howling_wind_rider", "labyrinth_cartographer", "first_cry_oracle", "cheshire_merchant",
                "white_glove_soul", "alpha_observer", "primal_survivor",
                "grief_amalgam", "blind_water_lord", "fallen_velvet", "primal_chaos",
                "thousand_face"
        };
        for (int i = 0; i < ids.length; i++) {
            INDEX.put(ids[i], i);
        }
    }

    private HiddenSequenceRegistry() {}

    public static boolean has(BondData bond, String id) {
        Integer idx = INDEX.get(id);
        if (idx == null) return false;
        return (bond.getHiddenSequences() & (1L << idx)) != 0;
    }

    public static void tryUnlock(ServerPlayer player, String id) {
        Integer idx = INDEX.get(id);
        if (idx == null) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        long mask = 1L << idx;
        if ((bond.getHiddenSequences() & mask) != 0) return;
        bond.setHiddenSequences(bond.getHiddenSequences() | mask);
        grantHiddenWeapon(player, id);
        player.displayClientMessage(Component.translatable(
                "hidden_sequence.cocojenna.unlocked",
                Component.translatable("hidden_sequence.cocojenna." + id)), true);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    private static void grantHiddenWeapon(ServerPlayer player, String id) {
        String weapon = switch (id) {
            case "hibiscus_distiller" -> "hibiscus_blood";
            case "defeated_stray" -> "fish_bone_tide";
            case "imaginary_walker" -> "moon_shadow";
            case "velvet_dreamer" -> "velvet_whisper";
            case "blind_ferryman" -> "blind_water_core";
            case "gear_orphan" -> "gear_windup";
            case "moon_alley_thief" -> "cheshire_grin";
            case "sleep_cathedral_ghost" -> "forgotten_page";
            case "howling_wind_rider" -> "stardust_step";
            case "labyrinth_cartographer" -> "paper_crow_ink";
            case "first_cry_oracle" -> "first_cry_memory";
            case "cheshire_merchant" -> "cheshire_grin";
            case "white_glove_soul" -> "white_glove_guide";
            case "alpha_observer" -> "alpha_observe";
            case "primal_survivor" -> "lament_split";
            case "grief_amalgam" -> "whisper_mud";
            case "blind_water_lord" -> "deep_sea_current";
            case "fallen_velvet" -> "fallen_velvet_claw";
            case "primal_chaos" -> "dark_tide";
            case "thousand_face" -> "mimic_disguise";
            default -> null;
        };
        if (weapon == null) return;
        var item = com.cocojenna.item.RyokatanaRegistry.get(weapon);
        if (item == null) return;
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item.get());
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
