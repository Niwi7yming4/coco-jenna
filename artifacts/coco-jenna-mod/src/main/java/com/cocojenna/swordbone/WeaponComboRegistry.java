package com.cocojenna.swordbone;

import com.cocojenna.capability.BondData;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/** 武器／劍骨組合技 — 設計書 2.4. */
public final class WeaponComboRegistry {

    public record ComboBonus(String id, float damageMult, float moveMult, String tag) {}

    private WeaponComboRegistry() {}

    public static ComboBonus active(Player player, BondData bond, ItemStack main, ItemStack off) {
        String mainId = id(main);
        String offId = id(off);
        if ("ryokatana_fish_bone_tide".equals(mainId) && "cat_bell_offhand".equals(offId)) {
            return new ComboBonus("fisher_cat_partner", 1.10f, 1f, "summon_cat");
        }
        long moonTags = bond.getSwordBones().stream()
                .filter(e -> !e.damaged())
                .map(SwordBoneEntry::weaponId)
                .filter(id -> id.contains("moon") || id.contains("moonlight"))
                .count();
        if (moonTags >= 3 && player.level().isNight()) {
            return new ComboBonus("moon_blessing", 1.15f, 1f, "moon");
        }
        long blindTags = bond.getSwordBones().stream()
                .filter(e -> !e.damaged())
                .map(SwordBoneEntry::weaponId)
                .filter(id -> id.contains("blind_water"))
                .count();
        if (blindTags >= 2 && hasBlindWaterGear(player)) {
            return new ComboBonus("abyss_walker", 1f, 1f, "water_speed");
        }
        return null;
    }

    private static String id(ItemStack stack) {
        if (stack.isEmpty()) return "";
        Item item = stack.getItem();
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    private static boolean hasBlindWaterGear(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.DAIKATANA_ABYSS.get())
                    || stack.is(ModItems.MUSOU_ABYSS_DEPTH.get())
                    || (RyokatanaRegistry.get("blind_water_core") != null
                    && stack.is(RyokatanaRegistry.get("blind_water_core").get()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRyokatanaId(String path) {
        return path.startsWith("ryokatana_")
                && RyokatanaRegistry.find(path.substring("ryokatana_".length())).isPresent();
    }
}
