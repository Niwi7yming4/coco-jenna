package com.cocojenna.client.gui;

import com.cocojenna.capability.BondData;
import com.cocojenna.network.ModNetwork;
import com.cocojenna.network.PictureBookEditPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** 繪本編輯器 — 背景、文字、貼紙、濾鏡. */
public class PictureBookScreen extends Screen {

    private static final String[] BACKGROUNDS = {"dawn", "moon", "garden", "festival", "memory"};
    private static final String[] STICKERS = {"coco", "jenna", "furball", "heart", "star"};
    private static final String[] FILTERS = {"none", "warm", "cool", "sepia", "dream"};

    private final BondData bond;
    private int bgIdx, stickerIdx, filterIdx;
    private int pageIdx;
    private EditBox captionBox;

    public PictureBookScreen(BondData bond) {
        super(Component.translatable("gui.cocojenna.picture_book.title"));
        this.bond = bond;
    }

    public static void open(BondData bond) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new PictureBookScreen(bond));
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = (width - 300) / 2;
        int top = (height - 200) / 2;
        addRenderableWidget(new ParchmentButton(left + 10, top + 150, 80, 16,
                Component.translatable("gui.cocojenna.picture_book.bg"),
                b -> { bgIdx = (bgIdx + 1) % BACKGROUNDS.length; }));
        addRenderableWidget(new ParchmentButton(left + 100, top + 150, 80, 16,
                Component.translatable("gui.cocojenna.picture_book.sticker"),
                b -> { stickerIdx = (stickerIdx + 1) % STICKERS.length; }));
        addRenderableWidget(new ParchmentButton(left + 190, top + 150, 80, 16,
                Component.translatable("gui.cocojenna.picture_book.filter"),
                b -> { filterIdx = (filterIdx + 1) % FILTERS.length; }));
        addRenderableWidget(new ParchmentButton(left + 90, top + 172, 120, 18,
                Component.translatable("gui.cocojenna.picture_book.save"),
                b -> savePage()));
        if (!bond.getPictureBookPages().isEmpty()) {
            addRenderableWidget(new ParchmentButton(left + 10, top + 172, 70, 18,
                    Component.translatable("gui.cocojenna.picture_book.shelve"),
                    b -> ModNetwork.CHANNEL.sendToServer(
                            new com.cocojenna.network.LibraryShelfPacket(0,
                                    Math.max(0, bond.getPictureBookPages().size() - 1)))));
        }
        captionBox = new EditBox(font, left + 20, top + 118, 260, 16,
                Component.translatable("gui.cocojenna.picture_book.caption_hint"));
        captionBox.setMaxLength(48);
        captionBox.setHint(Component.translatable("gui.cocojenna.picture_book.caption_hint"));
        addRenderableWidget(captionBox);
    }

    private void savePage() {
        String caption = captionBox != null ? captionBox.getValue().trim() : "";
        ModNetwork.CHANNEL.sendToServer(new PictureBookEditPacket(
                BACKGROUNDS[bgIdx], caption.isEmpty() ? "..." : caption,
                STICKERS[stickerIdx], FILTERS[filterIdx]));
        pageIdx++;
        if (captionBox != null) captionBox.setValue("");
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        int left = (width - 300) / 2;
        int top = (height - 200) / 2;
        CocoJennaUi.drawPanel(g, left, top, 300, 200);
        g.drawCenteredString(font, title, width / 2, top + 8, CocoJennaUi.COL_INK);

        int preview = previewColor();
        g.fill(left + 20, top + 28, left + 280, top + 138, preview);
        g.drawCenteredString(font, BACKGROUNDS[bgIdx], left + 150, top + 72, 0xFFFFFFFF);
        g.drawCenteredString(font, STICKERS[stickerIdx], left + 150, top + 88, CocoJennaUi.COL_ACCENT);
        g.drawCenteredString(font, FILTERS[filterIdx], left + 150, top + 104, CocoJennaUi.COL_INK_SOFT);
        if (captionBox != null && !captionBox.getValue().isEmpty()) {
            g.drawCenteredString(font, captionBox.getValue(), left + 150, top + 56, 0xFFFFFFFF);
        }

        List<BondData.PictureBookPage> pages = bond.getPictureBookPages();
        g.drawString(font, Component.translatable("gui.cocojenna.picture_book.pages", pages.size()),
                left + 14, top + 124, CocoJennaUi.COL_INK_SOFT);
        super.render(g, mx, my, partial);
    }

    private int previewColor() {
        return switch (FILTERS[filterIdx]) {
            case "warm" -> 0xFF886655;
            case "cool" -> 0xFF556688;
            case "sepia" -> 0xFF8A7A60;
            case "dream" -> 0xFF6A5A8A;
            default -> switch (BACKGROUNDS[bgIdx]) {
                case "moon" -> 0xFF2A2848;
                case "garden" -> 0xFF4A7A4A;
                case "festival" -> 0xFF8A4A6A;
                case "memory" -> 0xFF5A5A7A;
                default -> 0xFF7A8AAA;
            };
        };
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
