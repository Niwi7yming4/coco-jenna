package com.cocojenna.world.firstcry;

import com.cocojenna.CocoJennaMod;
import com.cocojenna.block.AncientStoneTabletBlock;
import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

/** 初啼村程序生成共用工具. */
public final class FirstCryBuildHelper {

    private FirstCryBuildHelper() {}

    public static void set(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }

    public static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }

    public static void fillRect(Level level, int x0, int y0, int z0, int x1, int y1, int z1, BlockState state) {
        int minX = Math.min(x0, x1);
        int maxX = Math.max(x0, x1);
        int minY = Math.min(y0, y1);
        int maxY = Math.max(y0, y1);
        int minZ = Math.min(z0, z1);
        int maxZ = Math.max(z0, z1);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    set(level, x, y, z, state);
                }
            }
        }
    }

    public static void hollowBox(Level level, BlockPos origin, int w, int h, int d,
            BlockState wall, BlockState floor, BlockState roof) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        fillRect(level, ox, oy, oz, ox + w - 1, oy, oz + d - 1, floor);
        for (int y = 1; y < h; y++) {
            for (int x = 0; x < w; x++) {
                for (int z = 0; z < d; z++) {
                    boolean edge = x == 0 || x == w - 1 || z == 0 || z == d - 1;
                    if (edge) {
                        set(level, ox + x, oy + y, oz + z, wall);
                    }
                }
            }
        }
        fillRect(level, ox, oy + h, oz, ox + w - 1, oy + h, oz + d - 1, roof);
    }

    public static void stripeWalls(Level level, BlockPos origin, int w, int h, int d) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        for (int y = 1; y < h; y++) {
            for (int x = 0; x < w; x++) {
                for (int z = 0; z < d; z++) {
                    boolean edge = x == 0 || x == w - 1 || z == 0 || z == d - 1;
                    if (edge) {
                        BlockState s = (x + z + y) % 2 == 0 ? planks() : wool();
                        set(level, ox + x, oy + y, oz + z, s);
                    }
                }
            }
        }
    }

    public static void doorGap(Level level, int x, int y, int z, Direction facing, int width, int height) {
        int dx = facing.getStepX();
        int dz = facing.getStepZ();
        int px = -facing.getClockWise().getStepZ();
        int pz = facing.getClockWise().getStepX();
        for (int w = 0; w < width; w++) {
            for (int dy = 0; dy < height; dy++) {
                set(level, x + px * w, y + dy, z + pz * w, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public static void catDoor(Level level, BlockPos pos, Direction facing) {
        set(level, pos, ModBlocks.PAWPRINT_GLASS.get().defaultBlockState());
        set(level, pos.above(), Blocks.AIR.defaultBlockState());
    }

    public static void fillDisk(Level level, BlockPos center, int radius, BlockState state) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    set(level, center.getX() + x, center.getY(), center.getZ() + z, state);
                }
            }
        }
    }

    public static void fillRing(Level level, BlockPos center, int inner, int outer, BlockState state) {
        for (int x = -outer; x <= outer; x++) {
            for (int z = -outer; z <= outer; z++) {
                int distSq = x * x + z * z;
                if (distSq <= outer * outer && distSq >= inner * inner) {
                    set(level, center.getX() + x, center.getY(), center.getZ() + z, state);
                }
            }
        }
    }

    /** 環形下半磚長椅（設計書 3.1）. */
    public static void lowerSlabRing(Level level, BlockPos center, int radius) {
        BlockState slab = Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
        for (int a = 0; a < 32; a++) {
            double ang = a * Math.PI * 2 / 32;
            int px = (int) Math.round(Math.cos(ang) * radius);
            int pz = (int) Math.round(Math.sin(ang) * radius);
            set(level, center.offset(px, 0, pz), slab);
        }
    }

    public static void radialPath(Level level, BlockPos center, double angle, int length, int width, BlockState path) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double perpX = -sin;
        double perpZ = cos;
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        for (int i = 1; i <= length; i++) {
            double fx = cx + cos * i;
            double fz = cz + sin * i;
            for (int w = -(width / 2); w <= width / 2; w++) {
                int x = (int) Math.round(fx + perpX * w);
                int z = (int) Math.round(fz + perpZ * w);
                set(level, x, cy, z, path);
            }
        }
    }

    public static void scatterWedgePlants(Level level, BlockPos center, int radius, int wedge, int count) {
        double base = wedge * Math.PI / 4;
        for (int i = 0; i < count; i++) {
            double ang = base + level.getRandom().nextDouble() * (Math.PI / 4);
            int r = 4 + level.getRandom().nextInt(radius - 4);
            int px = (int) (Math.cos(ang) * r);
            int pz = (int) (Math.sin(ang) * r);
            BlockState plant = level.getRandom().nextBoolean()
                    ? ModBlocks.COTTON_CANDY_SHRUB.get().defaultBlockState()
                    : ModBlocks.CATNIP.get().defaultBlockState();
            BlockPos p = center.offset(px, 1, pz);
            if (level.getBlockState(p.below()).is(ModBlocks.VELVET_GRASS.get())) {
                set(level, p, plant);
            }
        }
    }

    public static void tablet(Level level, BlockPos pos, int lore) {
        set(level, pos, ModBlocks.ANCIENT_STONE_TABLET.get().defaultBlockState()
                .setValue(AncientStoneTabletBlock.LORE, lore));
    }

    public static void lootChest(ServerLevel level, BlockPos pos, String lootId) {
        set(level, pos, Blocks.CHEST.defaultBlockState());
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RandomizableContainerBlockEntity chest) {
            chest.setLootTable(new ResourceLocation(CocoJennaMod.MOD_ID,
                    "structures/first_cry_village/" + lootId), level.random.nextLong());
        }
    }

    public static void waterPool(Level level, BlockPos origin, int w, int d) {
        fillRect(level, origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + w - 1, origin.getY(), origin.getZ() + d - 1, Blocks.WATER.defaultBlockState());
    }

    public static BlockState planks() {
        return ModBlocks.VELVET_PLANKS.get().defaultBlockState();
    }

    public static BlockState wool() {
        return ModBlocks.WOVEN_WOOL.get().defaultBlockState();
    }

    public static BlockState thatch() {
        return ModBlocks.THATCH_ROOF.get().defaultBlockState();
    }

    public static BlockState brick() {
        return ModBlocks.STARDUST_BRICK.get().defaultBlockState();
    }

    public static BlockState grass() {
        return ModBlocks.VELVET_GRASS.get().defaultBlockState();
    }
}
