package com.cocojenna.quest.qin;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.init.ModBlocks;
import com.cocojenna.quest.QinKemuQuestManager;
import com.cocojenna.weapon.WeaponData;
import com.cocojenna.weapon.WeaponUnsealManager;
import com.cocojenna.world.qin.MausoleumType;
import com.cocojenna.world.qin.MausoleumVariant;
import com.cocojenna.world.ruin.RuinMatrixSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

/** 皇陵探索 — 剪開紅紙封條、記錄發現、紙系武器共鳴加成. */
public final class MausoleumDiscoveryHandler {

    private MausoleumDiscoveryHandler() {}

    public static boolean tryDiscover(net.minecraft.server.level.ServerPlayer player, BlockPos pos, BlockState state) {
        if (!state.is(ModBlocks.SUSPICIOUS_WALL.get())) return false;
        if (!player.getMainHandItem().is(Items.SHEARS)) return false;
        if (!(player.level() instanceof ServerLevel level)) return false;

        var ruin = RuinMatrixSavedData.get(level).ruinAt(pos, 14);
        if (ruin.isEmpty() || !"mausoleum".equals(ruin.get().ruinId())) {
            QinKemuClueManager.onRedPaperScrapFound(player, ModCapabilities.getOrDefault(player));
            return false;
        }

        MausoleumType type = resolveType(ruin.get().variant());
        if (type == null) return false;

        BondData bond = ModCapabilities.getOrDefault(player);
        if (!bond.isMausoleumDiscovered(type.bit())) {
            QinKemuQuestManager.onMausoleumFound(player, bond, type.bit());
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                var stack = player.getInventory().getItem(i);
                if (WeaponData.isUnsealable(stack)) {
                    WeaponUnsealManager.applyPaperWeaponMausoleumBonus(stack);
                }
            }
            if (type == MausoleumType.SLEEPING_CHAMBER) {
                QinKemuClueManager.markSleepingChamber(player, pos.below(4));
            }
            QinKemuClueManager.grantClueItem(player);
        }
        return true;
    }

    private static MausoleumType resolveType(String variantId) {
        MausoleumVariant variant = MausoleumVariant.byId(variantId);
        if (variant != null) return variant.type();
        for (MausoleumType type : MausoleumType.values()) {
            if (variantId.startsWith(type.id())) return type;
        }
        return null;
    }
}
