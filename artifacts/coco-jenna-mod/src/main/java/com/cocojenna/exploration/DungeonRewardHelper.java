package com.cocojenna.exploration;

import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 地牢通關獎勵發放（設計書 3.2）. */
public final class DungeonRewardHelper {

    private DungeonRewardHelper() {}

    public static void grant(ServerPlayer player, String rewardKey) {
        switch (rewardKey) {
            case "stardust_cloth" -> give(player, ModItems.VELVET_FUR.get(), 3);
            case "workshop_cloak" -> giveRyokatana(player, "sanhua_thread");
            case "moonlight_ripple" -> giveRyokatana(player, "moonlight_ripple");
            case "gear_schedule" -> giveRyokatana(player, "gear_schedule");
            case "silent_whisper" -> giveRyokatana(player, "whisper_mud");
            case "abyss_anchor" -> giveRyokatana(player, "deep_sea_current");
            case "royal_glory" -> giveRyokatana(player, "royal_glory");
            case "storm_cloak" -> give(player, ModItems.STORM_CLOUD_FUR.get(), 2);
            case "sequence_badge" -> give(player, ModItems.SEQUENCE_BADGE.get(), 1);
            case "redeem_velvet" -> giveRyokatana(player, "fallen_velvet_claw");
            case "stardust_step" -> giveRyokatana(player, "stardust_step");
            case "dark_tide" -> giveRyokatana(player, "dark_tide");
            case "cheshire_grin" -> giveRyokatana(player, "cheshire_grin");
            case "moonlight_clear" -> giveRyokatana(player, "moonlight_clear");
            case "catnip_item" -> give(player, ModItems.CATNIP_ITEM.get(), 8);
            default -> give(player, ModItems.MAP_FRAGMENT.get(), 1);
        }
    }

    private static void give(ServerPlayer player, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }

    private static void giveRyokatana(ServerPlayer player, String id) {
        RyokatanaRegistry.find(id).ifPresent(ro -> give(player, ro.get(), 1));
    }
}
