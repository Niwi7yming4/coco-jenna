package com.cocojenna.world.ruin;

import com.cocojenna.CocoJennaMod;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Optional;

/** 遺跡矩陣 — 30 種結構 + outpost 基準. */
public enum RuinMatrixRegistry {

    OUTPOST("outpost", RuinPlacement.BOTH, RuinSource.NBT),
    WAR_RUINS("war_ruins", RuinPlacement.OVERWORLD, RuinSource.NBT),
    SCRATCHING_BARRICADE("scratching_barricade", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    VELVET_TOWER("velvet_tower", RuinPlacement.BOTH, RuinSource.NBT),
    MORTAR_POSITION("mortar_position", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    FALLEN_HEROES_MONUMENT("fallen_heroes_monument", RuinPlacement.OVERWORLD, RuinSource.NBT),
    FORGOTTEN_ALTAR("forgotten_altar", RuinPlacement.BOTH, RuinSource.NBT),
    BLACK_MUD_CONTAMINATED_TEMPLE("black_mud_contaminated_temple", RuinPlacement.OVERWORLD, RuinSource.NBT),
    MOON_SEALED_DUNGEON("moon_sealed_dungeon", RuinPlacement.BOTH, RuinSource.NBT),
    FULL_MOON_STARGAZING_WELL("full_moon_stargazing_well", RuinPlacement.BOTH, RuinSource.NBT),
    MEMORY_STONE_CIRCLE("memory_stone_circle", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    HOLY_WATER_POTION_LAB("holy_water_potion_lab", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    BLACK_MUD_FARM("black_mud_farm", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    CARDBOARD_REFUGEE_CAMP("cardboard_refugee_camp", RuinPlacement.BOTH, RuinSource.NBT),
    CATNIP_SMUGGLING_GREENHOUSE("catnip_smuggling_greenhouse", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    ABANDONED_GEAR_WORKSHOP("abandoned_gear_workshop", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    BLIND_WATER_SEWER("blind_water_sewer", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    IRONPAW_FORGE_RUINS("ironpaw_forge_ruins", RuinPlacement.BOTH, RuinSource.NBT),
    ALIENATED_CAT_TREE_TOWER("alienated_cat_tree_tower", RuinPlacement.CAT_KINGDOM, RuinSource.PROCEDURAL),
    VELVET_TREEHOUSE_REMAINS("velvet_treehouse_remains", RuinPlacement.CAT_KINGDOM, RuinSource.PROCEDURAL),
    ROYAL_LITTER_BOX_BASIN("royal_litter_box_basin", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    GIANT_YARN_BALL_NEST("giant_yarn_ball_nest", RuinPlacement.CAT_KINGDOM, RuinSource.PROCEDURAL),
    STRAY_CAT_CANTEEN("stray_cat_canteen", RuinPlacement.BOTH, RuinSource.NBT),
    ABANDONED_TOY_VAULT("abandoned_toy_vault", RuinPlacement.CAT_KINGDOM, RuinSource.NBT),
    CRASHED_VELVET_AIRSHIP("crashed_velvet_airship", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    ABYSS_SEAL_ANCHOR("abyss_seal_anchor", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    GIANT_CAT_SKELETON_RUINS("giant_cat_skeleton_ruins", RuinPlacement.OVERWORLD, RuinSource.PROCEDURAL),
    TEMPORAL_RIFT_PORTAL("temporal_rift_portal", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    GREAT_WALL_SEGMENT("great_wall_segment", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    PHANTOM_CLOCK_TOWER("phantom_clock_tower", RuinPlacement.BOTH, RuinSource.PROCEDURAL),
    VELVET_ALTAR_RUINS("velvet_altar_ruins", RuinPlacement.BOTH, RuinSource.NBT);

    public enum RuinPlacement { OVERWORLD, CAT_KINGDOM, BOTH }
    public enum RuinSource { NBT, PROCEDURAL }

    private final String id;
    private final RuinPlacement placement;
    private final RuinSource source;

    RuinMatrixRegistry(String id, RuinPlacement placement, RuinSource source) {
        this.id = id;
        this.placement = placement;
        this.source = source;
    }

    public String id() { return id; }

    public ResourceLocation structureId() {
        return new ResourceLocation(CocoJennaMod.MOD_ID, "ruins/" + id);
    }

    public ResourceLocation lootTable() {
        return new ResourceLocation(CocoJennaMod.MOD_ID, "structures/ruins/" + id);
    }

    public RuinPlacement placement() { return placement; }

    public RuinSource source() { return source; }

    public static Optional<RuinMatrixRegistry> byId(String id) {
        return Arrays.stream(values()).filter(r -> r.id.equals(id)).findFirst();
    }

    public static RuinMatrixRegistry roll(net.minecraft.util.RandomSource random, boolean catKingdom) {
        RuinMatrixRegistry[] pool = Arrays.stream(values())
                .filter(r -> catKingdom
                        ? r.placement == RuinPlacement.CAT_KINGDOM || r.placement == RuinPlacement.BOTH
                        : r.placement == RuinPlacement.OVERWORLD || r.placement == RuinPlacement.BOTH)
                .toArray(RuinMatrixRegistry[]::new);
        return pool[random.nextInt(pool.length)];
    }
}
