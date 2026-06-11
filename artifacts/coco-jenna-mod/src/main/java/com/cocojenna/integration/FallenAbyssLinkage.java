package com.cocojenna.integration;

import com.cocojenna.capability.BondData;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.world.KingdomBuildSavedData;
import com.cocojenna.world.RegionGenerators;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/** 覺醒值≥50 時於中央廣場生成盲水之鏡（需 fallen-abyss-mod，軟依賴）. */
public final class FallenAbyssLinkage {

    private static final ResourceLocation MIRROR_ID = new ResourceLocation("fallen_abyss", "blind_water_mirror");
    public static final BlockPos PLAZA_MIRROR = RegionGenerators.CENTRAL_PLAZA.offset(10, 1, 0);
    private static final String BUILDING_ID = "fallen_abyss_mirror";

    private FallenAbyssLinkage() {}

    public static boolean isFallenAbyssLoaded() {
        return ModList.get().isLoaded("fallen_abyss");
    }

    public static boolean awakeningReady(BondData bond) {
        return bond.getCocoAwakening() >= 50 || bond.getJennaAwakening() >= 50;
    }

    public static void trySpawnMirror(ServerLevel level, BondData bond, ServerPlayer notify) {
        if (!isFallenAbyssLoaded()) return;
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (!awakeningReady(bond)) return;

        KingdomBuildSavedData data = KingdomBuildSavedData.get(level);
        if (data.isBuildingPlaced(BUILDING_ID)) return;

        Block mirror = ForgeRegistries.BLOCKS.getValue(MIRROR_ID);
        if (mirror == null || mirror == Blocks.AIR) return;

        BlockPos base = PLAZA_MIRROR.below();
        level.setBlock(base, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState(), 3);
        level.setBlock(PLAZA_MIRROR, mirror.defaultBlockState(), 3);
        level.setBlock(PLAZA_MIRROR.above(), Blocks.END_ROD.defaultBlockState(), 3);

        data.markBuildingPlaced(BUILDING_ID);
        if (notify != null) {
            notify.displayClientMessage(
                    Component.translatable("cocojenna.fallen_abyss.mirror_spawned")
                            .withStyle(ChatFormatting.DARK_AQUA),
                    false);
        }
    }

    /** 反射讀取 fallen-abyss lockedPath（NONE / ABYSS / FLESH）. */
    public static String getFallenLockedPath(ServerPlayer player) {
        if (!isFallenAbyssLoaded()) return "NONE";
        try {
            Class<?> capClass = Class.forName("com.fallenabyss.capability.ModCapabilities");
            Object data = capClass.getMethod("getOrDefault", net.minecraft.world.entity.player.Player.class)
                    .invoke(null, player);
            Object path = data.getClass().getMethod("getLockedPath").invoke(data);
            return path == null ? "NONE" : path.toString();
        } catch (ReflectiveOperationException e) {
            return "NONE";
        }
    }

    /** 貓序列 commit 前：神墜側未鎖定 ABYSS/FLESH. */
    public static boolean canCommitFelinePath(ServerPlayer player) {
        if (!isFallenAbyssLoaded()) return true;
        String path = getFallenLockedPath(player);
        return "NONE".equals(path);
    }

    /** 客戶端：神墜已鎖定非 COCO 路徑時隱藏貓圓盤. */
    public static boolean isFallenPathLockedClient() {
        if (!ModList.get().isLoaded("fallen_abyss")) return false;
        try {
            Class<?> cls = Class.forName("com.fallenabyss.client.AbyssClientState");
            String locked = (String) cls.getMethod("getLockedPath").invoke(null);
            return locked != null && !"NONE".equals(locked);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
