package com.cocojenna.item;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.exploration.DungeonRegistry;
import com.cocojenna.exploration.ExplorationManager;
import com.cocojenna.overworld.OverworldPenetrationSavedData;
import com.cocojenna.overworld.OverworldRuinType;
import com.cocojenna.overworld.RuinMapFragmentHelper;
import com.cocojenna.overworld.RuinMapFragmentType;
import com.cocojenna.world.DungeonGenerators;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 貓之國地圖殘頁 — 標記遺跡或合成完整地圖. */
public class MapFragmentItem extends Item {

    public MapFragmentItem(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        RuinMapFragmentType type = RuinMapFragmentHelper.getType(stack);
        return Component.translatable("item.cocojenna.map_fragment")
                .append(" (")
                .append(RuinMapFragmentHelper.typeName(type))
                .append(")");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            RuinMapFragmentType fragType = RuinMapFragmentHelper.getType(stack);
            BondData bond = ModCapabilities.getOrDefault(sp);
            bond.collectRuinMapFragment(fragType);

            OverworldRuinType ruin = fragType.ruin;
            var data = OverworldPenetrationSavedData.get(sp.serverLevel());
            var near = data.findRuinNear(sp.blockPosition(), 1200);
            if (near == ruin || near == null) {
                player.displayClientMessage(Component.translatable(
                        "penetration.cocojenna.map.points_ruin",
                        Component.translatable("penetration.cocojenna.ruin." + ruin.name().toLowerCase())), false);
            } else {
                player.displayClientMessage(Component.translatable(
                        "penetration.cocojenna.map.points_ruin",
                        Component.translatable("penetration.cocojenna.ruin." + ruin.name().toLowerCase())), false);
            }

            String dungeon = findNextDungeon(bond);
            if (dungeon != null) {
                player.displayClientMessage(Component.translatable("explore.cocojenna.map.points_to",
                        Component.translatable("explore.cocojenna.dungeon.name." + dungeon)), false);
            }

            if (countSameType(player, fragType) >= 3) {
                consumeThree(sp, fragType, hand);
                player.displayClientMessage(Component.translatable(
                        "penetration.cocojenna.map.complete",
                        RuinMapFragmentHelper.typeName(fragType)), true);
                ExplorationManager.logExploration(sp, "penetration.cocojenna.map.journal",
                        fragType.name());
            } else {
                ExplorationManager.logExploration(sp, "explore.cocojenna.journal.dungeon", fragType.name());
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static int countSameType(Player player, RuinMapFragmentType type) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (RuinMapFragmentHelper.getType(s) == type) count += s.getCount();
        }
        return count;
    }

    private static void consumeThree(ServerPlayer player, RuinMapFragmentType type, InteractionHand hand) {
        int need = 3;
        for (int i = 0; i < player.getInventory().getContainerSize() && need > 0; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (RuinMapFragmentHelper.getType(s) != type) continue;
            int take = Math.min(need, s.getCount());
            s.shrink(take);
            need -= take;
        }
        ModCapabilities.getOrDefault(player).addOverworldInfluence(5);
    }

    private static String findNextDungeon(BondData bond) {
        for (int i = 0; i < 10; i++) {
            String id = DungeonGenerators.idAt(i);
            int flag = DungeonRegistry.flag(id);
            if (flag != 0 && !bond.hasDungeonCleared(flag)) {
                return id;
            }
        }
        return null;
    }
}
