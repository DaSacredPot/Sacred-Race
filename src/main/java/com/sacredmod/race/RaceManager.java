package com.sacredmod.race;

import com.sacredmod.block.ModBlocks;
import com.sacredmod.car.CarBrand;
import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.util.RaceMessages;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class RaceManager {
	private RaceManager() {
	}

	public static void register() {
		ServerTickEvents.END_LEVEL_TICK.register(RaceManager::tickLevel);
	}

	public static void startRace(net.minecraft.world.entity.player.Player player, BlockPos start) {
		if (!(player instanceof ServerPlayer serverPlayer) || !(player.level() instanceof ServerLevel level)) {
			return;
		}
		if (!(player.getVehicle() instanceof RaceCarEntity)) {
			RaceMessages.send(player, Component.literal("Hop in a car before starting a race."), true);
			return;
		}
		int checkpoints = countNearby(level, start, ModBlocks.RACE_CHECKPOINT, 96);
		int finishes = countNearby(level, start, ModBlocks.RACE_FINISH, 96);
		if (checkpoints <= 0 || finishes <= 0) {
			RaceMessages.send(player, Component.literal("Build checkpoints and a finish line within 96 blocks of the start."), false);
			return;
		}
		RaceStats stats = player.getAttachedOrCreate(ModAttachments.RACE_STATS).startRace(checkpoints);
		player.setAttached(ModAttachments.RACE_STATS, stats);
		RaceMessages.send(player, Component.literal("Race started! Hit " + checkpoints + " checkpoint(s), then the finish."), false);
		level.playSound(null, start, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0f, 1.2f);
	}

	public static void startArenaRace(ServerPlayer player) {
		beginArenaRace(player, false);
	}

	public static void restartArenaRace(ServerPlayer player) {
		beginArenaRace(player, true);
	}

	private static void beginArenaRace(ServerPlayer player, boolean restarting) {
		if (!(player.level() instanceof ServerLevel level) || !RaceDimension.isRaceDimension(level)) {
			RaceMessages.send(player, Component.literal("Race laps are only available in the race dimension."), true);
			return;
		}
		RaceStats current = player.getAttachedOrCreate(ModAttachments.RACE_STATS);
		if (restarting && !current.racing() && current.lapTarget() != RaceDimension.LAP_COUNT) {
			RaceMessages.send(player, Component.literal("There is no race to restart. Use /race start."), true);
			return;
		}
		if (!restarting && current.racing()) {
			RaceMessages.send(player, Component.literal("A race is already in progress. Use /race restart to start it over."), true);
			return;
		}
		if (!(player.getVehicle() instanceof RaceCarEntity)) {
			RaceMessages.send(player, Component.literal("Hop in a car before starting a race."), true);
			return;
		}
		RaceStats stats = current.startRace(1, RaceDimension.LAP_COUNT);
		player.setAttached(ModAttachments.RACE_STATS, stats);
		String message = restarting ? "Race restarted! Complete " : "Race started! Complete ";
		RaceMessages.send(player, Component.literal(message + RaceDimension.LAP_COUNT + " laps."), false);
		level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0f, 1.2f);
	}

	private static void tickLevel(ServerLevel level) {
		for (ServerPlayer player : level.players()) {
			RaceStats stats = player.getAttachedOrCreate(ModAttachments.RACE_STATS);
			if (!stats.racing() || !(player.getVehicle() instanceof RaceCarEntity car)) {
				continue;
			}
			stats = stats.tickRace();
			player.setAttached(ModAttachments.RACE_STATS, stats);
			if (RaceDimension.isRaceDimension(level)) {
				tickArenaRace(player, car, stats);
				continue;
			}
			BlockPos pos = player.blockPosition();
			BlockState state = level.getBlockState(pos);
			if (state.is(ModBlocks.RACE_CHECKPOINT) && stats.checkpointsHit() < stats.requiredCheckpoints()) {
				RaceStats next = stats.hitCheckpoint();
				player.setAttached(ModAttachments.RACE_STATS, next);
				RaceMessages.send(player, Component.literal("Checkpoint " + next.checkpointsHit() + "/" + next.requiredCheckpoints()), true);
				level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8f, 1.3f);
			}
			if (state.is(ModBlocks.RACE_FINISH) && stats.checkpointsHit() >= stats.requiredCheckpoints()) {
				finishRace(player, car, stats);
			}
		}
	}

	private static void tickArenaRace(ServerPlayer player, RaceCarEntity car, RaceStats stats) {
		double x = car.getX();
		double z = car.getZ();
		boolean atCheckpoint = Math.abs(x) <= 2.0 && z >= 49.0 && z <= 61.0;
		boolean atFinish = Math.abs(x) <= 2.0 && z >= -61.0 && z <= -49.0;
		if (atCheckpoint && !stats.checkpointPassed()) {
			RaceStats next = stats.hitCheckpoint();
			player.setAttached(ModAttachments.RACE_STATS, next);
			RaceMessages.send(player, Component.literal("Checkpoint reached! Cross the start line to complete lap "
					+ Math.min(next.completedLaps() + 1, next.lapTarget()) + "."), true);
			player.level().playSound(null, car.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
					SoundSource.PLAYERS, 0.8f, 1.3f);
		} else if (atFinish && stats.checkpointPassed()) {
			RaceStats next = stats.completeLap();
			if (next.completedLaps() >= next.lapTarget()) {
				finishRace(player, car, next);
			} else {
				player.setAttached(ModAttachments.RACE_STATS, next);
				RaceMessages.send(player, Component.literal("Lap " + next.completedLaps() + "/" + next.lapTarget()
						+ " complete!"), true);
				player.level().playSound(null, car.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
						SoundSource.PLAYERS, 0.8f, 1.5f);
			}
		}
	}

	private static void finishRace(ServerPlayer player, RaceCarEntity car, RaceStats stats) {
		int racesAfter = stats.racesCompleted() + 1;
		int diamonds = 3 + car.getCarLevel() + Math.min(5, racesAfter / 2);
		ItemStack reward = new ItemStack(Items.DIAMOND, diamonds);
		if (!player.addItem(reward)) {
			player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), reward));
		}

		boolean bonusCar = stats.bounty() >= 4 && player.getRandom().nextInt(3) == 0;
		if (bonusCar) {
			CarBrand prize = CarBrand.byId(player.getRandom().nextInt(CarBrand.values().length));
			ItemStack carStack = new ItemStack(prize.dropItem());
			if (!player.addItem(carStack)) {
				player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), carStack));
			}
			RaceMessages.send(player, Component.literal("Bounty bonus! You won a " + prize.maker() + " " + prize.model() + "."), false);
		}

		int cleared = Math.min(stats.bounty(), 2);
		RaceStats finished = stats.finishRace().withBounty(stats.bounty() - cleared);
		player.setAttached(ModAttachments.RACE_STATS, finished);
		RaceMessages.send(player, Component.literal("Finished! +" + diamonds + " diamonds. Races: " + finished.racesCompleted()
				+ ". Spend diamonds on your car to upgrade."), false);
		player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
	}

	private static int countNearby(ServerLevel level, BlockPos origin, net.minecraft.world.level.block.Block block, int range) {
		int count = 0;
		AABB box = new AABB(origin).inflate(range);
		BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
		BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
		for (BlockPos check : BlockPos.betweenClosed(min, max)) {
			if (level.getBlockState(check).is(block)) {
				count++;
			}
		}
		return count;
	}

	public static int payBounty(ServerPlayer player) {
		RaceStats stats = player.getAttachedOrCreate(ModAttachments.RACE_STATS);
		if (stats.bounty() <= 0) {
			return 0;
		}
		int cost = stats.bounty() * 2;
		int diamonds = countDiamonds(player);
		if (diamonds < cost && !player.hasInfiniteMaterials()) {
			RaceMessages.send(player, Component.literal("Need " + cost + " diamonds to clear a bounty of " + stats.bounty() + "."), false);
			return -1;
		}
		if (!player.hasInfiniteMaterials()) {
			removeDiamonds(player, cost);
		}
		player.setAttached(ModAttachments.RACE_STATS, stats.withBounty(0));
		RaceMessages.send(player, Component.literal("Bounty cleared for " + cost + " diamonds."), false);
		return cost;
	}

	private static int countDiamonds(ServerPlayer player) {
		int total = 0;
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(Items.DIAMOND)) {
				total += stack.getCount();
			}
		}
		return total;
	}

	private static void removeDiamonds(ServerPlayer player, int amount) {
		int remaining = amount;
		for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (!stack.is(Items.DIAMOND)) {
				continue;
			}
			int take = Math.min(remaining, stack.getCount());
			stack.shrink(take);
			remaining -= take;
		}
	}
}
