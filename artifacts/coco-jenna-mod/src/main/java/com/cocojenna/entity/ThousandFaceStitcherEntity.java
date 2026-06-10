package com.cocojenna.entity;

import com.cocojenna.blackmud.BlackMudCorruptionManager;
import com.cocojenna.init.ModItems;
import com.cocojenna.init.ModSounds;
import com.cocojenna.reputation.ReputationHelper;
import com.cocojenna.sequence.HiddenSequenceRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 千面縫合者 — 貓之國世界首領，擊敗後大幅淨化黑泥但不觸發初晴.
 */
public class ThousandFaceStitcherEntity extends GeneralCatEntity {

    private int phase = 1;
    private int skillCooldown;

    public ThousandFaceStitcherEntity(EntityType<? extends ThousandFaceStitcherEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.ATTACK_DAMAGE, 24.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) return;
        LivingEntity target = getTarget();
        if (target != null && skillCooldown-- <= 0) {
            switch (phase) {
                case 1 -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                case 2 -> target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                default -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            }
            skillCooldown = 100 - phase * 10;
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        float before = getHealth() / getMaxHealth();
        super.actuallyHurt(source, amount);
        if (!isAlive()) return;
        float after = getHealth() / getMaxHealth();
        if (phase == 1 && before > 0.66f && after <= 0.66f) {
            phase = 2;
            level().playSound(null, blockPosition(), ModSounds.WORLD_BLACK_MUD_SPREAD.get(),
                    SoundSource.HOSTILE, 1.2f, 0.8f);
        } else if (phase == 2 && before > 0.33f && after <= 0.33f) {
            phase = 3;
            addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && source.getEntity() instanceof ServerPlayer player) {
            BlackMudCorruptionManager.purifyRegion(player.serverLevel(), blockPosition(), 96, player);
            BlackMudCorruptionManager.onRegionalBossDefeated(player.serverLevel(),
                    BlackMudBossEntity.BossKind.THOUSAND_FACE);
            ReputationHelper.addRep(player, "central_plaza", 50);
            HiddenSequenceRegistry.tryUnlock(player, "thousand_face");
            ItemStack remnant = new ItemStack(ModItems.BLACK_MUD_REMNANT.get(), 24);
            if (!player.addItem(remnant)) player.drop(remnant, false);
            player.displayClientMessage(Component.translatable(
                    "boss.cocojenna.defeated",
                    Component.translatable("entity.cocojenna.thousand_face_stitcher")), true);
        }
        super.die(source);
    }
}
