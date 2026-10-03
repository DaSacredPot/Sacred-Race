package com.sacredmod.client;

import com.sacredmod.RaceMod;
import com.sacredmod.car.CarPart;
import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.race.ModAttachments;
import com.sacredmod.race.RaceDimension;
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
		RaceStats stats = minecraft.player.getAttachedOrElse(ModAttachments.RACE_STATS, RaceStats.EMPTY);
		boolean inRaceWorld = minecraft.player.level().dimension().equals(RaceDimension.KEY);
		if (stats.racing() || (inRaceWorld && stats.lapTarget() > 0)) {
			String time = String.format("%02d:%02d.%d", stats.elapsedTicks() / 1200,
					(stats.elapsedTicks() / 20) % 60, (stats.elapsedTicks() % 20) / 2);
			int displayedLap = stats.racing() ? Math.min(stats.completedLaps() + 1, stats.lapTarget()) : stats.completedLaps();
			String lap = "LAP " + displayedLap + "/" + stats.lapTarget()
					+ "   TIME " + time;
			String checkpoint = stats.racing()
					? "CHECKPOINT " + (stats.checkpointPassed() ? "CLEARED" : "AHEAD")
					: "RACE FINISHED";
			int panelWidth = Math.max(Minecraft.getInstance().font.width(lap), Minecraft.getInstance().font.width(checkpoint));
			int panelX = (graphics.guiWidth() - panelWidth) / 2;
			graphics.fill(panelX - 8, 4, panelX + panelWidth + 8, 34, 0xAA080E18);
			drawCentered(graphics, 8, lap);
			drawCentered(graphics, 20, checkpoint);
		}
		if (!(minecraft.player.getVehicle() instanceof RaceCarEntity car)) {
			return;
		}
		int x = 8;
		int y = graphics.guiHeight() - 52;
		String carName = car.getBrand().maker() + " " + car.getBrand().model() + "  Lv " + car.getCarLevel();
		String speedAndThrottle = "Speed " + car.speedKmh() + " km/h   Acceleration " + car.getThrottlePercent() + "%";
		String fuel = "Fuel " + fuelBar(car.getFuelPercent()) + " " + car.getFuelPercent() + "%   [Coal + right-click to refuel]";
		String upgrades = "Engine " + car.getPartLevel(CarPart.ENGINE) + "  Wheels " + car.getPartLevel(CarPart.WHEELS)
				+ "  Handling " + car.getPartLevel(CarPart.HANDLING) + "  Chassis " + car.getPartLevel(CarPart.CHASSIS);
		String statsLine = "Bounty " + stats.bounty() + "   Races " + stats.racesCompleted();
		int panelWidth = Math.max(Math.max(Minecraft.getInstance().font.width(carName), Minecraft.getInstance().font.width(speedAndThrottle)),
				Math.max(Minecraft.getInstance().font.width(fuel),
						Math.max(Minecraft.getInstance().font.width(upgrades), inRaceWorld ? 0 : Minecraft.getInstance().font.width(statsLine))));
		graphics.fill(x - 4, y - 4, x + panelWidth + 4, y + 51, 0xAA080E18);
		draw(graphics, x, y, carName);
		draw(graphics, x, y + 10, speedAndThrottle);
		draw(graphics, x, y + 20, fuel);
		draw(graphics, x, y + 30, upgrades);
		if (!inRaceWorld) {
			draw(graphics, x, y + 40, statsLine);
		}
	}

	private static void drawCentered(GuiGraphicsExtractor graphics, int y, String text) {
		int x = (graphics.guiWidth() - Minecraft.getInstance().font.width(text)) / 2;
		draw(graphics, x, y, text);
	}

	private static String fuelBar(int fuel) {
		int filled = Math.max(0, Math.min(10, fuel / 10));
		return "[" + "#".repeat(filled) + "-".repeat(10 - filled) + "]";
	}

	private static void draw(GuiGraphicsExtractor graphics, int x, int y, String text) {
		graphics.text(Minecraft.getInstance().font, Component.literal(text), x, y, 0xFFFFFF, true);
	}
}
