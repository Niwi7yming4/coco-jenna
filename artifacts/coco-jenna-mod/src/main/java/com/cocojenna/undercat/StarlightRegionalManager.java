package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;

/** 星光 DLC 區域事件（ch4+）. */
public final class StarlightRegionalManager {

    public static final int FLAG_CH4_WASTES = 512;
    public static final int FLAG_CH5_ABYSS = 1024;
    public static final int FLAG_CH6_EPILOGUE = 2048;

    private StarlightRegionalManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (player.tickCount % 40 != 0) return;

        if (player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) {
            tickOverworld(player);
        } else if (player.level().dimension().equals(ModDimensions.UNDERCAT_DOMAIN)) {
            tickUndercat(player);
        }
    }

    private static void tickOverworld(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!StarlightChapterManager.hasFlag(bond, StarlightChapterManager.FLAG_FINALE)) return;

        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        if (biome.is(ModBiomes.FORGOTTEN_WASTES) && !hasFlag(bond, FLAG_CH4_WASTES)) {
            setFlag(bond, FLAG_CH4_WASTES);
            DialogueManager.play(player, "starlight_ch4_wastes");
            bond.addKingdomReputation(5);
            player.displayClientMessage(Component.translatable("starlight.cocojenna.ch4_hint"), true);
            return;
        }

        if (biome.is(ModBiomes.STARDUST_DESERT) && hasFlag(bond, FLAG_CH4_WASTES) && !hasFlag(bond, FLAG_CH5_ABYSS)) {
            if (!player.level().isDay()) {
                setFlag(bond, FLAG_CH5_ABYSS);
                DialogueManager.play(player, "starlight_ch5_abyss");
                giveOrDrop(player, new ItemStack(ModItems.STARDUST_SOIL_ITEM.get(), 2));
                player.displayClientMessage(Component.translatable("starlight.cocojenna.ch5_hint"), true);
            }
        }

        if (biome.is(ModBiomes.STARDUST_DESERT) && hasFlag(bond, FLAG_CH5_ABYSS)
                && !hasFlag(bond, FLAG_CH6_EPILOGUE) && bond.getSisterBond() >= 60f) {
            setFlag(bond, FLAG_CH6_EPILOGUE);
            DialogueManager.play(player, "starlight_ch6_epilogue");
            bond.modifySisterBond(10f);
            bond.addShadowCoins(30);
            player.displayClientMessage(Component.translatable("starlight.cocojenna.ch6_hint"), true);
        }
    }

    private static void tickUndercat(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!hasFlag(bond, FLAG_CH4_WASTES) || hasFlag(bond, FLAG_CH5_ABYSS)) return;
        if (bond.getUndercatChapter() < 4) return;
        if ((bond.getUndercatRegions() & (1 << UndercatRegion.SILENT_LIBRARY.ordinal())) == 0) return;
        if (player.tickCount % 120 != 0) return;

        setFlag(bond, FLAG_CH5_ABYSS);
        DialogueManager.play(player, "starlight_ch5_library");
        player.displayClientMessage(Component.translatable("starlight.cocojenna.ch5_library_hint"), true);
    }

    private static boolean hasFlag(BondData bond, int flag) {
        return (bond.getUndercatSideFlags() & flag) != 0;
    }

    private static void setFlag(BondData bond, int flag) {
        bond.setUndercatSideFlags(bond.getUndercatSideFlags() | flag);
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
