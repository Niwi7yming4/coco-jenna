package com.cocojenna.combat;

import com.cocojenna.entity.GlitchCatEntity;
import com.cocojenna.entity.OrigamiCrowEntity;
import com.cocojenna.init.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** 折紙鴉／螢幕雜訊貓 — 僅特定武器可傷害（設計書 §10.2）. */
public final class SpecialMobCombat {

    private SpecialMobCombat() {}

    public static boolean blocksDamage(LivingEntity target, DamageSource source, float amount) {
        if (target instanceof GlitchCatEntity) {
            return !canHarmGlitchCat(source);
        }
        if (target instanceof OrigamiCrowEntity) {
            return !canHarmOrigamiCrow(source);
        }
        return false;
    }

    public static boolean canHarmGlitchCat(DamageSource source) {
        return isSpecialWeapon(source, "musou_toy_hammer", "ryokatana_screen_noise", "tarot_deck");
    }

    public static boolean canHarmOrigamiCrow(DamageSource source) {
        return isSpecialWeapon(source, "musou_hibiscus_fall", "ryokatana_origami_cut", "tarot_deck");
    }

    private static boolean isSpecialWeapon(DamageSource source, String... itemIds) {
        if (!(source.getEntity() instanceof Player player)) return false;
        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) return false;
        String path = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(weapon.getItem()).getPath();
        for (String id : itemIds) {
            if (path.equals(id)) return true;
        }
        if (path.equals("musou_toy_hammer")) return true;
        return false;
    }

    public static float bonusDamage(LivingEntity target, DamageSource source) {
        if (target instanceof GlitchCatEntity && canHarmGlitchCat(source)) return 1.5f;
        if (target instanceof OrigamiCrowEntity && canHarmOrigamiCrow(source)) return 1.4f;
        return 1f;
    }
}
