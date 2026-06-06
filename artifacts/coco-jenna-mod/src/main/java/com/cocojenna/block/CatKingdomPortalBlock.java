package com.cocojenna.block;

import com.cocojenna.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/** 貓之國傳送門虛空方塊 — 玩家接觸後傳送至貓之國維度。 */
public class CatKingdomPortalBlock extends Block {

    public CatKingdomPortalBlock(Properties props) { super(props); }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            if (player.isAlive() && !player.isCrouching()) {
                // 傳送至貓之國
                ServerLevel catKingdom = player.server.getLevel(ModDimensions.CAT_KINGDOM);
                if (catKingdom != null) {
                    player.changeDimension(catKingdom,
                            new net.minecraftforge.common.util.ITeleporter() {
                                @Override
                                public Entity placeEntity(Entity entity, ServerLevel currentWorld,
                                        ServerLevel destWorld, float yaw,
                                        java.util.function.Function<Boolean, Entity> repositionEntity) {
                                    Entity result = repositionEntity.apply(false);
                                    result.setPos(0, 100, 0); // 初始生成點
                                    return result;
                                }
                            });
                }
            }
        }
    }
}
