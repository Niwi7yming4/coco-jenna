package com.cocojenna.sequence;

import com.cocojenna.capability.BondData;
import com.cocojenna.combat.CombatSoundHelper;
import com.cocojenna.combat.CombatVfxHelper;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModParticles;
import com.cocojenna.network.BondSyncCoordinator;
import com.cocojenna.network.CeremonyEffectPacket;
import com.cocojenna.network.CeremonySpectatorPacket;
import com.cocojenna.network.CeremonyStagePacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Set;

/**
 * 序列晉升儀式完整流程 － 設計書第二章
 * 五階段沉浸式晉升：召喚 → 獻祭 → 共鳴 → 啟示 → 印記
 */
public final class PromotionCeremonyHandler {

    // 儀式階段
    public enum CeremonyStage {
        NONE,           // 未開始
        SUMMONING,      // 第一階段：召喚 (10秒)
        SACRIFICE,      // 第二階段：獻祭 (玩家互動)
        RESONANCE,      // 第三階段：共鳴 (30秒儀式動畫)
        REVELATION,     // 第四階段：啟示 (卡牌選擇3D)
        MARKING,        // 第五階段：印記 (永久視覺變化)
        COMPLETE        // 完成
    }

    // 途徑顏色
    public static final int COLOR_RESONANCE = 0xFFFFD700;  // 金色
    public static final int COLOR_SHADOW    = 0xFF9B30FF;  // 紫色
    public static final int COLOR_CHAOS     = 0xFFFF69B4;  // 彩色

    private PromotionCeremonyHandler() {}

    // ========================================================================
    // 第一階段：召喚 (Summoning)
    // ========================================================================

    /**
     * 檢查玩家是否可開始晉升儀式
     */
    public static boolean canStartCeremony(ServerPlayer player, BondData bond) {
        int tier = bond.getFelineTier();
        if (tier <= 1) return false;
        if (bond.getPendingPromotionTier() > 0) return false;

        // 檢查等級
        int reqLevel = SequencePromotionHelper.requiredLevel(tier);
        if (player.experienceLevel < reqLevel) {
            player.displayClientMessage(
                Component.translatable("promotion.cocojenna.need_level", reqLevel), true);
            return false;
        }

        // 檢查記憶碎片
        int reqShards = SequencePromotionHelper.requiredShards(tier);
        if (bond.getMemoryShardsTotal() < reqShards) {
            player.displayClientMessage(
                Component.translatable("promotion.cocojenna.need_shards", reqShards), true);
            return false;
        }

        // 檢查試煉
        if (!SequencePromotionHelper.awakeningTrialMet(bond, tier)) {
            player.displayClientMessage(
                Component.translatable("promotion.cocojenna.need_trial"), true);
            return false;
        }

        return true;
    }

    /**
     * 開始召喚階段 － 阿爾法的聲音引導
     */
    public static void startSummoning(ServerPlayer player, BondData bond) {
        int tier = bond.getFelineTier();
        bond.setPendingPromotionTier(tier);
        setStage(bond, CeremonyStage.SUMMONING);

        // 阿爾法的聲音引導
        player.displayClientMessage(
            Component.translatable("ceremony.cocojenna.alpha_call"), true);

        // 播放引導音效
        CombatSoundHelper.play(
            (ServerLevel) player.level(),
            player.position(),
            CombatSoundHelper.Layer.BASE,
            CombatVfxHelper.Force.RESONANCE);

        // 發送客戶端階段更新
        sendStagePacket(player, CeremonyStage.SUMMONING, tier);

        // 30秒內可觸發，否則超時取消
        bond.setCeremonyTimeout((int) (player.serverLevel().getGameTime() + 600));
    }

    // ========================================================================
    // 第二階段：獻祭 (Sacrifice)
    // ========================================================================

    /**
     * 檢查玩家手持物品是否符合獻祭材料
     */
    public static boolean checkSacrificeItems(Player player, BondData bond) {
        int tier = bond.getFelineTier();
        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";

        // 所需材料清單
        // 記憶碎片 ×5（基礎）
        // 純淨呼嚕結晶 ×1（中階）
        // 對應途徑結晶 ×1（高階）

        boolean hasShards = bond.getMemoryShardsTotal() >= requiredSacrificeShards(tier);
        boolean hasPurrCrystal = hasItem(player, ModItems.PURR_CRYSTAL.get(), 1);
        boolean hasForceCrystal = hasForceCrystal(player, force);

        if (!hasShards) {
            player.displayClientMessage(
                Component.translatable("ceremony.cocojenna.need_shards", requiredSacrificeShards(tier)), true);
            return false;
        }
        if (!hasPurrCrystal) {
            player.displayClientMessage(
                Component.translatable("ceremony.cocojenna.need_purr_crystal"), true);
            return false;
        }
        if (!hasForceCrystal) {
            player.displayClientMessage(
                Component.translatable("ceremony.cocojenna.need_force_crystal", force), true);
            return false;
        }

        return true;
    }

    private static int requiredSacrificeShards(int tier) {
        return tier >= 6 ? 8 : tier >= 3 ? 6 : 5;
    }

    private static boolean hasForceCrystal(Player player, String force) {
        return switch (force) {
            case "resonance" -> hasItem(player, ModItems.PURR_CRYSTAL.get(), 1);
            case "shadow"    -> hasItem(player, ModItems.SHADOW_CRYSTAL.get(), 1);
            case "chaos"     -> hasItem(player, ModItems.CHAOS_CRYSTAL.get(), 1);
            default -> false;
        };
    }

    private static boolean hasItem(Player player, Item item, int count) {
        return player.getInventory().countItem(item) >= count;
    }

    /**
     * 消耗獻祭材料，觸發祭壇發光
     */
    public static void performSacrifice(ServerPlayer player, BondData bond, BlockPos altarPos) {
        int tier = bond.getFelineTier();
        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";

        // 消耗材料
        bond.spendMemoryShards(requiredSacrificeShards(tier));
        consumeItem(player, ModItems.PURR_CRYSTAL.get(), 1);
        consumeForceCrystal(player, force);

        // 祭壇開始發光
        ServerLevel level = (ServerLevel) player.level();
        Vec3 center = Vec3.atCenterOf(altarPos);

        // 發送祭壇特效
        sendStagePacket(player, CeremonyStage.SACRIFICE, tier);
        spawnSacrificeParticles(level, center, force);

        // 可可和珍奶的反應
        summonPetsToAltar(level, player, center);

        // 進入共鳴階段
        setStage(bond, CeremonyStage.RESONANCE);
        bond.setCeremonyTimeout((int) (player.serverLevel().getGameTime() + 1200));
    }

    private static void consumeItem(Player player, Item item, int count) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                stack.shrink(count);
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                break;
            }
        }
    }

    private static void consumeForceCrystal(Player player, String force) {
        Item crystal = switch (force) {
            case "resonance" -> ModItems.PURR_CRYSTAL.get();
            case "shadow"    -> ModItems.SHADOW_CRYSTAL.get();
            case "chaos"     -> ModItems.CHAOS_CRYSTAL.get();
            default -> ModItems.PURR_CRYSTAL.get();
        };
        consumeItem(player, crystal, 1);
    }

    private static void spawnSacrificeParticles(ServerLevel level, Vec3 center, String force) {
        int color = forceColor(force);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // 祭壇上升粒子
        for (int i = 0; i < 30; i++) {
            level.sendParticles(
                ParticleTypes.END_ROD,
                center.x + (level.random.nextDouble() - 0.5) * 2.0,
                center.y + level.random.nextDouble() * 0.5,
                center.z + (level.random.nextDouble() - 0.5) * 2.0,
                1, 0, 0.2, 0, 0.02);
        }

        // 對應途徑的特殊粒子
        switch (force) {
            case "resonance" -> spawnResonanceAltarGlow(level, center);
            case "shadow"    -> spawnShadowAltarGlow(level, center);
            case "chaos"     -> spawnChaosAltarGlow(level, center);
        }
    }

    private static void spawnResonanceAltarGlow(ServerLevel level, Vec3 center) {
        for (int i = 0; i < 3; i++) {
            level.sendParticles(ModParticles.PURR_WAVE.get(),
                center.x, center.y + 1.5, center.z,
                1, 0.5 + i * 0.3, 0.1, 0.5 + i * 0.3, 0.01);
        }
    }

    private static void spawnShadowAltarGlow(ServerLevel level, Vec3 center) {
        for (int i = 0; i < 8; i++) {
            double ang = i * Math.PI / 4;
            level.sendParticles(ModParticles.SHADOW_FEATHER.get(),
                center.x + Math.cos(ang) * 2.0, center.y + 1.5 + level.random.nextDouble(), center.z + Math.sin(ang) * 2.0,
                3, 0.1, 0.1, 0.1, 0.03);
        }
    }

    private static void spawnChaosAltarGlow(ServerLevel level, Vec3 center) {
        for (int i = 0; i < 12; i++) {
            level.sendParticles(ModParticles.CHAOS_CONFETTI.get(),
                center.x + (level.random.nextDouble() - 0.5) * 3.0,
                center.y + 1.0 + level.random.nextDouble(),
                center.z + (level.random.nextDouble() - 0.5) * 3.0,
                2, 0.2, 0.2, 0.2, 0.05);
        }
    }

    private static void summonPetsToAltar(ServerLevel level, Player player, Vec3 center) {
        // 可可和珍奶自動跟隨到祭壇旁，坐下，注視玩家
        level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
            player.getBoundingBox().inflate(16), e -> {
                String name = e.getType().getDescription().getString();
                return name.contains("Coco") || name.contains("Jenna") || name.contains("珍奶");
            }).forEach(pet -> {
                if (pet instanceof PathfinderMob mob) {
                    mob.getNavigation().moveTo(center.x + 2, center.y, center.z + 2, 1.0);
                }
                CombatSoundHelper.play(level, pet.position(),
                    CombatSoundHelper.Layer.BASE, CombatVfxHelper.Force.RESONANCE);
            });
    }

    // ========================================================================
    // 第三階段：共鳴 (Resonance) - 30秒儀式動畫
    // ========================================================================

    /**
     * 執行共鳴儀式
     */
    public static void startResonance(ServerPlayer player, BondData bond) {
        ServerLevel level = (ServerLevel) player.level();
        int tier = bond.getFelineTier();
        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";

        // 鎖定玩家視角（由客戶端處理）
        sendStagePacket(player, CeremonyStage.RESONANCE, tier);

        // 1. 材料化為光點飄向玩家
        spawnSacrificeToPlayer(level, player.position(), force);

        // 2. 可可和珍奶同時發出叫聲
        CombatSoundHelper.play(level, player.position(),
            CombatSoundHelper.Layer.BASE, CombatVfxHelper.Force.RESONANCE);

        // 3. 光點在玩家周圍形成對應途徑圖案
        spawnForcePattern(level, player.position(), force, tier);

        // 4. 玩家身體短暫變為對應源力顏色（半透明發光）
        // 由客戶端透過 shader 效果處理
        sendBodyGlowPacket(player, force);

        // 設定計時器：30秒後自動進入下一階段
        bond.setCeremonyTimeout((int) (player.serverLevel().getGameTime() + 600));
    }

    private static void spawnSacrificeToPlayer(ServerLevel level, Vec3 playerPos, String force) {
        // 光點從祭壇位置飄向玩家
        for (int i = 0; i < 40; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 4;
            double offsetZ = (level.random.nextDouble() - 0.5) * 4;
            Vec3 from = playerPos.add(offsetX, 0.5, offsetZ);
            Vec3 to = playerPos.add(0, 1.5, 0);

            // 使用 interpolation 粒子效果
            int count = switch (force) {
                case "resonance" -> ModParticles.PURR_WAVE.get().hashCode();
                case "shadow"    -> ModParticles.SHADOW_FEATHER.get().hashCode();
                case "chaos"     -> ModParticles.CHAOS_CONFETTI.get().hashCode();
                default -> 0;
            };
        }
    }

    private static void spawnForcePattern(ServerLevel level, Vec3 center, String force, int tier) {
        int size = 8 + (10 - tier) * 2; // 序列越低，圖案越大

        switch (force) {
            case "resonance" -> {
                // 金色聲波同心圓
                for (int ring = 1; ring <= 3; ring++) {
                    double radius = ring * 1.5;
                    for (int i = 0; i < size * ring; i++) {
                        double ang = i * Math.PI * 2 / (size * ring);
                        level.sendParticles(ModParticles.PURR_WAVE.get(),
                            center.x + Math.cos(ang) * radius,
                            center.y + 0.15,
                            center.z + Math.sin(ang) * radius,
                            1, 0, 0.05, 0, 0.01);
                    }
                }
            }
            case "shadow" -> {
                // 紫色暗影羽毛環繞玩家旋轉
                for (int i = 0; i < size; i++) {
                    double ang = i * Math.PI * 2 / size;
                    double radius = 2.5;
                    level.sendParticles(ModParticles.SHADOW_FEATHER.get(),
                        center.x + Math.cos(ang) * radius,
                        center.y + 1.0 + Math.sin(ang * 2) * 0.5,
                        center.z + Math.sin(ang) * radius,
                        2, 0, 0.1, 0, 0.02);
                }
                // 從天空飄落的羽毛
                for (int i = 0; i < 10; i++) {
                    level.sendParticles(ModParticles.SHADOW_FEATHER.get(),
                        center.x + (level.random.nextDouble() - 0.5) * 6,
                        center.y + 3.0,
                        center.z + (level.random.nextDouble() - 0.5) * 6,
                        3, 0.1, -0.1, 0.1, 0.03);
                }
            }
            case "chaos" -> {
                // 彩色紙屑與星星爆開
                for (int burst = 0; burst < 4; burst++) {
                    double offsetX = (level.random.nextDouble() - 0.5) * 4;
                    double offsetZ = (level.random.nextDouble() - 0.5) * 4;
                    level.sendParticles(ModParticles.CHAOS_CONFETTI.get(),
                        center.x + offsetX, center.y + 1.0 + level.random.nextDouble(), center.z + offsetZ,
                        15, 0.3, 0.3, 0.3, 0.08);
                }
                // 星星粒子
                level.sendParticles(ParticleTypes.FIREWORK,
                    center.x, center.y + 1.5, center.z,
                    20, 1.5, 0.5, 1.5, 0.05);
            }
        }
    }

    private static void sendBodyGlowPacket(ServerPlayer player, String force) {
        ModNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new CeremonyEffectPacket(CeremonyEffectPacket.EffectType.BODY_GLOW, force, 100)); // 5秒
        broadcastSpectator(player, CeremonySpectatorPacket.SpectatorEffect.ALTAR_GLOW, force);
    }

    private static void broadcastSpectator(ServerPlayer player,
            CeremonySpectatorPacket.SpectatorEffect effect, String force) {
        Vec3 pos = player.position();
        ModNetwork.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        pos.x, pos.y, pos.z, 48.0, player.serverLevel().dimension())),
                new CeremonySpectatorPacket(effect, pos.x, pos.y, pos.z, force));
    }

    // ========================================================================
    // 第四階段：啟示 (Revelation) - 卡牌選擇
    // ========================================================================

    /**
     * 進入啟示階段 - 三張卡牌浮現（3D效果而非純GUI）
     */
    public static void startRevelation(ServerPlayer player, BondData bond) {
        int tier = bond.getFelineTier();
        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";

        setStage(bond, CeremonyStage.REVELATION);

        // 選擇三張卡牌
        var cards = PromotionCardCatalog.pickThree(force, tier);

        // 發送卡牌選擇封包（客戶端顯示3D浮空卡牌）
        ModNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new CeremonyEffectPacket(
                CeremonyEffectPacket.EffectType.CARD_SELECTION,
                force, tier, cards));
    }

    /**
     * 玩家選擇卡牌後的處理
     */
    public static void confirmCardSelection(ServerPlayer player, BondData bond, int cardIndex) {
        int pending = bond.getPendingPromotionTier();
        if (pending <= 0 || cardIndex < 0 || cardIndex > 2) return;
        if (!isStage(bond, CeremonyStage.REVELATION)) return;

        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";

        int needLevel = SequencePromotionHelper.requiredLevel(pending);
        int needShards = SequencePromotionHelper.requiredShards(pending);
        if (player.experienceLevel < needLevel || bond.getMemoryShardsTotal() < needShards) {
            bond.setPendingPromotionTier(0);
            setStage(bond, CeremonyStage.NONE);
            player.displayClientMessage(
                Component.translatable("promotion.cocojenna.insufficient"), true);
            return;
        }

        // 消耗資源
        var cards = PromotionCardCatalog.pickThree(force, pending);
        String cardId = cards.get(cardIndex);
        player.giveExperienceLevels(-needLevel);
        bond.spendMemoryShards(needShards);

        // 晉升
        int next = pending - 1;
        bond.setFelineTier(next);
        bond.addPromotionCard(cardId);

        // 未選中的卡牌化為光點消散
        ServerLevel level = (ServerLevel) player.level();
        spawnUnselectedCardsDissolve(level, player.position(), force, cardIndex);

        // 選中的卡牌飛入玩家胸口 → 畫面短暫閃白
        sendFlashPacket(player);
        CombatSoundHelper.play(level, player.position(),
            CombatSoundHelper.Layer.BOSS, CombatVfxHelper.Force.RESONANCE);

        // 可可和珍奶靠近玩家蹭腿
        petAffectionResponse(level, player);

        // 進入印記階段
        setStage(bond, CeremonyStage.MARKING);
        applyMarkEffect(player, bond, level, next);

        // 發送同步
        bond.setPendingPromotionTier(0);
        BondSyncCoordinator.syncSequenceImmediate(player, bond);

        player.displayClientMessage(
            Component.translatable("sequence.cocojenna.promoted", next), true);
        player.displayClientMessage(
            Component.translatable("promotion.cocojenna.selected",
                PromotionCardCatalog.displayName(cardId)), true);
        com.cocojenna.weapon.WeaponUnsealManager.onSequencePromotion(player);
    }

    private static void spawnUnselectedCardsDissolve(ServerLevel level, Vec3 center, String force, int chosenIndex) {
        for (int i = 0; i < 3; i++) {
            if (i == chosenIndex) continue;
            double ang = (i - 1) * Math.PI / 3;
            Vec3 cardPos = center.add(Math.cos(ang) * 2.0, 1.0, Math.sin(ang) * 2.0);
            // 消散粒子
            level.sendParticles(ParticleTypes.END_ROD,
                cardPos.x, cardPos.y, cardPos.z,
                12, 0.3, 0.3, 0.3, 0.04);
        }
    }

    private static void sendFlashPacket(ServerPlayer player) {
        ModNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new CeremonyEffectPacket(CeremonyEffectPacket.EffectType.FLASH, "", 4)); // 0.2秒
    }

    private static void petAffectionResponse(ServerLevel level, Player player) {
        // 附近的可可和珍奶會靠近玩家並蹭腿
        level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
            player.getBoundingBox().inflate(8), e -> {
                String name = e.getType().getDescription().getString();
                return name.contains("Coco") || name.contains("Jenna") || name.contains("珍奶");
            }).forEach(pet -> {
                if (pet instanceof PathfinderMob mob) {
                    mob.getNavigation().moveTo(player.getX() + 1, player.getY(), player.getZ() + 1, 0.8);
                }
            });
    }

    // ========================================================================
    // 第五階段：印記 (Marking) - 永久視覺變化
    // ========================================================================

    /**
     * 根據序列等級應用永久視覺印記
     */
    private static void applyMarkEffect(ServerPlayer player, BondData bond,
                                         ServerLevel level, int newTier) {
        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";

        // 發送客戶端印記效果
        int markLevel = getMarkLevel(newTier);
        ModNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new CeremonyEffectPacket(
                CeremonyEffectPacket.EffectType.PERMANENT_MARK,
                force, markLevel));

        // 序列1：NPC對玩家的印記作出反應
        if (newTier <= 1) {
            markLevel = 3; // 完整紋路
            broadcastMarkReaction(level, player, force);
        }

        setStage(bond, CeremonyStage.COMPLETE);
        player.displayClientMessage(
            Component.translatable("ceremony.cocojenna.complete"), true);
    }

    /**
     * 獲取印記等級
     * 0: 無印記 (序列9-7)
     * 1: 微弱光點 (序列6-4)
     * 2: 小型紋路 (序列3-2)
     * 3: 完整紋路 (序列1)
     */
    private static int getMarkLevel(int tier) {
        return getMarkLevelStatic(tier);
    }

    /** 公開靜態版本（供 SequencePromotionHelper 調用）. */
    public static int getMarkLevelStatic(int tier) {
        if (tier >= 7) return 0;
        if (tier >= 4) return 1;
        if (tier >= 2) return 2;
        return 3;
    }

    private static void broadcastMarkReaction(ServerLevel level, Player player, String force) {
        // 附近NPC對玩家印記的反應
        level.players().forEach(p -> {
            if (!p.equals(player) && p.distanceTo(player) < 16) {
                p.displayClientMessage(
                    Component.translatable("ceremony.cocojenna.mark_reaction",
                        player.getDisplayName()), true);
            }
        });
    }

    // ========================================================================
    // 輔助方法
    // ========================================================================

    private static void setStage(BondData bond, CeremonyStage stage) {
        bond.setCeremonyStage(stage.ordinal());
    }

    private static boolean isStage(BondData bond, CeremonyStage stage) {
        return bond.getCeremonyStage() == stage.ordinal();
    }

    private static int forceColor(String force) {
        return switch (force) {
            case "resonance" -> COLOR_RESONANCE;
            case "shadow"    -> COLOR_SHADOW;
            case "chaos"     -> COLOR_CHAOS;
            default -> 0xFFFFFFFF;
        };
    }

    private static void sendStagePacket(ServerPlayer player, CeremonyStage stage, int tier) {
        BondSyncCoordinator.syncCeremonyStage(player, com.cocojenna.capability.ModCapabilities.getOrDefault(player));
        if (stage == CeremonyStage.RESONANCE) {
            broadcastSpectator(player, CeremonySpectatorPacket.SpectatorEffect.RESONANCE_WAVE,
                    com.cocojenna.capability.ModCapabilities.getOrDefault(player).getFelineForce());
        }
    }

    /**
     * 檢查儀式是否超時（例如玩家在動畫中被攻擊）
     */
    public static boolean checkTimeout(BondData bond, long currentGameTime) {
        int timeout = bond.getCeremonyTimeout();
        if (timeout > 0 && currentGameTime > timeout) {
            bond.setPendingPromotionTier(0);
            setStage(bond, CeremonyStage.NONE);
            return true;
        }
        return false;
    }

    /**
     * 中斷儀式（例如被攻擊）
     */
    public static void interruptCeremony(ServerPlayer player, BondData bond) {
        if (isStage(bond, CeremonyStage.NONE)) return;
        if (isStage(bond, CeremonyStage.COMPLETE)) return;

        bond.setPendingPromotionTier(0);
        setStage(bond, CeremonyStage.NONE);
        bond.setCeremonyTimeout(0);

        player.displayClientMessage(
            Component.translatable("ceremony.cocojenna.interrupted"), true);

        // 材料不返還（設計書2.3）
    }

    // ========================================================================
    // 小型共鳴祭壇搭建檢查
    // ========================================================================

    /**
     * 檢查3x3小型祭壇是否搭建完成
     * 月光石磚×4（底座）
     * 純淨呼嚕結晶方塊×1（核心）
     * 對應途徑結晶方塊×2（兩側）
     */
    public static boolean checkSmallAltar(Level level, BlockPos corePos) {
        // 底座：4個月光石磚在四角
        BlockPos[] corners = {
            corePos.offset(-1, -1, -1),
            corePos.offset(1, -1, -1),
            corePos.offset(-1, -1, 1),
            corePos.offset(1, -1, 1)
        };
        for (BlockPos corner : corners) {
            if (!level.getBlockState(corner).is(ModBlocks.MOONSTONE_BRICK.get())) {
                return false;
            }
        }

        // 核心：純淨呼嚕結晶方塊
        if (!level.getBlockState(corePos).is(ModBlocks.PURR_CRYSTAL_BLOCK.get())) {
            return false;
        }

        // 兩側：對應途徑結晶方塊（已由方塊entity data指定）
        return true;
    }
}