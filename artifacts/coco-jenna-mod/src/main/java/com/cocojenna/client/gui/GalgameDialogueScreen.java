package com.cocojenna.client.gui;

import com.cocojenna.dialogue.DialogueChoice;
import com.cocojenna.dialogue.DialogueLine;
import com.cocojenna.dialogue.PortraitSide;
import com.cocojenna.network.DialogueResultPacket;
import com.cocojenna.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** GALGAME 風對話介面 v2 — 九切片框、背景、Auto/Skip/Log. */
public class GalgameDialogueScreen extends Screen {

    private static final int BOX_HEIGHT = 120;
    private static final int PORTRAIT_SIZE = 128;
    private static final int TYPE_SPEED = 2;
    private static final int CHOICE_W = 260;
    private static final int CHOICE_H = 22;
    private static final int LOG_MAX = 20;
    private static final int AUTO_DELAY = 45;
    private static final int PUNCTUATION_PAUSE = 12;

    private final String sceneId;
    private final List<DialogueLine> lines;
    private int lineIndex;
    private int charIndex;
    private int tickCounter;
    private int autoWaitTicks;
    private boolean lineComplete;
    private String pendingAction;
    private float portraitFade = 0f;
    private String currentBackground = "";

    private boolean autoMode;
    private boolean skipMode;
    private boolean showLog;
    private boolean hideUi;

    private final List<DialogueChoice> activeChoices = new ArrayList<>();
    private final Deque<String> logEntries = new ArrayDeque<>();
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
        portraitFade = Math.min(1f, portraitFade + 0.05f);

        DialogueLine line = currentLine();
        if (line == null) return;

        if (!line.backgroundId().equals(currentBackground)) {
            currentBackground = line.backgroundId();
            portraitFade = 0f;
        }

        if (!lineComplete) {
            if (skipMode) {
                finishTyping(line);
                return;
            }
            if (tickCounter % TYPE_SPEED == 0) {
                String full = fullText(line);
                if (charIndex < full.length()) {
                    charIndex++;
                    char c = full.charAt(charIndex - 1);
                    if (c == '。' || c == '！' || c == '？' || c == '.' || c == '!' || c == '?') {
                        autoWaitTicks = PUNCTUATION_PAUSE;
                    }
                } else {
                    finishTyping(line);
                }
            }
            if (autoWaitTicks > 0) autoWaitTicks--;
            return;
        }

        if (autoMode && lineComplete && activeChoices.isEmpty()) {
            if (autoWaitTicks > 0) {
                autoWaitTicks--;
            } else {
                advance();
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        DialogueLine line = currentLine();
        if (line == null) return;

        String bgId = line.backgroundId().isEmpty() ? "default" : line.backgroundId();
        GalgameDialogueRenderer.drawBackground(g, width, height, bgId, 0.85f);

        int boxTop = height - BOX_HEIGHT - (hideUi ? 8 : 36);
        int boxLeft = 20;
        int boxRight = width - 20;
        int boxW = boxRight - boxLeft;

        if (!hideUi) {
            renderPortraits(g, line, boxTop);
            GalgameDialogueRenderer.drawDialogBox(g, boxLeft, boxTop, boxW, BOX_HEIGHT);
            GalgameDialogueRenderer.drawNamePlate(g, font, boxLeft + 140, boxTop - 28,
                    Component.translatable(line.speakerKey()));

            String shown = fullText(line).substring(0, Math.min(charIndex, fullText(line).length()));
            g.drawWordWrap(font, Component.literal(shown), boxLeft + 140, boxTop + 14,
                    boxW - 160, CocoJennaUi.COL_INK);

            if (lineComplete && activeChoices.isEmpty()) {
                g.drawString(font, Component.translatable("gui.cocojenna.galgame.continue_hint"),
                        boxRight - 96, boxTop + BOX_HEIGHT - 20, 0xFFAA8899, false);
            }

            renderChoices(g, mouseX, mouseY, boxTop);
            renderControls(g);
        } else {
            g.drawWordWrap(font, Component.translatable(line.textKey()),
                    boxLeft + 12, boxTop + 12, boxW - 24, CocoJennaUi.COL_INK);
        }

        if (showLog) {
            GalgameDialogueRenderer.drawLogPanel(g, font, width - 320, 12, 308, height - 24,
                    new ArrayList<>(logEntries));
        }
    }

    private void renderPortraits(GuiGraphics g, DialogueLine line, int boxTop) {
        if (line.portraitSide() == PortraitSide.NONE) return;
        int py = boxTop - PORTRAIT_SIZE + 24;
        if (line.portraitSide() == PortraitSide.LEFT || line.portraitSide() == PortraitSide.RIGHT) {
            int px = line.portraitSide() == PortraitSide.LEFT ? 28 : width - PORTRAIT_SIZE - 28;
            GalgameDialogueRenderer.drawPortrait(g, line, px, py, tickCounter, portraitFade);
        }
    }

    private void renderControls(GuiGraphics g) {
        int y = height - 24;
        int x = 20;
        int bw = 52;
        int bh = 16;
        GalgameDialogueRenderer.drawControlButton(g, font, x, y, bw, bh,
                Component.translatable("gui.cocojenna.galgame.auto"), autoMode);
        GalgameDialogueRenderer.drawControlButton(g, font, x + bw + 4, y, bw, bh,
                Component.translatable("gui.cocojenna.galgame.skip"), skipMode);
        GalgameDialogueRenderer.drawControlButton(g, font, x + (bw + 4) * 2, y, bw, bh,
                Component.translatable("gui.cocojenna.galgame.log"), showLog);
        GalgameDialogueRenderer.drawControlButton(g, font, x + (bw + 4) * 3, y, bw, bh,
                Component.translatable("gui.cocojenna.galgame.hide"), hideUi);
    }

    private void renderChoices(GuiGraphics g, int mouseX, int mouseY, int boxTop) {
        if (activeChoices.isEmpty()) {
            hoveredChoice = -1;
            return;
        }
        int cx = width / 2 - CHOICE_W / 2;
        int cy = boxTop - 16 - activeChoices.size() * (CHOICE_H + 6);
        hoveredChoice = -1;
        for (int i = 0; i < activeChoices.size(); i++) {
            int y = cy + i * (CHOICE_H + 6);
            boolean hovered = mouseX >= cx && mouseX < cx + CHOICE_W
                    && mouseY >= y && mouseY < y + CHOICE_H;
            if (hovered) hoveredChoice = i;
            Component label = Component.translatable(activeChoices.get(i).labelKey())
                    .append(" (").append(String.valueOf(i + 1)).append(")");
            GalgameDialogueRenderer.drawChoicePanel(g, font, cx, y, CHOICE_W, CHOICE_H,
                    label, hovered, false);
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == GLFW.GLFW_KEY_L) {
            showLog = !showLog;
            return true;
        }
        if (key == GLFW.GLFW_KEY_H) {
            hideUi = !hideUi;
            return true;
        }
        if (key == GLFW.GLFW_KEY_A) {
            autoMode = !autoMode;
            return true;
        }
        if (key == GLFW.GLFW_KEY_S && (mods & GLFW.GLFW_MOD_CONTROL) == 0) {
            skipMode = !skipMode;
            return true;
        }
        if (hoveredChoice < 0 && key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_3) {
            int idx = key - GLFW.GLFW_KEY_1;
            if (idx < activeChoices.size()) {
                pickChoice(activeChoices.get(idx));
                return true;
            }
        }
        if (key == 257 || key == 32) {
            advance();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (clickControl((int) mx, (int) my)) return true;
            if (hoveredChoice >= 0 && hoveredChoice < activeChoices.size()) {
                pickChoice(activeChoices.get(hoveredChoice));
                return true;
            }
            advance();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    private boolean clickControl(int mx, int my) {
        int y = height - 24;
        int x = 20;
        int bw = 52;
        int bh = 16;
        if (my < y || my >= y + bh) return false;
        if (mx >= x && mx < x + bw) { autoMode = !autoMode; return true; }
        x += bw + 4;
        if (mx >= x && mx < x + bw) { skipMode = !skipMode; return true; }
        x += bw + 4;
        if (mx >= x && mx < x + bw) { showLog = !showLog; return true; }
        x += bw + 4;
        if (mx >= x && mx < x + bw) { hideUi = !hideUi; return true; }
        return false;
    }

    private void pickChoice(DialogueChoice choice) {
        pendingAction = choice.actionId();
        closeScene(choice.actionId());
    }

    private void advance() {
        DialogueLine line = currentLine();
        if (line == null) {
            closeScene(pendingAction);
            return;
        }
        if (!lineComplete) {
            finishTyping(line);
            return;
        }
        if (!activeChoices.isEmpty()) return;
        if (line.completeAction() != null) pendingAction = line.completeAction();
        pushLog(line);
        lineIndex++;
        charIndex = 0;
        lineComplete = false;
        activeChoices.clear();
        tickCounter = 0;
        autoWaitTicks = 0;
        portraitFade = 0f;
        if (lineIndex >= lines.size()) closeScene(pendingAction);
    }

    private void finishTyping(DialogueLine line) {
        charIndex = fullText(line).length();
        lineComplete = true;
        autoWaitTicks = line.autoDelayTicks() > 0 ? line.autoDelayTicks() : AUTO_DELAY;
        if (line.hasChoices()) {
            activeChoices.clear();
            activeChoices.addAll(line.choices());
        }
    }

    private void pushLog(DialogueLine line) {
        String entry = Component.translatable(line.speakerKey()).getString()
                + ": " + fullText(line);
        logEntries.addLast(entry);
        while (logEntries.size() > LOG_MAX) logEntries.removeFirst();
    }

    private String fullText(DialogueLine line) {
        return Component.translatable(line.textKey()).getString();
    }

    private void closeScene(String actionId) {
        ModNetwork.CHANNEL.sendToServer(new DialogueResultPacket(sceneId, actionId));
        onClose();
    }

    private DialogueLine currentLine() {
        if (lineIndex < 0 || lineIndex >= lines.size()) return null;
        return lines.get(lineIndex);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
