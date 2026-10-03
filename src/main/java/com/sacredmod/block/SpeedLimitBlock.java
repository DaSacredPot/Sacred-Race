package com.sacredmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import com.sacredmod.util.RaceMessages;

public class SpeedLimitBlock extends HorizontalDirectionalBlock {
	public static final IntegerProperty LIMIT_INDEX = IntegerProperty.create("limit_index", 0, 5);
	public static final int[] LIMITS_KMH = {20, 40, 60, 80, 100, 120};

	public SpeedLimitBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(LIMIT_INDEX, 2));
	}

	public static int kmh(BlockState state) {
		return LIMITS_KMH[Mth.clamp(state.getValue(LIMIT_INDEX), 0, LIMITS_KMH.length - 1)];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIMIT_INDEX);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		int next = (state.getValue(LIMIT_INDEX) + (player.isShiftKeyDown() ? LIMITS_KMH.length - 1 : 1)) % LIMITS_KMH.length;
		level.setBlock(pos, state.setValue(LIMIT_INDEX, next), 3);
		RaceMessages.send(player, Component.literal("Speed limit set to " + LIMITS_KMH[next] + " km/h."), true);
		return InteractionResult.SUCCESS;
	}
}
