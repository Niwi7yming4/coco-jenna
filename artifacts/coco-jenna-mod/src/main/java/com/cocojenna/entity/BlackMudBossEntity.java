package com.cocojenna.entity;

import com.cocojenna.blackmud.BlackMudCorruptionManager;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import com.cocojenna.reputation.ReputationHelper;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** 區域黑泥首領基類 — 擊敗後淨化區域，不觸發初晴. */
public class BlackMudBossEntity extends GeneralCatEntity implements BlackMudMob {

    public enum BossKind {
        GRIEF_AMALGAM("grief_amalgam", "velvet_forest", 48, "grief_amalgam"),
        BLIND_WATER_LORD("blind_water_lord", "blind_port", 64, "blind_water_lord"),
        FALLEN_VELVET("fallen_velvet", "forgotten_tower", 56, "fallen_velvet"),
        PRIMAL_CHAOS("primal_chaos", "dawn", 80, "primal_chaos"),
        FALLEN_GENERAL("fallen_general", "sleep_sanctuary", 52, "fallen_general"),
        HOWLING_SQUALL("howling_squall", "howling_gorge", 56, "howling_squall"),
        ASHURA_PHANTOM("ashura_phantom", "labyrinth", 60, "ashura_phantom"),
        GEAR_OVERLORD("gear_overlord", "gear_town", 48, "gear_overlord"),
        MOON_ALLEY_WRAITH("moon_alley_wraith", "moon_alley", 54, "moon_alley_wraith"),
        MOON_GUARDIAN("moon_guardian", "overworld", 80, "moon_guardian"),
        PLAZA_SENTINEL("plaza_sentinel", "central_plaza", 40, "plaza_sentinel"),
        FIRST_CRY_WARDEN("first_cry_warden", "first_cry", 36, "first_cry_warden"),
        THOUSAND_FACE("thousand_face_stitcher", "central_plaza", 96, "thousand_face");

        public final String id;
        public final String region;
        public final int purifyRadius;
        public final String hiddenSeq;

        BossKind(String id, String region, int purifyRadius, String hiddenSeq) {
            this.id = id;
            this.region = region;
            this.purifyRadius = purifyRadius;
            this.hiddenSeq = hiddenSeq;
        }
    }

    private final BossKind kind;
    private int skillCooldown = 80;
    private int griefSplitMask;

    public BlackMudBossEntity(EntityType<? extends BlackMudBossEntity> type, Level level, BossKind kind) {
        super(type, level);
        this.kind = kind;
    }

    public BossKind bossKind() {
        return kind;
    }

    @Override
    public int blackMudSequence() {
        return switch (kind) {
            case GRIEF_AMALGAM -> 4;
            case BLIND_WATER_LORD -> 3;
            case FALLEN_VELVET, FALLEN_GENERAL -> 2;
            case PRIMAL_CHAOS -> 1;
            default -> 5;
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) return;
        if (skillCooldown-- > 0) return;
        skillCooldown = skillInterval();
        useBossSkill();
    }

    private int skillInterval() {
        return switch (kind) {
            case BLIND_WATER_LORD, MOON_ALLEY_WRAITH, MOON_GUARDIAN -> 100;
            case PRIMAL_CHAOS, GEAR_OVERLORD -> 140;
            case HOWLING_SQUALL -> 70;
            default -> 120;
        };
    }

    private void useBossSkill() {
        LivingEntity target = getTarget();
        if (target == null && kind != BossKind.PRIMAL_CHAOS) return;

        switch (kind) {
            case BLIND_WATER_LORD, MOON_ALLEY_WRAITH -> blindDomain(14.0, 70);
            case MOON_GUARDIAN -> {
                moonGuardianBeam(target);
                if (getHealth() / getMaxHealth() < 0.3f) {
                    addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
                }
            }
            case FALLEN_VELVET, FALLEN_GENERAL -> {
                if (target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                }
            }
            case HOWLING_SQUALL -> {
                if (target != null) {
                    target.knockback(1.5, getX() - target.getX(), getZ() - target.getZ());
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 0));
                }
            }
            case ASHURA_PHANTOM -> {
                if (target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
                    target.hurt(damageSources().magic(), 4.0f);
                }
            }
            case GEAR_OVERLORD -> {
                AABB box = getBoundingBox().inflate(8.0);
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box,
                        t -> t.isAlive() && t != this && !(t instanceof Player p && p.isCreative()))) {
                    if (e instanceof Player p && !p.isAlliedTo(this)) {
                        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
                    }
                }
            }
            case PRIMAL_CHAOS -> chaosBurst();
            case PLAZA_SENTINEL, FIRST_CRY_WARDEN -> {
                if (target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0));
                }
            }
            default -> {}
        }
    }

    private void blindDomain(double radius, int blindTicks) {
        AABB box = getBoundingBox().inflate(radius);
        for (Player p : level().getEntitiesOfClass(Player.class, box, Player::isAlive)) {
            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, blindTicks, 0));
            p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, blindTicks / 2, 0));
        }
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 1.5, getZ(), 30, radius * 0.3, 0.5, radius * 0.3, 0.02);
        }
    }

    private void moonGuardianBeam(LivingEntity target) {
        if (target == null) return;
        target.hurt(damageSources().magic(), 15.0f);
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.END_ROD, target.getX(), target.getY() + 1.0, target.getZ(),
                    20, 0.3, 0.5, 0.3, 0.05);
        }
        if (random.nextFloat() < 0.35f && level() instanceof ServerLevel sl) {
            for (int i = 0; i < 2; i++) {
                var wraith = ModEntities.MOON_ALLEY_WRAITH.get().create(sl);
                if (wraith == null) continue;
                wraith.setPos(getX() + (random.nextDouble() - 0.5) * 4,
                        getY(), getZ() + (random.nextDouble() - 0.5) * 4);
                sl.addFreshEntity(wraith);
            }
        }
    }

    private void chaosBurst() {
        if (!(level() instanceof ServerLevel sl)) return;
        sl.sendParticles(ParticleTypes.REVERSE_PORTAL, getX(), getY() + 1.0, getZ(), 40, 2.0, 1.0, 2.0, 0.1);
        AABB box = getBoundingBox().inflate(6.0);
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box,
                t -> t.isAlive() && t != this && !(t instanceof Player p && p.isCreative()))) {
            if (e instanceof Player p && !p.isAlliedTo(this)) {
                e.hurt(damageSources().magic(), 6.0f);
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        float hpAfter = getHealth() - amount;
        if (kind == BossKind.GRIEF_AMALGAM && !level().isClientSide) {
            checkGriefSplit(hpAfter);
        }
        if (hpAfter <= 0) {
            onBossDefeated(source);
            discard();
            return;
        }
        super.actuallyHurt(source, amount);
    }

    private void checkGriefSplit(float hpAfter) {
        float max = getMaxHealth();
        float[] thresholds = {0.75f, 0.5f, 0.25f};
        for (int i = 0; i < thresholds.length; i++) {
            int bit = 1 << i;
            if ((griefSplitMask & bit) != 0) continue;
            if (getHealth() / max > thresholds[i] && hpAfter / max <= thresholds[i]) {
                griefSplitMask |= bit;
                spawnGriefFragment(i + 1);
            }
        }
    }

    private void spawnGriefFragment(int wave) {
        for (int i = 0; i < wave; i++) {
            var mimic = ModEntities.MIMIC_CAT.get().create(level());
            if (mimic == null) continue;
            double ox = (random.nextDouble() - 0.5) * 3.0;
            double oz = (random.nextDouble() - 0.5) * 3.0;
            mimic.setPos(getX() + ox, getY(), getZ() + oz);
            level().addFreshEntity(mimic);
        }
        level().playSound(null, blockPosition(), ModSounds.WORLD_BLACK_MUD_SPREAD.get(),
                SoundSource.HOSTILE, 0.8f, 1.2f + wave * 0.1f);
    }

    protected void onBossDefeated(DamageSource source) {
        level().playSound(null, blockPosition(),
                ModSounds.WORLD_BLACK_MUD_SPREAD.get(), SoundSource.HOSTILE, 1.5f, 1.6f);
        if (source.getEntity() instanceof ServerPlayer player) {
            BlackMudCorruptionManager.purifyRegion(
                    player.serverLevel(), blockPosition(), kind.purifyRadius, player);
            BlackMudCorruptionManager.onRegionalBossDefeated(player.serverLevel(), kind);
            ReputationHelper.onQuestComplete(player, kind.region);
            ReputationHelper.addRep(player, kind.region, 25);
            HiddenSequenceRegistry.tryUnlock(player, kind.hiddenSeq);
            ItemStack remnant = new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 8 + random.nextInt(8));
            if (!player.addItem(remnant)) player.drop(remnant, false);
            if (kind == BossKind.HOWLING_SQUALL) {
                ItemStack bone = new ItemStack(ModItems.SQUALL_UMBRELLA_BONE.get());
                if (!player.addItem(bone)) player.drop(bone, false);
                ItemStack fur = new ItemStack(ModItems.STORM_CLOUD_FUR.get(), 2 + random.nextInt(3));
                if (!player.addItem(fur)) player.drop(fur, false);
            }
            if (kind == BossKind.MOON_GUARDIAN) {
                ItemStack core = new ItemStack(ModItems.MOON_CORE.get());
                if (!player.addItem(core)) player.drop(core, false);
                ItemStack shards = new ItemStack(ModItems.MEMORY_SHARD.get(), 3);
                if (!player.addItem(shards)) player.drop(shards, false);
                com.cocojenna.capability.ModCapabilities.getOrDefault(player).addOverworldInfluence(10);
                if (player.level().dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                    com.cocojenna.overworld.PenetrationQuestManager.onMoonGuardianDefeated(player);
                }
            }
            player.displayClientMessage(Component.translatable(
                    "boss.cocojenna.defeated", Component.translatable("entity.cocojenna." + kind.id)), true);
        }
    }
}
