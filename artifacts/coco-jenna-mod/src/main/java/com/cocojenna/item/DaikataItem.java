package com.cocojenna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 大快刀 (Daikata) — 傳說武器基礎類別。
 *
 * <p>每把大快刀擁有獨特技能（{@link Skill}）。
 * 按住右鍵蓄力 40 tick 後釋放技能，冷卻 100 tick。
 */
public class DaikataItem extends SwordItem {

    public enum Skill {
        SUMMON_WARRIOR,    // 虎鐵：召喚貓武士協助戰鬥 30 秒
        DASH_SLASH,        // 風切：衝刺 5 格斬擊
        PHANTOM_STEP,      // 紫苑：殘影瞬移 8 格
        STILL_DOMAIN,      // 玄德：靜止領域（範圍跳躍力 -70%）
        GROUND_SLAM,       // 力士：震地擊飛
        CRESCENT_SLASH,    // 月牙：弧形月牙波
        STAR_GUIDE,        // 星圖：指引前往目標的路徑
        WATER_PRISON,      // 盲水：創造水牢困住目標
        NEON_BARRAGE,      // 霓虹：連續 5 段攻擊
        MECHANICAL_FRENZY, // 齒輪王：機械狂暴（攻速 +50% 10 秒）
        BLOOD_TIDE,        // 朱槿終極：召喚血浪
        GORGE_WIND,        // 嚎風：強力擊退
        DAWN_BURST,        // 初晴：光爆傷害
        ROYAL_DECREE,      // 王權：強制目標停止攻擊
        SHADOW_STRIKE,     // 影仿：影子突刺
        SILENT_SHIELD,     // 沉默：格擋護盾
        TWILIGHT_SLASH,    // 黃昏：暮光連斬
        TRADE_ROUTE,       // 白手套：呼叫特殊商人
        TOWER_COLLAPSE,    // 高塔：高空轟擊
        VILLAGE_BOND,      // 村魂：呼叫 NPC 援助
    }

    private final Skill skill;
    private final String variantId;

    public DaikataItem(Tier tier, int dmgBonus, float atkSpeed, Properties props,
            String variantId, Skill skill) {
        super(tier, dmgBonus, atkSpeed, props.rarity(Rarity.EPIC));
        this.variantId = variantId;
        this.skill = skill;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return super.use(level, player, hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        int chargeTime = getUseDuration(stack) - timeLeft;
        if (chargeTime < 40) return; // 未蓄力足

        if (!level.isClientSide) {
            performSkill(player, level);
        }
        // 播放技能音效
        level.playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.5f, 0.6f);
    }

    private void performSkill(Player player, Level level) {
        switch (skill) {
            case DASH_SLASH -> {
                Vec3 dir = player.getLookAngle().normalize().scale(5.0);
                player.setDeltaMovement(dir.x, 0.1, dir.z);
                player.hurtMarked = true;
                // 對路徑上的敵人造成傷害
                level.getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().expandTowards(dir).inflate(1.0),
                                e -> e != player)
                        .forEach(e -> e.hurt(level.damageSources().playerAttack(player), getDamageValue()));
            }
            case GROUND_SLAM -> {
                level.getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().inflate(5.0), e -> e != player)
                        .forEach(e -> {
                            e.setDeltaMovement(0, 1.5, 0);
                            e.hurt(level.damageSources().playerAttack(player), getDamageValue() * 0.8f);
                        });
            }
            case PHANTOM_STEP -> {
                double angle = player.getYRot() * Math.PI / 180;
                player.teleportTo(player.getX() - Math.sin(angle) * 8,
                        player.getY(), player.getZ() + Math.cos(angle) * 8);
            }
            case DAWN_BURST -> {
                level.getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().inflate(6.0), e -> e != player)
                        .forEach(e -> {
                            e.hurt(level.damageSources().playerAttack(player), getDamageValue() * 1.5f);
                            e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    net.minecraft.world.effect.MobEffects.BLINDNESS, 60, 0));
                        });
            }
            default -> {
                // 其他技能：通用光爆效果
                level.addParticle(net.minecraft.core.particles.ParticleTypes.FLASH,
                        player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
            }
        }
    }

    private float getDamageValue() {
        return (float) (getAttackDamage() + 4); // 包含基礎攻擊
    }

    @Override
    public int getUseDuration(ItemStack stack) { return 72000; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.cocojenna.daikata.skill",
                Component.translatable("skill.cocojenna." + skill.name().toLowerCase()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.cocojenna.daikata.charge_hint")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public Skill getSkill() { return skill; }
    public String getVariantId() { return variantId; }
}
