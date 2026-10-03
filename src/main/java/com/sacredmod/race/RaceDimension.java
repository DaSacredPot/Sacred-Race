package com.sacredmod.race;

import com.sacredmod.RaceMod;
import com.sacredmod.car.CarBrand;
import com.sacredmod.car.ModEntityTypes;
import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.util.RaceMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Set;

public final class RaceDimension {
	public static final ResourceKey<Level> KEY = ResourceKey.create(Registries.DIMENSION, RaceMod.id("race_world"));
	public static final int GROUND_Y = 73;
	public static final int LAP_COUNT = 3;

	private RaceDimension() {
	}

	public static boolean isRaceDimension(ServerLevel level) {
		return level.dimension().equals(KEY);
	}

	public static void returnToOverworld(ServerPlayer player) {
		player.stopRiding();
		ServerLevel overworld = player.level().getServer().getLevel(Level.OVERWORLD);
		if (overworld == null) {
			RaceMessages.send(player, Component.literal("The server's Overworld is unavailable."), false);
			RaceMod.LOGGER.error("Cannot return player {} to the Overworld because it is not loaded.", player.getScoreboardName());
			return;
		}
		var spawn = overworld.getServer().getRespawnData();
		BlockPos position = spawn.dimension().equals(Level.OVERWORLD) ? spawn.pos() : BlockPos.ZERO;
		int spawnY = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, position.getX(), position.getZ());
		if (!player.teleportTo(overworld, position.getX() + 0.5, spawnY, position.getZ() + 0.5,
				Set.of(), player.getYRot(), player.getXRot(), true)) {
			RaceMessages.send(player, Component.literal("Could not teleport you to the Overworld."), false);
		} else {
			player.setAttached(ModAttachments.RACE_STATS,
					player.getAttachedOrCreate(ModAttachments.RACE_STATS).cancelRace());
		}
	}

	public static void joinRaceWorld(ServerPlayer player) {
		joinRaceWorld(player, player.level().getServer());
	}

	private static void joinRaceWorld(ServerPlayer player, MinecraftServer server) {
		if (player.isRemoved()) {
			return;
		}
		player.stopRiding();
		ServerLevel raceWorld = server.getLevel(KEY);
		if (raceWorld == null) {
			RaceMessages.send(player, Component.literal("The race dimension is unavailable. Check the mod is installed on the server."), false);
			RaceMod.LOGGER.error("Race dimension {} is not loaded; cannot place joining player {}.", KEY.identifier(), player.getScoreboardName());
			return;
		}
		buildArena(raceWorld);
		int slot = findOpenGridSlot(raceWorld, player);
		if (slot < 0) {
			RaceMessages.send(player, Component.literal("The race grid is full. Try joining again when a spot opens."), false);
			return;
		}
		GridPosition position = gridPosition(slot);
		double x = position.x();
		double z = position.z();
		if (!player.teleportTo(raceWorld, x, GROUND_Y, z, Set.of(), -90.0f, 0.0f, true)) {
			RaceMessages.send(player, Component.literal("Could not teleport you to the race dimension."), false);
			RaceMod.LOGGER.error("Failed to teleport player {} to race dimension.", player.getScoreboardName());
			return;
		}
		player.setAttached(ModAttachments.RACE_STATS,
				player.getAttachedOrCreate(ModAttachments.RACE_STATS).cancelRace());

		RaceCarEntity car = findParkedCar(raceWorld, x, z);
		if (car == null) {
			car = new RaceCarEntity(ModEntityTypes.RACE_CAR, raceWorld);
			car.setBrand(CarBrand.VELDORA);
			car.setPos(x, GROUND_Y, z);
			car.setYRot(-90.0f);
			if (!raceWorld.addFreshEntity(car)) {
				RaceMessages.send(player, Component.literal("Could not spawn your race car."), false);
				RaceMod.LOGGER.error("Failed to spawn a starter race car for player {}.", player.getScoreboardName());
				return;
			}
		} else {
			car.setPos(x, GROUND_Y, z);
			car.setYRot(-90.0f);
		}
		if (!player.startRiding(car)) {
			RaceMessages.send(player, Component.literal("Your car is ready nearby; right-click it to drive."), false);
		}
		RaceMessages.send(player, Component.literal("Welcome to Sacred Speedway! Use /race start when you're ready, "
				+ "/race restart to restart an active race, or /race return to visit the Overworld."), false);
	}

	private static int findOpenGridSlot(ServerLevel level, ServerPlayer joiningPlayer) {
		for (int slot = 0; slot < 61; slot++) {
			GridPosition position = gridPosition(slot);
			double x = position.x();
			double z = position.z();
			boolean occupied = level.players().stream().anyMatch(player -> player != joiningPlayer
					&& player.distanceToSqr(x, GROUND_Y, z) < 9.0);
			if (!occupied) {
				return slot;
			}
		}
		return -1;
	}

	private static GridPosition gridPosition(int slot) {
		if (slot < 12) {
			return new GridPosition(-10.0 - slot / 3 * 5.0, -55.0 + slot % 3 * 3.0);
		}
		int infieldSlot = slot - 12;
		return new GridPosition(-18.0 + infieldSlot % 7 * 6.0, -18.0 + infieldSlot / 7 * 6.0);
	}

	private static RaceCarEntity findParkedCar(ServerLevel level, double x, double z) {
		var area = new net.minecraft.world.phys.AABB(x - 2.0, GROUND_Y - 1.0, z - 2.0, x + 2.0, GROUND_Y + 3.0, z + 2.0);
		return level.getEntitiesOfClass(RaceCarEntity.class, area, car -> !car.isVehicle()).stream().findFirst().orElse(null);
	}

	private record GridPosition(double x, double z) {
	}

	private static void buildArena(ServerLevel level) {
		BlockPos marker = new BlockPos(0, GROUND_Y - 2, 0);
		if (level.getBlockState(marker).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
			return;
		}
		RaceArena.build(level, marker);
	}
}
