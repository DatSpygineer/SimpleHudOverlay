package com.spygineer.hudoverlay;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SimpleHudOverlay.MODID)
public class SimpleHudOverlay {

	// Define mod id in a common place for everything to reference
	public static final String MODID = "simplehudoverlay";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();

	public SimpleHudOverlay() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
					() -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen))
			);
		}
	}

	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {

		}
	}

	@Mod.EventBusSubscriber()
	public static class PlayerEvents {
		public static Optional<? extends Registry<Biome>> getBiomeRegistry(Level level) {
			return level.registryAccess().registry(ForgeRegistries.Keys.BIOMES);
		}
		public static Optional<ResourceLocation> getKeyForBiome(Level level, Biome biome) {
			return getBiomeRegistry(level).isPresent() ? Optional.ofNullable(getBiomeRegistry(level).get().getKey(biome)) : Optional.empty();
		}
		public static Optional<Biome> getBiomeForKey(Level level, ResourceLocation key) {
			return getBiomeRegistry(level).isPresent() ? getBiomeRegistry(level).get().getOptional(key) : Optional.empty();
		}
		@OnlyIn(Dist.CLIENT)
		public static String getBiomeName(Level level, Biome biome) {
			return getKeyForBiome(level, biome).isPresent() ? I18n.get(Util.makeDescriptionId("biome", getKeyForBiome(level, biome).get())) : "";
		}
		@OnlyIn(Dist.CLIENT)
		public static String getBiomeName(Level level, ResourceLocation key) {
			if (getBiomeForKey(level, key).isPresent()) {
				return getBiomeName(level, getBiomeForKey(level, key).get());
			}
			return "";
		}
		@OnlyIn(Dist.CLIENT)
		private static String getDimensionName(ResourceLocation dimensionKey) {
			String name = I18n.get(Util.makeDescriptionId("dimension", dimensionKey));
			if (name.equals(Util.makeDescriptionId("dimension", dimensionKey))) {
				name = dimensionKey.toString();
				if (name.contains(":")) {
					name = name.substring(name.indexOf(":") + 1);
				}
				name = WordUtils.capitalize(name.replace('_', ' '));
			}
			return name;
		}
		@OnlyIn(Dist.CLIENT)
		public static String dimensionKeysToString(List<ResourceLocation> dimensions) {
			Set<String> dimensionNames = new HashSet<>();
			dimensions.forEach((key) -> dimensionNames.add(getDimensionName(key)));
			return String.join(", ", dimensionNames);
		}

		@SubscribeEvent
		public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
			final var inst = Minecraft.getInstance();
			if (!inst.options.renderDebug && inst.player != null) {
				try {
					final var pos = inst.player.blockPosition();
					final var level = inst.player.level();
					final var biome = getBiomeName(level, level.getBiome(pos).get());
					final var time = level.getDayTime();

					var g = event.getGuiGraphics();
					var x = Config.OVERLAY_OFFSET_X.get();
					var y = Config.OVERLAY_OFFSET_Y.get();
					if (Config.DISPLAY_COORDS.get()) {
						g.drawString(inst.font, String.format("X: %d, Y: %d, Z: %d", pos.getX(), pos.getY(), pos.getZ()), x, y, 0xE0E0E0);
						y += inst.font.lineHeight + 5;
					}
					if (Config.DISPLAY_BIOME.get()) {
						g.drawString(inst.font, String.format("Biome: %s", biome), x, y, 0xE0E0E0);
						y += inst.font.lineHeight + 5;
					}
					if (Config.DISPLAY_TIME.get()) {
						var hours = ((time / 1000) + 6) % 24;
						var hours12 = hours % 12;
						var minutes = (int)Math.floor(time / 16.6f) % 60;
						g.drawString(inst.font,
								Config.DISPLAY_TIME_24H.get() ?
										String.format("Time: %02d:%02d", hours, minutes) :
										String.format("Time: %02d:%02d %s", hours12 == 0 ? 12 : hours12, minutes, hours >= 12 ? "pm" : "am")
								, x, y, 0xE0E0E0);
						y += inst.font.lineHeight + 5;
					}
				} catch (Exception ex) {
					LOGGER.error("Exception thrown: {}", ex.getMessage());
				}
			}
		}
		private static void drawOnScreenWithBackground(GuiGraphics guiGraphics, int x, int y, String text, int backgroundColor, int textColor) {
			final var mc = Minecraft.getInstance();
			guiGraphics.fill(x - 1, y - 1, x + mc.font.width(text) + 1, y + mc.font.lineHeight - 1, backgroundColor);
			guiGraphics.drawString(mc.font, text, x, y, textColor);
		}
	}
}
