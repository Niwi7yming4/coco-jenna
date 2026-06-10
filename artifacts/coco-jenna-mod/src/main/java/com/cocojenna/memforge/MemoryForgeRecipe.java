package com.cocojenna.memforge;

import com.cocojenna.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Predicate;

/** 無上大快刀儀式配方（設計書第三章）. */
public enum MemoryForgeRecipe {

    SALMON_KING("musou_salmon_king",
            ModItems.GIANT_GREEN_FISH, 1,
            ModItems.COARSE_SALT, 64,
            r -> true, 0xFF3366AA),

    NIGHT_VERDICT("musou_night_verdict",
            ModItems.COCO_MEMORY_SHARD, 10,
            ModItems.PURR_CRYSTAL, 3,
            RitualConditions::needsCocoAndFullMoon, 0xFF6633AA),

    TOY_HAMMER("musou_toy_hammer",
            ModItems.JENNA_MEMORY_SHARD, 10,
            ModItems.TOY_SQUEAK, 1,
            RitualConditions::needsJennaAndDay, 0xFFFF66CC),

    HIBISCUS_FALL("musou_hibiscus_fall",
            ModItems.HIBISCUS_FLOWER_ITEM, 10,
            ModItems.HIBISCUS_TEAR, 3,
            RitualConditions::needsBondAndFullMoon, 0xFFCC2244),

    ABYSS_DEPTH("musou_abyss_depth",
            ModItems.RUSTED_ANCHOR, 1,
            ModItems.DEEP_SEA_PEARL, 3,
            RitualConditions::needsBlindPort, 0xFF111122),

    MAD_CARD("musou_mad_card",
            ModItems.TAROT_DECK, 1,
            ModItems.BLACKJACK_CHIP, 1,
            r -> true, 0xFFFFD700);

    public final String id;
    public final RegistryObject<Item> coreItem;
    public final int coreCount;
    public final RegistryObject<Item> catalystItem;
    public final int catalystCount;
    public final Predicate<MemoryForgeRitual> bonusCondition;
    public final int beamColor;

    MemoryForgeRecipe(String id,
                      RegistryObject<Item> coreItem, int coreCount,
                      RegistryObject<Item> catalystItem, int catalystCount,
                      Predicate<MemoryForgeRitual> bonusCondition,
                      int beamColor) {
        this.id = id;
        this.coreItem = coreItem;
        this.coreCount = coreCount;
        this.catalystItem = catalystItem;
        this.catalystCount = catalystCount;
        this.bonusCondition = bonusCondition;
        this.beamColor = beamColor;
    }

    public net.minecraft.resources.ResourceLocation resultId() {
        return new net.minecraft.resources.ResourceLocation("cocojenna", id);
    }

    public static MemoryForgeRecipe forCore(Item item) {
        for (MemoryForgeRecipe r : values()) {
            if (r.coreItem.get() == item) return r;
        }
        return null;
    }
}
