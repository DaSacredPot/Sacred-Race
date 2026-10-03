package com.sacredmod.car;

import com.sacredmod.RaceMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.Registry;
import net.minecraft.world.phys.Vec3;

public final class ModEntityTypes {
	public static final ResourceKey<EntityType<?>> RACE_CAR_KEY = ResourceKey.create(Registries.ENTITY_TYPE, RaceMod.id("race_car"));

	public static final EntityType<RaceCarEntity> RACE_CAR = register(
			RACE_CAR_KEY,
			EntityType.Builder.<RaceCarEntity>of(RaceCarEntity::new, MobCategory.MISC)
					.sized(1.8f, 1.25f)
					.eyeHeight(0.75f)
					.passengerAttachments(new Vec3(-0.2, 0.55, 0.12), new Vec3(0.2, 0.55, 0.12))
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
