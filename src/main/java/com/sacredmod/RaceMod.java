package com.sacredmod;

import com.sacredmod.block.ModBlockEntities;
import com.sacredmod.block.ModBlocks;
import com.sacredmod.car.ModEntityTypes;
import com.sacredmod.command.RaceCommands;
import com.sacredmod.item.ModItems;
import com.sacredmod.race.ModAttachments;
import com.sacredmod.race.RaceDimension;
import com.sacredmod.race.RaceManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaceMod implements ModInitializer {
	public static final String MOD_ID = "racemod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModAttachments.initialize();
		ModEntityTypes.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModItems.initialize();
		RaceCommands.register();
		RaceManager.register();
		RaceDimension.register();
		LOGGER.info("Race Mod ready — race dimension, multiplayer laps, and car upgrades loaded.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
