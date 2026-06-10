package com.cocojenna.block.entity;

import com.cocojenna.entity.*;
import com.cocojenna.init.ModBlockEntities;
import com.cocojenna.init.ModEntities;
import com.cocojenna.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SealPedestalBlockEntity extends BlockEntity {

    private ItemStack seal = ItemStack.EMPTY;
    private String sealedEntityId = "";

    public SealPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEAL_PEDESTAL.get(), pos, state);
    }

    public boolean placeSeal(ItemStack stack) {
        if (!stack.is(ModItems.SEAL_ORB.get()) && !stack.is(ModItems.SAMURAI_SEAL.get())
                && !stack.is(ModItems.GENERAL_SEAL.get())) {
            return false;
        }
        if (!seal.isEmpty()) return false;
        seal = stack.split(1);
        if (seal.hasTag() && seal.getTag().contains("SealedEntity")) {
            sealedEntityId = seal.getTag().getString("SealedEntity");
        } else {
            sealedEntityId = "";
        }
        setChanged();
        return true;
    }

    public boolean tryRevive(Player player) {
        if (seal.isEmpty() || !(level instanceof ServerLevel server)) return false;
        String entityId = sealedEntityId;
        if (seal.hasTag() && seal.getTag().contains("SealedEntity")) {
            entityId = seal.getTag().getString("SealedEntity");
        }
        if (!spawnFriendly(server, entityId, worldPosition.above())) {
            return false;
        }
        seal = ItemStack.EMPTY;
        sealedEntityId = "";
        setChanged();
        player.displayClientMessage(Component.translatable("block.cocojenna.seal_pedestal.revived"), true);
        return true;
    }

    private static boolean spawnFriendly(ServerLevel server, String entityId, BlockPos pos) {
        return switch (entityId) {
            case "samurai_cat" -> spawnAt(server, ModEntities.SAMURAI_CAT.get(), pos);
            case "sumo_cat" -> spawnAt(server, ModEntities.SUMO_CAT.get(), pos);
            case "court_lady", "court_lady_cat" -> spawnAt(server, ModEntities.COURT_LADY_CAT.get(), pos);
            case "monk_cat" -> spawnAt(server, ModEntities.MONK_CAT.get(), pos);
            case "general_cat" -> spawnAt(server, ModEntities.GENERAL_CAT.get(), pos);
            default -> false;
        };
    }

    private static boolean spawnAt(ServerLevel server, net.minecraft.world.entity.EntityType<? extends PathfinderMob> type,
            BlockPos pos) {
        var entity = type.create(server);
        if (entity == null) {
            return false;
        }
        entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        server.addFreshEntity(entity);
        return true;
    }

    public boolean hasSeal() { return !seal.isEmpty(); }

    public ItemStack removeSeal() {
        if (seal.isEmpty()) return ItemStack.EMPTY;
        ItemStack removed = seal.copy();
        seal = ItemStack.EMPTY;
        sealedEntityId = "";
        setChanged();
        return removed;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!seal.isEmpty()) tag.put("Seal", seal.save(new CompoundTag()));
        if (!sealedEntityId.isEmpty()) tag.putString("SealedEntityId", sealedEntityId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        seal = tag.contains("Seal") ? ItemStack.of(tag.getCompound("Seal")) : ItemStack.EMPTY;
        sealedEntityId = tag.getString("SealedEntityId");
    }
}
