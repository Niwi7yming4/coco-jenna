package com.cocojenna.reputation;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 聲望系統（設計書 1.8）. */
public final class ReputationHelper {

    public static final int WORSHIP = 100;

    private ReputationHelper() {}

    public static void addRep(ServerPlayer player, String region, int amount) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int before = bond.getReputation(region);
        bond.setReputation(region, before + amount);
        int after = bond.getReputation(region);
        if (before < WORSHIP && after >= WORSHIP) {
            grantWorshipReward(player, region);
        }
    }

    private static void grantWorshipReward(ServerPlayer player, String region) {
        var ro = switch (region) {
            case "royal" -> RyokatanaRegistry.get("royal_glory");
            case "gear_town" -> RyokatanaRegistry.get("gear_schedule");
            case "first_cry" -> RyokatanaRegistry.get("first_cry_beginner");
            case "blind_port" -> RyokatanaRegistry.get("blind_water_stealth");
            case "dawn" -> RyokatanaRegistry.get("dawn_hope");
            default -> null;
        };
        if (ro != null) {
            ItemStack blade = new ItemStack(ro.get());
            if (!player.addItem(blade)) player.drop(blade, false);
        }
        player.displayClientMessage(Component.translatable(
                "reputation.cocojenna.worship", Component.translatable("reputation.cocojenna." + region)), true);
    }

    /** 完成任務、交易、淨化等時呼叫 */
    public static void onQuestComplete(ServerPlayer player, String region) {
        addRep(player, region, 15);
    }
}
