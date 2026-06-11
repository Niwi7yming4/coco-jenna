package com.cocojenna.world.ruin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;

/** 九優先遺跡 lectern 相對座標（對照 gen_loot_and_ruin_nbt.py）. */
public final class RuinLecternRegistry {

    private record LecternDef(String ruinId, List<BlockPos> relative, String messageKey) {}

    private static final List<LecternDef> DEFS = List.of(
            new LecternDef("outpost", List.of(new BlockPos(0, 2, 1), new BlockPos(0, 5, 0)),
                    "ruin.cocojenna.outpost.lectern"),
            new LecternDef("moon_sealed_dungeon", List.of(new BlockPos(0, 4, 0)),
                    "ruin.cocojenna.moon_dungeon.lectern"),
            new LecternDef("black_mud_contaminated_temple", List.of(new BlockPos(2, 1, 1)),
                    "ruin.cocojenna.mud_temple.lectern"),
            new LecternDef("velvet_tower", List.of(new BlockPos(1, 2, 1)),
                    "ruin.cocojenna.velvet_tower.lectern"),
            new LecternDef("ironpaw_forge_ruins", List.of(new BlockPos(0, 1, 2)),
                    "ruin.cocojenna.ironpaw_forge.lectern"),
            new LecternDef("war_ruins", List.of(new BlockPos(-1, 1, 1)),
                    "ruin.cocojenna.war_ruins.lore"),
            new LecternDef("forgotten_altar", List.of(new BlockPos(0, 1, -1)),
                    "ruin.cocojenna.forgotten_altar.lore"),
            new LecternDef("stray_cat_canteen", List.of(new BlockPos(0, 1, -1)),
                    "ruin.cocojenna.stray_cat_canteen.lore"),
            new LecternDef("abandoned_toy_vault", List.of(new BlockPos(-1, 1, -1)),
                    "ruin.cocojenna.abandoned_toy_vault.lore"),
            new LecternDef("fallen_heroes_monument", List.of(new BlockPos(-2, 1, 0)),
                    "ruin.cocojenna.fallen_heroes_monument.lore"),
            new LecternDef("scratching_barricade", List.of(new BlockPos(-2, 1, 1)),
                    "ruin.cocojenna.scratching_barricade.lore"),
            new LecternDef("mortar_position", List.of(new BlockPos(1, 1, 1)),
                    "ruin.cocojenna.mortar_position.lore")
    );

    private static final Map<String, LecternDef> BY_ID = DEFS.stream()
            .collect(java.util.stream.Collectors.toMap(LecternDef::ruinId, d -> d));

    private RuinLecternRegistry() {}

    public static boolean tryInteract(ServerPlayer player, ServerLevel level, BlockPos lecternPos) {
        if (!level.getBlockState(lecternPos).is(Blocks.LECTERN)) return false;
        var data = RuinMatrixSavedData.get(level);
        var ruinOpt = data.ruinAt(lecternPos, 48);
        if (ruinOpt.isEmpty()) return false;
        LecternDef def = BY_ID.get(ruinOpt.get().ruinId());
        if (def == null) return false;
        BlockPos origin = ruinOpt.get().origin();
        BlockPos rel = lecternPos.subtract(origin);
        for (BlockPos expected : def.relative()) {
            if (expected.equals(rel)) {
                handleLectern(player, level, ruinOpt.get().ruinId(), def.messageKey(), lecternPos);
                return true;
            }
        }
        return false;
    }

    private static void handleLectern(ServerPlayer player, ServerLevel level, String ruinId,
            String messageKey, BlockPos pos) {
        player.displayClientMessage(Component.translatable(messageKey), false);
        RuinMatrixRegistry.byId(ruinId).ifPresent(ruin -> {
            if ("moon_sealed_dungeon".equals(ruinId)) {
                if (RuinInteractionRegistry.tryMoonDungeonDoor(level, pos)) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.GLOWING, 100, 0));
                }
            }
            if ("black_mud_contaminated_temple".equals(ruinId)) {
                RuinInteractionRegistry.tryPurifyMud(level, pos.relative(net.minecraft.core.Direction.DOWN), player);
            }
        });
        com.cocojenna.exploration.ExplorationManager.logExploration(player,
                "explore.cocojenna.ruin.lectern." + ruinId);
    }

    public static List<BlockPos> relativeLecterns(String ruinId) {
        LecternDef def = BY_ID.get(ruinId);
        return def == null ? List.of() : def.relative();
    }
}
