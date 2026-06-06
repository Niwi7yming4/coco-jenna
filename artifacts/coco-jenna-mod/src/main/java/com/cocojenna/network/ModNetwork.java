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
    }
}
