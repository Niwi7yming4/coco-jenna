package com.cocojenna.init;

import com.cocojenna.CocoJennaMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

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

    // ── Inner helper ────────────────────────────────────────────────────────

    private static class SimpleCatEffect extends MobEffect {
        protected SimpleCatEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
