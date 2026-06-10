package com.cocojenna.overworld;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** 主世界滲透：痕跡生成、動態事件、雙向影響. */
public final class OverworldPenetrationManager {

    private static final int TRACE_CHUNK_CHANCE = 5;

    private OverworldPenetrationManager() {}

    public static void onPlayerLogin(ServerPlayer player) {
        if (!player.serverLevel().dimension().equals(Level.OVERWORLD)) return;
        GrayWhiskerHutGenerator.ensureHut(player.serverLevel(), player.blockPosition());
        OverworldRuinGenerator.ensureStarterOutpost(player.serverLevel(), player.blockPosition());
    }

    public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
        long key = chunk.getPos().toLong();
        if (data.isChunkSeeded(key)) return;

        RandomSource random = RandomSource.create(level.getSeed() ^ key);
        if (random.nextInt(100) >= TRACE_CHUNK_CHANCE) {
            data.markChunkSeeded(key);
            return;
        }

        int x = chunk.getPos().getMinBlockX() + random.nextInt(16);
        int z = chunk.getPos().getMinBlockZ() + random.nextInt(16);
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        OverworldTraceType type = OverworldTraceType.roll(random);
        placeTrace(level, data, pos, type);
        data.markChunkSeeded(key);

        BlockPos hint = new BlockPos(chunk.getPos().getMinBlockX() + 8, 64, chunk.getPos().getMinBlockZ() + 8);
        OverworldRuinGenerator.trySeedChunkRuin(level, key, hint);
    }

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;
        long time = player.level().getGameTime();
        if (time % 36000 == 0 && player.getRandom().nextFloat() < 0.15f) {
            SmugglerCaravanManager.trySpawn(player);
        }
        if (time % 12000 == 0 && player.getRandom().nextFloat() < 0.08f) {
            trySpawnOverworldCat(player);
        }
        if (time % 200 == 0) {
            tickTraceParticles(player);
        }
        if (time % 100 == 0) {
            SmugglerCaravanManager.tickEscort(player);
        }
        if (time % 1200 == 0) {
            BlackMudLeakManager.tickPlayer(player);
        }
        if (!player.level().isDay() && time % 600 == 0) {
            trySpawnWanderingSludge(player);
        }
        if (time % 100 == 0) {
            MoonResonanceManager.tickNearby(player);
            FusionBuildingManager.tickAura(player);
            StrayCatGatheringManager.onPlayerObserve(player);
            OverworldRuinCorrosionManager.tickPlayer(player);
        }
        if (time % 200 == 0) {
            MoonResonanceManager.tickPlayer(player);
            StrayCatGatheringManager.tickPlayer(player);
            FusionBuildingManager.tickPlayer(player);
        }
    }

    public static boolean tryInteractTrace(ServerPlayer player, BlockPos pos) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return false;
        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        OverworldTraceType type = data.getTrace(pos);
        if (type == null) return false;

        switch (type) {
            case MOON_PAW -> {
                PenetrationQuestManager.onMoonPawInteract(player);
                OverworldDungeonQuestManager.onTraceFound(player);
            }
            case BLACK_MUD_RESIDUE -> {
                if (player.getMainHandItem().is(Items.GLASS_BOTTLE)) {
                    if (!player.getAbilities().instabuild) player.getMainHandItem().shrink(1);
                    player.getInventory().add(new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()));
                    player.displayClientMessage(Component.translatable("penetration.cocojenna.mud_sample"), true);
                    data.removeTrace(pos);
                    player.serverLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                } else {
                    player.displayClientMessage(Component.translatable("penetration.cocojenna.need_bottle"), true);
                }
            }
            case FORGOTTEN_TOY -> {
                dropOrGive(player, new ItemStack(ModItems.RAINBOW_YARN_BALL.get()));
                ModCapabilities.getOrDefault(player).modifyCocoEmotion(2f);
                ModCapabilities.getOrDefault(player).modifyJennaEmotion(2f);
                data.removeTrace(pos);
                player.serverLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
            case CAT_GRAFFITI -> {
                PenetrationQuestManager.onGraffitiRead(player);
                OverworldDungeonQuestManager.onClueCollected(player);
            }
            case MOONSTONE_SHARD -> {
                dropOrGive(player, new ItemStack(ModItems.MOONSTONE.get()));
                data.removeTrace(pos);
                player.serverLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        return true;
    }

    public static boolean tryInteractRuin(ServerPlayer player, BlockPos pos) {
        if (!player.level().dimension().equals(Level.OVERWORLD)) return false;
        if (BlackMudLeakManager.trySealLeak(player, pos)) return true;

        OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(player.serverLevel());
        OverworldRuinType ruin = data.findRuinNear(pos, 8);
        if (ruin == null) return false;

        if (ruin == OverworldRuinType.MOON_SEAL
                && player.getMainHandItem().is(ModItems.MOON_CORE.get())) {
            if (com.cocojenna.overworld.MoonCoreManager.tryActivate(player, true)) {
                if (!player.getAbilities().instabuild) {
                    player.getMainHandItem().shrink(1);
                }
            }
            return true;
        }
        if (ruin == OverworldRuinType.FORGOTTEN_ALTAR
                && player.getMainHandItem().is(ModItems.MEMORY_SHARD.get())) {
            if (!player.getAbilities().instabuild) player.getMainHandItem().shrink(1);
            dropOrGive(player, new ItemStack(ModItems.MOONSTONE.get(), 1 + player.getRandom().nextInt(2)));
            dropOrGive(player, new ItemStack(ModItems.CATNIP_ITEM.get(), 2));
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.NIGHT_VISION, 6000, 0, false, true, true));
            ModCapabilities.getOrDefault(player).addOverworldInfluence(3);
            player.displayClientMessage(Component.translatable("penetration.cocojenna.altar_blessing"), true);
            OverworldDungeonQuestManager.onDungeonCleared(player);
            OverworldDungeonQuestManager.onTruthRevealed(player);
            return true;
        }
        return false;
    }

    public static void onOverworldCatInteract(ServerPlayer player, OverworldCatNpcEntity npc, InteractionHand hand) {
        BondData bond = ModCapabilities.getOrDefault(player);
        ItemStack held = player.getItemInHand(hand);

        if (held.is(ModItems.CATNIP_ITEM.get()) || held.is(Items.COD) || held.is(Items.SALMON)) {
            if (!player.getAbilities().instabuild) held.shrink(1);
            npc.addNpcFavor(3);
            bond.addOverworldInfluence(1);
        }

        if (npc.getRole() == OverworldCatNpcEntity.Role.SMUGGLER
                && npc.getPersistentData().getBoolean("cocojenna_caravan")) {
            SmugglerCaravanManager.tryStartEscort(player, npc);
        }

        switch (npc.getRole()) {
            case SMUGGLER -> {
                if (npc.getNpcFavor() >= 30) {
                    player.displayClientMessage(Component.translatable("penetration.cocojenna.smuggler_trade"), true);
                    dropOrGive(player, new ItemStack(ModItems.BLACK_MUD_SAMPLE.get()));
                } else {
                    player.displayClientMessage(Component.translatable("penetration.cocojenna.smuggler_greet"), true);
                }
            }
            case POET -> {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.LUCK, 3600, 0, false, true, true));
                player.displayClientMessage(Component.translatable("penetration.cocojenna.poet_buff"), true);
            }
            case SCOUT -> player.displayClientMessage(Component.translatable("penetration.cocojenna.scout_report"), true);
            case LOST_KITTEN -> {
                if (com.cocojenna.kingdom.KingdomStrayCatManager.isKingdomStray(npc)) {
                    com.cocojenna.kingdom.KingdomStrayCatManager.onReturnedHome(player, npc);
                } else {
                    bond.addOverworldInfluence(5);
                    dropOrGive(player, new ItemStack(Items.POPPY));
                    player.displayClientMessage(Component.translatable("penetration.cocojenna.kitten_thanks"), true);
                }
                npc.addNpcFavor(10);
                npc.discard();
            }
            case VETERAN -> player.displayClientMessage(Component.translatable("penetration.cocojenna.veteran_story"), true);
            case ARCHAEOLOGIST -> {
                if (bond.getMemoryShardsTotal() < 8) {
                    dropOrGive(player, new ItemStack(ModItems.MEMORY_SHARD.get()));
                }
                OverworldRuinType ruin = OverworldPenetrationSavedData.get(player.serverLevel())
                        .findRuinNear(player.blockPosition(), 400);
                if (ruin != null) {
                    player.displayClientMessage(Component.translatable(
                            "penetration.cocojenna.archaeologist_ruin." + ruin.name().toLowerCase()), true);
                } else {
                    player.displayClientMessage(Component.translatable("penetration.cocojenna.archaeologist_map"), true);
                }
            }
            case BARKEEP -> {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.NIGHT_VISION, 3600, 0, false, true, true));
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 1800, 0, false, true, true));
                dropOrGive(player, new ItemStack(ModItems.CATNIP_ITEM.get(), 2));
                player.displayClientMessage(Component.translatable("penetration.cocojenna.barkeep_drink"), true);
            }
            case WHISPERER -> {
                dropOrGive(player, RuinMapFragmentHelper.typedStack(
                        RuinMapFragmentType.random(player.getRandom()), 1));
                dropOrGive(player, new ItemStack(ModItems.MEMORY_SHARD.get()));
                player.displayClientMessage(Component.translatable("penetration.cocojenna.whisperer_intel"), true);
            }
        }
        com.cocojenna.society.CatSocietyManager.onNpcInteract(player, npc);
        npc.addNpcFavor(1);
    }

    /** 主世界行動影響貓之國黑泥擴散（雙向影響）. */
    public static float blackMudSpreadMultiplierFromOverworld(BondData bond) {
        int influence = bond.getOverworldInfluence();
        if (influence >= 50) return 0.85f;
        if (influence >= 25) return 0.92f;
        return 1.0f;
    }

    private static void placeTrace(ServerLevel level, OverworldPenetrationSavedData data,
            BlockPos pos, OverworldTraceType type) {
        data.putTrace(pos, type);
        switch (type) {
            case MOON_PAW -> level.setBlock(pos, ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState(), 2);
            case BLACK_MUD_RESIDUE -> level.setBlock(pos, ModBlocks.BLACK_MUD.get().defaultBlockState(), 2);
            case FORGOTTEN_TOY -> level.setBlock(pos, Blocks.RED_WOOL.defaultBlockState(), 2);
            case CAT_GRAFFITI -> level.setBlock(pos, Blocks.GLOW_LICHEN.defaultBlockState(), 2);
            case MOONSTONE_SHARD -> level.setBlock(pos, ModBlocks.MOONSTONE_CLUSTER.get().defaultBlockState(), 2);
        }
    }

    private static void tickTraceParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        ChunkPos cp = player.chunkPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                ChunkPos near = new ChunkPos(cp.x + dx, cp.z + dz);
                BlockPos base = new BlockPos(near.getMinBlockX() + 8, 64, near.getMinBlockZ() + 8);
                OverworldPenetrationSavedData data = OverworldPenetrationSavedData.get(level);
                for (int i = 0; i < 3; i++) {
                    BlockPos p = base.offset(level.random.nextInt(16) - 8, 0, level.random.nextInt(16) - 8);
                    p = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, p);
                    if (data.getTrace(p) == OverworldTraceType.MOON_PAW
                            && level.getGameTime() % 40 == 0) {
                        level.sendParticles(ParticleTypes.END_ROD,
                                p.getX() + 0.5, p.getY() + 0.2, p.getZ() + 0.5,
                                1, 0.1, 0.05, 0.1, 0.01);
                    }
                }
            }
        }
    }

    private static void trySpawnOverworldCat(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        RandomSource random = level.random;
        BlockPos pos = player.blockPosition().offset(
                random.nextInt(32) - 16, 0, random.nextInt(32) - 16);
        pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
        var entity = ModEntities.OVERWORLD_CAT.get().create(level);
        if (entity == null) return;
        OverworldCatNpcEntity.Role[] roles = OverworldCatNpcEntity.Role.values();
        entity.setRole(roles[random.nextInt(roles.length)]);
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(entity);
    }

    private static void trySpawnWanderingSludge(ServerPlayer player) {
        if (player.getRandom().nextFloat() > 0.12f) return;
        ServerLevel level = player.serverLevel();
        if (level.getMaxLocalRawBrightness(player.blockPosition()) > 7) return;
        BlockPos pos = player.blockPosition().offset(
                player.getRandom().nextInt(24) - 12, 0, player.getRandom().nextInt(24) - 12);
        pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
        var mob = ModEntities.WANDERING_SLUDGE.get().create(level);
        if (mob == null) return;
        mob.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(mob);
    }

    private static void dropOrGive(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.level().addFreshEntity(new ItemEntity(player.level(),
                    player.getX(), player.getY(), player.getZ(), stack));
        }
    }
}
