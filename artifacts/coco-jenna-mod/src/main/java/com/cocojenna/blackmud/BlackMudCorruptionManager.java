package com.cocojenna.blackmud;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModEntities;
import com.cocojenna.reputation.ReputationHelper;
import com.cocojenna.entity.BlackMudBossEntity;
import com.cocojenna.world.BlindPortGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/** 黑泥區塊腐化、蔓延、怪物生成與淨化. */
public final class BlackMudCorruptionManager {

    private static final int SPREAD_INTERVAL = 72000; // 3 遊戲日
    private static final int SPREAD_BATCH_SIZE = 32;
    private static final int PLAYER_CHUNK_RADIUS = 3;

    private BlackMudCorruptionManager() {}

    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;

        long now = level.getGameTime();
        BlackMudSavedData data = BlackMudSavedData.get(level);
        if (data.isAfterRain()) return;

        if (!data.isInitialSeeded()) {
            seedInitialCorruption(level, data);
            data.setInitialSeeded(true);
        }

        if (!data.isSpreadCycleActive() && now - data.lastSpreadTick() >= spreadInterval(level)) {
            data.beginSpreadCycle();
        }
        if (data.isSpreadCycleActive()) {
            boolean finished = spreadBatch(level, data, level.random);
            if (finished) {
                data.endSpreadCycle();
                data.setLastSpreadTick(now);
            }
        }

        if (now % 200 == 0) {
            trySpawnMobs(level, data, level.random);
        }

        tickBlindWaterRain(level, data, now);

        if (now % 80 == 0) {
            tickCorruptionVisuals(level, data);
        }
        com.cocojenna.world.MonumentGrowthManager.tickPrimalCoreAura(level);
    }

    private static void tickCorruptionVisuals(ServerLevel level, BlackMudSavedData data) {
        if (level.players().isEmpty()) return;
        ServerPlayer anchor = level.players().get(level.random.nextInt(level.players().size()));
        ChunkPos center = anchor.chunkPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);
                if (!isChunkNearPlayers(level, cp, PLAYER_CHUNK_RADIUS)) continue;
                int stage = data.getStage(cp);
                if (stage <= 0) continue;
                int x = cp.getMinBlockX() + level.random.nextInt(16);
                int z = cp.getMinBlockZ() + level.random.nextInt(16);
                BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
                var particle = switch (stage) {
                    case 1 -> ParticleTypes.ASH;
                    case 2 -> ParticleTypes.SMOKE;
                    case 3 -> ParticleTypes.SQUID_INK;
                    case 4 -> ParticleTypes.SOUL_FIRE_FLAME;
                    case 5 -> ParticleTypes.REVERSE_PORTAL;
                    default -> ParticleTypes.WITCH;
                };
                int count = stage >= 4 ? 8 : 2;
                level.sendParticles(particle,
                        surface.getX() + 0.5, surface.getY() + 0.3, surface.getZ() + 0.5,
                        count, 0.4, 0.2, 0.4, 0.01);
            }
        }
    }

    /** 玩家腐蝕印記與黑泥階段升級（每 20 tick 於玩家 tick 呼叫）. */
    public static void tickPlayerCorrosion(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;

        int stage = stageAt(player.level(), player.blockPosition());
        BlackMudSavedData data = BlackMudSavedData.get(player.serverLevel());
        boolean blindRain = data.isBlindWaterRainActive(player.level().getGameTime());
        boolean inBlindPort = player.blockPosition().distSqr(BlindPortGenerator.CENTER) < 128 * 128;

        if (stage <= 0 && !blindRain) {
            return;
        }

        int exposure = stage + (blindRain && inBlindPort ? 2 : 0);
        MobEffectInstance mark = player.getEffect(ModEffects.CORROSION_MARK.get());
        int amp = mark != null ? mark.getAmplifier() : -1;
        int nextAmp = Math.min(4, Math.max(amp, exposure - 1));

        if (nextAmp >= 0) {
            player.addEffect(new MobEffectInstance(ModEffects.CORROSION_MARK.get(),
                    200, nextAmp, false, true, true));
        }

        if (blindRain && inBlindPort && player.level().getGameTime() % 60 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, true));
        }

        if (player.level().getGameTime() % 100 != 0) return;

        if (nextAmp >= 3 && !player.hasEffect(ModEffects.BLACK_MUD_STAGE1.get())) {
            player.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE1.get(), 600, 0));
        }
        if (nextAmp >= 4 && !player.hasEffect(ModEffects.BLACK_MUD_STAGE2.get())) {
            player.removeEffect(ModEffects.BLACK_MUD_STAGE1.get());
            player.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE2.get(), 600, 0));
        }
        if (nextAmp >= 4 && stage >= 3 && !player.hasEffect(ModEffects.BLACK_MUD_STAGE3.get())
                && player.getRandom().nextFloat() < 0.15f) {
            player.removeEffect(ModEffects.BLACK_MUD_STAGE2.get());
            player.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE3.get(), 400, 0));
        }
        if (nextAmp >= 4 && stage >= 4 && player.getRandom().nextFloat() < 0.08f) {
            player.removeEffect(ModEffects.BLACK_MUD_STAGE3.get());
            player.addEffect(new MobEffectInstance(ModEffects.BLACK_MUD_STAGE4.get(), 300, 0));
        }
    }

    private static void tickBlindWaterRain(ServerLevel level, BlackMudSavedData data, long now) {
        boolean active = data.isBlindWaterRainActive(now);
        if (!active && now % 12000 == 0 && level.random.nextFloat() < 0.35f) {
            data.setBlindWaterRainUntil(now + 6000);
            for (ServerPlayer p : level.players()) {
                if (p.blockPosition().distSqr(BlindPortGenerator.CENTER) < 160 * 160) {
                    p.displayClientMessage(Component.translatable("weather.cocojenna.blind_rain_start"), true);
                }
            }
            active = true;
        }
        if (active) {
            level.setWeatherParameters(0, 6000, true, level.isThundering());
            if (now >= data.blindWaterRainUntil()) {
                data.setBlindWaterRainUntil(0);
                level.setWeatherParameters(6000, 0, false, false);
            }
        }
    }

    private static int spreadInterval(ServerLevel level) {
        boolean fullMoon = level.getMoonPhase() == 0;
        int base = fullMoon ? SPREAD_INTERVAL / 2 : SPREAD_INTERVAL;
        float mult = com.cocojenna.gamble.BlackjackGambleManager.blackMudSpreadMultiplier(level);
        mult *= com.cocojenna.endgame.KingdomDecreeWorldEffects.blackMudSpreadMultiplier(level);
        float owMult = 1.0f;
        for (ServerPlayer p : level.players()) {
            owMult = Math.min(owMult, com.cocojenna.overworld.OverworldPenetrationManager
                    .blackMudSpreadMultiplierFromOverworld(com.cocojenna.capability.ModCapabilities.getOrDefault(p)));
        }
        mult /= owMult;
        return Math.max(6000, (int) (base / mult));
    }

    public static void onBlackjackFateResolved(ServerLevel level, boolean playerWon) {
        if (playerWon) {
            level.setWeatherParameters(6000, 0, false, false);
            for (ServerPlayer p : level.players()) {
                p.displayClientMessage(Component.translatable("blackjack.cocojenna.fate_blessing"), true);
            }
        } else {
            level.setDayTime(18000);
            level.setWeatherParameters(0, 72000, false, true);
            for (ServerPlayer p : level.players()) {
                p.displayClientMessage(Component.translatable("blackjack.cocojenna.fate_curse"), true);
            }
        }
    }

    private static void seedInitialCorruption(ServerLevel level, BlackMudSavedData data) {
        ChunkPos port = new ChunkPos(BlindPortGenerator.CENTER);
        data.setStage(port, 1);
        data.setStage(new ChunkPos(port.x + 1, port.z), 1);
        data.setStage(new ChunkPos(port.x, port.z + 1), 2);
    }

    private static boolean spreadBatch(ServerLevel level, BlackMudSavedData data, RandomSource random) {
        long[] keys = data.stageKeysSnapshot();
        if (keys.length == 0) return true;

        int cursor = data.spreadBatchCursor();
        int processed = 0;
        while (processed < SPREAD_BATCH_SIZE && cursor < keys.length) {
            int stage = data.stages().get(keys[cursor]) & 0xFF;
            if (stage >= 2) {
                ChunkPos origin = new ChunkPos(keys[cursor]);
                spreadFromOrigin(level, data, random, origin, stage);
            }
            cursor++;
            processed++;
        }
        data.setSpreadBatchCursor(cursor);
        return cursor >= keys.length;
    }

    private static void spreadFromOrigin(ServerLevel level, BlackMudSavedData data, RandomSource random,
            ChunkPos origin, int stage) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (random.nextFloat() > 0.35f) continue;
                ChunkPos neighbor = new ChunkPos(origin.x + dx, origin.z + dz);
                if (data.isProtected(neighbor)) continue;
                if (!isChunkNearPlayers(level, neighbor, PLAYER_CHUNK_RADIUS + 2)) continue;
                int current = data.getStage(neighbor);
                if (current < stage - 1 && current < 4) {
                    data.setStage(neighbor, current + 1);
                    corruptSurfaceInChunk(level, neighbor, random);
                }
            }
        }
    }

    private static boolean isChunkNearPlayers(ServerLevel level, ChunkPos cp, int chunkRadius) {
        for (ServerPlayer player : level.players()) {
            ChunkPos playerChunk = player.chunkPosition();
            if (Math.abs(playerChunk.x - cp.x) <= chunkRadius
                    && Math.abs(playerChunk.z - cp.z) <= chunkRadius) {
                return true;
            }
        }
        return false;
    }

    private static void corruptSurfaceInChunk(ServerLevel level, ChunkPos chunk, RandomSource random) {
        BlackMudSavedData data = BlackMudSavedData.get(level);
        int chunkStage = data.getStage(chunk);
        var mudState = BlackMudBlocks.blockStateForStage(Math.max(2, chunkStage));
        int baseX = chunk.getMinBlockX();
        int baseZ = chunk.getMinBlockZ();
        for (int i = 0; i < 6 + chunkStage; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
            BlockState below = level.getBlockState(surface.below());
            if (below.is(BlockTags.DIRT) || below.is(Blocks.GRASS_BLOCK)) {
                level.setBlock(surface.below(), mudState, 2);
            }
        }
    }

    private static void trySpawnMobs(ServerLevel level, BlackMudSavedData data, RandomSource random) {
        if (level.players().isEmpty()) return;
        if (data.isAfterRain()) return;

        int nearbyWisps = level.getEntitiesOfClass(
                com.cocojenna.entity.ForgottenWispEntity.class,
                level.players().get(0).getBoundingBox().inflate(96)).size();
        if (nearbyWisps >= 4) return;

        ServerPlayer anchor = level.players().get(random.nextInt(level.players().size()));
        if (com.cocojenna.gamble.BlackjackGambleManager.isPolarNightActive(level)
                && random.nextFloat() < 0.12f) {
            spawnForStage(level, anchor, 4, random);
        }
        if (random.nextFloat() < 0.008f) {
            trySpawnNativeMob(level, anchor, ModEntities.GLITCH_CAT.get(), random);
        }
        if (random.nextFloat() < 0.006f) {
            trySpawnNativeMob(level, anchor, ModEntities.ORIGAMI_CROW.get(), random);
        }
        ChunkPos center = anchor.chunkPosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);
                if (!isChunkNearPlayers(level, cp, PLAYER_CHUNK_RADIUS)) continue;
                int stage = data.getStage(cp);
                float chance = 0.025f;
                if (BlackMudEcology.isSeaOfSorrow(level, anchor)) chance = 0.06f;
                if (stage < 2 || random.nextFloat() > chance) continue;
                spawnForStage(level, anchor, stage, random);
            }
        }
    }

    private static void trySpawnNativeMob(ServerLevel level, ServerPlayer player,
            net.minecraft.world.entity.EntityType<?> type, RandomSource random) {
        BlockPos pos = findSpawnPos(level, player.blockPosition(), random);
        if (pos == null) return;
        var mob = type.create(level);
        if (mob == null) return;
        mob.setPos(pos.getX() + 0.5, pos.getY() + (type == ModEntities.ORIGAMI_CROW.get() ? 6 : 0), pos.getZ() + 0.5);
        level.addFreshEntity(mob);
    }

    private static void spawnForStage(ServerLevel level, ServerPlayer player, int stage, RandomSource random) {
        BlockPos pos = findSpawnPos(level, player.blockPosition(), random);
        if (pos == null) return;

        var type = switch (stage) {
            case 2 -> ModEntities.HEAT_LEECH.get();
            case 3 -> random.nextFloat() < 0.22f
                    ? ModEntities.FORGOTTEN_WISP.get()
                    : ModEntities.HEAT_LEECH.get();
            case 4 -> random.nextFloat() < 0.5f
                    ? ModEntities.WHISPERING_DOLL.get()
                    : ModEntities.MEMORY_MOTH.get();
            default -> ModEntities.MIMIC_CAT.get();
        };

        var mob = type.create(level);
        if (mob == null) return;
        mob.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (mob.checkSpawnRules(level, MobSpawnType.EVENT)) {
            level.addFreshEntity(mob);
        }
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos near, RandomSource random) {
        for (int i = 0; i < 10; i++) {
            int x = near.getX() + random.nextInt(20) - 10;
            int z = near.getZ() + random.nextInt(20) - 10;
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            if (isRoadSurface(level, surface.below())) continue;
            if (level.getBlockState(surface.below()).is(ModBlocks.BLACK_MUD.get())
                    || BlackMudBlocks.isBlackMud(level.getBlockState(surface.below()))
                    || level.getBlockState(surface).isAir()) {
                return surface;
            }
        }
        return null;
    }

    /** 月色小巷等道路不刷遺忘之影，避免「路上影太多」. */
    private static boolean isRoadSurface(ServerLevel level, BlockPos ground) {
        BlockState state = level.getBlockState(ground);
        return state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.STONE_BRICKS)
                || state.is(Blocks.SMOOTH_STONE)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.DEEPSLATE_BRICKS);
    }

    public static void purifyRegion(ServerLevel level, BlockPos center, int radius, ServerPlayer player) {
        BlackMudSavedData data = BlackMudSavedData.get(level);
        int chunkRadius = (radius + 15) / 16;
        ChunkPos centerChunk = new ChunkPos(center);
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                data.setStage(new ChunkPos(centerChunk.x + dx, centerChunk.z + dz), 0);
            }
        }

        int r2 = radius * radius;
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                if ((x - center.getX()) * (x - center.getX()) + (z - center.getZ()) * (z - center.getZ()) > r2) {
                    continue;
                }
                for (int y = center.getY() - 8; y <= center.getY() + 4; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (BlackMudBlocks.isBlackMud(level.getBlockState(p))) {
                        level.setBlock(p, ModBlocks.STARDUST_SOIL.get().defaultBlockState(), 2);
                    }
                }
            }
        }

        if (player != null) {
            ReputationHelper.addRep(player, "blind_port", 8);
            player.displayClientMessage(Component.translatable("blackmud.cocojenna.purified"), true);
        }
    }

    public static int stageAt(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server)) return 0;
        return BlackMudSavedData.get(server).getStage(new ChunkPos(pos));
    }

    public static void onRegionalBossDefeated(ServerLevel level, BlackMudBossEntity.BossKind kind) {
        BlackMudSavedData.get(level).markBossDefeated(kind.id);
    }

    public static boolean isBossAlive(ServerLevel level, String bossId) {
        return !BlackMudSavedData.get(level).isBossDefeated(bossId);
    }
}
