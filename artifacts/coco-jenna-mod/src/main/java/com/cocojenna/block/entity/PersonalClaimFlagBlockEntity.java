package com.cocojenna.block.entity;

import com.cocojenna.block.PersonalClaimFlagBlock;
import com.cocojenna.init.ModBlockEntities;
import com.cocojenna.kingdom.multiplayer.PersonalClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class PersonalClaimFlagBlockEntity extends BlockEntity {

    private UUID owner;

    public PersonalClaimFlagBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PERSONAL_CLAIM_FLAG.get(), pos, state);
    }

    public InteractionResult onUse(Player player) {
        if (level == null || level.isClientSide) return InteractionResult.SUCCESS;
        if (owner == null) {
            owner = player.getUUID();
            PersonalClaimSavedData claims = PersonalClaimSavedData.get(
                    (net.minecraft.server.level.ServerLevel) level);
            if (claims.tryPlaceClaim(owner, worldPosition)) {
                player.displayClientMessage(Component.translatable(
                        "kingdom.cocojenna.claim_placed"), true);
                setChanged();
                return InteractionResult.CONSUME;
            }
            owner = null;
            player.displayClientMessage(Component.translatable(
                    "kingdom.cocojenna.claim_failed"), true);
            return InteractionResult.FAIL;
        }
        if (owner.equals(player.getUUID()) && player.isShiftKeyDown()) {
            PersonalClaimSavedData claims = PersonalClaimSavedData.get(
                    (net.minecraft.server.level.ServerLevel) level);
            if (player.getMainHandItem().isEmpty()) {
                player.displayClientMessage(Component.translatable(
                        "kingdom.cocojenna.claim_owned"), true);
            } else {
                var look = player.getLookAngle();
                player.displayClientMessage(Component.translatable(
                        "kingdom.cocojenna.claim_info", worldPosition.getX(), worldPosition.getZ()), true);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (owner != null) tag.putUUID("owner", owner);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("owner")) owner = tag.getUUID("owner");
    }
}
