package com.cocojenna.combat;

import com.cocojenna.init.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/** 戰鬥音效分層 — 設計書第十二章. */
public final class CombatSoundHelper {

    public enum Layer { BASE, CRIT, KILL, BOSS, ENV }

    private CombatSoundHelper() {}

    public static void play(ServerLevel level, Vec3 pos, Layer layer, CombatVfxHelper.Force force) {
        SoundEvent sound = switch (layer) {
            case CRIT -> ModSounds.ENTITY_SEAL_FORM.get();
            case KILL -> ModSounds.ITEM_MEMORY_SHARD_PICKUP.get();
            case BOSS -> ModSounds.WORLD_BLACK_MUD_SPREAD.get();
            case ENV -> ModSounds.WORLD_CAT_KINGDOM_AMBIENT.get();
            default -> switch (force) {
                case RESONANCE -> ModSounds.COCO_PURR_DEEP.get();
                case SHADOW -> ModSounds.ENTITY_SEAL_FORM.get();
                case CHAOS -> ModSounds.JENNA_MEOW_EXCITED.get();
            };
        };
        float pitch = switch (layer) {
            case CRIT -> 1.4f;
            case KILL -> 1.6f;
            case BOSS -> 0.7f;
            default -> 1.0f;
        };
        float volume = switch (layer) {
            case CRIT, KILL -> 1.2f;
            case BOSS -> 1.5f;
            default -> 0.9f;
        };
        level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch);
    }
}
