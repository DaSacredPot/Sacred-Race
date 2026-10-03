package com.sacredmod.network;

import com.sacredmod.car.RaceCarEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class RaceNetworking {
	private RaceNetworking() {
	}

	public static void register() {
		PayloadTypeRegistry.serverboundPlay().register(RaceDriveInput.TYPE, RaceDriveInput.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(RaceDriveInput.TYPE, (payload, context) -> {
			if (context.player().getVehicle() instanceof RaceCarEntity car) {
				car.acceptDriverInput(context.player(), payload);
			}
		});
	}
}
