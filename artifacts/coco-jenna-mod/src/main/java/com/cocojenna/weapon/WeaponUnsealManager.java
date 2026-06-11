package com.cocojenna.weapon;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.combat.CombatSoundHelper;
import com.cocojenna.combat.CombatVfxHelper;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.BlackMudBossEntity;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import com.cocojenna.item.DaikataItem;
import com.cocojenna.item.RyokatanaItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * 武器解封共鳴累積與階段升級（設計書第二卷）.
 */
public final class WeaponUnsealManager {

    public static final int RESONANCE_STAGE1 = 30;
    public static final int RESONANCE_STAGE2 = 80;
    public static final int RESONANCE_STAGE3 = 150;
    public static final int ALTAR_SHARD_COST_STAGE1 = 5;
    public static final int ALTAR_SHARD_COST_STAGE2 = 10;
    public static final int ALTAR_SHARD_COST_STAGE3 = 15;

    /** 全部 50 把良快刀完整四階段技能縮放 */
    public static final java.util.Set<String> PILOT_VARIANTS = java.util.Collections.unmodifiableSet(
            new java.util.HashSet<>(com.cocojenna.item.RyokatanaRegistry.all().keySet()));

    private WeaponUnsealManager() {}

    public static boolean supportsStageScaling(String variantId) {
        return WeaponSkillRegistry.has(variantId) || PILOT_VARIANTS.contains(variantId);
    }

    public static void onBlackMudKill(ServerPlayer player, LivingEntity victim, ItemStack weapon) {
        if (!WeaponData.isUnsealable(weapon)) return;
        WeaponData.bindPlayer(weapon, player.getUUID());
        int gain = 1;
        if (victim instanceof BlackMudBossEntity) {
            gain = 10;
            WeaponData.setBossKilledForTier3(weapon, true);
        } else if (victim.getMaxHealth() >= 40 || victim instanceof Monster m && m.getArmorValue() > 5) {
            gain = 3;
        }
        if (isInBlackMudZone(player)) {
            gain = (int) Math.ceil(gain * 1.5);
        }
        addResonanceAndCheck(player, weapon, gain);
    }

    public static void onMemoryShardGain(ServerPlayer player, ItemStack weapon) {
        if (!WeaponData.isUnsealable(weapon)) return;
        WeaponData.bindPlayer(weapon, player.getUUID());
        addResonanceAndCheck(player, weapon, 2);
    }

    public static void onPromotion(ServerPlayer player, ItemStack weapon) {
        if (!WeaponData.isUnsealable(weapon)) return;
        addResonanceAndCheck(player, weapon, 10);
    }

    /** Called after sequence promotion — both hands checked. */
    public static void onSequencePromotion(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        onPromotion(player, main);
        if (off != main) {
            onPromotion(player, off);
        }
    }

    public static void onCatKingdomShrine(ServerPlayer player, ItemStack weapon) {
        if (!WeaponData.isUnsealable(weapon)) return;
        long day = player.level().getDayTime() / 24000L;
        if (WeaponData.getLastResonanceTime(weapon) == day) return;
        if (WeaponData.getDailyResonanceCount(weapon) >= 1) return;
        WeaponData.setLastResonanceTime(weapon, day);
        WeaponData.setDailyResonanceCount(weapon, WeaponData.getDailyResonanceCount(weapon) + 1);
        addResonanceAndCheck(player, weapon, 5);
    }

    public static boolean tryAltarInfusion(ServerPlayer player, ItemStack weapon, int memoryShards) {
        return tryAltarInfusion(player, weapon, memoryShards, false);
    }

    public static boolean tryAltarInfusion(ServerPlayer player, ItemStack weapon, int memoryShards,
            boolean waiveShardCost) {
        if (!WeaponData.isUnsealable(weapon)) return false;
        WeaponAwakeningStage stage = WeaponData.getStage(weapon);
        int cost = switch (stage) {
            case DORMANT -> ALTAR_SHARD_COST_STAGE1;
            case AWAKENED -> ALTAR_SHARD_COST_STAGE2;
            case ENLIGHTENED -> ALTAR_SHARD_COST_STAGE3;
            case RESONANCE -> 0;
        };
        if (cost == 0) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.max_stage")
                    .withStyle(ChatFormatting.GOLD), true);
            return false;
        }
        if (memoryShards < cost) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.need_shards", cost)
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (stage == WeaponAwakeningStage.AWAKENED && !WeaponData.isMemoryTaskCompleted(weapon)) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.need_memory_task")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (stage == WeaponAwakeningStage.ENLIGHTENED && !canReachResonanceStage(player, weapon)) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.need_resonance_req")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (!waiveShardCost && !player.getAbilities().instabuild) {
            consumeMemoryShards(player, cost);
        }
        String variantId = weapon.getItem() instanceof RyokatanaItem r ? r.getVariantId()
                : weapon.getItem() instanceof DaikataItem d ? d.getVariantId() : "";
        if (!variantId.isEmpty()) {
            var mat = WeaponResonanceMaterials.requiredMaterial(variantId);
            if (mat.isPresent() && stage == WeaponAwakeningStage.ENLIGHTENED) {
                if (!hasMaterial(player, mat.get())) {
                    player.displayClientMessage(Component.translatable("weapon.cocojenna.need_resonance_mat")
                            .withStyle(ChatFormatting.RED), true);
                    return false;
                }
                if (!player.getAbilities().instabuild) consumeStack(player, mat.get());
            }
        }
        WeaponData.addResonance(weapon, cost * 2);
        checkStageUpgrade(player, weapon);
        return true;
    }

    private static boolean hasMaterial(ServerPlayer player, ItemStack req) {
        return player.getInventory().countItem(req.getItem()) >= req.getCount();
    }

    private static void consumeStack(ServerPlayer player, ItemStack req) {
        int left = req.getCount();
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (s.is(req.getItem())) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }

    private static void consumeMemoryShards(ServerPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().items.size() && left > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (!s.is(ModItems.MEMORY_SHARD.get())) continue;
            int take = Math.min(left, s.getCount());
            s.shrink(take);
            left -= take;
        }
    }

    public static void tickPlayer(ServerPlayer player) {
        if (player.tickCount % 20 != 0) return;
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (WeaponData.isUnsealable(main)) {
            tickCatProximityBonus(player, main);
            tickShrineBonus(player, main);
            checkStageUpgrade(player, main);
        }
        if (WeaponData.isUnsealable(off) && off != main) {
            tickCatProximityBonus(player, off);
            tickShrineBonus(player, off);
            checkStageUpgrade(player, off);
        }
    }

    private static void tickShrineBonus(ServerPlayer player, ItemStack weapon) {
        if (player.tickCount % 100 != 0) return;
        if (!isCatKingdomShrine(player.blockPosition(), player.level())) return;
        onCatKingdomShrine(player, weapon);
    }

    private static void tickCatProximityBonus(ServerPlayer player, ItemStack weapon) {
        long now = player.level().getGameTime();
        if (now - WeaponData.getLastCatBonusTick(weapon) < 1200) return;
        AABB box = player.getBoundingBox().inflate(8);
        boolean catNear = !player.level().getEntitiesOfClass(AbstractCatEntity.class, box,
                c -> player.getUUID().equals(c.getOwnerUUID())).isEmpty();
        if (!catNear) return;
        WeaponData.setLastCatBonusTick(weapon, now);
        addResonanceAndCheck(player, weapon, 1);
    }

    private static void addResonanceAndCheck(ServerPlayer player, ItemStack weapon, int amount) {
        if (amount <= 0) return;
        WeaponData.addResonance(weapon, amount);
        checkStageUpgrade(player, weapon);
    }

    public static void checkStageUpgrade(ServerPlayer player, ItemStack weapon) {
        WeaponAwakeningStage current = WeaponData.getStage(weapon);
        int resonance = WeaponData.getResonance(weapon);
        WeaponAwakeningStage target = current;

        if (current == WeaponAwakeningStage.DORMANT && resonance >= RESONANCE_STAGE1) {
            target = WeaponAwakeningStage.AWAKENED;
        } else if (current == WeaponAwakeningStage.AWAKENED
                && resonance >= RESONANCE_STAGE2
                && WeaponData.isMemoryTaskCompleted(weapon)) {
            target = WeaponAwakeningStage.ENLIGHTENED;
        } else if (current == WeaponAwakeningStage.ENLIGHTENED
                && resonance >= RESONANCE_STAGE3
                && canReachResonanceStage(player, weapon)) {
            target = WeaponAwakeningStage.RESONANCE;
        }

        if (target.id > current.id) {
            upgradeToStage(player, weapon, target);
        }
    }

    public static boolean canReachResonanceStage(ServerPlayer player, ItemStack weapon) {
        BondData bond = ModCapabilities.getOrDefault(player);
        return bond.getCocoEmotion() >= 80
                && bond.getJennaEmotion() >= 80
                && WeaponData.isBossKilledForTier3(weapon);
    }

    public static void upgradeToStage(ServerPlayer player, ItemStack weapon, WeaponAwakeningStage stage) {
        WeaponAwakeningStage prev = WeaponData.getStage(weapon);
        if (stage.id <= prev.id) return;
        WeaponData.setStage(weapon, stage);
        String variant = WeaponData.variantId(weapon);
        Component itemKey = weaponKey(weapon);

        player.displayClientMessage(Component.translatable("weapon.cocojenna.stage_hint." + stage.key, itemKey)
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        player.displayClientMessage(Component.translatable("weapon.cocojenna.stage_unlock." + stage.key, itemKey)
                .withStyle(ChatFormatting.GOLD), true);

        player.level().playSound(null, player.blockPosition(),
                com.cocojenna.init.ModSounds.COMBAT_WEAPON_AWAKEN.get(),
                SoundSource.PLAYERS, 0.8f, 1.0f + stage.id * 0.15f);

        var force = CombatVfxHelper.of(ModCapabilities.getOrDefault(player).getFelineForce());
        int vfxTier = Math.min(4, stage.id + 1);
        CombatVfxHelper.skillCast(player.serverLevel(), player, force, vfxTier, false);
        CombatSoundHelper.Layer soundLayer = switch (stage) {
            case AWAKENED -> CombatSoundHelper.Layer.BASE;
            case ENLIGHTENED -> CombatSoundHelper.Layer.CRIT;
            case RESONANCE -> CombatSoundHelper.Layer.BOSS;
            default -> CombatSoundHelper.Layer.ENV;
        };
        CombatSoundHelper.play(player.serverLevel(), player.position(), soundLayer, force);
    }

    /** 秦可沐強行喚醒到甦醒（Week 6 完整化，此處提供 API）. */
    public static void forceAwakenToStage1(ItemStack weapon) {
        if (WeaponData.getStage(weapon).id < WeaponAwakeningStage.AWAKENED.id) {
            WeaponData.setStage(weapon, WeaponAwakeningStage.AWAKENED);
            WeaponData.setResonance(weapon, Math.max(WeaponData.getResonance(weapon), RESONANCE_STAGE1));
        }
    }

    private static Component weaponKey(ItemStack weapon) {
        if (weapon.getItem() instanceof RyokatanaItem r) {
            return Component.translatable("item.cocojenna.ryokatana_" + r.getVariantId());
        }
        if (weapon.getItem() instanceof DaikataItem d) {
            return Component.translatable("item.cocojenna.daikata_" + d.getVariantId());
        }
        return weapon.getHoverName();
    }

    private static boolean isInBlackMudZone(Player player) {
        return player.getPersistentData().getInt("cocojenna_blackmud_stage") >= 2;
    }

    public static boolean isCatKingdomShrine(BlockPos pos, Level level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return false;
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-6, -3, -6), pos.offset(6, 3, 6))) {
            var state = level.getBlockState(p);
            if (state.is(com.cocojenna.init.ModBlocks.FULL_MOON_ALTAR.get())
                    || state.is(com.cocojenna.init.ModBlocks.MOON_TRIAL_ALTAR.get())
                    || state.is(com.cocojenna.init.ModBlocks.MEMORY_MONUMENT_BASE.get())
                    || state.is(com.cocojenna.init.ModBlocks.MEMORY_MONUMENT_TOP.get())
                    || state.is(com.cocojenna.init.ModBlocks.ALTAR_FOUNDATION.get())) {
                return true;
            }
        }
        return false;
    }

    /** 秦可沐強行喚醒到甦醒（7 日冷卻，消耗好感度）. */
    public static boolean tryQinForceAwaken(ServerPlayer player, ItemStack weapon, BondData bond) {
        if (!WeaponData.isUnsealable(weapon)) return false;
        if (WeaponData.getStage(weapon).id >= WeaponAwakeningStage.AWAKENED.id) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.qin_already_awake")
                    .withStyle(ChatFormatting.GRAY), true);
            return false;
        }
        long now = player.level().getGameTime();
        if (now < bond.getQinWeaponAwakenCooldownUntil()) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.qin_awaken_cooldown")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (bond.getQinKemuFavor() < 40) {
            player.displayClientMessage(Component.translatable("weapon.cocojenna.qin_awaken_favor")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        WeaponData.bindPlayer(weapon, player.getUUID());
        WeaponData.setResonance(weapon, Math.max(WeaponData.getResonance(weapon), RESONANCE_STAGE1));
        if (WeaponData.getStage(weapon) == WeaponAwakeningStage.DORMANT) {
            upgradeToStage(player, weapon, WeaponAwakeningStage.AWAKENED);
        }
        bond.addQinKemuFavor(-10);
        bond.setQinWeaponAwakenCooldownUntil(now + 7L * 24000L);
        player.displayClientMessage(Component.translatable("weapon.cocojenna.qin_force_awaken")
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        return true;
    }

    /** 紙系武器在皇陵中獲得額外共鳴（設計書第八卷）. */
    public static void applyPaperWeaponMausoleumBonus(ItemStack weapon) {
        if (!WeaponData.isUnsealable(weapon)) return;
        String variant = WeaponData.variantId(weapon);
        if (variant.contains("paper") || variant.contains("hibiscus") || variant.contains("sanhua")) {
            WeaponData.addResonance(weapon, 20);
        }
    }
}
