package com.cocojenna.society;

import com.cocojenna.capability.FragmentedSequenceData;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.MudFarmerEntity;
import com.cocojenna.entity.MudGuardEntity;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** 破碎序列村民行為 — GoalSelector 優先級表簡化實作. */
public final class FragmentedVillagerBrain {

    private FragmentedVillagerBrain() {}

    public static void tick(Villager villager, FragmentedSequenceData data) {
        if (data.getRitualCooldown() > 0) {
            data.setRitualCooldown(data.getRitualCooldown() - 20);
        }
        int tick = data.getBehaviorTick() + 20;
        data.setBehaviorTick(tick);

        Player nearest = villager.level().getNearestPlayer(villager, 12);
        if (nearest != null && nearest.distanceTo(villager) < 4) {
            greetPlayer(villager, nearest, data);
        }

        switch (data.getProfession().category()) {
            case PLAY -> tickPlay(villager, data);
            case COMBAT -> tickCombat(villager, data);
            case RITUAL -> tickRitual(villager, data);
        }
    }

    private static void greetPlayer(Villager villager, Player player, FragmentedSequenceData data) {
        data.addBond(1);
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            FragmentedSequenceManager.onFirstMeet(sp, villager);
        }
    }

    private static void tickPlay(Villager villager, FragmentedSequenceData data) {
        AbstractCatEntity cat = findNearestCat(villager, 16);
        if (cat != null && villager.distanceTo(cat) > 3) {
            villager.getNavigation().moveTo(cat, 0.6);
        } else if (cat != null && villager.getRandom().nextInt(40) == 0) {
            cat.setDeltaMovement(cat.getDeltaMovement().add(0, 0.2, 0));
        }
        if (!(villager.level() instanceof ServerLevel level)) return;
        if (level.getMoonBrightness() > 0.9f && data.getStrength() >= 2
                && level.getDayTime() % 24000 > 13000) {
            for (Player p : level.getEntitiesOfClass(Player.class, villager.getBoundingBox().inflate(8))) {
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
                p.addEffect(new MobEffectInstance(MobEffects.LUCK, 200, 0));
            }
        }
    }

    private static void tickCombat(Villager villager, FragmentedSequenceData data) {
        AbstractCatEntity cat = findNearestCat(villager, 16);
        if (cat != null && cat.getHealth() < cat.getMaxHealth() * 0.5f) {
            villager.getNavigation().moveTo(cat, 1.0);
            return;
        }
        LivingEntity target = findMudTarget(villager, 16);
        if (target != null) {
            villager.getNavigation().moveTo(target, 0.85);
            if (villager.distanceTo(target) < 2.5 && villager.tickCount % 20 == 0) {
                float before = target.getHealth();
                target.hurt(villager.damageSources().mobAttack(villager), 2f + data.getStrength());
                if (!target.isAlive() && before > 0 && villager.getRandom().nextFloat() < 0.3f) {
                    tryDropGuardianBadge(villager);
                }
            }
        } else if (villager.tickCount % 100 == 0) {
            BlockPos patrol = villager.blockPosition().offset(
                    villager.getRandom().nextInt(17) - 8, 0, villager.getRandom().nextInt(17) - 8);
            var path = villager.getNavigation().createPath(patrol, 0);
            if (path != null) {
                villager.getNavigation().moveTo(path, 0.5);
            }
        }
    }

    private static void tickRitual(Villager villager, FragmentedSequenceData data) {
        if (!(villager.level() instanceof ServerLevel level)) return;
        long dayTime = level.getDayTime() % 24000;
        boolean fullMoon = level.getMoonBrightness() > 0.9f;
        if (fullMoon && dayTime > 13000 && dayTime < 23000 && data.getRitualCooldown() <= 0) {
            AABB area = villager.getBoundingBox().inflate(8);
            for (Player p : level.getEntitiesOfClass(Player.class, area)) {
                if (data.getProfession().category() == FragmentedProfession.Category.RITUAL) {
                    p.addEffect(new MobEffectInstance(MobEffects.LUCK, 400, 1));
                    p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                    if (p instanceof ServerPlayer sp) {
                        FragmentedQuestManager.onRitualWitness(sp, villager);
                    }
                } else {
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
                    p.addEffect(new MobEffectInstance(MobEffects.LUCK, 200, 0));
                }
            }
            data.setRitualCooldown(6000);
        }
        if (data.getStrength() >= 2 && villager.tickCount % 400 == 0) {
            Player nearest = villager.level().getNearestPlayer(villager, 6);
            if (nearest instanceof ServerPlayer sp && sp.getMainHandItem().is(ModItems.MEMORY_SHARD.get())) {
                sp.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("fragmented.cocojenna.memory_hint"),
                        true);
            }
        }
    }

    public static int countNearbyCats(Villager villager, double radius) {
        return villager.level().getEntitiesOfClass(AbstractCatEntity.class,
                villager.getBoundingBox().inflate(radius)).size();
    }

    public static int countNearbyMud(Villager villager, double radius) {
        BlockPos center = villager.blockPosition();
        int count = 0;
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                if (villager.level().getBlockState(center.offset(dx, 0, dz))
                        .is(ModBlocks.BLACK_MUD.get())) {
                    count++;
                }
            }
        }
        return count;
    }

    private static AbstractCatEntity findNearestCat(Villager villager, double radius) {
        return villager.level().getEntitiesOfClass(AbstractCatEntity.class,
                villager.getBoundingBox().inflate(radius))
                .stream()
                .min((a, b) -> Double.compare(villager.distanceTo(a), villager.distanceTo(b)))
                .orElse(null);
    }

    private static LivingEntity findMudTarget(Villager villager, double radius) {
        return villager.level().getEntitiesOfClass(LivingEntity.class,
                        villager.getBoundingBox().inflate(radius))
                .stream()
                .filter(e -> e instanceof MudGuardEntity || e instanceof MudFarmerEntity
                        || e instanceof Monster)
                .filter(e -> e != villager)
                .min((a, b) -> Double.compare(villager.distanceTo(a), villager.distanceTo(b)))
                .orElse(null);
    }

    private static void tryDropGuardianBadge(Villager villager) {
        villager.spawnAtLocation(new net.minecraft.world.item.ItemStack(ModItems.GUARDIAN_BADGE.get()));
        Player nearest = villager.level().getNearestPlayer(villager, 12);
        if (nearest instanceof ServerPlayer sp) {
            sp.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("fragmented.cocojenna.guardian_badge_drop"),
                    true);
        }
    }
}
