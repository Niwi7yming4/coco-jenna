package com.cocojenna.event;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.network.TriggerFirstDawnPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

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

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

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

        // 同步數據到客戶端
        if (player instanceof ServerPlayer sp) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new SyncBondDataPacket(bond.serializeNBT()));
        }
    }

    // ── 玩家拾取記憶碎片 ─────────────────────────────────────────────────

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        ItemStack stack = event.getStack();

        if (stack.getItem() == ModItems.MEMORY_SHARD.get()
                || stack.getItem() == ModItems.COCO_MEMORY_SHARD.get()
                || stack.getItem() == ModItems.JENNA_MEMORY_SHARD.get()) {

            BondData bond = ModCapabilities.getOrDefault(player);
            bond.addMemoryShard(1);
            bond.modifyCocoEmotion(3f);   // 閱讀記憶碎片給她們聽 +3
            bond.modifyJennaEmotion(3f);

            // 播放碎片拾取音效
            player.level().playSound(null, player.blockPosition(),
                    ModSounds.ITEM_MEMORY_SHARD_PICKUP.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

            // 顯示字幕提示
            player.displayClientMessage(
                    Component.translatable("cocojenna.memory_shard.pickup",
                            bond.getMemoryShardsTotal()),
                    true);

            // 同步到客戶端
            if (player instanceof ServerPlayer sp) {
                ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                        new SyncBondDataPacket(bond.serializeNBT()));
            }
        }
    }

    // ── 黑泥寄生持續效果 ────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

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
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.triggerEndgame();

        // 廣播初晴封包給附近所有玩家
        ModNetwork.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        player.serverLevel(),
                        player.getX(), player.getY(), player.getZ(),
                        512.0,
                        null)),
                new TriggerFirstDawnPacket());

        // 開啟終局模式
        player.level().getEntitiesOfClass(CocoEntity.class,
                player.getBoundingBox().inflate(200.0)).forEach(c -> c.setEndgame(true));
        player.level().getEntitiesOfClass(JennaEntity.class,
                player.getBoundingBox().inflate(200.0)).forEach(j -> j.setEndgame(true));

        // 同步數據
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }

    // ── 玩家與貓互動（右鍵）────────────────────────────────────────────

    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().level().isClientSide) return;

        Player player = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof CocoEntity coco && event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
            // 設定主人（首次互動）
            if (coco.getOwnerUUID() == null) {
                coco.setOwnerUUID(player.getUUID());
            }
            if (coco.getOwnerUUID().equals(player.getUUID())) {
                coco.onPet(player);
                BondData bond = ModCapabilities.getOrDefault(player);
                bond.setLastInteractCoco(player.level().getGameTime());
                event.setCanceled(true);
            }
        }

        if (target instanceof JennaEntity jenna && event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
            if (jenna.getOwnerUUID() == null) {
                jenna.setOwnerUUID(player.getUUID());
            }
            if (jenna.getOwnerUUID().equals(player.getUUID())) {
                jenna.onPet(player);
                BondData bond = ModCapabilities.getOrDefault(player);
                bond.setLastInteractJenna(player.level().getGameTime());
                event.setCanceled(true);
            }
        }
    }
}
