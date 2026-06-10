package com.cocojenna.endgame.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

/** 滿月祭典分階段事件（雨後設計書 Ch.13.2 完整時間軸）. */
public final class FestivalEventManager {

    /** 0=閒置 1=黃昏布置 2=入夜開幕 3=夜晚跳舞 4=午夜料理 5=深夜煙火 6=黎明許願 7=已結束 */
    public static final int PHASE_IDLE = 0;
    public static final int PHASE_SETUP = 1;
    public static final int PHASE_OPENING = 2;
    public static final int PHASE_DANCE = 3;
    public static final int PHASE_COOKING = 4;
    public static final int PHASE_FIREWORKS = 5;
    public static final int PHASE_WISHING = 6;
    public static final int PHASE_ENDED = 7;

    private static final int TICKS_SETUP = 2400;
    private static final int TICKS_OPENING = 2400;
    private static final int TICKS_DANCE = 3600;
    private static final int TICKS_COOKING = 4800;
    private static final int TICKS_FIREWORKS = 3600;
    private static final int TICKS_WISHING = 2400;

    private FestivalEventManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        int phase = bond.getFestivalPhase();
        if (phase <= PHASE_IDLE || phase >= PHASE_ENDED) return;

        long elapsed = player.level().getGameTime() - bond.getFestivalStartTick();
        int target = phaseForElapsed(elapsed);
        if (target > phase) {
            advanceTo(player, bond, target);
            phase = target;
        }
        if (phase == PHASE_FIREWORKS && player.tickCount % 40 == 0) {
            spawnFireworkParticles((ServerLevel) player.level(), player);
        }
        com.cocojenna.kingdom.PalaceFestivalBridge.tickPlayer(player);
    }

    private static int phaseForElapsed(long elapsed) {
        long t = 0;
        t += TICKS_SETUP;
        if (elapsed < t) return PHASE_SETUP;
        t += TICKS_OPENING;
        if (elapsed < t) return PHASE_OPENING;
        t += TICKS_DANCE;
        if (elapsed < t) return PHASE_DANCE;
        t += TICKS_COOKING;
        if (elapsed < t) return PHASE_COOKING;
        t += TICKS_FIREWORKS;
        if (elapsed < t) return PHASE_FIREWORKS;
        t += TICKS_WISHING;
        if (elapsed < t) return PHASE_WISHING;
        return PHASE_ENDED;
    }

    private static void advanceTo(ServerPlayer player, BondData bond, int phase) {
        bond.setFestivalPhase(phase);
        com.cocojenna.kingdom.PalaceFestivalBridge.onPhaseAdvance(player, bond, phase);
        switch (phase) {
            case PHASE_OPENING -> startOpening(player, bond);
            case PHASE_DANCE -> {
                player.displayClientMessage(
                        Component.translatable("kingdom.cocojenna.festival.dance_start"), true);
                DialogueManager.play(player, "kingdom_festival_dance");
            }
            case PHASE_COOKING -> {
                player.displayClientMessage(
                        Component.translatable("kingdom.cocojenna.festival.cooking_start"), true);
                DialogueManager.play(player, "kingdom_festival_cooking");
            }
            case PHASE_FIREWORKS -> {
                player.displayClientMessage(
                        Component.translatable("kingdom.cocojenna.festival.fireworks"), true);
                player.level().playSound(null, player.blockPosition(),
                        ModSounds.WORLD_FULL_MOON_FESTIVAL.get(), SoundSource.AMBIENT, 1.2f, 1.0f);
            }
            case PHASE_WISHING -> player.displayClientMessage(
                    Component.translatable("kingdom.cocojenna.festival.wish_start"), true);
            case PHASE_ENDED -> endFestival(player, bond);
            default -> { }
        }
        syncBond(player, bond);
    }

    private static void startOpening(ServerPlayer player, BondData bond) {
        player.displayClientMessage(
                Component.translatable("kingdom.cocojenna.festival.opening"), true);
        DialogueManager.play(player, "kingdom_festival_moon_start");
        player.addEffect(new MobEffectInstance(ModEffects.MOON_BLESSING.get(), 4800, 0));
        if (bond.getCocoAwakening() >= 3 || bond.getJennaAwakening() >= 3) {
            player.addEffect(new MobEffectInstance(ModEffects.COCOS_MARK.get(), 3600, 0));
            player.addEffect(new MobEffectInstance(ModEffects.JENNAS_CARE.get(), 3600, 0));
        }
    }

    public static void beginFestival(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        fulfillPendingWish(player, bond);
        bond.setFestivalPhase(PHASE_SETUP);
        bond.setFestivalStartTick(player.level().getGameTime());
        bond.setFestivalContestScore(0);
        bond.setFestivalContestSubmitted(false);
        bond.setFestivalSetupHelped(false);
        bond.setFestivalDanceDone(false);
        bond.setFestivalDanceScore(0);
        bond.setFestivalWish("");
        player.displayClientMessage(
                Component.translatable("kingdom.cocojenna.festival.setup"), true);
        syncBond(player, bond);
    }

    private static void fulfillPendingWish(ServerPlayer player, BondData bond) {
        String wish = bond.getPendingFestivalWish();
        if (wish == null || wish.isEmpty()) return;
        switch (wish) {
            case "happiness" -> bond.addKingdomHappiness(15);
            case "prosperity" -> bond.addKingdomProsperity(15);
            case "favor" -> {
                for (TownNpcProfile p : TownNpcProfile.ALL) {
                    if (bond.isTownNpcRecruited(p.id())) {
                        bond.addTownNpcFavor(p.id(), 5);
                    }
                }
            }
            case "memory" -> {
                bond.addKingdomProsperity(8);
                bond.addKingdomHappiness(8);
            }
            default -> bond.addKingdomHappiness(10);
        }
        player.displayClientMessage(Component.translatable(
                "kingdom.cocojenna.festival.wish_fulfilled",
                Component.translatable("kingdom.cocojenna.festival.wish." + wish)), true);
        bond.setPendingFestivalWish("");
    }

    private static void endFestival(ServerPlayer player, BondData bond) {
        bond.setFestivalPhase(PHASE_IDLE);
        bond.setFestivalPrepDay(7);
        bond.resetFestivalPrepProgress();
        bond.addKingdomHappiness(5);
        if (!bond.getFestivalWish().isEmpty()) {
            bond.setPendingFestivalWish(bond.getFestivalWish());
        }
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.ended"), true);
        syncBond(player, bond);
    }

    public static boolean isCookingContest(BondData bond) {
        return bond.getFestivalPhase() == PHASE_COOKING;
    }

    public static boolean isDancePhase(BondData bond) {
        return bond.getFestivalPhase() == PHASE_DANCE;
    }

    public static boolean isSetupPhase(BondData bond) {
        return bond.getFestivalPhase() == PHASE_SETUP;
    }

    public static boolean isWishingPhase(BondData bond) {
        return bond.getFestivalPhase() == PHASE_WISHING;
    }

    public static void onHelpSetup(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!isSetupPhase(bond)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.wrong_phase"), true);
            return;
        }
        if (bond.isFestivalSetupHelped()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.setup_already"), true);
            return;
        }
        bond.setFestivalSetupHelped(true);
        bond.addKingdomReputation(5);
        bond.addFestivalPrepProgress(3);
        player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.setup_help"), true);
        syncBond(player, bond);
    }

    public static void onDance(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!isDancePhase(bond)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.wrong_phase"), true);
            return;
        }
        if (bond.isFestivalDanceDone()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.dance_already"), true);
            return;
        }
        int score = 45 + player.getRandom().nextInt(35) + bond.getKingdomHappiness() / 8;
        score = Math.min(100, score);
        bond.setFestivalDanceDone(true);
        bond.setFestivalDanceScore(score);
        bond.addKingdomHappiness(3);
        bond.addKingdomReputation(score / 20);
        player.displayClientMessage(
                Component.translatable("kingdom.cocojenna.festival.dance_score", score), true);
        if (score >= 70) {
            DialogueManager.play(player, "kingdom_festival_dance_win");
        }
        syncBond(player, bond);
    }

    public static void onSubmitWish(ServerPlayer player, String wishKey) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!isWishingPhase(bond)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.wrong_phase"), true);
            return;
        }
        if (!bond.getFestivalWish().isEmpty()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.wish_already"), true);
            return;
        }
        if (!consumeDandelion(player)) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.festival.wish_need_fluff"), true);
            return;
        }
        String key = normalizeWish(wishKey);
        bond.setFestivalWish(key);
        bond.addKingdomHappiness(4);
        player.displayClientMessage(Component.translatable(
                "kingdom.cocojenna.festival.wish_sent",
                Component.translatable("kingdom.cocojenna.festival.wish." + key))
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        syncBond(player, bond);
    }

    private static String normalizeWish(String wishKey) {
        if (wishKey == null) return "peace";
        return switch (wishKey) {
            case "happiness", "prosperity", "favor", "memory", "peace" -> wishKey;
            default -> "peace";
        };
    }

    private static boolean consumeDandelion(ServerPlayer player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.is(ModItems.DANDELION_FLUFF.get())) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static void onContestSubmit(ServerPlayer player, int score) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isFestivalContestSubmitted()) {
            player.displayClientMessage(Component.translatable("kingdom.cocojenna.contest.already"), true);
            return;
        }
        bond.setFestivalContestSubmitted(true);
        bond.setFestivalContestScore(score);
        bond.addKingdomHappiness(Math.min(15, score / 5));
        bond.addKingdomReputation(score / 10);
        if (score >= 80) {
            ItemStack prize = new ItemStack(ModItems.GOLDEN_SPOON_TROPHY.get());
            if (!player.addItem(prize)) player.drop(prize, false);
            for (TownNpcProfile p : TownNpcProfile.ALL) {
                if (bond.isTownNpcRecruited(p.id())) {
                    bond.addTownNpcFavor(p.id(), 10);
                }
            }
            player.displayClientMessage(
                    Component.translatable("kingdom.cocojenna.contest.gold_spoon"), true);
        }
        player.displayClientMessage(
                Component.translatable("kingdom.cocojenna.contest.score", score), true);
        syncBond(player, bond);
    }

    public static void spawnFireworkParticles(ServerLevel level, ServerPlayer player) {
        var rng = player.getRandom();
        double x = player.getX() + (rng.nextDouble() - 0.5) * 24;
        double y = player.getY() + 8 + rng.nextInt(12);
        double z = player.getZ() + (rng.nextDouble() - 0.5) * 24;
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 6, 0.4, 0.4, 0.4, 0.02);
        level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 2, 0.2, 0.2, 0.2, 0.01);
    }

    private static void syncBond(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }
}
