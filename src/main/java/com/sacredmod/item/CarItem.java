package com.sacredmod.car;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class CarItem extends Item {
	private static final String LEVEL_KEY = "car_level";
	private static final String ENGINE_LEVEL_KEY = "engine_level";
	private static final String WHEELS_LEVEL_KEY = "wheels_level";
	private static final String HANDLING_LEVEL_KEY = "handling_level";
	private static final String CHASSIS_LEVEL_KEY = "chassis_level";
	private final CarBrand brand;

	public CarItem(CarBrand brand, Properties properties) {
		super(properties.stacksTo(1));
		this.brand = brand;
	}

	public CarBrand brand() {
		return brand;
	}

	public static int getStoredLevel(ItemStack stack) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = data.copyTag();
		int level = tag.getIntOr(LEVEL_KEY, 1);
		return Math.max(1, Math.min(RaceCarEntity.MAX_LEVEL, level));
	}

	public static void setStoredLevel(ItemStack stack, int level) {
		int clamped = Math.max(1, Math.min(RaceCarEntity.MAX_LEVEL, level));
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putInt(LEVEL_KEY, clamped);
			tag.putInt(ENGINE_LEVEL_KEY, clamped);
			tag.putInt(WHEELS_LEVEL_KEY, clamped);
			tag.putInt(HANDLING_LEVEL_KEY, clamped);
			tag.putInt(CHASSIS_LEVEL_KEY, clamped);
		});
	}

	public static int getStoredPartLevel(ItemStack stack, CarPart part) {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		String key = switch (part) {
			case ENGINE -> ENGINE_LEVEL_KEY;
			case WHEELS -> WHEELS_LEVEL_KEY;
			case HANDLING -> HANDLING_LEVEL_KEY;
			case CHASSIS -> CHASSIS_LEVEL_KEY;
		};
		int level = tag.getIntOr(key, getStoredLevel(stack));
		return Math.max(1, Math.min(RaceCarEntity.MAX_LEVEL, level));
	}

	public static void storePartLevels(ItemStack stack, RaceCarEntity car) {
		int rating = car.getCarLevel();
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putInt(LEVEL_KEY, rating);
			tag.putInt(ENGINE_LEVEL_KEY, car.getPartLevel(CarPart.ENGINE));
			tag.putInt(WHEELS_LEVEL_KEY, car.getPartLevel(CarPart.WHEELS));
			tag.putInt(HANDLING_LEVEL_KEY, car.getPartLevel(CarPart.HANDLING));
			tag.putInt(CHASSIS_LEVEL_KEY, car.getPartLevel(CarPart.CHASSIS));
		});
	}

	public static void applyPartLevels(ItemStack stack, RaceCarEntity car) {
		for (CarPart part : CarPart.values()) {
			car.setPartLevel(part, getStoredPartLevel(stack, part));
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		RaceCarEntity car = new RaceCarEntity(ModEntityTypes.RACE_CAR, level);
		car.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
		if (player != null) {
			car.setYRot(player.getYRot());
		}
		car.setBrand(this.brand);
		applyPartLevels(stack, car);
		if (!level.addFreshEntity(car)) {
			return InteractionResult.FAIL;
		}
		stack.consume(1, player);
		if (player instanceof ServerPlayer serverPlayer && !player.isSecondaryUseActive()) {
			serverPlayer.startRiding(car);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		int carLevel = getStoredLevel(stack);
		tooltip.accept(Component.literal(brand.maker() + " · Level " + carLevel).withStyle(ChatFormatting.AQUA));
		tooltip.accept(Component.literal("Engine " + getStoredPartLevel(stack, CarPart.ENGINE)
				+ " · Wheels " + getStoredPartLevel(stack, CarPart.WHEELS)
				+ " · Handling " + getStoredPartLevel(stack, CarPart.HANDLING)
				+ " · Chassis " + getStoredPartLevel(stack, CarPart.CHASSIS)).withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.literal("Place on the ground, then hop in to drive.").withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.literal("Use diamonds on the car for engine upgrades; use /race upgrade <part> for other parts.")
				.withStyle(ChatFormatting.GOLD));
	}
}
