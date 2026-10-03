package com.sacredmod.race;

import com.mojang.serialization.Codec;
import com.sacredmod.RaceMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class ModAttachments {
	public static final AttachmentType<RaceStats> RACE_STATS = AttachmentRegistry.create(
			RaceMod.id("race_stats"),
			builder -> builder
					.persistent(RaceStats.CODEC)
					.initializer(() -> RaceStats.EMPTY)
					.copyOnDeath()
					.syncWith(RaceStats.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
	);

	public static final AttachmentType<Integer> DEFAULT_SPEED_LIMIT = AttachmentRegistry.create(
			RaceMod.id("default_speed_limit"),
			builder -> builder.persistent(Codec.INT).initializer(() -> 60)
	);

	private ModAttachments() {
	}

	public static void initialize() {
	}
}
