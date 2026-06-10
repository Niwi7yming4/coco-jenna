package com.cocojenna.endgame.kingdom;

import com.cocojenna.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** 城鎮 NPC 資料：好感、偏好禮物、故事線. */
public record TownNpcProfile(
        String id,
        String nameZh,
        TownJobRank defaultJob,
        Item preferredGift,
        int recruitFavor,
        String[] storyScenes
) {
    public static final TownNpcProfile[] ALL = {
            new TownNpcProfile("ironpaw", "鐵爪", TownJobRank.CRAFTSMAN, Items.IRON_INGOT, 0,
                    story("ironpaw", 5)),
            new TownNpcProfile("sanhua", "三花", TownJobRank.GARDENER, ModItems.HIBISCUS_FLOWER_ITEM.get(), 0,
                    story("sanhua", 5)),
            new TownNpcProfile("cheshire", "柴郡", TownJobRank.MERCHANT, Items.EMERALD, 20,
                    story("cheshire", 5)),
            new TownNpcProfile("white_glove", "白手套", TownJobRank.FISHER, Items.COD, 0,
                    story("white_glove", 5)),
            new TownNpcProfile("alpha", "阿爾法", TownJobRank.SCHOLAR, ModItems.MEMORY_SHARD.get(), 60,
                    story("alpha", 5)),
            new TownNpcProfile("samurai", "風切丸", TownJobRank.SHADOW_GUARD, Items.IRON_SWORD, 30,
                    story("samurai", 3)),
            new TownNpcProfile("monk", "玄德", TownJobRank.SCHOLAR, Items.BOOK, 30,
                    story("monk", 3)),
            new TownNpcProfile("court_lady", "紫苑", TownJobRank.PERFORMER, Items.PINK_WOOL, 30,
                    story("court_lady", 3)),
    };

    private static String[] story(String npc, int chapters) {
        String[] scenes = new String[chapters];
        for (int i = 0; i < chapters; i++) {
            scenes[i] = "kingdom_story_" + npc + "_" + (i + 1);
        }
        return scenes;
    }

    public static TownNpcProfile byId(String id) {
        for (TownNpcProfile p : ALL) {
            if (p.id.equals(id)) return p;
        }
        return null;
    }

    public int favorForChapter(int chapter) {
        return switch (chapter) {
            case 1 -> 20;
            case 2 -> 40;
            case 3 -> 60;
            case 4 -> 80;
            case 5 -> 100;
            default -> 0;
        };
    }
}
