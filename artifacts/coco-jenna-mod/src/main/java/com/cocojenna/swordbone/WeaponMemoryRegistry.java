package com.cocojenna.swordbone;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** 傳說武器記憶 — 設計書 2.3. */
public final class WeaponMemoryRegistry {

    private static final Map<String, String> STORIES = new LinkedHashMap<>();

    static {
        STORIES.put("musou_salmon_king", """
                牠不是戰士。牠只是一隻漁貓，每天在碼頭賣魚。黑泥來的那天，牠沒有逃跑。\
                牠舉起最大的那條青魚，擋在碼頭前。牠說：『這是我的攤位。你們不能碰。』\
                後來，那條魚被黑泥腐蝕成了石頭，但再也沒有黑泥敢靠近那個碼頭。""");
        STORIES.put("daikatana_first_dawn", """
                初啼村的鐵匠把第一縷晨光敲進刀身。他說：『刀要會記得黎明前的冷，才配斬斷長夜。』""");
        STORIES.put("ryokatana_fallen_velvet_claw", """
                絨都的守衛貓在塔倒下的那天，把絨尾披風纏在刀柄上。披風燒盡了，刀還在。""");
        STORIES.put("daikatana_hibiscus_ultimate", """
                朱槿綻放之時，血與花汁一同滲入鋼中。從此這把刀只在盛放與凋零之間選擇目標。""");
    }

    private WeaponMemoryRegistry() {}

    public static boolean tryUnlock(ServerPlayer player, String weaponId, com.cocojenna.capability.BondData bond) {
        if (!STORIES.containsKey(weaponId) || bond.hasWeaponMemory(weaponId)) {
            return false;
        }
        bond.unlockWeaponMemory(weaponId);
        player.displayClientMessage(Component.translatable("weapon_memory.cocojenna.unlocked",
                Component.translatable("item.cocojenna." + weaponId)), false);
        player.displayClientMessage(Component.literal("§7" + STORIES.get(weaponId)), false);
        return true;
    }

    public static Optional<String> story(String weaponId) {
        return Optional.ofNullable(STORIES.get(weaponId));
    }

    public static Map<String, String> all() {
        return STORIES;
    }
}
