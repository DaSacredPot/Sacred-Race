package com.sacredmod.client;

import com.sacredmod.RaceMod;
import com.sacredmod.car.CarPart;
import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.race.ModAttachments;
import com.sacredmod.race.RaceStats;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class SpeedHud {
	private SpeedHud() {
	}

	public static void register() {
		HudElementRegistry.addLast(RaceMod.id("speed_hud"), (graphics, tickCounter) -> render(graphics));
	}

	private static void render(GuiGraphicsExtractor graphics) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.gui.hud.isHidden()) {
			return;
		}
		if (!(minecraft.player.getVehicle() instanceof RaceCarEntity car)) {
			return;
		}
		RaceStats stats = minecraft.player.getAttachedOrElse(ModAttachments.RACE_STATS, RaceStats.EMPTY);
		int x = 8;
		int y = graphics.guiHeight() - 52;
		draw(graphics, x, y, car.getBrand().maker() + " " + car.getBrand().model() + "  Lv " + car.getCarLevel());
		draw(graphics, x, y + 10, "E " + car.getPartLevel(CarPart.ENGINE) + "  W " + car.getPartLevel(CarPart.WHEELS)
				+ "  H " + car.getPartLevel(CarPart.HANDLING) + "  C " + car.getPartLevel(CarPart.CHASSIS));
		draw(graphics, x, y + 20, "Speed  " + car.speedKmh() + " km/h");
		draw(graphics, x, y + 30, "Bounty " + stats.bounty() + "   Races " + stats.racesCompleted());
		if (stats.racing()) {
			draw(graphics, x, y + 40, "Race  " + stats.checkpointsHit() + "/" + stats.requiredCheckpoints());
		}
	}

	private static void draw(GuiGraphicsExtractor graphics, int x, int y, String text) {
		graphics.text(Minecraft.getInstance().font, Component.literal(text), x, y, 0xFFFFFF, true);
	}
}
