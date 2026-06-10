package com.cocojenna.client.gui;

import com.cocojenna.dialogue.DialogueChoice;
import com.cocojenna.dialogue.DialogueLine;
import com.cocojenna.network.DialogueResultPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/** GALGAME 風對話介面 — 粉框、立繪、打字機文字. */
public class GalgameDialogueScreen extends Screen {

    private static final int BOX_HEIGHT = 110;
    private static final int PORTRAIT_SIZE = 96;
    private static final int TYPE_SPEED = 2;
    private static final int CHOICE_W = 220;
    private static final int CHOICE_H = 18;

    private final String sceneId;
    private final List<DialogueLine> lines;
    private int lineIndex;
    private int charIndex;
    private int tickCounter;
    private boolean lineComplete;
    private String pendingAction;

    private final List<DialogueChoice> activeChoices = new ArrayList<>();
    private int hoveredChoice = -1;

    public GalgameDialogueScreen(String sceneId, List<DialogueLine> lines) {
        super(Component.empty());
        this.sceneId = sceneId;
        this.lines = lines;
    }

    public static void open(String sceneId, List<DialogueLine> lines) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        mc.setScreen(new GalgameDialogueScreen(sceneId, new ArrayList<>(lines)));
    }

    @Override
    public void tick() {
        tickCounter++;
        if (!lineComplete && tickCounter % TYPE_SPEED == 0) {
            DialogueLine line = currentLine();
            if (line != null) {
                String full = Component.translatable(line.textKey()).getString();
                if (charIndex < full.length()) {
                    charIndex++;
                } else {
                    lineComplete = true;
                    if (line.hasChoices()) {
                        activeChoices.clear();
                        activeChoices.addAll(line.choices());
                    }
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fill(0, 0, width, height, 0x99000000);

        DialogueLine line = currentLine();
        if (line == null) {
            return;
        }

        int boxTop = height - BOX_HEIGHT - 24;
        int boxLeft = 24;
        int boxRight = width - 24;

        g.fill(boxLeft, boxTop, boxRight, boxTop + BOX_HEIGHT, 0xC0FFB6C1);
        g.fill(boxLeft + 2, boxTop + 2, boxRight - 2, boxTop + BOX_HEIGHT - 2, 0xF0FFF0F5);
        int border = CocoJennaUi.COL_ACCENT;
        g.fill(boxLeft, boxTop, boxRight, boxTop + 2, border);
        g.fill(boxLeft, boxTop + BOX_HEIGHT - 2, boxRight, boxTop + BOX_HEIGHT, border);
        g.fill(boxLeft, boxTop, boxLeft + 2, boxTop + BOX_HEIGHT, border);
        g.fill(boxRight - 2, boxTop, boxRight, boxTop + BOX_HEIGHT, border);

        int px = boxLeft + 12;
        int py = boxTop - PORTRAIT_SIZE + 20;
        g.fill(px, py, px + PORTRAIT_SIZE, py + PORTRAIT_SIZE, CocoJennaUi.COL_FRAME);
        ResourceLocation portrait = GuiTextures.portraitForLine(line.speakerKey(), line.portrait());
        CocoJennaUi.drawPortraitArt(g, portrait, px + 2, py + 2, PORTRAIT_SIZE - 4);

        Component speaker = Component.translatable(line.speakerKey());
        int nameW = font.width(speaker) + 16;
        g.fill(boxLeft + 120, boxTop - 22, boxLeft + 120 + nameW, boxTop - 4, CocoJennaUi.COL_ACCENT);
        g.drawString(font, speaker, boxLeft + 128, boxTop - 18, CocoJennaUi.COL_INK, false);

        String full = Component.translatable(line.textKey()).getString();
        String shown = full.substring(0, Math.min(charIndex, full.length()));
        g.drawWordWrap(font, Component.literal(shown), boxLeft + 120, boxTop + 12, boxRight - boxLeft - 140, CocoJennaUi.COL_INK);

        if (lineComplete && activeChoices.isEmpty()) {
            g.drawString(font, Component.translatable("gui.cocojenna.galgame.continue_hint"),
                    boxRight - 90, boxTop + BOX_HEIGHT - 18, 0xFFAA8899, false);
        }

        renderChoices(g, mouseX, mouseY, boxTop);
    }

    private void renderChoices(GuiGraphics g, int mouseX, int mouseY, int boxTop) {
        if (activeChoices.isEmpty()) {
            hoveredChoice = -1;
            return;
        }
        int cx = width / 2 - CHOICE_W / 2;
        int cy = boxTop - 12 - activeChoices.size() * (CHOICE_H + 4);
        hoveredChoice = -1;
        for (int i = 0; i < activeChoices.size(); i++) {
            int y = cy + i * (CHOICE_H + 4);
            boolean hovered = mouseX >= cx && mouseX < cx + CHOICE_W
                    && mouseY >= y && mouseY < y + CHOICE_H;
            if (hovered) hoveredChoice = i;
            Component label = Component.translatable(activeChoices.get(i).labelKey());
            CocoJennaUi.drawButton(g, font, cx, y, CHOICE_W, CHOICE_H, label, hovered, true, true);
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == 257 || key == 32) {
            advance();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (hoveredChoice >= 0 && hoveredChoice < activeChoices.size()) {
                DialogueChoice choice = activeChoices.get(hoveredChoice);
                pendingAction = choice.actionId();
                closeScene(choice.actionId());
                return true;
            }
            advance();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    private void advance() {
        DialogueLine line = currentLine();
        if (line == null) {
            closeScene(pendingAction);
            return;
        }
        if (!lineComplete) {
            charIndex = Component.translatable(line.textKey()).getString().length();
            lineComplete = true;
            if (line.hasChoices()) {
                activeChoices.clear();
                activeChoices.addAll(line.choices());
            }
            return;
        }
        if (!activeChoices.isEmpty()) {
            return;
        }
        if (line.completeAction() != null) {
            pendingAction = line.completeAction();
        }
        lineIndex++;
        charIndex = 0;
        lineComplete = false;
        activeChoices.clear();
        tickCounter = 0;
        if (lineIndex >= lines.size()) {
            closeScene(pendingAction);
        }
    }

    private void closeScene(String actionId) {
        ModNetwork.CHANNEL.sendToServer(new DialogueResultPacket(sceneId, actionId));
        onClose();
    }

    private DialogueLine currentLine() {
        if (lineIndex < 0 || lineIndex >= lines.size()) {
            return null;
        }
        return lines.get(lineIndex);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
