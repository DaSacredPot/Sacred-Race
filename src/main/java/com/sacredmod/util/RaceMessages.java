package com.sacredmod.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class RaceMessages {
	private RaceMessages() {
	}

	public static void send(Player player, Component message, boolean overlay) {
		if (overlay) {
			player.sendOverlayMessage(message);
		} else {
			player.sendSystemMessage(message);
		}
	}
}
