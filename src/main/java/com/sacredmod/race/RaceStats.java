package com.sacredmod.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RaceStats(
		int racesCompleted,
		int bounty,
		int checkpointsHit,
		boolean racing,
		int requiredCheckpoints,
		int completedLaps,
		int lapTarget,
		long elapsedTicks,
		boolean checkpointPassed
) {
	public static final RaceStats EMPTY = new RaceStats(0, 0, 0, false, 0, 0, 0, 0, false);

	public static final Codec<RaceStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("races", 0).forGetter(RaceStats::racesCompleted),
			Codec.INT.optionalFieldOf("bounty", 0).forGetter(RaceStats::bounty),
			Codec.INT.optionalFieldOf("checkpoints", 0).forGetter(RaceStats::checkpointsHit),
			Codec.BOOL.optionalFieldOf("racing", false).forGetter(RaceStats::racing),
			Codec.INT.optionalFieldOf("required", 0).forGetter(RaceStats::requiredCheckpoints),
			Codec.INT.optionalFieldOf("laps", 0).forGetter(RaceStats::completedLaps),
			Codec.INT.optionalFieldOf("lap_target", 0).forGetter(RaceStats::lapTarget),
			Codec.LONG.optionalFieldOf("elapsed_ticks", 0L).forGetter(RaceStats::elapsedTicks),
			Codec.BOOL.optionalFieldOf("checkpoint_passed", false).forGetter(RaceStats::checkpointPassed)
	).apply(instance, RaceStats::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RaceStats> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, RaceStats::racesCompleted,
			ByteBufCodecs.VAR_INT, RaceStats::bounty,
			ByteBufCodecs.VAR_INT, RaceStats::checkpointsHit,
			ByteBufCodecs.BOOL, RaceStats::racing,
			ByteBufCodecs.VAR_INT, RaceStats::requiredCheckpoints,
			ByteBufCodecs.VAR_INT, RaceStats::completedLaps,
			ByteBufCodecs.VAR_INT, RaceStats::lapTarget,
			ByteBufCodecs.VAR_LONG, RaceStats::elapsedTicks,
			ByteBufCodecs.BOOL, RaceStats::checkpointPassed,
			RaceStats::new
	);

	public RaceStats withBounty(int value) {
		return copy(Math.max(0, value), checkpointsHit, racing, requiredCheckpoints, completedLaps, lapTarget, elapsedTicks, checkpointPassed);
	}

	public RaceStats startRace(int required) {
		return startRace(required, 1);
	}

	public RaceStats startRace(int required, int targetLaps) {
		return copy(bounty, 0, true, Math.max(0, required), 0, Math.max(1, targetLaps), 0, false);
	}

	public RaceStats tickRace() {
		return racing ? copy(bounty, checkpointsHit, true, requiredCheckpoints, completedLaps, lapTarget, elapsedTicks + 1, checkpointPassed) : this;
	}

	public RaceStats hitCheckpoint() {
		return copy(bounty, checkpointsHit + 1, racing, requiredCheckpoints, completedLaps, lapTarget, elapsedTicks, true);
	}

	public RaceStats completeLap() {
		return copy(bounty, 0, racing, requiredCheckpoints, completedLaps + 1, lapTarget, elapsedTicks, false);
	}

	public RaceStats finishRace() {
		return new RaceStats(racesCompleted + 1, bounty, 0, false, 0, completedLaps, lapTarget, elapsedTicks, false);
	}

	public RaceStats cancelRace() {
		return new RaceStats(racesCompleted, bounty, 0, false, 0, 0, 0, 0, false);
	}

	private RaceStats copy(int newBounty, int newCheckpoints, boolean newRacing, int newRequired,
			int newLaps, int newLapTarget, long newElapsedTicks, boolean newCheckpointPassed) {
		return new RaceStats(racesCompleted, newBounty, newCheckpoints, newRacing, newRequired,
				newLaps, newLapTarget, newElapsedTicks, newCheckpointPassed);
	}
}
