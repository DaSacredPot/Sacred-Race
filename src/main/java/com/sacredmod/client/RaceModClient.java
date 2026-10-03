package com.sacredmod.client;

import com.sacredmod.car.ModEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class RaceModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEntityModelLayers.register();
		EntityRenderers.register(ModEntityTypes.RACE_CAR, RaceCarRenderer::new);
		RaceCarInput.register();
		SpeedHud.register();
	}
}
