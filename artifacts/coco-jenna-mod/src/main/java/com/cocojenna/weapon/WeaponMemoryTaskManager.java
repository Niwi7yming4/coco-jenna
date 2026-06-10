package com.cocojenna.weapon;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 武器記憶任務進度（BondData + 手持武器 NBT）.
 */
public final class WeaponMemoryTaskManager {

    private WeaponMemoryTaskManager() {}

    public static boolean tryStartFromNpc(ServerPlayer player, ItemStack weapon) {
        if (!WeaponData.isUnsealable(weapon)) return false;
        if (WeaponData.getStage(weapon).id < WeaponAwakeningStage.AWAKENED.id) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.need_awakened_first")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (WeaponData.isMemoryTaskCompleted(weapon)) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.memory_task.complete")
                    .withStyle(ChatFormatting.GOLD), true);
            return false;
        }
        String variant = WeaponData.variantId(weapon);
        var taskOpt = WeaponMemoryTaskRegistry.forWeapon(variant);
        if (taskOpt.isEmpty()) {
            // 無專屬任務的武器：達甦醒後自動視為完成
            WeaponData.setMemoryTaskCompleted(weapon, true);
            WeaponUnsealManager.checkStageUpgrade(player, weapon);
            return true;
        }
        var task = taskOpt.get();
        BondData bond = ModCapabilities.getOrDefault(player);
        if (task.id().equals(bond.getActiveWeaponMemoryTaskId())) {
            player.displayClientMessage(Component.translatable(task.descriptionKey())
                    .withStyle(ChatFormatting.AQUA), false);
            showProgress(player, bond, task);
            return true;
        }
        bond.setActiveWeaponMemoryTaskId(task.id());
        bond.setWeaponMemoryTaskProgress(0);
        WeaponData.setMemoryTaskId(weapon, task.id());
        player.displayClientMessage(Component.translatable("weapon.cocojenna.memory_task.start")
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        player.displayClientMessage(Component.translatable(task.descriptionKey())
                .withStyle(ChatFormatting.GRAY), false);
        return true;
    }

    public static void onBlackMudKill(ServerPlayer player, ItemStack weapon) {
        advance(player, weapon, WeaponMemoryTaskRegistry.TaskType.KILL_BLACK_MUD, 1);
        advance(player, weapon, WeaponMemoryTaskRegistry.TaskType.LAST_ARMOR, 1);
    }

    public static void onFish(ServerPlayer player) {
        advanceHeld(player, WeaponMemoryTaskRegistry.TaskType.FISH_IN_BLIND_WATER, 1);
    }

    public static void onHibiscusCollect(ServerPlayer player) {
        advanceHeld(player, WeaponMemoryTaskRegistry.TaskType.COLLECT_HIBISCUS, 1);
    }

    public static void onFullMoonAltar(ServerPlayer player) {
        advanceHeld(player, WeaponMemoryTaskRegistry.TaskType.FULL_MOON_ALTAR, 1);
    }

    public static void onPaperDeliver(ServerPlayer player) {
        advanceHeld(player, WeaponMemoryTaskRegistry.TaskType.DELIVER_PAPER, 1);
    }

    public static void onNarrativeEvent(ServerPlayer player, WeaponMemoryTaskRegistry.TaskType type, int amount) {
        advanceHeld(player, type, amount);
    }

    public static void onDaikataAltarInfusion(ServerPlayer player, ItemStack weapon) {
        if (!(weapon.getItem() instanceof com.cocojenna.item.DaikataItem)) return;
        if (!WeaponData.isUnsealable(weapon)) return;
        if (WeaponData.getStage(weapon) != WeaponAwakeningStage.ENLIGHTENED) return;
        int dust = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.SOUL_DUST.get())) dust += s.getCount();
        }
        if (dust < 3) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.need_soul_dust", 3)
                    .withStyle(ChatFormatting.RED), true);
            return;
        }
        if (!player.getAbilities().instabuild) {
            int left = 3;
            for (ItemStack s : player.getInventory().items) {
                if (!s.is(ModItems.SOUL_DUST.get())) continue;
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
                if (left == 0) break;
            }
        }
        WeaponData.addResonance(weapon, 20);
        WeaponUnsealManager.checkStageUpgrade(player, weapon);
        player.displayClientMessage(Component.translatable("weapon.cocojenna.daikata_altar_infused")
                .withStyle(ChatFormatting.GOLD), true);
    }

    private static void advanceHeld(ServerPlayer player, WeaponMemoryTaskRegistry.TaskType type, int amount) {
        advance(player, player.getMainHandItem(), type, amount);
        ItemStack off = player.getOffhandItem();
        if (off != player.getMainHandItem()) {
            advance(player, off, type, amount);
        }
    }

    private static void advance(ServerPlayer player, ItemStack weapon, WeaponMemoryTaskRegistry.TaskType type, int amount) {
        if (!WeaponData.isUnsealable(weapon) || WeaponData.isMemoryTaskCompleted(weapon)) return;
        String variant = WeaponData.variantId(weapon);
        var taskOpt = WeaponMemoryTaskRegistry.forWeapon(variant);
        if (taskOpt.isEmpty() || taskOpt.get().type() != type) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        if (!taskOpt.get().id().equals(bond.getActiveWeaponMemoryTaskId())) return;

        int progress = bond.getWeaponMemoryTaskProgress() + amount;
        bond.setWeaponMemoryTaskProgress(progress);
        var task = taskOpt.get();
        showProgress(player, bond, task);

        if (progress >= task.goal()) {
            complete(player, weapon, bond, task);
        }
    }

    private static void complete(ServerPlayer player, ItemStack weapon, BondData bond,
                                 WeaponMemoryTaskRegistry.MemoryTask task) {
        WeaponData.setMemoryTaskCompleted(weapon, true);
        bond.setActiveWeaponMemoryTaskId("");
        bond.setWeaponMemoryTaskProgress(0);
        player.displayClientMessage(Component.translatable("weapon.cocojenna.memory_task.complete")
                .withStyle(ChatFormatting.GOLD), true);
        WeaponMemoryCinematicManager.onTaskComplete(player, task);
        WeaponUnsealManager.checkStageUpgrade(player, weapon);
        com.cocojenna.society.FragmentedQuestManager.onWeaponMemoryComplete(player);
    }

    private static void showProgress(ServerPlayer player, BondData bond, WeaponMemoryTaskRegistry.MemoryTask task) {
        player.displayClientMessage(Component.translatable("weapon.cocojenna.memory_task.progress",
                        bond.getWeaponMemoryTaskProgress(), task.goal())
                .withStyle(ChatFormatting.DARK_AQUA), true);
    }
}
