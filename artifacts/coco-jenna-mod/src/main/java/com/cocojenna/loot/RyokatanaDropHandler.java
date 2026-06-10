package com.cocojenna.loot;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.item.RyokatanaRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 良快刀設計書補充掉落 — 主掉落由 entity loot table 處理（gen_ryokatana_content.py）.
 * 此處僅追加設計書 §4.1 中尚未有專屬怪物的稀有良快刀.
 */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public final class RyokatanaDropHandler {

    private RyokatanaDropHandler() {}

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        DamageSource src = event.getSource();
        if (!(src.getEntity() instanceof Player)) return;
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel)) return;

        DropRule rule = supplementaryRule(entity);
        if (rule == null) return;
        if (entity.getRandom().nextFloat() > rule.chance) return;

        var item = RyokatanaRegistry.get(rule.ryokatanaId);
        if (item == null) return;
        event.getDrops().add(entity.spawnAtLocation(new ItemStack(item.get())));
    }

    private record DropRule(String ryokatanaId, float chance) {}

    /** 設計書代理掉落：折紙鴉→低語偶、盲水幽母→盲水領主、螢幕貓→擬態貓、鐵鏽犬→原始混沌 */
    private static DropRule supplementaryRule(LivingEntity entity) {
        var type = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (type == null) return null;
        return switch (type.getPath()) {
            case "whispering_doll" -> new DropRule("origami_cut", 0.03f);
            case "blind_water_lord" -> new DropRule("jellyfish_bind", 0.05f);
            case "mimic_cat" -> new DropRule("screen_noise", 0.04f);
            case "primal_chaos" -> new DropRule("iron_rust_armor_break", 0.04f);
            case "wandering_sludge" -> new DropRule("whisper_mud", 0.03f);
            case "mud_farmer" -> new DropRule("lament_split", 0.05f);
            case "mud_guard" -> new DropRule("bronze_guard", 0.08f);
            case "mud_priest" -> new DropRule("fallen_velvet_claw", 0.08f);
            case "moon_guardian" -> new DropRule("moonlight_ripple", 0.25f);
            case "fur_ball_spirit" -> new DropRule("fish_bone_tide", 0.02f);
            case "shadow_claw" -> new DropRule("mimic_disguise", 0.04f);
            case "origami_crow" -> new DropRule("paper_crow_ink", 0.06f);
            case "glitch_cat" -> new DropRule("screen_noise", 0.05f);
            case "box_ghost" -> new DropRule("forgotten_page", 0.04f);
            case "arena_gladiator" -> new DropRule("iron_claw_apprentice", 0.03f);
            case "tape_colossus" -> new DropRule("velvet_cradle", 0.08f);
            case "catnip_dragon" -> new DropRule("silvervine_drunk", 0.07f);
            case "silenced_one" -> new DropRule("moon_shadow", 0.06f);
            case "blind_water_leech" -> new DropRule("jellyfish_bind", 0.03f);
            default -> null;
        };
    }
}
