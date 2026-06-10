package com.cocojenna.society;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.FragmentedSequenceCapability;
import com.cocojenna.capability.FragmentedSequenceData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 村民破碎序列生成與 tick 調度. */
@Mod.EventBusSubscriber
public final class FragmentedSequenceManager {

    private FragmentedSequenceManager() {}

    @SubscribeEvent
    public static void onVillagerJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Villager villager)) return;

        villager.getCapability(FragmentedSequenceCapability.FRAGMENTED).ifPresent(data -> {
            if (data.isActive()) return;
            RandomSource random = villager.getRandom();
            int strength = rollStrength(random);
            if (strength <= 0) return;

            data.setActive(true);
            data.setStrength(strength);
            data.setProfession(rollProfession(random, villager));
            FragmentedTradeOffers.applyOffers(villager, data);
        });
    }

    public static void refreshOffers(Villager villager) {
        villager.getCapability(FragmentedSequenceCapability.FRAGMENTED).ifPresent(data -> {
            if (!data.isActive()) return;
            FragmentedTradeOffers.applyOffers(villager, data);
        });
    }

    public static void tick(ServerLevel level) {
        if (level.getGameTime() % 20 != 0) return;
        for (ServerPlayer player : level.players()) {
            for (Villager villager : level.getEntitiesOfClass(
                    Villager.class, player.getBoundingBox().inflate(96))) {
                villager.getCapability(FragmentedSequenceCapability.FRAGMENTED).ifPresent(data -> {
                    if (!data.isActive()) return;
                    FragmentedVillagerBrain.tick(villager, data);
                    if (level.getGameTime() % 200 == 0) {
                        FragmentedTradeOffers.applyOffers(villager, data);
                    }
                });
            }
        }
    }

    public static void onPlayerTrade(ServerPlayer player, Villager villager) {
        villager.getCapability(FragmentedSequenceCapability.FRAGMENTED).ifPresent(data -> {
            if (!data.isActive()) return;
            data.addBondWithPlayer(2);
            if (data.getBondWithPlayer() >= 50) {
                net.minecraft.advancements.Advancement adv = player.server.getAdvancements()
                        .getAdvancement(new ResourceLocation(CocoJennaMod.MOD_ID, "sequence_resonance"));
                if (adv != null) {
                    player.getAdvancements().award(adv, "bond_50");
                }
            }
            FragmentedTradeOffers.applyOffers(villager, data);
        });
    }

    public static void onFirstMeet(ServerPlayer player, Villager villager) {
        villager.getCapability(FragmentedSequenceCapability.FRAGMENTED).ifPresent(data -> {
            if (!data.isActive() || data.hasMetPlayer()) return;
            data.setMetPlayer(true);
            net.minecraft.advancements.Advancement adv = player.server.getAdvancements()
                    .getAdvancement(new ResourceLocation(CocoJennaMod.MOD_ID, "fragmented_first_meet"));
            if (adv != null) {
                player.getAdvancements().award(adv, "meet");
            }
            FragmentedQuestManager.onMeetCarrier(player, villager, data);
        });
    }

    /** 設計書 1.3：70% 無感 / 20% 微感 / 8% 承載 / 2% 共鳴. */
    private static int rollStrength(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 70) return 0;
        if (roll < 90) return 1;
        if (roll < 98) return 2;
        return 3;
    }

    private static FragmentedProfession rollProfession(RandomSource random, Villager villager) {
        int cat = FragmentedVillagerBrain.countNearbyCats(villager, 16);
        int mud = FragmentedVillagerBrain.countNearbyMud(villager, 16);
        if (mud >= 2) return FragmentedProfession.rollCombat(random);
        if (cat >= 2) return FragmentedProfession.rollPlay(random);
        return switch (random.nextInt(3)) {
            case 0 -> FragmentedProfession.rollPlay(random);
            case 1 -> FragmentedProfession.rollCombat(random);
            default -> FragmentedProfession.rollRitual(random);
        };
    }
}
