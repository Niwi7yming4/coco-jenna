package com.cocojenna.overworld;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.guide.PenetrationGuideHelper;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.BondSyncCoordinator;
import com.cocojenna.quest.OnboardingQuestManager;
import com.cocojenna.world.portal.CatKingdomPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** 主世界滲透主線：月光腳印 → 記憶碎片 → 貓語 → 修復傳送門 → 踏入貓之國. */
public final class PenetrationQuestManager {

    public static final int STAGE_MOON_PAW = 0;
    public static final int STAGE_MEMORY_SHARDS = 1;
    public static final int STAGE_CAT_LANGUAGE = 2;
    public static final int STAGE_REPAIR_PORTAL = 3;
    public static final int STAGE_FIRST_ENTRY = 4;
    public static final int STAGE_COMPLETE = 5;

    private static final String DUNGEON_CLEARED_TAG = "cocojenna_penetration_dungeon";

    private PenetrationQuestManager() {}

    private static void advanceStage(ServerPlayer player, BondData bond, int stage) {
        bond.setPenetrationQuestStage(stage);
        PenetrationGuideHelper.syncForStage(player, stage);
    }

    public static boolean isDungeonCleared(ServerPlayer player) {
        return player.getPersistentData().getBoolean(DUNGEON_CLEARED_TAG);
    }

    public static void onMoonGuardianDefeated(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getPenetrationQuestStage() < STAGE_CAT_LANGUAGE) return;
        if (isDungeonCleared(player)) return;
        player.getPersistentData().putBoolean(DUNGEON_CLEARED_TAG, true);
        bond.addOverworldInfluence(8);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.dungeon_cleared"), true);
        ItemStack fragment = new ItemStack(ModItems.MAP_FRAGMENT.get());
        if (!player.addItem(fragment)) player.drop(fragment, false);
        BondSyncCoordinator.onHighFrequencyChange(player);
    }

    public static void onMoonPawInteract(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getPenetrationQuestStage() > STAGE_MOON_PAW) return;

        bond.incrementMoonPawTrail();
        bond.addOverworldInfluence(2);
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 6000, 0, false, true, true));
        player.displayClientMessage(Component.translatable("penetration.cocojenna.moon_paw_hint"), true);

        if (bond.getMoonPawTrailCount() >= 3) {
            advanceStage(player, bond, STAGE_MEMORY_SHARDS);
            BlockPos hut = GrayWhiskerHutGenerator.ensureHut(player.serverLevel(), player.blockPosition());
            player.displayClientMessage(Component.translatable("penetration.cocojenna.hut_found",
                    hut.getX(), hut.getZ()), true);
        }
        BondSyncCoordinator.onHighFrequencyChange(player);
    }

    public static void onGrayWhiskerInteract(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setGrayWhiskerMet(true);

        if (player.getMainHandItem().is(ModItems.OUTPOST_BADGE.get())
                || player.getMainHandItem().is(ModItems.GUARDIAN_BADGE.get())) {
            if (!player.getAbilities().instabuild) player.getMainHandItem().shrink(1);
            bond.addGrayWhiskerFavor(15);
            bond.addOverworldInfluence(10);
            player.displayClientMessage(Component.translatable("penetration.cocojenna.outpost_badge_given"), true);
            return;
        }

        if (bond.getPenetrationQuestStage() < STAGE_MEMORY_SHARDS) {
            DialogueManager.play(player, "gray_whisker_intro");
            return;
        }
        if (bond.getPenetrationQuestStage() == STAGE_MEMORY_SHARDS) {
            if (bond.getMemoryShardsTotal() >= 5) {
                bond.spendMemoryShards(5);
                advanceStage(player, bond, STAGE_CAT_LANGUAGE);
                bond.addGrayWhiskerFavor(10);
                DialogueManager.play(player, "gray_whisker_shards_done");
            } else {
                player.displayClientMessage(Component.translatable("penetration.cocojenna.shards_progress",
                        bond.getMemoryShardsTotal()), true);
                DialogueManager.play(player, "gray_whisker_intro");
            }
            return;
        }
        if (bond.getPenetrationQuestStage() == STAGE_CAT_LANGUAGE) {
            if (!isDungeonCleared(player)) {
                player.displayClientMessage(Component.translatable("penetration.cocojenna.need_dungeon"), true);
                return;
            }
            if (bond.getCatLanguageLevel() >= 3 && bond.getCatGraffitiRead() >= 3) {
                advanceStage(player, bond, STAGE_REPAIR_PORTAL);
                bond.addGrayWhiskerFavor(15);
                DialogueManager.play(player, "gray_whisker_portal_quest");
            } else {
                bond.incrementCatLanguageDialogue();
                DialogueManager.play(player, "gray_whisker_cat_language_" + bond.getCatLanguageLevel());
            }
            return;
        }
        if (bond.getPenetrationQuestStage() == STAGE_REPAIR_PORTAL) {
            tryRepairPortal(player, bond);
            return;
        }
        if (bond.getGrayWhiskerFavor() >= 20) {
            tryGrayWhiskerShop(player, bond);
            return;
        }
        DialogueManager.play(player, "gray_whisker_intro");
    }

    private static void tryRepairPortal(ServerPlayer player, BondData bond) {
        int moonstones = countItem(player, ModItems.MOONSTONE.get());
        int mudSamples = countItem(player, ModItems.BLACK_MUD_SAMPLE.get());
        int shards = bond.getMemoryShardsTotal();

        if (moonstones < 10 || mudSamples < 5 || shards < 3) {
            player.displayClientMessage(Component.translatable("penetration.cocojenna.portal_materials",
                    moonstones, mudSamples, shards), true);
            return;
        }

        ServerLevel level = player.serverLevel();
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        BlockPos frame = data.hutPortalFrame();
        if (frame.equals(BlockPos.ZERO)) {
            frame = GrayWhiskerHutGenerator.ensureHut(level, player.blockPosition()).offset(4, 1, 0);
        }

        consume(player, ModItems.MOONSTONE.get(), 10);
        consume(player, ModItems.BLACK_MUD_SAMPLE.get(), 5);
        bond.spendMemoryShards(3);

        CatKingdomPortalShape.tryIgnite(level, frame, player, net.minecraft.world.InteractionHand.MAIN_HAND);
        advanceStage(player, bond, STAGE_FIRST_ENTRY);
        bond.addGrayWhiskerFavor(20);
        bond.addOverworldInfluence(15);
        bond.addCatKingdomInfluence(10);
        player.displayClientMessage(Component.translatable("penetration.cocojenna.portal_repaired"), true);
        BondSyncCoordinator.syncFull(player, bond);
    }

    public static void onEnteredCatKingdom(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getPenetrationQuestStage() == STAGE_FIRST_ENTRY) {
            advanceStage(player, bond, STAGE_COMPLETE);
            bond.addCatKingdomInfluence(20);
            player.displayClientMessage(Component.translatable("penetration.cocojenna.first_entry"), true);
            OnboardingQuestManager.sendHint(player, "penetration.cocojenna.coco_remember");
            com.cocojenna.quest.KingdomTutorialManager.onEnteredKingdom(player);
        }
    }

    public static void onGraffitiRead(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.incrementCatGraffitiRead();
        if (bond.getCatLanguageLevel() >= 1) {
            player.displayClientMessage(Component.translatable(
                    "penetration.cocojenna.graffiti." + (bond.getCatGraffitiRead() % 3)), true);
        } else {
            player.displayClientMessage(Component.translatable("penetration.cocojenna.graffiti_unknown"), true);
        }
    }

    public static void onDialogueChoice(ServerPlayer player, String actionId) {
        BondData bond = ModCapabilities.getOrDefault(player);
        switch (actionId) {
            case "gray_whisker_help" -> {
                if (bond.getPenetrationQuestStage() < STAGE_MEMORY_SHARDS) {
                    advanceStage(player, bond, STAGE_MEMORY_SHARDS);
                }
            }
            case "gray_whisker_history" -> DialogueManager.play(player, "gray_whisker_history");
            case "gray_whisker_leave" -> { }
            default -> { }
        }
    }

    private static void tryGrayWhiskerShop(ServerPlayer player, BondData bond) {
        int discount = com.cocojenna.society.CatSocietyManager.discountPercent(bond);
        int shardNeed = 40 - discount / 2;
        int catnipNeed = 20 - discount / 3;
        if (bond.getGrayWhiskerFavor() >= shardNeed && bond.getMemoryShardsTotal() < 20) {
            dropOrGive(player, new net.minecraft.world.item.ItemStack(ModItems.MEMORY_SHARD.get()));
            bond.addGrayWhiskerFavor(-com.cocojenna.society.CatSocietyManager.applyDiscount(5, bond));
            player.displayClientMessage(Component.translatable("penetration.cocojenna.gray_whisker_shop_shard"), true);
            return;
        }
        if (bond.getGrayWhiskerFavor() >= catnipNeed) {
            int qty = com.cocojenna.society.CatSocietyManager.applyDiscount(3, bond);
            dropOrGive(player, new net.minecraft.world.item.ItemStack(ModItems.CATNIP_ITEM.get(), qty));
            player.displayClientMessage(Component.translatable("penetration.cocojenna.gray_whisker_shop_catnip"), true);
        }
    }

    private static void dropOrGive(ServerPlayer player, net.minecraft.world.item.ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static int countItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (stack.is(item)) n += stack.getCount();
        }
        return n;
    }

    private static void consume(ServerPlayer player, net.minecraft.world.item.Item item, int amount) {
        int left = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
            var stack = player.getInventory().getItem(i);
            if (!stack.is(item)) continue;
            int take = Math.min(left, stack.getCount());
            stack.shrink(take);
            left -= take;
        }
    }
}
