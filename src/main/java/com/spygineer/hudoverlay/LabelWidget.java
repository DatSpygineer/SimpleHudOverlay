package com.spygineer.hudoverlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LabelWidget extends AbstractWidget {
	private Font font;
	private int color;

	public LabelWidget(Font font, int x, int y, Component message, int color) {
		super(x, y, font.width(message.getString()), font.lineHeight, message);
		this.font = font;
		this.color = color;
	}
	public LabelWidget(Font font, int x, int y, Component message) {
		this(font, x, y, message, 0xE0E0E0);
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
		guiGraphics.drawString(font, getMessage(), getX(), getY(), color);
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
	}

	public int getColor() { return color; }
	public void setColor(int color) { this.color = color; }
	public Font getFont() { return font; }
	public void setFont(Font font) { this.font = font; }
}
