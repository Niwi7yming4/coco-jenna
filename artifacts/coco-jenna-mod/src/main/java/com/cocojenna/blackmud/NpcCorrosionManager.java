package com.cocojenna.blackmud;

import com.cocojenna.entity.*;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.init.ModEffects;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/** NPC 四階腐蝕：嗜睡 → 抗拒 → 同化邊緣 → 墮落絨尾. */
public final class NpcCorrosionManager {

    private NpcCorrosionManager() {}

    public static boolean isCorruptible(LivingEntity entity) {
        return entity instanceof SamuraiCatEntity
                || entity instanceof MonkCatEntity
                || entity instanceof CourtLadyCatEntity
                || entity instanceof SumoCatEntity
                || entity instanceof SanhuaWeaverEntity;
    }

    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAT_KINGDOM)) return;
        if (BlackMudSavedData.get(level).isAfterRain()) return;
        if (level.getGameTime() % 200 != 0) return;

        NpcCorrosionSavedData data = NpcCorrosionSavedData.get(level);
        for (LivingEntity npc : level.getEntitiesOfClass(LivingEntity.class, new AABB(-3.0E7, -64, -3.0E7, 3.0E7, 320, 3.0E7),
                NpcCorrosionManager::isCorruptible)) {
            int chunkStage = BlackMudCorruptionManager.stageAt(level, npc.blockPosition());
            if (chunkStage < 2) continue;
            int current = data.getStage(npc.getUUID());
            if (current >= 4) continue;
            if (level.random.nextFloat() < 0.08f + chunkStage * 0.04f) {
                int next = current + 1;
                data.setStage(npc.getUUID(), next);
                NpcCorrosionVisuals.onStageChanged(npc, next);
                if (next >= 4) {
                    transformToFallen(level, npc);
                }
            } else {
                NpcCorrosionVisuals.tickAmbient(level, npc, current);
            }
        }
    }

    public static void applyStageEffects(LivingEntity npc, int stage) {
        if (!(npc instanceof Mob mob)) return;
        switch (stage) {
            case 1 -> mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 0, false, false, true));
            case 2 -> mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0, false, false, true));
            case 3 -> {
                mob.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, true));
                mob.addEffect(new MobEffectInstance(ModEffects.CORROSION_MARK.get(), 200, 1, false, false, true));
            }
            default -> {}
        }
    }

    public static boolean tryPurify(Player player, LivingEntity npc) {
        if (!(player instanceof ServerPlayer sp) || !isCorruptible(npc)) return false;
        ItemStack held = player.getMainHandItem();
        if (!held.is(ModItems.HOLY_WATER.get()) && !held.is(ModItems.PURE_TEAR.get())) return false;
        NpcCorrosionSavedData data = NpcCorrosionSavedData.get(sp.serverLevel());
        int stage = data.getStage(npc.getUUID());
        if (stage <= 0) return false;
        held.shrink(1);
        data.setStage(npc.getUUID(), Math.max(0, stage - 1));
        sp.serverLevel().sendParticles(ParticleTypes.HAPPY_VILLAGER,
                npc.getX(), npc.getY() + 1, npc.getZ(), 12, 0.4, 0.5, 0.4, 0.02);
        player.displayClientMessage(Component.translatable("corrosion.cocojenna.purified_npc", stage - 1), true);
        return true;
    }

    private static void transformToFallen(ServerLevel level, LivingEntity npc) {
        var mimic = ModEntities.MIMIC_CAT.get().create(level);
        if (mimic == null) return;
        mimic.setPos(npc.getX(), npc.getY(), npc.getZ());
        level.addFreshEntity(mimic);
        npc.discard();
        for (ServerPlayer p : level.players()) {
            if (p.distanceToSqr(npc) < 64 * 64) {
                p.displayClientMessage(Component.translatable("corrosion.cocojenna.npc_fallen"), true);
            }
        }
    }
}
