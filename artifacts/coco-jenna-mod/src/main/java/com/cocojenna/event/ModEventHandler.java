package com.cocojenna.event;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.block.CatBedBlock;
import com.cocojenna.block.entity.FoodBowlBlockEntity;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.entity.SisterBondSystem;
import com.cocojenna.init.*;
import com.cocojenna.item.CatFoodItem;
import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.memforge.DaikatanaRitualManager;
import com.cocojenna.memforge.MemoryForgeManager;
import com.cocojenna.memforge.MemoryForgeRitual;
import com.cocojenna.memforge.MemoryForgeSavedData;
import com.cocojenna.memforge.SocketHelper;
import com.cocojenna.memforge.WeaponEnhanceHelper;
import com.cocojenna.gear.CloakEffectHelper;
import com.cocojenna.item.RyokatanaEffectHelper;
import com.cocojenna.reputation.ReputationHelper;
import com.cocojenna.sequence.HiddenSequenceBonuses;
import com.cocojenna.sequence.PromotionCardCombatRegistry;
import com.cocojenna.sequence.SequencePromotionHelper;
import com.cocojenna.gear.SetBonusHelper;
import com.cocojenna.sequence.FelineSequenceSkills;
import com.cocojenna.util.SequenceUnlockHelper;
import com.cocojenna.world.BlindPortGenerator;
import com.cocojenna.world.CatKingdomTerrainDecorator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkEvent;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.network.TriggerFirstDawnPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

/**
 * 模組主事件處理器。
 *
 * <p>涵蓋：
 * <ul>
 *   <li>玩家每日重置（情感值天數計算）</li>
 *   <li>玩家受傷時通知貓咪</li>
 *   <li>玩家死亡時清除特定效果</li>
 *   <li>玩家與貓互動（右鍵）</li>
 *   <li>黑泥寄生持續傷害</li>
 *   <li>終局「初晴」事件觸發</li>
 *   <li>記憶碎片相關系統</li>
 *   <li>貓咪連續三天未互動的懲罰</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = CocoJennaMod.MOD_ID)
public class ModEventHandler {

    // ── 玩家受傷 → 通知可可 ──────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (com.cocojenna.combat.SpecialMobCombat.blocksDamage(
                event.getEntity(), event.getSource(), event.getAmount())) {
            event.setCanceled(true);
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        ItemStack weapon = player.getMainHandItem();
        float amount = event.getAmount() * com.cocojenna.combat.SpecialMobCombat
                .bonusDamage(event.getEntity(), event.getSource());
        int lvl = WeaponEnhanceHelper.getLevel(weapon);
        if (lvl > 0) {
            amount *= WeaponEnhanceHelper.damageMultiplier(lvl);
        }
        float socketBonus = SocketHelper.hitBonus(weapon);
        if (socketBonus > 0f) {
            amount *= 1f + socketBonus;
        }
        amount *= RyokatanaEffectHelper.firstStrikeBonus(player, event.getEntity(), weapon);
        amount *= RyokatanaEffectHelper.damageMultiplier(player, event.getEntity(), weapon);
        if (player instanceof ServerPlayer sp) {
            BondData bond = ModCapabilities.getOrDefault(sp);
            LivingEntity living = event.getEntity();
            boolean armedDistill = com.cocojenna.combat.DistillCombatManager.isDistillArmed(sp);
            if (living instanceof com.cocojenna.entity.BlackMudMob) {
                if (armedDistill) {
                    com.cocojenna.combat.DistillCombatManager.consumeDistillStrike(sp);
                }
                com.cocojenna.combat.DistillCombatManager.onBlackMudHit(sp, living, armedDistill);
                amount *= com.cocojenna.combat.DistillCombatManager.damageMultiplier(living, armedDistill);
                if (com.cocojenna.overworld.MoonCoreManager.hasActiveBlessing(sp)) {
                    amount *= 1.3f;
                }
            }
            amount *= 1f + SequencePromotionHelper.cardDamageBonus(bond);
            amount *= PromotionCardCombatRegistry.damageMultiplier(sp, bond, event.getEntity());
            amount *= HiddenSequenceBonuses.damageMultiplier(sp, bond, event.getEntity());
            if (event.getEntity() instanceof Player) {
                amount *= 0.6f;
            }
        }
        if (weapon.hasTag() && weapon.getTag().contains("RitualDamageBonus")) {
            amount *= 1f + weapon.getTag().getFloat("RitualDamageBonus");
        }
        int enhanceLvl = WeaponEnhanceHelper.getLevel(weapon);
        if (enhanceLvl >= 10 && player.getRandom().nextFloat() < 0.12f) {
            amount *= 1.35f;
        }
        amount *= SetBonusHelper.damageMultiplier(player, event.getEntity());
        if (player instanceof ServerPlayer spDmg) {
            BondData boneBond = ModCapabilities.getOrDefault(spDmg);
            amount *= com.cocojenna.swordbone.SwordBoneManager.damageMultiplier(boneBond);
            amount *= com.cocojenna.combat.EnemyWeaknessHelper.damageMultiplier(
                    player, event.getEntity(), amount);
            var combo = com.cocojenna.swordbone.WeaponComboRegistry.active(
                    player, boneBond, weapon, player.getOffhandItem());
            if (combo != null) {
                amount *= combo.damageMult();
            }
            amount *= com.cocojenna.combat.WeaponTypeCombat.damageBonus(spDmg, weapon, event.getEntity());
            String weaponId = net.minecraftforge.registries.ForgeRegistries.ITEMS
                    .getKey(weapon.getItem()).getPath();
            amount *= com.cocojenna.entity.goal.LearningAttackGoal.learnedDamageReduction(
                    event.getEntity(), weaponId);
        }
        RyokatanaEffectHelper.onHit(player, event.getEntity(), weapon, amount);
        if (player instanceof ServerPlayer sp) {
            com.cocojenna.combat.WeaponTypeCombat.onHit(sp, weapon, event.getEntity());
            PromotionCardCombatRegistry.onHit(sp, ModCapabilities.getOrDefault(sp), event.getEntity(), amount);
            LivingEntity victim = event.getEntity();
            if (victim instanceof com.cocojenna.entity.BlackMudMob
                    && com.cocojenna.combat.DistillCombatManager.isCoreExposed(victim)
                    && victim.getHealth() - amount <= 0) {
                com.cocojenna.combat.DistillCombatManager.onDistillKill(sp, victim);
            }
        }
        event.setAmount(amount);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            SetBonusHelper.refresh(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (player instanceof ServerPlayer sp
                && com.cocojenna.swordbone.SwordBoneManager.tryDeathSave(sp, event.getAmount())) {
            event.setCanceled(true);
            return;
        }

        float hpFraction = (player.getHealth() - event.getAmount()) / player.getMaxHealth();

        // 通知附近的可可
        player.level().getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(50.0),
                c -> c.getOwnerUUID() != null && c.getOwnerUUID().equals(player.getUUID())
        ).forEach(coco -> {
            // 低血量音效反應
            if (hpFraction < 0.3f) {
                player.level().playSound(null, coco.blockPosition(),
                        ModSounds.COCO_MEOW_CONCERN.get(), SoundSource.NEUTRAL, 1.0f, 0.8f);
            }
        });

        // 通知珍奶
        player.level().getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(50.0),
                j -> j.getOwnerUUID() != null && j.getOwnerUUID().equals(player.getUUID())
        ).forEach(jenna -> {
            if (hpFraction < 0.4f) {
                player.level().playSound(null, jenna.blockPosition(),
                        ModSounds.JENNA_MEOW_COMPLAINT.get(), SoundSource.NEUTRAL, 0.8f, 1.0f);
            }
        });
    }

    // ── 玩家死亡 ─────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        if (player instanceof ServerPlayer sp) {
            MemoryForgeManager.onPlayerDeath(sp);
            DaikatanaRitualManager.onPlayerDeath(sp);
        }

        BondData bond = ModCapabilities.getOrDefault(player);

        // 死亡時降低情感值
        bond.modifyCocoEmotion(-3f);
        bond.modifyJennaEmotion(-3f);
        bond.modifySisterBond(-1f);
    }

    // ── 玩家登入 → 重置每日計數 ─────────────────────────────────────────

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        BondData bond = ModCapabilities.getOrDefault(player);

        if (player instanceof ServerPlayer sp) {
            com.cocojenna.guardian.GuardianTransferHelper.onPlayerLogin(sp);
            com.cocojenna.integration.FallenAbyssLinkage.trySpawnMirror(sp.serverLevel(), bond, sp);
            com.cocojenna.network.BondSyncCoordinator.syncLogin(sp, bond);
            com.cocojenna.network.MultiplayerBondSyncHelper.onPlayerLogin(sp);
            com.cocojenna.quest.OnboardingQuestManager.tickHints(sp);
            com.cocojenna.overworld.OverworldPenetrationManager.onPlayerLogin(sp);
            com.cocojenna.guide.PenetrationGuideHelper.syncForStage(sp, bond.getPenetrationQuestStage());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        BondData bond = ModCapabilities.getOrDefault(sp);
        if (event.getTo() == ModDimensions.CAT_KINGDOM
                || event.getFrom() == ModDimensions.CAT_KINGDOM
                || event.getTo() == ModDimensions.UNDERCAT_DOMAIN
                || event.getFrom() == ModDimensions.UNDERCAT_DOMAIN) {
            com.cocojenna.network.BondSyncCoordinator.syncLogin(sp, bond);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && !sp.level().isClientSide) {
            com.cocojenna.guardian.GuardianTransferHelper.recordLogout(sp);
            com.cocojenna.network.BondSyncCoordinator.onLogout(sp);
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && !sp.level().isClientSide) {
            com.cocojenna.sequence.HiddenSequenceTriggers.onItemCrafted(sp, event.getCrafting());
            com.cocojenna.quest.OnboardingQuestManager.onCraft(sp, event.getCrafting());
        }
    }

    // ── 玩家拾取記憶碎片 ─────────────────────────────────────────────────

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        ItemStack stack = event.getStack();

        if (!stack.isEmpty()) {
            BondData bond = ModCapabilities.getOrDefault(player);
            com.cocojenna.swordbone.SwordBoneManager.trackItem(bond, stack);
            if (player instanceof ServerPlayer sp) {
                com.cocojenna.swordbone.WeaponMemoryRegistry.tryUnlock(
                        sp, com.cocojenna.swordbone.SwordBoneManager.weaponId(stack), bond);
                com.cocojenna.quest.OnboardingQuestManager.onItemPickup(sp, stack);
            }
        }

        if (stack.getItem() == ModItems.MEMORY_SHARD.get()
                || stack.getItem() == ModItems.COCO_MEMORY_SHARD.get()
                || stack.getItem() == ModItems.JENNA_MEMORY_SHARD.get()) {

            BondData bond = ModCapabilities.getOrDefault(player);
            bond.addMemoryShard(1);
            bond.modifyCocoEmotion(3f);
            bond.modifyJennaEmotion(3f);
            if (player instanceof ServerPlayer sp) {
                bond.notifyShardGrowth(sp);
            }

            player.level().playSound(null, player.blockPosition(),
                    ModSounds.ITEM_MEMORY_SHARD_PICKUP.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            player.displayClientMessage(
                    Component.translatable("cocojenna.memory_shard.pickup",
                            bond.getMemoryShardsTotal()),
                    true);

            if (player instanceof ServerPlayer sp) {
                ItemStack held = sp.getMainHandItem();
                com.cocojenna.weapon.WeaponUnsealManager.onMemoryShardGain(sp, held);
                SequencePromotionHelper.tryPromote(sp, bond);
                ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                        new SyncBondDataPacket(bond.serializeNBT()));
                SequenceUnlockHelper.checkMilestones(sp, bond);
                com.cocojenna.trial.AwakeningTrialManager.check(sp);
            }
        }
    }

    @SubscribeEvent
    public static void onTradeWithVillager(TradeWithVillagerEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (event.getAbstractVillager() instanceof net.minecraft.world.entity.npc.Villager villager) {
            com.cocojenna.society.FragmentedSequenceManager.onPlayerTrade(sp, villager);
        }
    }

    @SubscribeEvent
    public static void onLevelChange(net.minecraftforge.event.entity.player.PlayerXpEvent.LevelChange event) {
        if (event.getEntity() instanceof ServerPlayer sp && event.getLevels() > 0) {
            SequencePromotionHelper.tryPromote(sp, ModCapabilities.getOrDefault(sp));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof com.cocojenna.entity.BlackMudMob
                && event.getEntity().level() instanceof net.minecraft.server.level.ServerLevel sl) {
            com.cocojenna.blackmud.BlackMudEcology.onHighSequenceDeath(sl, event.getEntity());
        }
        if (event.getSource().getEntity() instanceof Player killer) {
            ItemStack weapon = killer.getMainHandItem();
            RyokatanaEffectHelper.onKill(killer, event.getEntity(), weapon);
            if (killer instanceof ServerPlayer sp) {
                var bond = ModCapabilities.getOrDefault(sp);
                com.cocojenna.combat.CombatVfxHelper.onKill(
                        sp.serverLevel(), event.getEntity(),
                        com.cocojenna.combat.CombatVfxHelper.of(bond.getFelineForce()));
                com.cocojenna.guide.GuardianGuideProgress.onMobKill(sp, event.getEntity());
                if (event.getEntity() instanceof Monster mob) {
                    com.cocojenna.trial.AwakeningTrialCombatManager.onKill(sp, mob);
                }
                if (event.getEntity() instanceof com.cocojenna.entity.BlackMudMob) {
                    com.cocojenna.weapon.WeaponUnsealManager.onBlackMudKill(sp, event.getEntity(), weapon);
                    com.cocojenna.weapon.WeaponMemoryTaskManager.onBlackMudKill(sp, weapon);
                    com.cocojenna.society.FragmentedQuestManager.onMudKill(sp);
                    if (!com.cocojenna.combat.DistillCombatManager.wasDistillLootGranted(event.getEntity())) {
                        boolean distilled = com.cocojenna.combat.DistillCombatManager.isCoreExposed(event.getEntity());
                        com.cocojenna.blackmud.BlackMudDropManager.onKill(sp, event.getEntity(), distilled);
                    }
                }
                if (event.getEntity() instanceof com.cocojenna.entity.HeatLeechEntity) {
                    com.cocojenna.quest.TutorialGuideManager.onFirstHypothermiaEncounter(sp);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ItemStack result = event.getSmelting();
        if (result.isEmpty()) return;
        var recipes = sp.server.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        ItemStack remnant = new ItemStack(ModItems.BLACK_MUD_REMNANT.get());
        for (var recipe : recipes) {
            if (!recipe.getResultItem(sp.serverLevel().registryAccess()).is(result.getItem())) continue;
            boolean usesRemnant = recipe.getIngredients().stream()
                    .anyMatch(ing -> ing.test(remnant));
            if (usesRemnant) {
                com.cocojenna.sequence.HiddenSequenceTriggers.onRemnantBurned(sp, result.getCount());
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtSacrifice(LivingHurtEvent event) {
        if (!event.getEntity().level().isClientSide) {
            com.cocojenna.blackmud.BlackMudEcology.onHurtSacrifice(event.getEntity(), event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onMobEcologyTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.Mob mob && !mob.level().isClientSide) {
            com.cocojenna.blackmud.BlackMudEcology.tickMob(mob);
        }
    }

    // ── 黑泥寄生持續效果 ────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        if (player instanceof ServerPlayer sp) {
            CloakEffectHelper.tickPlayer(sp);
            if (player.level().getGameTime() % 20 == 0) {
                com.cocojenna.blackmud.BlackMudCorruptionManager.tickPlayerCorrosion(sp);
                com.cocojenna.blackmud.BlackMudEcology.tickPlayer(sp);
            }
            if (player.tickCount % 80 == 0) {
                com.cocojenna.sequence.HiddenSequenceTriggers.onPlayerTickRegion(sp);
            }
            if (player.tickCount % 20 == 0) {
                com.cocojenna.trial.AwakeningTrialCombatManager.tick(sp);
                com.cocojenna.growth.ThreeTrackGrowthManager.tickPlayer(sp);
                com.cocojenna.weapon.WeaponUnsealManager.tickPlayer(sp);
            }
            com.cocojenna.entity.ForgottenWispEntity.tickStolenRestore(player);
            int debuffTicks = player.getPersistentData().getInt("cocojenna_seq_debuff_ticks");
            if (debuffTicks > 0) {
                player.getPersistentData().putInt("cocojenna_seq_debuff_ticks", debuffTicks - 1);
                if (debuffTicks == 1) {
                    player.getPersistentData().remove("cocojenna_seq_debuff");
                }
            }
        }

        if (event.getEntity() instanceof com.cocojenna.entity.BlackMudMob) {
            com.cocojenna.combat.DistillCombatManager.tickEntity(event.getEntity());
        }

        // 黑泥第一階段 → 每 40 tick 傷 1
        if (player.hasEffect(ModEffects.BLACK_MUD_STAGE1.get())
                && player.level().getGameTime() % 40 == 0) {
            player.hurt(player.level().damageSources().magic(), 1.0f);
        }
        // 黑泥第二階段 → 每 20 tick 傷 2
        if (player.hasEffect(ModEffects.BLACK_MUD_STAGE2.get())
                && player.level().getGameTime() % 20 == 0) {
            player.hurt(player.level().damageSources().magic(), 2.0f);
        }
        // 黑泥第三階段 → 每 10 tick 傷 3
        if (player.hasEffect(ModEffects.BLACK_MUD_STAGE3.get())
                && player.level().getGameTime() % 10 == 0) {
            player.hurt(player.level().damageSources().magic(), 3.0f);
        }
        // 黑泥第四階段（深淵化）→ 每 10 tick 傷 4
        if (player.hasEffect(ModEffects.BLACK_MUD_STAGE4.get())
                && player.level().getGameTime() % 10 == 0) {
            player.hurt(player.level().damageSources().magic(), 4.0f);
        }
        // 腐蝕印記 → 每 60 tick 依等級傷害
        if (player.hasEffect(ModEffects.CORROSION_MARK.get())
                && player.level().getGameTime() % 60 == 0) {
            var mark = player.getEffect(ModEffects.CORROSION_MARK.get());
            if (mark != null) {
                player.hurt(player.level().damageSources().magic(), 0.5f + mark.getAmplifier());
            }
        }
    }

    // ── 玩家連續未互動降低情感值 ────────────────────────────────────────

    @SubscribeEvent
    public static void onDayChange(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        long gametime = player.level().getGameTime();
        // 每天 (24000 tick) 執行一次
        if (gametime % 24000 != 0) return;

        BondData bond = ModCapabilities.getOrDefault(player);
        long lastInteract = Math.max(bond.getLastInteractCoco(), bond.getLastInteractJenna());
        long daysSince = (gametime - lastInteract) / 24000;

        if (daysSince >= 3) {
            bond.modifyCocoEmotion(-1f);
            bond.modifyJennaEmotion(-1f);
            player.displayClientMessage(
                    Component.translatable("cocojenna.bond.neglect_warning"), true);
        }
    }

    // ── 終局事件「初晴」觸發 ────────────────────────────────────────────

    /**
     * 當最終 Boss 影爪被擊敗時觸發。
     * 由 ShadowClawEntity 的 die() 方法主動呼叫此方法。
     */
    public static void triggerFirstDawn(ServerPlayer player) {
        com.cocojenna.endgame.AfterRainManager.onFirstDawn(player);

        // 廣播初晴封包給附近所有玩家
        ModNetwork.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        player.getX(), player.getY(), player.getZ(),
                        512.0,
                        player.serverLevel().dimension())),
                new TriggerFirstDawnPacket());

        // 開啟終局模式
        player.level().getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(200.0)).forEach(c -> c.setEndgame(true));
        player.level().getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(200.0)).forEach(j -> j.setEndgame(true));

        BondData bond = ModCapabilities.getOrDefault(player);
        SequenceUnlockHelper.checkMilestones(player, bond);
    }

    // ── 貓之國地形表層修復 ───────────────────────────────────────────────

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel level)) {
            return;
        }
        if (event.getChunk() instanceof LevelChunk chunk) {
            if (level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
                CatKingdomTerrainDecorator.decorateChunk(level, chunk);
                com.cocojenna.world.KingdomMicroMarkers.decorateChunk(level, chunk);
                com.cocojenna.world.BiomeStructurePlacer.decorateChunk(level, chunk);
                com.cocojenna.world.BiomeDatapackStructurePlacer.decorateChunk(level, chunk);
                com.cocojenna.world.BiomeMediumStructurePlacer.decorateChunk(level, chunk);
                com.cocojenna.world.LollipopTreePlacer.decorateChunk(level, chunk);
            }
            if (level.dimension().equals(ModDimensions.UNDERCAT_DOMAIN)) {
                com.cocojenna.world.UndercatWorldGenerator.decorateChunk(level, chunk);
            }
            if (level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                com.cocojenna.overworld.OverworldPenetrationManager.onChunkLoad(level, chunk);
                com.cocojenna.world.ruin.RuinMatrixPlacer.tryPlaceOverworld(level, chunk);
                com.cocojenna.world.qin.MausoleumPlacer.tryPlace(level, chunk);
            }
            if (level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
                com.cocojenna.world.ruin.RuinMatrixPlacer.tryPlaceCatKingdom(level, chunk);
            }
        }
    }

    // ── 貓咪 AI：進食、尋碗、貓床睡眠、姊妹羈絆 ─────────────────────────

    private static long lastSharedFoodTick = 0L;
    private static boolean cocoAteSharedBowl = false;
    private static boolean jennaAteSharedBowl = false;
    private static long sleepTogetherSince = 0L;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;
        if (event.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            MemoryForgeManager.tick(serverLevel);
            DaikatanaRitualManager.tick(serverLevel);
            com.cocojenna.quest.qin.PaperizeRestoreManager.tick(serverLevel);
            if (serverLevel.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                com.cocojenna.overworld.StrayCatGatheringManager.tickGatherings(serverLevel);
            }
            com.cocojenna.society.FragmentedSequenceManager.tick(serverLevel);
            com.cocojenna.guardian.GuardianCatFollowHelper.tick(serverLevel);
            if (serverLevel.dimension().equals(ModDimensions.CAT_KINGDOM)) {
                com.cocojenna.blackmud.BlackMudCorruptionManager.tick(serverLevel);
                com.cocojenna.blackmud.NpcCorrosionManager.tick(serverLevel);
                com.cocojenna.endgame.AfterRainGameplayManager.tick(serverLevel);
                com.cocojenna.endgame.KingdomDecreeManager.tick(serverLevel);
                com.cocojenna.endgame.KingdomDecreeWorldEffects.tick(serverLevel);
                com.cocojenna.cloak.CloakOrderSavedData.tick(serverLevel);
                for (ServerPlayer p : serverLevel.players()) {
                    com.cocojenna.undercat.UndercatQuestManager.tryTriggerAtNight(p);
                }
            }
        }
        long time = event.level.getGameTime();

        for (Player player : event.level.players()) {
            if (player instanceof ServerPlayer sp) {
                com.cocojenna.network.BondSyncCoordinator.tick(sp);
                if (event.level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                    com.cocojenna.overworld.OverworldPenetrationManager.tickPlayer(sp);
                    com.cocojenna.overworld.PenetrationQuestHud.tick(sp);
                }
                com.cocojenna.network.MultiplayerBondSyncHelper.tick(sp);
            }
            if (player instanceof ServerPlayer sp && event.level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
                if (time % 200 == 0) {
                    com.cocojenna.quest.OnboardingQuestManager.tickHints(sp);
                    com.cocojenna.quest.KingdomTutorialManager.tickHints(sp);
                }
                tickRegionDialogue(sp);
                com.cocojenna.cozy.CozyEventManager.tickPlayer(sp);
                if (time % 2400 == 0) com.cocojenna.village.VillageManager.tickDaily(sp);
                if (time % 2400 == 0) com.cocojenna.undercat.UndercatDailyQuestManager.tickDaily(sp);
            }
            if (player instanceof ServerPlayer sp && event.level.dimension().equals(ModDimensions.UNDERCAT_DOMAIN)) {
                com.cocojenna.undercat.UndercatEnvironmentManager.tickPlayer(sp);
            }
            CocoEntity coco = findOwnedCat(event.level, player, CocoEntity.class);
            JennaEntity jenna = findOwnedCat(event.level, player, JennaEntity.class);
            if (coco != null && jenna != null) {
                SisterBondSystem.tick(event.level, player, coco, jenna);
                if (time % 40 == 0) {
                    tickCatSleep(event.level, player, coco, jenna);
                }
            }

            if (time % 60 != 0) continue;

            event.level.getEntitiesOfClass(AbstractCatEntity.class, player.getBoundingBox().inflate(48.0),
                    LivingEntity::isAlive).forEach(cat -> {
                if (cat.getOwnerUUID() == null || !cat.getOwnerUUID().equals(player.getUUID())) {
                    return;
                }
                tickCatFoodSeek(event.level, cat);
                tryEatFromBowl(event.level, cat, player);
            });
        }
    }

    @Nullable
    private static <T extends AbstractCatEntity> T findOwnedCat(
            net.minecraft.world.level.Level level, Player owner, Class<T> type) {
        var cats = level.getEntitiesOfClass(type, owner.getBoundingBox().inflate(64.0),
                c -> owner.getUUID().equals(c.getOwnerUUID()));
        return cats.isEmpty() ? null : cats.get(0);
    }

    private static void tickCatFoodSeek(net.minecraft.world.level.Level level, AbstractCatEntity cat) {
        BlockPos catPos = cat.blockPosition();
        FoodBowlBlockEntity nearest = null;
        BlockPos nearestPos = null;
        double nearestDist = 256.0;

        for (int dx = -16; dx <= 16; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -16; dz <= 16; dz++) {
                    BlockPos check = catPos.offset(dx, dy, dz);
                    var be = level.getBlockEntity(check);
                    if (!(be instanceof FoodBowlBlockEntity bowl) || !bowl.hasFood()) continue;
                    double dist = cat.distanceToSqr(check.getX() + 0.5, check.getY(), check.getZ() + 0.5);
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearest = bowl;
                        nearestPos = check;
                    }
                }
            }
        }
        if (nearest != null && nearestDist > 4.0 && nearestPos != null) {
            cat.getNavigation().moveTo(nearestPos.getX() + 0.5, nearestPos.getY(), nearestPos.getZ() + 0.5, 0.9);
        }
    }

    private static void tryEatFromBowl(net.minecraft.world.level.Level level, AbstractCatEntity cat, Player owner) {
        BlockPos catPos = cat.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos bowlBlock = catPos.offset(dx, dy, dz);
                    var be = level.getBlockEntity(bowlBlock);
                    if (!(be instanceof FoodBowlBlockEntity bowl) || !bowl.hasFood()) continue;
                    if (cat.distanceToSqr(bowlBlock.getX() + 0.5, bowlBlock.getY(), bowlBlock.getZ() + 0.5) > 2.5) {
                        continue;
                    }

                    ItemStack food = bowl.getFood();
                    BondData bond = ModCapabilities.getOrDefault(owner);
                    long now = level.getGameTime();
                    if (cat instanceof CocoEntity && now - bond.getLastFeedCoco() < 600) return;
                    if (cat instanceof JennaEntity && now - bond.getLastFeedJenna() < 600) return;

                    bowl.takeFood();
                    cat.heal(2.0f);
                    applyFoodBond(cat, owner, food, bond, now);
                    return;
                }
            }
        }
    }

    private static void applyFoodBond(AbstractCatEntity cat, Player owner, ItemStack food,
            BondData bond, long now) {
        if (food.getItem() instanceof CatFoodItem catFood) {
            ItemStack dummy = food.copy();
            if (cat instanceof CocoEntity coco) {
                catFood.feedCoco(coco, owner, dummy);
            } else if (cat instanceof JennaEntity jenna) {
                catFood.feedJenna(jenna, owner, dummy);
            }
        } else {
            if (cat instanceof CocoEntity) bond.modifyCocoEmotion(1f);
            if (cat instanceof JennaEntity) bond.modifyJennaEmotion(1f);
        }

        long tick = owner.level().getGameTime();
        if (tick - lastSharedFoodTick > 200) {
            cocoAteSharedBowl = false;
            jennaAteSharedBowl = false;
        }
        if (cat instanceof CocoEntity) cocoAteSharedBowl = true;
        if (cat instanceof JennaEntity) jennaAteSharedBowl = true;
        if (cocoAteSharedBowl && jennaAteSharedBowl) {
            SisterBondSystem.onSharedFood(owner);
            lastSharedFoodTick = tick;
            cocoAteSharedBowl = false;
            jennaAteSharedBowl = false;
        }

        if (owner instanceof ServerPlayer sp) {
            SequenceUnlockHelper.checkMilestones(sp, bond);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new SyncBondDataPacket(bond.serializeNBT()));
        }
    }

    private static void tickCatSleep(net.minecraft.world.level.Level level, Player owner,
            CocoEntity coco, JennaEntity jenna) {
        if (!level.isNight() && !level.isRaining()) {
            sleepTogetherSince = 0L;
            return;
        }

        BlockPos cocoBed = findBedUnder(level, coco);
        BlockPos jennaBed = findBedUnder(level, jenna);
        boolean cocoOnBed = cocoBed != null;
        boolean jennaOnBed = jennaBed != null;

        if (cocoOnBed) {
            coco.setSitting(true);
            if (CatBedBlock.isCozy(level, cocoBed)) {
                coco.heal(0.5f);
            }
        }
        if (jennaOnBed) {
            jenna.setSitting(true);
            if (CatBedBlock.isCozy(level, jennaBed)) {
                jenna.heal(0.5f);
            }
        }

        if (cocoOnBed && jennaOnBed && coco.distanceTo(jenna) < 3.0) {
            if (sleepTogetherSince == 0L) {
                sleepTogetherSince = level.getGameTime();
            } else if (level.getGameTime() - sleepTogetherSince >= 3600) {
                SisterBondSystem.onSleepTogether(owner);
                sleepTogetherSince = level.getGameTime();
            }
        } else {
            sleepTogetherSince = 0L;
        }
    }

    @Nullable
    private static BlockPos findBedUnder(net.minecraft.world.level.Level level, AbstractCatEntity cat) {
        BlockPos below = cat.blockPosition().below();
        if (level.getBlockState(below).is(ModBlocks.CAT_BED.get())) {
            return below;
        }
        if (level.getBlockState(cat.blockPosition()).is(ModBlocks.CAT_BED.get())) {
            return cat.blockPosition();
        }
        return null;
    }

    // ── 貓之國釣魚額外掉落 ───────────────────────────────────────────────

    @SubscribeEvent
    public static void onItemFished(net.minecraftforge.event.entity.player.ItemFishedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !isCatKingdomArea(player.level(), player.blockPosition())) {
            return;
        }

        var random = player.getRandom();
        boolean blindRiver = player.level().getBiome(player.blockPosition()).is(ModBiomes.BLIND_WATER_RIVER);
        ItemStack bonus = ItemStack.EMPTY;

        if (blindRiver) {
            float roll = random.nextFloat();
            if (roll < 0.005f) {
                var ro = com.cocojenna.item.RyokatanaRegistry.get("deep_sea_current");
                if (ro != null) bonus = new ItemStack(ro.get());
            } else if (roll < 0.15f) {
                bonus = new ItemStack(ModItems.CRAB_MEAT.get());
            } else if (roll < 0.35f) {
                bonus = new ItemStack(ModItems.DEEP_SEA_FISH.get());
            }
        } else if (player.level().isNight()) {
            float roll = random.nextFloat();
            if (roll < 0.008f) {
                var ro = com.cocojenna.item.RyokatanaRegistry.get("moonlight_ripple");
                if (ro != null) bonus = new ItemStack(ro.get());
            } else if (roll < 0.22f) {
                bonus = new ItemStack(ModItems.GLOW_FISH.get());
            }
        } else {
            float roll = random.nextFloat();
            if (roll < 0.18f) {
                bonus = new ItemStack(ModItems.DEEP_SEA_FISH.get());
            } else if (roll < 0.23f) {
                bonus = new ItemStack(ModItems.GIANT_GREEN_FISH.get());
            }
        }

        if (!bonus.isEmpty()) {
            event.getDrops().add(bonus);
        }
        if (blindRiver && player instanceof ServerPlayer sp) {
            com.cocojenna.weapon.WeaponMemoryTaskManager.onFish(sp);
        }
    }

    // ── 貓之國採集：藤蔓、蒲公英 ───────────────────────────────────────

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            MemoryForgeSavedData data = MemoryForgeSavedData.get(serverLevel);
            for (MemoryForgeRitual ritual : data.rituals().values()) {
                if (ritual.phase() != MemoryForgeRitual.Phase.DEFEND) continue;
                if (!ritual.containsBlock(event.getPos())) continue;
                ritual.damageBlock(event.getPos(), MemoryForgeRitual.BLOCK_MAX_HP);
                MemoryForgeManager.onAltarBlockDamaged(ritual, data);
                data.setDirty();
                if (ritual.isCoreDestroyed()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        BlockState state = event.getState();
        if (state.is(ModBlocks.HIBISCUS_FLOWER.get()) && event.getPlayer() instanceof ServerPlayer spHib) {
            com.cocojenna.weapon.WeaponMemoryTaskManager.onHibiscusCollect(spHib);
        }

        if (!isCatKingdomArea(event.getLevel(), event.getPos())) {
            return;
        }

        var level = event.getLevel();
        BlockPos pos = event.getPos();
        if (state.is(net.minecraft.world.level.block.Blocks.CHAIN)
                && event.getPlayer() instanceof ServerPlayer sp) {
            com.cocojenna.world.ruin.RuinInteractionRegistry.onChainBroken(
                    (net.minecraft.server.level.ServerLevel) level, pos, sp);
        }

        if (state.is(Blocks.VINE) || state.is(BlockTags.CLIMBABLE)) {
            if (level.getRandom().nextFloat() < 0.7f) {
                Blocks.AIR.popResource((net.minecraft.world.level.Level) level, pos,
                        new ItemStack(ModItems.FIBER_VINE.get(), 1 + level.getRandom().nextInt(2)));
            }
        }
        if (state.is(Blocks.DANDELION)) {
            Blocks.AIR.popResource((net.minecraft.world.level.Level) level, pos,
                    new ItemStack(ModItems.DANDELION_FLUFF.get(), 1 + level.getRandom().nextInt(2)));
        }
    }

    private static boolean isCatKingdomArea(net.minecraft.world.level.LevelAccessor level, BlockPos pos) {
        if (level instanceof net.minecraft.world.level.Level world
                && world.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            return true;
        }
        return level.getBiome(pos).is(ModTags.CAT_KINGDOM_BIOMES);
    }

    private static void tickRegionDialogue(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isMetBlindMerchant()
                && player.blockPosition().distSqr(BlindPortGenerator.CENTER) < 30 * 30) {
            bond.setMetBlindMerchant(true);
            DialogueManager.play(player, "blind_port_merchant");
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isShiftKeyDown()
                && com.cocojenna.quest.qin.QinKemuPaperizeManager.tryPaperize(
                        player, event.getHand(), event.getHitVec())) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.overworld.OverworldPenetrationManager.tryInteractRuin(player, event.getPos())) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.overworld.OverworldPenetrationManager.tryInteractTrace(player, event.getPos())) {
            event.setCanceled(true);
            return;
        }
        var state = event.getLevel().getBlockState(event.getPos());
        if (com.cocojenna.kingdom.PalaceRegionManager.tryInteract(player, event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.kingdom.EcologyDeepeningManager.tryHarvest(player, event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.kingdom.OpenAirTheaterManager.tryInteract(player, event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.kingdom.KingdomMicroInteractHandler.tryInteract(player, event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.kingdom.StardustDesertManager.tryWish(player, event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.world.firstcry.MemorySmeltHandler.trySmelt(player, event.getLevel(), event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.world.firstcry.FirstCryMoonAltarHandler.trySequenceTrial(
                player, state, event.getPos())) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.quest.qin.MausoleumDiscoveryHandler.tryDiscover(
                player, event.getPos(), state)) {
            event.setCanceled(true);
            return;
        }
        if (state.is(net.minecraft.world.level.block.Blocks.BELL)) {
            com.cocojenna.world.ruin.RuinInteractionRegistry.onBellUsed(
                    (net.minecraft.server.level.ServerLevel) event.getLevel(), event.getPos(), player);
        }
        if (state.is(ModBlocks.BLACK_MUD.get())) {
            com.cocojenna.world.firstcry.FirstCryHiddenInteractionHandler.onBlackMudBlock(player, state, event.getPos());
            com.cocojenna.world.ruin.RuinInteractionRegistry.tryPurifyMud(
                    (net.minecraft.server.level.ServerLevel) event.getLevel(), event.getPos(), player);
        }
        if (player.getMainHandItem().is(ModItems.PURIFIED_SALT.get())) {
            com.cocojenna.quest.FirstCryMainQuestManager.onBlackMudPurify(player);
        }
        if (state.is(ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get())) {
            if (com.cocojenna.overworld.FusionBuildingManager.tryEmbassyTeleport(player, event.getPos())) {
                event.setCanceled(true);
            }
            return;
        }
        if (!state.is(Blocks.ENCHANTING_TABLE)) return;

        var level = (net.minecraft.server.level.ServerLevel) event.getLevel();
        MemoryForgeSavedData data = MemoryForgeSavedData.get(level);
        MemoryForgeRitual active = data.get(event.getPos());
        ItemStack held = player.getItemInHand(event.getHand());

        if (active != null && active.phase() == MemoryForgeRitual.Phase.INJECT) {
            if (MemoryForgeManager.tryInjectCatalyst(player, event.getPos(), held)) {
                event.setCanceled(true);
            }
            return;
        }
        if (active == null && MemoryForgeManager.tryStart(player, event.getPos(), held)) {
            event.setCanceled(true);
            return;
        }
        if (com.cocojenna.swordbone.SwordBoneRitualManager.tryInteract(player, event.getPos(), event.getHand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (!sp.isCrouching() || !sp.getMainHandItem().isEmpty()) return;
        com.cocojenna.swordbone.SwordBoneManager.tryNineResonance(sp);
    }

    // ── 玩家與貓互動（右鍵）────────────────────────────────────────────

    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().level().isClientSide) return;

        Player player = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof CocoEntity coco && event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
            // 設定主人（首次互動）
            boolean firstMeet = coco.getOwnerUUID() == null;
            if (firstMeet) {
                coco.setOwnerUUID(player.getUUID());
                if (player instanceof ServerPlayer sp) {
                    com.cocojenna.quest.OnboardingQuestManager.onCatInteraction(sp);
                }
            }
            if (coco.getOwnerUUID().equals(player.getUUID())) {
                coco.onPet(player);
                BondData bond = ModCapabilities.getOrDefault(player);
                bond.setLastInteractCoco(player.level().getGameTime());
                if (player instanceof ServerPlayer sp) {
                    com.cocojenna.quest.OnboardingQuestManager.onPet(sp);
                    com.cocojenna.quest.KingdomTutorialManager.onFeedOrPet(sp);
                    com.cocojenna.network.BondSyncCoordinator.onHighFrequencyChange(sp);
                }
                event.setCanceled(true);
            }
        }

        if (target instanceof JennaEntity jenna && event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
            boolean firstMeetJenna = jenna.getOwnerUUID() == null;
            if (firstMeetJenna) {
                jenna.setOwnerUUID(player.getUUID());
                if (player instanceof ServerPlayer sp) {
                    com.cocojenna.quest.OnboardingQuestManager.onCatInteraction(sp);
                }
            }
            if (jenna.getOwnerUUID().equals(player.getUUID())) {
                jenna.onPet(player);
                BondData bond = ModCapabilities.getOrDefault(player);
                bond.setLastInteractJenna(player.level().getGameTime());
                if (player instanceof ServerPlayer sp) {
                    com.cocojenna.quest.OnboardingQuestManager.onPet(sp);
                    com.cocojenna.quest.KingdomTutorialManager.onFeedOrPet(sp);
                    com.cocojenna.network.BondSyncCoordinator.onHighFrequencyChange(sp);
                }
                event.setCanceled(true);
            }
        }

        if (target instanceof net.minecraft.world.entity.LivingEntity living
                && com.cocojenna.blackmud.NpcCorrosionManager.tryPurify(player, living)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (event.player instanceof ServerPlayer sp) {
            com.cocojenna.quest.TutorialGuideManager.tick(sp);
            com.cocojenna.exploration.ExplorationGuideManager.tickPlayer(sp);
            com.cocojenna.exploration.ExplorationGuideManager.autoRevealIfReady(sp);
            com.cocojenna.swordbone.SwordBoneRitualManager.tick(sp);
            com.cocojenna.swordbone.SwordBoneManager.tickResonance(sp);
            com.cocojenna.combat.WeaponTypeCombat.tickDecay(sp);
            applySwordBoneMovement(sp);
            com.cocojenna.armor.ArmorActiveMechanics.resetAwaken(
                    sp, com.cocojenna.capability.ModCapabilities.getOrDefault(sp));
            com.cocojenna.kingdom.ThroneHallManager.tickAura(sp);
            com.cocojenna.kingdom.RainbowCanyonManager.tickPlayer(sp);
            com.cocojenna.kingdom.CatnipHighlandsManager.tickPlayer(sp);
            com.cocojenna.kingdom.StardustDesertManager.tickPlayer(sp);
            com.cocojenna.kingdom.KingdomStrayCatManager.tickPlayer(sp);
            com.cocojenna.kingdom.OpenAirTheaterManager.tickWeeklyGathering(sp);
            com.cocojenna.society.CatSocietyManager.tickRomanceAura(sp);
            com.cocojenna.society.CatDreamManager.tickPlayer(sp);
            com.cocojenna.society.CatLifeEventManager.tickDaily(sp);
            com.cocojenna.village.VillageFestivalManager.tickDaily(sp);
            com.cocojenna.kingdom.EcologyDeepeningManager.tickPlayer(sp);
            com.cocojenna.kingdom.TownNpcWorldManager.tickPlayer(sp);
            com.cocojenna.kingdom.TownNpcScheduleManager.tickPlayer(sp);
            com.cocojenna.society.CatMarriageManager.tickDaily(sp);
            com.cocojenna.sequence.MoonCrossroadsManager.tickPlayer(sp);
            com.cocojenna.sequence.MoonCrossroadsManager.tickNovicePassives(sp);
            if (sp.tickCount % 40 == 0 && sp.level().getBiome(sp.blockPosition()).is(ModBiomes.STARDUST_DESERT)) {
                com.cocojenna.undercat.StarlightChapterManager.onEnterStardust(sp);
            }
            com.cocojenna.undercat.StarlightRegionalManager.tickPlayer(sp);
            com.cocojenna.world.ruin.RuinInteractionRegistry.onPlayerTick(sp);
            com.cocojenna.world.firstcry.FirstCrySacredBlessingHandler.tick(sp);
            com.cocojenna.world.firstcry.FirstCryBlackMudEventManager.tick(sp.serverLevel());
            com.cocojenna.world.firstcry.FirstCryHiddenInteractionHandler.trySneakPassage(sp);
            com.cocojenna.quest.FirstCryQuestHud.tick(sp);
            var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(sp);
            if (bond.getCeremonyStage() > 0) {
                com.cocojenna.sequence.PromotionCeremonyHandler.checkTimeout(
                        bond, sp.serverLevel().getGameTime());
            }
        }
    }

    private static void applySwordBoneMovement(ServerPlayer player) {
        if (player.tickCount % 20 != 0) return;
        var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
        float mult = com.cocojenna.swordbone.SwordBoneManager.moveMultiplier(bond);
        var combo = com.cocojenna.swordbone.WeaponComboRegistry.active(
                player, bond, player.getMainHandItem(), player.getOffhandItem());
        if (combo != null) {
            mult *= combo.moveMult();
            if ("water_speed".equals(combo.tag()) && player.isInWater()) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE, 40, 0, true, false, true));
            }
        }
        if (mult > 1.05f) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 40,
                    mult > 1.2f ? 1 : 0, true, false, true));
        }
        int glow = com.cocojenna.swordbone.SwordBoneManager.armorGlowTier(bond);
        if (glow >= 2 && player.tickCount % 60 == 0) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.GLOWING, 30, 0, true, false, false));
        }
    }
}
