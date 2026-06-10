package com.cocojenna.combat;

import com.cocojenna.blackmud.BlackMudBlocks;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** 蒸餾戰鬥：破殼 → 核心暴露 5–8 秒 → R 鍵蒸餾（貓之國 Ch.8.4）. */
public final class DistillCombatManager {

    private static final String SHELL_HITS = "cocojenna_shell_hits";
    private static final String CORE_TICKS = "cocojenna_core_ticks";
    private static final String DISTILL_READY = "cocojenna_distill_ready";
    private static final String LOOT_GRANTED = "cocojenna_distill_loot";

    private DistillCombatManager() {}

    public static void armDistillStrike(ServerPlayer player) {
        player.getPersistentData().putBoolean(DISTILL_READY, true);
        player.displayClientMessage(Component.translatable("combat.cocojenna.distill.armed"), true);
    }

    public static boolean isDistillArmed(ServerPlayer player) {
        return player.getPersistentData().getBoolean(DISTILL_READY);
    }

    public static boolean consumeDistillStrike(ServerPlayer player) {
        if (!player.getPersistentData().getBoolean(DISTILL_READY)) return false;
        player.getPersistentData().remove(DISTILL_READY);
        return true;
    }

    public static void exposeCore(LivingEntity mob, int ticks) {
        mob.getPersistentData().putInt(CORE_TICKS, ticks);
        mob.getPersistentData().remove(SHELL_HITS);
        mob.setGlowingTag(true);
    }

    public static boolean isCoreExposed(LivingEntity mob) {
        return mob.getPersistentData().getInt(CORE_TICKS) > 0;
    }

    public static int shellHitsRequired(BlackMudMob mob) {
        return switch (mob.blackMudSequence()) {
            case 9 -> 2;
            case 8 -> 3;
            case 7 -> 3;
            case 6 -> 4;
            default -> 3;
        };
    }

    public static void tickEntity(LivingEntity mob) {
        int core = mob.getPersistentData().getInt(CORE_TICKS);
        if (core <= 0) return;
        int next = core - 1;
        mob.getPersistentData().putInt(CORE_TICKS, next);
        if (next == 0) {
            mob.setGlowingTag(false);
            if (mob instanceof BlackMudMob bm) {
                mob.getPersistentData().putInt(SHELL_HITS, 0);
            }
        }
        if (mob.level() instanceof ServerLevel sl && mob.tickCount % 6 == 0) {
            sl.sendParticles(ModParticles.CHAOS_CONFETTI.get(),
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.55, mob.getZ(),
                    2, 0.25, 0.2, 0.25, 0.02);
        }
    }

    public static float damageMultiplier(LivingEntity target, boolean armedDistill) {
        if (!isCoreExposed(target)) return 1.0f;
        return armedDistill ? 2.5f : 1.75f;
    }

    /** 每次命中黑泥：削殼；殼破則暴露核心. */
    public static void onBlackMudHit(ServerPlayer player, LivingEntity target, boolean armedDistill) {
        if (!(target instanceof BlackMudMob bm)) return;
        if (isCoreExposed(target)) {
            if (armedDistill) {
                player.displayClientMessage(Component.translatable("combat.cocojenna.distill.core_hit"), true);
            }
            return;
        }
        int hits = target.getPersistentData().getInt(SHELL_HITS);
        int need = shellHitsRequired(bm);
        int delta = armedDistill ? 2 : 1;
        hits += delta;
        if (hits >= need) {
            int exposeTicks = 100 + player.getRandom().nextInt(41);
            exposeCore(target, exposeTicks);
            player.displayClientMessage(Component.translatable("combat.cocojenna.distill.core_exposed"), true);
        } else {
            target.getPersistentData().putInt(SHELL_HITS, hits);
            if (armedDistill && hits + 1 >= need) {
                player.displayClientMessage(Component.translatable("combat.cocojenna.distill.shell_crack"), true);
            }
        }
    }

    public static void onDistillKill(ServerPlayer player, LivingEntity target) {
        if (!(target instanceof BlackMudMob bm)) return;
        if (!isCoreExposed(target)) return;
        grantDistillLoot(player, bm);
        purifyUnderfoot(player.serverLevel(), target.blockPosition());
        player.displayClientMessage(Component.translatable("combat.cocojenna.distill.success"), true);
    }

    public static boolean wasDistillLootGranted(LivingEntity mob) {
        return mob.getPersistentData().getBoolean(LOOT_GRANTED);
    }

    private static void grantDistillLoot(ServerPlayer player, BlackMudMob mob) {
        LivingEntity entity = (LivingEntity) mob;
        entity.getPersistentData().putBoolean(LOOT_GRANTED, true);
        com.cocojenna.blackmud.BlackMudDropManager.onKill(player, entity, true);
        int seq = mob.blackMudSequence();
        int shards = Math.max(1, (10 - seq) / 2);
        ItemStack shard = new ItemStack(ModItems.MEMORY_SHARD.get(), shards);
        if (player.getRandom().nextFloat() < 0.55f) {
            if (!player.addItem(shard)) player.drop(shard, false);
        }
        if (player.getRandom().nextFloat() < 0.12f) {
            ItemStack tear = new ItemStack(ModItems.PURE_TEAR.get());
            if (!player.addItem(tear)) player.drop(tear, false);
        }
        player.giveExperiencePoints(8 + (10 - seq) * 4);
    }

    private static void purifyUnderfoot(ServerLevel level, BlockPos pos) {
        BlockPos ground = pos.below();
        BlockState state = level.getBlockState(ground);
        if (BlackMudBlocks.isBlackMud(state)) {
            level.setBlockAndUpdate(ground, BlackMudBlocks.blockStateForStage(1));
        }
    }
}
