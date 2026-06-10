package com.cocojenna.memforge;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import com.cocojenna.swordbone.SwordBoneEntry;
import com.cocojenna.swordbone.SwordBoneManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
/** 鐵爪鍛造台 — 修理武器與劍骨、販售卸劍石. */
public final class ForgeRepairHelper {

    private static final int UNSHEATH_COST_COINS = 120;
    private static final int REPAIR_IRON_COST = 2;
    private static final int BONE_REPAIR_CLAY_COST = 3;

    private ForgeRepairHelper() {}

    public static boolean tryRepairWeapon(ServerPlayer player, ItemStack weapon) {
        if (weapon.isEmpty() || !weapon.isDamageableItem()) return false;
        if (weapon.getDamageValue() <= 0) {
            player.displayClientMessage(Component.translatable("forge.cocojenna.repair_none"), true);
            return false;
        }
        if (!consumeIron(player, REPAIR_IRON_COST)) {
            player.displayClientMessage(Component.translatable("forge.cocojenna.repair_need_iron"), true);
            return false;
        }
        weapon.setDamageValue(Math.max(0, weapon.getDamageValue() - weapon.getMaxDamage() / 2));
        player.displayClientMessage(Component.translatable("forge.cocojenna.repair_done"), true);
        return true;
    }

    public static boolean tryRepairBones(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        boolean any = false;
        for (int i = 0; i < bond.getSwordBones().size(); i++) {
            SwordBoneEntry entry = bond.getSwordBones().get(i);
            if (!entry.damaged()) continue;
            if (!consumeClay(player, BONE_REPAIR_CLAY_COST)) {
                player.displayClientMessage(Component.translatable("forge.cocojenna.bone_need_clay"), true);
                return any;
            }
            bond.setSwordBoneAt(i, new SwordBoneEntry(entry.weaponId(), false));
            any = true;
        }
        if (any) {
            SwordBoneManager.sync(player);
            player.displayClientMessage(Component.translatable("forge.cocojenna.bone_repaired"), true);
        } else {
            player.displayClientMessage(Component.translatable("forge.cocojenna.bone_none"), true);
        }
        return any;
    }

    public static boolean tryBuyUnsheathStone(ServerPlayer player) {
        int coins = countPurrCoins(player);
        if (coins < UNSHEATH_COST_COINS) {
            player.displayClientMessage(Component.translatable("forge.cocojenna.buy_need_coins", UNSHEATH_COST_COINS), true);
            return false;
        }
        removeCoins(player, UNSHEATH_COST_COINS);
        ItemStack stone = new ItemStack(ModItems.UNSHEATH_STONE.get());
        if (!player.addItem(stone)) player.drop(stone, false);
        player.displayClientMessage(Component.translatable("forge.cocojenna.buy_unsheath"), true);
        return true;
    }

    private static boolean consumeIron(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(Items.IRON_INGOT)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
        return left == 0;
    }

    private static boolean consumeClay(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.MEMORY_CLAY.get())) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
        return left == 0;
    }

    private static int countPurrCoins(ServerPlayer player) {
        int total = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.PURR_COIN.get())) total += s.getCount();
            if (s.is(Items.GOLD_INGOT)) total += s.getCount();
        }
        return total;
    }

    private static void removeCoins(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.is(ModItems.PURR_COIN.get()) || s.is(Items.GOLD_INGOT)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }
}
