package com.cocojenna.society;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.kingdom.TownNpcProfile;
import com.cocojenna.init.ModItems;
import com.cocojenna.overworld.OverworldCatNpcEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** 貓社會好感、商店折扣、羈絆與招募（MCA 風格深化）. */
public final class CatSocietyManager {

    public static final int TIER_STRANGER = 0;
    public static final int TIER_ACQUAINTANCE = 10;
    public static final int TIER_FRIEND = 30;
    public static final int TIER_CLOSE = 50;
    public static final int TIER_FAMILY = 75;
    public static final int TIER_SOUL = 95;

    private CatSocietyManager() {}

    public static void onNpcInteract(ServerPlayer player, OverworldCatNpcEntity npc) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int favor = npc.getNpcFavor();
        String key = npcKey(npc);
        bond.setOverworldNpcFavor(key, favor);
        bond.setCatSocietyPeakFavor(Math.max(bond.getCatSocietyPeakFavor(), favor));
        bond.addCatSocietyInteractions(1);

        int tier = tierFor(favor);
        if (tier >= TIER_ACQUAINTANCE && tier < TIER_FRIEND) {
            player.displayClientMessage(Component.translatable("society.cocojenna.tier.acquaintance"), true);
        } else if (tier >= TIER_FRIEND && tier < TIER_FAMILY) {
            player.displayClientMessage(Component.translatable("society.cocojenna.tier.friend"), true);
        } else if (tier >= TIER_FAMILY && tier < TIER_SOUL) {
            player.displayClientMessage(Component.translatable("society.cocojenna.tier.family"), true);
        } else if (tier >= TIER_SOUL) {
            player.displayClientMessage(Component.translatable("society.cocojenna.tier.soul"), true);
            trySoulCompanion(player, bond, npc);
        }

        tryPassiveRecruit(player, bond, npc);
    }

    public static void onGift(ServerPlayer player, OverworldCatNpcEntity npc, ItemStack gift) {
        int bonus = 2;
        if (gift.is(Items.COD) || gift.is(Items.SALMON)) bonus = 3;
        if (gift.is(ModItems.CATNIP_ITEM.get())) bonus = 4;
        npc.addNpcFavor(bonus);
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.setOverworldNpcFavor(npcKey(npc), npc.getNpcFavor());
        bond.addCatSocietyInteractions(1);
        if (gift.is(ModItems.CATNIP_ITEM.get())) {
            onCatnipTraded(player, bond, gift.getCount());
        }
    }

    /** 主世界貓薄荷經濟 — 累積交易影響滲透與折扣. */
    public static void onCatnipTraded(ServerPlayer player, BondData bond, int amount) {
        bond.addCatnipTraded(amount);
        bond.addOverworldInfluence(amount);
        if (bond.getCatnipTradedTotal() % 10 == 0) {
            player.displayClientMessage(Component.translatable("society.cocojenna.catnip_economy",
                    bond.getCatnipTradedTotal()), true);
        }
    }

    public static void onTownNpcGift(ServerPlayer player, String npcId, ItemStack gift) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (gift.is(ModItems.CATNIP_ITEM.get())) {
            bond.addTownNpcFavor(npcId, 2);
        }
        updateTownRomance(player, bond, npcId);
    }

    public static void onRitualObserved(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addCatSocietyInteractions(2);
        bond.addOverworldInfluence(1);
    }

    public static int discountPercent(BondData bond) {
        int peak = Math.max(bond.getCatSocietyPeakFavor(), maxTownFavor(bond));
        if (peak >= TIER_SOUL) return 25;
        if (peak >= TIER_FAMILY) return 20;
        if (peak >= TIER_CLOSE) return 15;
        if (peak >= TIER_FRIEND) return 10;
        if (peak >= TIER_ACQUAINTANCE) return 5;
        return 0;
    }

    public static int applyDiscount(int baseCost, BondData bond) {
        int pct = discountPercent(bond);
        return Math.max(1, baseCost * (100 - pct) / 100);
    }

    public static void tryStrayRecruit(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getKingdomHappiness() < 60) return;
        if (bond.getVillagePopulation() >= bond.getVillageHousingCapacity()) return;
        if (player.getRandom().nextFloat() > 0.15f) return;

        bond.setVillagePopulation(bond.getVillagePopulation() + 1);
        bond.addKingdomHappiness(2);
        bond.recruitTownNpc(TownNpcProfile.ALL[player.getRandom().nextInt(TownNpcProfile.ALL.length)].id());
        player.displayClientMessage(Component.translatable("society.cocojenna.stray_recruited"), true);
    }

    private static void tryPassiveRecruit(ServerPlayer player, BondData bond, OverworldCatNpcEntity npc) {
        if (npc.getNpcFavor() < TIER_CLOSE) return;
        if (bond.getVillagePopulation() >= bond.getVillageHousingCapacity()) return;
        if (player.getRandom().nextFloat() > 0.08f) return;
        if (npc.getRole() != OverworldCatNpcEntity.Role.LOST_KITTEN
                && npc.getRole() != OverworldCatNpcEntity.Role.POET) return;

        bond.setVillagePopulation(bond.getVillagePopulation() + 1);
        bond.addOverworldInfluence(2);
        player.displayClientMessage(Component.translatable("society.cocojenna.village_welcomes"), true);
        npc.discard();
    }

    private static void trySoulCompanion(ServerPlayer player, BondData bond, OverworldCatNpcEntity npc) {
        if (bond.hasOverworldSoulCompanion(npcKey(npc))) return;
        bond.markOverworldSoulCompanion(npcKey(npc));
        player.displayClientMessage(Component.translatable("society.cocojenna.soul_bond",
                npc.getRole().name().toLowerCase()), true);
    }

    public static void tickRomanceAura(ServerPlayer player) {
        if (player.tickCount % 60 != 0) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        int maxStage = 0;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            maxStage = Math.max(maxStage, bond.getTownNpcRomanceStage(p.id()));
        }
        if (maxStage >= 2) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION, 100, 0, false, true, true));
        }
        if (maxStage >= 3) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.LUCK, 100, 0, false, true, true));
        }
    }

    private static void updateTownRomance(ServerPlayer player, BondData bond, String npcId) {
        TownNpcProfile profile = TownNpcProfile.byId(npcId);
        String name = profile != null ? profile.nameZh() : npcId;
        int favor = bond.getTownNpcFavor(npcId);
        if (favor >= 85 && bond.getTownNpcRomanceStage(npcId) < 3) {
            bond.setTownNpcRomanceStage(npcId, 3);
            player.displayClientMessage(Component.translatable("society.cocojenna.romance.confidant", name), true);
        } else if (favor >= 70 && bond.getTownNpcRomanceStage(npcId) < 2) {
            bond.setTownNpcRomanceStage(npcId, 2);
            player.displayClientMessage(Component.translatable("society.cocojenna.romance.close", name), true);
        } else if (favor >= 50 && bond.getTownNpcRomanceStage(npcId) < 1) {
            bond.setTownNpcRomanceStage(npcId, 1);
            player.displayClientMessage(Component.translatable("society.cocojenna.romance.friend", name), true);
        }
    }

    private static int maxTownFavor(BondData bond) {
        int max = 0;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            max = Math.max(max, bond.getTownNpcFavor(p.id()));
        }
        return max;
    }

    private static int tierFor(int favor) {
        if (favor >= TIER_SOUL) return TIER_SOUL;
        if (favor >= TIER_FAMILY) return TIER_FAMILY;
        if (favor >= TIER_CLOSE) return TIER_CLOSE;
        if (favor >= TIER_FRIEND) return TIER_FRIEND;
        if (favor >= TIER_ACQUAINTANCE) return TIER_ACQUAINTANCE;
        return TIER_STRANGER;
    }

    private static String npcKey(OverworldCatNpcEntity npc) {
        return npc.getRole().name() + ":" + npc.getUUID().toString().substring(0, 8);
    }
}
