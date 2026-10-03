package com.sacredmod.block;

import com.sacredmod.race.RaceManager;
import com.sacredmod.race.RaceDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RaceStartBlock extends Block {
	public RaceStartBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) {
			if (level instanceof ServerLevel serverLevel && RaceDimension.isRaceDimension(serverLevel)
					&& player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
				RaceManager.startArenaRace(serverPlayer);
			} else {
				RaceManager.startRace(player, pos);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
