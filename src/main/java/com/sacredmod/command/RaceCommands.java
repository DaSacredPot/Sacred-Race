package com.sacredmod.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.sacredmod.car.CarBrand;
import com.sacredmod.car.CarPart;
import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.race.RaceDimension;
import com.sacredmod.race.ModAttachments;
import com.sacredmod.race.RaceManager;
import com.sacredmod.race.RaceStats;
import com.sacredmod.util.RaceMessages;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public final class RaceCommands {
	private RaceCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("race")
				.executes(context -> status(context.getSource().getPlayerOrException()))
				.then(Commands.literal("status").executes(context -> status(context.getSource().getPlayerOrException())))
				.then(Commands.literal("start").executes(context -> {
					ServerPlayer player = context.getSource().getPlayerOrException();
					if (player.level() instanceof net.minecraft.server.level.ServerLevel level && RaceDimension.isRaceDimension(level)) {
						RaceManager.startArenaRace(player);
					} else {
						RaceManager.startRace(player, player.blockPosition());
					}
					return 1;
				}))
				.then(Commands.literal("restart").executes(context -> {
					RaceManager.restartArenaRace(context.getSource().getPlayerOrException());
					return 1;
				}))
				.then(Commands.literal("return").executes(context -> {
					RaceDimension.returnToOverworld(context.getSource().getPlayerOrException());
					return 1;
				}))
				.then(Commands.literal("world").executes(context -> {
					RaceDimension.joinRaceWorld(context.getSource().getPlayerOrException());
					return 1;
				}))
				.then(Commands.literal("paybounty").executes(context -> {
					int result = RaceManager.payBounty(context.getSource().getPlayerOrException());
					return result < 0 ? 0 : 1;
				}))
				.then(Commands.literal("cars").executes(context -> giveCars(context.getSource().getPlayerOrException())))
				.then(Commands.literal("upgrade")
						.executes(context -> upgrade(context.getSource().getPlayerOrException(), CarPart.ENGINE))
						.then(Commands.literal("engine").executes(context -> upgrade(context.getSource().getPlayerOrException(), CarPart.ENGINE)))
						.then(Commands.literal("wheels").executes(context -> upgrade(context.getSource().getPlayerOrException(), CarPart.WHEELS)))
						.then(Commands.literal("handling").executes(context -> upgrade(context.getSource().getPlayerOrException(), CarPart.HANDLING)))
						.then(Commands.literal("chassis").executes(context -> upgrade(context.getSource().getPlayerOrException(), CarPart.CHASSIS))))
				.then(Commands.literal("speedlimit")
						.then(Commands.literal("get").executes(context -> {
							var player = context.getSource().getPlayerOrException();
							int limit = player.level().getAttachedOrCreate(ModAttachments.DEFAULT_SPEED_LIMIT);
							context.getSource().sendSuccess(() -> Component.literal("Default radar limit is " + limit + " km/h."), false);
							return limit;
						}))
						.then(Commands.literal("set")
								.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
								.then(Commands.argument("kmh", IntegerArgumentType.integer(10, 200))
										.executes(context -> {
											int kmh = IntegerArgumentType.getInteger(context, "kmh");
											context.getSource().getLevel().setAttached(ModAttachments.DEFAULT_SPEED_LIMIT, kmh);
											context.getSource().sendSuccess(() -> Component.literal("Default radar limit is now " + kmh + " km/h."), true);
											return kmh;
										}))))
		));
	}

	private static int status(ServerPlayer player) {
		RaceStats stats = player.getAttachedOrCreate(ModAttachments.RACE_STATS);
		int limit = player.level().getAttachedOrCreate(ModAttachments.DEFAULT_SPEED_LIMIT);
		String carInfo = player.getVehicle() instanceof RaceCarEntity car
				? car.getBrand().maker() + " " + car.getBrand().model() + " lv" + car.getCarLevel()
						+ " [engine " + car.getPartLevel(CarPart.ENGINE) + ", wheels " + car.getPartLevel(CarPart.WHEELS)
						+ ", handling " + car.getPartLevel(CarPart.HANDLING) + ", chassis " + car.getPartLevel(CarPart.CHASSIS)
						+ "] @ " + car.speedKmh() + " km/h"
				: "not driving";
		RaceMessages.send(player, Component.literal("Races " + stats.racesCompleted() + " | Bounty " + stats.bounty()
				+ " | Default limit " + limit + " | " + carInfo), false);
		if (stats.racing()) {
			RaceMessages.send(player, Component.literal("Live race: " + stats.checkpointsHit() + "/" + stats.requiredCheckpoints() + " checkpoints."), false);
		}
		return 1;
	}

	private static int upgrade(ServerPlayer player, CarPart part) {
		if (!(player.getVehicle() instanceof RaceCarEntity car)) {
			RaceMessages.send(player, Component.literal("Sit in your car and hold diamonds, then run /race upgrade <engine|wheels|handling|chassis>."), false);
			return 0;
		}
		if (car.tryUpgrade(player, player.getItemInHand(InteractionHand.MAIN_HAND), part)) {
			return 1;
		}
		RaceMessages.send(player, Component.literal("Hold diamonds in your main hand to upgrade the " + part.displayName() + "."), false);
		return 0;
	}

	private static int giveCars(ServerPlayer player) {
		for (CarBrand brand : CarBrand.values()) {
			ItemStack stack = new ItemStack(brand.dropItem());
			if (!player.addItem(stack)) {
				player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack));
			}
		}
		RaceMessages.send(player, Component.literal("All " + CarBrand.values().length + " free cars are in your inventory or at your feet."), false);
		return CarBrand.values().length;
	}
}
