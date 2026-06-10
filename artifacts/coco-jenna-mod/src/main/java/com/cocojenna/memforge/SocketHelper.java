package com.cocojenna.memforge;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.DaikataItem;
import com.cocojenna.item.RyokatanaItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 金色算盤珠鑲嵌（設計書第八章）. */
public final class SocketHelper {

    public static final String SOCKET_COUNT = "SocketCount";
    private static final int MAX_SOCKETS_MUSOU = 3;
    private static final int MAX_SOCKETS_DAIKATA = 2;
    private static final int MAX_SOCKETS_RYO = 1;

    private SocketHelper() {}

    public static int maxSockets(ItemStack weapon) {
        if (weapon.getItem() instanceof DaikataItem d) {
            String v = d.getVariantId();
            if (v.startsWith("musou_")) return MAX_SOCKETS_MUSOU;
            return MAX_SOCKETS_DAIKATA;
        }
        if (weapon.getItem() instanceof RyokatanaItem) return MAX_SOCKETS_RYO;
        return 0;
    }

    public static int socketCount(ItemStack weapon) {
        return weapon.hasTag() ? weapon.getTag().getInt(SOCKET_COUNT) : 0;
    }

    public static boolean trySocket(ServerPlayer player, ItemStack weapon, ItemStack gem) {
        if (!WeaponEnhanceHelper.canSocket(ModCapabilities.getOrDefault(player).getIronpawForgeLevel())) {
            player.displayClientMessage(Component.translatable("socket.cocojenna.need_forge_master")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (!WeaponEnhanceHelper.canEnhance(weapon)) {
            player.displayClientMessage(Component.translatable("socket.cocojenna.invalid_weapon")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        int max = maxSockets(weapon);
        if (socketCount(weapon) >= max) {
            player.displayClientMessage(Component.translatable("socket.cocojenna.full")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (!gem.is(ModItems.GOLDEN_ABACUS_BEAD.get())
                && !gem.is(ModItems.SHADOW_CRYSTAL.get())
                && !gem.is(ModItems.MOONSTONE.get())) {
            player.displayClientMessage(Component.translatable("socket.cocojenna.invalid_gem")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (countCoins(player) < 100) {
            player.displayClientMessage(Component.translatable("socket.cocojenna.need_gold")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        removeGold(player, 100);
        gem.shrink(1);
        int n = socketCount(weapon) + 1;
        weapon.getOrCreateTag().putInt(SOCKET_COUNT, n);
        player.displayClientMessage(Component.translatable("socket.cocojenna.success", n)
                .withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    public static float hitBonus(ItemStack weapon) {
        int n = socketCount(weapon);
        if (n >= 3) return 0.25f;
        if (n >= 2) return 0.15f;
        if (n >= 1) return 0.05f;
        return 0f;
    }

    private static int countCoins(ServerPlayer player) {
        int t = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == net.minecraft.world.item.Items.GOLD_INGOT) t += s.getCount();
        }
        return t * 100;
    }

    private static void removeGold(ServerPlayer player, int amount) {
        int ingots = (amount + 99) / 100;
        int left = ingots;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() != net.minecraft.world.item.Items.GOLD_INGOT) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
            if (left <= 0) break;
        }
    }
}
