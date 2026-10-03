package com.sacredmod.car;

import com.sacredmod.RaceMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.Registry;

public final class ModEntityTypes {
	public static final ResourceKey<EntityType<?>> RACE_CAR_KEY = ResourceKey.create(Registries.ENTITY_TYPE, RaceMod.id("race_car"));

	public static final EntityType<RaceCarEntity> RACE_CAR = register(
			RACE_CAR_KEY,
			EntityType.Builder.<RaceCarEntity>of(RaceCarEntity::new, MobCategory.MISC)
					.sized(1.5f, 0.85f)
					.eyeHeight(0.55f)
					.clientTrackingRange(10)
	);

	private ModEntityTypes() {
	}

	private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> builder) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void initialize() {
	}
}
