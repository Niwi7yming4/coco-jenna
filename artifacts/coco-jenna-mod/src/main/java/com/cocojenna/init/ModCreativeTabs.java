package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CocoJennaMod.MOD_ID);

    private enum Category {
        ESSENTIALS,
        MATERIALS,
        RYOKATANA,
        LEGENDARY,
        GEAR,
        CONSUMABLES,
        BLOCKS,
        SPAWN_EGGS
    }

    private static final Set<String> STARTER_WEAPONS = Set.of(
            "fish_bone_blade", "yarn_ball_staff", "pawprint_dagger", "cat_bell_offhand",
            "golden_abacus_bead", "ironpaw_charm", "silvervine_bomb", "cat_bell_throw", "thunder_stone"
    );

    private static final Set<String> GEAR_ITEMS = Set.of(
            "velvet_tail_cape", "moonlight_collar", "stardust_ring", "jennas_old_bell"
    );

    public static final RegistryObject<CreativeModeTab> ESSENTIALS = register(Category.ESSENTIALS,
            () -> new ItemStack(ModItems.MEMORY_BOOK.get()));
    public static final RegistryObject<CreativeModeTab> MATERIALS = register(Category.MATERIALS,
            () -> new ItemStack(ModItems.VELVET_FUR.get()));
    public static final RegistryObject<CreativeModeTab> RYOKATANA = register(Category.RYOKATANA,
            () -> new ItemStack(ModItems.FISH_BONE_BLADE.get()));
    public static final RegistryObject<CreativeModeTab> LEGENDARY = register(Category.LEGENDARY,
            () -> new ItemStack(ModItems.DAIKATANA_TIGER_IRON.get()));
    public static final RegistryObject<CreativeModeTab> GEAR = register(Category.GEAR,
            () -> new ItemStack(ModItems.CLOAK_MOONLIGHT.get()));
    public static final RegistryObject<CreativeModeTab> CONSUMABLES = register(Category.CONSUMABLES,
            () -> new ItemStack(ModItems.PREMIUM_FISH_CAN.get()));
    public static final RegistryObject<CreativeModeTab> BLOCKS = register(Category.BLOCKS,
            () -> new ItemStack(ModBlocks.CAT_BED.get()));
    public static final RegistryObject<CreativeModeTab> SPAWN_EGGS = register(Category.SPAWN_EGGS,
            () -> new ItemStack(ModItems.COCO_SPAWN_EGG.get()));

    private static RegistryObject<CreativeModeTab> register(Category cat,
            java.util.function.Supplier<ItemStack> icon) {
        return CREATIVE_TABS.register(cat.name().toLowerCase(), () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.cocojenna." + cat.name().toLowerCase()))
                .icon(icon)
                .displayItems((params, output) -> fillTab(cat, output))
                .build());
    }

    private static void fillTab(Category target, CreativeModeTab.Output output) {
        for (RegistryObject<Item> ro : ModItems.ITEMS.getEntries()) {
            Item item = ro.get();
            if (categoryFor(item) == target) {
                output.accept(item);
            }
        }
    }

    private static Category categoryFor(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null || !CocoJennaMod.MOD_ID.equals(id.getNamespace())) {
            return Category.MATERIALS;
        }
        String path = id.getPath();

        if (path.endsWith("_spawn_egg")) {
            return Category.SPAWN_EGGS;
        }
        if (item instanceof BlockItem) {
            return Category.BLOCKS;
        }
        if (path.startsWith("ryokatana_") || STARTER_WEAPONS.contains(path)) {
            return Category.RYOKATANA;
        }
        if (path.startsWith("daikatana_") || path.startsWith("musou_")
                || path.equals("supreme_cat_claw")) {
            return Category.LEGENDARY;
        }
        if (path.startsWith("cloak_") || GEAR_ITEMS.contains(path)) {
            return Category.GEAR;
        }
        if (isConsumable(item, path)) {
            return Category.CONSUMABLES;
        }
        if (isEssential(item, path)) {
            return Category.ESSENTIALS;
        }
        return Category.MATERIALS;
    }

    private static boolean isConsumable(Item item, String path) {
        if (item instanceof CatFoodItem
                || item instanceof HolyWaterItem
                || item instanceof PurifyItem
                || item instanceof CombatElixirItem
                || item instanceof NineLivesCatnipItem
                || item instanceof SchrodingersBoxItem
                || item instanceof SpecialCatMealItem
                || item instanceof TwinStarMealItem) {
            return true;
        }
        return path.contains("fish") && path.contains("meal")
                || path.endsWith("_sashimi")
                || path.endsWith("_risotto")
                || path.equals("moth_scale_powder");
    }

    private static boolean isEssential(Item item, String path) {
        if (item instanceof MemoryBookItem
                || item instanceof MemoryShardItem
                || item instanceof SealOrbItem
                || item instanceof PawStampItem
                || item instanceof SequenceBadgeItem
                || item instanceof SequenceManualItem
                || item instanceof FeatherWandItem
                || item instanceof GroomingBrushItem
                || item instanceof GlassVialItem) {
            return true;
        }
        return path.equals("tarot_deck")
                || path.equals("blackjack_chip")
                || path.equals("purr_coin")
                || path.equals("full_moon_coin");
    }

    private ModCreativeTabs() {}
}
