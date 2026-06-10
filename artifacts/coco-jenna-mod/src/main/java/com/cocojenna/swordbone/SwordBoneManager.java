package com.cocojenna.swordbone;

import com.cocojenna.blackmud.BlackMudSavedData;
import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.gear.SetBonusHelper;
import com.cocojenna.item.DaikataItem;
import com.cocojenna.item.RyokatanaRegistry;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.SyncBondDataPacket;
import com.cocojenna.world.ForgottenTowerGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

/** 劍骨系統核心 — 設計書第一章. */
public final class SwordBoneManager {

    private static final int DEATH_SAVE_COOLDOWN = 72000;
    private static final int RESONANCE_COOLDOWN = 6000;
    private static final int RESONANCE_DURATION = 160;
    private static final Set<String> VELVET_FOUR = Set.of(
            "grief_amalgam", "fallen_velvet", "howling_squall", "ashura_phantom");

    private SwordBoneManager() {}

    public static int maxSlots(BondData bond, ServerLevel level) {
        if (!bond.isSwordBoneAwakened()) return 0;
        int max = 1;
        if (bond.getFelineTier() <= 6
                && (bond.getCocoEmotionLevel().ordinal() >= BondData.EmotionLevel.BONDED.ordinal()
                || bond.getJennaEmotionLevel().ordinal() >= BondData.EmotionLevel.BONDED.ordinal())) {
            max = 3;
        }
        if (bond.getFelineTier() <= 3 && defeatedAnyVelvetFour(level)) {
            max = 5;
        }
        if (bond.getFelineTier() <= 1 && bond.getSisterBond() > 80f) {
            max = 7;
        }
        if (bond.isSwordBoneSupreme()) {
            max = 9;
        }
        return max;
    }

    public static boolean canAwaken(BondData bond) {
        return !bond.isSwordBoneAwakened() && bond.getCollectedRyokatana().size() >= 10;
    }

    public static boolean tryAwaken(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!canAwaken(bond)) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.need_collection")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        bond.setSwordBoneAwakened(true);
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.awakened")
                .withStyle(ChatFormatting.GOLD), false);
        sync(player);
        return true;
    }

    public static boolean trySupremeRite(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.isSwordBoneSupreme()) return false;
        if (bond.getCollectedRyokatana().size() < 50) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.need_all_ryokatana")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (player.blockPosition().distSqr(ForgottenTowerGenerator.CENTER) > 20 * 20) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.need_tower")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }
        bond.setSwordBoneSupreme(true);
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.supreme_title")
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        sync(player);
        return true;
    }

    public static boolean canInsert(ServerPlayer player, ItemStack weapon) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isSwordBoneAwakened()) return false;
        if (bond.getSwordBones().size() >= maxSlots(bond, player.serverLevel())) return false;
        return classify(weapon) != null;
    }

    public static boolean insert(ServerPlayer player, ItemStack weapon) {
        String id = weaponId(weapon);
        if (id.isEmpty() || !canInsert(player, weapon)) return false;
        BondData bond = ModCapabilities.getOrDefault(player);
        bond.addSwordBone(new SwordBoneEntry(id, false));
        weapon.shrink(1);
        WeaponMemoryRegistry.tryUnlock(player, id, bond);
        trackRyokatana(bond, id);
        player.serverLevel().playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6f, 1.4f);
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.inserted",
                Component.translatable("item.cocojenna." + id)), true);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 4, false, false));
        sync(player);
        reactNpc(player, bond);
        return true;
    }

    public static float damageMultiplier(BondData bond) {
        float mult = 1f;
        for (SwordBoneEntry entry : bond.getSwordBones()) {
            if (entry.damaged()) continue;
            SwordBoneRarity rarity = rarityOf(entry.weaponId());
            mult += rarity.attackBonus;
        }
        if (bond.getSwordBones().size() >= 9 && bond.getSwordBones().stream().noneMatch(SwordBoneEntry::damaged)) {
            mult += 0.20f;
        }
        if (bond.isSwordBoneSupreme()) {
            mult += 0.05f;
        }
        return mult;
    }

    public static float moveMultiplier(BondData bond) {
        float bonus = 0f;
        for (SwordBoneEntry entry : bond.getSwordBones()) {
            if (entry.damaged()) continue;
            bonus += rarityOf(entry.weaponId()).specialBonus * 0.3f;
        }
        return 1f + bonus;
    }

    public static boolean tryDeathSave(ServerPlayer player, float damage) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (player.getHealth() - damage > 0f) return false;
        if (bond.getSwordBones().size() < 9) return false;
        if (player.level().getGameTime() < bond.getSwordBoneDeathSaveCd()) return false;
        List<SwordBoneEntry> bones = bond.getSwordBones();
        int idx = player.getRandom().nextInt(bones.size());
        SwordBoneEntry broken = bones.get(idx);
        bond.setSwordBoneAt(idx, new SwordBoneEntry(broken.weaponId(), true));
        bond.setSwordBoneDeathSaveCd(player.level().getGameTime() + DEATH_SAVE_COOLDOWN);
        player.setHealth(player.getMaxHealth() * 0.5f);
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.death_save")
                .withStyle(ChatFormatting.AQUA), true);
        sync(player);
        return true;
    }

    public static void tryNineResonance(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (bond.getSwordBones().size() < 9) return;
        if (player.level().getGameTime() < bond.getSwordBoneResonanceCd()) return;
        bond.setSwordBoneResonanceCd(player.level().getGameTime() + RESONANCE_COOLDOWN);
        bond.setSwordBoneResonanceUntil(player.level().getGameTime() + RESONANCE_DURATION);
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.nine_resonance")
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        sync(player);
    }

    public static void tickResonance(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (player.level().getGameTime() > bond.getSwordBoneResonanceUntil()) return;
        ServerLevel level = player.serverLevel();
        if (player.tickCount % 5 != 0) return;
        level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4),
                e -> e != player && e.isAttackable()).forEach(target ->
                target.hurt(player.damageSources().magic(), 2.5f));
        level.sendParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1, player.getZ(), 6, 1.2, 0.6, 1.2, 0.02);
    }

    public static void trackRyokatana(BondData bond, String itemPath) {
        if (itemPath.startsWith("ryokatana_")) {
            bond.collectRyokatana(itemPath.substring("ryokatana_".length()));
        }
    }

    public static void trackItem(BondData bond, ItemStack stack) {
        String id = weaponId(stack);
        if (!id.isEmpty()) {
            trackRyokatana(bond, id);
            if (id.startsWith("daikatana_") || id.startsWith("musou_")) {
                bond.collectRyokatana(id);
            }
        }
    }

    public static SwordBoneRarity classify(ItemStack stack) {
        String id = weaponId(stack);
        return id.isEmpty() ? null : rarityOf(id);
    }

    public static SwordBoneRarity rarityOf(String itemPath) {
        if (itemPath.startsWith("musou_")) return SwordBoneRarity.MUSOU;
        if (itemPath.startsWith("daikatana_")) return SwordBoneRarity.DAIKATANA;
        if (itemPath.startsWith("ryokatana_")) {
            String shortId = itemPath.substring("ryokatana_".length());
            return RyokatanaRegistry.find(shortId)
                    .map(ro -> tierRarity(ro.get()))
                    .orElse(SwordBoneRarity.COMMON);
        }
        return null;
    }

    private static SwordBoneRarity tierRarity(Item item) {
        if (item instanceof com.cocojenna.item.RyokatanaItem r) {
            Tier tier = r.getTier();
            if (tier == Tiers.DIAMOND) return SwordBoneRarity.LEGENDARY;
            if (tier == Tiers.IRON) return SwordBoneRarity.RARE;
        }
        return SwordBoneRarity.COMMON;
    }

    public static String weaponId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        Item item = stack.getItem();
        if (item instanceof com.cocojenna.item.RyokatanaItem
                || item instanceof DaikataItem
                || ForgeRegistries.ITEMS.getKey(item).getPath().startsWith("musou_")) {
            return ForgeRegistries.ITEMS.getKey(item).getPath();
        }
        return "";
    }

    public static boolean unsheath(ServerPlayer player, int slotIndex) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (slotIndex < 0 || slotIndex >= bond.getSwordBones().size()) return false;
        SwordBoneEntry entry = bond.getSwordBones().get(slotIndex);
        bond.removeSwordBone(slotIndex);
        ItemStack damaged = new ItemStack(
                ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation("cocojenna", entry.weaponId())));
        if (!damaged.isEmpty()) {
            damaged.setDamageValue(damaged.getMaxDamage() - 1);
            if (!player.addItem(damaged)) player.drop(damaged, false);
        }
        player.displayClientMessage(Component.translatable("swordbone.cocojenna.unsheathed"), true);
        sync(player);
        return true;
    }

    private static boolean defeatedAnyVelvetFour(ServerLevel level) {
        BlackMudSavedData data = BlackMudSavedData.get(level);
        for (String id : VELVET_FOUR) {
            if (data.isBossDefeated(id)) return true;
        }
        return false;
    }

    private static void reactNpc(ServerPlayer player, BondData bond) {
        int count = bond.getSwordBones().size();
        if (count >= 9) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.npc.alpha")
                    .withStyle(ChatFormatting.GRAY), false);
        } else if (count >= 7) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.npc.jenna")
                    .withStyle(ChatFormatting.LIGHT_PURPLE), false);
        } else if (count >= 5) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.npc.coco")
                    .withStyle(ChatFormatting.RED), false);
        } else if (count >= 3) {
            player.displayClientMessage(Component.translatable("swordbone.cocojenna.npc.ironpaw")
                    .withStyle(ChatFormatting.DARK_GRAY), false);
        }
    }

    public static void sync(ServerPlayer player) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(ModCapabilities.getOrDefault(player).serializeNBT()));
    }

    public static int armorGlowTier(BondData bond) {
        int n = (int) bond.getSwordBones().stream().filter(e -> !e.damaged()).count();
        if (n >= 9) return 4;
        if (n >= 7) return 3;
        if (n >= 5) return 2;
        if (n >= 3) return 1;
        return 0;
    }
}
