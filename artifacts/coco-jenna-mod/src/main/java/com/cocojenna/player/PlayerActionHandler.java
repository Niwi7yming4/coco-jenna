package com.cocojenna.player;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.AbstractCatEntity;
import com.cocojenna.entity.CocoEntity;
import com.cocojenna.entity.JennaEntity;
import com.cocojenna.init.ModItems;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenMemoryBookPacket;
import com.cocojenna.network.SyncBondDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PlayerActionHandler {

    private static final long DASH_COOLDOWN = 40L;

    private PlayerActionHandler() {}

    public static void openMemoryBook(ServerPlayer player) {
        com.cocojenna.sequence.HiddenSequenceTriggers.onMemoryBookOpen(player);
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new OpenMemoryBookPacket());
    }

    public static void toggleFollow(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        int next = (bond.getFollowDistance() + 1) % 3;
        bond.setFollowDistance(next);
        player.displayClientMessage(Component.translatable(
                "message.cocojenna.follow_mode." + next), true);
        sync(player, bond);
    }

    public static void recallCats(ServerPlayer player) {
        if (!ModCapabilities.getOrDefault(player).isGuardian()) {
            player.displayClientMessage(Component.translatable(
                    "message.cocojenna.recall_not_guardian"), true);
            return;
        }
        AABB box = player.getBoundingBox().inflate(128);
        for (AbstractCatEntity cat : player.serverLevel().getEntitiesOfClass(
                AbstractCatEntity.class, box, c -> player.getUUID().equals(c.getOwnerUUID()))) {
            cat.teleportTo(player.getX(), player.getY(), player.getZ());
            cat.getNavigation().stop();
        }
        player.displayClientMessage(Component.translatable("message.cocojenna.recall_done"), true);
    }

    public static void performRadialAction(ServerPlayer player, int action) {
        switch (action) {
            case com.cocojenna.network.CatRadialActionPacket.PET -> interactNearestCat(player);
            case com.cocojenna.network.CatRadialActionPacket.FEED -> feedNearestCat(player);
            case com.cocojenna.network.CatRadialActionPacket.GROOM -> groomNearestCat(player);
            case com.cocojenna.network.CatRadialActionPacket.PLAY -> playNearestCat(player);
            case com.cocojenna.network.CatRadialActionPacket.FOLLOW -> toggleFollow(player);
        }
    }

    public static void interactNearestCat(ServerPlayer player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isAllowAffection()) {
            player.displayClientMessage(Component.translatable(
                    "message.cocojenna.affection_disabled"), true);
            return;
        }
        AbstractCatEntity nearest = null;
        double best = 6.0 * 6.0;
        AABB box = player.getBoundingBox().inflate(6);
        for (AbstractCatEntity cat : player.serverLevel().getEntitiesOfClass(
                AbstractCatEntity.class, box, c -> player.getUUID().equals(c.getOwnerUUID()))) {
            double d = cat.distanceToSqr(player);
            if (d < best) {
                best = d;
                nearest = cat;
            }
        }
        if (nearest != null && nearest.onPet(player)) {
            player.displayClientMessage(Component.translatable(
                    "message.cocojenna.pet_success"), true);
        }
    }

    public static void dash(ServerPlayer player) {
        long now = player.level().getGameTime();
        long cd = player.getPersistentData().getLong("cocojenna_dash_cd");
        if (now < cd) return;
        player.getPersistentData().putLong("cocojenna_dash_cd", now + DASH_COOLDOWN);
        Vec3 look = player.getLookAngle();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 2, false, false, true));
        player.setDeltaMovement(look.x * 1.2, 0.15, look.z * 1.2);
        player.hurtMarked = true;
    }

    private static AbstractCatEntity findNearestOwnedCat(ServerPlayer player) {
        AbstractCatEntity nearest = null;
        double best = 8.0 * 8.0;
        for (AbstractCatEntity cat : player.serverLevel().getEntitiesOfClass(
                AbstractCatEntity.class, player.getBoundingBox().inflate(8),
                c -> player.getUUID().equals(c.getOwnerUUID()))) {
            double d = cat.distanceToSqr(player);
            if (d < best) {
                best = d;
                nearest = cat;
            }
        }
        return nearest;
    }

    private static void feedNearestCat(ServerPlayer player) {
        AbstractCatEntity cat = findNearestOwnedCat(player);
        if (cat == null) {
            player.displayClientMessage(Component.translatable("message.cocojenna.no_cat_near"), true);
            return;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof com.cocojenna.item.CatFoodItem food) {
                if (cat instanceof CocoEntity coco && food.feedCoco(coco, player, stack)) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    player.displayClientMessage(Component.translatable("message.cocojenna.feed_success"), true);
                    return;
                }
                if (cat instanceof JennaEntity jenna && food.feedJenna(jenna, player, stack)) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    player.displayClientMessage(Component.translatable("message.cocojenna.feed_success"), true);
                    return;
                }
            }
        }
        player.displayClientMessage(Component.translatable("message.cocojenna.feed_need_food"), true);
    }

    private static void groomNearestCat(ServerPlayer player) {
        AbstractCatEntity cat = findNearestOwnedCat(player);
        if (cat == null) {
            player.displayClientMessage(Component.translatable("message.cocojenna.no_cat_near"), true);
            return;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof com.cocojenna.item.GroomingBrushItem brush) {
                if (cat.canGroomToday()) {
                    brush.groom(cat, player, stack);
                    player.displayClientMessage(Component.translatable("message.cocojenna.groom_success"), true);
                }
                return;
            }
        }
        player.displayClientMessage(Component.translatable("message.cocojenna.groom_need_brush"), true);
    }

    private static void playNearestCat(ServerPlayer player) {
        AbstractCatEntity cat = findNearestOwnedCat(player);
        if (cat == null) {
            player.displayClientMessage(Component.translatable("message.cocojenna.no_cat_near"), true);
            return;
        }
        BondData bond = ModCapabilities.getOrDefault(player);
        if (cat instanceof JennaEntity) {
            bond.modifyJennaEmotion(2f);
            bond.modifyJennaPlayfulness(3f);
            player.displayClientMessage(Component.translatable("message.cocojenna.play_jenna"), true);
        } else {
            bond.modifyCocoEmotion(1.5f);
            player.displayClientMessage(Component.translatable("message.cocojenna.play_coco"), true);
        }
        sync(player, bond);
    }

    public static boolean hasMemoryBook(ServerPlayer player) {
        for (var stack : player.getInventory().items) {
            if (stack.is(ModItems.MEMORY_BOOK.get())) return true;
        }
        return false;
    }

    private static void sync(ServerPlayer player, BondData bond) {
        ModNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncBondDataPacket(bond.serializeNBT()));
    }
}
