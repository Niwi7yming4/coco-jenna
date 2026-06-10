package com.cocojenna.memforge;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.DaikataItem;
import com.cocojenna.item.RyokatanaItem;
import com.cocojenna.item.SupremeCatClawItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

/**
 * 傳說武器強化 +0～+10（設計書第七章）.
 */
public final class WeaponEnhanceHelper {

    public static final String ENHANCE_LEVEL = "EnhanceLevel";
    private static final Random RNG = new Random();

    private WeaponEnhanceHelper() {}

    public static boolean canEnhance(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof RyokatanaItem || item instanceof DaikataItem
                || item instanceof SupremeCatClawItem;
    }

    public static int getLevel(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(ENHANCE_LEVEL) : 0;
    }

    public static void setLevel(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(ENHANCE_LEVEL, Math.max(0, Math.min(10, level)));
    }

    /** 鍛造舖等級：Lv.1→+5、Lv.2→+10、Lv.3→+10+鑲嵌 */
    public static int maxLevelForForge(int forgeLevel) {
        return switch (Math.max(1, Math.min(3, forgeLevel))) {
            case 1 -> 5;
            case 2 -> 10;
            default -> 10;
        };
    }

    public static boolean canSocket(int forgeLevel) {
        return forgeLevel >= 3;
    }

    public static float damageMultiplier(int level) {
        return switch (level) {
            case 1 -> 1.05f;
            case 2 -> 1.10f;
            case 3 -> 1.16f;
            case 4 -> 1.23f;
            case 5 -> 1.31f;
            case 6 -> 1.40f;
            case 7 -> 1.50f;
            case 8 -> 1.61f;
            case 9 -> 1.73f;
            case 10 -> 2.00f;
            default -> 1.0f;
        };
    }

    public static boolean tryEnhance(ServerPlayer player, ItemStack weapon) {
        if (!canEnhance(weapon)) {
            player.displayClientMessage(Component.translatable("forge.cocojenna.invalid_weapon")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        int level = getLevel(weapon);
        int forgeLv = ModCapabilities.getOrDefault(player).getIronpawForgeLevel();
        int max = maxLevelForForge(forgeLv);
        if (level >= max) {
            player.displayClientMessage(Component.translatable(
                    forgeLv < 3 ? "forge.cocojenna.need_forge_upgrade" : "forge.cocojenna.max_level")
                    .withStyle(ChatFormatting.GOLD), true);
            return false;
        }

        EnhanceCost cost = costFor(level);
        if (!consumeMaterials(player, cost)) {
            player.displayClientMessage(Component.translatable("forge.cocojenna.missing_materials")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (RNG.nextFloat() * 100f > cost.successRate) {
            applyFailure(player, weapon, level, cost);
            return false;
        }

        setLevel(weapon, level + 1);
        player.displayClientMessage(Component.translatable("forge.cocojenna.success", level + 1)
                .withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    private static void applyFailure(ServerPlayer player, ItemStack weapon, int level, EnhanceCost cost) {
        player.displayClientMessage(Component.translatable("forge.cocojenna.fail")
                .withStyle(ChatFormatting.RED), true);
        if (consumeCharm(player)) {
            player.displayClientMessage(Component.translatable("forge.cocojenna.charm_saved")
                    .withStyle(ChatFormatting.GOLD), true);
            return;
        }
        if (cost.failDrop >= 1) {
            setLevel(weapon, Math.max(0, level - cost.failDrop));
        }
    }

    private static boolean consumeCharm(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.getItem() == ModItems.IRONPAW_CHARM.get() && s.getCount() > 0) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static boolean consumeMaterials(ServerPlayer player, EnhanceCost cost) {
        int iron = countItem(player, Items.IRON_INGOT);
        int gold = countItem(player, Items.GOLD_INGOT);
        int mud = countItem(player, ModItems.BLACK_MUD_REMNANT.get());
        int purr = countItem(player, ModItems.PURR_CRYSTAL.get());
        int shadow = countItem(player, ModItems.SHADOW_CRYSTAL.get());

        if (iron < cost.iron || gold < cost.gold || mud < cost.mud || purr < cost.purr || shadow < cost.shadow) {
            return false;
        }
        removeItem(player, Items.IRON_INGOT, cost.iron);
        removeItem(player, Items.GOLD_INGOT, cost.gold);
        removeItem(player, ModItems.BLACK_MUD_REMNANT.get(), cost.mud);
        removeItem(player, ModItems.PURR_CRYSTAL.get(), cost.purr);
        removeItem(player, ModItems.SHADOW_CRYSTAL.get(), cost.shadow);
        return true;
    }

    private static int countItem(ServerPlayer player, Item item) {
        int total = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == item) total += s.getCount();
        }
        return total;
    }

    private static void removeItem(ServerPlayer player, Item item, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.getItem() != item) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }

    private static EnhanceCost costFor(int fromLevel) {
        return switch (fromLevel) {
            case 0 -> new EnhanceCost(5, 0, 10, 0, 0, 100, 0);
            case 1 -> new EnhanceCost(10, 0, 20, 0, 0, 95, 0);
            case 2 -> new EnhanceCost(0, 5, 0, 1, 0, 85, 0);
            case 3 -> new EnhanceCost(0, 10, 0, 3, 0, 75, 1);
            case 4 -> new EnhanceCost(0, 0, 0, 5, 3, 65, 1);
            case 5 -> new EnhanceCost(0, 0, 0, 0, 5, 55, 2);
            case 6 -> new EnhanceCost(0, 0, 0, 10, 0, 45, 2);
            case 7 -> new EnhanceCost(0, 0, 0, 15, 0, 35, 3);
            case 8 -> new EnhanceCost(0, 0, 0, 20, 0, 25, 3);
            case 9 -> new EnhanceCost(0, 0, 0, 20, 5, 15, 10);
            default -> new EnhanceCost(0, 0, 0, 0, 0, 0, 0);
        };
    }

    private record EnhanceCost(int iron, int gold, int mud, int purr, int shadow,
                               float successRate, int failDrop) {}
}
