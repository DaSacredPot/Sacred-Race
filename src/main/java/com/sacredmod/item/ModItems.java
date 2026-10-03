package com.sacredmod.item;

import com.sacredmod.RaceMod;
import com.sacredmod.block.ModBlocks;
import com.sacredmod.car.CarBrand;
import com.sacredmod.car.CarItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;

import java.util.function.Function;

public final class ModItems {
	public static final Item VELDORA_CAR = register(CarBrand.VELDORA.itemId(), properties -> new CarItem(CarBrand.VELDORA, properties));
	public static final Item NIMBUS_CAR = register(CarBrand.NIMBUS.itemId(), properties -> new CarItem(CarBrand.NIMBUS, properties));
	public static final Item APEXLYN_CAR = register(CarBrand.APEXLYN.itemId(), properties -> new CarItem(CarBrand.APEXLYN, properties));
	public static final Item DRIFTORA_CAR = register(CarBrand.DRIFTORA.itemId(), properties -> new CarItem(CarBrand.DRIFTORA, properties));
	public static final Item ZEPHARA_CAR = register(CarBrand.ZEPHARA.itemId(), properties -> new CarItem(CarBrand.ZEPHARA, properties));
	public static final Item KORVEX_CAR = register(CarBrand.KORVEX.itemId(), properties -> new CarItem(CarBrand.KORVEX, properties));
	public static final Item BERCEDES_MENZ_CAR = register(CarBrand.BERCEDES_MENZ.itemId(), properties -> new CarItem(CarBrand.BERCEDES_MENZ, properties));
	public static final Item BORCHE_CAR = register(CarBrand.BORCHE.itemId(), properties -> new CarItem(CarBrand.BORCHE, properties));
	public static final Item PMV_CAR = register(CarBrand.PMV.itemId(), properties -> new CarItem(CarBrand.PMV, properties));

	public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(
			BuiltInRegistries.CREATIVE_MODE_TAB.key(), RaceMod.id("race")
	);

	public static final CreativeModeTab TAB = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(VELDORA_CAR))
			.title(Component.translatable("itemGroup.racemod.race"))
			.displayItems((params, output) -> {
				output.accept(VELDORA_CAR);
				output.accept(NIMBUS_CAR);
				output.accept(APEXLYN_CAR);
				output.accept(DRIFTORA_CAR);
				output.accept(ZEPHARA_CAR);
				output.accept(KORVEX_CAR);
				output.accept(BERCEDES_MENZ_CAR);
				output.accept(BORCHE_CAR);
				output.accept(PMV_CAR);
				output.accept(ModBlocks.SPEED_LIMIT.asItem());
				output.accept(ModBlocks.SPEED_RADAR.asItem());
				output.accept(ModBlocks.RACE_START.asItem());
				output.accept(ModBlocks.RACE_CHECKPOINT.asItem());
				output.accept(ModBlocks.RACE_FINISH.asItem());
			})
			.build();

	private ModItems() {
	}

	private static Item register(String name, Function<Item.Properties, Item> factory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RaceMod.id(name));
		Item item = factory.apply(new Item.Properties().setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static void initialize() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, TAB);
	}
}
