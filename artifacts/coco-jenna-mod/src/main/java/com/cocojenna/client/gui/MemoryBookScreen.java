package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.SisterBondSystem;
import com.cocojenna.exploration.LoreEntry;
import com.cocojenna.exploration.LoreRegistry;
import com.cocojenna.exploration.WildCatType;
import com.cocojenna.network.BondSettingsPacket;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.sequence.PromotionCardCatalog;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * 記憶之書 — 情感／記憶／王國／傳說／怪貓／日誌／設定（探索設計書 2.2 / 4.3 / 5.4）.
 */
public class MemoryBookScreen extends Screen {

    private static final int GUI_W = MemoryBookUi.PANEL_W;
    private static final int GUI_H = MemoryBookUi.PANEL_H;
    private static final int TAB_COUNT = 9;
    private static final String[] TAB_KEYS = {
            "emotion", "memory", "weapon", "quest", "lore", "kingdom", "wildcat", "journal", "settings"
    };

    private int tab = 0;
    private BondData bond;

    /** 分頁點擊區（自訂繪製，不用石頭 Button） */
    private final int[] tabX = new int[TAB_COUNT];
    private final int[] tabY = new int[TAB_COUNT];
    private final int tabW = MemoryBookUi.TAB_W;
    private final int tabH = MemoryBookUi.TAB_H;

    public MemoryBookScreen(BondData bond) {
        super(Component.translatable("gui.cocojenna.memory_book.title"));
        this.bond = bond;
    }

    public static void open(Player player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        net.minecraft.client.Minecraft.getInstance().setScreen(new MemoryBookScreen(bond));
    }

    @Override
    protected void init() {
        int left = (width - GUI_W) / 2;
        int top = (height - GUI_H) / 2;
        clearWidgets();

        int tabStartX = left + 6;
        int tabStartY = top + MemoryBookUi.HEADER_H + 8;
        for (int i = 0; i < TAB_COUNT; i++) {
            tabX[i] = tabStartX;
            tabY[i] = tabStartY + i * (tabH + MemoryBookUi.TAB_GAP);
        }

        if (tab == 8) {
            initSettings(left, top);
        }
    }

    private void initSettings(int left, int top) {
        int cx = left + MemoryBookUi.SIDEBAR_W + 8;
        int cy = top + MemoryBookUi.HEADER_H + 22;
        addRenderableWidget(new ParchmentButton(cx, cy, 130, 18,
                Component.translatable("gui.cocojenna.settings.follow." + bond.getFollowDistance()),
                b -> cycleFollow()));
        cy += 24;
        addToggle(cx, cy, BondSettingsPacket.AFFECTION, bond.isAllowAffection(),
                "gui.cocojenna.settings.affection");
        addToggle(cx + 138, cy, BondSettingsPacket.MUTE, bond.isMuteMode(),
                "gui.cocojenna.settings.mute");
        cy += 22;
        addToggle(cx, cy, BondSettingsPacket.EXPLORE, bond.isAllowExplore(),
                "gui.cocojenna.settings.explore");
        addToggle(cx + 138, cy, BondSettingsPacket.COMBAT, bond.isAllowCombat(),
                "gui.cocojenna.settings.combat");
        cy += 26;
        addRenderableWidget(new ParchmentButton(cx, cy, GUI_W - MemoryBookUi.SIDEBAR_W - MemoryBookUi.PAD - 8, 18,
                Component.translatable("gui.cocojenna.skills.open"),
                b -> SkillSettingsScreen.open(), true));
    }

    private void addToggle(int x, int y, int id, boolean on, String labelKey) {
        addRenderableWidget(new ParchmentButton(x, y, 130, 18,
                Component.translatable(labelKey).append(": ").append(onOff(on)),
                b -> {
                    ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(id, on ? 0 : 1));
                    switch (id) {
                        case BondSettingsPacket.AFFECTION -> bond.setAllowAffection(!on);
                        case BondSettingsPacket.EXPLORE -> bond.setAllowExplore(!on);
                        case BondSettingsPacket.COMBAT -> bond.setAllowCombat(!on);
                        case BondSettingsPacket.MUTE -> bond.setMuteMode(!on);
                        default -> {}
                    }
                    init();
                }));
    }

    private Component onOff(boolean on) {
        return Component.translatable(on ? "gui.cocojenna.settings.on" : "gui.cocojenna.settings.off");
    }

    private void cycleFollow() {
        int next = (bond.getFollowDistance() + 1) % 3;
        bond.setFollowDistance(next);
        ModNetwork.CHANNEL.sendToServer(new BondSettingsPacket(BondSettingsPacket.FOLLOW, next));
        init();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        int left = (width - GUI_W) / 2;
        int top = (height - GUI_H) / 2;

        MemoryBookUi.drawPanel(g, left, top, GUI_W, GUI_H);
        MemoryBookUi.drawHeader(g, font, left, top, GUI_W, title);

        renderSidebar(g, left, top, mouseX, mouseY);

        int contentX = left + MemoryBookUi.SIDEBAR_W + 8;
        int contentY = top + MemoryBookUi.HEADER_H + 12;
        int contentW = GUI_W - MemoryBookUi.SIDEBAR_W - MemoryBookUi.PAD - 8;
        int contentH = GUI_H - MemoryBookUi.HEADER_H - MemoryBookUi.PAD - 16;
        int clipBottom = top + GUI_H - MemoryBookUi.PAD - 4;

        g.enableScissor(contentX - 2, contentY - 2, contentX + contentW + 2, clipBottom);
        switch (tab) {
            case 0 -> renderEmotionTab(g, contentX, contentY, contentW, contentH);
            case 1 -> renderMemoryTab(g, contentX, contentY, contentW, contentH);
            case 2 -> renderWeaponTab(g, contentX, contentY, contentW, contentH);
            case 3 -> renderQuestTab(g, contentX, contentY, contentW, contentH);
            case 4 -> renderLoreTab(g, contentX, contentY, contentW, contentH);
            case 5 -> renderKingdomTab(g, contentX, contentY, contentW, contentH);
            case 6 -> renderWildCatTab(g, contentX, contentY, contentW, contentH);
            case 7 -> renderJournalTab(g, contentX, contentY, contentW, contentH);
            case 8 -> renderSettingsTab(g, contentX, contentY, contentW);
        }
        g.disableScissor();

        super.render(g, mouseX, mouseY, partial);
    }

    private void renderEmotionTab(GuiGraphics g, int x, int y, int w, int h) {
        int colGap = 20;
        int colW = (w - colGap) / 2;
        int portraitSize = 32;
        int barStep = 24;

        // 可可欄
        MemoryBookUi.drawPortrait(g, MemoryBookUi.TEX_COCO, x, y, portraitSize);
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.coco_title"),
                x + portraitSize + 8, y + 6, 0xFF1A1A2E, false);
        g.drawString(font, emotionLevelLabel(bond.getCocoEmotionLevel()),
                x + portraitSize + 8, y + 18, MemoryBookUi.COL_INK_SOFT, false);

        int barY = y + portraitSize + 10;
        MemoryBookUi.drawStatBar(g, font, x, barY, colW,
                Component.translatable("gui.cocojenna.stat.emotion"),
                bond.getCocoEmotion(), 100, 0xFFFFBF00);
        MemoryBookUi.drawStatBar(g, font, x, barY + barStep, colW,
                Component.translatable("gui.cocojenna.stat.independence"),
                bond.getCocoIndependence(), 100, 0xFF6699FF);
        MemoryBookUi.drawStatBar(g, font, x, barY + barStep * 2, colW,
                Component.translatable("gui.cocojenna.stat.awakening"),
                bond.getCocoAwakening(), 50, 0xFF9933CC);

        // 珍奶欄
        int jx = x + colW + colGap;
        MemoryBookUi.drawPortrait(g, MemoryBookUi.TEX_JENNA, jx, y, portraitSize);
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.jenna_title"),
                jx + portraitSize + 8, y + 6, 0xFFC96823, false);
        g.drawString(font, emotionLevelLabel(bond.getJennaEmotionLevel()),
                jx + portraitSize + 8, y + 18, MemoryBookUi.COL_INK_SOFT, false);

        MemoryBookUi.drawStatBar(g, font, jx, barY, colW,
                Component.translatable("gui.cocojenna.stat.emotion"),
                bond.getJennaEmotion(), 100, 0xFF33AA33);
        MemoryBookUi.drawStatBar(g, font, jx, barY + barStep, colW,
                Component.translatable("gui.cocojenna.stat.independence"),
                bond.getJennaIndependence(), 100, 0xFF88CC88);
        MemoryBookUi.drawStatBar(g, font, jx, barY + barStep * 2, colW,
                Component.translatable("gui.cocojenna.stat.awakening"),
                bond.getJennaAwakening(), 50, 0xFF66BB66);

        int bridgeY = barY + barStep * 3 + 8;
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.sister_bond",
                        SisterBondSystem.getBondTitle(bond.getSisterBond())),
                x, bridgeY, MemoryBookUi.COL_INK, false);
        MemoryBookUi.drawBondBridge(g, font, x, bridgeY + 12, w, bond.getSisterBond());

        int footY = bridgeY + 40;
        var cocoHint = Component.translatable("gui.cocojenna.memory_book.coco_hint."
                + bond.getCocoEmotionLevel().name().toLowerCase());
        for (var line : font.split(cocoHint, w - 8)) {
            if (footY > y + h - 22) break;
            g.drawString(font, line, x, footY, 0xFF8B6914, false);
            footY += 11;
        }

        String force = bond.getFelineForce();
        if (force == null || force.isEmpty()) force = "resonance";
        Component seq = Component.translatable("gui.cocojenna.sequence_tier",
                bond.getFelineTier(),
                Component.translatable("gui.cocojenna.force." + force));
        int bottomY = y + h - 12;
        g.drawString(font, seq, x, bottomY, 0xFF663399, false);
        if (bond.isGuardian()) {
            Component badge = Component.translatable("gui.cocojenna.guardian_badge");
            g.drawString(font, badge, x + w - font.width(badge), bottomY, 0xFFFFD700, false);
        }
    }

    private Component emotionLevelLabel(BondData.EmotionLevel level) {
        return Component.translatable("gui.cocojenna.emotion_level." + level.name().toLowerCase());
    }

    private void renderMemoryTab(GuiGraphics g, int x, int y, int w, int h) {
        int total = bond.getMemoryShardsTotal();
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.shards_count", total),
                x, y, MemoryBookUi.COL_INK, false);
        MemoryBookUi.drawStatBar(g, font, x, y + 14, w,
                Component.translatable("gui.cocojenna.stat.shards"),
                total, 50, 0xFF33AABB);

        int treeH = Math.min(96, h / 2);
        g.drawString(font, Component.translatable("gui.cocojenna.memory_tree.title"),
                x, y + 36, MemoryBookUi.COL_ACCENT_DK, false);
        MemoryTreePanel.render(g, font, bond, x, y + 48, w, treeH);

        int ly = y + 48 + treeH + 8;
        int[] milestones = {1, 5, 10, 20, 30, 50};
        boolean any = false;
        for (int m : milestones) {
            if (total < m) continue;
            any = true;
            for (var line : font.split(Component.translatable(
                    "gui.cocojenna.memory_book.shard_lore." + m), w - 8)) {
                if (ly > y + h - 40) break;
                g.drawString(font, line, x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
                ly += 10;
            }
            ly += 4;
        }
        if (!any) {
            g.drawString(font, Component.translatable("gui.cocojenna.memory_book.shards_empty"),
                    x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
        }

        int cardY = y + h - 36;
        g.fill(x, cardY - 4, x + w, cardY - 3, 0xFF9A8A6A);
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.cards_count",
                bond.getPromotionCardCount()), x, cardY, 0xFF663399, false);
        cardY += 12;
        for (String cardId : bond.getOwnedPromotionCards()) {
            if (cardY > y + h - 4) break;
            g.drawString(font, "• " + PromotionCardCatalog.displayName(cardId).getString(),
                    x + 8, cardY, MemoryBookUi.COL_INK_SOFT, false);
            cardY += 10;
        }
    }

    private void renderWeaponTab(GuiGraphics g, int x, int y, int w, int h) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.weapon_title"),
                x, y, MemoryBookUi.COL_INK, false);
        int ly = y + 16;
        String taskId = bond.getActiveWeaponMemoryTaskId();
        boolean any = false;
        if (taskId != null && !taskId.isEmpty()) {
            any = true;
            var active = com.cocojenna.weapon.WeaponMemoryTaskRegistry.byId(taskId);
            if (active.isPresent()) {
                g.drawString(font, Component.translatable(active.get().descriptionKey()),
                        x + 4, ly, MemoryBookUi.COL_ACCENT_DK, false);
            } else {
                g.drawString(font, taskId, x + 4, ly, MemoryBookUi.COL_ACCENT_DK, false);
            }
            ly += 14;
            g.drawString(font, Component.translatable("gui.cocojenna.memory_book.weapon_progress",
                            bond.getWeaponMemoryTaskProgress()),
                    x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
            ly += 16;
        }
        for (var task : com.cocojenna.weapon.WeaponMemoryTaskRegistry.allTasks()) {
            if (ly > y + h - 10) break;
            if (task.id().equals(taskId)) continue;
            g.drawString(font, "• " + Component.translatable(task.descriptionKey()).getString(),
                    x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
            ly += 10;
            any = true;
        }
        if (!any) {
            g.drawString(font, Component.translatable("gui.cocojenna.memory_book.weapon_empty"),
                    x + 4, y + 16, MemoryBookUi.COL_INK_SOFT, false);
        }
    }

    private void renderQuestTab(GuiGraphics g, int x, int y, int w, int h) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.quest_title"),
                x, y, MemoryBookUi.COL_INK, false);
        int ly = y + 16;
        int fragmented = 0;
        var player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            fragmented = player.getPersistentData().getInt("cocojenna_fragmented_quest");
        }
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.quest_penetration",
                        bond.getPenetrationQuestStage()),
                x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
        ly += 12;
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.quest_first_cry",
                        bond.getFirstCryQuestStage()),
                x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
        ly += 12;
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.quest_fragmented", fragmented),
                x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
        ly += 12;
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.quest_undercat",
                        bond.getUndercatChapter(), bond.getUndercatStage()),
                x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
        ly += 16;
        if (fragmented >= 2) {
            for (var line : font.split(Component.translatable(
                    "gui.cocojenna.memory_book.quest_scribe_hint"), w - 8)) {
                if (ly > y + h - 8) break;
                g.drawString(font, line, x + 4, ly, MemoryBookUi.COL_ACCENT_DK, false);
                ly += 10;
            }
        }
    }

    private void renderKingdomTab(GuiGraphics g, int x, int y, int w, int h) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.kingdom_map"),
                x, y, MemoryBookUi.COL_INK, false);

        int texW = 256;
        int texH = 160;
        int mapW = Math.min(texW, w - 8);
        int mapH = mapW * texH / texW;
        int mapX = x + (w - mapW) / 2;
        int mapY = y + 14;
        g.fill(mapX - 2, mapY - 2, mapX + mapW + 2, mapY + mapH + 2, MemoryBookUi.COL_FRAME);
        g.fill(mapX, mapY, mapX + mapW, mapY + mapH, 0xFF2A3A2A);
        // 螢幕尺寸與 UV 分開，避免 128px 貼圖被平鋪成 2×2
        g.blit(GuiTextures.worldMap(), mapX, mapY, mapW, mapH, 0, 0, texW, texH, texW, texH);

        String[] regions = {
                "velvet_forest", "moon_alley", "first_cry_plains", "howling_gorge",
                "blind_water_river", "dawn_highlands", "forgotten_wastes", "cardboard_slums",
                "moonlight_beach", "rainbow_canyon", "catnip_highlands", "stardust_desert"
        };
        int col1X = x;
        int col2X = x + w / 2 + 4;
        int ry = mapY + mapH + 10;
        for (int i = 0; i < regions.length; i++) {
            String r = regions[i];
            boolean unlocked = bond.getMemoryShardsTotal() >= regionThreshold(r) || r.equals("first_cry_plains");
            int color = unlocked ? MemoryBookUi.COL_INK : 0xFF999999;
            int rep = bond.getReputation(repKey(r));
            Component line = Component.translatable("biome.cocojenna." + r)
                    .append(Component.literal("  " + rep));
            int lx = (i % 2 == 0) ? col1X : col2X;
            int ly = ry + (i / 2) * 12;
            if (!unlocked) {
                g.drawString(font, Component.translatable("gui.cocojenna.locked_short"),
                        lx, ly, color, false);
                g.drawString(font, line, lx + font.width(Component.translatable("gui.cocojenna.locked_short")), ly, color, false);
            } else {
                g.drawString(font, line, lx, ly, color, false);
            }
        }
    }

    private void renderLoreTab(GuiGraphics g, int x, int y, int w, int h) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.lore_title",
                        bond.getLoreDiscoveryCount(), LoreRegistry.all().size()),
                x, y, MemoryBookUi.COL_INK, false);
        int ly = y + 16;
        int illW = Math.min(96, w / 3);
        int textX = x + illW + 12;
        int textW = w - illW - 16;
        for (LoreEntry entry : LoreRegistry.all()) {
            if (ly > y + h - 40) break;
            boolean found = bond.hasLore(entry.id());
            if (found) {
                var ill = GuiTextures.loreIllustration(entry.key());
                if (net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(ill).isPresent()) {
                    g.fill(x + 2, ly, x + illW, ly + 72, MemoryBookUi.COL_FRAME);
                    g.blit(ill, x + 4, ly + 2, illW - 4, 68, 0, 0, 128, 96, 128, 96);
                } else {
                    g.fill(x + 2, ly, x + illW, ly + 72, 0xFF3A2A3A);
                }
                Component title = Component.translatable("explore.cocojenna.lore." + entry.key() + ".title");
                g.drawString(font, title.getString(), textX, ly + 4, MemoryBookUi.COL_INK, false);
                int ty = ly + 16;
                for (var line : font.split(Component.translatable(
                        "explore.cocojenna.lore." + entry.key() + ".body"), textW)) {
                    if (ty > ly + 70) break;
                    g.drawString(font, line, textX, ty, MemoryBookUi.COL_INK_SOFT, false);
                    ty += 9;
                }
                ly += 78;
                if (LoreRegistry.isRegionComplete(bond, entry.region())) {
                    g.drawString(font, Component.translatable("gui.cocojenna.memory_book.lore_region_complete",
                                    Component.translatable("biome.cocojenna." + entry.region())).getString(),
                            textX, ly - 6, 0xFFCCAA44, false);
                }
            } else {
                g.drawString(font, "？ " + Component.translatable("gui.cocojenna.locked_short").getString(),
                        x + 4, ly + 28, 0xFF999999, false);
                ly += 40;
            }
            ly += 4;
        }
    }

    private void renderWildCatTab(GuiGraphics g, int x, int y, int w, int h) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.wildcat_title",
                        bond.getWildCatDiscoveryCount(), 15),
                x, y, MemoryBookUi.COL_INK, false);
        int ly = y + 18;
        int colW = w / 2 - 4;
        int i = 0;
        for (WildCatType type : WildCatType.values()) {
            int col = i % 2;
            int row = i / 2;
            int lx = x + col * (colW + 8);
            int ty = ly + row * 14;
            if (ty > y + h - 10) break;
            boolean found = bond.hasWildCat(type.id());
            Component name = Component.translatable("explore.cocojenna.wildcat." + type.name().toLowerCase());
            int color = found ? MemoryBookUi.COL_INK : 0xFFAAAAAA;
            String prefix = found ? "☺ " : "？ ";
            g.drawString(font, prefix + name.getString(), lx, ty, color, false);
            i++;
        }
    }

    private void renderJournalTab(GuiGraphics g, int x, int y, int w, int h) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.journal_title"),
                x, y, MemoryBookUi.COL_INK, false);
        int ly = y + 16;
        var entries = bond.getExplorationJournal();
        if (entries.isEmpty()) {
            g.drawString(font, Component.translatable("gui.cocojenna.memory_book.journal_empty"),
                    x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
            return;
        }
        for (String key : entries) {
            if (ly > y + h - 8) break;
            Component line = journalLine(key);
            for (var wrapped : font.split(line, w - 8)) {
                if (ly > y + h - 8) break;
                g.drawString(font, wrapped, x + 4, ly, MemoryBookUi.COL_INK_SOFT, false);
                ly += 10;
            }
            ly += 2;
        }
    }

    private Component journalLine(String key) {
        if (key.startsWith("lore:")) {
            return Component.translatable("explore.cocojenna.journal.lore",
                    Component.translatable("explore.cocojenna.lore." + key.substring(5) + ".title"));
        }
        if (key.startsWith("wildcat:")) {
            return Component.translatable("explore.cocojenna.journal.wildcat",
                    Component.translatable("explore.cocojenna.wildcat." + key.substring(8)));
        }
        if (key.startsWith("dungeon:")) {
            return Component.translatable("explore.cocojenna.journal.dungeon", key.substring(8));
        }
        return Component.translatable(key);
    }

    private void renderSettingsTab(GuiGraphics g, int x, int y, int w) {
        g.drawString(font, Component.translatable("gui.cocojenna.settings.title"),
                x, y, MemoryBookUi.COL_INK, false);
        g.drawString(font, Component.translatable("gui.cocojenna.settings.follow_label"),
                x, y + 14, MemoryBookUi.COL_INK_SOFT, false);
    }

    private void renderSidebar(GuiGraphics g, int left, int top, int mouseX, int mouseY) {
        g.drawString(font, Component.translatable("gui.cocojenna.memory_book.sidebar"),
                left + 8, top + MemoryBookUi.HEADER_H + 2, MemoryBookUi.COL_INK_SOFT, false);
        for (int i = 0; i < TAB_COUNT; i++) {
            boolean selected = tab == i;
            boolean hovered = mouseX >= tabX[i] && mouseX < tabX[i] + tabW
                    && mouseY >= tabY[i] && mouseY < tabY[i] + tabH;
            ResourceLocation icon = GuiTextures.memoryBookTab(i);
            MemoryBookUi.drawTab(g, font, tabX[i], tabY[i], tabW, tabH,
                    selected, hovered, icon,
                    Component.translatable("gui.cocojenna.memory_book.tab." + TAB_KEYS[i]));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < TAB_COUNT; i++) {
                if (mouseX >= tabX[i] && mouseX < tabX[i] + tabW
                        && mouseY >= tabY[i] && mouseY < tabY[i] + tabH) {
                    tab = i;
                    init();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int regionThreshold(String region) {
        return switch (region) {
            case "velvet_forest", "moon_alley", "first_cry_plains" -> 0;
            case "howling_gorge", "blind_water_river" -> 5;
            case "dawn_highlands", "cardboard_slums" -> 10;
            case "moonlight_beach", "catnip_highlands" -> 20;
            case "rainbow_canyon" -> 30;
            case "forgotten_wastes" -> 40;
            case "stardust_desert" -> 50;
            default -> 0;
        };
    }

    private static String repKey(String region) {
        return switch (region) {
            case "blind_water_river" -> "blind_port";
            case "dawn_highlands", "moon_alley" -> "dawn";
            case "howling_gorge", "forgotten_wastes", "stardust_desert" -> "royal";
            case "cardboard_slums" -> "gear_town";
            default -> "first_cry";
        };
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
