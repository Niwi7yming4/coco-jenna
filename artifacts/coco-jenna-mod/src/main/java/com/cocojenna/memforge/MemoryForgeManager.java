package com.cocojenna.memforge;

import com.cocojenna.memforge.MemoryForgeRitual.Phase;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import com.cocojenna.network.MemoryForgeHudPacket;
import com.cocojenna.network.ModNetwork;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class MemoryForgeManager {

    public static final int AWAKEN_TICKS = 30 * 20;
    public static final int DEFEND_TICKS = 60 * 20;
    public static final int INJECT_TICKS = 10 * 20;
    private static final Map<BlockPos, ServerLevel> ALTAR_LEVELS = new HashMap<>();

    private MemoryForgeManager() {}

    @Nullable
    public static ServerLevel levelFor(BlockPos altar) {
        return ALTAR_LEVELS.get(altar);
    }

    public static boolean tryStart(ServerPlayer player, BlockPos enchantPos, ItemStack held) {
        ServerLevel level = player.serverLevel();
        if (!MemoryForgeStructure.isValidAltar(level, enchantPos)) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.invalid_structure")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        MemoryForgeRecipe recipe = MemoryForgeRecipe.forCore(held.getItem());
        if (recipe == null) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.unknown_core")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (held.getCount() < recipe.coreCount) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.not_enough_core")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        MemoryForgeSavedData data = MemoryForgeSavedData.get(level);
        if (data.get(enchantPos) != null) {
            return false;
        }

        held.shrink(recipe.coreCount);
        BlockPos core = MemoryForgeStructure.findCore(level, enchantPos).orElse(enchantPos.below(2));
        MemoryForgeRitual ritual = new MemoryForgeRitual(enchantPos, core, player.getUUID(), recipe,
                level.getGameTime());
        ritual.initBlockHp(level);
        data.add(ritual);
        ALTAR_LEVELS.put(enchantPos, level);

        level.playSound(null, enchantPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 0.6f);
        player.displayClientMessage(Component.translatable("ritual.cocojenna.started",
                Component.translatable("item.cocojenna." + recipe.id)), true);
        syncForgeHud(player, ritual, level.getGameTime());
        return true;
    }

    private static void syncForgeHud(@Nullable ServerPlayer player, MemoryForgeRitual ritual, long now) {
        if (player == null) return;
        int remaining = (int) Math.max(0, ritual.phaseEndTick() - now);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new MemoryForgeHudPacket(ritual.phase().ordinal(), remaining, ritual.altarHpRatio()));
    }

    private static void clearForgeHud(@Nullable ServerPlayer player) {
        if (player == null) return;
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new MemoryForgeHudPacket(-1, 0, 0f));
    }

    public static boolean tryInjectCatalyst(ServerPlayer player, BlockPos enchantPos, ItemStack held) {
        ServerLevel level = player.serverLevel();
        MemoryForgeSavedData data = MemoryForgeSavedData.get(level);
        MemoryForgeRitual ritual = data.get(enchantPos);
        if (ritual == null || ritual.phase() != Phase.INJECT) return false;

        MemoryForgeRecipe recipe = ritual.recipe();
        if (!held.is(recipe.catalystItem.get()) || held.getCount() < recipe.catalystCount) {
            failWrongCatalyst(player, ritual, data);
            return true;
        }
        held.shrink(recipe.catalystCount);
        ritual.markCatalystInjected();
        ritual.setPhase(Phase.RESONANCE, level.getGameTime());
        data.setDirty();
        completeRitual(player, ritual, data);
        return true;
    }

    public static void tick(ServerLevel level) {
        MemoryForgeSavedData data = MemoryForgeSavedData.get(level);
        if (data.rituals().isEmpty()) return;

        long now = level.getGameTime();
        var copy = new HashMap<>(data.rituals());
        for (MemoryForgeRitual ritual : copy.values()) {
            ALTAR_LEVELS.putIfAbsent(ritual.altarPos(), level);
            if (ritual.blockHp().isEmpty()) {
                ritual.initBlockHp(level);
            }
            if (ritual.isCoreDestroyed() || !level.getBlockState(ritual.corePos()).is(Blocks.DIAMOND_BLOCK)) {
                failBrokenCore(ritual, data);
                continue;
            }
            Phase phase = ritual.phase();
            if (phase == Phase.AWAKEN) {
                tickAwaken(level, ritual, data, now);
            } else if (phase == Phase.DEFEND) {
                tickDefend(level, ritual, data, now);
            } else if (phase == Phase.INJECT) {
                tickInject(level, ritual, data, now);
            }
            if (now % 10 == 0) {
                syncForgeHud(ritual.player(), ritual, now);
            }
        }
    }

    private static void tickAwaken(ServerLevel level, MemoryForgeRitual ritual,
                                   MemoryForgeSavedData data, long now) {
        if (now % 5 == 0) {
            level.sendParticles(ParticleTypes.END_ROD,
                    ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + 1.2,
                    ritual.altarPos().getZ() + 0.5, 4, 1.2, 0.6, 1.2, 0.01);
        }
        if (now >= ritual.phaseEndTick()) {
            ritual.setPhase(Phase.DEFEND, now + DEFEND_TICKS);
            data.setDirty();
            ServerPlayer player = ritual.player();
            if (player != null) {
                player.displayClientMessage(Component.translatable("ritual.cocojenna.defend_now")
                        .withStyle(ChatFormatting.GOLD), true);
            }
        }
    }

    private static void tickDefend(ServerLevel level, MemoryForgeRitual ritual,
                                  MemoryForgeSavedData data, long now) {
        if (now >= ritual.phaseEndTick()) {
            ritual.setPhase(Phase.INJECT, now + INJECT_TICKS);
            data.setDirty();
            ServerPlayer player = ritual.player();
            if (player != null) {
                player.displayClientMessage(Component.translatable("ritual.cocojenna.inject_now")
                        .withStyle(ChatFormatting.GOLD), true);
            }
            return;
        }
        if (now % 40 == 0) {
            spawnDefender(level, ritual);
        }
        if (now % 20 == 0) {
            tickAltarDamage(level, ritual);
        }
        if (now % 10 == 0) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + 1.0,
                    ritual.altarPos().getZ() + 0.5, 8, 1.5, 0.5, 1.5, 0.01);
        }
    }

    private static void tickInject(ServerLevel level, MemoryForgeRitual ritual,
                                   MemoryForgeSavedData data, long now) {
        if (now >= ritual.phaseEndTick() && !ritual.catalystInjected()) {
            ritual.markPenaltyApplied();
            ServerPlayer player = ritual.player();
            if (player != null) {
                player.displayClientMessage(Component.translatable("ritual.cocojenna.inject_late")
                        .withStyle(ChatFormatting.YELLOW), true);
            }
            if (player != null) {
                ritual.setPhase(Phase.RESONANCE, now);
                completeRitual(player, ritual, data);
            }
        }
    }

    private static void tickAltarDamage(ServerLevel level, MemoryForgeRitual ritual) {
        var box = new net.minecraft.world.phys.AABB(ritual.altarPos()).inflate(8);
        var defenders = level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, box,
                e -> e.isAlive() && e.getPersistentData().getBoolean("MemoryForgeDefender"));
        if (defenders.isEmpty()) return;
        for (var defender : defenders) {
            BlockPos hit = ritual.damageRandomBlock(level, 2.5f);
            if (hit != null) {
                level.sendParticles(ParticleTypes.SMOKE,
                        hit.getX() + 0.5, hit.getY() + 0.5, hit.getZ() + 0.5,
                        6, 0.2, 0.2, 0.2, 0.01);
            }
        }
        if (ritual.isCoreDestroyed()) {
            failBrokenCore(ritual, MemoryForgeSavedData.get(level));
        }
    }

    public static void onAltarBlockDamaged(MemoryForgeRitual ritual, MemoryForgeSavedData data) {
        if (ritual.isCoreDestroyed()) {
            failBrokenCore(ritual, data);
        }
    }

    private static void spawnDefender(ServerLevel level, MemoryForgeRitual ritual) {
        var box = new net.minecraft.world.phys.AABB(ritual.altarPos()).inflate(14);
        long nearby = level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, box,
                e -> e.isAlive() && e.getPersistentData().getBoolean("MemoryForgeDefender")).size();
        if (nearby >= 6) return;

        var type = switch (level.random.nextInt(3)) {
            case 0 -> ModEntities.HEAT_LEECH.get();
            case 1 -> ModEntities.MIMIC_CAT.get();
            default -> ModEntities.FORGOTTEN_WISP.get();
        };
        var mob = type.create(level);
        if (mob == null) return;
        BlockPos p = ritual.altarPos().offset(level.random.nextInt(7) - 3, 1, level.random.nextInt(7) - 3);
        mob.setPos(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
        mob.getPersistentData().putBoolean("MemoryForgeDefender", true);
        if (mob.checkSpawnRules(level, net.minecraft.world.entity.MobSpawnType.EVENT)) {
            level.addFreshEntity(mob);
        }
    }

    private static void completeRitual(ServerPlayer player, MemoryForgeRitual ritual,
                                       MemoryForgeSavedData data) {
        Item result = ForgeRegistries.ITEMS.getValue(ritual.recipe().resultId());
        if (result == null || result == Items.AIR) {
            result = ModItems.SUPREME_CAT_CLAW.get();
        }
        ItemStack weapon = new ItemStack(result);
        if (ritual.penaltyApplied()) {
            WeaponEnhanceHelper.setLevel(weapon, 0);
            weapon.getOrCreateTag().putBoolean("RitualPenalty", true);
        }
        if (ritual.recipe().bonusCondition.test(ritual)) {
            ritual.markBonusApplied();
            weapon.getOrCreateTag().putBoolean("RitualBonus", true);
            WeaponEnhanceHelper.setLevel(weapon, Math.max(1, WeaponEnhanceHelper.getLevel(weapon)));
            weapon.getOrCreateTag().putFloat("RitualDamageBonus", 0.15f);
        }

        ServerLevel level = player.serverLevel();
        ItemEntity drop = new ItemEntity(level,
                ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + 1.5,
                ritual.altarPos().getZ() + 0.5, weapon);
        drop.setPickUpDelay(40);
        level.addFreshEntity(drop);

        for (int i = 0; i < 40; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + i * 0.5,
                    ritual.altarPos().getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.02);
        }
        if (ritual.recipe() == MemoryForgeRecipe.HIBISCUS_FALL) {
            BlockPos altar = ritual.altarPos();
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (dx * dx + dz * dz > 16) continue;
                    BlockPos ground = altar.offset(dx, 0, dz);
                    if (level.getBlockState(ground).isAir()
                            || level.getBlockState(ground.below()).isSolidRender(level, ground.below())) {
                        level.setBlock(ground, ModBlocks.HIBISCUS_FLOWER.get().defaultBlockState(), 2);
                    }
                }
            }
            for (int i = 0; i < 60; i++) {
                level.sendParticles(ParticleTypes.CHERRY_LEAVES,
                        altar.getX() + 0.5, altar.getY() + 1.5, altar.getZ() + 0.5,
                        8, 2.0, 1.0, 2.0, 0.01);
            }
        }
        level.playSound(null, ritual.altarPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);
        level.getServer().getPlayerList().broadcastSystemMessage(
                Component.translatable("ritual.cocojenna.broadcast",
                        player.getDisplayName(),
                        Component.translatable("item.cocojenna." + ritual.recipe().id)),
                false);

        ritual.setPhase(Phase.DONE, level.getGameTime());
        data.remove(ritual.altarPos());
        ALTAR_LEVELS.remove(ritual.altarPos());
        clearForgeHud(player);
    }

    private static void failBrokenCore(MemoryForgeRitual ritual, MemoryForgeSavedData data) {
        ServerPlayer player = ritual.player();
        if (player != null) {
            clearForgeHud(player);
            player.displayClientMessage(Component.translatable("ritual.cocojenna.core_broken")
                    .withStyle(ChatFormatting.RED), true);
            // 50% material return — catalyst not consumed yet
            ItemStack partial = new ItemStack(ritual.recipe().coreItem.get(),
                    Math.max(1, ritual.recipe().coreCount / 2));
            player.addItem(partial);
        }
        ritual.setPhase(Phase.FAILED, 0);
        data.remove(ritual.altarPos());
        ALTAR_LEVELS.remove(ritual.altarPos());
    }

    private static void failWrongCatalyst(ServerPlayer player, MemoryForgeRitual ritual,
                                          MemoryForgeSavedData data) {
        player.displayClientMessage(Component.translatable("ritual.cocojenna.wrong_catalyst")
                .withStyle(ChatFormatting.DARK_RED), true);
        player.serverLevel().playSound(null, ritual.altarPos(), SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS, 0.6f, 1.4f);
        ritual.setPhase(Phase.FAILED, 0);
        data.remove(ritual.altarPos());
        ALTAR_LEVELS.remove(ritual.altarPos());
    }

    public static void onPlayerDeath(ServerPlayer player) {
        for (ServerLevel level : player.server.getAllLevels()) {
            MemoryForgeSavedData data = MemoryForgeSavedData.get(level);
            var toRemove = data.rituals().entrySet().stream()
                    .filter(e -> e.getValue().playerId().equals(player.getUUID()))
                    .map(Map.Entry::getKey)
                    .toList();
            for (BlockPos altar : toRemove) {
                data.remove(altar);
                ALTAR_LEVELS.remove(altar);
            }
        }
    }
}
