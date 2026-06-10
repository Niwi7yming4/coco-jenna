package com.cocojenna.endgame.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.endgame.AfterRainGameplayManager;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/** 貓咪圖書館書架：存放繪本、NPC 閱讀（雨後 Ch.23–24）. */
public final class CatLibraryManager {

    public static final int MAX_SHELF = 12;
    public static final int CURATOR_THRESHOLD = 10;

    private CatLibraryManager() {}

    public static boolean canUseLibrary(BondData bond) {
        return bond.isBuildingPlaced("cat_library");
    }

    public static void shelvePage(ServerPlayer player, int pageIndex) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!canUseLibrary(bond)) {
            player.displayClientMessage(Component.translatable("library.cocojenna.need_building"), true);
            return;
        }
        var pages = bond.getPictureBookPages();
        if (pageIndex < 0 || pageIndex >= pages.size()) return;
        if (bond.getLibraryShelfPages().size() >= MAX_SHELF) {
            player.displayClientMessage(Component.translatable("library.cocojenna.shelf_full"), true);
            return;
        }
        bond.getLibraryShelfPages().add(pages.get(pageIndex));
        player.displayClientMessage(Component.translatable("library.cocojenna.shelved"), true);
        checkCurator(player, bond);
        sync(player, bond);
    }

    public static void readShelf(ServerPlayer player, int slot) {
        BondData bond = ModCapabilities.getOrDefault(player);
        var shelf = bond.getLibraryShelfPages();
        if (slot < 0 || slot >= shelf.size()) return;
        var page = shelf.get(slot);
        player.displayClientMessage(Component.translatable("library.cocojenna.reading",
                page.caption().isEmpty() ? "…" : page.caption()), false);
        bond.addKingdomHappiness(1);
        tickNpcReaders(player, bond);
        sync(player, bond);
    }

    private static void tickNpcReaders(ServerPlayer player, BondData bond) {
        if (player.getRandom().nextFloat() > 0.35f) return;
        for (var p : TownNpcProfile.ALL) {
            if ("SCHOLAR".equals(bond.getTownNpcJob(p.id()))) {
                bond.addTownNpcFavor(p.id(), 2);
                player.displayClientMessage(Component.translatable("library.cocojenna.npc_read",
                        p.nameZh()), true);
                return;
            }
        }
    }

    private static void checkCurator(ServerPlayer player, BondData bond) {
        if (bond.isLibraryCurator()) return;
        if (bond.getLibraryShelfPages().size() >= CURATOR_THRESHOLD) {
            bond.setLibraryCurator(true);
            bond.addKingdomReputation(15);
            player.displayClientMessage(Component.translatable("library.cocojenna.curator"), true);
        }
    }

    public static void tickScholarResearch(ServerPlayer player) {
        if (!AfterRainGameplayManager.isPeaceMode(player)) return;
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!canUseLibrary(bond)) return;
        if (player.level().getGameTime() % 2400 != 0) return;

        int scholars = 0;
        for (var p : TownNpcProfile.ALL) {
            if ("SCHOLAR".equals(bond.getTownNpcJob(p.id()))) scholars++;
        }
        if (scholars <= 0) return;

        bond.addBuildCreativity(scholars);
        bond.addKingdomProsperity(scholars);
        if (bond.getLibraryShelfPages().size() >= 3 && !bond.isBuildingPlaced("cat_school")) {
            bond.addBuildingProgress("cat_school", 2);
        }
    }

    public static boolean catsPresentForDecree(ServerPlayer player) {
        boolean coco = false, jenna = false;
        for (AbstractCatEntity cat : player.level().getEntitiesOfClass(AbstractCatEntity.class,
                player.getBoundingBox().inflate(16))) {
            if (!(cat.getOwnerUUID() != null && cat.getOwnerUUID().equals(player.getUUID()))) continue;
            if (cat instanceof CocoEntity) coco = true;
            if (cat instanceof JennaEntity) jenna = true;
        }
        return coco || jenna;
    }

    public static boolean bothCatsPresent(ServerPlayer player) {
        boolean coco = false, jenna = false;
        for (AbstractCatEntity cat : player.level().getEntitiesOfClass(AbstractCatEntity.class,
                player.getBoundingBox().inflate(16))) {
            if (!(cat.getOwnerUUID() != null && cat.getOwnerUUID().equals(player.getUUID()))) continue;
            if (cat instanceof CocoEntity) coco = true;
            if (cat instanceof JennaEntity) jenna = true;
        }
        return coco && jenna;
    }

    private static void sync(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }
}
