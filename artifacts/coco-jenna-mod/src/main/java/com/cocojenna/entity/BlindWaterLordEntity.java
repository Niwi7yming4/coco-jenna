package com.cocojenna.entity;

import com.cocojenna.init.ModItems;
import com.cocojenna.kingdom.multiplayer.MultiplayerKingdomEvents;
import com.cocojenna.capability.ModCapabilities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BlindWaterLordEntity extends BlackMudBossEntity {

    private int mpSkillCd;
    @Nullable
    private UUID linkedA;
    @Nullable
    private UUID linkedB;

    public BlindWaterLordEntity(EntityType<? extends BlindWaterLordEntity> type, Level level) {
        super(type, level, BossKind.BLIND_WATER_LORD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    private boolean soloMode() {
        if (!(level() instanceof ServerLevel sl)) return true;
        List<ServerPlayer> nearby = sl.getEntitiesOfClass(ServerPlayer.class, getBoundingBox().inflate(48));
        if (nearby.size() <= 1) return true;
        return nearby.stream().allMatch(MultiplayerKingdomEvents::hasLoneWolfMedal);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || soloMode()) return;
        if (--mpSkillCd > 0) return;
        mpSkillCd = 100;
        List<ServerPlayer> players = level().getEntitiesOfClass(ServerPlayer.class, getBoundingBox().inflate(32));
        if (players.size() < 2) return;
        if (linkedA == null || linkedB == null) {
            linkedA = players.get(0).getUUID();
            linkedB = players.get(1).getUUID();
        }
        Player a = level().getPlayerByUUID(linkedA);
        Player b = level().getPlayerByUUID(linkedB);
        if (a != null && b != null && a.distanceToSqr(b) > 64) {
            a.hurt(damageSources().magic(), 6f);
            b.hurt(damageSources().magic(), 6f);
        }
        for (ServerPlayer p : players) {
            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
        }
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source, int looting, boolean recentlyHit) {
        if (source.getEntity() instanceof ServerPlayer killer && soloMode()) {
            ModCapabilities.getOrDefault(killer).getMultiplayerSection().setSoloBossClear(true);
        }
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }
}
