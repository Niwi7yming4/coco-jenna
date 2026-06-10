package com.cocojenna.quest.qin;

import com.cocojenna.dialogue.DialogueManager;
import com.cocojenna.entity.AFangEntity;
import com.cocojenna.entity.LiJiangEntity;
import com.cocojenna.entity.QinKemuEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

/** 秦可沐 × 阿房 × 黎姜 三角對話. */
public final class QinTriangleDialogueManager {

    private static final int COOLDOWN_TICKS = 12000;

    private QinTriangleDialogueManager() {}

    public static void tryAmbient(ServerLevel level, QinKemuEntity qin) {
        if (level.getGameTime() % 200 != 0) return;
        long last = qin.getPersistentData().getLong("cocojenna_triangle_dialogue");
        if (level.getGameTime() - last < COOLDOWN_TICKS) return;
        boolean afang = !level.getEntitiesOfClass(AFangEntity.class, qin.getBoundingBox().inflate(8)).isEmpty();
        boolean lijiang = !level.getEntitiesOfClass(LiJiangEntity.class, qin.getBoundingBox().inflate(8)).isEmpty();
        if (!afang || !lijiang) return;
        ServerPlayer nearby = level.getEntitiesOfClass(ServerPlayer.class, qin.getBoundingBox().inflate(12))
                .stream().findFirst().orElse(null);
        if (nearby == null) return;
        int scene = (int) (level.getDayTime() / 6000 % 3);
        String id = switch (scene) {
            case 0 -> "qin_triangle_morning";
            case 1 -> "qin_triangle_paper";
            default -> "qin_triangle_mausoleum";
        };
        DialogueManager.play(nearby, id);
        qin.getPersistentData().putLong("cocojenna_triangle_dialogue", level.getGameTime());
    }

    public static void tryManual(ServerPlayer player, QinKemuEntity qin) {
        AABB box = qin.getBoundingBox().inflate(10);
        boolean afang = !player.level().getEntitiesOfClass(AFangEntity.class, box).isEmpty();
        boolean lijiang = !player.level().getEntitiesOfClass(LiJiangEntity.class, box).isEmpty();
        if (afang && lijiang) {
            DialogueManager.play(player, "qin_triangle_full");
        }
    }
}
