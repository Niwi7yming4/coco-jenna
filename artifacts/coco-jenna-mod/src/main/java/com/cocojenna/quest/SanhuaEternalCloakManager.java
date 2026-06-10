package com.cocojenna.quest;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.cloak.CloakDefinition;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;

/** 三花「最後的披風」任務 — 5 段故事 + 材料交付. */
public final class SanhuaEternalCloakManager {

    private SanhuaEternalCloakManager() {}

    /** 推進故事前檢查（章節 2 需材料）. */
    public static boolean canAdvanceStory(ServerPlayer player, String npcId, int nextChapter) {
        if (!"sanhua".equals(npcId) || nextChapter != 2) return true;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isSanhuaEternalMaterials()) return true;
        if (hasMaterials(player)) {
            consumeMaterials(player);
            bond.setSanhuaEternalMaterials(true);
            player.displayClientMessage(Component.translatable("quest.cocojenna.sanhua.materials_ok"), true);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncBondDataPacket(bond.serializeNBT()));
            return true;
        }
        player.displayClientMessage(Component.translatable("quest.cocojenna.sanhua.need_materials",
                8, 4, 5, 3), true);
        return false;
    }

    /** 章節 4 完成後發放永恆披風. */
    public static void onStoryChapterComplete(ServerPlayer player, String npcId, int chapter) {
        if (!"sanhua".equals(npcId) || chapter != 4) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        ItemStack cloak = CloakDefinition.ETERNAL.craftWithoutCost();
        if (!player.addItem(cloak)) player.drop(cloak, false);
        bond.addTownNpcFavor("sanhua", 10);
        bond.addKingdomHappiness(15);
        bond.modifySisterBond(8f);
        player.displayClientMessage(Component.translatable("quest.cocojenna.sanhua.eternal_cloak"), true);
    }

    public static boolean tryTurnInMaterials(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isSanhuaEternalMaterials()) {
            player.displayClientMessage(Component.translatable("quest.cocojenna.sanhua.already_turned_in"), true);
            return false;
        }
        if (!hasMaterials(player)) {
            player.displayClientMessage(Component.translatable("quest.cocojenna.sanhua.need_materials",
                    8, 4, 5, 3), true);
            return false;
        }
        consumeMaterials(player);
        bond.setSanhuaEternalMaterials(true);
        bond.addTownNpcFavor("sanhua", 12);
        player.displayClientMessage(Component.translatable("quest.cocojenna.sanhua.materials_ok"), true);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
        return true;
    }

    private static boolean hasMaterials(ServerPlayer player) {
        return count(player, ModItems.VELVET_FUR.get()) >= 8
                && count(player, ModItems.STARDUST_SOIL_ITEM.get()) >= 4
                && count(player, ModItems.PURR_CRYSTAL.get()) >= 5
                && count(player, Items.DANDELION) >= 3;
    }

    private static void consumeMaterials(ServerPlayer player) {
        remove(player, ModItems.VELVET_FUR.get(), 8);
        remove(player, ModItems.STARDUST_SOIL_ITEM.get(), 4);
        remove(player, ModItems.PURR_CRYSTAL.get(), 5);
        remove(player, Items.DANDELION, 3);
    }

    private static int count(ServerPlayer player, Item item) {
        Inventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) n += s.getCount();
        }
        return n;
    }

    private static void remove(ServerPlayer player, Item item, int amount) {
        int left = amount;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (!s.is(item)) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }
}
