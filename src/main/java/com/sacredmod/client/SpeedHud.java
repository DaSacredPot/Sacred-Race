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
		if (minecraft.options.getCameraType().isFirstPerson()) {
			renderCockpit(graphics, car);
		} else {
			renderRaceTelemetry(graphics, car, stats, inRaceWorld);
		}
	}

	private static void renderCockpit(GuiGraphicsExtractor graphics, RaceCarEntity car) {
		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		int centerX = width / 2;
		int panelTop = height - 93;
		graphics.fill(centerX - 155, panelTop, centerX + 155, height, 0xE9111820);
		graphics.fill(centerX - 155, panelTop, centerX + 155, panelTop + 3, 0xFF42B8C6);
		draw(graphics, centerX - 144, panelTop + 11, car.getBrand().maker().toUpperCase(), 0xFFB9D4DB);

		int wheelX = centerX - 78;
		int wheelY = height - 43;
		drawRing(graphics, wheelX, wheelY, 25, 0xFF27333D, 0xFFD7E2E7);
		drawRing(graphics, wheelX, wheelY, 3, 0xFF191F26, 0xFF42B8C6);
		float steering = car.getSteeringAngle();
		drawLine(graphics, wheelX, wheelY, wheelX + (int) (Math.sin(steering) * 21),
				wheelY - (int) (Math.cos(steering) * 21), 3, 0xFFE0E7E9);
		drawLine(graphics, wheelX, wheelY, wheelX + (int) (Math.sin(steering + 2.35f) * 18),
				wheelY - (int) (Math.cos(steering + 2.35f) * 18), 3, 0xFFB8C4CA);
		drawLine(graphics, wheelX, wheelY, wheelX + (int) (Math.sin(steering - 2.35f) * 18),
				wheelY - (int) (Math.cos(steering - 2.35f) * 18), 3, 0xFFB8C4CA);

		int gaugeX = centerX + 76;
		int gaugeY = height - 48;
		drawRing(graphics, gaugeX, gaugeY, 33, 0xFF182129, 0xFF768994);
		drawRing(graphics, gaugeX, gaugeY, 27, 0xFF0B1016, 0xFF30434E);
		for (int tick = 0; tick <= 12; tick++) {
			double angle = Math.toRadians(-225 + tick * 22.5);
			int outerX = gaugeX + (int) (Math.cos(angle) * 26);
			int outerY = gaugeY + (int) (Math.sin(angle) * 26);
			int innerX = gaugeX + (int) (Math.cos(angle) * (tick % 3 == 0 ? 20 : 23));
			int innerY = gaugeY + (int) (Math.sin(angle) * (tick % 3 == 0 ? 20 : 23));
			drawLine(graphics, innerX, innerY, outerX, outerY, tick % 3 == 0 ? 2 : 1,
					tick >= 10 ? 0xFFFF685D : 0xFFE9F0F2);
		}
		int speed = Math.min(240, car.speedKmh());
		double needleAngle = Math.toRadians(-225 + speed / 240.0 * 270);
		drawLine(graphics, gaugeX, gaugeY, gaugeX + (int) (Math.cos(needleAngle) * 22),
				gaugeY + (int) (Math.sin(needleAngle) * 22), 2, 0xFFFF584F);
		drawRing(graphics, gaugeX, gaugeY, 3, 0xFFBE2F37, 0xFFFFCF85);
		draw(graphics, gaugeX - Minecraft.getInstance().font.width(Integer.toString(car.speedKmh())) / 2,
				height - 55, Integer.toString(car.speedKmh()), 0xFFFFFFFF);
		draw(graphics, gaugeX - Minecraft.getInstance().font.width("KM/H") / 2,
				height - 43, "KM/H", 0xFF9DB0BA);

		draw(graphics, centerX - 145, height - 12, "A  \u2190 LEFT", 0xFFE0E7E9);
		draw(graphics, centerX + 79, height - 12, "RIGHT \u2192  D", 0xFFE0E7E9);

		int throttle = car.getThrottlePercent();
		draw(graphics, centerX - 12, panelTop + 24, "THROTTLE " + (throttle > 0 ? "+" : "") + throttle + "%",
				0xFFB9C9CE);
		drawThrottleBar(graphics, centerX - 12, panelTop + 36, 88, throttle);
		draw(graphics, centerX - 12, panelTop + 51, "FUEL " + car.getFuelPercent() + "%", 0xFFB9C9CE);
		drawBar(graphics, centerX - 12, panelTop + 63, 88, car.getFuelPercent(),
				car.getFuelPercent() < 20 ? 0xFFFF6257 : 0xFFFFC45B);
	}

	private static void renderRaceTelemetry(GuiGraphicsExtractor graphics, RaceCarEntity car, RaceStats stats, boolean inRaceWorld) {
		int x = 8;
		int y = graphics.guiHeight() - 52;
		String speed = "Speed " + car.speedKmh() + " km/h  Acceleration " + car.getThrottlePercent() + "%";
		String fuel = "Fuel " + fuelBar(car.getFuelPercent()) + " " + car.getFuelPercent() + "%";
		String upgrades = "Engine " + car.getPartLevel(CarPart.ENGINE) + "  Wheels " + car.getPartLevel(CarPart.WHEELS)
				+ "  Handling " + car.getPartLevel(CarPart.HANDLING) + "  Chassis " + car.getPartLevel(CarPart.CHASSIS);
		int panelWidth = Math.max(Minecraft.getInstance().font.width(speed),
				Math.max(Minecraft.getInstance().font.width(fuel), Minecraft.getInstance().font.width(upgrades)));
		graphics.fill(x - 4, y - 4, x + panelWidth + 4, y + (inRaceWorld ? 40 : 50), 0xAA080E18);
		draw(graphics, x, y, car.getBrand().maker() + " " + car.getBrand().model() + "  Lv " + car.getCarLevel());
		draw(graphics, x, y + 10, speed);
		draw(graphics, x, y + 20, fuel);
		draw(graphics, x, y + 30, upgrades);
		if (!inRaceWorld) {
			draw(graphics, x, y + 40, "Bounty " + stats.bounty() + "   Races " + stats.racesCompleted());
		}
	}

	private static void drawBar(GuiGraphicsExtractor graphics, int x, int y, int width, int percent, int color) {
		graphics.fill(x, y, x + width, y + 6, 0xFF27333B);
		graphics.fill(x, y, x + width * Math.max(0, Math.min(100, percent)) / 100, y + 6, color);
	}

	private static void drawThrottleBar(GuiGraphicsExtractor graphics, int x, int y, int width, int throttle) {
		int middle = x + width / 2;
		graphics.fill(x, y, x + width, y + 6, 0xFF27333B);
		graphics.fill(middle - 1, y - 2, middle + 1, y + 8, 0xFF94A8B1);
		int length = width / 2 * Math.min(100, Math.abs(throttle)) / 100;
		if (throttle > 0) {
			graphics.fill(middle, y, middle + length, y + 6, 0xFF49CDD4);
		} else if (throttle < 0) {
			graphics.fill(middle - length, y, middle, y + 6, 0xFFFF9C5B);
		}
	}

	private static void drawRing(GuiGraphicsExtractor graphics, int centerX, int centerY, int radius, int fill, int edge) {
		for (int y = -radius; y <= radius; y++) {
			int span = (int) Math.sqrt(radius * radius - y * y);
			graphics.fill(centerX - span, centerY + y, centerX + span + 1, centerY + y + 1, fill);
		}
		for (int degree = 0; degree < 360; degree += 4) {
			double angle = Math.toRadians(degree);
			int x = centerX + (int) (Math.cos(angle) * radius);
			int y = centerY + (int) (Math.sin(angle) * radius);
			graphics.fill(x - 1, y - 1, x + 2, y + 2, edge);
		}
	}

	private static void drawLine(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int thickness, int color) {
		int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
		for (int step = 0; step <= steps; step++) {
			float amount = steps == 0 ? 0 : step / (float) steps;
			int x = Math.round(x1 + (x2 - x1) * amount);
			int y = Math.round(y1 + (y2 - y1) * amount);
			graphics.fill(x - thickness / 2, y - thickness / 2, x + thickness / 2 + 1, y + thickness / 2 + 1, color);
		}
	}

	private static void drawCentered(GuiGraphicsExtractor graphics, int y, String text) {
		int x = (graphics.guiWidth() - Minecraft.getInstance().font.width(text)) / 2;
		draw(graphics, x, y, text, 0xFFFFFFFF);
	}

	private static String fuelBar(int fuel) {
		int filled = Math.max(0, Math.min(10, fuel / 10));
		return "[" + "#".repeat(filled) + "-".repeat(10 - filled) + "]";
	}

	private static void draw(GuiGraphicsExtractor graphics, int x, int y, String text) {
		draw(graphics, x, y, text, 0xFFFFFFFF);
	}

	private static void drawCentered(GuiGraphicsExtractor graphics, int y, String text, int color) {
		int x = (graphics.guiWidth() - Minecraft.getInstance().font.width(text)) / 2;
		draw(graphics, x, y, text, color);
	}

	private static void draw(GuiGraphicsExtractor graphics, int x, int y, String text, int color) {
		graphics.text(Minecraft.getInstance().font, Component.literal(text), x, y, color, true);
	}
}
