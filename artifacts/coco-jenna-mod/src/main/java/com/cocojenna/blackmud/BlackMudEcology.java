package com.cocojenna.blackmud;

import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.BlackMudMob;
import com.cocojenna.init.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** 悲傷之海生態：共鳴、犧牲、哀悼（設計書 卷三）. */
public final class BlackMudEcology {

    private static final String MOURN_TICKS = "cocojenna_mud_mourn";
    private static final String IN_SEA = "cocojenna_in_sorrow_sea";

    private BlackMudEcology() {}

    public static void onHighSequenceDeath(ServerLevel level, LivingEntity victim) {
        if (!(victim instanceof BlackMudMob bm)) return;
        if (bm.blackMudSequence() > 5) return;

        Vec3 pos = victim.position();
        for (Mob mob : level.getEntitiesOfClass(Mob.class, new AABB(pos, pos).inflate(16))) {
            if (!(mob instanceof BlackMudMob)) continue;
            mob.getPersistentData().putInt(MOURN_TICKS, 100);
            mob.setDeltaMovement(Vec3.ZERO);
        }
        level.sendParticles(ParticleTypes.SCULK_SOUL, pos.x, pos.y + 0.5, pos.z, 12, 0.6, 0.4, 0.6, 0.02);
        for (ServerPlayer p : level.players()) {
            if (p.distanceToSqr(pos) < 48 * 48) {
                p.displayClientMessage(Component.translatable("blackmud.cocojenna.mourning"), true);
            }
        }
    }

    public static void onHurtSacrifice(LivingEntity victim, float amount) {
        if (!(victim instanceof BlackMudMob bm)) return;
        if (bm.blackMudSequence() > 5) return;
        if (!(victim.level() instanceof ServerLevel level)) return;

        for (Mob ally : level.getEntitiesOfClass(Mob.class, victim.getBoundingBox().inflate(10))) {
            if (ally == victim || !(ally instanceof BlackMudMob allyBm)) continue;
            if (!BlackMudSequenceCatalog.isLowSequence(allyBm.blackMudSequence())) continue;
            if (ally.getPersistentData().getInt(MOURN_TICKS) > 0) continue;
            ally.setTarget(victim.getLastHurtByMob());
            ally.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1));
            if (ally.getRandom().nextFloat() < 0.35f) {
                ally.hurt(victim.damageSources().generic(), amount * 0.25f);
            }
            break;
        }
    }

    public static void tickMob(Mob mob) {
        int mourn = mob.getPersistentData().getInt(MOURN_TICKS);
        if (mourn <= 0) return;
        mob.getPersistentData().putInt(MOURN_TICKS, mourn - 1);
        mob.setDeltaMovement(Vec3.ZERO);
        if (mob.level().isClientSide) return;
        if (mob.tickCount % 15 == 0 && mob.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.FALLING_SPORE_BLOSSOM,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(), 1, 0.1, 0.2, 0.1, 0);
        }
    }

    public static void tickPlayer(ServerPlayer player) {
        if (player.tickCount % 40 != 0) return;
        ServerLevel level = player.serverLevel();
        boolean sea = isSeaOfSorrow(level, player);
        boolean was = player.getPersistentData().getBoolean(IN_SEA);
        if (sea && !was) {
            player.getPersistentData().putBoolean(IN_SEA, true);
            player.displayClientMessage(Component.translatable("blackmud.cocojenna.sea.enter"), true);
        } else if (!sea && was) {
            player.getPersistentData().putBoolean(IN_SEA, false);
        }
        if (sea && player.tickCount % 80 == 0) {
            level.sendParticles(ModParticles.STARDUST_SPARK.get(),
                    player.getX(), player.getY() + 1, player.getZ(), 4, 1.5, 0.5, 1.5, 0.01);
        }
        var bond = ModCapabilities.getOrDefault(player);
        if (sea && player.tickCount % 200 == 0
                && (bond.getCocoEmotion() + bond.getJennaEmotion() > 80f
                || bond.getKingdomHappiness() > 50)) {
            resonancePulse(level, player);
        }
    }

    public static boolean isSeaOfSorrow(ServerLevel level, ServerPlayer player) {
        if (BlackMudSavedData.get(level).isAfterRain()) return false;
        var data = BlackMudSavedData.get(level);
        var center = player.chunkPosition();
        int heavy = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (data.getStage(new net.minecraft.world.level.ChunkPos(center.x + dx, center.z + dz)) >= 4) {
                    heavy++;
                }
            }
        }
        return heavy >= 5;
    }

    private static void resonancePulse(ServerLevel level, ServerPlayer player) {
        for (Monster mob : level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(24))) {
            if (!(mob instanceof BlackMudMob)) continue;
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0));
            if (mob.getTarget() == null) mob.setTarget(player);
        }
        player.displayClientMessage(Component.translatable("blackmud.cocojenna.resonance"), true);
    }
}
