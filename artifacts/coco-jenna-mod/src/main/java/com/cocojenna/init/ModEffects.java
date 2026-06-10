package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ModEffects {

    private static UUID modifierId(String key) {
        return UUID.nameUUIDFromBytes(("cocojenna:" + key).getBytes(StandardCharsets.UTF_8));
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CocoJennaMod.MOD_ID);

    /** 可可的印記 — 幸運 I */
    public static final RegistryObject<MobEffect> COCOS_MARK = MOB_EFFECTS.register(
            "cocos_mark",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xFFBF00));

    /** 安心 — 血量自然恢復 +50% */
    public static final RegistryObject<MobEffect> AT_EASE = MOB_EFFECTS.register(
            "at_ease",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xFFD700));

    /** 溫暖的寧靜 — 壓力值 -50%（視覺效果） */
    public static final RegistryObject<MobEffect> WARM_SERENITY = MOB_EFFECTS.register(
            "warm_serenity",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xFFA07A));

    /** 珍奶的關心 — 抗性 I */
    public static final RegistryObject<MobEffect> JENNAS_CARE = MOB_EFFECTS.register(
            "jennas_care",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xE6FF00));

    /** 被記住的感覺 — 記憶碎片掉落率 +100% */
    public static final RegistryObject<MobEffect> REMEMBERED = MOB_EFFECTS.register(
            "remembered",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xC0C0FF));

    /** 心靈同步 — 短暫看到貓視角 */
    public static final RegistryObject<MobEffect> MIND_SYNC = MOB_EFFECTS.register(
            "mind_sync",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xFF69B4));

    /** 被治癒 — 觀看姊妹互動時獲得 */
    public static final RegistryObject<MobEffect> HEALED_HEART = MOB_EFFECTS.register(
            "healed_heart",
            () -> new SimpleCatEffect(MobEffectCategory.BENEFICIAL, 0xFF1493));

    /** 黑泥寄生 第一階段 */
    public static final RegistryObject<MobEffect> BLACK_MUD_STAGE1 = MOB_EFFECTS.register(
            "black_mud_stage1",
            () -> new SimpleCatEffect(MobEffectCategory.HARMFUL, 0x222233));

    /** 黑泥寄生 第二階段 */
    public static final RegistryObject<MobEffect> BLACK_MUD_STAGE2 = MOB_EFFECTS.register(
            "black_mud_stage2",
            () -> new SimpleCatEffect(MobEffectCategory.HARMFUL, 0x110011));

    /** 黑泥寄生 第三階段 */
    public static final RegistryObject<MobEffect> BLACK_MUD_STAGE3 = MOB_EFFECTS.register(
            "black_mud_stage3",
            () -> new SimpleCatEffect(MobEffectCategory.HARMFUL, 0x000000));

    /** 黑泥寄生 第四階段 — 深淵化 */
    public static final RegistryObject<MobEffect> BLACK_MUD_STAGE4 = MOB_EFFECTS.register(
            "black_mud_stage4",
            () -> new SimpleCatEffect(MobEffectCategory.HARMFUL, 0x000005));

    /** 失溫 — 失溫者光環，移動速度 -10% */
    public static final RegistryObject<MobEffect> HEAT_LEECH_CHILL = MOB_EFFECTS.register(
            "heat_leech_chill",
            () -> new ChilledEffect());

    /** 腐蝕印記 — 盲水區域累積暴露，疊加黑泥階段 */
    public static final RegistryObject<MobEffect> CORROSION_MARK = MOB_EFFECTS.register(
            "corrosion_mark",
            () -> new SimpleCatEffect(MobEffectCategory.HARMFUL, 0x1A3A4A));

    /** 月光祝福 — 滿月祭典開幕式臨時增益 */
    public static final RegistryObject<MobEffect> MOON_BLESSING = MOB_EFFECTS.register(
            "moon_blessing",
            () -> new MoonBlessingEffect());

    // ── Inner helper ────────────────────────────────────────────────────────

    private static class SimpleCatEffect extends MobEffect {
        protected SimpleCatEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    /** 失溫者光環：移動速度 -10% */
    private static class ChilledEffect extends MobEffect {
        ChilledEffect() {
            super(MobEffectCategory.HARMFUL, 0x88CCFF);
            addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    modifierId("heat_leech_chill").toString(), -0.10,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    /** 滿月祭典：移動速度 +10% */
    private static class MoonBlessingEffect extends MobEffect {
        MoonBlessingEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xE8E8FF);
            addAttributeModifier(Attributes.MOVEMENT_SPEED,
                    modifierId("moon_blessing").toString(), 0.10,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }
}
