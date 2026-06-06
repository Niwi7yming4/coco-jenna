package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * 貓之國 (Cat Kingdom) 維度定義
 *
 * <p>維度 ID: cocojenna:cat_kingdom
 *
 * <p>結構：
 * <ul>
 *   <li>絨毛森林 (Velvet Forest) — 初始區域</li>
 *   <li>月色小巷 (Moon Alley) — 中期區域</li>
 *   <li>初啼村 (First Cry Village) — 中心聚落</li>
 *   <li>齒輪鎮 (Gear Town) — 工業區域</li>
 *   <li>無明港 (Blind Water Port) — 黑市與港口</li>
 *   <li>嚎風峽谷 (Howling Gorge) — 危險地帶</li>
 *   <li>遺忘高塔 (Forgotten Tower) — 主線區域</li>
 *   <li>黎明高地 (Dawn Highlands) — 終局區域</li>
 *   <li>睡眠聖殿 (Sleep Sanctuary) — 精神空間</li>
 * </ul>
 */
public class ModDimensions {

    /** 貓之國維度 Key */
    public static final ResourceKey<Level> CAT_KINGDOM = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation(CocoJennaMod.MOD_ID, "cat_kingdom"));

    public static void register(IEventBus bus) {
        // 維度透過 data pack 定義（dimension/, dimension_type/, worldgen/）
        // 此處僅保存 ResourceKey 以供其他系統引用
    }
}
