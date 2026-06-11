package com.cocojenna.kingdom;

import com.cocojenna.init.ModBiomes;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

/** §10 生態深化：霓虹菇、光纖藤、孢子樹等. */
public final class EcologyDeepeningManager {

    private EcologyDeepeningManager() {}

    public static void tickPlayer(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (player.tickCount % 30 != 0) return;

        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        if (biome.is(ModBiomes.VELVET_FOREST) || biome.is(ModBiomes.FORGOTTEN_WASTES)
                || biome.is(ModBiomes.CARDBOARD_SLUMS)) {
            if (player.level().isNight() && player.getRandom().nextInt(4) == 0) {
                player.serverLevel().sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                        player.getX(), player.getY() + 0.8, player.getZ(), 1, 0.3, 0.2, 0.3, 0.01);
            }
        }
        if (biome.is(ModBiomes.MOON_ALLEY) && player.getRandom().nextInt(6) == 0) {
            player.serverLevel().sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1.5, player.getZ(), 1, 0.2, 0.4, 0.2, 0.005);
        }
        if (biome.is(ModBiomes.CATNIP_HIGHLANDS) && player.tickCount % 120 == 0
                && player.getRandom().nextFloat() < 0.35f) {
            give(player, new ItemStack(ModItems.CATNIP_ITEM.get(), 1 + player.getRandom().nextInt(2)));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.catnip_field"), true);
        }
        if (biome.is(ModBiomes.VELVET_FOREST) && player.isShiftKeyDown()
                && player.getMainHandItem().isEmpty() && player.getRandom().nextInt(8) == 0) {
            give(player, new ItemStack(ModItems.VELVET_FUR.get()));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.velvet_forage"), true);
        }
        if (biome.is(ModBiomes.FIRST_CRY_PLAINS) && player.onGround()
                && player.getRandom().nextInt(10) == 0) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, 40, 0, true, false));
        }
        if (biome.is(ModBiomes.MOONLIGHT_BEACH) && player.isInWater()
                && player.getRandom().nextInt(12) == 0) {
            player.displayClientMessage(Component.translatable("ecology.cocojenna.moon_beach"), true);
        }
        if (biome.is(ModBiomes.STARDUST_DESERT) && player.tickCount % 80 == 0
                && player.getRandom().nextFloat() < 0.2f) {
            player.serverLevel().sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 0.5, player.getZ(), 2, 0.5, 0.3, 0.5, 0.01);
        }
        if (biome.is(ModBiomes.HOWLING_GORGE) && player.getRandom().nextInt(20) == 0) {
            player.displayClientMessage(Component.translatable("ecology.cocojenna.gorge_wind"), true);
        }
        if (biome.is(ModBiomes.BLIND_WATER_RIVER) && player.isInWater()
                && player.getRandom().nextInt(15) == 0) {
            give(player, new ItemStack(ModItems.MEMORY_SHARD.get()));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.blind_water"), true);
        }
        if (biome.is(ModBiomes.RAINBOW_CANYON) && player.getRandom().nextInt(25) == 0) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.GLOWING, 60, 0, true, false));
        }
    }

    public static boolean tryHarvest(ServerPlayer player, BlockPos pos, BlockState state) {
        if (!player.level().dimension().equals(ModDimensions.CAT_KINGDOM)) return false;

        if (state.is(ModBlocks.NEON_MUSHROOM.get())) {
            give(player, new ItemStack(ModItems.NEON_MUSHROOM_ITEM.get(), 1 + player.getRandom().nextInt(2)));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.neon_harvest"), true);
            return true;
        }
        if (state.is(ModBlocks.SPORE_FRUIT_NODE.get())) {
            give(player, new ItemStack(ModItems.SPORE_FRUIT.get(), 1 + player.getRandom().nextInt(2)));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.spore_harvest"), true);
            return true;
        }
        if (state.is(ModBlocks.VELVET_VINE.get())) {
            give(player, new ItemStack(ModItems.FIBER_VINE.get(), 2 + player.getRandom().nextInt(2)));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.fiber_harvest"), true);
            return true;
        }
        if (state.is(ModBlocks.COTTON_CANDY_SHRUB.get())) {
            ItemStack stack = new ItemStack(net.minecraft.world.item.Items.COOKIE,
                    1 + player.getRandom().nextInt(2));
            stack.setHoverName(Component.translatable("item.cocojenna.cotton_candy"));
            give(player, stack);
            player.displayClientMessage(Component.translatable("ecology.cocojenna.cotton_harvest"), true);
            return true;
        }
        if (state.is(ModBlocks.VELVET_GRASS.get())) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED, 80, 0, true, false));
            player.displayClientMessage(Component.translatable("ecology.cocojenna.velvet_grass_step"), true);
            return true;
        }
        return false;
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }
}
