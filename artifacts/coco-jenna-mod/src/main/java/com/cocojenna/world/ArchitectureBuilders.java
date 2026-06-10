package com.cocojenna.world;

import com.cocojenna.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** 設計書建築共用建造工具 — 圓角絨毛美學. */
public final class ArchitectureBuilders {

    private ArchitectureBuilders() {}

    public static void set(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }

    public static void set(Level level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
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

    public static void fillCylinder(Level level, BlockPos center, int radius, int height, BlockState wall, BlockState hollow) {
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean edge = x * x + z * z >= (radius - 1) * (radius - 1);
                    set(level, center.getX() + x, center.getY() + y, center.getZ() + z,
                            edge ? wall : (y == 0 ? wall : hollow));
                }
            }
        }
    }

    /** 生命線球樹 — 中空樹幹 + 三層樹冠平台. */
    public static void buildLifeYarnTree(ServerLevel level, BlockPos base, int trunkRadius, int height) {
        BlockState bark = ModBlocks.VELVET_PLANKS.get().defaultBlockState();
        BlockState leaf = ModBlocks.WOVEN_WOOL.get().defaultBlockState();
        int bx = base.getX();
        int by = base.getY();
        int bz = base.getZ();

        for (int y = 0; y < height; y++) {
            int r = trunkRadius - (y > height * 0.7 ? 1 : 0);
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r) {
                        boolean hollow = x * x + z * z < (r - 1) * (r - 1) && y > 2 && y < height - 4;
                        if (!hollow) {
                            set(level, bx + x, by + y, bz + z, bark);
                        }
                    }
                }
            }
        }

        int[] crownHeights = {height / 3, (height * 2) / 3, height - 2};
        int[] crownRadii = {10, 8, 6};
        for (int i = 0; i < crownHeights.length; i++) {
            int cy = by + crownHeights[i];
            fillDisk(level, new BlockPos(bx, cy, bz), crownRadii[i], leaf);
            for (int a = 0; a < 6; a++) {
                double ang = a * Math.PI * 2 / 6;
                int px = bx + (int) (Math.cos(ang) * (crownRadii[i] - 2));
                int pz = bz + (int) (Math.sin(ang) * (crownRadii[i] - 2));
                set(level, px, cy + 1, pz, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
            }
        }

        set(level, bx, by + height + 2, bz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        for (int y = 1; y <= 3; y++) {
            set(level, bx + 2, by + y, bz, Blocks.AIR.defaultBlockState());
            set(level, bx - 2, by + y, bz, Blocks.AIR.defaultBlockState());
        }
    }

    /** 可可珍奶雙層毛線小屋. */
    public static void buildTwinCottage(Level level, BlockPos origin) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        BlockState wall = ModBlocks.WOVEN_WOOL.get().defaultBlockState();
        BlockState floor = ModBlocks.VELVET_CARPET.get().defaultBlockState();
        BlockState roof = ModBlocks.VELVET_PLANKS.get().defaultBlockState();

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                set(level, ox + x, oy, oz + z, floor);
            }
        }
        for (int y = 1; y <= 3; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    boolean edge = Math.abs(x) == 4 || Math.abs(z) == 4;
                    if (edge) set(level, ox + x, oy + y, oz + z, wall);
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                set(level, ox + x, oy + 4, oz + z, roof);
            }
        }
        set(level, ox - 2, oy + 1, oz, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, ox + 2, oy + 1, oz, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, ox, oy + 1, oz + 4, ModBlocks.FOOD_BOWL.get().defaultBlockState());
        set(level, ox + 3, oy + 1, oz + 3, ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        set(level, ox, oy + 1, oz + 4, Blocks.AIR.defaultBlockState());
        set(level, ox, oy + 2, oz + 4, Blocks.AIR.defaultBlockState());
        set(level, ox, oy + 1, oz - 4, Blocks.AIR.defaultBlockState());
        set(level, ox, oy, oz - 5, Blocks.AIR.defaultBlockState());

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, ox + x, oy + 4, oz + z, floor);
            }
        }
        set(level, ox, oy + 5, oz - 3, Blocks.LADDER.defaultBlockState()
                .setValue(net.minecraft.world.level.block.LadderBlock.FACING, Direction.SOUTH));
        set(level, ox + 2, oy + 5, oz, ModBlocks.MEMORY_MONUMENT_BASE.get().defaultBlockState());
    }

    /** 記憶紀念碑貓抓柱塔 — 可成長高度（30→50→70→100 五階敘事）. */
    public static void buildScratchMonumentTower(Level level, BlockPos base, int height) {
        int bx = base.getX();
        int by = base.getY();
        int bz = base.getZ();
        for (int y = 0; y < height; y++) {
            int r = Math.max(2, 4 - y / 12);
            BlockState core = y < 8
                    ? ModBlocks.MEMORY_MONUMENT_BASE.get().defaultBlockState()
                    : ModBlocks.CAT_SCRATCH_BOARD.get().defaultBlockState();
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r) {
                        set(level, bx + x, by + y, bz + z, core);
                    }
                }
            }
            if (height >= 50 && y == 24) {
                fillDisk(level, new BlockPos(bx, by + y, bz), r + 2, ModBlocks.WOVEN_WOOL.get().defaultBlockState());
                for (int a = 0; a < 6; a++) {
                    double ang = a * Math.PI * 2 / 6;
                    set(level, bx + (int) (Math.cos(ang) * (r + 2)), by + y + 1, bz + (int) (Math.sin(ang) * (r + 2)),
                            ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
                }
            }
            if (height >= 70 && y % 14 == 13) {
                for (int a = 0; a < 8; a++) {
                    double ang = a * Math.PI * 2 / 8;
                    set(level, bx + (int) (Math.cos(ang) * (r + 1)), by + y, bz + (int) (Math.sin(ang) * (r + 1)),
                            ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
                    set(level, bx + (int) (Math.cos(ang) * (r + 2)), by + y + 1, bz + (int) (Math.sin(ang) * (r + 2)),
                            ModBlocks.VELVET_VINE.get().defaultBlockState());
                }
            } else if (y % 10 == 9) {
                for (int a = 0; a < 8; a++) {
                    double ang = a * Math.PI * 2 / 8;
                    set(level, bx + (int) (Math.cos(ang) * (r + 1)), by + y, bz + (int) (Math.sin(ang) * (r + 1)),
                            ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
                }
            }
        }
        if (height >= 100) {
            fillDisk(level, base.above(height - 2), 5, ModBlocks.STARDUST_BRICK.get().defaultBlockState());
            set(level, bx, by + height, bz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
            set(level, bx, by + height + 1, bz, ModBlocks.MEMORY_MONUMENT_TOP.get().defaultBlockState());
        } else if (height >= 70) {
            set(level, bx, by + height, bz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        } else {
            set(level, bx, by + height, bz, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
        }
    }

    /** 齒輪鎮工廠棚 — 孢子金屬框架 + 茅草頂. */
    public static void buildGearShed(Level level, int cx, int cy, int cz) {
        BlockState floor = ModBlocks.STARDUST_BRICK.get().defaultBlockState();
        BlockState wall = ModBlocks.SPORE_METAL_BLOCK.get().defaultBlockState();
        BlockState roof = ModBlocks.THATCH_ROOF.get().defaultBlockState();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                set(level, cx + x, cy, cz + z, floor);
            }
        }
        for (int y = 1; y <= 4; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    boolean edge = Math.abs(x) == 3 || Math.abs(z) == 3;
                    if (edge) {
                        set(level, cx + x, cy + y, cz + z, wall);
                    }
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, cx + x, cy + 5, cz + z, roof);
            }
        }
        set(level, cx, cy + 1, cz + 3, Blocks.AIR.defaultBlockState());
        set(level, cx, cy + 2, cz + 3, Blocks.AIR.defaultBlockState());
        set(level, cx, cy + 6, cz, ModBlocks.NEON_MUSH_LAMP.get().defaultBlockState());
        set(level, cx - 2, cy + 1, cz - 2, ModBlocks.DISTILLER.get().defaultBlockState());
    }

    /** 月色小巷段落 — 月光石步道 + 燈柱 + 爪印玻璃欄杆. */
    public static void buildAlleySegment(Level level, BlockPos origin, int length, Direction along) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        int dx = along.getStepX();
        int dz = along.getStepZ();
        BlockState path = ModBlocks.MOONSTONE_BLOCK.get().defaultBlockState();
        BlockState rail = ModBlocks.PAWPRINT_GLASS.get().defaultBlockState();
        for (int i = 0; i < length; i++) {
            int x = ox + dx * i;
            int z = oz + dz * i;
            set(level, x, oy, z, path);
            set(level, x - dz, oy + 1, z + dx, rail);
            set(level, x + dz, oy + 1, z - dx, rail);
            if (i % 4 == 0) {
                set(level, x, oy + 1, z, ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
            }
            if (i % 6 == 3) {
                set(level, x - dz * 2, oy + 1, z + dx * 2, ModBlocks.NEON_MUSHROOM_POT.get().defaultBlockState());
            }
        }
    }

    /** 遺忘高塔尖塔 — 星塵磚塔身 + 暗影結晶裝飾. */
    public static void buildForgottenTowerSpire(Level level, BlockPos base, int height) {
        int bx = base.getX();
        int by = base.getY();
        int bz = base.getZ();
        BlockState wall = ModBlocks.STARDUST_BRICK.get().defaultBlockState();
        BlockState accent = ModBlocks.SHADOW_CRYSTAL_BLOCK.get().defaultBlockState();
        BlockState salt = ModBlocks.SALT_BLOCK.get().defaultBlockState();
        for (int y = 0; y < height; y++) {
            int r = y < height / 3 ? 3 : (y < height * 2 / 3 ? 2 : 1);
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (Math.abs(x) == r || Math.abs(z) == r) {
                        boolean crystal = (x + z + y) % 5 == 0 && y > 4;
                        set(level, bx + x, by + y, bz + z, crystal ? accent : wall);
                    }
                }
            }
            if (y == 0) {
                fillDisk(level, base, r + 1, salt);
            }
            if (y % 8 == 7) {
                for (int a = 0; a < 4; a++) {
                    double ang = a * Math.PI * 2 / 4;
                    set(level, bx + (int) (Math.cos(ang) * (r + 1)), by + y, bz + (int) (Math.sin(ang) * (r + 1)),
                            ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState());
                }
            }
        }
        set(level, bx, by + height, bz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        set(level, bx, by + height + 1, bz, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
    }

    /** 絨毛樹 — 原木樹幹 + 柔軟樹冠. */
    public static void buildVelvetTree(Level level, BlockPos base, int trunkHeight) {
        int bx = base.getX();
        int by = base.getY();
        int bz = base.getZ();
        BlockState log = ModBlocks.VELVET_TREE_LOG.get().defaultBlockState();
        BlockState leaf = ModBlocks.VELVET_TREE_LEAVES.get().defaultBlockState();
        for (int y = 0; y < trunkHeight; y++) {
            set(level, bx, by + y, bz, log);
        }
        for (int layer = 0; layer < 3; layer++) {
            int cy = by + trunkHeight - 1 + layer;
            int radius = 3 - layer;
            fillDisk(level, new BlockPos(bx, cy, bz), radius, leaf);
        }
        set(level, bx, by + trunkHeight + 2, bz, ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState());
    }

    /** 茅草小屋 — 編織羊毛牆 + 茅草頂（初啼村周邊）. */
    public static void buildThatchedCottage(Level level, BlockPos origin) {
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        BlockState wall = ModBlocks.WOVEN_WOOL.get().defaultBlockState();
        BlockState floor = ModBlocks.VELVET_PLANKS.get().defaultBlockState();
        BlockState roof = ModBlocks.THATCH_ROOF.get().defaultBlockState();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                set(level, ox + x, oy, oz + z, floor);
            }
        }
        for (int y = 1; y <= 3; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                        set(level, ox + x, oy + y, oz + z, wall);
                    }
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, ox + x, oy + 4, oz + z, roof);
            }
        }
        set(level, ox, oy + 1, oz + 3, Blocks.AIR.defaultBlockState());
        set(level, ox, oy + 2, oz + 3, Blocks.AIR.defaultBlockState());
        set(level, ox - 2, oy + 1, oz, ModBlocks.TOY_BOX.get().defaultBlockState());
        set(level, ox + 2, oy + 1, oz - 1, ModBlocks.CAT_BED.get().defaultBlockState());
        set(level, ox, oy + 1, oz - 2, ModBlocks.SCRATCHING_POST.get().defaultBlockState());
    }

    /** 三花子蘑菇小屋 — 霓虹菇頂 + 編織羊毛牆. */
    public static void buildMushroomCottage(Level level, int cx, int cy, int cz) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x * x + z * z <= 5) {
                    set(level, cx + x, cy + 4, cz + z, ModBlocks.NEON_MUSHROOM.get().defaultBlockState());
                }
            }
        }
        for (int y = 0; y <= 3; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    boolean edge = Math.abs(x) == 2 || Math.abs(z) == 2;
                    if (edge || y == 0) {
                        set(level, cx + x, cy + y, cz + z, ModBlocks.WOVEN_WOOL.get().defaultBlockState());
                    }
                }
            }
        }
        set(level, cx, cy + 1, cz + 2, Blocks.AIR.defaultBlockState());
        set(level, cx + 1, cy + 1, cz, ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        set(level, cx - 2, cy, cz + 2, ModBlocks.NEON_MUSHROOM_POT.get().defaultBlockState());
        set(level, cx + 2, cy, cz - 1, ModBlocks.TOY_BOX.get().defaultBlockState());
        set(level, cx - 1, cy, cz + 3, ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState());
    }

    public static void buildPortalGate(Level level, BlockPos pos, Direction facing, String style) {
        BlockState frame = switch (style) {
            case "moon" -> ModBlocks.MOONSTONE_BRICK.get().defaultBlockState();
            case "velvet" -> ModBlocks.VELVET_BLOCK.get().defaultBlockState();
            case "gear" -> Blocks.IRON_BLOCK.defaultBlockState();
            case "village" -> ModBlocks.WOVEN_WOOL.get().defaultBlockState();
            default -> ModBlocks.CAT_KINGDOM_PORTAL_FRAME.get().defaultBlockState();
        };
        int dx = facing.getStepX();
        int dz = facing.getStepZ();
        for (int h = 0; h < 4; h++) {
            set(level, pos.getX() - dz, pos.getY() + h, pos.getZ() + dx, frame);
            set(level, pos.getX() + dz * 2, pos.getY() + h, pos.getZ() - dx * 2, frame);
        }
        for (int w = -1; w <= 2; w++) {
            set(level, pos.getX() + dx * w - dz, pos.getY(), pos.getZ() + dz * w + dx, frame);
            set(level, pos.getX() + dx * w - dz, pos.getY() + 3, pos.getZ() + dz * w + dx, frame);
        }
        set(level, pos.getX(), pos.getY() + 1, pos.getZ(), ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
    }

    public static void buildYarnHouse(Level level, int cx, int cy, int cz, MapColor color) {
        BlockState wall = ModBlocks.WOVEN_WOOL.get().defaultBlockState();
        BlockState floor = ModBlocks.VELVET_PLANKS.get().defaultBlockState();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                set(level, cx + x, cy, cz + z, floor);
            }
        }
        for (int y = 1; y <= 3; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                        set(level, cx + x, cy + y, cz + z, wall);
                    }
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                set(level, cx + x, cy + 4, cz + z, ModBlocks.THATCH_ROOF.get().defaultBlockState());
            }
        }
        set(level, cx, cy + 1, cz + 2, Blocks.AIR.defaultBlockState());
        set(level, cx, cy + 2, cz + 2, Blocks.AIR.defaultBlockState());
        set(level, cx, cy + 1, cz, ModBlocks.SCRATCHING_POST.get().defaultBlockState());
        set(level, cx + 1, cy + 1, cz - 1, ModBlocks.CAT_CLIMB_PLATFORM.get().defaultBlockState());
    }

    public static void buildRopeBridge(Level level, BlockPos from, BlockPos to) {
        int steps = Math.max(Math.abs(to.getX() - from.getX()), Math.abs(to.getZ() - from.getZ()));
        for (int i = 0; i <= steps; i++) {
            double t = steps == 0 ? 0 : (double) i / steps;
            int x = (int) MthLerp(t, from.getX(), to.getX());
            int z = (int) MthLerp(t, from.getZ(), to.getZ());
            set(level, x, from.getY(), z, Blocks.OAK_PLANKS.defaultBlockState());
            set(level, x, from.getY() - 1, z, Blocks.CHAIN.defaultBlockState());
            if (i % 3 == 0) {
                set(level, x, from.getY() + 1, z, ModBlocks.YARN_BALL_LAMP.get().defaultBlockState());
            }
        }
    }

    /** 阿爾法觀測台 — 半透明浮空平台（設計書 3.3）. */
    public static void buildAlphaObservatory(ServerLevel level, BlockPos base) {
        int bx = base.getX();
        int by = base.getY();
        int bz = base.getZ();
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x * x + z * z <= 16) {
                    set(level, bx + x, by, bz + z, ModBlocks.MOONSTONE_BRICK.get().defaultBlockState());
                }
            }
        }
        set(level, bx, by + 1, bz, ModBlocks.PURR_CRYSTAL_BLOCK.get().defaultBlockState());
        for (int y = 1; y <= 8; y++) {
            set(level, bx, by - y, bz, ModBlocks.MOONSTONE_LAMP_POST.get().defaultBlockState());
        }
        set(level, bx, by - 9, bz, ModBlocks.CAT_KINGDOM_PORTAL.get().defaultBlockState());
    }

    private static double MthLerp(double t, int a, int b) {
        return a + (b - a) * t;
    }
}
