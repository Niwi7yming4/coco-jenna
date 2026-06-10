package com.cocojenna.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Registered GAL-style dialogue scenes from design doc narratives. */
public final class DialogueScripts {

    private static final Map<String, DialogueScene> SCENES = new HashMap<>();

    static {
        register(new DialogueScene("first_cry_elder_welcome", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.first_cry.narrator_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.first_cry.elder_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.first_cry.elder_2", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.first_cry.elder_3", Portrait.ELDER, null, "elder_met")
        )));

        register(new DialogueScene("first_cry_elder_hint", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.first_cry.elder_hint", Portrait.ELDER)
        )));

        register(new DialogueScene("first_cry_elder_reward", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.first_cry.elder_reward_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.first_cry.elder_reward_2", Portrait.ELDER, null, "quest_complete")
        )));

        register(new DialogueScene("first_cry_samurai_meet", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.samurai",
                        "dialogue.cocojenna.samurai.meet_1", Portrait.SAMURAI),
                new DialogueLine("dialogue.cocojenna.speaker.samurai",
                        "dialogue.cocojenna.samurai.meet_2", Portrait.SAMURAI, null, "samurai_met")
        )));

        register(new DialogueScene("first_cry_samurai_duel", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.samurai",
                        "dialogue.cocojenna.samurai.duel_1", Portrait.SAMURAI),
                new DialogueLine("dialogue.cocojenna.speaker.samurai",
                        "dialogue.cocojenna.samurai.duel_2", Portrait.SAMURAI)
        )));

        register(new DialogueScene("first_cry_samurai_defeat", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.samurai",
                        "dialogue.cocojenna.samurai.defeat_1", Portrait.SAMURAI),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.samurai.defeat_2", Portrait.NARRATOR, null, "duel_done")
        )));

        register(new DialogueScene("first_cry_calling_start", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "quest.cocojenna.first_cry_calling.start_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "quest.cocojenna.first_cry_calling.start_2", Portrait.ELDER)
        )));
        register(new DialogueScene("first_cry_calling_pagepaw_hint", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "quest.cocojenna.first_cry_calling.pagepaw", Portrait.ELDER)
        )));
        register(new DialogueScene("first_cry_calling_complete", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "quest.cocojenna.first_cry_calling.complete", Portrait.ELDER)
        )));
        register(new DialogueScene("black_mud_secret_start", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "quest.cocojenna.black_mud_secret.start", Portrait.NARRATOR)
        )));
        register(new DialogueScene("black_mud_secret_complete", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "quest.cocojenna.black_mud_secret.complete", Portrait.NARRATOR)
        )));

        register(new DialogueScene("qin_kemu_ch1_awake", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch1_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch1_2", Portrait.MERCHANT)
        )));
        register(new DialogueScene("qin_kemu_ch2_hungry", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch2_1", Portrait.MERCHANT)
        )));
        register(new DialogueScene("qin_kemu_ch3_maids", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch3_1", Portrait.MERCHANT)
        )));
        register(new DialogueScene("qin_kemu_ch4_paper", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch4_1", Portrait.MERCHANT)
        )));
        register(new DialogueScene("qin_kemu_ch5_sealing", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch5_1", Portrait.MERCHANT)
        )));
        register(new DialogueScene("qin_kemu_ch6_past", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "quest.cocojenna.qin_kemu.ch6_1", Portrait.MERCHANT)
        )));

        register(new DialogueScene("memory_cinematic_fish_bone", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.fish_bone.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.fish_bone.2", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.fish_bone.3", Portrait.NARRATOR)
        )));
        register(new DialogueScene("memory_cinematic_iron_rust", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.iron_rust.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.iron_rust.2", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.iron_rust.3", Portrait.NARRATOR)
        )));
        register(new DialogueScene("memory_cinematic_hibiscus", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.hibiscus.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.hibiscus.2", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.hibiscus.3", Portrait.NARRATOR)
        )));
        register(new DialogueScene("memory_cinematic_moonlight", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.moonlight.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.moonlight.2", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.moonlight.3", Portrait.NARRATOR)
        )));
        register(new DialogueScene("qin_triangle_morning", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "qin.cocojenna.triangle.morning.qin", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.afang",
                        "qin.cocojenna.triangle.morning.afang", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.lijiang",
                        "qin.cocojenna.triangle.morning.lijiang", Portrait.JENNA)
        )));
        register(new DialogueScene("qin_triangle_paper", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "qin.cocojenna.triangle.paper.qin", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.afang",
                        "qin.cocojenna.triangle.paper.afang", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.lijiang",
                        "qin.cocojenna.triangle.paper.lijiang", Portrait.JENNA)
        )));
        register(new DialogueScene("qin_triangle_mausoleum", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.lijiang",
                        "qin.cocojenna.triangle.mausoleum.lijiang", Portrait.JENNA),
                new DialogueLine("dialogue.cocojenna.speaker.afang",
                        "qin.cocojenna.triangle.mausoleum.afang", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "qin.cocojenna.triangle.mausoleum.qin", Portrait.MERCHANT)
        )));
        register(new DialogueScene("qin_triangle_full", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "qin.cocojenna.triangle.full.qin", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.afang",
                        "qin.cocojenna.triangle.full.afang", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.lijiang",
                        "qin.cocojenna.triangle.full.lijiang", Portrait.JENNA),
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "qin.cocojenna.triangle.full.qin2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("memory_cinematic_paper_crow", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.paper_crow.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.qin_kemu",
                        "weapon.cinematic.cocojenna.paper_crow.2", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "weapon.cinematic.cocojenna.paper_crow.3", Portrait.NARRATOR)
        )));

        register(new DialogueScene("cat_dream_penetration", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.penetration.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.penetration.2", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.penetration.3", Portrait.NARRATOR)
        )));
        register(new DialogueScene("cat_dream_moon", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.moon.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.moon.2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("cat_dream_village", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.village.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.village.2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("cat_dream_coco", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dream.cocojenna.coco.1", Portrait.COCO),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dream.cocojenna.coco.2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("shadow_claw_phase2", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.shadow_claw",
                        "boss.cocojenna.dialogue.phase2.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "boss.cocojenna.dialogue.phase2.2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("shadow_claw_phase3_hint", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.shadow_claw",
                        "boss.cocojenna.dialogue.phase3.1", Portrait.NARRATOR)
        )));
        register(new DialogueScene("shadow_claw_redemption", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "boss.cocojenna.dialogue.redemption.1", Portrait.COCO),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "boss.cocojenna.dialogue.redemption.2", Portrait.JENNA)
        )));
        register(new DialogueScene("shadow_claw_purge", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.shadow_claw",
                        "boss.cocojenna.dialogue.purge.1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "boss.cocojenna.dialogue.purge.2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("shadow_claw_twin_resonance", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "boss.cocojenna.dialogue.twin.1", Portrait.COCO),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "boss.cocojenna.dialogue.twin.2", Portrait.JENNA)
        )));
        for (String npcId : List.of("sanhua", "ironpaw", "cheshire", "alpha", "samurai")) {
            register(new DialogueScene("cat_dream_npc_" + npcId, List.of(
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "dream.cocojenna.npc." + npcId + ".1", Portrait.NARRATOR),
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "dream.cocojenna.npc." + npcId + ".2", Portrait.NARRATOR)
            )));
        }

        registerRyokatanaMemoryCinematics();

        register(new DialogueScene("gray_whisker_intro", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.intro_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.intro_2", Portrait.ELDER,
                        List.of(
                                new DialogueChoice("penetration.cocojenna.choice.help", "gray_whisker_help"),
                                new DialogueChoice("penetration.cocojenna.choice.history", "gray_whisker_history"),
                                new DialogueChoice("penetration.cocojenna.choice.leave", "gray_whisker_leave")
                        ), null)
        )));

        register(new DialogueScene("gray_whisker_history", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.history_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.history_2", Portrait.ELDER)
        )));

        register(new DialogueScene("gray_whisker_shards_done", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.shards_done", Portrait.ELDER)
        )));

        register(new DialogueScene("gray_whisker_portal_quest", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.portal_quest", Portrait.ELDER)
        )));

        register(new DialogueScene("gray_whisker_cat_language_1", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.lang_1", Portrait.ELDER)
        )));
        register(new DialogueScene("gray_whisker_cat_language_2", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.lang_2", Portrait.ELDER)
        )));
        register(new DialogueScene("gray_whisker_cat_language_3", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.gray_whisker",
                        "penetration.cocojenna.gray_whisker.lang_3", Portrait.ELDER)
        )));

        register(new DialogueScene("gear_town_ironpaw", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.ironpaw",
                        "dialogue.cocojenna.gear_town.ironpaw_1", Portrait.IRONPAW),
                new DialogueLine("dialogue.cocojenna.speaker.ironpaw",
                        "dialogue.cocojenna.gear_town.ironpaw_2", Portrait.IRONPAW)
        )));

        register(new DialogueScene("blind_port_merchant", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.merchant",
                        "dialogue.cocojenna.blind_port.merchant_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.merchant",
                        "dialogue.cocojenna.blind_port.merchant_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("memory_shard_read", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.memory.placeholder", Portrait.NARRATOR)
        )));

        register(new DialogueScene("sanhua_greeting", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.sanhua",
                        "dialogue.cocojenna.sanhua.greeting_1", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.sanhua",
                        "dialogue.cocojenna.sanhua.greeting_2", Portrait.SANHUA)
        )));

        register(new DialogueScene("cheshire_greeting", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.cheshire",
                        "dialogue.cocojenna.cheshire.greeting_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.cheshire",
                        "dialogue.cocojenna.cheshire.greeting_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("white_glove_greeting", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.white_glove",
                        "dialogue.cocojenna.white_glove.greeting_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.white_glove",
                        "dialogue.cocojenna.white_glove.greeting_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("blackjack_greeting", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.blackjack",
                        "dialogue.cocojenna.blackjack.greeting_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.blackjack",
                        "dialogue.cocojenna.blackjack.greeting_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("alpha_greeting", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.alpha",
                        "dialogue.cocojenna.alpha.greeting_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.alpha",
                        "dialogue.cocojenna.alpha.greeting_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("moon_crossroads_intro", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.alpha",
                        "force.cocojenna.dialogue.intro_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.alpha",
                        "force.cocojenna.dialogue.intro_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("moon_crossroads_chosen", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.alpha",
                        "force.cocojenna.dialogue.chosen_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.alpha",
                        "force.cocojenna.dialogue.chosen_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("afterrain_first_cry", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.afterrain.first_cry_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.elder",
                        "dialogue.cocojenna.afterrain.first_cry_2", Portrait.ELDER)
        )));

        register(new DialogueScene("afterrain_gear_town", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.ironpaw",
                        "dialogue.cocojenna.afterrain.gear_town_1", Portrait.IRONPAW),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.afterrain.gear_town_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("afterrain_blind_port", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.merchant",
                        "dialogue.cocojenna.afterrain.blind_port_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.afterrain.blind_port_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("afterrain_moon_alley", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.afterrain.moon_alley_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.afterrain.moon_alley_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_ch1_trigger", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch1_trigger_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dialogue.cocojenna.undercat.ch1_trigger_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_ch1_complete", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.ch1_complete_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch1_complete_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_entrance_tree", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.entrance_tree_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dialogue.cocojenna.undercat.entrance_tree_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_entrance_blind", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.entrance_blind_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.entrance_blind_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("undercat_entrance_gear", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.entrance_gear_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.ironpaw",
                        "dialogue.cocojenna.undercat.entrance_gear_2", Portrait.IRONPAW)
        )));

        register(new DialogueScene("undercat_entrance_lighthouse", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.entrance_lighthouse_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.entrance_lighthouse_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_entrance_sanctuary", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.entrance_sanctuary_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.abbess",
                        "dialogue.cocojenna.undercat.entrance_sanctuary_2", Portrait.ELDER)
        )));

        register(new DialogueScene("undercat_corrugata_m1", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.corrugata_m1_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.corrugata_m1_2", Portrait.ELDER,
                        List.of(
                                new DialogueChoice("dialogue.cocojenna.choice.corrugata_promise", "corrugata_promise"),
                                new DialogueChoice("dialogue.cocojenna.choice.corrugata_distance", "corrugata_distance")
                        ), null)
        )));

        register(new DialogueScene("undercat_corrugata_m2", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.corrugata_m2_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.corrugata_m2_2", Portrait.ELDER,
                        List.of(
                                new DialogueChoice("dialogue.cocojenna.choice.corrugata_accept_crown", "corrugata_accept_crown"),
                                new DialogueChoice("dialogue.cocojenna.choice.corrugata_refuse_crown", "corrugata_refuse_crown")
                        ), null)
        )));

        register(new DialogueScene("undercat_corrugata_bond", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.corrugata_bond_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.corrugata_bond_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_one_eye_m1", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.one_eye_m1_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.one_eye_m1_2", Portrait.MERCHANT,
                        List.of(
                                new DialogueChoice("dialogue.cocojenna.choice.one_eye_trust", "one_eye_trust"),
                                new DialogueChoice("dialogue.cocojenna.choice.one_eye_business", "one_eye_business")
                        ), null)
        )));

        register(new DialogueScene("undercat_one_eye_m2", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.one_eye_m2_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.one_eye_m2_2", Portrait.MERCHANT,
                        List.of(
                                new DialogueChoice("dialogue.cocojenna.choice.one_eye_sail", "one_eye_sail"),
                                new DialogueChoice("dialogue.cocojenna.choice.one_eye_decline", "one_eye_decline")
                        ), null)
        )));

        register(new DialogueScene("undercat_one_eye_romance", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.one_eye_romance_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.one_eye_romance_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_hub_fallback", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.undercat.hub_fallback", Portrait.ELDER)
        )));

        register(new DialogueScene("undercat_ch2_one_eye", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.ch2_one_eye_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.one_eye",
                        "dialogue.cocojenna.undercat.ch2_one_eye_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("undercat_ch2_farm", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch2_farm_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.greenpaw",
                        "dialogue.cocojenna.undercat.ch2_farm_2", Portrait.MERCHANT)
        )));

        register(new DialogueScene("undercat_ch2_complete", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.greenpaw",
                        "dialogue.cocojenna.undercat.ch2_complete_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch2_complete_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_ch3_scarface", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.scarface",
                        "dialogue.cocojenna.undercat.ch3_scarface_1", Portrait.SAMURAI),
                new DialogueLine("dialogue.cocojenna.speaker.scarface",
                        "dialogue.cocojenna.undercat.ch3_scarface_2", Portrait.SAMURAI)
        )));

        register(new DialogueScene("undercat_ch3_complete", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.scarface",
                        "dialogue.cocojenna.undercat.ch3_complete_1", Portrait.SAMURAI),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch3_complete_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_ch4_abbess", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.abbess",
                        "dialogue.cocojenna.undercat.ch4_abbess_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.abbess",
                        "dialogue.cocojenna.undercat.ch4_abbess_2", Portrait.ELDER)
        )));

        register(new DialogueScene("undercat_ch4_trial", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch4_trial_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.abbess",
                        "dialogue.cocojenna.undercat.ch4_trial_2", Portrait.ELDER)
        )));

        register(new DialogueScene("undercat_ch4_complete", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.abbess",
                        "dialogue.cocojenna.undercat.ch4_complete_1", Portrait.ELDER),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.ch4_complete_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("undercat_finale", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.undercat.finale_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dialogue.cocojenna.undercat.finale_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("starlight_ch1_beacon", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch1_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dialogue.cocojenna.starlight.ch1_2", Portrait.COCO)
        )));

        register(new DialogueScene("starlight_ch2_convergence", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.corrugata",
                        "dialogue.cocojenna.starlight.ch2_1", Portrait.ELDER,
                        List.of(
                                new DialogueChoice("dialogue.cocojenna.starlight.choice.follow", "starlight_follow"),
                                new DialogueChoice("dialogue.cocojenna.starlight.choice.wait", "starlight_wait")
                        ), null),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch2_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("starlight_ch3_finale", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch3_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "dialogue.cocojenna.starlight.ch3_2", Portrait.JENNA,
                        List.of(
                                new DialogueChoice("dialogue.cocojenna.starlight.choice.pledge", "starlight_pledge")
                        ), null)
        )));

        register(new DialogueScene("starlight_ch4_wastes", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch4_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dialogue.cocojenna.starlight.ch4_2", Portrait.COCO)
        )));

        register(new DialogueScene("starlight_ch5_abyss", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch5_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "dialogue.cocojenna.starlight.ch5_2", Portrait.JENNA)
        )));

        register(new DialogueScene("starlight_ch5_library", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch5_lib_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.abbess",
                        "dialogue.cocojenna.starlight.ch5_lib_2", Portrait.ELDER)
        )));

        register(new DialogueScene("starlight_ch6_epilogue", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "dialogue.cocojenna.starlight.ch6_1", Portrait.COCO),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "dialogue.cocojenna.starlight.ch6_2", Portrait.JENNA),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.starlight.ch6_3", Portrait.NARRATOR)
        )));

        register(new DialogueScene("afterrain_velvet", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.sanhua",
                        "dialogue.cocojenna.afterrain.velvet_1", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.afterrain.velvet_2", Portrait.NARRATOR)
        )));

        register(new DialogueScene("fallen_velvet_choice", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.sanhua",
                        "redemption.cocojenna.fallen_velvet.choice_prompt", Portrait.SANHUA,
                        List.of(
                                new DialogueChoice("redemption.cocojenna.choice.distill", "velvet_distill"),
                                new DialogueChoice("redemption.cocojenna.choice.soothe", "velvet_soothe")
                        ), null)
        )));

        register(new DialogueScene("fallen_velvet_redemption", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.redemption.fallen_velvet_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.sanhua",
                        "dialogue.cocojenna.redemption.fallen_velvet_2", Portrait.SANHUA),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.redemption.fallen_velvet_3", Portrait.NARRATOR)
        )));

        register(new DialogueScene("kingdom_theater_court_lady", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.court_lady",
                        "kingdom.cocojenna.theater.dialogue_1", Portrait.MERCHANT),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "kingdom.cocojenna.theater.dialogue_2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("kingdom_theater_generic", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "kingdom.cocojenna.theater.generic_1", Portrait.NARRATOR)
        )));

        register(new DialogueScene("kingdom_throne_blessing", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "kingdom.cocojenna.throne.dialogue_1", Portrait.COCO),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "kingdom.cocojenna.throne.dialogue_2", Portrait.JENNA),
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "kingdom.cocojenna.throne.dialogue_3", Portrait.NARRATOR)
        )));

        registerKingdomStories();
        registerNightSecrets();
        register(new DialogueScene("kingdom_festival_moon_start", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "kingdom.cocojenna.festival.dialogue_1", Portrait.NARRATOR),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "kingdom.cocojenna.festival.dialogue_2", Portrait.NARRATOR)
        )));
        register(new DialogueScene("kingdom_festival_cooking", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "kingdom.cocojenna.festival.cooking_1", Portrait.COCO),
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "kingdom.cocojenna.festival.cooking_2", Portrait.JENNA)
        )));
        register(new DialogueScene("kingdom_festival_dance", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.jenna",
                        "kingdom.cocojenna.festival.dance_1", Portrait.JENNA),
                new DialogueLine("dialogue.cocojenna.speaker.coco",
                        "kingdom.cocojenna.festival.dance_2", Portrait.COCO)
        )));
        register(new DialogueScene("kingdom_festival_dance_win", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "kingdom.cocojenna.festival.dance_win", Portrait.NARRATOR)
        )));
    }

    private static void registerNightSecrets() {
        String[] events = {
                "rooftop_meeting", "secret_fish_share", "moonlight_dance", "furball_king_play",
                "npc_dream_guard", "portal_gaze", "stardust_bath", "secret_base"
        };
        for (String id : events) {
            register(new DialogueScene("night_secret_" + id, List.of(
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "night.cocojenna.dialogue." + id + "_1", Portrait.NARRATOR),
                    new DialogueLine("dialogue.cocojenna.speaker.coco",
                            "night.cocojenna.dialogue." + id + "_2", Portrait.COCO)
            )));
        }
    }

    private static void registerKingdomStories() {
        String[][] stories = {
                {"ironpaw", "dialogue.cocojenna.speaker.ironpaw", "IRONPAW"},
                {"sanhua", "dialogue.cocojenna.speaker.sanhua", "SANHUA"},
                {"cheshire", "dialogue.cocojenna.speaker.cheshire", "MERCHANT"},
                {"white_glove", "dialogue.cocojenna.speaker.white_glove", "MERCHANT"},
                {"alpha", "dialogue.cocojenna.speaker.alpha", "NARRATOR"},
                {"samurai", "dialogue.cocojenna.speaker.samurai", "SAMURAI"},
                {"monk", "dialogue.cocojenna.speaker.monk", "NARRATOR"},
                {"court_lady", "dialogue.cocojenna.speaker.court_lady", "MERCHANT"},
        };
        for (String[] s : stories) {
            for (int ch = 1; ch <= 5; ch++) {
                Portrait p = Portrait.valueOf(s[2]);
                register(new DialogueScene("kingdom_story_" + s[0] + "_" + ch, List.of(
                        new DialogueLine(s[1], "kingdom.cocojenna.story." + s[0] + "_" + ch, p)
                )));
            }
        }

        String[] npcLore = {
                "blade_mark", "molten_paw", "miso", "mint_ear", "moon_whisper",
                "soft_pad", "tide_tail", "mud_bean", "wander_stray"
        };
        for (String npc : npcLore) {
            register(new DialogueScene("first_cry_npc_lore_" + npc, List.of(
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "dialogue.cocojenna.first_cry.lore." + npc, Portrait.NARRATOR)
            )));
        }
        register(new DialogueScene("first_cry_wander_stray", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.stray",
                        "dialogue.cocojenna.first_cry.wander_stray", Portrait.MERCHANT)
        )));
        register(new DialogueScene("fragmented_ritual_witness", List.of(
                new DialogueLine("dialogue.cocojenna.speaker.narrator",
                        "dialogue.cocojenna.fragmented.ritual_witness", Portrait.NARRATOR)
        )));
    }

    private DialogueScripts() {}

    private static void registerRyokatanaMemoryCinematics() {
        for (String variant : com.cocojenna.item.RyokatanaRegistry.all().keySet()) {
            String sceneId = "memory_cinematic_" + variant;
            if (SCENES.containsKey(sceneId)) continue;
            register(new DialogueScene(sceneId, List.of(
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "weapon.cinematic.cocojenna." + variant + ".1", Portrait.NARRATOR),
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "weapon.cinematic.cocojenna." + variant + ".2", Portrait.NARRATOR),
                    new DialogueLine("dialogue.cocojenna.speaker.narrator",
                            "weapon.cinematic.cocojenna." + variant + ".3", Portrait.NARRATOR)
            )));
        }
    }

    private static void register(DialogueScene scene) {
        SCENES.put(scene.id(), scene);
    }

    public static DialogueScene get(String id) {
        return SCENES.get(id);
    }

    /** Build a dynamic memory shard scene from translation key. */
    public static DialogueScene memoryShard(String shardTextKey) {
        return new DialogueScene("memory_dynamic_" + shardTextKey,
                List.of(new DialogueLine("dialogue.cocojenna.speaker.narrator", shardTextKey, Portrait.NARRATOR)));
    }

    public static List<DialogueLine> linesForPacket(DialogueScene scene) {
        return new ArrayList<>(scene.lines());
    }
}
