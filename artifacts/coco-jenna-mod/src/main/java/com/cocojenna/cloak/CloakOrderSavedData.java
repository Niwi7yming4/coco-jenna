package com.cocojenna.cloak;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

/** 披風訂單 — 三花子縫製需 1 遊戲日. */
public class CloakOrderSavedData extends SavedData {

    private static final String DATA_NAME = "cocojenna_cloak_orders";
    private static final int CRAFT_DELAY = 24000;
    private final Map<UUID, List<Order>> orders = new HashMap<>();

    public record Order(int cloakIndex, long readyTick) {}

    public static CloakOrderSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                CloakOrderSavedData::load,
                CloakOrderSavedData::new,
                DATA_NAME);
    }

    public CloakOrderSavedData() {}

    public static boolean placeOrder(ServerPlayer player, int cloakIndex) {
        var defOpt = CloakDefinition.byIndex(cloakIndex);
        if (defOpt.isEmpty()) return false;
        CloakDefinition def = defOpt.get();
        if (!def.canCraft(player)) {
            player.displayClientMessage(Component.translatable("cloak.cocojenna.missing_materials"), true);
            return false;
        }
        for (ItemStack need : def.cost) {
            removeItem(player, need.getItem(), need.getCount());
        }
        CloakOrderSavedData data = get(player.serverLevel());
        data.orders.computeIfAbsent(player.getUUID(), k -> new ArrayList<>())
                .add(new Order(cloakIndex, player.level().getGameTime() + CRAFT_DELAY));
        data.setDirty();
        player.displayClientMessage(Component.translatable(
                "cloak.cocojenna.order_placed",
                Component.translatable("item.cocojenna." + def.itemId)), true);
        return true;
    }

    public static void tick(ServerLevel level) {
        long now = level.getGameTime();
        CloakOrderSavedData data = get(level);
        for (ServerPlayer player : level.players()) {
            List<Order> list = data.orders.get(player.getUUID());
            if (list == null || list.isEmpty()) continue;
            Iterator<Order> it = list.iterator();
            while (it.hasNext()) {
                Order order = it.next();
                if (now >= order.readyTick()) {
                    deliver(player, order.cloakIndex());
                    it.remove();
                    data.setDirty();
                }
            }
        }
    }

    public static boolean deliverPending(ServerPlayer player) {
        CloakOrderSavedData data = get(player.serverLevel());
        List<Order> list = data.orders.get(player.getUUID());
        if (list == null || list.isEmpty()) return false;
        long now = player.level().getGameTime();
        Iterator<Order> it = list.iterator();
        boolean delivered = false;
        while (it.hasNext()) {
            Order order = it.next();
            if (now >= order.readyTick()) {
                deliver(player, order.cloakIndex());
                it.remove();
                delivered = true;
            }
        }
        if (delivered) data.setDirty();
        return delivered;
    }

    private static void deliver(ServerPlayer player, int cloakIndex) {
        CloakDefinition.byIndex(cloakIndex).ifPresent(def -> {
            ItemStack stack = def.craftWithoutCost();
            if (!stack.isEmpty()) {
                if (!player.addItem(stack)) player.drop(stack, false);
                player.displayClientMessage(Component.translatable(
                        "cloak.cocojenna.crafted",
                        Component.translatable("item.cocojenna." + def.itemId)), true);
            }
        });
    }

    public static boolean repairCloak(ServerPlayer player) {
        ItemStack target = null;
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.hasTag() || !stack.getTag().contains("CloakDurability")) continue;
            int max = stack.getTag().getInt("CloakMaxDurability");
            if (max <= 0) max = stack.getTag().getInt("CloakDurability");
            int cur = stack.getTag().getInt("CloakDurability");
            if (cur < max && (target == null || cur < target.getTag().getInt("CloakDurability"))) {
                target = stack;
                slot = i;
            }
        }
        if (target == null) {
            player.displayClientMessage(Component.translatable("cloak.cocojenna.nothing_to_repair"), true);
            return false;
        }
        int velvetCost = 5;
        if (countItem(player, com.cocojenna.init.ModItems.VELVET_FUR.get()) < velvetCost) {
            player.displayClientMessage(Component.translatable("cloak.cocojenna.missing_materials"), true);
            return false;
        }
        removeItem(player, com.cocojenna.init.ModItems.VELVET_FUR.get(), velvetCost);
        ItemStack repaired = player.getInventory().getItem(slot);
        int max = repaired.getTag().contains("CloakMaxDurability")
                ? repaired.getTag().getInt("CloakMaxDurability")
                : repaired.getTag().getInt("CloakDurability");
        repaired.getTag().putInt("CloakMaxDurability", max);
        repaired.getTag().putInt("CloakDurability", max);
        player.displayClientMessage(Component.translatable("cloak.cocojenna.repaired"), true);
        return true;
    }

    private static int countItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        int t = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(item)) t += s.getCount();
        }
        return t;
    }

    private static void removeItem(ServerPlayer player, net.minecraft.world.item.Item item, int count) {
        int left = count;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.is(item)) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }

    public static CloakOrderSavedData load(CompoundTag tag) {
        CloakOrderSavedData data = new CloakOrderSavedData();
        ListTag players = tag.getList("players", Tag.TAG_COMPOUND);
        for (Tag entry : players) {
            CompoundTag p = (CompoundTag) entry;
            UUID id = p.getUUID("id");
            ListTag orders = p.getList("orders", Tag.TAG_COMPOUND);
            List<Order> list = new ArrayList<>();
            for (Tag o : orders) {
                CompoundTag oc = (CompoundTag) o;
                list.add(new Order(oc.getInt("cloak"), oc.getLong("ready")));
            }
            data.orders.put(id, list);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag players = new ListTag();
        orders.forEach((uuid, list) -> {
            CompoundTag p = new CompoundTag();
            p.putUUID("id", uuid);
            ListTag orderList = new ListTag();
            for (Order o : list) {
                CompoundTag oc = new CompoundTag();
                oc.putInt("cloak", o.cloakIndex());
                oc.putLong("ready", o.readyTick());
                orderList.add(oc);
            }
            p.put("orders", orderList);
            players.add(p);
        });
        tag.put("players", players);
        return tag;
    }
}
