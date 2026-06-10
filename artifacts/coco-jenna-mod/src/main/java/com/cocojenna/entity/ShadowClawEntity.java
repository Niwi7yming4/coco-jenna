package com.cocojenna.entity;

import com.cocojenna.endgame.ShadowClawBattleManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * 影爪 — 最終 Boss，四階段敘事戰鬥.
 */
public class ShadowClawEntity extends GeneralCatEntity {

    private ShadowClawBattleManager.StoryPhase storyPhase = ShadowClawBattleManager.StoryPhase.FALLEN_GENERAL;
    private ShadowClawBattleManager.Ending chosenEnding;

    public ShadowClawEntity(EntityType<? extends ShadowClawEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        return spawnType == MobSpawnType.EVENT
                || spawnType == MobSpawnType.STRUCTURE
                || spawnType == MobSpawnType.SPAWN_EGG
                || spawnType == MobSpawnType.COMMAND
                || spawnType == MobSpawnType.MOB_SUMMONED;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return GeneralCatEntity.createAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 500.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 20.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, 15.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            ShadowClawBattleManager.tick(this);
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        float before = getHealth();
        super.actuallyHurt(source, amount);
        if (source.getEntity() instanceof Player player) {
            ShadowClawBattleManager.onHurt(this, player, getHealth());
        }
        if (before > 0 && getHealth() <= 0 && triggersFirstDawnOnDeath()) {
            if (chosenEnding == ShadowClawBattleManager.Ending.REDEMPTION) {
                com.cocojenna.endgame.AfterRainManager.setShadowClawEnding(
                        source.getEntity() instanceof ServerPlayer sp ? sp : null, "redemption");
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            ShadowClawBattleManager.onPlayerInteract(this, sp);
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    public ShadowClawBattleManager.StoryPhase getStoryPhase() { return storyPhase; }

    public void setStoryPhase(ShadowClawBattleManager.StoryPhase phase) { this.storyPhase = phase; }

    public void setChosenEnding(ShadowClawBattleManager.Ending ending) { this.chosenEnding = ending; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("StoryPhase", storyPhase.name());
        if (chosenEnding != null) tag.putString("Ending", chosenEnding.name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("StoryPhase")) {
            try {
                storyPhase = ShadowClawBattleManager.StoryPhase.valueOf(tag.getString("StoryPhase"));
            } catch (Exception ignored) {}
        }
        if (tag.contains("Ending")) {
            try {
                chosenEnding = ShadowClawBattleManager.Ending.valueOf(tag.getString("Ending"));
            } catch (Exception ignored) {}
        }
    }
}
