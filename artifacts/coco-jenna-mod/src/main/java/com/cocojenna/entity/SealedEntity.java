package com.cocojenna.entity;

import com.cocojenna.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 封印物 (Sealed Entity) — 敵人HP歸零後的凝結形態。
 *
 * <p>封印物是一個漂浮的光球，可被玩家拾取。
 * 拾取後成為物品欄中的「封印物」物品，可：
 * <ul>
 *   <li>放置在「封印物展示台」上進行展示</li>
 *   <li>使用「聖水」或「朱槿花之淚」復活為友好 NPC</li>
 *   <li>交給「蒸餾台」提取記憶碎片材料</li>
 * </ul>
 */
public class SealedEntity extends net.minecraft.world.entity.Entity {

    private static final EntityDataAccessor<String> DATA_ORIGINAL_ENTITY =
            SynchedEntityData.defineId(SealedEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_GLOW_COLOR =
            SynchedEntityData.defineId(SealedEntity.class, EntityDataSerializers.INT);

    private static final int PICKUP_RANGE = 2; // 格
    private int age = 0;

    public SealedEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_ORIGINAL_ENTITY, "unknown");
        entityData.define(DATA_GLOW_COLOR, 0xFFFFAA);
    }

    @Override
    public void tick() {
        super.tick();
        age++;

        // 輕微浮動動畫
        double floatY = Math.sin(age * 0.05) * 0.02;
        setDeltaMovement(0, floatY, 0);

        // 發光粒子（客戶端）
        if (level().isClientSide && age % 5 == 0) {
            spawnGlowParticles();
        }

        // 玩家靠近自動吸引
        if (!level().isClientSide) {
            Player nearest = level().getNearestPlayer(this, PICKUP_RANGE);
            if (nearest != null) {
                // 飛向玩家
                Vec3 diff = nearest.position().subtract(position()).normalize().scale(0.3);
                setDeltaMovement(diff);
                if (distanceTo(nearest) < 0.8) {
                    pickup(nearest);
                }
            }
        }
    }

    private void pickup(Player player) {
        level().playSound(null, blockPosition(), ModSounds.ENTITY_SEAL_FORM.get(),
                SoundSource.NEUTRAL, 1.0f, 1.2f);
        // 給予玩家對應封印物物品
        net.minecraft.world.item.ItemStack sealItem = createSealItem();
        if (!player.addItem(sealItem)) {
            // 物品欄滿了，掉在地上
            ItemEntity drop = new ItemEntity(level(), getX(), getY(), getZ(), sealItem);
            level().addFreshEntity(drop);
        }
        this.discard();
    }

    private net.minecraft.world.item.ItemStack createSealItem() {
        // 根據原始實體類型返回對應封印物
        var items = com.cocojenna.init.ModItems.class;
        String original = getOriginalEntity();
        net.minecraft.world.item.Item sealItem = switch (original) {
            case "samurai_cat" -> com.cocojenna.init.ModItems.SAMURAI_SEAL.get();
            case "general_cat" -> com.cocojenna.init.ModItems.GENERAL_SEAL.get();
            default -> com.cocojenna.init.ModItems.SEAL_ORB.get();
        };
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(sealItem);
        // 在 NBT 中記錄原始實體 ID
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("SealedEntity", original);
        return stack;
    }

    private void spawnGlowParticles() {
        int color = entityData.get(DATA_GLOW_COLOR);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        level().addParticle(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                getX() + (random.nextDouble() - 0.5) * 0.5,
                getY() + random.nextDouble() * 0.5,
                getZ() + (random.nextDouble() - 0.5) * 0.5,
                0, 0.05, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(DATA_ORIGINAL_ENTITY, tag.getString("OriginalEntity"));
        entityData.set(DATA_GLOW_COLOR, tag.getInt("GlowColor"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("OriginalEntity", getOriginalEntity());
        tag.putInt("GlowColor", entityData.get(DATA_GLOW_COLOR));
    }

    public void setOriginalEntity(String id) {
        entityData.set(DATA_ORIGINAL_ENTITY, id);
        // 根據實體類型設定顏色
        int color = switch (id) {
            case "samurai_cat" -> 0xFF4444;
            case "sumo_cat"    -> 0xFF8844;
            case "court_lady"  -> 0xCC44FF;
            case "monk_cat"    -> 0xFFAA44;
            case "general_cat" -> 0xFFDD00;
            default            -> 0xAADDFF;
        };
        entityData.set(DATA_GLOW_COLOR, color);
    }

    public String getOriginalEntity() { return entityData.get(DATA_ORIGINAL_ENTITY); }

    @Override
    public boolean isPickable() { return true; }
}
