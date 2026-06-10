package com.cocojenna.memforge;

import com.cocojenna.init.ModBlocks;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenDaikatanaRitualPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/** Multi-phase daikatana forging at altar foundation blocks. */
public final class DaikatanaRitualManager {

    public static final int FORGE_TICKS = 40 * 20;
    public static final int QUENCH_TICKS = 15 * 20;

    private DaikatanaRitualManager() {}

    public static void openGui(ServerPlayer player, BlockPos altarPos) {
        sync(player, altarPos);
    }

    public static void sync(ServerPlayer player, BlockPos altarPos) {
        ServerLevel level = player.serverLevel();
        DaikatanaRitual ritual = DaikatanaRitualSavedData.get(level).get(altarPos);
        int phaseOrd = -1;
        int recipeOrd = -1;
        long end = 0L;
        if (ritual != null) {
            phaseOrd = ritual.phase().ordinal();
            recipeOrd = ritual.recipe().ordinal();
            end = ritual.phaseEndTick();
        }
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenDaikatanaRitualPacket(altarPos, phaseOrd, recipeOrd, end, level.getGameTime()));
    }

    public static boolean tryStartByOrdinal(ServerPlayer player, BlockPos altarPos, int recipeOrd) {
        if (recipeOrd < 0 || recipeOrd >= DaikatanaRitualRecipe.values().length) {
            return false;
        }
        return tryStart(player, altarPos, new ItemStack(DaikatanaRitualRecipe.values()[recipeOrd].catalyst));
    }

    public static boolean tryQuenchFromGui(ServerPlayer player, BlockPos altarPos) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.GLASS_BOTTLE) || stack.is(Items.WATER_BUCKET)) {
                player.getInventory().selected = i < 9 ? i : 0;
                if (i >= 9) {
                    ItemStack swap = player.getInventory().getItem(player.getInventory().selected);
                    player.getInventory().setItem(player.getInventory().selected, stack);
                    player.getInventory().setItem(i, swap);
                }
                return tryQuench(player, altarPos, player.getItemInHand(InteractionHand.MAIN_HAND));
            }
        }
        player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_need_water")
                .withStyle(ChatFormatting.YELLOW), true);
        return false;
    }

    public static boolean tryStart(ServerPlayer player, BlockPos altarPos, ItemStack held) {
        ServerLevel level = player.serverLevel();
        if (!level.getBlockState(altarPos).is(ModBlocks.ALTAR_FOUNDATION.get())) {
            return false;
        }
        DaikatanaRitualRecipe recipe = DaikatanaRitualRecipe.forCatalyst(held.getItem());
        if (recipe == null) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_need_catalyst")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        DaikatanaRitualSavedData data = DaikatanaRitualSavedData.get(level);
        if (data.get(altarPos) != null) {
            return false;
        }
        if (!recipe.hasMaterials(player.getInventory())) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_missing_mats",
                    Component.translatable("item.cocojenna.daikatana_" + recipe.id))
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (!player.getAbilities().instabuild) {
            recipe.consumeMaterials(player.getInventory());
        }
        DaikatanaRitual ritual = new DaikatanaRitual(altarPos, player.getUUID(), recipe, level.getGameTime());
        data.add(ritual);
        level.playSound(null, altarPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 0.8f);
        player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_started",
                Component.translatable("item.cocojenna.daikatana_" + recipe.id)), true);
        sync(player, altarPos);
        return true;
    }

    public static boolean tryQuench(ServerPlayer player, BlockPos altarPos, ItemStack held) {
        ServerLevel level = player.serverLevel();
        DaikatanaRitualSavedData data = DaikatanaRitualSavedData.get(level);
        DaikatanaRitual ritual = data.get(altarPos);
        if (ritual == null || ritual.phase() != DaikatanaRitual.Phase.QUENCH) {
            return false;
        }
        if (!held.is(Items.WATER_BUCKET) && !held.is(Items.GLASS_BOTTLE)) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_need_water")
                    .withStyle(ChatFormatting.YELLOW), true);
            return true;
        }
        if (!player.getAbilities().instabuild) {
            if (held.is(Items.WATER_BUCKET)) {
                player.setItemInHand(player.getUsedItemHand(), Items.BUCKET.getDefaultInstance());
            } else {
                held.shrink(1);
            }
        }
        complete(player, ritual, data);
        return true;
    }

    public static void tick(ServerLevel level) {
        DaikatanaRitualSavedData data = DaikatanaRitualSavedData.get(level);
        if (data.rituals().isEmpty()) return;

        long now = level.getGameTime();
        var snapshot = new HashMap<>(data.rituals());
        for (DaikatanaRitual ritual : snapshot.values()) {
            BlockState altar = level.getBlockState(ritual.altarPos());
            if (!altar.is(ModBlocks.ALTAR_FOUNDATION.get())) {
                data.remove(ritual.altarPos());
                continue;
            }
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(ritual.playerId());
            if (ritual.phase() == DaikatanaRitual.Phase.FORGING) {
                tickForge(level, ritual, player, now);
                if (player != null && now % 20 == 0) {
                    sync(player, ritual.altarPos());
                }
                if (now >= ritual.phaseEndTick()) {
                    ritual.setPhase(DaikatanaRitual.Phase.QUENCH, now + QUENCH_TICKS);
                    data.setDirty();
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_quench")
                                .withStyle(ChatFormatting.GOLD), true);
                        sync(player, ritual.altarPos());
                    }
                }
            } else if (ritual.phase() == DaikatanaRitual.Phase.QUENCH) {
                if (now % 10 == 0) {
                    spawnForgeParticles(level, ritual.altarPos(), ParticleTypes.BUBBLE);
                }
                if (player != null && now % 20 == 0) {
                    sync(player, ritual.altarPos());
                }
                if (now >= ritual.phaseEndTick()) {
                    if (player != null) {
                        complete(player, ritual, data);
                    } else {
                        ritual.setPhase(DaikatanaRitual.Phase.FAILED, 0);
                        data.remove(ritual.altarPos());
                    }
                }
            }
        }
    }

    private static void tickForge(ServerLevel level, DaikatanaRitual ritual,
                                  ServerPlayer player, long now) {
        BlockPos pos = ritual.altarPos();
        if (now % 5 == 0) {
            spawnForgeParticles(level, pos, ParticleTypes.SOUL_FIRE_FLAME);
        }
        if (player == null) return;
        if (player.blockPosition().distSqr(pos) > 12 * 12) {
            player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_too_far")
                    .withStyle(ChatFormatting.RED), true);
            DaikatanaRitualSavedData.get(level).remove(pos);
        }
    }

    private static void spawnForgeParticles(ServerLevel level, BlockPos pos,
                                            net.minecraft.core.particles.ParticleOptions type) {
        level.sendParticles(type,
                pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                6, 0.4, 0.6, 0.4, 0.02);
    }

    private static void complete(ServerPlayer player, DaikatanaRitual ritual,
                                 DaikatanaRitualSavedData data) {
        ServerLevel level = player.serverLevel();
        ItemStack weapon = new ItemStack(ritual.recipe().result);
        weapon.getOrCreateTag().putBoolean("DaikatanaRitual", true);
        ItemEntity drop = new ItemEntity(level,
                ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + 1.5,
                ritual.altarPos().getZ() + 0.5, weapon);
        drop.setPickUpDelay(20);
        level.addFreshEntity(drop);
        level.playSound(null, ritual.altarPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0f, 1.1f);
        for (int i = 0; i < 24; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    ritual.altarPos().getX() + 0.5, ritual.altarPos().getY() + 1.0 + i * 0.15,
                    ritual.altarPos().getZ() + 0.5, 2, 0.15, 0.15, 0.15, 0.01);
        }
        player.displayClientMessage(Component.translatable("ritual.cocojenna.daikatana_complete",
                Component.translatable("item.cocojenna.daikatana_" + ritual.recipe().id)), true);
        ritual.setPhase(DaikatanaRitual.Phase.DONE, level.getGameTime());
        data.remove(ritual.altarPos());
        sync(player, ritual.altarPos());
    }

    public static void onPlayerDeath(ServerPlayer player) {
        for (ServerLevel level : player.server.getAllLevels()) {
            DaikatanaRitualSavedData data = DaikatanaRitualSavedData.get(level);
            var toRemove = data.rituals().entrySet().stream()
                    .filter(e -> e.getValue().playerId().equals(player.getUUID()))
                    .map(Map.Entry::getKey)
                    .toList();
            for (BlockPos pos : toRemove) {
                data.remove(pos);
            }
        }
    }
}
