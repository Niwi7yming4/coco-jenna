package com.cocojenna.combat;

import com.cocojenna.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 武器類型戰鬥效果本體. */
public final class WeaponTypeCombat {

    private static final String RHYTHM_COMBO = "cocojenna_rhythm_combo";
    private static final String RHYTHM_TICK = "cocojenna_rhythm_tick";
    private static final String MORPH_FORM = "cocojenna_morph_form";

    private WeaponTypeCombat() {}

    public static float damageBonus(ServerPlayer player, ItemStack weapon, LivingEntity target) {
        WeaponType type = WeaponTypeRegistry.of(weapon);
        if (type == null) return 1f;
        return switch (type) {
            case RHYTHM -> 1f + Math.min(0.35f, player.getPersistentData().getInt(RHYTHM_COMBO) * 0.05f);
            case CURSE -> player.getHealth() / player.getMaxHealth() < 0.35f ? 1.25f : 1.12f;
            case ENVIRONMENT -> environmentBonus(player) ? 1.20f : 1f;
            case MORPH -> morphForm(weapon) == 1 ? 1.18f : 1.08f;
        };
    }

    public static void onHit(ServerPlayer player, ItemStack weapon, LivingEntity target) {
        WeaponType type = WeaponTypeRegistry.of(weapon);
        if (type == null) return;
        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        switch (type) {
            case RHYTHM -> {
                int last = data.getInt(RHYTHM_TICK);
                int combo = now - last <= 24 ? data.getInt(RHYTHM_COMBO) + 1 : 1;
                data.putInt(RHYTHM_COMBO, Math.min(7, combo));
                data.putInt(RHYTHM_TICK, (int) now);
            }
            case CURSE -> {
                if (player.getRandom().nextFloat() < 0.12f) {
                    player.hurt(player.damageSources().magic(), 1f);
                }
            }
            case MORPH -> toggleMorphOnShift(player, weapon);
            case ENVIRONMENT -> {
                if (player.isInWater()) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
                }
            }
        }
    }

    public static void tickDecay(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        if (now - data.getInt(RHYTHM_TICK) > 40) {
            data.putInt(RHYTHM_COMBO, 0);
        }
    }

    private static void toggleMorphOnShift(ServerPlayer player, ItemStack weapon) {
        if (!player.isShiftKeyDown()) return;
        CompoundTag tag = weapon.getOrCreateTag();
        int form = tag.getInt(MORPH_FORM);
        tag.putInt(MORPH_FORM, form == 0 ? 1 : 0);
    }

    private static int morphForm(ItemStack weapon) {
        return weapon.getOrCreateTag().getInt(MORPH_FORM);
    }

    private static boolean environmentBonus(ServerPlayer player) {
        BlockPos below = player.blockPosition().below();
        BlockState state = player.level().getBlockState(below);
        if (player.isInWater() || state.is(Blocks.WATER)) return true;
        if (state.is(BlockTags.SAND)) return true;
        if (player.level().getBiome(player.blockPosition()).is(ModTags.CAT_KINGDOM_BIOMES)) return true;
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS);
    }
}
