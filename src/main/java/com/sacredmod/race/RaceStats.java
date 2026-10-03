package com.sacredmod.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RaceStats(int racesCompleted, int bounty, int checkpointsHit, boolean racing, int requiredCheckpoints) {
	public static final RaceStats EMPTY = new RaceStats(0, 0, 0, false, 0);

	public static final Codec<RaceStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("races", 0).forGetter(RaceStats::racesCompleted),
			Codec.INT.optionalFieldOf("bounty", 0).forGetter(RaceStats::bounty),
			Codec.INT.optionalFieldOf("checkpoints", 0).forGetter(RaceStats::checkpointsHit),
			Codec.BOOL.optionalFieldOf("racing", false).forGetter(RaceStats::racing),
			Codec.INT.optionalFieldOf("required", 0).forGetter(RaceStats::requiredCheckpoints)
	).apply(instance, RaceStats::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RaceStats> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, RaceStats::racesCompleted,
			ByteBufCodecs.VAR_INT, RaceStats::bounty,
			ByteBufCodecs.VAR_INT, RaceStats::checkpointsHit,
			ByteBufCodecs.BOOL, RaceStats::racing,
			ByteBufCodecs.VAR_INT, RaceStats::requiredCheckpoints,
			RaceStats::new
	);

	public RaceStats withBounty(int value) {
		return new RaceStats(racesCompleted, Math.max(0, value), checkpointsHit, racing, requiredCheckpoints);
	}

	public RaceStats startRace(int required) {
		return new RaceStats(racesCompleted, bounty, 0, true, required);
	}

	public RaceStats hitCheckpoint() {
		return new RaceStats(racesCompleted, bounty, checkpointsHit + 1, racing, requiredCheckpoints);
	}

	public RaceStats finishRace() {
		return new RaceStats(racesCompleted + 1, bounty, 0, false, 0);
	}

	public RaceStats cancelRace() {
		return new RaceStats(racesCompleted, bounty, 0, false, 0);
	}
}
