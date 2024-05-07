package com.spygineer.hudoverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
	private final Screen parent;

	private CycleButton<Boolean> btnShowCoords;
	private CycleButton<Boolean> btnShowBiome;
	private CycleButton<Boolean> btnShowTime;
	private CycleButton<Boolean> btnShowTime24h;
	private EditBoxWithLabel offsetXBox, offsetYBox;

	public ConfigScreen(Screen parent) {
		super(Component.literal("HUD overlay config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		btnShowCoords = CycleButton.onOffBuilder(Config.DISPLAY_COORDS.get())
				.create(this.width / 2 - 100, this.height / 2 - 100,
						200, 20, Component.literal("Show coordinates"));
		addRenderableWidget(btnShowCoords);
		btnShowBiome = CycleButton.onOffBuilder(Config.DISPLAY_BIOME.get())
				.create(this.width / 2 - 100, this.height / 2 - 70,
						200, 20, Component.literal("Show biome"));
		addRenderableWidget(btnShowBiome);
		btnShowTime = CycleButton.onOffBuilder(Config.DISPLAY_TIME.get())
				.create(this.width / 2 - 100, this.height / 2 - 40,
						200, 20, Component.literal("Show time"));
		addRenderableWidget(btnShowTime);
		btnShowTime24h = CycleButton.onOffBuilder(Config.DISPLAY_TIME_24H.get())
				.create(this.width / 2 - 100, this.height / 2 - 10,
						200, 20, Component.literal("Show time in 24 hours"));
		addRenderableWidget(btnShowTime24h);

		offsetXBox = new EditBoxWithLabel(font,
			this.width / 2 - 50 - font.width("X Offset:  "), (this.height / 2 - 10) + 30,
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
		addRenderableWidget(offsetXBox);
		offsetYBox = new EditBoxWithLabel(font,
			this.width / 2 - 50 - font.width("Y Offset:  "), (this.height / 2 - 10) + 60,
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
		addRenderableWidget(offsetYBox);

		addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, b -> {
			save();
			Minecraft.getInstance().setScreen(parent);
		}).pos(this.width / 2 - 100, this.height / 2 + 100).size(200, 20).build());
	}

	@Override
	public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float ticks) {
		this.renderDirtBackground(context);
		context.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
		super.render(context, mouseX, mouseY, ticks);
	}

	public void save() {
		Config.DISPLAY_COORDS.set(btnShowCoords.getValue());
		Config.DISPLAY_BIOME.set(btnShowBiome.getValue());
		Config.DISPLAY_TIME.set(btnShowTime.getValue());
		Config.DISPLAY_TIME_24H.set(btnShowTime24h.getValue());
		try {
			Config.OVERLAY_OFFSET_X.set(Integer.valueOf(offsetXBox.getValue()));
			Config.OVERLAY_OFFSET_Y.set(Integer.valueOf(offsetYBox.getValue()));
		} catch (Exception e) {
			SimpleHudOverlay.LOGGER.error("Failed to set x/y offset, exception thrown: {}", e.getMessage());
		}
	}

	@Override
	public void onClose() {
		save();
		if (minecraft != null && parent != null) minecraft.setScreen(parent);
		else super.onClose();
	}
}
