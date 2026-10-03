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
				|| !(minecraft.player.getVehicle() instanceof RaceCarEntity)) {
			return;
		}
		RaceCarEntity car = (RaceCarEntity) minecraft.player.getVehicle();
		boolean forward = minecraft.options.keyUp.isDown() && car.getFuelPercent() > 0;
		boolean backward = minecraft.options.keyDown.isDown() && car.getFuelPercent() > 0;
		boolean left = minecraft.options.keyLeft.isDown();
		boolean right = minecraft.options.keyRight.isDown();
		car.setInput(right, left, forward, backward);
		ClientPlayNetworking.send(new RaceDriveInput(
				forward,
				backward,
				left,
				right
		));
	}
}
