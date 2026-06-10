package com.cocojenna.swordbone;

import com.cocojenna.memforge.MemoryForgeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 劍骨插入／覺醒儀式 — 記憶鑄造祭壇，蹲下長按 5 秒. */
public final class SwordBoneRitualManager {

    public static final int CHANNEL_TICKS = 100;

    private record ChannelState(BlockPos altar, String mode, int ticks, ItemStack weapon) {}

    private static final Map<UUID, ChannelState> ACTIVE = new HashMap<>();

    private SwordBoneRitualManager() {}

    public static boolean tryInteract(ServerPlayer player, BlockPos pos, InteractionHand hand) {
        ServerLevel level = player.serverLevel();
        if (!level.getBlockState(pos).is(Blocks.ENCHANTING_TABLE)) return false;
        if (!MemoryForgeStructure.isValidAltar(level, pos)) return false;

        ItemStack held = player.getItemInHand(hand);

        if (player.isCrouching() && held.isEmpty()) {
            var bond = com.cocojenna.capability.ModCapabilities.getOrDefault(player);
            if (SwordBoneManager.canAwaken(bond)) {
                start(player, pos, "awaken", ItemStack.EMPTY);
                return true;
            }
            if (bond.isSwordBoneAwakened() && !bond.isSwordBoneSupreme()) {
                start(player, pos, "supreme", ItemStack.EMPTY);
                return true;
            }
            return false;
        }

        if (!player.isCrouching() || held.isEmpty()) return false;
        if (!SwordBoneManager.canInsert(player, held)) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.cannot_insert"), true);
            return true;
        }
        start(player, pos, "insert", held.copy());
        return true;
    }

    private static void start(ServerPlayer player, BlockPos altar, String mode, ItemStack weapon) {
        ACTIVE.put(player.getUUID(), new ChannelState(altar, mode, 0, weapon));
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.channel_start"), true);
    }

    public static void tick(ServerPlayer player) {
        ChannelState state = ACTIVE.get(player.getUUID());
        if (state == null) return;

        if (!player.isCrouching()
                || player.blockPosition().distSqr(state.altar()) > 36
                || !player.serverLevel().getBlockState(state.altar()).is(Blocks.ENCHANTING_TABLE)) {
            cancel(player);
            return;
        }

        if ("insert".equals(state.mode())) {
            ItemStack held = player.getMainHandItem();
            if (held.isEmpty() || !SwordBoneManager.weaponId(held).equals(SwordBoneManager.weaponId(state.weapon()))) {
                cancel(player);
                return;
            }
        }

        int ticks = state.ticks() + 1;
        ServerLevel level = player.serverLevel();
        if (ticks % 10 == 0) {
            level.playSound(null, state.altar(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.5f, 0.8f);
            level.sendParticles(ParticleTypes.SOUL,
                    player.getX(), player.getY() + 1, player.getZ(), 4, 0.3, 0.4, 0.3, 0.01);
        }

        if (ticks >= CHANNEL_TICKS) {
            ACTIVE.remove(player.getUUID());
            finish(player, state);
            return;
        }
        ACTIVE.put(player.getUUID(), new ChannelState(state.altar(), state.mode(), ticks, state.weapon()));
    }

    private static void finish(ServerPlayer player, ChannelState state) {
        switch (state.mode()) {
            case "awaken" -> SwordBoneManager.tryAwaken(player);
            case "supreme" -> SwordBoneManager.trySupremeRite(player);
            case "insert" -> {
                ItemStack held = player.getMainHandItem();
                if (!held.isEmpty()) SwordBoneManager.insert(player, held);
            }
            default -> {}
        }
    }

    private static void cancel(ServerPlayer player) {
        if (ACTIVE.remove(player.getUUID()) != null) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.channel_cancel"), true);
        }
    }

    public static float channelProgress(ServerPlayer player) {
        ChannelState state = ACTIVE.get(player.getUUID());
        if (state == null) return 0f;
        return state.ticks() / (float) CHANNEL_TICKS;
    }
}
