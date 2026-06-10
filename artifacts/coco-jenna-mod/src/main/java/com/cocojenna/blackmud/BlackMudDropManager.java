package com.cocojenna.blackmud;

import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 黑泥殘骸與遺物掉落（設計書 卷四 §4.2、§5.2）. */
public final class BlackMudDropManager {

    private BlackMudDropManager() {}

    public static void onKill(ServerPlayer player, LivingEntity victim, boolean distilled) {
        if (!(victim instanceof BlackMudMob bm)) return;
        RandomSource rng = player.getRandom();
        int seq = bm.blackMudSequence();
        float lootMult = distilled ? 1.6f : 1f;

        dropRemnants(player, seq, rng, lootMult);
        tryRelic(player, seq, rng, distilled);
        if (distilled && seq <= 2 && rng.nextFloat() < 0.05f) {
            give(player, new ItemStack(ModItems.PURE_TEAR.get(), 1));
        }
        if (distilled && seq == 1 && rng.nextFloat() < 0.35f) {
            give(player, new ItemStack(ModItems.PRIMAL_CHAOS_CORE.get(), 1));
        }
    }

    private static void dropRemnants(ServerPlayer player, int seq, RandomSource rng, float mult) {
        int remnants = switch (seq) {
            case 9, 8 -> 1 + rng.nextInt(2);
            case 7, 6 -> 2 + rng.nextInt(2);
            case 5 -> 2 + rng.nextInt(3);
            case 4 -> 3 + rng.nextInt(2);
            case 3 -> 3 + rng.nextInt(3);
            case 2 -> 4 + rng.nextInt(2);
            default -> 5 + rng.nextInt(3);
        };
        give(player, new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), Math.round(remnants * mult)));

        if (seq <= 7 && rng.nextFloat() < 0.22f * mult) {
            int n = 1 + rng.nextInt(seq <= 6 ? 3 : 1);
            give(player, new ItemStack(ModItems.MEMORY_PARTICLE.get(), n));
        }
        if (seq <= 5 && rng.nextFloat() < 0.14f * mult) {
            give(player, new ItemStack(ModItems.SHADOW_CRYSTAL.get(), 1));
        }
        if (seq <= 4 && rng.nextFloat() < 0.10f * mult) {
            give(player, new ItemStack(ModItems.GRIEF_GEL.get(), 1));
        }
        if (seq <= 3 && rng.nextFloat() < 0.12f * mult) {
            give(player, new ItemStack(ModItems.CHAOS_CRYSTAL.get(), 1));
        }
        if (seq == 3 && rng.nextFloat() < 0.18f * mult) {
            give(player, new ItemStack(ModItems.BLIND_WATER_GEL.get(), 1));
        }
        if (seq == 2 && rng.nextFloat() < 0.15f * mult) {
            give(player, new ItemStack(ModItems.FALLEN_CORE.get(), 1));
        }
        if (seq == 1 && rng.nextFloat() < 0.20f * mult) {
            give(player, new ItemStack(ModItems.PRIMAL_CHAOS_SHARD.get(), 1));
        }
    }

    private static void tryRelic(ServerPlayer player, int seq, RandomSource rng, boolean distilled) {
        float chance = distilled ? 0.08f : 0.03f;
        Item relic = switch (seq) {
            case 7 -> rng.nextFloat() < chance ? ModItems.UNSENT_LETTER.get() : null;
            case 6 -> rng.nextFloat() < chance ? ModItems.RUSTED_BELL.get() : null;
            case 4 -> rng.nextFloat() < chance * 1.4f ? ModItems.HALF_SCARF.get() : null;
            case 2 -> rng.nextFloat() < chance * 1.2f ? ModItems.FADED_COLLAR.get() : null;
            case 1 -> rng.nextFloat() < chance * 0.8f ? ModItems.PURE_DROP.get() : null;
            default -> null;
        };
        if (relic != null) {
            give(player, new ItemStack(relic));
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("blackmud.cocojenna.relic.found"), true);
        }
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!player.addItem(stack)) player.drop(stack, false);
    }
}
