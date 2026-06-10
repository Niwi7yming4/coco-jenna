package com.cocojenna.world.firstcry;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.quest.FirstCryMainQuestManager;
import com.cocojenna.shop.ReputationShopOffers;
import com.cocojenna.shop.RyokatanaShopOffers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** 初啼村 NPC 右鍵互動（設計書 四）. */
public final class FirstCryVillageNpcHandler {

    private FirstCryVillageNpcHandler() {}

    public static boolean onInteract(ServerPlayer player, String npcId) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        if (player.blockPosition().distSqr(FirstCryLayout.CENTER) > 85 * 85) return false;
        return switch (npcId) {
            case "ryokatsu" -> {
                FirstCryMainQuestManager.onNpcTalk(player, npcId);
                var prog = com.cocojenna.quest.FirstCryProgress.get(player.serverLevel());
                if (prog.getBlackMudStage() >= 1 && !prog.isBlackMudPurified()) {
                    player.displayClientMessage(
                            Component.translatable("quest.cocojenna.black_mud_secret.hud_purify"), false);
                }
                yield true;
            }
            case "pagepaw" -> {
                FirstCryMainQuestManager.onNpcTalk(player, npcId);
                yield true;
            }
            case "moon_whisper" -> {
                if (player.level().getMoonBrightness() >= 1.0f) {
                    DialogueManager.play(player, "first_cry_npc_lore_moon_whisper");
                    player.displayClientMessage(
                            Component.translatable("first_cry.cocojenna.moon_priest"), false);
                } else {
                    player.displayClientMessage(
                            Component.translatable("first_cry.cocojenna.moon_priest_absent"), false);
                }
                yield true;
            }
            case "wander_stray" -> {
                DialogueManager.play(player, "first_cry_wander_stray");
                yield true;
            }
            case "blade_mark", "molten_paw", "miso", "mint_ear", "soft_pad", "tide_tail", "mud_bean" -> {
                DialogueManager.play(player, "first_cry_npc_lore_" + npcId);
                yield switch (npcId) {
                    case "blade_mark" -> RyokatanaShopOffers.tryPurchaseWithShards(player, 0);
                    case "molten_paw" -> ReputationShopOffers.tryPurchase(player, 6);
                    case "miso" -> sellFood(player);
                    case "mint_ear" -> sellSeeds(player);
                    case "soft_pad" -> sellInnSupplies(player);
                    case "tide_tail" -> HarborTravelManager.tryTravel(player, 1);
                    case "mud_bean" -> sellFarmGoods(player);
                    default -> true;
                };
            }
            default -> false;
        };
    }

    private static boolean sellFood(ServerPlayer player) {
        if (!payShardOrEmerald(player, 1, 2)) return false;
        ItemStack food = new ItemStack(ModItems.BASIC_FISH_PUREE.get(), 3);
        if (!player.addItem(food)) player.drop(food, false);
        player.displayClientMessage(Component.translatable("first_cry.cocojenna.bought_food"), false);
        return true;
    }

    private static boolean sellSeeds(ServerPlayer player) {
        if (!payShardOrEmerald(player, 1, 1)) return false;
        ItemStack seeds = new ItemStack(ModItems.LEGEND_CATNIP_SEED.get(), 4);
        if (!player.addItem(seeds)) player.drop(seeds, false);
        player.displayClientMessage(Component.translatable("first_cry.cocojenna.bought_seeds"), false);
        return true;
    }

    private static boolean sellInnSupplies(ServerPlayer player) {
        if (!payShardOrEmerald(player, 1, 3)) return false;
        ItemStack toy = new ItemStack(ModItems.TOY_SQUEAK.get());
        if (!player.addItem(toy)) player.drop(toy, false);
        player.displayClientMessage(Component.translatable("first_cry.cocojenna.bought_toy"), false);
        return true;
    }

    private static boolean sellFarmGoods(ServerPlayer player) {
        if (!payShardOrEmerald(player, 1, 2)) return false;
        ItemStack fruit = new ItemStack(ModItems.SPORE_FRUIT.get(), 2);
        if (!player.addItem(fruit)) player.drop(fruit, false);
        player.displayClientMessage(Component.translatable("first_cry.cocojenna.bought_farm"), false);
        return true;
    }

    private static boolean payShardOrEmerald(ServerPlayer player, int shards, int emeralds) {
        int shardLeft = shards;
        for (int i = 0; i < player.getInventory().getContainerSize() && shardLeft > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(ModItems.MEMORY_SHARD.get())) {
                int take = Math.min(shardLeft, s.getCount());
                s.shrink(take);
                shardLeft -= take;
            }
        }
        if (shardLeft == 0) return true;
        int emLeft = emeralds;
        for (int i = 0; i < player.getInventory().getContainerSize() && emLeft > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(Items.EMERALD)) {
                int take = Math.min(emLeft, s.getCount());
                s.shrink(take);
                emLeft -= take;
            }
        }
        if (emLeft > 0) {
            player.displayClientMessage(Component.translatable("first_cry.cocojenna.need_currency"), true);
            return false;
        }
        return true;
    }
}
