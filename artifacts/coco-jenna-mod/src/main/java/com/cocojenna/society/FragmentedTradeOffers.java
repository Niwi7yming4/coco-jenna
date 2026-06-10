package com.cocojenna.society;

import com.cocojenna.capability.FragmentedSequenceData;
import com.cocojenna.init.ModItems;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

/** 破碎序列村民 5 層交易表（9 職業完整）. */
public final class FragmentedTradeOffers {

    private FragmentedTradeOffers() {}

    public static void applyOffers(Villager villager, FragmentedSequenceData data) {
        MerchantOffers offers = new MerchantOffers();
        int tier = Math.min(4, data.getBondWithPlayer() / 25);
        addTierOffers(offers, data.getProfession(), tier);
        villager.setOffers(offers);
    }

    private static void addTierOffers(MerchantOffers offers, FragmentedProfession prof, int tier) {
        switch (prof) {
            case CAT_COMPANION -> {
                offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 2),
                        new ItemStack(ModItems.CATNIP_ITEM.get()), 8, 2, 0.05f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.STRING, 4),
                        new ItemStack(ModItems.MEMORY_PARTICLE.get(), 8), 6, 5, 0.08f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 5),
                        new ItemStack(ModItems.MEMORY_SHARD.get()), 3, 10, 0.1f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get()),
                        new ItemStack(ModItems.BLANK_MEMORY_CARD.get()), 2, 15, 0.12f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 12),
                        new ItemStack(ModItems.COCO_MEMORY_SHARD.get()), 1, 20, 0.15f));
            }
            case VELVET_PLAYWORKER -> {
                offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 3),
                        new ItemStack(ModItems.VELVET_FUR.get(), 2), 8, 2, 0.05f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.WHITE_WOOL, 6),
                        new ItemStack(ModItems.VELVET_FUR.get()), 6, 4, 0.06f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(ModItems.VELVET_FUR.get(), 4),
                        new ItemStack(Items.PINK_WOOL, 8), 6, 8, 0.08f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 6),
                        new ItemStack(ModItems.VELVET_FUR.get(), 4), 4, 12, 0.1f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 2),
                        new ItemStack(ModItems.PURR_CRYSTAL.get()), 2, 18, 0.12f));
            }
            case MOON_PLAYMATE -> {
                offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 4),
                        new ItemStack(ModItems.MOONSTONE.get()), 6, 3, 0.06f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(ModItems.MOONSTONE.get()),
                        new ItemStack(Items.GLOWSTONE_DUST, 4), 6, 5, 0.07f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 8),
                        new ItemStack(ModItems.MOONSTONE.get(), 2), 4, 10, 0.09f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get()),
                        new ItemStack(ModItems.MOONLIGHT_FOOTPRINT.get()), 3, 14, 0.11f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MOONSTONE.get(), 3),
                        new ItemStack(ModItems.PURE_DROP.get()), 2, 22, 0.14f));
            }
            case CAT_WARDER -> {
                offers.add(new MerchantOffer(new ItemStack(Items.IRON_INGOT, 4),
                        new ItemStack(Items.IRON_SWORD), 4, 5, 0.05f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 3),
                        new ItemStack(Items.SHIELD), 3, 8, 0.07f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 3),
                        new ItemStack(Items.EMERALD, 6), 4, 10, 0.1f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 10),
                        new ItemStack(ModItems.VELVET_BEGINNER_CHESTPLATE.get()), 2, 16, 0.12f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 2),
                        new ItemStack(ModItems.SEALED_MEMORY_BOOK.get()), 1, 24, 0.15f));
            }
            case VELVET_SENTINEL -> {
                offers.add(new MerchantOffer(new ItemStack(Items.IRON_INGOT, 6),
                        new ItemStack(ModItems.VELVET_BEGINNER_HELMET.get()), 3, 5, 0.05f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 4),
                        new ItemStack(ModItems.MOONSTONE.get(), 2), 5, 7, 0.07f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 2),
                        new ItemStack(ModItems.VELVET_BEGINNER_LEGGINGS.get()), 3, 11, 0.09f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 12),
                        new ItemStack(ModItems.VELVET_BEGINNER_BOOTS.get()), 2, 15, 0.11f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 3),
                        new ItemStack(ModItems.PURE_DROP.get()), 1, 20, 0.14f));
            }
            case MUD_EXORCIST -> {
                offers.add(new MerchantOffer(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 2),
                        new ItemStack(Items.GLOWSTONE_DUST, 4), 6, 5, 0.08f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 5),
                        new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()), 4, 7, 0.08f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 4),
                        new ItemStack(ModItems.HIBISCUS_TEAR.get()), 3, 10, 0.1f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 10),
                        new ItemStack(ModItems.HIBISCUS_TEAR.get()), 2, 14, 0.12f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 2),
                        new ItemStack(ModItems.PURE_DROP.get()), 2, 20, 0.15f));
            }
            case MEMORY_SCRIBE -> {
                offers.add(new MerchantOffer(new ItemStack(Items.BOOK, 2),
                        new ItemStack(ModItems.MEMORY_SHARD.get()), 4, 5, 0.07f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 4),
                        new ItemStack(ModItems.MAP_FRAGMENT.get()), 4, 6, 0.08f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get()),
                        new ItemStack(ModItems.BLANK_MEMORY_CARD.get()), 3, 10, 0.1f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 2),
                        new ItemStack(Items.ENCHANTED_BOOK), 2, 15, 0.12f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 16),
                        new ItemStack(ModItems.SEALED_MEMORY_BOOK.get()), 1, 25, 0.18f));
            }
            case BROKEN_SCHOLAR -> {
                offers.add(new MerchantOffer(new ItemStack(Items.BOOK, 3),
                        new ItemStack(ModItems.MEMORY_PARTICLE.get(), 2), 5, 4, 0.06f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 3),
                        new ItemStack(Items.BOOKSHELF), 4, 6, 0.07f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_PARTICLE.get(), 4),
                        new ItemStack(ModItems.MEMORY_SHARD.get()), 3, 9, 0.09f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 8),
                        new ItemStack(ModItems.GUARDIAN_GUIDE.get()), 2, 14, 0.11f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 3),
                        new ItemStack(ModItems.GUARDIAN_GUIDE.get()), 1, 22, 0.14f));
            }
            case MOON_PROPHET -> {
                offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 8),
                        new ItemStack(ModItems.MOONSTONE.get(), 2), 4, 8, 0.1f));
                if (tier >= 1) offers.add(new MerchantOffer(new ItemStack(ModItems.MOONSTONE.get()),
                        new ItemStack(ModItems.MOONLIGHT_FOOTPRINT.get()), 4, 6, 0.08f));
                if (tier >= 2) offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 12),
                        new ItemStack(ModItems.PURR_CRYSTAL.get()), 3, 12, 0.1f));
                if (tier >= 3) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 2),
                        new ItemStack(ModItems.MOONLIGHT_COLLAR.get()), 2, 18, 0.13f));
                if (tier >= 4) offers.add(new MerchantOffer(new ItemStack(ModItems.MEMORY_SHARD.get(), 3),
                        new ItemStack(ModItems.PURE_DROP.get()), 1, 25, 0.2f));
            }
        }
    }
}
