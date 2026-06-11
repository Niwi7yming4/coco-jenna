package com.cocojenna.endgame;

import com.cocojenna.combat.CombatVfxHelper;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.entity.ShadowClawEntity;
import com.cocojenna.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 影爪四階段戰鬥與救贖/肅清分支（守護者設計書）.
 */
public final class ShadowClawBattleManager {

    public enum StoryPhase {
        FALLEN_GENERAL,
        REGRETFUL_UNCLE,
        FULL_CORRUPTION,
        TWIN_RESONANCE
    }

    public enum Ending { REDEMPTION, PURGE }

    private ShadowClawBattleManager() {}

    public static void onHurt(ShadowClawEntity boss, Player attacker, float newHealth) {
        if (boss.level().isClientSide) return;
        float ratio = newHealth / boss.getMaxHealth();
        StoryPhase phase = boss.getStoryPhase();

        if (phase == StoryPhase.FALLEN_GENERAL
                && !boss.getPersistentData().getBoolean("cocojenna_phase1_dialogue")) {
            boss.getPersistentData().putBoolean("cocojenna_phase1_dialogue", true);
            if (attacker instanceof ServerPlayer sp) {
                DialogueManager.play(sp, "shadow_claw_phase1");
            }
            spawnPhaseVfx(boss, 1);
        }

        if (phase == StoryPhase.FALLEN_GENERAL && ratio <= 0.6f) {
            boss.setStoryPhase(StoryPhase.REGRETFUL_UNCLE);
            boss.setInvulnerable(true);
            boss.setNoAi(true);
            broadcast(boss, attacker, "boss.cocojenna.shadow_claw.phase2");
            if (attacker instanceof ServerPlayer sp) {
                DialogueManager.play(sp, "shadow_claw_phase2");
                sp.displayClientMessage(Component.translatable("boss.cocojenna.shadow_claw.choice_hint")
                        .withStyle(ChatFormatting.GOLD), false);
            }
            spawnPhaseVfx(boss, 2);
        }
        if (phase == StoryPhase.REGRETFUL_UNCLE && ratio <= 0.35f && attacker instanceof ServerPlayer sp2
                && !boss.getPersistentData().getBoolean("cocojenna_phase3_dialogue")) {
            boss.getPersistentData().putBoolean("cocojenna_phase3_dialogue", true);
            DialogueManager.play(sp2, "shadow_claw_phase3_hint");
        }
    }

    public static void onPlayerInteract(ShadowClawEntity boss, ServerPlayer player) {
        if (boss.getStoryPhase() != StoryPhase.REGRETFUL_UNCLE) return;
        if (player.isShiftKeyDown()) {
            chooseRedemption(boss, player);
        } else {
            choosePurge(boss, player);
        }
    }

    private static void chooseRedemption(ShadowClawEntity boss, ServerPlayer player) {
        var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
        if (bond.getKingdomHappiness() < 60) {
            player.displayClientMessage(Component.translatable("boss.cocojenna.shadow_claw.need_happiness_redemption")
                    .withStyle(ChatFormatting.YELLOW), false);
            return;
        }
        boss.setChosenEnding(Ending.REDEMPTION);
        boss.setStoryPhase(StoryPhase.TWIN_RESONANCE);
        boss.setInvulnerable(false);
        boss.setNoAi(false);
        boss.heal(boss.getMaxHealth() * 0.3f);
        broadcast(boss, player, "boss.cocojenna.shadow_claw.redemption");
        AfterRainManager.setShadowClawEnding(player, "redemption");
        bond.setShadowClawEnding("redemption");
        if (bond.getKingdomHappiness() < 80) bond.addKingdomHappiness(5);
        DialogueManager.play(player, "shadow_claw_redemption");
        spawnPhaseVfx(boss, 3);
        com.cocojenna.society.FragmentedQuestManager.onShadowClawComplete(player);
    }

    private static void choosePurge(ShadowClawEntity boss, ServerPlayer player) {
        var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
        if (bond.getKingdomHappiness() >= 70) {
            player.displayClientMessage(Component.translatable("boss.cocojenna.shadow_claw.too_happy_purge")
                    .withStyle(ChatFormatting.YELLOW), false);
            return;
        }
        boss.setChosenEnding(Ending.PURGE);
        boss.setStoryPhase(StoryPhase.FULL_CORRUPTION);
        boss.setInvulnerable(false);
        boss.setNoAi(false);
        boss.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                .setBaseValue(25.0);
        broadcast(boss, player, "boss.cocojenna.shadow_claw.purge");
        AfterRainManager.setShadowClawEnding(player, "purge");
        bond.setShadowClawEnding("purge");
        DialogueManager.play(player, "shadow_claw_phase4");
        DialogueManager.play(player, "shadow_claw_purge");
        spawnPhaseVfx(boss, 4);
        com.cocojenna.society.FragmentedQuestManager.onShadowClawComplete(player);
    }

    public static void tick(ShadowClawEntity boss) {
        if (boss.level().isClientSide) return;
        Player target = boss.level().getNearestPlayer(boss, 32);
        if (target == null) return;

        if (boss.getStoryPhase() == StoryPhase.FALLEN_GENERAL
                && !boss.getPersistentData().getBoolean("cocojenna_intro_vfx")
                && target.distanceToSqr(boss) < 24 * 24) {
            boss.getPersistentData().putBoolean("cocojenna_intro_vfx", true);
            if (boss.level() instanceof ServerLevel sl) {
                CombatVfxHelper.bossIntro(sl, boss.position(), "shadow_claw");
            }
        }

        if (boss.getStoryPhase() == StoryPhase.FULL_CORRUPTION && target.getHealth() <= 0.5f) {
            tryTwinResonance(boss, target);
        }
        if (boss.getStoryPhase() == StoryPhase.TWIN_RESONANCE
                && target.getHealth() / target.getMaxHealth() < 0.15f) {
            tryTwinResonance(boss, target);
        }
    }

    private static void tryTwinResonance(ShadowClawEntity boss, Player player) {
        if (boss.getPersistentData().getBoolean("cocojenna_twin_resonance")) return;
        AABB box = player.getBoundingBox().inflate(24);
        boolean coco = !boss.level().getEntitiesOfClass(CocoEntity.class, box).isEmpty();
        boolean jenna = !boss.level().getEntitiesOfClass(JennaEntity.class, box).isEmpty();
        if (!coco && !jenna) return;

        boss.getPersistentData().putBoolean("cocojenna_twin_resonance", true);
        player.heal(player.getMaxHealth() * 0.4f);
        if (player instanceof ServerPlayer sp) {
            DialogueManager.play(sp, "shadow_claw_twin_resonance");
            spawnPhaseVfx(boss, 5);
        }
        player.displayClientMessage(Component.translatable("boss.cocojenna.shadow_claw.twin_resonance")
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        boss.level().playSound(null, player.blockPosition(),
                ModSounds.WORLD_FIRST_DAWN.get(), SoundSource.PLAYERS, 0.6f, 1.2f);
    }

    private static void broadcast(ShadowClawEntity boss, Player player, String key) {
        boss.level().playSound(null, boss.blockPosition(),
                ModSounds.WORLD_BLACK_MUD_SPREAD.get(), SoundSource.HOSTILE, 1.5f, 0.7f);
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.DARK_RED), false);
    }

    private static void spawnPhaseVfx(ShadowClawEntity boss, int phase) {
        if (!(boss.level() instanceof ServerLevel level)) return;
        Vec3 pos = boss.position().add(0, boss.getBbHeight() * 0.5, 0);
        CombatVfxHelper.bossPhaseShift(level, pos, phase);
        if (phase == 4) {
            CombatVfxHelper.blackMudExplosion(level, pos);
        } else if (phase == 5) {
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    pos.x, pos.y, pos.z, 40, 0.8, 1.0, 0.8, 0.04);
        }
    }
}
