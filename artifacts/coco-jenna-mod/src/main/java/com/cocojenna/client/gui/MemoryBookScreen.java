package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.capability.ModCapabilities;
import com.cocojenna.entity.SisterBondSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * 記憶之書 GUI — 顯示可可、珍奶和玩家的關係面板。
 *
 * <p>分頁：
 * <ul>
 *   <li>左頁：可可的三軌數值 + 行為日誌</li>
 *   <li>右頁：珍奶的三軌數值 + 姊妹羈絆</li>
 *   <li>第二頁展開：記憶碎片文本</li>
 * </ul>
 *
 * <p>視覺風格：
 * <ul>
 *   <li>羊皮紙背景，繁體中文手寫風格字體</li>
 *   <li>可可相關區域：深色邊框</li>
 *   <li>珍奶相關區域：暖色邊框</li>
 *   <li>讀取記憶碎片時有逐字打字效果</li>
 * </ul>
 */
public class MemoryBookScreen extends Screen {

    private static final int PAGE_WIDTH = 154;
    private static final int PAGE_HEIGHT = 180;

    private int currentPage = 0;
    private int typingTick = 0;
    private BondData bond;

    public MemoryBookScreen(BondData bond) {
        super(Component.translatable("gui.cocojenna.memory_book.title"));
        this.bond = bond;
    }

    public static void open(Player player) {
        BondData bond = ModCapabilities.getOrDefault(player);
        net.minecraft.client.Minecraft.getInstance().setScreen(new MemoryBookScreen(bond));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        int centerX = width / 2;
        int centerY = height / 2;
        int bookLeft = centerX - PAGE_WIDTH;
        int bookTop = centerY - PAGE_HEIGHT / 2;

        // ── 背景（羊皮紙色）───────────────────────────────────────────
        graphics.fill(bookLeft, bookTop, bookLeft + PAGE_WIDTH * 2, bookTop + PAGE_HEIGHT,
                0xFFF0E68C);
        graphics.fill(bookLeft + PAGE_WIDTH - 1, bookTop, bookLeft + PAGE_WIDTH + 1, bookTop + PAGE_HEIGHT,
                0xFF8B7355); // 書脊

        switch (currentPage) {
            case 0 -> renderBondPage(graphics, bookLeft, bookTop);
            case 1 -> renderMemoryShardPage(graphics, bookLeft, bookTop);
        }

        // ── 頁碼 ────────────────────────────────────────────────────────
        graphics.drawString(font, Component.literal((currentPage + 1) + " / 2"),
                centerX - 10, bookTop + PAGE_HEIGHT - 15, 0xFF5C4033, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderBondPage(GuiGraphics graphics, int bookLeft, int bookTop) {
        int leftX = bookLeft + 10;
        int rightX = bookLeft + PAGE_WIDTH + 10;

        // ── 左頁：可可 ────────────────────────────────────────────────
        graphics.drawString(font,
                Component.translatable("gui.cocojenna.memory_book.coco_title")
                        .withStyle(net.minecraft.ChatFormatting.DARK_GRAY),
                leftX, bookTop + 10, 0xFF2a2a2a, false);

        // 情感條
        renderBar(graphics, leftX, bookTop + 25, bond.getCocoEmotion(), 100f,
                0xFF1a1a1a, "❤ " + (int) bond.getCocoEmotion());
        // 獨立性條
        renderBar(graphics, leftX, bookTop + 40, bond.getCocoIndependence(), 100f,
                0xFF4a4a8a, "🌙 " + (int) bond.getCocoIndependence());
        // 覺醒條
        renderBar(graphics, leftX, bookTop + 55, bond.getCocoAwakening(), 50f,
                0xFF9933CC, "✦ " + bond.getCocoAwakening() + "/50");

        // 情感等級
        graphics.drawString(font,
                Component.literal(bond.getCocoEmotionLevel().name()),
                leftX, bookTop + 70, 0xFF5C4033, false);

        // 行為提示
        graphics.drawString(font,
                Component.translatable("gui.cocojenna.memory_book.coco_hint." + bond.getCocoEmotionLevel().name().toLowerCase()),
                leftX, bookTop + 85, 0xFF8B7355, false);

        // ── 右頁：珍奶 ────────────────────────────────────────────────
        graphics.drawString(font,
                Component.translatable("gui.cocojenna.memory_book.jenna_title")
                        .withStyle(net.minecraft.ChatFormatting.DARK_GREEN),
                rightX, bookTop + 10, 0xFF2a5a2a, false);

        renderBar(graphics, rightX, bookTop + 25, bond.getJennaEmotion(), 100f,
                0xFF1a5a1a, "❤ " + (int) bond.getJennaEmotion());
        renderBar(graphics, rightX, bookTop + 40, bond.getJennaIndependence(), 100f,
                0xFF5a8a5a, "🌿 " + (int) bond.getJennaIndependence());
        renderBar(graphics, rightX, bookTop + 55, bond.getJennaAwakening(), 50f,
                0xFF339933, "✦ " + bond.getJennaAwakening() + "/50");

        // 姊妹羈絆（跨頁中央）
        int centerX = bookLeft + PAGE_WIDTH;
        graphics.drawString(font,
                Component.translatable("gui.cocojenna.memory_book.sister_bond",
                        SisterBondSystem.getBondTitle(bond.getSisterBond())),
                centerX - 30, bookTop + 120, 0xFF8B2252, false);
        renderBar(graphics, bookLeft + PAGE_WIDTH / 2, bookTop + 135, bond.getSisterBond(), 100f,
                0xFFCC3366, "💕 " + (int) bond.getSisterBond());

        // 記憶碎片進度
        graphics.drawString(font,
                Component.literal("📖 " + bond.getMemoryShardsTotal() + "/50"),
                centerX - 15, bookTop + 155, 0xFF33AABB, false);
    }

    private void renderMemoryShardPage(GuiGraphics graphics, int bookLeft, int bookTop) {
        graphics.drawString(font,
                Component.translatable("gui.cocojenna.memory_book.shards_title"),
                bookLeft + 10, bookTop + 10, 0xFF2a2a2a, false);
        // TODO: 顯示已收集的記憶碎片文本列表（分頁）
        graphics.drawString(font,
                Component.literal("記憶碎片: " + bond.getMemoryShardsTotal() + " 個"),
                bookLeft + 10, bookTop + 30, 0xFF5C4033, false);
    }

    private void renderBar(GuiGraphics graphics, int x, int y, float value, float max,
            int color, String label) {
        int BAR_W = 120;
        int BAR_H = 8;
        // 背景
        graphics.fill(x, y, x + BAR_W, y + BAR_H, 0xFF555555);
        // 數值
        int filled = (int) (BAR_W * value / max);
        graphics.fill(x, y, x + filled, y + BAR_H, color);
        // 標籤
        graphics.drawString(font, Component.literal(label), x, y - 10, 0xFF2a2a2a, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 翻頁箭頭
        int centerX = width / 2;
        int bookTop = height / 2 - PAGE_HEIGHT / 2;

        if (mouseX < centerX - PAGE_WIDTH + 20 && mouseY > bookTop + PAGE_HEIGHT - 25) {
            if (currentPage > 0) currentPage--;
        } else if (mouseX > centerX + PAGE_WIDTH - 20 && mouseY > bookTop + PAGE_HEIGHT - 25) {
            if (currentPage < 1) currentPage++;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
