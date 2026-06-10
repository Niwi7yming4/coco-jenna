package com.cocojenna.memforge;

import com.cocojenna.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;

/** Auto-generated from data/cocojenna/recipes/daikatana_*.json — altar forging requirements. */
public enum DaikatanaRitualRecipe {
    ABYSS(
        "abyss",
        ModItems.DAIKATANA_ABYSS.get(),
        ModItems.MOONSTONE.get(),
        new Mat[]{
            new Mat(ModItems.MOONSTONE.get(), 2),
            new Mat(Items.LEATHER, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    CRESCENT(
        "crescent",
        ModItems.DAIKATANA_CRESCENT.get(),
        ModItems.MOONSTONE.get(),
        new Mat[]{
            new Mat(ModItems.MOONSTONE.get(), 2),
            new Mat(ModItems.SPORE_FRUIT.get(), 2),
            new Mat(Items.GOLD_INGOT, 2),
            new Mat(Items.LEATHER, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    MOON_VERDICT(
        "moon_verdict",
        ModItems.DAIKATANA_MOON_VERDICT.get(),
        ModItems.MOON_CORE.get(),
        new Mat[]{
            new Mat(ModItems.MOON_CORE.get(), 1),
            new Mat(ModItems.MOONSTONE.get(), 4),
            new Mat(ModItems.DAIKATANA_CRESCENT.get(), 1),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    DUSK_END(
        "dusk_end",
        ModItems.DAIKATANA_DUSK_END.get(),
        ModItems.SALT.get(),
        new Mat[]{
            new Mat(ModItems.SALT.get(), 2),
            new Mat(Items.DIAMOND, 2),
            new Mat(Items.STRING, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    FIRST_DAWN(
        "first_dawn",
        ModItems.DAIKATANA_FIRST_DAWN.get(),
        ModItems.CHAOS_CRYSTAL.get(),
        new Mat[]{
            new Mat(ModItems.CHAOS_CRYSTAL.get(), 1),
            new Mat(ModItems.MOONSTONE.get(), 1),
            new Mat(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cocojenna", "ryokatana_dawn_hope")), 1),
            new Mat(Items.NETHERITE_INGOT, 1),
            new Mat(Items.STICK, 1),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4)
        }),
    FORGOTTEN_TOWER(
        "forgotten_tower",
        ModItems.DAIKATANA_FORGOTTEN_TOWER.get(),
        ModItems.SPORE_FRUIT.get(),
        new Mat[]{
            new Mat(ModItems.SPORE_FRUIT.get(), 2),
            new Mat(Items.FLINT, 2),
            new Mat(Items.LEATHER, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    GEAR_KING(
        "gear_king",
        ModItems.DAIKATANA_GEAR_KING.get(),
        ModItems.JENNA_FUR.get(),
        new Mat[]{
            new Mat(ModItems.JENNA_FUR.get(), 2),
            new Mat(Items.BONE, 2),
            new Mat(Items.GLASS, 2),
            new Mat(Items.GOLD_INGOT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    HIBISCUS_ULTIMATE(
        "hibiscus_ultimate",
        ModItems.DAIKATANA_HIBISCUS_ULTIMATE.get(),
        ModItems.SPORE_FRUIT.get(),
        new Mat[]{
            new Mat(ModItems.SPORE_FRUIT.get(), 2),
            new Mat(Items.FLINT, 2),
            new Mat(Items.LEATHER, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    HOWLING_GORGE(
        "howling_gorge",
        ModItems.DAIKATANA_HOWLING_GORGE.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(Items.DIAMOND, 2),
            new Mat(Items.FLINT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2),
            new Mat(ModItems.PURR_CRYSTAL.get(), 1)
        }),
    NEON_DANCE(
        "neon_dance",
        ModItems.DAIKATANA_NEON_DANCE.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(Items.COAL, 2),
            new Mat(Items.EMERALD, 2),
            new Mat(Items.STICK, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2),
            new Mat(ModItems.PURR_CRYSTAL.get(), 1)
        }),
    PHANTOM(
        "phantom",
        ModItems.DAIKATANA_PHANTOM.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(Items.COAL, 2),
            new Mat(Items.EMERALD, 2),
            new Mat(Items.STICK, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2),
            new Mat(ModItems.PURR_CRYSTAL.get(), 1)
        }),
    ROYAL_AUTHORITY(
        "royal_authority",
        ModItems.DAIKATANA_ROYAL_AUTHORITY.get(),
        ModItems.SPORE_FRUIT.get(),
        new Mat[]{
            new Mat(ModItems.SPORE_FRUIT.get(), 2),
            new Mat(Items.FLINT, 2),
            new Mat(Items.LEATHER, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    SHADOW_CLAW_IMITATION(
        "shadow_claw_imitation",
        ModItems.DAIKATANA_SHADOW_CLAW_IMITATION.get(),
        ModItems.COCO_FUR.get(),
        new Mat[]{
            new Mat(ModItems.COCO_FUR.get(), 2),
            new Mat(Items.FEATHER, 2),
            new Mat(Items.IRON_INGOT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    SHOCKWAVE(
        "shockwave",
        ModItems.DAIKATANA_SHOCKWAVE.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(Items.COAL, 2),
            new Mat(Items.EMERALD, 2),
            new Mat(Items.STICK, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2),
            new Mat(ModItems.PURR_CRYSTAL.get(), 1)
        }),
    SILENT_GUARD(
        "silent_guard",
        ModItems.DAIKATANA_SILENT_GUARD.get(),
        ModItems.SALT.get(),
        new Mat[]{
            new Mat(ModItems.SALT.get(), 2),
            new Mat(Items.COAL, 2),
            new Mat(Items.STICK, 2),
            new Mat(Items.STRING, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    STAR_MAP(
        "star_map",
        ModItems.DAIKATANA_STAR_MAP.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(Items.DIAMOND, 2),
            new Mat(Items.FLINT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2),
            new Mat(ModItems.PURR_CRYSTAL.get(), 1)
        }),
    STORM_UMBRELLA(
        "storm_umbrella",
        ModItems.DAIKATANA_STORM_UMBRELLA.get(),
        ModItems.BLACK_MUD_REMNANT.get(),
        new Mat[]{
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 1),
            new Mat(ModItems.SQUALL_UMBRELLA_BONE.get(), 1),
            new Mat(ModItems.STORM_CLOUD_FUR.get(), 1),
            new Mat(Items.IRON_INGOT, 1),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    SUPPRESSION(
        "suppression",
        ModItems.DAIKATANA_SUPPRESSION.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(Items.STICK, 2),
            new Mat(Items.STRING, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2),
            new Mat(ModItems.PURR_CRYSTAL.get(), 1)
        }),
    TIGER_IRON(
        "tiger_iron",
        ModItems.DAIKATANA_TIGER_IRON.get(),
        ModItems.PURR_CRYSTAL.get(),
        new Mat[]{
            new Mat(ModItems.PURR_CRYSTAL.get(), 1),
            new Mat(Items.NETHERITE_INGOT, 1),
            new Mat(Items.STICK, 1),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4)
        }),
    VILLAGE_SOUL(
        "village_soul",
        ModItems.DAIKATANA_VILLAGE_SOUL.get(),
        ModItems.SALT.get(),
        new Mat[]{
            new Mat(ModItems.SALT.get(), 2),
            new Mat(ModItems.SPORE_FRUIT.get(), 2),
            new Mat(Items.DIAMOND, 2),
            new Mat(Items.FLINT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    WHITE_GLOVE_CONTRACT(
        "white_glove_contract",
        ModItems.DAIKATANA_WHITE_GLOVE_CONTRACT.get(),
        ModItems.VELVET_FUR.get(),
        new Mat[]{
            new Mat(ModItems.VELVET_FUR.get(), 2),
            new Mat(Items.IRON_INGOT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        }),
    WIND_CUT(
        "wind_cut",
        ModItems.DAIKATANA_WIND_CUT.get(),
        ModItems.MOONSTONE.get(),
        new Mat[]{
            new Mat(ModItems.MOONSTONE.get(), 2),
            new Mat(Items.BONE, 2),
            new Mat(Items.GOLD_INGOT, 2),
            new Mat(ModItems.BLACK_MUD_REMNANT.get(), 4),
            new Mat(Items.NETHERITE_INGOT, 2)
        });

    public final String id;
    public final Item result;
    public final Item catalyst;
    public final Mat[] materials;

    public record Mat(Item item, int count) {}

    DaikatanaRitualRecipe(String id, Item result, Item catalyst, Mat[] materials) {
        this.id = id;
        this.result = result;
        this.catalyst = catalyst;
        this.materials = materials;
    }

    @Nullable
    public static DaikatanaRitualRecipe forCatalyst(Item held) {
        for (DaikatanaRitualRecipe r : values()) {
            if (r.catalyst == held) return r;
        }
        return null;
    }

    public boolean hasMaterials(net.minecraft.world.entity.player.Inventory inv) {
        for (Mat m : materials) {
            if (countItem(inv, m.item) < m.count) return false;
        }
        return true;
    }

    public void consumeMaterials(net.minecraft.world.entity.player.Inventory inv) {
        for (Mat m : materials) {
            int left = m.count;
            for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {
                var stack = inv.getItem(i);
                if (stack.is(m.item)) {
                    int take = Math.min(left, stack.getCount());
                    stack.shrink(take);
                    left -= take;
                }
            }
        }
    }

    private static int countItem(net.minecraft.world.entity.player.Inventory inv, Item item) {
        int total = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            if (stack.is(item)) total += stack.getCount();
        }
        return total;
    }
}
