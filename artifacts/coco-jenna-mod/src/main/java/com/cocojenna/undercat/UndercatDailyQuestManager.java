package com.cocojenna.undercat;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** 地下貓域每日任務重置與完成（批次 D）. */
public final class UndercatDailyQuestManager {

    private UndercatDailyQuestManager() {}

    public static void tickDaily(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.UNDERCAT_DOMAIN)
                && !player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return;
        }
        BondData bond = ModCapabilities.getOrDefault(player);
        long day = player.level().getDayTime() / 24000L;
        if (bond.getUndercatDailyDay() == day) return;
        bond.setUndercatDailyDay(day);
        bond.setUndercatDailyDone(false);
        UndercatDailyQuest quest = UndercatDailyQuest.forDay(day);
        bond.setUndercatDailyQuest(quest.ordinal());
        player.displayClientMessage(Component.translatable(
                "undercat.cocojenna.daily.new", Component.translatable(
                        "undercat.cocojenna.daily." + quest.id)), true);
    }

    public static UndercatDailyQuest current(BondData bond) {
        int idx = bond.getUndercatDailyQuest();
        if (idx < 0 || idx >= UndercatDailyQuest.values().length) {
            return UndercatDailyQuest.forDay(bond.getUndercatDailyDay());
        }
        return UndercatDailyQuest.values()[idx];
    }

    public static boolean complete(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getUndercatChapter() <= 0) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.need_main"), true);
            return false;
        }
        if (bond.isUndercatDailyDone()) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.already"), true);
            return false;
        }
        UndercatDailyQuest quest = current(bond);
        if (quest == UndercatDailyQuest.ORPHAN_DONATE && bond.getShadowCoins() < 15) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.need_coins"), true);
            return false;
        }
        if (quest == UndercatDailyQuest.ORPHAN_DONATE) {
            bond.addShadowCoins(-15);
        }
        if (quest == UndercatDailyQuest.ARENA_TIP && bond.getArenaBetAmount() <= 0) {
            player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.need_bet"), true);
            return false;
        }
        bond.setUndercatDailyDone(true);
        bond.addShadowCoins(quest.coinReward);
        bond.addUndercatRep(UndercatFaction.CARDBOARD_KINGDOM, quest.repReward / 2);
        bond.addUndercatRep(UndercatFaction.SMUGGLER_UNION, quest.repReward / 2);
        player.displayClientMessage(Component.translatable("undercat.cocojenna.daily.done",
                Component.translatable("undercat.cocojenna.daily." + quest.id), quest.coinReward), true);
        UndercatQuestManager.openHub(player);
        return true;
    }

    public static void onCatnipPlanted(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isUndercatDailyDone()) return;
        if (current(bond) != UndercatDailyQuest.CATNIP_TEND) return;
        complete(player);
    }
}
