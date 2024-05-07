package com.spygineer.hudoverlay;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = SimpleHudOverlay.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.BooleanValue DISPLAY_COORDS = BUILDER
			.comment("Should the player coordinates be displayed?")
			.define("display_coords", true);
	public static final ForgeConfigSpec.BooleanValue DISPLAY_BIOME = BUILDER
			.comment("Should the current biome be displayed?")
			.define("display_biome", false);
	public static final ForgeConfigSpec.BooleanValue DISPLAY_TIME = BUILDER
			.comment("Should the current in-game time be displayed?")
			.define("display_time", false);
	public static final ForgeConfigSpec.BooleanValue DISPLAY_TIME_24H = BUILDER
			.comment("Display the in-game time in 24 hours.")
			.define("time_24h", true);
	public static final ForgeConfigSpec.IntValue OVERLAY_OFFSET_X = BUILDER
			.comment("Overlay's x offset")
			.defineInRange("offset_x", 5, 0, Integer.MAX_VALUE);
	public static final ForgeConfigSpec.IntValue OVERLAY_OFFSET_Y = BUILDER
			.comment("Overlay's y offset")
			.defineInRange("offset_y", 5, 0, Integer.MAX_VALUE);

	public static final ForgeConfigSpec SPEC = BUILDER.build();

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
	}

	public void save() {
		SPEC.save();
	}
}
