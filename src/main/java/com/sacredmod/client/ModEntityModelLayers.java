package com.sacredmod.client;

import com.sacredmod.RaceMod;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class ModEntityModelLayers {
	public static final ModelLayerLocation RACE_CAR = new ModelLayerLocation(RaceMod.id("race_car"), "main");

	private ModEntityModelLayers() {
	}

	public static void register() {
		ModelLayerRegistry.registerModelLayer(RACE_CAR, RaceCarModel::createBodyLayer);
	}
}
