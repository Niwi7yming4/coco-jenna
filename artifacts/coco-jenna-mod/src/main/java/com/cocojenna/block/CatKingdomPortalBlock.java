package com.cocojenna.block;

import com.cocojenna.guide.GuardianGuideManager;
import com.cocojenna.init.ModDimensions;
import com.cocojenna.world.FirstCryVillageGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/** 貓之國傳送門 — 主世界與貓之國雙向傳送。 */
public class CatKingdomPortalBlock extends Block {

    public CatKingdomPortalBlock(Properties props) {
        super(props);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }
        if (!player.isAlive() || player.isCrouching() || player.isOnPortalCooldown()) {
            return;
        }

        ServerLevel destination;
        BlockPos target;
        if (level.dimension().equals(ModDimensions.CAT_KINGDOM)) {
            destination = player.server.getLevel(Level.OVERWORLD);
            if (destination == null) {
                return;
            }
            BlockPos spawn = destination.getSharedSpawnPos();
            target = spawn.above();
        } else {
            destination = player.server.getLevel(ModDimensions.CAT_KINGDOM);
            if (destination == null) {
                return;
            }
            target = FirstCryVillageGenerator.ensureVillage(destination, player);
        }

        player.changeDimension(destination, new KingdomTeleporter(target));
        player.setPortalCooldown();
    }

    /** 具名內部類，避免匿名類在執行期載入失敗。 */
    private static final class KingdomTeleporter implements ITeleporter {
        private final BlockPos destPos;

        KingdomTeleporter(BlockPos destPos) {
            this.destPos = destPos;
        }

        @Override
        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld,
                float yaw, Function<Boolean, Entity> repositionEntity) {
            Entity result = repositionEntity.apply(false);
            result.teleportTo(destPos.getX() + 0.5, destPos.getY(), destPos.getZ() + 0.5);
            if (result instanceof ServerPlayer sp) {
                sp.setYRot(180.0F);
                if (destWorld.dimension().equals(ModDimensions.CAT_KINGDOM)) {
                    GuardianGuideManager.onFirstEnterCatKingdom(sp);
                    com.cocojenna.overworld.PenetrationQuestManager.onEnteredCatKingdom(sp);
                    com.cocojenna.sequence.MoonCrossroadsManager.onEnterKingdom(sp);
                }
            }
            return result;
        }
    }
}
