package com.cocojenna.endgame.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** MPS 排程產出實際物料（雨後 BOM 和平版）. */
public final class MpsProductionManager {

    private MpsProductionManager() {}

    public static void applyDayProduction(ServerPlayer player, BondData bond) {
        int day = (bond.getMpsDayIndex() + 6) % 7;
        boolean worked = false;
        for (int b = 0; b < 4; b++) {
            MpsTask task = MpsTask.byId(bond.getMpsCell(day, b));
            if (task == MpsTask.REST || task == MpsTask.FESTIVAL) continue;
            MpsTimeBlock block = MpsTimeBlock.of(b);
            float mult = task.gather ? block.gatherMult : block.craftMult;
            mult *= jobMultiplier(bond, task);
            mult *= com.cocojenna.society.ProfessionBuildingBinder.buildingTaskMultiplier(bond, task);
            if (bond.getNpcFatigue() > 50) mult *= 0.75f;
            grantTaskOutput(player, bond, task, mult);
            worked = true;
        }
        if (worked) {
            bond.addNpcFatigue(6);
        }
    }

    private static float jobMultiplier(BondData bond, MpsTask task) {
        TownJobRank rank = switch (task) {
            case GATHER_WOOD, PROCESS_WOOD, WEAVE_CARPET, BUILD_LIGHT -> TownJobRank.CRAFTSMAN;
            case GATHER_MOONSTONE -> TownJobRank.SCHOLAR;
            case GATHER_FUR, GATHER_NEON -> TownJobRank.GARDENER;
            case FISH_NIGHT -> TownJobRank.FISHER;
            case BUILD_STAGE -> TownJobRank.ARCHITECT;
            case MAKE_WREATH, DECORATE -> TownJobRank.PERFORMER;
            case COOK_PREP -> TownJobRank.CHEF;
            default -> null;
        };
        if (rank == null) return 1f;
        int workers = 0;
        for (TownNpcProfile p : TownNpcProfile.ALL) {
            if (!bond.isTownNpcRecruited(p.id()) && !isAutoRecruited(bond, p)) continue;
            String job = bond.getTownNpcJob(p.id());
            if (job.isEmpty()) job = p.defaultJob().name();
            if (rank.name().equals(job)) workers++;
        }
        return 1f + workers * 0.18f;
    }

    private static boolean isAutoRecruited(BondData bond, TownNpcProfile p) {
        return switch (p.id()) {
            case "ironpaw" -> bond.isMetIronpaw();
            case "sanhua" -> bond.hasPeaceScene("afterrain_velvet");
            case "cheshire" -> bond.isMetBlindMerchant();
            case "white_glove" -> bond.hasPeaceScene("afterrain_blind_port");
            case "alpha" -> bond.isEndgameUnlocked();
            case "samurai" -> bond.getFirstCryQuestStage() >= com.cocojenna.quest.FirstCryQuestManager.STAGE_DUEL_DONE;
            case "monk" -> bond.isBuildingPlaced("cat_library") || bond.hasPeaceScene("afterrain_gear_town");
            case "court_lady" -> bond.isBuildingPlaced("open_air_theater")
                    || bond.getKingdomHappiness() >= 75;
            default -> false;
        };
    }

    private static void grantTaskOutput(ServerPlayer player, BondData bond, MpsTask task, float mult) {
        switch (task) {
            case GATHER_WOOD -> give(player, scaled(new ItemStack(Items.OAK_PLANKS, 4), mult));
            case PROCESS_WOOD -> give(player, scaled(new ItemStack(Items.STICK, 8), mult));
            case GATHER_MOONSTONE -> give(player, scaled(new ItemStack(ModItems.MOONSTONE.get(), 2), mult));
            case GATHER_FUR -> give(player, scaled(new ItemStack(ModItems.VELVET_FUR.get(), 3), mult));
            case WEAVE_CARPET -> give(player, scaled(new ItemStack(ModItems.VELVET_FUR.get(), 2), mult));
            case FISH_NIGHT -> give(player, scaled(new ItemStack(Items.COD, 3), mult));
            case GATHER_NEON -> give(player, scaled(new ItemStack(ModItems.NEON_MUSHROOM_ITEM.get(), 2), mult));
            case BUILD_STAGE -> bond.addBuildingProgress("festival_stage", Math.max(1, Math.round(8 * mult)));
            case BUILD_LIGHT -> give(player, scaled(new ItemStack(ModItems.MOONSTONE.get(), 1), mult));
            case MAKE_WREATH -> give(player, scaled(new ItemStack(Items.POPPY, 4), mult));
            case COOK_PREP -> give(player, scaled(new ItemStack(ModItems.BASIC_FISH_PUREE.get(), 2), mult));
            case DECORATE -> bond.addBuildingProgress("festival_stage", Math.max(1, Math.round(4 * mult)));
            default -> { }
        }
    }

    private static ItemStack scaled(ItemStack base, float mult) {
        int count = Math.max(1, Math.round(base.getCount() * mult));
        return new ItemStack(base.getItem(), count);
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
