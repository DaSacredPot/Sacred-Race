package com.sacredmod.block;

import com.sacredmod.RaceMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public final class ModBlockEntities {
	public static final ResourceKey<BlockEntityType<?>> SPEED_RADAR_KEY = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, RaceMod.id("speed_radar"));

	public static final BlockEntityType<SpeedRadarBlockEntity> SPEED_RADAR = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			SPEED_RADAR_KEY,
			FabricBlockEntityTypeBuilder.create(SpeedRadarBlockEntity::new, ModBlocks.SPEED_RADAR).build()
	);

	private ModBlockEntities() {
	}

	public static void initialize() {
	}
}
