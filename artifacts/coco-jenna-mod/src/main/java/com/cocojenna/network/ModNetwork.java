package com.cocojenna.network;

import com.cocojenna.CocoJennaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CocoJennaMod.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private static int packetId = 0;

    public static void register() {
        // 客戶端 ← 伺服器：同步 BondData
        CHANNEL.registerMessage(packetId++,
                SyncBondDataPacket.class,
                SyncBondDataPacket::encode,
                SyncBondDataPacket::decode,
                SyncBondDataPacket::handle);

        // 客戶端 ← 伺服器：開啟記憶之書 GUI
        CHANNEL.registerMessage(packetId++,
                OpenMemoryBookPacket.class,
                OpenMemoryBookPacket::encode,
                OpenMemoryBookPacket::decode,
                OpenMemoryBookPacket::handle);

        // 客戶端 ← 伺服器：播放初晴事件
        CHANNEL.registerMessage(packetId++,
                TriggerFirstDawnPacket.class,
                TriggerFirstDawnPacket::encode,
                TriggerFirstDawnPacket::decode,
                TriggerFirstDawnPacket::handle);

        // 客戶端 ← 伺服器：觸發心靈同步視角
        CHANNEL.registerMessage(packetId++,
                MindSyncViewPacket.class,
                MindSyncViewPacket::encode,
                MindSyncViewPacket::decode,
                MindSyncViewPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenGalgameDialoguePacket.class,
                OpenGalgameDialoguePacket::encode,
                OpenGalgameDialoguePacket::decode,
                OpenGalgameDialoguePacket::handle);

        CHANNEL.registerMessage(packetId++,
                DialogueResultPacket.class,
                DialogueResultPacket::encode,
                DialogueResultPacket::decode,
                DialogueResultPacket::handle);

        CHANNEL.registerMessage(packetId++,
                CastSequenceSkillPacket.class,
                CastSequenceSkillPacket::encode,
                CastSequenceSkillPacket::decode,
                CastSequenceSkillPacket::handle);

        CHANNEL.registerMessage(packetId++,
                EnhanceWeaponPacket.class,
                EnhanceWeaponPacket::encode,
                EnhanceWeaponPacket::decode,
                EnhanceWeaponPacket::handle);

        CHANNEL.registerMessage(packetId++,
                IronpawForgeActionPacket.class,
                IronpawForgeActionPacket::encode,
                IronpawForgeActionPacket::decode,
                IronpawForgeActionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                BondSettingsPacket.class,
                BondSettingsPacket::encode,
                BondSettingsPacket::decode,
                BondSettingsPacket::handle);

        CHANNEL.registerMessage(packetId++,
                MpsSchedulePacket.class,
                MpsSchedulePacket::encode,
                MpsSchedulePacket::decode,
                MpsSchedulePacket::handle);

        CHANNEL.registerMessage(packetId++,
                SocketWeaponPacket.class,
                SocketWeaponPacket::encode,
                SocketWeaponPacket::decode,
                SocketWeaponPacket::handle);

        CHANNEL.registerMessage(packetId++,
                CraftCloakPacket.class,
                CraftCloakPacket::encode,
                CraftCloakPacket::decode,
                CraftCloakPacket::handle);

        CHANNEL.registerMessage(packetId++,
                BuyRyokatanaPacket.class,
                BuyRyokatanaPacket::encode,
                BuyRyokatanaPacket::decode,
                BuyRyokatanaPacket::handle);

        CHANNEL.registerMessage(packetId++,
                BlackjackGamblePacket.class,
                BlackjackGamblePacket::encode,
                BlackjackGamblePacket::decode,
                BlackjackGamblePacket::handle);

        CHANNEL.registerMessage(packetId++,
                BuyCheshireMarketPacket.class,
                BuyCheshireMarketPacket::encode,
                BuyCheshireMarketPacket::decode,
                BuyCheshireMarketPacket::handle);

        CHANNEL.registerMessage(packetId++,
                PlayerActionPacket.class,
                PlayerActionPacket::encode,
                PlayerActionPacket::decode,
                PlayerActionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenPromotionPacket.class,
                OpenPromotionPacket::encode,
                OpenPromotionPacket::decode,
                OpenPromotionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                SelectPromotionCardPacket.class,
                SelectPromotionCardPacket::encode,
                SelectPromotionCardPacket::decode,
                SelectPromotionCardPacket::handle);

        CHANNEL.registerMessage(packetId++,
                RepairCloakPacket.class,
                RepairCloakPacket::encode,
                RepairCloakPacket::decode,
                RepairCloakPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenSkillSettingsPacket.class,
                OpenSkillSettingsPacket::encode,
                OpenSkillSettingsPacket::decode,
                OpenSkillSettingsPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenKingdomTerminalPacket.class,
                OpenKingdomTerminalPacket::encode,
                OpenKingdomTerminalPacket::decode,
                OpenKingdomTerminalPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenKingdomHubPacket.class,
                OpenKingdomHubPacket::encode,
                OpenKingdomHubPacket::decode,
                OpenKingdomHubPacket::handle);

        CHANNEL.registerMessage(packetId++,
                KingdomDecreeActionPacket.class,
                KingdomDecreeActionPacket::encode,
                KingdomDecreeActionPacket::decode,
                KingdomDecreeActionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenAbyssRunPacket.class,
                OpenAbyssRunPacket::encode,
                OpenAbyssRunPacket::decode,
                OpenAbyssRunPacket::handle);

        CHANNEL.registerMessage(packetId++,
                AbyssRunActionPacket.class,
                AbyssRunActionPacket::encode,
                AbyssRunActionPacket::decode,
                AbyssRunActionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenCatKitchenPacket.class,
                OpenCatKitchenPacket::encode,
                OpenCatKitchenPacket::decode,
                OpenCatKitchenPacket::handle);

        CHANNEL.registerMessage(packetId++,
                CookFoodPacket.class,
                CookFoodPacket::encode,
                CookFoodPacket::decode,
                CookFoodPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenPictureBookPacket.class,
                OpenPictureBookPacket::encode,
                OpenPictureBookPacket::decode,
                OpenPictureBookPacket::handle);

        CHANNEL.registerMessage(packetId++,
                PictureBookEditPacket.class,
                PictureBookEditPacket::encode,
                PictureBookEditPacket::decode,
                PictureBookEditPacket::handle);

        CHANNEL.registerMessage(packetId++,
                BuildingContributePacket.class,
                BuildingContributePacket::encode,
                BuildingContributePacket::decode,
                BuildingContributePacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenCatCoreEngineeringPacket.class,
                OpenCatCoreEngineeringPacket::encode,
                OpenCatCoreEngineeringPacket::decode,
                OpenCatCoreEngineeringPacket::handle);

        CHANNEL.registerMessage(packetId++,
                BuildingPlacePacket.class,
                BuildingPlacePacket::encode,
                BuildingPlacePacket::decode,
                BuildingPlacePacket::handle);

        CHANNEL.registerMessage(packetId++,
                WebUiActionPacket.class,
                WebUiActionPacket::encode,
                WebUiActionPacket::decode,
                WebUiActionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                WebUiStatePacket.class,
                WebUiStatePacket::encode,
                WebUiStatePacket::decode,
                WebUiStatePacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenUndercatHubPacket.class,
                OpenUndercatHubPacket::encode,
                OpenUndercatHubPacket::decode,
                OpenUndercatHubPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenRiverVoyagePacket.class,
                OpenRiverVoyagePacket::encode,
                OpenRiverVoyagePacket::decode,
                OpenRiverVoyagePacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenCatnipPlantPacket.class,
                OpenCatnipPlantPacket::encode,
                OpenCatnipPlantPacket::decode,
                OpenCatnipPlantPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenDaikatanaRitualPacket.class,
                OpenDaikatanaRitualPacket::encode,
                OpenDaikatanaRitualPacket::decode,
                OpenDaikatanaRitualPacket::handle);

        CHANNEL.registerMessage(packetId++,
                DaikatanaRitualActionPacket.class,
                DaikatanaRitualActionPacket::encode,
                DaikatanaRitualActionPacket::decode,
                DaikatanaRitualActionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                SellCatnipPacket.class,
                SellCatnipPacket::encode,
                SellCatnipPacket::decode,
                SellCatnipPacket::handle);

        CHANNEL.registerMessage(packetId++,
                BuyReputationShopPacket.class,
                BuyReputationShopPacket::encode,
                BuyReputationShopPacket::decode,
                BuyReputationShopPacket::handle);

        CHANNEL.registerMessage(packetId++,
                DistillStrikePacket.class,
                DistillStrikePacket::encode,
                DistillStrikePacket::decode,
                DistillStrikePacket::handle);

        CHANNEL.registerMessage(packetId++,
                LibraryShelfPacket.class,
                LibraryShelfPacket::encode,
                LibraryShelfPacket::decode,
                LibraryShelfPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenMemoryTheaterPacket.class,
                OpenMemoryTheaterPacket::encode,
                OpenMemoryTheaterPacket::decode,
                OpenMemoryTheaterPacket::handle);

        CHANNEL.registerMessage(packetId++,
                MemoryTheaterReplayPacket.class,
                MemoryTheaterReplayPacket::encode,
                MemoryTheaterReplayPacket::decode,
                MemoryTheaterReplayPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenMemoryTheaterRequestPacket.class,
                OpenMemoryTheaterRequestPacket::encode,
                OpenMemoryTheaterRequestPacket::decode,
                OpenMemoryTheaterRequestPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenAlphaExchangePacket.class,
                OpenAlphaExchangePacket::encode,
                OpenAlphaExchangePacket::decode,
                OpenAlphaExchangePacket::handle);

        CHANNEL.registerMessage(packetId++,
                AlphaExchangePacket.class,
                AlphaExchangePacket::encode,
                AlphaExchangePacket::decode,
                AlphaExchangePacket::handle);

        CHANNEL.registerMessage(packetId++,
                SanhuaQuestPacket.class,
                SanhuaQuestPacket::encode,
                SanhuaQuestPacket::decode,
                SanhuaQuestPacket::handle);

        // ── 晉升儀式封包 ─────────────────────────────────────────────────
        CHANNEL.registerMessage(packetId++,
                CeremonyStagePacket.class,
                CeremonyStagePacket::encode,
                CeremonyStagePacket::decode,
                CeremonyStagePacket::handle);

        CHANNEL.registerMessage(packetId++,
                MemoryForgeHudPacket.class,
                MemoryForgeHudPacket::encode,
                MemoryForgeHudPacket::decode,
                MemoryForgeHudPacket::handle);

        CHANNEL.registerMessage(packetId++,
                CeremonyEffectPacket.class,
                CeremonyEffectPacket::encode,
                CeremonyEffectPacket::decode,
                CeremonyEffectPacket::handle);

        // ── 動態鏡頭封包 ─────────────────────────────────────────────────
        CHANNEL.registerMessage(packetId++,
                CameraShakePacket.class,
                CameraShakePacket::encode,
                CameraShakePacket::decode,
                CameraShakePacket::handle);

        CHANNEL.registerMessage(packetId++,
                ScreenFilterPacket.class,
                ScreenFilterPacket::encode,
                ScreenFilterPacket::decode,
                ScreenFilterPacket::handle);

        CHANNEL.registerMessage(packetId++,
                IncrementalBondSyncPacket.class,
                IncrementalBondSyncPacket::encode,
                IncrementalBondSyncPacket::decode,
                IncrementalBondSyncPacket::handle);

        CHANNEL.registerMessage(packetId++,
                MarkUpdatePacket.class,
                MarkUpdatePacket::encode,
                MarkUpdatePacket::decode,
                MarkUpdatePacket::handle);

        CHANNEL.registerMessage(packetId++,
                OnboardingHintPacket.class,
                OnboardingHintPacket::encode,
                OnboardingHintPacket::decode,
                OnboardingHintPacket::handle);

        CHANNEL.registerMessage(packetId++,
                CeremonySpectatorPacket.class,
                CeremonySpectatorPacket::encode,
                CeremonySpectatorPacket::decode,
                CeremonySpectatorPacket::handle);

        CHANNEL.registerMessage(packetId++,
                OpenForceSelectionPacket.class,
                OpenForceSelectionPacket::encode,
                OpenForceSelectionPacket::decode,
                OpenForceSelectionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                ConfirmForceSelectionPacket.class,
                ConfirmForceSelectionPacket::encode,
                ConfirmForceSelectionPacket::decode,
                ConfirmForceSelectionPacket::handle);

        CHANNEL.registerMessage(packetId++,
                CatRadialActionPacket.class,
                CatRadialActionPacket::encode,
                CatRadialActionPacket::decode,
                CatRadialActionPacket::handle);
    }
}
