package com.cocojenna.weapon;

import com.cocojenna.CocoJennaMod;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 從 data/cocojenna/weapon_skills/*.json 載入技能. */
public final class WeaponSkillRegistry extends SimplePreparableReloadListener<Map<String, WeaponSkillDefinition>> {

    public static final WeaponSkillRegistry INSTANCE = new WeaponSkillRegistry();

    private static final Gson GSON = new Gson();
    private static final Map<String, WeaponSkillDefinition> SKILLS = new HashMap<>();

    private WeaponSkillRegistry() {}

    public static Optional<WeaponSkillDefinition> get(String variantId) {
        return Optional.ofNullable(SKILLS.get(variantId));
    }

    public static boolean has(String variantId) {
        return SKILLS.containsKey(variantId);
    }

    @Override
    protected Map<String, WeaponSkillDefinition> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<String, WeaponSkillDefinition> map = new HashMap<>();
        var resources = manager.listResources("weapon_skills", id -> id.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            try (var stream = manager.getResource(entry.getKey()).orElseThrow().open()) {
                JsonObject root = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                String variantId = root.get("variant_id").getAsString();
                String skillId = root.has("skill_id") ? root.get("skill_id").getAsString() : variantId;
                String archetype = root.has("archetype") ? root.get("archetype").getAsString() : "slash";
                String vfxTheme = root.has("vfx_theme") ? root.get("vfx_theme").getAsString() : archetype;
                String soundKey = root.has("sound_key") ? root.get("sound_key").getAsString() : "sweep";
                String chargeProfile = root.has("charge_profile") ? root.get("charge_profile").getAsString() : "standard";
                WeaponChargeProfile profile = WeaponChargeProfile.fromId(chargeProfile);
                int charge = root.has("min_charge_ticks") ? root.get("min_charge_ticks").getAsInt() : profile.minTicks();
                int cd = root.has("cooldown_ticks") ? root.get("cooldown_ticks").getAsInt() : 60;
                float mana = root.has("mana_cost") ? root.get("mana_cost").getAsFloat() : 0f;
                List<WeaponSkillDefinition.StageEffect> stages = new ArrayList<>();
                if (root.has("stage_effects")) {
                    JsonArray arr = root.getAsJsonArray("stage_effects");
                    for (var el : arr) {
                        JsonObject o = el.getAsJsonObject();
                        stages.add(new WeaponSkillDefinition.StageEffect(
                                o.get("stage").getAsInt(),
                                o.get("damage_mult").getAsFloat(),
                                o.get("radius_mult").getAsFloat(),
                                o.has("particle_tier") ? o.get("particle_tier").getAsInt() : 1));
                    }
                }
                map.put(variantId, new WeaponSkillDefinition(variantId, skillId, archetype,
                        charge, cd, mana, vfxTheme, soundKey, chargeProfile, stages));
            } catch (Exception e) {
                CocoJennaMod.LOGGER.warn("Failed loading weapon skill {}: {}", entry.getKey(), e.toString());
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<String, WeaponSkillDefinition> prepared, ResourceManager manager, ProfilerFiller profiler) {
        SKILLS.clear();
        SKILLS.putAll(prepared);
        CocoJennaMod.LOGGER.info("Loaded {} weapon skill definitions", SKILLS.size());
    }
}
