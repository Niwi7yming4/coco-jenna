package com.cocojenna.weapon;

import com.cocojenna.CocoJennaMod;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** 每把武器共鳴祭壇二階段材料. */
public final class WeaponResonanceMaterials extends SimplePreparableReloadListener<Map<String, ItemStack>> {

    public static final WeaponResonanceMaterials INSTANCE = new WeaponResonanceMaterials();

    private static final Gson GSON = new Gson();
    private static final Map<String, ItemStack> BY_VARIANT = new HashMap<>();

    private WeaponResonanceMaterials() {}

    public static Optional<ItemStack> requiredMaterial(String variantId) {
        return Optional.ofNullable(BY_VARIANT.get(variantId));
    }

    public static boolean hasMaterial(PlayerInventoryProxy player, String variantId) {
        return requiredMaterial(variantId)
                .map(req -> player.count(req.getItem()) >= req.getCount())
                .orElse(true);
    }

    @FunctionalInterface
    public interface PlayerInventoryProxy {
        int count(Item item);
    }

    @Override
    protected Map<String, ItemStack> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<String, ItemStack> map = new HashMap<>();
        var loc = new ResourceLocation(CocoJennaMod.MOD_ID, "weapon_resonance_materials.json");
        manager.getResource(loc).ifPresent(res -> {
            try (var stream = res.open()) {
                JsonObject root = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                for (var entry : root.entrySet()) {
                    JsonObject o = entry.getValue().getAsJsonObject();
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(o.get("item").getAsString()));
                    int count = o.get("count").getAsInt();
                    if (item != null) map.put(entry.getKey(), new ItemStack(item, count));
                }
            } catch (Exception e) {
                CocoJennaMod.LOGGER.warn("weapon_resonance_materials load failed: {}", e.toString());
            }
        });
        return map;
    }

    @Override
    protected void apply(Map<String, ItemStack> prepared, ResourceManager manager, ProfilerFiller profiler) {
        BY_VARIANT.clear();
        BY_VARIANT.putAll(prepared);
    }
}
