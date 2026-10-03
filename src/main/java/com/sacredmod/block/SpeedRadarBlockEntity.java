package com.sacredmod.block;

import com.sacredmod.car.RaceCarEntity;
import com.sacredmod.race.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SpeedRadarBlockEntity extends BlockEntity {
	private int cooldown;

	public SpeedRadarBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SPEED_RADAR, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SpeedRadarBlockEntity radar) {
		if (radar.cooldown > 0) {
			radar.cooldown--;
			return;
		}
		radar.cooldown = 20;
		int limit = nearbyLimit(level, pos);
		AABB box = new AABB(pos).inflate(12.0);
		for (RaceCarEntity car : level.getEntitiesOfClass(RaceCarEntity.class, box)) {
			int speed = car.speedKmh();
			if (speed <= limit) {
				continue;
			}
			if (car.getControllingPassenger() instanceof Player player) {
				car.onRadarCaught(player, limit);
			}
		}
	}

	private static int nearbyLimit(Level level, BlockPos origin) {
		int best = level.getAttachedOrCreate(ModAttachments.DEFAULT_SPEED_LIMIT);
		double bestDist = 32 * 32;
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int dy = -4; dy <= 4; dy++) {
			for (int dx = -16; dx <= 16; dx++) {
				for (int dz = -16; dz <= 16; dz++) {
					cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
					BlockState state = level.getBlockState(cursor);
					if (state.getBlock() instanceof SpeedLimitBlock) {
						double dist = origin.distSqr(cursor);
						if (dist < bestDist) {
							bestDist = dist;
							best = SpeedLimitBlock.kmh(state);
						}
					}
				}
			}
		}
		return best;
	}
}
