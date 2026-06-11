package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.*;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenForceSelectionPacket;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.world.MoonCrossroadsPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 月光三岔路 — 初始源力自主選擇（設計書 v1.0 §1）. */
public final class MoonCrossroadsManager {

    public static final int STAGE_NONE = 0;
    public static final int STAGE_ACTIVE = 1;
    public static final int STAGE_CHOOSE = 2;
    public static final int STAGE_DONE = 3;

    public static final int MASK_RESONANCE = 1;
    public static final int MASK_SHADOW = 2;
    public static final int MASK_CHAOS = 4;
    public static final int MASK_ALL = 7;

    private static final int MAX_RESETS = 3;

    private static final Map<UUID, TrialSession> SESSIONS = new HashMap<>();

    private record TrialSession(String force, int progress, int goal, long deadlineTick) {}

    private MoonCrossroadsManager() {}

    public static boolean hasChosenForce(BondData bond) {
        return bond.getForceQuestStage() >= STAGE_DONE && !bond.getFelineForce().isEmpty();
    }

    public static boolean canStartQuest(ServerPlayer player, BondData bond) {
        if (hasChosenForce(bond)) return false;
        if (!com.cocojenna.integration.FallenAbyssLinkage.canCommitFelinePath(player)) return false;
        if (bond.getForceQuestStage() > STAGE_NONE) return false;
        if (bond.getCocoEmotion() < 20f || bond.getJennaEmotion() < 20f) return false;
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)
                && !bond.isGrayWhiskerMet()) {
            return false;
        }
        return true;
    }

    public static void onEnterKingdom(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (hasChosenForce(bond)) return;
        if (bond.getForceQuestStage() == STAGE_NONE && canStartQuest(player, bond)) {
            player.displayClientMessage(
                    Component.translatable("force.cocojenna.crossroads.hint_alpha"), true);
        }
    }

    public static void tryStartFromAlpha(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!canStartQuest(player, bond)) {
            if (!hasChosenForce(bond) && bond.getForceQuestStage() == STAGE_CHOOSE) {
                openSelection(player);
            } else if (bond.getCocoEmotion() < 20f || bond.getJennaEmotion() < 20f) {
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.crossroads.need_bond"), true);
            }
            return;
        }
        bond.setForceQuestStage(STAGE_ACTIVE);
        MoonCrossroadsPlacer.ensurePlaced((ServerLevel) player.level());
        DialogueManager.play(player, "moon_crossroads_intro");
        player.displayClientMessage(
                Component.translatable("force.cocojenna.crossroads.started"), true);
        sync(player, bond);
    }

    public static void onAltarInteract(ServerPlayer player, String force, BlockPos pos) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (hasChosenForce(bond)) {
            if (bond.getForceQuestStage() == STAGE_DONE) {
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.crossroads.promotion_hint"), true);
            }
            return;
        }
        if (bond.getForceQuestStage() < STAGE_ACTIVE) {
            player.displayClientMessage(
                    Component.translatable("force.cocojenna.crossroads.talk_alpha"), true);
            return;
        }
        int mask = maskFor(force);
        if ((bond.getForceTrialsMask() & mask) != 0) {
            player.displayClientMessage(
                    Component.translatable("force.cocojenna.crossroads.trial_done", forceLabel(force)), true);
            return;
        }
        if (SESSIONS.containsKey(player.getUUID())) {
            player.displayClientMessage(
                    Component.translatable("force.cocojenna.crossroads.trial_active"), true);
            return;
        }
        startTrial(player, bond, force, pos);
    }

    private static void startTrial(ServerPlayer player, BondData bond, String force, BlockPos altar) {
        ServerLevel level = player.serverLevel();
        long deadline = level.getGameTime() + 600;
        switch (force) {
            case "resonance" -> {
                SESSIONS.put(player.getUUID(), new TrialSession(force, 0, 3, Long.MAX_VALUE));
                spawnScarecrows(level, player, altar);
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.trial.resonance.start"), true);
            }
            case "shadow" -> {
                SESSIONS.put(player.getUUID(), new TrialSession(force, 0, 5, deadline));
                spawnGhostTargets(level, player, altar);
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.trial.shadow.start"), true);
            }
            case "chaos" -> {
                SESSIONS.put(player.getUUID(), new TrialSession(force, 0, 5, deadline));
                spawnBalloons(level, player, altar);
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.trial.chaos.start"), true);
            }
            default -> { return; }
        }
        bond.setActiveTrialForce(force);
        applyTrialBuff(player, force);
        sync(player, bond);
    }

    private static void applyTrialBuff(ServerPlayer player, String force) {
        switch (force) {
            case "resonance" -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));
            case "shadow" -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
            case "chaos" -> player.addEffect(new MobEffectInstance(MobEffects.LUCK, 600, 1));
        }
    }

    public static void tickPlayer(ServerPlayer player) {
        TrialSession session = SESSIONS.get(player.getUUID());
        if (session == null) return;
        if (session.deadlineTick() != Long.MAX_VALUE
                && player.level().getGameTime() > session.deadlineTick()) {
            failTrial(player, session.force());
        }
    }

    private static void failTrial(ServerPlayer player, String force) {
        SESSIONS.remove(player.getUUID());
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setActiveTrialForce("");
        clearTrialMobs(player);
        player.displayClientMessage(
                Component.translatable("force.cocojenna.trial.failed", forceLabel(force)), true);
        sync(player, bond);
    }

    public static void onTrialTargetDefeated(ServerPlayer player, Entity entity) {
        onTrialProgress(player, 1);
    }

    public static void onTrialTargetHit(ServerPlayer player, Entity entity, int amount) {
        onTrialProgress(player, amount);
    }

    private static void onTrialProgress(ServerPlayer player, int delta) {
        TrialSession session = SESSIONS.get(player.getUUID());
        if (session == null) return;
        int next = session.progress() + delta;
        if (next >= session.goal()) {
            completeTrial(player, session.force());
        } else {
            SESSIONS.put(player.getUUID(), new TrialSession(
                    session.force(), next, session.goal(), session.deadlineTick()));
        }
    }

    private static void completeTrial(ServerPlayer player, String force) {
        SESSIONS.remove(player.getUUID());
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setActiveTrialForce("");
        bond.addForceTrialMask(maskFor(force));
        clearTrialMobs(player);

        switch (force) {
            case "resonance" -> {
                bond.modifyCocoEmotion(3f);
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.trial.resonance.done"), true);
            }
            case "shadow" -> {
                bond.modifyJennaEmotion(3f);
                player.displayClientMessage(
                        Component.translatable("force.cocojenna.trial.shadow.done"), true);
            }
            case "chaos" -> player.displayClientMessage(
                    Component.translatable("force.cocojenna.trial.chaos.done"), true);
        }

        if ((bond.getForceTrialsMask() & MASK_ALL) == MASK_ALL) {
            bond.setForceQuestStage(STAGE_CHOOSE);
            openSelection(player);
        }
        sync(player, bond);
    }

    public static void openSelection(ServerPlayer player) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenForceSelectionPacket());
    }

    public static boolean confirmForce(ServerPlayer player, String force) {
        if (!force.equals("resonance") && !force.equals("shadow") && !force.equals("chaos")) {
            return false;
        }
        if (!com.cocojenna.integration.FallenAbyssLinkage.canCommitFelinePath(player)) {
            player.displayClientMessage(Component.translatable("fallen_abyss.path.blocked"), true);
            return false;
        }
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getForceQuestStage() != STAGE_CHOOSE) return false;
        if ((bond.getForceTrialsMask() & MASK_ALL) != MASK_ALL) return false;

        bond.setFelineForce(force);
        if (bond.getFelineTier() <= 0) bond.setFelineTier(9);
        bond.setForceQuestStage(STAGE_DONE);
        bond.setMarkForce(force);
        bond.setMarkLevel(1);

        grantNoviceBadge(player, force);
        applyForcePassives(player, bond, force);

        player.displayClientMessage(
                Component.translatable("force.cocojenna.chosen", forceLabel(force)), true);
        DialogueManager.play(player, "moon_crossroads_chosen");

        for (AbstractCatEntity cat : player.serverLevel().getEntitiesOfClass(
                AbstractCatEntity.class, player.getBoundingBox().inflate(16),
                c -> player.getUUID().equals(c.getOwnerUUID()))) {
            if (cat instanceof CocoEntity) {
                cat.setSitting(true);
            } else if (cat instanceof JennaEntity jenna) {
                jenna.setDeltaMovement(0, 0.5, 0);
            }
        }

        ServerLevel level = player.serverLevel();
        level.sendParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1, player.getZ(), 40, 0.6, 0.8, 0.6, 0.02);
        sync(player, bond);
        return true;
    }

    public static boolean tryResetForce(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!hasChosenForce(bond)) return false;
        if (bond.getForceResetCount() >= MAX_RESETS) {
            player.displayClientMessage(
                    Component.translatable("force.cocojenna.reset.limit"), true);
            return false;
        }
        String old = bond.getFelineForce();
        var kept = new java.util.ArrayList<>(bond.getOwnedPromotionCards());
        kept.removeIf(c -> c.startsWith(old + "_"));
        bond.replaceOwnedPromotionCards(kept);
        bond.setFelineForce("");
        bond.setForceQuestStage(STAGE_CHOOSE);
        bond.setForceResetCount(bond.getForceResetCount() + 1);
        bond.setMarkLevel(0);
        bond.setMarkForce("");
        player.displayClientMessage(
                Component.translatable("force.cocojenna.reset.done"), true);
        openSelection(player);
        sync(player, bond);
        return true;
    }

    private static void grantNoviceBadge(ServerPlayer player, String force) {
        ItemStack badge = new ItemStack(ModItems.NOVICE_FORCE_BADGE.get());
        badge.getOrCreateTag().putString("Force", force);
        if (!player.addItem(badge)) player.drop(badge, false);
    }

    public static void applyForcePassives(ServerPlayer player, BondData bond, String force) {
        switch (force) {
            case "resonance" -> {
                player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20 * 60 * 60 * 24, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, false, true));
            }
            case "shadow" -> {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60 * 60 * 24, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 20 * 60 * 60 * 24, 0, false, false, true));
            }
            case "chaos" -> {
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 20 * 60 * 60 * 24, 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 60 * 60 * 24, 0, false, false, true));
            }
        }
    }

    public static void tickNovicePassives(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!hasChosenForce(bond)) return;
        if (!hasNoviceBadge(player)) return;
        long t = player.level().getGameTime();
        if (t % 80 != 0) return;
        switch (bond.getFelineForce()) {
            case "resonance" -> {
                if (player.getHealth() < player.getMaxHealth()) player.heal(0.5f);
            }
            case "shadow" -> { /* speed via effect on choose */ }
            case "chaos" -> { /* dodge handled in combat hooks — skip */ }
        }
    }

    private static boolean hasNoviceBadge(ServerPlayer player) {
        for (ItemStack s : player.getInventory().items) {
            if (s.is(ModItems.NOVICE_FORCE_BADGE.get())) return true;
        }
        return false;
    }

    private static void spawnScarecrows(ServerLevel level, ServerPlayer player, BlockPos altar) {
        for (int i = 0; i < 3; i++) {
            var e = ModEntities.PRACTICE_SCARECROW.get().create(level);
            if (e == null) continue;
            e.bindTrialOwner(player.getUUID());
            e.moveTo(altar.getX() + 2 + i, altar.getY() + 1, altar.getZ() + 1, 0, 0);
            level.addFreshEntity(e);
        }
    }

    private static void spawnGhostTargets(ServerLevel level, ServerPlayer player, BlockPos altar) {
        for (int i = 0; i < 5; i++) {
            var e = ModEntities.GHOST_TARGET.get().create(level);
            if (e == null) continue;
            e.bindTrialOwner(player.getUUID());
            double ox = altar.getX() + (level.random.nextDouble() - 0.5) * 8;
            double oz = altar.getZ() + (level.random.nextDouble() - 0.5) * 8;
            e.moveTo(ox, altar.getY() + 2, oz, 0, 0);
            level.addFreshEntity(e);
        }
    }

    private static void spawnBalloons(ServerLevel level, ServerPlayer player, BlockPos altar) {
        for (int i = 0; i < 5; i++) {
            var e = ModEntities.TRIAL_BALLOON.get().create(level);
            if (e == null) continue;
            e.bindTrialOwner(player.getUUID());
            e.moveTo(altar.getX() + i - 2, altar.getY() + 3 + i * 0.3, altar.getZ() - 2, 0, 0);
            level.addFreshEntity(e);
        }
    }

    private static void clearTrialMobs(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        AABB box = player.getBoundingBox().inflate(48);
        for (PracticeScarecrowEntity e : level.getEntitiesOfClass(PracticeScarecrowEntity.class, box)) {
            if (player.getUUID().equals(e.getTrialOwner())) e.discard();
        }
        for (GhostTargetEntity e : level.getEntitiesOfClass(GhostTargetEntity.class, box)) {
            if (player.getUUID().equals(e.getTrialOwner())) e.discard();
        }
        for (TrialBalloonEntity e : level.getEntitiesOfClass(TrialBalloonEntity.class, box)) {
            if (player.getUUID().equals(e.getTrialOwner())) e.discard();
        }
    }

    private static int maskFor(String force) {
        return switch (force) {
            case "shadow" -> MASK_SHADOW;
            case "chaos" -> MASK_CHAOS;
            default -> MASK_RESONANCE;
        };
    }

    private static Component forceLabel(String force) {
        return Component.translatable("gui.cocojenna.force." + force);
    }

    private static void sync(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }
}
