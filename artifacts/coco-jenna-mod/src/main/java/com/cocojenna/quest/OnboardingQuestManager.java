package com.cocojenna.quest;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.BondSyncCoordinator;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OnboardingHintPacket;
import com.cocojenna.world.portal.CatKingdomPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;

/** 非侵入式入門任務鏈（遇見貓 → 傳送門）. */
public final class OnboardingQuestManager {

    public static final int STEP_COMPLETE = 7;

    private OnboardingQuestManager() {}

    public static void onCatInteraction(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOnboardingQuestStep() >= STEP_COMPLETE) return;
        if (bond.getOnboardingQuestStep() == 0) {
            advance(player, bond, 1, "onboarding.cocojenna.step0");
        }
    }

    public static void onFeed(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOnboardingQuestStep() == 1) {
            advance(player, bond, 2, "onboarding.cocojenna.step1");
        }
    }

    public static void onPet(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOnboardingQuestStep() == 2) {
            advance(player, bond, 3, "onboarding.cocojenna.step2");
        }
    }

    public static void onItemPickup(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty() || player.level().isClientSide) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOnboardingQuestStep() != 3) return;

        if (stack.is(ItemTags.LOGS)) {
            bond.setOnboardingWoodCollected(bond.getOnboardingWoodCollected() + stack.getCount());
        }
        if (stack.is(ItemTags.STONE_TOOL_MATERIALS) || stack.is(Items.COBBLESTONE)) {
            bond.setOnboardingStoneCollected(bond.getOnboardingStoneCollected() + stack.getCount());
        }
        if (bond.getOnboardingWoodCollected() >= 5 && bond.getOnboardingStoneCollected() >= 5) {
            advance(player, bond, 4, "onboarding.cocojenna.step3");
        } else {
            sendHint(player, "onboarding.cocojenna.step3_progress",
                    bond.getOnboardingWoodCollected(), bond.getOnboardingStoneCollected());
        }
        BondSyncCoordinator.onHighFrequencyChange(player);
    }

    public static void onCraft(ServerPlayer player, ItemStack result) {
        if (result.is(ModItems.CAT_BELL_OFFHAND.get())) {
            BondData bond = ModCapabilities.getOrDefault(player);
            if (bond.getOnboardingQuestStep() == 4) {
                advance(player, bond, 5, "onboarding.cocojenna.step4");
            }
        }
    }

    public static void onBellAtAltar(ServerPlayer player, BlockPos altarPos) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOnboardingQuestStep() < 5) {
            player.displayClientMessage(Component.translatable("onboarding.cocojenna.need_bell"), true);
            return;
        }
        var level = player.serverLevel();
        BlockPos framePos = altarPos.north();
        if (!level.getBlockState(framePos).is(ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get())) {
            level.setBlock(framePos, ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.above(), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.above(2), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.above(3), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.east(), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.east().above(), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.east().above(2), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
            level.setBlock(framePos.east().above(3), ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState(), 3);
        }
        CatKingdomPortalShape.tryIgnite(level, framePos, player, net.minecraft.world.InteractionHand.MAIN_HAND);
        advance(player, bond, STEP_COMPLETE, "onboarding.cocojenna.step5");
        player.displayClientMessage(Component.translatable("message.cocojenna.portal_ignited"), true);
    }

    public static void tickHints(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getOnboardingQuestStep() >= STEP_COMPLETE) return;
        if (player.level().getGameTime() % 200 != 0) return;

        if (bond.getCocoEmotionLevel().ordinal() >= BondData.EmotionLevel.ATTACHED.ordinal()
                && bond.getOnboardingQuestStep() >= 2) {
            sendHint(player, "onboarding.cocojenna.attached_resonance");
        }

        String key = switch (bond.getOnboardingQuestStep()) {
            case 0 -> "onboarding.cocojenna.step0";
            case 1 -> "onboarding.cocojenna.step1";
            case 2 -> "onboarding.cocojenna.step2";
            case 3 -> "onboarding.cocojenna.step3";
            case 4 -> "onboarding.cocojenna.step4";
            case 5 -> "onboarding.cocojenna.step5";
            default -> null;
        };
        if (key != null) sendHint(player, key);
    }

    private static void advance(ServerPlayer player, BondData bond, int step, String hintKey) {
        bond.setOnboardingQuestStep(step);
        sendHint(player, hintKey);
        BondSyncCoordinator.onHighFrequencyChange(player);
    }

    public static void sendHint(ServerPlayer player, String key, Object... args) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OnboardingHintPacket(key, args));
    }
}
