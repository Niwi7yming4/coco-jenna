package com.cocojenna.kingdom;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** 微型探索點右鍵互動（設計書 §3.2）. */
public final class KingdomMicroInteractHandler {

    private static final int COOLDOWN_TICKS = 6000;

    private KingdomMicroInteractHandler() {}

    public static boolean tryInteract(ServerPlayer player, BlockPos pos, BlockState state) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;

        MicroType type = detect(state);
        if (type == null) return false;

        BondData bond = ModCapabilities.getOrDefault(player);
        long now = player.level().getGameTime();
        long last = bond.getKingdomMicroCooldown(pos.asLong());
        if (now - last < COOLDOWN_TICKS) {
            player.displayClientMessage(Component.translatable("explore.cocojenna.micro.cooldown"), true);
            return true;
        }
        bond.setKingdomMicroCooldown(pos.asLong(), now);

        switch (type) {
            case PAW -> {
                bond.addReputation("royal", 1);
                player.displayClientMessage(Component.translatable("explore.cocojenna.micro.paw"), true);
            }
            case YARN -> {
                if (player.getRandom().nextInt(3) == 0) {
                    give(player, new ItemStack(ModItems.RAINBOW_YARN_BALL.get()));
                }
                player.displayClientMessage(Component.translatable("explore.cocojenna.micro.yarn"), true);
            }
            case TOY_MOUSE -> {
                bond.addKingdomHappiness(1);
                player.displayClientMessage(Component.translatable("explore.cocojenna.micro.toy"), true);
            }
            case FISH_BONE -> {
                if (player.getRandom().nextInt(5) == 0) {
                    give(player, new ItemStack(ModItems.MEMORY_SHARD.get()));
                } else {
                    give(player, new ItemStack(Items.BONE_MEAL));
                }
                player.displayClientMessage(Component.translatable("explore.cocojenna.micro.bone"), true);
            }
            case CATNIP -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true, true));
                player.displayClientMessage(Component.translatable("explore.cocojenna.micro.catnip"), true);
            }
            case PURR_STONE -> {
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 120, 0, false, true, true));
                player.displayClientMessage(Component.translatable("explore.cocojenna.micro.purr"), true);
            }
        }
        return true;
    }

    private static MicroType detect(BlockState state) {
        if (state.is(Blocks.RED_CARPET)) return MicroType.PAW;
        if (state.is(Blocks.WHITE_WOOL) || state.is(Blocks.RED_WOOL)) return MicroType.YARN;
        if (state.is(Blocks.BROWN_WOOL) || state.is(Blocks.BROWN_CARPET)) return MicroType.TOY_MOUSE;
        if (state.is(Blocks.BONE_BLOCK)) return MicroType.FISH_BONE;
        if (state.is(Blocks.FERN) || state.is(ModBlocks.CATNIP.get())) return MicroType.CATNIP;
        if (state.is(ModBlocks.PURR_CRYSTAL_BLOCK.get()) || state.is(Blocks.AMETHYST_BLOCK)) return MicroType.PURR_STONE;
        return null;
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private enum MicroType { PAW, YARN, TOY_MOUSE, FISH_BONE, CATNIP, PURR_STONE }
}
