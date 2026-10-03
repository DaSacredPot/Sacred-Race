package com.sacredmod.block;

import com.sacredmod.RaceMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public final class ModBlocks {
	public static final Block SPEED_LIMIT = register("speed_limit", SpeedLimitBlock::new, streetProperties(MapColor.COLOR_YELLOW));
	public static final Block SPEED_RADAR = register("speed_radar", SpeedRadarBlock::new, streetProperties(MapColor.COLOR_RED));
	public static final Block RACE_START = register("race_start", RaceStartBlock::new, streetProperties(MapColor.COLOR_LIGHT_GREEN));
	public static final Block RACE_CHECKPOINT = register("race_checkpoint", RaceCheckpointBlock::new, streetProperties(MapColor.COLOR_LIGHT_BLUE));
	public static final Block RACE_FINISH = register("race_finish", RaceFinishBlock::new, streetProperties(MapColor.COLOR_ORANGE));

	private ModBlocks() {
	}

	private static BlockBehaviour.Properties streetProperties(MapColor color) {
		return BlockBehaviour.Properties.of().mapColor(color).strength(1.5f, 6.0f).sound(SoundType.METAL);
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = key(name);
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, RaceMod.id(name));
		Block block = factory.apply(properties.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
		BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
		return block;
	}

	public static ResourceKey<Block> key(String name) {
		return ResourceKey.create(Registries.BLOCK, RaceMod.id(name));
	}

	public static void initialize() {
	}
}
