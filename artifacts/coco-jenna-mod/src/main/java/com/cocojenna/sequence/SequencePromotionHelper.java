package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenPromotionPacket;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * 序列晉升（設計書 1.2 — 等級門檻 + 記憶碎片 + 三選一卡牌）.
 *
 * <p>整合新晉升儀式系統（設計書第二章）：
 * <ul>
 *   <li>tryPromoteSimplified() — 跳過儀式動畫，直接卡牌選擇（設計書 2.4）</li>
 *   <li>tryPromoteCeremony() — 完整五階段沉浸式儀式（設計書 2.1）</li>
 * </ul>
 */
public final class SequencePromotionHelper {

    private SequencePromotionHelper() {}

    public static int requiredLevel(int currentTier) {
        return switch (currentTier) {
            case 9 -> 10;
            case 8 -> 20;
            case 7 -> 30;
            case 6 -> 40;
            case 5 -> 50;
            case 4 -> 60;
            case 3 -> 70;
            case 2 -> 80;
            default -> Integer.MAX_VALUE;
        };
    }

    public static int requiredShards(int currentTier) {
        return switch (currentTier) {
            case 9 -> 3;
            case 8 -> 8;
            case 7 -> 15;
            case 6 -> 22;
            case 5 -> 30;
            case 4 -> 40;
            case 3 -> 50;
            case 2 -> 65;
            default -> Integer.MAX_VALUE;
        };
    }

    /**
     * 根據玩家設定，決定使用簡化晉升或完整儀式
     */
    public static void tryPromote(ServerPlayer player, BondData bond) {
        if (bond.getFelineTier() <= 1) return;
        if (bond.getPendingPromotionTier() > 0) return;
        if (!MoonCrossroadsManager.hasChosenForce(bond)) {
            player.displayClientMessage(
                Component.translatable("force.cocojenna.need_choose"), true);
            return;
        }
        int tier = bond.getFelineTier();
        if (player.experienceLevel < requiredLevel(tier)) {
            player.displayClientMessage(
                Component.translatable("promotion.cocojenna.need_level", requiredLevel(tier)), true);
            return;
        }
        if (bond.getMemoryShardsTotal() < requiredShards(tier)) {
            player.displayClientMessage(
                Component.translatable("promotion.cocojenna.need_shards", requiredShards(tier)), true);
            return;
        }
        if (!awakeningTrialMet(bond, tier)) {
            player.displayClientMessage(Component.translatable("promotion.cocojenna.need_trial"), true);
            return;
        }

        if (bond.isSimplifiedCeremony()) {
            // 設計書 2.4：跳過儀式動畫，直接卡牌選擇
            tryPromoteSimplified(player, bond);
        } else {
            // 設計書 2.1：完整五階段沉浸式儀式
            com.cocojenna.sequence.PromotionCeremonyHandler.startSummoning(player, bond);
        }
    }

    /**
     * 簡化晉升 — 跳過動畫直接進入卡牌選擇（設計書 2.4）
     */
    private static void tryPromoteSimplified(ServerPlayer player, BondData bond) {
        int tier = bond.getFelineTier();
        bond.setPendingPromotionTier(tier);

        // 直接消耗材料（無需祭壇）
        int needLevel = requiredLevel(tier);
        int needShards = requiredShards(tier);
        player.giveExperienceLevels(-needLevel);
        bond.spendMemoryShards(needShards);

        var cards = PromotionCardCatalog.pickThree(bond.getFelineForce(), tier);
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new OpenPromotionPacket(tier, bond.getFelineForce(), cards));
    }

    /**
     * 簡化晉升的卡牌確認
     */
    public static void confirmPromotion(ServerPlayer player, BondData bond, int cardIndex) {
        int pending = bond.getPendingPromotionTier();
        if (pending <= 0 || cardIndex < 0 || cardIndex > 2) return;

        // 如果已經是簡化模式，不需要再次檢查材料
        if (bond.isSimplifiedCeremony()) {
            confirmSimplified(player, bond, pending, cardIndex);
            return;
        }

        // 否則走完整儀式路徑
        com.cocojenna.sequence.PromotionCeremonyHandler.confirmCardSelection(player, bond, cardIndex);
    }

    private static void confirmSimplified(ServerPlayer player, BondData bond, int pending, int cardIndex) {
        var cards = PromotionCardCatalog.pickThree(bond.getFelineForce(), pending);
        String cardId = cards.get(cardIndex);

        int next = pending - 1;
        bond.setFelineTier(next);
        bond.addPromotionCard(cardId);
        bond.setPendingPromotionTier(0);

        // 簡化膜式也應用印記
        int markLevel = com.cocojenna.sequence.PromotionCeremonyHandler.getMarkLevelStatic(next);
        bond.setMarkLevel(markLevel);
        bond.setMarkForce(bond.getFelineForce());

        player.displayClientMessage(Component.translatable(
                "sequence.cocojenna.promoted", next), true);
        player.displayClientMessage(Component.translatable(
                "promotion.cocojenna.selected", PromotionCardCatalog.displayName(cardId)), true);
        com.cocojenna.weapon.WeaponUnsealManager.onSequencePromotion(player);
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    /** 序列 3→2、2→1 需完成覺醒試煉（設計書 1.3）. */
    public static boolean awakeningTrialMet(BondData bond, int currentTier) {
        if (currentTier > 3) return true;
        int need = currentTier == 3 ? 2 : (currentTier == 2 ? 3 : 4);
        return bond.getAwakeningTrialTier() >= need;
    }

    public static float cardDamageBonus(BondData bond) {
        float fromCards = 0f;
        for (String id : bond.getOwnedPromotionCards()) {
            fromCards += PromotionCardCatalog.cardBonus(id) * 0.5f;
        }
        return fromCards + bond.getPromotionCardBonus();
    }
}
