package com.spygineer.hudoverlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class EditBoxWithLabel extends EditBox {
	private final Component label;
	private final Font font;
	private final Function<String, Boolean> verify;

	public EditBoxWithLabel(Font font, int x, int y, int with, int height, Component label) {
		super(font, x, y, with, height, label);
		this.label = label;
		this.font = font;
		this.verify = (str) -> true;
	}

	public EditBoxWithLabel(Font font, int x, int y, int with, int height, Component label, String value) {
		super(font, x, y, with, height, label);
		this.label = label;
		this.font = font;
		setValue(value);
		this.verify = (str) -> true;
	}
	public EditBoxWithLabel(Font font, int x, int y, int with, int height, Component label, EditBox toCopy) {
		super(font, x, y, with, height, label);
		this.label = label;
		this.font = font;
		setValue(toCopy.getValue());
		this.verify = (str) -> true;
	}
	public EditBoxWithLabel(Font font, int x, int y, int with, int height, Component label, Function<String, Boolean> verify) {
		super(font, x, y, with, height, label);
		this.label = label;
		this.font = font;
		this.verify = verify;
	}

	public EditBoxWithLabel(Font font, int x, int y, int with, int height, Component label, String value, Function<String, Boolean> verify) {
		super(font, x, y, with, height, label);
		this.label = label;
		this.font = font;
		setValue(value);
		this.verify = verify;
	}
	public EditBoxWithLabel(Font font, int x, int y, int with, int height, Component label, EditBox toCopy, Function<String, Boolean> verify) {
		super(font, x, y, with, height, label);
		this.label = label;
		this.font = font;
		setValue(toCopy.getValue());
		this.verify = verify;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float p_93660_) {
		var width = font.width(label.getString());
		context.drawString(font, label.getString(), this.getX(), this.getY() + height / 2 - font.lineHeight / 2, 0xE0E0E0);
		setX(getX() + width + 2);
		super.render(context, mouseX, mouseY, p_93660_);
		setX(getX() - width - 2);
	}
	@Override
	public int getWidth() {
		return super.getWidth() + font.width(label.getString());
	}

	@Override
	public void insertText(String text) {
		if (this.verify.apply(text)) {
			super.insertText(text);
		}
	}
}
