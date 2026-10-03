package com.sacredmod.race;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

final class RaceArena {
	private static final int OUTER_X = 80;
	private static final int OUTER_Z = 60;
	private static final int INNER_X = 68;
	private static final int INNER_Z = 48;
	private static final BlockState TRACK = Blocks.BLUE_ICE.defaultBlockState();
	private static final BlockState[] CROWD_COLORS = {
			Blocks.WOOL.red().defaultBlockState(),
			Blocks.WOOL.orange().defaultBlockState(),
			Blocks.WOOL.yellow().defaultBlockState(),
			Blocks.WOOL.lime().defaultBlockState(),
			Blocks.WOOL.blue().defaultBlockState(),
			Blocks.WOOL.magenta().defaultBlockState()
	};

	private RaceArena() {
	}

	static void build(ServerLevel level, BlockPos marker) {
		boolean existingArena = level.getBlockState(marker).is(Blocks.OBSIDIAN);
		int roadY = RaceDimension.GROUND_Y - 1;
		for (int x = -OUTER_X; x <= OUTER_X; x++) {
			for (int z = -OUTER_Z; z <= OUTER_Z; z++) {
				double outer = square((double) x / OUTER_X) + square((double) z / OUTER_Z);
				double inner = square((double) x / INNER_X) + square((double) z / INNER_Z);
				if (outer <= 1.0 && inner >= 1.0) {
					level.setBlock(new BlockPos(x, roadY, z), TRACK, Block.UPDATE_CLIENTS);
				}
			}
		}

		buildCornerFences(level, roadY);
		buildGate(level, 0, -55, true);
		buildGate(level, 0, 55, false);
		if (!existingArena) {
			buildGrandstands(level);
		}
		buildLights(level);
		level.setBlock(marker, Blocks.BLUE_ICE.defaultBlockState(), Block.UPDATE_CLIENTS);
	}

	private static void buildCornerFences(ServerLevel level, int roadY) {
		buildFenceArc(level, roadY, 20, 160);
		buildFenceArc(level, roadY, 200, 340);
	}

	private static void buildFenceArc(ServerLevel level, int roadY, int startDegrees, int endDegrees) {
		int previousX = Integer.MIN_VALUE;
		int previousZ = Integer.MIN_VALUE;
		for (int degrees = startDegrees; degrees <= endDegrees; degrees++) {
			double angle = Math.toRadians(degrees);
			int x = (int) Math.round((OUTER_X + 2) * Math.cos(angle));
			int z = (int) Math.round((OUTER_Z + 2) * Math.sin(angle));
			if (previousX != Integer.MIN_VALUE) {
				int steps = Math.max(Math.abs(x - previousX), Math.abs(z - previousZ));
				for (int step = 1; step <= steps; step++) {
					int fenceX = previousX + (x - previousX) * step / steps;
					int fenceZ = previousZ + (z - previousZ) * step / steps;
					placeFence(level, roadY, fenceX, fenceZ);
				}
			} else {
				placeFence(level, roadY, x, z);
			}
			previousX = x;
			previousZ = z;
		}
	}

	private static void placeFence(ServerLevel level, int roadY, int x, int z) {
		BlockState fence = Blocks.OAK_FENCE.defaultBlockState();
		level.setBlock(new BlockPos(x, roadY + 1, z), fence, Block.UPDATE_ALL);
		level.setBlock(new BlockPos(x, roadY + 2, z), fence, Block.UPDATE_ALL);
	}

	private static void buildGate(ServerLevel level, int centerX, int centerZ, boolean start) {
		int y = RaceDimension.GROUND_Y - 1;
		for (int offset = -6; offset <= 6; offset++) {
			BlockState stripe;
			if (start) {
				stripe = Math.floorMod(offset, 2) == 0
						? Blocks.CONCRETE.white().defaultBlockState()
						: Blocks.CONCRETE.red().defaultBlockState();
			} else {
				stripe = Math.floorMod(offset, 2) == 0
						? Blocks.CONCRETE.yellow().defaultBlockState()
						: Blocks.CONCRETE.blue().defaultBlockState();
			}
			level.setBlock(new BlockPos(centerX, y, centerZ + offset), stripe, Block.UPDATE_CLIENTS);
		}
	}

	private static void buildGrandstands(ServerLevel level) {
		for (int tier = 0; tier < 3; tier++) {
			int y = RaceDimension.GROUND_Y + tier;
			for (int x = -54; x <= 54; x += 6) {
				int color = Math.floorMod(x / 6 + tier, CROWD_COLORS.length);
				addSpectator(level, x, y, -(66 + tier * 2), CROWD_COLORS[color], -90.0f);
				addSpectator(level, x, y, 66 + tier * 2, CROWD_COLORS[color], 90.0f);
			}
			for (int z = -36; z <= 36; z += 6) {
				int color = Math.floorMod(z / 6 + tier, CROWD_COLORS.length);
				addSpectator(level, -(86 + tier * 2), y, z, CROWD_COLORS[color], 0.0f);
				addSpectator(level, 86 + tier * 2, y, z, CROWD_COLORS[color], 180.0f);
			}
		}
	}

	private static void addSpectator(ServerLevel level, int x, int y, int z, BlockState seat, float yaw) {
		level.setBlock(new BlockPos(x, y, z), seat, Block.UPDATE_CLIENTS);
		EntityType<?> armorStandType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("armor_stand"));
		if (armorStandType == null) {
			throw new IllegalStateException("Minecraft's armor stand entity type is unavailable.");
		}
		if (!(armorStandType.create(level, EntitySpawnReason.TRIGGERED) instanceof ArmorStand spectator)) {
			throw new IllegalStateException("Could not create a race spectator armor stand.");
		}
		spectator.setPos(x + 0.5, y + 1.0, z + 0.5);
		spectator.setYRot(yaw);
		spectator.setNoBasePlate(true);
		spectator.setShowArms(true);
		spectator.setNoGravity(true);
		spectator.setSilent(true);
		spectator.setPermanentlyInvulnerable(true);
		spectator.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));
		if (!level.addFreshEntity(spectator)) {
			throw new IllegalStateException("Could not add a race spectator to the race dimension.");
		}
	}

	private static void buildLights(ServerLevel level) {
		for (int x = -48; x <= 48; x += 24) {
			addLight(level, x, -63);
			addLight(level, x, 63);
		}
		for (int z = -32; z <= 32; z += 24) {
			addLight(level, -83, z);
			addLight(level, 83, z);
		}
	}

	private static void addLight(ServerLevel level, int x, int z) {
		int y = RaceDimension.GROUND_Y - 1;
		level.setBlock(new BlockPos(x, y, z), Blocks.IRON_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
		level.setBlock(new BlockPos(x, y + 1, z), Blocks.IRON_BARS.defaultBlockState(), Block.UPDATE_CLIENTS);
		level.setBlock(new BlockPos(x, y + 2, z), Blocks.IRON_BARS.defaultBlockState(), Block.UPDATE_CLIENTS);
		level.setBlock(new BlockPos(x, y + 3, z), Blocks.SEA_LANTERN.defaultBlockState(), Block.UPDATE_CLIENTS);
	}

	private static double square(double value) {
		return value * value;
	}

}
