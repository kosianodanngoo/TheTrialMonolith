package io.github.kosianodangoo.trialmonolith.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class InputWidget extends EditBox {
    public int borderColor = 0xFFFFFFFF;
    public int innerColor = 0x000000FF;
    public int borderWidth = 1;

    public InputWidget(Font font, int x, int y, int w, int h, Component message) {
        super(font, x, y, w, h, message);
        this.setBordered(false);
    }

    @Override
    public int getInnerWidth() {
        return this.width - borderWidth - borderWidth;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible()) {
            int x = getX() - borderWidth;
            int y = getY() - borderWidth;
            int xEdge = getX() + borderWidth + width;
            int yEdge = getY() + borderWidth + height;
            int x1 = x - borderWidth;
            int x2 = xEdge + borderWidth;
            int y1 = y - borderWidth;
            int y2 = yEdge + borderWidth;
            // inner
            guiGraphics.fill(x1, y1, x2, y2, innerColor);

            // border
            guiGraphics.fill(x1, y, xEdge, y1, borderColor);
            guiGraphics.fill(x2, y1, xEdge, yEdge, borderColor);
            guiGraphics.fill(x, y2, x2, yEdge, borderColor);
            guiGraphics.fill(x, y, x1, y2, borderColor);
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
