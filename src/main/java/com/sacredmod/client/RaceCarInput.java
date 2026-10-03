package com.sacredmod.client;

import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.network.RaceDriveInput;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public final class RaceCarInput {
	private RaceCarInput() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(RaceCarInput::sendInput);
	}

	private static void sendInput(Minecraft minecraft) {
		if (minecraft.player == null || minecraft.level == null
				|| !(minecraft.player.getVehicle() instanceof RaceCarEntity)
				|| !ClientPlayNetworking.canSend(RaceDriveInput.TYPE)) {
			return;
		}
		ClientPlayNetworking.send(new RaceDriveInput(
				minecraft.options.keyUp.isDown(),
				minecraft.options.keyDown.isDown(),
				minecraft.options.keyLeft.isDown(),
				minecraft.options.keyRight.isDown()
		));
	}
}
