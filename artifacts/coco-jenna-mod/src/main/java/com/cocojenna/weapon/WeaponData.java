package com.cocojenna.weapon;

import com.cocojenna.item.DaikataItem;
import com.cocojenna.item.RyokatanaItem;
import com.cocojenna.item.SupremeCatClawItem;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 武器 NBT 解封資料（設計書第九卷 §9.1）.
 */
public final class WeaponData {

    public static final String ROOT = "cocojenna:WeaponData";
    private static final String AWAKENING = "AwakeningStage";
    private static final String RESONANCE = "Resonance";
    private static final String MEMORY_TASK = "MemoryTaskCompleted";
    private static final String BOSS_TIER3 = "BossKilledForTier3";
    private static final String BONDED = "BondedPlayer";
    private static final String TASK_ID = "MemoryTaskID";
    private static final String LAST_RES_TIME = "LastResonanceTime";
    private static final String DAILY_RES_COUNT = "DailyResonanceCount";
    private static final String LAST_CAT_BONUS = "LastCatBonusTick";

    private WeaponData() {}

    public static boolean isUnsealable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof RyokatanaItem || item instanceof DaikataItem
                || item instanceof SupremeCatClawItem;
    }

    public static CompoundTag getOrCreate(ItemStack stack) {
        CompoundTag root = stack.getOrCreateTag();
        if (!root.contains(ROOT)) {
            CompoundTag data = new CompoundTag();
            data.putInt(AWAKENING, 0);
            data.putInt(RESONANCE, 0);
            data.putBoolean(MEMORY_TASK, false);
            data.putBoolean(BOSS_TIER3, false);
            data.putString(BONDED, "");
            data.putString(TASK_ID, "");
            data.putLong(LAST_RES_TIME, 0);
            data.putInt(DAILY_RES_COUNT, 0);
            data.putLong(LAST_CAT_BONUS, 0);
            root.put(ROOT, data);
        }
        return root.getCompound(ROOT);
    }

    @Nullable
    public static CompoundTag get(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(ROOT)) return null;
        return stack.getTag().getCompound(ROOT);
    }

    public static WeaponAwakeningStage getStage(ItemStack stack) {
        CompoundTag data = get(stack);
        if (data == null) return WeaponAwakeningStage.DORMANT;
        return WeaponAwakeningStage.fromId(data.getInt(AWAKENING));
    }

    public static void setStage(ItemStack stack, WeaponAwakeningStage stage) {
        getOrCreate(stack).putInt(AWAKENING, stage.id);
    }

    public static int getResonance(ItemStack stack) {
        CompoundTag data = get(stack);
        return data == null ? 0 : data.getInt(RESONANCE);
    }

    public static void setResonance(ItemStack stack, int value) {
        getOrCreate(stack).putInt(RESONANCE, Math.max(0, value));
    }

    public static void addResonance(ItemStack stack, int amount) {
        setResonance(stack, getResonance(stack) + amount);
    }

    public static boolean isMemoryTaskCompleted(ItemStack stack) {
        CompoundTag data = get(stack);
        return data != null && data.getBoolean(MEMORY_TASK);
    }

    public static void setMemoryTaskCompleted(ItemStack stack, boolean done) {
        getOrCreate(stack).putBoolean(MEMORY_TASK, done);
    }

    public static boolean isBossKilledForTier3(ItemStack stack) {
        CompoundTag data = get(stack);
        return data != null && data.getBoolean(BOSS_TIER3);
    }

    public static void setBossKilledForTier3(ItemStack stack, boolean done) {
        getOrCreate(stack).putBoolean(BOSS_TIER3, done);
    }

    public static void bindPlayer(ItemStack stack, UUID playerId) {
        CompoundTag data = getOrCreate(stack);
        if (data.getString(BONDED).isEmpty()) {
            data.putString(BONDED, playerId.toString());
        }
    }

    @Nullable
    public static UUID getBondedPlayer(ItemStack stack) {
        CompoundTag data = get(stack);
        if (data == null) return null;
        String s = data.getString(BONDED);
        if (s.isEmpty()) return null;
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String getMemoryTaskId(ItemStack stack) {
        CompoundTag data = get(stack);
        return data == null ? "" : data.getString(TASK_ID);
    }

    public static void setMemoryTaskId(ItemStack stack, String id) {
        getOrCreate(stack).putString(TASK_ID, id);
    }

    public static long getLastCatBonusTick(ItemStack stack) {
        CompoundTag data = get(stack);
        return data == null ? 0 : data.getLong(LAST_CAT_BONUS);
    }

    public static void setLastCatBonusTick(ItemStack stack, long tick) {
        getOrCreate(stack).putLong(LAST_CAT_BONUS, tick);
    }

    public static int getDailyResonanceCount(ItemStack stack) {
        CompoundTag data = get(stack);
        return data == null ? 0 : data.getInt(DAILY_RES_COUNT);
    }

    public static void setDailyResonanceCount(ItemStack stack, int count) {
        getOrCreate(stack).putInt(DAILY_RES_COUNT, count);
    }

    public static long getLastResonanceTime(ItemStack stack) {
        CompoundTag data = get(stack);
        return data == null ? 0 : data.getLong(LAST_RES_TIME);
    }

    public static void setLastResonanceTime(ItemStack stack, long day) {
        getOrCreate(stack).putLong(LAST_RES_TIME, day);
    }

    /** 技能威力倍率（階段 + 強化）. */
    public static float skillPowerMultiplier(ItemStack stack) {
        WeaponAwakeningStage stage = getStage(stack);
        float stageMult = switch (stage) {
            case DORMANT -> 0f;
            case AWAKENED -> 0.7f;
            case ENLIGHTENED -> 1.0f;
            case RESONANCE -> 1.4f;
        };
        int enhance = WeaponEnhanceHelper.getLevel(stack);
        return stageMult * (1f + enhance * 0.03f);
    }

    public static float attackMultiplier(ItemStack stack) {
        return getStage(stack).attackMultiplier * WeaponEnhanceHelper.damageMultiplier(WeaponEnhanceHelper.getLevel(stack));
    }

    public static int skillCooldownTicks(ItemStack stack, int baseCooldown) {
        return switch (getStage(stack)) {
            case DORMANT -> baseCooldown;
            case AWAKENED -> (int) (baseCooldown * 1.2f);
            case ENLIGHTENED -> baseCooldown;
            case RESONANCE -> (int) (baseCooldown * 0.85f);
        };
    }

    public static String variantId(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof RyokatanaItem r) return r.getVariantId();
        if (item instanceof DaikataItem d) return d.getVariantId();
        return "";
    }
}
