package com.cocojenna.dialogue;

import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.OpenGalgameDialoguePacket;
import com.cocojenna.quest.FirstCryQuestManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

/**
 * Server-side dialogue orchestration. Opens GAL scenes on client and handles completion actions.
 */
public final class DialogueManager {

    private DialogueManager() {}

    public static void play(ServerPlayer player, String sceneId) {
        DialogueScene scene = DialogueScripts.get(sceneId);
        if (scene == null) {
            return;
        }
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenGalgameDialoguePacket(sceneId, DialogueScripts.linesForPacket(scene)));
    }

    public static void playMemoryShard(ServerPlayer player, String textKey) {
        DialogueScene scene = DialogueScripts.memoryShard(textKey);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenGalgameDialoguePacket(scene.id(), scene.lines()));
    }

    public static void onSceneComplete(ServerPlayer player, String sceneId, String actionId) {
        if (actionId != null && (actionId.startsWith("corrugata_") || actionId.startsWith("one_eye_"))) {
            com.cocojenna.undercat.UndercatSideStoryManager.onDialogueChoice(player, actionId);
            return;
        }
        if ("velvet_distill".equals(actionId) || "velvet_soothe".equals(actionId)) {
            com.cocojenna.endgame.FallenVelvetChoiceManager.onChoice(player, actionId);
            return;
        }
        if (actionId != null && actionId.startsWith("gray_whisker_")) {
            com.cocojenna.overworld.PenetrationQuestManager.onDialogueChoice(player, actionId);
            return;
        }
        switch (actionId) {
            case "elder_met" -> FirstCryQuestManager.onElderDialogueEnd(player);
            case "quest_complete" -> FirstCryQuestManager.onRewardDialogueEnd(player);
            case "samurai_met" -> FirstCryQuestManager.onSamuraiDialogueEnd(player);
            case "duel_done" -> { /* already handled in combat */ }
            default -> { }
        }
    }
}
