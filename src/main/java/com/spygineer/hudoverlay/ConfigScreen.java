package com.spygineer.hudoverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
	private final Screen parent;

	private CycleButton<Boolean> btnShowFps;
	private CycleButton<Boolean> btnShowCoords;
	private CycleButton<Boolean> btnShowBiome;
	private CycleButton<Boolean> btnShowTime;
	private CycleButton<Boolean> btnShowTime24h;
	private Button resetBtn;
	private EditBoxWithLabel offsetXBox, offsetYBox;
	private ForgeSlider rSlider, gSlider, bSlider;
	private LabelWidget labelColor;

	public ConfigScreen(Screen parent) {
		super(Component.literal("HUD overlay config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		int offset = -140;
		btnShowFps = CycleButton.onOffBuilder(Config.DISPLAY_FPS.get())
				.create(this.width / 2 - 100, this.height / 2 + offset,
						200, 20, Component.literal("Show FPS"));
		offset += 30;
		addRenderableWidget(btnShowFps);
		btnShowCoords = CycleButton.onOffBuilder(Config.DISPLAY_COORDS.get())
				.create(this.width / 2 - 100, this.height / 2 + offset,
						200, 20, Component.literal("Show coordinates"));
		offset += 30;
		addRenderableWidget(btnShowCoords);
		btnShowBiome = CycleButton.onOffBuilder(Config.DISPLAY_BIOME.get())
				.create(this.width / 2 - 100, this.height / 2 + offset,
						200, 20, Component.literal("Show biome"));
		offset += 30;
		addRenderableWidget(btnShowBiome);
		btnShowTime = CycleButton.onOffBuilder(Config.DISPLAY_TIME.get())
				.create(this.width / 2 - 100, this.height / 2 + offset,
						200, 20, Component.literal("Show time"));
		offset += 30;
		addRenderableWidget(btnShowTime);
		btnShowTime24h = CycleButton.onOffBuilder(Config.DISPLAY_TIME_24H.get())
				.create(this.width / 2 - 100, this.height / 2 + offset,
						200, 20, Component.literal("Show time in 24 hours"));
		offset += 40;
		addRenderableWidget(btnShowTime24h);

		offsetXBox = new EditBoxWithLabel(font,
			this.width / 2 - 50 - font.width("X Offset:  "), (this.height / 2 - 10) + offset,
			100, 20, Component.literal("X Offset:  "), String.valueOf(Config.OVERLAY_OFFSET_X.get()),
			(str) -> {
				try {
					Integer.parseInt(str);
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}
		);
		offset += 30;
		addRenderableWidget(offsetXBox);
		offsetYBox = new EditBoxWithLabel(font,
			this.width / 2 - 50 - font.width("Y Offset:  "), (this.height / 2 - 10) + offset,
			100, 20, Component.literal("Y Offset:  "), String.valueOf(Config.OVERLAY_OFFSET_Y.get()),
				(str) -> {
					try {
						Integer.parseInt(str);
					} catch (NumberFormatException e) {
						return false;
					}
					return true;
				}
		);
		offset += 30;
		addRenderableWidget(offsetYBox);
		labelColor = new LabelWidget(font, this.width / 2 - 50, (this.height / 2 - 10) + offset,
				Component.literal("Text Color:")
		);

		offset += 20;
		addRenderableWidget(labelColor);

		int color = Config.TEXT_COLOR.get();
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		rSlider = new ForgeSlider(
			this.width / 2 - 50, (this.height / 2 - 10) + offset,
			100, 10,
			Component.literal("R:"),
			Component.empty(),
			0, 255, r, 1.0, 1,
			true
		);
		offset += 10;
		addRenderableWidget(rSlider);
		gSlider = new ForgeSlider(
			this.width / 2 - 50, (this.height / 2 - 10) + offset,
			100, 10,
			Component.literal("G:"),
			Component.empty(),
			0, 255, g, 1.0, 1,
			true
		);
		offset += 10;
		addRenderableWidget(gSlider);
		bSlider = new ForgeSlider(
			this.width / 2 - 50, (this.height / 2 - 10) + offset,
			100, 10,
			Component.literal("B:"),
			Component.empty(),
			0, 255, b, 1.0, 1,
			true
		);
		offset += 20;
		addRenderableWidget(bSlider);

		resetBtn = new Button.Builder(Component.literal("Reset"), _b -> {
			Config.TEXT_COLOR.set(0xE0E0E0);
			rSlider.setValue(0xE0);
			gSlider.setValue(0xE0);
			bSlider.setValue(0xE0);
		}).pos(this.width / 2 - 25, (this.height / 2 - 10) + offset)
			.size(50, 10).build();
		addRenderableWidget(resetBtn);

		offset += 5;

		addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, _b -> {
			save();
			Minecraft.getInstance().setScreen(parent);
		}).pos(this.width / 2 - 100, this.height / 2 + offset).size(200, 20).build());
	}

	@Override
	public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float ticks) {
		this.renderDirtBackground(context);
		context.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);

		int rectY = rSlider.getY();
		int rectY2 = bSlider.getY() + 10;
		int rectX = rSlider.getX() + rSlider.getWidth() + 20;
		int rectX2 = rectX + (rectY2 - rectY);

		try {
			int color = 0xFF << 24 | rSlider.getValueInt() << 16 | gSlider.getValueInt() << 8 | bSlider.getValueInt();
			context.fill(rectX, rectY, rectX2, rectY2, color);
		} catch (NumberFormatException e) {
			SimpleHudOverlay.LOGGER.error("Failed to set text color preview, exception thrown: {}", e.getMessage());
		}
		super.render(context, mouseX, mouseY, ticks);
	}

	public void save() {
		Config.DISPLAY_FPS.set(btnShowFps.getValue());
		Config.DISPLAY_COORDS.set(btnShowCoords.getValue());
		Config.DISPLAY_BIOME.set(btnShowBiome.getValue());
		Config.DISPLAY_TIME.set(btnShowTime.getValue());
		Config.DISPLAY_TIME_24H.set(btnShowTime24h.getValue());
		try {
			Config.OVERLAY_OFFSET_X.set(Integer.valueOf(offsetXBox.getValue()));
			Config.OVERLAY_OFFSET_Y.set(Integer.valueOf(offsetYBox.getValue()));
		} catch (NumberFormatException e) {
			SimpleHudOverlay.LOGGER.error("Failed to set x/y offset, exception thrown: {}", e.getMessage());
		}
		try {
			Config.TEXT_COLOR.set(rSlider.getValueInt() << 16 | gSlider.getValueInt() << 8 | bSlider.getValueInt());
		} catch (NumberFormatException e) {
			SimpleHudOverlay.LOGGER.error("Failed to set text color, exception thrown: {}", e.getMessage());
		}
	}

	@Override
	public void onClose() {
		save();
		if (minecraft != null && parent != null) minecraft.setScreen(parent);
		else super.onClose();
	}
}
