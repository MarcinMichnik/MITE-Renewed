package miterenewed;

import miterenewed.handlers.PlayerExhaustionHandler;
import miterenewed.handlers.PlayerHealthRegenHandler;
import miterenewed.handlers.PlayerProgressionHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.rule.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.server.MinecraftServer;

public class MITERenewed implements ModInitializer {
	public static final String MOD_ID = "mite-renewed";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello MITERenewed world!");

		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

		PlayerProgressionHandler.init();
		PlayerExhaustionHandler.init();
		PlayerHealthRegenHandler.init();
	}

	private void onServerStarted(MinecraftServer server) {
		// Disable vanilla health regen because the mod will have different rules for this
        server.getOverworld().getGameRules()
			.setValue(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
	}

}