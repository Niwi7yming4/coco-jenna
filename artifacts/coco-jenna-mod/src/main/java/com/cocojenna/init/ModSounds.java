package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CocoJennaMod.MOD_ID);

    // ── Coco 音效 ──────────────────────────────────────────────────────────
    /** 短促提醒 */
    public static final RegistryObject<SoundEvent> COCO_MEOW_SHORT =
            register("entity.coco.meow_short");
    /** 低沉擔憂（玩家危險時） */
    public static final RegistryObject<SoundEvent> COCO_MEOW_CONCERN =
            register("entity.coco.meow_concern");
    /** 深沉呼嚕（極度放鬆） */
    public static final RegistryObject<SoundEvent> COCO_PURR_DEEP =
            register("entity.coco.purr_deep");
    /** 嘶嘶聲（威脅敵人） */
    public static final RegistryObject<SoundEvent> COCO_HISS =
            register("entity.coco.hiss");

    // ── Jenna 音效 ─────────────────────────────────────────────────────────
    /** 上揚音調，表示好奇 */
    public static final RegistryObject<SoundEvent> JENNA_MEOW_QUESTION =
            register("entity.jenna.meow_question");
    /** 短促快速，看到食物或玩具 */
    public static final RegistryObject<SoundEvent> JENNA_MEOW_EXCITED =
            register("entity.jenna.meow_excited");
    /** 連續低鳴，表示不滿或被忽略 */
    public static final RegistryObject<SoundEvent> JENNA_MEOW_COMPLAINT =
            register("entity.jenna.meow_complaint");
    /** 輕柔呼嚕，隨時隨地 */
    public static final RegistryObject<SoundEvent> JENNA_PURR_LIGHT =
            register("entity.jenna.purr_light");

    // ── 世界音效 ──────────────────────────────────────────────────────────
    /** 初晴事件音樂 */
    public static final RegistryObject<SoundEvent> WORLD_FIRST_DAWN =
            register("world.first_dawn");
    /** 貓之國環境音 */
    public static final RegistryObject<SoundEvent> WORLD_CAT_KINGDOM_AMBIENT =
            register("world.cat_kingdom_ambient");
    /** 滿月祭典音樂 */
    public static final RegistryObject<SoundEvent> WORLD_FULL_MOON_FESTIVAL =
            register("world.full_moon_festival");
    /** 記憶碎片拾取音效 */
    public static final RegistryObject<SoundEvent> ITEM_MEMORY_SHARD_PICKUP =
            register("item.memory_shard.pickup");
    /** 蒸餾台運作音效 */
    public static final RegistryObject<SoundEvent> BLOCK_DISTILLER_WORK =
            register("block.distiller.work");
    /** 封印物凝結音效 */
    public static final RegistryObject<SoundEvent> ENTITY_SEAL_FORM =
            register("entity.seal.form");
    /** 黑泥侵蝕音效 */
    public static final RegistryObject<SoundEvent> WORLD_BLACK_MUD_SPREAD =
            register("world.black_mud_spread");
    /** 絨毛森林區域環境 */
    public static final RegistryObject<SoundEvent> WORLD_VELVET_FOREST_AMBIENT =
            register("world.velvet_forest_ambient");
    /** 月影巷區域環境 */
    public static final RegistryObject<SoundEvent> WORLD_MOON_ALLEY_AMBIENT =
            register("world.moon_alley_ambient");
    /** 初啼村日間氛圍 */
    public static final RegistryObject<SoundEvent> WORLD_FIRST_CRY_AMBIENT =
            register("world.first_cry_ambient");
    /** 地下貓域霓虹環境 */
    public static final RegistryObject<SoundEvent> WORLD_UNDERCAT_AMBIENT =
            register("world.undercat_ambient");
    /** 序列晉升儀式完成 */
    public static final RegistryObject<SoundEvent> CEREMONY_PROMOTION_COMPLETE =
            register("ceremony.promotion_complete");
    /** 武器覺醒階段提升 */
    public static final RegistryObject<SoundEvent> COMBAT_WEAPON_AWAKEN =
            register("combat.weapon_awaken");
    /** 王國交易成交 */
    public static final RegistryObject<SoundEvent> KINGDOM_TRADE_COMPLETE =
            register("kingdom.trade_complete");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CocoJennaMod.MOD_ID, name)));
    }
}
